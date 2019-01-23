package org.telegram.ui.Components;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.TextUtils.TruncateAt;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.AndroidUtilities.LinkMovementMethodMy;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.LocaleController.LocaleInfo;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.MessagesStorage.IntCallback;
import org.telegram.messenger.NotificationsController;
import org.telegram.messenger.R;
import org.telegram.messenger.SecretChatHelper;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.EncryptedChat;
import org.telegram.tgnet.TLRPC.InputPeer;
import org.telegram.tgnet.TLRPC.TL_account_reportPeer;
import org.telegram.tgnet.TLRPC.TL_dialog;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_inputReportReasonChildAbuse;
import org.telegram.tgnet.TLRPC.TL_inputReportReasonPornography;
import org.telegram.tgnet.TLRPC.TL_inputReportReasonSpam;
import org.telegram.tgnet.TLRPC.TL_inputReportReasonViolence;
import org.telegram.tgnet.TLRPC.TL_langPackLanguage;
import org.telegram.tgnet.TLRPC.TL_messages_report;
import org.telegram.tgnet.TLRPC.TL_peerNotifySettings;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.AlertDialog.Builder;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.AccountSelectCell;
import org.telegram.ui.Cells.RadioColorCell;
import org.telegram.ui.Cells.TextColorCell;
import org.telegram.ui.LaunchActivity;
import org.telegram.ui.NotificationsCustomSettingsActivity;
import org.telegram.ui.NotificationsSettingsActivity.NotificationException;
import org.telegram.ui.ProfileNotificationsActivity;
import org.telegram.ui.ReportOtherActivity;

public class AlertsCreator {

    public interface AccountSelectDelegate {
        void didSelectAccount(int i);
    }

    public interface DatePickerDelegate {
        void didSelectDate(int i, int i2, int i3);
    }

    public interface PaymentAlertDelegate {
        void didPressedNewCard();
    }

