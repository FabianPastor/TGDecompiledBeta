package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.text.StaticLayout;
import android.view.View;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.TLRPC$RecentMeUrl;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AvatarDrawable;

public class DialogMeUrlCell extends BaseCell {
    private AvatarDrawable avatarDrawable = new AvatarDrawable();
    private ImageReceiver avatarImage = new ImageReceiver(this);
    private int avatarTop = AndroidUtilities.dp(10.0f);
    private int currentAccount = UserConfig.selectedAccount;
    private boolean drawNameBot;
    private boolean drawNameBroadcast;
    private boolean drawNameGroup;
    private boolean drawNameLock;
    private boolean drawVerified;
    private boolean isSelected;
    private StaticLayout messageLayout;
    private int messageLeft;
    private int messageTop = AndroidUtilities.dp(40.0f);
    private StaticLayout nameLayout;
    private int nameLeft;
    private int nameLockLeft;
    private int nameLockTop;
    private int nameMuteLeft;
    private TLRPC$RecentMeUrl recentMeUrl;
    public boolean useSeparator;

    public boolean hasOverlappingRendering() {
        return false;
    }

    public DialogMeUrlCell(Context context) {
        super(context);
        Theme.createDialogsResources(context);
        this.avatarImage.setRoundRadius(AndroidUtilities.dp(26.0f));
    }

