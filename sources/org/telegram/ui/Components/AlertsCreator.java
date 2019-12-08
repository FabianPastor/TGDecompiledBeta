package org.telegram.ui.Components;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Point;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Html;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.TextUtils.TruncateAt;
import android.text.style.URLSpan;
import android.util.Base64;
import android.util.SparseArray;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.CountDownLatch;
import org.telegram.messenger.AccountInstance;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.AndroidUtilities.LinkMovementMethodMy;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.LocaleController.LocaleInfo;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessageObject.GroupedMessages;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.MessagesStorage.BooleanCallback;
import org.telegram.messenger.MessagesStorage.IntCallback;
import org.telegram.messenger.NotificationsController;
import org.telegram.messenger.SecretChatHelper;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.SerializedData;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.tgnet.TLRPC.ChannelParticipant;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.ChatFull;
import org.telegram.tgnet.TLRPC.EncryptedChat;
import org.telegram.tgnet.TLRPC.InputPeer;
import org.telegram.tgnet.TLRPC.TL_account_reportPeer;
import org.telegram.tgnet.TLRPC.TL_channelParticipantAdmin;
import org.telegram.tgnet.TLRPC.TL_channelParticipantCreator;
import org.telegram.tgnet.TLRPC.TL_channels_channelParticipant;
import org.telegram.tgnet.TLRPC.TL_channels_createChannel;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_help_getSupport;
import org.telegram.tgnet.TLRPC.TL_help_support;
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
import org.telegram.ui.Cells.CheckBoxCell;
import org.telegram.ui.Cells.RadioColorCell;
import org.telegram.ui.Cells.TextColorCell;
import org.telegram.ui.ChatActivity;
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

    public interface ScheduleDatePickerDelegate {
        void didSelectDate(boolean z, int i);
    }

    static /* synthetic */ void lambda$null$29(TLObject tLObject, TL_error tL_error) {
    }

    static /* synthetic */ void lambda$null$57(TLObject tLObject, TL_error tL_error) {
    }

    /* JADX WARNING: Removed duplicated region for block: B:260:0x04ef  */
    /* JADX WARNING: Removed duplicated region for block: B:254:0x04c4  */
    /* JADX WARNING: Removed duplicated region for block: B:254:0x04c4  */
    /* JADX WARNING: Removed duplicated region for block: B:260:0x04ef  */
    /* JADX WARNING: Removed duplicated region for block: B:260:0x04ef  */
    /* JADX WARNING: Removed duplicated region for block: B:254:0x04c4  */
    /* JADX WARNING: Removed duplicated region for block: B:170:0x0338  */
    /* JADX WARNING: Removed duplicated region for block: B:166:0x031b  */
    /* JADX WARNING: Removed duplicated region for block: B:166:0x031b  */
    /* JADX WARNING: Removed duplicated region for block: B:170:0x0338  */
    /* JADX WARNING: Removed duplicated region for block: B:200:0x03c8  */
    /* JADX WARNING: Removed duplicated region for block: B:196:0x03b0  */
    /* JADX WARNING: Removed duplicated region for block: B:196:0x03b0  */
    /* JADX WARNING: Removed duplicated region for block: B:200:0x03c8  */
    public static android.app.Dialog processError(int r17, org.telegram.tgnet.TLRPC.TL_error r18, org.telegram.ui.ActionBar.BaseFragment r19, org.telegram.tgnet.TLObject r20, java.lang.Object... r21) {
        /*
        r0 = r18;
        r1 = r19;
        r2 = r20;
        r3 = r21;
        r4 = r0.code;
        r5 = 406; // 0x196 float:5.69E-43 double:2.006E-321;
        if (r4 == r5) goto L_0x059a;
    L_0x000e:
        r5 = r0.text;
        if (r5 != 0) goto L_0x0014;
    L_0x0012:
        goto L_0x059a;
    L_0x0014:
        r6 = r2 instanceof org.telegram.tgnet.TLRPC.TL_account_saveSecureValue;
        r7 = "\n";
        r9 = "InvalidPhoneNumber";
        r10 = "PHONE_NUMBER_INVALID";
        r11 = NUM; // 0x7f0d03f9 float:1.8744177E38 double:1.05313028E-314;
        r12 = "ErrorOccurred";
        r13 = NUM; // 0x7f0d0479 float:1.8744437E38 double:1.0531303432E-314;
        r14 = "FloodWait";
        r15 = "FLOOD_WAIT";
        if (r6 != 0) goto L_0x053b;
    L_0x002a:
        r6 = r2 instanceof org.telegram.tgnet.TLRPC.TL_account_getAuthorizationForm;
        if (r6 == 0) goto L_0x0030;
    L_0x002e:
        goto L_0x053b;
    L_0x0030:
        r6 = r2 instanceof org.telegram.tgnet.TLRPC.TL_channels_joinChannel;
        r8 = 0;
        if (r6 != 0) goto L_0x0504;
    L_0x0035:
        r6 = r2 instanceof org.telegram.tgnet.TLRPC.TL_channels_editAdmin;
        if (r6 != 0) goto L_0x0504;
    L_0x0039:
        r6 = r2 instanceof org.telegram.tgnet.TLRPC.TL_channels_inviteToChannel;
        if (r6 != 0) goto L_0x0504;
    L_0x003d:
        r6 = r2 instanceof org.telegram.tgnet.TLRPC.TL_messages_addChatUser;
        if (r6 != 0) goto L_0x0504;
    L_0x0041:
        r6 = r2 instanceof org.telegram.tgnet.TLRPC.TL_messages_startBot;
        if (r6 != 0) goto L_0x0504;
    L_0x0045:
        r6 = r2 instanceof org.telegram.tgnet.TLRPC.TL_channels_editBanned;
        if (r6 != 0) goto L_0x0504;
    L_0x0049:
        r6 = r2 instanceof org.telegram.tgnet.TLRPC.TL_messages_editChatDefaultBannedRights;
        if (r6 != 0) goto L_0x0504;
    L_0x004d:
        r6 = r2 instanceof org.telegram.tgnet.TLRPC.TL_messages_editChatAdmin;
        if (r6 != 0) goto L_0x0504;
    L_0x0051:
        r6 = r2 instanceof org.telegram.tgnet.TLRPC.TL_messages_migrateChat;
        if (r6 == 0) goto L_0x0057;
    L_0x0055:
        goto L_0x0504;
    L_0x0057:
        r6 = r2 instanceof org.telegram.tgnet.TLRPC.TL_messages_createChat;
        if (r6 == 0) goto L_0x006f;
    L_0x005b:
        r3 = r5.startsWith(r15);
        if (r3 == 0) goto L_0x0068;
    L_0x0061:
        r0 = r0.text;
        showFloodWaitAlert(r0, r1);
        goto L_0x0598;
    L_0x0068:
        r0 = r0.text;
        showAddUserAlert(r0, r1, r8, r2);
        goto L_0x0598;
    L_0x006f:
        r6 = r2 instanceof org.telegram.tgnet.TLRPC.TL_channels_createChannel;
        if (r6 == 0) goto L_0x0087;
    L_0x0073:
        r3 = r5.startsWith(r15);
        if (r3 == 0) goto L_0x0080;
    L_0x0079:
        r0 = r0.text;
        showFloodWaitAlert(r0, r1);
        goto L_0x0598;
    L_0x0080:
        r0 = r0.text;
        showAddUserAlert(r0, r1, r8, r2);
        goto L_0x0598;
    L_0x0087:
        r6 = r2 instanceof org.telegram.tgnet.TLRPC.TL_messages_editMessage;
        if (r6 == 0) goto L_0x00b1;
    L_0x008b:
        r0 = "MESSAGE_NOT_MODIFIED";
        r0 = r5.equals(r0);
        if (r0 != 0) goto L_0x0598;
    L_0x0093:
        if (r1 == 0) goto L_0x00a3;
    L_0x0095:
        r0 = NUM; // 0x7f0d03c6 float:1.8744074E38 double:1.053130255E-314;
        r2 = "EditMessageError";
        r0 = org.telegram.messenger.LocaleController.getString(r2, r0);
        showSimpleAlert(r1, r0);
        goto L_0x0598;
    L_0x00a3:
        r0 = NUM; // 0x7f0d03c6 float:1.8744074E38 double:1.053130255E-314;
        r2 = "EditMessageError";
        r0 = org.telegram.messenger.LocaleController.getString(r2, r0);
        showSimpleToast(r1, r0);
        goto L_0x0598;
    L_0x00b1:
        r6 = r2 instanceof org.telegram.tgnet.TLRPC.TL_messages_sendMessage;
        r16 = -1;
        if (r6 != 0) goto L_0x048d;
    L_0x00b7:
        r6 = r2 instanceof org.telegram.tgnet.TLRPC.TL_messages_sendMedia;
        if (r6 != 0) goto L_0x048d;
    L_0x00bb:
        r6 = r2 instanceof org.telegram.tgnet.TLRPC.TL_messages_sendInlineBotResult;
        if (r6 != 0) goto L_0x048d;
    L_0x00bf:
        r6 = r2 instanceof org.telegram.tgnet.TLRPC.TL_messages_forwardMessages;
        if (r6 != 0) goto L_0x048d;
    L_0x00c3:
        r6 = r2 instanceof org.telegram.tgnet.TLRPC.TL_messages_sendMultiMedia;
        if (r6 != 0) goto L_0x048d;
    L_0x00c7:
        r6 = r2 instanceof org.telegram.tgnet.TLRPC.TL_messages_sendScheduledMessages;
        if (r6 == 0) goto L_0x00cd;
    L_0x00cb:
        goto L_0x048d;
    L_0x00cd:
        r6 = r2 instanceof org.telegram.tgnet.TLRPC.TL_messages_importChatInvite;
        if (r6 == 0) goto L_0x0106;
    L_0x00d1:
        r2 = r5.startsWith(r15);
        if (r2 == 0) goto L_0x00e0;
    L_0x00d7:
        r0 = org.telegram.messenger.LocaleController.getString(r14, r13);
        showSimpleAlert(r1, r0);
        goto L_0x0598;
    L_0x00e0:
        r0 = r0.text;
        r2 = "USERS_TOO_MUCH";
        r0 = r0.equals(r2);
        if (r0 == 0) goto L_0x00f8;
    L_0x00ea:
        r0 = NUM; // 0x7f0d054f float:1.8744871E38 double:1.053130449E-314;
        r2 = "JoinToGroupErrorFull";
        r0 = org.telegram.messenger.LocaleController.getString(r2, r0);
        showSimpleAlert(r1, r0);
        goto L_0x0598;
    L_0x00f8:
        r0 = NUM; // 0x7f0d0550 float:1.8744873E38 double:1.0531304495E-314;
        r2 = "JoinToGroupErrorNotExist";
        r0 = org.telegram.messenger.LocaleController.getString(r2, r0);
        showSimpleAlert(r1, r0);
        goto L_0x0598;
    L_0x0106:
        r6 = r2 instanceof org.telegram.tgnet.TLRPC.TL_messages_getAttachedStickers;
        if (r6 == 0) goto L_0x0137;
    L_0x010a:
        if (r1 == 0) goto L_0x0598;
    L_0x010c:
        r2 = r19.getParentActivity();
        if (r2 == 0) goto L_0x0598;
    L_0x0112:
        r1 = r19.getParentActivity();
        r2 = new java.lang.StringBuilder;
        r2.<init>();
        r3 = org.telegram.messenger.LocaleController.getString(r12, r11);
        r2.append(r3);
        r2.append(r7);
        r0 = r0.text;
        r2.append(r0);
        r0 = r2.toString();
        r0 = android.widget.Toast.makeText(r1, r0, r8);
        r0.show();
        goto L_0x0598;
    L_0x0137:
        r6 = r2 instanceof org.telegram.tgnet.TLRPC.TL_account_confirmPhone;
        r8 = "InvalidCode";
        r11 = "PHONE_CODE_EMPTY";
        if (r6 != 0) goto L_0x041f;
    L_0x013f:
        r6 = r2 instanceof org.telegram.tgnet.TLRPC.TL_account_verifyPhone;
        if (r6 != 0) goto L_0x041f;
    L_0x0143:
        r6 = r2 instanceof org.telegram.tgnet.TLRPC.TL_account_verifyEmail;
        if (r6 == 0) goto L_0x0149;
    L_0x0147:
        goto L_0x041f;
    L_0x0149:
        r6 = r2 instanceof org.telegram.tgnet.TLRPC.TL_auth_resendCode;
        if (r6 == 0) goto L_0x01cd;
    L_0x014d:
        r2 = r5.contains(r10);
        if (r2 == 0) goto L_0x015f;
    L_0x0153:
        r2 = NUM; // 0x7f0d0531 float:1.874481E38 double:1.053130434E-314;
        r0 = org.telegram.messenger.LocaleController.getString(r9, r2);
        r0 = showSimpleAlert(r1, r0);
        return r0;
    L_0x015f:
        r2 = r0.text;
        r2 = r2.contains(r11);
        if (r2 != 0) goto L_0x01c1;
    L_0x0167:
        r2 = r0.text;
        r3 = "PHONE_CODE_INVALID";
        r2 = r2.contains(r3);
        if (r2 == 0) goto L_0x0172;
    L_0x0171:
        goto L_0x01c1;
    L_0x0172:
        r2 = r0.text;
        r3 = "PHONE_CODE_EXPIRED";
        r2 = r2.contains(r3);
        if (r2 == 0) goto L_0x018a;
    L_0x017c:
        r0 = NUM; // 0x7f0d02e5 float:1.8743618E38 double:1.0531301436E-314;
        r2 = "CodeExpired";
        r0 = org.telegram.messenger.LocaleController.getString(r2, r0);
        r0 = showSimpleAlert(r1, r0);
        return r0;
    L_0x018a:
        r2 = r0.text;
        r2 = r2.startsWith(r15);
        if (r2 == 0) goto L_0x019b;
    L_0x0192:
        r0 = org.telegram.messenger.LocaleController.getString(r14, r13);
        r0 = showSimpleAlert(r1, r0);
        return r0;
    L_0x019b:
        r2 = r0.code;
        r3 = -1000; // 0xfffffffffffffCLASSNAME float:NaN double:NaN;
        if (r2 == r3) goto L_0x0598;
    L_0x01a1:
        r2 = new java.lang.StringBuilder;
        r2.<init>();
        r3 = NUM; // 0x7f0d03f9 float:1.8744177E38 double:1.05313028E-314;
        r3 = org.telegram.messenger.LocaleController.getString(r12, r3);
        r2.append(r3);
        r2.append(r7);
        r0 = r0.text;
        r2.append(r0);
        r0 = r2.toString();
        r0 = showSimpleAlert(r1, r0);
        return r0;
    L_0x01c1:
        r0 = NUM; // 0x7f0d052e float:1.8744804E38 double:1.0531304327E-314;
        r0 = org.telegram.messenger.LocaleController.getString(r8, r0);
        r0 = showSimpleAlert(r1, r0);
        return r0;
    L_0x01cd:
        r6 = r2 instanceof org.telegram.tgnet.TLRPC.TL_account_sendConfirmPhoneCode;
        if (r6 == 0) goto L_0x0200;
    L_0x01d1:
        r0 = 400; // 0x190 float:5.6E-43 double:1.976E-321;
        if (r4 != r0) goto L_0x01e3;
    L_0x01d5:
        r0 = NUM; // 0x7f0d01fb float:1.8743143E38 double:1.053130028E-314;
        r2 = "CancelLinkExpired";
        r0 = org.telegram.messenger.LocaleController.getString(r2, r0);
        r0 = showSimpleAlert(r1, r0);
        return r0;
    L_0x01e3:
        if (r5 == 0) goto L_0x0598;
    L_0x01e5:
        r0 = r5.startsWith(r15);
        if (r0 == 0) goto L_0x01f4;
    L_0x01eb:
        r0 = org.telegram.messenger.LocaleController.getString(r14, r13);
        r0 = showSimpleAlert(r1, r0);
        return r0;
    L_0x01f4:
        r0 = NUM; // 0x7f0d03f9 float:1.8744177E38 double:1.05313028E-314;
        r0 = org.telegram.messenger.LocaleController.getString(r12, r0);
        r0 = showSimpleAlert(r1, r0);
        return r0;
    L_0x0200:
        r4 = r2 instanceof org.telegram.tgnet.TLRPC.TL_account_changePhone;
        if (r4 == 0) goto L_0x0265;
    L_0x0204:
        r2 = r5.contains(r10);
        if (r2 == 0) goto L_0x0216;
    L_0x020a:
        r2 = NUM; // 0x7f0d0531 float:1.874481E38 double:1.053130434E-314;
        r0 = org.telegram.messenger.LocaleController.getString(r9, r2);
        showSimpleAlert(r1, r0);
        goto L_0x0598;
    L_0x0216:
        r2 = r0.text;
        r2 = r2.contains(r11);
        if (r2 != 0) goto L_0x0259;
    L_0x021e:
        r2 = r0.text;
        r3 = "PHONE_CODE_INVALID";
        r2 = r2.contains(r3);
        if (r2 == 0) goto L_0x0229;
    L_0x0228:
        goto L_0x0259;
    L_0x0229:
        r2 = r0.text;
        r3 = "PHONE_CODE_EXPIRED";
        r2 = r2.contains(r3);
        if (r2 == 0) goto L_0x0241;
    L_0x0233:
        r0 = NUM; // 0x7f0d02e5 float:1.8743618E38 double:1.0531301436E-314;
        r2 = "CodeExpired";
        r0 = org.telegram.messenger.LocaleController.getString(r2, r0);
        showSimpleAlert(r1, r0);
        goto L_0x0598;
    L_0x0241:
        r2 = r0.text;
        r2 = r2.startsWith(r15);
        if (r2 == 0) goto L_0x0252;
    L_0x0249:
        r0 = org.telegram.messenger.LocaleController.getString(r14, r13);
        showSimpleAlert(r1, r0);
        goto L_0x0598;
    L_0x0252:
        r0 = r0.text;
        showSimpleAlert(r1, r0);
        goto L_0x0598;
    L_0x0259:
        r0 = NUM; // 0x7f0d052e float:1.8744804E38 double:1.0531304327E-314;
        r0 = org.telegram.messenger.LocaleController.getString(r8, r0);
        showSimpleAlert(r1, r0);
        goto L_0x0598;
    L_0x0265:
        r4 = r2 instanceof org.telegram.tgnet.TLRPC.TL_account_sendChangePhoneCode;
        if (r4 == 0) goto L_0x02f1;
    L_0x0269:
        r2 = r5.contains(r10);
        if (r2 == 0) goto L_0x027b;
    L_0x026f:
        r2 = NUM; // 0x7f0d0531 float:1.874481E38 double:1.053130434E-314;
        r0 = org.telegram.messenger.LocaleController.getString(r9, r2);
        showSimpleAlert(r1, r0);
        goto L_0x0598;
    L_0x027b:
        r2 = r0.text;
        r2 = r2.contains(r11);
        if (r2 != 0) goto L_0x02e5;
    L_0x0283:
        r2 = r0.text;
        r4 = "PHONE_CODE_INVALID";
        r2 = r2.contains(r4);
        if (r2 == 0) goto L_0x028e;
    L_0x028d:
        goto L_0x02e5;
    L_0x028e:
        r2 = r0.text;
        r4 = "PHONE_CODE_EXPIRED";
        r2 = r2.contains(r4);
        if (r2 == 0) goto L_0x02a6;
    L_0x0298:
        r0 = NUM; // 0x7f0d02e5 float:1.8743618E38 double:1.0531301436E-314;
        r2 = "CodeExpired";
        r0 = org.telegram.messenger.LocaleController.getString(r2, r0);
        showSimpleAlert(r1, r0);
        goto L_0x0598;
    L_0x02a6:
        r2 = r0.text;
        r2 = r2.startsWith(r15);
        if (r2 == 0) goto L_0x02b7;
    L_0x02ae:
        r0 = org.telegram.messenger.LocaleController.getString(r14, r13);
        showSimpleAlert(r1, r0);
        goto L_0x0598;
    L_0x02b7:
        r0 = r0.text;
        r2 = "PHONE_NUMBER_OCCUPIED";
        r0 = r0.startsWith(r2);
        if (r0 == 0) goto L_0x02d9;
    L_0x02c1:
        r0 = NUM; // 0x7f0d0210 float:1.8743186E38 double:1.0531300384E-314;
        r2 = 1;
        r2 = new java.lang.Object[r2];
        r4 = 0;
        r3 = r3[r4];
        r3 = (java.lang.String) r3;
        r2[r4] = r3;
        r3 = "ChangePhoneNumberOccupied";
        r0 = org.telegram.messenger.LocaleController.formatString(r3, r0, r2);
        showSimpleAlert(r1, r0);
        goto L_0x0598;
    L_0x02d9:
        r0 = NUM; // 0x7f0d03f9 float:1.8744177E38 double:1.05313028E-314;
        r0 = org.telegram.messenger.LocaleController.getString(r12, r0);
        showSimpleAlert(r1, r0);
        goto L_0x0598;
    L_0x02e5:
        r0 = NUM; // 0x7f0d052e float:1.8744804E38 double:1.0531304327E-314;
        r0 = org.telegram.messenger.LocaleController.getString(r8, r0);
        showSimpleAlert(r1, r0);
        goto L_0x0598;
    L_0x02f1:
        r3 = r2 instanceof org.telegram.tgnet.TLRPC.TL_updateUserName;
        if (r3 == 0) goto L_0x0346;
    L_0x02f5:
        r0 = r5.hashCode();
        r2 = NUM; // 0x1137676e float:1.4468026E-28 double:1.427077146E-315;
        if (r0 == r2) goto L_0x030e;
    L_0x02fe:
        r2 = NUM; // 0x1fCLASSNAMEbe7 float:8.45377E-20 double:2.634235846E-315;
        if (r0 == r2) goto L_0x0304;
    L_0x0303:
        goto L_0x0318;
    L_0x0304:
        r0 = "USERNAME_OCCUPIED";
        r0 = r5.equals(r0);
        if (r0 == 0) goto L_0x0318;
    L_0x030c:
        r0 = 1;
        goto L_0x0319;
    L_0x030e:
        r0 = "USERNAME_INVALID";
        r0 = r5.equals(r0);
        if (r0 == 0) goto L_0x0318;
    L_0x0316:
        r0 = 0;
        goto L_0x0319;
    L_0x0318:
        r0 = -1;
    L_0x0319:
        if (r0 == 0) goto L_0x0338;
    L_0x031b:
        r2 = 1;
        if (r0 == r2) goto L_0x032a;
    L_0x031e:
        r0 = NUM; // 0x7f0d03f9 float:1.8744177E38 double:1.05313028E-314;
        r0 = org.telegram.messenger.LocaleController.getString(r12, r0);
        showSimpleAlert(r1, r0);
        goto L_0x0598;
    L_0x032a:
        r0 = NUM; // 0x7f0d0b11 float:1.874786E38 double:1.053131177E-314;
        r2 = "UsernameInUse";
        r0 = org.telegram.messenger.LocaleController.getString(r2, r0);
        showSimpleAlert(r1, r0);
        goto L_0x0598;
    L_0x0338:
        r0 = NUM; // 0x7f0d0b12 float:1.8747863E38 double:1.0531311777E-314;
        r2 = "UsernameInvalid";
        r0 = org.telegram.messenger.LocaleController.getString(r2, r0);
        showSimpleAlert(r1, r0);
        goto L_0x0598;
    L_0x0346:
        r3 = r2 instanceof org.telegram.tgnet.TLRPC.TL_contacts_importContacts;
        if (r3 == 0) goto L_0x037c;
    L_0x034a:
        if (r0 == 0) goto L_0x0373;
    L_0x034c:
        r2 = r5.startsWith(r15);
        if (r2 == 0) goto L_0x0353;
    L_0x0352:
        goto L_0x0373;
    L_0x0353:
        r2 = new java.lang.StringBuilder;
        r2.<init>();
        r3 = NUM; // 0x7f0d03f9 float:1.8744177E38 double:1.05313028E-314;
        r3 = org.telegram.messenger.LocaleController.getString(r12, r3);
        r2.append(r3);
        r2.append(r7);
        r0 = r0.text;
        r2.append(r0);
        r0 = r2.toString();
        showSimpleAlert(r1, r0);
        goto L_0x0598;
    L_0x0373:
        r0 = org.telegram.messenger.LocaleController.getString(r14, r13);
        showSimpleAlert(r1, r0);
        goto L_0x0598;
    L_0x037c:
        r3 = r2 instanceof org.telegram.tgnet.TLRPC.TL_account_getPassword;
        if (r3 != 0) goto L_0x0405;
    L_0x0380:
        r3 = r2 instanceof org.telegram.tgnet.TLRPC.TL_account_getTmpPassword;
        if (r3 == 0) goto L_0x0386;
    L_0x0384:
        goto L_0x0405;
    L_0x0386:
        r3 = r2 instanceof org.telegram.tgnet.TLRPC.TL_payments_sendPaymentForm;
        if (r3 == 0) goto L_0x03d6;
    L_0x038a:
        r2 = r5.hashCode();
        r3 = -NUM; // 0xffffffffbbcefe0b float:-0.NUM double:NaN;
        if (r2 == r3) goto L_0x03a3;
    L_0x0393:
        r3 = -NUM; // 0xffffffffd14178b6 float:-5.1934618E10 double:NaN;
        if (r2 == r3) goto L_0x0399;
    L_0x0398:
        goto L_0x03ad;
    L_0x0399:
        r2 = "PAYMENT_FAILED";
        r2 = r5.equals(r2);
        if (r2 == 0) goto L_0x03ad;
    L_0x03a1:
        r2 = 1;
        goto L_0x03ae;
    L_0x03a3:
        r2 = "BOT_PRECHECKOUT_FAILED";
        r2 = r5.equals(r2);
        if (r2 == 0) goto L_0x03ad;
    L_0x03ab:
        r2 = 0;
        goto L_0x03ae;
    L_0x03ad:
        r2 = -1;
    L_0x03ae:
        if (r2 == 0) goto L_0x03c8;
    L_0x03b0:
        r3 = 1;
        if (r2 == r3) goto L_0x03ba;
    L_0x03b3:
        r0 = r0.text;
        showSimpleToast(r1, r0);
        goto L_0x0598;
    L_0x03ba:
        r0 = NUM; // 0x7f0d0816 float:1.8746313E38 double:1.0531308003E-314;
        r2 = "PaymentFailed";
        r0 = org.telegram.messenger.LocaleController.getString(r2, r0);
        showSimpleToast(r1, r0);
        goto L_0x0598;
    L_0x03c8:
        r0 = NUM; // 0x7f0d0823 float:1.874634E38 double:1.0531308067E-314;
        r2 = "PaymentPrecheckoutFailed";
        r0 = org.telegram.messenger.LocaleController.getString(r2, r0);
        showSimpleToast(r1, r0);
        goto L_0x0598;
    L_0x03d6:
        r2 = r2 instanceof org.telegram.tgnet.TLRPC.TL_payments_validateRequestedInfo;
        if (r2 == 0) goto L_0x0598;
    L_0x03da:
        r2 = r5.hashCode();
        r3 = NUM; // 0x68CLASSNAMEc float:7.606448E24 double:8.68580028E-315;
        if (r2 == r3) goto L_0x03e4;
    L_0x03e3:
        goto L_0x03ee;
    L_0x03e4:
        r2 = "SHIPPING_NOT_AVAILABLE";
        r2 = r5.equals(r2);
        if (r2 == 0) goto L_0x03ee;
    L_0x03ec:
        r16 = 0;
    L_0x03ee:
        if (r16 == 0) goto L_0x03f7;
    L_0x03f0:
        r0 = r0.text;
        showSimpleToast(r1, r0);
        goto L_0x0598;
    L_0x03f7:
        r0 = NUM; // 0x7f0d0818 float:1.8746317E38 double:1.0531308012E-314;
        r2 = "PaymentNoShippingMethod";
        r0 = org.telegram.messenger.LocaleController.getString(r2, r0);
        showSimpleToast(r1, r0);
        goto L_0x0598;
    L_0x0405:
        r2 = r0.text;
        r2 = r2.startsWith(r15);
        if (r2 == 0) goto L_0x0418;
    L_0x040d:
        r0 = r0.text;
        r0 = getFloodWaitString(r0);
        showSimpleToast(r1, r0);
        goto L_0x0598;
    L_0x0418:
        r0 = r0.text;
        showSimpleToast(r1, r0);
        goto L_0x0598;
    L_0x041f:
        r2 = r0.text;
        r2 = r2.contains(r11);
        if (r2 != 0) goto L_0x0481;
    L_0x0427:
        r2 = r0.text;
        r3 = "PHONE_CODE_INVALID";
        r2 = r2.contains(r3);
        if (r2 != 0) goto L_0x0481;
    L_0x0431:
        r2 = r0.text;
        r3 = "CODE_INVALID";
        r2 = r2.contains(r3);
        if (r2 != 0) goto L_0x0481;
    L_0x043b:
        r2 = r0.text;
        r3 = "CODE_EMPTY";
        r2 = r2.contains(r3);
        if (r2 == 0) goto L_0x0446;
    L_0x0445:
        goto L_0x0481;
    L_0x0446:
        r2 = r0.text;
        r3 = "PHONE_CODE_EXPIRED";
        r2 = r2.contains(r3);
        if (r2 != 0) goto L_0x0473;
    L_0x0450:
        r2 = r0.text;
        r3 = "EMAIL_VERIFY_EXPIRED";
        r2 = r2.contains(r3);
        if (r2 == 0) goto L_0x045b;
    L_0x045a:
        goto L_0x0473;
    L_0x045b:
        r2 = r0.text;
        r2 = r2.startsWith(r15);
        if (r2 == 0) goto L_0x046c;
    L_0x0463:
        r0 = org.telegram.messenger.LocaleController.getString(r14, r13);
        r0 = showSimpleAlert(r1, r0);
        return r0;
    L_0x046c:
        r0 = r0.text;
        r0 = showSimpleAlert(r1, r0);
        return r0;
    L_0x0473:
        r0 = NUM; // 0x7f0d02e5 float:1.8743618E38 double:1.0531301436E-314;
        r2 = "CodeExpired";
        r0 = org.telegram.messenger.LocaleController.getString(r2, r0);
        r0 = showSimpleAlert(r1, r0);
        return r0;
    L_0x0481:
        r0 = NUM; // 0x7f0d052e float:1.8744804E38 double:1.0531304327E-314;
        r0 = org.telegram.messenger.LocaleController.getString(r8, r0);
        r0 = showSimpleAlert(r1, r0);
        return r0;
    L_0x048d:
        r0 = r0.text;
        r2 = r0.hashCode();
        r3 = -NUM; // 0xfffffffvar_b816 float:-8.417163E-27 double:NaN;
        if (r2 == r3) goto L_0x04b7;
    L_0x0498:
        r3 = -NUM; // 0xffffffffe4efe6c1 float:-3.5403195E22 double:NaN;
        if (r2 == r3) goto L_0x04ad;
    L_0x049d:
        r3 = NUM; // 0x45b984e0 float:5936.6094 double:5.77951115E-315;
        if (r2 == r3) goto L_0x04a3;
    L_0x04a2:
        goto L_0x04c1;
    L_0x04a3:
        r2 = "SCHEDULE_TOO_MUCH";
        r0 = r0.equals(r2);
        if (r0 == 0) goto L_0x04c1;
    L_0x04ab:
        r8 = 2;
        goto L_0x04c2;
    L_0x04ad:
        r2 = "PEER_FLOOD";
        r0 = r0.equals(r2);
        if (r0 == 0) goto L_0x04c1;
    L_0x04b5:
        r8 = 0;
        goto L_0x04c2;
    L_0x04b7:
        r2 = "USER_BANNED_IN_CHANNEL";
        r0 = r0.equals(r2);
        if (r0 == 0) goto L_0x04c1;
    L_0x04bf:
        r8 = 1;
        goto L_0x04c2;
    L_0x04c1:
        r8 = -1;
    L_0x04c2:
        if (r8 == 0) goto L_0x04ef;
    L_0x04c4:
        r0 = 1;
        if (r8 == r0) goto L_0x04da;
    L_0x04c7:
        r0 = 2;
        if (r8 == r0) goto L_0x04cc;
    L_0x04ca:
        goto L_0x0598;
    L_0x04cc:
        r0 = NUM; // 0x7f0d05f5 float:1.8745208E38 double:1.053130531E-314;
        r2 = "MessageScheduledLimitReached";
        r0 = org.telegram.messenger.LocaleController.getString(r2, r0);
        showSimpleToast(r1, r0);
        goto L_0x0598;
    L_0x04da:
        r1 = org.telegram.messenger.NotificationCenter.getInstance(r17);
        r2 = org.telegram.messenger.NotificationCenter.needShowAlert;
        r0 = new java.lang.Object[r0];
        r3 = 5;
        r3 = java.lang.Integer.valueOf(r3);
        r4 = 0;
        r0[r4] = r3;
        r1.postNotificationName(r2, r0);
        goto L_0x0598;
    L_0x04ef:
        r0 = 1;
        r4 = 0;
        r1 = org.telegram.messenger.NotificationCenter.getInstance(r17);
        r2 = org.telegram.messenger.NotificationCenter.needShowAlert;
        r0 = new java.lang.Object[r0];
        r3 = java.lang.Integer.valueOf(r4);
        r0[r4] = r3;
        r1.postNotificationName(r2, r0);
        goto L_0x0598;
    L_0x0504:
        if (r1 == 0) goto L_0x051d;
    L_0x0506:
        r0 = r0.text;
        if (r3 == 0) goto L_0x0517;
    L_0x050a:
        r4 = r3.length;
        if (r4 <= 0) goto L_0x0517;
    L_0x050d:
        r4 = 0;
        r3 = r3[r4];
        r3 = (java.lang.Boolean) r3;
        r8 = r3.booleanValue();
        goto L_0x0518;
    L_0x0517:
        r8 = 0;
    L_0x0518:
        showAddUserAlert(r0, r1, r8, r2);
        goto L_0x0598;
    L_0x051d:
        r0 = r0.text;
        r1 = "PEER_FLOOD";
        r0 = r0.equals(r1);
        if (r0 == 0) goto L_0x0598;
    L_0x0527:
        r0 = org.telegram.messenger.NotificationCenter.getInstance(r17);
        r1 = org.telegram.messenger.NotificationCenter.needShowAlert;
        r2 = 1;
        r3 = new java.lang.Object[r2];
        r2 = java.lang.Integer.valueOf(r2);
        r4 = 0;
        r3[r4] = r2;
        r0.postNotificationName(r1, r3);
        goto L_0x0598;
    L_0x053b:
        r2 = r0.text;
        r2 = r2.contains(r10);
        if (r2 == 0) goto L_0x054e;
    L_0x0543:
        r2 = NUM; // 0x7f0d0531 float:1.874481E38 double:1.053130434E-314;
        r0 = org.telegram.messenger.LocaleController.getString(r9, r2);
        showSimpleAlert(r1, r0);
        goto L_0x0598;
    L_0x054e:
        r2 = r0.text;
        r2 = r2.startsWith(r15);
        if (r2 == 0) goto L_0x055e;
    L_0x0556:
        r0 = org.telegram.messenger.LocaleController.getString(r14, r13);
        showSimpleAlert(r1, r0);
        goto L_0x0598;
    L_0x055e:
        r2 = r0.text;
        r3 = "APP_VERSION_OUTDATED";
        r2 = r3.equals(r2);
        if (r2 == 0) goto L_0x057a;
    L_0x0568:
        r0 = r19.getParentActivity();
        r1 = NUM; // 0x7f0d0ac2 float:1.87477E38 double:1.053131138E-314;
        r2 = "UpdateAppAlert";
        r1 = org.telegram.messenger.LocaleController.getString(r2, r1);
        r2 = 1;
        showUpdateAppAlert(r0, r1, r2);
        goto L_0x0598;
    L_0x057a:
        r2 = new java.lang.StringBuilder;
        r2.<init>();
        r3 = NUM; // 0x7f0d03f9 float:1.8744177E38 double:1.05313028E-314;
        r3 = org.telegram.messenger.LocaleController.getString(r12, r3);
        r2.append(r3);
        r2.append(r7);
        r0 = r0.text;
        r2.append(r0);
        r0 = r2.toString();
        showSimpleAlert(r1, r0);
    L_0x0598:
        r0 = 0;
        return r0;
    L_0x059a:
        r0 = 0;
        return r0;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.AlertsCreator.processError(int, org.telegram.tgnet.TLRPC$TL_error, org.telegram.ui.ActionBar.BaseFragment, org.telegram.tgnet.TLObject, java.lang.Object[]):android.app.Dialog");
    }

    public static Toast showSimpleToast(BaseFragment baseFragment, String str) {
        if (str == null) {
            return null;
        }
        Context context;
        if (baseFragment == null || baseFragment.getParentActivity() == null) {
            context = ApplicationLoader.applicationContext;
        } else {
            context = baseFragment.getParentActivity();
        }
        Toast makeText = Toast.makeText(context, str, 1);
        makeText.show();
        return makeText;
    }

    public static AlertDialog showUpdateAppAlert(Context context, String str, boolean z) {
        if (context == null || str == null) {
            return null;
        }
        Builder builder = new Builder(context);
        builder.setTitle(LocaleController.getString("AppName", NUM));
        builder.setMessage(str);
        builder.setPositiveButton(LocaleController.getString("OK", NUM), null);
        if (z) {
            builder.setNegativeButton(LocaleController.getString("UpdateApp", NUM), new -$$Lambda$AlertsCreator$msGS4QN_R2Ivdo98cFI--iWFJUI(context));
        }
        return builder.show();
    }

    public static Builder createLanguageAlert(LaunchActivity launchActivity, TL_langPackLanguage tL_langPackLanguage) {
        if (tL_langPackLanguage == null) {
            return null;
        }
        String formatString;
        int indexOf;
        tL_langPackLanguage.lang_code = tL_langPackLanguage.lang_code.replace('-', '_').toLowerCase();
        tL_langPackLanguage.plural_code = tL_langPackLanguage.plural_code.replace('-', '_').toLowerCase();
        String str = tL_langPackLanguage.base_lang_code;
        if (str != null) {
            tL_langPackLanguage.base_lang_code = str.replace('-', '_').toLowerCase();
        }
        final Builder builder = new Builder((Context) launchActivity);
        String str2 = "OK";
        if (LocaleController.getInstance().getCurrentLocaleInfo().shortName.equals(tL_langPackLanguage.lang_code)) {
            builder.setTitle(LocaleController.getString("Language", NUM));
            formatString = LocaleController.formatString("LanguageSame", NUM, tL_langPackLanguage.name);
            builder.setNegativeButton(LocaleController.getString(str2, NUM), null);
            builder.setNeutralButton(LocaleController.getString("SETTINGS", NUM), new -$$Lambda$AlertsCreator$LEkyy2uvzoVwagVSlDPe5F3R2jI(launchActivity));
        } else if (tL_langPackLanguage.strings_count == 0) {
            builder.setTitle(LocaleController.getString("LanguageUnknownTitle", NUM));
            formatString = LocaleController.formatString("LanguageUnknownCustomAlert", NUM, tL_langPackLanguage.name);
            builder.setNegativeButton(LocaleController.getString(str2, NUM), null);
        } else {
            builder.setTitle(LocaleController.getString("LanguageTitle", NUM));
            if (tL_langPackLanguage.official) {
                formatString = LocaleController.formatString("LanguageAlert", NUM, tL_langPackLanguage.name, Integer.valueOf((int) Math.ceil((double) ((((float) tL_langPackLanguage.translated_count) / ((float) tL_langPackLanguage.strings_count)) * 100.0f))));
            } else {
                formatString = LocaleController.formatString("LanguageCustomAlert", NUM, tL_langPackLanguage.name, Integer.valueOf((int) Math.ceil((double) ((((float) tL_langPackLanguage.translated_count) / ((float) tL_langPackLanguage.strings_count)) * 100.0f))));
            }
            builder.setPositiveButton(LocaleController.getString("Change", NUM), new -$$Lambda$AlertsCreator$TOP_U4EklnBKZLMjFtDvABD1mPg(tL_langPackLanguage, launchActivity));
            builder.setNegativeButton(LocaleController.getString("Cancel", NUM), null);
        }
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(AndroidUtilities.replaceTags(formatString));
        int indexOf2 = TextUtils.indexOf(spannableStringBuilder, '[');
        if (indexOf2 != -1) {
            int i = indexOf2 + 1;
            indexOf = TextUtils.indexOf(spannableStringBuilder, ']', i);
            if (!(indexOf2 == -1 || indexOf == -1)) {
                spannableStringBuilder.delete(indexOf, indexOf + 1);
                spannableStringBuilder.delete(indexOf2, i);
            }
        } else {
            indexOf = -1;
        }
        if (!(indexOf2 == -1 || indexOf == -1)) {
            spannableStringBuilder.setSpan(new URLSpanNoUnderline(tL_langPackLanguage.translations_url) {
                public void onClick(View view) {
                    builder.getDismissRunnable().run();
                    super.onClick(view);
                }
            }, indexOf2, indexOf - 1, 33);
        }
        TextView textView = new TextView(launchActivity);
        textView.setText(spannableStringBuilder);
        textView.setTextSize(1, 16.0f);
        textView.setLinkTextColor(Theme.getColor("dialogTextLink"));
        textView.setHighlightColor(Theme.getColor("dialogLinkSelection"));
        textView.setPadding(AndroidUtilities.dp(23.0f), 0, AndroidUtilities.dp(23.0f), 0);
        textView.setMovementMethod(new LinkMovementMethodMy());
        textView.setTextColor(Theme.getColor("dialogTextBlack"));
        builder.setView(textView);
        return builder;
    }

    static /* synthetic */ void lambda$createLanguageAlert$2(TL_langPackLanguage tL_langPackLanguage, LaunchActivity launchActivity, DialogInterface dialogInterface, int i) {
        String stringBuilder;
        StringBuilder stringBuilder2;
        if (tL_langPackLanguage.official) {
            stringBuilder2 = new StringBuilder();
            stringBuilder2.append("remote_");
            stringBuilder2.append(tL_langPackLanguage.lang_code);
            stringBuilder = stringBuilder2.toString();
        } else {
            stringBuilder2 = new StringBuilder();
            stringBuilder2.append("unofficial_");
            stringBuilder2.append(tL_langPackLanguage.lang_code);
            stringBuilder = stringBuilder2.toString();
        }
        LocaleInfo languageFromDict = LocaleController.getInstance().getLanguageFromDict(stringBuilder);
        if (languageFromDict == null) {
            languageFromDict = new LocaleInfo();
            languageFromDict.name = tL_langPackLanguage.native_name;
            languageFromDict.nameEnglish = tL_langPackLanguage.name;
            languageFromDict.shortName = tL_langPackLanguage.lang_code;
            languageFromDict.baseLangCode = tL_langPackLanguage.base_lang_code;
            languageFromDict.pluralLangCode = tL_langPackLanguage.plural_code;
            languageFromDict.isRtl = tL_langPackLanguage.rtl;
            if (tL_langPackLanguage.official) {
                languageFromDict.pathToFile = "remote";
            } else {
                languageFromDict.pathToFile = "unofficial";
            }
        }
        LocaleController.getInstance().applyLanguage(languageFromDict, true, false, false, true, UserConfig.selectedAccount);
        launchActivity.rebuildAllFragments(true);
    }

    public static boolean checkSlowMode(Context context, int i, long j, boolean z) {
        int i2 = (int) j;
        if (i2 < 0) {
            Chat chat = MessagesController.getInstance(i).getChat(Integer.valueOf(-i2));
            if (!(chat == null || !chat.slowmode_enabled || ChatObject.hasAdminRights(chat))) {
                if (!z) {
                    ChatFull chatFull = MessagesController.getInstance(i).getChatFull(chat.id);
                    if (chatFull == null) {
                        chatFull = MessagesStorage.getInstance(i).loadChatInfo(chat.id, new CountDownLatch(1), false, false);
                    }
                    if (chatFull != null && chatFull.slowmode_next_send_date >= ConnectionsManager.getInstance(i).getCurrentTime()) {
                        z = true;
                    }
                }
                if (z) {
                    createSimpleAlert(context, chat.title, LocaleController.getString("SlowmodeSendError", NUM)).show();
                    return true;
                }
            }
        }
        return false;
    }

    public static Builder createSimpleAlert(Context context, String str) {
        return createSimpleAlert(context, null, str);
    }

    public static Builder createSimpleAlert(Context context, String str, String str2) {
        if (str2 == null) {
            return null;
        }
        CharSequence str3;
        Builder builder = new Builder(context);
        if (str3 == null) {
            str3 = LocaleController.getString("AppName", NUM);
        }
        builder.setTitle(str3);
        builder.setMessage(str2);
        builder.setPositiveButton(LocaleController.getString("OK", NUM), null);
        return builder;
    }

    public static Dialog showSimpleAlert(BaseFragment baseFragment, String str) {
        return showSimpleAlert(baseFragment, null, str);
    }

    public static Dialog showSimpleAlert(BaseFragment baseFragment, String str, String str2) {
        if (str2 == null || baseFragment == null || baseFragment.getParentActivity() == null) {
            return null;
        }
        AlertDialog create = createSimpleAlert(baseFragment.getParentActivity(), str, str2).create();
        baseFragment.showDialog(create);
        return create;
    }

    /* JADX WARNING: Removed duplicated region for block: B:21:0x00eb  */
    /* JADX WARNING: Removed duplicated region for block: B:20:0x00e6  */
    /* JADX WARNING: Removed duplicated region for block: B:25:0x00f8  */
    /* JADX WARNING: Removed duplicated region for block: B:24:0x00f3  */
    public static void showBlockReportSpamAlert(org.telegram.ui.ActionBar.BaseFragment r18, long r19, org.telegram.tgnet.TLRPC.User r21, org.telegram.tgnet.TLRPC.Chat r22, org.telegram.tgnet.TLRPC.EncryptedChat r23, boolean r24, org.telegram.tgnet.TLRPC.ChatFull r25, org.telegram.messenger.MessagesStorage.IntCallback r26) {
        /*
        r0 = r18;
        r7 = r22;
        r1 = r25;
        if (r0 == 0) goto L_0x01df;
    L_0x0008:
        r2 = r18.getParentActivity();
        if (r2 != 0) goto L_0x0010;
    L_0x000e:
        goto L_0x01df;
    L_0x0010:
        r3 = r18.getAccountInstance();
        r11 = new org.telegram.ui.ActionBar.AlertDialog$Builder;
        r2 = r18.getParentActivity();
        r11.<init>(r2);
        r2 = r18.getCurrentAccount();
        r2 = org.telegram.messenger.MessagesController.getNotificationsSettings(r2);
        r4 = new java.lang.StringBuilder;
        r4.<init>();
        r5 = "dialog_bar_report";
        r4.append(r5);
        r5 = r19;
        r4.append(r5);
        r4 = r4.toString();
        r8 = 0;
        r2 = r2.getBoolean(r4, r8);
        r4 = 1;
        if (r21 == 0) goto L_0x012c;
    L_0x0040:
        r1 = NUM; // 0x7f0d01bc float:1.8743015E38 double:1.053129997E-314;
        r9 = new java.lang.Object[r4];
        r10 = org.telegram.messenger.UserObject.getFirstName(r21);
        r9[r8] = r10;
        r10 = "BlockUserTitle";
        r1 = org.telegram.messenger.LocaleController.formatString(r10, r1, r9);
        r11.setTitle(r1);
        r1 = NUM; // 0x7f0d01b8 float:1.8743007E38 double:1.053129995E-314;
        r9 = new java.lang.Object[r4];
        r10 = org.telegram.messenger.UserObject.getFirstName(r21);
        r9[r8] = r10;
        r10 = "BlockUserAlert";
        r1 = org.telegram.messenger.LocaleController.formatString(r10, r1, r9);
        r1 = org.telegram.messenger.AndroidUtilities.replaceTags(r1);
        r11.setMessage(r1);
        r1 = NUM; // 0x7f0d01b6 float:1.8743003E38 double:1.053129994E-314;
        r9 = "BlockContact";
        r1 = org.telegram.messenger.LocaleController.getString(r9, r1);
        r9 = 2;
        r10 = new org.telegram.ui.Cells.CheckBoxCell[r9];
        r14 = new android.widget.LinearLayout;
        r15 = r18.getParentActivity();
        r14.<init>(r15);
        r14.setOrientation(r4);
        r15 = 0;
    L_0x0085:
        if (r15 >= r9) goto L_0x011d;
    L_0x0087:
        if (r15 != 0) goto L_0x0091;
    L_0x0089:
        if (r2 != 0) goto L_0x0091;
    L_0x008b:
        r16 = r1;
        r17 = r2;
        goto L_0x0114;
    L_0x0091:
        r9 = new org.telegram.ui.Cells.CheckBoxCell;
        r13 = r18.getParentActivity();
        r9.<init>(r13, r4);
        r10[r15] = r9;
        r9 = r10[r15];
        r13 = org.telegram.ui.ActionBar.Theme.getSelectorDrawable(r8);
        r9.setBackgroundDrawable(r13);
        r9 = r10[r15];
        r13 = java.lang.Integer.valueOf(r15);
        r9.setTag(r13);
        r9 = "";
        if (r15 != 0) goto L_0x00c3;
    L_0x00b2:
        r13 = r10[r15];
        r12 = NUM; // 0x7f0d0370 float:1.87439E38 double:1.0531302123E-314;
        r16 = r1;
        r1 = "DeleteReportSpam";
        r1 = org.telegram.messenger.LocaleController.getString(r1, r12);
        r13.setText(r1, r9, r4, r8);
        goto L_0x00da;
    L_0x00c3:
        r16 = r1;
        if (r15 != r4) goto L_0x00da;
    L_0x00c7:
        r1 = r10[r15];
        r12 = NUM; // 0x7f0d0373 float:1.8743906E38 double:1.053130214E-314;
        r13 = new java.lang.Object[r8];
        r17 = r2;
        r2 = "DeleteThisChat";
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r12, r13);
        r1.setText(r2, r9, r4, r8);
        goto L_0x00dc;
    L_0x00da:
        r17 = r2;
    L_0x00dc:
        r1 = r10[r15];
        r2 = org.telegram.messenger.LocaleController.isRTL;
        r9 = NUM; // 0x41800000 float:16.0 double:5.42932517E-315;
        r12 = NUM; // 0x41000000 float:8.0 double:5.38787994E-315;
        if (r2 == 0) goto L_0x00eb;
    L_0x00e6:
        r2 = org.telegram.messenger.AndroidUtilities.dp(r9);
        goto L_0x00ef;
    L_0x00eb:
        r2 = org.telegram.messenger.AndroidUtilities.dp(r12);
    L_0x00ef:
        r13 = org.telegram.messenger.LocaleController.isRTL;
        if (r13 == 0) goto L_0x00f8;
    L_0x00f3:
        r9 = org.telegram.messenger.AndroidUtilities.dp(r12);
        goto L_0x00fc;
    L_0x00f8:
        r9 = org.telegram.messenger.AndroidUtilities.dp(r9);
    L_0x00fc:
        r1.setPadding(r2, r8, r9, r8);
        r1 = r10[r15];
        r2 = -2;
        r9 = -1;
        r2 = org.telegram.ui.Components.LayoutHelper.createLinear(r9, r2);
        r14.addView(r1, r2);
        r1 = r10[r15];
        r2 = new org.telegram.ui.Components.-$$Lambda$AlertsCreator$UlzF7udPi23pwPuOF4VG7RaKfIg;
        r2.<init>(r10);
        r1.setOnClickListener(r2);
    L_0x0114:
        r15 = r15 + 1;
        r1 = r16;
        r2 = r17;
        r9 = 2;
        goto L_0x0085;
    L_0x011d:
        r16 = r1;
        r1 = 12;
        r11.setCustomViewOffset(r1);
        r11.setView(r14);
        r4 = r10;
        r12 = r16;
        goto L_0x01a4;
    L_0x012c:
        if (r7 == 0) goto L_0x016a;
    L_0x012e:
        if (r24 == 0) goto L_0x016a;
    L_0x0130:
        r2 = NUM; // 0x7f0d0900 float:1.8746788E38 double:1.053130916E-314;
        r9 = "ReportUnrelatedGroup";
        r2 = org.telegram.messenger.LocaleController.getString(r9, r2);
        r11.setTitle(r2);
        if (r1 == 0) goto L_0x015d;
    L_0x013e:
        r1 = r1.location;
        r2 = r1 instanceof org.telegram.tgnet.TLRPC.TL_channelLocation;
        if (r2 == 0) goto L_0x015d;
    L_0x0144:
        r1 = (org.telegram.tgnet.TLRPC.TL_channelLocation) r1;
        r2 = NUM; // 0x7f0d0901 float:1.874679E38 double:1.0531309164E-314;
        r4 = new java.lang.Object[r4];
        r1 = r1.address;
        r4[r8] = r1;
        r1 = "ReportUnrelatedGroupText";
        r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r4);
        r1 = org.telegram.messenger.AndroidUtilities.replaceTags(r1);
        r11.setMessage(r1);
        goto L_0x0199;
    L_0x015d:
        r1 = NUM; // 0x7f0d0902 float:1.8746792E38 double:1.053130917E-314;
        r2 = "ReportUnrelatedGroupTextNoAddress";
        r1 = org.telegram.messenger.LocaleController.getString(r2, r1);
        r11.setMessage(r1);
        goto L_0x0199;
    L_0x016a:
        r1 = NUM; // 0x7f0d08fe float:1.8746784E38 double:1.053130915E-314;
        r2 = "ReportSpamTitle";
        r1 = org.telegram.messenger.LocaleController.getString(r2, r1);
        r11.setTitle(r1);
        r1 = org.telegram.messenger.ChatObject.isChannel(r22);
        if (r1 == 0) goto L_0x018d;
    L_0x017c:
        r1 = r7.megagroup;
        if (r1 != 0) goto L_0x018d;
    L_0x0180:
        r1 = NUM; // 0x7f0d08fa float:1.8746776E38 double:1.053130913E-314;
        r2 = "ReportSpamAlertChannel";
        r1 = org.telegram.messenger.LocaleController.getString(r2, r1);
        r11.setMessage(r1);
        goto L_0x0199;
    L_0x018d:
        r1 = NUM; // 0x7f0d08fb float:1.8746778E38 double:1.0531309134E-314;
        r2 = "ReportSpamAlertGroup";
        r1 = org.telegram.messenger.LocaleController.getString(r2, r1);
        r11.setMessage(r1);
    L_0x0199:
        r1 = NUM; // 0x7f0d08f0 float:1.8746755E38 double:1.053130908E-314;
        r2 = "ReportChat";
        r1 = org.telegram.messenger.LocaleController.getString(r2, r1);
        r12 = r1;
        r4 = 0;
    L_0x01a4:
        r13 = new org.telegram.ui.Components.-$$Lambda$AlertsCreator$FzGvQfgCXLhRw__kmqisROHRtNs;
        r1 = r13;
        r2 = r21;
        r5 = r19;
        r7 = r22;
        r8 = r23;
        r9 = r24;
        r10 = r26;
        r1.<init>(r2, r3, r4, r5, r7, r8, r9, r10);
        r11.setPositiveButton(r12, r13);
        r1 = NUM; // 0x7f0d01f7 float:1.8743135E38 double:1.053130026E-314;
        r2 = "Cancel";
        r1 = org.telegram.messenger.LocaleController.getString(r2, r1);
        r2 = 0;
        r11.setNegativeButton(r1, r2);
        r1 = r11.create();
        r0.showDialog(r1);
        r0 = -1;
        r0 = r1.getButton(r0);
        r0 = (android.widget.TextView) r0;
        if (r0 == 0) goto L_0x01df;
    L_0x01d6:
        r1 = "dialogTextRed2";
        r1 = org.telegram.ui.ActionBar.Theme.getColor(r1);
        r0.setTextColor(r1);
    L_0x01df:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.AlertsCreator.showBlockReportSpamAlert(org.telegram.ui.ActionBar.BaseFragment, long, org.telegram.tgnet.TLRPC$User, org.telegram.tgnet.TLRPC$Chat, org.telegram.tgnet.TLRPC$EncryptedChat, boolean, org.telegram.tgnet.TLRPC$ChatFull, org.telegram.messenger.MessagesStorage$IntCallback):void");
    }

    static /* synthetic */ void lambda$showBlockReportSpamAlert$3(CheckBoxCell[] checkBoxCellArr, View view) {
        Integer num = (Integer) view.getTag();
        checkBoxCellArr[num.intValue()].setChecked(checkBoxCellArr[num.intValue()].isChecked() ^ 1, true);
    }

    static /* synthetic */ void lambda$showBlockReportSpamAlert$4(User user, AccountInstance accountInstance, CheckBoxCell[] checkBoxCellArr, long j, Chat chat, EncryptedChat encryptedChat, boolean z, IntCallback intCallback, DialogInterface dialogInterface, int i) {
        User user2 = user;
        long j2 = j;
        IntCallback intCallback2 = intCallback;
        if (user2 != null) {
            accountInstance.getMessagesController().blockUser(user2.id);
        }
        if (checkBoxCellArr == null || (checkBoxCellArr[0] != null && checkBoxCellArr[0].isChecked())) {
            MessagesController messagesController = accountInstance.getMessagesController();
            boolean z2 = chat != null && z;
            messagesController.reportSpam(j, user, chat, encryptedChat, z2);
        }
        if (checkBoxCellArr == null || checkBoxCellArr[1].isChecked()) {
            if (chat == null) {
                accountInstance.getMessagesController().deleteDialog(j2, 0);
            } else if (ChatObject.isNotInChat(chat)) {
                accountInstance.getMessagesController().deleteDialog(j2, 0);
            } else {
                accountInstance.getMessagesController().deleteUserFromChat((int) (-j2), accountInstance.getMessagesController().getUser(Integer.valueOf(accountInstance.getUserConfig().getClientUserId())), null);
            }
            intCallback2.run(1);
            return;
        }
        intCallback2.run(0);
    }

    public static void showCustomNotificationsDialog(BaseFragment baseFragment, long j, int i, ArrayList<NotificationException> arrayList, int i2, IntCallback intCallback) {
        showCustomNotificationsDialog(baseFragment, j, i, arrayList, i2, intCallback, null);
    }

    public static void showCustomNotificationsDialog(BaseFragment baseFragment, long j, int i, ArrayList<NotificationException> arrayList, int i2, IntCallback intCallback, IntCallback intCallback2) {
        BaseFragment baseFragment2 = baseFragment;
        long j2 = j;
        if (baseFragment2 != null && baseFragment.getParentActivity() != null) {
            String str;
            Builder builder;
            View view;
            boolean isGlobalNotificationsEnabled = NotificationsController.getInstance(i2).isGlobalNotificationsEnabled(j2);
            String[] strArr = new String[5];
            strArr[0] = LocaleController.getString("NotificationsTurnOn", NUM);
            int i3 = 1;
            String str2 = "MuteFor";
            strArr[1] = LocaleController.formatString(str2, NUM, LocaleController.formatPluralString("Hours", 1));
            strArr[2] = LocaleController.formatString(str2, NUM, LocaleController.formatPluralString("Days", 2));
            Drawable drawable = null;
            if (j2 == 0 && (baseFragment2 instanceof NotificationsCustomSettingsActivity)) {
                str = null;
            } else {
                str = LocaleController.getString("NotificationsCustomize", NUM);
            }
            strArr[3] = str;
            strArr[4] = LocaleController.getString("NotificationsTurnOff", NUM);
            int[] iArr = new int[]{NUM, NUM, NUM, NUM, NUM};
            View linearLayout = new LinearLayout(baseFragment.getParentActivity());
            linearLayout.setOrientation(1);
            Builder builder2 = new Builder(baseFragment.getParentActivity());
            int i4 = 0;
            while (i4 < strArr.length) {
                int i5;
                int[] iArr2;
                Drawable drawable2;
                boolean z;
                if (strArr[i4] == null) {
                    i5 = i4;
                    builder = builder2;
                    iArr2 = iArr;
                    drawable2 = drawable;
                    z = isGlobalNotificationsEnabled;
                    view = linearLayout;
                } else {
                    TextView textView = new TextView(baseFragment.getParentActivity());
                    Drawable drawable3 = baseFragment.getParentActivity().getResources().getDrawable(iArr[i4]);
                    if (i4 == strArr.length - i3) {
                        textView.setTextColor(Theme.getColor("dialogTextRed"));
                        drawable3.setColorFilter(new PorterDuffColorFilter(Theme.getColor("dialogRedIcon"), Mode.MULTIPLY));
                    } else {
                        textView.setTextColor(Theme.getColor("dialogTextBlack"));
                        drawable3.setColorFilter(new PorterDuffColorFilter(Theme.getColor("dialogIcon"), Mode.MULTIPLY));
                    }
                    textView.setTextSize(i3, 16.0f);
                    textView.setLines(i3);
                    textView.setMaxLines(i3);
                    textView.setCompoundDrawablesWithIntrinsicBounds(drawable3, drawable, drawable, drawable);
                    textView.setTag(Integer.valueOf(i4));
                    textView.setBackgroundDrawable(Theme.getSelectorDrawable(false));
                    textView.setPadding(AndroidUtilities.dp(24.0f), 0, AndroidUtilities.dp(24.0f), 0);
                    textView.setSingleLine(i3);
                    textView.setGravity(19);
                    textView.setCompoundDrawablePadding(AndroidUtilities.dp(26.0f));
                    textView.setText(strArr[i4]);
                    linearLayout.addView(textView, LayoutHelper.createLinear(-1, 48, 51));
                    -$$Lambda$AlertsCreator$_asGOIm29x1HlIHBvP0Lw8bBq2Y -__lambda_alertscreator__asgoim29x1hlihbvp0lw8bbq2y = r0;
                    TextView textView2 = textView;
                    i5 = i4;
                    builder = builder2;
                    z = isGlobalNotificationsEnabled;
                    view = linearLayout;
                    iArr2 = iArr;
                    drawable2 = drawable;
                    -$$Lambda$AlertsCreator$_asGOIm29x1HlIHBvP0Lw8bBq2Y -__lambda_alertscreator__asgoim29x1hlihbvp0lw8bbq2y2 = new -$$Lambda$AlertsCreator$_asGOIm29x1HlIHBvP0Lw8bBq2Y(j, i2, isGlobalNotificationsEnabled, intCallback2, i, baseFragment, arrayList, intCallback, builder);
                    textView2.setOnClickListener(-__lambda_alertscreator__asgoim29x1hlihbvp0lw8bbq2y);
                }
                i4 = i5 + 1;
                j2 = j;
                linearLayout = view;
                builder2 = builder;
                isGlobalNotificationsEnabled = z;
                iArr = iArr2;
                drawable = drawable2;
                i3 = 1;
            }
            builder = builder2;
            view = linearLayout;
            Builder builder3 = builder;
            builder3.setTitle(LocaleController.getString("Notifications", NUM));
            builder3.setView(view);
            baseFragment2.showDialog(builder3.create());
        }
    }

    static /* synthetic */ void lambda$showCustomNotificationsDialog$5(long j, int i, boolean z, IntCallback intCallback, int i2, BaseFragment baseFragment, ArrayList arrayList, IntCallback intCallback2, Builder builder, View view) {
        long j2 = j;
        IntCallback intCallback3 = intCallback;
        int i3 = i2;
        BaseFragment baseFragment2 = baseFragment;
        IntCallback intCallback4 = intCallback2;
        int intValue = ((Integer) view.getTag()).intValue();
        String str = "notify2_";
        Editor edit;
        TLRPC.Dialog dialog;
        if (intValue == 0) {
            if (j2 != 0) {
                edit = MessagesController.getNotificationsSettings(i).edit();
                StringBuilder stringBuilder;
                if (z) {
                    stringBuilder = new StringBuilder();
                    stringBuilder.append(str);
                    stringBuilder.append(j2);
                    edit.remove(stringBuilder.toString());
                } else {
                    stringBuilder = new StringBuilder();
                    stringBuilder.append(str);
                    stringBuilder.append(j2);
                    edit.putInt(stringBuilder.toString(), 0);
                }
                MessagesStorage.getInstance(i).setDialogFlags(j2, 0);
                edit.commit();
                dialog = (TLRPC.Dialog) MessagesController.getInstance(i).dialogs_dict.get(j2);
                if (dialog != null) {
                    dialog.notify_settings = new TL_peerNotifySettings();
                }
                NotificationsController.getInstance(i).updateServerNotificationsSettings(j2);
                if (intCallback3 != null) {
                    if (z) {
                        intCallback3.run(0);
                    } else {
                        intCallback3.run(1);
                    }
                }
            } else {
                NotificationsController.getInstance(i).setGlobalNotificationsEnabled(i3, 0);
            }
        } else if (intValue != 3) {
            int currentTime = ConnectionsManager.getInstance(i).getCurrentTime();
            if (intValue == 1) {
                currentTime += 3600;
            } else if (intValue == 2) {
                currentTime += 172800;
            } else if (intValue == 4) {
                currentTime = Integer.MAX_VALUE;
            }
            if (j2 != 0) {
                long j3;
                edit = MessagesController.getNotificationsSettings(i).edit();
                StringBuilder stringBuilder2;
                if (intValue != 4) {
                    stringBuilder2 = new StringBuilder();
                    stringBuilder2.append(str);
                    stringBuilder2.append(j2);
                    edit.putInt(stringBuilder2.toString(), 3);
                    StringBuilder stringBuilder3 = new StringBuilder();
                    stringBuilder3.append("notifyuntil_");
                    stringBuilder3.append(j2);
                    edit.putInt(stringBuilder3.toString(), currentTime);
                    j3 = (((long) currentTime) << 32) | 1;
                } else if (z) {
                    stringBuilder2 = new StringBuilder();
                    stringBuilder2.append(str);
                    stringBuilder2.append(j2);
                    edit.putInt(stringBuilder2.toString(), 2);
                    j3 = 1;
                } else {
                    StringBuilder stringBuilder4 = new StringBuilder();
                    stringBuilder4.append(str);
                    stringBuilder4.append(j2);
                    edit.remove(stringBuilder4.toString());
                    j3 = 0;
                }
                NotificationsController.getInstance(i).removeNotificationsForDialog(j2);
                MessagesStorage.getInstance(i).setDialogFlags(j2, j3);
                edit.commit();
                dialog = (TLRPC.Dialog) MessagesController.getInstance(i).dialogs_dict.get(j2);
                if (dialog != null) {
                    dialog.notify_settings = new TL_peerNotifySettings();
                    if (intValue != 4 || z) {
                        dialog.notify_settings.mute_until = currentTime;
                    }
                }
                NotificationsController.getInstance(i).updateServerNotificationsSettings(j2);
                if (intCallback3 != null) {
                    if (intValue != 4 || z) {
                        intCallback3.run(1);
                    } else {
                        intCallback3.run(0);
                    }
                }
            } else if (intValue == 4) {
                NotificationsController.getInstance(i).setGlobalNotificationsEnabled(i3, Integer.MAX_VALUE);
            } else {
                NotificationsController.getInstance(i).setGlobalNotificationsEnabled(i3, currentTime);
            }
        } else if (j2 != 0) {
            Bundle bundle = new Bundle();
            bundle.putLong("dialog_id", j2);
            baseFragment2.presentFragment(new ProfileNotificationsActivity(bundle));
        } else {
            baseFragment2.presentFragment(new NotificationsCustomSettingsActivity(i3, arrayList));
        }
        if (intCallback4 != null) {
            intCallback4.run(intValue);
        }
        builder.getDismissRunnable().run();
    }

    public static AlertDialog showSecretLocationAlert(Context context, int i, Runnable runnable, boolean z) {
        ArrayList arrayList = new ArrayList();
        i = MessagesController.getInstance(i).availableMapProviders;
        if ((i & 1) != 0) {
            arrayList.add(LocaleController.getString("MapPreviewProviderTelegram", NUM));
        }
        if ((i & 2) != 0) {
            arrayList.add(LocaleController.getString("MapPreviewProviderGoogle", NUM));
        }
        if ((i & 4) != 0) {
            arrayList.add(LocaleController.getString("MapPreviewProviderYandex", NUM));
        }
        arrayList.add(LocaleController.getString("MapPreviewProviderNobody", NUM));
        Builder items = new Builder(context).setTitle(LocaleController.getString("ChooseMapPreviewProvider", NUM)).setItems((CharSequence[]) arrayList.toArray(new String[0]), new -$$Lambda$AlertsCreator$xsWG0GwLAfoZ3mWFVK-Gpb3LFZU(runnable));
        if (!z) {
            items.setNegativeButton(LocaleController.getString("Cancel", NUM), null);
        }
        AlertDialog show = items.show();
        if (z) {
            show.setCanceledOnTouchOutside(false);
        }
        return show;
    }

    static /* synthetic */ void lambda$showSecretLocationAlert$6(Runnable runnable, DialogInterface dialogInterface, int i) {
        SharedConfig.setSecretMapPreviewType(i);
        if (runnable != null) {
            runnable.run();
        }
    }

    private static void updateDayPicker(NumberPicker numberPicker, NumberPicker numberPicker2, NumberPicker numberPicker3) {
        Calendar instance = Calendar.getInstance();
        instance.set(2, numberPicker2.getValue());
        instance.set(1, numberPicker3.getValue());
        numberPicker.setMinValue(1);
        numberPicker.setMaxValue(instance.getActualMaximum(5));
    }

    private static void checkPickerDate(NumberPicker numberPicker, NumberPicker numberPicker2, NumberPicker numberPicker3) {
        Calendar instance = Calendar.getInstance();
        instance.setTimeInMillis(System.currentTimeMillis());
        int i = instance.get(1);
        int i2 = instance.get(2);
        int i3 = instance.get(5);
        if (i > numberPicker3.getValue()) {
            numberPicker3.setValue(i);
        }
        if (numberPicker3.getValue() == i) {
            if (i2 > numberPicker2.getValue()) {
                numberPicker2.setValue(i2);
            }
            if (i2 == numberPicker2.getValue() && i3 > numberPicker.getValue()) {
                numberPicker.setValue(i3);
            }
        }
    }

    public static AlertDialog createSupportAlert(final BaseFragment baseFragment) {
        if (baseFragment == null || baseFragment.getParentActivity() == null) {
            return null;
        }
        TextView textView = new TextView(baseFragment.getParentActivity());
        SpannableString spannableString = new SpannableString(Html.fromHtml(LocaleController.getString("AskAQuestionInfo", NUM).replace("\n", "<br>")));
        URLSpan[] uRLSpanArr = (URLSpan[]) spannableString.getSpans(0, spannableString.length(), URLSpan.class);
        for (Object obj : uRLSpanArr) {
            int spanStart = spannableString.getSpanStart(obj);
            int spanEnd = spannableString.getSpanEnd(obj);
            spannableString.removeSpan(obj);
            spannableString.setSpan(new URLSpanNoUnderline(obj.getURL()) {
                public void onClick(View view) {
                    baseFragment.dismissCurrentDialig();
                    super.onClick(view);
                }
            }, spanStart, spanEnd, 0);
        }
        textView.setText(spannableString);
        textView.setTextSize(1, 16.0f);
        textView.setLinkTextColor(Theme.getColor("dialogTextLink"));
        textView.setHighlightColor(Theme.getColor("dialogLinkSelection"));
        textView.setPadding(AndroidUtilities.dp(23.0f), 0, AndroidUtilities.dp(23.0f), 0);
        textView.setMovementMethod(new LinkMovementMethodMy());
        textView.setTextColor(Theme.getColor("dialogTextBlack"));
        Builder builder = new Builder(baseFragment.getParentActivity());
        builder.setView(textView);
        builder.setTitle(LocaleController.getString("AskAQuestion", NUM));
        builder.setPositiveButton(LocaleController.getString("AskButton", NUM), new -$$Lambda$AlertsCreator$EzjiQz8yTI29ns2yqRHiEerUWlM(baseFragment));
        builder.setNegativeButton(LocaleController.getString("Cancel", NUM), null);
        return builder.create();
    }

    private static void performAskAQuestion(BaseFragment baseFragment) {
        int currentAccount = baseFragment.getCurrentAccount();
        SharedPreferences mainSettings = MessagesController.getMainSettings(currentAccount);
        int i = mainSettings.getInt("support_id", 0);
        User user = null;
        if (i != 0) {
            User user2 = MessagesController.getInstance(currentAccount).getUser(Integer.valueOf(i));
            if (user2 == null) {
                String string = mainSettings.getString("support_user", null);
                if (string != null) {
                    try {
                        byte[] decode = Base64.decode(string, 0);
                        if (decode != null) {
                            SerializedData serializedData = new SerializedData(decode);
                            User TLdeserialize = User.TLdeserialize(serializedData, serializedData.readInt32(false), false);
                            if (TLdeserialize != null && TLdeserialize.id == 333000) {
                                TLdeserialize = null;
                            }
                            serializedData.cleanup();
                            user = TLdeserialize;
                        }
                    } catch (Exception e) {
                        FileLog.e(e);
                    }
                }
            }
            user = user2;
        }
        if (user == null) {
            AlertDialog alertDialog = new AlertDialog(baseFragment.getParentActivity(), 3);
            alertDialog.setCanCacnel(false);
            alertDialog.show();
            ConnectionsManager.getInstance(currentAccount).sendRequest(new TL_help_getSupport(), new -$$Lambda$AlertsCreator$GDu6aId31Rd2vioJ0wsptBukYwc(mainSettings, alertDialog, currentAccount, baseFragment));
            return;
        }
        MessagesController.getInstance(currentAccount).putUser(user, true);
        Bundle bundle = new Bundle();
        bundle.putInt("user_id", user.id);
        baseFragment.presentFragment(new ChatActivity(bundle));
    }

    static /* synthetic */ void lambda$performAskAQuestion$10(SharedPreferences sharedPreferences, AlertDialog alertDialog, int i, BaseFragment baseFragment, TLObject tLObject, TL_error tL_error) {
        if (tL_error == null) {
            AndroidUtilities.runOnUIThread(new -$$Lambda$AlertsCreator$T55y6w9Jdq5KQuxXglYYcNrsBVU(sharedPreferences, (TL_help_support) tLObject, alertDialog, i, baseFragment));
            return;
        }
        AndroidUtilities.runOnUIThread(new -$$Lambda$AlertsCreator$ZFb8gzQSJrgqxI-Up-sl0V6VQYo(alertDialog));
    }

    static /* synthetic */ void lambda$null$8(SharedPreferences sharedPreferences, TL_help_support tL_help_support, AlertDialog alertDialog, int i, BaseFragment baseFragment) {
        Editor edit = sharedPreferences.edit();
        edit.putInt("support_id", tL_help_support.user.id);
        SerializedData serializedData = new SerializedData();
        tL_help_support.user.serializeToStream(serializedData);
        edit.putString("support_user", Base64.encodeToString(serializedData.toByteArray(), 0));
        edit.commit();
        serializedData.cleanup();
        try {
            alertDialog.dismiss();
        } catch (Exception e) {
            FileLog.e(e);
        }
        ArrayList arrayList = new ArrayList();
        arrayList.add(tL_help_support.user);
        MessagesStorage.getInstance(i).putUsersAndChats(arrayList, null, true, true);
        MessagesController.getInstance(i).putUser(tL_help_support.user, false);
        Bundle bundle = new Bundle();
        bundle.putInt("user_id", tL_help_support.user.id);
        baseFragment.presentFragment(new ChatActivity(bundle));
    }

    static /* synthetic */ void lambda$null$9(AlertDialog alertDialog) {
        try {
            alertDialog.dismiss();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public static void createClearOrDeleteDialogAlert(BaseFragment baseFragment, boolean z, Chat chat, User user, boolean z2, BooleanCallback booleanCallback) {
        createClearOrDeleteDialogAlert(baseFragment, z, false, false, chat, user, z2, booleanCallback);
    }

    public static void createClearOrDeleteDialogAlert(BaseFragment baseFragment, boolean z, boolean z2, boolean z3, Chat chat, User user, boolean z4, BooleanCallback booleanCallback) {
        Object obj = chat;
        Object obj2 = user;
        if (baseFragment != null && baseFragment.getParentActivity() != null) {
            if (obj != null || obj2 != null) {
                boolean z5;
                Builder builder;
                String string;
                int currentAccount = baseFragment.getCurrentAccount();
                Context parentActivity = baseFragment.getParentActivity();
                Builder builder2 = new Builder(parentActivity);
                int clientUserId = UserConfig.getInstance(currentAccount).getClientUserId();
                final CheckBoxCell[] checkBoxCellArr = new CheckBoxCell[1];
                TextView textView = new TextView(parentActivity);
                textView.setTextColor(Theme.getColor("dialogTextBlack"));
                textView.setTextSize(1, 16.0f);
                textView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
                boolean z6 = ChatObject.isChannel(chat) && !TextUtils.isEmpty(obj.username);
                AnonymousClass3 anonymousClass3 = new FrameLayout(parentActivity) {
                    /* Access modifiers changed, original: protected */
                    public void onMeasure(int i, int i2) {
                        super.onMeasure(i, i2);
                        if (checkBoxCellArr[0] != null) {
                            setMeasuredDimension(getMeasuredWidth(), (getMeasuredHeight() + checkBoxCellArr[0].getMeasuredHeight()) + AndroidUtilities.dp(7.0f));
                        }
                    }
                };
                builder2.setView(anonymousClass3);
                Drawable avatarDrawable = new AvatarDrawable();
                avatarDrawable.setTextSize(AndroidUtilities.dp(12.0f));
                BackupImageView backupImageView = new BackupImageView(parentActivity);
                backupImageView.setRoundRadius(AndroidUtilities.dp(20.0f));
                anonymousClass3.addView(backupImageView, LayoutHelper.createFrame(40, 40.0f, (LocaleController.isRTL ? 5 : 3) | 48, 22.0f, 5.0f, 22.0f, 0.0f));
                TextView textView2 = new TextView(parentActivity);
                textView2.setTextColor(Theme.getColor("actionBarDefaultSubmenuItem"));
                textView2.setTextSize(1, 20.0f);
                textView2.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
                textView2.setLines(1);
                textView2.setMaxLines(1);
                textView2.setSingleLine(true);
                textView2.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
                textView2.setEllipsize(TruncateAt.END);
                String str = "ClearHistoryCache";
                String str2 = "ClearHistory";
                String str3 = "DeleteChatUser";
                if (!z) {
                    z5 = z6;
                    builder = builder2;
                    if (z2) {
                        String str4 = "DeleteMegaMenu";
                        if (!ChatObject.isChannel(chat)) {
                            textView2.setText(LocaleController.getString(str4, NUM));
                        } else if (obj.megagroup) {
                            textView2.setText(LocaleController.getString(str4, NUM));
                        } else {
                            textView2.setText(LocaleController.getString("ChannelDeleteMenu", NUM));
                        }
                    } else {
                        textView2.setText(LocaleController.getString(str3, NUM));
                    }
                } else if (z6) {
                    z5 = z6;
                    builder = builder2;
                    textView2.setText(LocaleController.getString(str, NUM));
                } else {
                    z5 = z6;
                    builder = builder2;
                    textView2.setText(LocaleController.getString(str2, NUM));
                }
                anonymousClass3.addView(textView2, LayoutHelper.createFrame(-1, -2.0f, (LocaleController.isRTL ? 5 : 3) | 48, (float) (LocaleController.isRTL ? 21 : 76), 11.0f, (float) (LocaleController.isRTL ? 76 : 21), 0.0f));
                anonymousClass3.addView(textView, LayoutHelper.createFrame(-2, -2.0f, (LocaleController.isRTL ? 5 : 3) | 48, 24.0f, 57.0f, 24.0f, 9.0f));
                Object obj3 = (obj2 == null || obj2.bot || obj2.id == clientUserId || !MessagesController.getInstance(currentAccount).canRevokePmInbox) ? null : 1;
                if (obj2 != null) {
                    currentAccount = MessagesController.getInstance(currentAccount).revokeTimePmLimit;
                } else {
                    currentAccount = MessagesController.getInstance(currentAccount).revokeTimeLimit;
                }
                Object obj4;
                if (z4 || obj2 == null || obj3 == null || r0 != Integer.MAX_VALUE) {
                    currentAccount = 1;
                    obj4 = null;
                } else {
                    currentAccount = 1;
                    obj4 = 1;
                }
                boolean[] zArr = new boolean[currentAccount];
                if (!(z3 || obj4 == null)) {
                    int i;
                    float f;
                    int dp;
                    checkBoxCellArr[0] = new CheckBoxCell(parentActivity, currentAccount);
                    checkBoxCellArr[0].setBackgroundDrawable(Theme.getSelectorDrawable(false));
                    if (z) {
                        CheckBoxCell checkBoxCell = checkBoxCellArr[0];
                        Object[] objArr = new Object[currentAccount];
                        objArr[0] = UserObject.getFirstName(user);
                        i = 0;
                        checkBoxCell.setText(LocaleController.formatString("ClearHistoryOptionAlso", NUM, objArr), "", false, false);
                    } else {
                        i = 0;
                        checkBoxCellArr[0].setText(LocaleController.formatString("DeleteMessagesOptionAlso", NUM, UserObject.getFirstName(user)), "", false, false);
                    }
                    FrameLayout frameLayout = checkBoxCellArr[i];
                    if (LocaleController.isRTL) {
                        f = 16.0f;
                        dp = AndroidUtilities.dp(16.0f);
                    } else {
                        f = 16.0f;
                        dp = AndroidUtilities.dp(8.0f);
                    }
                    if (LocaleController.isRTL) {
                        f = 8.0f;
                    }
                    frameLayout.setPadding(dp, 0, AndroidUtilities.dp(f), 0);
                    anonymousClass3.addView(checkBoxCellArr[0], LayoutHelper.createFrame(-1, 48.0f, 83, 0.0f, 0.0f, 0.0f, 0.0f));
                    checkBoxCellArr[0].setOnClickListener(new -$$Lambda$AlertsCreator$b9LvzOmxlr-UJrl-uDMYPiB7I6I(zArr));
                }
                if (obj2 != null) {
                    if (obj2.id == clientUserId) {
                        avatarDrawable.setAvatarType(2);
                        backupImageView.setImage(null, null, avatarDrawable, obj2);
                    } else {
                        avatarDrawable.setInfo((User) obj2);
                        backupImageView.setImage(ImageLocation.getForUser(obj2, false), "50_50", avatarDrawable, obj2);
                    }
                } else if (obj != null) {
                    avatarDrawable.setInfo((Chat) obj);
                    backupImageView.setImage(ImageLocation.getForChat(obj, false), "50_50", avatarDrawable, obj);
                }
                if (z3) {
                    textView.setText(AndroidUtilities.replaceTags(LocaleController.getString("DeleteAllMessagesAlert", NUM)));
                } else if (z) {
                    if (obj2 != null) {
                        if (z4) {
                            textView.setText(AndroidUtilities.replaceTags(LocaleController.formatString("AreYouSureClearHistoryWithSecretUser", NUM, UserObject.getUserName(user))));
                        } else if (obj2.id == clientUserId) {
                            textView.setText(AndroidUtilities.replaceTags(LocaleController.getString("AreYouSureClearHistorySavedMessages", NUM)));
                        } else {
                            textView.setText(AndroidUtilities.replaceTags(LocaleController.formatString("AreYouSureClearHistoryWithUser", NUM, UserObject.getUserName(user))));
                        }
                    } else if (obj != null) {
                        if (!ChatObject.isChannel(chat) || (obj.megagroup && TextUtils.isEmpty(obj.username))) {
                            textView.setText(AndroidUtilities.replaceTags(LocaleController.formatString("AreYouSureClearHistoryWithChat", NUM, obj.title)));
                        } else if (obj.megagroup) {
                            textView.setText(LocaleController.getString("AreYouSureClearHistoryGroup", NUM));
                        } else {
                            textView.setText(LocaleController.getString("AreYouSureClearHistoryChannel", NUM));
                        }
                    }
                } else if (z2) {
                    if (!ChatObject.isChannel(chat)) {
                        textView.setText(LocaleController.getString("AreYouSureDeleteAndExit", NUM));
                    } else if (obj.megagroup) {
                        textView.setText(LocaleController.getString("MegaDeleteAlert", NUM));
                    } else {
                        textView.setText(LocaleController.getString("ChannelDeleteAlert", NUM));
                    }
                } else if (obj2 != null) {
                    if (z4) {
                        textView.setText(AndroidUtilities.replaceTags(LocaleController.formatString("AreYouSureDeleteThisChatWithSecretUser", NUM, UserObject.getUserName(user))));
                    } else if (obj2.id == clientUserId) {
                        textView.setText(AndroidUtilities.replaceTags(LocaleController.getString("AreYouSureDeleteThisChatSavedMessages", NUM)));
                    } else {
                        textView.setText(AndroidUtilities.replaceTags(LocaleController.formatString("AreYouSureDeleteThisChatWithUser", NUM, UserObject.getUserName(user))));
                    }
                } else if (!ChatObject.isChannel(chat)) {
                    textView.setText(AndroidUtilities.replaceTags(LocaleController.formatString("AreYouSureDeleteAndExitName", NUM, obj.title)));
                } else if (obj.megagroup) {
                    textView.setText(AndroidUtilities.replaceTags(LocaleController.formatString("MegaLeaveAlertWithName", NUM, obj.title)));
                } else {
                    textView.setText(AndroidUtilities.replaceTags(LocaleController.formatString("ChannelLeaveAlertWithName", NUM, obj.title)));
                }
                if (z3) {
                    string = LocaleController.getString("DeleteAll", NUM);
                } else if (z) {
                    if (z5) {
                        string = LocaleController.getString(str, NUM);
                    } else {
                        string = LocaleController.getString(str2, NUM);
                    }
                } else if (z2) {
                    if (!ChatObject.isChannel(chat)) {
                        string = LocaleController.getString("DeleteMega", NUM);
                    } else if (obj.megagroup) {
                        string = LocaleController.getString("DeleteMega", NUM);
                    } else {
                        string = LocaleController.getString("ChannelDelete", NUM);
                    }
                } else if (!ChatObject.isChannel(chat)) {
                    string = LocaleController.getString(str3, NUM);
                } else if (obj.megagroup) {
                    string = LocaleController.getString("LeaveMegaMenu", NUM);
                } else {
                    string = LocaleController.getString("LeaveChannelMenu", NUM);
                }
                str = string;
                -$$Lambda$AlertsCreator$MnHLfrgmi5mlg6nnWL9u3dTctXY -__lambda_alertscreator_mnhlfrgmi5mlg6nnwl9u3dtctxy = new -$$Lambda$AlertsCreator$MnHLfrgmi5mlg6nnWL9u3dTctXY(user, z5, z3, zArr, baseFragment, z, z2, chat, z4, booleanCallback);
                Builder builder3 = builder;
                builder3.setPositiveButton(str, -__lambda_alertscreator_mnhlfrgmi5mlg6nnwl9u3dtctxy);
                builder3.setNegativeButton(LocaleController.getString("Cancel", NUM), null);
                AlertDialog create = builder3.create();
                baseFragment.showDialog(create);
                TextView textView3 = (TextView) create.getButton(-1);
                if (textView3 != null) {
                    textView3.setTextColor(Theme.getColor("dialogTextRed2"));
                }
            }
        }
    }

    static /* synthetic */ void lambda$createClearOrDeleteDialogAlert$11(boolean[] zArr, View view) {
        CheckBoxCell checkBoxCell = (CheckBoxCell) view;
        zArr[0] = zArr[0] ^ 1;
        checkBoxCell.setChecked(zArr[0], true);
    }

    static /* synthetic */ void lambda$createClearOrDeleteDialogAlert$13(User user, boolean z, boolean z2, boolean[] zArr, BaseFragment baseFragment, boolean z3, boolean z4, Chat chat, boolean z5, BooleanCallback booleanCallback, DialogInterface dialogInterface, int i) {
        User user2 = user;
        BooleanCallback booleanCallback2 = booleanCallback;
        boolean z6 = false;
        if (user2 == null || z || z2 || !zArr[0]) {
            if (booleanCallback2 != null) {
                if (z2 || zArr[0]) {
                    z6 = true;
                }
                booleanCallback2.run(z6);
            }
            return;
        }
        MessagesStorage.getInstance(baseFragment.getCurrentAccount()).getMessagesCount((long) user2.id, new -$$Lambda$AlertsCreator$ynL20oTkOuYkquYnexp3LkHhsQA(baseFragment, z3, z4, chat, user, z5, booleanCallback, zArr));
    }

    static /* synthetic */ void lambda$null$12(BaseFragment baseFragment, boolean z, boolean z2, Chat chat, User user, boolean z3, BooleanCallback booleanCallback, boolean[] zArr, int i) {
        BooleanCallback booleanCallback2 = booleanCallback;
        if (i >= 50) {
            createClearOrDeleteDialogAlert(baseFragment, z, z2, true, chat, user, z3, booleanCallback);
        } else if (booleanCallback2 != null) {
            booleanCallback.run(zArr[0]);
        }
    }

    public static Builder createDatePickerDialog(Context context, int i, int i2, int i3, int i4, int i5, int i6, String str, boolean z, DatePickerDelegate datePickerDelegate) {
        int i7 = i4;
        boolean z2 = z;
        if (context == null) {
            return null;
        }
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(0);
        linearLayout.setWeightSum(1.0f);
        NumberPicker numberPicker = new NumberPicker(context);
        NumberPicker numberPicker2 = new NumberPicker(context);
        NumberPicker numberPicker3 = new NumberPicker(context);
        linearLayout.addView(numberPicker2, LayoutHelper.createLinear(0, -2, 0.3f));
        numberPicker2.setOnScrollListener(new -$$Lambda$AlertsCreator$T_Bov-pnF6uHYp80S0XjKagohb8(z2, numberPicker2, numberPicker, numberPicker3));
        numberPicker.setMinValue(0);
        numberPicker.setMaxValue(11);
        linearLayout.addView(numberPicker, LayoutHelper.createLinear(0, -2, 0.3f));
        numberPicker.setFormatter(-$$Lambda$AlertsCreator$VJlLh0GlRlmw33MoCufOTwK8nNQ.INSTANCE);
        numberPicker.setOnValueChangedListener(new -$$Lambda$AlertsCreator$mo66TgHnBRhv_TioolPkrpTopo0(numberPicker2, numberPicker, numberPicker3));
        numberPicker.setOnScrollListener(new -$$Lambda$AlertsCreator$X9eAz-vGDgt2Lf0L8benu7NuJlM(z2, numberPicker2, numberPicker, numberPicker3));
        Calendar instance = Calendar.getInstance();
        instance.setTimeInMillis(System.currentTimeMillis());
        int i8 = instance.get(1);
        numberPicker3.setMinValue(i8 + i);
        numberPicker3.setMaxValue(i8 + i2);
        numberPicker3.setValue(i8 + i3);
        linearLayout.addView(numberPicker3, LayoutHelper.createLinear(0, -2, 0.4f));
        numberPicker3.setOnValueChangedListener(new -$$Lambda$AlertsCreator$bKffcPksBrvNdwh2OS4PPtN2x7Q(numberPicker2, numberPicker, numberPicker3));
        numberPicker3.setOnScrollListener(new -$$Lambda$AlertsCreator$zkQYqiQoYsM32whUQLrZMANC5KI(z2, numberPicker2, numberPicker, numberPicker3));
        updateDayPicker(numberPicker2, numberPicker, numberPicker3);
        if (z2) {
            checkPickerDate(numberPicker2, numberPicker, numberPicker3);
        }
        if (i7 != -1) {
            numberPicker2.setValue(i7);
            numberPicker.setValue(i5);
            numberPicker3.setValue(i6);
        }
        Builder builder = new Builder(context);
        builder.setTitle(str);
        builder.setView(linearLayout);
        builder.setPositiveButton(LocaleController.getString("Set", NUM), new -$$Lambda$AlertsCreator$X4hsYk6lRuv0sZ1pb1kdqPkzAAs(z, numberPicker2, numberPicker, numberPicker3, datePickerDelegate));
        builder.setNegativeButton(LocaleController.getString("Cancel", NUM), null);
        return builder;
    }

    static /* synthetic */ void lambda$createDatePickerDialog$14(boolean z, NumberPicker numberPicker, NumberPicker numberPicker2, NumberPicker numberPicker3, NumberPicker numberPicker4, int i) {
        if (z && i == 0) {
            checkPickerDate(numberPicker, numberPicker2, numberPicker3);
        }
    }

    static /* synthetic */ String lambda$createDatePickerDialog$15(int i) {
        Calendar instance = Calendar.getInstance();
        instance.set(5, 1);
        instance.set(2, i);
        return instance.getDisplayName(2, 1, Locale.getDefault());
    }

    static /* synthetic */ void lambda$createDatePickerDialog$17(boolean z, NumberPicker numberPicker, NumberPicker numberPicker2, NumberPicker numberPicker3, NumberPicker numberPicker4, int i) {
        if (z && i == 0) {
            checkPickerDate(numberPicker, numberPicker2, numberPicker3);
        }
    }

    static /* synthetic */ void lambda$createDatePickerDialog$19(boolean z, NumberPicker numberPicker, NumberPicker numberPicker2, NumberPicker numberPicker3, NumberPicker numberPicker4, int i) {
        if (z && i == 0) {
            checkPickerDate(numberPicker, numberPicker2, numberPicker3);
        }
    }

    static /* synthetic */ void lambda$createDatePickerDialog$20(boolean z, NumberPicker numberPicker, NumberPicker numberPicker2, NumberPicker numberPicker3, DatePickerDelegate datePickerDelegate, DialogInterface dialogInterface, int i) {
        if (z) {
            checkPickerDate(numberPicker, numberPicker2, numberPicker3);
        }
        datePickerDelegate.didSelectDate(numberPicker3.getValue(), numberPicker2.getValue(), numberPicker.getValue());
    }

    private static boolean checkScheduleDate(TextView textView, boolean z, NumberPicker numberPicker, NumberPicker numberPicker2, NumberPicker numberPicker3) {
        int i;
        TextView textView2 = textView;
        int value = numberPicker.getValue();
        int value2 = numberPicker2.getValue();
        int value3 = numberPicker3.getValue();
        Calendar instance = Calendar.getInstance();
        long currentTimeMillis = System.currentTimeMillis();
        instance.setTimeInMillis(currentTimeMillis);
        int i2 = instance.get(1);
        int i3 = instance.get(6);
        instance.setTimeInMillis(System.currentTimeMillis() + (((((long) value) * 24) * 3600) * 1000));
        instance.set(11, value2);
        instance.set(12, value3);
        long timeInMillis = instance.getTimeInMillis();
        long j = currentTimeMillis + 60000;
        if (timeInMillis <= j) {
            instance.setTimeInMillis(j);
            if (i3 != instance.get(6)) {
                numberPicker.setValue(1);
                value = 1;
            }
            value3 = instance.get(11);
            numberPicker2.setValue(value3);
            i = instance.get(12);
            numberPicker3.setValue(i);
            value2 = value3;
            value3 = i;
        }
        i = instance.get(1);
        int i4 = i2;
        instance.setTimeInMillis(System.currentTimeMillis() + (((((long) value) * 24) * 3600) * 1000));
        instance.set(11, value2);
        instance.set(12, value3);
        if (textView2 != null) {
            long timeInMillis2 = instance.getTimeInMillis();
            value = value == 0 ? 0 : i4 == i ? 1 : 2;
            if (z) {
                value += 3;
            }
            textView2.setText(LocaleController.getInstance().formatterScheduleSend[value].format(timeInMillis2));
        }
        return timeInMillis - currentTimeMillis > 60000;
    }

    public static BottomSheet.Builder createScheduleDatePickerDialog(Context context, boolean z, ScheduleDatePickerDelegate scheduleDatePickerDelegate) {
        return createScheduleDatePickerDialog(context, z, -1, scheduleDatePickerDelegate, null);
    }

    public static BottomSheet.Builder createScheduleDatePickerDialog(Context context, boolean z, ScheduleDatePickerDelegate scheduleDatePickerDelegate, Runnable runnable) {
        return createScheduleDatePickerDialog(context, z, -1, scheduleDatePickerDelegate, runnable);
    }

    public static BottomSheet.Builder createScheduleDatePickerDialog(Context context, boolean z, long j, ScheduleDatePickerDelegate scheduleDatePickerDelegate, Runnable runnable) {
        Context context2 = context;
        boolean z2 = z;
        if (context2 == null) {
            return null;
        }
        int i;
        String str;
        Calendar calendar;
        BottomSheet.Builder builder = new BottomSheet.Builder(context2, false, 1);
        builder.setApplyBottomPadding(false);
        final NumberPicker numberPicker = new NumberPicker(context2);
        numberPicker.setTextOffset(AndroidUtilities.dp(10.0f));
        numberPicker.setItemCount(5);
        final NumberPicker numberPicker2 = new NumberPicker(context2);
        numberPicker2.setItemCount(5);
        numberPicker2.setTextOffset(-AndroidUtilities.dp(10.0f));
        final NumberPicker numberPicker3 = new NumberPicker(context2);
        numberPicker3.setItemCount(5);
        numberPicker3.setTextOffset(-AndroidUtilities.dp(34.0f));
        AnonymousClass4 anonymousClass4 = new LinearLayout(context2) {
            boolean ignoreLayout = false;

            /* Access modifiers changed, original: protected */
            public void onMeasure(int i, int i2) {
                this.ignoreLayout = true;
                Point point = AndroidUtilities.displaySize;
                int i3 = point.x > point.y ? 3 : 5;
                numberPicker.setItemCount(i3);
                numberPicker2.setItemCount(i3);
                numberPicker3.setItemCount(i3);
                numberPicker.getLayoutParams().height = AndroidUtilities.dp(54.0f) * i3;
                numberPicker2.getLayoutParams().height = AndroidUtilities.dp(54.0f) * i3;
                numberPicker3.getLayoutParams().height = AndroidUtilities.dp(54.0f) * i3;
                this.ignoreLayout = false;
                super.onMeasure(i, i2);
            }

            public void requestLayout() {
                if (!this.ignoreLayout) {
                    super.requestLayout();
                }
            }
        };
        anonymousClass4.setOrientation(1);
        TextView textView = new TextView(context2);
        if (z2) {
            i = NUM;
            str = "SetReminder";
        } else {
            i = NUM;
            str = "ScheduleMessage";
        }
        textView.setText(LocaleController.getString(str, i));
        textView.setTextColor(Theme.getColor("dialogTextBlack"));
        textView.setTextSize(1, 20.0f);
        String str2 = "fonts/rmedium.ttf";
        textView.setTypeface(AndroidUtilities.getTypeface(str2));
        anonymousClass4.addView(textView, LayoutHelper.createLinear(-1, -2, 51, 22, 12, 22, 4));
        textView.setOnTouchListener(-$$Lambda$AlertsCreator$7vxVL9TgAW5Vn6ZIlSM9Zg3Jmqc.INSTANCE);
        LinearLayout linearLayout = new LinearLayout(context2);
        linearLayout.setOrientation(0);
        linearLayout.setWeightSum(1.0f);
        anonymousClass4.addView(linearLayout, LayoutHelper.createLinear(-1, -2));
        long currentTimeMillis = System.currentTimeMillis();
        Calendar instance = Calendar.getInstance();
        instance.setTimeInMillis(currentTimeMillis);
        int i2 = instance.get(1);
        TextView textView2 = new TextView(context2);
        linearLayout.addView(numberPicker, LayoutHelper.createLinear(0, 270, 0.5f));
        numberPicker.setMinValue(0);
        numberPicker.setMaxValue(365);
        numberPicker.setWrapSelectorWheel(false);
        numberPicker.setFormatter(new -$$Lambda$AlertsCreator$S8IVp4ro537QRHJsGZF0Tvy23PA(currentTimeMillis, instance, i2));
        -$$Lambda$AlertsCreator$AoS0yEr1wQv0zK5zFQt2NMzqXOQ -__lambda_alertscreator_aos0yer1wqv0zk5zfqt2nmzqxoq = r0;
        Calendar calendar2 = instance;
        LinearLayout linearLayout2 = linearLayout;
        -$$Lambda$AlertsCreator$AoS0yEr1wQv0zK5zFQt2NMzqXOQ -__lambda_alertscreator_aos0yer1wqv0zk5zfqt2nmzqxoq2 = new -$$Lambda$AlertsCreator$AoS0yEr1wQv0zK5zFQt2NMzqXOQ(textView2, z, numberPicker, numberPicker2, numberPicker3);
        numberPicker.setOnValueChangedListener(-__lambda_alertscreator_aos0yer1wqv0zk5zfqt2nmzqxoq);
        numberPicker2.setMinValue(0);
        numberPicker2.setMaxValue(23);
        LinearLayout linearLayout3 = linearLayout2;
        linearLayout3.addView(numberPicker2, LayoutHelper.createLinear(0, 270, 0.2f));
        numberPicker2.setFormatter(-$$Lambda$AlertsCreator$cTWaWPpARtn2mZSMxvTqevgwElI.INSTANCE);
        numberPicker2.setOnValueChangedListener(-__lambda_alertscreator_aos0yer1wqv0zk5zfqt2nmzqxoq);
        numberPicker3.setMinValue(0);
        numberPicker3.setMaxValue(59);
        numberPicker3.setValue(0);
        numberPicker3.setFormatter(-$$Lambda$AlertsCreator$VSdCDpRaOSSEWlFtIMHcNwKBt7E.INSTANCE);
        linearLayout3.addView(numberPicker3, LayoutHelper.createLinear(0, 270, 0.3f));
        numberPicker3.setOnValueChangedListener(-__lambda_alertscreator_aos0yer1wqv0zk5zfqt2nmzqxoq);
        if (j > 0) {
            long j2 = 1000 * j;
            calendar = calendar2;
            calendar.setTimeInMillis(System.currentTimeMillis());
            calendar.set(12, 0);
            calendar.set(11, 0);
            int timeInMillis = (int) ((j2 - calendar.getTimeInMillis()) / 86400000);
            calendar.setTimeInMillis(j2);
            if (timeInMillis >= 0) {
                numberPicker3.setValue(calendar.get(12));
                numberPicker2.setValue(calendar.get(11));
                numberPicker.setValue(timeInMillis);
            }
        } else {
            calendar = calendar2;
        }
        boolean[] zArr = new boolean[]{true};
        checkScheduleDate(textView2, z2, numberPicker, numberPicker2, numberPicker3);
        textView2.setPadding(AndroidUtilities.dp(34.0f), 0, AndroidUtilities.dp(34.0f), 0);
        textView2.setGravity(17);
        textView2.setTextColor(Theme.getColor("featuredStickers_buttonText"));
        textView2.setTextSize(1, 14.0f);
        textView2.setTypeface(AndroidUtilities.getTypeface(str2));
        textView2.setBackgroundDrawable(Theme.createSimpleSelectorRoundRectDrawable(AndroidUtilities.dp(4.0f), Theme.getColor("featuredStickers_addButton"), Theme.getColor("featuredStickers_addButtonPressed")));
        anonymousClass4.addView(textView2, LayoutHelper.createLinear(-1, 48, 83, 16, 15, 16, 16));
        boolean[] zArr2 = zArr;
        textView2.setOnClickListener(new -$$Lambda$AlertsCreator$5-lZYxMK4SyRqWBsGAs7CrRik3Q(zArr, z, numberPicker, numberPicker2, numberPicker3, calendar, scheduleDatePickerDelegate, builder));
        builder.setCustomView(anonymousClass4);
        builder.show().setOnDismissListener(new -$$Lambda$AlertsCreator$XV0y1q8-l5UYh_GaKmTABvo6FCI(runnable, zArr2));
        return builder;
    }

    static /* synthetic */ String lambda$createScheduleDatePickerDialog$22(long j, Calendar calendar, int i, int i2) {
        if (i2 == 0) {
            return LocaleController.getString("MessageScheduleToday", NUM);
        }
        j += ((long) i2) * 86400000;
        calendar.setTimeInMillis(j);
        if (calendar.get(1) == i) {
            return LocaleController.getInstance().formatterScheduleDay.format(j);
        }
        return LocaleController.getInstance().formatterScheduleYear.format(j);
    }

    static /* synthetic */ void lambda$createScheduleDatePickerDialog$26(boolean[] zArr, boolean z, NumberPicker numberPicker, NumberPicker numberPicker2, NumberPicker numberPicker3, Calendar calendar, ScheduleDatePickerDelegate scheduleDatePickerDelegate, BottomSheet.Builder builder, View view) {
        zArr[0] = false;
        boolean checkScheduleDate = checkScheduleDate(null, z, numberPicker, numberPicker2, numberPicker3);
        calendar.setTimeInMillis(System.currentTimeMillis() + (((((long) numberPicker.getValue()) * 24) * 3600) * 1000));
        calendar.set(11, numberPicker2.getValue());
        calendar.set(12, numberPicker3.getValue());
        if (checkScheduleDate) {
            calendar.set(13, 0);
        }
        scheduleDatePickerDelegate.didSelectDate(true, (int) (calendar.getTimeInMillis() / 1000));
        builder.getDismissRunnable().run();
    }

    static /* synthetic */ void lambda$createScheduleDatePickerDialog$27(Runnable runnable, boolean[] zArr, DialogInterface dialogInterface) {
        if (runnable != null && zArr[0]) {
            runnable.run();
        }
    }

    public static Dialog createMuteAlert(Context context, long j) {
        if (context == null) {
            return null;
        }
        BottomSheet.Builder builder = new BottomSheet.Builder(context);
        builder.setTitle(LocaleController.getString("Notifications", NUM));
        CharSequence[] charSequenceArr = new CharSequence[4];
        Object[] objArr = new Object[1];
        objArr[0] = LocaleController.formatPluralString("Hours", 1);
        String str = "MuteFor";
        charSequenceArr[0] = LocaleController.formatString(str, NUM, objArr);
        charSequenceArr[1] = LocaleController.formatString(str, NUM, LocaleController.formatPluralString("Hours", 8));
        charSequenceArr[2] = LocaleController.formatString(str, NUM, LocaleController.formatPluralString("Days", 2));
        charSequenceArr[3] = LocaleController.getString("MuteDisable", NUM);
        builder.setItems(charSequenceArr, new -$$Lambda$AlertsCreator$t2Ybx4P5povsI8rLJfyId65Wgtc(j));
        return builder.create();
    }

    static /* synthetic */ void lambda$createMuteAlert$28(long j, DialogInterface dialogInterface, int i) {
        int i2 = 2;
        if (i == 0) {
            i2 = 0;
        } else if (i == 1) {
            i2 = 1;
        } else if (i != 2) {
            i2 = 3;
        }
        NotificationsController.getInstance(UserConfig.selectedAccount).setDialogNotificationsSettings(j, i2);
    }

    public static void createReportAlert(Context context, long j, int i, BaseFragment baseFragment) {
        if (context != null && baseFragment != null) {
            BottomSheet.Builder builder = new BottomSheet.Builder(context);
            builder.setTitle(LocaleController.getString("ReportChat", NUM));
            builder.setItems(new CharSequence[]{LocaleController.getString("ReportChatSpam", NUM), LocaleController.getString("ReportChatViolence", NUM), LocaleController.getString("ReportChatChild", NUM), LocaleController.getString("ReportChatPornography", NUM), LocaleController.getString("ReportChatOther", NUM)}, new -$$Lambda$AlertsCreator$T53vdwu9Baw7mp6rou_Dgn0z1tE(j, i, baseFragment, context));
            baseFragment.showDialog(builder.create());
        }
    }

    static /* synthetic */ void lambda$createReportAlert$30(long j, int i, BaseFragment baseFragment, Context context, DialogInterface dialogInterface, int i2) {
        if (i2 == 4) {
            Bundle bundle = new Bundle();
            bundle.putLong("dialog_id", j);
            bundle.putLong("message_id", (long) i);
            baseFragment.presentFragment(new ReportOtherActivity(bundle));
            return;
        }
        TLObject tL_messages_report;
        InputPeer inputPeer = MessagesController.getInstance(UserConfig.selectedAccount).getInputPeer((int) j);
        if (i != 0) {
            tL_messages_report = new TL_messages_report();
            tL_messages_report.peer = inputPeer;
            tL_messages_report.id.add(Integer.valueOf(i));
            if (i2 == 0) {
                tL_messages_report.reason = new TL_inputReportReasonSpam();
            } else if (i2 == 1) {
                tL_messages_report.reason = new TL_inputReportReasonViolence();
            } else if (i2 == 2) {
                tL_messages_report.reason = new TL_inputReportReasonChildAbuse();
            } else if (i2 == 3) {
                tL_messages_report.reason = new TL_inputReportReasonPornography();
            }
        } else {
            tL_messages_report = new TL_account_reportPeer();
            tL_messages_report.peer = inputPeer;
            if (i2 == 0) {
                tL_messages_report.reason = new TL_inputReportReasonSpam();
            } else if (i2 == 1) {
                tL_messages_report.reason = new TL_inputReportReasonViolence();
            } else if (i2 == 2) {
                tL_messages_report.reason = new TL_inputReportReasonChildAbuse();
            } else if (i2 == 3) {
                tL_messages_report.reason = new TL_inputReportReasonPornography();
            }
        }
        ConnectionsManager.getInstance(UserConfig.selectedAccount).sendRequest(tL_messages_report, -$$Lambda$AlertsCreator$ipvWLYO2H-0n1c-wDoGY-DRLDu0.INSTANCE);
        Toast.makeText(context, LocaleController.getString("ReportChatSent", NUM), 0).show();
    }

    private static String getFloodWaitString(String str) {
        int intValue = Utilities.parseInt(str).intValue();
        if (intValue < 60) {
            str = LocaleController.formatPluralString("Seconds", intValue);
        } else {
            str = LocaleController.formatPluralString("Minutes", intValue / 60);
        }
        return LocaleController.formatString("FloodWaitTime", NUM, str);
    }

    public static void showFloodWaitAlert(String str, BaseFragment baseFragment) {
        if (str != null && str.startsWith("FLOOD_WAIT") && baseFragment != null && baseFragment.getParentActivity() != null) {
            int intValue = Utilities.parseInt(str).intValue();
            if (intValue < 60) {
                str = LocaleController.formatPluralString("Seconds", intValue);
            } else {
                str = LocaleController.formatPluralString("Minutes", intValue / 60);
            }
            Builder builder = new Builder(baseFragment.getParentActivity());
            builder.setTitle(LocaleController.getString("AppName", NUM));
            builder.setMessage(LocaleController.formatString("FloodWaitTime", NUM, str));
            builder.setPositiveButton(LocaleController.getString("OK", NUM), null);
            baseFragment.showDialog(builder.create(), true, null);
        }
    }

    public static void showSendMediaAlert(int i, BaseFragment baseFragment) {
        if (i != 0) {
            Builder builder = new Builder(baseFragment.getParentActivity());
            builder.setTitle(LocaleController.getString("AppName", NUM));
            if (i == 1) {
                builder.setMessage(LocaleController.getString("ErrorSendRestrictedStickers", NUM));
            } else if (i == 2) {
                builder.setMessage(LocaleController.getString("ErrorSendRestrictedMedia", NUM));
            } else if (i == 3) {
                builder.setMessage(LocaleController.getString("ErrorSendRestrictedPolls", NUM));
            } else if (i == 4) {
                builder.setMessage(LocaleController.getString("ErrorSendRestrictedStickersAll", NUM));
            } else if (i == 5) {
                builder.setMessage(LocaleController.getString("ErrorSendRestrictedMediaAll", NUM));
            } else if (i == 6) {
                builder.setMessage(LocaleController.getString("ErrorSendRestrictedPollsAll", NUM));
            }
            builder.setPositiveButton(LocaleController.getString("OK", NUM), null);
            baseFragment.showDialog(builder.create(), true, null);
        }
    }

    public static void showAddUserAlert(String str, BaseFragment baseFragment, boolean z, TLObject tLObject) {
        if (str != null && baseFragment != null && baseFragment.getParentActivity() != null) {
            Builder builder = new Builder(baseFragment.getParentActivity());
            builder.setTitle(LocaleController.getString("AppName", NUM));
            Object obj = -1;
            switch (str.hashCode()) {
                case -2120721660:
                    if (str.equals("CHANNELS_ADMIN_LOCATED_TOO_MUCH")) {
                        obj = 17;
                        break;
                    }
                    break;
                case -2012133105:
                    if (str.equals("CHANNELS_ADMIN_PUBLIC_TOO_MUCH")) {
                        obj = 16;
                        break;
                    }
                    break;
                case -1763467626:
                    if (str.equals("USERS_TOO_FEW")) {
                        obj = 9;
                        break;
                    }
                    break;
                case -538116776:
                    if (str.equals("USER_BLOCKED")) {
                        obj = 1;
                        break;
                    }
                    break;
                case -512775857:
                    if (str.equals("USER_RESTRICTED")) {
                        obj = 10;
                        break;
                    }
                    break;
                case -454039871:
                    if (str.equals("PEER_FLOOD")) {
                        obj = null;
                        break;
                    }
                    break;
                case -420079733:
                    if (str.equals("BOTS_TOO_MUCH")) {
                        obj = 7;
                        break;
                    }
                    break;
                case 98635865:
                    if (str.equals("USER_KICKED")) {
                        obj = 13;
                        break;
                    }
                    break;
                case 517420851:
                    if (str.equals("USER_BOT")) {
                        obj = 2;
                        break;
                    }
                    break;
                case 845559454:
                    if (str.equals("YOU_BLOCKED_USER")) {
                        obj = 11;
                        break;
                    }
                    break;
                case 916342611:
                    if (str.equals("USER_ADMIN_INVALID")) {
                        obj = 15;
                        break;
                    }
                    break;
                case 1047173446:
                    if (str.equals("CHAT_ADMIN_BAN_REQUIRED")) {
                        obj = 12;
                        break;
                    }
                    break;
                case 1167301807:
                    if (str.equals("USERS_TOO_MUCH")) {
                        obj = 4;
                        break;
                    }
                    break;
                case 1227003815:
                    if (str.equals("USER_ID_INVALID")) {
                        obj = 3;
                        break;
                    }
                    break;
                case 1253103379:
                    if (str.equals("ADMINS_TOO_MUCH")) {
                        obj = 6;
                        break;
                    }
                    break;
                case 1355367367:
                    if (str.equals("CHANNELS_TOO_MUCH")) {
                        obj = 18;
                        break;
                    }
                    break;
                case 1623167701:
                    if (str.equals("USER_NOT_MUTUAL_CONTACT")) {
                        obj = 5;
                        break;
                    }
                    break;
                case 1754587486:
                    if (str.equals("CHAT_ADMIN_INVITE_REQUIRED")) {
                        obj = 14;
                        break;
                    }
                    break;
                case 1916725894:
                    if (str.equals("USER_PRIVACY_RESTRICTED")) {
                        obj = 8;
                        break;
                    }
                    break;
            }
            switch (obj) {
                case null:
                    builder.setMessage(LocaleController.getString("NobodyLikesSpam2", NUM));
                    builder.setNegativeButton(LocaleController.getString("MoreInfo", NUM), new -$$Lambda$AlertsCreator$FG737OZ0DoNKPJjQXh_OkRfCA0o(baseFragment));
                    break;
                case 1:
                case 2:
                case 3:
                    if (!z) {
                        builder.setMessage(LocaleController.getString("GroupUserCantAdd", NUM));
                        break;
                    } else {
                        builder.setMessage(LocaleController.getString("ChannelUserCantAdd", NUM));
                        break;
                    }
                case 4:
                    if (!z) {
                        builder.setMessage(LocaleController.getString("GroupUserAddLimit", NUM));
                        break;
                    } else {
                        builder.setMessage(LocaleController.getString("ChannelUserAddLimit", NUM));
                        break;
                    }
                case 5:
                    if (!z) {
                        builder.setMessage(LocaleController.getString("GroupUserLeftError", NUM));
                        break;
                    } else {
                        builder.setMessage(LocaleController.getString("ChannelUserLeftError", NUM));
                        break;
                    }
                case 6:
                    if (!z) {
                        builder.setMessage(LocaleController.getString("GroupUserCantAdmin", NUM));
                        break;
                    } else {
                        builder.setMessage(LocaleController.getString("ChannelUserCantAdmin", NUM));
                        break;
                    }
                case 7:
                    if (!z) {
                        builder.setMessage(LocaleController.getString("GroupUserCantBot", NUM));
                        break;
                    } else {
                        builder.setMessage(LocaleController.getString("ChannelUserCantBot", NUM));
                        break;
                    }
                case 8:
                    if (!z) {
                        builder.setMessage(LocaleController.getString("InviteToGroupError", NUM));
                        break;
                    } else {
                        builder.setMessage(LocaleController.getString("InviteToChannelError", NUM));
                        break;
                    }
                case 9:
                    builder.setMessage(LocaleController.getString("CreateGroupError", NUM));
                    break;
                case 10:
                    builder.setMessage(LocaleController.getString("UserRestricted", NUM));
                    break;
                case 11:
                    builder.setMessage(LocaleController.getString("YouBlockedUser", NUM));
                    break;
                case 12:
                case 13:
                    builder.setMessage(LocaleController.getString("AddAdminErrorBlacklisted", NUM));
                    break;
                case 14:
                    builder.setMessage(LocaleController.getString("AddAdminErrorNotAMember", NUM));
                    break;
                case 15:
                    builder.setMessage(LocaleController.getString("AddBannedErrorAdmin", NUM));
                    break;
                case 16:
                    builder.setMessage(LocaleController.getString("PublicChannelsTooMuch", NUM));
                    break;
                case 17:
                    builder.setMessage(LocaleController.getString("LocatedChannelsTooMuch", NUM));
                    break;
                case 18:
                    if (!(tLObject instanceof TL_channels_createChannel)) {
                        builder.setMessage(LocaleController.getString("ChannelTooMuchJoin", NUM));
                        break;
                    } else {
                        builder.setMessage(LocaleController.getString("ChannelTooMuch", NUM));
                        break;
                    }
                default:
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append(LocaleController.getString("ErrorOccurred", NUM));
                    stringBuilder.append("\n");
                    stringBuilder.append(str);
                    builder.setMessage(stringBuilder.toString());
                    break;
            }
            builder.setPositiveButton(LocaleController.getString("OK", NUM), null);
            baseFragment.showDialog(builder.create(), true, null);
        }
    }

    public static Dialog createColorSelectDialog(Activity activity, long j, int i, Runnable runnable) {
        int i2;
        Context context = activity;
        long j2 = j;
        int i3 = i;
        Runnable runnable2 = runnable;
        SharedPreferences notificationsSettings = MessagesController.getNotificationsSettings(UserConfig.selectedAccount);
        String str = "GroupLed";
        String str2 = "MessagesLed";
        if (j2 != 0) {
            StringBuilder stringBuilder = new StringBuilder();
            String str3 = "color_";
            stringBuilder.append(str3);
            stringBuilder.append(j2);
            if (notificationsSettings.contains(stringBuilder.toString())) {
                StringBuilder stringBuilder2 = new StringBuilder();
                stringBuilder2.append(str3);
                stringBuilder2.append(j2);
                i2 = notificationsSettings.getInt(stringBuilder2.toString(), -16776961);
            } else if (((int) j2) < 0) {
                i2 = notificationsSettings.getInt(str, -16776961);
            } else {
                i2 = notificationsSettings.getInt(str2, -16776961);
            }
        } else if (i3 == 1) {
            i2 = notificationsSettings.getInt(str2, -16776961);
        } else if (i3 == 0) {
            i2 = notificationsSettings.getInt(str, -16776961);
        } else {
            i2 = notificationsSettings.getInt("ChannelLed", -16776961);
        }
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(1);
        int i4 = 9;
        String[] strArr = new String[]{LocaleController.getString("ColorRed", NUM), LocaleController.getString("ColorOrange", NUM), LocaleController.getString("ColorYellow", NUM), LocaleController.getString("ColorGreen", NUM), LocaleController.getString("ColorCyan", NUM), LocaleController.getString("ColorBlue", NUM), LocaleController.getString("ColorViolet", NUM), LocaleController.getString("ColorPink", NUM), LocaleController.getString("ColorWhite", NUM)};
        int[] iArr = new int[]{i2};
        int i5 = 0;
        while (i5 < i4) {
            RadioColorCell radioColorCell = new RadioColorCell(context);
            radioColorCell.setPadding(AndroidUtilities.dp(4.0f), 0, AndroidUtilities.dp(4.0f), 0);
            radioColorCell.setTag(Integer.valueOf(i5));
            int[] iArr2 = TextColorCell.colors;
            radioColorCell.setCheckColor(iArr2[i5], iArr2[i5]);
            radioColorCell.setTextAndValue(strArr[i5], i2 == TextColorCell.colorsToSave[i5]);
            linearLayout.addView(radioColorCell);
            radioColorCell.setOnClickListener(new -$$Lambda$AlertsCreator$ScqzpZH4v14YocninsFGZW7M2Jg(linearLayout, iArr));
            i5++;
            i4 = 9;
        }
        Builder builder = new Builder(context);
        builder.setTitle(LocaleController.getString("LedColor", NUM));
        builder.setView(linearLayout);
        builder.setPositiveButton(LocaleController.getString("Set", NUM), new -$$Lambda$AlertsCreator$FvZi-Oc6SB-TNIVCJ_gANG1xN6Q(j, iArr, i, runnable));
        builder.setNeutralButton(LocaleController.getString("LedDisabled", NUM), new -$$Lambda$AlertsCreator$MiBCObfRNfGTxHncINZd0ySicJI(j2, i3, runnable2));
        if (j2 != 0) {
            builder.setNegativeButton(LocaleController.getString("Default", NUM), new -$$Lambda$AlertsCreator$YJRTLtRAU8CFlit5MrQHwkrqy0Q(j2, runnable2));
        }
        return builder.create();
    }

    static /* synthetic */ void lambda$createColorSelectDialog$32(LinearLayout linearLayout, int[] iArr, View view) {
        int childCount = linearLayout.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View view2 = (RadioColorCell) linearLayout.getChildAt(i);
            view2.setChecked(view2 == view, true);
        }
        iArr[0] = TextColorCell.colorsToSave[((Integer) view.getTag()).intValue()];
    }

    static /* synthetic */ void lambda$createColorSelectDialog$33(long j, int[] iArr, int i, Runnable runnable, DialogInterface dialogInterface, int i2) {
        Editor edit = MessagesController.getNotificationsSettings(UserConfig.selectedAccount).edit();
        if (j != 0) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("color_");
            stringBuilder.append(j);
            edit.putInt(stringBuilder.toString(), iArr[0]);
        } else if (i == 1) {
            edit.putInt("MessagesLed", iArr[0]);
        } else if (i == 0) {
            edit.putInt("GroupLed", iArr[0]);
        } else {
            edit.putInt("ChannelLed", iArr[0]);
        }
        edit.commit();
        if (runnable != null) {
            runnable.run();
        }
    }

    static /* synthetic */ void lambda$createColorSelectDialog$34(long j, int i, Runnable runnable, DialogInterface dialogInterface, int i2) {
        Editor edit = MessagesController.getNotificationsSettings(UserConfig.selectedAccount).edit();
        if (j != 0) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("color_");
            stringBuilder.append(j);
            edit.putInt(stringBuilder.toString(), 0);
        } else if (i == 1) {
            edit.putInt("MessagesLed", 0);
        } else if (i == 0) {
            edit.putInt("GroupLed", 0);
        } else {
            edit.putInt("ChannelLed", 0);
        }
        edit.commit();
        if (runnable != null) {
            runnable.run();
        }
    }

    static /* synthetic */ void lambda$createColorSelectDialog$35(long j, Runnable runnable, DialogInterface dialogInterface, int i) {
        Editor edit = MessagesController.getNotificationsSettings(UserConfig.selectedAccount).edit();
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("color_");
        stringBuilder.append(j);
        edit.remove(stringBuilder.toString());
        edit.commit();
        if (runnable != null) {
            runnable.run();
        }
    }

    public static Dialog createVibrationSelectDialog(Activity activity, long j, boolean z, boolean z2, Runnable runnable) {
        String str = j != 0 ? "vibrate_" : z ? "vibrate_group" : "vibrate_messages";
        return createVibrationSelectDialog(activity, j, str, runnable);
    }

    public static Dialog createVibrationSelectDialog(Activity activity, long j, String str, Runnable runnable) {
        String[] strArr;
        Builder builder;
        Context context = activity;
        long j2 = j;
        String str2 = str;
        SharedPreferences notificationsSettings = MessagesController.getNotificationsSettings(UserConfig.selectedAccount);
        int[] iArr = new int[1];
        String str3 = "Long";
        String str4 = "Short";
        String str5 = "VibrationDefault";
        String str6 = "VibrationDisabled";
        int i = 0;
        if (j2 != 0) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(str2);
            stringBuilder.append(j2);
            iArr[0] = notificationsSettings.getInt(stringBuilder.toString(), 0);
            if (iArr[0] == 3) {
                iArr[0] = 2;
            } else if (iArr[0] == 2) {
                iArr[0] = 3;
            }
            strArr = new String[]{LocaleController.getString(str5, NUM), LocaleController.getString(str4, NUM), LocaleController.getString(str3, NUM), LocaleController.getString(str6, NUM)};
        } else {
            iArr[0] = notificationsSettings.getInt(str2, 0);
            if (iArr[0] == 0) {
                iArr[0] = 1;
            } else if (iArr[0] == 1) {
                iArr[0] = 2;
            } else if (iArr[0] == 2) {
                iArr[0] = 0;
            }
            strArr = new String[]{LocaleController.getString(str6, NUM), LocaleController.getString(str5, NUM), LocaleController.getString(str4, NUM), LocaleController.getString(str3, NUM), LocaleController.getString("OnlyIfSilent", NUM)};
        }
        String[] strArr2 = strArr;
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(1);
        Builder builder2 = new Builder(context);
        int i2 = 0;
        while (i2 < strArr2.length) {
            FrameLayout radioColorCell = new RadioColorCell(context);
            radioColorCell.setPadding(AndroidUtilities.dp(4.0f), i, AndroidUtilities.dp(4.0f), i);
            radioColorCell.setTag(Integer.valueOf(i2));
            radioColorCell.setCheckColor(Theme.getColor("radioBackground"), Theme.getColor("dialogRadioBackgroundChecked"));
            radioColorCell.setTextAndValue(strArr2[i2], iArr[i] == i2);
            linearLayout.addView(radioColorCell);
            -$$Lambda$AlertsCreator$syzpMtTwphGwWxsLrHqlm0H7ZL0 -__lambda_alertscreator_syzpmttwphgwwxslrhqlm0h7zl0 = r1;
            FrameLayout frameLayout = radioColorCell;
            int i3 = i2;
            builder = builder2;
            -$$Lambda$AlertsCreator$syzpMtTwphGwWxsLrHqlm0H7ZL0 -__lambda_alertscreator_syzpmttwphgwwxslrhqlm0h7zl02 = new -$$Lambda$AlertsCreator$syzpMtTwphGwWxsLrHqlm0H7ZL0(iArr, j, str, builder2, runnable);
            frameLayout.setOnClickListener(-__lambda_alertscreator_syzpmttwphgwwxslrhqlm0h7zl0);
            i2 = i3 + 1;
            i = 0;
            builder2 = builder;
            context = activity;
        }
        builder = builder2;
        builder.setTitle(LocaleController.getString("Vibrate", NUM));
        builder.setView(linearLayout);
        builder.setPositiveButton(LocaleController.getString("Cancel", NUM), null);
        return builder.create();
    }

    static /* synthetic */ void lambda$createVibrationSelectDialog$36(int[] iArr, long j, String str, Builder builder, Runnable runnable, View view) {
        iArr[0] = ((Integer) view.getTag()).intValue();
        Editor edit = MessagesController.getNotificationsSettings(UserConfig.selectedAccount).edit();
        if (j != 0) {
            StringBuilder stringBuilder;
            if (iArr[0] == 0) {
                stringBuilder = new StringBuilder();
                stringBuilder.append(str);
                stringBuilder.append(j);
                edit.putInt(stringBuilder.toString(), 0);
            } else if (iArr[0] == 1) {
                stringBuilder = new StringBuilder();
                stringBuilder.append(str);
                stringBuilder.append(j);
                edit.putInt(stringBuilder.toString(), 1);
            } else if (iArr[0] == 2) {
                stringBuilder = new StringBuilder();
                stringBuilder.append(str);
                stringBuilder.append(j);
                edit.putInt(stringBuilder.toString(), 3);
            } else if (iArr[0] == 3) {
                stringBuilder = new StringBuilder();
                stringBuilder.append(str);
                stringBuilder.append(j);
                edit.putInt(stringBuilder.toString(), 2);
            }
        } else if (iArr[0] == 0) {
            edit.putInt(str, 2);
        } else if (iArr[0] == 1) {
            edit.putInt(str, 0);
        } else if (iArr[0] == 2) {
            edit.putInt(str, 1);
        } else if (iArr[0] == 3) {
            edit.putInt(str, 3);
        } else if (iArr[0] == 4) {
            edit.putInt(str, 4);
        }
        edit.commit();
        builder.getDismissRunnable().run();
        if (runnable != null) {
            runnable.run();
        }
    }

    public static Dialog createLocationUpdateDialog(Activity activity, User user, IntCallback intCallback) {
        Context context = activity;
        int[] iArr = new int[1];
        int i = 3;
        String[] strArr = new String[]{LocaleController.getString("SendLiveLocationFor15m", NUM), LocaleController.getString("SendLiveLocationFor1h", NUM), LocaleController.getString("SendLiveLocationFor8h", NUM)};
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(1);
        TextView textView = new TextView(context);
        if (user != null) {
            textView.setText(LocaleController.formatString("LiveLocationAlertPrivate", NUM, UserObject.getFirstName(user)));
        } else {
            textView.setText(LocaleController.getString("LiveLocationAlertGroup", NUM));
        }
        textView.setTextColor(Theme.getColor("dialogTextBlack"));
        textView.setTextSize(1, 16.0f);
        textView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
        if (LocaleController.isRTL) {
            i = 5;
        }
        linearLayout.addView(textView, LayoutHelper.createLinear(-2, -2, i | 48, 24, 0, 24, 8));
        i = 0;
        while (i < strArr.length) {
            RadioColorCell radioColorCell = new RadioColorCell(context);
            radioColorCell.setPadding(AndroidUtilities.dp(4.0f), 0, AndroidUtilities.dp(4.0f), 0);
            radioColorCell.setTag(Integer.valueOf(i));
            radioColorCell.setCheckColor(Theme.getColor("radioBackground"), Theme.getColor("dialogRadioBackgroundChecked"));
            radioColorCell.setTextAndValue(strArr[i], iArr[0] == i);
            linearLayout.addView(radioColorCell);
            radioColorCell.setOnClickListener(new -$$Lambda$AlertsCreator$jseC0HYyR6q-PUszkpljeGky6c4(iArr, linearLayout));
            i++;
        }
        Builder builder = new Builder(context);
        builder.setTopImage(new ShareLocationDrawable(context, 0), Theme.getColor("dialogTopBackground"));
        builder.setView(linearLayout);
        builder.setPositiveButton(LocaleController.getString("ShareFile", NUM), new -$$Lambda$AlertsCreator$4DgzvGzLV2R1O7syobir4ZMBj70(iArr, intCallback));
        builder.setNeutralButton(LocaleController.getString("Cancel", NUM), null);
        return builder.create();
    }

    static /* synthetic */ void lambda$createLocationUpdateDialog$37(int[] iArr, LinearLayout linearLayout, View view) {
        iArr[0] = ((Integer) view.getTag()).intValue();
        int childCount = linearLayout.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childAt = linearLayout.getChildAt(i);
            if (childAt instanceof RadioColorCell) {
                ((RadioColorCell) childAt).setChecked(childAt == view, true);
            }
        }
    }

    static /* synthetic */ void lambda$createLocationUpdateDialog$38(int[] iArr, IntCallback intCallback, DialogInterface dialogInterface, int i) {
        int i2 = iArr[0] == 0 ? 900 : iArr[0] == 1 ? 3600 : 28800;
        intCallback.run(i2);
    }

    public static Builder createContactsPermissionDialog(Activity activity, IntCallback intCallback) {
        Builder builder = new Builder((Context) activity);
        builder.setTopImage(NUM, Theme.getColor("dialogTopBackground"));
        builder.setMessage(AndroidUtilities.replaceTags(LocaleController.getString("ContactsPermissionAlert", NUM)));
        builder.setPositiveButton(LocaleController.getString("ContactsPermissionAlertContinue", NUM), new -$$Lambda$AlertsCreator$sSs0RRmSmQ5x-TafRaBFBiuL70Q(intCallback));
        builder.setNegativeButton(LocaleController.getString("ContactsPermissionAlertNotNow", NUM), new -$$Lambda$AlertsCreator$sDLDnfWTCsAi_gc9Vf8y3-fzeAw(intCallback));
        return builder;
    }

    public static Dialog createFreeSpaceDialog(LaunchActivity launchActivity) {
        Context context = launchActivity;
        int[] iArr = new int[1];
        int i = SharedConfig.keepMedia;
        int i2 = 3;
        if (i == 2) {
            iArr[0] = 3;
        } else if (i == 0) {
            iArr[0] = 1;
        } else if (i == 1) {
            iArr[0] = 2;
        } else if (i == 3) {
            iArr[0] = 0;
        }
        String[] strArr = new String[]{LocaleController.formatPluralString("Days", 3), LocaleController.formatPluralString("Weeks", 1), LocaleController.formatPluralString("Months", 1), LocaleController.getString("LowDiskSpaceNeverRemove", NUM)};
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(1);
        TextView textView = new TextView(context);
        textView.setText(LocaleController.getString("LowDiskSpaceTitle2", NUM));
        textView.setTextColor(Theme.getColor("dialogTextBlack"));
        textView.setTextSize(1, 16.0f);
        textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        textView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
        if (LocaleController.isRTL) {
            i2 = 5;
        }
        linearLayout.addView(textView, LayoutHelper.createLinear(-2, -2, i2 | 48, 24, 0, 24, 8));
        i2 = 0;
        while (i2 < strArr.length) {
            RadioColorCell radioColorCell = new RadioColorCell(context);
            radioColorCell.setPadding(AndroidUtilities.dp(4.0f), 0, AndroidUtilities.dp(4.0f), 0);
            radioColorCell.setTag(Integer.valueOf(i2));
            radioColorCell.setCheckColor(Theme.getColor("radioBackground"), Theme.getColor("dialogRadioBackgroundChecked"));
            radioColorCell.setTextAndValue(strArr[i2], iArr[0] == i2);
            linearLayout.addView(radioColorCell);
            radioColorCell.setOnClickListener(new -$$Lambda$AlertsCreator$eQUomDIkBtJHqhk8FO0iWAMg7Qs(iArr, linearLayout));
            i2++;
        }
        Builder builder = new Builder(context);
        builder.setTitle(LocaleController.getString("LowDiskSpaceTitle", NUM));
        builder.setMessage(LocaleController.getString("LowDiskSpaceMessage", NUM));
        builder.setView(linearLayout);
        builder.setPositiveButton(LocaleController.getString("OK", NUM), new -$$Lambda$AlertsCreator$KHkLeEyAM3ueqVXJldazmtZAKQY(iArr));
        builder.setNeutralButton(LocaleController.getString("ClearMediaCache", NUM), new -$$Lambda$AlertsCreator$iClqD2-jkNM7kdikk3gtVUT5Sn4(context));
        return builder.create();
    }

    static /* synthetic */ void lambda$createFreeSpaceDialog$41(int[] iArr, LinearLayout linearLayout, View view) {
        int intValue = ((Integer) view.getTag()).intValue();
        if (intValue == 0) {
            iArr[0] = 3;
        } else if (intValue == 1) {
            iArr[0] = 0;
        } else if (intValue == 2) {
            iArr[0] = 1;
        } else if (intValue == 3) {
            iArr[0] = 2;
        }
        int childCount = linearLayout.getChildCount();
        for (intValue = 0; intValue < childCount; intValue++) {
            View childAt = linearLayout.getChildAt(intValue);
            if (childAt instanceof RadioColorCell) {
                ((RadioColorCell) childAt).setChecked(childAt == view, true);
            }
        }
    }

    public static Dialog createPrioritySelectDialog(Activity activity, long j, int i, Runnable runnable) {
        String[] strArr;
        View view;
        Context context = activity;
        long j2 = j;
        int i2 = i;
        SharedPreferences notificationsSettings = MessagesController.getNotificationsSettings(UserConfig.selectedAccount);
        int[] iArr = new int[1];
        String str = "NotificationsPriorityUrgent";
        String str2 = "NotificationsPriorityHigh";
        String str3 = "NotificationsPriorityMedium";
        String str4 = "NotificationsPriorityLow";
        int i3 = 0;
        if (j2 != 0) {
            int i4;
            String[] strArr2;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("priority_");
            stringBuilder.append(j2);
            iArr[0] = notificationsSettings.getInt(stringBuilder.toString(), 3);
            if (iArr[0] == 3) {
                iArr[0] = 0;
            } else if (iArr[0] == 4) {
                iArr[0] = 1;
            } else {
                i4 = 5;
                if (iArr[0] == 5) {
                    iArr[0] = 2;
                } else if (iArr[0] == 0) {
                    iArr[0] = 3;
                } else {
                    iArr[0] = 4;
                }
                strArr2 = new String[i4];
                strArr2[0] = LocaleController.getString("NotificationsPrioritySettings", NUM);
                strArr2[1] = LocaleController.getString(str4, NUM);
                strArr2[2] = LocaleController.getString(str3, NUM);
                strArr2[3] = LocaleController.getString(str2, NUM);
                strArr2[4] = LocaleController.getString(str, NUM);
                strArr = strArr2;
            }
            i4 = 5;
            strArr2 = new String[i4];
            strArr2[0] = LocaleController.getString("NotificationsPrioritySettings", NUM);
            strArr2[1] = LocaleController.getString(str4, NUM);
            strArr2[2] = LocaleController.getString(str3, NUM);
            strArr2[3] = LocaleController.getString(str2, NUM);
            strArr2[4] = LocaleController.getString(str, NUM);
            strArr = strArr2;
        } else {
            int i5;
            if (j2 == 0) {
                if (i2 == 1) {
                    iArr[0] = notificationsSettings.getInt("priority_messages", 1);
                } else if (i2 == 0) {
                    iArr[0] = notificationsSettings.getInt("priority_group", 1);
                } else if (i2 == 2) {
                    iArr[0] = notificationsSettings.getInt("priority_channel", 1);
                }
            }
            if (iArr[0] == 4) {
                iArr[0] = 0;
            } else if (iArr[0] == 5) {
                iArr[0] = 1;
            } else {
                if (iArr[0] == 0) {
                    i5 = 2;
                    iArr[0] = 2;
                } else {
                    i5 = 2;
                    iArr[0] = 3;
                }
                strArr = new String[]{LocaleController.getString(str4, NUM), LocaleController.getString(str3, NUM), LocaleController.getString(str2, NUM), LocaleController.getString(str, NUM)};
            }
            i5 = 2;
            strArr = new String[]{LocaleController.getString(str4, NUM), LocaleController.getString(str3, NUM), LocaleController.getString(str2, NUM), LocaleController.getString(str, NUM)};
        }
        View linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(1);
        Builder builder = new Builder(context);
        int i6 = 0;
        while (i6 < strArr.length) {
            FrameLayout radioColorCell = new RadioColorCell(context);
            radioColorCell.setPadding(AndroidUtilities.dp(4.0f), i3, AndroidUtilities.dp(4.0f), i3);
            radioColorCell.setTag(Integer.valueOf(i6));
            radioColorCell.setCheckColor(Theme.getColor("radioBackground"), Theme.getColor("dialogRadioBackgroundChecked"));
            radioColorCell.setTextAndValue(strArr[i6], iArr[i3] == i6);
            linearLayout.addView(radioColorCell);
            -$$Lambda$AlertsCreator$oQy1pJfs9yOPZRG3s-N56Cz0IZU -__lambda_alertscreator_oqy1pjfs9yopzrg3s-n56cz0izu = r1;
            FrameLayout frameLayout = radioColorCell;
            int i7 = i6;
            Builder builder2 = builder;
            view = linearLayout;
            -$$Lambda$AlertsCreator$oQy1pJfs9yOPZRG3s-N56Cz0IZU -__lambda_alertscreator_oqy1pjfs9yopzrg3s-n56cz0izu2 = new -$$Lambda$AlertsCreator$oQy1pJfs9yOPZRG3s-N56Cz0IZU(iArr, j, i, notificationsSettings, builder, runnable);
            frameLayout.setOnClickListener(-__lambda_alertscreator_oqy1pjfs9yopzrg3s-n56cz0izu);
            i6 = i7 + 1;
            i3 = 0;
            context = activity;
            linearLayout = view;
            j2 = j;
        }
        view = linearLayout;
        Builder builder3 = builder;
        builder3.setTitle(LocaleController.getString("NotificationsImportance", NUM));
        builder3.setView(view);
        builder3.setPositiveButton(LocaleController.getString("Cancel", NUM), null);
        return builder3.create();
    }

    static /* synthetic */ void lambda$createPrioritySelectDialog$44(int[] iArr, long j, int i, SharedPreferences sharedPreferences, Builder builder, Runnable runnable, View view) {
        iArr[0] = ((Integer) view.getTag()).intValue();
        Editor edit = MessagesController.getNotificationsSettings(UserConfig.selectedAccount).edit();
        int i2 = 5;
        if (j != 0) {
            int i3 = 3;
            if (iArr[0] != 0) {
                i3 = iArr[0] == 1 ? 4 : iArr[0] == 2 ? 5 : iArr[0] == 3 ? 0 : 1;
            }
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("priority_");
            stringBuilder.append(j);
            edit.putInt(stringBuilder.toString(), i3);
        } else {
            if (iArr[0] == 0) {
                i2 = 4;
            } else if (iArr[0] != 1) {
                i2 = iArr[0] == 2 ? 0 : 1;
            }
            String str;
            if (i == 1) {
                str = "priority_messages";
                edit.putInt(str, i2);
                iArr[0] = sharedPreferences.getInt(str, 1);
            } else if (i == 0) {
                str = "priority_group";
                edit.putInt(str, i2);
                iArr[0] = sharedPreferences.getInt(str, 1);
            } else if (i == 2) {
                str = "priority_channel";
                edit.putInt(str, i2);
                iArr[0] = sharedPreferences.getInt(str, 1);
            }
        }
        edit.commit();
        builder.getDismissRunnable().run();
        if (runnable != null) {
            runnable.run();
        }
    }

    public static Dialog createPopupSelectDialog(Activity activity, int i, Runnable runnable) {
        SharedPreferences notificationsSettings = MessagesController.getNotificationsSettings(UserConfig.selectedAccount);
        int[] iArr = new int[1];
        if (i == 1) {
            iArr[0] = notificationsSettings.getInt("popupAll", 0);
        } else if (i == 0) {
            iArr[0] = notificationsSettings.getInt("popupGroup", 0);
        } else {
            iArr[0] = notificationsSettings.getInt("popupChannel", 0);
        }
        String[] strArr = new String[]{LocaleController.getString("NoPopup", NUM), LocaleController.getString("OnlyWhenScreenOn", NUM), LocaleController.getString("OnlyWhenScreenOff", NUM), LocaleController.getString("AlwaysShowPopup", NUM)};
        LinearLayout linearLayout = new LinearLayout(activity);
        linearLayout.setOrientation(1);
        Builder builder = new Builder((Context) activity);
        int i2 = 0;
        while (i2 < strArr.length) {
            RadioColorCell radioColorCell = new RadioColorCell(activity);
            radioColorCell.setTag(Integer.valueOf(i2));
            radioColorCell.setPadding(AndroidUtilities.dp(4.0f), 0, AndroidUtilities.dp(4.0f), 0);
            radioColorCell.setCheckColor(Theme.getColor("radioBackground"), Theme.getColor("dialogRadioBackgroundChecked"));
            radioColorCell.setTextAndValue(strArr[i2], iArr[0] == i2);
            linearLayout.addView(radioColorCell);
            radioColorCell.setOnClickListener(new -$$Lambda$AlertsCreator$RGMgMsZL-ejZf0S_hIu1EuItwsE(iArr, i, builder, runnable));
            i2++;
        }
        builder.setTitle(LocaleController.getString("PopupNotification", NUM));
        builder.setView(linearLayout);
        builder.setPositiveButton(LocaleController.getString("Cancel", NUM), null);
        return builder.create();
    }

    static /* synthetic */ void lambda$createPopupSelectDialog$45(int[] iArr, int i, Builder builder, Runnable runnable, View view) {
        iArr[0] = ((Integer) view.getTag()).intValue();
        Editor edit = MessagesController.getNotificationsSettings(UserConfig.selectedAccount).edit();
        if (i == 1) {
            edit.putInt("popupAll", iArr[0]);
        } else if (i == 0) {
            edit.putInt("popupGroup", iArr[0]);
        } else {
            edit.putInt("popupChannel", iArr[0]);
        }
        edit.commit();
        builder.getDismissRunnable().run();
        if (runnable != null) {
            runnable.run();
        }
    }

    public static Dialog createSingleChoiceDialog(Activity activity, String[] strArr, String str, int i, OnClickListener onClickListener) {
        LinearLayout linearLayout = new LinearLayout(activity);
        linearLayout.setOrientation(1);
        Builder builder = new Builder((Context) activity);
        int i2 = 0;
        while (i2 < strArr.length) {
            RadioColorCell radioColorCell = new RadioColorCell(activity);
            radioColorCell.setPadding(AndroidUtilities.dp(4.0f), 0, AndroidUtilities.dp(4.0f), 0);
            radioColorCell.setTag(Integer.valueOf(i2));
            radioColorCell.setCheckColor(Theme.getColor("radioBackground"), Theme.getColor("dialogRadioBackgroundChecked"));
            radioColorCell.setTextAndValue(strArr[i2], i == i2);
            linearLayout.addView(radioColorCell);
            radioColorCell.setOnClickListener(new -$$Lambda$AlertsCreator$s_f4il5Cdz2aKo3SpNpW8kGHrcQ(builder, onClickListener));
            i2++;
        }
        builder.setTitle(str);
        builder.setView(linearLayout);
        builder.setPositiveButton(LocaleController.getString("Cancel", NUM), null);
        return builder.create();
    }

    static /* synthetic */ void lambda$createSingleChoiceDialog$46(Builder builder, OnClickListener onClickListener, View view) {
        int intValue = ((Integer) view.getTag()).intValue();
        builder.getDismissRunnable().run();
        onClickListener.onClick(null, intValue);
    }

    public static Builder createTTLAlert(Context context, EncryptedChat encryptedChat) {
        Builder builder = new Builder(context);
        builder.setTitle(LocaleController.getString("MessageLifetime", NUM));
        NumberPicker numberPicker = new NumberPicker(context);
        numberPicker.setMinValue(0);
        numberPicker.setMaxValue(20);
        int i = encryptedChat.ttl;
        if (i <= 0 || i >= 16) {
            i = encryptedChat.ttl;
            if (i == 30) {
                numberPicker.setValue(16);
            } else if (i == 60) {
                numberPicker.setValue(17);
            } else if (i == 3600) {
                numberPicker.setValue(18);
            } else if (i == 86400) {
                numberPicker.setValue(19);
            } else if (i == 604800) {
                numberPicker.setValue(20);
            } else if (i == 0) {
                numberPicker.setValue(0);
            }
        } else {
            numberPicker.setValue(i);
        }
        numberPicker.setFormatter(-$$Lambda$AlertsCreator$uoFeTKAjtXsAuhg_3CHs0TvRxWE.INSTANCE);
        builder.setView(numberPicker);
        builder.setNegativeButton(LocaleController.getString("Done", NUM), new -$$Lambda$AlertsCreator$Ky57ZWK-GIOKEvj3_MJdqhC5X9o(encryptedChat, numberPicker));
        return builder;
    }

    static /* synthetic */ String lambda$createTTLAlert$47(int i) {
        if (i == 0) {
            return LocaleController.getString("ShortMessageLifetimeForever", NUM);
        }
        if (i >= 1 && i < 16) {
            return LocaleController.formatTTLString(i);
        }
        if (i == 16) {
            return LocaleController.formatTTLString(30);
        }
        if (i == 17) {
            return LocaleController.formatTTLString(60);
        }
        if (i == 18) {
            return LocaleController.formatTTLString(3600);
        }
        if (i == 19) {
            return LocaleController.formatTTLString(86400);
        }
        return i == 20 ? LocaleController.formatTTLString(604800) : "";
    }

    static /* synthetic */ void lambda$createTTLAlert$48(EncryptedChat encryptedChat, NumberPicker numberPicker, DialogInterface dialogInterface, int i) {
        int i2 = encryptedChat.ttl;
        int value = numberPicker.getValue();
        if (value >= 0 && value < 16) {
            encryptedChat.ttl = value;
        } else if (value == 16) {
            encryptedChat.ttl = 30;
        } else if (value == 17) {
            encryptedChat.ttl = 60;
        } else if (value == 18) {
            encryptedChat.ttl = 3600;
        } else if (value == 19) {
            encryptedChat.ttl = 86400;
        } else if (value == 20) {
            encryptedChat.ttl = 604800;
        }
        if (i2 != encryptedChat.ttl) {
            SecretChatHelper.getInstance(UserConfig.selectedAccount).sendTTLMessage(encryptedChat, null);
            MessagesStorage.getInstance(UserConfig.selectedAccount).updateEncryptedChatTTL(encryptedChat);
        }
    }

    public static AlertDialog createAccountSelectDialog(Activity activity, AccountSelectDelegate accountSelectDelegate) {
        if (UserConfig.getActivatedAccountsCount() < 2) {
            return null;
        }
        Builder builder = new Builder((Context) activity);
        Runnable dismissRunnable = builder.getDismissRunnable();
        AlertDialog[] alertDialogArr = new AlertDialog[1];
        LinearLayout linearLayout = new LinearLayout(activity);
        linearLayout.setOrientation(1);
        for (int i = 0; i < 3; i++) {
            if (UserConfig.getInstance(i).getCurrentUser() != null) {
                AccountSelectCell accountSelectCell = new AccountSelectCell(activity);
                accountSelectCell.setAccount(i, false);
                accountSelectCell.setPadding(AndroidUtilities.dp(14.0f), 0, AndroidUtilities.dp(14.0f), 0);
                accountSelectCell.setBackgroundDrawable(Theme.getSelectorDrawable(false));
                linearLayout.addView(accountSelectCell, LayoutHelper.createLinear(-1, 50));
                accountSelectCell.setOnClickListener(new -$$Lambda$AlertsCreator$0y0_2Pk2ItkXAjP_tr7kpNUKH0k(alertDialogArr, dismissRunnable, accountSelectDelegate));
            }
        }
        builder.setTitle(LocaleController.getString("SelectAccount", NUM));
        builder.setView(linearLayout);
        builder.setPositiveButton(LocaleController.getString("Cancel", NUM), null);
        AlertDialog create = builder.create();
        alertDialogArr[0] = create;
        return create;
    }

    static /* synthetic */ void lambda$createAccountSelectDialog$49(AlertDialog[] alertDialogArr, Runnable runnable, AccountSelectDelegate accountSelectDelegate, View view) {
        if (alertDialogArr[0] != null) {
            alertDialogArr[0].setOnDismissListener(null);
        }
        runnable.run();
        accountSelectDelegate.didSelectAccount(((AccountSelectCell) view).getAccountNumber());
    }

    /* JADX WARNING: Removed duplicated region for block: B:195:0x03a6  */
    /* JADX WARNING: Removed duplicated region for block: B:194:0x03a4  */
    /* JADX WARNING: Removed duplicated region for block: B:72:0x00fd  */
    /* JADX WARNING: Removed duplicated region for block: B:71:0x00fb  */
    /* JADX WARNING: Removed duplicated region for block: B:141:0x0282  */
    /* JADX WARNING: Removed duplicated region for block: B:140:0x027d  */
    /* JADX WARNING: Removed duplicated region for block: B:145:0x028f  */
    /* JADX WARNING: Removed duplicated region for block: B:144:0x028a  */
    public static void createDeleteMessagesAlert(org.telegram.ui.ActionBar.BaseFragment r34, org.telegram.tgnet.TLRPC.User r35, org.telegram.tgnet.TLRPC.Chat r36, org.telegram.tgnet.TLRPC.EncryptedChat r37, org.telegram.tgnet.TLRPC.ChatFull r38, long r39, org.telegram.messenger.MessageObject r41, android.util.SparseArray<org.telegram.messenger.MessageObject>[] r42, org.telegram.messenger.MessageObject.GroupedMessages r43, boolean r44, int r45, java.lang.Runnable r46) {
        /*
        r14 = r34;
        r3 = r35;
        r4 = r36;
        r5 = r37;
        r9 = r41;
        r11 = r43;
        r0 = r45;
        if (r14 == 0) goto L_0x05e7;
    L_0x0010:
        if (r3 != 0) goto L_0x0018;
    L_0x0012:
        if (r4 != 0) goto L_0x0018;
    L_0x0014:
        if (r5 != 0) goto L_0x0018;
    L_0x0016:
        goto L_0x05e7;
    L_0x0018:
        r1 = r34.getParentActivity();
        if (r1 != 0) goto L_0x001f;
    L_0x001e:
        return;
    L_0x001f:
        r15 = r34.getCurrentAccount();
        r2 = new org.telegram.ui.ActionBar.AlertDialog$Builder;
        r2.<init>(r1);
        r6 = 1;
        r7 = 0;
        if (r11 == 0) goto L_0x0033;
    L_0x002c:
        r8 = r11.messages;
        r8 = r8.size();
        goto L_0x0044;
    L_0x0033:
        if (r9 == 0) goto L_0x0037;
    L_0x0035:
        r8 = 1;
        goto L_0x0044;
    L_0x0037:
        r8 = r42[r7];
        r8 = r8.size();
        r10 = r42[r6];
        r10 = r10.size();
        r8 = r8 + r10;
    L_0x0044:
        if (r5 == 0) goto L_0x004f;
    L_0x0046:
        r10 = r5.id;
        r12 = (long) r10;
        r10 = 32;
        r12 = r12 << r10;
    L_0x004c:
        r20 = r12;
        goto L_0x0059;
    L_0x004f:
        if (r3 == 0) goto L_0x0054;
    L_0x0051:
        r10 = r3.id;
        goto L_0x0057;
    L_0x0054:
        r10 = r4.id;
        r10 = -r10;
    L_0x0057:
        r12 = (long) r10;
        goto L_0x004c;
    L_0x0059:
        r10 = 3;
        r12 = new boolean[r10];
        r13 = new boolean[r6];
        if (r3 == 0) goto L_0x006a;
    L_0x0060:
        r7 = org.telegram.messenger.MessagesController.getInstance(r15);
        r7 = r7.canRevokePmInbox;
        if (r7 == 0) goto L_0x006a;
    L_0x0068:
        r7 = 1;
        goto L_0x006b;
    L_0x006a:
        r7 = 0;
    L_0x006b:
        if (r3 == 0) goto L_0x0074;
    L_0x006d:
        r10 = org.telegram.messenger.MessagesController.getInstance(r15);
        r10 = r10.revokeTimePmLimit;
        goto L_0x007a;
    L_0x0074:
        r10 = org.telegram.messenger.MessagesController.getInstance(r15);
        r10 = r10.revokeTimeLimit;
    L_0x007a:
        if (r5 != 0) goto L_0x0087;
    L_0x007c:
        if (r3 == 0) goto L_0x0087;
    L_0x007e:
        if (r7 == 0) goto L_0x0087;
    L_0x0080:
        r6 = NUM; // 0x7fffffff float:NaN double:1.060997895E-314;
        if (r10 != r6) goto L_0x0087;
    L_0x0085:
        r6 = 1;
        goto L_0x0088;
    L_0x0087:
        r6 = 0;
    L_0x0088:
        r17 = NUM; // 0x41800000 float:16.0 double:5.42932517E-315;
        r18 = NUM; // 0x41000000 float:8.0 double:5.38787994E-315;
        r11 = "";
        r30 = r8;
        if (r4 == 0) goto L_0x0344;
    L_0x0092:
        r8 = r4.megagroup;
        if (r8 == 0) goto L_0x0344;
    L_0x0096:
        if (r44 != 0) goto L_0x0344;
    L_0x0098:
        r7 = org.telegram.messenger.ChatObject.canBlockUsers(r36);
        r8 = org.telegram.tgnet.ConnectionsManager.getInstance(r15);
        r8 = r8.getCurrentTime();
        if (r9 == 0) goto L_0x0105;
    L_0x00a6:
        r31 = r6;
        r6 = r9.messageOwner;
        r6 = r6.action;
        if (r6 == 0) goto L_0x00c1;
    L_0x00ae:
        r3 = r6 instanceof org.telegram.tgnet.TLRPC.TL_messageActionEmpty;
        if (r3 != 0) goto L_0x00c1;
    L_0x00b2:
        r3 = r6 instanceof org.telegram.tgnet.TLRPC.TL_messageActionChatDeleteUser;
        if (r3 != 0) goto L_0x00c1;
    L_0x00b6:
        r3 = r6 instanceof org.telegram.tgnet.TLRPC.TL_messageActionChatJoinedByLink;
        if (r3 != 0) goto L_0x00c1;
    L_0x00ba:
        r3 = r6 instanceof org.telegram.tgnet.TLRPC.TL_messageActionChatAddUser;
        if (r3 == 0) goto L_0x00bf;
    L_0x00be:
        goto L_0x00c1;
    L_0x00bf:
        r3 = 0;
        goto L_0x00d1;
    L_0x00c1:
        r3 = org.telegram.messenger.MessagesController.getInstance(r15);
        r6 = r9.messageOwner;
        r6 = r6.from_id;
        r6 = java.lang.Integer.valueOf(r6);
        r3 = r3.getUser(r6);
    L_0x00d1:
        r6 = r41.isSendError();
        if (r6 != 0) goto L_0x00f8;
    L_0x00d7:
        r22 = r41.getDialogId();
        r6 = (r22 > r39 ? 1 : (r22 == r39 ? 0 : -1));
        if (r6 != 0) goto L_0x00f8;
    L_0x00df:
        r6 = r9.messageOwner;
        r6 = r6.action;
        if (r6 == 0) goto L_0x00e9;
    L_0x00e5:
        r6 = r6 instanceof org.telegram.tgnet.TLRPC.TL_messageActionEmpty;
        if (r6 == 0) goto L_0x00f8;
    L_0x00e9:
        r6 = r41.isOut();
        if (r6 == 0) goto L_0x00f8;
    L_0x00ef:
        r6 = r9.messageOwner;
        r6 = r6.date;
        r8 = r8 - r6;
        if (r8 > r10) goto L_0x00f8;
    L_0x00f6:
        r6 = 1;
        goto L_0x00f9;
    L_0x00f8:
        r6 = 0;
    L_0x00f9:
        if (r6 == 0) goto L_0x00fd;
    L_0x00fb:
        r6 = 1;
        goto L_0x00fe;
    L_0x00fd:
        r6 = 0;
    L_0x00fe:
        r32 = r2;
        r5 = r6;
        r22 = r13;
        goto L_0x0197;
    L_0x0105:
        r31 = r6;
        r3 = 1;
        r6 = -1;
    L_0x0109:
        if (r3 < 0) goto L_0x0147;
    L_0x010b:
        r9 = r6;
        r6 = 0;
    L_0x010d:
        r19 = r42[r3];
        r5 = r19.size();
        r22 = r13;
        if (r6 >= r5) goto L_0x0139;
    L_0x0117:
        r5 = r42[r3];
        r5 = r5.valueAt(r6);
        r5 = (org.telegram.messenger.MessageObject) r5;
        r13 = -1;
        if (r9 != r13) goto L_0x0126;
    L_0x0122:
        r9 = r5.messageOwner;
        r9 = r9.from_id;
    L_0x0126:
        if (r9 < 0) goto L_0x0136;
    L_0x0128:
        r5 = r5.messageOwner;
        r5 = r5.from_id;
        if (r9 == r5) goto L_0x012f;
    L_0x012e:
        goto L_0x0136;
    L_0x012f:
        r6 = r6 + 1;
        r5 = r37;
        r13 = r22;
        goto L_0x010d;
    L_0x0136:
        r5 = -2;
        r6 = -2;
        goto L_0x013b;
    L_0x0139:
        r6 = r9;
        r5 = -2;
    L_0x013b:
        if (r6 != r5) goto L_0x013e;
    L_0x013d:
        goto L_0x0149;
    L_0x013e:
        r3 = r3 + -1;
        r5 = r37;
        r9 = r41;
        r13 = r22;
        goto L_0x0109;
    L_0x0147:
        r22 = r13;
    L_0x0149:
        r3 = 1;
        r5 = 0;
    L_0x014b:
        if (r3 < 0) goto L_0x0183;
    L_0x014d:
        r9 = r5;
        r5 = 0;
    L_0x014f:
        r13 = r42[r3];
        r13 = r13.size();
        if (r5 >= r13) goto L_0x017d;
    L_0x0157:
        r13 = r42[r3];
        r13 = r13.valueAt(r5);
        r13 = (org.telegram.messenger.MessageObject) r13;
        r32 = r2;
        r2 = 1;
        if (r3 != r2) goto L_0x0178;
    L_0x0164:
        r2 = r13.isOut();
        if (r2 == 0) goto L_0x0178;
    L_0x016a:
        r2 = r13.messageOwner;
        r13 = r2.action;
        if (r13 != 0) goto L_0x0178;
    L_0x0170:
        r2 = r2.date;
        r2 = r8 - r2;
        if (r2 > r10) goto L_0x0178;
    L_0x0176:
        r9 = r9 + 1;
    L_0x0178:
        r5 = r5 + 1;
        r2 = r32;
        goto L_0x014f;
    L_0x017d:
        r32 = r2;
        r3 = r3 + -1;
        r5 = r9;
        goto L_0x014b;
    L_0x0183:
        r32 = r2;
        r2 = -1;
        if (r6 == r2) goto L_0x0196;
    L_0x0188:
        r2 = org.telegram.messenger.MessagesController.getInstance(r15);
        r3 = java.lang.Integer.valueOf(r6);
        r2 = r2.getUser(r3);
        r3 = r2;
        goto L_0x0197;
    L_0x0196:
        r3 = 0;
    L_0x0197:
        if (r3 == 0) goto L_0x02cb;
    L_0x0199:
        r2 = r3.id;
        r6 = org.telegram.messenger.UserConfig.getInstance(r15);
        r6 = r6.getClientUserId();
        if (r2 == r6) goto L_0x02cb;
    L_0x01a5:
        r2 = 1;
        if (r0 != r2) goto L_0x020b;
    L_0x01a8:
        r6 = r4.creator;
        if (r6 != 0) goto L_0x020b;
    L_0x01ac:
        r13 = new org.telegram.ui.ActionBar.AlertDialog[r2];
        r0 = new org.telegram.ui.ActionBar.AlertDialog;
        r2 = 3;
        r0.<init>(r1, r2);
        r1 = 0;
        r13[r1] = r0;
        r12 = new org.telegram.tgnet.TLRPC$TL_channels_getParticipant;
        r12.<init>();
        r0 = org.telegram.messenger.MessagesController.getInputChannel(r36);
        r12.channel = r0;
        r0 = org.telegram.messenger.MessagesController.getInstance(r15);
        r0 = r0.getInputUser(r3);
        r12.user_id = r0;
        r11 = org.telegram.tgnet.ConnectionsManager.getInstance(r15);
        r10 = new org.telegram.ui.Components.-$$Lambda$AlertsCreator$lDp8yqhR02j-Tm5fYE0PsJuUsAE;
        r0 = r10;
        r1 = r13;
        r2 = r34;
        r3 = r35;
        r4 = r36;
        r5 = r37;
        r6 = r38;
        r7 = r39;
        r9 = r41;
        r14 = r10;
        r10 = r42;
        r19 = r15;
        r15 = r11;
        r11 = r43;
        r16 = r14;
        r14 = r12;
        r12 = r44;
        r33 = r13;
        r13 = r46;
        r0.<init>(r1, r2, r3, r4, r5, r6, r7, r9, r10, r11, r12, r13);
        r0 = r16;
        r0 = r15.sendRequest(r14, r0);
        r1 = new org.telegram.ui.Components.-$$Lambda$AlertsCreator$W2EWhTpIMHSc6Msis0wOifLCBfg;
        r6 = r19;
        r3 = r33;
        r1.<init>(r3, r6, r0, r2);
        r2 = 1000; // 0x3e8 float:1.401E-42 double:4.94E-321;
        org.telegram.messenger.AndroidUtilities.runOnUIThread(r1, r2);
        return;
    L_0x020b:
        r2 = r14;
        r6 = r15;
        r8 = new android.widget.FrameLayout;
        r8.<init>(r1);
        r9 = 0;
        r10 = 0;
    L_0x0214:
        r13 = 3;
        if (r9 >= r13) goto L_0x02c1;
    L_0x0217:
        r14 = 2;
        if (r0 == r14) goto L_0x021c;
    L_0x021a:
        if (r7 != 0) goto L_0x0222;
    L_0x021c:
        if (r9 != 0) goto L_0x0222;
    L_0x021e:
        r19 = r7;
        goto L_0x02b9;
    L_0x0222:
        r14 = new org.telegram.ui.Cells.CheckBoxCell;
        r15 = 1;
        r14.<init>(r1, r15);
        r39 = 0;
        r13 = org.telegram.ui.ActionBar.Theme.getSelectorDrawable(r39);
        r14.setBackgroundDrawable(r13);
        r13 = java.lang.Integer.valueOf(r9);
        r14.setTag(r13);
        if (r9 != 0) goto L_0x024a;
    L_0x023a:
        r13 = NUM; // 0x7f0d035b float:1.8743857E38 double:1.053130202E-314;
        r15 = "DeleteBanUser";
        r13 = org.telegram.messenger.LocaleController.getString(r15, r13);
        r15 = 0;
        r14.setText(r13, r11, r15, r15);
    L_0x0247:
        r19 = r7;
        goto L_0x0279;
    L_0x024a:
        r13 = 1;
        r15 = 0;
        if (r9 != r13) goto L_0x025b;
    L_0x024e:
        r13 = NUM; // 0x7f0d0370 float:1.87439E38 double:1.0531302123E-314;
        r0 = "DeleteReportSpam";
        r0 = org.telegram.messenger.LocaleController.getString(r0, r13);
        r14.setText(r0, r11, r15, r15);
        goto L_0x0247;
    L_0x025b:
        r0 = 2;
        if (r9 != r0) goto L_0x0247;
    L_0x025e:
        r13 = 1;
        r0 = new java.lang.Object[r13];
        r13 = r3.first_name;
        r19 = r7;
        r7 = r3.last_name;
        r7 = org.telegram.messenger.ContactsController.formatName(r13, r7);
        r0[r15] = r7;
        r7 = "DeleteAllFrom";
        r13 = NUM; // 0x7f0d0356 float:1.8743847E38 double:1.0531301995E-314;
        r0 = org.telegram.messenger.LocaleController.formatString(r7, r13, r0);
        r14.setText(r0, r11, r15, r15);
    L_0x0279:
        r0 = org.telegram.messenger.LocaleController.isRTL;
        if (r0 == 0) goto L_0x0282;
    L_0x027d:
        r0 = org.telegram.messenger.AndroidUtilities.dp(r17);
        goto L_0x0286;
    L_0x0282:
        r0 = org.telegram.messenger.AndroidUtilities.dp(r18);
    L_0x0286:
        r7 = org.telegram.messenger.LocaleController.isRTL;
        if (r7 == 0) goto L_0x028f;
    L_0x028a:
        r7 = org.telegram.messenger.AndroidUtilities.dp(r18);
        goto L_0x0293;
    L_0x028f:
        r7 = org.telegram.messenger.AndroidUtilities.dp(r17);
    L_0x0293:
        r13 = 0;
        r14.setPadding(r0, r13, r7, r13);
        r23 = -1;
        r24 = NUM; // 0x42400000 float:48.0 double:5.491493014E-315;
        r25 = 51;
        r26 = 0;
        r0 = r10 * 48;
        r0 = (float) r0;
        r28 = 0;
        r29 = 0;
        r27 = r0;
        r0 = org.telegram.ui.Components.LayoutHelper.createFrame(r23, r24, r25, r26, r27, r28, r29);
        r8.addView(r14, r0);
        r0 = new org.telegram.ui.Components.-$$Lambda$AlertsCreator$DwEKuO6ZyzaWNWnekWtkR1v9rpE;
        r0.<init>(r12);
        r14.setOnClickListener(r0);
        r10 = r10 + 1;
    L_0x02b9:
        r9 = r9 + 1;
        r0 = r45;
        r7 = r19;
        goto L_0x0214;
    L_0x02c1:
        r0 = r32;
        r0.setView(r8);
        r9 = r22;
        r1 = 0;
        goto L_0x033a;
    L_0x02cb:
        r2 = r14;
        r6 = r15;
        r0 = r32;
        if (r5 <= 0) goto L_0x0336;
    L_0x02d1:
        r7 = new android.widget.FrameLayout;
        r7.<init>(r1);
        r8 = new org.telegram.ui.Cells.CheckBoxCell;
        r9 = 1;
        r8.<init>(r1, r9);
        r1 = 0;
        r9 = org.telegram.ui.ActionBar.Theme.getSelectorDrawable(r1);
        r8.setBackgroundDrawable(r9);
        r9 = NUM; // 0x7f0d0368 float:1.8743883E38 double:1.0531302084E-314;
        r10 = "DeleteMessagesOption";
        r9 = org.telegram.messenger.LocaleController.getString(r10, r9);
        r8.setText(r9, r11, r1, r1);
        r1 = org.telegram.messenger.LocaleController.isRTL;
        if (r1 == 0) goto L_0x02f9;
    L_0x02f4:
        r1 = org.telegram.messenger.AndroidUtilities.dp(r17);
        goto L_0x02fd;
    L_0x02f9:
        r1 = org.telegram.messenger.AndroidUtilities.dp(r18);
    L_0x02fd:
        r9 = org.telegram.messenger.LocaleController.isRTL;
        if (r9 == 0) goto L_0x0306;
    L_0x0301:
        r9 = org.telegram.messenger.AndroidUtilities.dp(r18);
        goto L_0x030a;
    L_0x0306:
        r9 = org.telegram.messenger.AndroidUtilities.dp(r17);
    L_0x030a:
        r10 = 0;
        r8.setPadding(r1, r10, r9, r10);
        r13 = -1;
        r14 = NUM; // 0x42400000 float:48.0 double:5.491493014E-315;
        r15 = 51;
        r16 = 0;
        r17 = 0;
        r18 = 0;
        r19 = 0;
        r1 = org.telegram.ui.Components.LayoutHelper.createFrame(r13, r14, r15, r16, r17, r18, r19);
        r7.addView(r8, r1);
        r1 = new org.telegram.ui.Components.-$$Lambda$AlertsCreator$1UbQhjcQQh0Q-8XlrRhVnlVtboI;
        r9 = r22;
        r1.<init>(r9);
        r8.setOnClickListener(r1);
        r0.setView(r7);
        r1 = 9;
        r0.setCustomViewOffset(r1);
        r1 = 1;
        goto L_0x033a;
    L_0x0336:
        r9 = r22;
        r1 = 0;
        r3 = 0;
    L_0x033a:
        r25 = r3;
        r7 = r5;
        r19 = r6;
        r8 = r30;
        r3 = 0;
        goto L_0x04bd;
    L_0x0344:
        r0 = r2;
        r31 = r6;
        r9 = r13;
        r2 = r14;
        r6 = r15;
        if (r44 != 0) goto L_0x04b4;
    L_0x034c:
        r3 = org.telegram.messenger.ChatObject.isChannel(r36);
        if (r3 != 0) goto L_0x04b4;
    L_0x0352:
        if (r37 != 0) goto L_0x04b4;
    L_0x0354:
        r3 = org.telegram.tgnet.ConnectionsManager.getInstance(r6);
        r3 = r3.getCurrentTime();
        r5 = r35;
        if (r5 == 0) goto L_0x0370;
    L_0x0360:
        r8 = r5.id;
        r13 = org.telegram.messenger.UserConfig.getInstance(r6);
        r13 = r13.getClientUserId();
        if (r8 == r13) goto L_0x0370;
    L_0x036c:
        r8 = r5.bot;
        if (r8 == 0) goto L_0x0372;
    L_0x0370:
        if (r4 == 0) goto L_0x0418;
    L_0x0372:
        r8 = r41;
        if (r8 == 0) goto L_0x03b1;
    L_0x0376:
        r13 = r41.isSendError();
        if (r13 != 0) goto L_0x03a1;
    L_0x037c:
        r13 = r8.messageOwner;
        r13 = r13.action;
        if (r13 == 0) goto L_0x038a;
    L_0x0382:
        r14 = r13 instanceof org.telegram.tgnet.TLRPC.TL_messageActionEmpty;
        if (r14 != 0) goto L_0x038a;
    L_0x0386:
        r13 = r13 instanceof org.telegram.tgnet.TLRPC.TL_messageActionPhoneCall;
        if (r13 == 0) goto L_0x03a1;
    L_0x038a:
        r13 = r41.isOut();
        if (r13 != 0) goto L_0x0398;
    L_0x0390:
        if (r7 != 0) goto L_0x0398;
    L_0x0392:
        r7 = org.telegram.messenger.ChatObject.hasAdminRights(r36);
        if (r7 == 0) goto L_0x03a1;
    L_0x0398:
        r7 = r8.messageOwner;
        r7 = r7.date;
        r3 = r3 - r7;
        if (r3 > r10) goto L_0x03a1;
    L_0x039f:
        r3 = 1;
        goto L_0x03a2;
    L_0x03a1:
        r3 = 0;
    L_0x03a2:
        if (r3 == 0) goto L_0x03a6;
    L_0x03a4:
        r7 = 1;
        goto L_0x03a7;
    L_0x03a6:
        r7 = 0;
    L_0x03a7:
        r3 = r41.isOut();
        r10 = 1;
        r3 = r3 ^ r10;
        r19 = r6;
        goto L_0x041c;
    L_0x03b1:
        r13 = 1;
        r14 = 0;
        r15 = 0;
    L_0x03b4:
        if (r13 < 0) goto L_0x0413;
    L_0x03b6:
        r16 = r15;
        r15 = r14;
        r14 = 0;
    L_0x03ba:
        r19 = r42[r13];
        r5 = r19.size();
        if (r14 >= r5) goto L_0x0407;
    L_0x03c2:
        r5 = r42[r13];
        r5 = r5.valueAt(r14);
        r5 = (org.telegram.messenger.MessageObject) r5;
        r19 = r6;
        r6 = r5.messageOwner;
        r6 = r6.action;
        if (r6 == 0) goto L_0x03db;
    L_0x03d2:
        r8 = r6 instanceof org.telegram.tgnet.TLRPC.TL_messageActionEmpty;
        if (r8 != 0) goto L_0x03db;
    L_0x03d6:
        r6 = r6 instanceof org.telegram.tgnet.TLRPC.TL_messageActionPhoneCall;
        if (r6 != 0) goto L_0x03db;
    L_0x03da:
        goto L_0x03fe;
    L_0x03db:
        r6 = r5.isOut();
        if (r6 != 0) goto L_0x03eb;
    L_0x03e1:
        if (r7 != 0) goto L_0x03eb;
    L_0x03e3:
        if (r4 == 0) goto L_0x03fe;
    L_0x03e5:
        r6 = org.telegram.messenger.ChatObject.canBlockUsers(r36);
        if (r6 == 0) goto L_0x03fe;
    L_0x03eb:
        r6 = r5.messageOwner;
        r6 = r6.date;
        r6 = r3 - r6;
        if (r6 > r10) goto L_0x03fe;
    L_0x03f3:
        r16 = r16 + 1;
        if (r15 != 0) goto L_0x03fe;
    L_0x03f7:
        r5 = r5.isOut();
        if (r5 != 0) goto L_0x03fe;
    L_0x03fd:
        r15 = 1;
    L_0x03fe:
        r14 = r14 + 1;
        r5 = r35;
        r8 = r41;
        r6 = r19;
        goto L_0x03ba;
    L_0x0407:
        r19 = r6;
        r13 = r13 + -1;
        r5 = r35;
        r8 = r41;
        r14 = r15;
        r15 = r16;
        goto L_0x03b4;
    L_0x0413:
        r19 = r6;
        r3 = r14;
        r7 = r15;
        goto L_0x041c;
    L_0x0418:
        r19 = r6;
        r3 = 0;
        r7 = 0;
    L_0x041c:
        if (r7 <= 0) goto L_0x04b0;
    L_0x041e:
        r5 = new android.widget.FrameLayout;
        r5.<init>(r1);
        r6 = new org.telegram.ui.Cells.CheckBoxCell;
        r8 = 1;
        r6.<init>(r1, r8);
        r1 = 0;
        r10 = org.telegram.ui.ActionBar.Theme.getSelectorDrawable(r1);
        r6.setBackgroundDrawable(r10);
        if (r31 == 0) goto L_0x044a;
    L_0x0433:
        r10 = NUM; // 0x7f0d0369 float:1.8743885E38 double:1.053130209E-314;
        r13 = new java.lang.Object[r8];
        r8 = org.telegram.messenger.UserObject.getFirstName(r35);
        r13[r1] = r8;
        r8 = "DeleteMessagesOptionAlso";
        r8 = org.telegram.messenger.LocaleController.formatString(r8, r10, r13);
        r6.setText(r8, r11, r1, r1);
        r8 = r30;
        goto L_0x046b;
    L_0x044a:
        r8 = r30;
        if (r4 == 0) goto L_0x045f;
    L_0x044e:
        if (r3 != 0) goto L_0x0452;
    L_0x0450:
        if (r7 != r8) goto L_0x045f;
    L_0x0452:
        r10 = NUM; // 0x7f0d0361 float:1.874387E38 double:1.053130205E-314;
        r13 = "DeleteForAll";
        r10 = org.telegram.messenger.LocaleController.getString(r13, r10);
        r6.setText(r10, r11, r1, r1);
        goto L_0x046b;
    L_0x045f:
        r10 = NUM; // 0x7f0d0368 float:1.8743883E38 double:1.0531302084E-314;
        r13 = "DeleteMessagesOption";
        r10 = org.telegram.messenger.LocaleController.getString(r13, r10);
        r6.setText(r10, r11, r1, r1);
    L_0x046b:
        r1 = org.telegram.messenger.LocaleController.isRTL;
        if (r1 == 0) goto L_0x0474;
    L_0x046f:
        r1 = org.telegram.messenger.AndroidUtilities.dp(r17);
        goto L_0x0478;
    L_0x0474:
        r1 = org.telegram.messenger.AndroidUtilities.dp(r18);
    L_0x0478:
        r10 = org.telegram.messenger.LocaleController.isRTL;
        if (r10 == 0) goto L_0x0481;
    L_0x047c:
        r10 = org.telegram.messenger.AndroidUtilities.dp(r18);
        goto L_0x0485;
    L_0x0481:
        r10 = org.telegram.messenger.AndroidUtilities.dp(r17);
    L_0x0485:
        r11 = 0;
        r6.setPadding(r1, r11, r10, r11);
        r22 = -1;
        r23 = NUM; // 0x42400000 float:48.0 double:5.491493014E-315;
        r24 = 51;
        r25 = 0;
        r26 = 0;
        r27 = 0;
        r28 = 0;
        r1 = org.telegram.ui.Components.LayoutHelper.createFrame(r22, r23, r24, r25, r26, r27, r28);
        r5.addView(r6, r1);
        r1 = new org.telegram.ui.Components.-$$Lambda$AlertsCreator$VV1-P4Ux4zkJ-q55i6EgDEwfAZA;
        r1.<init>(r9);
        r6.setOnClickListener(r1);
        r0.setView(r5);
        r1 = 9;
        r0.setCustomViewOffset(r1);
        r1 = 1;
        goto L_0x04bb;
    L_0x04b0:
        r8 = r30;
        r1 = 0;
        goto L_0x04bb;
    L_0x04b4:
        r19 = r6;
        r8 = r30;
        r1 = 0;
        r3 = 0;
        r7 = 0;
    L_0x04bb:
        r25 = 0;
    L_0x04bd:
        r5 = NUM; // 0x7f0d0351 float:1.8743837E38 double:1.053130197E-314;
        r6 = "Delete";
        r5 = org.telegram.messenger.LocaleController.getString(r6, r5);
        r6 = new org.telegram.ui.Components.-$$Lambda$AlertsCreator$4yTgCoGKvK2-DGmNDTg0ZKZlWLU;
        r10 = r19;
        r15 = r6;
        r16 = r41;
        r17 = r43;
        r18 = r37;
        r22 = r9;
        r23 = r44;
        r24 = r42;
        r26 = r12;
        r27 = r36;
        r28 = r38;
        r29 = r46;
        r15.<init>(r16, r17, r18, r19, r20, r22, r23, r24, r25, r26, r27, r28, r29);
        r0.setPositiveButton(r5, r6);
        r5 = "messages";
        r6 = 1;
        if (r8 != r6) goto L_0x04f7;
    L_0x04ea:
        r9 = NUM; // 0x7f0d0371 float:1.8743902E38 double:1.053130213E-314;
        r10 = "DeleteSingleMessagesTitle";
        r9 = org.telegram.messenger.LocaleController.getString(r10, r9);
        r0.setTitle(r9);
        goto L_0x050c;
    L_0x04f7:
        r9 = NUM; // 0x7f0d036d float:1.8743894E38 double:1.053130211E-314;
        r10 = new java.lang.Object[r6];
        r6 = org.telegram.messenger.LocaleController.formatPluralString(r5, r8);
        r11 = 0;
        r10[r11] = r6;
        r6 = "DeleteMessagesTitle";
        r6 = org.telegram.messenger.LocaleController.formatString(r6, r9, r10);
        r0.setTitle(r6);
    L_0x050c:
        r6 = NUM; // 0x7f0d0122 float:1.8742703E38 double:1.053129921E-314;
        r9 = "AreYouSureDeleteSingleMessage";
        r10 = NUM; // 0x7f0d011e float:1.8742695E38 double:1.053129919E-314;
        r11 = "AreYouSureDeleteFewMessages";
        if (r4 == 0) goto L_0x054b;
    L_0x0518:
        if (r3 == 0) goto L_0x054b;
    L_0x051a:
        if (r1 == 0) goto L_0x0536;
    L_0x051c:
        if (r7 == r8) goto L_0x0536;
    L_0x051e:
        r1 = NUM; // 0x7f0d036c float:1.8743891E38 double:1.0531302103E-314;
        r3 = 1;
        r3 = new java.lang.Object[r3];
        r4 = org.telegram.messenger.LocaleController.formatPluralString(r5, r7);
        r5 = 0;
        r3[r5] = r4;
        r4 = "DeleteMessagesTextGroupPart";
        r1 = org.telegram.messenger.LocaleController.formatString(r4, r1, r3);
        r0.setMessage(r1);
        goto L_0x05c1;
    L_0x0536:
        r3 = 1;
        if (r8 != r3) goto L_0x0542;
    L_0x0539:
        r1 = org.telegram.messenger.LocaleController.getString(r9, r6);
        r0.setMessage(r1);
        goto L_0x05c1;
    L_0x0542:
        r1 = org.telegram.messenger.LocaleController.getString(r11, r10);
        r0.setMessage(r1);
        goto L_0x05c1;
    L_0x054b:
        if (r1 == 0) goto L_0x058c;
    L_0x054d:
        if (r31 != 0) goto L_0x058c;
    L_0x054f:
        if (r7 == r8) goto L_0x058c;
    L_0x0551:
        if (r4 == 0) goto L_0x056a;
    L_0x0553:
        r1 = NUM; // 0x7f0d036b float:1.874389E38 double:1.05313021E-314;
        r3 = 1;
        r3 = new java.lang.Object[r3];
        r4 = org.telegram.messenger.LocaleController.formatPluralString(r5, r7);
        r6 = 0;
        r3[r6] = r4;
        r4 = "DeleteMessagesTextGroup";
        r1 = org.telegram.messenger.LocaleController.formatString(r4, r1, r3);
        r0.setMessage(r1);
        goto L_0x05c1;
    L_0x056a:
        r6 = 0;
        r1 = NUM; // 0x7f0d036a float:1.8743887E38 double:1.0531302094E-314;
        r3 = 2;
        r3 = new java.lang.Object[r3];
        r4 = org.telegram.messenger.LocaleController.formatPluralString(r5, r7);
        r3[r6] = r4;
        r4 = org.telegram.messenger.UserObject.getFirstName(r35);
        r5 = 1;
        r3[r5] = r4;
        r4 = "DeleteMessagesText";
        r1 = org.telegram.messenger.LocaleController.formatString(r4, r1, r3);
        r1 = org.telegram.messenger.AndroidUtilities.replaceTags(r1);
        r0.setMessage(r1);
        goto L_0x05c1;
    L_0x058c:
        if (r4 == 0) goto L_0x05af;
    L_0x058e:
        r1 = r4.megagroup;
        if (r1 == 0) goto L_0x05af;
    L_0x0592:
        r1 = 1;
        if (r8 != r1) goto L_0x05a2;
    L_0x0595:
        r1 = NUM; // 0x7f0d0123 float:1.8742705E38 double:1.0531299213E-314;
        r3 = "AreYouSureDeleteSingleMessageMega";
        r1 = org.telegram.messenger.LocaleController.getString(r3, r1);
        r0.setMessage(r1);
        goto L_0x05c1;
    L_0x05a2:
        r1 = NUM; // 0x7f0d011f float:1.8742697E38 double:1.0531299193E-314;
        r3 = "AreYouSureDeleteFewMessagesMega";
        r1 = org.telegram.messenger.LocaleController.getString(r3, r1);
        r0.setMessage(r1);
        goto L_0x05c1;
    L_0x05af:
        r1 = 1;
        if (r8 != r1) goto L_0x05ba;
    L_0x05b2:
        r1 = org.telegram.messenger.LocaleController.getString(r9, r6);
        r0.setMessage(r1);
        goto L_0x05c1;
    L_0x05ba:
        r1 = org.telegram.messenger.LocaleController.getString(r11, r10);
        r0.setMessage(r1);
    L_0x05c1:
        r1 = NUM; // 0x7f0d01f7 float:1.8743135E38 double:1.053130026E-314;
        r3 = "Cancel";
        r1 = org.telegram.messenger.LocaleController.getString(r3, r1);
        r3 = 0;
        r0.setNegativeButton(r1, r3);
        r0 = r0.create();
        r2.showDialog(r0);
        r1 = -1;
        r0 = r0.getButton(r1);
        r0 = (android.widget.TextView) r0;
        if (r0 == 0) goto L_0x05e7;
    L_0x05de:
        r1 = "dialogTextRed2";
        r1 = org.telegram.ui.ActionBar.Theme.getColor(r1);
        r0.setTextColor(r1);
    L_0x05e7:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.AlertsCreator.createDeleteMessagesAlert(org.telegram.ui.ActionBar.BaseFragment, org.telegram.tgnet.TLRPC$User, org.telegram.tgnet.TLRPC$Chat, org.telegram.tgnet.TLRPC$EncryptedChat, org.telegram.tgnet.TLRPC$ChatFull, long, org.telegram.messenger.MessageObject, android.util.SparseArray[], org.telegram.messenger.MessageObject$GroupedMessages, boolean, int, java.lang.Runnable):void");
    }

    static /* synthetic */ void lambda$null$50(AlertDialog[] alertDialogArr, TLObject tLObject, BaseFragment baseFragment, User user, Chat chat, EncryptedChat encryptedChat, ChatFull chatFull, long j, MessageObject messageObject, SparseArray[] sparseArrayArr, GroupedMessages groupedMessages, boolean z, Runnable runnable) {
        int i;
        try {
            alertDialogArr[0].dismiss();
        } catch (Throwable unused) {
        }
        alertDialogArr[0] = null;
        if (tLObject != null) {
            ChannelParticipant channelParticipant = ((TL_channels_channelParticipant) tLObject).participant;
            if (!((channelParticipant instanceof TL_channelParticipantAdmin) || (channelParticipant instanceof TL_channelParticipantCreator))) {
                i = 0;
                createDeleteMessagesAlert(baseFragment, user, chat, encryptedChat, chatFull, j, messageObject, sparseArrayArr, groupedMessages, z, i, runnable);
            }
        }
        i = 2;
        createDeleteMessagesAlert(baseFragment, user, chat, encryptedChat, chatFull, j, messageObject, sparseArrayArr, groupedMessages, z, i, runnable);
    }

    static /* synthetic */ void lambda$createDeleteMessagesAlert$53(AlertDialog[] alertDialogArr, int i, int i2, BaseFragment baseFragment) {
        if (alertDialogArr[0] != null) {
            alertDialogArr[0].setOnCancelListener(new -$$Lambda$AlertsCreator$jpF5yH_8BUaBH_d3vb0aVnNLKR4(i, i2));
            baseFragment.showDialog(alertDialogArr[0]);
        }
    }

    static /* synthetic */ void lambda$createDeleteMessagesAlert$54(boolean[] zArr, View view) {
        if (view.isEnabled()) {
            CheckBoxCell checkBoxCell = (CheckBoxCell) view;
            Integer num = (Integer) checkBoxCell.getTag();
            zArr[num.intValue()] = zArr[num.intValue()] ^ 1;
            checkBoxCell.setChecked(zArr[num.intValue()], true);
        }
    }

    static /* synthetic */ void lambda$createDeleteMessagesAlert$55(boolean[] zArr, View view) {
        CheckBoxCell checkBoxCell = (CheckBoxCell) view;
        zArr[0] = zArr[0] ^ 1;
        checkBoxCell.setChecked(zArr[0], true);
    }

    static /* synthetic */ void lambda$createDeleteMessagesAlert$56(boolean[] zArr, View view) {
        CheckBoxCell checkBoxCell = (CheckBoxCell) view;
        zArr[0] = zArr[0] ^ 1;
        checkBoxCell.setChecked(zArr[0], true);
    }

    /* JADX WARNING: Removed duplicated region for block: B:49:0x0116  */
    /* JADX WARNING: Removed duplicated region for block: B:39:0x00e8  */
    static /* synthetic */ void lambda$createDeleteMessagesAlert$58(org.telegram.messenger.MessageObject r19, org.telegram.messenger.MessageObject.GroupedMessages r20, org.telegram.tgnet.TLRPC.EncryptedChat r21, int r22, long r23, boolean[] r25, boolean r26, android.util.SparseArray[] r27, org.telegram.tgnet.TLRPC.User r28, boolean[] r29, org.telegram.tgnet.TLRPC.Chat r30, org.telegram.tgnet.TLRPC.ChatFull r31, java.lang.Runnable r32, android.content.DialogInterface r33, int r34) {
        /*
        r0 = r19;
        r1 = r20;
        r9 = r28;
        r10 = r30;
        r11 = 10;
        r12 = 0;
        r14 = 0;
        r15 = 1;
        r8 = 0;
        if (r0 == 0) goto L_0x00a0;
    L_0x0011:
        r7 = new java.util.ArrayList;
        r7.<init>();
        if (r1 == 0) goto L_0x0057;
    L_0x0018:
        r2 = 0;
    L_0x0019:
        r3 = r1.messages;
        r3 = r3.size();
        if (r2 >= r3) goto L_0x0082;
    L_0x0021:
        r3 = r1.messages;
        r3 = r3.get(r2);
        r3 = (org.telegram.messenger.MessageObject) r3;
        r4 = r3.getId();
        r4 = java.lang.Integer.valueOf(r4);
        r7.add(r4);
        if (r21 == 0) goto L_0x0054;
    L_0x0036:
        r4 = r3.messageOwner;
        r4 = r4.random_id;
        r6 = (r4 > r12 ? 1 : (r4 == r12 ? 0 : -1));
        if (r6 == 0) goto L_0x0054;
    L_0x003e:
        r4 = r3.type;
        if (r4 == r11) goto L_0x0054;
    L_0x0042:
        if (r14 != 0) goto L_0x0049;
    L_0x0044:
        r14 = new java.util.ArrayList;
        r14.<init>();
    L_0x0049:
        r3 = r3.messageOwner;
        r3 = r3.random_id;
        r3 = java.lang.Long.valueOf(r3);
        r14.add(r3);
    L_0x0054:
        r2 = r2 + 1;
        goto L_0x0019;
    L_0x0057:
        r1 = r19.getId();
        r1 = java.lang.Integer.valueOf(r1);
        r7.add(r1);
        if (r21 == 0) goto L_0x0082;
    L_0x0064:
        r1 = r0.messageOwner;
        r1 = r1.random_id;
        r3 = (r1 > r12 ? 1 : (r1 == r12 ? 0 : -1));
        if (r3 == 0) goto L_0x0082;
    L_0x006c:
        r1 = r0.type;
        if (r1 == r11) goto L_0x0082;
    L_0x0070:
        r1 = new java.util.ArrayList;
        r1.<init>();
        r2 = r0.messageOwner;
        r2 = r2.random_id;
        r2 = java.lang.Long.valueOf(r2);
        r1.add(r2);
        r2 = r1;
        goto L_0x0083;
    L_0x0082:
        r2 = r14;
    L_0x0083:
        r1 = org.telegram.messenger.MessagesController.getInstance(r22);
        r0 = r0.messageOwner;
        r0 = r0.to_id;
        r6 = r0.channel_id;
        r11 = r25[r8];
        r0 = r1;
        r1 = r7;
        r3 = r21;
        r4 = r23;
        r12 = r7;
        r7 = r11;
        r11 = 0;
        r8 = r26;
        r0.deleteMessages(r1, r2, r3, r4, r6, r7, r8);
        r7 = r12;
        goto L_0x013b;
    L_0x00a0:
        r7 = r14;
        r16 = 1;
    L_0x00a3:
        if (r16 < 0) goto L_0x013a;
    L_0x00a5:
        r7 = new java.util.ArrayList;
        r7.<init>();
        r0 = 0;
    L_0x00ab:
        r1 = r27[r16];
        r1 = r1.size();
        if (r0 >= r1) goto L_0x00c3;
    L_0x00b3:
        r1 = r27[r16];
        r1 = r1.keyAt(r0);
        r1 = java.lang.Integer.valueOf(r1);
        r7.add(r1);
        r0 = r0 + 1;
        goto L_0x00ab;
    L_0x00c3:
        r0 = r7.isEmpty();
        if (r0 != 0) goto L_0x00e5;
    L_0x00c9:
        r0 = r27[r16];
        r1 = r7.get(r8);
        r1 = (java.lang.Integer) r1;
        r1 = r1.intValue();
        r0 = r0.get(r1);
        r0 = (org.telegram.messenger.MessageObject) r0;
        r0 = r0.messageOwner;
        r0 = r0.to_id;
        r0 = r0.channel_id;
        if (r0 == 0) goto L_0x00e5;
    L_0x00e3:
        r6 = r0;
        goto L_0x00e6;
    L_0x00e5:
        r6 = 0;
    L_0x00e6:
        if (r21 == 0) goto L_0x0116;
    L_0x00e8:
        r0 = new java.util.ArrayList;
        r0.<init>();
        r1 = 0;
    L_0x00ee:
        r2 = r27[r16];
        r2 = r2.size();
        if (r1 >= r2) goto L_0x0114;
    L_0x00f6:
        r2 = r27[r16];
        r2 = r2.valueAt(r1);
        r2 = (org.telegram.messenger.MessageObject) r2;
        r3 = r2.messageOwner;
        r3 = r3.random_id;
        r5 = (r3 > r12 ? 1 : (r3 == r12 ? 0 : -1));
        if (r5 == 0) goto L_0x0111;
    L_0x0106:
        r2 = r2.type;
        if (r2 == r11) goto L_0x0111;
    L_0x010a:
        r2 = java.lang.Long.valueOf(r3);
        r0.add(r2);
    L_0x0111:
        r1 = r1 + 1;
        goto L_0x00ee;
    L_0x0114:
        r2 = r0;
        goto L_0x0117;
    L_0x0116:
        r2 = r14;
    L_0x0117:
        r0 = org.telegram.messenger.MessagesController.getInstance(r22);
        r17 = r25[r8];
        r1 = r7;
        r3 = r21;
        r4 = r23;
        r18 = r7;
        r7 = r17;
        r11 = 0;
        r8 = r26;
        r0.deleteMessages(r1, r2, r3, r4, r6, r7, r8);
        r0 = r27[r16];
        r0.clear();
        r16 = r16 + -1;
        r7 = r18;
        r8 = 0;
        r11 = 10;
        goto L_0x00a3;
    L_0x013a:
        r11 = 0;
    L_0x013b:
        if (r9 == 0) goto L_0x017c;
    L_0x013d:
        r0 = r29[r11];
        if (r0 == 0) goto L_0x014c;
    L_0x0141:
        r0 = org.telegram.messenger.MessagesController.getInstance(r22);
        r1 = r10.id;
        r2 = r31;
        r0.deleteUserFromChat(r1, r9, r2);
    L_0x014c:
        r0 = r29[r15];
        if (r0 == 0) goto L_0x0170;
    L_0x0150:
        r0 = new org.telegram.tgnet.TLRPC$TL_channels_reportSpam;
        r0.<init>();
        r1 = org.telegram.messenger.MessagesController.getInputChannel(r30);
        r0.channel = r1;
        r1 = org.telegram.messenger.MessagesController.getInstance(r22);
        r1 = r1.getInputUser(r9);
        r0.user_id = r1;
        r0.id = r7;
        r1 = org.telegram.tgnet.ConnectionsManager.getInstance(r22);
        r2 = org.telegram.ui.Components.-$$Lambda$AlertsCreator$tYCa1lgn0riGcq9hDcB5xxGo_bI.INSTANCE;
        r1.sendRequest(r0, r2);
    L_0x0170:
        r0 = 2;
        r0 = r29[r0];
        if (r0 == 0) goto L_0x017c;
    L_0x0175:
        r0 = org.telegram.messenger.MessagesController.getInstance(r22);
        r0.deleteUserChannelHistory(r10, r9, r11);
    L_0x017c:
        if (r32 == 0) goto L_0x0181;
    L_0x017e:
        r32.run();
    L_0x0181:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.AlertsCreator.lambda$createDeleteMessagesAlert$58(org.telegram.messenger.MessageObject, org.telegram.messenger.MessageObject$GroupedMessages, org.telegram.tgnet.TLRPC$EncryptedChat, int, long, boolean[], boolean, android.util.SparseArray[], org.telegram.tgnet.TLRPC$User, boolean[], org.telegram.tgnet.TLRPC$Chat, org.telegram.tgnet.TLRPC$ChatFull, java.lang.Runnable, android.content.DialogInterface, int):void");
    }
}
