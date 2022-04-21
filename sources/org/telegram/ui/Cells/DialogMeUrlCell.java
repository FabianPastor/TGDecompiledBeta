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
import org.telegram.tgnet.TLRPC;
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
    private TLRPC.RecentMeUrl recentMeUrl;
    public boolean useSeparator;

    public DialogMeUrlCell(Context context) {
        super(context);
        Theme.createDialogsResources(context);
        this.avatarImage.setRoundRadius(AndroidUtilities.dp(26.0f));
    }

    public void setRecentMeUrl(TLRPC.RecentMeUrl url) {
        this.recentMeUrl = url;
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
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(View.MeasureSpec.getSize(widthMeasureSpec), AndroidUtilities.dp(72.0f) + (this.useSeparator ? 1 : 0));
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean changed, int left, int top, int right, int bottom) {
        if (changed) {
            buildLayout();
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:126:0x03f2  */
    /* JADX WARNING: Removed duplicated region for block: B:131:0x040b  */
    /* JADX WARNING: Removed duplicated region for block: B:148:0x0476  */
    /* JADX WARNING: Removed duplicated region for block: B:173:0x0518  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void buildLayout() {
        /*
            r25 = this;
            r1 = r25
            java.lang.String r0 = ""
            android.text.TextPaint[] r2 = org.telegram.ui.ActionBar.Theme.dialogs_namePaint
            r3 = 0
            r2 = r2[r3]
            android.text.TextPaint[] r4 = org.telegram.ui.ActionBar.Theme.dialogs_messagePaint
            r13 = r4[r3]
            r1.drawNameGroup = r3
            r1.drawNameBroadcast = r3
            r1.drawNameLock = r3
            r1.drawNameBot = r3
            r1.drawVerified = r3
            org.telegram.tgnet.TLRPC$RecentMeUrl r9 = r1.recentMeUrl
            boolean r4 = r9 instanceof org.telegram.tgnet.TLRPC.TL_recentMeUrlChat
            r5 = 0
            r7 = 1099694080(0x418CLASSNAME, float:17.5)
            r8 = 1099169792(0x41840000, float:16.5)
            r10 = 1
            r11 = 1096810496(0x41600000, float:14.0)
            if (r4 == 0) goto L_0x00b9
            int r4 = r1.currentAccount
            org.telegram.messenger.MessagesController r4 = org.telegram.messenger.MessagesController.getInstance(r4)
            org.telegram.tgnet.TLRPC$RecentMeUrl r9 = r1.recentMeUrl
            long r14 = r9.chat_id
            java.lang.Long r9 = java.lang.Long.valueOf(r14)
            org.telegram.tgnet.TLRPC$Chat r4 = r4.getChat(r9)
            long r14 = r4.id
            int r9 = (r14 > r5 ? 1 : (r14 == r5 ? 0 : -1))
            if (r9 < 0) goto L_0x0052
            boolean r5 = org.telegram.messenger.ChatObject.isChannel(r4)
            if (r5 == 0) goto L_0x0049
            boolean r5 = r4.megagroup
            if (r5 != 0) goto L_0x0049
            goto L_0x0052
        L_0x0049:
            r1.drawNameGroup = r10
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r7)
            r1.nameLockTop = r5
            goto L_0x005a
        L_0x0052:
            r1.drawNameBroadcast = r10
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r8)
            r1.nameLockTop = r5
        L_0x005a:
            boolean r5 = r4.verified
            r1.drawVerified = r5
            boolean r5 = org.telegram.messenger.LocaleController.isRTL
            if (r5 != 0) goto L_0x0085
            int r5 = org.telegram.messenger.AndroidUtilities.leftBaseline
            float r5 = (float) r5
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            r1.nameLockLeft = r5
            int r5 = org.telegram.messenger.AndroidUtilities.leftBaseline
            int r5 = r5 + 4
            float r5 = (float) r5
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            boolean r6 = r1.drawNameGroup
            if (r6 == 0) goto L_0x007b
            android.graphics.drawable.Drawable r6 = org.telegram.ui.ActionBar.Theme.dialogs_groupDrawable
            goto L_0x007d
        L_0x007b:
            android.graphics.drawable.Drawable r6 = org.telegram.ui.ActionBar.Theme.dialogs_broadcastDrawable
        L_0x007d:
            int r6 = r6.getIntrinsicWidth()
            int r5 = r5 + r6
            r1.nameLeft = r5
            goto L_0x00a7
        L_0x0085:
            int r5 = r25.getMeasuredWidth()
            int r6 = org.telegram.messenger.AndroidUtilities.leftBaseline
            float r6 = (float) r6
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            int r5 = r5 - r6
            boolean r6 = r1.drawNameGroup
            if (r6 == 0) goto L_0x0098
            android.graphics.drawable.Drawable r6 = org.telegram.ui.ActionBar.Theme.dialogs_groupDrawable
            goto L_0x009a
        L_0x0098:
            android.graphics.drawable.Drawable r6 = org.telegram.ui.ActionBar.Theme.dialogs_broadcastDrawable
        L_0x009a:
            int r6 = r6.getIntrinsicWidth()
            int r5 = r5 - r6
            r1.nameLockLeft = r5
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r11)
            r1.nameLeft = r5
        L_0x00a7:
            java.lang.String r0 = r4.title
            org.telegram.ui.Components.AvatarDrawable r5 = r1.avatarDrawable
            r5.setInfo((org.telegram.tgnet.TLRPC.Chat) r4)
            org.telegram.messenger.ImageReceiver r5 = r1.avatarImage
            org.telegram.ui.Components.AvatarDrawable r6 = r1.avatarDrawable
            org.telegram.tgnet.TLRPC$RecentMeUrl r7 = r1.recentMeUrl
            r5.setForUserOrChat(r4, r6, r7)
            goto L_0x02f1
        L_0x00b9:
            boolean r4 = r9 instanceof org.telegram.tgnet.TLRPC.TL_recentMeUrlUser
            if (r4 == 0) goto L_0x0144
            int r4 = r1.currentAccount
            org.telegram.messenger.MessagesController r4 = org.telegram.messenger.MessagesController.getInstance(r4)
            org.telegram.tgnet.TLRPC$RecentMeUrl r5 = r1.recentMeUrl
            long r5 = r5.user_id
            java.lang.Long r5 = java.lang.Long.valueOf(r5)
            org.telegram.tgnet.TLRPC$User r4 = r4.getUser(r5)
            boolean r5 = org.telegram.messenger.LocaleController.isRTL
            if (r5 != 0) goto L_0x00dd
            int r5 = org.telegram.messenger.AndroidUtilities.leftBaseline
            float r5 = (float) r5
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            r1.nameLeft = r5
            goto L_0x00e3
        L_0x00dd:
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r11)
            r1.nameLeft = r5
        L_0x00e3:
            if (r4 == 0) goto L_0x0130
            boolean r5 = r4.bot
            if (r5 == 0) goto L_0x012c
            r1.drawNameBot = r10
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r8)
            r1.nameLockTop = r5
            boolean r5 = org.telegram.messenger.LocaleController.isRTL
            if (r5 != 0) goto L_0x0111
            int r5 = org.telegram.messenger.AndroidUtilities.leftBaseline
            float r5 = (float) r5
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            r1.nameLockLeft = r5
            int r5 = org.telegram.messenger.AndroidUtilities.leftBaseline
            int r5 = r5 + 4
            float r5 = (float) r5
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            android.graphics.drawable.Drawable r6 = org.telegram.ui.ActionBar.Theme.dialogs_botDrawable
            int r6 = r6.getIntrinsicWidth()
            int r5 = r5 + r6
            r1.nameLeft = r5
            goto L_0x012c
        L_0x0111:
            int r5 = r25.getMeasuredWidth()
            int r6 = org.telegram.messenger.AndroidUtilities.leftBaseline
            float r6 = (float) r6
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            int r5 = r5 - r6
            android.graphics.drawable.Drawable r6 = org.telegram.ui.ActionBar.Theme.dialogs_botDrawable
            int r6 = r6.getIntrinsicWidth()
            int r5 = r5 - r6
            r1.nameLockLeft = r5
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r11)
            r1.nameLeft = r5
        L_0x012c:
            boolean r5 = r4.verified
            r1.drawVerified = r5
        L_0x0130:
            java.lang.String r0 = org.telegram.messenger.UserObject.getUserName(r4)
            org.telegram.ui.Components.AvatarDrawable r5 = r1.avatarDrawable
            r5.setInfo((org.telegram.tgnet.TLRPC.User) r4)
            org.telegram.messenger.ImageReceiver r5 = r1.avatarImage
            org.telegram.ui.Components.AvatarDrawable r6 = r1.avatarDrawable
            org.telegram.tgnet.TLRPC$RecentMeUrl r7 = r1.recentMeUrl
            r5.setForUserOrChat(r4, r6, r7)
            goto L_0x02f1
        L_0x0144:
            boolean r4 = r9 instanceof org.telegram.tgnet.TLRPC.TL_recentMeUrlStickerSet
            r12 = 0
            r14 = 5
            if (r4 == 0) goto L_0x0195
            boolean r4 = org.telegram.messenger.LocaleController.isRTL
            if (r4 != 0) goto L_0x0159
            int r4 = org.telegram.messenger.AndroidUtilities.leftBaseline
            float r4 = (float) r4
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            r1.nameLeft = r4
            goto L_0x015f
        L_0x0159:
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r11)
            r1.nameLeft = r4
        L_0x015f:
            org.telegram.tgnet.TLRPC$RecentMeUrl r4 = r1.recentMeUrl
            org.telegram.tgnet.TLRPC$StickerSetCovered r4 = r4.set
            org.telegram.tgnet.TLRPC$StickerSet r4 = r4.set
            java.lang.String r0 = r4.title
            org.telegram.ui.Components.AvatarDrawable r4 = r1.avatarDrawable
            org.telegram.tgnet.TLRPC$RecentMeUrl r5 = r1.recentMeUrl
            org.telegram.tgnet.TLRPC$StickerSetCovered r5 = r5.set
            org.telegram.tgnet.TLRPC$StickerSet r5 = r5.set
            java.lang.String r5 = r5.title
            r4.setInfo(r14, r5, r12)
            org.telegram.messenger.ImageReceiver r4 = r1.avatarImage
            org.telegram.tgnet.TLRPC$RecentMeUrl r5 = r1.recentMeUrl
            org.telegram.tgnet.TLRPC$StickerSetCovered r5 = r5.set
            org.telegram.tgnet.TLRPC$Document r5 = r5.cover
            org.telegram.messenger.ImageLocation r17 = org.telegram.messenger.ImageLocation.getForDocument(r5)
            r18 = 0
            org.telegram.ui.Components.AvatarDrawable r5 = r1.avatarDrawable
            r20 = 0
            org.telegram.tgnet.TLRPC$RecentMeUrl r6 = r1.recentMeUrl
            r22 = 0
            r16 = r4
            r19 = r5
            r21 = r6
            r16.setImage(r17, r18, r19, r20, r21, r22)
            goto L_0x02f1
        L_0x0195:
            boolean r4 = r9 instanceof org.telegram.tgnet.TLRPC.TL_recentMeUrlChatInvite
            if (r4 == 0) goto L_0x02be
            boolean r4 = org.telegram.messenger.LocaleController.isRTL
            if (r4 != 0) goto L_0x01a7
            int r4 = org.telegram.messenger.AndroidUtilities.leftBaseline
            float r4 = (float) r4
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            r1.nameLeft = r4
            goto L_0x01ad
        L_0x01a7:
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r11)
            r1.nameLeft = r4
        L_0x01ad:
            org.telegram.tgnet.TLRPC$RecentMeUrl r4 = r1.recentMeUrl
            org.telegram.tgnet.TLRPC$ChatInvite r4 = r4.chat_invite
            org.telegram.tgnet.TLRPC$Chat r4 = r4.chat
            if (r4 == 0) goto L_0x0216
            org.telegram.ui.Components.AvatarDrawable r4 = r1.avatarDrawable
            org.telegram.tgnet.TLRPC$RecentMeUrl r9 = r1.recentMeUrl
            org.telegram.tgnet.TLRPC$ChatInvite r9 = r9.chat_invite
            org.telegram.tgnet.TLRPC$Chat r9 = r9.chat
            r4.setInfo((org.telegram.tgnet.TLRPC.Chat) r9)
            org.telegram.tgnet.TLRPC$RecentMeUrl r4 = r1.recentMeUrl
            org.telegram.tgnet.TLRPC$ChatInvite r4 = r4.chat_invite
            org.telegram.tgnet.TLRPC$Chat r4 = r4.chat
            java.lang.String r0 = r4.title
            org.telegram.tgnet.TLRPC$RecentMeUrl r4 = r1.recentMeUrl
            org.telegram.tgnet.TLRPC$ChatInvite r4 = r4.chat_invite
            org.telegram.tgnet.TLRPC$Chat r4 = r4.chat
            long r14 = r4.id
            int r4 = (r14 > r5 ? 1 : (r14 == r5 ? 0 : -1))
            if (r4 < 0) goto L_0x01f4
            org.telegram.tgnet.TLRPC$RecentMeUrl r4 = r1.recentMeUrl
            org.telegram.tgnet.TLRPC$ChatInvite r4 = r4.chat_invite
            org.telegram.tgnet.TLRPC$Chat r4 = r4.chat
            boolean r4 = org.telegram.messenger.ChatObject.isChannel(r4)
            if (r4 == 0) goto L_0x01eb
            org.telegram.tgnet.TLRPC$RecentMeUrl r4 = r1.recentMeUrl
            org.telegram.tgnet.TLRPC$ChatInvite r4 = r4.chat_invite
            org.telegram.tgnet.TLRPC$Chat r4 = r4.chat
            boolean r4 = r4.megagroup
            if (r4 != 0) goto L_0x01eb
            goto L_0x01f4
        L_0x01eb:
            r1.drawNameGroup = r10
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r7)
            r1.nameLockTop = r4
            goto L_0x01fc
        L_0x01f4:
            r1.drawNameBroadcast = r10
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r8)
            r1.nameLockTop = r4
        L_0x01fc:
            org.telegram.tgnet.TLRPC$RecentMeUrl r4 = r1.recentMeUrl
            org.telegram.tgnet.TLRPC$ChatInvite r4 = r4.chat_invite
            org.telegram.tgnet.TLRPC$Chat r4 = r4.chat
            boolean r4 = r4.verified
            r1.drawVerified = r4
            org.telegram.messenger.ImageReceiver r4 = r1.avatarImage
            org.telegram.tgnet.TLRPC$RecentMeUrl r5 = r1.recentMeUrl
            org.telegram.tgnet.TLRPC$ChatInvite r5 = r5.chat_invite
            org.telegram.tgnet.TLRPC$Chat r5 = r5.chat
            org.telegram.ui.Components.AvatarDrawable r6 = r1.avatarDrawable
            org.telegram.tgnet.TLRPC$RecentMeUrl r7 = r1.recentMeUrl
            r4.setForUserOrChat(r5, r6, r7)
            goto L_0x0274
        L_0x0216:
            org.telegram.tgnet.TLRPC$RecentMeUrl r4 = r1.recentMeUrl
            org.telegram.tgnet.TLRPC$ChatInvite r4 = r4.chat_invite
            java.lang.String r0 = r4.title
            org.telegram.ui.Components.AvatarDrawable r4 = r1.avatarDrawable
            org.telegram.tgnet.TLRPC$RecentMeUrl r5 = r1.recentMeUrl
            org.telegram.tgnet.TLRPC$ChatInvite r5 = r5.chat_invite
            java.lang.String r5 = r5.title
            r4.setInfo(r14, r5, r12)
            org.telegram.tgnet.TLRPC$RecentMeUrl r4 = r1.recentMeUrl
            org.telegram.tgnet.TLRPC$ChatInvite r4 = r4.chat_invite
            boolean r4 = r4.broadcast
            if (r4 != 0) goto L_0x0241
            org.telegram.tgnet.TLRPC$RecentMeUrl r4 = r1.recentMeUrl
            org.telegram.tgnet.TLRPC$ChatInvite r4 = r4.chat_invite
            boolean r4 = r4.channel
            if (r4 == 0) goto L_0x0238
            goto L_0x0241
        L_0x0238:
            r1.drawNameGroup = r10
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r7)
            r1.nameLockTop = r4
            goto L_0x0249
        L_0x0241:
            r1.drawNameBroadcast = r10
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r8)
            r1.nameLockTop = r4
        L_0x0249:
            org.telegram.tgnet.TLRPC$RecentMeUrl r4 = r1.recentMeUrl
            org.telegram.tgnet.TLRPC$ChatInvite r4 = r4.chat_invite
            org.telegram.tgnet.TLRPC$Photo r4 = r4.photo
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r4 = r4.sizes
            r5 = 50
            org.telegram.tgnet.TLRPC$PhotoSize r4 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r4, r5)
            org.telegram.messenger.ImageReceiver r14 = r1.avatarImage
            org.telegram.tgnet.TLRPC$RecentMeUrl r5 = r1.recentMeUrl
            org.telegram.tgnet.TLRPC$ChatInvite r5 = r5.chat_invite
            org.telegram.tgnet.TLRPC$Photo r5 = r5.photo
            org.telegram.messenger.ImageLocation r15 = org.telegram.messenger.ImageLocation.getForPhoto((org.telegram.tgnet.TLRPC.PhotoSize) r4, (org.telegram.tgnet.TLRPC.Photo) r5)
            org.telegram.ui.Components.AvatarDrawable r5 = r1.avatarDrawable
            r18 = 0
            org.telegram.tgnet.TLRPC$RecentMeUrl r6 = r1.recentMeUrl
            r20 = 0
            java.lang.String r16 = "50_50"
            r17 = r5
            r19 = r6
            r14.setImage(r15, r16, r17, r18, r19, r20)
        L_0x0274:
            boolean r4 = org.telegram.messenger.LocaleController.isRTL
            if (r4 != 0) goto L_0x029b
            int r4 = org.telegram.messenger.AndroidUtilities.leftBaseline
            float r4 = (float) r4
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            r1.nameLockLeft = r4
            int r4 = org.telegram.messenger.AndroidUtilities.leftBaseline
            int r4 = r4 + 4
            float r4 = (float) r4
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            boolean r5 = r1.drawNameGroup
            if (r5 == 0) goto L_0x0291
            android.graphics.drawable.Drawable r5 = org.telegram.ui.ActionBar.Theme.dialogs_groupDrawable
            goto L_0x0293
        L_0x0291:
            android.graphics.drawable.Drawable r5 = org.telegram.ui.ActionBar.Theme.dialogs_broadcastDrawable
        L_0x0293:
            int r5 = r5.getIntrinsicWidth()
            int r4 = r4 + r5
            r1.nameLeft = r4
            goto L_0x02f1
        L_0x029b:
            int r4 = r25.getMeasuredWidth()
            int r5 = org.telegram.messenger.AndroidUtilities.leftBaseline
            float r5 = (float) r5
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            int r4 = r4 - r5
            boolean r5 = r1.drawNameGroup
            if (r5 == 0) goto L_0x02ae
            android.graphics.drawable.Drawable r5 = org.telegram.ui.ActionBar.Theme.dialogs_groupDrawable
            goto L_0x02b0
        L_0x02ae:
            android.graphics.drawable.Drawable r5 = org.telegram.ui.ActionBar.Theme.dialogs_broadcastDrawable
        L_0x02b0:
            int r5 = r5.getIntrinsicWidth()
            int r4 = r4 - r5
            r1.nameLockLeft = r4
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r11)
            r1.nameLeft = r4
            goto L_0x02f1
        L_0x02be:
            boolean r4 = r9 instanceof org.telegram.tgnet.TLRPC.TL_recentMeUrlUnknown
            if (r4 == 0) goto L_0x02e6
            boolean r4 = org.telegram.messenger.LocaleController.isRTL
            if (r4 != 0) goto L_0x02d0
            int r4 = org.telegram.messenger.AndroidUtilities.leftBaseline
            float r4 = (float) r4
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            r1.nameLeft = r4
            goto L_0x02d6
        L_0x02d0:
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r11)
            r1.nameLeft = r4
        L_0x02d6:
            java.lang.String r0 = "Url"
            org.telegram.messenger.ImageReceiver r4 = r1.avatarImage
            r5 = 0
            r6 = 0
            org.telegram.ui.Components.AvatarDrawable r7 = r1.avatarDrawable
            r8 = 0
            org.telegram.tgnet.TLRPC$RecentMeUrl r9 = r1.recentMeUrl
            r10 = 0
            r4.setImage(r5, r6, r7, r8, r9, r10)
            goto L_0x02f1
        L_0x02e6:
            org.telegram.messenger.ImageReceiver r4 = r1.avatarImage
            r5 = 0
            r6 = 0
            org.telegram.ui.Components.AvatarDrawable r7 = r1.avatarDrawable
            r8 = 0
            r10 = 0
            r4.setImage(r5, r6, r7, r8, r9, r10)
        L_0x02f1:
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            int r5 = r1.currentAccount
            org.telegram.messenger.MessagesController r5 = org.telegram.messenger.MessagesController.getInstance(r5)
            java.lang.String r5 = r5.linkPrefix
            r4.append(r5)
            java.lang.String r5 = "/"
            r4.append(r5)
            org.telegram.tgnet.TLRPC$RecentMeUrl r5 = r1.recentMeUrl
            java.lang.String r5 = r5.url
            r4.append(r5)
            java.lang.String r14 = r4.toString()
            boolean r4 = android.text.TextUtils.isEmpty(r0)
            if (r4 == 0) goto L_0x0322
            r4 = 2131626025(0x7f0e0829, float:1.8879275E38)
            java.lang.String r5 = "HiddenName"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r5, r4)
            r15 = r0
            goto L_0x0323
        L_0x0322:
            r15 = r0
        L_0x0323:
            boolean r0 = org.telegram.messenger.LocaleController.isRTL
            if (r0 != 0) goto L_0x0334
            int r0 = r25.getMeasuredWidth()
            int r4 = r1.nameLeft
            int r0 = r0 - r4
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r11)
            int r0 = r0 - r4
            goto L_0x0343
        L_0x0334:
            int r0 = r25.getMeasuredWidth()
            int r4 = r1.nameLeft
            int r0 = r0 - r4
            int r4 = org.telegram.messenger.AndroidUtilities.leftBaseline
            float r4 = (float) r4
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            int r0 = r0 - r4
        L_0x0343:
            boolean r4 = r1.drawNameLock
            r5 = 1082130432(0x40800000, float:4.0)
            if (r4 == 0) goto L_0x0356
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r5)
            android.graphics.drawable.Drawable r5 = org.telegram.ui.ActionBar.Theme.dialogs_lockDrawable
            int r5 = r5.getIntrinsicWidth()
            int r4 = r4 + r5
            int r0 = r0 - r4
            goto L_0x0388
        L_0x0356:
            boolean r4 = r1.drawNameGroup
            if (r4 == 0) goto L_0x0367
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r5)
            android.graphics.drawable.Drawable r5 = org.telegram.ui.ActionBar.Theme.dialogs_groupDrawable
            int r5 = r5.getIntrinsicWidth()
            int r4 = r4 + r5
            int r0 = r0 - r4
            goto L_0x0388
        L_0x0367:
            boolean r4 = r1.drawNameBroadcast
            if (r4 == 0) goto L_0x0378
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r5)
            android.graphics.drawable.Drawable r5 = org.telegram.ui.ActionBar.Theme.dialogs_broadcastDrawable
            int r5 = r5.getIntrinsicWidth()
            int r4 = r4 + r5
            int r0 = r0 - r4
            goto L_0x0388
        L_0x0378:
            boolean r4 = r1.drawNameBot
            if (r4 == 0) goto L_0x0388
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r5)
            android.graphics.drawable.Drawable r5 = org.telegram.ui.ActionBar.Theme.dialogs_botDrawable
            int r5 = r5.getIntrinsicWidth()
            int r4 = r4 + r5
            int r0 = r0 - r4
        L_0x0388:
            boolean r4 = r1.drawVerified
            r16 = 1086324736(0x40CLASSNAME, float:6.0)
            if (r4 == 0) goto L_0x03a3
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r16)
            android.graphics.drawable.Drawable r5 = org.telegram.ui.ActionBar.Theme.dialogs_verifiedDrawable
            int r5 = r5.getIntrinsicWidth()
            int r4 = r4 + r5
            int r0 = r0 - r4
            boolean r5 = org.telegram.messenger.LocaleController.isRTL
            if (r5 == 0) goto L_0x03a3
            int r5 = r1.nameLeft
            int r5 = r5 + r4
            r1.nameLeft = r5
        L_0x03a3:
            r12 = 1094713344(0x41400000, float:12.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r12)
            int r11 = java.lang.Math.max(r4, r0)
            r0 = 10
            r4 = 32
            java.lang.String r0 = r15.replace(r0, r4)     // Catch:{ Exception -> 0x03da }
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r12)     // Catch:{ Exception -> 0x03da }
            int r4 = r11 - r4
            float r4 = (float) r4     // Catch:{ Exception -> 0x03da }
            android.text.TextUtils$TruncateAt r5 = android.text.TextUtils.TruncateAt.END     // Catch:{ Exception -> 0x03da }
            java.lang.CharSequence r5 = android.text.TextUtils.ellipsize(r0, r2, r4, r5)     // Catch:{ Exception -> 0x03da }
            android.text.StaticLayout r0 = new android.text.StaticLayout     // Catch:{ Exception -> 0x03da }
            android.text.Layout$Alignment r8 = android.text.Layout.Alignment.ALIGN_NORMAL     // Catch:{ Exception -> 0x03da }
            r9 = 1065353216(0x3var_, float:1.0)
            r10 = 0
            r17 = 0
            r4 = r0
            r6 = r2
            r7 = r11
            r23 = r11
            r11 = r17
            r4.<init>(r5, r6, r7, r8, r9, r10, r11)     // Catch:{ Exception -> 0x03d8 }
            r1.nameLayout = r0     // Catch:{ Exception -> 0x03d8 }
            goto L_0x03e0
        L_0x03d8:
            r0 = move-exception
            goto L_0x03dd
        L_0x03da:
            r0 = move-exception
            r23 = r11
        L_0x03dd:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x03e0:
            int r0 = r25.getMeasuredWidth()
            int r4 = org.telegram.messenger.AndroidUtilities.leftBaseline
            int r4 = r4 + 16
            float r4 = (float) r4
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            int r0 = r0 - r4
            boolean r4 = org.telegram.messenger.LocaleController.isRTL
            if (r4 != 0) goto L_0x040b
            int r4 = org.telegram.messenger.AndroidUtilities.leftBaseline
            float r4 = (float) r4
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            r1.messageLeft = r4
            boolean r4 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r4 == 0) goto L_0x0404
            r4 = 1095761920(0x41500000, float:13.0)
            goto L_0x0406
        L_0x0404:
            r4 = 1091567616(0x41100000, float:9.0)
        L_0x0406:
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            goto L_0x0427
        L_0x040b:
            r4 = 1098907648(0x41800000, float:16.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            r1.messageLeft = r4
            int r4 = r25.getMeasuredWidth()
            boolean r5 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r5 == 0) goto L_0x0420
            r5 = 1115815936(0x42820000, float:65.0)
            goto L_0x0422
        L_0x0420:
            r5 = 1114898432(0x42740000, float:61.0)
        L_0x0422:
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            int r4 = r4 - r5
        L_0x0427:
            org.telegram.messenger.ImageReceiver r5 = r1.avatarImage
            float r6 = (float) r4
            int r7 = r1.avatarTop
            float r7 = (float) r7
            r8 = 1112539136(0x42500000, float:52.0)
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r8)
            float r9 = (float) r9
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r8)
            float r8 = (float) r8
            r5.setImageCoords(r6, r7, r9, r8)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r12)
            int r11 = java.lang.Math.max(r5, r0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r12)
            int r0 = r11 - r0
            float r0 = (float) r0
            android.text.TextUtils$TruncateAt r5 = android.text.TextUtils.TruncateAt.END
            java.lang.CharSequence r17 = android.text.TextUtils.ellipsize(r14, r13, r0, r5)
            android.text.StaticLayout r0 = new android.text.StaticLayout     // Catch:{ Exception -> 0x046c }
            android.text.Layout$Alignment r9 = android.text.Layout.Alignment.ALIGN_NORMAL     // Catch:{ Exception -> 0x046c }
            r10 = 1065353216(0x3var_, float:1.0)
            r12 = 0
            r18 = 0
            r5 = r0
            r6 = r17
            r7 = r13
            r8 = r11
            r24 = r11
            r11 = r12
            r12 = r18
            r5.<init>(r6, r7, r8, r9, r10, r11, r12)     // Catch:{ Exception -> 0x046a }
            r1.messageLayout = r0     // Catch:{ Exception -> 0x046a }
            goto L_0x0472
        L_0x046a:
            r0 = move-exception
            goto L_0x046f
        L_0x046c:
            r0 = move-exception
            r24 = r11
        L_0x046f:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x0472:
            boolean r0 = org.telegram.messenger.LocaleController.isRTL
            if (r0 == 0) goto L_0x0518
            android.text.StaticLayout r0 = r1.nameLayout
            r5 = 0
            if (r0 == 0) goto L_0x04d7
            int r0 = r0.getLineCount()
            if (r0 <= 0) goto L_0x04d7
            android.text.StaticLayout r0 = r1.nameLayout
            float r0 = r0.getLineLeft(r3)
            android.text.StaticLayout r6 = r1.nameLayout
            float r6 = r6.getLineWidth(r3)
            double r6 = (double) r6
            double r6 = java.lang.Math.ceil(r6)
            boolean r8 = r1.drawVerified
            if (r8 == 0) goto L_0x04bc
            int r8 = r1.nameLeft
            double r8 = (double) r8
            r10 = r23
            double r11 = (double) r10
            java.lang.Double.isNaN(r11)
            double r11 = r11 - r6
            java.lang.Double.isNaN(r8)
            double r8 = r8 + r11
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r16)
            double r11 = (double) r11
            java.lang.Double.isNaN(r11)
            double r8 = r8 - r11
            android.graphics.drawable.Drawable r11 = org.telegram.ui.ActionBar.Theme.dialogs_verifiedDrawable
            int r11 = r11.getIntrinsicWidth()
            double r11 = (double) r11
            java.lang.Double.isNaN(r11)
            double r8 = r8 - r11
            int r8 = (int) r8
            r1.nameMuteLeft = r8
            goto L_0x04be
        L_0x04bc:
            r10 = r23
        L_0x04be:
            int r8 = (r0 > r5 ? 1 : (r0 == r5 ? 0 : -1))
            if (r8 != 0) goto L_0x04d9
            double r8 = (double) r10
            int r11 = (r6 > r8 ? 1 : (r6 == r8 ? 0 : -1))
            if (r11 >= 0) goto L_0x04d9
            int r8 = r1.nameLeft
            double r8 = (double) r8
            double r11 = (double) r10
            java.lang.Double.isNaN(r11)
            double r11 = r11 - r6
            java.lang.Double.isNaN(r8)
            double r8 = r8 + r11
            int r8 = (int) r8
            r1.nameLeft = r8
            goto L_0x04d9
        L_0x04d7:
            r10 = r23
        L_0x04d9:
            android.text.StaticLayout r0 = r1.messageLayout
            if (r0 == 0) goto L_0x0514
            int r0 = r0.getLineCount()
            if (r0 <= 0) goto L_0x0514
            android.text.StaticLayout r0 = r1.messageLayout
            float r0 = r0.getLineLeft(r3)
            int r5 = (r0 > r5 ? 1 : (r0 == r5 ? 0 : -1))
            if (r5 != 0) goto L_0x0510
            android.text.StaticLayout r5 = r1.messageLayout
            float r3 = r5.getLineWidth(r3)
            double r5 = (double) r3
            double r5 = java.lang.Math.ceil(r5)
            r7 = r24
            double r8 = (double) r7
            int r3 = (r5 > r8 ? 1 : (r5 == r8 ? 0 : -1))
            if (r3 >= 0) goto L_0x0595
            int r3 = r1.messageLeft
            double r8 = (double) r3
            double r11 = (double) r7
            java.lang.Double.isNaN(r11)
            double r11 = r11 - r5
            java.lang.Double.isNaN(r8)
            double r8 = r8 + r11
            int r3 = (int) r8
            r1.messageLeft = r3
            goto L_0x0595
        L_0x0510:
            r7 = r24
            goto L_0x0595
        L_0x0514:
            r7 = r24
            goto L_0x0595
        L_0x0518:
            r10 = r23
            r7 = r24
            android.text.StaticLayout r0 = r1.nameLayout
            if (r0 == 0) goto L_0x0561
            int r0 = r0.getLineCount()
            if (r0 <= 0) goto L_0x0561
            android.text.StaticLayout r0 = r1.nameLayout
            float r0 = r0.getLineRight(r3)
            float r5 = (float) r10
            int r5 = (r0 > r5 ? 1 : (r0 == r5 ? 0 : -1))
            if (r5 != 0) goto L_0x0550
            android.text.StaticLayout r5 = r1.nameLayout
            float r5 = r5.getLineWidth(r3)
            double r5 = (double) r5
            double r5 = java.lang.Math.ceil(r5)
            double r8 = (double) r10
            int r11 = (r5 > r8 ? 1 : (r5 == r8 ? 0 : -1))
            if (r11 >= 0) goto L_0x0550
            int r8 = r1.nameLeft
            double r8 = (double) r8
            double r11 = (double) r10
            java.lang.Double.isNaN(r11)
            double r11 = r11 - r5
            java.lang.Double.isNaN(r8)
            double r8 = r8 - r11
            int r8 = (int) r8
            r1.nameLeft = r8
        L_0x0550:
            boolean r5 = r1.drawVerified
            if (r5 == 0) goto L_0x0561
            int r5 = r1.nameLeft
            float r5 = (float) r5
            float r5 = r5 + r0
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r16)
            float r6 = (float) r6
            float r5 = r5 + r6
            int r5 = (int) r5
            r1.nameMuteLeft = r5
        L_0x0561:
            android.text.StaticLayout r0 = r1.messageLayout
            if (r0 == 0) goto L_0x0595
            int r0 = r0.getLineCount()
            if (r0 <= 0) goto L_0x0595
            android.text.StaticLayout r0 = r1.messageLayout
            float r0 = r0.getLineRight(r3)
            float r5 = (float) r7
            int r5 = (r0 > r5 ? 1 : (r0 == r5 ? 0 : -1))
            if (r5 != 0) goto L_0x0595
            android.text.StaticLayout r5 = r1.messageLayout
            float r3 = r5.getLineWidth(r3)
            double r5 = (double) r3
            double r5 = java.lang.Math.ceil(r5)
            double r8 = (double) r7
            int r3 = (r5 > r8 ? 1 : (r5 == r8 ? 0 : -1))
            if (r3 >= 0) goto L_0x0595
            int r3 = r1.messageLeft
            double r8 = (double) r3
            double r11 = (double) r7
            java.lang.Double.isNaN(r11)
            double r11 = r11 - r5
            java.lang.Double.isNaN(r8)
            double r8 = r8 - r11
            int r3 = (int) r8
            r1.messageLeft = r3
        L_0x0595:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.DialogMeUrlCell.buildLayout():void");
    }

    public void setDialogSelected(boolean value) {
        if (this.isSelected != value) {
            invalidate();
        }
        this.isSelected = value;
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        if (this.isSelected) {
            canvas.drawRect(0.0f, 0.0f, (float) getMeasuredWidth(), (float) getMeasuredHeight(), Theme.dialogs_tabletSeletedPaint);
        }
        if (this.drawNameLock) {
            setDrawableBounds(Theme.dialogs_lockDrawable, this.nameLockLeft, this.nameLockTop);
            Theme.dialogs_lockDrawable.draw(canvas);
        } else if (this.drawNameGroup) {
            setDrawableBounds(Theme.dialogs_groupDrawable, this.nameLockLeft, this.nameLockTop);
            Theme.dialogs_groupDrawable.draw(canvas);
        } else if (this.drawNameBroadcast) {
            setDrawableBounds(Theme.dialogs_broadcastDrawable, this.nameLockLeft, this.nameLockTop);
            Theme.dialogs_broadcastDrawable.draw(canvas);
        } else if (this.drawNameBot) {
            setDrawableBounds(Theme.dialogs_botDrawable, this.nameLockLeft, this.nameLockTop);
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
            setDrawableBounds(Theme.dialogs_verifiedDrawable, this.nameMuteLeft, AndroidUtilities.dp(16.5f));
            setDrawableBounds(Theme.dialogs_verifiedCheckDrawable, this.nameMuteLeft, AndroidUtilities.dp(16.5f));
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

    public boolean hasOverlappingRendering() {
        return false;
    }
}
