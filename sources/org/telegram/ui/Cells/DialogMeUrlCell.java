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

    /* JADX WARNING: Removed duplicated region for block: B:101:0x0376  */
    /* JADX WARNING: Removed duplicated region for block: B:126:0x0418  */
    /* JADX WARNING: Removed duplicated region for block: B:79:0x02f2  */
    /* JADX WARNING: Removed duplicated region for block: B:84:0x030b  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void buildLayout() {
        /*
            r23 = this;
            r1 = r23
            java.lang.String r0 = ""
            android.text.TextPaint[] r2 = org.telegram.ui.ActionBar.Theme.dialogs_namePaint
            r3 = 0
            r2 = r2[r3]
            android.text.TextPaint[] r4 = org.telegram.ui.ActionBar.Theme.dialogs_messagePaint
            r13 = r4[r3]
            r1.drawNameLock = r3
            r1.drawVerified = r3
            org.telegram.tgnet.TLRPC$RecentMeUrl r9 = r1.recentMeUrl
            boolean r4 = r9 instanceof org.telegram.tgnet.TLRPC.TL_recentMeUrlChat
            r11 = 1096810496(0x41600000, float:14.0)
            if (r4 == 0) goto L_0x006e
            int r4 = r1.currentAccount
            org.telegram.messenger.MessagesController r4 = org.telegram.messenger.MessagesController.getInstance(r4)
            org.telegram.tgnet.TLRPC$RecentMeUrl r5 = r1.recentMeUrl
            long r5 = r5.chat_id
            java.lang.Long r5 = java.lang.Long.valueOf(r5)
            org.telegram.tgnet.TLRPC$Chat r4 = r4.getChat(r5)
            boolean r5 = r4.verified
            r1.drawVerified = r5
            boolean r5 = org.telegram.messenger.LocaleController.isRTL
            if (r5 != 0) goto L_0x0048
            int r5 = org.telegram.messenger.AndroidUtilities.leftBaseline
            float r5 = (float) r5
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            r1.nameLockLeft = r5
            int r5 = org.telegram.messenger.AndroidUtilities.leftBaseline
            int r5 = r5 + 4
            float r5 = (float) r5
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            r1.nameLeft = r5
            goto L_0x005c
        L_0x0048:
            int r5 = r23.getMeasuredWidth()
            int r6 = org.telegram.messenger.AndroidUtilities.leftBaseline
            float r6 = (float) r6
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            int r5 = r5 - r6
            r1.nameLockLeft = r5
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r11)
            r1.nameLeft = r5
        L_0x005c:
            java.lang.String r0 = r4.title
            org.telegram.ui.Components.AvatarDrawable r5 = r1.avatarDrawable
            r5.setInfo((org.telegram.tgnet.TLRPC.Chat) r4)
            org.telegram.messenger.ImageReceiver r5 = r1.avatarImage
            org.telegram.ui.Components.AvatarDrawable r6 = r1.avatarDrawable
            org.telegram.tgnet.TLRPC$RecentMeUrl r7 = r1.recentMeUrl
            r5.setForUserOrChat(r4, r6, r7)
            goto L_0x0224
        L_0x006e:
            boolean r4 = r9 instanceof org.telegram.tgnet.TLRPC.TL_recentMeUrlUser
            if (r4 == 0) goto L_0x00eb
            int r4 = r1.currentAccount
            org.telegram.messenger.MessagesController r4 = org.telegram.messenger.MessagesController.getInstance(r4)
            org.telegram.tgnet.TLRPC$RecentMeUrl r5 = r1.recentMeUrl
            long r5 = r5.user_id
            java.lang.Long r5 = java.lang.Long.valueOf(r5)
            org.telegram.tgnet.TLRPC$User r4 = r4.getUser(r5)
            boolean r5 = org.telegram.messenger.LocaleController.isRTL
            if (r5 != 0) goto L_0x0092
            int r5 = org.telegram.messenger.AndroidUtilities.leftBaseline
            float r5 = (float) r5
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            r1.nameLeft = r5
            goto L_0x0098
        L_0x0092:
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r11)
            r1.nameLeft = r5
        L_0x0098:
            if (r4 == 0) goto L_0x00d7
            boolean r5 = r4.bot
            if (r5 == 0) goto L_0x00d3
            r5 = 1099169792(0x41840000, float:16.5)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            r1.nameLockTop = r5
            boolean r5 = org.telegram.messenger.LocaleController.isRTL
            if (r5 != 0) goto L_0x00bf
            int r5 = org.telegram.messenger.AndroidUtilities.leftBaseline
            float r5 = (float) r5
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            r1.nameLockLeft = r5
            int r5 = org.telegram.messenger.AndroidUtilities.leftBaseline
            int r5 = r5 + 4
            float r5 = (float) r5
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            r1.nameLeft = r5
            goto L_0x00d3
        L_0x00bf:
            int r5 = r23.getMeasuredWidth()
            int r6 = org.telegram.messenger.AndroidUtilities.leftBaseline
            float r6 = (float) r6
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            int r5 = r5 - r6
            r1.nameLockLeft = r5
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r11)
            r1.nameLeft = r5
        L_0x00d3:
            boolean r5 = r4.verified
            r1.drawVerified = r5
        L_0x00d7:
            java.lang.String r0 = org.telegram.messenger.UserObject.getUserName(r4)
            org.telegram.ui.Components.AvatarDrawable r5 = r1.avatarDrawable
            r5.setInfo((org.telegram.tgnet.TLRPC.User) r4)
            org.telegram.messenger.ImageReceiver r5 = r1.avatarImage
            org.telegram.ui.Components.AvatarDrawable r6 = r1.avatarDrawable
            org.telegram.tgnet.TLRPC$RecentMeUrl r7 = r1.recentMeUrl
            r5.setForUserOrChat(r4, r6, r7)
            goto L_0x0224
        L_0x00eb:
            boolean r4 = r9 instanceof org.telegram.tgnet.TLRPC.TL_recentMeUrlStickerSet
            r5 = 0
            r6 = 5
            if (r4 == 0) goto L_0x013a
            boolean r4 = org.telegram.messenger.LocaleController.isRTL
            if (r4 != 0) goto L_0x0100
            int r4 = org.telegram.messenger.AndroidUtilities.leftBaseline
            float r4 = (float) r4
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            r1.nameLeft = r4
            goto L_0x0106
        L_0x0100:
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r11)
            r1.nameLeft = r4
        L_0x0106:
            org.telegram.tgnet.TLRPC$RecentMeUrl r4 = r1.recentMeUrl
            org.telegram.tgnet.TLRPC$StickerSetCovered r4 = r4.set
            org.telegram.tgnet.TLRPC$StickerSet r4 = r4.set
            java.lang.String r0 = r4.title
            org.telegram.ui.Components.AvatarDrawable r4 = r1.avatarDrawable
            org.telegram.tgnet.TLRPC$RecentMeUrl r8 = r1.recentMeUrl
            org.telegram.tgnet.TLRPC$StickerSetCovered r8 = r8.set
            org.telegram.tgnet.TLRPC$StickerSet r8 = r8.set
            java.lang.String r8 = r8.title
            r4.setInfo(r6, r8, r5)
            org.telegram.messenger.ImageReceiver r14 = r1.avatarImage
            org.telegram.tgnet.TLRPC$RecentMeUrl r4 = r1.recentMeUrl
            org.telegram.tgnet.TLRPC$StickerSetCovered r4 = r4.set
            org.telegram.tgnet.TLRPC$Document r4 = r4.cover
            org.telegram.messenger.ImageLocation r15 = org.telegram.messenger.ImageLocation.getForDocument(r4)
            r16 = 0
            org.telegram.ui.Components.AvatarDrawable r4 = r1.avatarDrawable
            r18 = 0
            org.telegram.tgnet.TLRPC$RecentMeUrl r5 = r1.recentMeUrl
            r20 = 0
            r17 = r4
            r19 = r5
            r14.setImage(r15, r16, r17, r18, r19, r20)
            goto L_0x0224
        L_0x013a:
            boolean r4 = r9 instanceof org.telegram.tgnet.TLRPC.TL_recentMeUrlChatInvite
            if (r4 == 0) goto L_0x01f1
            boolean r4 = org.telegram.messenger.LocaleController.isRTL
            if (r4 != 0) goto L_0x014c
            int r4 = org.telegram.messenger.AndroidUtilities.leftBaseline
            float r4 = (float) r4
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            r1.nameLeft = r4
            goto L_0x0152
        L_0x014c:
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r11)
            r1.nameLeft = r4
        L_0x0152:
            org.telegram.tgnet.TLRPC$RecentMeUrl r4 = r1.recentMeUrl
            org.telegram.tgnet.TLRPC$ChatInvite r4 = r4.chat_invite
            org.telegram.tgnet.TLRPC$Chat r4 = r4.chat
            if (r4 == 0) goto L_0x0187
            org.telegram.ui.Components.AvatarDrawable r4 = r1.avatarDrawable
            org.telegram.tgnet.TLRPC$RecentMeUrl r5 = r1.recentMeUrl
            org.telegram.tgnet.TLRPC$ChatInvite r5 = r5.chat_invite
            org.telegram.tgnet.TLRPC$Chat r5 = r5.chat
            r4.setInfo((org.telegram.tgnet.TLRPC.Chat) r5)
            org.telegram.tgnet.TLRPC$RecentMeUrl r4 = r1.recentMeUrl
            org.telegram.tgnet.TLRPC$ChatInvite r4 = r4.chat_invite
            org.telegram.tgnet.TLRPC$Chat r4 = r4.chat
            java.lang.String r0 = r4.title
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
            goto L_0x01c3
        L_0x0187:
            org.telegram.tgnet.TLRPC$RecentMeUrl r4 = r1.recentMeUrl
            org.telegram.tgnet.TLRPC$ChatInvite r4 = r4.chat_invite
            java.lang.String r0 = r4.title
            org.telegram.ui.Components.AvatarDrawable r4 = r1.avatarDrawable
            org.telegram.tgnet.TLRPC$RecentMeUrl r8 = r1.recentMeUrl
            org.telegram.tgnet.TLRPC$ChatInvite r8 = r8.chat_invite
            java.lang.String r8 = r8.title
            r4.setInfo(r6, r8, r5)
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
        L_0x01c3:
            boolean r4 = org.telegram.messenger.LocaleController.isRTL
            if (r4 != 0) goto L_0x01dc
            int r4 = org.telegram.messenger.AndroidUtilities.leftBaseline
            float r4 = (float) r4
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            r1.nameLockLeft = r4
            int r4 = org.telegram.messenger.AndroidUtilities.leftBaseline
            int r4 = r4 + 4
            float r4 = (float) r4
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            r1.nameLeft = r4
            goto L_0x0224
        L_0x01dc:
            int r4 = r23.getMeasuredWidth()
            int r5 = org.telegram.messenger.AndroidUtilities.leftBaseline
            float r5 = (float) r5
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            int r4 = r4 - r5
            r1.nameLockLeft = r4
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r11)
            r1.nameLeft = r4
            goto L_0x0224
        L_0x01f1:
            boolean r4 = r9 instanceof org.telegram.tgnet.TLRPC.TL_recentMeUrlUnknown
            if (r4 == 0) goto L_0x0219
            boolean r4 = org.telegram.messenger.LocaleController.isRTL
            if (r4 != 0) goto L_0x0203
            int r4 = org.telegram.messenger.AndroidUtilities.leftBaseline
            float r4 = (float) r4
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            r1.nameLeft = r4
            goto L_0x0209
        L_0x0203:
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r11)
            r1.nameLeft = r4
        L_0x0209:
            java.lang.String r0 = "Url"
            org.telegram.messenger.ImageReceiver r4 = r1.avatarImage
            r5 = 0
            r6 = 0
            org.telegram.ui.Components.AvatarDrawable r7 = r1.avatarDrawable
            r8 = 0
            org.telegram.tgnet.TLRPC$RecentMeUrl r9 = r1.recentMeUrl
            r10 = 0
            r4.setImage(r5, r6, r7, r8, r9, r10)
            goto L_0x0224
        L_0x0219:
            org.telegram.messenger.ImageReceiver r4 = r1.avatarImage
            r5 = 0
            r6 = 0
            org.telegram.ui.Components.AvatarDrawable r7 = r1.avatarDrawable
            r8 = 0
            r10 = 0
            r4.setImage(r5, r6, r7, r8, r9, r10)
        L_0x0224:
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
            if (r4 == 0) goto L_0x0255
            r4 = 2131626131(0x7f0e0893, float:1.887949E38)
            java.lang.String r5 = "HiddenName"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r5, r4)
            r15 = r0
            goto L_0x0256
        L_0x0255:
            r15 = r0
        L_0x0256:
            boolean r0 = org.telegram.messenger.LocaleController.isRTL
            if (r0 != 0) goto L_0x0267
            int r0 = r23.getMeasuredWidth()
            int r4 = r1.nameLeft
            int r0 = r0 - r4
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r11)
            int r0 = r0 - r4
            goto L_0x0276
        L_0x0267:
            int r0 = r23.getMeasuredWidth()
            int r4 = r1.nameLeft
            int r0 = r0 - r4
            int r4 = org.telegram.messenger.AndroidUtilities.leftBaseline
            float r4 = (float) r4
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            int r0 = r0 - r4
        L_0x0276:
            boolean r4 = r1.drawNameLock
            if (r4 == 0) goto L_0x0288
            r4 = 1082130432(0x40800000, float:4.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            android.graphics.drawable.Drawable r5 = org.telegram.ui.ActionBar.Theme.dialogs_lockDrawable
            int r5 = r5.getIntrinsicWidth()
            int r4 = r4 + r5
            int r0 = r0 - r4
        L_0x0288:
            boolean r4 = r1.drawVerified
            r16 = 1086324736(0x40CLASSNAME, float:6.0)
            if (r4 == 0) goto L_0x02a3
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r16)
            android.graphics.drawable.Drawable r5 = org.telegram.ui.ActionBar.Theme.dialogs_verifiedDrawable
            int r5 = r5.getIntrinsicWidth()
            int r4 = r4 + r5
            int r0 = r0 - r4
            boolean r5 = org.telegram.messenger.LocaleController.isRTL
            if (r5 == 0) goto L_0x02a3
            int r5 = r1.nameLeft
            int r5 = r5 + r4
            r1.nameLeft = r5
        L_0x02a3:
            r12 = 1094713344(0x41400000, float:12.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r12)
            int r11 = java.lang.Math.max(r4, r0)
            r0 = 10
            r4 = 32
            java.lang.String r0 = r15.replace(r0, r4)     // Catch:{ Exception -> 0x02da }
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r12)     // Catch:{ Exception -> 0x02da }
            int r4 = r11 - r4
            float r4 = (float) r4     // Catch:{ Exception -> 0x02da }
            android.text.TextUtils$TruncateAt r5 = android.text.TextUtils.TruncateAt.END     // Catch:{ Exception -> 0x02da }
            java.lang.CharSequence r5 = android.text.TextUtils.ellipsize(r0, r2, r4, r5)     // Catch:{ Exception -> 0x02da }
            android.text.StaticLayout r0 = new android.text.StaticLayout     // Catch:{ Exception -> 0x02da }
            android.text.Layout$Alignment r8 = android.text.Layout.Alignment.ALIGN_NORMAL     // Catch:{ Exception -> 0x02da }
            r9 = 1065353216(0x3var_, float:1.0)
            r10 = 0
            r17 = 0
            r4 = r0
            r6 = r2
            r7 = r11
            r21 = r11
            r11 = r17
            r4.<init>(r5, r6, r7, r8, r9, r10, r11)     // Catch:{ Exception -> 0x02d8 }
            r1.nameLayout = r0     // Catch:{ Exception -> 0x02d8 }
            goto L_0x02e0
        L_0x02d8:
            r0 = move-exception
            goto L_0x02dd
        L_0x02da:
            r0 = move-exception
            r21 = r11
        L_0x02dd:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x02e0:
            int r0 = r23.getMeasuredWidth()
            int r4 = org.telegram.messenger.AndroidUtilities.leftBaseline
            int r4 = r4 + 16
            float r4 = (float) r4
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            int r0 = r0 - r4
            boolean r4 = org.telegram.messenger.LocaleController.isRTL
            if (r4 != 0) goto L_0x030b
            int r4 = org.telegram.messenger.AndroidUtilities.leftBaseline
            float r4 = (float) r4
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            r1.messageLeft = r4
            boolean r4 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r4 == 0) goto L_0x0304
            r4 = 1095761920(0x41500000, float:13.0)
            goto L_0x0306
        L_0x0304:
            r4 = 1091567616(0x41100000, float:9.0)
        L_0x0306:
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            goto L_0x0327
        L_0x030b:
            r4 = 1098907648(0x41800000, float:16.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            r1.messageLeft = r4
            int r4 = r23.getMeasuredWidth()
            boolean r5 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r5 == 0) goto L_0x0320
            r5 = 1115815936(0x42820000, float:65.0)
            goto L_0x0322
        L_0x0320:
            r5 = 1114898432(0x42740000, float:61.0)
        L_0x0322:
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            int r4 = r4 - r5
        L_0x0327:
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
            android.text.StaticLayout r0 = new android.text.StaticLayout     // Catch:{ Exception -> 0x036c }
            android.text.Layout$Alignment r9 = android.text.Layout.Alignment.ALIGN_NORMAL     // Catch:{ Exception -> 0x036c }
            r10 = 1065353216(0x3var_, float:1.0)
            r12 = 0
            r18 = 0
            r5 = r0
            r6 = r17
            r7 = r13
            r8 = r11
            r22 = r11
            r11 = r12
            r12 = r18
            r5.<init>(r6, r7, r8, r9, r10, r11, r12)     // Catch:{ Exception -> 0x036a }
            r1.messageLayout = r0     // Catch:{ Exception -> 0x036a }
            goto L_0x0372
        L_0x036a:
            r0 = move-exception
            goto L_0x036f
        L_0x036c:
            r0 = move-exception
            r22 = r11
        L_0x036f:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x0372:
            boolean r0 = org.telegram.messenger.LocaleController.isRTL
            if (r0 == 0) goto L_0x0418
            android.text.StaticLayout r0 = r1.nameLayout
            r5 = 0
            if (r0 == 0) goto L_0x03d7
            int r0 = r0.getLineCount()
            if (r0 <= 0) goto L_0x03d7
            android.text.StaticLayout r0 = r1.nameLayout
            float r0 = r0.getLineLeft(r3)
            android.text.StaticLayout r6 = r1.nameLayout
            float r6 = r6.getLineWidth(r3)
            double r6 = (double) r6
            double r6 = java.lang.Math.ceil(r6)
            boolean r8 = r1.drawVerified
            if (r8 == 0) goto L_0x03bc
            int r8 = r1.nameLeft
            double r8 = (double) r8
            r10 = r21
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
            goto L_0x03be
        L_0x03bc:
            r10 = r21
        L_0x03be:
            int r8 = (r0 > r5 ? 1 : (r0 == r5 ? 0 : -1))
            if (r8 != 0) goto L_0x03d9
            double r8 = (double) r10
            int r11 = (r6 > r8 ? 1 : (r6 == r8 ? 0 : -1))
            if (r11 >= 0) goto L_0x03d9
            int r8 = r1.nameLeft
            double r8 = (double) r8
            double r11 = (double) r10
            java.lang.Double.isNaN(r11)
            double r11 = r11 - r6
            java.lang.Double.isNaN(r8)
            double r8 = r8 + r11
            int r8 = (int) r8
            r1.nameLeft = r8
            goto L_0x03d9
        L_0x03d7:
            r10 = r21
        L_0x03d9:
            android.text.StaticLayout r0 = r1.messageLayout
            if (r0 == 0) goto L_0x0414
            int r0 = r0.getLineCount()
            if (r0 <= 0) goto L_0x0414
            android.text.StaticLayout r0 = r1.messageLayout
            float r0 = r0.getLineLeft(r3)
            int r5 = (r0 > r5 ? 1 : (r0 == r5 ? 0 : -1))
            if (r5 != 0) goto L_0x0410
            android.text.StaticLayout r5 = r1.messageLayout
            float r3 = r5.getLineWidth(r3)
            double r5 = (double) r3
            double r5 = java.lang.Math.ceil(r5)
            r7 = r22
            double r8 = (double) r7
            int r3 = (r5 > r8 ? 1 : (r5 == r8 ? 0 : -1))
            if (r3 >= 0) goto L_0x0495
            int r3 = r1.messageLeft
            double r8 = (double) r3
            double r11 = (double) r7
            java.lang.Double.isNaN(r11)
            double r11 = r11 - r5
            java.lang.Double.isNaN(r8)
            double r8 = r8 + r11
            int r3 = (int) r8
            r1.messageLeft = r3
            goto L_0x0495
        L_0x0410:
            r7 = r22
            goto L_0x0495
        L_0x0414:
            r7 = r22
            goto L_0x0495
        L_0x0418:
            r10 = r21
            r7 = r22
            android.text.StaticLayout r0 = r1.nameLayout
            if (r0 == 0) goto L_0x0461
            int r0 = r0.getLineCount()
            if (r0 <= 0) goto L_0x0461
            android.text.StaticLayout r0 = r1.nameLayout
            float r0 = r0.getLineRight(r3)
            float r5 = (float) r10
            int r5 = (r0 > r5 ? 1 : (r0 == r5 ? 0 : -1))
            if (r5 != 0) goto L_0x0450
            android.text.StaticLayout r5 = r1.nameLayout
            float r5 = r5.getLineWidth(r3)
            double r5 = (double) r5
            double r5 = java.lang.Math.ceil(r5)
            double r8 = (double) r10
            int r11 = (r5 > r8 ? 1 : (r5 == r8 ? 0 : -1))
            if (r11 >= 0) goto L_0x0450
            int r8 = r1.nameLeft
            double r8 = (double) r8
            double r11 = (double) r10
            java.lang.Double.isNaN(r11)
            double r11 = r11 - r5
            java.lang.Double.isNaN(r8)
            double r8 = r8 - r11
            int r8 = (int) r8
            r1.nameLeft = r8
        L_0x0450:
            boolean r5 = r1.drawVerified
            if (r5 == 0) goto L_0x0461
            int r5 = r1.nameLeft
            float r5 = (float) r5
            float r5 = r5 + r0
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r16)
            float r6 = (float) r6
            float r5 = r5 + r6
            int r5 = (int) r5
            r1.nameMuteLeft = r5
        L_0x0461:
            android.text.StaticLayout r0 = r1.messageLayout
            if (r0 == 0) goto L_0x0495
            int r0 = r0.getLineCount()
            if (r0 <= 0) goto L_0x0495
            android.text.StaticLayout r0 = r1.messageLayout
            float r0 = r0.getLineRight(r3)
            float r5 = (float) r7
            int r5 = (r0 > r5 ? 1 : (r0 == r5 ? 0 : -1))
            if (r5 != 0) goto L_0x0495
            android.text.StaticLayout r5 = r1.messageLayout
            float r3 = r5.getLineWidth(r3)
            double r5 = (double) r3
            double r5 = java.lang.Math.ceil(r5)
            double r8 = (double) r7
            int r3 = (r5 > r8 ? 1 : (r5 == r8 ? 0 : -1))
            if (r3 >= 0) goto L_0x0495
            int r3 = r1.messageLeft
            double r8 = (double) r3
            double r11 = (double) r7
            java.lang.Double.isNaN(r11)
            double r11 = r11 - r5
            java.lang.Double.isNaN(r8)
            double r8 = r8 - r11
            int r3 = (int) r8
            r1.messageLeft = r3
        L_0x0495:
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
