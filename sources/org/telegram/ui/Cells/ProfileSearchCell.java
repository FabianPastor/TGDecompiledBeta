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
    private int countTop = AndroidUtilities.dp(25.0f);
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
    private StaticLayout onlineLayout;
    private int onlineLeft;
    private RectF rect = new RectF();
    private boolean savedMessages;
    private CharSequence subLabel;
    public boolean useSeparator;
    private User user;

    public ProfileSearchCell(Context context) {
        super(context);
        this.avatarImage.setRoundRadius(AndroidUtilities.dp(26.0f));
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
        setMeasuredDimension(MeasureSpec.getSize(i), AndroidUtilities.dp(72.0f));
    }

    /* Access modifiers changed, original: protected */
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        if (!(this.user == null && this.chat == null && this.encryptedChat == null) && z) {
            buildLayout();
        }
    }

    /* JADX WARNING: Missing block: B:130:0x03aa, code skipped:
            if (r5.expires > org.telegram.tgnet.ConnectionsManager.getInstance(r0.currentAccount).getCurrentTime()) goto L_0x03ac;
     */
    public void buildLayout() {
        /*
        r27 = this;
        r0 = r27;
        r1 = 0;
        r0.drawNameBroadcast = r1;
        r0.drawNameLock = r1;
        r0.drawNameGroup = r1;
        r0.drawCheck = r1;
        r0.drawNameBot = r1;
        r2 = r0.encryptedChat;
        r3 = NUM; // 0x41840000 float:16.5 double:5.43062033E-315;
        r4 = 32;
        r5 = NUM; // 0x41300000 float:11.0 double:5.4034219E-315;
        r6 = 1;
        if (r2 == 0) goto L_0x0065;
    L_0x0018:
        r0.drawNameLock = r6;
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
        r2 = r27.getMeasuredWidth();
        r7 = org.telegram.messenger.AndroidUtilities.leftBaseline;
        r7 = r7 + 2;
        r7 = (float) r7;
        r7 = org.telegram.messenger.AndroidUtilities.dp(r7);
        r2 = r2 - r7;
        r7 = org.telegram.ui.ActionBar.Theme.dialogs_lockDrawable;
        r7 = r7.getIntrinsicWidth();
        r2 = r2 - r7;
        r0.nameLockLeft = r2;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r5);
        r0.nameLeft = r2;
    L_0x005d:
        r2 = org.telegram.messenger.AndroidUtilities.dp(r3);
        r0.nameLockTop = r2;
        goto L_0x0172;
    L_0x0065:
        r2 = r0.chat;
        if (r2 == 0) goto L_0x00f5;
    L_0x0069:
        r3 = r2.id;
        r7 = NUM; // 0x41e40000 float:28.5 double:5.461704254E-315;
        if (r3 >= 0) goto L_0x007e;
    L_0x006f:
        r2 = org.telegram.messenger.AndroidUtilities.makeBroadcastId(r3);
        r0.dialog_id = r2;
        r0.drawNameBroadcast = r6;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r7);
        r0.nameLockTop = r2;
        goto L_0x00a1;
    L_0x007e:
        r3 = -r3;
        r8 = (long) r3;
        r0.dialog_id = r8;
        r2 = org.telegram.messenger.ChatObject.isChannel(r2);
        if (r2 == 0) goto L_0x0097;
    L_0x0088:
        r2 = r0.chat;
        r2 = r2.megagroup;
        if (r2 != 0) goto L_0x0097;
    L_0x008e:
        r0.drawNameBroadcast = r6;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r7);
        r0.nameLockTop = r2;
        goto L_0x00a1;
    L_0x0097:
        r0.drawNameGroup = r6;
        r2 = NUM; // 0x41var_ float:30.0 double:5.465589745E-315;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r2);
        r0.nameLockTop = r2;
    L_0x00a1:
        r2 = r0.chat;
        r2 = r2.verified;
        r0.drawCheck = r2;
        r2 = org.telegram.messenger.LocaleController.isRTL;
        if (r2 != 0) goto L_0x00cf;
    L_0x00ab:
        r2 = org.telegram.messenger.AndroidUtilities.leftBaseline;
        r2 = (float) r2;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r2);
        r0.nameLockLeft = r2;
        r2 = org.telegram.messenger.AndroidUtilities.leftBaseline;
        r2 = r2 + 4;
        r2 = (float) r2;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r2);
        r3 = r0.drawNameGroup;
        if (r3 == 0) goto L_0x00c4;
    L_0x00c1:
        r3 = org.telegram.ui.ActionBar.Theme.dialogs_groupDrawable;
        goto L_0x00c6;
    L_0x00c4:
        r3 = org.telegram.ui.ActionBar.Theme.dialogs_broadcastDrawable;
    L_0x00c6:
        r3 = r3.getIntrinsicWidth();
        r2 = r2 + r3;
        r0.nameLeft = r2;
        goto L_0x0172;
    L_0x00cf:
        r2 = r27.getMeasuredWidth();
        r3 = org.telegram.messenger.AndroidUtilities.leftBaseline;
        r3 = r3 + 2;
        r3 = (float) r3;
        r3 = org.telegram.messenger.AndroidUtilities.dp(r3);
        r2 = r2 - r3;
        r3 = r0.drawNameGroup;
        if (r3 == 0) goto L_0x00e4;
    L_0x00e1:
        r3 = org.telegram.ui.ActionBar.Theme.dialogs_groupDrawable;
        goto L_0x00e6;
    L_0x00e4:
        r3 = org.telegram.ui.ActionBar.Theme.dialogs_broadcastDrawable;
    L_0x00e6:
        r3 = r3.getIntrinsicWidth();
        r2 = r2 - r3;
        r0.nameLockLeft = r2;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r5);
        r0.nameLeft = r2;
        goto L_0x0172;
    L_0x00f5:
        r2 = r0.user;
        if (r2 == 0) goto L_0x0172;
    L_0x00f9:
        r2 = r2.id;
        r7 = (long) r2;
        r0.dialog_id = r7;
        r2 = org.telegram.messenger.LocaleController.isRTL;
        if (r2 != 0) goto L_0x010c;
    L_0x0102:
        r2 = org.telegram.messenger.AndroidUtilities.leftBaseline;
        r2 = (float) r2;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r2);
        r0.nameLeft = r2;
        goto L_0x0112;
    L_0x010c:
        r2 = org.telegram.messenger.AndroidUtilities.dp(r5);
        r0.nameLeft = r2;
    L_0x0112:
        r2 = r0.user;
        r7 = r2.bot;
        if (r7 == 0) goto L_0x0164;
    L_0x0118:
        r2 = org.telegram.messenger.MessagesController.isSupportUser(r2);
        if (r2 != 0) goto L_0x0164;
    L_0x011e:
        r0.drawNameBot = r6;
        r2 = org.telegram.messenger.LocaleController.isRTL;
        if (r2 != 0) goto L_0x0140;
    L_0x0124:
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
        goto L_0x015d;
    L_0x0140:
        r2 = r27.getMeasuredWidth();
        r7 = org.telegram.messenger.AndroidUtilities.leftBaseline;
        r7 = r7 + 2;
        r7 = (float) r7;
        r7 = org.telegram.messenger.AndroidUtilities.dp(r7);
        r2 = r2 - r7;
        r7 = org.telegram.ui.ActionBar.Theme.dialogs_botDrawable;
        r7 = r7.getIntrinsicWidth();
        r2 = r2 - r7;
        r0.nameLockLeft = r2;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r5);
        r0.nameLeft = r2;
    L_0x015d:
        r2 = org.telegram.messenger.AndroidUtilities.dp(r3);
        r0.nameLockTop = r2;
        goto L_0x016c;
    L_0x0164:
        r2 = NUM; // 0x41880000 float:17.0 double:5.431915495E-315;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r2);
        r0.nameLockTop = r2;
    L_0x016c:
        r2 = r0.user;
        r2 = r2.verified;
        r0.drawCheck = r2;
    L_0x0172:
        r2 = r0.currentName;
        r3 = "";
        if (r2 == 0) goto L_0x0179;
    L_0x0178:
        goto L_0x0190;
    L_0x0179:
        r2 = r0.chat;
        if (r2 == 0) goto L_0x0180;
    L_0x017d:
        r2 = r2.title;
        goto L_0x018a;
    L_0x0180:
        r2 = r0.user;
        if (r2 == 0) goto L_0x0189;
    L_0x0184:
        r2 = org.telegram.messenger.UserObject.getUserName(r2);
        goto L_0x018a;
    L_0x0189:
        r2 = r3;
    L_0x018a:
        r7 = 10;
        r2 = r2.replace(r7, r4);
    L_0x0190:
        r4 = r2.length();
        if (r4 != 0) goto L_0x01cb;
    L_0x0196:
        r2 = r0.user;
        if (r2 == 0) goto L_0x01c2;
    L_0x019a:
        r2 = r2.phone;
        if (r2 == 0) goto L_0x01c2;
    L_0x019e:
        r2 = r2.length();
        if (r2 == 0) goto L_0x01c2;
    L_0x01a4:
        r2 = org.telegram.PhoneFormat.PhoneFormat.getInstance();
        r4 = new java.lang.StringBuilder;
        r4.<init>();
        r7 = "+";
        r4.append(r7);
        r7 = r0.user;
        r7 = r7.phone;
        r4.append(r7);
        r4 = r4.toString();
        r2 = r2.format(r4);
        goto L_0x01cb;
    L_0x01c2:
        r2 = NUM; // 0x7f0d04a7 float:1.874453E38 double:1.053130366E-314;
        r4 = "HiddenName";
        r2 = org.telegram.messenger.LocaleController.getString(r4, r2);
    L_0x01cb:
        r4 = r0.encryptedChat;
        if (r4 == 0) goto L_0x01d2;
    L_0x01cf:
        r4 = org.telegram.ui.ActionBar.Theme.dialogs_nameEncryptedPaint;
        goto L_0x01d4;
    L_0x01d2:
        r4 = org.telegram.ui.ActionBar.Theme.dialogs_namePaint;
    L_0x01d4:
        r9 = r4;
        r4 = org.telegram.messenger.LocaleController.isRTL;
        if (r4 != 0) goto L_0x01ea;
    L_0x01d9:
        r4 = r27.getMeasuredWidth();
        r7 = r0.nameLeft;
        r4 = r4 - r7;
        r7 = NUM; // 0x41600000 float:14.0 double:5.41896386E-315;
        r7 = org.telegram.messenger.AndroidUtilities.dp(r7);
        r4 = r4 - r7;
        r0.nameWidth = r4;
        goto L_0x01fb;
    L_0x01ea:
        r4 = r27.getMeasuredWidth();
        r7 = r0.nameLeft;
        r4 = r4 - r7;
        r7 = org.telegram.messenger.AndroidUtilities.leftBaseline;
        r7 = (float) r7;
        r7 = org.telegram.messenger.AndroidUtilities.dp(r7);
        r4 = r4 - r7;
        r0.nameWidth = r4;
    L_0x01fb:
        r7 = r0.drawNameLock;
        r8 = NUM; // 0x40CLASSNAME float:6.0 double:5.367157323E-315;
        if (r7 == 0) goto L_0x0212;
    L_0x0201:
        r7 = r0.nameWidth;
        r8 = org.telegram.messenger.AndroidUtilities.dp(r8);
        r10 = org.telegram.ui.ActionBar.Theme.dialogs_lockDrawable;
        r10 = r10.getIntrinsicWidth();
        r8 = r8 + r10;
        r7 = r7 - r8;
        r0.nameWidth = r7;
        goto L_0x0250;
    L_0x0212:
        r7 = r0.drawNameBroadcast;
        if (r7 == 0) goto L_0x0227;
    L_0x0216:
        r7 = r0.nameWidth;
        r8 = org.telegram.messenger.AndroidUtilities.dp(r8);
        r10 = org.telegram.ui.ActionBar.Theme.dialogs_broadcastDrawable;
        r10 = r10.getIntrinsicWidth();
        r8 = r8 + r10;
        r7 = r7 - r8;
        r0.nameWidth = r7;
        goto L_0x0250;
    L_0x0227:
        r7 = r0.drawNameGroup;
        if (r7 == 0) goto L_0x023c;
    L_0x022b:
        r7 = r0.nameWidth;
        r8 = org.telegram.messenger.AndroidUtilities.dp(r8);
        r10 = org.telegram.ui.ActionBar.Theme.dialogs_groupDrawable;
        r10 = r10.getIntrinsicWidth();
        r8 = r8 + r10;
        r7 = r7 - r8;
        r0.nameWidth = r7;
        goto L_0x0250;
    L_0x023c:
        r7 = r0.drawNameBot;
        if (r7 == 0) goto L_0x0250;
    L_0x0240:
        r7 = r0.nameWidth;
        r8 = org.telegram.messenger.AndroidUtilities.dp(r8);
        r10 = org.telegram.ui.ActionBar.Theme.dialogs_botDrawable;
        r10 = r10.getIntrinsicWidth();
        r8 = r8 + r10;
        r7 = r7 - r8;
        r0.nameWidth = r7;
    L_0x0250:
        r7 = r0.nameWidth;
        r8 = r27.getPaddingLeft();
        r10 = r27.getPaddingRight();
        r8 = r8 + r10;
        r7 = r7 - r8;
        r0.nameWidth = r7;
        r7 = r27.getPaddingLeft();
        r8 = r27.getPaddingRight();
        r7 = r7 + r8;
        r4 = r4 - r7;
        r7 = r0.drawCount;
        r18 = NUM; // 0x41400000 float:12.0 double:5.408602553E-315;
        r15 = 0;
        if (r7 == 0) goto L_0x02f8;
    L_0x026f:
        r7 = r0.currentAccount;
        r7 = org.telegram.messenger.MessagesController.getInstance(r7);
        r7 = r7.dialogs_dict;
        r10 = r0.dialog_id;
        r7 = r7.get(r10);
        r7 = (org.telegram.tgnet.TLRPC.Dialog) r7;
        if (r7 == 0) goto L_0x02f3;
    L_0x0281:
        r7 = r7.unread_count;
        if (r7 == 0) goto L_0x02f3;
    L_0x0285:
        r0.lastUnreadCount = r7;
        r6 = new java.lang.Object[r6];
        r7 = java.lang.Integer.valueOf(r7);
        r6[r1] = r7;
        r7 = "%d";
        r6 = java.lang.String.format(r7, r6);
        r7 = org.telegram.messenger.AndroidUtilities.dp(r18);
        r8 = org.telegram.ui.ActionBar.Theme.dialogs_countTextPaint;
        r8 = r8.measureText(r6);
        r10 = (double) r8;
        r10 = java.lang.Math.ceil(r10);
        r8 = (int) r10;
        r7 = java.lang.Math.max(r7, r8);
        r0.countWidth = r7;
        r7 = new android.text.StaticLayout;
        r21 = org.telegram.ui.ActionBar.Theme.dialogs_countTextPaint;
        r8 = r0.countWidth;
        r23 = android.text.Layout.Alignment.ALIGN_CENTER;
        r24 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r25 = 0;
        r26 = 0;
        r19 = r7;
        r20 = r6;
        r22 = r8;
        r19.<init>(r20, r21, r22, r23, r24, r25, r26);
        r0.countLayout = r7;
        r6 = r0.countWidth;
        r7 = NUM; // 0x41900000 float:18.0 double:5.43450582E-315;
        r7 = org.telegram.messenger.AndroidUtilities.dp(r7);
        r6 = r6 + r7;
        r7 = r0.nameWidth;
        r7 = r7 - r6;
        r0.nameWidth = r7;
        r7 = org.telegram.messenger.LocaleController.isRTL;
        r8 = NUM; // 0x41980000 float:19.0 double:5.43709615E-315;
        if (r7 != 0) goto L_0x02e7;
    L_0x02d8:
        r6 = r27.getMeasuredWidth();
        r7 = r0.countWidth;
        r6 = r6 - r7;
        r7 = org.telegram.messenger.AndroidUtilities.dp(r8);
        r6 = r6 - r7;
        r0.countLeft = r6;
        goto L_0x02fc;
    L_0x02e7:
        r7 = org.telegram.messenger.AndroidUtilities.dp(r8);
        r0.countLeft = r7;
        r7 = r0.nameLeft;
        r7 = r7 + r6;
        r0.nameLeft = r7;
        goto L_0x02fc;
    L_0x02f3:
        r0.lastUnreadCount = r1;
        r0.countLayout = r15;
        goto L_0x02fc;
    L_0x02f8:
        r0.lastUnreadCount = r1;
        r0.countLayout = r15;
    L_0x02fc:
        r6 = r0.nameWidth;
        r7 = org.telegram.messenger.AndroidUtilities.dp(r18);
        r6 = r6 - r7;
        r6 = (float) r6;
        r7 = android.text.TextUtils.TruncateAt.END;
        r8 = android.text.TextUtils.ellipsize(r2, r9, r6, r7);
        r2 = new android.text.StaticLayout;
        r10 = r0.nameWidth;
        r11 = android.text.Layout.Alignment.ALIGN_NORMAL;
        r12 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r13 = 0;
        r14 = 0;
        r7 = r2;
        r7.<init>(r8, r9, r10, r11, r12, r13, r14);
        r0.nameLayout = r2;
        r2 = r0.chat;
        r6 = NUM; // 0x41500000 float:13.0 double:5.413783207E-315;
        r7 = NUM; // 0x41CLASSNAME float:25.0 double:5.45263811E-315;
        if (r2 == 0) goto L_0x0331;
    L_0x0322:
        r2 = r0.subLabel;
        if (r2 == 0) goto L_0x0327;
    L_0x0326:
        goto L_0x0331;
    L_0x0327:
        r0.onlineLayout = r15;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r7);
        r0.nameTop = r2;
        goto L_0x0404;
    L_0x0331:
        r2 = org.telegram.messenger.LocaleController.isRTL;
        if (r2 != 0) goto L_0x033f;
    L_0x0335:
        r2 = org.telegram.messenger.AndroidUtilities.leftBaseline;
        r2 = (float) r2;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r2);
        r0.onlineLeft = r2;
        goto L_0x0345;
    L_0x033f:
        r2 = org.telegram.messenger.AndroidUtilities.dp(r5);
        r0.onlineLeft = r2;
    L_0x0345:
        r2 = org.telegram.ui.ActionBar.Theme.dialogs_offlinePaint;
        r5 = r0.subLabel;
        if (r5 == 0) goto L_0x034f;
    L_0x034b:
        r12 = r2;
        r3 = r5;
        goto L_0x03c2;
    L_0x034f:
        r5 = r0.user;
        if (r5 == 0) goto L_0x03c1;
    L_0x0353:
        r3 = org.telegram.messenger.MessagesController.isSupportUser(r5);
        if (r3 == 0) goto L_0x0363;
    L_0x0359:
        r3 = NUM; // 0x7f0d0958 float:1.8746966E38 double:1.0531309593E-314;
        r5 = "SupportStatus";
        r3 = org.telegram.messenger.LocaleController.getString(r5, r3);
        goto L_0x03c1;
    L_0x0363:
        r3 = r0.user;
        r5 = r3.bot;
        if (r5 == 0) goto L_0x0373;
    L_0x0369:
        r3 = NUM; // 0x7f0d01b3 float:1.8742997E38 double:1.0531299925E-314;
        r5 = "Bot";
        r3 = org.telegram.messenger.LocaleController.getString(r5, r3);
        goto L_0x03c1;
    L_0x0373:
        r5 = r3.id;
        r8 = 333000; // 0x514c8 float:4.66632E-40 double:1.64524E-318;
        if (r5 == r8) goto L_0x03b8;
    L_0x037a:
        r8 = 777000; // 0xbdb28 float:1.088809E-39 double:3.83889E-318;
        if (r5 != r8) goto L_0x0380;
    L_0x037f:
        goto L_0x03b8;
    L_0x0380:
        r5 = r0.currentAccount;
        r3 = org.telegram.messenger.LocaleController.formatUserStatus(r5, r3);
        r5 = r0.user;
        if (r5 == 0) goto L_0x03c1;
    L_0x038a:
        r5 = r5.id;
        r8 = r0.currentAccount;
        r8 = org.telegram.messenger.UserConfig.getInstance(r8);
        r8 = r8.getClientUserId();
        if (r5 == r8) goto L_0x03ac;
    L_0x0398:
        r5 = r0.user;
        r5 = r5.status;
        if (r5 == 0) goto L_0x03c1;
    L_0x039e:
        r5 = r5.expires;
        r8 = r0.currentAccount;
        r8 = org.telegram.tgnet.ConnectionsManager.getInstance(r8);
        r8 = r8.getCurrentTime();
        if (r5 <= r8) goto L_0x03c1;
    L_0x03ac:
        r2 = org.telegram.ui.ActionBar.Theme.dialogs_onlinePaint;
        r3 = NUM; // 0x7f0d0680 float:1.874549E38 double:1.0531305997E-314;
        r5 = "Online";
        r3 = org.telegram.messenger.LocaleController.getString(r5, r3);
        goto L_0x03c1;
    L_0x03b8:
        r3 = NUM; // 0x7f0d08de float:1.8746719E38 double:1.053130899E-314;
        r5 = "ServiceNotifications";
        r3 = org.telegram.messenger.LocaleController.getString(r5, r3);
    L_0x03c1:
        r12 = r2;
    L_0x03c2:
        r2 = r0.savedMessages;
        if (r2 == 0) goto L_0x03cf;
    L_0x03c6:
        r0.onlineLayout = r15;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r7);
        r0.nameTop = r2;
        goto L_0x0404;
    L_0x03cf:
        r2 = org.telegram.messenger.AndroidUtilities.dp(r18);
        r2 = r4 - r2;
        r2 = (float) r2;
        r5 = android.text.TextUtils.TruncateAt.END;
        r11 = android.text.TextUtils.ellipsize(r3, r12, r2, r5);
        r2 = new android.text.StaticLayout;
        r14 = android.text.Layout.Alignment.ALIGN_NORMAL;
        r15 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r16 = 0;
        r17 = 0;
        r10 = r2;
        r13 = r4;
        r10.<init>(r11, r12, r13, r14, r15, r16, r17);
        r0.onlineLayout = r2;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r0.nameTop = r2;
        r2 = r0.subLabel;
        if (r2 == 0) goto L_0x0404;
    L_0x03f7:
        r2 = r0.chat;
        if (r2 == 0) goto L_0x0404;
    L_0x03fb:
        r2 = r0.nameLockTop;
        r3 = org.telegram.messenger.AndroidUtilities.dp(r18);
        r2 = r2 - r3;
        r0.nameLockTop = r2;
    L_0x0404:
        r2 = org.telegram.messenger.LocaleController.isRTL;
        if (r2 == 0) goto L_0x0422;
    L_0x0408:
        r2 = r27.getMeasuredWidth();
        r3 = org.telegram.messenger.AndroidUtilities.isTablet();
        if (r3 == 0) goto L_0x0415;
    L_0x0412:
        r3 = NUM; // 0x42820000 float:65.0 double:5.51286321E-315;
        goto L_0x0417;
    L_0x0415:
        r3 = NUM; // 0x42740000 float:61.0 double:5.50833014E-315;
    L_0x0417:
        r3 = org.telegram.messenger.AndroidUtilities.dp(r3);
        r2 = r2 - r3;
        r3 = r27.getPaddingRight();
        r2 = r2 - r3;
        goto L_0x0434;
    L_0x0422:
        r2 = org.telegram.messenger.AndroidUtilities.isTablet();
        if (r2 == 0) goto L_0x0429;
    L_0x0428:
        goto L_0x042b;
    L_0x0429:
        r6 = NUM; // 0x41100000 float:9.0 double:5.39306059E-315;
    L_0x042b:
        r2 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r3 = r27.getPaddingLeft();
        r2 = r2 + r3;
    L_0x0434:
        r3 = r0.avatarImage;
        r5 = NUM; // 0x41200000 float:10.0 double:5.398241246E-315;
        r5 = org.telegram.messenger.AndroidUtilities.dp(r5);
        r6 = NUM; // 0x42500000 float:52.0 double:5.496673668E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r7 = NUM; // 0x42500000 float:52.0 double:5.496673668E-315;
        r7 = org.telegram.messenger.AndroidUtilities.dp(r7);
        r3.setImageCoords(r2, r5, r6, r7);
        r2 = org.telegram.messenger.LocaleController.isRTL;
        if (r2 == 0) goto L_0x04b7;
    L_0x044f:
        r2 = r0.nameLayout;
        r2 = r2.getLineCount();
        if (r2 <= 0) goto L_0x0483;
    L_0x0457:
        r2 = r0.nameLayout;
        r2 = r2.getLineLeft(r1);
        r3 = 0;
        r2 = (r2 > r3 ? 1 : (r2 == r3 ? 0 : -1));
        if (r2 != 0) goto L_0x0483;
    L_0x0462:
        r2 = r0.nameLayout;
        r2 = r2.getLineWidth(r1);
        r2 = (double) r2;
        r2 = java.lang.Math.ceil(r2);
        r5 = r0.nameWidth;
        r6 = (double) r5;
        r8 = (r2 > r6 ? 1 : (r2 == r6 ? 0 : -1));
        if (r8 >= 0) goto L_0x0483;
    L_0x0474:
        r6 = r0.nameLeft;
        r6 = (double) r6;
        r8 = (double) r5;
        java.lang.Double.isNaN(r8);
        r8 = r8 - r2;
        java.lang.Double.isNaN(r6);
        r6 = r6 + r8;
        r2 = (int) r6;
        r0.nameLeft = r2;
    L_0x0483:
        r2 = r0.onlineLayout;
        if (r2 == 0) goto L_0x0520;
    L_0x0487:
        r2 = r2.getLineCount();
        if (r2 <= 0) goto L_0x0520;
    L_0x048d:
        r2 = r0.onlineLayout;
        r2 = r2.getLineLeft(r1);
        r3 = 0;
        r2 = (r2 > r3 ? 1 : (r2 == r3 ? 0 : -1));
        if (r2 != 0) goto L_0x0520;
    L_0x0498:
        r2 = r0.onlineLayout;
        r1 = r2.getLineWidth(r1);
        r1 = (double) r1;
        r1 = java.lang.Math.ceil(r1);
        r3 = (double) r4;
        r5 = (r1 > r3 ? 1 : (r1 == r3 ? 0 : -1));
        if (r5 >= 0) goto L_0x0520;
    L_0x04a8:
        r5 = r0.onlineLeft;
        r5 = (double) r5;
        java.lang.Double.isNaN(r3);
        r3 = r3 - r1;
        java.lang.Double.isNaN(r5);
        r5 = r5 + r3;
        r1 = (int) r5;
        r0.onlineLeft = r1;
        goto L_0x0520;
    L_0x04b7:
        r2 = r0.nameLayout;
        r2 = r2.getLineCount();
        if (r2 <= 0) goto L_0x04ed;
    L_0x04bf:
        r2 = r0.nameLayout;
        r2 = r2.getLineRight(r1);
        r3 = r0.nameWidth;
        r3 = (float) r3;
        r2 = (r2 > r3 ? 1 : (r2 == r3 ? 0 : -1));
        if (r2 != 0) goto L_0x04ed;
    L_0x04cc:
        r2 = r0.nameLayout;
        r2 = r2.getLineWidth(r1);
        r2 = (double) r2;
        r2 = java.lang.Math.ceil(r2);
        r5 = r0.nameWidth;
        r6 = (double) r5;
        r8 = (r2 > r6 ? 1 : (r2 == r6 ? 0 : -1));
        if (r8 >= 0) goto L_0x04ed;
    L_0x04de:
        r6 = r0.nameLeft;
        r6 = (double) r6;
        r8 = (double) r5;
        java.lang.Double.isNaN(r8);
        r8 = r8 - r2;
        java.lang.Double.isNaN(r6);
        r6 = r6 - r8;
        r2 = (int) r6;
        r0.nameLeft = r2;
    L_0x04ed:
        r2 = r0.onlineLayout;
        if (r2 == 0) goto L_0x0520;
    L_0x04f1:
        r2 = r2.getLineCount();
        if (r2 <= 0) goto L_0x0520;
    L_0x04f7:
        r2 = r0.onlineLayout;
        r2 = r2.getLineRight(r1);
        r3 = (float) r4;
        r2 = (r2 > r3 ? 1 : (r2 == r3 ? 0 : -1));
        if (r2 != 0) goto L_0x0520;
    L_0x0502:
        r2 = r0.onlineLayout;
        r1 = r2.getLineWidth(r1);
        r1 = (double) r1;
        r1 = java.lang.Math.ceil(r1);
        r3 = (double) r4;
        r5 = (r1 > r3 ? 1 : (r1 == r3 ? 0 : -1));
        if (r5 >= 0) goto L_0x0520;
    L_0x0512:
        r5 = r0.onlineLeft;
        r5 = (double) r5;
        java.lang.Double.isNaN(r3);
        r3 = r3 - r1;
        java.lang.Double.isNaN(r5);
        r5 = r5 - r3;
        r1 = (int) r5;
        r0.onlineLeft = r1;
    L_0x0520:
        r1 = r0.nameLeft;
        r2 = r27.getPaddingLeft();
        r1 = r1 + r2;
        r0.nameLeft = r1;
        r1 = r0.onlineLeft;
        r2 = r27.getPaddingLeft();
        r1 = r1 + r2;
        r0.onlineLeft = r1;
        r1 = r0.nameLockLeft;
        r2 = r27.getPaddingLeft();
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
        r0.setInfo(r3, r1, r1, r3);
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
                    BaseCell.setDrawableBounds(Theme.dialogs_verifiedDrawable, lineRight, this.nameLockTop);
                    BaseCell.setDrawableBounds(Theme.dialogs_verifiedCheckDrawable, lineRight, this.nameLockTop);
                    Theme.dialogs_verifiedDrawable.draw(canvas);
                    Theme.dialogs_verifiedCheckDrawable.draw(canvas);
                }
            }
            if (this.onlineLayout != null) {
                canvas.save();
                canvas.translate((float) this.onlineLeft, (float) AndroidUtilities.dp(40.0f));
                this.onlineLayout.draw(canvas);
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
        stringBuilder.append(this.nameLayout.getText());
        stringBuilder.append(", ");
        stringBuilder.append(this.subLabel);
        accessibilityNodeInfo.setText(stringBuilder.toString());
    }
}
