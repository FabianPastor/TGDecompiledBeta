package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.text.StaticLayout;
import android.view.View.MeasureSpec;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.TLRPC.RecentMeUrl;
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
    private RecentMeUrl recentMeUrl;
    public boolean useSeparator;

    public boolean hasOverlappingRendering() {
        return false;
    }

    public DialogMeUrlCell(Context context) {
        super(context);
        Theme.createDialogsResources(context);
        this.avatarImage.setRoundRadius(AndroidUtilities.dp(26.0f));
    }

    public void setRecentMeUrl(RecentMeUrl recentMeUrl) {
        this.recentMeUrl = recentMeUrl;
        requestLayout();
    }

    /* Access modifiers changed, original: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.avatarImage.onDetachedFromWindow();
    }

    /* Access modifiers changed, original: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.avatarImage.onAttachedToWindow();
    }

    /* Access modifiers changed, original: protected */
    public void onMeasure(int i, int i2) {
        setMeasuredDimension(MeasureSpec.getSize(i), AndroidUtilities.dp(72.0f) + this.useSeparator);
    }

    /* Access modifiers changed, original: protected */
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        if (z) {
            buildLayout();
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:111:0x0376  */
    /* JADX WARNING: Removed duplicated region for block: B:126:0x03e9  */
    /* JADX WARNING: Removed duplicated region for block: B:121:0x03d0  */
    /* JADX WARNING: Removed duplicated region for block: B:159:0x04d3  */
    /* JADX WARNING: Removed duplicated region for block: B:138:0x0445  */
    public void buildLayout() {
        /*
        r19 = this;
        r1 = r19;
        r0 = org.telegram.ui.ActionBar.Theme.dialogs_namePaint;
        r2 = 0;
        r5 = r0[r2];
        r0 = org.telegram.ui.ActionBar.Theme.dialogs_messagePaint;
        r11 = r0[r2];
        r1.drawNameGroup = r2;
        r1.drawNameBroadcast = r2;
        r1.drawNameLock = r2;
        r1.drawNameBot = r2;
        r1.drawVerified = r2;
        r0 = r1.recentMeUrl;
        r3 = r0 instanceof org.telegram.tgnet.TLRPC.TL_recentMeUrlChat;
        r4 = NUM; // 0x418CLASSNAME float:17.5 double:5.43321066E-315;
        r6 = NUM; // 0x41840000 float:16.5 double:5.43062033E-315;
        r7 = 1;
        r8 = NUM; // 0x41600000 float:14.0 double:5.41896386E-315;
        if (r3 == 0) goto L_0x00bf;
    L_0x0022:
        r0 = r1.currentAccount;
        r0 = org.telegram.messenger.MessagesController.getInstance(r0);
        r3 = r1.recentMeUrl;
        r3 = r3.chat_id;
        r3 = java.lang.Integer.valueOf(r3);
        r0 = r0.getChat(r3);
        r3 = r0.id;
        if (r3 < 0) goto L_0x004c;
    L_0x0038:
        r3 = org.telegram.messenger.ChatObject.isChannel(r0);
        if (r3 == 0) goto L_0x0043;
    L_0x003e:
        r3 = r0.megagroup;
        if (r3 != 0) goto L_0x0043;
    L_0x0042:
        goto L_0x004c;
    L_0x0043:
        r1.drawNameGroup = r7;
        r3 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r1.nameLockTop = r3;
        goto L_0x0054;
    L_0x004c:
        r1.drawNameBroadcast = r7;
        r3 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r1.nameLockTop = r3;
    L_0x0054:
        r3 = r0.verified;
        r1.drawVerified = r3;
        r3 = org.telegram.messenger.LocaleController.isRTL;
        if (r3 != 0) goto L_0x007f;
    L_0x005c:
        r3 = org.telegram.messenger.AndroidUtilities.leftBaseline;
        r3 = (float) r3;
        r3 = org.telegram.messenger.AndroidUtilities.dp(r3);
        r1.nameLockLeft = r3;
        r3 = org.telegram.messenger.AndroidUtilities.leftBaseline;
        r3 = r3 + 4;
        r3 = (float) r3;
        r3 = org.telegram.messenger.AndroidUtilities.dp(r3);
        r4 = r1.drawNameGroup;
        if (r4 == 0) goto L_0x0075;
    L_0x0072:
        r4 = org.telegram.ui.ActionBar.Theme.dialogs_groupDrawable;
        goto L_0x0077;
    L_0x0075:
        r4 = org.telegram.ui.ActionBar.Theme.dialogs_broadcastDrawable;
    L_0x0077:
        r4 = r4.getIntrinsicWidth();
        r3 = r3 + r4;
        r1.nameLeft = r3;
        goto L_0x00a1;
    L_0x007f:
        r3 = r19.getMeasuredWidth();
        r4 = org.telegram.messenger.AndroidUtilities.leftBaseline;
        r4 = (float) r4;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r3 = r3 - r4;
        r4 = r1.drawNameGroup;
        if (r4 == 0) goto L_0x0092;
    L_0x008f:
        r4 = org.telegram.ui.ActionBar.Theme.dialogs_groupDrawable;
        goto L_0x0094;
    L_0x0092:
        r4 = org.telegram.ui.ActionBar.Theme.dialogs_broadcastDrawable;
    L_0x0094:
        r4 = r4.getIntrinsicWidth();
        r3 = r3 - r4;
        r1.nameLockLeft = r3;
        r3 = org.telegram.messenger.AndroidUtilities.dp(r8);
        r1.nameLeft = r3;
    L_0x00a1:
        r3 = r0.title;
        r4 = r1.avatarDrawable;
        r4.setInfo(r0);
        r12 = r1.avatarImage;
        r13 = org.telegram.messenger.ImageLocation.getForChat(r0, r2);
        r15 = r1.avatarDrawable;
        r16 = 0;
        r0 = r1.recentMeUrl;
        r18 = 0;
        r14 = "50_50";
        r17 = r0;
        r12.setImage(r13, r14, r15, r16, r17, r18);
        goto L_0x02e2;
    L_0x00bf:
        r3 = r0 instanceof org.telegram.tgnet.TLRPC.TL_recentMeUrlUser;
        if (r3 == 0) goto L_0x0156;
    L_0x00c3:
        r0 = r1.currentAccount;
        r0 = org.telegram.messenger.MessagesController.getInstance(r0);
        r3 = r1.recentMeUrl;
        r3 = r3.user_id;
        r3 = java.lang.Integer.valueOf(r3);
        r0 = r0.getUser(r3);
        r3 = org.telegram.messenger.LocaleController.isRTL;
        if (r3 != 0) goto L_0x00e3;
    L_0x00d9:
        r3 = org.telegram.messenger.AndroidUtilities.leftBaseline;
        r3 = (float) r3;
        r3 = org.telegram.messenger.AndroidUtilities.dp(r3);
        r1.nameLeft = r3;
        goto L_0x00e9;
    L_0x00e3:
        r3 = org.telegram.messenger.AndroidUtilities.dp(r8);
        r1.nameLeft = r3;
    L_0x00e9:
        if (r0 == 0) goto L_0x0136;
    L_0x00eb:
        r3 = r0.bot;
        if (r3 == 0) goto L_0x0132;
    L_0x00ef:
        r1.drawNameBot = r7;
        r3 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r1.nameLockTop = r3;
        r3 = org.telegram.messenger.LocaleController.isRTL;
        if (r3 != 0) goto L_0x0117;
    L_0x00fb:
        r3 = org.telegram.messenger.AndroidUtilities.leftBaseline;
        r3 = (float) r3;
        r3 = org.telegram.messenger.AndroidUtilities.dp(r3);
        r1.nameLockLeft = r3;
        r3 = org.telegram.messenger.AndroidUtilities.leftBaseline;
        r3 = r3 + 4;
        r3 = (float) r3;
        r3 = org.telegram.messenger.AndroidUtilities.dp(r3);
        r4 = org.telegram.ui.ActionBar.Theme.dialogs_botDrawable;
        r4 = r4.getIntrinsicWidth();
        r3 = r3 + r4;
        r1.nameLeft = r3;
        goto L_0x0132;
    L_0x0117:
        r3 = r19.getMeasuredWidth();
        r4 = org.telegram.messenger.AndroidUtilities.leftBaseline;
        r4 = (float) r4;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r3 = r3 - r4;
        r4 = org.telegram.ui.ActionBar.Theme.dialogs_botDrawable;
        r4 = r4.getIntrinsicWidth();
        r3 = r3 - r4;
        r1.nameLockLeft = r3;
        r3 = org.telegram.messenger.AndroidUtilities.dp(r8);
        r1.nameLeft = r3;
    L_0x0132:
        r3 = r0.verified;
        r1.drawVerified = r3;
    L_0x0136:
        r3 = org.telegram.messenger.UserObject.getUserName(r0);
        r4 = r1.avatarDrawable;
        r4.setInfo(r0);
        r12 = r1.avatarImage;
        r13 = org.telegram.messenger.ImageLocation.getForUser(r0, r2);
        r15 = r1.avatarDrawable;
        r16 = 0;
        r0 = r1.recentMeUrl;
        r18 = 0;
        r14 = "50_50";
        r17 = r0;
        r12.setImage(r13, r14, r15, r16, r17, r18);
        goto L_0x02e2;
    L_0x0156:
        r3 = r0 instanceof org.telegram.tgnet.TLRPC.TL_recentMeUrlStickerSet;
        r9 = 0;
        r10 = 5;
        if (r3 == 0) goto L_0x0199;
    L_0x015c:
        r0 = org.telegram.messenger.LocaleController.isRTL;
        if (r0 != 0) goto L_0x016a;
    L_0x0160:
        r0 = org.telegram.messenger.AndroidUtilities.leftBaseline;
        r0 = (float) r0;
        r0 = org.telegram.messenger.AndroidUtilities.dp(r0);
        r1.nameLeft = r0;
        goto L_0x0170;
    L_0x016a:
        r0 = org.telegram.messenger.AndroidUtilities.dp(r8);
        r1.nameLeft = r0;
    L_0x0170:
        r0 = r1.recentMeUrl;
        r0 = r0.set;
        r0 = r0.set;
        r3 = r0.title;
        r0 = r1.avatarDrawable;
        r0.setInfo(r10, r3, r9);
        r12 = r1.avatarImage;
        r0 = r1.recentMeUrl;
        r0 = r0.set;
        r0 = r0.cover;
        r13 = org.telegram.messenger.ImageLocation.getForDocument(r0);
        r14 = 0;
        r15 = r1.avatarDrawable;
        r16 = 0;
        r0 = r1.recentMeUrl;
        r18 = 0;
        r17 = r0;
        r12.setImage(r13, r14, r15, r16, r17, r18);
        goto L_0x02e2;
    L_0x0199:
        r3 = r0 instanceof org.telegram.tgnet.TLRPC.TL_recentMeUrlChatInvite;
        if (r3 == 0) goto L_0x02a5;
    L_0x019d:
        r0 = org.telegram.messenger.LocaleController.isRTL;
        if (r0 != 0) goto L_0x01ab;
    L_0x01a1:
        r0 = org.telegram.messenger.AndroidUtilities.leftBaseline;
        r0 = (float) r0;
        r0 = org.telegram.messenger.AndroidUtilities.dp(r0);
        r1.nameLeft = r0;
        goto L_0x01b1;
    L_0x01ab:
        r0 = org.telegram.messenger.AndroidUtilities.dp(r8);
        r1.nameLeft = r0;
    L_0x01b1:
        r0 = r1.recentMeUrl;
        r0 = r0.chat_invite;
        r3 = r0.chat;
        if (r3 == 0) goto L_0x020c;
    L_0x01b9:
        r0 = r1.avatarDrawable;
        r0.setInfo(r3);
        r0 = r1.recentMeUrl;
        r0 = r0.chat_invite;
        r0 = r0.chat;
        r3 = r0.title;
        r9 = r0.id;
        if (r9 < 0) goto L_0x01e4;
    L_0x01ca:
        r0 = org.telegram.messenger.ChatObject.isChannel(r0);
        if (r0 == 0) goto L_0x01db;
    L_0x01d0:
        r0 = r1.recentMeUrl;
        r0 = r0.chat_invite;
        r0 = r0.chat;
        r0 = r0.megagroup;
        if (r0 != 0) goto L_0x01db;
    L_0x01da:
        goto L_0x01e4;
    L_0x01db:
        r1.drawNameGroup = r7;
        r0 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r1.nameLockTop = r0;
        goto L_0x01ec;
    L_0x01e4:
        r1.drawNameBroadcast = r7;
        r0 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r1.nameLockTop = r0;
    L_0x01ec:
        r0 = r1.recentMeUrl;
        r0 = r0.chat_invite;
        r0 = r0.chat;
        r4 = r0.verified;
        r1.drawVerified = r4;
        r12 = r1.avatarImage;
        r13 = org.telegram.messenger.ImageLocation.getForChat(r0, r2);
        r15 = r1.avatarDrawable;
        r16 = 0;
        r0 = r1.recentMeUrl;
        r18 = 0;
        r14 = "50_50";
        r17 = r0;
        r12.setImage(r13, r14, r15, r16, r17, r18);
        goto L_0x025b;
    L_0x020c:
        r0 = r0.title;
        r3 = r1.avatarDrawable;
        r3.setInfo(r10, r0, r9);
        r3 = r1.recentMeUrl;
        r3 = r3.chat_invite;
        r9 = r3.broadcast;
        if (r9 != 0) goto L_0x0229;
    L_0x021b:
        r3 = r3.channel;
        if (r3 == 0) goto L_0x0220;
    L_0x021f:
        goto L_0x0229;
    L_0x0220:
        r1.drawNameGroup = r7;
        r3 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r1.nameLockTop = r3;
        goto L_0x0231;
    L_0x0229:
        r1.drawNameBroadcast = r7;
        r3 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r1.nameLockTop = r3;
    L_0x0231:
        r3 = r1.recentMeUrl;
        r3 = r3.chat_invite;
        r3 = r3.photo;
        r3 = r3.sizes;
        r4 = 50;
        r3 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r3, r4);
        r12 = r1.avatarImage;
        r4 = r1.recentMeUrl;
        r4 = r4.chat_invite;
        r4 = r4.photo;
        r13 = org.telegram.messenger.ImageLocation.getForPhoto(r3, r4);
        r15 = r1.avatarDrawable;
        r16 = 0;
        r3 = r1.recentMeUrl;
        r18 = 0;
        r14 = "50_50";
        r17 = r3;
        r12.setImage(r13, r14, r15, r16, r17, r18);
        r3 = r0;
    L_0x025b:
        r0 = org.telegram.messenger.LocaleController.isRTL;
        if (r0 != 0) goto L_0x0282;
    L_0x025f:
        r0 = org.telegram.messenger.AndroidUtilities.leftBaseline;
        r0 = (float) r0;
        r0 = org.telegram.messenger.AndroidUtilities.dp(r0);
        r1.nameLockLeft = r0;
        r0 = org.telegram.messenger.AndroidUtilities.leftBaseline;
        r0 = r0 + 4;
        r0 = (float) r0;
        r0 = org.telegram.messenger.AndroidUtilities.dp(r0);
        r4 = r1.drawNameGroup;
        if (r4 == 0) goto L_0x0278;
    L_0x0275:
        r4 = org.telegram.ui.ActionBar.Theme.dialogs_groupDrawable;
        goto L_0x027a;
    L_0x0278:
        r4 = org.telegram.ui.ActionBar.Theme.dialogs_broadcastDrawable;
    L_0x027a:
        r4 = r4.getIntrinsicWidth();
        r0 = r0 + r4;
        r1.nameLeft = r0;
        goto L_0x02e2;
    L_0x0282:
        r0 = r19.getMeasuredWidth();
        r4 = org.telegram.messenger.AndroidUtilities.leftBaseline;
        r4 = (float) r4;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r0 = r0 - r4;
        r4 = r1.drawNameGroup;
        if (r4 == 0) goto L_0x0295;
    L_0x0292:
        r4 = org.telegram.ui.ActionBar.Theme.dialogs_groupDrawable;
        goto L_0x0297;
    L_0x0295:
        r4 = org.telegram.ui.ActionBar.Theme.dialogs_broadcastDrawable;
    L_0x0297:
        r4 = r4.getIntrinsicWidth();
        r0 = r0 - r4;
        r1.nameLockLeft = r0;
        r0 = org.telegram.messenger.AndroidUtilities.dp(r8);
        r1.nameLeft = r0;
        goto L_0x02e2;
    L_0x02a5:
        r3 = r0 instanceof org.telegram.tgnet.TLRPC.TL_recentMeUrlUnknown;
        if (r3 == 0) goto L_0x02d1;
    L_0x02a9:
        r0 = org.telegram.messenger.LocaleController.isRTL;
        if (r0 != 0) goto L_0x02b7;
    L_0x02ad:
        r0 = org.telegram.messenger.AndroidUtilities.leftBaseline;
        r0 = (float) r0;
        r0 = org.telegram.messenger.AndroidUtilities.dp(r0);
        r1.nameLeft = r0;
        goto L_0x02bd;
    L_0x02b7:
        r0 = org.telegram.messenger.AndroidUtilities.dp(r8);
        r1.nameLeft = r0;
    L_0x02bd:
        r12 = r1.avatarImage;
        r13 = 0;
        r14 = 0;
        r15 = r1.avatarDrawable;
        r16 = 0;
        r0 = r1.recentMeUrl;
        r18 = 0;
        r17 = r0;
        r12.setImage(r13, r14, r15, r16, r17, r18);
        r3 = "Url";
        goto L_0x02e2;
    L_0x02d1:
        r12 = r1.avatarImage;
        r13 = 0;
        r14 = 0;
        r15 = r1.avatarDrawable;
        r16 = 0;
        r18 = 0;
        r17 = r0;
        r12.setImage(r13, r14, r15, r16, r17, r18);
        r3 = "";
    L_0x02e2:
        r0 = new java.lang.StringBuilder;
        r0.<init>();
        r4 = r1.currentAccount;
        r4 = org.telegram.messenger.MessagesController.getInstance(r4);
        r4 = r4.linkPrefix;
        r0.append(r4);
        r4 = "/";
        r0.append(r4);
        r4 = r1.recentMeUrl;
        r4 = r4.url;
        r0.append(r4);
        r12 = r0.toString();
        r0 = android.text.TextUtils.isEmpty(r3);
        if (r0 == 0) goto L_0x0311;
    L_0x0308:
        r0 = NUM; // 0x7f0e055c float:1.887782E38 double:1.0531628345E-314;
        r3 = "HiddenName";
        r3 = org.telegram.messenger.LocaleController.getString(r3, r0);
    L_0x0311:
        r0 = org.telegram.messenger.LocaleController.isRTL;
        if (r0 != 0) goto L_0x0321;
    L_0x0315:
        r0 = r19.getMeasuredWidth();
        r4 = r1.nameLeft;
        r0 = r0 - r4;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r8);
        goto L_0x032f;
    L_0x0321:
        r0 = r19.getMeasuredWidth();
        r4 = r1.nameLeft;
        r0 = r0 - r4;
        r4 = org.telegram.messenger.AndroidUtilities.leftBaseline;
        r4 = (float) r4;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
    L_0x032f:
        r0 = r0 - r4;
        r4 = r1.drawNameLock;
        r6 = NUM; // 0x40800000 float:4.0 double:5.34643471E-315;
        if (r4 == 0) goto L_0x0343;
    L_0x0336:
        r4 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r6 = org.telegram.ui.ActionBar.Theme.dialogs_lockDrawable;
        r6 = r6.getIntrinsicWidth();
    L_0x0340:
        r4 = r4 + r6;
        r0 = r0 - r4;
        goto L_0x0370;
    L_0x0343:
        r4 = r1.drawNameGroup;
        if (r4 == 0) goto L_0x0352;
    L_0x0347:
        r4 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r6 = org.telegram.ui.ActionBar.Theme.dialogs_groupDrawable;
        r6 = r6.getIntrinsicWidth();
        goto L_0x0340;
    L_0x0352:
        r4 = r1.drawNameBroadcast;
        if (r4 == 0) goto L_0x0361;
    L_0x0356:
        r4 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r6 = org.telegram.ui.ActionBar.Theme.dialogs_broadcastDrawable;
        r6 = r6.getIntrinsicWidth();
        goto L_0x0340;
    L_0x0361:
        r4 = r1.drawNameBot;
        if (r4 == 0) goto L_0x0370;
    L_0x0365:
        r4 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r6 = org.telegram.ui.ActionBar.Theme.dialogs_botDrawable;
        r6 = r6.getIntrinsicWidth();
        goto L_0x0340;
    L_0x0370:
        r4 = r1.drawVerified;
        r14 = NUM; // 0x40CLASSNAME float:6.0 double:5.367157323E-315;
        if (r4 == 0) goto L_0x038b;
    L_0x0376:
        r4 = org.telegram.messenger.AndroidUtilities.dp(r14);
        r6 = org.telegram.ui.ActionBar.Theme.dialogs_verifiedDrawable;
        r6 = r6.getIntrinsicWidth();
        r4 = r4 + r6;
        r0 = r0 - r4;
        r6 = org.telegram.messenger.LocaleController.isRTL;
        if (r6 == 0) goto L_0x038b;
    L_0x0386:
        r6 = r1.nameLeft;
        r6 = r6 + r4;
        r1.nameLeft = r6;
    L_0x038b:
        r13 = NUM; // 0x41400000 float:12.0 double:5.408602553E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r13);
        r15 = java.lang.Math.max(r4, r0);
        r0 = 10;
        r4 = 32;
        r0 = r3.replace(r0, r4);	 Catch:{ Exception -> 0x03ba }
        r3 = org.telegram.messenger.AndroidUtilities.dp(r13);	 Catch:{ Exception -> 0x03ba }
        r3 = r15 - r3;
        r3 = (float) r3;	 Catch:{ Exception -> 0x03ba }
        r4 = android.text.TextUtils.TruncateAt.END;	 Catch:{ Exception -> 0x03ba }
        r4 = android.text.TextUtils.ellipsize(r0, r5, r3, r4);	 Catch:{ Exception -> 0x03ba }
        r0 = new android.text.StaticLayout;	 Catch:{ Exception -> 0x03ba }
        r7 = android.text.Layout.Alignment.ALIGN_NORMAL;	 Catch:{ Exception -> 0x03ba }
        r8 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r9 = 0;
        r10 = 0;
        r3 = r0;
        r6 = r15;
        r3.<init>(r4, r5, r6, r7, r8, r9, r10);	 Catch:{ Exception -> 0x03ba }
        r1.nameLayout = r0;	 Catch:{ Exception -> 0x03ba }
        goto L_0x03be;
    L_0x03ba:
        r0 = move-exception;
        org.telegram.messenger.FileLog.e(r0);
    L_0x03be:
        r0 = r19.getMeasuredWidth();
        r3 = org.telegram.messenger.AndroidUtilities.leftBaseline;
        r3 = r3 + 16;
        r3 = (float) r3;
        r3 = org.telegram.messenger.AndroidUtilities.dp(r3);
        r0 = r0 - r3;
        r3 = org.telegram.messenger.LocaleController.isRTL;
        if (r3 != 0) goto L_0x03e9;
    L_0x03d0:
        r3 = org.telegram.messenger.AndroidUtilities.leftBaseline;
        r3 = (float) r3;
        r3 = org.telegram.messenger.AndroidUtilities.dp(r3);
        r1.messageLeft = r3;
        r3 = org.telegram.messenger.AndroidUtilities.isTablet();
        if (r3 == 0) goto L_0x03e2;
    L_0x03df:
        r3 = NUM; // 0x41500000 float:13.0 double:5.413783207E-315;
        goto L_0x03e4;
    L_0x03e2:
        r3 = NUM; // 0x41100000 float:9.0 double:5.39306059E-315;
    L_0x03e4:
        r3 = org.telegram.messenger.AndroidUtilities.dp(r3);
        goto L_0x0405;
    L_0x03e9:
        r3 = NUM; // 0x41800000 float:16.0 double:5.42932517E-315;
        r3 = org.telegram.messenger.AndroidUtilities.dp(r3);
        r1.messageLeft = r3;
        r3 = r19.getMeasuredWidth();
        r4 = org.telegram.messenger.AndroidUtilities.isTablet();
        if (r4 == 0) goto L_0x03fe;
    L_0x03fb:
        r4 = NUM; // 0x42820000 float:65.0 double:5.51286321E-315;
        goto L_0x0400;
    L_0x03fe:
        r4 = NUM; // 0x42740000 float:61.0 double:5.50833014E-315;
    L_0x0400:
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r3 = r3 - r4;
    L_0x0405:
        r4 = r1.avatarImage;
        r5 = r1.avatarTop;
        r6 = NUM; // 0x42500000 float:52.0 double:5.496673668E-315;
        r7 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4.setImageCoords(r3, r5, r7, r6);
        r3 = org.telegram.messenger.AndroidUtilities.dp(r13);
        r3 = java.lang.Math.max(r3, r0);
        r0 = org.telegram.messenger.AndroidUtilities.dp(r13);
        r0 = r3 - r0;
        r0 = (float) r0;
        r4 = android.text.TextUtils.TruncateAt.END;
        r7 = android.text.TextUtils.ellipsize(r12, r11, r0, r4);
        r0 = new android.text.StaticLayout;	 Catch:{ Exception -> 0x043d }
        r10 = android.text.Layout.Alignment.ALIGN_NORMAL;	 Catch:{ Exception -> 0x043d }
        r4 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r12 = 0;
        r13 = 0;
        r6 = r0;
        r8 = r11;
        r9 = r3;
        r11 = r4;
        r6.<init>(r7, r8, r9, r10, r11, r12, r13);	 Catch:{ Exception -> 0x043d }
        r1.messageLayout = r0;	 Catch:{ Exception -> 0x043d }
        goto L_0x0441;
    L_0x043d:
        r0 = move-exception;
        org.telegram.messenger.FileLog.e(r0);
    L_0x0441:
        r0 = org.telegram.messenger.LocaleController.isRTL;
        if (r0 == 0) goto L_0x04d3;
    L_0x0445:
        r0 = r1.nameLayout;
        r4 = 0;
        if (r0 == 0) goto L_0x049f;
    L_0x044a:
        r0 = r0.getLineCount();
        if (r0 <= 0) goto L_0x049f;
    L_0x0450:
        r0 = r1.nameLayout;
        r0 = r0.getLineLeft(r2);
        r5 = r1.nameLayout;
        r5 = r5.getLineWidth(r2);
        r5 = (double) r5;
        r5 = java.lang.Math.ceil(r5);
        r7 = r1.drawVerified;
        if (r7 == 0) goto L_0x0488;
    L_0x0465:
        r7 = r1.nameLeft;
        r7 = (double) r7;
        r9 = (double) r15;
        java.lang.Double.isNaN(r9);
        r9 = r9 - r5;
        java.lang.Double.isNaN(r7);
        r7 = r7 + r9;
        r9 = org.telegram.messenger.AndroidUtilities.dp(r14);
        r9 = (double) r9;
        java.lang.Double.isNaN(r9);
        r7 = r7 - r9;
        r9 = org.telegram.ui.ActionBar.Theme.dialogs_verifiedDrawable;
        r9 = r9.getIntrinsicWidth();
        r9 = (double) r9;
        java.lang.Double.isNaN(r9);
        r7 = r7 - r9;
        r7 = (int) r7;
        r1.nameMuteLeft = r7;
    L_0x0488:
        r0 = (r0 > r4 ? 1 : (r0 == r4 ? 0 : -1));
        if (r0 != 0) goto L_0x049f;
    L_0x048c:
        r7 = (double) r15;
        r0 = (r5 > r7 ? 1 : (r5 == r7 ? 0 : -1));
        if (r0 >= 0) goto L_0x049f;
    L_0x0491:
        r0 = r1.nameLeft;
        r9 = (double) r0;
        java.lang.Double.isNaN(r7);
        r7 = r7 - r5;
        java.lang.Double.isNaN(r9);
        r9 = r9 + r7;
        r0 = (int) r9;
        r1.nameLeft = r0;
    L_0x049f:
        r0 = r1.messageLayout;
        if (r0 == 0) goto L_0x054a;
    L_0x04a3:
        r0 = r0.getLineCount();
        if (r0 <= 0) goto L_0x054a;
    L_0x04a9:
        r0 = r1.messageLayout;
        r0 = r0.getLineLeft(r2);
        r0 = (r0 > r4 ? 1 : (r0 == r4 ? 0 : -1));
        if (r0 != 0) goto L_0x054a;
    L_0x04b3:
        r0 = r1.messageLayout;
        r0 = r0.getLineWidth(r2);
        r4 = (double) r0;
        r4 = java.lang.Math.ceil(r4);
        r2 = (double) r3;
        r0 = (r4 > r2 ? 1 : (r4 == r2 ? 0 : -1));
        if (r0 >= 0) goto L_0x054a;
    L_0x04c3:
        r0 = r1.messageLeft;
        r6 = (double) r0;
        java.lang.Double.isNaN(r2);
        r2 = r2 - r4;
        java.lang.Double.isNaN(r6);
        r6 = r6 + r2;
        r0 = (int) r6;
        r1.messageLeft = r0;
        goto L_0x054a;
    L_0x04d3:
        r0 = r1.nameLayout;
        if (r0 == 0) goto L_0x0517;
    L_0x04d7:
        r0 = r0.getLineCount();
        if (r0 <= 0) goto L_0x0517;
    L_0x04dd:
        r0 = r1.nameLayout;
        r0 = r0.getLineRight(r2);
        r4 = (float) r15;
        r4 = (r0 > r4 ? 1 : (r0 == r4 ? 0 : -1));
        if (r4 != 0) goto L_0x0506;
    L_0x04e8:
        r4 = r1.nameLayout;
        r4 = r4.getLineWidth(r2);
        r4 = (double) r4;
        r4 = java.lang.Math.ceil(r4);
        r6 = (double) r15;
        r8 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1));
        if (r8 >= 0) goto L_0x0506;
    L_0x04f8:
        r8 = r1.nameLeft;
        r8 = (double) r8;
        java.lang.Double.isNaN(r6);
        r6 = r6 - r4;
        java.lang.Double.isNaN(r8);
        r8 = r8 - r6;
        r4 = (int) r8;
        r1.nameLeft = r4;
    L_0x0506:
        r4 = r1.drawVerified;
        if (r4 == 0) goto L_0x0517;
    L_0x050a:
        r4 = r1.nameLeft;
        r4 = (float) r4;
        r4 = r4 + r0;
        r0 = org.telegram.messenger.AndroidUtilities.dp(r14);
        r0 = (float) r0;
        r4 = r4 + r0;
        r0 = (int) r4;
        r1.nameMuteLeft = r0;
    L_0x0517:
        r0 = r1.messageLayout;
        if (r0 == 0) goto L_0x054a;
    L_0x051b:
        r0 = r0.getLineCount();
        if (r0 <= 0) goto L_0x054a;
    L_0x0521:
        r0 = r1.messageLayout;
        r0 = r0.getLineRight(r2);
        r4 = (float) r3;
        r0 = (r0 > r4 ? 1 : (r0 == r4 ? 0 : -1));
        if (r0 != 0) goto L_0x054a;
    L_0x052c:
        r0 = r1.messageLayout;
        r0 = r0.getLineWidth(r2);
        r4 = (double) r0;
        r4 = java.lang.Math.ceil(r4);
        r2 = (double) r3;
        r0 = (r4 > r2 ? 1 : (r4 == r2 ? 0 : -1));
        if (r0 >= 0) goto L_0x054a;
    L_0x053c:
        r0 = r1.messageLeft;
        r6 = (double) r0;
        java.lang.Double.isNaN(r2);
        r2 = r2 - r4;
        java.lang.Double.isNaN(r6);
        r6 = r6 - r2;
        r0 = (int) r6;
        r1.messageLeft = r0;
    L_0x054a:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.DialogMeUrlCell.buildLayout():void");
    }

    public void setDialogSelected(boolean z) {
        if (this.isSelected != z) {
            invalidate();
        }
        this.isSelected = z;
    }

    /* Access modifiers changed, original: protected */
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
                FileLog.e(e);
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
