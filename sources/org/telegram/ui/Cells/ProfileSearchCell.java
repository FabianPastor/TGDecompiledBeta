package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.text.StaticLayout;
import android.view.View.MeasureSpec;
import android.view.accessibility.AccessibilityNodeInfo;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.EncryptedChat;
import org.telegram.tgnet.TLRPC.FileLocation;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.NotificationsSettingsActivity.NotificationException;

public class ProfileSearchCell extends BaseCell {
    private AvatarDrawable avatarDrawable;
    private ImageReceiver avatarImage = new ImageReceiver(this);
    private Chat chat;
    private StaticLayout countLayout;
    private int countLeft;
    private int countTop = AndroidUtilities.dp(19.0f);
    private int countWidth;
    private int currentAccount = UserConfig.selectedAccount;
    private CharSequence currentName;
    private long dialog_id;
    private boolean drawCheck;
    private boolean drawCount;
    private boolean drawNameBot;
    private boolean drawNameBroadcast;
    private boolean drawNameGroup;
    private boolean drawNameLock;
    private EncryptedChat encryptedChat;
    private FileLocation lastAvatar;
    private String lastName;
    private int lastStatus;
    private int lastUnreadCount;
    private StaticLayout nameLayout;
    private int nameLeft;
    private int nameLockLeft;
    private int nameLockTop;
    private int nameTop;
    private int nameWidth;
    private RectF rect = new RectF();
    private boolean savedMessages;
    private StaticLayout statusLayout;
    private int statusLeft;
    private CharSequence subLabel;
    private int sublabelOffsetX;
    private int sublabelOffsetY;
    public boolean useSeparator;
    private User user;

    public ProfileSearchCell(Context context) {
        super(context);
        this.avatarImage.setRoundRadius(AndroidUtilities.dp(23.0f));
        this.avatarDrawable = new AvatarDrawable();
    }

    public void setData(TLObject tLObject, EncryptedChat encryptedChat, CharSequence charSequence, CharSequence charSequence2, boolean z, boolean z2) {
        this.currentName = charSequence;
        if (tLObject instanceof User) {
            this.user = (User) tLObject;
            this.chat = null;
        } else if (tLObject instanceof Chat) {
            this.chat = (Chat) tLObject;
            this.user = null;
        }
        this.encryptedChat = encryptedChat;
        this.subLabel = charSequence2;
        this.drawCount = z;
        this.savedMessages = z2;
        update(0);
    }

