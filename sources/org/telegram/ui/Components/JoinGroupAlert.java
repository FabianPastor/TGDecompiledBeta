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
import org.telegram.tgnet.TLRPC$Chat;
import org.telegram.tgnet.TLRPC$ChatInvite;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$TL_messages_importChatInvite;
import org.telegram.tgnet.TLRPC$Updates;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.Cells.JoinSheetUserCell;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.Components.Bulletin;
import org.telegram.ui.Components.RecyclerListView;

public class JoinGroupAlert extends BottomSheet {
    /* access modifiers changed from: private */
    public TLRPC$ChatInvite chatInvite;
    private TLRPC$Chat currentChat;
    private BaseFragment fragment;
    private String hash;
    private RadialProgressView requestProgressView;
    private TextView requestTextView;

    /* JADX WARNING: Illegal instructions before constructor call */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public JoinGroupAlert(android.content.Context r23, org.telegram.tgnet.TLObject r24, java.lang.String r25, org.telegram.ui.ActionBar.BaseFragment r26, org.telegram.ui.ActionBar.Theme.ResourcesProvider r27) {
        /*
            r22 = this;
            r0 = r22
            r1 = r23
            r2 = r24
            r3 = r27
            r4 = 0
            r0.<init>(r1, r4, r3)
            r0.setApplyBottomPadding(r4)
            r0.setApplyTopPadding(r4)
            java.lang.String r5 = "windowBackgroundWhite"
            int r5 = r0.getThemedColor(r5)
            r0.fixNavigationBar(r5)
            r5 = r26
            r0.fragment = r5
            boolean r5 = r2 instanceof org.telegram.tgnet.TLRPC$ChatInvite
            if (r5 == 0) goto L_0x0028
            org.telegram.tgnet.TLRPC$ChatInvite r2 = (org.telegram.tgnet.TLRPC$ChatInvite) r2
            r0.chatInvite = r2
            goto L_0x0030
        L_0x0028:
            boolean r5 = r2 instanceof org.telegram.tgnet.TLRPC$Chat
            if (r5 == 0) goto L_0x0030
            org.telegram.tgnet.TLRPC$Chat r2 = (org.telegram.tgnet.TLRPC$Chat) r2
            r0.currentChat = r2
        L_0x0030:
            r2 = r25
            r0.hash = r2
            android.widget.LinearLayout r2 = new android.widget.LinearLayout
            r2.<init>(r1)
            r5 = 1
            r2.setOrientation(r5)
            r2.setClickable(r5)
            android.widget.FrameLayout r6 = new android.widget.FrameLayout
            r6.<init>(r1)
            r6.addView(r2)
            androidx.core.widget.NestedScrollView r7 = new androidx.core.widget.NestedScrollView
            r7.<init>(r1)
            r7.addView(r6)
            r0.setCustomView(r7)
            android.widget.ImageView r7 = new android.widget.ImageView
            r7.<init>(r1)
            java.lang.String r8 = "listSelectorSDK21"
            int r8 = r0.getThemedColor(r8)
            android.graphics.drawable.Drawable r8 = org.telegram.ui.ActionBar.Theme.createSelectorDrawable(r8)
            r7.setBackground(r8)
            java.lang.String r8 = "key_sheet_other"
            int r8 = r0.getThemedColor(r8)
            r7.setColorFilter(r8)
            r8 = 2131165480(0x7var_, float:1.7945178E38)
            r7.setImageResource(r8)
            org.telegram.ui.Components.JoinGroupAlert$$ExternalSyntheticLambda2 r8 = new org.telegram.ui.Components.JoinGroupAlert$$ExternalSyntheticLambda2
            r8.<init>(r0)
            r7.setOnClickListener(r8)
            r8 = 1090519040(0x41000000, float:8.0)
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r8)
            r7.setPadding(r9, r9, r9, r9)
            r10 = 36
            r11 = 1108344832(0x42100000, float:36.0)
            r12 = 8388661(0x800035, float:1.1755018E-38)
            r13 = 1086324736(0x40CLASSNAME, float:6.0)
            r14 = 1090519040(0x41000000, float:8.0)
            r15 = 1086324736(0x40CLASSNAME, float:6.0)
            r16 = 0
            android.widget.FrameLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createFrame(r10, r11, r12, r13, r14, r15, r16)
            r6.addView(r7, r9)
            org.telegram.ui.Components.BackupImageView r6 = new org.telegram.ui.Components.BackupImageView
            r6.<init>(r1)
            r7 = 1108082688(0x420CLASSNAME, float:35.0)
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
            r6.setRoundRadius(r7)
            r9 = 70
            r10 = 70
            r11 = 49
            r12 = 0
            r13 = 29
            r14 = 0
            r15 = 0
            android.widget.LinearLayout$LayoutParams r7 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r9, (int) r10, (int) r11, (int) r12, (int) r13, (int) r14, (int) r15)
            r2.addView(r6, r7)
            org.telegram.tgnet.TLRPC$ChatInvite r7 = r0.chatInvite
            r9 = 0
            if (r7 == 0) goto L_0x010d
            org.telegram.tgnet.TLRPC$Chat r7 = r7.chat
            if (r7 == 0) goto L_0x00da
            org.telegram.ui.Components.AvatarDrawable r7 = new org.telegram.ui.Components.AvatarDrawable
            org.telegram.tgnet.TLRPC$ChatInvite r9 = r0.chatInvite
            org.telegram.tgnet.TLRPC$Chat r9 = r9.chat
            r7.<init>((org.telegram.tgnet.TLRPC$Chat) r9)
            org.telegram.tgnet.TLRPC$ChatInvite r9 = r0.chatInvite
            org.telegram.tgnet.TLRPC$Chat r10 = r9.chat
            java.lang.String r11 = r10.title
            int r12 = r10.participants_count
            r6.setForUserOrChat(r10, r7, r9)
            r9 = r11
            goto L_0x0108
        L_0x00da:
            org.telegram.ui.Components.AvatarDrawable r7 = new org.telegram.ui.Components.AvatarDrawable
            r7.<init>()
            r10 = 0
            org.telegram.tgnet.TLRPC$ChatInvite r12 = r0.chatInvite
            java.lang.String r12 = r12.title
            r7.setInfo(r10, r12, r9)
            org.telegram.tgnet.TLRPC$ChatInvite r9 = r0.chatInvite
            java.lang.String r10 = r9.title
            int r12 = r9.participants_count
            org.telegram.tgnet.TLRPC$Photo r9 = r9.photo
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r9 = r9.sizes
            r11 = 50
            org.telegram.tgnet.TLRPC$PhotoSize r9 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r9, r11)
            org.telegram.tgnet.TLRPC$ChatInvite r11 = r0.chatInvite
            org.telegram.tgnet.TLRPC$Photo r11 = r11.photo
            org.telegram.messenger.ImageLocation r9 = org.telegram.messenger.ImageLocation.getForPhoto((org.telegram.tgnet.TLRPC$PhotoSize) r9, (org.telegram.tgnet.TLRPC$Photo) r11)
            org.telegram.tgnet.TLRPC$ChatInvite r11 = r0.chatInvite
            java.lang.String r13 = "50_50"
            r6.setImage((org.telegram.messenger.ImageLocation) r9, (java.lang.String) r13, (android.graphics.drawable.Drawable) r7, (java.lang.Object) r11)
            r9 = r10
        L_0x0108:
            org.telegram.tgnet.TLRPC$ChatInvite r6 = r0.chatInvite
            java.lang.String r6 = r6.about
            goto L_0x0146
        L_0x010d:
            org.telegram.tgnet.TLRPC$Chat r7 = r0.currentChat
            if (r7 == 0) goto L_0x0144
            org.telegram.ui.Components.AvatarDrawable r7 = new org.telegram.ui.Components.AvatarDrawable
            org.telegram.tgnet.TLRPC$Chat r10 = r0.currentChat
            r7.<init>((org.telegram.tgnet.TLRPC$Chat) r10)
            org.telegram.tgnet.TLRPC$Chat r10 = r0.currentChat
            java.lang.String r10 = r10.title
            int r11 = r0.currentAccount
            org.telegram.messenger.MessagesController r11 = org.telegram.messenger.MessagesController.getInstance(r11)
            org.telegram.tgnet.TLRPC$Chat r12 = r0.currentChat
            long r12 = r12.id
            org.telegram.tgnet.TLRPC$ChatFull r11 = r11.getChatFull(r12)
            if (r11 == 0) goto L_0x012e
            java.lang.String r9 = r11.about
        L_0x012e:
            org.telegram.tgnet.TLRPC$Chat r12 = r0.currentChat
            int r12 = r12.participants_count
            if (r11 == 0) goto L_0x0137
            int r11 = r11.participants_count
            goto L_0x0138
        L_0x0137:
            r11 = 0
        L_0x0138:
            int r12 = java.lang.Math.max(r12, r11)
            org.telegram.tgnet.TLRPC$Chat r11 = r0.currentChat
            r6.setForUserOrChat(r11, r7, r11)
            r6 = r9
            r9 = r10
            goto L_0x0146
        L_0x0144:
            r6 = r9
            r12 = 0
        L_0x0146:
            android.widget.TextView r7 = new android.widget.TextView
            r7.<init>(r1)
            java.lang.String r10 = "fonts/rmedium.ttf"
            android.graphics.Typeface r11 = org.telegram.messenger.AndroidUtilities.getTypeface(r10)
            r7.setTypeface(r11)
            r11 = 1099431936(0x41880000, float:17.0)
            r7.setTextSize(r5, r11)
            java.lang.String r11 = "dialogTextBlack"
            int r13 = r0.getThemedColor(r11)
            r7.setTextColor(r13)
            r7.setText(r9)
            r7.setSingleLine(r5)
            android.text.TextUtils$TruncateAt r9 = android.text.TextUtils.TruncateAt.END
            r7.setEllipsize(r9)
            r13 = -2
            r14 = -2
            r15 = 49
            r16 = 10
            r17 = 9
            r18 = 10
            if (r12 <= 0) goto L_0x017c
            r19 = 0
            goto L_0x017e
        L_0x017c:
            r19 = 20
        L_0x017e:
            android.widget.LinearLayout$LayoutParams r13 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r13, (int) r14, (int) r15, (int) r16, (int) r17, (int) r18, (int) r19)
            r2.addView(r7, r13)
            org.telegram.tgnet.TLRPC$ChatInvite r7 = r0.chatInvite
            if (r7 == 0) goto L_0x0199
            boolean r13 = r7.channel
            if (r13 == 0) goto L_0x0191
            boolean r13 = r7.megagroup
            if (r13 == 0) goto L_0x01a7
        L_0x0191:
            org.telegram.tgnet.TLRPC$Chat r7 = r7.chat
            boolean r7 = org.telegram.messenger.ChatObject.isChannelAndNotMegaGroup(r7)
            if (r7 != 0) goto L_0x01a7
        L_0x0199:
            org.telegram.tgnet.TLRPC$Chat r7 = r0.currentChat
            boolean r7 = org.telegram.messenger.ChatObject.isChannel(r7)
            if (r7 == 0) goto L_0x01a9
            org.telegram.tgnet.TLRPC$Chat r7 = r0.currentChat
            boolean r7 = r7.megagroup
            if (r7 != 0) goto L_0x01a9
        L_0x01a7:
            r7 = 1
            goto L_0x01aa
        L_0x01a9:
            r7 = 0
        L_0x01aa:
            boolean r13 = android.text.TextUtils.isEmpty(r6)
            r13 = r13 ^ r5
            java.lang.String r14 = "dialogTextGray3"
            r15 = 1096810496(0x41600000, float:14.0)
            if (r12 <= 0) goto L_0x01fe
            android.widget.TextView r9 = new android.widget.TextView
            r9.<init>(r1)
            r9.setTextSize(r5, r15)
            int r15 = r0.getThemedColor(r14)
            r9.setTextColor(r15)
            r9.setSingleLine(r5)
            android.text.TextUtils$TruncateAt r15 = android.text.TextUtils.TruncateAt.END
            r9.setEllipsize(r15)
            if (r7 == 0) goto L_0x01da
            java.lang.Object[] r15 = new java.lang.Object[r4]
            java.lang.String r8 = "Subscribers"
            java.lang.String r8 = org.telegram.messenger.LocaleController.formatPluralString(r8, r12, r15)
            r9.setText(r8)
            goto L_0x01e5
        L_0x01da:
            java.lang.Object[] r8 = new java.lang.Object[r4]
            java.lang.String r15 = "Members"
            java.lang.String r8 = org.telegram.messenger.LocaleController.formatPluralString(r15, r12, r8)
            r9.setText(r8)
        L_0x01e5:
            r15 = -2
            r16 = -2
            r17 = 49
            r18 = 10
            r19 = 3
            r20 = 10
            if (r13 == 0) goto L_0x01f5
            r21 = 0
            goto L_0x01f7
        L_0x01f5:
            r21 = 20
        L_0x01f7:
            android.widget.LinearLayout$LayoutParams r8 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r15, (int) r16, (int) r17, (int) r18, (int) r19, (int) r20, (int) r21)
            r2.addView(r9, r8)
        L_0x01fe:
            r8 = 1097859072(0x41700000, float:15.0)
            r9 = 17
            if (r13 == 0) goto L_0x022d
            android.widget.TextView r12 = new android.widget.TextView
            r12.<init>(r1)
            r12.setGravity(r9)
            r12.setText(r6)
            int r6 = r0.getThemedColor(r11)
            r12.setTextColor(r6)
            r12.setTextSize(r5, r8)
            r15 = -1
            r16 = -2
            r17 = 48
            r18 = 24
            r19 = 10
            r20 = 24
            r21 = 20
            android.widget.LinearLayout$LayoutParams r6 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r15, (int) r16, (int) r17, (int) r18, (int) r19, (int) r20, (int) r21)
            r2.addView(r12, r6)
        L_0x022d:
            org.telegram.tgnet.TLRPC$ChatInvite r6 = r0.chatInvite
            r11 = 48
            r12 = -1
            if (r6 == 0) goto L_0x0356
            boolean r13 = r6.request_needed
            if (r13 == 0) goto L_0x023a
            goto L_0x0356
        L_0x023a:
            if (r6 == 0) goto L_0x0457
            java.util.ArrayList<org.telegram.tgnet.TLRPC$User> r5 = r6.participants
            boolean r5 = r5.isEmpty()
            if (r5 != 0) goto L_0x028f
            org.telegram.ui.Components.RecyclerListView r5 = new org.telegram.ui.Components.RecyclerListView
            r5.<init>(r1)
            r6 = 1090519040(0x41000000, float:8.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            r5.setPadding(r4, r4, r4, r6)
            r5.setNestedScrollingEnabled(r4)
            r5.setClipToPadding(r4)
            androidx.recyclerview.widget.LinearLayoutManager r6 = new androidx.recyclerview.widget.LinearLayoutManager
            android.content.Context r7 = r22.getContext()
            r6.<init>(r7, r4, r4)
            r5.setLayoutManager(r6)
            r5.setHorizontalScrollBarEnabled(r4)
            r5.setVerticalScrollBarEnabled(r4)
            org.telegram.ui.Components.JoinGroupAlert$UsersAdapter r6 = new org.telegram.ui.Components.JoinGroupAlert$UsersAdapter
            r6.<init>(r1)
            r5.setAdapter(r6)
            java.lang.String r6 = "dialogScrollGlow"
            int r6 = r0.getThemedColor(r6)
            r5.setGlowColor(r6)
            r13 = -2
            r14 = 90
            r15 = 49
            r16 = 0
            r17 = 0
            r18 = 0
            r19 = 7
            android.widget.LinearLayout$LayoutParams r6 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r13, (int) r14, (int) r15, (int) r16, (int) r17, (int) r18, (int) r19)
            r2.addView(r5, r6)
        L_0x028f:
            android.view.View r5 = new android.view.View
            r5.<init>(r1)
            java.lang.String r6 = "dialogShadowLine"
            int r6 = r0.getThemedColor(r6)
            r5.setBackgroundColor(r6)
            android.widget.LinearLayout$LayoutParams r6 = new android.widget.LinearLayout$LayoutParams
            int r7 = org.telegram.messenger.AndroidUtilities.getShadowHeight()
            r6.<init>(r12, r7)
            r2.addView(r5, r6)
            org.telegram.ui.Components.PickerBottomLayout r5 = new org.telegram.ui.Components.PickerBottomLayout
            r5.<init>(r1, r4, r3)
            r1 = 83
            android.widget.FrameLayout$LayoutParams r1 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r12, (int) r11, (int) r1)
            r2.addView(r5, r1)
            android.widget.TextView r1 = r5.cancelButton
            r2 = 1099956224(0x41900000, float:18.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r2)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r2)
            r1.setPadding(r3, r4, r6, r4)
            android.widget.TextView r1 = r5.cancelButton
            java.lang.String r3 = "dialogTextBlue2"
            int r6 = r0.getThemedColor(r3)
            r1.setTextColor(r6)
            android.widget.TextView r1 = r5.cancelButton
            r6 = 2131624832(0x7f0e0380, float:1.8876855E38)
            java.lang.String r7 = "Cancel"
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r7, r6)
            java.lang.String r6 = r6.toUpperCase()
            r1.setText(r6)
            android.widget.TextView r1 = r5.cancelButton
            org.telegram.ui.Components.JoinGroupAlert$$ExternalSyntheticLambda3 r6 = new org.telegram.ui.Components.JoinGroupAlert$$ExternalSyntheticLambda3
            r6.<init>(r0)
            r1.setOnClickListener(r6)
            android.widget.LinearLayout r1 = r5.doneButton
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r2)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            r1.setPadding(r6, r4, r2, r4)
            android.widget.LinearLayout r1 = r5.doneButton
            r1.setVisibility(r4)
            android.widget.TextView r1 = r5.doneButtonBadgeTextView
            r2 = 8
            r1.setVisibility(r2)
            android.widget.TextView r1 = r5.doneButtonTextView
            int r2 = r0.getThemedColor(r3)
            r1.setTextColor(r2)
            org.telegram.tgnet.TLRPC$ChatInvite r1 = r0.chatInvite
            boolean r2 = r1.channel
            if (r2 == 0) goto L_0x0319
            boolean r2 = r1.megagroup
            if (r2 == 0) goto L_0x0329
        L_0x0319:
            org.telegram.tgnet.TLRPC$Chat r1 = r1.chat
            boolean r1 = org.telegram.messenger.ChatObject.isChannel(r1)
            if (r1 == 0) goto L_0x033c
            org.telegram.tgnet.TLRPC$ChatInvite r1 = r0.chatInvite
            org.telegram.tgnet.TLRPC$Chat r1 = r1.chat
            boolean r1 = r1.megagroup
            if (r1 != 0) goto L_0x033c
        L_0x0329:
            android.widget.TextView r1 = r5.doneButtonTextView
            r2 = 2131627801(0x7f0e0var_, float:1.8882877E38)
            java.lang.String r3 = "ProfileJoinChannel"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            java.lang.String r2 = r2.toUpperCase()
            r1.setText(r2)
            goto L_0x034a
        L_0x033c:
            android.widget.TextView r1 = r5.doneButtonTextView
            r2 = 2131626364(0x7f0e097c, float:1.8879962E38)
            java.lang.String r3 = "JoinGroup"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r1.setText(r2)
        L_0x034a:
            android.widget.LinearLayout r1 = r5.doneButton
            org.telegram.ui.Components.JoinGroupAlert$$ExternalSyntheticLambda4 r2 = new org.telegram.ui.Components.JoinGroupAlert$$ExternalSyntheticLambda4
            r2.<init>(r0)
            r1.setOnClickListener(r2)
            goto L_0x0457
        L_0x0356:
            android.widget.FrameLayout r1 = new android.widget.FrameLayout
            android.content.Context r4 = r22.getContext()
            r1.<init>(r4)
            r4 = -2
            android.widget.LinearLayout$LayoutParams r4 = org.telegram.ui.Components.LayoutHelper.createLinear(r12, r4)
            r2.addView(r1, r4)
            org.telegram.ui.Components.RadialProgressView r4 = new org.telegram.ui.Components.RadialProgressView
            android.content.Context r6 = r22.getContext()
            r4.<init>(r6, r3)
            r0.requestProgressView = r4
            java.lang.String r3 = "featuredStickers_addButton"
            int r6 = r0.getThemedColor(r3)
            r4.setProgressColor(r6)
            org.telegram.ui.Components.RadialProgressView r4 = r0.requestProgressView
            r6 = 1107296256(0x42000000, float:32.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            r4.setSize(r6)
            org.telegram.ui.Components.RadialProgressView r4 = r0.requestProgressView
            r6 = 4
            r4.setVisibility(r6)
            org.telegram.ui.Components.RadialProgressView r4 = r0.requestProgressView
            android.widget.FrameLayout$LayoutParams r6 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r11, (int) r11, (int) r9)
            r1.addView(r4, r6)
            android.widget.TextView r4 = new android.widget.TextView
            android.content.Context r6 = r22.getContext()
            r4.<init>(r6)
            r0.requestTextView = r4
            r6 = 1086324736(0x40CLASSNAME, float:6.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            int r3 = r0.getThemedColor(r3)
            java.lang.String r11 = "featuredStickers_addButtonPressed"
            int r11 = r0.getThemedColor(r11)
            android.graphics.drawable.Drawable r3 = org.telegram.ui.ActionBar.Theme.createSimpleSelectorRoundRectDrawable(r6, r3, r11)
            r4.setBackground(r3)
            android.widget.TextView r3 = r0.requestTextView
            android.text.TextUtils$TruncateAt r4 = android.text.TextUtils.TruncateAt.END
            r3.setEllipsize(r4)
            android.widget.TextView r3 = r0.requestTextView
            r3.setGravity(r9)
            android.widget.TextView r3 = r0.requestTextView
            r3.setSingleLine(r5)
            android.widget.TextView r3 = r0.requestTextView
            if (r7 == 0) goto L_0x03d2
            r4 = 2131628030(0x7f0e0ffe, float:1.8883341E38)
            java.lang.String r6 = "RequestToJoinChannel"
            goto L_0x03d7
        L_0x03d2:
            r4 = 2131628034(0x7f0e1002, float:1.888335E38)
            java.lang.String r6 = "RequestToJoinGroup"
        L_0x03d7:
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r6, r4)
            r3.setText(r4)
            android.widget.TextView r3 = r0.requestTextView
            java.lang.String r4 = "featuredStickers_buttonText"
            int r4 = r0.getThemedColor(r4)
            r3.setTextColor(r4)
            android.widget.TextView r3 = r0.requestTextView
            r3.setTextSize(r5, r8)
            android.widget.TextView r3 = r0.requestTextView
            android.graphics.Typeface r4 = org.telegram.messenger.AndroidUtilities.getTypeface(r10)
            r3.setTypeface(r4)
            android.widget.TextView r3 = r0.requestTextView
            org.telegram.ui.Components.JoinGroupAlert$$ExternalSyntheticLambda5 r4 = new org.telegram.ui.Components.JoinGroupAlert$$ExternalSyntheticLambda5
            r4.<init>(r0, r7)
            r3.setOnClickListener(r4)
            android.widget.TextView r3 = r0.requestTextView
            r15 = -1
            r16 = 48
            r17 = 8388611(0x800003, float:1.1754948E-38)
            r18 = 16
            r19 = 0
            r20 = 16
            r21 = 0
            android.widget.LinearLayout$LayoutParams r4 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r15, (int) r16, (int) r17, (int) r18, (int) r19, (int) r20, (int) r21)
            r1.addView(r3, r4)
            android.widget.TextView r1 = new android.widget.TextView
            android.content.Context r3 = r22.getContext()
            r1.<init>(r3)
            r1.setGravity(r9)
            r3 = 1096810496(0x41600000, float:14.0)
            r1.setTextSize(r5, r3)
            if (r7 == 0) goto L_0x0431
            r3 = 2131628032(0x7f0e1000, float:1.8883345E38)
            java.lang.String r4 = "RequestToJoinChannelDescription"
            goto L_0x0436
        L_0x0431:
            r3 = 2131628036(0x7f0e1004, float:1.8883353E38)
            java.lang.String r4 = "RequestToJoinGroupDescription"
        L_0x0436:
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            r1.setText(r3)
            int r3 = r0.getThemedColor(r14)
            r1.setTextColor(r3)
            r4 = -1
            r5 = -2
            r6 = 48
            r7 = 24
            r8 = 17
            r9 = 24
            r10 = 15
            android.widget.LinearLayout$LayoutParams r3 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r4, (int) r5, (int) r6, (int) r7, (int) r8, (int) r9, (int) r10)
            r2.addView(r1, r3)
        L_0x0457:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.JoinGroupAlert.<init>(android.content.Context, org.telegram.tgnet.TLObject, java.lang.String, org.telegram.ui.ActionBar.BaseFragment, org.telegram.ui.ActionBar.Theme$ResourcesProvider):void");
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0(View view) {
        dismiss();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$7(boolean z, View view) {
        AndroidUtilities.runOnUIThread(new JoinGroupAlert$$ExternalSyntheticLambda7(this), 400);
        if (this.chatInvite != null || this.currentChat == null) {
            TLRPC$TL_messages_importChatInvite tLRPC$TL_messages_importChatInvite = new TLRPC$TL_messages_importChatInvite();
            tLRPC$TL_messages_importChatInvite.hash = this.hash;
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_messages_importChatInvite, new JoinGroupAlert$$ExternalSyntheticLambda12(this, z, tLRPC$TL_messages_importChatInvite), 2);
            return;
        }
        MessagesController.getInstance(this.currentAccount).addUserToChat(this.currentChat.id, UserConfig.getInstance(this.currentAccount).getCurrentUser(), 0, (String) null, (BaseFragment) null, true, new JoinGroupAlert$$ExternalSyntheticLambda6(this), new JoinGroupAlert$$ExternalSyntheticLambda10(this, z));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$1() {
        if (!isDismissed()) {
            this.requestTextView.setVisibility(4);
            this.requestProgressView.setVisibility(0);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ boolean lambda$new$3(boolean z, TLRPC$TL_error tLRPC$TL_error) {
        if (tLRPC$TL_error != null && "INVITE_REQUEST_SENT".equals(tLRPC$TL_error.text)) {
            setOnDismissListener(new JoinGroupAlert$$ExternalSyntheticLambda1(this, z));
        }
        dismiss();
        return false;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$2(boolean z, DialogInterface dialogInterface) {
        showBulletin(getContext(), this.fragment, z);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$6(boolean z, TLRPC$TL_messages_importChatInvite tLRPC$TL_messages_importChatInvite, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new JoinGroupAlert$$ExternalSyntheticLambda9(this, tLRPC$TL_error, z, tLRPC$TL_messages_importChatInvite));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$5(TLRPC$TL_error tLRPC$TL_error, boolean z, TLRPC$TL_messages_importChatInvite tLRPC$TL_messages_importChatInvite) {
        BaseFragment baseFragment = this.fragment;
        if (baseFragment != null && baseFragment.getParentActivity() != null) {
            if (tLRPC$TL_error != null) {
                if ("INVITE_REQUEST_SENT".equals(tLRPC$TL_error.text)) {
                    setOnDismissListener(new JoinGroupAlert$$ExternalSyntheticLambda0(this, z));
                } else {
                    AlertsCreator.processError(this.currentAccount, tLRPC$TL_error, this.fragment, tLRPC$TL_messages_importChatInvite, new Object[0]);
                }
            }
            dismiss();
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$4(boolean z, DialogInterface dialogInterface) {
        showBulletin(getContext(), this.fragment, z);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$8(View view) {
        dismiss();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$11(View view) {
        dismiss();
        TLRPC$TL_messages_importChatInvite tLRPC$TL_messages_importChatInvite = new TLRPC$TL_messages_importChatInvite();
        tLRPC$TL_messages_importChatInvite.hash = this.hash;
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_messages_importChatInvite, new JoinGroupAlert$$ExternalSyntheticLambda11(this, tLRPC$TL_messages_importChatInvite), 2);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$10(TLRPC$TL_messages_importChatInvite tLRPC$TL_messages_importChatInvite, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        if (tLRPC$TL_error == null) {
            MessagesController.getInstance(this.currentAccount).processUpdates((TLRPC$Updates) tLObject, false);
        }
        AndroidUtilities.runOnUIThread(new JoinGroupAlert$$ExternalSyntheticLambda8(this, tLRPC$TL_error, tLObject, tLRPC$TL_messages_importChatInvite));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$9(TLRPC$TL_error tLRPC$TL_error, TLObject tLObject, TLRPC$TL_messages_importChatInvite tLRPC$TL_messages_importChatInvite) {
        BaseFragment baseFragment = this.fragment;
        if (baseFragment != null && baseFragment.getParentActivity() != null) {
            if (tLRPC$TL_error == null) {
                TLRPC$Updates tLRPC$Updates = (TLRPC$Updates) tLObject;
                if (!tLRPC$Updates.chats.isEmpty()) {
                    TLRPC$Chat tLRPC$Chat = tLRPC$Updates.chats.get(0);
                    tLRPC$Chat.left = false;
                    tLRPC$Chat.kicked = false;
                    MessagesController.getInstance(this.currentAccount).putUsers(tLRPC$Updates.users, false);
                    MessagesController.getInstance(this.currentAccount).putChats(tLRPC$Updates.chats, false);
                    Bundle bundle = new Bundle();
                    bundle.putLong("chat_id", tLRPC$Chat.id);
                    if (MessagesController.getInstance(this.currentAccount).checkCanOpenChat(bundle, this.fragment)) {
                        ChatActivity chatActivity = new ChatActivity(bundle);
                        BaseFragment baseFragment2 = this.fragment;
                        baseFragment2.presentFragment(chatActivity, baseFragment2 instanceof ChatActivity);
                        return;
                    }
                    return;
                }
                return;
            }
            AlertsCreator.processError(this.currentAccount, tLRPC$TL_error, this.fragment, tLRPC$TL_messages_importChatInvite, new Object[0]);
        }
    }

    public static void showBulletin(Context context, BaseFragment baseFragment, boolean z) {
        String str;
        Bulletin.TwoLineLottieLayout twoLineLottieLayout = new Bulletin.TwoLineLottieLayout(context, baseFragment.getResourceProvider());
        twoLineLottieLayout.imageView.setAnimation(NUM, 28, 28);
        twoLineLottieLayout.titleTextView.setText(LocaleController.getString("RequestToJoinSent", NUM));
        if (z) {
            str = LocaleController.getString("RequestToJoinChannelSentDescription", NUM);
        } else {
            str = LocaleController.getString("RequestToJoinGroupSentDescription", NUM);
        }
        twoLineLottieLayout.subtitleTextView.setText(str);
        Bulletin.make(baseFragment, (Bulletin.Layout) twoLineLottieLayout, 2750).show();
    }

    private class UsersAdapter extends RecyclerListView.SelectionAdapter {
        private Context context;

        public long getItemId(int i) {
            return (long) i;
        }

        public int getItemViewType(int i) {
            return 0;
        }

        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            return false;
        }

        public UsersAdapter(Context context2) {
            this.context = context2;
        }

        public int getItemCount() {
            int i;
            int size = JoinGroupAlert.this.chatInvite.participants.size();
            if (JoinGroupAlert.this.chatInvite.chat != null) {
                i = JoinGroupAlert.this.chatInvite.chat.participants_count;
            } else {
                i = JoinGroupAlert.this.chatInvite.participants_count;
            }
            return size != i ? size + 1 : size;
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            JoinSheetUserCell joinSheetUserCell = new JoinSheetUserCell(this.context);
            joinSheetUserCell.setLayoutParams(new RecyclerView.LayoutParams(AndroidUtilities.dp(100.0f), AndroidUtilities.dp(90.0f)));
            return new RecyclerListView.Holder(joinSheetUserCell);
        }

        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            int i2;
            JoinSheetUserCell joinSheetUserCell = (JoinSheetUserCell) viewHolder.itemView;
            if (i < JoinGroupAlert.this.chatInvite.participants.size()) {
                joinSheetUserCell.setUser(JoinGroupAlert.this.chatInvite.participants.get(i));
                return;
            }
            if (JoinGroupAlert.this.chatInvite.chat != null) {
                i2 = JoinGroupAlert.this.chatInvite.chat.participants_count;
            } else {
                i2 = JoinGroupAlert.this.chatInvite.participants_count;
            }
            joinSheetUserCell.setCount(i2 - JoinGroupAlert.this.chatInvite.participants.size());
        }
    }
}