    /* JADX WARNING: Missing block: B:192:0x04c8, code:
            if (r3.equals("USERNAME_INVALID") != false) goto L_0x04b2;
     */
    /* JADX WARNING: Missing block: B:221:0x057c, code:
            if (r3.equals("BOT_PRECHECKOUT_FAILED") != false) goto L_0x056b;
     */
    public static android.app.Dialog processError(int r7, org.telegram.tgnet.TLRPC.TL_error r8, org.telegram.ui.ActionBar.BaseFragment r9, org.telegram.tgnet.TLObject r10, java.lang.Object... r11) {
        /*
        r6 = NUM; // 0x7f0CLASSNAMEf2 float:1.8610202E38 double:1.0530976445E-314;
        r5 = NUM; // 0x7f0CLASSNAMEbf float:1.8610618E38 double:1.053097746E-314;
        r4 = NUM; // 0x7f0CLASSNAME float:1.8610851E38 double:1.0530978026E-314;
        r2 = 1;
        r1 = 0;
        r0 = r8.code;
        r3 = 406; // 0x196 float:5.69E-43 double:2.006E-321;
        if (r0 == r3) goto L_0x0015;
    L_0x0011:
        r0 = r8.text;
        if (r0 != 0) goto L_0x0017;
    L_0x0015:
        r0 = 0;
    L_0x0016:
        return r0;
    L_0x0017:
        r0 = r10 instanceof org.telegram.tgnet.TLRPC.TL_account_saveSecureValue;
        if (r0 != 0) goto L_0x001f;
    L_0x001b:
        r0 = r10 instanceof org.telegram.tgnet.TLRPC.TL_account_getAuthorizationForm;
        if (r0 == 0) goto L_0x0091;
    L_0x001f:
        r0 = r8.text;
        r1 = "PHONE_NUMBER_INVALID";
        r0 = r0.contains(r1);
        if (r0 == 0) goto L_0x0039;
    L_0x002a:
        r0 = "InvalidPhoneNumber";
        r1 = NUM; // 0x7f0CLASSNAMEdb float:1.8611194E38 double:1.053097886E-314;
        r0 = org.telegram.messenger.LocaleController.getString(r0, r1);
        showSimpleAlert(r9, r0);
    L_0x0037:
        r0 = 0;
        goto L_0x0016;
    L_0x0039:
        r0 = r8.text;
        r1 = "FLOOD_WAIT";
        r0 = r0.startsWith(r1);
        if (r0 == 0) goto L_0x004f;
    L_0x0044:
        r0 = "FloodWait";
        r0 = org.telegram.messenger.LocaleController.getString(r0, r4);
        showSimpleAlert(r9, r0);
        goto L_0x0037;
    L_0x004f:
        r0 = "APP_VERSION_OUTDATED";
        r1 = r8.text;
        r0 = r0.equals(r1);
        if (r0 == 0) goto L_0x006c;
    L_0x005a:
        r0 = r9.getParentActivity();
        r1 = "UpdateAppAlert";
        r3 = NUM; // 0x7f0CLASSNAMEc float:1.8613597E38 double:1.0530984716E-314;
        r1 = org.telegram.messenger.LocaleController.getString(r1, r3);
        showUpdateAppAlert(r0, r1, r2);
        goto L_0x0037;
    L_0x006c:
        r0 = new java.lang.StringBuilder;
        r0.<init>();
        r1 = "ErrorOccurred";
        r1 = org.telegram.messenger.LocaleController.getString(r1, r5);
        r0 = r0.append(r1);
        r1 = "\n";
        r0 = r0.append(r1);
        r1 = r8.text;
        r0 = r0.append(r1);
        r0 = r0.toString();
        showSimpleAlert(r9, r0);
        goto L_0x0037;
    L_0x0091:
        r0 = r10 instanceof org.telegram.tgnet.TLRPC.TL_channels_joinChannel;
        if (r0 != 0) goto L_0x00b1;
    L_0x0095:
        r0 = r10 instanceof org.telegram.tgnet.TLRPC.TL_channels_editAdmin;
        if (r0 != 0) goto L_0x00b1;
    L_0x0099:
        r0 = r10 instanceof org.telegram.tgnet.TLRPC.TL_channels_inviteToChannel;
        if (r0 != 0) goto L_0x00b1;
    L_0x009d:
        r0 = r10 instanceof org.telegram.tgnet.TLRPC.TL_messages_addChatUser;
        if (r0 != 0) goto L_0x00b1;
    L_0x00a1:
        r0 = r10 instanceof org.telegram.tgnet.TLRPC.TL_messages_startBot;
        if (r0 != 0) goto L_0x00b1;
    L_0x00a5:
        r0 = r10 instanceof org.telegram.tgnet.TLRPC.TL_channels_editBanned;
        if (r0 != 0) goto L_0x00b1;
    L_0x00a9:
        r0 = r10 instanceof org.telegram.tgnet.TLRPC.TL_messages_editChatDefaultBannedRights;
        if (r0 != 0) goto L_0x00b1;
    L_0x00ad:
        r0 = r10 instanceof org.telegram.tgnet.TLRPC.TL_messages_editChatAdmin;
        if (r0 == 0) goto L_0x00e0;
    L_0x00b1:
        if (r9 == 0) goto L_0x00c2;
    L_0x00b3:
        r2 = r8.text;
        r0 = r11[r1];
        r0 = (java.lang.Boolean) r0;
        r0 = r0.booleanValue();
        showAddUserAlert(r2, r9, r0);
        goto L_0x0037;
    L_0x00c2:
        r0 = r8.text;
        r3 = "PEER_FLOOD";
        r0 = r0.equals(r3);
        if (r0 == 0) goto L_0x0037;
    L_0x00cd:
        r0 = org.telegram.messenger.NotificationCenter.getInstance(r7);
        r3 = org.telegram.messenger.NotificationCenter.needShowAlert;
        r4 = new java.lang.Object[r2];
        r2 = java.lang.Integer.valueOf(r2);
        r4[r1] = r2;
        r0.postNotificationName(r3, r4);
        goto L_0x0037;
    L_0x00e0:
        r0 = r10 instanceof org.telegram.tgnet.TLRPC.TL_messages_createChat;
        if (r0 == 0) goto L_0x00fd;
    L_0x00e4:
        r0 = r8.text;
        r2 = "FLOOD_WAIT";
        r0 = r0.startsWith(r2);
        if (r0 == 0) goto L_0x00f6;
    L_0x00ef:
        r0 = r8.text;
        showFloodWaitAlert(r0, r9);
        goto L_0x0037;
    L_0x00f6:
        r0 = r8.text;
        showAddUserAlert(r0, r9, r1);
        goto L_0x0037;
    L_0x00fd:
        r0 = r10 instanceof org.telegram.tgnet.TLRPC.TL_channels_createChannel;
        if (r0 == 0) goto L_0x011a;
    L_0x0101:
        r0 = r8.text;
        r2 = "FLOOD_WAIT";
        r0 = r0.startsWith(r2);
        if (r0 == 0) goto L_0x0113;
    L_0x010c:
        r0 = r8.text;
        showFloodWaitAlert(r0, r9);
        goto L_0x0037;
    L_0x0113:
        r0 = r8.text;
        showAddUserAlert(r0, r9, r1);
        goto L_0x0037;
    L_0x011a:
        r0 = r10 instanceof org.telegram.tgnet.TLRPC.TL_messages_editMessage;
        if (r0 == 0) goto L_0x0149;
    L_0x011e:
        r0 = r8.text;
        r1 = "MESSAGE_NOT_MODIFIED";
        r0 = r0.equals(r1);
        if (r0 != 0) goto L_0x0037;
    L_0x0129:
        if (r9 == 0) goto L_0x013a;
    L_0x012b:
        r0 = "EditMessageError";
        r1 = NUM; // 0x7f0CLASSNAME float:1.8610537E38 double:1.053097726E-314;
        r0 = org.telegram.messenger.LocaleController.getString(r0, r1);
        showSimpleAlert(r9, r0);
        goto L_0x0037;
    L_0x013a:
        r0 = "EditMessageError";
        r1 = NUM; // 0x7f0CLASSNAME float:1.8610537E38 double:1.053097726E-314;
        r0 = org.telegram.messenger.LocaleController.getString(r0, r1);
        showSimpleToast(r9, r0);
        goto L_0x0037;
    L_0x0149:
        r0 = r10 instanceof org.telegram.tgnet.TLRPC.TL_messages_sendMessage;
        if (r0 != 0) goto L_0x0161;
    L_0x014d:
        r0 = r10 instanceof org.telegram.tgnet.TLRPC.TL_messages_sendMedia;
        if (r0 != 0) goto L_0x0161;
    L_0x0151:
        r0 = r10 instanceof org.telegram.tgnet.TLRPC.TL_messages_sendBroadcast;
        if (r0 != 0) goto L_0x0161;
    L_0x0155:
        r0 = r10 instanceof org.telegram.tgnet.TLRPC.TL_messages_sendInlineBotResult;
        if (r0 != 0) goto L_0x0161;
    L_0x0159:
        r0 = r10 instanceof org.telegram.tgnet.TLRPC.TL_messages_forwardMessages;
        if (r0 != 0) goto L_0x0161;
    L_0x015d:
        r0 = r10 instanceof org.telegram.tgnet.TLRPC.TL_messages_sendMultiMedia;
        if (r0 == 0) goto L_0x019e;
    L_0x0161:
        r0 = r8.text;
        r3 = "PEER_FLOOD";
        r0 = r0.equals(r3);
        if (r0 == 0) goto L_0x017f;
    L_0x016c:
        r0 = org.telegram.messenger.NotificationCenter.getInstance(r7);
        r3 = org.telegram.messenger.NotificationCenter.needShowAlert;
        r2 = new java.lang.Object[r2];
        r4 = java.lang.Integer.valueOf(r1);
        r2[r1] = r4;
        r0.postNotificationName(r3, r2);
        goto L_0x0037;
    L_0x017f:
        r0 = r8.text;
        r3 = "USER_BANNED_IN_CHANNEL";
        r0 = r0.equals(r3);
        if (r0 == 0) goto L_0x0037;
    L_0x018a:
        r0 = org.telegram.messenger.NotificationCenter.getInstance(r7);
        r3 = org.telegram.messenger.NotificationCenter.needShowAlert;
        r2 = new java.lang.Object[r2];
        r4 = 5;
        r4 = java.lang.Integer.valueOf(r4);
        r2[r1] = r4;
        r0.postNotificationName(r3, r2);
        goto L_0x0037;
    L_0x019e:
        r0 = r10 instanceof org.telegram.tgnet.TLRPC.TL_messages_importChatInvite;
        if (r0 == 0) goto L_0x01e2;
    L_0x01a2:
        r0 = r8.text;
        r1 = "FLOOD_WAIT";
        r0 = r0.startsWith(r1);
        if (r0 == 0) goto L_0x01b9;
    L_0x01ad:
        r0 = "FloodWait";
        r0 = org.telegram.messenger.LocaleController.getString(r0, r4);
        showSimpleAlert(r9, r0);
        goto L_0x0037;
    L_0x01b9:
        r0 = r8.text;
        r1 = "USERS_TOO_MUCH";
        r0 = r0.equals(r1);
        if (r0 == 0) goto L_0x01d3;
    L_0x01c4:
        r0 = "JoinToGroupErrorFull";
        r1 = NUM; // 0x7f0CLASSNAMEf8 float:1.8611253E38 double:1.0530979004E-314;
        r0 = org.telegram.messenger.LocaleController.getString(r0, r1);
        showSimpleAlert(r9, r0);
        goto L_0x0037;
    L_0x01d3:
        r0 = "JoinToGroupErrorNotExist";
        r1 = NUM; // 0x7f0CLASSNAMEf9 float:1.8611255E38 double:1.053097901E-314;
        r0 = org.telegram.messenger.LocaleController.getString(r0, r1);
        showSimpleAlert(r9, r0);
        goto L_0x0037;
    L_0x01e2:
        r0 = r10 instanceof org.telegram.tgnet.TLRPC.TL_messages_getAttachedStickers;
        if (r0 == 0) goto L_0x021c;
    L_0x01e6:
        if (r9 == 0) goto L_0x0037;
    L_0x01e8:
        r0 = r9.getParentActivity();
        if (r0 == 0) goto L_0x0037;
    L_0x01ee:
        r0 = r9.getParentActivity();
        r2 = new java.lang.StringBuilder;
        r2.<init>();
        r3 = "ErrorOccurred";
        r3 = org.telegram.messenger.LocaleController.getString(r3, r5);
        r2 = r2.append(r3);
        r3 = "\n";
        r2 = r2.append(r3);
        r3 = r8.text;
        r2 = r2.append(r3);
        r2 = r2.toString();
        r0 = android.widget.Toast.makeText(r0, r2, r1);
        r0.show();
        goto L_0x0037;
    L_0x021c:
        r0 = r10 instanceof org.telegram.tgnet.TLRPC.TL_account_confirmPhone;
        if (r0 != 0) goto L_0x0228;
    L_0x0220:
        r0 = r10 instanceof org.telegram.tgnet.TLRPC.TL_account_verifyPhone;
        if (r0 != 0) goto L_0x0228;
    L_0x0224:
        r0 = r10 instanceof org.telegram.tgnet.TLRPC.TL_account_verifyEmail;
        if (r0 == 0) goto L_0x02a7;
    L_0x0228:
        r0 = r8.text;
        r1 = "PHONE_CODE_EMPTY";
        r0 = r0.contains(r1);
        if (r0 != 0) goto L_0x0254;
    L_0x0233:
        r0 = r8.text;
        r1 = "PHONE_CODE_INVALID";
        r0 = r0.contains(r1);
        if (r0 != 0) goto L_0x0254;
    L_0x023e:
        r0 = r8.text;
        r1 = "CODE_INVALID";
        r0 = r0.contains(r1);
        if (r0 != 0) goto L_0x0254;
    L_0x0249:
        r0 = r8.text;
        r1 = "CODE_EMPTY";
        r0 = r0.contains(r1);
        if (r0 == 0) goto L_0x0264;
    L_0x0254:
        r0 = "InvalidCode";
        r1 = NUM; // 0x7f0CLASSNAMEd8 float:1.8611188E38 double:1.0530978846E-314;
        r0 = org.telegram.messenger.LocaleController.getString(r0, r1);
        r0 = showSimpleAlert(r9, r0);
        goto L_0x0016;
    L_0x0264:
        r0 = r8.text;
        r1 = "PHONE_CODE_EXPIRED";
        r0 = r0.contains(r1);
        if (r0 != 0) goto L_0x027a;
    L_0x026f:
        r0 = r8.text;
        r1 = "EMAIL_VERIFY_EXPIRED";
        r0 = r0.contains(r1);
        if (r0 == 0) goto L_0x0287;
    L_0x027a:
        r0 = "CodeExpired";
        r0 = org.telegram.messenger.LocaleController.getString(r0, r6);
        r0 = showSimpleAlert(r9, r0);
        goto L_0x0016;
    L_0x0287:
        r0 = r8.text;
        r1 = "FLOOD_WAIT";
        r0 = r0.startsWith(r1);
        if (r0 == 0) goto L_0x029f;
    L_0x0292:
        r0 = "FloodWait";
        r0 = org.telegram.messenger.LocaleController.getString(r0, r4);
        r0 = showSimpleAlert(r9, r0);
        goto L_0x0016;
    L_0x029f:
        r0 = r8.text;
        r0 = showSimpleAlert(r9, r0);
        goto L_0x0016;
    L_0x02a7:
        r0 = r10 instanceof org.telegram.tgnet.TLRPC.TL_auth_resendCode;
        if (r0 == 0) goto L_0x0349;
    L_0x02ab:
        r0 = r8.text;
        r1 = "PHONE_NUMBER_INVALID";
        r0 = r0.contains(r1);
        if (r0 == 0) goto L_0x02c6;
    L_0x02b6:
        r0 = "InvalidPhoneNumber";
        r1 = NUM; // 0x7f0CLASSNAMEdb float:1.8611194E38 double:1.053097886E-314;
        r0 = org.telegram.messenger.LocaleController.getString(r0, r1);
        r0 = showSimpleAlert(r9, r0);
        goto L_0x0016;
    L_0x02c6:
        r0 = r8.text;
        r1 = "PHONE_CODE_EMPTY";
        r0 = r0.contains(r1);
        if (r0 != 0) goto L_0x02dc;
    L_0x02d1:
        r0 = r8.text;
        r1 = "PHONE_CODE_INVALID";
        r0 = r0.contains(r1);
        if (r0 == 0) goto L_0x02ec;
    L_0x02dc:
        r0 = "InvalidCode";
        r1 = NUM; // 0x7f0CLASSNAMEd8 float:1.8611188E38 double:1.0530978846E-314;
        r0 = org.telegram.messenger.LocaleController.getString(r0, r1);
        r0 = showSimpleAlert(r9, r0);
        goto L_0x0016;
    L_0x02ec:
        r0 = r8.text;
        r1 = "PHONE_CODE_EXPIRED";
        r0 = r0.contains(r1);
        if (r0 == 0) goto L_0x0304;
    L_0x02f7:
        r0 = "CodeExpired";
        r0 = org.telegram.messenger.LocaleController.getString(r0, r6);
        r0 = showSimpleAlert(r9, r0);
        goto L_0x0016;
    L_0x0304:
        r0 = r8.text;
        r1 = "FLOOD_WAIT";
        r0 = r0.startsWith(r1);
        if (r0 == 0) goto L_0x031c;
    L_0x030f:
        r0 = "FloodWait";
        r0 = org.telegram.messenger.LocaleController.getString(r0, r4);
        r0 = showSimpleAlert(r9, r0);
        goto L_0x0016;
    L_0x031c:
        r0 = r8.code;
        r1 = -1000; // 0xfffffffffffffCLASSNAME float:NaN double:NaN;
        if (r0 == r1) goto L_0x0037;
    L_0x0322:
        r0 = new java.lang.StringBuilder;
        r0.<init>();
        r1 = "ErrorOccurred";
        r1 = org.telegram.messenger.LocaleController.getString(r1, r5);
        r0 = r0.append(r1);
        r1 = "\n";
        r0 = r0.append(r1);
        r1 = r8.text;
        r0 = r0.append(r1);
        r0 = r0.toString();
        r0 = showSimpleAlert(r9, r0);
        goto L_0x0016;
    L_0x0349:
        r0 = r10 instanceof org.telegram.tgnet.TLRPC.TL_account_sendConfirmPhoneCode;
        if (r0 == 0) goto L_0x038c;
    L_0x034d:
        r0 = r8.code;
        r1 = 400; // 0x190 float:5.6E-43 double:1.976E-321;
        if (r0 != r1) goto L_0x0363;
    L_0x0353:
        r0 = "CancelLinkExpired";
        r1 = NUM; // 0x7f0CLASSNAMEc float:1.8609833E38 double:1.0530975546E-314;
        r0 = org.telegram.messenger.LocaleController.getString(r0, r1);
        r0 = showSimpleAlert(r9, r0);
        goto L_0x0016;
    L_0x0363:
        r0 = r8.text;
        if (r0 == 0) goto L_0x0037;
    L_0x0367:
        r0 = r8.text;
        r1 = "FLOOD_WAIT";
        r0 = r0.startsWith(r1);
        if (r0 == 0) goto L_0x037f;
    L_0x0372:
        r0 = "FloodWait";
        r0 = org.telegram.messenger.LocaleController.getString(r0, r4);
        r0 = showSimpleAlert(r9, r0);
        goto L_0x0016;
    L_0x037f:
        r0 = "ErrorOccurred";
        r0 = org.telegram.messenger.LocaleController.getString(r0, r5);
        r0 = showSimpleAlert(r9, r0);
        goto L_0x0016;
    L_0x038c:
        r0 = r10 instanceof org.telegram.tgnet.TLRPC.TL_account_changePhone;
        if (r0 == 0) goto L_0x0404;
    L_0x0390:
        r0 = r8.text;
        r1 = "PHONE_NUMBER_INVALID";
        r0 = r0.contains(r1);
        if (r0 == 0) goto L_0x03aa;
    L_0x039b:
        r0 = "InvalidPhoneNumber";
        r1 = NUM; // 0x7f0CLASSNAMEdb float:1.8611194E38 double:1.053097886E-314;
        r0 = org.telegram.messenger.LocaleController.getString(r0, r1);
        showSimpleAlert(r9, r0);
        goto L_0x0037;
    L_0x03aa:
        r0 = r8.text;
        r1 = "PHONE_CODE_EMPTY";
        r0 = r0.contains(r1);
        if (r0 != 0) goto L_0x03c0;
    L_0x03b5:
        r0 = r8.text;
        r1 = "PHONE_CODE_INVALID";
        r0 = r0.contains(r1);
        if (r0 == 0) goto L_0x03cf;
    L_0x03c0:
        r0 = "InvalidCode";
        r1 = NUM; // 0x7f0CLASSNAMEd8 float:1.8611188E38 double:1.0530978846E-314;
        r0 = org.telegram.messenger.LocaleController.getString(r0, r1);
        showSimpleAlert(r9, r0);
        goto L_0x0037;
    L_0x03cf:
        r0 = r8.text;
        r1 = "PHONE_CODE_EXPIRED";
        r0 = r0.contains(r1);
        if (r0 == 0) goto L_0x03e6;
    L_0x03da:
        r0 = "CodeExpired";
        r0 = org.telegram.messenger.LocaleController.getString(r0, r6);
        showSimpleAlert(r9, r0);
        goto L_0x0037;
    L_0x03e6:
        r0 = r8.text;
        r1 = "FLOOD_WAIT";
        r0 = r0.startsWith(r1);
        if (r0 == 0) goto L_0x03fd;
    L_0x03f1:
        r0 = "FloodWait";
        r0 = org.telegram.messenger.LocaleController.getString(r0, r4);
        showSimpleAlert(r9, r0);
        goto L_0x0037;
    L_0x03fd:
        r0 = r8.text;
        showSimpleAlert(r9, r0);
        goto L_0x0037;
    L_0x0404:
        r0 = r10 instanceof org.telegram.tgnet.TLRPC.TL_account_sendChangePhoneCode;
        if (r0 == 0) goto L_0x04a3;
    L_0x0408:
        r0 = r8.text;
        r3 = "PHONE_NUMBER_INVALID";
        r0 = r0.contains(r3);
        if (r0 == 0) goto L_0x0422;
    L_0x0413:
        r0 = "InvalidPhoneNumber";
        r1 = NUM; // 0x7f0CLASSNAMEdb float:1.8611194E38 double:1.053097886E-314;
        r0 = org.telegram.messenger.LocaleController.getString(r0, r1);
        showSimpleAlert(r9, r0);
        goto L_0x0037;
    L_0x0422:
        r0 = r8.text;
        r3 = "PHONE_CODE_EMPTY";
        r0 = r0.contains(r3);
        if (r0 != 0) goto L_0x0438;
    L_0x042d:
        r0 = r8.text;
        r3 = "PHONE_CODE_INVALID";
        r0 = r0.contains(r3);
        if (r0 == 0) goto L_0x0447;
    L_0x0438:
        r0 = "InvalidCode";
        r1 = NUM; // 0x7f0CLASSNAMEd8 float:1.8611188E38 double:1.0530978846E-314;
        r0 = org.telegram.messenger.LocaleController.getString(r0, r1);
        showSimpleAlert(r9, r0);
        goto L_0x0037;
    L_0x0447:
        r0 = r8.text;
        r3 = "PHONE_CODE_EXPIRED";
        r0 = r0.contains(r3);
        if (r0 == 0) goto L_0x045e;
    L_0x0452:
        r0 = "CodeExpired";
        r0 = org.telegram.messenger.LocaleController.getString(r0, r6);
        showSimpleAlert(r9, r0);
        goto L_0x0037;
    L_0x045e:
        r0 = r8.text;
        r3 = "FLOOD_WAIT";
        r0 = r0.startsWith(r3);
        if (r0 == 0) goto L_0x0475;
    L_0x0469:
        r0 = "FloodWait";
        r0 = org.telegram.messenger.LocaleController.getString(r0, r4);
        showSimpleAlert(r9, r0);
        goto L_0x0037;
    L_0x0475:
        r0 = r8.text;
        r3 = "PHONE_NUMBER_OCCUPIED";
        r0 = r0.startsWith(r3);
        if (r0 == 0) goto L_0x0497;
    L_0x0480:
        r3 = "ChangePhoneNumberOccupied";
        r4 = NUM; // 0x7f0CLASSNAMEe float:1.860987E38 double:1.0530975635E-314;
        r2 = new java.lang.Object[r2];
        r0 = r11[r1];
        r0 = (java.lang.String) r0;
        r2[r1] = r0;
        r0 = org.telegram.messenger.LocaleController.formatString(r3, r4, r2);
        showSimpleAlert(r9, r0);
        goto L_0x0037;
    L_0x0497:
        r0 = "ErrorOccurred";
        r0 = org.telegram.messenger.LocaleController.getString(r0, r5);
        showSimpleAlert(r9, r0);
        goto L_0x0037;
    L_0x04a3:
        r0 = r10 instanceof org.telegram.tgnet.TLRPC.TL_updateUserName;
        if (r0 == 0) goto L_0x04f4;
    L_0x04a7:
        r3 = r8.text;
        r0 = -1;
        r4 = r3.hashCode();
        switch(r4) {
            case 288843630: goto L_0x04c1;
            case 533175271: goto L_0x04cb;
            default: goto L_0x04b1;
        };
    L_0x04b1:
        r1 = r0;
    L_0x04b2:
        switch(r1) {
            case 0: goto L_0x04d6;
            case 1: goto L_0x04e5;
            default: goto L_0x04b5;
        };
    L_0x04b5:
        r0 = "ErrorOccurred";
        r0 = org.telegram.messenger.LocaleController.getString(r0, r5);
        showSimpleAlert(r9, r0);
        goto L_0x0037;
    L_0x04c1:
        r2 = "USERNAME_INVALID";
        r2 = r3.equals(r2);
        if (r2 == 0) goto L_0x04b1;
    L_0x04ca:
        goto L_0x04b2;
    L_0x04cb:
        r1 = "USERNAME_OCCUPIED";
        r1 = r3.equals(r1);
        if (r1 == 0) goto L_0x04b1;
    L_0x04d4:
        r1 = r2;
        goto L_0x04b2;
    L_0x04d6:
        r0 = "UsernameInvalid";
        r1 = NUM; // 0x7f0CLASSNAMEc6 float:1.8613747E38 double:1.053098508E-314;
        r0 = org.telegram.messenger.LocaleController.getString(r0, r1);
        showSimpleAlert(r9, r0);
        goto L_0x0037;
    L_0x04e5:
        r0 = "UsernameInUse";
        r1 = NUM; // 0x7f0CLASSNAMEc5 float:1.8613745E38 double:1.0530985076E-314;
        r0 = org.telegram.messenger.LocaleController.getString(r0, r1);
        showSimpleAlert(r9, r0);
        goto L_0x0037;
    L_0x04f4:
        r0 = r10 instanceof org.telegram.tgnet.TLRPC.TL_contacts_importContacts;
        if (r0 == 0) goto L_0x0537;
    L_0x04f8:
        if (r8 == 0) goto L_0x0505;
    L_0x04fa:
        r0 = r8.text;
        r1 = "FLOOD_WAIT";
        r0 = r0.startsWith(r1);
        if (r0 == 0) goto L_0x0511;
    L_0x0505:
        r0 = "FloodWait";
        r0 = org.telegram.messenger.LocaleController.getString(r0, r4);
        showSimpleAlert(r9, r0);
        goto L_0x0037;
    L_0x0511:
        r0 = new java.lang.StringBuilder;
        r0.<init>();
        r1 = "ErrorOccurred";
        r1 = org.telegram.messenger.LocaleController.getString(r1, r5);
        r0 = r0.append(r1);
        r1 = "\n";
        r0 = r0.append(r1);
        r1 = r8.text;
        r0 = r0.append(r1);
        r0 = r0.toString();
        showSimpleAlert(r9, r0);
        goto L_0x0037;
    L_0x0537:
        r0 = r10 instanceof org.telegram.tgnet.TLRPC.TL_account_getPassword;
        if (r0 != 0) goto L_0x053f;
    L_0x053b:
        r0 = r10 instanceof org.telegram.tgnet.TLRPC.TL_account_getTmpPassword;
        if (r0 == 0) goto L_0x055c;
    L_0x053f:
        r0 = r8.text;
        r1 = "FLOOD_WAIT";
        r0 = r0.startsWith(r1);
        if (r0 == 0) goto L_0x0555;
    L_0x054a:
        r0 = r8.text;
        r0 = getFloodWaitString(r0);
        showSimpleToast(r9, r0);
        goto L_0x0037;
    L_0x0555:
        r0 = r8.text;
        showSimpleToast(r9, r0);
        goto L_0x0037;
    L_0x055c:
        r0 = r10 instanceof org.telegram.tgnet.TLRPC.TL_payments_sendPaymentForm;
        if (r0 == 0) goto L_0x05a8;
    L_0x0560:
        r3 = r8.text;
        r0 = -1;
        r4 = r3.hashCode();
        switch(r4) {
            case -1144062453: goto L_0x0575;
            case -784238410: goto L_0x057f;
            default: goto L_0x056a;
        };
    L_0x056a:
        r1 = r0;
    L_0x056b:
        switch(r1) {
            case 0: goto L_0x058a;
            case 1: goto L_0x0599;
            default: goto L_0x056e;
        };
    L_0x056e:
        r0 = r8.text;
        showSimpleToast(r9, r0);
        goto L_0x0037;
    L_0x0575:
        r2 = "BOT_PRECHECKOUT_FAILED";
        r2 = r3.equals(r2);
        if (r2 == 0) goto L_0x056a;
    L_0x057e:
        goto L_0x056b;
    L_0x057f:
        r1 = "PAYMENT_FAILED";
        r1 = r3.equals(r1);
        if (r1 == 0) goto L_0x056a;
    L_0x0588:
        r1 = r2;
        goto L_0x056b;
    L_0x058a:
        r0 = "PaymentPrecheckoutFailed";
        r1 = NUM; // 0x7f0CLASSNAME float:1.8612573E38 double:1.053098222E-314;
        r0 = org.telegram.messenger.LocaleController.getString(r0, r1);
        showSimpleToast(r9, r0);
        goto L_0x0037;
    L_0x0599:
        r0 = "PaymentFailed";
        r1 = NUM; // 0x7f0CLASSNAME float:1.8612547E38 double:1.0530982156E-314;
        r0 = org.telegram.messenger.LocaleController.getString(r0, r1);
        showSimpleToast(r9, r0);
        goto L_0x0037;
    L_0x05a8:
        r0 = r10 instanceof org.telegram.tgnet.TLRPC.TL_payments_validateRequestedInfo;
        if (r0 == 0) goto L_0x0037;
    L_0x05ac:
        r2 = r8.text;
        r0 = -1;
        r3 = r2.hashCode();
        switch(r3) {
            case 1758025548: goto L_0x05c0;
            default: goto L_0x05b6;
        };
    L_0x05b6:
        switch(r0) {
            case 0: goto L_0x05cb;
            default: goto L_0x05b9;
        };
    L_0x05b9:
        r0 = r8.text;
        showSimpleToast(r9, r0);
        goto L_0x0037;
    L_0x05c0:
        r3 = "SHIPPING_NOT_AVAILABLE";
        r2 = r2.equals(r3);
        if (r2 == 0) goto L_0x05b6;
    L_0x05c9:
        r0 = r1;
        goto L_0x05b6;
    L_0x05cb:
        r0 = "PaymentNoShippingMethod";
        r1 = NUM; // 0x7f0CLASSNAME float:1.861255E38 double:1.0530982166E-314;
        r0 = org.telegram.messenger.LocaleController.getString(r0, r1);
        showSimpleToast(r9, r0);
        goto L_0x0037;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.AlertsCreator.processError(int, org.telegram.tgnet.TLRPC$TL_error, org.telegram.ui.ActionBar.BaseFragment, org.telegram.tgnet.TLObject, java.lang.Object[]):android.app.Dialog");
    }

    public static Toast showSimpleToast(BaseFragment baseFragment, String text) {
        if (text == null) {
            return null;
        }
        Context context;
        if (baseFragment == null || baseFragment.getParentActivity() == null) {
            context = ApplicationLoader.applicationContext;
        } else {
            context = baseFragment.getParentActivity();
        }
        Toast toast = Toast.makeText(context, text, 1);
        toast.show();
        return toast;
    }

    public static AlertDialog showUpdateAppAlert(Context context, String text, boolean updateApp) {
        if (context == null || text == null) {
            return null;
        }
        Builder builder = new Builder(context);
        builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
        builder.setMessage(text);
        builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), null);
        if (updateApp) {
            builder.setNegativeButton(LocaleController.getString("UpdateApp", R.string.UpdateApp), new AlertsCreator$$Lambda$0(context));
        }
        return builder.show();
    }

    public static Builder createLanguageAlert(LaunchActivity activity, TL_langPackLanguage language) {
        if (language == null) {
            return null;
        }
        String str;
        int end;
        language.lang_code = language.lang_code.replace('-', '_').toLowerCase();
        language.plural_code = language.plural_code.replace('-', '_').toLowerCase();
        if (language.base_lang_code != null) {
            language.base_lang_code = language.base_lang_code.replace('-', '_').toLowerCase();
        }
        final Builder builder = new Builder((Context) activity);
        if (LocaleController.getInstance().getCurrentLocaleInfo().shortName.equals(language.lang_code)) {
            builder.setTitle(LocaleController.getString("Language", R.string.Language));
            str = LocaleController.formatString("LanguageSame", R.string.LanguageSame, language.name);
            builder.setNegativeButton(LocaleController.getString("OK", R.string.OK), null);
            builder.setNeutralButton(LocaleController.getString("SETTINGS", R.string.SETTINGS), new AlertsCreator$$Lambda$1(activity));
        } else if (language.strings_count == 0) {
            builder.setTitle(LocaleController.getString("LanguageUnknownTitle", R.string.LanguageUnknownTitle));
            str = LocaleController.formatString("LanguageUnknownCustomAlert", R.string.LanguageUnknownCustomAlert, language.name);
            builder.setNegativeButton(LocaleController.getString("OK", R.string.OK), null);
        } else {
            builder.setTitle(LocaleController.getString("LanguageTitle", R.string.LanguageTitle));
            if (language.official) {
                str = LocaleController.formatString("LanguageAlert", R.string.LanguageAlert, language.name, Integer.valueOf((int) Math.ceil((double) ((((float) language.translated_count) / ((float) language.strings_count)) * 100.0f))));
            } else {
                str = LocaleController.formatString("LanguageCustomAlert", R.string.LanguageCustomAlert, language.name, Integer.valueOf((int) Math.ceil((double) ((((float) language.translated_count) / ((float) language.strings_count)) * 100.0f))));
            }
            builder.setPositiveButton(LocaleController.getString("Change", R.string.Change), new AlertsCreator$$Lambda$2(language, activity));
            builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
        }
        SpannableStringBuilder spanned = new SpannableStringBuilder(AndroidUtilities.replaceTags(str));
        int start = TextUtils.indexOf(spanned, '[');
        if (start != -1) {
            end = TextUtils.indexOf(spanned, ']', start + 1);
            if (!(start == -1 || end == -1)) {
                spanned.delete(end, end + 1);
                spanned.delete(start, start + 1);
            }
        } else {
            end = -1;
        }
        if (!(start == -1 || end == -1)) {
            spanned.setSpan(new URLSpanNoUnderline(language.translations_url) {
                public void onClick(View widget) {
                    builder.getDismissRunnable().run();
                    super.onClick(widget);
                }
            }, start, end - 1, 33);
        }
        TextView message = new TextView(activity);
        message.setText(spanned);
        message.setTextSize(1, 16.0f);
        message.setLinkTextColor(Theme.getColor("dialogTextLink"));
        message.setHighlightColor(Theme.getColor("dialogLinkSelection"));
        message.setPadding(AndroidUtilities.dp(23.0f), 0, AndroidUtilities.dp(23.0f), 0);
        message.setMovementMethod(new LinkMovementMethodMy());
        message.setTextColor(Theme.getColor("dialogTextBlack"));
        builder.setView(message);
        return builder;
    }

    static final /* synthetic */ void lambda$createLanguageAlert$2$AlertsCreator(TL_langPackLanguage language, LaunchActivity activity, DialogInterface dialogInterface, int i) {
        String key;
        if (language.official) {
            key = "remote_" + language.lang_code;
        } else {
            key = "unofficial_" + language.lang_code;
        }
        LocaleInfo localeInfo = LocaleController.getInstance().getLanguageFromDict(key);
        if (localeInfo == null) {
            localeInfo = new LocaleInfo();
            localeInfo.name = language.native_name;
            localeInfo.nameEnglish = language.name;
            localeInfo.shortName = language.lang_code;
            localeInfo.baseLangCode = language.base_lang_code;
            localeInfo.pluralLangCode = language.plural_code;
            localeInfo.isRtl = language.rtl;
            if (language.official) {
                localeInfo.pathToFile = "remote";
            } else {
                localeInfo.pathToFile = "unofficial";
            }
        }
        LocaleController.getInstance().applyLanguage(localeInfo, true, false, false, true, UserConfig.selectedAccount);
        activity.rebuildAllFragments(true);
    }

    public static Builder createSimpleAlert(Context context, String text) {
        if (text == null) {
            return null;
        }
        Builder builder = new Builder(context);
        builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
        builder.setMessage(text);
        builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), null);
        return builder;
    }

    public static Dialog showSimpleAlert(BaseFragment baseFragment, String text) {
        if (text == null || baseFragment == null || baseFragment.getParentActivity() == null) {
            return null;
        }
        Dialog dialog = createSimpleAlert(baseFragment.getParentActivity(), text).create();
        baseFragment.showDialog(dialog);
        return dialog;
    }

    public static void showCustomNotificationsDialog(BaseFragment parentFragment, long did, int globalType, ArrayList<NotificationException> exceptions, int currentAccount, IntCallback callback) {
        showCustomNotificationsDialog(parentFragment, did, globalType, exceptions, currentAccount, callback, null);
    }

    public static void showCustomNotificationsDialog(BaseFragment parentFragment, long did, int globalType, ArrayList<NotificationException> exceptions, int currentAccount, IntCallback callback, IntCallback resultCallback) {
        if (parentFragment != null && parentFragment.getParentActivity() != null) {
            String str;
            boolean defaultEnabled = NotificationsController.getInstance(currentAccount).isGlobalNotificationsEnabled(did);
            String[] descriptions = new String[5];
            descriptions[0] = LocaleController.getString("NotificationsTurnOn", R.string.NotificationsTurnOn);
            descriptions[1] = LocaleController.formatString("MuteFor", R.string.MuteFor, LocaleController.formatPluralString("Hours", 1));
            descriptions[2] = LocaleController.formatString("MuteFor", R.string.MuteFor, LocaleController.formatPluralString("Days", 2));
            if (did == 0 && (parentFragment instanceof NotificationsCustomSettingsActivity)) {
                str = null;
            } else {
                str = LocaleController.getString("NotificationsCustomize", R.string.NotificationsCustomize);
            }
            descriptions[3] = str;
            descriptions[4] = LocaleController.getString("NotificationsTurnOff", R.string.NotificationsTurnOff);
            int[] iArr = new int[5];
            iArr = new int[]{R.drawable.notifications_on, R.drawable.notifications_mute1h, R.drawable.notifications_mute2d, R.drawable.notifications_settings, R.drawable.notifications_off};
            View linearLayout = new LinearLayout(parentFragment.getParentActivity());
            linearLayout.setOrientation(1);
            Builder builder = new Builder(parentFragment.getParentActivity());
            for (int a = 0; a < descriptions.length; a++) {
                if (descriptions[a] != null) {
                    linearLayout = new TextView(parentFragment.getParentActivity());
                    Drawable drawable = parentFragment.getParentActivity().getResources().getDrawable(iArr[a]);
                    if (a == descriptions.length - 1) {
                        linearLayout.setTextColor(Theme.getColor("dialogTextRed"));
                        drawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor("dialogRedIcon"), Mode.MULTIPLY));
                    } else {
                        linearLayout.setTextColor(Theme.getColor("dialogTextBlack"));
                        drawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor("dialogIcon"), Mode.MULTIPLY));
                    }
                    linearLayout.setTextSize(1, 16.0f);
                    linearLayout.setLines(1);
                    linearLayout.setMaxLines(1);
                    linearLayout.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
                    linearLayout.setTag(Integer.valueOf(a));
                    linearLayout.setBackgroundDrawable(Theme.getSelectorDrawable(false));
                    linearLayout.setPadding(AndroidUtilities.dp(24.0f), 0, AndroidUtilities.dp(24.0f), 0);
                    linearLayout.setSingleLine(true);
                    linearLayout.setGravity(19);
                    linearLayout.setCompoundDrawablePadding(AndroidUtilities.dp(26.0f));
                    linearLayout.setText(descriptions[a]);
                    linearLayout.addView(linearLayout, LayoutHelper.createLinear(-1, 48, 51));
                    linearLayout.setOnClickListener(new AlertsCreator$$Lambda$3(did, currentAccount, defaultEnabled, resultCallback, globalType, parentFragment, exceptions, callback, builder));
                }
            }
            builder.setTitle(LocaleController.getString("Notifications", R.string.Notifications));
            builder.setView(linearLayout);
            parentFragment.showDialog(builder.create());
        }
    }

    static final /* synthetic */ void lambda$showCustomNotificationsDialog$3$AlertsCreator(long did, int currentAccount, boolean defaultEnabled, IntCallback resultCallback, int globalType, BaseFragment parentFragment, ArrayList exceptions, IntCallback callback, Builder builder, View v) {
        int i = ((Integer) v.getTag()).intValue();
        Editor editor;
        TL_dialog dialog;
        if (i == 0) {
            if (did != 0) {
                editor = MessagesController.getNotificationsSettings(currentAccount).edit();
                if (defaultEnabled) {
                    editor.remove("notify2_" + did);
                } else {
                    editor.putInt("notify2_" + did, 0);
                }
                MessagesStorage.getInstance(currentAccount).setDialogFlags(did, 0);
                editor.commit();
                dialog = (TL_dialog) MessagesController.getInstance(currentAccount).dialogs_dict.get(did);
                if (dialog != null) {
                    dialog.notify_settings = new TL_peerNotifySettings();
                }
                NotificationsController.getInstance(currentAccount).updateServerNotificationsSettings(did);
                if (resultCallback != null) {
                    if (defaultEnabled) {
                        resultCallback.lambda$null$87$MessagesStorage(0);
                    } else {
                        resultCallback.lambda$null$87$MessagesStorage(1);
                    }
                }
            } else {
                NotificationsController.getInstance(currentAccount).setGlobalNotificationsEnabled(globalType, 0);
            }
        } else if (i != 3) {
            int untilTime = ConnectionsManager.getInstance(currentAccount).getCurrentTime();
            if (i == 1) {
                untilTime += 3600;
            } else if (i == 2) {
                untilTime += 172800;
            } else if (i == 4) {
                untilTime = Integer.MAX_VALUE;
            }
            if (did != 0) {
                long flags;
                editor = MessagesController.getNotificationsSettings(currentAccount).edit();
                if (i != 4) {
                    editor.putInt("notify2_" + did, 3);
                    editor.putInt("notifyuntil_" + did, untilTime);
                    flags = (((long) untilTime) << 32) | 1;
                } else if (defaultEnabled) {
                    editor.putInt("notify2_" + did, 2);
                    flags = 1;
                } else {
                    editor.remove("notify2_" + did);
                    flags = 0;
                }
                NotificationsController.getInstance(currentAccount).removeNotificationsForDialog(did);
                MessagesStorage.getInstance(currentAccount).setDialogFlags(did, flags);
                editor.commit();
                dialog = (TL_dialog) MessagesController.getInstance(currentAccount).dialogs_dict.get(did);
                if (dialog != null) {
                    dialog.notify_settings = new TL_peerNotifySettings();
                    if (i != 4 || defaultEnabled) {
                        dialog.notify_settings.mute_until = untilTime;
                    }
                }
                NotificationsController.getInstance(currentAccount).updateServerNotificationsSettings(did);
                if (resultCallback != null) {
                    if (i != 4 || defaultEnabled) {
                        resultCallback.lambda$null$87$MessagesStorage(1);
                    } else {
                        resultCallback.lambda$null$87$MessagesStorage(0);
                    }
                }
            } else if (i == 4) {
                NotificationsController.getInstance(currentAccount).setGlobalNotificationsEnabled(globalType, Integer.MAX_VALUE);
            } else {
                NotificationsController.getInstance(currentAccount).setGlobalNotificationsEnabled(globalType, untilTime);
            }
        } else if (did != 0) {
            Bundle args = new Bundle();
            args.putLong("dialog_id", did);
            parentFragment.presentFragment(new ProfileNotificationsActivity(args));
        } else {
            parentFragment.presentFragment(new NotificationsCustomSettingsActivity(globalType, exceptions));
        }
        if (callback != null) {
            callback.lambda$null$87$MessagesStorage(i);
        }
        builder.getDismissRunnable().run();
    }

    public static AlertDialog showSecretLocationAlert(Context context, int currentAccount, Runnable onSelectRunnable, boolean inChat) {
        ArrayList<String> arrayList = new ArrayList();
        int providers = MessagesController.getInstance(currentAccount).availableMapProviders;
        if ((providers & 1) != 0) {
            arrayList.add(LocaleController.getString("MapPreviewProviderTelegram", R.string.MapPreviewProviderTelegram));
        }
        if ((providers & 2) != 0) {
            arrayList.add(LocaleController.getString("MapPreviewProviderGoogle", R.string.MapPreviewProviderGoogle));
        }
        if ((providers & 4) != 0) {
            arrayList.add(LocaleController.getString("MapPreviewProviderYandex", R.string.MapPreviewProviderYandex));
        }
        arrayList.add(LocaleController.getString("MapPreviewProviderNobody", R.string.MapPreviewProviderNobody));
        Builder builder = new Builder(context).setTitle(LocaleController.getString("ChooseMapPreviewProvider", R.string.ChooseMapPreviewProvider)).setItems((CharSequence[]) arrayList.toArray(new String[0]), new AlertsCreator$$Lambda$4(onSelectRunnable));
        if (!inChat) {
            builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
        }
        AlertDialog dialog = builder.show();
        if (inChat) {
            dialog.setCanceledOnTouchOutside(false);
        }
        return dialog;
    }

    static final /* synthetic */ void lambda$showSecretLocationAlert$4$AlertsCreator(Runnable onSelectRunnable, DialogInterface dialog, int which) {
        SharedConfig.setSecretMapPreviewType(which);
        if (onSelectRunnable != null) {
            onSelectRunnable.run();
        }
    }

    private static void updateDayPicker(NumberPicker dayPicker, NumberPicker monthPicker, NumberPicker yearPicker) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(2, monthPicker.getValue());
        calendar.set(1, yearPicker.getValue());
        dayPicker.setMinValue(1);
        dayPicker.setMaxValue(calendar.getActualMaximum(5));
    }

    private static void checkPickerDate(NumberPicker dayPicker, NumberPicker monthPicker, NumberPicker yearPicker) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        int currentYear = calendar.get(1);
        int currentMonth = calendar.get(2);
        int currentDay = calendar.get(5);
        if (currentYear > yearPicker.getValue()) {
            yearPicker.setValue(currentYear);
        }
        if (yearPicker.getValue() == currentYear) {
            if (currentMonth > monthPicker.getValue()) {
                monthPicker.setValue(currentMonth);
            }
            if (currentMonth == monthPicker.getValue() && currentDay > dayPicker.getValue()) {
                dayPicker.setValue(currentDay);
            }
        }
    }

    public static void createClearOrDeleteDialogAlert(BaseFragment fragment, boolean clear, Chat chat, User user, boolean secret, Runnable onProcessRunnable) {
        if (fragment != null && fragment.getParentActivity() != null) {
            if (chat != null || user != null) {
                String actionText;
                Context context = fragment.getParentActivity();
                Builder builder = new Builder(context);
                View frameLayout = new FrameLayout(context);
                builder.setView(frameLayout);
                Drawable avatarDrawable = new AvatarDrawable();
                avatarDrawable.setTextSize(AndroidUtilities.dp(12.0f));
                frameLayout = new BackupImageView(context);
                frameLayout.setRoundRadius(AndroidUtilities.dp(20.0f));
                frameLayout.addView(frameLayout, LayoutHelper.createFrame(40, 40.0f, 51, 22.0f, 5.0f, 0.0f, 0.0f));
                frameLayout = new TextView(context);
                frameLayout.setTextColor(Theme.getColor("actionBarDefaultSubmenuItem"));
                frameLayout.setTextSize(1, 20.0f);
                frameLayout.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
                frameLayout.setLines(1);
                frameLayout.setMaxLines(1);
                frameLayout.setSingleLine(true);
                frameLayout.setGravity(19);
                frameLayout.setEllipsize(TruncateAt.END);
                if (clear) {
                    frameLayout.setText(LocaleController.getString("ClearHistory", R.string.ClearHistory));
                } else {
                    frameLayout.setText(LocaleController.getString("DeleteChatUser", R.string.DeleteChatUser));
                }
                frameLayout.addView(frameLayout, LayoutHelper.createFrame(-1, -1.0f, 51, 76.0f, 11.0f, 21.0f, 0.0f));
                frameLayout = new TextView(context);
                frameLayout.setTextColor(Theme.getColor("dialogTextBlack"));
                frameLayout.setTextSize(1, 16.0f);
                frameLayout.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
                frameLayout.addView(frameLayout, LayoutHelper.createFrame(-2, -2.0f, (LocaleController.isRTL ? 5 : 3) | 48, 24.0f, 57.0f, 24.0f, 9.0f));
                TLObject avatar = null;
                if (user != null) {
                    avatarDrawable.setInfo(user);
                    if (!(user.photo == null || user.photo.photo_small == null || user.photo.photo_small.volume_id == 0 || user.photo.photo_small.local_id == 0)) {
                        avatar = user.photo.photo_small;
                    }
                } else if (chat != null) {
                    avatarDrawable.setInfo(chat);
                    if (!(chat.photo == null || chat.photo.photo_small == null || chat.photo.photo_small.volume_id == 0 || chat.photo.photo_small.local_id == 0)) {
                        avatar = chat.photo.photo_small;
                    }
                }
                frameLayout.setImage(avatar, "50_50", avatarDrawable, (Object) user);
                if (clear) {
                    if (user != null) {
                        if (secret) {
                            frameLayout.setText(AndroidUtilities.replaceTags(LocaleController.formatString("AreYouSureClearHistoryWithSecretUser", R.string.AreYouSureClearHistoryWithSecretUser, ContactsController.formatName(user.first_name, user.last_name))));
                        } else {
                            frameLayout.setText(AndroidUtilities.replaceTags(LocaleController.formatString("AreYouSureClearHistoryWithUser", R.string.AreYouSureClearHistoryWithUser, ContactsController.formatName(user.first_name, user.last_name))));
                        }
                    } else if (chat != null) {
                        if (!ChatObject.isChannel(chat) || (chat.megagroup && TextUtils.isEmpty(chat.username))) {
                            frameLayout.setText(AndroidUtilities.replaceTags(LocaleController.formatString("AreYouSureClearHistoryWithChat", R.string.AreYouSureClearHistoryWithChat, chat.title)));
                        } else if (chat.megagroup) {
                            frameLayout.setText(LocaleController.getString("AreYouSureClearHistoryGroup", R.string.AreYouSureClearHistoryGroup));
                        } else {
                            frameLayout.setText(LocaleController.getString("AreYouSureClearHistoryChannel", R.string.AreYouSureClearHistoryChannel));
                        }
                    }
                } else if (user != null) {
                    if (secret) {
                        frameLayout.setText(AndroidUtilities.replaceTags(LocaleController.formatString("AreYouSureDeleteThisChatWithSecretUser", R.string.AreYouSureDeleteThisChatWithSecretUser, ContactsController.formatName(user.first_name, user.last_name))));
                    } else {
                        frameLayout.setText(AndroidUtilities.replaceTags(LocaleController.formatString("AreYouSureDeleteThisChatWithUser", R.string.AreYouSureDeleteThisChatWithUser, ContactsController.formatName(user.first_name, user.last_name))));
                    }
                } else if (!ChatObject.isChannel(chat)) {
                    frameLayout.setText(AndroidUtilities.replaceTags(LocaleController.formatString("AreYouSureDeleteAndExitName", R.string.AreYouSureDeleteAndExitName, chat.title)));
                } else if (chat.megagroup) {
                    frameLayout.setText(AndroidUtilities.replaceTags(LocaleController.formatString("MegaLeaveAlertWithName", R.string.MegaLeaveAlertWithName, chat.title)));
                } else {
                    frameLayout.setText(AndroidUtilities.replaceTags(LocaleController.formatString("ChannelLeaveAlertWithName", R.string.ChannelLeaveAlertWithName, chat.title)));
                }
                if (clear) {
                    actionText = LocaleController.getString("ClearHistory", R.string.ClearHistory);
                } else if (!ChatObject.isChannel(chat)) {
                    actionText = LocaleController.getString("DeleteChatUser", R.string.DeleteChatUser);
                } else if (chat.megagroup) {
                    actionText = LocaleController.getString("LeaveMegaMenu", R.string.LeaveMegaMenu);
                } else {
                    actionText = LocaleController.getString("LeaveChannelMenu", R.string.LeaveChannelMenu);
                }
                builder.setPositiveButton(actionText, new AlertsCreator$$Lambda$5(onProcessRunnable));
                builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
                AlertDialog alertDialog = builder.create();
                fragment.showDialog(alertDialog);
                ((TextView) alertDialog.getButton(-1)).setTextColor(Theme.getColor("dialogTextRed2"));
            }
        }
    }

    static final /* synthetic */ void lambda$createClearOrDeleteDialogAlert$5$AlertsCreator(Runnable onProcessRunnable, DialogInterface dialogInterface, int i) {
        if (onProcessRunnable != null) {
            onProcessRunnable.run();
        }
    }

    public static Builder createDatePickerDialog(Context context, int minYear, int maxYear, int currentYearDiff, int selectedDay, int selectedMonth, int selectedYear, String title, boolean checkMinDate, DatePickerDelegate datePickerDelegate) {
        if (context == null) {
            return null;
        }
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(0);
        linearLayout.setWeightSum(1.0f);
        NumberPicker monthPicker = new NumberPicker(context);
        NumberPicker dayPicker = new NumberPicker(context);
        NumberPicker yearPicker = new NumberPicker(context);
        linearLayout.addView(dayPicker, LayoutHelper.createLinear(0, -2, 0.3f));
        dayPicker.setOnScrollListener(new AlertsCreator$$Lambda$6(checkMinDate, dayPicker, monthPicker, yearPicker));
        monthPicker.setMinValue(0);
        monthPicker.setMaxValue(11);
        linearLayout.addView(monthPicker, LayoutHelper.createLinear(0, -2, 0.3f));
        monthPicker.setFormatter(AlertsCreator$$Lambda$7.$instance);
        monthPicker.setOnValueChangedListener(new AlertsCreator$$Lambda$8(dayPicker, monthPicker, yearPicker));
        monthPicker.setOnScrollListener(new AlertsCreator$$Lambda$9(checkMinDate, dayPicker, monthPicker, yearPicker));
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        int currentYear = calendar.get(1);
        yearPicker.setMinValue(currentYear + minYear);
        yearPicker.setMaxValue(currentYear + maxYear);
        yearPicker.setValue(currentYear + currentYearDiff);
        linearLayout.addView(yearPicker, LayoutHelper.createLinear(0, -2, 0.4f));
        yearPicker.setOnValueChangedListener(new AlertsCreator$$Lambda$10(dayPicker, monthPicker, yearPicker));
        yearPicker.setOnScrollListener(new AlertsCreator$$Lambda$11(checkMinDate, dayPicker, monthPicker, yearPicker));
        updateDayPicker(dayPicker, monthPicker, yearPicker);
        if (checkMinDate) {
            checkPickerDate(dayPicker, monthPicker, yearPicker);
        }
        if (selectedDay != -1) {
            dayPicker.setValue(selectedDay);
            monthPicker.setValue(selectedMonth);
            yearPicker.setValue(selectedYear);
        }
        Builder builder = new Builder(context);
        builder.setTitle(title);
        builder.setView(linearLayout);
        builder.setPositiveButton(LocaleController.getString("Set", R.string.Set), new AlertsCreator$$Lambda$12(checkMinDate, dayPicker, monthPicker, yearPicker, datePickerDelegate));
        builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
        return builder;
    }

    static final /* synthetic */ void lambda$createDatePickerDialog$6$AlertsCreator(boolean checkMinDate, NumberPicker dayPicker, NumberPicker monthPicker, NumberPicker yearPicker, NumberPicker view, int scrollState) {
        if (checkMinDate && scrollState == 0) {
            checkPickerDate(dayPicker, monthPicker, yearPicker);
        }
    }

    static final /* synthetic */ String lambda$createDatePickerDialog$7$AlertsCreator(int value) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(5, 1);
        calendar.set(2, value);
        return calendar.getDisplayName(2, 1, Locale.getDefault());
    }

    static final /* synthetic */ void lambda$createDatePickerDialog$9$AlertsCreator(boolean checkMinDate, NumberPicker dayPicker, NumberPicker monthPicker, NumberPicker yearPicker, NumberPicker view, int scrollState) {
        if (checkMinDate && scrollState == 0) {
            checkPickerDate(dayPicker, monthPicker, yearPicker);
        }
    }

    static final /* synthetic */ void lambda$createDatePickerDialog$11$AlertsCreator(boolean checkMinDate, NumberPicker dayPicker, NumberPicker monthPicker, NumberPicker yearPicker, NumberPicker view, int scrollState) {
        if (checkMinDate && scrollState == 0) {
            checkPickerDate(dayPicker, monthPicker, yearPicker);
        }
    }

    static final /* synthetic */ void lambda$createDatePickerDialog$12$AlertsCreator(boolean checkMinDate, NumberPicker dayPicker, NumberPicker monthPicker, NumberPicker yearPicker, DatePickerDelegate datePickerDelegate, DialogInterface dialog, int which) {
        if (checkMinDate) {
            checkPickerDate(dayPicker, monthPicker, yearPicker);
        }
        datePickerDelegate.didSelectDate(yearPicker.getValue(), monthPicker.getValue(), dayPicker.getValue());
    }

    public static Dialog createMuteAlert(Context context, long dialog_id) {
        if (context == null) {
            return null;
        }
        BottomSheet.Builder builder = new BottomSheet.Builder(context);
        builder.setTitle(LocaleController.getString("Notifications", R.string.Notifications));
        CharSequence[] items = new CharSequence[4];
        items[0] = LocaleController.formatString("MuteFor", R.string.MuteFor, LocaleController.formatPluralString("Hours", 1));
        items[1] = LocaleController.formatString("MuteFor", R.string.MuteFor, LocaleController.formatPluralString("Hours", 8));
        items[2] = LocaleController.formatString("MuteFor", R.string.MuteFor, LocaleController.formatPluralString("Days", 2));
        items[3] = LocaleController.getString("MuteDisable", R.string.MuteDisable);
        builder.setItems(items, new AlertsCreator$$Lambda$13(dialog_id));
        return builder.create();
    }

    static final /* synthetic */ void lambda$createMuteAlert$13$AlertsCreator(long dialog_id, DialogInterface dialogInterface, int i) {
        long flags;
        int untilTime = ConnectionsManager.getInstance(UserConfig.selectedAccount).getCurrentTime();
        if (i == 0) {
            untilTime += 3600;
        } else if (i == 1) {
            untilTime += 28800;
        } else if (i == 2) {
            untilTime += 172800;
        } else if (i == 3) {
            untilTime = Integer.MAX_VALUE;
        }
        Editor editor = MessagesController.getNotificationsSettings(UserConfig.selectedAccount).edit();
        if (i == 3) {
            editor.putInt("notify2_" + dialog_id, 2);
            flags = 1;
        } else {
            editor.putInt("notify2_" + dialog_id, 3);
            editor.putInt("notifyuntil_" + dialog_id, untilTime);
            flags = (((long) untilTime) << 32) | 1;
        }
        NotificationsController.getInstance(UserConfig.selectedAccount).removeNotificationsForDialog(dialog_id);
        MessagesStorage.getInstance(UserConfig.selectedAccount).setDialogFlags(dialog_id, flags);
        editor.commit();
        TL_dialog dialog = (TL_dialog) MessagesController.getInstance(UserConfig.selectedAccount).dialogs_dict.get(dialog_id);
        if (dialog != null) {
            dialog.notify_settings = new TL_peerNotifySettings();
            dialog.notify_settings.mute_until = untilTime;
        }
        NotificationsController.getInstance(UserConfig.selectedAccount).updateServerNotificationsSettings(dialog_id);
    }

    public static void createReportAlert(Context context, long dialog_id, int messageId, BaseFragment parentFragment) {
        if (context != null && parentFragment != null) {
            BottomSheet.Builder builder = new BottomSheet.Builder(context);
            builder.setTitle(LocaleController.getString("ReportChat", R.string.ReportChat));
            builder.setItems(new CharSequence[]{LocaleController.getString("ReportChatSpam", R.string.ReportChatSpam), LocaleController.getString("ReportChatViolence", R.string.ReportChatViolence), LocaleController.getString("ReportChatChild", R.string.ReportChatChild), LocaleController.getString("ReportChatPornography", R.string.ReportChatPornography), LocaleController.getString("ReportChatOther", R.string.ReportChatOther)}, new AlertsCreator$$Lambda$14(dialog_id, messageId, parentFragment, context));
            parentFragment.showDialog(builder.create());
        }
    }

    static final /* synthetic */ void lambda$createReportAlert$15$AlertsCreator(long dialog_id, int messageId, BaseFragment parentFragment, Context context, DialogInterface dialogInterface, int i) {
        if (i == 4) {
            Bundle args = new Bundle();
            args.putLong("dialog_id", dialog_id);
            args.putLong("message_id", (long) messageId);
            parentFragment.presentFragment(new ReportOtherActivity(args));
            return;
        }
        TLObject req;
        InputPeer peer = MessagesController.getInstance(UserConfig.selectedAccount).getInputPeer((int) dialog_id);
        TLObject request;
        if (messageId != 0) {
            request = new TL_messages_report();
            request.peer = peer;
            request.id.add(Integer.valueOf(messageId));
            if (i == 0) {
                request.reason = new TL_inputReportReasonSpam();
            } else if (i == 1) {
                request.reason = new TL_inputReportReasonViolence();
            } else if (i == 2) {
                request.reason = new TL_inputReportReasonChildAbuse();
            } else if (i == 3) {
                request.reason = new TL_inputReportReasonPornography();
            }
            req = request;
        } else {
            request = new TL_account_reportPeer();
            request.peer = peer;
            if (i == 0) {
                request.reason = new TL_inputReportReasonSpam();
            } else if (i == 1) {
                request.reason = new TL_inputReportReasonViolence();
            } else if (i == 2) {
                request.reason = new TL_inputReportReasonChildAbuse();
            } else if (i == 3) {
                request.reason = new TL_inputReportReasonPornography();
            }
            req = request;
        }
        ConnectionsManager.getInstance(UserConfig.selectedAccount).sendRequest(req, AlertsCreator$$Lambda$34.$instance);
        Toast.makeText(context, LocaleController.getString("ReportChatSent", R.string.ReportChatSent), 0).show();
    }

    static final /* synthetic */ void lambda$null$14$AlertsCreator(TLObject response, TL_error error) {
    }

    private static String getFloodWaitString(String error) {
        String timeString;
        int time = Utilities.parseInt(error).intValue();
        if (time < 60) {
            timeString = LocaleController.formatPluralString("Seconds", time);
        } else {
            timeString = LocaleController.formatPluralString("Minutes", time / 60);
        }
        return LocaleController.formatString("FloodWaitTime", R.string.FloodWaitTime, timeString);
    }

    public static void showFloodWaitAlert(String error, BaseFragment fragment) {
        if (error != null && error.startsWith("FLOOD_WAIT") && fragment != null && fragment.getParentActivity() != null) {
            String timeString;
            int time = Utilities.parseInt(error).intValue();
            if (time < 60) {
                timeString = LocaleController.formatPluralString("Seconds", time);
            } else {
                timeString = LocaleController.formatPluralString("Minutes", time / 60);
            }
            Builder builder = new Builder(fragment.getParentActivity());
            builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
            builder.setMessage(LocaleController.formatString("FloodWaitTime", R.string.FloodWaitTime, timeString));
            builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), null);
            fragment.showDialog(builder.create(), true, null);
        }
    }

    public static void showSendMediaAlert(int result, BaseFragment fragment) {
        if (result != 0) {
            Builder builder = new Builder(fragment.getParentActivity());
            builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
            if (result == 1) {
                builder.setMessage(LocaleController.getString("ErrorSendRestrictedStickers", R.string.ErrorSendRestrictedStickers));
            } else if (result == 2) {
                builder.setMessage(LocaleController.getString("ErrorSendRestrictedMedia", R.string.ErrorSendRestrictedMedia));
            } else if (result == 3) {
                builder.setMessage(LocaleController.getString("ErrorSendRestrictedPolls", R.string.ErrorSendRestrictedPolls));
            } else if (result == 4) {
                builder.setMessage(LocaleController.getString("ErrorSendRestrictedStickersAll", R.string.ErrorSendRestrictedStickersAll));
            } else if (result == 5) {
                builder.setMessage(LocaleController.getString("ErrorSendRestrictedMediaAll", R.string.ErrorSendRestrictedMediaAll));
            } else if (result == 6) {
                builder.setMessage(LocaleController.getString("ErrorSendRestrictedPollsAll", R.string.ErrorSendRestrictedPollsAll));
            }
            builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), null);
            fragment.showDialog(builder.create(), true, null);
        }
    }

    public static void showAddUserAlert(String error, BaseFragment fragment, boolean isChannel) {
        if (error != null && fragment != null && fragment.getParentActivity() != null) {
            Builder builder = new Builder(fragment.getParentActivity());
            builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
            boolean z = true;
            switch (error.hashCode()) {
                case -1763467626:
                    if (error.equals("USERS_TOO_FEW")) {
                        z = true;
                        break;
                    }
                    break;
                case -538116776:
                    if (error.equals("USER_BLOCKED")) {
                        z = true;
                        break;
                    }
                    break;
                case -512775857:
                    if (error.equals("USER_RESTRICTED")) {
                        z = true;
                        break;
                    }
                    break;
                case -454039871:
                    if (error.equals("PEER_FLOOD")) {
                        z = false;
                        break;
                    }
                    break;
                case -420079733:
                    if (error.equals("BOTS_TOO_MUCH")) {
                        z = true;
                        break;
                    }
                    break;
                case 98635865:
                    if (error.equals("USER_KICKED")) {
                        z = true;
                        break;
                    }
                    break;
                case 517420851:
                    if (error.equals("USER_BOT")) {
                        z = true;
                        break;
                    }
                    break;
                case 845559454:
                    if (error.equals("YOU_BLOCKED_USER")) {
                        z = true;
                        break;
                    }
                    break;
                case 916342611:
                    if (error.equals("USER_ADMIN_INVALID")) {
                        z = true;
                        break;
                    }
                    break;
                case 1047173446:
                    if (error.equals("CHAT_ADMIN_BAN_REQUIRED")) {
                        z = true;
                        break;
                    }
                    break;
                case 1167301807:
                    if (error.equals("USERS_TOO_MUCH")) {
                        z = true;
                        break;
                    }
                    break;
                case 1227003815:
                    if (error.equals("USER_ID_INVALID")) {
                        z = true;
                        break;
                    }
                    break;
                case 1253103379:
                    if (error.equals("ADMINS_TOO_MUCH")) {
                        z = true;
                        break;
                    }
                    break;
                case 1623167701:
                    if (error.equals("USER_NOT_MUTUAL_CONTACT")) {
                        z = true;
                        break;
                    }
                    break;
                case 1754587486:
                    if (error.equals("CHAT_ADMIN_INVITE_REQUIRED")) {
                        z = true;
                        break;
                    }
                    break;
                case 1916725894:
                    if (error.equals("USER_PRIVACY_RESTRICTED")) {
                        z = true;
                        break;
                    }
                    break;
            }
            switch (z) {
                case false:
                    builder.setMessage(LocaleController.getString("NobodyLikesSpam2", R.string.NobodyLikesSpam2));
                    builder.setNegativeButton(LocaleController.getString("MoreInfo", R.string.MoreInfo), new AlertsCreator$$Lambda$15(fragment));
                    break;
                case true:
                case true:
                case true:
                    if (!isChannel) {
                        builder.setMessage(LocaleController.getString("GroupUserCantAdd", R.string.GroupUserCantAdd));
                        break;
                    } else {
                        builder.setMessage(LocaleController.getString("ChannelUserCantAdd", R.string.ChannelUserCantAdd));
                        break;
                    }
                case true:
                    if (!isChannel) {
                        builder.setMessage(LocaleController.getString("GroupUserAddLimit", R.string.GroupUserAddLimit));
                        break;
                    } else {
                        builder.setMessage(LocaleController.getString("ChannelUserAddLimit", R.string.ChannelUserAddLimit));
                        break;
                    }
                case true:
                    if (!isChannel) {
                        builder.setMessage(LocaleController.getString("GroupUserLeftError", R.string.GroupUserLeftError));
                        break;
                    } else {
                        builder.setMessage(LocaleController.getString("ChannelUserLeftError", R.string.ChannelUserLeftError));
                        break;
                    }
                case true:
                    if (!isChannel) {
                        builder.setMessage(LocaleController.getString("GroupUserCantAdmin", R.string.GroupUserCantAdmin));
                        break;
                    } else {
                        builder.setMessage(LocaleController.getString("ChannelUserCantAdmin", R.string.ChannelUserCantAdmin));
                        break;
                    }
                case true:
                    if (!isChannel) {
                        builder.setMessage(LocaleController.getString("GroupUserCantBot", R.string.GroupUserCantBot));
                        break;
                    } else {
                        builder.setMessage(LocaleController.getString("ChannelUserCantBot", R.string.ChannelUserCantBot));
                        break;
                    }
                case true:
                    if (!isChannel) {
                        builder.setMessage(LocaleController.getString("InviteToGroupError", R.string.InviteToGroupError));
                        break;
                    } else {
                        builder.setMessage(LocaleController.getString("InviteToChannelError", R.string.InviteToChannelError));
                        break;
                    }
                case true:
                    builder.setMessage(LocaleController.getString("CreateGroupError", R.string.CreateGroupError));
                    break;
                case true:
                    builder.setMessage(LocaleController.getString("UserRestricted", R.string.UserRestricted));
                    break;
                case true:
                    builder.setMessage(LocaleController.getString("YouBlockedUser", R.string.YouBlockedUser));
                    break;
                case true:
                case true:
                    builder.setMessage(LocaleController.getString("AddAdminErrorBlacklisted", R.string.AddAdminErrorBlacklisted));
                    break;
                case true:
                    builder.setMessage(LocaleController.getString("AddAdminErrorNotAMember", R.string.AddAdminErrorNotAMember));
                    break;
                case true:
                    builder.setMessage(LocaleController.getString("AddBannedErrorAdmin", R.string.AddBannedErrorAdmin));
                    break;
                default:
                    builder.setMessage(LocaleController.getString("ErrorOccurred", R.string.ErrorOccurred) + "\n" + error);
                    break;
            }
            builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), null);
            fragment.showDialog(builder.create(), true, null);
        }
    }

    public static Dialog createColorSelectDialog(Activity parentActivity, long dialog_id, int globalType, Runnable onSelect) {
        int currentColor;
        SharedPreferences preferences = MessagesController.getNotificationsSettings(UserConfig.selectedAccount);
        if (dialog_id != 0) {
            if (preferences.contains("color_" + dialog_id)) {
                currentColor = preferences.getInt("color_" + dialog_id, -16776961);
            } else if (((int) dialog_id) < 0) {
                currentColor = preferences.getInt("GroupLed", -16776961);
            } else {
                currentColor = preferences.getInt("MessagesLed", -16776961);
            }
        } else if (globalType == 1) {
            currentColor = preferences.getInt("MessagesLed", -16776961);
        } else if (globalType == 0) {
            currentColor = preferences.getInt("GroupLed", -16776961);
        } else {
            currentColor = preferences.getInt("ChannelLed", -16776961);
        }
        LinearLayout linearLayout = new LinearLayout(parentActivity);
        linearLayout.setOrientation(1);
        String[] descriptions = new String[]{LocaleController.getString("ColorRed", R.string.ColorRed), LocaleController.getString("ColorOrange", R.string.ColorOrange), LocaleController.getString("ColorYellow", R.string.ColorYellow), LocaleController.getString("ColorGreen", R.string.ColorGreen), LocaleController.getString("ColorCyan", R.string.ColorCyan), LocaleController.getString("ColorBlue", R.string.ColorBlue), LocaleController.getString("ColorViolet", R.string.ColorViolet), LocaleController.getString("ColorPink", R.string.ColorPink), LocaleController.getString("ColorWhite", R.string.ColorWhite)};
        int[] selectedColor = new int[]{currentColor};
        for (int a = 0; a < 9; a++) {
            RadioColorCell cell = new RadioColorCell(parentActivity);
            cell.setPadding(AndroidUtilities.dp(4.0f), 0, AndroidUtilities.dp(4.0f), 0);
            cell.setTag(Integer.valueOf(a));
            cell.setCheckColor(TextColorCell.colors[a], TextColorCell.colors[a]);
            cell.setTextAndValue(descriptions[a], currentColor == TextColorCell.colorsToSave[a]);
            linearLayout.addView(cell);
            cell.setOnClickListener(new AlertsCreator$$Lambda$16(linearLayout, selectedColor));
        }
        Builder builder = new Builder((Context) parentActivity);
        builder.setTitle(LocaleController.getString("LedColor", R.string.LedColor));
        builder.setView(linearLayout);
        builder.setPositiveButton(LocaleController.getString("Set", R.string.Set), new AlertsCreator$$Lambda$17(dialog_id, selectedColor, globalType, onSelect));
        builder.setNeutralButton(LocaleController.getString("LedDisabled", R.string.LedDisabled), new AlertsCreator$$Lambda$18(dialog_id, globalType, onSelect));
        if (dialog_id != 0) {
            builder.setNegativeButton(LocaleController.getString("Default", R.string.Default), new AlertsCreator$$Lambda$19(dialog_id, onSelect));
        }
        return builder.create();
    }

    static final /* synthetic */ void lambda$createColorSelectDialog$17$AlertsCreator(LinearLayout linearLayout, int[] selectedColor, View v) {
        int count = linearLayout.getChildCount();
        for (int a1 = 0; a1 < count; a1++) {
            boolean z;
            View cell1 = (RadioColorCell) linearLayout.getChildAt(a1);
            if (cell1 == v) {
                z = true;
            } else {
                z = false;
            }
            cell1.setChecked(z, true);
        }
        selectedColor[0] = TextColorCell.colorsToSave[((Integer) v.getTag()).intValue()];
    }

    static final /* synthetic */ void lambda$createColorSelectDialog$18$AlertsCreator(long dialog_id, int[] selectedColor, int globalType, Runnable onSelect, DialogInterface dialogInterface, int which) {
        Editor editor = MessagesController.getNotificationsSettings(UserConfig.selectedAccount).edit();
        if (dialog_id != 0) {
            editor.putInt("color_" + dialog_id, selectedColor[0]);
        } else if (globalType == 1) {
            editor.putInt("MessagesLed", selectedColor[0]);
        } else if (globalType == 0) {
            editor.putInt("GroupLed", selectedColor[0]);
        } else {
            editor.putInt("ChannelLed", selectedColor[0]);
        }
        editor.commit();
        if (onSelect != null) {
            onSelect.run();
        }
    }

    static final /* synthetic */ void lambda$createColorSelectDialog$19$AlertsCreator(long dialog_id, int globalType, Runnable onSelect, DialogInterface dialog, int which) {
        Editor editor = MessagesController.getNotificationsSettings(UserConfig.selectedAccount).edit();
        if (dialog_id != 0) {
            editor.putInt("color_" + dialog_id, 0);
        } else if (globalType == 1) {
            editor.putInt("MessagesLed", 0);
        } else if (globalType == 0) {
            editor.putInt("GroupLed", 0);
        } else {
            editor.putInt("ChannelLed", 0);
        }
        editor.commit();
        if (onSelect != null) {
            onSelect.run();
        }
    }

    static final /* synthetic */ void lambda$createColorSelectDialog$20$AlertsCreator(long dialog_id, Runnable onSelect, DialogInterface dialog, int which) {
        Editor editor = MessagesController.getNotificationsSettings(UserConfig.selectedAccount).edit();
        editor.remove("color_" + dialog_id);
        editor.commit();
        if (onSelect != null) {
            onSelect.run();
        }
    }

    public static Dialog createVibrationSelectDialog(Activity parentActivity, long dialog_id, boolean globalGroup, boolean globalAll, Runnable onSelect) {
        String prefix = dialog_id != 0 ? "vibrate_" : globalGroup ? "vibrate_group" : "vibrate_messages";
        return createVibrationSelectDialog(parentActivity, dialog_id, prefix, onSelect);
    }

    public static Dialog createVibrationSelectDialog(Activity parentActivity, long dialog_id, String prefKeyPrefix, Runnable onSelect) {
        String[] descriptions;
        SharedPreferences preferences = MessagesController.getNotificationsSettings(UserConfig.selectedAccount);
        int[] selected = new int[1];
        if (dialog_id != 0) {
            selected[0] = preferences.getInt(prefKeyPrefix + dialog_id, 0);
            if (selected[0] == 3) {
                selected[0] = 2;
            } else if (selected[0] == 2) {
                selected[0] = 3;
            }
            descriptions = new String[]{LocaleController.getString("VibrationDefault", R.string.VibrationDefault), LocaleController.getString("Short", R.string.Short), LocaleController.getString("Long", R.string.Long), LocaleController.getString("VibrationDisabled", R.string.VibrationDisabled)};
        } else {
            selected[0] = preferences.getInt(prefKeyPrefix, 0);
            if (selected[0] == 0) {
                selected[0] = 1;
            } else if (selected[0] == 1) {
                selected[0] = 2;
            } else if (selected[0] == 2) {
                selected[0] = 0;
            }
            descriptions = new String[]{LocaleController.getString("VibrationDisabled", R.string.VibrationDisabled), LocaleController.getString("VibrationDefault", R.string.VibrationDefault), LocaleController.getString("Short", R.string.Short), LocaleController.getString("Long", R.string.Long), LocaleController.getString("OnlyIfSilent", R.string.OnlyIfSilent)};
        }
        LinearLayout linearLayout = new LinearLayout(parentActivity);
        linearLayout.setOrientation(1);
        Builder builder = new Builder((Context) parentActivity);
        int a = 0;
        while (a < descriptions.length) {
            RadioColorCell cell = new RadioColorCell(parentActivity);
            cell.setPadding(AndroidUtilities.dp(4.0f), 0, AndroidUtilities.dp(4.0f), 0);
            cell.setTag(Integer.valueOf(a));
            cell.setCheckColor(Theme.getColor("radioBackground"), Theme.getColor("dialogRadioBackgroundChecked"));
            cell.setTextAndValue(descriptions[a], selected[0] == a);
            linearLayout.addView(cell);
            cell.setOnClickListener(new AlertsCreator$$Lambda$20(selected, dialog_id, prefKeyPrefix, builder, onSelect));
            a++;
        }
        builder.setTitle(LocaleController.getString("Vibrate", R.string.Vibrate));
        builder.setView(linearLayout);
        builder.setPositiveButton(LocaleController.getString("Cancel", R.string.Cancel), null);
        return builder.create();
    }

    static final /* synthetic */ void lambda$createVibrationSelectDialog$21$AlertsCreator(int[] selected, long dialog_id, String prefKeyPrefix, Builder builder, Runnable onSelect, View v) {
        selected[0] = ((Integer) v.getTag()).intValue();
        Editor editor = MessagesController.getNotificationsSettings(UserConfig.selectedAccount).edit();
        if (dialog_id != 0) {
            if (selected[0] == 0) {
                editor.putInt(prefKeyPrefix + dialog_id, 0);
            } else if (selected[0] == 1) {
                editor.putInt(prefKeyPrefix + dialog_id, 1);
            } else if (selected[0] == 2) {
                editor.putInt(prefKeyPrefix + dialog_id, 3);
            } else if (selected[0] == 3) {
                editor.putInt(prefKeyPrefix + dialog_id, 2);
            }
        } else if (selected[0] == 0) {
            editor.putInt(prefKeyPrefix, 2);
        } else if (selected[0] == 1) {
            editor.putInt(prefKeyPrefix, 0);
        } else if (selected[0] == 2) {
            editor.putInt(prefKeyPrefix, 1);
        } else if (selected[0] == 3) {
            editor.putInt(prefKeyPrefix, 3);
        } else if (selected[0] == 4) {
            editor.putInt(prefKeyPrefix, 4);
        }
        editor.commit();
        builder.getDismissRunnable().run();
        if (onSelect != null) {
            onSelect.run();
        }
    }

    public static Dialog createLocationUpdateDialog(Activity parentActivity, User user, IntCallback callback) {
        int[] selected = new int[1];
        String[] descriptions = new String[]{LocaleController.getString("SendLiveLocationFor15m", R.string.SendLiveLocationFor15m), LocaleController.getString("SendLiveLocationFor1h", R.string.SendLiveLocationFor1h), LocaleController.getString("SendLiveLocationFor8h", R.string.SendLiveLocationFor8h)};
        LinearLayout linearLayout = new LinearLayout(parentActivity);
        linearLayout.setOrientation(1);
        TextView titleTextView = new TextView(parentActivity);
        if (user != null) {
            titleTextView.setText(LocaleController.formatString("LiveLocationAlertPrivate", R.string.LiveLocationAlertPrivate, UserObject.getFirstName(user)));
        } else {
            titleTextView.setText(LocaleController.getString("LiveLocationAlertGroup", R.string.LiveLocationAlertGroup));
        }
        titleTextView.setTextColor(Theme.getColor("dialogTextBlack"));
        titleTextView.setTextSize(1, 16.0f);
        titleTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
        linearLayout.addView(titleTextView, LayoutHelper.createLinear(-2, -2, (LocaleController.isRTL ? 5 : 3) | 48, 24, 0, 24, 8));
        int a = 0;
        while (a < descriptions.length) {
            RadioColorCell cell = new RadioColorCell(parentActivity);
            cell.setPadding(AndroidUtilities.dp(4.0f), 0, AndroidUtilities.dp(4.0f), 0);
            cell.setTag(Integer.valueOf(a));
            cell.setCheckColor(Theme.getColor("radioBackground"), Theme.getColor("dialogRadioBackgroundChecked"));
            cell.setTextAndValue(descriptions[a], selected[0] == a);
            linearLayout.addView(cell);
            cell.setOnClickListener(new AlertsCreator$$Lambda$21(selected, linearLayout));
            a++;
        }
        Builder builder = new Builder((Context) parentActivity);
        builder.setTopImage(new ShareLocationDrawable(parentActivity, false), Theme.getColor("dialogTopBackground"));
        builder.setView(linearLayout);
        builder.setPositiveButton(LocaleController.getString("ShareFile", R.string.ShareFile), new AlertsCreator$$Lambda$22(selected, callback));
        builder.setNeutralButton(LocaleController.getString("Cancel", R.string.Cancel), null);
        return builder.create();
    }

    static final /* synthetic */ void lambda$createLocationUpdateDialog$22$AlertsCreator(int[] selected, LinearLayout linearLayout, View v) {
        selected[0] = ((Integer) v.getTag()).intValue();
        int count = linearLayout.getChildCount();
        for (int a1 = 0; a1 < count; a1++) {
            View child = linearLayout.getChildAt(a1);
            if (child instanceof RadioColorCell) {
                boolean z;
                RadioColorCell radioColorCell = (RadioColorCell) child;
                if (child == v) {
                    z = true;
                } else {
                    z = false;
                }
                radioColorCell.setChecked(z, true);
            }
        }
    }

    static final /* synthetic */ void lambda$createLocationUpdateDialog$23$AlertsCreator(int[] selected, IntCallback callback, DialogInterface dialog, int which) {
        int time;
        if (selected[0] == 0) {
            time = 900;
        } else if (selected[0] == 1) {
            time = 3600;
        } else {
            time = 28800;
        }
        callback.lambda$null$87$MessagesStorage(time);
    }

    public static Builder createContactsPermissionDialog(Activity parentActivity, IntCallback callback) {
        Builder builder = new Builder((Context) parentActivity);
        builder.setTopImage((int) R.drawable.permissions_contacts, Theme.getColor("dialogTopBackground"));
        builder.setMessage(AndroidUtilities.replaceTags(LocaleController.getString("ContactsPermissionAlert", R.string.ContactsPermissionAlert)));
        builder.setPositiveButton(LocaleController.getString("ContactsPermissionAlertContinue", R.string.ContactsPermissionAlertContinue), new AlertsCreator$$Lambda$23(callback));
        builder.setNegativeButton(LocaleController.getString("ContactsPermissionAlertNotNow", R.string.ContactsPermissionAlertNotNow), new AlertsCreator$$Lambda$24(callback));
        return builder;
    }

    public static Dialog createFreeSpaceDialog(LaunchActivity parentActivity) {
        int[] selected = new int[1];
        int keepMedia = MessagesController.getGlobalMainSettings().getInt("keep_media", 2);
        if (keepMedia == 2) {
            selected[0] = 3;
        } else if (keepMedia == 0) {
            selected[0] = 1;
        } else if (keepMedia == 1) {
            selected[0] = 2;
        } else if (keepMedia == 3) {
            selected[0] = 0;
        }
        String[] descriptions = new String[]{LocaleController.formatPluralString("Days", 3), LocaleController.formatPluralString("Weeks", 1), LocaleController.formatPluralString("Months", 1), LocaleController.getString("LowDiskSpaceNeverRemove", R.string.LowDiskSpaceNeverRemove)};
        LinearLayout linearLayout = new LinearLayout(parentActivity);
        linearLayout.setOrientation(1);
        View titleTextView = new TextView(parentActivity);
        titleTextView.setText(LocaleController.getString("LowDiskSpaceTitle2", R.string.LowDiskSpaceTitle2));
        titleTextView.setTextColor(Theme.getColor("dialogTextBlack"));
        titleTextView.setTextSize(1, 16.0f);
        titleTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        titleTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
        linearLayout.addView(titleTextView, LayoutHelper.createLinear(-2, -2, (LocaleController.isRTL ? 5 : 3) | 48, 24, 0, 24, 8));
        int a = 0;
        while (a < descriptions.length) {
            RadioColorCell cell = new RadioColorCell(parentActivity);
            cell.setPadding(AndroidUtilities.dp(4.0f), 0, AndroidUtilities.dp(4.0f), 0);
            cell.setTag(Integer.valueOf(a));
            cell.setCheckColor(Theme.getColor("radioBackground"), Theme.getColor("dialogRadioBackgroundChecked"));
            cell.setTextAndValue(descriptions[a], selected[0] == a);
            linearLayout.addView(cell);
            cell.setOnClickListener(new AlertsCreator$$Lambda$25(selected, linearLayout));
            a++;
        }
        Builder builder = new Builder((Context) parentActivity);
        builder.setTitle(LocaleController.getString("LowDiskSpaceTitle", R.string.LowDiskSpaceTitle));
        builder.setMessage(LocaleController.getString("LowDiskSpaceMessage", R.string.LowDiskSpaceMessage));
        builder.setView(linearLayout);
        builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), new AlertsCreator$$Lambda$26(selected));
        builder.setNeutralButton(LocaleController.getString("ClearMediaCache", R.string.ClearMediaCache), new AlertsCreator$$Lambda$27(parentActivity));
        return builder.create();
    }

    static final /* synthetic */ void lambda$createFreeSpaceDialog$26$AlertsCreator(int[] selected, LinearLayout linearLayout, View v) {
        int num = ((Integer) v.getTag()).intValue();
        if (num == 0) {
            selected[0] = 3;
        } else if (num == 1) {
            selected[0] = 0;
        } else if (num == 2) {
            selected[0] = 1;
        } else if (num == 3) {
            selected[0] = 2;
        }
        int count = linearLayout.getChildCount();
        for (int a1 = 0; a1 < count; a1++) {
            View child = linearLayout.getChildAt(a1);
            if (child instanceof RadioColorCell) {
                boolean z;
                RadioColorCell radioColorCell = (RadioColorCell) child;
                if (child == v) {
                    z = true;
                } else {
                    z = false;
                }
                radioColorCell.setChecked(z, true);
            }
        }
    }

    public static Dialog createPrioritySelectDialog(Activity parentActivity, long dialog_id, int globalType, Runnable onSelect) {
        String[] descriptions;
        SharedPreferences preferences = MessagesController.getNotificationsSettings(UserConfig.selectedAccount);
        int[] selected = new int[1];
        if (dialog_id != 0) {
            selected[0] = preferences.getInt("priority_" + dialog_id, 3);
            if (selected[0] == 3) {
                selected[0] = 0;
            } else if (selected[0] == 4) {
                selected[0] = 1;
            } else if (selected[0] == 5) {
                selected[0] = 2;
            } else if (selected[0] == 0) {
                selected[0] = 3;
            } else {
                selected[0] = 4;
            }
            descriptions = new String[]{LocaleController.getString("NotificationsPrioritySettings", R.string.NotificationsPrioritySettings), LocaleController.getString("NotificationsPriorityLow", R.string.NotificationsPriorityLow), LocaleController.getString("NotificationsPriorityMedium", R.string.NotificationsPriorityMedium), LocaleController.getString("NotificationsPriorityHigh", R.string.NotificationsPriorityHigh), LocaleController.getString("NotificationsPriorityUrgent", R.string.NotificationsPriorityUrgent)};
        } else {
            if (dialog_id == 0) {
                if (globalType == 1) {
                    selected[0] = preferences.getInt("priority_messages", 1);
                } else if (globalType == 0) {
                    selected[0] = preferences.getInt("priority_group", 1);
                } else if (globalType == 2) {
                    selected[0] = preferences.getInt("priority_channel", 1);
                }
            }
            if (selected[0] == 4) {
                selected[0] = 0;
            } else if (selected[0] == 5) {
                selected[0] = 1;
            } else if (selected[0] == 0) {
                selected[0] = 2;
            } else {
                selected[0] = 3;
            }
            descriptions = new String[]{LocaleController.getString("NotificationsPriorityLow", R.string.NotificationsPriorityLow), LocaleController.getString("NotificationsPriorityMedium", R.string.NotificationsPriorityMedium), LocaleController.getString("NotificationsPriorityHigh", R.string.NotificationsPriorityHigh), LocaleController.getString("NotificationsPriorityUrgent", R.string.NotificationsPriorityUrgent)};
        }
        LinearLayout linearLayout = new LinearLayout(parentActivity);
        linearLayout.setOrientation(1);
        Builder builder = new Builder((Context) parentActivity);
        int a = 0;
        while (a < descriptions.length) {
            RadioColorCell cell = new RadioColorCell(parentActivity);
            cell.setPadding(AndroidUtilities.dp(4.0f), 0, AndroidUtilities.dp(4.0f), 0);
            cell.setTag(Integer.valueOf(a));
            cell.setCheckColor(Theme.getColor("radioBackground"), Theme.getColor("dialogRadioBackgroundChecked"));
            cell.setTextAndValue(descriptions[a], selected[0] == a);
            linearLayout.addView(cell);
            cell.setOnClickListener(new AlertsCreator$$Lambda$28(selected, dialog_id, globalType, preferences, builder, onSelect));
            a++;
        }
        builder.setTitle(LocaleController.getString("NotificationsImportance", R.string.NotificationsImportance));
        builder.setView(linearLayout);
        builder.setPositiveButton(LocaleController.getString("Cancel", R.string.Cancel), null);
        return builder.create();
    }

    static final /* synthetic */ void lambda$createPrioritySelectDialog$29$AlertsCreator(int[] selected, long dialog_id, int globalType, SharedPreferences preferences, Builder builder, Runnable onSelect, View v) {
        selected[0] = ((Integer) v.getTag()).intValue();
        Editor editor = MessagesController.getNotificationsSettings(UserConfig.selectedAccount).edit();
        int option;
        if (dialog_id != 0) {
            if (selected[0] == 0) {
                option = 3;
            } else if (selected[0] == 1) {
                option = 4;
            } else if (selected[0] == 2) {
                option = 5;
            } else if (selected[0] == 3) {
                option = 0;
            } else {
                option = 1;
            }
            editor.putInt("priority_" + dialog_id, option);
        } else {
            if (selected[0] == 0) {
                option = 4;
            } else if (selected[0] == 1) {
                option = 5;
            } else if (selected[0] == 2) {
                option = 0;
            } else {
                option = 1;
            }
            if (globalType == 1) {
                editor.putInt("priority_messages", option);
                selected[0] = preferences.getInt("priority_messages", 1);
            } else if (globalType == 0) {
                editor.putInt("priority_group", option);
                selected[0] = preferences.getInt("priority_group", 1);
            } else if (globalType == 2) {
                editor.putInt("priority_channel", option);
                selected[0] = preferences.getInt("priority_channel", 1);
            }
        }
        editor.commit();
        builder.getDismissRunnable().run();
        if (onSelect != null) {
            onSelect.run();
        }
    }

    public static Dialog createPopupSelectDialog(Activity parentActivity, int globalType, Runnable onSelect) {
        SharedPreferences preferences = MessagesController.getNotificationsSettings(UserConfig.selectedAccount);
        int[] selected = new int[1];
        if (globalType == 1) {
            selected[0] = preferences.getInt("popupAll", 0);
        } else if (globalType == 0) {
            selected[0] = preferences.getInt("popupGroup", 0);
        } else {
            selected[0] = preferences.getInt("popupChannel", 0);
        }
        String[] descriptions = new String[]{LocaleController.getString("NoPopup", R.string.NoPopup), LocaleController.getString("OnlyWhenScreenOn", R.string.OnlyWhenScreenOn), LocaleController.getString("OnlyWhenScreenOff", R.string.OnlyWhenScreenOff), LocaleController.getString("AlwaysShowPopup", R.string.AlwaysShowPopup)};
        LinearLayout linearLayout = new LinearLayout(parentActivity);
        linearLayout.setOrientation(1);
        Builder builder = new Builder((Context) parentActivity);
        for (int a = 0; a < descriptions.length; a++) {
            boolean z;
            RadioColorCell cell = new RadioColorCell(parentActivity);
            cell.setTag(Integer.valueOf(a));
            cell.setPadding(AndroidUtilities.dp(4.0f), 0, AndroidUtilities.dp(4.0f), 0);
            cell.setCheckColor(Theme.getColor("radioBackground"), Theme.getColor("dialogRadioBackgroundChecked"));
            String str = descriptions[a];
            if (selected[0] == a) {
                z = true;
            } else {
                z = false;
            }
            cell.setTextAndValue(str, z);
            linearLayout.addView(cell);
            cell.setOnClickListener(new AlertsCreator$$Lambda$29(selected, globalType, builder, onSelect));
        }
        builder.setTitle(LocaleController.getString("PopupNotification", R.string.PopupNotification));
        builder.setView(linearLayout);
        builder.setPositiveButton(LocaleController.getString("Cancel", R.string.Cancel), null);
        return builder.create();
    }

    static final /* synthetic */ void lambda$createPopupSelectDialog$30$AlertsCreator(int[] selected, int globalType, Builder builder, Runnable onSelect, View v) {
        selected[0] = ((Integer) v.getTag()).intValue();
        Editor editor = MessagesController.getNotificationsSettings(UserConfig.selectedAccount).edit();
        if (globalType == 1) {
            editor.putInt("popupAll", selected[0]);
        } else if (globalType == 0) {
            editor.putInt("popupGroup", selected[0]);
        } else {
            editor.putInt("popupChannel", selected[0]);
        }
        editor.commit();
        builder.getDismissRunnable().run();
        if (onSelect != null) {
            onSelect.run();
        }
    }

    public static Dialog createSingleChoiceDialog(Activity parentActivity, String[] options, String title, int selected, OnClickListener listener) {
        LinearLayout linearLayout = new LinearLayout(parentActivity);
        linearLayout.setOrientation(1);
        Builder builder = new Builder((Context) parentActivity);
        for (int a = 0; a < options.length; a++) {
            boolean z;
            RadioColorCell cell = new RadioColorCell(parentActivity);
            cell.setPadding(AndroidUtilities.dp(4.0f), 0, AndroidUtilities.dp(4.0f), 0);
            cell.setTag(Integer.valueOf(a));
            cell.setCheckColor(Theme.getColor("radioBackground"), Theme.getColor("dialogRadioBackgroundChecked"));
            String str = options[a];
            if (selected == a) {
                z = true;
            } else {
                z = false;
            }
            cell.setTextAndValue(str, z);
            linearLayout.addView(cell);
            cell.setOnClickListener(new AlertsCreator$$Lambda$30(builder, listener));
        }
        builder.setTitle(title);
        builder.setView(linearLayout);
        builder.setPositiveButton(LocaleController.getString("Cancel", R.string.Cancel), null);
        return builder.create();
    }

    static final /* synthetic */ void lambda$createSingleChoiceDialog$31$AlertsCreator(Builder builder, OnClickListener listener, View v) {
        int sel = ((Integer) v.getTag()).intValue();
        builder.getDismissRunnable().run();
        listener.onClick(null, sel);
    }

    public static Builder createTTLAlert(Context context, EncryptedChat encryptedChat) {
        Builder builder = new Builder(context);
        builder.setTitle(LocaleController.getString("MessageLifetime", R.string.MessageLifetime));
        NumberPicker numberPicker = new NumberPicker(context);
        numberPicker.setMinValue(0);
        numberPicker.setMaxValue(20);
        if (encryptedChat.ttl > 0 && encryptedChat.ttl < 16) {
            numberPicker.setValue(encryptedChat.ttl);
        } else if (encryptedChat.ttl == 30) {
            numberPicker.setValue(16);
        } else if (encryptedChat.ttl == 60) {
            numberPicker.setValue(17);
        } else if (encryptedChat.ttl == 3600) {
            numberPicker.setValue(18);
        } else if (encryptedChat.ttl == 86400) {
            numberPicker.setValue(19);
        } else if (encryptedChat.ttl == 604800) {
            numberPicker.setValue(20);
        } else if (encryptedChat.ttl == 0) {
            numberPicker.setValue(0);
        }
        numberPicker.setFormatter(AlertsCreator$$Lambda$31.$instance);
        builder.setView(numberPicker);
        builder.setNegativeButton(LocaleController.getString("Done", R.string.Done), new AlertsCreator$$Lambda$32(encryptedChat, numberPicker));
        return builder;
    }

    static final /* synthetic */ String lambda$createTTLAlert$32$AlertsCreator(int value) {
        if (value == 0) {
            return LocaleController.getString("ShortMessageLifetimeForever", R.string.ShortMessageLifetimeForever);
        }
        if (value >= 1 && value < 16) {
            return LocaleController.formatTTLString(value);
        }
        if (value == 16) {
            return LocaleController.formatTTLString(30);
        }
        if (value == 17) {
            return LocaleController.formatTTLString(60);
        }
        if (value == 18) {
            return LocaleController.formatTTLString(3600);
        }
        if (value == 19) {
            return LocaleController.formatTTLString(86400);
        }
        if (value == 20) {
            return LocaleController.formatTTLString(604800);
        }
        return "";
    }

    static final /* synthetic */ void lambda$createTTLAlert$33$AlertsCreator(EncryptedChat encryptedChat, NumberPicker numberPicker, DialogInterface dialog, int which) {
        int oldValue = encryptedChat.ttl;
        which = numberPicker.getValue();
        if (which >= 0 && which < 16) {
            encryptedChat.ttl = which;
        } else if (which == 16) {
            encryptedChat.ttl = 30;
        } else if (which == 17) {
            encryptedChat.ttl = 60;
        } else if (which == 18) {
            encryptedChat.ttl = 3600;
        } else if (which == 19) {
            encryptedChat.ttl = 86400;
        } else if (which == 20) {
            encryptedChat.ttl = 604800;
        }
        if (oldValue != encryptedChat.ttl) {
            SecretChatHelper.getInstance(UserConfig.selectedAccount).sendTTLMessage(encryptedChat, null);
            MessagesStorage.getInstance(UserConfig.selectedAccount).updateEncryptedChatTTL(encryptedChat);
        }
    }

    public static AlertDialog createAccountSelectDialog(Activity parentActivity, AccountSelectDelegate delegate) {
        if (UserConfig.getActivatedAccountsCount() < 2) {
            return null;
        }
        Builder builder = new Builder((Context) parentActivity);
        Runnable dismissRunnable = builder.getDismissRunnable();
        AlertDialog[] alertDialog = new AlertDialog[1];
        LinearLayout linearLayout = new LinearLayout(parentActivity);
        linearLayout.setOrientation(1);
        for (int a = 0; a < 3; a++) {
            if (UserConfig.getInstance(a).getCurrentUser() != null) {
                AccountSelectCell cell = new AccountSelectCell(parentActivity);
                cell.setAccount(a, false);
                cell.setPadding(AndroidUtilities.dp(14.0f), 0, AndroidUtilities.dp(14.0f), 0);
                cell.setBackgroundDrawable(Theme.getSelectorDrawable(false));
                linearLayout.addView(cell, LayoutHelper.createLinear(-1, 50));
                cell.setOnClickListener(new AlertsCreator$$Lambda$33(alertDialog, dismissRunnable, delegate));
            }
        }
        builder.setTitle(LocaleController.getString("SelectAccount", R.string.SelectAccount));
        builder.setView(linearLayout);
        builder.setPositiveButton(LocaleController.getString("Cancel", R.string.Cancel), null);
        AlertDialog create = builder.create();
        alertDialog[0] = create;
        return create;
    }

    static final /* synthetic */ void lambda$createAccountSelectDialog$34$AlertsCreator(AlertDialog[] alertDialog, Runnable dismissRunnable, AccountSelectDelegate delegate, View v) {
        if (alertDialog[0] != null) {
            alertDialog[0].setOnDismissListener(null);
        }
        dismissRunnable.run();
        delegate.didSelectAccount(((AccountSelectCell) v).getAccountNumber());
    }
}
