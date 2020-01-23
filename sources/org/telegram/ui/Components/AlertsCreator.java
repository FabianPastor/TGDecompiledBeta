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
import android.os.Vibrator;
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
import org.telegram.messenger.NotificationCenter;
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
import org.telegram.ui.ActionBar.Theme.ThemeAccent;
import org.telegram.ui.ActionBar.Theme.ThemeInfo;
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
import org.telegram.ui.ThemePreviewActivity;

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

    static /* synthetic */ void lambda$createThemeCreateDialog$61(DialogInterface dialogInterface, int i) {
    }

    static /* synthetic */ void lambda$null$31(TLObject tLObject, TL_error tL_error) {
    }

    static /* synthetic */ void lambda$null$59(TLObject tLObject, TL_error tL_error) {
    }

    /* JADX WARNING: Removed duplicated region for block: B:271:0x054a  */
    /* JADX WARNING: Removed duplicated region for block: B:266:0x0520  */
    /* JADX WARNING: Removed duplicated region for block: B:266:0x0520  */
    /* JADX WARNING: Removed duplicated region for block: B:271:0x054a  */
    /* JADX WARNING: Removed duplicated region for block: B:271:0x054a  */
    /* JADX WARNING: Removed duplicated region for block: B:266:0x0520  */
    /* JADX WARNING: Removed duplicated region for block: B:181:0x0387  */
    /* JADX WARNING: Removed duplicated region for block: B:177:0x036a  */
    /* JADX WARNING: Removed duplicated region for block: B:177:0x036a  */
    /* JADX WARNING: Removed duplicated region for block: B:181:0x0387  */
    /* JADX WARNING: Removed duplicated region for block: B:211:0x041a  */
    /* JADX WARNING: Removed duplicated region for block: B:207:0x0402  */
    /* JADX WARNING: Removed duplicated region for block: B:207:0x0402  */
    /* JADX WARNING: Removed duplicated region for block: B:211:0x041a  */
    public static android.app.Dialog processError(int r18, org.telegram.tgnet.TLRPC.TL_error r19, org.telegram.ui.ActionBar.BaseFragment r20, org.telegram.tgnet.TLObject r21, java.lang.Object... r22) {
        /*
        r0 = r19;
        r1 = r20;
        r2 = r21;
        r3 = r22;
        r4 = r0.code;
        r5 = 0;
        r6 = 406; // 0x196 float:5.69E-43 double:2.006E-321;
        if (r4 == r6) goto L_0x061b;
    L_0x000f:
        r6 = r0.text;
        if (r6 != 0) goto L_0x0015;
    L_0x0013:
        goto L_0x061b;
    L_0x0015:
        r7 = r2 instanceof org.telegram.tgnet.TLRPC.TL_account_saveSecureValue;
        r8 = "\n";
        r10 = "InvalidPhoneNumber";
        r11 = "PHONE_NUMBER_INVALID";
        r13 = "ErrorOccurred";
        r15 = "FloodWait";
        r9 = "FLOOD_WAIT";
        if (r7 != 0) goto L_0x05bb;
    L_0x0025:
        r7 = r2 instanceof org.telegram.tgnet.TLRPC.TL_account_getAuthorizationForm;
        if (r7 == 0) goto L_0x002b;
    L_0x0029:
        goto L_0x05bb;
    L_0x002b:
        r7 = r2 instanceof org.telegram.tgnet.TLRPC.TL_channels_joinChannel;
        r12 = "CHANNELS_TOO_MUCH";
        if (r7 != 0) goto L_0x055f;
    L_0x0031:
        r14 = r2 instanceof org.telegram.tgnet.TLRPC.TL_channels_editAdmin;
        if (r14 != 0) goto L_0x055f;
    L_0x0035:
        r14 = r2 instanceof org.telegram.tgnet.TLRPC.TL_channels_inviteToChannel;
        if (r14 != 0) goto L_0x055f;
    L_0x0039:
        r14 = r2 instanceof org.telegram.tgnet.TLRPC.TL_messages_addChatUser;
        if (r14 != 0) goto L_0x055f;
    L_0x003d:
        r14 = r2 instanceof org.telegram.tgnet.TLRPC.TL_messages_startBot;
        if (r14 != 0) goto L_0x055f;
    L_0x0041:
        r14 = r2 instanceof org.telegram.tgnet.TLRPC.TL_channels_editBanned;
        if (r14 != 0) goto L_0x055f;
    L_0x0045:
        r14 = r2 instanceof org.telegram.tgnet.TLRPC.TL_messages_editChatDefaultBannedRights;
        if (r14 != 0) goto L_0x055f;
    L_0x0049:
        r14 = r2 instanceof org.telegram.tgnet.TLRPC.TL_messages_editChatAdmin;
        if (r14 != 0) goto L_0x055f;
    L_0x004d:
        r14 = r2 instanceof org.telegram.tgnet.TLRPC.TL_messages_migrateChat;
        if (r14 == 0) goto L_0x0053;
    L_0x0051:
        goto L_0x055f;
    L_0x0053:
        r7 = r2 instanceof org.telegram.tgnet.TLRPC.TL_messages_createChat;
        r14 = 2;
        if (r7 == 0) goto L_0x007e;
    L_0x0058:
        r3 = r6.equals(r12);
        if (r3 == 0) goto L_0x0067;
    L_0x005e:
        r0 = new org.telegram.ui.TooManyCommunitiesActivity;
        r0.<init>(r14);
        r1.presentFragment(r0);
        return r5;
    L_0x0067:
        r3 = r0.text;
        r3 = r3.startsWith(r9);
        if (r3 == 0) goto L_0x0076;
    L_0x006f:
        r0 = r0.text;
        showFloodWaitAlert(r0, r1);
        goto L_0x061b;
    L_0x0076:
        r0 = r0.text;
        r3 = 0;
        showAddUserAlert(r0, r1, r3, r2);
        goto L_0x061b;
    L_0x007e:
        r7 = r2 instanceof org.telegram.tgnet.TLRPC.TL_channels_createChannel;
        if (r7 == 0) goto L_0x00a8;
    L_0x0082:
        r3 = r6.equals(r12);
        if (r3 == 0) goto L_0x0091;
    L_0x0088:
        r0 = new org.telegram.ui.TooManyCommunitiesActivity;
        r0.<init>(r14);
        r1.presentFragment(r0);
        return r5;
    L_0x0091:
        r3 = r0.text;
        r3 = r3.startsWith(r9);
        if (r3 == 0) goto L_0x00a0;
    L_0x0099:
        r0 = r0.text;
        showFloodWaitAlert(r0, r1);
        goto L_0x061b;
    L_0x00a0:
        r0 = r0.text;
        r3 = 0;
        showAddUserAlert(r0, r1, r3, r2);
        goto L_0x061b;
    L_0x00a8:
        r7 = r2 instanceof org.telegram.tgnet.TLRPC.TL_messages_editMessage;
        if (r7 == 0) goto L_0x00d2;
    L_0x00ac:
        r0 = "MESSAGE_NOT_MODIFIED";
        r0 = r6.equals(r0);
        if (r0 != 0) goto L_0x061b;
    L_0x00b4:
        if (r1 == 0) goto L_0x00c4;
    L_0x00b6:
        r0 = NUM; // 0x7f0e0413 float:1.8877153E38 double:1.053162672E-314;
        r2 = "EditMessageError";
        r0 = org.telegram.messenger.LocaleController.getString(r2, r0);
        showSimpleAlert(r1, r0);
        goto L_0x061b;
    L_0x00c4:
        r0 = NUM; // 0x7f0e0413 float:1.8877153E38 double:1.053162672E-314;
        r2 = "EditMessageError";
        r0 = org.telegram.messenger.LocaleController.getString(r2, r0);
        showSimpleToast(r1, r0);
        goto L_0x061b;
    L_0x00d2:
        r7 = r2 instanceof org.telegram.tgnet.TLRPC.TL_messages_sendMessage;
        r17 = -1;
        if (r7 != 0) goto L_0x04e9;
    L_0x00d8:
        r7 = r2 instanceof org.telegram.tgnet.TLRPC.TL_messages_sendMedia;
        if (r7 != 0) goto L_0x04e9;
    L_0x00dc:
        r7 = r2 instanceof org.telegram.tgnet.TLRPC.TL_messages_sendInlineBotResult;
        if (r7 != 0) goto L_0x04e9;
    L_0x00e0:
        r7 = r2 instanceof org.telegram.tgnet.TLRPC.TL_messages_forwardMessages;
        if (r7 != 0) goto L_0x04e9;
    L_0x00e4:
        r7 = r2 instanceof org.telegram.tgnet.TLRPC.TL_messages_sendMultiMedia;
        if (r7 != 0) goto L_0x04e9;
    L_0x00e8:
        r7 = r2 instanceof org.telegram.tgnet.TLRPC.TL_messages_sendScheduledMessages;
        if (r7 == 0) goto L_0x00ee;
    L_0x00ec:
        goto L_0x04e9;
    L_0x00ee:
        r7 = r2 instanceof org.telegram.tgnet.TLRPC.TL_messages_importChatInvite;
        if (r7 == 0) goto L_0x013d;
    L_0x00f2:
        r2 = r6.startsWith(r9);
        if (r2 == 0) goto L_0x0104;
    L_0x00f8:
        r2 = NUM; // 0x7f0e04ca float:1.8877524E38 double:1.0531627624E-314;
        r0 = org.telegram.messenger.LocaleController.getString(r15, r2);
        showSimpleAlert(r1, r0);
        goto L_0x061b;
    L_0x0104:
        r2 = r0.text;
        r3 = "USERS_TOO_MUCH";
        r2 = r2.equals(r3);
        if (r2 == 0) goto L_0x011c;
    L_0x010e:
        r0 = NUM; // 0x7f0e05aa float:1.8877978E38 double:1.053162873E-314;
        r2 = "JoinToGroupErrorFull";
        r0 = org.telegram.messenger.LocaleController.getString(r2, r0);
        showSimpleAlert(r1, r0);
        goto L_0x061b;
    L_0x011c:
        r0 = r0.text;
        r0 = r0.equals(r12);
        if (r0 == 0) goto L_0x012f;
    L_0x0124:
        r0 = new org.telegram.ui.TooManyCommunitiesActivity;
        r2 = 0;
        r0.<init>(r2);
        r1.presentFragment(r0);
        goto L_0x061b;
    L_0x012f:
        r0 = NUM; // 0x7f0e05ab float:1.887798E38 double:1.0531628735E-314;
        r2 = "JoinToGroupErrorNotExist";
        r0 = org.telegram.messenger.LocaleController.getString(r2, r0);
        showSimpleAlert(r1, r0);
        goto L_0x061b;
    L_0x013d:
        r7 = r2 instanceof org.telegram.tgnet.TLRPC.TL_messages_getAttachedStickers;
        if (r7 == 0) goto L_0x0172;
    L_0x0141:
        if (r1 == 0) goto L_0x061b;
    L_0x0143:
        r2 = r20.getParentActivity();
        if (r2 == 0) goto L_0x061b;
    L_0x0149:
        r1 = r20.getParentActivity();
        r2 = new java.lang.StringBuilder;
        r2.<init>();
        r3 = NUM; // 0x7f0e0448 float:1.887726E38 double:1.053162698E-314;
        r3 = org.telegram.messenger.LocaleController.getString(r13, r3);
        r2.append(r3);
        r2.append(r8);
        r0 = r0.text;
        r2.append(r0);
        r0 = r2.toString();
        r2 = 0;
        r0 = android.widget.Toast.makeText(r1, r0, r2);
        r0.show();
        goto L_0x061b;
    L_0x0172:
        r7 = r2 instanceof org.telegram.tgnet.TLRPC.TL_account_confirmPhone;
        if (r7 != 0) goto L_0x0474;
    L_0x0176:
        r7 = r2 instanceof org.telegram.tgnet.TLRPC.TL_account_verifyPhone;
        if (r7 != 0) goto L_0x0474;
    L_0x017a:
        r7 = r2 instanceof org.telegram.tgnet.TLRPC.TL_account_verifyEmail;
        if (r7 == 0) goto L_0x0180;
    L_0x017e:
        goto L_0x0474;
    L_0x0180:
        r7 = r2 instanceof org.telegram.tgnet.TLRPC.TL_auth_resendCode;
        if (r7 == 0) goto L_0x020b;
    L_0x0184:
        r2 = r6.contains(r11);
        if (r2 == 0) goto L_0x0196;
    L_0x018a:
        r2 = NUM; // 0x7f0e058c float:1.8877918E38 double:1.053162858E-314;
        r0 = org.telegram.messenger.LocaleController.getString(r10, r2);
        r0 = showSimpleAlert(r1, r0);
        return r0;
    L_0x0196:
        r2 = r0.text;
        r3 = "PHONE_CODE_EMPTY";
        r2 = r2.contains(r3);
        if (r2 != 0) goto L_0x01fd;
    L_0x01a0:
        r2 = r0.text;
        r3 = "PHONE_CODE_INVALID";
        r2 = r2.contains(r3);
        if (r2 == 0) goto L_0x01ab;
    L_0x01aa:
        goto L_0x01fd;
    L_0x01ab:
        r2 = r0.text;
        r3 = "PHONE_CODE_EXPIRED";
        r2 = r2.contains(r3);
        if (r2 == 0) goto L_0x01c3;
    L_0x01b5:
        r0 = NUM; // 0x7f0e030f float:1.8876626E38 double:1.0531625435E-314;
        r2 = "CodeExpired";
        r0 = org.telegram.messenger.LocaleController.getString(r2, r0);
        r0 = showSimpleAlert(r1, r0);
        return r0;
    L_0x01c3:
        r2 = r0.text;
        r2 = r2.startsWith(r9);
        if (r2 == 0) goto L_0x01d7;
    L_0x01cb:
        r2 = NUM; // 0x7f0e04ca float:1.8877524E38 double:1.0531627624E-314;
        r0 = org.telegram.messenger.LocaleController.getString(r15, r2);
        r0 = showSimpleAlert(r1, r0);
        return r0;
    L_0x01d7:
        r2 = r0.code;
        r3 = -1000; // 0xfffffffffffffCLASSNAME float:NaN double:NaN;
        if (r2 == r3) goto L_0x061b;
    L_0x01dd:
        r2 = new java.lang.StringBuilder;
        r2.<init>();
        r3 = NUM; // 0x7f0e0448 float:1.887726E38 double:1.053162698E-314;
        r3 = org.telegram.messenger.LocaleController.getString(r13, r3);
        r2.append(r3);
        r2.append(r8);
        r0 = r0.text;
        r2.append(r0);
        r0 = r2.toString();
        r0 = showSimpleAlert(r1, r0);
        return r0;
    L_0x01fd:
        r0 = NUM; // 0x7f0e0589 float:1.8877912E38 double:1.0531628567E-314;
        r2 = "InvalidCode";
        r0 = org.telegram.messenger.LocaleController.getString(r2, r0);
        r0 = showSimpleAlert(r1, r0);
        return r0;
    L_0x020b:
        r7 = r2 instanceof org.telegram.tgnet.TLRPC.TL_account_sendConfirmPhoneCode;
        if (r7 == 0) goto L_0x0241;
    L_0x020f:
        r0 = 400; // 0x190 float:5.6E-43 double:1.976E-321;
        if (r4 != r0) goto L_0x0221;
    L_0x0213:
        r0 = NUM; // 0x7f0e0221 float:1.8876143E38 double:1.053162426E-314;
        r2 = "CancelLinkExpired";
        r0 = org.telegram.messenger.LocaleController.getString(r2, r0);
        r0 = showSimpleAlert(r1, r0);
        return r0;
    L_0x0221:
        if (r6 == 0) goto L_0x061b;
    L_0x0223:
        r0 = r6.startsWith(r9);
        if (r0 == 0) goto L_0x0235;
    L_0x0229:
        r0 = NUM; // 0x7f0e04ca float:1.8877524E38 double:1.0531627624E-314;
        r0 = org.telegram.messenger.LocaleController.getString(r15, r0);
        r0 = showSimpleAlert(r1, r0);
        return r0;
    L_0x0235:
        r0 = NUM; // 0x7f0e0448 float:1.887726E38 double:1.053162698E-314;
        r0 = org.telegram.messenger.LocaleController.getString(r13, r0);
        r0 = showSimpleAlert(r1, r0);
        return r0;
    L_0x0241:
        r4 = r2 instanceof org.telegram.tgnet.TLRPC.TL_account_changePhone;
        if (r4 == 0) goto L_0x02ad;
    L_0x0245:
        r2 = r6.contains(r11);
        if (r2 == 0) goto L_0x0257;
    L_0x024b:
        r2 = NUM; // 0x7f0e058c float:1.8877918E38 double:1.053162858E-314;
        r0 = org.telegram.messenger.LocaleController.getString(r10, r2);
        showSimpleAlert(r1, r0);
        goto L_0x061b;
    L_0x0257:
        r2 = r0.text;
        r3 = "PHONE_CODE_EMPTY";
        r2 = r2.contains(r3);
        if (r2 != 0) goto L_0x029f;
    L_0x0261:
        r2 = r0.text;
        r3 = "PHONE_CODE_INVALID";
        r2 = r2.contains(r3);
        if (r2 == 0) goto L_0x026c;
    L_0x026b:
        goto L_0x029f;
    L_0x026c:
        r2 = r0.text;
        r3 = "PHONE_CODE_EXPIRED";
        r2 = r2.contains(r3);
        if (r2 == 0) goto L_0x0284;
    L_0x0276:
        r0 = NUM; // 0x7f0e030f float:1.8876626E38 double:1.0531625435E-314;
        r2 = "CodeExpired";
        r0 = org.telegram.messenger.LocaleController.getString(r2, r0);
        showSimpleAlert(r1, r0);
        goto L_0x061b;
    L_0x0284:
        r2 = r0.text;
        r2 = r2.startsWith(r9);
        if (r2 == 0) goto L_0x0298;
    L_0x028c:
        r2 = NUM; // 0x7f0e04ca float:1.8877524E38 double:1.0531627624E-314;
        r0 = org.telegram.messenger.LocaleController.getString(r15, r2);
        showSimpleAlert(r1, r0);
        goto L_0x061b;
    L_0x0298:
        r0 = r0.text;
        showSimpleAlert(r1, r0);
        goto L_0x061b;
    L_0x029f:
        r0 = NUM; // 0x7f0e0589 float:1.8877912E38 double:1.0531628567E-314;
        r2 = "InvalidCode";
        r0 = org.telegram.messenger.LocaleController.getString(r2, r0);
        showSimpleAlert(r1, r0);
        goto L_0x061b;
    L_0x02ad:
        r4 = r2 instanceof org.telegram.tgnet.TLRPC.TL_account_sendChangePhoneCode;
        if (r4 == 0) goto L_0x0340;
    L_0x02b1:
        r2 = r6.contains(r11);
        if (r2 == 0) goto L_0x02c3;
    L_0x02b7:
        r2 = NUM; // 0x7f0e058c float:1.8877918E38 double:1.053162858E-314;
        r0 = org.telegram.messenger.LocaleController.getString(r10, r2);
        showSimpleAlert(r1, r0);
        goto L_0x061b;
    L_0x02c3:
        r2 = r0.text;
        r4 = "PHONE_CODE_EMPTY";
        r2 = r2.contains(r4);
        if (r2 != 0) goto L_0x0332;
    L_0x02cd:
        r2 = r0.text;
        r4 = "PHONE_CODE_INVALID";
        r2 = r2.contains(r4);
        if (r2 == 0) goto L_0x02d8;
    L_0x02d7:
        goto L_0x0332;
    L_0x02d8:
        r2 = r0.text;
        r4 = "PHONE_CODE_EXPIRED";
        r2 = r2.contains(r4);
        if (r2 == 0) goto L_0x02f0;
    L_0x02e2:
        r0 = NUM; // 0x7f0e030f float:1.8876626E38 double:1.0531625435E-314;
        r2 = "CodeExpired";
        r0 = org.telegram.messenger.LocaleController.getString(r2, r0);
        showSimpleAlert(r1, r0);
        goto L_0x061b;
    L_0x02f0:
        r2 = r0.text;
        r2 = r2.startsWith(r9);
        if (r2 == 0) goto L_0x0304;
    L_0x02f8:
        r2 = NUM; // 0x7f0e04ca float:1.8877524E38 double:1.0531627624E-314;
        r0 = org.telegram.messenger.LocaleController.getString(r15, r2);
        showSimpleAlert(r1, r0);
        goto L_0x061b;
    L_0x0304:
        r0 = r0.text;
        r2 = "PHONE_NUMBER_OCCUPIED";
        r0 = r0.startsWith(r2);
        if (r0 == 0) goto L_0x0326;
    L_0x030e:
        r0 = NUM; // 0x7f0e0236 float:1.8876186E38 double:1.0531624363E-314;
        r2 = 1;
        r2 = new java.lang.Object[r2];
        r4 = 0;
        r3 = r3[r4];
        r3 = (java.lang.String) r3;
        r2[r4] = r3;
        r3 = "ChangePhoneNumberOccupied";
        r0 = org.telegram.messenger.LocaleController.formatString(r3, r0, r2);
        showSimpleAlert(r1, r0);
        goto L_0x061b;
    L_0x0326:
        r0 = NUM; // 0x7f0e0448 float:1.887726E38 double:1.053162698E-314;
        r0 = org.telegram.messenger.LocaleController.getString(r13, r0);
        showSimpleAlert(r1, r0);
        goto L_0x061b;
    L_0x0332:
        r0 = NUM; // 0x7f0e0589 float:1.8877912E38 double:1.0531628567E-314;
        r2 = "InvalidCode";
        r0 = org.telegram.messenger.LocaleController.getString(r2, r0);
        showSimpleAlert(r1, r0);
        goto L_0x061b;
    L_0x0340:
        r3 = r2 instanceof org.telegram.tgnet.TLRPC.TL_updateUserName;
        if (r3 == 0) goto L_0x0395;
    L_0x0344:
        r0 = r6.hashCode();
        r2 = NUM; // 0x1137676e float:1.4468026E-28 double:1.427077146E-315;
        if (r0 == r2) goto L_0x035d;
    L_0x034d:
        r2 = NUM; // 0x1fCLASSNAMEbe7 float:8.45377E-20 double:2.634235846E-315;
        if (r0 == r2) goto L_0x0353;
    L_0x0352:
        goto L_0x0367;
    L_0x0353:
        r0 = "USERNAME_OCCUPIED";
        r0 = r6.equals(r0);
        if (r0 == 0) goto L_0x0367;
    L_0x035b:
        r0 = 1;
        goto L_0x0368;
    L_0x035d:
        r0 = "USERNAME_INVALID";
        r0 = r6.equals(r0);
        if (r0 == 0) goto L_0x0367;
    L_0x0365:
        r0 = 0;
        goto L_0x0368;
    L_0x0367:
        r0 = -1;
    L_0x0368:
        if (r0 == 0) goto L_0x0387;
    L_0x036a:
        r2 = 1;
        if (r0 == r2) goto L_0x0379;
    L_0x036d:
        r0 = NUM; // 0x7f0e0448 float:1.887726E38 double:1.053162698E-314;
        r0 = org.telegram.messenger.LocaleController.getString(r13, r0);
        showSimpleAlert(r1, r0);
        goto L_0x061b;
    L_0x0379:
        r0 = NUM; // 0x7f0e0bcb float:1.888116E38 double:1.053163648E-314;
        r2 = "UsernameInUse";
        r0 = org.telegram.messenger.LocaleController.getString(r2, r0);
        showSimpleAlert(r1, r0);
        goto L_0x061b;
    L_0x0387:
        r0 = NUM; // 0x7f0e0bcc float:1.8881163E38 double:1.0531636487E-314;
        r2 = "UsernameInvalid";
        r0 = org.telegram.messenger.LocaleController.getString(r2, r0);
        showSimpleAlert(r1, r0);
        goto L_0x061b;
    L_0x0395:
        r3 = r2 instanceof org.telegram.tgnet.TLRPC.TL_contacts_importContacts;
        if (r3 == 0) goto L_0x03ce;
    L_0x0399:
        if (r0 == 0) goto L_0x03c2;
    L_0x039b:
        r2 = r6.startsWith(r9);
        if (r2 == 0) goto L_0x03a2;
    L_0x03a1:
        goto L_0x03c2;
    L_0x03a2:
        r2 = new java.lang.StringBuilder;
        r2.<init>();
        r3 = NUM; // 0x7f0e0448 float:1.887726E38 double:1.053162698E-314;
        r3 = org.telegram.messenger.LocaleController.getString(r13, r3);
        r2.append(r3);
        r2.append(r8);
        r0 = r0.text;
        r2.append(r0);
        r0 = r2.toString();
        showSimpleAlert(r1, r0);
        goto L_0x061b;
    L_0x03c2:
        r0 = NUM; // 0x7f0e04ca float:1.8877524E38 double:1.0531627624E-314;
        r0 = org.telegram.messenger.LocaleController.getString(r15, r0);
        showSimpleAlert(r1, r0);
        goto L_0x061b;
    L_0x03ce:
        r3 = r2 instanceof org.telegram.tgnet.TLRPC.TL_account_getPassword;
        if (r3 != 0) goto L_0x045a;
    L_0x03d2:
        r3 = r2 instanceof org.telegram.tgnet.TLRPC.TL_account_getTmpPassword;
        if (r3 == 0) goto L_0x03d8;
    L_0x03d6:
        goto L_0x045a;
    L_0x03d8:
        r3 = r2 instanceof org.telegram.tgnet.TLRPC.TL_payments_sendPaymentForm;
        if (r3 == 0) goto L_0x0428;
    L_0x03dc:
        r2 = r6.hashCode();
        r3 = -NUM; // 0xffffffffbbcefe0b float:-0.NUM double:NaN;
        if (r2 == r3) goto L_0x03f5;
    L_0x03e5:
        r3 = -NUM; // 0xffffffffd14178b6 float:-5.1934618E10 double:NaN;
        if (r2 == r3) goto L_0x03eb;
    L_0x03ea:
        goto L_0x03ff;
    L_0x03eb:
        r2 = "PAYMENT_FAILED";
        r2 = r6.equals(r2);
        if (r2 == 0) goto L_0x03ff;
    L_0x03f3:
        r2 = 1;
        goto L_0x0400;
    L_0x03f5:
        r2 = "BOT_PRECHECKOUT_FAILED";
        r2 = r6.equals(r2);
        if (r2 == 0) goto L_0x03ff;
    L_0x03fd:
        r2 = 0;
        goto L_0x0400;
    L_0x03ff:
        r2 = -1;
    L_0x0400:
        if (r2 == 0) goto L_0x041a;
    L_0x0402:
        r3 = 1;
        if (r2 == r3) goto L_0x040c;
    L_0x0405:
        r0 = r0.text;
        showSimpleToast(r1, r0);
        goto L_0x061b;
    L_0x040c:
        r0 = NUM; // 0x7f0e088e float:1.887948E38 double:1.0531632386E-314;
        r2 = "PaymentFailed";
        r0 = org.telegram.messenger.LocaleController.getString(r2, r0);
        showSimpleToast(r1, r0);
        goto L_0x061b;
    L_0x041a:
        r0 = NUM; // 0x7f0e089b float:1.8879506E38 double:1.053163245E-314;
        r2 = "PaymentPrecheckoutFailed";
        r0 = org.telegram.messenger.LocaleController.getString(r2, r0);
        showSimpleToast(r1, r0);
        goto L_0x061b;
    L_0x0428:
        r2 = r2 instanceof org.telegram.tgnet.TLRPC.TL_payments_validateRequestedInfo;
        if (r2 == 0) goto L_0x061b;
    L_0x042c:
        r2 = r6.hashCode();
        r3 = NUM; // 0x68CLASSNAMEc float:7.606448E24 double:8.68580028E-315;
        if (r2 == r3) goto L_0x0436;
    L_0x0435:
        goto L_0x0441;
    L_0x0436:
        r2 = "SHIPPING_NOT_AVAILABLE";
        r2 = r6.equals(r2);
        if (r2 == 0) goto L_0x0441;
    L_0x043e:
        r16 = 0;
        goto L_0x0443;
    L_0x0441:
        r16 = -1;
    L_0x0443:
        if (r16 == 0) goto L_0x044c;
    L_0x0445:
        r0 = r0.text;
        showSimpleToast(r1, r0);
        goto L_0x061b;
    L_0x044c:
        r0 = NUM; // 0x7f0e0890 float:1.8879483E38 double:1.0531632396E-314;
        r2 = "PaymentNoShippingMethod";
        r0 = org.telegram.messenger.LocaleController.getString(r2, r0);
        showSimpleToast(r1, r0);
        goto L_0x061b;
    L_0x045a:
        r2 = r0.text;
        r2 = r2.startsWith(r9);
        if (r2 == 0) goto L_0x046d;
    L_0x0462:
        r0 = r0.text;
        r0 = getFloodWaitString(r0);
        showSimpleToast(r1, r0);
        goto L_0x061b;
    L_0x046d:
        r0 = r0.text;
        showSimpleToast(r1, r0);
        goto L_0x061b;
    L_0x0474:
        r2 = r0.text;
        r3 = "PHONE_CODE_EMPTY";
        r2 = r2.contains(r3);
        if (r2 != 0) goto L_0x04db;
    L_0x047e:
        r2 = r0.text;
        r3 = "PHONE_CODE_INVALID";
        r2 = r2.contains(r3);
        if (r2 != 0) goto L_0x04db;
    L_0x0488:
        r2 = r0.text;
        r3 = "CODE_INVALID";
        r2 = r2.contains(r3);
        if (r2 != 0) goto L_0x04db;
    L_0x0492:
        r2 = r0.text;
        r3 = "CODE_EMPTY";
        r2 = r2.contains(r3);
        if (r2 == 0) goto L_0x049d;
    L_0x049c:
        goto L_0x04db;
    L_0x049d:
        r2 = r0.text;
        r3 = "PHONE_CODE_EXPIRED";
        r2 = r2.contains(r3);
        if (r2 != 0) goto L_0x04cd;
    L_0x04a7:
        r2 = r0.text;
        r3 = "EMAIL_VERIFY_EXPIRED";
        r2 = r2.contains(r3);
        if (r2 == 0) goto L_0x04b2;
    L_0x04b1:
        goto L_0x04cd;
    L_0x04b2:
        r2 = r0.text;
        r2 = r2.startsWith(r9);
        if (r2 == 0) goto L_0x04c6;
    L_0x04ba:
        r2 = NUM; // 0x7f0e04ca float:1.8877524E38 double:1.0531627624E-314;
        r0 = org.telegram.messenger.LocaleController.getString(r15, r2);
        r0 = showSimpleAlert(r1, r0);
        return r0;
    L_0x04c6:
        r0 = r0.text;
        r0 = showSimpleAlert(r1, r0);
        return r0;
    L_0x04cd:
        r0 = NUM; // 0x7f0e030f float:1.8876626E38 double:1.0531625435E-314;
        r2 = "CodeExpired";
        r0 = org.telegram.messenger.LocaleController.getString(r2, r0);
        r0 = showSimpleAlert(r1, r0);
        return r0;
    L_0x04db:
        r0 = NUM; // 0x7f0e0589 float:1.8877912E38 double:1.0531628567E-314;
        r2 = "InvalidCode";
        r0 = org.telegram.messenger.LocaleController.getString(r2, r0);
        r0 = showSimpleAlert(r1, r0);
        return r0;
    L_0x04e9:
        r0 = r0.text;
        r2 = r0.hashCode();
        r3 = -NUM; // 0xfffffffvar_b816 float:-8.417163E-27 double:NaN;
        if (r2 == r3) goto L_0x0513;
    L_0x04f4:
        r3 = -NUM; // 0xffffffffe4efe6c1 float:-3.5403195E22 double:NaN;
        if (r2 == r3) goto L_0x0509;
    L_0x04f9:
        r3 = NUM; // 0x45b984e0 float:5936.6094 double:5.77951115E-315;
        if (r2 == r3) goto L_0x04ff;
    L_0x04fe:
        goto L_0x051d;
    L_0x04ff:
        r2 = "SCHEDULE_TOO_MUCH";
        r0 = r0.equals(r2);
        if (r0 == 0) goto L_0x051d;
    L_0x0507:
        r0 = 2;
        goto L_0x051e;
    L_0x0509:
        r2 = "PEER_FLOOD";
        r0 = r0.equals(r2);
        if (r0 == 0) goto L_0x051d;
    L_0x0511:
        r0 = 0;
        goto L_0x051e;
    L_0x0513:
        r2 = "USER_BANNED_IN_CHANNEL";
        r0 = r0.equals(r2);
        if (r0 == 0) goto L_0x051d;
    L_0x051b:
        r0 = 1;
        goto L_0x051e;
    L_0x051d:
        r0 = -1;
    L_0x051e:
        if (r0 == 0) goto L_0x054a;
    L_0x0520:
        r2 = 1;
        if (r0 == r2) goto L_0x0535;
    L_0x0523:
        if (r0 == r14) goto L_0x0527;
    L_0x0525:
        goto L_0x061b;
    L_0x0527:
        r0 = NUM; // 0x7f0e0656 float:1.8878327E38 double:1.053162958E-314;
        r2 = "MessageScheduledLimitReached";
        r0 = org.telegram.messenger.LocaleController.getString(r2, r0);
        showSimpleToast(r1, r0);
        goto L_0x061b;
    L_0x0535:
        r0 = org.telegram.messenger.NotificationCenter.getInstance(r18);
        r1 = org.telegram.messenger.NotificationCenter.needShowAlert;
        r2 = new java.lang.Object[r2];
        r3 = 5;
        r3 = java.lang.Integer.valueOf(r3);
        r4 = 0;
        r2[r4] = r3;
        r0.postNotificationName(r1, r2);
        goto L_0x061b;
    L_0x054a:
        r2 = 1;
        r4 = 0;
        r0 = org.telegram.messenger.NotificationCenter.getInstance(r18);
        r1 = org.telegram.messenger.NotificationCenter.needShowAlert;
        r2 = new java.lang.Object[r2];
        r3 = java.lang.Integer.valueOf(r4);
        r2[r4] = r3;
        r0.postNotificationName(r1, r2);
        goto L_0x061b;
    L_0x055f:
        if (r1 == 0) goto L_0x0584;
    L_0x0561:
        r4 = r0.text;
        r4 = r4.equals(r12);
        if (r4 == 0) goto L_0x0584;
    L_0x0569:
        if (r7 != 0) goto L_0x057a;
    L_0x056b:
        r0 = r2 instanceof org.telegram.tgnet.TLRPC.TL_channels_inviteToChannel;
        if (r0 == 0) goto L_0x0570;
    L_0x056f:
        goto L_0x057a;
    L_0x0570:
        r0 = new org.telegram.ui.TooManyCommunitiesActivity;
        r2 = 1;
        r0.<init>(r2);
        r1.presentFragment(r0);
        goto L_0x0583;
    L_0x057a:
        r0 = new org.telegram.ui.TooManyCommunitiesActivity;
        r4 = 0;
        r0.<init>(r4);
        r1.presentFragment(r0);
    L_0x0583:
        return r5;
    L_0x0584:
        r4 = 0;
        if (r1 == 0) goto L_0x059d;
    L_0x0587:
        r0 = r0.text;
        if (r3 == 0) goto L_0x0597;
    L_0x058b:
        r6 = r3.length;
        if (r6 <= 0) goto L_0x0597;
    L_0x058e:
        r3 = r3[r4];
        r3 = (java.lang.Boolean) r3;
        r14 = r3.booleanValue();
        goto L_0x0598;
    L_0x0597:
        r14 = 0;
    L_0x0598:
        showAddUserAlert(r0, r1, r14, r2);
        goto L_0x061b;
    L_0x059d:
        r0 = r0.text;
        r1 = "PEER_FLOOD";
        r0 = r0.equals(r1);
        if (r0 == 0) goto L_0x061b;
    L_0x05a7:
        r0 = org.telegram.messenger.NotificationCenter.getInstance(r18);
        r1 = org.telegram.messenger.NotificationCenter.needShowAlert;
        r2 = 1;
        r3 = new java.lang.Object[r2];
        r2 = java.lang.Integer.valueOf(r2);
        r4 = 0;
        r3[r4] = r2;
        r0.postNotificationName(r1, r3);
        goto L_0x061b;
    L_0x05bb:
        r2 = r0.text;
        r2 = r2.contains(r11);
        if (r2 == 0) goto L_0x05ce;
    L_0x05c3:
        r2 = NUM; // 0x7f0e058c float:1.8877918E38 double:1.053162858E-314;
        r0 = org.telegram.messenger.LocaleController.getString(r10, r2);
        showSimpleAlert(r1, r0);
        goto L_0x061b;
    L_0x05ce:
        r2 = r0.text;
        r2 = r2.startsWith(r9);
        if (r2 == 0) goto L_0x05e1;
    L_0x05d6:
        r2 = NUM; // 0x7f0e04ca float:1.8877524E38 double:1.0531627624E-314;
        r0 = org.telegram.messenger.LocaleController.getString(r15, r2);
        showSimpleAlert(r1, r0);
        goto L_0x061b;
    L_0x05e1:
        r2 = r0.text;
        r3 = "APP_VERSION_OUTDATED";
        r2 = r3.equals(r2);
        if (r2 == 0) goto L_0x05fd;
    L_0x05eb:
        r0 = r20.getParentActivity();
        r1 = NUM; // 0x7f0e0b7b float:1.8880999E38 double:1.0531636087E-314;
        r2 = "UpdateAppAlert";
        r1 = org.telegram.messenger.LocaleController.getString(r2, r1);
        r2 = 1;
        showUpdateAppAlert(r0, r1, r2);
        goto L_0x061b;
    L_0x05fd:
        r2 = new java.lang.StringBuilder;
        r2.<init>();
        r3 = NUM; // 0x7f0e0448 float:1.887726E38 double:1.053162698E-314;
        r3 = org.telegram.messenger.LocaleController.getString(r13, r3);
        r2.append(r3);
        r2.append(r8);
        r0 = r0.text;
        r2.append(r0);
        r0 = r2.toString();
        showSimpleAlert(r1, r0);
    L_0x061b:
        return r5;
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

    /* JADX WARNING: Removed duplicated region for block: B:27:0x00f5  */
    /* JADX WARNING: Removed duplicated region for block: B:26:0x00f0  */
    /* JADX WARNING: Removed duplicated region for block: B:31:0x0102  */
    /* JADX WARNING: Removed duplicated region for block: B:30:0x00fd  */
    /* JADX WARNING: Removed duplicated region for block: B:35:0x0136  */
    /* JADX WARNING: Removed duplicated region for block: B:12:0x004a  */
    /* JADX WARNING: Removed duplicated region for block: B:58:? A:{SYNTHETIC, RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:52:0x01e0  */
    public static void showBlockReportSpamAlert(org.telegram.ui.ActionBar.BaseFragment r18, long r19, org.telegram.tgnet.TLRPC.User r21, org.telegram.tgnet.TLRPC.Chat r22, org.telegram.tgnet.TLRPC.EncryptedChat r23, boolean r24, org.telegram.tgnet.TLRPC.ChatFull r25, org.telegram.messenger.MessagesStorage.IntCallback r26) {
        /*
        r0 = r18;
        r7 = r22;
        r1 = r25;
        if (r0 == 0) goto L_0x01e9;
    L_0x0008:
        r2 = r18.getParentActivity();
        if (r2 != 0) goto L_0x0010;
    L_0x000e:
        goto L_0x01e9;
    L_0x0010:
        r3 = r18.getAccountInstance();
        r11 = new org.telegram.ui.ActionBar.AlertDialog$Builder;
        r2 = r18.getParentActivity();
        r11.<init>(r2);
        r2 = r18.getCurrentAccount();
        r2 = org.telegram.messenger.MessagesController.getNotificationsSettings(r2);
        r4 = 1;
        r5 = 0;
        if (r23 != 0) goto L_0x0045;
    L_0x0029:
        r6 = new java.lang.StringBuilder;
        r6.<init>();
        r8 = "dialog_bar_report";
        r6.append(r8);
        r8 = r19;
        r6.append(r8);
        r6 = r6.toString();
        r2 = r2.getBoolean(r6, r5);
        if (r2 == 0) goto L_0x0043;
    L_0x0042:
        goto L_0x0047;
    L_0x0043:
        r2 = 0;
        goto L_0x0048;
    L_0x0045:
        r8 = r19;
    L_0x0047:
        r2 = 1;
    L_0x0048:
        if (r21 == 0) goto L_0x0136;
    L_0x004a:
        r1 = NUM; // 0x7f0e01e0 float:1.8876011E38 double:1.053162394E-314;
        r6 = new java.lang.Object[r4];
        r10 = org.telegram.messenger.UserObject.getFirstName(r21);
        r6[r5] = r10;
        r10 = "BlockUserTitle";
        r1 = org.telegram.messenger.LocaleController.formatString(r10, r1, r6);
        r11.setTitle(r1);
        r1 = NUM; // 0x7f0e01dc float:1.8876003E38 double:1.053162392E-314;
        r6 = new java.lang.Object[r4];
        r10 = org.telegram.messenger.UserObject.getFirstName(r21);
        r6[r5] = r10;
        r10 = "BlockUserAlert";
        r1 = org.telegram.messenger.LocaleController.formatString(r10, r1, r6);
        r1 = org.telegram.messenger.AndroidUtilities.replaceTags(r1);
        r11.setMessage(r1);
        r1 = NUM; // 0x7f0e01da float:1.8875999E38 double:1.053162391E-314;
        r6 = "BlockContact";
        r1 = org.telegram.messenger.LocaleController.getString(r6, r1);
        r6 = 2;
        r10 = new org.telegram.ui.Cells.CheckBoxCell[r6];
        r14 = new android.widget.LinearLayout;
        r15 = r18.getParentActivity();
        r14.<init>(r15);
        r14.setOrientation(r4);
        r15 = 0;
    L_0x008f:
        if (r15 >= r6) goto L_0x0127;
    L_0x0091:
        if (r15 != 0) goto L_0x009b;
    L_0x0093:
        if (r2 != 0) goto L_0x009b;
    L_0x0095:
        r16 = r1;
        r17 = r2;
        goto L_0x011e;
    L_0x009b:
        r6 = new org.telegram.ui.Cells.CheckBoxCell;
        r13 = r18.getParentActivity();
        r6.<init>(r13, r4);
        r10[r15] = r6;
        r6 = r10[r15];
        r13 = org.telegram.ui.ActionBar.Theme.getSelectorDrawable(r5);
        r6.setBackgroundDrawable(r13);
        r6 = r10[r15];
        r13 = java.lang.Integer.valueOf(r15);
        r6.setTag(r13);
        r6 = "";
        if (r15 != 0) goto L_0x00cd;
    L_0x00bc:
        r13 = r10[r15];
        r12 = NUM; // 0x7f0e03b1 float:1.8876954E38 double:1.0531626235E-314;
        r16 = r1;
        r1 = "DeleteReportSpam";
        r1 = org.telegram.messenger.LocaleController.getString(r1, r12);
        r13.setText(r1, r6, r4, r5);
        goto L_0x00e4;
    L_0x00cd:
        r16 = r1;
        if (r15 != r4) goto L_0x00e4;
    L_0x00d1:
        r1 = r10[r15];
        r12 = NUM; // 0x7f0e03b6 float:1.8876964E38 double:1.053162626E-314;
        r13 = new java.lang.Object[r5];
        r17 = r2;
        r2 = "DeleteThisChat";
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r12, r13);
        r1.setText(r2, r6, r4, r5);
        goto L_0x00e6;
    L_0x00e4:
        r17 = r2;
    L_0x00e6:
        r1 = r10[r15];
        r2 = org.telegram.messenger.LocaleController.isRTL;
        r6 = NUM; // 0x41800000 float:16.0 double:5.42932517E-315;
        r12 = NUM; // 0x41000000 float:8.0 double:5.38787994E-315;
        if (r2 == 0) goto L_0x00f5;
    L_0x00f0:
        r2 = org.telegram.messenger.AndroidUtilities.dp(r6);
        goto L_0x00f9;
    L_0x00f5:
        r2 = org.telegram.messenger.AndroidUtilities.dp(r12);
    L_0x00f9:
        r13 = org.telegram.messenger.LocaleController.isRTL;
        if (r13 == 0) goto L_0x0102;
    L_0x00fd:
        r6 = org.telegram.messenger.AndroidUtilities.dp(r12);
        goto L_0x0106;
    L_0x0102:
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
    L_0x0106:
        r1.setPadding(r2, r5, r6, r5);
        r1 = r10[r15];
        r2 = -2;
        r6 = -1;
        r2 = org.telegram.ui.Components.LayoutHelper.createLinear(r6, r2);
        r14.addView(r1, r2);
        r1 = r10[r15];
        r2 = new org.telegram.ui.Components.-$$Lambda$AlertsCreator$UlzF7udPi23pwPuOF4VG7RaKfIg;
        r2.<init>(r10);
        r1.setOnClickListener(r2);
    L_0x011e:
        r15 = r15 + 1;
        r1 = r16;
        r2 = r17;
        r6 = 2;
        goto L_0x008f;
    L_0x0127:
        r16 = r1;
        r1 = 12;
        r11.setCustomViewOffset(r1);
        r11.setView(r14);
        r4 = r10;
        r12 = r16;
        goto L_0x01ae;
    L_0x0136:
        if (r7 == 0) goto L_0x0174;
    L_0x0138:
        if (r24 == 0) goto L_0x0174;
    L_0x013a:
        r2 = NUM; // 0x7f0e0990 float:1.8880003E38 double:1.053163366E-314;
        r6 = "ReportUnrelatedGroup";
        r2 = org.telegram.messenger.LocaleController.getString(r6, r2);
        r11.setTitle(r2);
        if (r1 == 0) goto L_0x0167;
    L_0x0148:
        r1 = r1.location;
        r2 = r1 instanceof org.telegram.tgnet.TLRPC.TL_channelLocation;
        if (r2 == 0) goto L_0x0167;
    L_0x014e:
        r1 = (org.telegram.tgnet.TLRPC.TL_channelLocation) r1;
        r2 = NUM; // 0x7f0e0991 float:1.8880005E38 double:1.0531633666E-314;
        r4 = new java.lang.Object[r4];
        r1 = r1.address;
        r4[r5] = r1;
        r1 = "ReportUnrelatedGroupText";
        r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r4);
        r1 = org.telegram.messenger.AndroidUtilities.replaceTags(r1);
        r11.setMessage(r1);
        goto L_0x01a3;
    L_0x0167:
        r1 = NUM; // 0x7f0e0992 float:1.8880007E38 double:1.053163367E-314;
        r2 = "ReportUnrelatedGroupTextNoAddress";
        r1 = org.telegram.messenger.LocaleController.getString(r2, r1);
        r11.setMessage(r1);
        goto L_0x01a3;
    L_0x0174:
        r1 = NUM; // 0x7f0e098e float:1.8879999E38 double:1.053163365E-314;
        r2 = "ReportSpamTitle";
        r1 = org.telegram.messenger.LocaleController.getString(r2, r1);
        r11.setTitle(r1);
        r1 = org.telegram.messenger.ChatObject.isChannel(r22);
        if (r1 == 0) goto L_0x0197;
    L_0x0186:
        r1 = r7.megagroup;
        if (r1 != 0) goto L_0x0197;
    L_0x018a:
        r1 = NUM; // 0x7f0e098a float:1.887999E38 double:1.053163363E-314;
        r2 = "ReportSpamAlertChannel";
        r1 = org.telegram.messenger.LocaleController.getString(r2, r1);
        r11.setMessage(r1);
        goto L_0x01a3;
    L_0x0197:
        r1 = NUM; // 0x7f0e098b float:1.8879993E38 double:1.0531633636E-314;
        r2 = "ReportSpamAlertGroup";
        r1 = org.telegram.messenger.LocaleController.getString(r2, r1);
        r11.setMessage(r1);
    L_0x01a3:
        r1 = NUM; // 0x7f0e0980 float:1.887997E38 double:1.053163358E-314;
        r2 = "ReportChat";
        r1 = org.telegram.messenger.LocaleController.getString(r2, r1);
        r12 = r1;
        r4 = 0;
    L_0x01ae:
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
        r1 = NUM; // 0x7f0e021c float:1.8876133E38 double:1.0531624234E-314;
        r2 = "Cancel";
        r1 = org.telegram.messenger.LocaleController.getString(r2, r1);
        r2 = 0;
        r11.setNegativeButton(r1, r2);
        r1 = r11.create();
        r0.showDialog(r1);
        r0 = -1;
        r0 = r1.getButton(r0);
        r0 = (android.widget.TextView) r0;
        if (r0 == 0) goto L_0x01e9;
    L_0x01e0:
        r1 = "dialogTextRed2";
        r1 = org.telegram.ui.ActionBar.Theme.getColor(r1);
        r0.setTextColor(r1);
    L_0x01e9:
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
        ArrayList arrayList2 = new ArrayList();
        i = MessagesController.getInstance(i).availableMapProviders;
        if ((i & 1) != 0) {
            arrayList.add(LocaleController.getString("MapPreviewProviderTelegram", NUM));
            arrayList2.add(Integer.valueOf(0));
        }
        if ((i & 2) != 0) {
            arrayList.add(LocaleController.getString("MapPreviewProviderGoogle", NUM));
            arrayList2.add(Integer.valueOf(1));
        }
        if ((i & 4) != 0) {
            arrayList.add(LocaleController.getString("MapPreviewProviderYandex", NUM));
            arrayList2.add(Integer.valueOf(3));
        }
        arrayList.add(LocaleController.getString("MapPreviewProviderNobody", NUM));
        arrayList2.add(Integer.valueOf(2));
        Builder builder = new Builder(context);
        builder.setTitle(LocaleController.getString("MapPreviewProviderTitle", NUM));
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(1);
        builder.setView(linearLayout);
        for (int i2 = 0; i2 < arrayList.size(); i2++) {
            RadioColorCell radioColorCell = new RadioColorCell(context);
            radioColorCell.setPadding(AndroidUtilities.dp(4.0f), 0, AndroidUtilities.dp(4.0f), 0);
            radioColorCell.setTag(Integer.valueOf(i2));
            radioColorCell.setCheckColor(Theme.getColor("radioBackground"), Theme.getColor("dialogRadioBackgroundChecked"));
            radioColorCell.setTextAndValue((String) arrayList.get(i2), SharedConfig.mapPreviewType == ((Integer) arrayList2.get(i2)).intValue());
            linearLayout.addView(radioColorCell);
            radioColorCell.setOnClickListener(new -$$Lambda$AlertsCreator$OnXNDjADtC_bD3GK3ao8UUcfhIA(arrayList2, runnable, builder));
        }
        if (!z) {
            builder.setNegativeButton(LocaleController.getString("Cancel", NUM), null);
        }
        AlertDialog show = builder.show();
        if (z) {
            show.setCanceledOnTouchOutside(false);
        }
        return show;
    }

    static /* synthetic */ void lambda$showSecretLocationAlert$6(ArrayList arrayList, Runnable runnable, Builder builder, View view) {
        SharedConfig.setSecretMapPreviewType(((Integer) arrayList.get(((Integer) view.getTag()).intValue())).intValue());
        if (runnable != null) {
            runnable.run();
        }
        builder.getDismissRunnable().run();
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
                BackupImageView backupImageView;
                String string;
                int currentAccount = baseFragment.getCurrentAccount();
                Context parentActivity = baseFragment.getParentActivity();
                Builder builder = new Builder(parentActivity);
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
                builder.setView(anonymousClass3);
                Drawable avatarDrawable = new AvatarDrawable();
                avatarDrawable.setTextSize(AndroidUtilities.dp(12.0f));
                BackupImageView backupImageView2 = new BackupImageView(parentActivity);
                backupImageView2.setRoundRadius(AndroidUtilities.dp(20.0f));
                anonymousClass3.addView(backupImageView2, LayoutHelper.createFrame(40, 40.0f, (LocaleController.isRTL ? 5 : 3) | 48, 22.0f, 5.0f, 22.0f, 0.0f));
                TextView textView2 = new TextView(parentActivity);
                textView2.setTextColor(Theme.getColor("actionBarDefaultSubmenuItem"));
                textView2.setTextSize(1, 20.0f);
                textView2.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
                textView2.setLines(1);
                textView2.setMaxLines(1);
                textView2.setSingleLine(true);
                textView2.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
                textView2.setEllipsize(TruncateAt.END);
                String str = "DeleteChatUser";
                String str2 = "ClearHistoryCache";
                String str3 = "ClearHistory";
                Builder builder2 = builder;
                String str4 = "LeaveMegaMenu";
                if (!z) {
                    z5 = z6;
                    backupImageView = backupImageView2;
                    if (z2) {
                        if (!ChatObject.isChannel(chat)) {
                            textView2.setText(LocaleController.getString("DeleteMegaMenu", NUM));
                        } else if (obj.megagroup) {
                            textView2.setText(LocaleController.getString("DeleteMegaMenu", NUM));
                        } else {
                            textView2.setText(LocaleController.getString("ChannelDeleteMenu", NUM));
                        }
                    } else if (obj == null) {
                        textView2.setText(LocaleController.getString(str, NUM));
                    } else if (!ChatObject.isChannel(chat)) {
                        textView2.setText(LocaleController.getString(str4, NUM));
                    } else if (obj.megagroup) {
                        textView2.setText(LocaleController.getString(str4, NUM));
                    } else {
                        textView2.setText(LocaleController.getString("LeaveChannelMenu", NUM));
                    }
                } else if (z6) {
                    z5 = z6;
                    backupImageView = backupImageView2;
                    textView2.setText(LocaleController.getString(str2, NUM));
                } else {
                    z5 = z6;
                    backupImageView = backupImageView2;
                    textView2.setText(LocaleController.getString(str3, NUM));
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
                BackupImageView backupImageView3;
                if (obj2 == null) {
                    backupImageView3 = backupImageView;
                    if (obj != null) {
                        avatarDrawable.setInfo((Chat) obj);
                        backupImageView3.setImage(ImageLocation.getForChat(obj, false), "50_50", avatarDrawable, obj);
                    }
                } else if (obj2.id == clientUserId) {
                    avatarDrawable.setAvatarType(2);
                    backupImageView.setImage(null, null, avatarDrawable, obj2);
                } else {
                    backupImageView3 = backupImageView;
                    avatarDrawable.setInfo((User) obj2);
                    backupImageView3.setImage(ImageLocation.getForUser(obj2, false), "50_50", avatarDrawable, obj2);
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
                        string = LocaleController.getString(str2, NUM);
                    } else {
                        string = LocaleController.getString(str3, NUM);
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
                    string = LocaleController.getString(str, NUM);
                } else if (obj.megagroup) {
                    string = LocaleController.getString(str4, NUM);
                } else {
                    string = LocaleController.getString("LeaveChannelMenu", NUM);
                }
                str3 = string;
                -$$Lambda$AlertsCreator$MnHLfrgmi5mlg6nnWL9u3dTctXY -__lambda_alertscreator_mnhlfrgmi5mlg6nnwl9u3dtctxy = new -$$Lambda$AlertsCreator$MnHLfrgmi5mlg6nnWL9u3dTctXY(user, z5, z3, zArr, baseFragment, z, z2, chat, z4, booleanCallback);
                Builder builder3 = builder2;
                builder3.setPositiveButton(str3, -__lambda_alertscreator_mnhlfrgmi5mlg6nnwl9u3dtctxy);
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

    public static BottomSheet.Builder createScheduleDatePickerDialog(Context context, long j, ScheduleDatePickerDelegate scheduleDatePickerDelegate) {
        return createScheduleDatePickerDialog(context, j, -1, scheduleDatePickerDelegate, null);
    }

    public static BottomSheet.Builder createScheduleDatePickerDialog(Context context, long j, ScheduleDatePickerDelegate scheduleDatePickerDelegate, Runnable runnable) {
        return createScheduleDatePickerDialog(context, j, -1, scheduleDatePickerDelegate, runnable);
    }

    /* JADX WARNING: Removed duplicated region for block: B:36:0x0281  */
    /* JADX WARNING: Removed duplicated region for block: B:35:0x027f  */
    public static org.telegram.ui.ActionBar.BottomSheet.Builder createScheduleDatePickerDialog(android.content.Context r32, long r33, long r35, org.telegram.ui.Components.AlertsCreator.ScheduleDatePickerDelegate r37, java.lang.Runnable r38) {
        /*
        r0 = r32;
        r8 = r33;
        r1 = 0;
        if (r0 != 0) goto L_0x0008;
    L_0x0007:
        return r1;
    L_0x0008:
        r2 = org.telegram.messenger.UserConfig.selectedAccount;
        r2 = org.telegram.messenger.UserConfig.getInstance(r2);
        r10 = r2.getClientUserId();
        r11 = new org.telegram.ui.ActionBar.BottomSheet$Builder;
        r12 = 0;
        r11.<init>(r0, r12);
        r11.setApplyBottomPadding(r12);
        r13 = new org.telegram.ui.Components.NumberPicker;
        r13.<init>(r0);
        r2 = NUM; // 0x41200000 float:10.0 double:5.398241246E-315;
        r3 = org.telegram.messenger.AndroidUtilities.dp(r2);
        r13.setTextOffset(r3);
        r3 = 5;
        r13.setItemCount(r3);
        r14 = new org.telegram.ui.Components.NumberPicker;
        r14.<init>(r0);
        r14.setItemCount(r3);
        r2 = org.telegram.messenger.AndroidUtilities.dp(r2);
        r2 = -r2;
        r14.setTextOffset(r2);
        r15 = new org.telegram.ui.Components.NumberPicker;
        r15.<init>(r0);
        r15.setItemCount(r3);
        r16 = NUM; // 0x42080000 float:34.0 double:5.473360725E-315;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r16);
        r2 = -r2;
        r15.setTextOffset(r2);
        r7 = new org.telegram.ui.Components.AlertsCreator$4;
        r7.<init>(r0, r13, r14, r15);
        r6 = 1;
        r7.setOrientation(r6);
        r2 = new android.widget.FrameLayout;
        r2.<init>(r0);
        r17 = -1;
        r18 = -2;
        r19 = 51;
        r20 = 22;
        r21 = 0;
        r22 = 0;
        r23 = 4;
        r3 = org.telegram.ui.Components.LayoutHelper.createLinear(r17, r18, r19, r20, r21, r22, r23);
        r7.addView(r2, r3);
        r3 = new android.widget.TextView;
        r3.<init>(r0);
        r4 = (long) r10;
        r17 = (r8 > r4 ? 1 : (r8 == r4 ? 0 : -1));
        if (r17 != 0) goto L_0x0089;
    L_0x007c:
        r1 = NUM; // 0x7f0e0a41 float:1.8880362E38 double:1.0531634536E-314;
        r12 = "SetReminder";
        r1 = org.telegram.messenger.LocaleController.getString(r12, r1);
        r3.setText(r1);
        goto L_0x0095;
    L_0x0089:
        r1 = NUM; // 0x7f0e09cf float:1.888013E38 double:1.053163397E-314;
        r12 = "ScheduleMessage";
        r1 = org.telegram.messenger.LocaleController.getString(r12, r1);
        r3.setText(r1);
    L_0x0095:
        r1 = "dialogTextBlack";
        r1 = org.telegram.ui.ActionBar.Theme.getColor(r1);
        r3.setTextColor(r1);
        r1 = NUM; // 0x41a00000 float:20.0 double:5.439686476E-315;
        r3.setTextSize(r6, r1);
        r12 = "fonts/rmedium.ttf";
        r1 = org.telegram.messenger.AndroidUtilities.getTypeface(r12);
        r3.setTypeface(r1);
        r19 = -2;
        r20 = -NUM; // 0xffffffffCLASSNAME float:-2.0 double:NaN;
        r21 = 51;
        r22 = 0;
        r23 = NUM; // 0x41400000 float:12.0 double:5.408602553E-315;
        r24 = 0;
        r25 = 0;
        r1 = org.telegram.ui.Components.LayoutHelper.createFrame(r19, r20, r21, r22, r23, r24, r25);
        r2.addView(r3, r1);
        r1 = org.telegram.ui.Components.-$$Lambda$AlertsCreator$7vxVL9TgAW5Vn6ZIlSM9Zg3Jmqc.INSTANCE;
        r3.setOnTouchListener(r1);
        r1 = (int) r8;
        if (r1 <= 0) goto L_0x017f;
    L_0x00c9:
        r3 = (r8 > r4 ? 1 : (r8 == r4 ? 0 : -1));
        if (r3 == 0) goto L_0x017f;
    L_0x00cd:
        r3 = org.telegram.messenger.UserConfig.selectedAccount;
        r3 = org.telegram.messenger.MessagesController.getInstance(r3);
        r1 = java.lang.Integer.valueOf(r1);
        r1 = r3.getUser(r1);
        if (r1 == 0) goto L_0x017f;
    L_0x00dd:
        r3 = r1.bot;
        if (r3 != 0) goto L_0x017f;
    L_0x00e1:
        r3 = r1.status;
        if (r3 == 0) goto L_0x017f;
    L_0x00e5:
        r3 = r3.expires;
        if (r3 <= 0) goto L_0x017f;
    L_0x00e9:
        r1 = org.telegram.messenger.UserObject.getFirstName(r1);
        r3 = r1.length();
        r6 = 10;
        if (r3 <= r6) goto L_0x010f;
    L_0x00f5:
        r3 = new java.lang.StringBuilder;
        r3.<init>();
        r20 = r4;
        r4 = 0;
        r1 = r1.substring(r4, r6);
        r3.append(r1);
        r1 = "…";
        r3.append(r1);
        r1 = r3.toString();
        goto L_0x0112;
    L_0x010f:
        r20 = r4;
        r4 = 0;
    L_0x0112:
        r3 = new org.telegram.ui.ActionBar.ActionBarMenuItem;
        r5 = "key_sheet_other";
        r5 = org.telegram.ui.ActionBar.Theme.getColor(r5);
        r6 = 0;
        r3.<init>(r0, r6, r4, r5);
        r3.setLongClickEnabled(r4);
        r4 = 2;
        r3.setSubMenuOpenSide(r4);
        r4 = NUM; // 0x7var_fa float:1.7945085E38 double:1.0529356265E-314;
        r3.setIcon(r4);
        r4 = "player_actionBarSelector";
        r4 = org.telegram.ui.ActionBar.Theme.getColor(r4);
        r5 = 1;
        r4 = org.telegram.ui.ActionBar.Theme.createSelectorDrawable(r4, r5);
        r3.setBackgroundDrawable(r4);
        r22 = 40;
        r23 = NUM; // 0x42200000 float:40.0 double:5.481131706E-315;
        r24 = 53;
        r25 = 0;
        r26 = NUM; // 0x41000000 float:8.0 double:5.38787994E-315;
        r27 = NUM; // 0x40a00000 float:5.0 double:5.356796015E-315;
        r28 = 0;
        r4 = org.telegram.ui.Components.LayoutHelper.createFrame(r22, r23, r24, r25, r26, r27, r28);
        r2.addView(r3, r4);
        r2 = NUM; // 0x7f0e09d0 float:1.8880132E38 double:1.0531633977E-314;
        r4 = 1;
        r5 = new java.lang.Object[r4];
        r6 = 0;
        r5[r6] = r1;
        r1 = "ScheduleWhenOnline";
        r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5);
        r3.addSubItem(r4, r1);
        r1 = new org.telegram.ui.Components.-$$Lambda$AlertsCreator$8KnvvPJIzLIdgYs5h2wTeC9iFLw;
        r1.<init>(r3);
        r3.setOnClickListener(r1);
        r1 = new org.telegram.ui.Components.-$$Lambda$AlertsCreator$KR_OgeK9dqo37BHX7GI6av8Vcek;
        r6 = r37;
        r1.<init>(r6, r11);
        r3.setDelegate(r1);
        r1 = NUM; // 0x7f0e002c float:1.8875127E38 double:1.0531621784E-314;
        r2 = "AccDescrMoreOptions";
        r1 = org.telegram.messenger.LocaleController.getString(r2, r1);
        r3.setContentDescription(r1);
        goto L_0x0183;
    L_0x017f:
        r6 = r37;
        r20 = r4;
    L_0x0183:
        r5 = new android.widget.LinearLayout;
        r5.<init>(r0);
        r1 = 0;
        r5.setOrientation(r1);
        r1 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r5.setWeightSum(r1);
        r1 = -1;
        r2 = -2;
        r1 = org.telegram.ui.Components.LayoutHelper.createLinear(r1, r2);
        r7.addView(r5, r1);
        r1 = java.lang.System.currentTimeMillis();
        r3 = java.util.Calendar.getInstance();
        r3.setTimeInMillis(r1);
        r4 = 1;
        r6 = r3.get(r4);
        r17 = r11;
        r11 = new android.widget.TextView;
        r11.<init>(r0);
        r0 = NUM; // 0x3var_ float:0.5 double:5.222099017E-315;
        r19 = r12;
        r12 = 270; // 0x10e float:3.78E-43 double:1.334E-321;
        r4 = 0;
        r0 = org.telegram.ui.Components.LayoutHelper.createLinear(r4, r12, r0);
        r5.addView(r13, r0);
        r13.setMinValue(r4);
        r0 = 365; // 0x16d float:5.11E-43 double:1.803E-321;
        r13.setMaxValue(r0);
        r13.setWrapSelectorWheel(r4);
        r0 = new org.telegram.ui.Components.-$$Lambda$AlertsCreator$r8Tt-baFJlhUXYOi4kO6UUP-Dbg;
        r0.<init>(r1, r3, r6);
        r13.setFormatter(r0);
        r6 = new org.telegram.ui.Components.-$$Lambda$AlertsCreator$6AnhMphU6whhzbgD5XkCr_q_09g;
        r0 = r6;
        r1 = r11;
        r2 = r10;
        r29 = r3;
        r22 = 1;
        r3 = r33;
        r30 = r5;
        r5 = r13;
        r12 = r6;
        r22 = r10;
        r10 = 1;
        r6 = r14;
        r31 = r7;
        r7 = r15;
        r0.<init>(r1, r2, r3, r5, r6, r7);
        r13.setOnValueChangedListener(r12);
        r0 = 0;
        r14.setMinValue(r0);
        r1 = 23;
        r14.setMaxValue(r1);
        r1 = NUM; // 0x3e4ccccd float:0.2 double:5.164075695E-315;
        r2 = 270; // 0x10e float:3.78E-43 double:1.334E-321;
        r1 = org.telegram.ui.Components.LayoutHelper.createLinear(r0, r2, r1);
        r2 = r30;
        r2.addView(r14, r1);
        r1 = org.telegram.ui.Components.-$$Lambda$AlertsCreator$TSQJEOtVCEhgAIPpdVghXXq1LRE.INSTANCE;
        r14.setFormatter(r1);
        r14.setOnValueChangedListener(r12);
        r15.setMinValue(r0);
        r1 = 59;
        r15.setMaxValue(r1);
        r15.setValue(r0);
        r1 = org.telegram.ui.Components.-$$Lambda$AlertsCreator$KakG1tQ5ETn782zFMW9iKAMpW2A.INSTANCE;
        r15.setFormatter(r1);
        r1 = NUM; // 0x3e99999a float:0.3 double:5.188942835E-315;
        r3 = 270; // 0x10e float:3.78E-43 double:1.334E-321;
        r1 = org.telegram.ui.Components.LayoutHelper.createLinear(r0, r3, r1);
        r2.addView(r15, r1);
        r15.setOnValueChangedListener(r12);
        r0 = 0;
        r2 = (r35 > r0 ? 1 : (r35 == r0 ? 0 : -1));
        if (r2 <= 0) goto L_0x0274;
    L_0x0232:
        r0 = NUM; // 0x7ffffffe float:NaN double:1.0609978945E-314;
        r2 = (r35 > r0 ? 1 : (r35 == r0 ? 0 : -1));
        if (r2 == 0) goto L_0x0274;
    L_0x0239:
        r0 = 1000; // 0x3e8 float:1.401E-42 double:4.94E-321;
        r0 = r0 * r35;
        r2 = java.lang.System.currentTimeMillis();
        r12 = r29;
        r12.setTimeInMillis(r2);
        r2 = 12;
        r3 = 0;
        r12.set(r2, r3);
        r4 = 11;
        r12.set(r4, r3);
        r5 = r12.getTimeInMillis();
        r5 = r0 - r5;
        r23 = 86400000; // 0x5265CLASSNAME float:7.82218E-36 double:4.2687272E-316;
        r5 = r5 / r23;
        r3 = (int) r5;
        r12.setTimeInMillis(r0);
        if (r3 < 0) goto L_0x0276;
    L_0x0262:
        r0 = r12.get(r2);
        r15.setValue(r0);
        r0 = r12.get(r4);
        r14.setValue(r0);
        r13.setValue(r3);
        goto L_0x0276;
    L_0x0274:
        r12 = r29;
    L_0x0276:
        r7 = new boolean[r10];
        r0 = 0;
        r7[r0] = r10;
        r1 = (r20 > r8 ? 1 : (r20 == r8 ? 0 : -1));
        if (r1 != 0) goto L_0x0281;
    L_0x027f:
        r1 = 1;
        goto L_0x0282;
    L_0x0281:
        r1 = 0;
    L_0x0282:
        checkScheduleDate(r11, r1, r13, r14, r15);
        r1 = org.telegram.messenger.AndroidUtilities.dp(r16);
        r2 = org.telegram.messenger.AndroidUtilities.dp(r16);
        r11.setPadding(r1, r0, r2, r0);
        r0 = 17;
        r11.setGravity(r0);
        r0 = "featuredStickers_buttonText";
        r0 = org.telegram.ui.ActionBar.Theme.getColor(r0);
        r11.setTextColor(r0);
        r0 = NUM; // 0x41600000 float:14.0 double:5.41896386E-315;
        r11.setTextSize(r10, r0);
        r0 = org.telegram.messenger.AndroidUtilities.getTypeface(r19);
        r11.setTypeface(r0);
        r0 = NUM; // 0x40800000 float:4.0 double:5.34643471E-315;
        r0 = org.telegram.messenger.AndroidUtilities.dp(r0);
        r1 = "featuredStickers_addButton";
        r1 = org.telegram.ui.ActionBar.Theme.getColor(r1);
        r2 = "featuredStickers_addButtonPressed";
        r2 = org.telegram.ui.ActionBar.Theme.getColor(r2);
        r0 = org.telegram.ui.ActionBar.Theme.createSimpleSelectorRoundRectDrawable(r0, r1, r2);
        r11.setBackgroundDrawable(r0);
        r23 = -1;
        r24 = 48;
        r25 = 83;
        r26 = 16;
        r27 = 15;
        r28 = 16;
        r29 = 16;
        r0 = org.telegram.ui.Components.LayoutHelper.createLinear(r23, r24, r25, r26, r27, r28, r29);
        r10 = r31;
        r10.addView(r11, r0);
        r6 = new org.telegram.ui.Components.-$$Lambda$AlertsCreator$ZpZtLHOTj8yqCPWZNifekiyWumA;
        r0 = r6;
        r1 = r7;
        r2 = r22;
        r3 = r33;
        r5 = r13;
        r13 = r6;
        r6 = r14;
        r14 = r7;
        r7 = r15;
        r8 = r12;
        r9 = r37;
        r12 = r10;
        r10 = r17;
        r0.<init>(r1, r2, r3, r5, r6, r7, r8, r9, r10);
        r11.setOnClickListener(r13);
        r0 = r17;
        r0.setCustomView(r12);
        r1 = r0.show();
        r2 = new org.telegram.ui.Components.-$$Lambda$AlertsCreator$1lIWD0cerrPgajgmoGrGBRAlclk;
        r3 = r38;
        r2.<init>(r3, r14);
        r1.setOnDismissListener(r2);
        return r0;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.AlertsCreator.createScheduleDatePickerDialog(android.content.Context, long, long, org.telegram.ui.Components.AlertsCreator$ScheduleDatePickerDelegate, java.lang.Runnable):org.telegram.ui.ActionBar.BottomSheet$Builder");
    }

    static /* synthetic */ void lambda$createScheduleDatePickerDialog$23(ScheduleDatePickerDelegate scheduleDatePickerDelegate, BottomSheet.Builder builder, int i) {
        if (i == 1) {
            scheduleDatePickerDelegate.didSelectDate(true, NUM);
            builder.getDismissRunnable().run();
        }
    }

    static /* synthetic */ String lambda$createScheduleDatePickerDialog$24(long j, Calendar calendar, int i, int i2) {
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

    static /* synthetic */ void lambda$createScheduleDatePickerDialog$25(TextView textView, int i, long j, NumberPicker numberPicker, NumberPicker numberPicker2, NumberPicker numberPicker3, NumberPicker numberPicker4, int i2, int i3) {
        checkScheduleDate(textView, ((long) i) == j, numberPicker, numberPicker2, numberPicker3);
    }

    static /* synthetic */ void lambda$createScheduleDatePickerDialog$28(boolean[] zArr, int i, long j, NumberPicker numberPicker, NumberPicker numberPicker2, NumberPicker numberPicker3, Calendar calendar, ScheduleDatePickerDelegate scheduleDatePickerDelegate, BottomSheet.Builder builder, View view) {
        zArr[0] = false;
        boolean checkScheduleDate = checkScheduleDate(null, ((long) i) == j, numberPicker, numberPicker2, numberPicker3);
        calendar.setTimeInMillis(System.currentTimeMillis() + (((((long) numberPicker.getValue()) * 24) * 3600) * 1000));
        calendar.set(11, numberPicker2.getValue());
        calendar.set(12, numberPicker3.getValue());
        if (checkScheduleDate) {
            calendar.set(13, 0);
        }
        scheduleDatePickerDelegate.didSelectDate(true, (int) (calendar.getTimeInMillis() / 1000));
        builder.getDismissRunnable().run();
    }

    static /* synthetic */ void lambda$createScheduleDatePickerDialog$29(Runnable runnable, boolean[] zArr, DialogInterface dialogInterface) {
        if (runnable != null && zArr[0]) {
            runnable.run();
        }
    }

    public static Dialog createMuteAlert(Context context, long j) {
        if (context == null) {
            return null;
        }
        BottomSheet.Builder builder = new BottomSheet.Builder(context);
        builder.setTitle(LocaleController.getString("Notifications", NUM), true);
        CharSequence[] charSequenceArr = new CharSequence[4];
        Object[] objArr = new Object[1];
        objArr[0] = LocaleController.formatPluralString("Hours", 1);
        String str = "MuteFor";
        charSequenceArr[0] = LocaleController.formatString(str, NUM, objArr);
        charSequenceArr[1] = LocaleController.formatString(str, NUM, LocaleController.formatPluralString("Hours", 8));
        charSequenceArr[2] = LocaleController.formatString(str, NUM, LocaleController.formatPluralString("Days", 2));
        charSequenceArr[3] = LocaleController.getString("MuteDisable", NUM);
        builder.setItems(charSequenceArr, new -$$Lambda$AlertsCreator$S6Vo-T677tzumXQiZlpxidWWOI4(j));
        return builder.create();
    }

    static /* synthetic */ void lambda$createMuteAlert$30(long j, DialogInterface dialogInterface, int i) {
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
            builder.setTitle(LocaleController.getString("ReportChat", NUM), true);
            builder.setItems(new CharSequence[]{LocaleController.getString("ReportChatSpam", NUM), LocaleController.getString("ReportChatViolence", NUM), LocaleController.getString("ReportChatChild", NUM), LocaleController.getString("ReportChatPornography", NUM), LocaleController.getString("ReportChatOther", NUM)}, new -$$Lambda$AlertsCreator$dlmdqoQ3R6pBq6j2guFwIdEgVE0(j, i, baseFragment, context));
            baseFragment.showDialog(builder.create());
        }
    }

    static /* synthetic */ void lambda$createReportAlert$32(long j, int i, BaseFragment baseFragment, Context context, DialogInterface dialogInterface, int i2) {
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
        ConnectionsManager.getInstance(UserConfig.selectedAccount).sendRequest(tL_messages_report, -$$Lambda$AlertsCreator$NpoDU2Mba05BmoLPTdtKeWiFGaI.INSTANCE);
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
                case 1377621075:
                    if (str.equals("USER_CHANNELS_TOO_MUCH")) {
                        obj = 19;
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
            String str2 = "ChannelTooMuchTitle";
            switch (obj) {
                case null:
                    builder.setMessage(LocaleController.getString("NobodyLikesSpam2", NUM));
                    builder.setNegativeButton(LocaleController.getString("MoreInfo", NUM), new -$$Lambda$AlertsCreator$05NWor8e9g8v84nB8eC2NpLdD5o(baseFragment));
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
                    builder.setTitle(LocaleController.getString(str2, NUM));
                    if (!(tLObject instanceof TL_channels_createChannel)) {
                        builder.setMessage(LocaleController.getString("ChannelTooMuchJoin", NUM));
                        break;
                    } else {
                        builder.setMessage(LocaleController.getString("ChannelTooMuch", NUM));
                        break;
                    }
                case 19:
                    builder.setTitle(LocaleController.getString(str2, NUM));
                    builder.setMessage(LocaleController.getString("UserChannelTooMuchJoin", NUM));
                    break;
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
            radioColorCell.setOnClickListener(new -$$Lambda$AlertsCreator$HFAm3goRkxSxcPQK05l9kfLrhHg(linearLayout, iArr));
            i5++;
            i4 = 9;
        }
        Builder builder = new Builder(context);
        builder.setTitle(LocaleController.getString("LedColor", NUM));
        builder.setView(linearLayout);
        builder.setPositiveButton(LocaleController.getString("Set", NUM), new -$$Lambda$AlertsCreator$f5-lXMPH008ooPa5FMJuEwh0OrQ(j, iArr, i, runnable));
        builder.setNeutralButton(LocaleController.getString("LedDisabled", NUM), new -$$Lambda$AlertsCreator$0TAT2OqvSjJdKO3lAw6w3B95D9k(j2, i3, runnable2));
        if (j2 != 0) {
            builder.setNegativeButton(LocaleController.getString("Default", NUM), new -$$Lambda$AlertsCreator$7gGGrRc8bYYUeDry48ux1LpgV4s(j2, runnable2));
        }
        return builder.create();
    }

    static /* synthetic */ void lambda$createColorSelectDialog$34(LinearLayout linearLayout, int[] iArr, View view) {
        int childCount = linearLayout.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View view2 = (RadioColorCell) linearLayout.getChildAt(i);
            view2.setChecked(view2 == view, true);
        }
        iArr[0] = TextColorCell.colorsToSave[((Integer) view.getTag()).intValue()];
    }

    static /* synthetic */ void lambda$createColorSelectDialog$35(long j, int[] iArr, int i, Runnable runnable, DialogInterface dialogInterface, int i2) {
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

    static /* synthetic */ void lambda$createColorSelectDialog$36(long j, int i, Runnable runnable, DialogInterface dialogInterface, int i2) {
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

    static /* synthetic */ void lambda$createColorSelectDialog$37(long j, Runnable runnable, DialogInterface dialogInterface, int i) {
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
            -$$Lambda$AlertsCreator$Tk2YqVMTqwW8mYxUqnK8-9-w364 -__lambda_alertscreator_tk2yqvmtqww8myxuqnk8-9-w364 = r1;
            FrameLayout frameLayout = radioColorCell;
            int i3 = i2;
            builder = builder2;
            -$$Lambda$AlertsCreator$Tk2YqVMTqwW8mYxUqnK8-9-w364 -__lambda_alertscreator_tk2yqvmtqww8myxuqnk8-9-w3642 = new -$$Lambda$AlertsCreator$Tk2YqVMTqwW8mYxUqnK8-9-w364(iArr, j, str, builder2, runnable);
            frameLayout.setOnClickListener(-__lambda_alertscreator_tk2yqvmtqww8myxuqnk8-9-w364);
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

    static /* synthetic */ void lambda$createVibrationSelectDialog$38(int[] iArr, long j, String str, Builder builder, Runnable runnable, View view) {
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
            radioColorCell.setOnClickListener(new -$$Lambda$AlertsCreator$hAp3bfIJRf-X8ZqxnAYFVZoSg6g(iArr, linearLayout));
            i++;
        }
        Builder builder = new Builder(context);
        builder.setTopImage(new ShareLocationDrawable(context, 0), Theme.getColor("dialogTopBackground"));
        builder.setView(linearLayout);
        builder.setPositiveButton(LocaleController.getString("ShareFile", NUM), new -$$Lambda$AlertsCreator$zvbzJHjlBZQIkowXbIV84jyO3o0(iArr, intCallback));
        builder.setNeutralButton(LocaleController.getString("Cancel", NUM), null);
        return builder.create();
    }

    static /* synthetic */ void lambda$createLocationUpdateDialog$39(int[] iArr, LinearLayout linearLayout, View view) {
        iArr[0] = ((Integer) view.getTag()).intValue();
        int childCount = linearLayout.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childAt = linearLayout.getChildAt(i);
            if (childAt instanceof RadioColorCell) {
                ((RadioColorCell) childAt).setChecked(childAt == view, true);
            }
        }
    }

    static /* synthetic */ void lambda$createLocationUpdateDialog$40(int[] iArr, IntCallback intCallback, DialogInterface dialogInterface, int i) {
        int i2 = iArr[0] == 0 ? 900 : iArr[0] == 1 ? 3600 : 28800;
        intCallback.run(i2);
    }

    public static Builder createContactsPermissionDialog(Activity activity, IntCallback intCallback) {
        Builder builder = new Builder((Context) activity);
        builder.setTopImage(NUM, Theme.getColor("dialogTopBackground"));
        builder.setMessage(AndroidUtilities.replaceTags(LocaleController.getString("ContactsPermissionAlert", NUM)));
        builder.setPositiveButton(LocaleController.getString("ContactsPermissionAlertContinue", NUM), new -$$Lambda$AlertsCreator$NbNSmrEGNAuorerzpV7fH1xWr4E(intCallback));
        builder.setNegativeButton(LocaleController.getString("ContactsPermissionAlertNotNow", NUM), new -$$Lambda$AlertsCreator$MyqgLv-vQ0gI1odORBtLNhHQEvI(intCallback));
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
            radioColorCell.setOnClickListener(new -$$Lambda$AlertsCreator$4zQOCx7szZg2PNvAzTA1bpbrFQA(iArr, linearLayout));
            i2++;
        }
        Builder builder = new Builder(context);
        builder.setTitle(LocaleController.getString("LowDiskSpaceTitle", NUM));
        builder.setMessage(LocaleController.getString("LowDiskSpaceMessage", NUM));
        builder.setView(linearLayout);
        builder.setPositiveButton(LocaleController.getString("OK", NUM), new -$$Lambda$AlertsCreator$KjNI7sT_C-bswPjwnMDRlnskBPc(iArr));
        builder.setNeutralButton(LocaleController.getString("ClearMediaCache", NUM), new -$$Lambda$AlertsCreator$Va_eEgh11ksr6bHiAG-lOGJYMp8(context));
        return builder.create();
    }

    static /* synthetic */ void lambda$createFreeSpaceDialog$43(int[] iArr, LinearLayout linearLayout, View view) {
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
            -$$Lambda$AlertsCreator$A8Ah-02R-GWIahgC-Bv-fH-0imo -__lambda_alertscreator_a8ah-02r-gwiahgc-bv-fh-0imo = r1;
            FrameLayout frameLayout = radioColorCell;
            int i7 = i6;
            Builder builder2 = builder;
            view = linearLayout;
            -$$Lambda$AlertsCreator$A8Ah-02R-GWIahgC-Bv-fH-0imo -__lambda_alertscreator_a8ah-02r-gwiahgc-bv-fh-0imo2 = new -$$Lambda$AlertsCreator$A8Ah-02R-GWIahgC-Bv-fH-0imo(iArr, j, i, notificationsSettings, builder, runnable);
            frameLayout.setOnClickListener(-__lambda_alertscreator_a8ah-02r-gwiahgc-bv-fh-0imo);
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

    static /* synthetic */ void lambda$createPrioritySelectDialog$46(int[] iArr, long j, int i, SharedPreferences sharedPreferences, Builder builder, Runnable runnable, View view) {
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
            radioColorCell.setOnClickListener(new -$$Lambda$AlertsCreator$nPpY9ibadcCLASSNAMEcml3wjF5p5_Vyg(iArr, i, builder, runnable));
            i2++;
        }
        builder.setTitle(LocaleController.getString("PopupNotification", NUM));
        builder.setView(linearLayout);
        builder.setPositiveButton(LocaleController.getString("Cancel", NUM), null);
        return builder.create();
    }

    static /* synthetic */ void lambda$createPopupSelectDialog$47(int[] iArr, int i, Builder builder, Runnable runnable, View view) {
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
            radioColorCell.setOnClickListener(new -$$Lambda$AlertsCreator$LWtnoVn16WLVqoU3zq1q2hohPjg(builder, onClickListener));
            i2++;
        }
        builder.setTitle(str);
        builder.setView(linearLayout);
        builder.setPositiveButton(LocaleController.getString("Cancel", NUM), null);
        return builder.create();
    }

    static /* synthetic */ void lambda$createSingleChoiceDialog$48(Builder builder, OnClickListener onClickListener, View view) {
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
        numberPicker.setFormatter(-$$Lambda$AlertsCreator$gGUuyj3b3BbVn9I3aKfktk8so9Q.INSTANCE);
        builder.setView(numberPicker);
        builder.setNegativeButton(LocaleController.getString("Done", NUM), new -$$Lambda$AlertsCreator$elY6BxYZd2w1Q3YdF1ndgHl4T4g(encryptedChat, numberPicker));
        return builder;
    }

    static /* synthetic */ String lambda$createTTLAlert$49(int i) {
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

    static /* synthetic */ void lambda$createTTLAlert$50(EncryptedChat encryptedChat, NumberPicker numberPicker, DialogInterface dialogInterface, int i) {
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
                accountSelectCell.setOnClickListener(new -$$Lambda$AlertsCreator$O9BULpaauaaWtGXQsuOhgCJ9ct0(alertDialogArr, dismissRunnable, accountSelectDelegate));
            }
        }
        builder.setTitle(LocaleController.getString("SelectAccount", NUM));
        builder.setView(linearLayout);
        builder.setPositiveButton(LocaleController.getString("Cancel", NUM), null);
        AlertDialog create = builder.create();
        alertDialogArr[0] = create;
        return create;
    }

    static /* synthetic */ void lambda$createAccountSelectDialog$51(AlertDialog[] alertDialogArr, Runnable runnable, AccountSelectDelegate accountSelectDelegate, View view) {
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
        if (r14 == 0) goto L_0x05e9;
    L_0x0010:
        if (r3 != 0) goto L_0x0018;
    L_0x0012:
        if (r4 != 0) goto L_0x0018;
    L_0x0014:
        if (r5 != 0) goto L_0x0018;
    L_0x0016:
        goto L_0x05e9;
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
        r10 = new org.telegram.ui.Components.-$$Lambda$AlertsCreator$PfqFazdn9NQ97ARqEHrnjPsSobk;
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
        r1 = new org.telegram.ui.Components.-$$Lambda$AlertsCreator$s2TKXlQWhiQaw3CtiAviNd-var_E;
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
        r13 = NUM; // 0x7f0e039b float:1.887691E38 double:1.0531626127E-314;
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
        r13 = NUM; // 0x7f0e03b1 float:1.8876954E38 double:1.0531626235E-314;
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
        r13 = NUM; // 0x7f0e0390 float:1.8876887E38 double:1.053162607E-314;
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
        r0 = new org.telegram.ui.Components.-$$Lambda$AlertsCreator$VV1-P4Ux4zkJ-q55i6EgDEwfAZA;
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
        r9 = NUM; // 0x7f0e03a9 float:1.8876938E38 double:1.0531626196E-314;
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
        r1 = new org.telegram.ui.Components.-$$Lambda$AlertsCreator$oLtqhGeaZR0ainTSrxnNxCm_SWk;
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
        r10 = NUM; // 0x7f0e03aa float:1.887694E38 double:1.05316262E-314;
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
        r10 = NUM; // 0x7f0e03a1 float:1.8876922E38 double:1.0531626156E-314;
        r13 = "DeleteForAll";
        r10 = org.telegram.messenger.LocaleController.getString(r13, r10);
        r6.setText(r10, r11, r1, r1);
        goto L_0x046b;
    L_0x045f:
        r10 = NUM; // 0x7f0e03a9 float:1.8876938E38 double:1.0531626196E-314;
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
        r1 = new org.telegram.ui.Components.-$$Lambda$AlertsCreator$4OHKaYeK4rq2SjIUdvqrdqZQdMA;
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
        r5 = NUM; // 0x7f0e038b float:1.8876877E38 double:1.0531626047E-314;
        r6 = "Delete";
        r5 = org.telegram.messenger.LocaleController.getString(r6, r5);
        r6 = new org.telegram.ui.Components.-$$Lambda$AlertsCreator$broXPqwNv8W5CV_H317xOPkk6GM;
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
        r9 = NUM; // 0x7f0e03b2 float:1.8876956E38 double:1.053162624E-314;
        r10 = "DeleteSingleMessagesTitle";
        r9 = org.telegram.messenger.LocaleController.getString(r10, r9);
        r0.setTitle(r9);
        goto L_0x050c;
    L_0x04f7:
        r9 = NUM; // 0x7f0e03ae float:1.8876948E38 double:1.053162622E-314;
        r10 = new java.lang.Object[r6];
        r6 = org.telegram.messenger.LocaleController.formatPluralString(r5, r8);
        r11 = 0;
        r10[r11] = r6;
        r6 = "DeleteMessagesTitle";
        r6 = org.telegram.messenger.LocaleController.formatString(r6, r9, r10);
        r0.setTitle(r6);
    L_0x050c:
        r6 = NUM; // 0x7f0e0136 float:1.8875666E38 double:1.05316231E-314;
        r9 = "AreYouSureDeleteSingleMessage";
        r10 = NUM; // 0x7f0e0130 float:1.8875654E38 double:1.053162307E-314;
        r11 = "AreYouSureDeleteFewMessages";
        if (r4 == 0) goto L_0x054b;
    L_0x0518:
        if (r3 == 0) goto L_0x054b;
    L_0x051a:
        if (r1 == 0) goto L_0x0536;
    L_0x051c:
        if (r7 == r8) goto L_0x0536;
    L_0x051e:
        r1 = NUM; // 0x7f0e03ad float:1.8876946E38 double:1.0531626215E-314;
        r3 = 1;
        r3 = new java.lang.Object[r3];
        r4 = org.telegram.messenger.LocaleController.formatPluralString(r5, r7);
        r5 = 0;
        r3[r5] = r4;
        r4 = "DeleteMessagesTextGroupPart";
        r1 = org.telegram.messenger.LocaleController.formatString(r4, r1, r3);
        r0.setMessage(r1);
        goto L_0x05c3;
    L_0x0536:
        r3 = 1;
        if (r8 != r3) goto L_0x0542;
    L_0x0539:
        r1 = org.telegram.messenger.LocaleController.getString(r9, r6);
        r0.setMessage(r1);
        goto L_0x05c3;
    L_0x0542:
        r1 = org.telegram.messenger.LocaleController.getString(r11, r10);
        r0.setMessage(r1);
        goto L_0x05c3;
    L_0x054b:
        if (r1 == 0) goto L_0x058c;
    L_0x054d:
        if (r31 != 0) goto L_0x058c;
    L_0x054f:
        if (r7 == r8) goto L_0x058c;
    L_0x0551:
        if (r4 == 0) goto L_0x056a;
    L_0x0553:
        r1 = NUM; // 0x7f0e03ac float:1.8876944E38 double:1.053162621E-314;
        r3 = 1;
        r3 = new java.lang.Object[r3];
        r4 = org.telegram.messenger.LocaleController.formatPluralString(r5, r7);
        r6 = 0;
        r3[r6] = r4;
        r4 = "DeleteMessagesTextGroup";
        r1 = org.telegram.messenger.LocaleController.formatString(r4, r1, r3);
        r0.setMessage(r1);
        goto L_0x05c3;
    L_0x056a:
        r6 = 0;
        r1 = NUM; // 0x7f0e03ab float:1.8876942E38 double:1.0531626206E-314;
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
        goto L_0x05c3;
    L_0x058c:
        if (r4 == 0) goto L_0x05b1;
    L_0x058e:
        r1 = r4.megagroup;
        if (r1 == 0) goto L_0x05b1;
    L_0x0592:
        if (r44 != 0) goto L_0x05b1;
    L_0x0594:
        r1 = 1;
        if (r8 != r1) goto L_0x05a4;
    L_0x0597:
        r1 = NUM; // 0x7f0e0137 float:1.8875668E38 double:1.0531623103E-314;
        r3 = "AreYouSureDeleteSingleMessageMega";
        r1 = org.telegram.messenger.LocaleController.getString(r3, r1);
        r0.setMessage(r1);
        goto L_0x05c3;
    L_0x05a4:
        r1 = NUM; // 0x7f0e0131 float:1.8875656E38 double:1.0531623073E-314;
        r3 = "AreYouSureDeleteFewMessagesMega";
        r1 = org.telegram.messenger.LocaleController.getString(r3, r1);
        r0.setMessage(r1);
        goto L_0x05c3;
    L_0x05b1:
        r1 = 1;
        if (r8 != r1) goto L_0x05bc;
    L_0x05b4:
        r1 = org.telegram.messenger.LocaleController.getString(r9, r6);
        r0.setMessage(r1);
        goto L_0x05c3;
    L_0x05bc:
        r1 = org.telegram.messenger.LocaleController.getString(r11, r10);
        r0.setMessage(r1);
    L_0x05c3:
        r1 = NUM; // 0x7f0e021c float:1.8876133E38 double:1.0531624234E-314;
        r3 = "Cancel";
        r1 = org.telegram.messenger.LocaleController.getString(r3, r1);
        r3 = 0;
        r0.setNegativeButton(r1, r3);
        r0 = r0.create();
        r2.showDialog(r0);
        r1 = -1;
        r0 = r0.getButton(r1);
        r0 = (android.widget.TextView) r0;
        if (r0 == 0) goto L_0x05e9;
    L_0x05e0:
        r1 = "dialogTextRed2";
        r1 = org.telegram.ui.ActionBar.Theme.getColor(r1);
        r0.setTextColor(r1);
    L_0x05e9:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.AlertsCreator.createDeleteMessagesAlert(org.telegram.ui.ActionBar.BaseFragment, org.telegram.tgnet.TLRPC$User, org.telegram.tgnet.TLRPC$Chat, org.telegram.tgnet.TLRPC$EncryptedChat, org.telegram.tgnet.TLRPC$ChatFull, long, org.telegram.messenger.MessageObject, android.util.SparseArray[], org.telegram.messenger.MessageObject$GroupedMessages, boolean, int, java.lang.Runnable):void");
    }

    static /* synthetic */ void lambda$null$52(AlertDialog[] alertDialogArr, TLObject tLObject, BaseFragment baseFragment, User user, Chat chat, EncryptedChat encryptedChat, ChatFull chatFull, long j, MessageObject messageObject, SparseArray[] sparseArrayArr, GroupedMessages groupedMessages, boolean z, Runnable runnable) {
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

    static /* synthetic */ void lambda$createDeleteMessagesAlert$55(AlertDialog[] alertDialogArr, int i, int i2, BaseFragment baseFragment) {
        if (alertDialogArr[0] != null) {
            alertDialogArr[0].setOnCancelListener(new -$$Lambda$AlertsCreator$nZ0vhjin-DIdWeCiq6nwyI2M8P0(i, i2));
            baseFragment.showDialog(alertDialogArr[0]);
        }
    }

    static /* synthetic */ void lambda$createDeleteMessagesAlert$56(boolean[] zArr, View view) {
        if (view.isEnabled()) {
            CheckBoxCell checkBoxCell = (CheckBoxCell) view;
            Integer num = (Integer) checkBoxCell.getTag();
            zArr[num.intValue()] = zArr[num.intValue()] ^ 1;
            checkBoxCell.setChecked(zArr[num.intValue()], true);
        }
    }

    static /* synthetic */ void lambda$createDeleteMessagesAlert$57(boolean[] zArr, View view) {
        CheckBoxCell checkBoxCell = (CheckBoxCell) view;
        zArr[0] = zArr[0] ^ 1;
        checkBoxCell.setChecked(zArr[0], true);
    }

    static /* synthetic */ void lambda$createDeleteMessagesAlert$58(boolean[] zArr, View view) {
        CheckBoxCell checkBoxCell = (CheckBoxCell) view;
        zArr[0] = zArr[0] ^ 1;
        checkBoxCell.setChecked(zArr[0], true);
    }

    /* JADX WARNING: Removed duplicated region for block: B:49:0x0116  */
    /* JADX WARNING: Removed duplicated region for block: B:39:0x00e8  */
    static /* synthetic */ void lambda$createDeleteMessagesAlert$60(org.telegram.messenger.MessageObject r19, org.telegram.messenger.MessageObject.GroupedMessages r20, org.telegram.tgnet.TLRPC.EncryptedChat r21, int r22, long r23, boolean[] r25, boolean r26, android.util.SparseArray[] r27, org.telegram.tgnet.TLRPC.User r28, boolean[] r29, org.telegram.tgnet.TLRPC.Chat r30, org.telegram.tgnet.TLRPC.ChatFull r31, java.lang.Runnable r32, android.content.DialogInterface r33, int r34) {
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
        r2 = org.telegram.ui.Components.-$$Lambda$AlertsCreator$A22Jxfm5gYlBAKThQRHubUhFEUI.INSTANCE;
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
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.AlertsCreator.lambda$createDeleteMessagesAlert$60(org.telegram.messenger.MessageObject, org.telegram.messenger.MessageObject$GroupedMessages, org.telegram.tgnet.TLRPC$EncryptedChat, int, long, boolean[], boolean, android.util.SparseArray[], org.telegram.tgnet.TLRPC$User, boolean[], org.telegram.tgnet.TLRPC$Chat, org.telegram.tgnet.TLRPC$ChatFull, java.lang.Runnable, android.content.DialogInterface, int):void");
    }

    public static void createThemeCreateDialog(BaseFragment baseFragment, int i, ThemeInfo themeInfo, ThemeAccent themeAccent) {
        BaseFragment baseFragment2 = baseFragment;
        if (baseFragment2 != null && baseFragment.getParentActivity() != null) {
            Context parentActivity = baseFragment.getParentActivity();
            EditTextBoldCursor editTextBoldCursor = new EditTextBoldCursor(parentActivity);
            editTextBoldCursor.setBackgroundDrawable(Theme.createEditTextDrawable(parentActivity, true));
            Builder builder = new Builder(parentActivity);
            builder.setTitle(LocaleController.getString("NewTheme", NUM));
            builder.setNegativeButton(LocaleController.getString("Cancel", NUM), null);
            builder.setPositiveButton(LocaleController.getString("Create", NUM), -$$Lambda$AlertsCreator$0QtuIWIKEN8KFAWtr5Uy2CbfW1o.INSTANCE);
            LinearLayout linearLayout = new LinearLayout(parentActivity);
            linearLayout.setOrientation(1);
            builder.setView(linearLayout);
            TextView textView = new TextView(parentActivity);
            if (i != 0) {
                textView.setText(AndroidUtilities.replaceTags(LocaleController.getString("EnterThemeNameEdit", NUM)));
            } else {
                textView.setText(LocaleController.getString("EnterThemeName", NUM));
            }
            textView.setTextSize(16.0f);
            textView.setPadding(AndroidUtilities.dp(23.0f), AndroidUtilities.dp(12.0f), AndroidUtilities.dp(23.0f), AndroidUtilities.dp(6.0f));
            String str = "dialogTextBlack";
            textView.setTextColor(Theme.getColor(str));
            linearLayout.addView(textView, LayoutHelper.createLinear(-1, -2));
            editTextBoldCursor.setTextSize(1, 16.0f);
            editTextBoldCursor.setTextColor(Theme.getColor(str));
            editTextBoldCursor.setMaxLines(1);
            editTextBoldCursor.setLines(1);
            editTextBoldCursor.setInputType(16385);
            editTextBoldCursor.setGravity(51);
            editTextBoldCursor.setSingleLine(true);
            editTextBoldCursor.setImeOptions(6);
            editTextBoldCursor.setCursorColor(Theme.getColor("windowBackgroundWhiteBlackText"));
            editTextBoldCursor.setCursorSize(AndroidUtilities.dp(20.0f));
            editTextBoldCursor.setCursorWidth(1.5f);
            editTextBoldCursor.setPadding(0, AndroidUtilities.dp(4.0f), 0, 0);
            linearLayout.addView(editTextBoldCursor, LayoutHelper.createLinear(-1, 36, 51, 24, 6, 24, 0));
            editTextBoldCursor.setOnEditorActionListener(-$$Lambda$AlertsCreator$DJqb4RTx9GX9dj02-QDzvL2X5Jo.INSTANCE);
            editTextBoldCursor.setText(generateThemeName(themeAccent));
            editTextBoldCursor.setSelection(editTextBoldCursor.length());
            AlertDialog create = builder.create();
            create.setOnShowListener(new -$$Lambda$AlertsCreator$dywCIYGCwAk2dvnBomvTJlKTZmI(editTextBoldCursor));
            baseFragment2.showDialog(create);
            editTextBoldCursor.requestFocus();
            create.getButton(-1).setOnClickListener(new -$$Lambda$AlertsCreator$kY_IuBuIRtt9K0_qm7YfE68TTG8(baseFragment, editTextBoldCursor, themeAccent, themeInfo, create));
        }
    }

    static /* synthetic */ void lambda$null$63(EditTextBoldCursor editTextBoldCursor) {
        editTextBoldCursor.requestFocus();
        AndroidUtilities.showKeyboard(editTextBoldCursor);
    }

    static /* synthetic */ void lambda$createThemeCreateDialog$67(BaseFragment baseFragment, EditTextBoldCursor editTextBoldCursor, ThemeAccent themeAccent, ThemeInfo themeInfo, AlertDialog alertDialog, View view) {
        if (baseFragment.getParentActivity() != null) {
            if (editTextBoldCursor.length() == 0) {
                Vibrator vibrator = (Vibrator) ApplicationLoader.applicationContext.getSystemService("vibrator");
                if (vibrator != null) {
                    vibrator.vibrate(200);
                }
                AndroidUtilities.shakeView(editTextBoldCursor, 2.0f, 0);
                return;
            }
            if (baseFragment instanceof ThemePreviewActivity) {
                Theme.applyPreviousTheme();
                baseFragment.finishFragment();
            }
            if (themeAccent != null) {
                themeInfo.setCurrentAccentId(themeAccent.id);
                Theme.refreshThemeColors();
                Utilities.searchQueue.postRunnable(new -$$Lambda$AlertsCreator$1jvar_OO0E8-EZLKIOCErJX2WIT8(editTextBoldCursor, alertDialog, baseFragment));
                return;
            }
            processCreate(editTextBoldCursor, alertDialog, baseFragment);
        }
    }

    private static void processCreate(EditTextBoldCursor editTextBoldCursor, AlertDialog alertDialog, BaseFragment baseFragment) {
        if (!(baseFragment == null || baseFragment.getParentActivity() == null)) {
            AndroidUtilities.hideKeyboard(editTextBoldCursor);
            ThemeInfo createNewTheme = Theme.createNewTheme(editTextBoldCursor.getText().toString());
            NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.themeListUpdated, new Object[0]);
            new ThemeEditorView().show(baseFragment.getParentActivity(), createNewTheme);
            alertDialog.dismiss();
            SharedPreferences globalMainSettings = MessagesController.getGlobalMainSettings();
            String str = "themehint";
            if (!globalMainSettings.getBoolean(str, false)) {
                globalMainSettings.edit().putBoolean(str, true).commit();
                try {
                    Toast.makeText(baseFragment.getParentActivity(), LocaleController.getString("CreateNewThemeHelp", NUM), 1).show();
                } catch (Exception e) {
                    FileLog.e(e);
                }
            }
        }
    }

    /* JADX WARNING: Missing block: B:6:0x0906, code skipped:
            if (r5 != 0) goto L_0x0913;
     */
    private static java.lang.String generateThemeName(org.telegram.ui.ActionBar.Theme.ThemeAccent r18) {
        /*
        r0 = 107; // 0x6b float:1.5E-43 double:5.3E-322;
        r0 = new java.lang.String[r0];
        r1 = 0;
        r2 = "Ancient";
        r0[r1] = r2;
        r2 = 1;
        r3 = "Antique";
        r0[r2] = r3;
        r3 = 2;
        r4 = "Autumn";
        r0[r3] = r4;
        r4 = 3;
        r5 = "Baby";
        r0[r4] = r5;
        r5 = 4;
        r6 = "Barely";
        r0[r5] = r6;
        r6 = 5;
        r7 = "Baroque";
        r0[r6] = r7;
        r7 = 6;
        r8 = "Blazing";
        r0[r7] = r8;
        r8 = 7;
        r9 = "Blushing";
        r0[r8] = r9;
        r9 = 8;
        r10 = "Bohemian";
        r0[r9] = r10;
        r10 = 9;
        r11 = "Bubbly";
        r0[r10] = r11;
        r11 = 10;
        r12 = "Burning";
        r0[r11] = r12;
        r12 = 11;
        r13 = "Buttered";
        r0[r12] = r13;
        r13 = 12;
        r14 = "Classic";
        r0[r13] = r14;
        r14 = 13;
        r15 = "Clear";
        r0[r14] = r15;
        r15 = 14;
        r16 = "Cool";
        r0[r15] = r16;
        r16 = 15;
        r17 = "Cosmic";
        r0[r16] = r17;
        r16 = 16;
        r17 = "Cotton";
        r0[r16] = r17;
        r16 = 17;
        r17 = "Cozy";
        r0[r16] = r17;
        r16 = 18;
        r17 = "Crystal";
        r0[r16] = r17;
        r16 = 19;
        r17 = "Dark";
        r0[r16] = r17;
        r16 = 20;
        r17 = "Daring";
        r0[r16] = r17;
        r16 = 21;
        r17 = "Darling";
        r0[r16] = r17;
        r16 = 22;
        r17 = "Dawn";
        r0[r16] = r17;
        r16 = 23;
        r17 = "Dazzling";
        r0[r16] = r17;
        r16 = 24;
        r17 = "Deep";
        r0[r16] = r17;
        r16 = 25;
        r17 = "Deepest";
        r0[r16] = r17;
        r16 = 26;
        r17 = "Delicate";
        r0[r16] = r17;
        r16 = 27;
        r17 = "Delightful";
        r0[r16] = r17;
        r16 = 28;
        r17 = "Divine";
        r0[r16] = r17;
        r16 = 29;
        r17 = "Double";
        r0[r16] = r17;
        r16 = 30;
        r17 = "Downtown";
        r0[r16] = r17;
        r16 = 31;
        r17 = "Dreamy";
        r0[r16] = r17;
        r16 = 32;
        r17 = "Dusky";
        r0[r16] = r17;
        r16 = 33;
        r17 = "Dusty";
        r0[r16] = r17;
        r16 = 34;
        r17 = "Electric";
        r0[r16] = r17;
        r16 = 35;
        r17 = "Enchanted";
        r0[r16] = r17;
        r16 = 36;
        r17 = "Endless";
        r0[r16] = r17;
        r16 = 37;
        r17 = "Evening";
        r0[r16] = r17;
        r16 = 38;
        r17 = "Fantastic";
        r0[r16] = r17;
        r16 = 39;
        r17 = "Flirty";
        r0[r16] = r17;
        r16 = 40;
        r17 = "Forever";
        r0[r16] = r17;
        r16 = 41;
        r17 = "Frigid";
        r0[r16] = r17;
        r16 = 42;
        r17 = "Frosty";
        r0[r16] = r17;
        r16 = 43;
        r17 = "Frozen";
        r0[r16] = r17;
        r16 = 44;
        r17 = "Gentle";
        r0[r16] = r17;
        r16 = 45;
        r17 = "Heavenly";
        r0[r16] = r17;
        r16 = 46;
        r17 = "Hyper";
        r0[r16] = r17;
        r16 = 47;
        r17 = "Icy";
        r0[r16] = r17;
        r16 = 48;
        r17 = "Infinite";
        r0[r16] = r17;
        r16 = 49;
        r17 = "Innocent";
        r0[r16] = r17;
        r16 = 50;
        r17 = "Instant";
        r0[r16] = r17;
        r16 = 51;
        r17 = "Luscious";
        r0[r16] = r17;
        r16 = 52;
        r17 = "Lunar";
        r0[r16] = r17;
        r16 = 53;
        r17 = "Lustrous";
        r0[r16] = r17;
        r16 = 54;
        r17 = "Magic";
        r0[r16] = r17;
        r16 = 55;
        r17 = "Majestic";
        r0[r16] = r17;
        r16 = 56;
        r17 = "Mambo";
        r0[r16] = r17;
        r16 = 57;
        r17 = "Midnight";
        r0[r16] = r17;
        r16 = 58;
        r17 = "Millenium";
        r0[r16] = r17;
        r16 = 59;
        r17 = "Morning";
        r0[r16] = r17;
        r16 = 60;
        r17 = "Mystic";
        r0[r16] = r17;
        r16 = 61;
        r17 = "Natural";
        r0[r16] = r17;
        r16 = 62;
        r17 = "Neon";
        r0[r16] = r17;
        r16 = 63;
        r17 = "Night";
        r0[r16] = r17;
        r16 = 64;
        r17 = "Opaque";
        r0[r16] = r17;
        r16 = 65;
        r17 = "Paradise";
        r0[r16] = r17;
        r16 = 66;
        r17 = "Perfect";
        r0[r16] = r17;
        r16 = 67;
        r17 = "Perky";
        r0[r16] = r17;
        r16 = 68;
        r17 = "Polished";
        r0[r16] = r17;
        r16 = 69;
        r17 = "Powerful";
        r0[r16] = r17;
        r16 = 70;
        r17 = "Rich";
        r0[r16] = r17;
        r16 = 71;
        r17 = "Royal";
        r0[r16] = r17;
        r16 = 72;
        r17 = "Sheer";
        r0[r16] = r17;
        r16 = 73;
        r17 = "Simply";
        r0[r16] = r17;
        r16 = 74;
        r17 = "Sizzling";
        r0[r16] = r17;
        r16 = 75;
        r17 = "Solar";
        r0[r16] = r17;
        r16 = 76;
        r17 = "Sparkling";
        r0[r16] = r17;
        r16 = 77;
        r17 = "Splendid";
        r0[r16] = r17;
        r16 = 78;
        r17 = "Spicy";
        r0[r16] = r17;
        r16 = 79;
        r17 = "Spring";
        r0[r16] = r17;
        r16 = 80;
        r17 = "Stellar";
        r0[r16] = r17;
        r16 = 81;
        r17 = "Sugared";
        r0[r16] = r17;
        r16 = 82;
        r17 = "Summer";
        r0[r16] = r17;
        r16 = 83;
        r17 = "Sunny";
        r0[r16] = r17;
        r16 = 84;
        r17 = "Super";
        r0[r16] = r17;
        r16 = 85;
        r17 = "Sweet";
        r0[r16] = r17;
        r16 = 86;
        r17 = "Tender";
        r0[r16] = r17;
        r16 = 87;
        r17 = "Tenacious";
        r0[r16] = r17;
        r16 = 88;
        r17 = "Tidal";
        r0[r16] = r17;
        r16 = 89;
        r17 = "Toasted";
        r0[r16] = r17;
        r16 = 90;
        r17 = "Totally";
        r0[r16] = r17;
        r16 = 91;
        r17 = "Tranquil";
        r0[r16] = r17;
        r16 = 92;
        r17 = "Tropical";
        r0[r16] = r17;
        r16 = 93;
        r17 = "True";
        r0[r16] = r17;
        r16 = 94;
        r17 = "Twilight";
        r0[r16] = r17;
        r16 = 95;
        r17 = "Twinkling";
        r0[r16] = r17;
        r16 = 96;
        r17 = "Ultimate";
        r0[r16] = r17;
        r16 = 97;
        r17 = "Ultra";
        r0[r16] = r17;
        r16 = 98;
        r17 = "Velvety";
        r0[r16] = r17;
        r16 = 99;
        r17 = "Vibrant";
        r0[r16] = r17;
        r16 = 100;
        r17 = "Vintage";
        r0[r16] = r17;
        r16 = 101; // 0x65 float:1.42E-43 double:5.0E-322;
        r17 = "Virtual";
        r0[r16] = r17;
        r16 = 102; // 0x66 float:1.43E-43 double:5.04E-322;
        r17 = "Warm";
        r0[r16] = r17;
        r16 = 103; // 0x67 float:1.44E-43 double:5.1E-322;
        r17 = "Warmest";
        r0[r16] = r17;
        r16 = 104; // 0x68 float:1.46E-43 double:5.14E-322;
        r17 = "Whipped";
        r0[r16] = r17;
        r16 = 105; // 0x69 float:1.47E-43 double:5.2E-322;
        r17 = "Wild";
        r0[r16] = r17;
        r16 = 106; // 0x6a float:1.49E-43 double:5.24E-322;
        r17 = "Winsome";
        r0[r16] = r17;
        r0 = java.util.Arrays.asList(r0);
        r15 = 81;
        r15 = new java.lang.String[r15];
        r17 = "Ambrosia";
        r15[r1] = r17;
        r17 = "Attack";
        r15[r2] = r17;
        r2 = "Avalanche";
        r15[r3] = r2;
        r2 = "Blast";
        r15[r4] = r2;
        r2 = "Bliss";
        r15[r5] = r2;
        r2 = "Blossom";
        r15[r6] = r2;
        r2 = "Blush";
        r15[r7] = r2;
        r2 = "Burst";
        r15[r8] = r2;
        r2 = "Butter";
        r15[r9] = r2;
        r2 = "Candy";
        r15[r10] = r2;
        r2 = "Carnival";
        r15[r11] = r2;
        r2 = "Charm";
        r15[r12] = r2;
        r2 = "Chiffon";
        r15[r13] = r2;
        r2 = "Cloud";
        r15[r14] = r2;
        r2 = "Comet";
        r4 = 14;
        r15[r4] = r2;
        r2 = 15;
        r4 = "Delight";
        r15[r2] = r4;
        r2 = 16;
        r4 = "Dream";
        r15[r2] = r4;
        r2 = 17;
        r4 = "Dust";
        r15[r2] = r4;
        r2 = 18;
        r4 = "Fantasy";
        r15[r2] = r4;
        r2 = 19;
        r4 = "Flame";
        r15[r2] = r4;
        r2 = 20;
        r4 = "Flash";
        r15[r2] = r4;
        r2 = 21;
        r4 = "Fire";
        r15[r2] = r4;
        r2 = 22;
        r4 = "Freeze";
        r15[r2] = r4;
        r2 = 23;
        r4 = "Frost";
        r15[r2] = r4;
        r2 = 24;
        r4 = "Glade";
        r15[r2] = r4;
        r2 = 25;
        r4 = "Glaze";
        r15[r2] = r4;
        r2 = 26;
        r4 = "Gleam";
        r15[r2] = r4;
        r2 = 27;
        r4 = "Glimmer";
        r15[r2] = r4;
        r2 = 28;
        r4 = "Glitter";
        r15[r2] = r4;
        r2 = 29;
        r4 = "Glow";
        r15[r2] = r4;
        r2 = 30;
        r4 = "Grande";
        r15[r2] = r4;
        r2 = 31;
        r4 = "Haze";
        r15[r2] = r4;
        r2 = 32;
        r4 = "Highlight";
        r15[r2] = r4;
        r2 = 33;
        r4 = "Ice";
        r15[r2] = r4;
        r2 = 34;
        r4 = "Illusion";
        r15[r2] = r4;
        r2 = 35;
        r4 = "Intrigue";
        r15[r2] = r4;
        r2 = 36;
        r4 = "Jewel";
        r15[r2] = r4;
        r2 = 37;
        r4 = "Jubilee";
        r15[r2] = r4;
        r2 = 38;
        r4 = "Kiss";
        r15[r2] = r4;
        r2 = 39;
        r4 = "Lights";
        r15[r2] = r4;
        r2 = 40;
        r4 = "Lollypop";
        r15[r2] = r4;
        r2 = 41;
        r4 = "Love";
        r15[r2] = r4;
        r2 = 42;
        r4 = "Luster";
        r15[r2] = r4;
        r2 = 43;
        r4 = "Madness";
        r15[r2] = r4;
        r2 = 44;
        r4 = "Matte";
        r15[r2] = r4;
        r2 = 45;
        r4 = "Mirage";
        r15[r2] = r4;
        r2 = 46;
        r4 = "Mist";
        r15[r2] = r4;
        r2 = 47;
        r4 = "Moon";
        r15[r2] = r4;
        r2 = 48;
        r4 = "Muse";
        r15[r2] = r4;
        r2 = 49;
        r4 = "Myth";
        r15[r2] = r4;
        r2 = 50;
        r4 = "Nectar";
        r15[r2] = r4;
        r2 = 51;
        r4 = "Nova";
        r15[r2] = r4;
        r2 = 52;
        r4 = "Parfait";
        r15[r2] = r4;
        r2 = 53;
        r4 = "Passion";
        r15[r2] = r4;
        r2 = 54;
        r4 = "Pop";
        r15[r2] = r4;
        r2 = 55;
        r4 = "Rain";
        r15[r2] = r4;
        r2 = 56;
        r4 = "Reflection";
        r15[r2] = r4;
        r2 = 57;
        r4 = "Rhapsody";
        r15[r2] = r4;
        r2 = 58;
        r4 = "Romance";
        r15[r2] = r4;
        r2 = 59;
        r4 = "Satin";
        r15[r2] = r4;
        r2 = 60;
        r4 = "Sensation";
        r15[r2] = r4;
        r2 = 61;
        r4 = "Silk";
        r15[r2] = r4;
        r2 = 62;
        r4 = "Shine";
        r15[r2] = r4;
        r2 = 63;
        r4 = "Shadow";
        r15[r2] = r4;
        r2 = 64;
        r4 = "Shimmer";
        r15[r2] = r4;
        r2 = 65;
        r4 = "Sky";
        r15[r2] = r4;
        r2 = 66;
        r4 = "Spice";
        r15[r2] = r4;
        r2 = 67;
        r4 = "Star";
        r15[r2] = r4;
        r2 = 68;
        r4 = "Sugar";
        r15[r2] = r4;
        r2 = 69;
        r4 = "Sunrise";
        r15[r2] = r4;
        r2 = 70;
        r4 = "Sunset";
        r15[r2] = r4;
        r2 = 71;
        r4 = "Sun";
        r15[r2] = r4;
        r2 = 72;
        r4 = "Twist";
        r15[r2] = r4;
        r2 = 73;
        r4 = "Unbound";
        r15[r2] = r4;
        r2 = 74;
        r4 = "Velvet";
        r15[r2] = r4;
        r2 = 75;
        r4 = "Vibrant";
        r15[r2] = r4;
        r2 = 76;
        r4 = "Waters";
        r15[r2] = r4;
        r2 = 77;
        r4 = "Wine";
        r15[r2] = r4;
        r2 = 78;
        r4 = "Wink";
        r15[r2] = r4;
        r2 = 79;
        r4 = "Wonder";
        r15[r2] = r4;
        r2 = 80;
        r4 = "Zone";
        r15[r2] = r4;
        r2 = java.util.Arrays.asList(r15);
        r4 = new java.util.HashMap;
        r4.<init>();
        r5 = 9306112; // 0x8e0000 float:1.304064E-38 double:4.59783E-317;
        r5 = java.lang.Integer.valueOf(r5);
        r6 = "Berry";
        r4.put(r5, r6);
        r5 = 14598550; // 0xdeCLASSNAME float:2.0456926E-38 double:7.212642E-317;
        r5 = java.lang.Integer.valueOf(r5);
        r6 = "Brandy";
        r4.put(r5, r6);
        r5 = 8391495; // 0x800b47 float:1.1758989E-38 double:4.1459494E-317;
        r5 = java.lang.Integer.valueOf(r5);
        r6 = "Cherry";
        r4.put(r5, r6);
        r5 = 16744272; // 0xff7var_ float:2.3463723E-38 double:8.2727696E-317;
        r5 = java.lang.Integer.valueOf(r5);
        r6 = "Coral";
        r4.put(r5, r6);
        r5 = 14372985; // 0xdb5079 float:2.0140842E-38 double:7.101198E-317;
        r5 = java.lang.Integer.valueOf(r5);
        r6 = "Cranberry";
        r4.put(r5, r6);
        r5 = 14423100; // 0xdCLASSNAMEc float:2.0211068E-38 double:7.125958E-317;
        r5 = java.lang.Integer.valueOf(r5);
        r6 = "Crimson";
        r4.put(r5, r6);
        r5 = 14725375; // 0xe0b0ff float:2.0634645E-38 double:7.275302E-317;
        r5 = java.lang.Integer.valueOf(r5);
        r6 = "Mauve";
        r4.put(r5, r6);
        r5 = 16761035; // 0xffc0cb float:2.3487213E-38 double:8.2810516E-317;
        r5 = java.lang.Integer.valueOf(r5);
        r6 = "Pink";
        r4.put(r5, r6);
        r5 = 16711680; // 0xfvar_ float:2.3418052E-38 double:8.256667E-317;
        r5 = java.lang.Integer.valueOf(r5);
        r6 = "Red";
        r4.put(r5, r6);
        r5 = 16711807; // 0xfvar_f float:2.341823E-38 double:8.2567297E-317;
        r5 = java.lang.Integer.valueOf(r5);
        r6 = "Rose";
        r4.put(r5, r6);
        r5 = 8406555; // 0x80461b float:1.1780093E-38 double:4.15339E-317;
        r5 = java.lang.Integer.valueOf(r5);
        r6 = "Russet";
        r4.put(r5, r6);
        r5 = 16720896; // 0xfvar_ float:2.3430966E-38 double:8.2612203E-317;
        r5 = java.lang.Integer.valueOf(r5);
        r6 = "Scarlet";
        r4.put(r5, r6);
        r5 = 15856113; // 0xf1f1f1 float:2.2219147E-38 double:7.8339607E-317;
        r5 = java.lang.Integer.valueOf(r5);
        r6 = "Seashell";
        r4.put(r5, r6);
        r5 = 16724889; // 0xfvar_ float:2.3436561E-38 double:8.263193E-317;
        r5 = java.lang.Integer.valueOf(r5);
        r6 = "Strawberry";
        r4.put(r5, r6);
        r5 = 16760576; // 0xffbvar_ float:2.348657E-38 double:8.280825E-317;
        r5 = java.lang.Integer.valueOf(r5);
        r6 = "Amber";
        r4.put(r5, r6);
        r5 = 15438707; // 0xeb9373 float:2.1634236E-38 double:7.6277347E-317;
        r5 = java.lang.Integer.valueOf(r5);
        r6 = "Apricot";
        r4.put(r5, r6);
        r5 = 16508850; // 0xfbe7b2 float:2.3133826E-38 double:8.1564556E-317;
        r5 = java.lang.Integer.valueOf(r5);
        r6 = "Banana";
        r4.put(r5, r6);
        r5 = 10601738; // 0xa1CLASSNAMEa float:1.4856199E-38 double:5.2379545E-317;
        r5 = java.lang.Integer.valueOf(r5);
        r6 = "Citrus";
        r4.put(r5, r6);
        r5 = 11560192; // 0xb06500 float:1.6199279E-38 double:5.7114937E-317;
        r5 = java.lang.Integer.valueOf(r5);
        r6 = "Ginger";
        r4.put(r5, r6);
        r5 = 16766720; // 0xffd700 float:2.3495179E-38 double:8.2838603E-317;
        r5 = java.lang.Integer.valueOf(r5);
        r6 = "Gold";
        r4.put(r5, r6);
        r5 = 16640272; // 0xfde910 float:2.3317988E-38 double:8.2213867E-317;
        r5 = java.lang.Integer.valueOf(r5);
        r6 = "Lemon";
        r4.put(r5, r6);
        r5 = 16753920; // 0xffa500 float:2.3477242E-38 double:8.2775363E-317;
        r5 = java.lang.Integer.valueOf(r5);
        r6 = "Orange";
        r4.put(r5, r6);
        r5 = 16770484; // 0xffe5b4 float:2.3500453E-38 double:8.28572E-317;
        r5 = java.lang.Integer.valueOf(r5);
        r6 = "Peach";
        r4.put(r5, r6);
        r5 = 16739155; // 0xff6b53 float:2.3456552E-38 double:8.2702414E-317;
        r5 = java.lang.Integer.valueOf(r5);
        r6 = "Persimmon";
        r4.put(r5, r6);
        r5 = 14996514; // 0xe4d422 float:2.1014592E-38 double:7.4092624E-317;
        r5 = java.lang.Integer.valueOf(r5);
        r6 = "Sunflower";
        r4.put(r5, r6);
        r5 = 15893760; // 0xvar_ float:2.2271901E-38 double:7.852561E-317;
        r5 = java.lang.Integer.valueOf(r5);
        r6 = "Tangerine";
        r4.put(r5, r6);
        r5 = 16763004; // 0xffCLASSNAMEc float:2.3489972E-38 double:8.2820244E-317;
        r5 = java.lang.Integer.valueOf(r5);
        r6 = "Topaz";
        r4.put(r5, r6);
        r5 = 16776960; // 0xfffvar_ float:2.3509528E-38 double:8.2889196E-317;
        r5 = java.lang.Integer.valueOf(r5);
        r6 = "Yellow";
        r4.put(r5, r6);
        r5 = 3688720; // 0x384910 float:5.168998E-39 double:1.82247E-317;
        r5 = java.lang.Integer.valueOf(r5);
        r6 = "Clover";
        r4.put(r5, r6);
        r5 = 8628829; // 0x83aa5d float:1.2091565E-38 double:4.263208E-317;
        r5 = java.lang.Integer.valueOf(r5);
        r6 = "Cucumber";
        r4.put(r5, r6);
        r5 = 5294200; // 0x50CLASSNAME float:7.418754E-39 double:2.6156823E-317;
        r5 = java.lang.Integer.valueOf(r5);
        r6 = "Emerald";
        r4.put(r5, r6);
        r5 = 11907932; // 0xb5b35c float:1.6686567E-38 double:5.8833E-317;
        r5 = java.lang.Integer.valueOf(r5);
        r6 = "Olive";
        r4.put(r5, r6);
        r5 = 65280; // 0xfvar_ float:9.1477E-41 double:3.22526E-319;
        r5 = java.lang.Integer.valueOf(r5);
        r6 = "Green";
        r4.put(r5, r6);
        r5 = 43115; // 0xa86b float:6.0417E-41 double:2.13016E-319;
        r5 = java.lang.Integer.valueOf(r5);
        r6 = "Jade";
        r4.put(r5, r6);
        r5 = 2730887; // 0x29ab87 float:3.826788E-39 double:1.3492374E-317;
        r5 = java.lang.Integer.valueOf(r5);
        r6 = "Jungle";
        r4.put(r5, r6);
        r5 = 12582656; // 0xbffvar_ float:1.7632057E-38 double:6.216658E-317;
        r5 = java.lang.Integer.valueOf(r5);
        r6 = "Lime";
        r4.put(r5, r6);
        r5 = 776785; // 0xbda51 float:1.088508E-39 double:3.83783E-318;
        r5 = java.lang.Integer.valueOf(r5);
        r6 = "Malachite";
        r4.put(r5, r6);
        r5 = 10026904; // 0x98fvar_ float:1.4050685E-38 double:4.953949E-317;
        r5 = java.lang.Integer.valueOf(r5);
        r6 = "Mint";
        r4.put(r5, r6);
        r5 = 11394989; // 0xaddfad float:1.596778E-38 double:5.6298726E-317;
        r5 = java.lang.Integer.valueOf(r5);
        r6 = "Moss";
        r4.put(r5, r6);
        r5 = 3234721; // 0x315ba1 float:4.53281E-39 double:1.5981645E-317;
        r5 = java.lang.Integer.valueOf(r5);
        r6 = "Azure";
        r4.put(r5, r6);
        r5 = 255; // 0xff float:3.57E-43 double:1.26E-321;
        r5 = java.lang.Integer.valueOf(r5);
        r6 = "Blue";
        r4.put(r5, r6);
        r5 = 18347; // 0x47ab float:2.571E-41 double:9.0646E-320;
        r5 = java.lang.Integer.valueOf(r5);
        r6 = "Cobalt";
        r4.put(r5, r6);
        r5 = 5204422; // 0x4var_c6 float:7.292949E-39 double:2.571326E-317;
        r5 = java.lang.Integer.valueOf(r5);
        r6 = "Indigo";
        r4.put(r5, r6);
        r5 = 96647; // 0x17987 float:1.35431E-40 double:4.775E-319;
        r5 = java.lang.Integer.valueOf(r5);
        r6 = "Lagoon";
        r4.put(r5, r6);
        r5 = 7461346; // 0x71d9e2 float:1.0455573E-38 double:3.6863947E-317;
        r5 = java.lang.Integer.valueOf(r5);
        r6 = "Aquamarine";
        r4.put(r5, r6);
        r5 = 1182351; // 0x120a8f float:1.656827E-39 double:5.84159E-318;
        r5 = java.lang.Integer.valueOf(r5);
        r6 = "Ultramarine";
        r4.put(r5, r6);
        r5 = 128; // 0x80 float:1.794E-43 double:6.32E-322;
        r5 = java.lang.Integer.valueOf(r5);
        r6 = "Navy";
        r4.put(r5, r6);
        r5 = 3101086; // 0x2var_e float:4.345547E-39 double:1.53214E-317;
        r5 = java.lang.Integer.valueOf(r5);
        r6 = "Sapphire";
        r4.put(r5, r6);
        r5 = 7788522; // 0x76d7ea float:1.0914044E-38 double:3.848041E-317;
        r5 = java.lang.Integer.valueOf(r5);
        r6 = "Sky";
        r4.put(r5, r6);
        r5 = 32896; // 0x8080 float:4.6097E-41 double:1.6253E-319;
        r5 = java.lang.Integer.valueOf(r5);
        r6 = "Teal";
        r4.put(r5, r6);
        r5 = 4251856; // 0x40e0d0 float:5.958119E-39 double:2.100696E-317;
        r5 = java.lang.Integer.valueOf(r5);
        r6 = "Turquoise";
        r4.put(r5, r6);
        r5 = 10053324; // 0x9966cc float:1.4087707E-38 double:4.967002E-317;
        r5 = java.lang.Integer.valueOf(r5);
        r6 = "Amethyst";
        r4.put(r5, r6);
        r5 = 5046581; // 0x4d0135 float:7.071766E-39 double:2.4933423E-317;
        r5 = java.lang.Integer.valueOf(r5);
        r6 = "Blackberry";
        r4.put(r5, r6);
        r5 = 6373457; // 0x614051 float:8.931116E-39 double:3.148906E-317;
        r5 = java.lang.Integer.valueOf(r5);
        r6 = "Eggplant";
        r4.put(r5, r6);
        r5 = 13148872; // 0xc8a2c8 float:1.8425494E-38 double:6.496406E-317;
        r5 = java.lang.Integer.valueOf(r5);
        r6 = "Lilac";
        r4.put(r5, r6);
        r5 = 11894492; // 0xb57edc float:1.6667733E-38 double:5.87666E-317;
        r5 = java.lang.Integer.valueOf(r5);
        r6 = "Lavender";
        r4.put(r5, r6);
        r5 = 13421823; // 0xccccff float:1.880798E-38 double:6.6312616E-317;
        r5 = java.lang.Integer.valueOf(r5);
        r6 = "Periwinkle";
        r4.put(r5, r6);
        r5 = 8663417; // 0x843179 float:1.2140033E-38 double:4.2802967E-317;
        r5 = java.lang.Integer.valueOf(r5);
        r6 = "Plum";
        r4.put(r5, r6);
        r5 = 6684825; // 0x660099 float:9.367435E-39 double:3.3027424E-317;
        r5 = java.lang.Integer.valueOf(r5);
        r6 = "Purple";
        r4.put(r5, r6);
        r5 = 14204888; // 0xd8bfd8 float:1.9905288E-38 double:7.018147E-317;
        r5 = java.lang.Integer.valueOf(r5);
        r6 = "Thistle";
        r4.put(r5, r6);
        r5 = 14315734; // 0xda70d6 float:2.0060616E-38 double:7.0729124E-317;
        r5 = java.lang.Integer.valueOf(r5);
        r6 = "Orchid";
        r4.put(r5, r6);
        r5 = 2361920; // 0x240a40 float:3.309755E-39 double:1.1669435E-317;
        r5 = java.lang.Integer.valueOf(r5);
        r6 = "Violet";
        r4.put(r5, r6);
        r5 = 4137225; // 0x3var_ float:5.797487E-39 double:2.0440607E-317;
        r5 = java.lang.Integer.valueOf(r5);
        r6 = "Bronze";
        r4.put(r5, r6);
        r5 = 3604994; // 0x370202 float:5.051673E-39 double:1.7811037E-317;
        r5 = java.lang.Integer.valueOf(r5);
        r6 = "Chocolate";
        r4.put(r5, r6);
        r5 = 8077056; // 0x7b3var_ float:1.1318366E-38 double:3.990596E-317;
        r5 = java.lang.Integer.valueOf(r5);
        r6 = "Cinnamon";
        r4.put(r5, r6);
        r5 = 3153694; // 0x301f1e float:4.419267E-39 double:1.558132E-317;
        r5 = java.lang.Integer.valueOf(r5);
        r6 = "Cocoa";
        r4.put(r5, r6);
        r5 = 7365973; // 0x706555 float:1.0321927E-38 double:3.639274E-317;
        r5 = java.lang.Integer.valueOf(r5);
        r6 = "Coffee";
        r4.put(r5, r6);
        r5 = 7956873; // 0x796989 float:1.1149954E-38 double:3.9312176E-317;
        r5 = java.lang.Integer.valueOf(r5);
        r6 = "Rum";
        r4.put(r5, r6);
        r5 = 5113350; // 0x4e0606 float:7.16533E-39 double:2.5263306E-317;
        r5 = java.lang.Integer.valueOf(r5);
        r6 = "Mahogany";
        r4.put(r5, r6);
        r5 = 7875865; // 0x782d19 float:1.1036438E-38 double:3.8911943E-317;
        r5 = java.lang.Integer.valueOf(r5);
        r6 = "Mocha";
        r4.put(r5, r6);
        r5 = 12759680; // 0xc2b280 float:1.788012E-38 double:6.3041195E-317;
        r5 = java.lang.Integer.valueOf(r5);
        r6 = "Sand";
        r4.put(r5, r6);
        r5 = 8924439; // 0x882d17 float:1.2505803E-38 double:4.4092587E-317;
        r5 = java.lang.Integer.valueOf(r5);
        r6 = "Sienna";
        r4.put(r5, r6);
        r5 = 7864585; // 0x780109 float:1.1020631E-38 double:3.8856213E-317;
        r5 = java.lang.Integer.valueOf(r5);
        r6 = "Maple";
        r4.put(r5, r6);
        r5 = 15787660; // 0xf0e68c float:2.2123224E-38 double:7.8001404E-317;
        r5 = java.lang.Integer.valueOf(r5);
        r6 = "Khaki";
        r4.put(r5, r6);
        r5 = 12088115; // 0xb87333 float:1.6939057E-38 double:5.9723223E-317;
        r5 = java.lang.Integer.valueOf(r5);
        r6 = "Copper";
        r4.put(r5, r6);
        r5 = 12144200; // 0xb94e48 float:1.7017649E-38 double:6.000032E-317;
        r5 = java.lang.Integer.valueOf(r5);
        r6 = "Chestnut";
        r4.put(r5, r6);
        r5 = 15653316; // 0xeed9c4 float:2.1934968E-38 double:7.7337657E-317;
        r5 = java.lang.Integer.valueOf(r5);
        r6 = "Almond";
        r4.put(r5, r6);
        r5 = 16776656; // 0xfffdd0 float:2.3509102E-38 double:8.2887694E-317;
        r5 = java.lang.Integer.valueOf(r5);
        r6 = "Cream";
        r4.put(r5, r6);
        r5 = 12186367; // 0xb9f2ff float:1.7076737E-38 double:6.0208653E-317;
        r5 = java.lang.Integer.valueOf(r5);
        r6 = "Diamond";
        r4.put(r5, r6);
        r5 = 11109127; // 0xa98307 float:1.5567203E-38 double:5.488638E-317;
        r5 = java.lang.Integer.valueOf(r5);
        r6 = "Honey";
        r4.put(r5, r6);
        r5 = 16777200; // 0xfffff0 float:2.3509865E-38 double:8.289038E-317;
        r5 = java.lang.Integer.valueOf(r5);
        r6 = "Ivory";
        r4.put(r5, r6);
        r5 = 15392968; // 0xeae0c8 float:2.1570142E-38 double:7.6051367E-317;
        r5 = java.lang.Integer.valueOf(r5);
        r6 = "Pearl";
        r4.put(r5, r6);
        r5 = 15725299; // 0xeff2f3 float:2.2035837E-38 double:7.76933E-317;
        r5 = java.lang.Integer.valueOf(r5);
        r6 = "Porcelain";
        r4.put(r5, r6);
        r5 = 13745832; // 0xd1bea8 float:1.9262013E-38 double:6.7913434E-317;
        r5 = java.lang.Integer.valueOf(r5);
        r6 = "Vanilla";
        r4.put(r5, r6);
        r5 = 16777215; // 0xffffff float:2.3509886E-38 double:8.2890456E-317;
        r5 = java.lang.Integer.valueOf(r5);
        r6 = "White";
        r4.put(r5, r6);
        r5 = 8421504; // 0x808080 float:1.180104E-38 double:4.160776E-317;
        r5 = java.lang.Integer.valueOf(r5);
        r6 = "Gray";
        r4.put(r5, r6);
        r5 = java.lang.Integer.valueOf(r1);
        r6 = "Black";
        r4.put(r5, r6);
        r5 = 15266260; // 0xe8f1d4 float:2.1392587E-38 double:7.5425346E-317;
        r5 = java.lang.Integer.valueOf(r5);
        r6 = "Chrome";
        r4.put(r5, r6);
        r5 = 3556687; // 0x36454f float:4.98398E-39 double:1.757237E-317;
        r5 = java.lang.Integer.valueOf(r5);
        r6 = "Charcoal";
        r4.put(r5, r6);
        r5 = 789277; // 0xc0b1d float:1.106013E-39 double:3.899547E-318;
        r5 = java.lang.Integer.valueOf(r5);
        r6 = "Ebony";
        r4.put(r5, r6);
        r5 = 12632256; // 0xc0c0c0 float:1.7701561E-38 double:6.2411637E-317;
        r5 = java.lang.Integer.valueOf(r5);
        r6 = "Silver";
        r4.put(r5, r6);
        r5 = 16119285; // 0xf5f5f5 float:2.258793E-38 double:7.963985E-317;
        r5 = java.lang.Integer.valueOf(r5);
        r6 = "Smoke";
        r4.put(r5, r6);
        r5 = 2499381; // 0x262335 float:3.502379E-39 double:1.2348583E-317;
        r5 = java.lang.Integer.valueOf(r5);
        r6 = "Steel";
        r4.put(r5, r6);
        r5 = 5220413; // 0x4fa83d float:7.315357E-39 double:2.5792267E-317;
        r5 = java.lang.Integer.valueOf(r5);
        r6 = "Apple";
        r4.put(r5, r6);
        r5 = 8434628; // 0x80b3c4 float:1.1819431E-38 double:4.16726E-317;
        r5 = java.lang.Integer.valueOf(r5);
        r6 = "Glacier";
        r4.put(r5, r6);
        r5 = 16693933; // 0xfebaad float:2.3393183E-38 double:8.247899E-317;
        r5 = java.lang.Integer.valueOf(r5);
        r6 = "Melon";
        r4.put(r5, r6);
        r5 = 12929932; // 0xCLASSNAMEb8c float:1.8118694E-38 double:6.388235E-317;
        r5 = java.lang.Integer.valueOf(r5);
        r6 = "Mulberry";
        r4.put(r5, r6);
        r5 = 11126466; // 0xa9c6c2 float:1.55915E-38 double:5.4972046E-317;
        r5 = java.lang.Integer.valueOf(r5);
        r6 = "Opal";
        r4.put(r5, r6);
        r5 = 5547512; // 0x54a5f8 float:7.77372E-39 double:2.740835E-317;
        r5 = java.lang.Integer.valueOf(r5);
        r6 = "Blue";
        r4.put(r5, r6);
        if (r18 != 0) goto L_0x0900;
    L_0x08f7:
        r5 = org.telegram.ui.ActionBar.Theme.getCurrentTheme();
        r5 = r5.getAccent(r1);
        goto L_0x0902;
    L_0x0900:
        r5 = r18;
    L_0x0902:
        if (r5 == 0) goto L_0x0909;
    L_0x0904:
        r5 = r5.accentColor;
        if (r5 == 0) goto L_0x0909;
    L_0x0908:
        goto L_0x0913;
    L_0x0909:
        r5 = org.telegram.ui.ActionBar.Theme.getCachedWallpaper();
        r5 = org.telegram.messenger.AndroidUtilities.calcDrawableColor(r5);
        r5 = r5[r1];
    L_0x0913:
        r1 = 0;
        r6 = NUM; // 0x7fffffff float:NaN double:1.060997895E-314;
        r7 = android.graphics.Color.red(r5);
        r8 = android.graphics.Color.green(r5);
        r5 = android.graphics.Color.blue(r5);
        r4 = r4.entrySet();
        r4 = r4.iterator();
    L_0x092b:
        r10 = r4.hasNext();
        if (r10 == 0) goto L_0x097e;
    L_0x0931:
        r10 = r4.next();
        r10 = (java.util.Map.Entry) r10;
        r11 = r10.getKey();
        r11 = (java.lang.Integer) r11;
        r12 = r11.intValue();
        r12 = android.graphics.Color.red(r12);
        r13 = r11.intValue();
        r13 = android.graphics.Color.green(r13);
        r11 = r11.intValue();
        r11 = android.graphics.Color.blue(r11);
        r14 = r7 + r12;
        r14 = r14 / r3;
        r12 = r7 - r12;
        r13 = r8 - r13;
        r11 = r5 - r11;
        r15 = r14 + 512;
        r15 = r15 * r12;
        r15 = r15 * r12;
        r12 = r15 >> 8;
        r15 = r13 * 4;
        r15 = r15 * r13;
        r12 = r12 + r15;
        r13 = 767 - r14;
        r13 = r13 * r11;
        r13 = r13 * r11;
        r11 = r13 >> 8;
        r12 = r12 + r11;
        if (r12 >= r6) goto L_0x092b;
    L_0x0976:
        r1 = r10.getValue();
        r1 = (java.lang.String) r1;
        r6 = r12;
        goto L_0x092b;
    L_0x097e:
        r4 = org.telegram.messenger.Utilities.random;
        r4 = r4.nextInt();
        r4 = r4 % r3;
        if (r4 != 0) goto L_0x09ac;
    L_0x0987:
        r2 = new java.lang.StringBuilder;
        r2.<init>();
        r3 = org.telegram.messenger.Utilities.random;
        r4 = r0.size();
        r3 = r3.nextInt(r4);
        r0 = r0.get(r3);
        r0 = (java.lang.String) r0;
        r2.append(r0);
        r0 = " ";
        r2.append(r0);
        r2.append(r1);
        r0 = r2.toString();
        goto L_0x09d0;
    L_0x09ac:
        r0 = new java.lang.StringBuilder;
        r0.<init>();
        r0.append(r1);
        r1 = " ";
        r0.append(r1);
        r1 = org.telegram.messenger.Utilities.random;
        r3 = r2.size();
        r1 = r1.nextInt(r3);
        r1 = r2.get(r1);
        r1 = (java.lang.String) r1;
        r0.append(r1);
        r0 = r0.toString();
    L_0x09d0:
        return r0;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.AlertsCreator.generateThemeName(org.telegram.ui.ActionBar.Theme$ThemeAccent):java.lang.String");
    }
}
