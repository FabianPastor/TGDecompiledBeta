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

    /* JADX WARNING: Missing block: B:159:0x0435, code skipped:
            if (r4.expires > org.telegram.tgnet.ConnectionsManager.getInstance(r0.currentAccount).getCurrentTime()) goto L_0x0437;
     */
    public void buildLayout() {
        /*
        r24 = this;
        r0 = r24;
        r1 = 0;
        r0.drawNameBroadcast = r1;
        r0.drawNameLock = r1;
        r0.drawNameGroup = r1;
        r0.drawCheck = r1;
        r0.drawNameBot = r1;
        r2 = r0.encryptedChat;
        r3 = 32;
        r4 = 1;
        r5 = NUM; // 0x41300000 float:11.0 double:5.4034219E-315;
        if (r2 == 0) goto L_0x0065;
    L_0x0016:
        r0.drawNameLock = r4;
        r2 = r2.id;
        r6 = (long) r2;
        r6 = r6 << r3;
        r0.dialog_id = r6;
        r2 = org.telegram.messenger.LocaleController.isRTL;
        if (r2 != 0) goto L_0x003e;
    L_0x0022:
        r2 = org.telegram.messenger.AndroidUtilities.leftBaseline;
        r2 = (float) r2;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r2);
        r0.nameLockLeft = r2;
        r2 = org.telegram.messenger.AndroidUtilities.leftBaseline;
        r2 = r2 + 4;
        r2 = (float) r2;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r2);
        r6 = org.telegram.ui.ActionBar.Theme.dialogs_lockDrawable;
        r6 = r6.getIntrinsicWidth();
        r2 = r2 + r6;
        r0.nameLeft = r2;
        goto L_0x005b;
    L_0x003e:
        r2 = r24.getMeasuredWidth();
        r6 = org.telegram.messenger.AndroidUtilities.leftBaseline;
        r6 = r6 + 2;
        r6 = (float) r6;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r2 = r2 - r6;
        r6 = org.telegram.ui.ActionBar.Theme.dialogs_lockDrawable;
        r6 = r6.getIntrinsicWidth();
        r2 = r2 - r6;
        r0.nameLockLeft = r2;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r5);
        r0.nameLeft = r2;
    L_0x005b:
        r2 = NUM; // 0x41b00000 float:22.0 double:5.44486713E-315;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r2);
        r0.nameLockTop = r2;
        goto L_0x0186;
    L_0x0065:
        r2 = r0.chat;
        if (r2 == 0) goto L_0x0103;
    L_0x0069:
        r6 = r2.id;
        r6 = -r6;
        r6 = (long) r6;
        r0.dialog_id = r6;
        r6 = org.telegram.messenger.SharedConfig.drawDialogIcons;
        if (r6 == 0) goto L_0x0094;
    L_0x0073:
        r2 = org.telegram.messenger.ChatObject.isChannel(r2);
        if (r2 == 0) goto L_0x008a;
    L_0x0079:
        r2 = r0.chat;
        r2 = r2.megagroup;
        if (r2 != 0) goto L_0x008a;
    L_0x007f:
        r0.drawNameBroadcast = r4;
        r2 = NUM; // 0x41b40000 float:22.5 double:5.446162293E-315;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r2);
        r0.nameLockTop = r2;
        goto L_0x0094;
    L_0x008a:
        r0.drawNameGroup = r4;
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
        r6 = r0.drawNameGroup;
        if (r6 == 0) goto L_0x00bb;
    L_0x00b8:
        r6 = org.telegram.ui.ActionBar.Theme.dialogs_groupDrawable;
        goto L_0x00bd;
    L_0x00bb:
        r6 = org.telegram.ui.ActionBar.Theme.dialogs_broadcastDrawable;
    L_0x00bd:
        r6 = r6.getIntrinsicWidth();
        r2 = r2 + r6;
        r0.nameLeft = r2;
        goto L_0x0186;
    L_0x00c6:
        r2 = r24.getMeasuredWidth();
        r6 = org.telegram.messenger.AndroidUtilities.leftBaseline;
        r6 = r6 + 2;
        r6 = (float) r6;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r2 = r2 - r6;
        r6 = r0.drawNameGroup;
        if (r6 == 0) goto L_0x00db;
    L_0x00d8:
        r6 = org.telegram.ui.ActionBar.Theme.dialogs_groupDrawable;
        goto L_0x00dd;
    L_0x00db:
        r6 = org.telegram.ui.ActionBar.Theme.dialogs_broadcastDrawable;
    L_0x00dd:
        r6 = r6.getIntrinsicWidth();
        r2 = r2 - r6;
        r0.nameLockLeft = r2;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r5);
        r0.nameLeft = r2;
        goto L_0x0186;
    L_0x00ec:
        r2 = org.telegram.messenger.LocaleController.isRTL;
        if (r2 != 0) goto L_0x00fb;
    L_0x00f0:
        r2 = org.telegram.messenger.AndroidUtilities.leftBaseline;
        r2 = (float) r2;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r2);
        r0.nameLeft = r2;
        goto L_0x0186;
    L_0x00fb:
        r2 = org.telegram.messenger.AndroidUtilities.dp(r5);
        r0.nameLeft = r2;
        goto L_0x0186;
    L_0x0103:
        r2 = r0.user;
        if (r2 == 0) goto L_0x0186;
    L_0x0107:
        r2 = r2.id;
        r6 = (long) r2;
        r0.dialog_id = r6;
        r2 = org.telegram.messenger.LocaleController.isRTL;
        if (r2 != 0) goto L_0x011a;
    L_0x0110:
        r2 = org.telegram.messenger.AndroidUtilities.leftBaseline;
        r2 = (float) r2;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r2);
        r0.nameLeft = r2;
        goto L_0x0120;
    L_0x011a:
        r2 = org.telegram.messenger.AndroidUtilities.dp(r5);
        r0.nameLeft = r2;
    L_0x0120:
        r2 = org.telegram.messenger.SharedConfig.drawDialogIcons;
        if (r2 == 0) goto L_0x0178;
    L_0x0124:
        r2 = r0.user;
        r6 = r2.bot;
        if (r6 == 0) goto L_0x0178;
    L_0x012a:
        r2 = org.telegram.messenger.MessagesController.isSupportUser(r2);
        if (r2 != 0) goto L_0x0178;
    L_0x0130:
        r0.drawNameBot = r4;
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
        r6 = org.telegram.ui.ActionBar.Theme.dialogs_botDrawable;
        r6 = r6.getIntrinsicWidth();
        r2 = r2 + r6;
        r0.nameLeft = r2;
        goto L_0x016f;
    L_0x0152:
        r2 = r24.getMeasuredWidth();
        r6 = org.telegram.messenger.AndroidUtilities.leftBaseline;
        r6 = r6 + 2;
        r6 = (float) r6;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r2 = r2 - r6;
        r6 = org.telegram.ui.ActionBar.Theme.dialogs_botDrawable;
        r6 = r6.getIntrinsicWidth();
        r2 = r2 - r6;
        r0.nameLockLeft = r2;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r5);
        r0.nameLeft = r2;
    L_0x016f:
        r2 = NUM; // 0x41a40000 float:20.5 double:5.44098164E-315;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r2);
        r0.nameLockTop = r2;
        goto L_0x0180;
    L_0x0178:
        r2 = NUM; // 0x41a80000 float:21.0 double:5.442276803E-315;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r2);
        r0.nameLockTop = r2;
    L_0x0180:
        r2 = r0.user;
        r2 = r2.verified;
        r0.drawCheck = r2;
    L_0x0186:
        r2 = r0.currentName;
        if (r2 == 0) goto L_0x018b;
    L_0x018a:
        goto L_0x01a3;
    L_0x018b:
        r2 = r0.chat;
        if (r2 == 0) goto L_0x0192;
    L_0x018f:
        r2 = r2.title;
        goto L_0x019d;
    L_0x0192:
        r2 = r0.user;
        if (r2 == 0) goto L_0x019b;
    L_0x0196:
        r2 = org.telegram.messenger.UserObject.getUserName(r2);
        goto L_0x019d;
    L_0x019b:
        r2 = "";
    L_0x019d:
        r6 = 10;
        r2 = r2.replace(r6, r3);
    L_0x01a3:
        r3 = r2.length();
        if (r3 != 0) goto L_0x01de;
    L_0x01a9:
        r2 = r0.user;
        if (r2 == 0) goto L_0x01d5;
    L_0x01ad:
        r2 = r2.phone;
        if (r2 == 0) goto L_0x01d5;
    L_0x01b1:
        r2 = r2.length();
        if (r2 == 0) goto L_0x01d5;
    L_0x01b7:
        r2 = org.telegram.PhoneFormat.PhoneFormat.getInstance();
        r3 = new java.lang.StringBuilder;
        r3.<init>();
        r6 = "+";
        r3.append(r6);
        r6 = r0.user;
        r6 = r6.phone;
        r3.append(r6);
        r3 = r3.toString();
        r2 = r2.format(r3);
        goto L_0x01de;
    L_0x01d5:
        r2 = NUM; // 0x7f0e0558 float:1.8877812E38 double:1.0531628325E-314;
        r3 = "HiddenName";
        r2 = org.telegram.messenger.LocaleController.getString(r3, r2);
    L_0x01de:
        r3 = r0.encryptedChat;
        if (r3 == 0) goto L_0x01e5;
    L_0x01e2:
        r3 = org.telegram.ui.ActionBar.Theme.dialogs_searchNameEncryptedPaint;
        goto L_0x01e7;
    L_0x01e5:
        r3 = org.telegram.ui.ActionBar.Theme.dialogs_searchNamePaint;
    L_0x01e7:
        r8 = r3;
        r3 = org.telegram.messenger.LocaleController.isRTL;
        if (r3 != 0) goto L_0x01fd;
    L_0x01ec:
        r3 = r24.getMeasuredWidth();
        r6 = r0.nameLeft;
        r3 = r3 - r6;
        r6 = NUM; // 0x41600000 float:14.0 double:5.41896386E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r3 = r3 - r6;
        r0.nameWidth = r3;
        goto L_0x020e;
    L_0x01fd:
        r3 = r24.getMeasuredWidth();
        r6 = r0.nameLeft;
        r3 = r3 - r6;
        r6 = org.telegram.messenger.AndroidUtilities.leftBaseline;
        r6 = (float) r6;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r3 = r3 - r6;
        r0.nameWidth = r3;
    L_0x020e:
        r6 = r0.drawNameLock;
        r7 = NUM; // 0x40CLASSNAME float:6.0 double:5.367157323E-315;
        if (r6 == 0) goto L_0x0225;
    L_0x0214:
        r6 = r0.nameWidth;
        r7 = org.telegram.messenger.AndroidUtilities.dp(r7);
        r9 = org.telegram.ui.ActionBar.Theme.dialogs_lockDrawable;
        r9 = r9.getIntrinsicWidth();
        r7 = r7 + r9;
        r6 = r6 - r7;
        r0.nameWidth = r6;
        goto L_0x0263;
    L_0x0225:
        r6 = r0.drawNameBroadcast;
        if (r6 == 0) goto L_0x023a;
    L_0x0229:
        r6 = r0.nameWidth;
        r7 = org.telegram.messenger.AndroidUtilities.dp(r7);
        r9 = org.telegram.ui.ActionBar.Theme.dialogs_broadcastDrawable;
        r9 = r9.getIntrinsicWidth();
        r7 = r7 + r9;
        r6 = r6 - r7;
        r0.nameWidth = r6;
        goto L_0x0263;
    L_0x023a:
        r6 = r0.drawNameGroup;
        if (r6 == 0) goto L_0x024f;
    L_0x023e:
        r6 = r0.nameWidth;
        r7 = org.telegram.messenger.AndroidUtilities.dp(r7);
        r9 = org.telegram.ui.ActionBar.Theme.dialogs_groupDrawable;
        r9 = r9.getIntrinsicWidth();
        r7 = r7 + r9;
        r6 = r6 - r7;
        r0.nameWidth = r6;
        goto L_0x0263;
    L_0x024f:
        r6 = r0.drawNameBot;
        if (r6 == 0) goto L_0x0263;
    L_0x0253:
        r6 = r0.nameWidth;
        r7 = org.telegram.messenger.AndroidUtilities.dp(r7);
        r9 = org.telegram.ui.ActionBar.Theme.dialogs_botDrawable;
        r9 = r9.getIntrinsicWidth();
        r7 = r7 + r9;
        r6 = r6 - r7;
        r0.nameWidth = r6;
    L_0x0263:
        r6 = r0.nameWidth;
        r7 = r24.getPaddingLeft();
        r9 = r24.getPaddingRight();
        r7 = r7 + r9;
        r6 = r6 - r7;
        r0.nameWidth = r6;
        r6 = r24.getPaddingLeft();
        r7 = r24.getPaddingRight();
        r6 = r6 + r7;
        r3 = r3 - r6;
        r6 = r0.drawCount;
        r14 = NUM; // 0x41400000 float:12.0 double:5.408602553E-315;
        r15 = NUM; // 0x41980000 float:19.0 double:5.43709615E-315;
        r13 = 0;
        if (r6 == 0) goto L_0x030b;
    L_0x0284:
        r6 = r0.currentAccount;
        r6 = org.telegram.messenger.MessagesController.getInstance(r6);
        r6 = r6.dialogs_dict;
        r9 = r0.dialog_id;
        r6 = r6.get(r9);
        r6 = (org.telegram.tgnet.TLRPC.Dialog) r6;
        if (r6 == 0) goto L_0x0306;
    L_0x0296:
        r6 = r6.unread_count;
        if (r6 == 0) goto L_0x0306;
    L_0x029a:
        r0.lastUnreadCount = r6;
        r4 = new java.lang.Object[r4];
        r6 = java.lang.Integer.valueOf(r6);
        r4[r1] = r6;
        r6 = "%d";
        r4 = java.lang.String.format(r6, r4);
        r6 = org.telegram.messenger.AndroidUtilities.dp(r14);
        r7 = org.telegram.ui.ActionBar.Theme.dialogs_countTextPaint;
        r7 = r7.measureText(r4);
        r9 = (double) r7;
        r9 = java.lang.Math.ceil(r9);
        r7 = (int) r9;
        r6 = java.lang.Math.max(r6, r7);
        r0.countWidth = r6;
        r6 = new android.text.StaticLayout;
        r18 = org.telegram.ui.ActionBar.Theme.dialogs_countTextPaint;
        r7 = r0.countWidth;
        r20 = android.text.Layout.Alignment.ALIGN_CENTER;
        r21 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r22 = 0;
        r23 = 0;
        r16 = r6;
        r17 = r4;
        r19 = r7;
        r16.<init>(r17, r18, r19, r20, r21, r22, r23);
        r0.countLayout = r6;
        r4 = r0.countWidth;
        r6 = NUM; // 0x41900000 float:18.0 double:5.43450582E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = r4 + r6;
        r6 = r0.nameWidth;
        r6 = r6 - r4;
        r0.nameWidth = r6;
        r6 = org.telegram.messenger.LocaleController.isRTL;
        if (r6 != 0) goto L_0x02fa;
    L_0x02eb:
        r4 = r24.getMeasuredWidth();
        r6 = r0.countWidth;
        r4 = r4 - r6;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r15);
        r4 = r4 - r6;
        r0.countLeft = r4;
        goto L_0x030f;
    L_0x02fa:
        r6 = org.telegram.messenger.AndroidUtilities.dp(r15);
        r0.countLeft = r6;
        r6 = r0.nameLeft;
        r6 = r6 + r4;
        r0.nameLeft = r6;
        goto L_0x030f;
    L_0x0306:
        r0.lastUnreadCount = r1;
        r0.countLayout = r13;
        goto L_0x030f;
    L_0x030b:
        r0.lastUnreadCount = r1;
        r0.countLayout = r13;
    L_0x030f:
        r4 = r0.nameWidth;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r14);
        r4 = r4 - r6;
        r4 = (float) r4;
        r6 = android.text.TextUtils.TruncateAt.END;
        r7 = android.text.TextUtils.ellipsize(r2, r8, r4, r6);
        r2 = new android.text.StaticLayout;
        r9 = r0.nameWidth;
        r10 = android.text.Layout.Alignment.ALIGN_NORMAL;
        r11 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r12 = 0;
        r4 = 0;
        r6 = r2;
        r1 = r13;
        r13 = r4;
        r6.<init>(r7, r8, r9, r10, r11, r12, r13);
        r0.nameLayout = r2;
        r2 = org.telegram.ui.ActionBar.Theme.dialogs_offlinePaint;
        r4 = org.telegram.messenger.LocaleController.isRTL;
        if (r4 != 0) goto L_0x033f;
    L_0x0335:
        r4 = org.telegram.messenger.AndroidUtilities.leftBaseline;
        r4 = (float) r4;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r0.statusLeft = r4;
        goto L_0x0345;
    L_0x033f:
        r4 = org.telegram.messenger.AndroidUtilities.dp(r5);
        r0.statusLeft = r4;
    L_0x0345:
        r4 = r0.chat;
        if (r4 == 0) goto L_0x03d4;
    L_0x0349:
        r6 = r0.subLabel;
        if (r6 == 0) goto L_0x034f;
    L_0x034d:
        goto L_0x03d4;
    L_0x034f:
        if (r4 == 0) goto L_0x03cb;
    L_0x0351:
        r4 = org.telegram.messenger.ChatObject.isChannel(r4);
        if (r4 == 0) goto L_0x038c;
    L_0x0357:
        r4 = r0.chat;
        r6 = r4.megagroup;
        if (r6 != 0) goto L_0x038c;
    L_0x035d:
        r6 = r4.participants_count;
        if (r6 == 0) goto L_0x0368;
    L_0x0361:
        r4 = "Subscribers";
        r13 = org.telegram.messenger.LocaleController.formatPluralString(r4, r6);
        goto L_0x03cc;
    L_0x0368:
        r4 = r4.username;
        r4 = android.text.TextUtils.isEmpty(r4);
        if (r4 == 0) goto L_0x037e;
    L_0x0370:
        r4 = NUM; // 0x7f0e0279 float:1.8876321E38 double:1.0531624694E-314;
        r6 = "ChannelPrivate";
        r4 = org.telegram.messenger.LocaleController.getString(r6, r4);
        r13 = r4.toLowerCase();
        goto L_0x03cc;
    L_0x037e:
        r4 = NUM; // 0x7f0e027c float:1.8876328E38 double:1.053162471E-314;
        r6 = "ChannelPublic";
        r4 = org.telegram.messenger.LocaleController.getString(r6, r4);
        r13 = r4.toLowerCase();
        goto L_0x03cc;
    L_0x038c:
        r4 = r0.chat;
        r6 = r4.participants_count;
        if (r6 == 0) goto L_0x0399;
    L_0x0392:
        r4 = "Members";
        r13 = org.telegram.messenger.LocaleController.formatPluralString(r4, r6);
        goto L_0x03cc;
    L_0x0399:
        r6 = r4.has_geo;
        if (r6 == 0) goto L_0x03a7;
    L_0x039d:
        r4 = NUM; // 0x7f0e0632 float:1.8878254E38 double:1.05316294E-314;
        r6 = "MegaLocation";
        r13 = org.telegram.messenger.LocaleController.getString(r6, r4);
        goto L_0x03cc;
    L_0x03a7:
        r4 = r4.username;
        r4 = android.text.TextUtils.isEmpty(r4);
        if (r4 == 0) goto L_0x03bd;
    L_0x03af:
        r4 = NUM; // 0x7f0e0633 float:1.8878256E38 double:1.0531629407E-314;
        r6 = "MegaPrivate";
        r4 = org.telegram.messenger.LocaleController.getString(r6, r4);
        r13 = r4.toLowerCase();
        goto L_0x03cc;
    L_0x03bd:
        r4 = NUM; // 0x7f0e0636 float:1.8878262E38 double:1.053162942E-314;
        r6 = "MegaPublic";
        r4 = org.telegram.messenger.LocaleController.getString(r6, r4);
        r13 = r4.toLowerCase();
        goto L_0x03cc;
    L_0x03cb:
        r13 = r1;
    L_0x03cc:
        r4 = org.telegram.messenger.AndroidUtilities.dp(r15);
        r0.nameTop = r4;
        goto L_0x0459;
    L_0x03d4:
        r13 = r0.subLabel;
        if (r13 == 0) goto L_0x03da;
    L_0x03d8:
        goto L_0x044e;
    L_0x03da:
        r4 = r0.user;
        if (r4 == 0) goto L_0x044d;
    L_0x03de:
        r4 = org.telegram.messenger.MessagesController.isSupportUser(r4);
        if (r4 == 0) goto L_0x03ee;
    L_0x03e4:
        r4 = NUM; // 0x7f0e0aba float:1.8880607E38 double:1.0531635133E-314;
        r6 = "SupportStatus";
        r13 = org.telegram.messenger.LocaleController.getString(r6, r4);
        goto L_0x044e;
    L_0x03ee:
        r4 = r0.user;
        r6 = r4.bot;
        if (r6 == 0) goto L_0x03fe;
    L_0x03f4:
        r4 = NUM; // 0x7f0e01e8 float:1.8876027E38 double:1.0531623977E-314;
        r6 = "Bot";
        r13 = org.telegram.messenger.LocaleController.getString(r6, r4);
        goto L_0x044e;
    L_0x03fe:
        r6 = r4.id;
        r7 = 333000; // 0x514c8 float:4.66632E-40 double:1.64524E-318;
        if (r6 == r7) goto L_0x0443;
    L_0x0405:
        r7 = 777000; // 0xbdb28 float:1.088809E-39 double:3.83889E-318;
        if (r6 != r7) goto L_0x040b;
    L_0x040a:
        goto L_0x0443;
    L_0x040b:
        r6 = r0.currentAccount;
        r13 = org.telegram.messenger.LocaleController.formatUserStatus(r6, r4);
        r4 = r0.user;
        if (r4 == 0) goto L_0x044e;
    L_0x0415:
        r4 = r4.id;
        r6 = r0.currentAccount;
        r6 = org.telegram.messenger.UserConfig.getInstance(r6);
        r6 = r6.getClientUserId();
        if (r4 == r6) goto L_0x0437;
    L_0x0423:
        r4 = r0.user;
        r4 = r4.status;
        if (r4 == 0) goto L_0x044e;
    L_0x0429:
        r4 = r4.expires;
        r6 = r0.currentAccount;
        r6 = org.telegram.tgnet.ConnectionsManager.getInstance(r6);
        r6 = r6.getCurrentTime();
        if (r4 <= r6) goto L_0x044e;
    L_0x0437:
        r2 = org.telegram.ui.ActionBar.Theme.dialogs_onlinePaint;
        r4 = NUM; // 0x7f0e0771 float:1.8878901E38 double:1.053163098E-314;
        r6 = "Online";
        r13 = org.telegram.messenger.LocaleController.getString(r6, r4);
        goto L_0x044e;
    L_0x0443:
        r4 = NUM; // 0x7f0e0a1e float:1.888029E38 double:1.0531634363E-314;
        r6 = "ServiceNotifications";
        r13 = org.telegram.messenger.LocaleController.getString(r6, r4);
        goto L_0x044e;
    L_0x044d:
        r13 = r1;
    L_0x044e:
        r4 = r0.savedMessages;
        if (r4 == 0) goto L_0x0459;
    L_0x0452:
        r4 = org.telegram.messenger.AndroidUtilities.dp(r15);
        r0.nameTop = r4;
        r13 = r1;
    L_0x0459:
        r11 = r2;
        r2 = android.text.TextUtils.isEmpty(r13);
        if (r2 != 0) goto L_0x0491;
    L_0x0460:
        r1 = org.telegram.messenger.AndroidUtilities.dp(r14);
        r1 = r3 - r1;
        r1 = (float) r1;
        r2 = android.text.TextUtils.TruncateAt.END;
        r10 = android.text.TextUtils.ellipsize(r13, r11, r1, r2);
        r1 = new android.text.StaticLayout;
        r13 = android.text.Layout.Alignment.ALIGN_NORMAL;
        r14 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r15 = 0;
        r16 = 0;
        r9 = r1;
        r12 = r3;
        r9.<init>(r10, r11, r12, r13, r14, r15, r16);
        r0.statusLayout = r1;
        r1 = NUM; // 0x41100000 float:9.0 double:5.39306059E-315;
        r1 = org.telegram.messenger.AndroidUtilities.dp(r1);
        r0.nameTop = r1;
        r1 = r0.nameLockTop;
        r2 = NUM; // 0x41200000 float:10.0 double:5.398241246E-315;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r2);
        r1 = r1 - r2;
        r0.nameLockTop = r1;
        goto L_0x0493;
    L_0x0491:
        r0.statusLayout = r1;
    L_0x0493:
        r1 = org.telegram.messenger.LocaleController.isRTL;
        if (r1 == 0) goto L_0x04a8;
    L_0x0497:
        r1 = r24.getMeasuredWidth();
        r2 = NUM; // 0x42640000 float:57.0 double:5.503149485E-315;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r2);
        r1 = r1 - r2;
        r2 = r24.getPaddingRight();
        r1 = r1 - r2;
        goto L_0x04b1;
    L_0x04a8:
        r1 = org.telegram.messenger.AndroidUtilities.dp(r5);
        r2 = r24.getPaddingLeft();
        r1 = r1 + r2;
    L_0x04b1:
        r2 = r0.avatarImage;
        r4 = NUM; // 0x40e00000 float:7.0 double:5.37751863E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r5 = NUM; // 0x42380000 float:46.0 double:5.488902687E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r5);
        r5 = org.telegram.messenger.AndroidUtilities.dp(r5);
        r2.setImageCoords(r1, r4, r6, r5);
        r1 = org.telegram.messenger.LocaleController.isRTL;
        if (r1 == 0) goto L_0x0533;
    L_0x04ca:
        r1 = r0.nameLayout;
        r1 = r1.getLineCount();
        r2 = 0;
        if (r1 <= 0) goto L_0x04ff;
    L_0x04d3:
        r1 = r0.nameLayout;
        r4 = 0;
        r1 = r1.getLineLeft(r4);
        r1 = (r1 > r2 ? 1 : (r1 == r2 ? 0 : -1));
        if (r1 != 0) goto L_0x04ff;
    L_0x04de:
        r1 = r0.nameLayout;
        r1 = r1.getLineWidth(r4);
        r4 = (double) r1;
        r4 = java.lang.Math.ceil(r4);
        r1 = r0.nameWidth;
        r6 = (double) r1;
        r8 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1));
        if (r8 >= 0) goto L_0x04ff;
    L_0x04f0:
        r6 = r0.nameLeft;
        r6 = (double) r6;
        r8 = (double) r1;
        java.lang.Double.isNaN(r8);
        r8 = r8 - r4;
        java.lang.Double.isNaN(r6);
        r6 = r6 + r8;
        r1 = (int) r6;
        r0.nameLeft = r1;
    L_0x04ff:
        r1 = r0.statusLayout;
        if (r1 == 0) goto L_0x059e;
    L_0x0503:
        r1 = r1.getLineCount();
        if (r1 <= 0) goto L_0x059e;
    L_0x0509:
        r1 = r0.statusLayout;
        r4 = 0;
        r1 = r1.getLineLeft(r4);
        r1 = (r1 > r2 ? 1 : (r1 == r2 ? 0 : -1));
        if (r1 != 0) goto L_0x059e;
    L_0x0514:
        r1 = r0.statusLayout;
        r1 = r1.getLineWidth(r4);
        r1 = (double) r1;
        r1 = java.lang.Math.ceil(r1);
        r3 = (double) r3;
        r5 = (r1 > r3 ? 1 : (r1 == r3 ? 0 : -1));
        if (r5 >= 0) goto L_0x059e;
    L_0x0524:
        r5 = r0.statusLeft;
        r5 = (double) r5;
        java.lang.Double.isNaN(r3);
        r3 = r3 - r1;
        java.lang.Double.isNaN(r5);
        r5 = r5 + r3;
        r1 = (int) r5;
        r0.statusLeft = r1;
        goto L_0x059e;
    L_0x0533:
        r1 = r0.nameLayout;
        r1 = r1.getLineCount();
        if (r1 <= 0) goto L_0x056a;
    L_0x053b:
        r1 = r0.nameLayout;
        r2 = 0;
        r1 = r1.getLineRight(r2);
        r4 = r0.nameWidth;
        r4 = (float) r4;
        r1 = (r1 > r4 ? 1 : (r1 == r4 ? 0 : -1));
        if (r1 != 0) goto L_0x056a;
    L_0x0549:
        r1 = r0.nameLayout;
        r1 = r1.getLineWidth(r2);
        r1 = (double) r1;
        r1 = java.lang.Math.ceil(r1);
        r4 = r0.nameWidth;
        r5 = (double) r4;
        r7 = (r1 > r5 ? 1 : (r1 == r5 ? 0 : -1));
        if (r7 >= 0) goto L_0x056a;
    L_0x055b:
        r5 = r0.nameLeft;
        r5 = (double) r5;
        r7 = (double) r4;
        java.lang.Double.isNaN(r7);
        r7 = r7 - r1;
        java.lang.Double.isNaN(r5);
        r5 = r5 - r7;
        r1 = (int) r5;
        r0.nameLeft = r1;
    L_0x056a:
        r1 = r0.statusLayout;
        if (r1 == 0) goto L_0x059e;
    L_0x056e:
        r1 = r1.getLineCount();
        if (r1 <= 0) goto L_0x059e;
    L_0x0574:
        r1 = r0.statusLayout;
        r2 = 0;
        r1 = r1.getLineRight(r2);
        r4 = (float) r3;
        r1 = (r1 > r4 ? 1 : (r1 == r4 ? 0 : -1));
        if (r1 != 0) goto L_0x059e;
    L_0x0580:
        r1 = r0.statusLayout;
        r1 = r1.getLineWidth(r2);
        r1 = (double) r1;
        r1 = java.lang.Math.ceil(r1);
        r3 = (double) r3;
        r5 = (r1 > r3 ? 1 : (r1 == r3 ? 0 : -1));
        if (r5 >= 0) goto L_0x059e;
    L_0x0590:
        r5 = r0.statusLeft;
        r5 = (double) r5;
        java.lang.Double.isNaN(r3);
        r3 = r3 - r1;
        java.lang.Double.isNaN(r5);
        r5 = r5 - r3;
        r1 = (int) r5;
        r0.statusLeft = r1;
    L_0x059e:
        r1 = r0.nameLeft;
        r2 = r24.getPaddingLeft();
        r1 = r1 + r2;
        r0.nameLeft = r1;
        r1 = r0.statusLeft;
        r2 = r24.getPaddingLeft();
        r1 = r1 + r2;
        r0.statusLeft = r1;
        r1 = r0.nameLockLeft;
        r2 = r24.getPaddingLeft();
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
