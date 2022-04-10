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
    private BaseFragment fragment;
    private String hash;
    private RadialProgressView requestProgressView;
    private TextView requestTextView;

    /* JADX WARNING: Illegal instructions before constructor call */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public JoinGroupAlert(android.content.Context r24, org.telegram.tgnet.TLRPC$ChatInvite r25, java.lang.String r26, org.telegram.ui.ActionBar.BaseFragment r27) {
        /*
            r23 = this;
            r0 = r23
            r1 = r24
            r2 = r25
            r3 = 0
            r0.<init>(r1, r3)
            r0.setApplyBottomPadding(r3)
            r0.setApplyTopPadding(r3)
            r4 = r27
            r0.fragment = r4
            r0.chatInvite = r2
            r4 = r26
            r0.hash = r4
            android.widget.LinearLayout r4 = new android.widget.LinearLayout
            r4.<init>(r1)
            r5 = 1
            r4.setOrientation(r5)
            r4.setClickable(r5)
            android.widget.FrameLayout r6 = new android.widget.FrameLayout
            r6.<init>(r1)
            r6.addView(r4)
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
            int r8 = org.telegram.ui.ActionBar.Theme.getColor(r8)
            r7.setColorFilter(r8)
            r8 = 2131165540(0x7var_, float:1.79453E38)
            r7.setImageResource(r8)
            org.telegram.ui.Components.JoinGroupAlert$$ExternalSyntheticLambda1 r8 = new org.telegram.ui.Components.JoinGroupAlert$$ExternalSyntheticLambda1
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
            r4.addView(r6, r7)
            org.telegram.tgnet.TLRPC$Chat r7 = r2.chat
            if (r7 == 0) goto L_0x00b6
            org.telegram.ui.Components.AvatarDrawable r7 = new org.telegram.ui.Components.AvatarDrawable
            org.telegram.tgnet.TLRPC$Chat r9 = r2.chat
            r7.<init>((org.telegram.tgnet.TLRPC$Chat) r9)
            org.telegram.tgnet.TLRPC$Chat r9 = r2.chat
            java.lang.String r10 = r9.title
            int r11 = r9.participants_count
            r6.setForUserOrChat(r9, r7, r2)
            goto L_0x00dc
        L_0x00b6:
            org.telegram.ui.Components.AvatarDrawable r7 = new org.telegram.ui.Components.AvatarDrawable
            r7.<init>()
            r9 = 0
            java.lang.String r11 = r2.title
            r12 = 0
            r7.setInfo(r9, r11, r12)
            java.lang.String r10 = r2.title
            int r11 = r2.participants_count
            org.telegram.tgnet.TLRPC$Photo r9 = r2.photo
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r9 = r9.sizes
            r12 = 50
            org.telegram.tgnet.TLRPC$PhotoSize r9 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r9, r12)
            org.telegram.tgnet.TLRPC$Photo r12 = r2.photo
            org.telegram.messenger.ImageLocation r9 = org.telegram.messenger.ImageLocation.getForPhoto((org.telegram.tgnet.TLRPC$PhotoSize) r9, (org.telegram.tgnet.TLRPC$Photo) r12)
            java.lang.String r12 = "50_50"
            r6.setImage((org.telegram.messenger.ImageLocation) r9, (java.lang.String) r12, (android.graphics.drawable.Drawable) r7, (java.lang.Object) r2)
        L_0x00dc:
            android.widget.TextView r6 = new android.widget.TextView
            r6.<init>(r1)
            java.lang.String r7 = "fonts/rmedium.ttf"
            android.graphics.Typeface r9 = org.telegram.messenger.AndroidUtilities.getTypeface(r7)
            r6.setTypeface(r9)
            r9 = 1099431936(0x41880000, float:17.0)
            r6.setTextSize(r5, r9)
            java.lang.String r9 = "dialogTextBlack"
            int r12 = org.telegram.ui.ActionBar.Theme.getColor(r9)
            r6.setTextColor(r12)
            r6.setText(r10)
            r6.setSingleLine(r5)
            android.text.TextUtils$TruncateAt r10 = android.text.TextUtils.TruncateAt.END
            r6.setEllipsize(r10)
            r12 = -2
            r13 = -2
            r14 = 49
            r15 = 10
            r16 = 9
            r17 = 10
            if (r11 <= 0) goto L_0x0112
            r18 = 0
            goto L_0x0114
        L_0x0112:
            r18 = 20
        L_0x0114:
            android.widget.LinearLayout$LayoutParams r12 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r12, (int) r13, (int) r14, (int) r15, (int) r16, (int) r17, (int) r18)
            r4.addView(r6, r12)
            boolean r6 = r2.channel
            if (r6 == 0) goto L_0x0123
            boolean r6 = r2.megagroup
            if (r6 == 0) goto L_0x012b
        L_0x0123:
            org.telegram.tgnet.TLRPC$Chat r6 = r2.chat
            boolean r6 = org.telegram.messenger.ChatObject.isChannelAndNotMegaGroup(r6)
            if (r6 == 0) goto L_0x012d
        L_0x012b:
            r6 = 1
            goto L_0x012e
        L_0x012d:
            r6 = 0
        L_0x012e:
            java.lang.String r12 = r2.about
            boolean r12 = android.text.TextUtils.isEmpty(r12)
            r12 = r12 ^ r5
            java.lang.String r13 = "dialogTextGray3"
            r14 = 1096810496(0x41600000, float:14.0)
            if (r11 <= 0) goto L_0x0181
            android.widget.TextView r15 = new android.widget.TextView
            r15.<init>(r1)
            r15.setTextSize(r5, r14)
            int r10 = org.telegram.ui.ActionBar.Theme.getColor(r13)
            r15.setTextColor(r10)
            r15.setSingleLine(r5)
            android.text.TextUtils$TruncateAt r10 = android.text.TextUtils.TruncateAt.END
            r15.setEllipsize(r10)
            if (r6 == 0) goto L_0x015e
            java.lang.String r10 = "Subscribers"
            java.lang.String r10 = org.telegram.messenger.LocaleController.formatPluralString(r10, r11)
            r15.setText(r10)
            goto L_0x0167
        L_0x015e:
            java.lang.String r10 = "Members"
            java.lang.String r10 = org.telegram.messenger.LocaleController.formatPluralString(r10, r11)
            r15.setText(r10)
        L_0x0167:
            r16 = -2
            r17 = -2
            r18 = 49
            r19 = 10
            r20 = 3
            r21 = 10
            if (r12 == 0) goto L_0x0178
            r22 = 0
            goto L_0x017a
        L_0x0178:
            r22 = 20
        L_0x017a:
            android.widget.LinearLayout$LayoutParams r10 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r16, (int) r17, (int) r18, (int) r19, (int) r20, (int) r21, (int) r22)
            r4.addView(r15, r10)
        L_0x0181:
            r10 = 1097859072(0x41700000, float:15.0)
            r11 = 17
            if (r12 == 0) goto L_0x01b2
            android.widget.TextView r12 = new android.widget.TextView
            r12.<init>(r1)
            r12.setGravity(r11)
            java.lang.String r15 = r2.about
            r12.setText(r15)
            int r9 = org.telegram.ui.ActionBar.Theme.getColor(r9)
            r12.setTextColor(r9)
            r12.setTextSize(r5, r10)
            r15 = -1
            r16 = -2
            r17 = 48
            r18 = 24
            r19 = 10
            r20 = 24
            r21 = 20
            android.widget.LinearLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r15, (int) r16, (int) r17, (int) r18, (int) r19, (int) r20, (int) r21)
            r4.addView(r12, r9)
        L_0x01b2:
            boolean r9 = r2.request_needed
            r12 = 48
            r15 = -1
            if (r9 == 0) goto L_0x02bc
            android.widget.FrameLayout r2 = new android.widget.FrameLayout
            android.content.Context r3 = r23.getContext()
            r2.<init>(r3)
            r3 = -2
            android.widget.LinearLayout$LayoutParams r3 = org.telegram.ui.Components.LayoutHelper.createLinear(r15, r3)
            r4.addView(r2, r3)
            org.telegram.ui.Components.RadialProgressView r3 = new org.telegram.ui.Components.RadialProgressView
            android.content.Context r8 = r23.getContext()
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r9 = r0.resourcesProvider
            r3.<init>(r8, r9)
            r0.requestProgressView = r3
            java.lang.String r8 = "featuredStickers_addButton"
            int r9 = r0.getThemedColor(r8)
            r3.setProgressColor(r9)
            org.telegram.ui.Components.RadialProgressView r3 = r0.requestProgressView
            r9 = 1107296256(0x42000000, float:32.0)
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r9)
            r3.setSize(r9)
            org.telegram.ui.Components.RadialProgressView r3 = r0.requestProgressView
            r9 = 4
            r3.setVisibility(r9)
            org.telegram.ui.Components.RadialProgressView r3 = r0.requestProgressView
            android.widget.FrameLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r12, (int) r12, (int) r11)
            r2.addView(r3, r9)
            android.widget.TextView r3 = new android.widget.TextView
            android.content.Context r9 = r23.getContext()
            r3.<init>(r9)
            r0.requestTextView = r3
            r9 = 1086324736(0x40CLASSNAME, float:6.0)
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r9)
            int r8 = r0.getThemedColor(r8)
            java.lang.String r12 = "featuredStickers_addButtonPressed"
            int r12 = r0.getThemedColor(r12)
            android.graphics.drawable.Drawable r8 = org.telegram.ui.ActionBar.Theme.createSimpleSelectorRoundRectDrawable(r9, r8, r12)
            r3.setBackground(r8)
            android.widget.TextView r3 = r0.requestTextView
            android.text.TextUtils$TruncateAt r8 = android.text.TextUtils.TruncateAt.END
            r3.setEllipsize(r8)
            android.widget.TextView r3 = r0.requestTextView
            r3.setGravity(r11)
            android.widget.TextView r3 = r0.requestTextView
            r3.setSingleLine(r5)
            android.widget.TextView r3 = r0.requestTextView
            if (r6 == 0) goto L_0x0237
            r8 = 2131627763(0x7f0e0ef3, float:1.88828E38)
            java.lang.String r9 = "RequestToJoinChannel"
            goto L_0x023c
        L_0x0237:
            r8 = 2131627767(0x7f0e0ef7, float:1.8882808E38)
            java.lang.String r9 = "RequestToJoinGroup"
        L_0x023c:
            java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r9, r8)
            r3.setText(r8)
            android.widget.TextView r3 = r0.requestTextView
            java.lang.String r8 = "featuredStickers_buttonText"
            int r8 = r0.getThemedColor(r8)
            r3.setTextColor(r8)
            android.widget.TextView r3 = r0.requestTextView
            r3.setTextSize(r5, r10)
            android.widget.TextView r3 = r0.requestTextView
            android.graphics.Typeface r7 = org.telegram.messenger.AndroidUtilities.getTypeface(r7)
            r3.setTypeface(r7)
            android.widget.TextView r3 = r0.requestTextView
            org.telegram.ui.Components.JoinGroupAlert$$ExternalSyntheticLambda4 r7 = new org.telegram.ui.Components.JoinGroupAlert$$ExternalSyntheticLambda4
            r7.<init>(r0, r1, r6)
            r3.setOnClickListener(r7)
            android.widget.TextView r1 = r0.requestTextView
            r15 = -1
            r16 = 48
            r17 = 8388611(0x800003, float:1.1754948E-38)
            r18 = 16
            r19 = 0
            r20 = 16
            r21 = 0
            android.widget.LinearLayout$LayoutParams r3 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r15, (int) r16, (int) r17, (int) r18, (int) r19, (int) r20, (int) r21)
            r2.addView(r1, r3)
            android.widget.TextView r1 = new android.widget.TextView
            android.content.Context r2 = r23.getContext()
            r1.<init>(r2)
            r1.setGravity(r11)
            r1.setTextSize(r5, r14)
            if (r6 == 0) goto L_0x0294
            r2 = 2131627765(0x7f0e0ef5, float:1.8882804E38)
            java.lang.String r3 = "RequestToJoinChannelDescription"
            goto L_0x0299
        L_0x0294:
            r2 = 2131627769(0x7f0e0ef9, float:1.8882812E38)
            java.lang.String r3 = "RequestToJoinGroupDescription"
        L_0x0299:
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r1.setText(r2)
            int r2 = org.telegram.ui.ActionBar.Theme.getColor(r13)
            r1.setTextColor(r2)
            r5 = -1
            r6 = -2
            r7 = 48
            r8 = 24
            r9 = 17
            r10 = 24
            r11 = 15
            android.widget.LinearLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r5, (int) r6, (int) r7, (int) r8, (int) r9, (int) r10, (int) r11)
            r4.addView(r1, r2)
            goto L_0x03cf
        L_0x02bc:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$User> r5 = r2.participants
            boolean r5 = r5.isEmpty()
            if (r5 != 0) goto L_0x030e
            org.telegram.ui.Components.RecyclerListView r5 = new org.telegram.ui.Components.RecyclerListView
            r5.<init>(r1)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r8)
            r5.setPadding(r3, r3, r3, r6)
            r5.setNestedScrollingEnabled(r3)
            r5.setClipToPadding(r3)
            androidx.recyclerview.widget.LinearLayoutManager r6 = new androidx.recyclerview.widget.LinearLayoutManager
            android.content.Context r7 = r23.getContext()
            r6.<init>(r7, r3, r3)
            r5.setLayoutManager(r6)
            r5.setHorizontalScrollBarEnabled(r3)
            r5.setVerticalScrollBarEnabled(r3)
            org.telegram.ui.Components.JoinGroupAlert$UsersAdapter r6 = new org.telegram.ui.Components.JoinGroupAlert$UsersAdapter
            r6.<init>(r1)
            r5.setAdapter(r6)
            java.lang.String r6 = "dialogScrollGlow"
            int r6 = org.telegram.ui.ActionBar.Theme.getColor(r6)
            r5.setGlowColor(r6)
            r16 = -2
            r17 = 90
            r18 = 49
            r19 = 0
            r20 = 0
            r21 = 0
            r22 = 7
            android.widget.LinearLayout$LayoutParams r6 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r16, (int) r17, (int) r18, (int) r19, (int) r20, (int) r21, (int) r22)
            r4.addView(r5, r6)
        L_0x030e:
            android.view.View r5 = new android.view.View
            r5.<init>(r1)
            java.lang.String r6 = "dialogShadowLine"
            int r6 = org.telegram.ui.ActionBar.Theme.getColor(r6)
            r5.setBackgroundColor(r6)
            android.widget.LinearLayout$LayoutParams r6 = new android.widget.LinearLayout$LayoutParams
            int r7 = org.telegram.messenger.AndroidUtilities.getShadowHeight()
            r6.<init>(r15, r7)
            r4.addView(r5, r6)
            org.telegram.ui.Components.PickerBottomLayout r5 = new org.telegram.ui.Components.PickerBottomLayout
            r5.<init>(r1, r3)
            r1 = 83
            android.widget.FrameLayout$LayoutParams r1 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r15, (int) r12, (int) r1)
            r4.addView(r5, r1)
            android.widget.TextView r1 = r5.cancelButton
            r4 = 1099956224(0x41900000, float:18.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r4)
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r4)
            r1.setPadding(r6, r3, r7, r3)
            android.widget.TextView r1 = r5.cancelButton
            java.lang.String r6 = "dialogTextBlue2"
            int r7 = org.telegram.ui.ActionBar.Theme.getColor(r6)
            r1.setTextColor(r7)
            android.widget.TextView r1 = r5.cancelButton
            r7 = 2131624750(0x7f0e032e, float:1.8876689E38)
            java.lang.String r8 = "Cancel"
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r8, r7)
            java.lang.String r7 = r7.toUpperCase()
            r1.setText(r7)
            android.widget.TextView r1 = r5.cancelButton
            org.telegram.ui.Components.JoinGroupAlert$$ExternalSyntheticLambda3 r7 = new org.telegram.ui.Components.JoinGroupAlert$$ExternalSyntheticLambda3
            r7.<init>(r0)
            r1.setOnClickListener(r7)
            android.widget.LinearLayout r1 = r5.doneButton
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r4)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            r1.setPadding(r7, r3, r4, r3)
            android.widget.LinearLayout r1 = r5.doneButton
            r1.setVisibility(r3)
            android.widget.TextView r1 = r5.doneButtonBadgeTextView
            r3 = 8
            r1.setVisibility(r3)
            android.widget.TextView r1 = r5.doneButtonTextView
            int r3 = org.telegram.ui.ActionBar.Theme.getColor(r6)
            r1.setTextColor(r3)
            boolean r1 = r2.channel
            if (r1 == 0) goto L_0x0396
            boolean r1 = r2.megagroup
            if (r1 == 0) goto L_0x03a4
        L_0x0396:
            org.telegram.tgnet.TLRPC$Chat r1 = r2.chat
            boolean r1 = org.telegram.messenger.ChatObject.isChannel(r1)
            if (r1 == 0) goto L_0x03b7
            org.telegram.tgnet.TLRPC$Chat r1 = r2.chat
            boolean r1 = r1.megagroup
            if (r1 != 0) goto L_0x03b7
        L_0x03a4:
            android.widget.TextView r1 = r5.doneButtonTextView
            r2 = 2131627538(0x7f0e0e12, float:1.8882343E38)
            java.lang.String r3 = "ProfileJoinChannel"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            java.lang.String r2 = r2.toUpperCase()
            r1.setText(r2)
            goto L_0x03c5
        L_0x03b7:
            android.widget.TextView r1 = r5.doneButtonTextView
            r2 = 2131626191(0x7f0e08cf, float:1.8879611E38)
            java.lang.String r3 = "JoinGroup"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r1.setText(r2)
        L_0x03c5:
            android.widget.LinearLayout r1 = r5.doneButton
            org.telegram.ui.Components.JoinGroupAlert$$ExternalSyntheticLambda2 r2 = new org.telegram.ui.Components.JoinGroupAlert$$ExternalSyntheticLambda2
            r2.<init>(r0)
            r1.setOnClickListener(r2)
        L_0x03cf:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.JoinGroupAlert.<init>(android.content.Context, org.telegram.tgnet.TLRPC$ChatInvite, java.lang.String, org.telegram.ui.ActionBar.BaseFragment):void");
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0(View view) {
        dismiss();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$5(Context context, boolean z, View view) {
        AndroidUtilities.runOnUIThread(new JoinGroupAlert$$ExternalSyntheticLambda5(this), 400);
        TLRPC$TL_messages_importChatInvite tLRPC$TL_messages_importChatInvite = new TLRPC$TL_messages_importChatInvite();
        tLRPC$TL_messages_importChatInvite.hash = this.hash;
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_messages_importChatInvite, new JoinGroupAlert$$ExternalSyntheticLambda8(this, context, z, tLRPC$TL_messages_importChatInvite), 2);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$1() {
        if (!isDismissed()) {
            this.requestTextView.setVisibility(4);
            this.requestProgressView.setVisibility(0);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$4(Context context, boolean z, TLRPC$TL_messages_importChatInvite tLRPC$TL_messages_importChatInvite, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new JoinGroupAlert$$ExternalSyntheticLambda6(this, tLRPC$TL_error, context, z, tLRPC$TL_messages_importChatInvite));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$3(TLRPC$TL_error tLRPC$TL_error, Context context, boolean z, TLRPC$TL_messages_importChatInvite tLRPC$TL_messages_importChatInvite) {
        BaseFragment baseFragment = this.fragment;
        if (baseFragment != null && baseFragment.getParentActivity() != null) {
            if (tLRPC$TL_error != null) {
                if ("INVITE_REQUEST_SENT".equals(tLRPC$TL_error.text)) {
                    setOnDismissListener(new JoinGroupAlert$$ExternalSyntheticLambda0(this, context, z));
                } else {
                    AlertsCreator.processError(this.currentAccount, tLRPC$TL_error, this.fragment, tLRPC$TL_messages_importChatInvite, new Object[0]);
                }
            }
            dismiss();
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$2(Context context, boolean z, DialogInterface dialogInterface) {
        String str;
        Bulletin.TwoLineLottieLayout twoLineLottieLayout = new Bulletin.TwoLineLottieLayout(context, this.fragment.getResourceProvider());
        twoLineLottieLayout.imageView.setAnimation(NUM, 28, 28);
        twoLineLottieLayout.titleTextView.setText(LocaleController.getString("RequestToJoinSent", NUM));
        if (z) {
            str = LocaleController.getString("RequestToJoinChannelSentDescription", NUM);
        } else {
            str = LocaleController.getString("RequestToJoinGroupSentDescription", NUM);
        }
        twoLineLottieLayout.subtitleTextView.setText(str);
        Bulletin.make(this.fragment, (Bulletin.Layout) twoLineLottieLayout, 2750).show();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$6(View view) {
        dismiss();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$9(View view) {
        dismiss();
        TLRPC$TL_messages_importChatInvite tLRPC$TL_messages_importChatInvite = new TLRPC$TL_messages_importChatInvite();
        tLRPC$TL_messages_importChatInvite.hash = this.hash;
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_messages_importChatInvite, new JoinGroupAlert$$ExternalSyntheticLambda9(this, tLRPC$TL_messages_importChatInvite), 2);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$8(TLRPC$TL_messages_importChatInvite tLRPC$TL_messages_importChatInvite, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        if (tLRPC$TL_error == null) {
            MessagesController.getInstance(this.currentAccount).processUpdates((TLRPC$Updates) tLObject, false);
        }
        AndroidUtilities.runOnUIThread(new JoinGroupAlert$$ExternalSyntheticLambda7(this, tLRPC$TL_error, tLObject, tLRPC$TL_messages_importChatInvite));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$7(TLRPC$TL_error tLRPC$TL_error, TLObject tLObject, TLRPC$TL_messages_importChatInvite tLRPC$TL_messages_importChatInvite) {
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
