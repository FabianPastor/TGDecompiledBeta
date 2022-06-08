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
    public JoinGroupAlert(android.content.Context r24, org.telegram.tgnet.TLObject r25, java.lang.String r26, org.telegram.ui.ActionBar.BaseFragment r27) {
        /*
            r23 = this;
            r0 = r23
            r1 = r24
            r2 = r25
            r3 = 0
            r0.<init>(r1, r3)
            r0.setApplyBottomPadding(r3)
            r0.setApplyTopPadding(r3)
            r23.fixNavigationBar()
            r4 = r27
            r0.fragment = r4
            boolean r4 = r2 instanceof org.telegram.tgnet.TLRPC$ChatInvite
            if (r4 == 0) goto L_0x0020
            org.telegram.tgnet.TLRPC$ChatInvite r2 = (org.telegram.tgnet.TLRPC$ChatInvite) r2
            r0.chatInvite = r2
            goto L_0x0028
        L_0x0020:
            boolean r4 = r2 instanceof org.telegram.tgnet.TLRPC$Chat
            if (r4 == 0) goto L_0x0028
            org.telegram.tgnet.TLRPC$Chat r2 = (org.telegram.tgnet.TLRPC$Chat) r2
            r0.currentChat = r2
        L_0x0028:
            r2 = r26
            r0.hash = r2
            android.widget.LinearLayout r2 = new android.widget.LinearLayout
            r2.<init>(r1)
            r4 = 1
            r2.setOrientation(r4)
            r2.setClickable(r4)
            android.widget.FrameLayout r5 = new android.widget.FrameLayout
            r5.<init>(r1)
            r5.addView(r2)
            androidx.core.widget.NestedScrollView r6 = new androidx.core.widget.NestedScrollView
            r6.<init>(r1)
            r6.addView(r5)
            r0.setCustomView(r6)
            android.widget.ImageView r6 = new android.widget.ImageView
            r6.<init>(r1)
            java.lang.String r7 = "listSelectorSDK21"
            int r7 = r0.getThemedColor(r7)
            android.graphics.drawable.Drawable r7 = org.telegram.ui.ActionBar.Theme.createSelectorDrawable(r7)
            r6.setBackground(r7)
            java.lang.String r7 = "key_sheet_other"
            int r7 = org.telegram.ui.ActionBar.Theme.getColor(r7)
            r6.setColorFilter(r7)
            r7 = 2131165480(0x7var_, float:1.7945178E38)
            r6.setImageResource(r7)
            org.telegram.ui.Components.JoinGroupAlert$$ExternalSyntheticLambda2 r7 = new org.telegram.ui.Components.JoinGroupAlert$$ExternalSyntheticLambda2
            r7.<init>(r0)
            r6.setOnClickListener(r7)
            r7 = 1090519040(0x41000000, float:8.0)
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r7)
            r6.setPadding(r8, r8, r8, r8)
            r9 = 36
            r10 = 1108344832(0x42100000, float:36.0)
            r11 = 8388661(0x800035, float:1.1755018E-38)
            r12 = 1086324736(0x40CLASSNAME, float:6.0)
            r13 = 1090519040(0x41000000, float:8.0)
            r14 = 1086324736(0x40CLASSNAME, float:6.0)
            r15 = 0
            android.widget.FrameLayout$LayoutParams r8 = org.telegram.ui.Components.LayoutHelper.createFrame(r9, r10, r11, r12, r13, r14, r15)
            r5.addView(r6, r8)
            org.telegram.ui.Components.BackupImageView r5 = new org.telegram.ui.Components.BackupImageView
            r5.<init>(r1)
            r6 = 1108082688(0x420CLASSNAME, float:35.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            r5.setRoundRadius(r6)
            r8 = 70
            r9 = 70
            r10 = 49
            r11 = 0
            r12 = 29
            r13 = 0
            r14 = 0
            android.widget.LinearLayout$LayoutParams r6 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r8, (int) r9, (int) r10, (int) r11, (int) r12, (int) r13, (int) r14)
            r2.addView(r5, r6)
            org.telegram.tgnet.TLRPC$ChatInvite r6 = r0.chatInvite
            r8 = 0
            if (r6 == 0) goto L_0x0104
            org.telegram.tgnet.TLRPC$Chat r6 = r6.chat
            if (r6 == 0) goto L_0x00d1
            org.telegram.ui.Components.AvatarDrawable r6 = new org.telegram.ui.Components.AvatarDrawable
            org.telegram.tgnet.TLRPC$ChatInvite r8 = r0.chatInvite
            org.telegram.tgnet.TLRPC$Chat r8 = r8.chat
            r6.<init>((org.telegram.tgnet.TLRPC$Chat) r8)
            org.telegram.tgnet.TLRPC$ChatInvite r8 = r0.chatInvite
            org.telegram.tgnet.TLRPC$Chat r9 = r8.chat
            java.lang.String r10 = r9.title
            int r11 = r9.participants_count
            r5.setForUserOrChat(r9, r6, r8)
            r8 = r10
            goto L_0x00ff
        L_0x00d1:
            org.telegram.ui.Components.AvatarDrawable r6 = new org.telegram.ui.Components.AvatarDrawable
            r6.<init>()
            r9 = 0
            org.telegram.tgnet.TLRPC$ChatInvite r11 = r0.chatInvite
            java.lang.String r11 = r11.title
            r6.setInfo(r9, r11, r8)
            org.telegram.tgnet.TLRPC$ChatInvite r8 = r0.chatInvite
            java.lang.String r9 = r8.title
            int r11 = r8.participants_count
            org.telegram.tgnet.TLRPC$Photo r8 = r8.photo
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r8 = r8.sizes
            r10 = 50
            org.telegram.tgnet.TLRPC$PhotoSize r8 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r8, r10)
            org.telegram.tgnet.TLRPC$ChatInvite r10 = r0.chatInvite
            org.telegram.tgnet.TLRPC$Photo r10 = r10.photo
            org.telegram.messenger.ImageLocation r8 = org.telegram.messenger.ImageLocation.getForPhoto((org.telegram.tgnet.TLRPC$PhotoSize) r8, (org.telegram.tgnet.TLRPC$Photo) r10)
            org.telegram.tgnet.TLRPC$ChatInvite r10 = r0.chatInvite
            java.lang.String r12 = "50_50"
            r5.setImage((org.telegram.messenger.ImageLocation) r8, (java.lang.String) r12, (android.graphics.drawable.Drawable) r6, (java.lang.Object) r10)
            r8 = r9
        L_0x00ff:
            org.telegram.tgnet.TLRPC$ChatInvite r5 = r0.chatInvite
            java.lang.String r5 = r5.about
            goto L_0x013d
        L_0x0104:
            org.telegram.tgnet.TLRPC$Chat r6 = r0.currentChat
            if (r6 == 0) goto L_0x013b
            org.telegram.ui.Components.AvatarDrawable r6 = new org.telegram.ui.Components.AvatarDrawable
            org.telegram.tgnet.TLRPC$Chat r9 = r0.currentChat
            r6.<init>((org.telegram.tgnet.TLRPC$Chat) r9)
            org.telegram.tgnet.TLRPC$Chat r9 = r0.currentChat
            java.lang.String r9 = r9.title
            int r10 = r0.currentAccount
            org.telegram.messenger.MessagesController r10 = org.telegram.messenger.MessagesController.getInstance(r10)
            org.telegram.tgnet.TLRPC$Chat r11 = r0.currentChat
            long r11 = r11.id
            org.telegram.tgnet.TLRPC$ChatFull r10 = r10.getChatFull(r11)
            if (r10 == 0) goto L_0x0125
            java.lang.String r8 = r10.about
        L_0x0125:
            org.telegram.tgnet.TLRPC$Chat r11 = r0.currentChat
            int r11 = r11.participants_count
            if (r10 == 0) goto L_0x012e
            int r10 = r10.participants_count
            goto L_0x012f
        L_0x012e:
            r10 = 0
        L_0x012f:
            int r11 = java.lang.Math.max(r11, r10)
            org.telegram.tgnet.TLRPC$Chat r10 = r0.currentChat
            r5.setForUserOrChat(r10, r6, r10)
            r5 = r8
            r8 = r9
            goto L_0x013d
        L_0x013b:
            r5 = r8
            r11 = 0
        L_0x013d:
            android.widget.TextView r6 = new android.widget.TextView
            r6.<init>(r1)
            java.lang.String r9 = "fonts/rmedium.ttf"
            android.graphics.Typeface r10 = org.telegram.messenger.AndroidUtilities.getTypeface(r9)
            r6.setTypeface(r10)
            r10 = 1099431936(0x41880000, float:17.0)
            r6.setTextSize(r4, r10)
            java.lang.String r10 = "dialogTextBlack"
            int r12 = org.telegram.ui.ActionBar.Theme.getColor(r10)
            r6.setTextColor(r12)
            r6.setText(r8)
            r6.setSingleLine(r4)
            android.text.TextUtils$TruncateAt r8 = android.text.TextUtils.TruncateAt.END
            r6.setEllipsize(r8)
            r12 = -2
            r13 = -2
            r14 = 49
            r15 = 10
            r16 = 9
            r17 = 10
            if (r11 <= 0) goto L_0x0173
            r18 = 0
            goto L_0x0175
        L_0x0173:
            r18 = 20
        L_0x0175:
            android.widget.LinearLayout$LayoutParams r12 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r12, (int) r13, (int) r14, (int) r15, (int) r16, (int) r17, (int) r18)
            r2.addView(r6, r12)
            org.telegram.tgnet.TLRPC$ChatInvite r6 = r0.chatInvite
            if (r6 == 0) goto L_0x0190
            boolean r12 = r6.channel
            if (r12 == 0) goto L_0x0188
            boolean r12 = r6.megagroup
            if (r12 == 0) goto L_0x019e
        L_0x0188:
            org.telegram.tgnet.TLRPC$Chat r6 = r6.chat
            boolean r6 = org.telegram.messenger.ChatObject.isChannelAndNotMegaGroup(r6)
            if (r6 != 0) goto L_0x019e
        L_0x0190:
            org.telegram.tgnet.TLRPC$Chat r6 = r0.currentChat
            boolean r6 = org.telegram.messenger.ChatObject.isChannel(r6)
            if (r6 == 0) goto L_0x01a0
            org.telegram.tgnet.TLRPC$Chat r6 = r0.currentChat
            boolean r6 = r6.megagroup
            if (r6 != 0) goto L_0x01a0
        L_0x019e:
            r6 = 1
            goto L_0x01a1
        L_0x01a0:
            r6 = 0
        L_0x01a1:
            boolean r12 = android.text.TextUtils.isEmpty(r5)
            r12 = r12 ^ r4
            java.lang.String r13 = "dialogTextGray3"
            r14 = 1096810496(0x41600000, float:14.0)
            if (r11 <= 0) goto L_0x01f6
            android.widget.TextView r15 = new android.widget.TextView
            r15.<init>(r1)
            r15.setTextSize(r4, r14)
            int r8 = org.telegram.ui.ActionBar.Theme.getColor(r13)
            r15.setTextColor(r8)
            r15.setSingleLine(r4)
            android.text.TextUtils$TruncateAt r8 = android.text.TextUtils.TruncateAt.END
            r15.setEllipsize(r8)
            if (r6 == 0) goto L_0x01d1
            java.lang.Object[] r8 = new java.lang.Object[r3]
            java.lang.String r14 = "Subscribers"
            java.lang.String r8 = org.telegram.messenger.LocaleController.formatPluralString(r14, r11, r8)
            r15.setText(r8)
            goto L_0x01dc
        L_0x01d1:
            java.lang.Object[] r8 = new java.lang.Object[r3]
            java.lang.String r14 = "Members"
            java.lang.String r8 = org.telegram.messenger.LocaleController.formatPluralString(r14, r11, r8)
            r15.setText(r8)
        L_0x01dc:
            r16 = -2
            r17 = -2
            r18 = 49
            r19 = 10
            r20 = 3
            r21 = 10
            if (r12 == 0) goto L_0x01ed
            r22 = 0
            goto L_0x01ef
        L_0x01ed:
            r22 = 20
        L_0x01ef:
            android.widget.LinearLayout$LayoutParams r8 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r16, (int) r17, (int) r18, (int) r19, (int) r20, (int) r21, (int) r22)
            r2.addView(r15, r8)
        L_0x01f6:
            r8 = 1097859072(0x41700000, float:15.0)
            r11 = 17
            if (r12 == 0) goto L_0x0224
            android.widget.TextView r12 = new android.widget.TextView
            r12.<init>(r1)
            r12.setGravity(r11)
            r12.setText(r5)
            int r5 = org.telegram.ui.ActionBar.Theme.getColor(r10)
            r12.setTextColor(r5)
            r12.setTextSize(r4, r8)
            r14 = -1
            r15 = -2
            r16 = 48
            r17 = 24
            r18 = 10
            r19 = 24
            r20 = 20
            android.widget.LinearLayout$LayoutParams r5 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r14, (int) r15, (int) r16, (int) r17, (int) r18, (int) r19, (int) r20)
            r2.addView(r12, r5)
        L_0x0224:
            org.telegram.tgnet.TLRPC$ChatInvite r5 = r0.chatInvite
            r10 = 48
            r12 = -1
            if (r5 == 0) goto L_0x034b
            boolean r14 = r5.request_needed
            if (r14 == 0) goto L_0x0231
            goto L_0x034b
        L_0x0231:
            if (r5 == 0) goto L_0x044e
            java.util.ArrayList<org.telegram.tgnet.TLRPC$User> r4 = r5.participants
            boolean r4 = r4.isEmpty()
            if (r4 != 0) goto L_0x0284
            org.telegram.ui.Components.RecyclerListView r4 = new org.telegram.ui.Components.RecyclerListView
            r4.<init>(r1)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r7)
            r4.setPadding(r3, r3, r3, r5)
            r4.setNestedScrollingEnabled(r3)
            r4.setClipToPadding(r3)
            androidx.recyclerview.widget.LinearLayoutManager r5 = new androidx.recyclerview.widget.LinearLayoutManager
            android.content.Context r6 = r23.getContext()
            r5.<init>(r6, r3, r3)
            r4.setLayoutManager(r5)
            r4.setHorizontalScrollBarEnabled(r3)
            r4.setVerticalScrollBarEnabled(r3)
            org.telegram.ui.Components.JoinGroupAlert$UsersAdapter r5 = new org.telegram.ui.Components.JoinGroupAlert$UsersAdapter
            r5.<init>(r1)
            r4.setAdapter(r5)
            java.lang.String r5 = "dialogScrollGlow"
            int r5 = org.telegram.ui.ActionBar.Theme.getColor(r5)
            r4.setGlowColor(r5)
            r13 = -2
            r14 = 90
            r15 = 49
            r16 = 0
            r17 = 0
            r18 = 0
            r19 = 7
            android.widget.LinearLayout$LayoutParams r5 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r13, (int) r14, (int) r15, (int) r16, (int) r17, (int) r18, (int) r19)
            r2.addView(r4, r5)
        L_0x0284:
            android.view.View r4 = new android.view.View
            r4.<init>(r1)
            java.lang.String r5 = "dialogShadowLine"
            int r5 = org.telegram.ui.ActionBar.Theme.getColor(r5)
            r4.setBackgroundColor(r5)
            android.widget.LinearLayout$LayoutParams r5 = new android.widget.LinearLayout$LayoutParams
            int r6 = org.telegram.messenger.AndroidUtilities.getShadowHeight()
            r5.<init>(r12, r6)
            r2.addView(r4, r5)
            org.telegram.ui.Components.PickerBottomLayout r4 = new org.telegram.ui.Components.PickerBottomLayout
            r4.<init>(r1, r3)
            r1 = 83
            android.widget.FrameLayout$LayoutParams r1 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r12, (int) r10, (int) r1)
            r2.addView(r4, r1)
            android.widget.TextView r1 = r4.cancelButton
            r2 = 1099956224(0x41900000, float:18.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r2)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r2)
            r1.setPadding(r5, r3, r6, r3)
            android.widget.TextView r1 = r4.cancelButton
            java.lang.String r5 = "dialogTextBlue2"
            int r6 = org.telegram.ui.ActionBar.Theme.getColor(r5)
            r1.setTextColor(r6)
            android.widget.TextView r1 = r4.cancelButton
            r6 = 2131624813(0x7f0e036d, float:1.8876816E38)
            java.lang.String r7 = "Cancel"
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r7, r6)
            java.lang.String r6 = r6.toUpperCase()
            r1.setText(r6)
            android.widget.TextView r1 = r4.cancelButton
            org.telegram.ui.Components.JoinGroupAlert$$ExternalSyntheticLambda3 r6 = new org.telegram.ui.Components.JoinGroupAlert$$ExternalSyntheticLambda3
            r6.<init>(r0)
            r1.setOnClickListener(r6)
            android.widget.LinearLayout r1 = r4.doneButton
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r2)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            r1.setPadding(r6, r3, r2, r3)
            android.widget.LinearLayout r1 = r4.doneButton
            r1.setVisibility(r3)
            android.widget.TextView r1 = r4.doneButtonBadgeTextView
            r2 = 8
            r1.setVisibility(r2)
            android.widget.TextView r1 = r4.doneButtonTextView
            int r2 = org.telegram.ui.ActionBar.Theme.getColor(r5)
            r1.setTextColor(r2)
            org.telegram.tgnet.TLRPC$ChatInvite r1 = r0.chatInvite
            boolean r2 = r1.channel
            if (r2 == 0) goto L_0x030e
            boolean r2 = r1.megagroup
            if (r2 == 0) goto L_0x031e
        L_0x030e:
            org.telegram.tgnet.TLRPC$Chat r1 = r1.chat
            boolean r1 = org.telegram.messenger.ChatObject.isChannel(r1)
            if (r1 == 0) goto L_0x0331
            org.telegram.tgnet.TLRPC$ChatInvite r1 = r0.chatInvite
            org.telegram.tgnet.TLRPC$Chat r1 = r1.chat
            boolean r1 = r1.megagroup
            if (r1 != 0) goto L_0x0331
        L_0x031e:
            android.widget.TextView r1 = r4.doneButtonTextView
            r2 = 2131627726(0x7f0e0ece, float:1.8882725E38)
            java.lang.String r3 = "ProfileJoinChannel"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            java.lang.String r2 = r2.toUpperCase()
            r1.setText(r2)
            goto L_0x033f
        L_0x0331:
            android.widget.TextView r1 = r4.doneButtonTextView
            r2 = 2131626308(0x7f0e0944, float:1.8879849E38)
            java.lang.String r3 = "JoinGroup"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r1.setText(r2)
        L_0x033f:
            android.widget.LinearLayout r1 = r4.doneButton
            org.telegram.ui.Components.JoinGroupAlert$$ExternalSyntheticLambda4 r2 = new org.telegram.ui.Components.JoinGroupAlert$$ExternalSyntheticLambda4
            r2.<init>(r0)
            r1.setOnClickListener(r2)
            goto L_0x044e
        L_0x034b:
            android.widget.FrameLayout r1 = new android.widget.FrameLayout
            android.content.Context r3 = r23.getContext()
            r1.<init>(r3)
            r3 = -2
            android.widget.LinearLayout$LayoutParams r3 = org.telegram.ui.Components.LayoutHelper.createLinear(r12, r3)
            r2.addView(r1, r3)
            org.telegram.ui.Components.RadialProgressView r3 = new org.telegram.ui.Components.RadialProgressView
            android.content.Context r5 = r23.getContext()
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r7 = r0.resourcesProvider
            r3.<init>(r5, r7)
            r0.requestProgressView = r3
            java.lang.String r5 = "featuredStickers_addButton"
            int r7 = r0.getThemedColor(r5)
            r3.setProgressColor(r7)
            org.telegram.ui.Components.RadialProgressView r3 = r0.requestProgressView
            r7 = 1107296256(0x42000000, float:32.0)
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
            r3.setSize(r7)
            org.telegram.ui.Components.RadialProgressView r3 = r0.requestProgressView
            r7 = 4
            r3.setVisibility(r7)
            org.telegram.ui.Components.RadialProgressView r3 = r0.requestProgressView
            android.widget.FrameLayout$LayoutParams r7 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r10, (int) r10, (int) r11)
            r1.addView(r3, r7)
            android.widget.TextView r3 = new android.widget.TextView
            android.content.Context r7 = r23.getContext()
            r3.<init>(r7)
            r0.requestTextView = r3
            r7 = 1086324736(0x40CLASSNAME, float:6.0)
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
            int r5 = r0.getThemedColor(r5)
            java.lang.String r10 = "featuredStickers_addButtonPressed"
            int r10 = r0.getThemedColor(r10)
            android.graphics.drawable.Drawable r5 = org.telegram.ui.ActionBar.Theme.createSimpleSelectorRoundRectDrawable(r7, r5, r10)
            r3.setBackground(r5)
            android.widget.TextView r3 = r0.requestTextView
            android.text.TextUtils$TruncateAt r5 = android.text.TextUtils.TruncateAt.END
            r3.setEllipsize(r5)
            android.widget.TextView r3 = r0.requestTextView
            r3.setGravity(r11)
            android.widget.TextView r3 = r0.requestTextView
            r3.setSingleLine(r4)
            android.widget.TextView r3 = r0.requestTextView
            if (r6 == 0) goto L_0x03c9
            r5 = 2131627955(0x7f0e0fb3, float:1.888319E38)
            java.lang.String r7 = "RequestToJoinChannel"
            goto L_0x03ce
        L_0x03c9:
            r5 = 2131627959(0x7f0e0fb7, float:1.8883197E38)
            java.lang.String r7 = "RequestToJoinGroup"
        L_0x03ce:
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r7, r5)
            r3.setText(r5)
            android.widget.TextView r3 = r0.requestTextView
            java.lang.String r5 = "featuredStickers_buttonText"
            int r5 = r0.getThemedColor(r5)
            r3.setTextColor(r5)
            android.widget.TextView r3 = r0.requestTextView
            r3.setTextSize(r4, r8)
            android.widget.TextView r3 = r0.requestTextView
            android.graphics.Typeface r5 = org.telegram.messenger.AndroidUtilities.getTypeface(r9)
            r3.setTypeface(r5)
            android.widget.TextView r3 = r0.requestTextView
            org.telegram.ui.Components.JoinGroupAlert$$ExternalSyntheticLambda5 r5 = new org.telegram.ui.Components.JoinGroupAlert$$ExternalSyntheticLambda5
            r5.<init>(r0, r6)
            r3.setOnClickListener(r5)
            android.widget.TextView r3 = r0.requestTextView
            r14 = -1
            r15 = 48
            r16 = 8388611(0x800003, float:1.1754948E-38)
            r17 = 16
            r18 = 0
            r19 = 16
            r20 = 0
            android.widget.LinearLayout$LayoutParams r5 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r14, (int) r15, (int) r16, (int) r17, (int) r18, (int) r19, (int) r20)
            r1.addView(r3, r5)
            android.widget.TextView r1 = new android.widget.TextView
            android.content.Context r3 = r23.getContext()
            r1.<init>(r3)
            r1.setGravity(r11)
            r3 = 1096810496(0x41600000, float:14.0)
            r1.setTextSize(r4, r3)
            if (r6 == 0) goto L_0x0428
            r3 = 2131627957(0x7f0e0fb5, float:1.8883193E38)
            java.lang.String r4 = "RequestToJoinChannelDescription"
            goto L_0x042d
        L_0x0428:
            r3 = 2131627961(0x7f0e0fb9, float:1.8883201E38)
            java.lang.String r4 = "RequestToJoinGroupDescription"
        L_0x042d:
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            r1.setText(r3)
            int r3 = org.telegram.ui.ActionBar.Theme.getColor(r13)
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
        L_0x044e:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.JoinGroupAlert.<init>(android.content.Context, org.telegram.tgnet.TLObject, java.lang.String, org.telegram.ui.ActionBar.BaseFragment):void");
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