    public void setException(NotificationException notificationException, CharSequence charSequence) {
        String string;
        boolean z = notificationException.hasCustom;
        int i = notificationException.notify;
        int i2 = notificationException.muteUntil;
        String str = "NotificationsCustom";
        String str2 = "NotificationsUnmuted";
        int i3 = 0;
        if (i != 3 || i2 == Integer.MAX_VALUE) {
            if (i == 0 || i == 1) {
                i3 = 1;
            }
            string = (i3 == 0 || !z) ? i3 != 0 ? LocaleController.getString(str2, NUM) : LocaleController.getString("NotificationsMuted", NUM) : LocaleController.getString(str, NUM);
        } else {
            i2 -= ConnectionsManager.getInstance(this.currentAccount).getCurrentTime();
            if (i2 <= 0) {
                string = z ? LocaleController.getString(str, NUM) : LocaleController.getString(str2, NUM);
            } else {
                String str3 = "WillUnmuteIn";
                Object[] objArr;
                if (i2 < 3600) {
                    objArr = new Object[1];
                    objArr[0] = LocaleController.formatPluralString("Minutes", i2 / 60);
                    string = LocaleController.formatString(str3, NUM, objArr);
                } else if (i2 < 86400) {
                    objArr = new Object[1];
                    objArr[0] = LocaleController.formatPluralString("Hours", (int) Math.ceil((double) ((((float) i2) / 60.0f) / 60.0f)));
                    string = LocaleController.formatString(str3, NUM, objArr);
                } else if (i2 < 31536000) {
                    objArr = new Object[1];
                    objArr[0] = LocaleController.formatPluralString("Days", (int) Math.ceil((double) (((((float) i2) / 60.0f) / 60.0f) / 24.0f)));
                    string = LocaleController.formatString(str3, NUM, objArr);
                } else {
                    string = null;
                }
            }
        }
        if (string == null) {
            string = LocaleController.getString("NotificationsOff", NUM);
        }
        String str4 = string;
        long j = notificationException.did;
        int i4 = (int) j;
        i = (int) (j >> 32);
        User user;
        if (i4 == 0) {
            EncryptedChat encryptedChat = MessagesController.getInstance(this.currentAccount).getEncryptedChat(Integer.valueOf(i));
            if (encryptedChat != null) {
                user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(encryptedChat.user_id));
                if (user != null) {
                    setData(user, encryptedChat, charSequence, str4, false, false);
                }
            }
        } else if (i4 > 0) {
            user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(i4));
            if (user != null) {
                setData(user, null, charSequence, str4, false, false);
            }
        } else {
            Chat chat = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(-i4));
            if (chat != null) {
                setData(chat, null, charSequence, str4, false, false);
            }
        }
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
        setMeasuredDimension(MeasureSpec.getSize(i), AndroidUtilities.dp(60.0f) + this.useSeparator);
    }

    /* Access modifiers changed, original: protected */
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        if (!(this.user == null && this.chat == null && this.encryptedChat == null) && z) {
            buildLayout();
        }
    }

    public void setSublabelOffset(int i, int i2) {
        this.sublabelOffsetX = i;
        this.sublabelOffsetY = i2;
    }

    /* JADX WARNING: Removed duplicated region for block: B:164:0x047c  */
    /* JADX WARNING: Removed duplicated region for block: B:163:0x044a  */
    /* JADX WARNING: Removed duplicated region for block: B:168:0x0493  */
    /* JADX WARNING: Removed duplicated region for block: B:167:0x0482  */
    /* JADX WARNING: Removed duplicated region for block: B:187:0x051c  */
    /* JADX WARNING: Removed duplicated region for block: B:171:0x04b5  */
    /* JADX WARNING: Missing block: B:153:0x041d, code skipped:
            if (r4.expires > org.telegram.tgnet.ConnectionsManager.getInstance(r0.currentAccount).getCurrentTime()) goto L_0x041f;
     */
    public void buildLayout() {
        /*
        r25 = this;
        r0 = r25;
        r1 = 0;
        r0.drawNameBroadcast = r1;
        r0.drawNameLock = r1;
        r0.drawNameGroup = r1;
        r0.drawCheck = r1;
        r0.drawNameBot = r1;
        r2 = r0.encryptedChat;
        r3 = NUM; // 0x41a40000 float:20.5 double:5.44098164E-315;
        r4 = 32;
        r5 = 1;
        r6 = NUM; // 0x41300000 float:11.0 double:5.4034219E-315;
        if (r2 == 0) goto L_0x0065;
    L_0x0018:
        r0.drawNameLock = r5;
        r2 = r2.id;
        r7 = (long) r2;
        r7 = r7 << r4;
        r0.dialog_id = r7;
        r2 = org.telegram.messenger.LocaleController.isRTL;
        if (r2 != 0) goto L_0x0040;
    L_0x0024:
        r2 = org.telegram.messenger.AndroidUtilities.leftBaseline;
        r2 = (float) r2;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r2);
        r0.nameLockLeft = r2;
        r2 = org.telegram.messenger.AndroidUtilities.leftBaseline;
        r2 = r2 + 4;
        r2 = (float) r2;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r2);
        r7 = org.telegram.ui.ActionBar.Theme.dialogs_lockDrawable;
        r7 = r7.getIntrinsicWidth();
        r2 = r2 + r7;
        r0.nameLeft = r2;
        goto L_0x005d;
    L_0x0040:
        r2 = r25.getMeasuredWidth();
        r7 = org.telegram.messenger.AndroidUtilities.leftBaseline;
        r7 = r7 + 2;
        r7 = (float) r7;
        r7 = org.telegram.messenger.AndroidUtilities.dp(r7);
        r2 = r2 - r7;
        r7 = org.telegram.ui.ActionBar.Theme.dialogs_lockDrawable;
        r7 = r7.getIntrinsicWidth();
        r2 = r2 - r7;
        r0.nameLockLeft = r2;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r0.nameLeft = r2;
    L_0x005d:
        r2 = org.telegram.messenger.AndroidUtilities.dp(r3);
        r0.nameLockTop = r2;
        goto L_0x0184;
    L_0x0065:
        r2 = r0.chat;
        if (r2 == 0) goto L_0x0103;
    L_0x0069:
        r3 = r2.id;
        r3 = -r3;
        r7 = (long) r3;
        r0.dialog_id = r7;
        r3 = org.telegram.messenger.SharedConfig.drawDialogIcons;
        if (r3 == 0) goto L_0x0094;
    L_0x0073:
        r2 = org.telegram.messenger.ChatObject.isChannel(r2);
        if (r2 == 0) goto L_0x008a;
    L_0x0079:
        r2 = r0.chat;
        r2 = r2.megagroup;
        if (r2 != 0) goto L_0x008a;
    L_0x007f:
        r0.drawNameBroadcast = r5;
        r2 = NUM; // 0x41b40000 float:22.5 double:5.446162293E-315;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r2);
        r0.nameLockTop = r2;
        goto L_0x0094;
    L_0x008a:
        r0.drawNameGroup = r5;
        r2 = NUM; // 0x41CLASSNAME float:24.0 double:5.450047783E-315;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r2);
        r0.nameLockTop = r2;
    L_0x0094:
        r2 = r0.chat;
        r2 = r2.verified;
        r0.drawCheck = r2;
        r2 = org.telegram.messenger.SharedConfig.drawDialogIcons;
        if (r2 == 0) goto L_0x00ec;
    L_0x009e:
        r2 = org.telegram.messenger.LocaleController.isRTL;
        if (r2 != 0) goto L_0x00c6;
    L_0x00a2:
        r2 = org.telegram.messenger.AndroidUtilities.leftBaseline;
        r2 = (float) r2;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r2);
        r0.nameLockLeft = r2;
        r2 = org.telegram.messenger.AndroidUtilities.leftBaseline;
        r2 = r2 + 4;
        r2 = (float) r2;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r2);
        r3 = r0.drawNameGroup;
        if (r3 == 0) goto L_0x00bb;
    L_0x00b8:
        r3 = org.telegram.ui.ActionBar.Theme.dialogs_groupDrawable;
        goto L_0x00bd;
    L_0x00bb:
        r3 = org.telegram.ui.ActionBar.Theme.dialogs_broadcastDrawable;
    L_0x00bd:
        r3 = r3.getIntrinsicWidth();
        r2 = r2 + r3;
        r0.nameLeft = r2;
        goto L_0x0184;
    L_0x00c6:
        r2 = r25.getMeasuredWidth();
        r3 = org.telegram.messenger.AndroidUtilities.leftBaseline;
        r3 = r3 + 2;
        r3 = (float) r3;
        r3 = org.telegram.messenger.AndroidUtilities.dp(r3);
        r2 = r2 - r3;
        r3 = r0.drawNameGroup;
        if (r3 == 0) goto L_0x00db;
    L_0x00d8:
        r3 = org.telegram.ui.ActionBar.Theme.dialogs_groupDrawable;
        goto L_0x00dd;
    L_0x00db:
        r3 = org.telegram.ui.ActionBar.Theme.dialogs_broadcastDrawable;
    L_0x00dd:
        r3 = r3.getIntrinsicWidth();
        r2 = r2 - r3;
        r0.nameLockLeft = r2;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r0.nameLeft = r2;
        goto L_0x0184;
    L_0x00ec:
        r2 = org.telegram.messenger.LocaleController.isRTL;
        if (r2 != 0) goto L_0x00fb;
    L_0x00f0:
        r2 = org.telegram.messenger.AndroidUtilities.leftBaseline;
        r2 = (float) r2;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r2);
        r0.nameLeft = r2;
        goto L_0x0184;
    L_0x00fb:
        r2 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r0.nameLeft = r2;
        goto L_0x0184;
    L_0x0103:
        r2 = r0.user;
        if (r2 == 0) goto L_0x0184;
    L_0x0107:
        r2 = r2.id;
        r7 = (long) r2;
        r0.dialog_id = r7;
        r2 = org.telegram.messenger.LocaleController.isRTL;
        if (r2 != 0) goto L_0x011a;
    L_0x0110:
        r2 = org.telegram.messenger.AndroidUtilities.leftBaseline;
        r2 = (float) r2;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r2);
        r0.nameLeft = r2;
        goto L_0x0120;
    L_0x011a:
        r2 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r0.nameLeft = r2;
    L_0x0120:
        r2 = org.telegram.messenger.SharedConfig.drawDialogIcons;
        if (r2 == 0) goto L_0x0176;
    L_0x0124:
        r2 = r0.user;
        r7 = r2.bot;
        if (r7 == 0) goto L_0x0176;
    L_0x012a:
        r2 = org.telegram.messenger.MessagesController.isSupportUser(r2);
        if (r2 != 0) goto L_0x0176;
    L_0x0130:
        r0.drawNameBot = r5;
        r2 = org.telegram.messenger.LocaleController.isRTL;
        if (r2 != 0) goto L_0x0152;
    L_0x0136:
        r2 = org.telegram.messenger.AndroidUtilities.leftBaseline;
        r2 = (float) r2;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r2);
        r0.nameLockLeft = r2;
        r2 = org.telegram.messenger.AndroidUtilities.leftBaseline;
        r2 = r2 + 4;
        r2 = (float) r2;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r2);
        r7 = org.telegram.ui.ActionBar.Theme.dialogs_botDrawable;
        r7 = r7.getIntrinsicWidth();
        r2 = r2 + r7;
        r0.nameLeft = r2;
        goto L_0x016f;
    L_0x0152:
        r2 = r25.getMeasuredWidth();
        r7 = org.telegram.messenger.AndroidUtilities.leftBaseline;
        r7 = r7 + 2;
        r7 = (float) r7;
        r7 = org.telegram.messenger.AndroidUtilities.dp(r7);
        r2 = r2 - r7;
        r7 = org.telegram.ui.ActionBar.Theme.dialogs_botDrawable;
        r7 = r7.getIntrinsicWidth();
        r2 = r2 - r7;
        r0.nameLockLeft = r2;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r0.nameLeft = r2;
    L_0x016f:
        r2 = org.telegram.messenger.AndroidUtilities.dp(r3);
        r0.nameLockTop = r2;
        goto L_0x017e;
    L_0x0176:
        r2 = NUM; // 0x41a80000 float:21.0 double:5.442276803E-315;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r2);
        r0.nameLockTop = r2;
    L_0x017e:
        r2 = r0.user;
        r2 = r2.verified;
        r0.drawCheck = r2;
    L_0x0184:
        r2 = r0.currentName;
        if (r2 == 0) goto L_0x0189;
    L_0x0188:
        goto L_0x01a1;
    L_0x0189:
        r2 = r0.chat;
        if (r2 == 0) goto L_0x0190;
    L_0x018d:
        r2 = r2.title;
        goto L_0x019b;
    L_0x0190:
        r2 = r0.user;
        if (r2 == 0) goto L_0x0199;
    L_0x0194:
        r2 = org.telegram.messenger.UserObject.getUserName(r2);
        goto L_0x019b;
    L_0x0199:
        r2 = "";
    L_0x019b:
        r3 = 10;
        r2 = r2.replace(r3, r4);
    L_0x01a1:
        r3 = r2.length();
        if (r3 != 0) goto L_0x01dc;
    L_0x01a7:
        r2 = r0.user;
        if (r2 == 0) goto L_0x01d3;
    L_0x01ab:
        r2 = r2.phone;
        if (r2 == 0) goto L_0x01d3;
    L_0x01af:
        r2 = r2.length();
        if (r2 == 0) goto L_0x01d3;
    L_0x01b5:
        r2 = org.telegram.PhoneFormat.PhoneFormat.getInstance();
        r3 = new java.lang.StringBuilder;
        r3.<init>();
        r4 = "+";
        r3.append(r4);
        r4 = r0.user;
        r4 = r4.phone;
        r3.append(r4);
        r3 = r3.toString();
        r2 = r2.format(r3);
        goto L_0x01dc;
    L_0x01d3:
        r2 = NUM; // 0x7f0d050a float:1.8744731E38 double:1.053130415E-314;
        r3 = "HiddenName";
        r2 = org.telegram.messenger.LocaleController.getString(r3, r2);
    L_0x01dc:
        r3 = r0.encryptedChat;
        if (r3 == 0) goto L_0x01e3;
    L_0x01e0:
        r3 = org.telegram.ui.ActionBar.Theme.dialogs_searchNameEncryptedPaint;
        goto L_0x01e5;
    L_0x01e3:
        r3 = org.telegram.ui.ActionBar.Theme.dialogs_searchNamePaint;
    L_0x01e5:
        r9 = r3;
        r3 = org.telegram.messenger.LocaleController.isRTL;
        if (r3 != 0) goto L_0x01fb;
    L_0x01ea:
        r3 = r25.getMeasuredWidth();
        r4 = r0.nameLeft;
        r3 = r3 - r4;
        r4 = NUM; // 0x41600000 float:14.0 double:5.41896386E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r3 = r3 - r4;
        r0.nameWidth = r3;
        goto L_0x020c;
    L_0x01fb:
        r3 = r25.getMeasuredWidth();
        r4 = r0.nameLeft;
        r3 = r3 - r4;
        r4 = org.telegram.messenger.AndroidUtilities.leftBaseline;
        r4 = (float) r4;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r3 = r3 - r4;
        r0.nameWidth = r3;
    L_0x020c:
        r4 = r0.drawNameLock;
        r7 = NUM; // 0x40CLASSNAME float:6.0 double:5.367157323E-315;
        if (r4 == 0) goto L_0x0223;
    L_0x0212:
        r4 = r0.nameWidth;
        r7 = org.telegram.messenger.AndroidUtilities.dp(r7);
        r8 = org.telegram.ui.ActionBar.Theme.dialogs_lockDrawable;
        r8 = r8.getIntrinsicWidth();
        r7 = r7 + r8;
        r4 = r4 - r7;
        r0.nameWidth = r4;
        goto L_0x0261;
    L_0x0223:
        r4 = r0.drawNameBroadcast;
        if (r4 == 0) goto L_0x0238;
    L_0x0227:
        r4 = r0.nameWidth;
        r7 = org.telegram.messenger.AndroidUtilities.dp(r7);
        r8 = org.telegram.ui.ActionBar.Theme.dialogs_broadcastDrawable;
        r8 = r8.getIntrinsicWidth();
        r7 = r7 + r8;
        r4 = r4 - r7;
        r0.nameWidth = r4;
        goto L_0x0261;
    L_0x0238:
        r4 = r0.drawNameGroup;
        if (r4 == 0) goto L_0x024d;
    L_0x023c:
        r4 = r0.nameWidth;
        r7 = org.telegram.messenger.AndroidUtilities.dp(r7);
        r8 = org.telegram.ui.ActionBar.Theme.dialogs_groupDrawable;
        r8 = r8.getIntrinsicWidth();
        r7 = r7 + r8;
        r4 = r4 - r7;
        r0.nameWidth = r4;
        goto L_0x0261;
    L_0x024d:
        r4 = r0.drawNameBot;
        if (r4 == 0) goto L_0x0261;
    L_0x0251:
        r4 = r0.nameWidth;
        r7 = org.telegram.messenger.AndroidUtilities.dp(r7);
        r8 = org.telegram.ui.ActionBar.Theme.dialogs_botDrawable;
        r8 = r8.getIntrinsicWidth();
        r7 = r7 + r8;
        r4 = r4 - r7;
        r0.nameWidth = r4;
    L_0x0261:
        r4 = r0.nameWidth;
        r7 = r25.getPaddingLeft();
        r8 = r25.getPaddingRight();
        r7 = r7 + r8;
        r4 = r4 - r7;
        r0.nameWidth = r4;
        r4 = r25.getPaddingLeft();
        r7 = r25.getPaddingRight();
        r4 = r4 + r7;
        r3 = r3 - r4;
        r4 = r0.drawCount;
        r15 = NUM; // 0x41400000 float:12.0 double:5.408602553E-315;
        r16 = NUM; // 0x41980000 float:19.0 double:5.43709615E-315;
        r14 = 0;
        if (r4 == 0) goto L_0x0309;
    L_0x0282:
        r4 = r0.currentAccount;
        r4 = org.telegram.messenger.MessagesController.getInstance(r4);
        r4 = r4.dialogs_dict;
        r7 = r0.dialog_id;
        r4 = r4.get(r7);
        r4 = (org.telegram.tgnet.TLRPC.Dialog) r4;
        if (r4 == 0) goto L_0x0304;
    L_0x0294:
        r4 = r4.unread_count;
        if (r4 == 0) goto L_0x0304;
    L_0x0298:
        r0.lastUnreadCount = r4;
        r5 = new java.lang.Object[r5];
        r4 = java.lang.Integer.valueOf(r4);
        r5[r1] = r4;
        r4 = "%d";
        r4 = java.lang.String.format(r4, r5);
        r5 = org.telegram.messenger.AndroidUtilities.dp(r15);
        r7 = org.telegram.ui.ActionBar.Theme.dialogs_countTextPaint;
        r7 = r7.measureText(r4);
        r7 = (double) r7;
        r7 = java.lang.Math.ceil(r7);
        r7 = (int) r7;
        r5 = java.lang.Math.max(r5, r7);
        r0.countWidth = r5;
        r5 = new android.text.StaticLayout;
        r19 = org.telegram.ui.ActionBar.Theme.dialogs_countTextPaint;
        r7 = r0.countWidth;
        r21 = android.text.Layout.Alignment.ALIGN_CENTER;
        r22 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r23 = 0;
        r24 = 0;
        r17 = r5;
        r18 = r4;
        r20 = r7;
        r17.<init>(r18, r19, r20, r21, r22, r23, r24);
        r0.countLayout = r5;
        r4 = r0.countWidth;
        r5 = NUM; // 0x41900000 float:18.0 double:5.43450582E-315;
        r5 = org.telegram.messenger.AndroidUtilities.dp(r5);
        r4 = r4 + r5;
        r5 = r0.nameWidth;
        r5 = r5 - r4;
        r0.nameWidth = r5;
        r5 = org.telegram.messenger.LocaleController.isRTL;
        if (r5 != 0) goto L_0x02f8;
    L_0x02e9:
        r4 = r25.getMeasuredWidth();
        r5 = r0.countWidth;
        r4 = r4 - r5;
        r5 = org.telegram.messenger.AndroidUtilities.dp(r16);
        r4 = r4 - r5;
        r0.countLeft = r4;
        goto L_0x030d;
    L_0x02f8:
        r5 = org.telegram.messenger.AndroidUtilities.dp(r16);
        r0.countLeft = r5;
        r5 = r0.nameLeft;
        r5 = r5 + r4;
        r0.nameLeft = r5;
        goto L_0x030d;
    L_0x0304:
        r0.lastUnreadCount = r1;
        r0.countLayout = r14;
        goto L_0x030d;
    L_0x0309:
        r0.lastUnreadCount = r1;
        r0.countLayout = r14;
    L_0x030d:
        r4 = r0.nameWidth;
        r5 = org.telegram.messenger.AndroidUtilities.dp(r15);
        r4 = r4 - r5;
        r4 = (float) r4;
        r5 = android.text.TextUtils.TruncateAt.END;
        r8 = android.text.TextUtils.ellipsize(r2, r9, r4, r5);
        r2 = new android.text.StaticLayout;
        r10 = r0.nameWidth;
        r11 = android.text.Layout.Alignment.ALIGN_NORMAL;
        r12 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r13 = 0;
        r4 = 0;
        r7 = r2;
        r5 = r14;
        r14 = r4;
        r7.<init>(r8, r9, r10, r11, r12, r13, r14);
        r0.nameLayout = r2;
        r2 = org.telegram.ui.ActionBar.Theme.dialogs_offlinePaint;
        r4 = org.telegram.messenger.LocaleController.isRTL;
        if (r4 != 0) goto L_0x033d;
    L_0x0333:
        r4 = org.telegram.messenger.AndroidUtilities.leftBaseline;
        r4 = (float) r4;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r0.statusLeft = r4;
        goto L_0x0343;
    L_0x033d:
        r4 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r0.statusLeft = r4;
    L_0x0343:
        r4 = r0.chat;
        if (r4 == 0) goto L_0x03bc;
    L_0x0347:
        r7 = r0.subLabel;
        if (r7 == 0) goto L_0x034d;
    L_0x034b:
        goto L_0x03bc;
    L_0x034d:
        if (r4 == 0) goto L_0x03b3;
    L_0x034f:
        r4 = org.telegram.messenger.ChatObject.isChannel(r4);
        if (r4 == 0) goto L_0x037f;
    L_0x0355:
        r4 = r0.chat;
        r7 = r4.megagroup;
        if (r7 != 0) goto L_0x037f;
    L_0x035b:
        r4 = r4.username;
        r4 = android.text.TextUtils.isEmpty(r4);
        if (r4 == 0) goto L_0x0371;
    L_0x0363:
        r4 = NUM; // 0x7f0d025a float:1.8743336E38 double:1.053130075E-314;
        r7 = "ChannelPrivate";
        r4 = org.telegram.messenger.LocaleController.getString(r7, r4);
        r14 = r4.toLowerCase();
        goto L_0x03b4;
    L_0x0371:
        r4 = NUM; // 0x7f0d025d float:1.8743342E38 double:1.0531300765E-314;
        r7 = "ChannelPublic";
        r4 = org.telegram.messenger.LocaleController.getString(r7, r4);
        r14 = r4.toLowerCase();
        goto L_0x03b4;
    L_0x037f:
        r4 = r0.chat;
        r7 = r4.has_geo;
        if (r7 == 0) goto L_0x038f;
    L_0x0385:
        r4 = NUM; // 0x7f0d05d7 float:1.8745147E38 double:1.053130516E-314;
        r7 = "MegaLocation";
        r14 = org.telegram.messenger.LocaleController.getString(r7, r4);
        goto L_0x03b4;
    L_0x038f:
        r4 = r4.username;
        r4 = android.text.TextUtils.isEmpty(r4);
        if (r4 == 0) goto L_0x03a5;
    L_0x0397:
        r4 = NUM; // 0x7f0d05d8 float:1.874515E38 double:1.0531305167E-314;
        r7 = "MegaPrivate";
        r4 = org.telegram.messenger.LocaleController.getString(r7, r4);
        r14 = r4.toLowerCase();
        goto L_0x03b4;
    L_0x03a5:
        r4 = NUM; // 0x7f0d05db float:1.8745155E38 double:1.053130518E-314;
        r7 = "MegaPublic";
        r4 = org.telegram.messenger.LocaleController.getString(r7, r4);
        r14 = r4.toLowerCase();
        goto L_0x03b4;
    L_0x03b3:
        r14 = r5;
    L_0x03b4:
        r4 = org.telegram.messenger.AndroidUtilities.dp(r16);
        r0.nameTop = r4;
        goto L_0x0443;
    L_0x03bc:
        r14 = r0.subLabel;
        if (r14 == 0) goto L_0x03c2;
    L_0x03c0:
        goto L_0x0436;
    L_0x03c2:
        r4 = r0.user;
        if (r4 == 0) goto L_0x0435;
    L_0x03c6:
        r4 = org.telegram.messenger.MessagesController.isSupportUser(r4);
        if (r4 == 0) goto L_0x03d6;
    L_0x03cc:
        r4 = NUM; // 0x7f0d0a2e float:1.87474E38 double:1.053131065E-314;
        r7 = "SupportStatus";
        r14 = org.telegram.messenger.LocaleController.getString(r7, r4);
        goto L_0x0436;
    L_0x03d6:
        r4 = r0.user;
        r7 = r4.bot;
        if (r7 == 0) goto L_0x03e6;
    L_0x03dc:
        r4 = NUM; // 0x7f0d01cb float:1.8743046E38 double:1.0531300043E-314;
        r7 = "Bot";
        r14 = org.telegram.messenger.LocaleController.getString(r7, r4);
        goto L_0x0436;
    L_0x03e6:
        r7 = r4.id;
        r8 = 333000; // 0x514c8 float:4.66632E-40 double:1.64524E-318;
        if (r7 == r8) goto L_0x042b;
    L_0x03ed:
        r8 = 777000; // 0xbdb28 float:1.088809E-39 double:3.83889E-318;
        if (r7 != r8) goto L_0x03f3;
    L_0x03f2:
        goto L_0x042b;
    L_0x03f3:
        r7 = r0.currentAccount;
        r14 = org.telegram.messenger.LocaleController.formatUserStatus(r7, r4);
        r4 = r0.user;
        if (r4 == 0) goto L_0x0436;
    L_0x03fd:
        r4 = r4.id;
        r7 = r0.currentAccount;
        r7 = org.telegram.messenger.UserConfig.getInstance(r7);
        r7 = r7.getClientUserId();
        if (r4 == r7) goto L_0x041f;
    L_0x040b:
        r4 = r0.user;
        r4 = r4.status;
        if (r4 == 0) goto L_0x0436;
    L_0x0411:
        r4 = r4.expires;
        r7 = r0.currentAccount;
        r7 = org.telegram.tgnet.ConnectionsManager.getInstance(r7);
        r7 = r7.getCurrentTime();
        if (r4 <= r7) goto L_0x0436;
    L_0x041f:
        r2 = org.telegram.ui.ActionBar.Theme.dialogs_onlinePaint;
        r4 = NUM; // 0x7f0d0703 float:1.8745755E38 double:1.0531306644E-314;
        r7 = "Online";
        r14 = org.telegram.messenger.LocaleController.getString(r7, r4);
        goto L_0x0436;
    L_0x042b:
        r4 = NUM; // 0x7f0d099b float:1.8747102E38 double:1.0531309925E-314;
        r7 = "ServiceNotifications";
        r14 = org.telegram.messenger.LocaleController.getString(r7, r4);
        goto L_0x0436;
    L_0x0435:
        r14 = r5;
    L_0x0436:
        r4 = r0.savedMessages;
        if (r4 == 0) goto L_0x0443;
    L_0x043a:
        r4 = org.telegram.messenger.AndroidUtilities.dp(r16);
        r0.nameTop = r4;
        r12 = r2;
        r14 = r5;
        goto L_0x0444;
    L_0x0443:
        r12 = r2;
    L_0x0444:
        r2 = android.text.TextUtils.isEmpty(r14);
        if (r2 != 0) goto L_0x047c;
    L_0x044a:
        r2 = org.telegram.messenger.AndroidUtilities.dp(r15);
        r2 = r3 - r2;
        r2 = (float) r2;
        r4 = android.text.TextUtils.TruncateAt.END;
        r11 = android.text.TextUtils.ellipsize(r14, r12, r2, r4);
        r2 = new android.text.StaticLayout;
        r14 = android.text.Layout.Alignment.ALIGN_NORMAL;
        r15 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r16 = 0;
        r17 = 0;
        r10 = r2;
        r13 = r3;
        r10.<init>(r11, r12, r13, r14, r15, r16, r17);
        r0.statusLayout = r2;
        r2 = NUM; // 0x41100000 float:9.0 double:5.39306059E-315;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r2);
        r0.nameTop = r2;
        r2 = r0.nameLockTop;
        r4 = NUM; // 0x41200000 float:10.0 double:5.398241246E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r2 = r2 - r4;
        r0.nameLockTop = r2;
        goto L_0x047e;
    L_0x047c:
        r0.statusLayout = r5;
    L_0x047e:
        r2 = org.telegram.messenger.LocaleController.isRTL;
        if (r2 == 0) goto L_0x0493;
    L_0x0482:
        r2 = r25.getMeasuredWidth();
        r4 = NUM; // 0x42640000 float:57.0 double:5.503149485E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r2 = r2 - r4;
        r4 = r25.getPaddingRight();
        r2 = r2 - r4;
        goto L_0x049c;
    L_0x0493:
        r2 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = r25.getPaddingLeft();
        r2 = r2 + r4;
    L_0x049c:
        r4 = r0.avatarImage;
        r5 = NUM; // 0x40e00000 float:7.0 double:5.37751863E-315;
        r5 = org.telegram.messenger.AndroidUtilities.dp(r5);
        r6 = NUM; // 0x42380000 float:46.0 double:5.488902687E-315;
        r7 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4.setImageCoords(r2, r5, r7, r6);
        r2 = org.telegram.messenger.LocaleController.isRTL;
        if (r2 == 0) goto L_0x051c;
    L_0x04b5:
        r2 = r0.nameLayout;
        r2 = r2.getLineCount();
        r4 = 0;
        if (r2 <= 0) goto L_0x04e9;
    L_0x04be:
        r2 = r0.nameLayout;
        r2 = r2.getLineLeft(r1);
        r2 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1));
        if (r2 != 0) goto L_0x04e9;
    L_0x04c8:
        r2 = r0.nameLayout;
        r2 = r2.getLineWidth(r1);
        r5 = (double) r2;
        r5 = java.lang.Math.ceil(r5);
        r2 = r0.nameWidth;
        r7 = (double) r2;
        r9 = (r5 > r7 ? 1 : (r5 == r7 ? 0 : -1));
        if (r9 >= 0) goto L_0x04e9;
    L_0x04da:
        r7 = r0.nameLeft;
        r7 = (double) r7;
        r9 = (double) r2;
        java.lang.Double.isNaN(r9);
        r9 = r9 - r5;
        java.lang.Double.isNaN(r7);
        r7 = r7 + r9;
        r2 = (int) r7;
        r0.nameLeft = r2;
    L_0x04e9:
        r2 = r0.statusLayout;
        if (r2 == 0) goto L_0x0585;
    L_0x04ed:
        r2 = r2.getLineCount();
        if (r2 <= 0) goto L_0x0585;
    L_0x04f3:
        r2 = r0.statusLayout;
        r2 = r2.getLineLeft(r1);
        r2 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1));
        if (r2 != 0) goto L_0x0585;
    L_0x04fd:
        r2 = r0.statusLayout;
        r1 = r2.getLineWidth(r1);
        r1 = (double) r1;
        r1 = java.lang.Math.ceil(r1);
        r3 = (double) r3;
        r5 = (r1 > r3 ? 1 : (r1 == r3 ? 0 : -1));
        if (r5 >= 0) goto L_0x0585;
    L_0x050d:
        r5 = r0.statusLeft;
        r5 = (double) r5;
        java.lang.Double.isNaN(r3);
        r3 = r3 - r1;
        java.lang.Double.isNaN(r5);
        r5 = r5 + r3;
        r1 = (int) r5;
        r0.statusLeft = r1;
        goto L_0x0585;
    L_0x051c:
        r2 = r0.nameLayout;
        r2 = r2.getLineCount();
        if (r2 <= 0) goto L_0x0552;
    L_0x0524:
        r2 = r0.nameLayout;
        r2 = r2.getLineRight(r1);
        r4 = r0.nameWidth;
        r4 = (float) r4;
        r2 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1));
        if (r2 != 0) goto L_0x0552;
    L_0x0531:
        r2 = r0.nameLayout;
        r2 = r2.getLineWidth(r1);
        r4 = (double) r2;
        r4 = java.lang.Math.ceil(r4);
        r2 = r0.nameWidth;
        r6 = (double) r2;
        r8 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1));
        if (r8 >= 0) goto L_0x0552;
    L_0x0543:
        r6 = r0.nameLeft;
        r6 = (double) r6;
        r8 = (double) r2;
        java.lang.Double.isNaN(r8);
        r8 = r8 - r4;
        java.lang.Double.isNaN(r6);
        r6 = r6 - r8;
        r2 = (int) r6;
        r0.nameLeft = r2;
    L_0x0552:
        r2 = r0.statusLayout;
        if (r2 == 0) goto L_0x0585;
    L_0x0556:
        r2 = r2.getLineCount();
        if (r2 <= 0) goto L_0x0585;
    L_0x055c:
        r2 = r0.statusLayout;
        r2 = r2.getLineRight(r1);
        r4 = (float) r3;
        r2 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1));
        if (r2 != 0) goto L_0x0585;
    L_0x0567:
        r2 = r0.statusLayout;
        r1 = r2.getLineWidth(r1);
        r1 = (double) r1;
        r1 = java.lang.Math.ceil(r1);
        r3 = (double) r3;
        r5 = (r1 > r3 ? 1 : (r1 == r3 ? 0 : -1));
        if (r5 >= 0) goto L_0x0585;
    L_0x0577:
        r5 = r0.statusLeft;
        r5 = (double) r5;
        java.lang.Double.isNaN(r3);
        r3 = r3 - r1;
        java.lang.Double.isNaN(r5);
        r5 = r5 - r3;
        r1 = (int) r5;
        r0.statusLeft = r1;
    L_0x0585:
        r1 = r0.nameLeft;
        r2 = r25.getPaddingLeft();
        r1 = r1 + r2;
        r0.nameLeft = r1;
        r1 = r0.statusLeft;
        r2 = r25.getPaddingLeft();
        r1 = r1 + r2;
        r0.statusLeft = r1;
        r1 = r0.nameLockLeft;
        r2 = r25.getPaddingLeft();
        r1 = r1 + r2;
        r0.nameLockLeft = r1;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.ProfileSearchCell.buildLayout():void");
    }

    /* JADX WARNING: Removed duplicated region for block: B:45:0x00b3  */
    /* JADX WARNING: Removed duplicated region for block: B:64:0x00ee  */
    /* JADX WARNING: Removed duplicated region for block: B:63:0x00d6  */
    /* JADX WARNING: Removed duplicated region for block: B:67:0x00fa  */
    /* JADX WARNING: Removed duplicated region for block: B:79:0x0120 A:{RETURN} */
    /* JADX WARNING: Missing block: B:37:0x00a4, code skipped:
            if (r0.local_id != r1.local_id) goto L_0x00a6;
     */
    public void update(int r13) {
        /*
        r12 = this;
        r0 = r12.user;
        r1 = 0;
        r2 = 1;
        r3 = 0;
        if (r0 == 0) goto L_0x003e;
    L_0x0007:
        r4 = r12.avatarDrawable;
        r4.setInfo(r0);
        r0 = r12.savedMessages;
        if (r0 == 0) goto L_0x0022;
    L_0x0010:
        r0 = r12.avatarDrawable;
        r0.setAvatarType(r2);
        r4 = r12.avatarImage;
        r5 = 0;
        r6 = 0;
        r7 = r12.avatarDrawable;
        r8 = 0;
        r9 = 0;
        r10 = 0;
        r4.setImage(r5, r6, r7, r8, r9, r10);
        goto L_0x0074;
    L_0x0022:
        r0 = r12.user;
        r0 = r0.photo;
        if (r0 == 0) goto L_0x002a;
    L_0x0028:
        r1 = r0.photo_small;
    L_0x002a:
        r4 = r12.avatarImage;
        r0 = r12.user;
        r5 = org.telegram.messenger.ImageLocation.getForUser(r0, r3);
        r7 = r12.avatarDrawable;
        r8 = 0;
        r9 = r12.user;
        r10 = 0;
        r6 = "50_50";
        r4.setImage(r5, r6, r7, r8, r9, r10);
        goto L_0x0074;
    L_0x003e:
        r0 = r12.chat;
        if (r0 == 0) goto L_0x0063;
    L_0x0042:
        r0 = r0.photo;
        if (r0 == 0) goto L_0x0048;
    L_0x0046:
        r1 = r0.photo_small;
    L_0x0048:
        r0 = r12.avatarDrawable;
        r4 = r12.chat;
        r0.setInfo(r4);
        r5 = r12.avatarImage;
        r0 = r12.chat;
        r6 = org.telegram.messenger.ImageLocation.getForChat(r0, r3);
        r8 = r12.avatarDrawable;
        r9 = 0;
        r10 = r12.chat;
        r11 = 0;
        r7 = "50_50";
        r5.setImage(r6, r7, r8, r9, r10, r11);
        goto L_0x0074;
    L_0x0063:
        r0 = r12.avatarDrawable;
        r0.setInfo(r3, r1, r1);
        r4 = r12.avatarImage;
        r5 = 0;
        r6 = 0;
        r7 = r12.avatarDrawable;
        r8 = 0;
        r9 = 0;
        r10 = 0;
        r4.setImage(r5, r6, r7, r8, r9, r10);
    L_0x0074:
        if (r13 == 0) goto L_0x0121;
    L_0x0076:
        r0 = r13 & 2;
        if (r0 == 0) goto L_0x007e;
    L_0x007a:
        r0 = r12.user;
        if (r0 != 0) goto L_0x0086;
    L_0x007e:
        r0 = r13 & 8;
        if (r0 == 0) goto L_0x00a8;
    L_0x0082:
        r0 = r12.chat;
        if (r0 == 0) goto L_0x00a8;
    L_0x0086:
        r0 = r12.lastAvatar;
        if (r0 == 0) goto L_0x008c;
    L_0x008a:
        if (r1 == 0) goto L_0x00a6;
    L_0x008c:
        r0 = r12.lastAvatar;
        if (r0 != 0) goto L_0x0092;
    L_0x0090:
        if (r1 != 0) goto L_0x00a6;
    L_0x0092:
        r0 = r12.lastAvatar;
        if (r0 == 0) goto L_0x00a8;
    L_0x0096:
        if (r1 == 0) goto L_0x00a8;
    L_0x0098:
        r4 = r0.volume_id;
        r6 = r1.volume_id;
        r8 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1));
        if (r8 != 0) goto L_0x00a6;
    L_0x00a0:
        r0 = r0.local_id;
        r4 = r1.local_id;
        if (r0 == r4) goto L_0x00a8;
    L_0x00a6:
        r0 = 1;
        goto L_0x00a9;
    L_0x00a8:
        r0 = 0;
    L_0x00a9:
        if (r0 != 0) goto L_0x00c0;
    L_0x00ab:
        r4 = r13 & 4;
        if (r4 == 0) goto L_0x00c0;
    L_0x00af:
        r4 = r12.user;
        if (r4 == 0) goto L_0x00c0;
    L_0x00b3:
        r4 = r4.status;
        if (r4 == 0) goto L_0x00ba;
    L_0x00b7:
        r4 = r4.expires;
        goto L_0x00bb;
    L_0x00ba:
        r4 = 0;
    L_0x00bb:
        r5 = r12.lastStatus;
        if (r4 == r5) goto L_0x00c0;
    L_0x00bf:
        r0 = 1;
    L_0x00c0:
        if (r0 != 0) goto L_0x00ca;
    L_0x00c2:
        r4 = r13 & 1;
        if (r4 == 0) goto L_0x00ca;
    L_0x00c6:
        r4 = r12.user;
        if (r4 != 0) goto L_0x00d2;
    L_0x00ca:
        r4 = r13 & 16;
        if (r4 == 0) goto L_0x00fb;
    L_0x00ce:
        r4 = r12.chat;
        if (r4 == 0) goto L_0x00fb;
    L_0x00d2:
        r4 = r12.user;
        if (r4 == 0) goto L_0x00ee;
    L_0x00d6:
        r4 = new java.lang.StringBuilder;
        r4.<init>();
        r5 = r12.user;
        r5 = r5.first_name;
        r4.append(r5);
        r5 = r12.user;
        r5 = r5.last_name;
        r4.append(r5);
        r4 = r4.toString();
        goto L_0x00f2;
    L_0x00ee:
        r4 = r12.chat;
        r4 = r4.title;
    L_0x00f2:
        r5 = r12.lastName;
        r4 = r4.equals(r5);
        if (r4 != 0) goto L_0x00fb;
    L_0x00fa:
        r0 = 1;
    L_0x00fb:
        if (r0 != 0) goto L_0x011e;
    L_0x00fd:
        r4 = r12.drawCount;
        if (r4 == 0) goto L_0x011e;
    L_0x0101:
        r13 = r13 & 256;
        if (r13 == 0) goto L_0x011e;
    L_0x0105:
        r13 = r12.currentAccount;
        r13 = org.telegram.messenger.MessagesController.getInstance(r13);
        r13 = r13.dialogs_dict;
        r4 = r12.dialog_id;
        r13 = r13.get(r4);
        r13 = (org.telegram.tgnet.TLRPC.Dialog) r13;
        if (r13 == 0) goto L_0x011e;
    L_0x0117:
        r13 = r13.unread_count;
        r4 = r12.lastUnreadCount;
        if (r13 == r4) goto L_0x011e;
    L_0x011d:
        r0 = 1;
    L_0x011e:
        if (r0 != 0) goto L_0x0121;
    L_0x0120:
        return;
    L_0x0121:
        r13 = r12.user;
        if (r13 == 0) goto L_0x014a;
    L_0x0125:
        r13 = r13.status;
        if (r13 == 0) goto L_0x012e;
    L_0x0129:
        r13 = r13.expires;
        r12.lastStatus = r13;
        goto L_0x0130;
    L_0x012e:
        r12.lastStatus = r3;
    L_0x0130:
        r13 = new java.lang.StringBuilder;
        r13.<init>();
        r0 = r12.user;
        r0 = r0.first_name;
        r13.append(r0);
        r0 = r12.user;
        r0 = r0.last_name;
        r13.append(r0);
        r13 = r13.toString();
        r12.lastName = r13;
        goto L_0x0152;
    L_0x014a:
        r13 = r12.chat;
        if (r13 == 0) goto L_0x0152;
    L_0x014e:
        r13 = r13.title;
        r12.lastName = r13;
    L_0x0152:
        r12.lastAvatar = r1;
        r13 = r12.getMeasuredWidth();
        if (r13 != 0) goto L_0x0165;
    L_0x015a:
        r13 = r12.getMeasuredHeight();
        if (r13 == 0) goto L_0x0161;
    L_0x0160:
        goto L_0x0165;
    L_0x0161:
        r12.requestLayout();
        goto L_0x0168;
    L_0x0165:
        r12.buildLayout();
    L_0x0168:
        r12.postInvalidate();
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.ProfileSearchCell.update(int):void");
    }

    /* Access modifiers changed, original: protected */
    public void onDraw(Canvas canvas) {
        if (this.user != null || this.chat != null || this.encryptedChat != null) {
            int lineRight;
            if (this.useSeparator) {
                if (LocaleController.isRTL) {
                    canvas.drawLine(0.0f, (float) (getMeasuredHeight() - 1), (float) (getMeasuredWidth() - AndroidUtilities.dp((float) AndroidUtilities.leftBaseline)), (float) (getMeasuredHeight() - 1), Theme.dividerPaint);
                } else {
                    canvas.drawLine((float) AndroidUtilities.dp((float) AndroidUtilities.leftBaseline), (float) (getMeasuredHeight() - 1), (float) getMeasuredWidth(), (float) (getMeasuredHeight() - 1), Theme.dividerPaint);
                }
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
                canvas.translate((float) this.nameLeft, (float) this.nameTop);
                this.nameLayout.draw(canvas);
                canvas.restore();
                if (this.drawCheck) {
                    if (!LocaleController.isRTL) {
                        lineRight = (int) ((((float) this.nameLeft) + this.nameLayout.getLineRight(0)) + ((float) AndroidUtilities.dp(6.0f)));
                    } else if (this.nameLayout.getLineLeft(0) == 0.0f) {
                        lineRight = (this.nameLeft - AndroidUtilities.dp(6.0f)) - Theme.dialogs_verifiedDrawable.getIntrinsicWidth();
                    } else {
                        double d = (double) (this.nameLeft + this.nameWidth);
                        double ceil = Math.ceil((double) this.nameLayout.getLineWidth(0));
                        Double.isNaN(d);
                        d -= ceil;
                        double dp = (double) AndroidUtilities.dp(6.0f);
                        Double.isNaN(dp);
                        d -= dp;
                        dp = (double) Theme.dialogs_verifiedDrawable.getIntrinsicWidth();
                        Double.isNaN(dp);
                        lineRight = (int) (d - dp);
                    }
                    BaseCell.setDrawableBounds(Theme.dialogs_verifiedDrawable, lineRight, this.nameTop + AndroidUtilities.dp(3.0f));
                    BaseCell.setDrawableBounds(Theme.dialogs_verifiedCheckDrawable, lineRight, this.nameTop + AndroidUtilities.dp(3.0f));
                    Theme.dialogs_verifiedDrawable.draw(canvas);
                    Theme.dialogs_verifiedCheckDrawable.draw(canvas);
                }
            }
            if (this.statusLayout != null) {
                canvas.save();
                canvas.translate((float) (this.statusLeft + this.sublabelOffsetX), (float) (AndroidUtilities.dp(33.0f) + this.sublabelOffsetY));
                this.statusLayout.draw(canvas);
                canvas.restore();
            }
            if (this.countLayout != null) {
                lineRight = this.countLeft - AndroidUtilities.dp(5.5f);
                this.rect.set((float) lineRight, (float) this.countTop, (float) ((lineRight + this.countWidth) + AndroidUtilities.dp(11.0f)), (float) (this.countTop + AndroidUtilities.dp(23.0f)));
                RectF rectF = this.rect;
                float f = AndroidUtilities.density;
                canvas.drawRoundRect(rectF, f * 11.5f, f * 11.5f, MessagesController.getInstance(this.currentAccount).isDialogMuted(this.dialog_id) ? Theme.dialogs_countGrayPaint : Theme.dialogs_countPaint);
                canvas.save();
                canvas.translate((float) this.countLeft, (float) (this.countTop + AndroidUtilities.dp(4.0f)));
                this.countLayout.draw(canvas);
                canvas.restore();
            }
            this.avatarImage.draw(canvas);
        }
    }

    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
        super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
        StringBuilder stringBuilder = new StringBuilder();
        StaticLayout staticLayout = this.nameLayout;
        if (staticLayout != null) {
            stringBuilder.append(staticLayout.getText());
        }
        if (this.statusLayout != null) {
            if (stringBuilder.length() > 0) {
                stringBuilder.append(", ");
            }
            stringBuilder.append(this.statusLayout.getText());
        }
        accessibilityNodeInfo.setText(stringBuilder.toString());
    }
}
