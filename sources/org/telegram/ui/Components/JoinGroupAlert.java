package org.telegram.ui.Components;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import androidx.recyclerview.widget.RecyclerView;
import org.telegram.messenger.AndroidUtilities;
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
import org.telegram.ui.Components.RecyclerListView;

public class JoinGroupAlert extends BottomSheet {
    /* access modifiers changed from: private */
    public TLRPC$ChatInvite chatInvite;
    private BaseFragment fragment;
    private String hash;

    /* JADX WARNING: Illegal instructions before constructor call */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public JoinGroupAlert(android.content.Context r19, org.telegram.tgnet.TLRPC$ChatInvite r20, java.lang.String r21, org.telegram.ui.ActionBar.BaseFragment r22) {
        /*
            r18 = this;
            r0 = r18
            r1 = r19
            r2 = r20
            r3 = 0
            r0.<init>(r1, r3)
            r0.setApplyBottomPadding(r3)
            r0.setApplyTopPadding(r3)
            r4 = r22
            r0.fragment = r4
            r0.chatInvite = r2
            r4 = r21
            r0.hash = r4
            android.widget.LinearLayout r4 = new android.widget.LinearLayout
            r4.<init>(r1)
            r5 = 1
            r4.setOrientation(r5)
            r4.setClickable(r5)
            r0.setCustomView(r4)
            org.telegram.ui.Components.BackupImageView r6 = new org.telegram.ui.Components.BackupImageView
            r6.<init>(r1)
            r7 = 1108082688(0x420CLASSNAME, float:35.0)
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
            r6.setRoundRadius(r7)
            r8 = 70
            r9 = 70
            r10 = 49
            r11 = 0
            r12 = 29
            r13 = 0
            r14 = 0
            android.widget.LinearLayout$LayoutParams r7 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r8, (int) r9, (int) r10, (int) r11, (int) r12, (int) r13, (int) r14)
            r4.addView(r6, r7)
            org.telegram.tgnet.TLRPC$Chat r7 = r2.chat
            if (r7 == 0) goto L_0x005e
            org.telegram.ui.Components.AvatarDrawable r7 = new org.telegram.ui.Components.AvatarDrawable
            org.telegram.tgnet.TLRPC$Chat r8 = r2.chat
            r7.<init>((org.telegram.tgnet.TLRPC$Chat) r8)
            org.telegram.tgnet.TLRPC$Chat r8 = r2.chat
            java.lang.String r9 = r8.title
            int r10 = r8.participants_count
            r6.setForUserOrChat(r8, r7, r2)
            goto L_0x0084
        L_0x005e:
            org.telegram.ui.Components.AvatarDrawable r7 = new org.telegram.ui.Components.AvatarDrawable
            r7.<init>()
            r8 = 0
            java.lang.String r10 = r2.title
            r11 = 0
            r7.setInfo(r8, r10, r11)
            java.lang.String r9 = r2.title
            int r10 = r2.participants_count
            org.telegram.tgnet.TLRPC$Photo r8 = r2.photo
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r8 = r8.sizes
            r11 = 50
            org.telegram.tgnet.TLRPC$PhotoSize r8 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r8, r11)
            org.telegram.tgnet.TLRPC$Photo r11 = r2.photo
            org.telegram.messenger.ImageLocation r8 = org.telegram.messenger.ImageLocation.getForPhoto((org.telegram.tgnet.TLRPC$PhotoSize) r8, (org.telegram.tgnet.TLRPC$Photo) r11)
            java.lang.String r11 = "50_50"
            r6.setImage((org.telegram.messenger.ImageLocation) r8, (java.lang.String) r11, (android.graphics.drawable.Drawable) r7, (java.lang.Object) r2)
        L_0x0084:
            android.widget.TextView r6 = new android.widget.TextView
            r6.<init>(r1)
            java.lang.String r7 = "fonts/rmedium.ttf"
            android.graphics.Typeface r7 = org.telegram.messenger.AndroidUtilities.getTypeface(r7)
            r6.setTypeface(r7)
            r7 = 1099431936(0x41880000, float:17.0)
            r6.setTextSize(r5, r7)
            java.lang.String r7 = "dialogTextBlack"
            int r7 = org.telegram.ui.ActionBar.Theme.getColor(r7)
            r6.setTextColor(r7)
            r6.setText(r9)
            r6.setSingleLine(r5)
            android.text.TextUtils$TruncateAt r7 = android.text.TextUtils.TruncateAt.END
            r6.setEllipsize(r7)
            r11 = -2
            r12 = -2
            r13 = 49
            r14 = 10
            r15 = 9
            r16 = 10
            if (r10 <= 0) goto L_0x00ba
            r17 = 0
            goto L_0x00be
        L_0x00ba:
            r7 = 20
            r17 = 20
        L_0x00be:
            android.widget.LinearLayout$LayoutParams r7 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r11, (int) r12, (int) r13, (int) r14, (int) r15, (int) r16, (int) r17)
            r4.addView(r6, r7)
            if (r10 <= 0) goto L_0x011a
            android.widget.TextView r6 = new android.widget.TextView
            r6.<init>(r1)
            r7 = 1096810496(0x41600000, float:14.0)
            r6.setTextSize(r5, r7)
            java.lang.String r7 = "dialogTextGray3"
            int r7 = org.telegram.ui.ActionBar.Theme.getColor(r7)
            r6.setTextColor(r7)
            r6.setSingleLine(r5)
            android.text.TextUtils$TruncateAt r5 = android.text.TextUtils.TruncateAt.END
            r6.setEllipsize(r5)
            boolean r5 = r2.channel
            if (r5 != 0) goto L_0x00ff
            org.telegram.tgnet.TLRPC$Chat r5 = r2.chat
            boolean r5 = org.telegram.messenger.ChatObject.isChannel(r5)
            if (r5 == 0) goto L_0x00f5
            org.telegram.tgnet.TLRPC$Chat r5 = r2.chat
            boolean r5 = r5.megagroup
            if (r5 != 0) goto L_0x00f5
            goto L_0x00ff
        L_0x00f5:
            java.lang.String r5 = "Members"
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatPluralString(r5, r10)
            r6.setText(r5)
            goto L_0x0108
        L_0x00ff:
            java.lang.String r5 = "Subscribers"
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatPluralString(r5, r10)
            r6.setText(r5)
        L_0x0108:
            r7 = -2
            r8 = -2
            r9 = 49
            r10 = 10
            r11 = 3
            r12 = 10
            r13 = 20
            android.widget.LinearLayout$LayoutParams r5 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r7, (int) r8, (int) r9, (int) r10, (int) r11, (int) r12, (int) r13)
            r4.addView(r6, r5)
        L_0x011a:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$User> r5 = r2.participants
            boolean r5 = r5.isEmpty()
            if (r5 != 0) goto L_0x0169
            org.telegram.ui.Components.RecyclerListView r5 = new org.telegram.ui.Components.RecyclerListView
            r5.<init>(r1)
            r6 = 1090519040(0x41000000, float:8.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            r5.setPadding(r3, r3, r3, r6)
            r5.setNestedScrollingEnabled(r3)
            r5.setClipToPadding(r3)
            androidx.recyclerview.widget.LinearLayoutManager r6 = new androidx.recyclerview.widget.LinearLayoutManager
            android.content.Context r7 = r18.getContext()
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
            r7 = -2
            r8 = 90
            r9 = 49
            r10 = 0
            r11 = 0
            r12 = 0
            r13 = 7
            android.widget.LinearLayout$LayoutParams r6 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r7, (int) r8, (int) r9, (int) r10, (int) r11, (int) r12, (int) r13)
            r4.addView(r5, r6)
        L_0x0169:
            android.view.View r5 = new android.view.View
            r5.<init>(r1)
            java.lang.String r6 = "dialogShadowLine"
            int r6 = org.telegram.ui.ActionBar.Theme.getColor(r6)
            r5.setBackgroundColor(r6)
            android.widget.LinearLayout$LayoutParams r6 = new android.widget.LinearLayout$LayoutParams
            int r7 = org.telegram.messenger.AndroidUtilities.getShadowHeight()
            r8 = -1
            r6.<init>(r8, r7)
            r4.addView(r5, r6)
            org.telegram.ui.Components.PickerBottomLayout r5 = new org.telegram.ui.Components.PickerBottomLayout
            r5.<init>(r1, r3)
            r1 = 48
            r6 = 83
            android.widget.FrameLayout$LayoutParams r1 = org.telegram.ui.Components.LayoutHelper.createFrame(r8, r1, r6)
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
            r7 = 2131624665(0x7f0e02d9, float:1.8876516E38)
            java.lang.String r8 = "Cancel"
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r8, r7)
            java.lang.String r7 = r7.toUpperCase()
            r1.setText(r7)
            android.widget.TextView r1 = r5.cancelButton
            org.telegram.ui.Components.JoinGroupAlert$$ExternalSyntheticLambda0 r7 = new org.telegram.ui.Components.JoinGroupAlert$$ExternalSyntheticLambda0
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
            if (r1 == 0) goto L_0x01f4
            boolean r1 = r2.megagroup
            if (r1 == 0) goto L_0x0202
        L_0x01f4:
            org.telegram.tgnet.TLRPC$Chat r1 = r2.chat
            boolean r1 = org.telegram.messenger.ChatObject.isChannel(r1)
            if (r1 == 0) goto L_0x0215
            org.telegram.tgnet.TLRPC$Chat r1 = r2.chat
            boolean r1 = r1.megagroup
            if (r1 != 0) goto L_0x0215
        L_0x0202:
            android.widget.TextView r1 = r5.doneButtonTextView
            r2 = 2131627251(0x7f0e0cf3, float:1.8881761E38)
            java.lang.String r3 = "ProfileJoinChannel"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            java.lang.String r2 = r2.toUpperCase()
            r1.setText(r2)
            goto L_0x0223
        L_0x0215:
            android.widget.TextView r1 = r5.doneButtonTextView
            r2 = 2131625996(0x7f0e080c, float:1.8879216E38)
            java.lang.String r3 = "JoinGroup"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r1.setText(r2)
        L_0x0223:
            android.widget.LinearLayout r1 = r5.doneButton
            org.telegram.ui.Components.JoinGroupAlert$$ExternalSyntheticLambda1 r2 = new org.telegram.ui.Components.JoinGroupAlert$$ExternalSyntheticLambda1
            r2.<init>(r0)
            r1.setOnClickListener(r2)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.JoinGroupAlert.<init>(android.content.Context, org.telegram.tgnet.TLRPC$ChatInvite, java.lang.String, org.telegram.ui.ActionBar.BaseFragment):void");
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0(View view) {
        dismiss();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$3(View view) {
        dismiss();
        TLRPC$TL_messages_importChatInvite tLRPC$TL_messages_importChatInvite = new TLRPC$TL_messages_importChatInvite();
        tLRPC$TL_messages_importChatInvite.hash = this.hash;
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_messages_importChatInvite, new JoinGroupAlert$$ExternalSyntheticLambda3(this, tLRPC$TL_messages_importChatInvite), 2);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$2(TLRPC$TL_messages_importChatInvite tLRPC$TL_messages_importChatInvite, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        if (tLRPC$TL_error == null) {
            MessagesController.getInstance(this.currentAccount).processUpdates((TLRPC$Updates) tLObject, false);
        }
        AndroidUtilities.runOnUIThread(new JoinGroupAlert$$ExternalSyntheticLambda2(this, tLRPC$TL_error, tLObject, tLRPC$TL_messages_importChatInvite));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$1(TLRPC$TL_error tLRPC$TL_error, TLObject tLObject, TLRPC$TL_messages_importChatInvite tLRPC$TL_messages_importChatInvite) {
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