    public void setRecentMeUrl(TLRPC$RecentMeUrl tLRPC$RecentMeUrl) {
        this.recentMeUrl = tLRPC$RecentMeUrl;
        requestLayout();
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.avatarImage.onDetachedFromWindow();
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.avatarImage.onAttachedToWindow();
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        setMeasuredDimension(View.MeasureSpec.getSize(i), AndroidUtilities.dp(72.0f) + (this.useSeparator ? 1 : 0));
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        if (z) {
            buildLayout();
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:111:0x0376  */
    /* JADX WARNING: Removed duplicated region for block: B:121:0x03d0  */
    /* JADX WARNING: Removed duplicated region for block: B:126:0x03e9  */
    /* JADX WARNING: Removed duplicated region for block: B:138:0x0449  */
    /* JADX WARNING: Removed duplicated region for block: B:159:0x04d7  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void buildLayout() {
        /*
            r19 = this;
            r1 = r19
            android.text.TextPaint[] r0 = org.telegram.ui.ActionBar.Theme.dialogs_namePaint
            r2 = 0
            r5 = r0[r2]
            android.text.TextPaint[] r0 = org.telegram.ui.ActionBar.Theme.dialogs_messagePaint
            r11 = r0[r2]
            r1.drawNameGroup = r2
            r1.drawNameBroadcast = r2
            r1.drawNameLock = r2
            r1.drawNameBot = r2
            r1.drawVerified = r2
            org.telegram.tgnet.TLRPC$RecentMeUrl r0 = r1.recentMeUrl
            boolean r3 = r0 instanceof org.telegram.tgnet.TLRPC$TL_recentMeUrlChat
            r4 = 1099694080(0x418CLASSNAME, float:17.5)
            r6 = 1099169792(0x41840000, float:16.5)
            r7 = 1
            r8 = 1096810496(0x41600000, float:14.0)
            if (r3 == 0) goto L_0x00bf
            int r0 = r1.currentAccount
            org.telegram.messenger.MessagesController r0 = org.telegram.messenger.MessagesController.getInstance(r0)
            org.telegram.tgnet.TLRPC$RecentMeUrl r3 = r1.recentMeUrl
            int r3 = r3.chat_id
            java.lang.Integer r3 = java.lang.Integer.valueOf(r3)
            org.telegram.tgnet.TLRPC$Chat r0 = r0.getChat(r3)
            int r3 = r0.id
            if (r3 < 0) goto L_0x004c
            boolean r3 = org.telegram.messenger.ChatObject.isChannel(r0)
            if (r3 == 0) goto L_0x0043
            boolean r3 = r0.megagroup
            if (r3 != 0) goto L_0x0043
            goto L_0x004c
        L_0x0043:
            r1.drawNameGroup = r7
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r4)
            r1.nameLockTop = r3
            goto L_0x0054
        L_0x004c:
            r1.drawNameBroadcast = r7
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r6)
            r1.nameLockTop = r3
        L_0x0054:
            boolean r3 = r0.verified
            r1.drawVerified = r3
            boolean r3 = org.telegram.messenger.LocaleController.isRTL
            if (r3 != 0) goto L_0x007f
            int r3 = org.telegram.messenger.AndroidUtilities.leftBaseline
            float r3 = (float) r3
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            r1.nameLockLeft = r3
            int r3 = org.telegram.messenger.AndroidUtilities.leftBaseline
            int r3 = r3 + 4
            float r3 = (float) r3
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            boolean r4 = r1.drawNameGroup
            if (r4 == 0) goto L_0x0075
            android.graphics.drawable.Drawable r4 = org.telegram.ui.ActionBar.Theme.dialogs_groupDrawable
            goto L_0x0077
        L_0x0075:
            android.graphics.drawable.Drawable r4 = org.telegram.ui.ActionBar.Theme.dialogs_broadcastDrawable
        L_0x0077:
            int r4 = r4.getIntrinsicWidth()
            int r3 = r3 + r4
            r1.nameLeft = r3
            goto L_0x00a1
        L_0x007f:
            int r3 = r19.getMeasuredWidth()
            int r4 = org.telegram.messenger.AndroidUtilities.leftBaseline
            float r4 = (float) r4
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            int r3 = r3 - r4
            boolean r4 = r1.drawNameGroup
            if (r4 == 0) goto L_0x0092
            android.graphics.drawable.Drawable r4 = org.telegram.ui.ActionBar.Theme.dialogs_groupDrawable
            goto L_0x0094
        L_0x0092:
            android.graphics.drawable.Drawable r4 = org.telegram.ui.ActionBar.Theme.dialogs_broadcastDrawable
        L_0x0094:
            int r4 = r4.getIntrinsicWidth()
            int r3 = r3 - r4
            r1.nameLockLeft = r3
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r8)
            r1.nameLeft = r3
        L_0x00a1:
            java.lang.String r3 = r0.title
            org.telegram.ui.Components.AvatarDrawable r4 = r1.avatarDrawable
            r4.setInfo((org.telegram.tgnet.TLRPC$Chat) r0)
            org.telegram.messenger.ImageReceiver r12 = r1.avatarImage
            org.telegram.messenger.ImageLocation r13 = org.telegram.messenger.ImageLocation.getForChat(r0, r2)
            org.telegram.ui.Components.AvatarDrawable r15 = r1.avatarDrawable
            r16 = 0
            org.telegram.tgnet.TLRPC$RecentMeUrl r0 = r1.recentMeUrl
            r18 = 0
            java.lang.String r14 = "50_50"
            r17 = r0
            r12.setImage(r13, r14, r15, r16, r17, r18)
            goto L_0x02e2
        L_0x00bf:
            boolean r3 = r0 instanceof org.telegram.tgnet.TLRPC$TL_recentMeUrlUser
            if (r3 == 0) goto L_0x0156
            int r0 = r1.currentAccount
            org.telegram.messenger.MessagesController r0 = org.telegram.messenger.MessagesController.getInstance(r0)
            org.telegram.tgnet.TLRPC$RecentMeUrl r3 = r1.recentMeUrl
            int r3 = r3.user_id
            java.lang.Integer r3 = java.lang.Integer.valueOf(r3)
            org.telegram.tgnet.TLRPC$User r0 = r0.getUser(r3)
            boolean r3 = org.telegram.messenger.LocaleController.isRTL
            if (r3 != 0) goto L_0x00e3
            int r3 = org.telegram.messenger.AndroidUtilities.leftBaseline
            float r3 = (float) r3
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            r1.nameLeft = r3
            goto L_0x00e9
        L_0x00e3:
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r8)
            r1.nameLeft = r3
        L_0x00e9:
            if (r0 == 0) goto L_0x0136
            boolean r3 = r0.bot
            if (r3 == 0) goto L_0x0132
            r1.drawNameBot = r7
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r6)
            r1.nameLockTop = r3
            boolean r3 = org.telegram.messenger.LocaleController.isRTL
            if (r3 != 0) goto L_0x0117
            int r3 = org.telegram.messenger.AndroidUtilities.leftBaseline
            float r3 = (float) r3
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            r1.nameLockLeft = r3
            int r3 = org.telegram.messenger.AndroidUtilities.leftBaseline
            int r3 = r3 + 4
            float r3 = (float) r3
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            android.graphics.drawable.Drawable r4 = org.telegram.ui.ActionBar.Theme.dialogs_botDrawable
            int r4 = r4.getIntrinsicWidth()
            int r3 = r3 + r4
            r1.nameLeft = r3
            goto L_0x0132
        L_0x0117:
            int r3 = r19.getMeasuredWidth()
            int r4 = org.telegram.messenger.AndroidUtilities.leftBaseline
            float r4 = (float) r4
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            int r3 = r3 - r4
            android.graphics.drawable.Drawable r4 = org.telegram.ui.ActionBar.Theme.dialogs_botDrawable
            int r4 = r4.getIntrinsicWidth()
            int r3 = r3 - r4
            r1.nameLockLeft = r3
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r8)
            r1.nameLeft = r3
        L_0x0132:
            boolean r3 = r0.verified
            r1.drawVerified = r3
        L_0x0136:
            java.lang.String r3 = org.telegram.messenger.UserObject.getUserName(r0)
            org.telegram.ui.Components.AvatarDrawable r4 = r1.avatarDrawable
            r4.setInfo((org.telegram.tgnet.TLRPC$User) r0)
            org.telegram.messenger.ImageReceiver r12 = r1.avatarImage
            org.telegram.messenger.ImageLocation r13 = org.telegram.messenger.ImageLocation.getForUser(r0, r2)
            org.telegram.ui.Components.AvatarDrawable r15 = r1.avatarDrawable
            r16 = 0
            org.telegram.tgnet.TLRPC$RecentMeUrl r0 = r1.recentMeUrl
            r18 = 0
            java.lang.String r14 = "50_50"
            r17 = r0
            r12.setImage(r13, r14, r15, r16, r17, r18)
            goto L_0x02e2
        L_0x0156:
            boolean r3 = r0 instanceof org.telegram.tgnet.TLRPC$TL_recentMeUrlStickerSet
            r9 = 0
            r10 = 5
            if (r3 == 0) goto L_0x0199
            boolean r0 = org.telegram.messenger.LocaleController.isRTL
            if (r0 != 0) goto L_0x016a
            int r0 = org.telegram.messenger.AndroidUtilities.leftBaseline
            float r0 = (float) r0
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r1.nameLeft = r0
            goto L_0x0170
        L_0x016a:
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r8)
            r1.nameLeft = r0
        L_0x0170:
            org.telegram.tgnet.TLRPC$RecentMeUrl r0 = r1.recentMeUrl
            org.telegram.tgnet.TLRPC$StickerSetCovered r0 = r0.set
            org.telegram.tgnet.TLRPC$StickerSet r0 = r0.set
            java.lang.String r3 = r0.title
            org.telegram.ui.Components.AvatarDrawable r0 = r1.avatarDrawable
            r0.setInfo(r10, r3, r9)
            org.telegram.messenger.ImageReceiver r12 = r1.avatarImage
            org.telegram.tgnet.TLRPC$RecentMeUrl r0 = r1.recentMeUrl
            org.telegram.tgnet.TLRPC$StickerSetCovered r0 = r0.set
            org.telegram.tgnet.TLRPC$Document r0 = r0.cover
            org.telegram.messenger.ImageLocation r13 = org.telegram.messenger.ImageLocation.getForDocument(r0)
            r14 = 0
            org.telegram.ui.Components.AvatarDrawable r15 = r1.avatarDrawable
            r16 = 0
            org.telegram.tgnet.TLRPC$RecentMeUrl r0 = r1.recentMeUrl
            r18 = 0
            r17 = r0
            r12.setImage(r13, r14, r15, r16, r17, r18)
            goto L_0x02e2
        L_0x0199:
            boolean r3 = r0 instanceof org.telegram.tgnet.TLRPC$TL_recentMeUrlChatInvite
            if (r3 == 0) goto L_0x02a5
            boolean r0 = org.telegram.messenger.LocaleController.isRTL
            if (r0 != 0) goto L_0x01ab
            int r0 = org.telegram.messenger.AndroidUtilities.leftBaseline
            float r0 = (float) r0
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r1.nameLeft = r0
            goto L_0x01b1
        L_0x01ab:
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r8)
            r1.nameLeft = r0
        L_0x01b1:
            org.telegram.tgnet.TLRPC$RecentMeUrl r0 = r1.recentMeUrl
            org.telegram.tgnet.TLRPC$ChatInvite r0 = r0.chat_invite
            org.telegram.tgnet.TLRPC$Chat r3 = r0.chat
            if (r3 == 0) goto L_0x020c
            org.telegram.ui.Components.AvatarDrawable r0 = r1.avatarDrawable
            r0.setInfo((org.telegram.tgnet.TLRPC$Chat) r3)
            org.telegram.tgnet.TLRPC$RecentMeUrl r0 = r1.recentMeUrl
            org.telegram.tgnet.TLRPC$ChatInvite r0 = r0.chat_invite
            org.telegram.tgnet.TLRPC$Chat r0 = r0.chat
            java.lang.String r3 = r0.title
            int r9 = r0.id
            if (r9 < 0) goto L_0x01e4
            boolean r0 = org.telegram.messenger.ChatObject.isChannel(r0)
            if (r0 == 0) goto L_0x01db
            org.telegram.tgnet.TLRPC$RecentMeUrl r0 = r1.recentMeUrl
            org.telegram.tgnet.TLRPC$ChatInvite r0 = r0.chat_invite
            org.telegram.tgnet.TLRPC$Chat r0 = r0.chat
            boolean r0 = r0.megagroup
            if (r0 != 0) goto L_0x01db
            goto L_0x01e4
        L_0x01db:
            r1.drawNameGroup = r7
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r4)
            r1.nameLockTop = r0
            goto L_0x01ec
        L_0x01e4:
            r1.drawNameBroadcast = r7
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r6)
            r1.nameLockTop = r0
        L_0x01ec:
            org.telegram.tgnet.TLRPC$RecentMeUrl r0 = r1.recentMeUrl
            org.telegram.tgnet.TLRPC$ChatInvite r0 = r0.chat_invite
            org.telegram.tgnet.TLRPC$Chat r0 = r0.chat
            boolean r4 = r0.verified
            r1.drawVerified = r4
            org.telegram.messenger.ImageReceiver r12 = r1.avatarImage
            org.telegram.messenger.ImageLocation r13 = org.telegram.messenger.ImageLocation.getForChat(r0, r2)
            org.telegram.ui.Components.AvatarDrawable r15 = r1.avatarDrawable
            r16 = 0
            org.telegram.tgnet.TLRPC$RecentMeUrl r0 = r1.recentMeUrl
            r18 = 0
            java.lang.String r14 = "50_50"
            r17 = r0
            r12.setImage(r13, r14, r15, r16, r17, r18)
            goto L_0x025b
        L_0x020c:
            java.lang.String r0 = r0.title
            org.telegram.ui.Components.AvatarDrawable r3 = r1.avatarDrawable
            r3.setInfo(r10, r0, r9)
            org.telegram.tgnet.TLRPC$RecentMeUrl r3 = r1.recentMeUrl
            org.telegram.tgnet.TLRPC$ChatInvite r3 = r3.chat_invite
            boolean r9 = r3.broadcast
            if (r9 != 0) goto L_0x0229
            boolean r3 = r3.channel
            if (r3 == 0) goto L_0x0220
            goto L_0x0229
        L_0x0220:
            r1.drawNameGroup = r7
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r4)
            r1.nameLockTop = r3
            goto L_0x0231
        L_0x0229:
            r1.drawNameBroadcast = r7
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r6)
            r1.nameLockTop = r3
        L_0x0231:
            org.telegram.tgnet.TLRPC$RecentMeUrl r3 = r1.recentMeUrl
            org.telegram.tgnet.TLRPC$ChatInvite r3 = r3.chat_invite
            org.telegram.tgnet.TLRPC$Photo r3 = r3.photo
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r3 = r3.sizes
            r4 = 50
            org.telegram.tgnet.TLRPC$PhotoSize r3 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r3, r4)
            org.telegram.messenger.ImageReceiver r12 = r1.avatarImage
            org.telegram.tgnet.TLRPC$RecentMeUrl r4 = r1.recentMeUrl
            org.telegram.tgnet.TLRPC$ChatInvite r4 = r4.chat_invite
            org.telegram.tgnet.TLRPC$Photo r4 = r4.photo
            org.telegram.messenger.ImageLocation r13 = org.telegram.messenger.ImageLocation.getForPhoto((org.telegram.tgnet.TLRPC$PhotoSize) r3, (org.telegram.tgnet.TLRPC$Photo) r4)
            org.telegram.ui.Components.AvatarDrawable r15 = r1.avatarDrawable
            r16 = 0
            org.telegram.tgnet.TLRPC$RecentMeUrl r3 = r1.recentMeUrl
            r18 = 0
            java.lang.String r14 = "50_50"
            r17 = r3
            r12.setImage(r13, r14, r15, r16, r17, r18)
            r3 = r0
        L_0x025b:
            boolean r0 = org.telegram.messenger.LocaleController.isRTL
            if (r0 != 0) goto L_0x0282
            int r0 = org.telegram.messenger.AndroidUtilities.leftBaseline
            float r0 = (float) r0
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r1.nameLockLeft = r0
            int r0 = org.telegram.messenger.AndroidUtilities.leftBaseline
            int r0 = r0 + 4
            float r0 = (float) r0
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            boolean r4 = r1.drawNameGroup
            if (r4 == 0) goto L_0x0278
            android.graphics.drawable.Drawable r4 = org.telegram.ui.ActionBar.Theme.dialogs_groupDrawable
            goto L_0x027a
        L_0x0278:
            android.graphics.drawable.Drawable r4 = org.telegram.ui.ActionBar.Theme.dialogs_broadcastDrawable
        L_0x027a:
            int r4 = r4.getIntrinsicWidth()
            int r0 = r0 + r4
            r1.nameLeft = r0
            goto L_0x02e2
        L_0x0282:
            int r0 = r19.getMeasuredWidth()
            int r4 = org.telegram.messenger.AndroidUtilities.leftBaseline
            float r4 = (float) r4
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            int r0 = r0 - r4
            boolean r4 = r1.drawNameGroup
            if (r4 == 0) goto L_0x0295
            android.graphics.drawable.Drawable r4 = org.telegram.ui.ActionBar.Theme.dialogs_groupDrawable
            goto L_0x0297
        L_0x0295:
            android.graphics.drawable.Drawable r4 = org.telegram.ui.ActionBar.Theme.dialogs_broadcastDrawable
        L_0x0297:
            int r4 = r4.getIntrinsicWidth()
            int r0 = r0 - r4
            r1.nameLockLeft = r0
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r8)
            r1.nameLeft = r0
            goto L_0x02e2
        L_0x02a5:
            boolean r3 = r0 instanceof org.telegram.tgnet.TLRPC$TL_recentMeUrlUnknown
            if (r3 == 0) goto L_0x02d1
            boolean r0 = org.telegram.messenger.LocaleController.isRTL
            if (r0 != 0) goto L_0x02b7
            int r0 = org.telegram.messenger.AndroidUtilities.leftBaseline
            float r0 = (float) r0
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r1.nameLeft = r0
            goto L_0x02bd
        L_0x02b7:
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r8)
            r1.nameLeft = r0
        L_0x02bd:
            org.telegram.messenger.ImageReceiver r12 = r1.avatarImage
            r13 = 0
            r14 = 0
            org.telegram.ui.Components.AvatarDrawable r15 = r1.avatarDrawable
            r16 = 0
            org.telegram.tgnet.TLRPC$RecentMeUrl r0 = r1.recentMeUrl
            r18 = 0
            r17 = r0
            r12.setImage(r13, r14, r15, r16, r17, r18)
            java.lang.String r3 = "Url"
            goto L_0x02e2
        L_0x02d1:
            org.telegram.messenger.ImageReceiver r12 = r1.avatarImage
            r13 = 0
            r14 = 0
            org.telegram.ui.Components.AvatarDrawable r15 = r1.avatarDrawable
            r16 = 0
            r18 = 0
            r17 = r0
            r12.setImage(r13, r14, r15, r16, r17, r18)
            java.lang.String r3 = ""
        L_0x02e2:
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            int r4 = r1.currentAccount
            org.telegram.messenger.MessagesController r4 = org.telegram.messenger.MessagesController.getInstance(r4)
            java.lang.String r4 = r4.linkPrefix
            r0.append(r4)
            java.lang.String r4 = "/"
            r0.append(r4)
            org.telegram.tgnet.TLRPC$RecentMeUrl r4 = r1.recentMeUrl
            java.lang.String r4 = r4.url
            r0.append(r4)
            java.lang.String r12 = r0.toString()
            boolean r0 = android.text.TextUtils.isEmpty(r3)
            if (r0 == 0) goto L_0x0311
            r0 = 2131625498(0x7f0e061a, float:1.8878206E38)
            java.lang.String r3 = "HiddenName"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r3, r0)
        L_0x0311:
            boolean r0 = org.telegram.messenger.LocaleController.isRTL
            if (r0 != 0) goto L_0x0321
            int r0 = r19.getMeasuredWidth()
            int r4 = r1.nameLeft
            int r0 = r0 - r4
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r8)
            goto L_0x032f
        L_0x0321:
            int r0 = r19.getMeasuredWidth()
            int r4 = r1.nameLeft
            int r0 = r0 - r4
            int r4 = org.telegram.messenger.AndroidUtilities.leftBaseline
            float r4 = (float) r4
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
        L_0x032f:
            int r0 = r0 - r4
            boolean r4 = r1.drawNameLock
            r6 = 1082130432(0x40800000, float:4.0)
            if (r4 == 0) goto L_0x0343
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r6)
            android.graphics.drawable.Drawable r6 = org.telegram.ui.ActionBar.Theme.dialogs_lockDrawable
            int r6 = r6.getIntrinsicWidth()
        L_0x0340:
            int r4 = r4 + r6
            int r0 = r0 - r4
            goto L_0x0370
        L_0x0343:
            boolean r4 = r1.drawNameGroup
            if (r4 == 0) goto L_0x0352
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r6)
            android.graphics.drawable.Drawable r6 = org.telegram.ui.ActionBar.Theme.dialogs_groupDrawable
            int r6 = r6.getIntrinsicWidth()
            goto L_0x0340
        L_0x0352:
            boolean r4 = r1.drawNameBroadcast
            if (r4 == 0) goto L_0x0361
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r6)
            android.graphics.drawable.Drawable r6 = org.telegram.ui.ActionBar.Theme.dialogs_broadcastDrawable
            int r6 = r6.getIntrinsicWidth()
            goto L_0x0340
        L_0x0361:
            boolean r4 = r1.drawNameBot
            if (r4 == 0) goto L_0x0370
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r6)
            android.graphics.drawable.Drawable r6 = org.telegram.ui.ActionBar.Theme.dialogs_botDrawable
            int r6 = r6.getIntrinsicWidth()
            goto L_0x0340
        L_0x0370:
            boolean r4 = r1.drawVerified
            r14 = 1086324736(0x40CLASSNAME, float:6.0)
            if (r4 == 0) goto L_0x038b
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r14)
            android.graphics.drawable.Drawable r6 = org.telegram.ui.ActionBar.Theme.dialogs_verifiedDrawable
            int r6 = r6.getIntrinsicWidth()
            int r4 = r4 + r6
            int r0 = r0 - r4
            boolean r6 = org.telegram.messenger.LocaleController.isRTL
            if (r6 == 0) goto L_0x038b
            int r6 = r1.nameLeft
            int r6 = r6 + r4
            r1.nameLeft = r6
        L_0x038b:
            r13 = 1094713344(0x41400000, float:12.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r13)
            int r15 = java.lang.Math.max(r4, r0)
            r0 = 10
            r4 = 32
            java.lang.String r0 = r3.replace(r0, r4)     // Catch:{ Exception -> 0x03ba }
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r13)     // Catch:{ Exception -> 0x03ba }
            int r3 = r15 - r3
            float r3 = (float) r3     // Catch:{ Exception -> 0x03ba }
            android.text.TextUtils$TruncateAt r4 = android.text.TextUtils.TruncateAt.END     // Catch:{ Exception -> 0x03ba }
            java.lang.CharSequence r4 = android.text.TextUtils.ellipsize(r0, r5, r3, r4)     // Catch:{ Exception -> 0x03ba }
            android.text.StaticLayout r0 = new android.text.StaticLayout     // Catch:{ Exception -> 0x03ba }
            android.text.Layout$Alignment r7 = android.text.Layout.Alignment.ALIGN_NORMAL     // Catch:{ Exception -> 0x03ba }
            r8 = 1065353216(0x3var_, float:1.0)
            r9 = 0
            r10 = 0
            r3 = r0
            r6 = r15
            r3.<init>(r4, r5, r6, r7, r8, r9, r10)     // Catch:{ Exception -> 0x03ba }
            r1.nameLayout = r0     // Catch:{ Exception -> 0x03ba }
            goto L_0x03be
        L_0x03ba:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x03be:
            int r0 = r19.getMeasuredWidth()
            int r3 = org.telegram.messenger.AndroidUtilities.leftBaseline
            int r3 = r3 + 16
            float r3 = (float) r3
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            int r0 = r0 - r3
            boolean r3 = org.telegram.messenger.LocaleController.isRTL
            if (r3 != 0) goto L_0x03e9
            int r3 = org.telegram.messenger.AndroidUtilities.leftBaseline
            float r3 = (float) r3
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            r1.messageLeft = r3
            boolean r3 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r3 == 0) goto L_0x03e2
            r3 = 1095761920(0x41500000, float:13.0)
            goto L_0x03e4
        L_0x03e2:
            r3 = 1091567616(0x41100000, float:9.0)
        L_0x03e4:
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            goto L_0x0405
        L_0x03e9:
            r3 = 1098907648(0x41800000, float:16.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            r1.messageLeft = r3
            int r3 = r19.getMeasuredWidth()
            boolean r4 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r4 == 0) goto L_0x03fe
            r4 = 1115815936(0x42820000, float:65.0)
            goto L_0x0400
        L_0x03fe:
            r4 = 1114898432(0x42740000, float:61.0)
        L_0x0400:
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            int r3 = r3 - r4
        L_0x0405:
            org.telegram.messenger.ImageReceiver r4 = r1.avatarImage
            float r3 = (float) r3
            int r5 = r1.avatarTop
            float r5 = (float) r5
            r6 = 1112539136(0x42500000, float:52.0)
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r6)
            float r7 = (float) r7
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            float r6 = (float) r6
            r4.setImageCoords(r3, r5, r7, r6)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r13)
            int r3 = java.lang.Math.max(r3, r0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r13)
            int r0 = r3 - r0
            float r0 = (float) r0
            android.text.TextUtils$TruncateAt r4 = android.text.TextUtils.TruncateAt.END
            java.lang.CharSequence r7 = android.text.TextUtils.ellipsize(r12, r11, r0, r4)
            android.text.StaticLayout r0 = new android.text.StaticLayout     // Catch:{ Exception -> 0x0441 }
            android.text.Layout$Alignment r10 = android.text.Layout.Alignment.ALIGN_NORMAL     // Catch:{ Exception -> 0x0441 }
            r4 = 1065353216(0x3var_, float:1.0)
            r12 = 0
            r13 = 0
            r6 = r0
            r8 = r11
            r9 = r3
            r11 = r4
            r6.<init>(r7, r8, r9, r10, r11, r12, r13)     // Catch:{ Exception -> 0x0441 }
            r1.messageLayout = r0     // Catch:{ Exception -> 0x0441 }
            goto L_0x0445
        L_0x0441:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x0445:
            boolean r0 = org.telegram.messenger.LocaleController.isRTL
            if (r0 == 0) goto L_0x04d7
            android.text.StaticLayout r0 = r1.nameLayout
            r4 = 0
            if (r0 == 0) goto L_0x04a3
            int r0 = r0.getLineCount()
            if (r0 <= 0) goto L_0x04a3
            android.text.StaticLayout r0 = r1.nameLayout
            float r0 = r0.getLineLeft(r2)
            android.text.StaticLayout r5 = r1.nameLayout
            float r5 = r5.getLineWidth(r2)
            double r5 = (double) r5
            double r5 = java.lang.Math.ceil(r5)
            boolean r7 = r1.drawVerified
            if (r7 == 0) goto L_0x048c
            int r7 = r1.nameLeft
            double r7 = (double) r7
            double r9 = (double) r15
            java.lang.Double.isNaN(r9)
            double r9 = r9 - r5
            java.lang.Double.isNaN(r7)
            double r7 = r7 + r9
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r14)
            double r9 = (double) r9
            java.lang.Double.isNaN(r9)
            double r7 = r7 - r9
            android.graphics.drawable.Drawable r9 = org.telegram.ui.ActionBar.Theme.dialogs_verifiedDrawable
            int r9 = r9.getIntrinsicWidth()
            double r9 = (double) r9
            java.lang.Double.isNaN(r9)
            double r7 = r7 - r9
            int r7 = (int) r7
            r1.nameMuteLeft = r7
        L_0x048c:
            int r0 = (r0 > r4 ? 1 : (r0 == r4 ? 0 : -1))
            if (r0 != 0) goto L_0x04a3
            double r7 = (double) r15
            int r0 = (r5 > r7 ? 1 : (r5 == r7 ? 0 : -1))
            if (r0 >= 0) goto L_0x04a3
            int r0 = r1.nameLeft
            double r9 = (double) r0
            java.lang.Double.isNaN(r7)
            double r7 = r7 - r5
            java.lang.Double.isNaN(r9)
            double r9 = r9 + r7
            int r0 = (int) r9
            r1.nameLeft = r0
        L_0x04a3:
            android.text.StaticLayout r0 = r1.messageLayout
            if (r0 == 0) goto L_0x054e
            int r0 = r0.getLineCount()
            if (r0 <= 0) goto L_0x054e
            android.text.StaticLayout r0 = r1.messageLayout
            float r0 = r0.getLineLeft(r2)
            int r0 = (r0 > r4 ? 1 : (r0 == r4 ? 0 : -1))
            if (r0 != 0) goto L_0x054e
            android.text.StaticLayout r0 = r1.messageLayout
            float r0 = r0.getLineWidth(r2)
            double r4 = (double) r0
            double r4 = java.lang.Math.ceil(r4)
            double r2 = (double) r3
            int r0 = (r4 > r2 ? 1 : (r4 == r2 ? 0 : -1))
            if (r0 >= 0) goto L_0x054e
            int r0 = r1.messageLeft
            double r6 = (double) r0
            java.lang.Double.isNaN(r2)
            double r2 = r2 - r4
            java.lang.Double.isNaN(r6)
            double r6 = r6 + r2
            int r0 = (int) r6
            r1.messageLeft = r0
            goto L_0x054e
        L_0x04d7:
            android.text.StaticLayout r0 = r1.nameLayout
            if (r0 == 0) goto L_0x051b
            int r0 = r0.getLineCount()
            if (r0 <= 0) goto L_0x051b
            android.text.StaticLayout r0 = r1.nameLayout
            float r0 = r0.getLineRight(r2)
            float r4 = (float) r15
            int r4 = (r0 > r4 ? 1 : (r0 == r4 ? 0 : -1))
            if (r4 != 0) goto L_0x050a
            android.text.StaticLayout r4 = r1.nameLayout
            float r4 = r4.getLineWidth(r2)
            double r4 = (double) r4
            double r4 = java.lang.Math.ceil(r4)
            double r6 = (double) r15
            int r8 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1))
            if (r8 >= 0) goto L_0x050a
            int r8 = r1.nameLeft
            double r8 = (double) r8
            java.lang.Double.isNaN(r6)
            double r6 = r6 - r4
            java.lang.Double.isNaN(r8)
            double r8 = r8 - r6
            int r4 = (int) r8
            r1.nameLeft = r4
        L_0x050a:
            boolean r4 = r1.drawVerified
            if (r4 == 0) goto L_0x051b
            int r4 = r1.nameLeft
            float r4 = (float) r4
            float r4 = r4 + r0
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r14)
            float r0 = (float) r0
            float r4 = r4 + r0
            int r0 = (int) r4
            r1.nameMuteLeft = r0
        L_0x051b:
            android.text.StaticLayout r0 = r1.messageLayout
            if (r0 == 0) goto L_0x054e
            int r0 = r0.getLineCount()
            if (r0 <= 0) goto L_0x054e
            android.text.StaticLayout r0 = r1.messageLayout
            float r0 = r0.getLineRight(r2)
            float r4 = (float) r3
            int r0 = (r0 > r4 ? 1 : (r0 == r4 ? 0 : -1))
            if (r0 != 0) goto L_0x054e
            android.text.StaticLayout r0 = r1.messageLayout
            float r0 = r0.getLineWidth(r2)
            double r4 = (double) r0
            double r4 = java.lang.Math.ceil(r4)
            double r2 = (double) r3
            int r0 = (r4 > r2 ? 1 : (r4 == r2 ? 0 : -1))
            if (r0 >= 0) goto L_0x054e
            int r0 = r1.messageLeft
            double r6 = (double) r0
            java.lang.Double.isNaN(r2)
            double r2 = r2 - r4
            java.lang.Double.isNaN(r6)
            double r6 = r6 - r2
            int r0 = (int) r6
            r1.messageLeft = r0
        L_0x054e:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.DialogMeUrlCell.buildLayout():void");
    }

    public void setDialogSelected(boolean z) {
        if (this.isSelected != z) {
            invalidate();
        }
        this.isSelected = z;
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        if (this.isSelected) {
            canvas.drawRect(0.0f, 0.0f, (float) getMeasuredWidth(), (float) getMeasuredHeight(), Theme.dialogs_tabletSeletedPaint);
        }
        if (this.drawNameLock) {
            BaseCell.setDrawableBounds(Theme.dialogs_lockDrawable, this.nameLockLeft, this.nameLockTop);
            Theme.dialogs_lockDrawable.draw(canvas);
        } else if (this.drawNameGroup) {
            BaseCell.setDrawableBounds(Theme.dialogs_groupDrawable, this.nameLockLeft, this.nameLockTop);
            Theme.dialogs_groupDrawable.draw(canvas);
        } else if (this.drawNameBroadcast) {
            BaseCell.setDrawableBounds(Theme.dialogs_broadcastDrawable, this.nameLockLeft, this.nameLockTop);
            Theme.dialogs_broadcastDrawable.draw(canvas);
        } else if (this.drawNameBot) {
            BaseCell.setDrawableBounds(Theme.dialogs_botDrawable, this.nameLockLeft, this.nameLockTop);
            Theme.dialogs_botDrawable.draw(canvas);
        }
        if (this.nameLayout != null) {
            canvas.save();
            canvas.translate((float) this.nameLeft, (float) AndroidUtilities.dp(13.0f));
            this.nameLayout.draw(canvas);
            canvas.restore();
        }
        if (this.messageLayout != null) {
            canvas.save();
            canvas.translate((float) this.messageLeft, (float) this.messageTop);
            try {
                this.messageLayout.draw(canvas);
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
            canvas.restore();
        }
        if (this.drawVerified) {
            BaseCell.setDrawableBounds(Theme.dialogs_verifiedDrawable, this.nameMuteLeft, AndroidUtilities.dp(16.5f));
            BaseCell.setDrawableBounds(Theme.dialogs_verifiedCheckDrawable, this.nameMuteLeft, AndroidUtilities.dp(16.5f));
            Theme.dialogs_verifiedDrawable.draw(canvas);
            Theme.dialogs_verifiedCheckDrawable.draw(canvas);
        }
        if (this.useSeparator) {
            if (LocaleController.isRTL) {
                canvas.drawLine(0.0f, (float) (getMeasuredHeight() - 1), (float) (getMeasuredWidth() - AndroidUtilities.dp((float) AndroidUtilities.leftBaseline)), (float) (getMeasuredHeight() - 1), Theme.dividerPaint);
            } else {
                canvas.drawLine((float) AndroidUtilities.dp((float) AndroidUtilities.leftBaseline), (float) (getMeasuredHeight() - 1), (float) getMeasuredWidth(), (float) (getMeasuredHeight() - 1), Theme.dividerPaint);
            }
        }
        this.avatarImage.draw(canvas);
    }
}
