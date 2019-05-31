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

    /* JADX WARNING: Removed duplicated region for block: B:111:0x0383  */
    /* JADX WARNING: Removed duplicated region for block: B:126:0x03f6  */
    /* JADX WARNING: Removed duplicated region for block: B:121:0x03dd  */
    /* JADX WARNING: Removed duplicated region for block: B:159:0x04e0  */
    /* JADX WARNING: Removed duplicated region for block: B:138:0x0452  */
    public void buildLayout() {
        /*
        r21 = this;
        r1 = r21;
        r4 = org.telegram.ui.ActionBar.Theme.dialogs_namePaint;
        r10 = org.telegram.ui.ActionBar.Theme.dialogs_messagePaint;
        r13 = 0;
        r1.drawNameGroup = r13;
        r1.drawNameBroadcast = r13;
        r1.drawNameLock = r13;
        r1.drawNameBot = r13;
        r1.drawVerified = r13;
        r0 = r1.recentMeUrl;
        r2 = r0 instanceof org.telegram.tgnet.TLRPC.TL_recentMeUrlChat;
        r3 = NUM; // 0x418CLASSNAME float:17.5 double:5.43321066E-315;
        r5 = NUM; // 0x41840000 float:16.5 double:5.43062033E-315;
        r6 = 1;
        r7 = NUM; // 0x41600000 float:14.0 double:5.41896386E-315;
        if (r2 == 0) goto L_0x00bd;
    L_0x001e:
        r0 = r1.currentAccount;
        r0 = org.telegram.messenger.MessagesController.getInstance(r0);
        r2 = r1.recentMeUrl;
        r2 = r2.chat_id;
        r2 = java.lang.Integer.valueOf(r2);
        r0 = r0.getChat(r2);
        r2 = r0.id;
        if (r2 < 0) goto L_0x0048;
    L_0x0034:
        r2 = org.telegram.messenger.ChatObject.isChannel(r0);
        if (r2 == 0) goto L_0x003f;
    L_0x003a:
        r2 = r0.megagroup;
        if (r2 != 0) goto L_0x003f;
    L_0x003e:
        goto L_0x0048;
    L_0x003f:
        r1.drawNameGroup = r6;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r3);
        r1.nameLockTop = r2;
        goto L_0x0050;
    L_0x0048:
        r1.drawNameBroadcast = r6;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r5);
        r1.nameLockTop = r2;
    L_0x0050:
        r2 = r0.verified;
        r1.drawVerified = r2;
        r2 = org.telegram.messenger.LocaleController.isRTL;
        if (r2 != 0) goto L_0x007b;
    L_0x0058:
        r2 = org.telegram.messenger.AndroidUtilities.leftBaseline;
        r2 = (float) r2;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r2);
        r1.nameLockLeft = r2;
        r2 = org.telegram.messenger.AndroidUtilities.leftBaseline;
        r2 = r2 + 4;
        r2 = (float) r2;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r2);
        r3 = r1.drawNameGroup;
        if (r3 == 0) goto L_0x0071;
    L_0x006e:
        r3 = org.telegram.ui.ActionBar.Theme.dialogs_groupDrawable;
        goto L_0x0073;
    L_0x0071:
        r3 = org.telegram.ui.ActionBar.Theme.dialogs_broadcastDrawable;
    L_0x0073:
        r3 = r3.getIntrinsicWidth();
        r2 = r2 + r3;
        r1.nameLeft = r2;
        goto L_0x009d;
    L_0x007b:
        r2 = r21.getMeasuredWidth();
        r3 = org.telegram.messenger.AndroidUtilities.leftBaseline;
        r3 = (float) r3;
        r3 = org.telegram.messenger.AndroidUtilities.dp(r3);
        r2 = r2 - r3;
        r3 = r1.drawNameGroup;
        if (r3 == 0) goto L_0x008e;
    L_0x008b:
        r3 = org.telegram.ui.ActionBar.Theme.dialogs_groupDrawable;
        goto L_0x0090;
    L_0x008e:
        r3 = org.telegram.ui.ActionBar.Theme.dialogs_broadcastDrawable;
    L_0x0090:
        r3 = r3.getIntrinsicWidth();
        r2 = r2 - r3;
        r1.nameLockLeft = r2;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r7);
        r1.nameLeft = r2;
    L_0x009d:
        r2 = r0.title;
        r3 = r1.avatarDrawable;
        r3.setInfo(r0);
        r14 = r1.avatarImage;
        r15 = org.telegram.messenger.ImageLocation.getForChat(r0, r13);
        r0 = r1.avatarDrawable;
        r18 = 0;
        r3 = r1.recentMeUrl;
        r20 = 0;
        r16 = "50_50";
        r17 = r0;
        r19 = r3;
        r14.setImage(r15, r16, r17, r18, r19, r20);
        goto L_0x02ef;
    L_0x00bd:
        r2 = r0 instanceof org.telegram.tgnet.TLRPC.TL_recentMeUrlUser;
        if (r2 == 0) goto L_0x0156;
    L_0x00c1:
        r0 = r1.currentAccount;
        r0 = org.telegram.messenger.MessagesController.getInstance(r0);
        r2 = r1.recentMeUrl;
        r2 = r2.user_id;
        r2 = java.lang.Integer.valueOf(r2);
        r0 = r0.getUser(r2);
        r2 = org.telegram.messenger.LocaleController.isRTL;
        if (r2 != 0) goto L_0x00e1;
    L_0x00d7:
        r2 = org.telegram.messenger.AndroidUtilities.leftBaseline;
        r2 = (float) r2;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r2);
        r1.nameLeft = r2;
        goto L_0x00e7;
    L_0x00e1:
        r2 = org.telegram.messenger.AndroidUtilities.dp(r7);
        r1.nameLeft = r2;
    L_0x00e7:
        if (r0 == 0) goto L_0x0134;
    L_0x00e9:
        r2 = r0.bot;
        if (r2 == 0) goto L_0x0130;
    L_0x00ed:
        r1.drawNameBot = r6;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r5);
        r1.nameLockTop = r2;
        r2 = org.telegram.messenger.LocaleController.isRTL;
        if (r2 != 0) goto L_0x0115;
    L_0x00f9:
        r2 = org.telegram.messenger.AndroidUtilities.leftBaseline;
        r2 = (float) r2;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r2);
        r1.nameLockLeft = r2;
        r2 = org.telegram.messenger.AndroidUtilities.leftBaseline;
        r2 = r2 + 4;
        r2 = (float) r2;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r2);
        r3 = org.telegram.ui.ActionBar.Theme.dialogs_botDrawable;
        r3 = r3.getIntrinsicWidth();
        r2 = r2 + r3;
        r1.nameLeft = r2;
        goto L_0x0130;
    L_0x0115:
        r2 = r21.getMeasuredWidth();
        r3 = org.telegram.messenger.AndroidUtilities.leftBaseline;
        r3 = (float) r3;
        r3 = org.telegram.messenger.AndroidUtilities.dp(r3);
        r2 = r2 - r3;
        r3 = org.telegram.ui.ActionBar.Theme.dialogs_botDrawable;
        r3 = r3.getIntrinsicWidth();
        r2 = r2 - r3;
        r1.nameLockLeft = r2;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r7);
        r1.nameLeft = r2;
    L_0x0130:
        r2 = r0.verified;
        r1.drawVerified = r2;
    L_0x0134:
        r2 = org.telegram.messenger.UserObject.getUserName(r0);
        r3 = r1.avatarDrawable;
        r3.setInfo(r0);
        r14 = r1.avatarImage;
        r15 = org.telegram.messenger.ImageLocation.getForUser(r0, r13);
        r0 = r1.avatarDrawable;
        r18 = 0;
        r3 = r1.recentMeUrl;
        r20 = 0;
        r16 = "50_50";
        r17 = r0;
        r19 = r3;
        r14.setImage(r15, r16, r17, r18, r19, r20);
        goto L_0x02ef;
    L_0x0156:
        r2 = r0 instanceof org.telegram.tgnet.TLRPC.TL_recentMeUrlStickerSet;
        r8 = 0;
        r9 = 5;
        if (r2 == 0) goto L_0x019c;
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
        r0 = org.telegram.messenger.AndroidUtilities.dp(r7);
        r1.nameLeft = r0;
    L_0x0170:
        r0 = r1.recentMeUrl;
        r0 = r0.set;
        r0 = r0.set;
        r2 = r0.title;
        r0 = r1.avatarDrawable;
        r0.setInfo(r9, r2, r8, r13);
        r14 = r1.avatarImage;
        r0 = r1.recentMeUrl;
        r0 = r0.set;
        r0 = r0.cover;
        r15 = org.telegram.messenger.ImageLocation.getForDocument(r0);
        r16 = 0;
        r0 = r1.avatarDrawable;
        r18 = 0;
        r3 = r1.recentMeUrl;
        r20 = 0;
        r17 = r0;
        r19 = r3;
        r14.setImage(r15, r16, r17, r18, r19, r20);
        goto L_0x02ef;
    L_0x019c:
        r2 = r0 instanceof org.telegram.tgnet.TLRPC.TL_recentMeUrlChatInvite;
        if (r2 == 0) goto L_0x02ac;
    L_0x01a0:
        r0 = org.telegram.messenger.LocaleController.isRTL;
        if (r0 != 0) goto L_0x01ae;
    L_0x01a4:
        r0 = org.telegram.messenger.AndroidUtilities.leftBaseline;
        r0 = (float) r0;
        r0 = org.telegram.messenger.AndroidUtilities.dp(r0);
        r1.nameLeft = r0;
        goto L_0x01b4;
    L_0x01ae:
        r0 = org.telegram.messenger.AndroidUtilities.dp(r7);
        r1.nameLeft = r0;
    L_0x01b4:
        r0 = r1.recentMeUrl;
        r0 = r0.chat_invite;
        r2 = r0.chat;
        if (r2 == 0) goto L_0x0211;
    L_0x01bc:
        r0 = r1.avatarDrawable;
        r0.setInfo(r2);
        r0 = r1.recentMeUrl;
        r0 = r0.chat_invite;
        r0 = r0.chat;
        r2 = r0.title;
        r8 = r0.id;
        if (r8 < 0) goto L_0x01e7;
    L_0x01cd:
        r0 = org.telegram.messenger.ChatObject.isChannel(r0);
        if (r0 == 0) goto L_0x01de;
    L_0x01d3:
        r0 = r1.recentMeUrl;
        r0 = r0.chat_invite;
        r0 = r0.chat;
        r0 = r0.megagroup;
        if (r0 != 0) goto L_0x01de;
    L_0x01dd:
        goto L_0x01e7;
    L_0x01de:
        r1.drawNameGroup = r6;
        r0 = org.telegram.messenger.AndroidUtilities.dp(r3);
        r1.nameLockTop = r0;
        goto L_0x01ef;
    L_0x01e7:
        r1.drawNameBroadcast = r6;
        r0 = org.telegram.messenger.AndroidUtilities.dp(r5);
        r1.nameLockTop = r0;
    L_0x01ef:
        r0 = r1.recentMeUrl;
        r0 = r0.chat_invite;
        r0 = r0.chat;
        r3 = r0.verified;
        r1.drawVerified = r3;
        r14 = r1.avatarImage;
        r15 = org.telegram.messenger.ImageLocation.getForChat(r0, r13);
        r0 = r1.avatarDrawable;
        r18 = 0;
        r3 = r1.recentMeUrl;
        r20 = 0;
        r16 = "50_50";
        r17 = r0;
        r19 = r3;
        r14.setImage(r15, r16, r17, r18, r19, r20);
        goto L_0x0262;
    L_0x0211:
        r0 = r0.title;
        r2 = r1.avatarDrawable;
        r2.setInfo(r9, r0, r8, r13);
        r2 = r1.recentMeUrl;
        r2 = r2.chat_invite;
        r8 = r2.broadcast;
        if (r8 != 0) goto L_0x022e;
    L_0x0220:
        r2 = r2.channel;
        if (r2 == 0) goto L_0x0225;
    L_0x0224:
        goto L_0x022e;
    L_0x0225:
        r1.drawNameGroup = r6;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r3);
        r1.nameLockTop = r2;
        goto L_0x0236;
    L_0x022e:
        r1.drawNameBroadcast = r6;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r5);
        r1.nameLockTop = r2;
    L_0x0236:
        r2 = r1.recentMeUrl;
        r2 = r2.chat_invite;
        r2 = r2.photo;
        r2 = r2.sizes;
        r3 = 50;
        r2 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r2, r3);
        r14 = r1.avatarImage;
        r3 = r1.recentMeUrl;
        r3 = r3.chat_invite;
        r3 = r3.photo;
        r15 = org.telegram.messenger.ImageLocation.getForPhoto(r2, r3);
        r2 = r1.avatarDrawable;
        r18 = 0;
        r3 = r1.recentMeUrl;
        r20 = 0;
        r16 = "50_50";
        r17 = r2;
        r19 = r3;
        r14.setImage(r15, r16, r17, r18, r19, r20);
        r2 = r0;
    L_0x0262:
        r0 = org.telegram.messenger.LocaleController.isRTL;
        if (r0 != 0) goto L_0x0289;
    L_0x0266:
        r0 = org.telegram.messenger.AndroidUtilities.leftBaseline;
        r0 = (float) r0;
        r0 = org.telegram.messenger.AndroidUtilities.dp(r0);
        r1.nameLockLeft = r0;
        r0 = org.telegram.messenger.AndroidUtilities.leftBaseline;
        r0 = r0 + 4;
        r0 = (float) r0;
        r0 = org.telegram.messenger.AndroidUtilities.dp(r0);
        r3 = r1.drawNameGroup;
        if (r3 == 0) goto L_0x027f;
    L_0x027c:
        r3 = org.telegram.ui.ActionBar.Theme.dialogs_groupDrawable;
        goto L_0x0281;
    L_0x027f:
        r3 = org.telegram.ui.ActionBar.Theme.dialogs_broadcastDrawable;
    L_0x0281:
        r3 = r3.getIntrinsicWidth();
        r0 = r0 + r3;
        r1.nameLeft = r0;
        goto L_0x02ef;
    L_0x0289:
        r0 = r21.getMeasuredWidth();
        r3 = org.telegram.messenger.AndroidUtilities.leftBaseline;
        r3 = (float) r3;
        r3 = org.telegram.messenger.AndroidUtilities.dp(r3);
        r0 = r0 - r3;
        r3 = r1.drawNameGroup;
        if (r3 == 0) goto L_0x029c;
    L_0x0299:
        r3 = org.telegram.ui.ActionBar.Theme.dialogs_groupDrawable;
        goto L_0x029e;
    L_0x029c:
        r3 = org.telegram.ui.ActionBar.Theme.dialogs_broadcastDrawable;
    L_0x029e:
        r3 = r3.getIntrinsicWidth();
        r0 = r0 - r3;
        r1.nameLockLeft = r0;
        r0 = org.telegram.messenger.AndroidUtilities.dp(r7);
        r1.nameLeft = r0;
        goto L_0x02ef;
    L_0x02ac:
        r2 = r0 instanceof org.telegram.tgnet.TLRPC.TL_recentMeUrlUnknown;
        if (r2 == 0) goto L_0x02db;
    L_0x02b0:
        r0 = org.telegram.messenger.LocaleController.isRTL;
        if (r0 != 0) goto L_0x02be;
    L_0x02b4:
        r0 = org.telegram.messenger.AndroidUtilities.leftBaseline;
        r0 = (float) r0;
        r0 = org.telegram.messenger.AndroidUtilities.dp(r0);
        r1.nameLeft = r0;
        goto L_0x02c4;
    L_0x02be:
        r0 = org.telegram.messenger.AndroidUtilities.dp(r7);
        r1.nameLeft = r0;
    L_0x02c4:
        r14 = r1.avatarImage;
        r15 = 0;
        r16 = 0;
        r0 = r1.avatarDrawable;
        r18 = 0;
        r2 = r1.recentMeUrl;
        r20 = 0;
        r17 = r0;
        r19 = r2;
        r14.setImage(r15, r16, r17, r18, r19, r20);
        r2 = "Url";
        goto L_0x02ef;
    L_0x02db:
        r14 = r1.avatarImage;
        r15 = 0;
        r16 = 0;
        r2 = r1.avatarDrawable;
        r18 = 0;
        r20 = 0;
        r17 = r2;
        r19 = r0;
        r14.setImage(r15, r16, r17, r18, r19, r20);
        r2 = "";
    L_0x02ef:
        r0 = new java.lang.StringBuilder;
        r0.<init>();
        r3 = r1.currentAccount;
        r3 = org.telegram.messenger.MessagesController.getInstance(r3);
        r3 = r3.linkPrefix;
        r0.append(r3);
        r3 = "/";
        r0.append(r3);
        r3 = r1.recentMeUrl;
        r3 = r3.url;
        r0.append(r3);
        r11 = r0.toString();
        r0 = android.text.TextUtils.isEmpty(r2);
        if (r0 == 0) goto L_0x031e;
    L_0x0315:
        r0 = NUM; // 0x7f0d04d8 float:1.874463E38 double:1.05313039E-314;
        r2 = "HiddenName";
        r2 = org.telegram.messenger.LocaleController.getString(r2, r0);
    L_0x031e:
        r0 = org.telegram.messenger.LocaleController.isRTL;
        if (r0 != 0) goto L_0x032e;
    L_0x0322:
        r0 = r21.getMeasuredWidth();
        r3 = r1.nameLeft;
        r0 = r0 - r3;
        r3 = org.telegram.messenger.AndroidUtilities.dp(r7);
        goto L_0x033c;
    L_0x032e:
        r0 = r21.getMeasuredWidth();
        r3 = r1.nameLeft;
        r0 = r0 - r3;
        r3 = org.telegram.messenger.AndroidUtilities.leftBaseline;
        r3 = (float) r3;
        r3 = org.telegram.messenger.AndroidUtilities.dp(r3);
    L_0x033c:
        r0 = r0 - r3;
        r3 = r1.drawNameLock;
        r5 = NUM; // 0x40800000 float:4.0 double:5.34643471E-315;
        if (r3 == 0) goto L_0x0350;
    L_0x0343:
        r3 = org.telegram.messenger.AndroidUtilities.dp(r5);
        r5 = org.telegram.ui.ActionBar.Theme.dialogs_lockDrawable;
        r5 = r5.getIntrinsicWidth();
    L_0x034d:
        r3 = r3 + r5;
        r0 = r0 - r3;
        goto L_0x037d;
    L_0x0350:
        r3 = r1.drawNameGroup;
        if (r3 == 0) goto L_0x035f;
    L_0x0354:
        r3 = org.telegram.messenger.AndroidUtilities.dp(r5);
        r5 = org.telegram.ui.ActionBar.Theme.dialogs_groupDrawable;
        r5 = r5.getIntrinsicWidth();
        goto L_0x034d;
    L_0x035f:
        r3 = r1.drawNameBroadcast;
        if (r3 == 0) goto L_0x036e;
    L_0x0363:
        r3 = org.telegram.messenger.AndroidUtilities.dp(r5);
        r5 = org.telegram.ui.ActionBar.Theme.dialogs_broadcastDrawable;
        r5 = r5.getIntrinsicWidth();
        goto L_0x034d;
    L_0x036e:
        r3 = r1.drawNameBot;
        if (r3 == 0) goto L_0x037d;
    L_0x0372:
        r3 = org.telegram.messenger.AndroidUtilities.dp(r5);
        r5 = org.telegram.ui.ActionBar.Theme.dialogs_botDrawable;
        r5 = r5.getIntrinsicWidth();
        goto L_0x034d;
    L_0x037d:
        r3 = r1.drawVerified;
        r14 = NUM; // 0x40CLASSNAME float:6.0 double:5.367157323E-315;
        if (r3 == 0) goto L_0x0398;
    L_0x0383:
        r3 = org.telegram.messenger.AndroidUtilities.dp(r14);
        r5 = org.telegram.ui.ActionBar.Theme.dialogs_verifiedDrawable;
        r5 = r5.getIntrinsicWidth();
        r3 = r3 + r5;
        r0 = r0 - r3;
        r5 = org.telegram.messenger.LocaleController.isRTL;
        if (r5 == 0) goto L_0x0398;
    L_0x0393:
        r5 = r1.nameLeft;
        r5 = r5 + r3;
        r1.nameLeft = r5;
    L_0x0398:
        r12 = NUM; // 0x41400000 float:12.0 double:5.408602553E-315;
        r3 = org.telegram.messenger.AndroidUtilities.dp(r12);
        r15 = java.lang.Math.max(r3, r0);
        r0 = 10;
        r3 = 32;
        r0 = r2.replace(r0, r3);	 Catch:{ Exception -> 0x03c7 }
        r2 = org.telegram.messenger.AndroidUtilities.dp(r12);	 Catch:{ Exception -> 0x03c7 }
        r2 = r15 - r2;
        r2 = (float) r2;	 Catch:{ Exception -> 0x03c7 }
        r3 = android.text.TextUtils.TruncateAt.END;	 Catch:{ Exception -> 0x03c7 }
        r3 = android.text.TextUtils.ellipsize(r0, r4, r2, r3);	 Catch:{ Exception -> 0x03c7 }
        r0 = new android.text.StaticLayout;	 Catch:{ Exception -> 0x03c7 }
        r6 = android.text.Layout.Alignment.ALIGN_NORMAL;	 Catch:{ Exception -> 0x03c7 }
        r7 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r8 = 0;
        r9 = 0;
        r2 = r0;
        r5 = r15;
        r2.<init>(r3, r4, r5, r6, r7, r8, r9);	 Catch:{ Exception -> 0x03c7 }
        r1.nameLayout = r0;	 Catch:{ Exception -> 0x03c7 }
        goto L_0x03cb;
    L_0x03c7:
        r0 = move-exception;
        org.telegram.messenger.FileLog.e(r0);
    L_0x03cb:
        r0 = r21.getMeasuredWidth();
        r2 = org.telegram.messenger.AndroidUtilities.leftBaseline;
        r2 = r2 + 16;
        r2 = (float) r2;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r2);
        r0 = r0 - r2;
        r2 = org.telegram.messenger.LocaleController.isRTL;
        if (r2 != 0) goto L_0x03f6;
    L_0x03dd:
        r2 = org.telegram.messenger.AndroidUtilities.leftBaseline;
        r2 = (float) r2;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r2);
        r1.messageLeft = r2;
        r2 = org.telegram.messenger.AndroidUtilities.isTablet();
        if (r2 == 0) goto L_0x03ef;
    L_0x03ec:
        r2 = NUM; // 0x41500000 float:13.0 double:5.413783207E-315;
        goto L_0x03f1;
    L_0x03ef:
        r2 = NUM; // 0x41100000 float:9.0 double:5.39306059E-315;
    L_0x03f1:
        r2 = org.telegram.messenger.AndroidUtilities.dp(r2);
        goto L_0x0412;
    L_0x03f6:
        r2 = NUM; // 0x41800000 float:16.0 double:5.42932517E-315;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r2);
        r1.messageLeft = r2;
        r2 = r21.getMeasuredWidth();
        r3 = org.telegram.messenger.AndroidUtilities.isTablet();
        if (r3 == 0) goto L_0x040b;
    L_0x0408:
        r3 = NUM; // 0x42820000 float:65.0 double:5.51286321E-315;
        goto L_0x040d;
    L_0x040b:
        r3 = NUM; // 0x42740000 float:61.0 double:5.50833014E-315;
    L_0x040d:
        r3 = org.telegram.messenger.AndroidUtilities.dp(r3);
        r2 = r2 - r3;
    L_0x0412:
        r3 = r1.avatarImage;
        r4 = r1.avatarTop;
        r5 = NUM; // 0x42500000 float:52.0 double:5.496673668E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r5);
        r5 = org.telegram.messenger.AndroidUtilities.dp(r5);
        r3.setImageCoords(r2, r4, r6, r5);
        r2 = org.telegram.messenger.AndroidUtilities.dp(r12);
        r2 = java.lang.Math.max(r2, r0);
        r0 = org.telegram.messenger.AndroidUtilities.dp(r12);
        r0 = r2 - r0;
        r0 = (float) r0;
        r3 = android.text.TextUtils.TruncateAt.END;
        r6 = android.text.TextUtils.ellipsize(r11, r10, r0, r3);
        r0 = new android.text.StaticLayout;	 Catch:{ Exception -> 0x044a }
        r9 = android.text.Layout.Alignment.ALIGN_NORMAL;	 Catch:{ Exception -> 0x044a }
        r3 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r11 = 0;
        r12 = 0;
        r5 = r0;
        r7 = r10;
        r8 = r2;
        r10 = r3;
        r5.<init>(r6, r7, r8, r9, r10, r11, r12);	 Catch:{ Exception -> 0x044a }
        r1.messageLayout = r0;	 Catch:{ Exception -> 0x044a }
        goto L_0x044e;
    L_0x044a:
        r0 = move-exception;
        org.telegram.messenger.FileLog.e(r0);
    L_0x044e:
        r0 = org.telegram.messenger.LocaleController.isRTL;
        if (r0 == 0) goto L_0x04e0;
    L_0x0452:
        r0 = r1.nameLayout;
        r3 = 0;
        if (r0 == 0) goto L_0x04ac;
    L_0x0457:
        r0 = r0.getLineCount();
        if (r0 <= 0) goto L_0x04ac;
    L_0x045d:
        r0 = r1.nameLayout;
        r0 = r0.getLineLeft(r13);
        r4 = r1.nameLayout;
        r4 = r4.getLineWidth(r13);
        r4 = (double) r4;
        r4 = java.lang.Math.ceil(r4);
        r6 = r1.drawVerified;
        if (r6 == 0) goto L_0x0495;
    L_0x0472:
        r6 = r1.nameLeft;
        r6 = (double) r6;
        r8 = (double) r15;
        java.lang.Double.isNaN(r8);
        r8 = r8 - r4;
        java.lang.Double.isNaN(r6);
        r6 = r6 + r8;
        r8 = org.telegram.messenger.AndroidUtilities.dp(r14);
        r8 = (double) r8;
        java.lang.Double.isNaN(r8);
        r6 = r6 - r8;
        r8 = org.telegram.ui.ActionBar.Theme.dialogs_verifiedDrawable;
        r8 = r8.getIntrinsicWidth();
        r8 = (double) r8;
        java.lang.Double.isNaN(r8);
        r6 = r6 - r8;
        r6 = (int) r6;
        r1.nameMuteLeft = r6;
    L_0x0495:
        r0 = (r0 > r3 ? 1 : (r0 == r3 ? 0 : -1));
        if (r0 != 0) goto L_0x04ac;
    L_0x0499:
        r6 = (double) r15;
        r0 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1));
        if (r0 >= 0) goto L_0x04ac;
    L_0x049e:
        r0 = r1.nameLeft;
        r8 = (double) r0;
        java.lang.Double.isNaN(r6);
        r6 = r6 - r4;
        java.lang.Double.isNaN(r8);
        r8 = r8 + r6;
        r0 = (int) r8;
        r1.nameLeft = r0;
    L_0x04ac:
        r0 = r1.messageLayout;
        if (r0 == 0) goto L_0x0557;
    L_0x04b0:
        r0 = r0.getLineCount();
        if (r0 <= 0) goto L_0x0557;
    L_0x04b6:
        r0 = r1.messageLayout;
        r0 = r0.getLineLeft(r13);
        r0 = (r0 > r3 ? 1 : (r0 == r3 ? 0 : -1));
        if (r0 != 0) goto L_0x0557;
    L_0x04c0:
        r0 = r1.messageLayout;
        r0 = r0.getLineWidth(r13);
        r3 = (double) r0;
        r3 = java.lang.Math.ceil(r3);
        r5 = (double) r2;
        r0 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1));
        if (r0 >= 0) goto L_0x0557;
    L_0x04d0:
        r0 = r1.messageLeft;
        r7 = (double) r0;
        java.lang.Double.isNaN(r5);
        r5 = r5 - r3;
        java.lang.Double.isNaN(r7);
        r7 = r7 + r5;
        r0 = (int) r7;
        r1.messageLeft = r0;
        goto L_0x0557;
    L_0x04e0:
        r0 = r1.nameLayout;
        if (r0 == 0) goto L_0x0524;
    L_0x04e4:
        r0 = r0.getLineCount();
        if (r0 <= 0) goto L_0x0524;
    L_0x04ea:
        r0 = r1.nameLayout;
        r0 = r0.getLineRight(r13);
        r3 = (float) r15;
        r3 = (r0 > r3 ? 1 : (r0 == r3 ? 0 : -1));
        if (r3 != 0) goto L_0x0513;
    L_0x04f5:
        r3 = r1.nameLayout;
        r3 = r3.getLineWidth(r13);
        r3 = (double) r3;
        r3 = java.lang.Math.ceil(r3);
        r5 = (double) r15;
        r7 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1));
        if (r7 >= 0) goto L_0x0513;
    L_0x0505:
        r7 = r1.nameLeft;
        r7 = (double) r7;
        java.lang.Double.isNaN(r5);
        r5 = r5 - r3;
        java.lang.Double.isNaN(r7);
        r7 = r7 - r5;
        r3 = (int) r7;
        r1.nameLeft = r3;
    L_0x0513:
        r3 = r1.drawVerified;
        if (r3 == 0) goto L_0x0524;
    L_0x0517:
        r3 = r1.nameLeft;
        r3 = (float) r3;
        r3 = r3 + r0;
        r0 = org.telegram.messenger.AndroidUtilities.dp(r14);
        r0 = (float) r0;
        r3 = r3 + r0;
        r0 = (int) r3;
        r1.nameMuteLeft = r0;
    L_0x0524:
        r0 = r1.messageLayout;
        if (r0 == 0) goto L_0x0557;
    L_0x0528:
        r0 = r0.getLineCount();
        if (r0 <= 0) goto L_0x0557;
    L_0x052e:
        r0 = r1.messageLayout;
        r0 = r0.getLineRight(r13);
        r3 = (float) r2;
        r0 = (r0 > r3 ? 1 : (r0 == r3 ? 0 : -1));
        if (r0 != 0) goto L_0x0557;
    L_0x0539:
        r0 = r1.messageLayout;
        r0 = r0.getLineWidth(r13);
        r3 = (double) r0;
        r3 = java.lang.Math.ceil(r3);
        r5 = (double) r2;
        r0 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1));
        if (r0 >= 0) goto L_0x0557;
    L_0x0549:
        r0 = r1.messageLeft;
        r7 = (double) r0;
        java.lang.Double.isNaN(r5);
        r5 = r5 - r3;
        java.lang.Double.isNaN(r7);
        r7 = r7 - r5;
        r0 = (int) r7;
        r1.messageLeft = r0;
    L_0x0557:
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
