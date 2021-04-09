package org.telegram.ui.Components;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import androidx.recyclerview.widget.RecyclerView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.MessagesController;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
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
    public JoinGroupAlert(android.content.Context r23, org.telegram.tgnet.TLRPC$ChatInvite r24, java.lang.String r25, org.telegram.ui.ActionBar.BaseFragment r26) {
        /*
            r22 = this;
            r0 = r22
            r1 = r23
            r9 = r24
            r10 = 0
            r0.<init>(r1, r10)
            r0.setApplyBottomPadding(r10)
            r0.setApplyTopPadding(r10)
            r2 = r26
            r0.fragment = r2
            r0.chatInvite = r9
            r2 = r25
            r0.hash = r2
            android.widget.LinearLayout r11 = new android.widget.LinearLayout
            r11.<init>(r1)
            r12 = 1
            r11.setOrientation(r12)
            r11.setClickable(r12)
            r0.setCustomView(r11)
            org.telegram.ui.Components.BackupImageView r2 = new org.telegram.ui.Components.BackupImageView
            r2.<init>(r1)
            r3 = 1108082688(0x420CLASSNAME, float:35.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            r2.setRoundRadius(r3)
            r13 = 70
            r14 = 70
            r15 = 49
            r16 = 0
            r17 = 29
            r18 = 0
            r19 = 0
            android.widget.LinearLayout$LayoutParams r3 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r13, (int) r14, (int) r15, (int) r16, (int) r17, (int) r18, (int) r19)
            r11.addView(r2, r3)
            org.telegram.tgnet.TLRPC$Chat r3 = r9.chat
            if (r3 == 0) goto L_0x0072
            org.telegram.ui.Components.AvatarDrawable r7 = new org.telegram.ui.Components.AvatarDrawable
            org.telegram.tgnet.TLRPC$Chat r3 = r9.chat
            r7.<init>((org.telegram.tgnet.TLRPC$Chat) r3)
            org.telegram.tgnet.TLRPC$Chat r3 = r9.chat
            java.lang.String r13 = r3.title
            int r14 = r3.participants_count
            org.telegram.messenger.ImageLocation r3 = org.telegram.messenger.ImageLocation.getForUserOrChat(r3, r12)
            org.telegram.tgnet.TLRPC$Chat r4 = r9.chat
            r5 = 2
            org.telegram.messenger.ImageLocation r5 = org.telegram.messenger.ImageLocation.getForUserOrChat(r4, r5)
            java.lang.String r4 = "50_50"
            java.lang.String r6 = "50_50"
            r8 = r24
            r2.setImage((org.telegram.messenger.ImageLocation) r3, (java.lang.String) r4, (org.telegram.messenger.ImageLocation) r5, (java.lang.String) r6, (android.graphics.drawable.Drawable) r7, (java.lang.Object) r8)
            goto L_0x0096
        L_0x0072:
            org.telegram.ui.Components.AvatarDrawable r3 = new org.telegram.ui.Components.AvatarDrawable
            r3.<init>()
            java.lang.String r4 = r9.title
            r5 = 0
            r3.setInfo(r10, r4, r5)
            java.lang.String r13 = r9.title
            int r14 = r9.participants_count
            org.telegram.tgnet.TLRPC$Photo r4 = r9.photo
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r4 = r4.sizes
            r5 = 50
            org.telegram.tgnet.TLRPC$PhotoSize r4 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r4, r5)
            org.telegram.tgnet.TLRPC$Photo r5 = r9.photo
            org.telegram.messenger.ImageLocation r4 = org.telegram.messenger.ImageLocation.getForPhoto((org.telegram.tgnet.TLRPC$PhotoSize) r4, (org.telegram.tgnet.TLRPC$Photo) r5)
            java.lang.String r5 = "50_50"
            r2.setImage((org.telegram.messenger.ImageLocation) r4, (java.lang.String) r5, (android.graphics.drawable.Drawable) r3, (java.lang.Object) r9)
        L_0x0096:
            android.widget.TextView r2 = new android.widget.TextView
            r2.<init>(r1)
            java.lang.String r3 = "fonts/rmedium.ttf"
            android.graphics.Typeface r3 = org.telegram.messenger.AndroidUtilities.getTypeface(r3)
            r2.setTypeface(r3)
            r3 = 1099431936(0x41880000, float:17.0)
            r2.setTextSize(r12, r3)
            java.lang.String r3 = "dialogTextBlack"
            int r3 = org.telegram.ui.ActionBar.Theme.getColor(r3)
            r2.setTextColor(r3)
            r2.setText(r13)
            r2.setSingleLine(r12)
            android.text.TextUtils$TruncateAt r3 = android.text.TextUtils.TruncateAt.END
            r2.setEllipsize(r3)
            r15 = -2
            r16 = -2
            r17 = 49
            r18 = 10
            r19 = 9
            r20 = 10
            if (r14 <= 0) goto L_0x00cd
            r21 = 0
            goto L_0x00d1
        L_0x00cd:
            r3 = 20
            r21 = 20
        L_0x00d1:
            android.widget.LinearLayout$LayoutParams r3 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r15, (int) r16, (int) r17, (int) r18, (int) r19, (int) r20, (int) r21)
            r11.addView(r2, r3)
            if (r14 <= 0) goto L_0x0111
            android.widget.TextView r2 = new android.widget.TextView
            r2.<init>(r1)
            r3 = 1096810496(0x41600000, float:14.0)
            r2.setTextSize(r12, r3)
            java.lang.String r3 = "dialogTextGray3"
            int r3 = org.telegram.ui.ActionBar.Theme.getColor(r3)
            r2.setTextColor(r3)
            r2.setSingleLine(r12)
            android.text.TextUtils$TruncateAt r3 = android.text.TextUtils.TruncateAt.END
            r2.setEllipsize(r3)
            java.lang.String r3 = "Members"
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatPluralString(r3, r14)
            r2.setText(r3)
            r12 = -2
            r13 = -2
            r14 = 49
            r15 = 10
            r16 = 3
            r17 = 10
            r18 = 20
            android.widget.LinearLayout$LayoutParams r3 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r12, (int) r13, (int) r14, (int) r15, (int) r16, (int) r17, (int) r18)
            r11.addView(r2, r3)
        L_0x0111:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$User> r2 = r9.participants
            boolean r2 = r2.isEmpty()
            if (r2 != 0) goto L_0x0163
            org.telegram.ui.Components.RecyclerListView r2 = new org.telegram.ui.Components.RecyclerListView
            r2.<init>(r1)
            r3 = 1090519040(0x41000000, float:8.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            r2.setPadding(r10, r10, r10, r3)
            r2.setNestedScrollingEnabled(r10)
            r2.setClipToPadding(r10)
            androidx.recyclerview.widget.LinearLayoutManager r3 = new androidx.recyclerview.widget.LinearLayoutManager
            android.content.Context r4 = r22.getContext()
            r3.<init>(r4, r10, r10)
            r2.setLayoutManager(r3)
            r2.setHorizontalScrollBarEnabled(r10)
            r2.setVerticalScrollBarEnabled(r10)
            org.telegram.ui.Components.JoinGroupAlert$UsersAdapter r3 = new org.telegram.ui.Components.JoinGroupAlert$UsersAdapter
            r3.<init>(r1)
            r2.setAdapter(r3)
            java.lang.String r3 = "dialogScrollGlow"
            int r3 = org.telegram.ui.ActionBar.Theme.getColor(r3)
            r2.setGlowColor(r3)
            r12 = -2
            r13 = 90
            r14 = 49
            r15 = 0
            r16 = 0
            r17 = 0
            r18 = 7
            android.widget.LinearLayout$LayoutParams r3 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r12, (int) r13, (int) r14, (int) r15, (int) r16, (int) r17, (int) r18)
            r11.addView(r2, r3)
        L_0x0163:
            android.view.View r2 = new android.view.View
            r2.<init>(r1)
            java.lang.String r3 = "dialogShadowLine"
            int r3 = org.telegram.ui.ActionBar.Theme.getColor(r3)
            r2.setBackgroundColor(r3)
            android.widget.LinearLayout$LayoutParams r3 = new android.widget.LinearLayout$LayoutParams
            int r4 = org.telegram.messenger.AndroidUtilities.getShadowHeight()
            r5 = -1
            r3.<init>(r5, r4)
            r11.addView(r2, r3)
            org.telegram.ui.Components.PickerBottomLayout r2 = new org.telegram.ui.Components.PickerBottomLayout
            r2.<init>(r1, r10)
            r1 = 48
            r3 = 83
            android.widget.FrameLayout$LayoutParams r1 = org.telegram.ui.Components.LayoutHelper.createFrame(r5, r1, r3)
            r11.addView(r2, r1)
            android.widget.TextView r1 = r2.cancelButton
            r3 = 1099956224(0x41900000, float:18.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r3)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r3)
            r1.setPadding(r4, r10, r5, r10)
            android.widget.TextView r1 = r2.cancelButton
            java.lang.String r4 = "dialogTextBlue2"
            int r5 = org.telegram.ui.ActionBar.Theme.getColor(r4)
            r1.setTextColor(r5)
            android.widget.TextView r1 = r2.cancelButton
            r5 = 2131624639(0x7f0e02bf, float:1.8876463E38)
            java.lang.String r6 = "Cancel"
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r6, r5)
            java.lang.String r5 = r5.toUpperCase()
            r1.setText(r5)
            android.widget.TextView r1 = r2.cancelButton
            org.telegram.ui.Components.-$$Lambda$JoinGroupAlert$AseK4-oUXkiQDAchBqaTq60CJa0 r5 = new org.telegram.ui.Components.-$$Lambda$JoinGroupAlert$AseK4-oUXkiQDAchBqaTq60CJa0
            r5.<init>()
            r1.setOnClickListener(r5)
            android.widget.LinearLayout r1 = r2.doneButton
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r3)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            r1.setPadding(r5, r10, r3, r10)
            android.widget.LinearLayout r1 = r2.doneButton
            r1.setVisibility(r10)
            android.widget.TextView r1 = r2.doneButtonBadgeTextView
            r3 = 8
            r1.setVisibility(r3)
            android.widget.TextView r1 = r2.doneButtonTextView
            int r3 = org.telegram.ui.ActionBar.Theme.getColor(r4)
            r1.setTextColor(r3)
            boolean r1 = r9.channel
            if (r1 == 0) goto L_0x01ee
            boolean r1 = r9.megagroup
            if (r1 == 0) goto L_0x01fc
        L_0x01ee:
            org.telegram.tgnet.TLRPC$Chat r1 = r9.chat
            boolean r1 = org.telegram.messenger.ChatObject.isChannel(r1)
            if (r1 == 0) goto L_0x020f
            org.telegram.tgnet.TLRPC$Chat r1 = r9.chat
            boolean r1 = r1.megagroup
            if (r1 != 0) goto L_0x020f
        L_0x01fc:
            android.widget.TextView r1 = r2.doneButtonTextView
            r3 = 2131627008(0x7f0e0CLASSNAME, float:1.8881268E38)
            java.lang.String r4 = "ProfileJoinChannel"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            java.lang.String r3 = r3.toUpperCase()
            r1.setText(r3)
            goto L_0x021d
        L_0x020f:
            android.widget.TextView r1 = r2.doneButtonTextView
            r3 = 2131625870(0x7f0e078e, float:1.887896E38)
            java.lang.String r4 = "JoinGroup"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            r1.setText(r3)
        L_0x021d:
            android.widget.LinearLayout r1 = r2.doneButton
            org.telegram.ui.Components.-$$Lambda$JoinGroupAlert$lV5gzJCFdYlzirjF_v5fq8Kw-ug r2 = new org.telegram.ui.Components.-$$Lambda$JoinGroupAlert$lV5gzJCFdYlzirjF_v5fq8Kw-ug
            r2.<init>()
            r1.setOnClickListener(r2)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.JoinGroupAlert.<init>(android.content.Context, org.telegram.tgnet.TLRPC$ChatInvite, java.lang.String, org.telegram.ui.ActionBar.BaseFragment):void");
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$0 */
    public /* synthetic */ void lambda$new$0$JoinGroupAlert(View view) {
        dismiss();
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$3 */
    public /* synthetic */ void lambda$new$3$JoinGroupAlert(View view) {
        dismiss();
        TLRPC$TL_messages_importChatInvite tLRPC$TL_messages_importChatInvite = new TLRPC$TL_messages_importChatInvite();
        tLRPC$TL_messages_importChatInvite.hash = this.hash;
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_messages_importChatInvite, new RequestDelegate(tLRPC$TL_messages_importChatInvite) {
            public final /* synthetic */ TLRPC$TL_messages_importChatInvite f$1;

            {
                this.f$1 = r2;
            }

            public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                JoinGroupAlert.this.lambda$null$2$JoinGroupAlert(this.f$1, tLObject, tLRPC$TL_error);
            }
        }, 2);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$2 */
    public /* synthetic */ void lambda$null$2$JoinGroupAlert(TLRPC$TL_messages_importChatInvite tLRPC$TL_messages_importChatInvite, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        if (tLRPC$TL_error == null) {
            MessagesController.getInstance(this.currentAccount).processUpdates((TLRPC$Updates) tLObject, false);
        }
        AndroidUtilities.runOnUIThread(new Runnable(tLRPC$TL_error, tLObject, tLRPC$TL_messages_importChatInvite) {
            public final /* synthetic */ TLRPC$TL_error f$1;
            public final /* synthetic */ TLObject f$2;
            public final /* synthetic */ TLRPC$TL_messages_importChatInvite f$3;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
            }

            public final void run() {
                JoinGroupAlert.this.lambda$null$1$JoinGroupAlert(this.f$1, this.f$2, this.f$3);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$1 */
    public /* synthetic */ void lambda$null$1$JoinGroupAlert(TLRPC$TL_error tLRPC$TL_error, TLObject tLObject, TLRPC$TL_messages_importChatInvite tLRPC$TL_messages_importChatInvite) {
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
                    bundle.putInt("chat_id", tLRPC$Chat.id);
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
