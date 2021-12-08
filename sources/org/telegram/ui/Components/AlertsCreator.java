package org.telegram.ui.Components;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Outline;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.Editable;
import android.text.Html;
import android.text.InputFilter;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.URLSpan;
import android.util.Base64;
import android.util.SparseArray;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import java.io.File;
import java.net.IDN;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import org.telegram.messenger.AccountInstance;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.DialogObject;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationsController;
import org.telegram.messenger.SecretChatHelper;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.SvgHelper;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.browser.Browser;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.SerializedData;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.ActionBarPopupWindow;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.AccountSelectCell;
import org.telegram.ui.Cells.CheckBoxCell;
import org.telegram.ui.Cells.RadioColorCell;
import org.telegram.ui.Cells.TextColorCell;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.Components.voip.VoIPHelper;
import org.telegram.ui.LaunchActivity;
import org.telegram.ui.NotificationsCustomSettingsActivity;
import org.telegram.ui.NotificationsSettingsActivity;
import org.telegram.ui.ProfileNotificationsActivity;
import org.telegram.ui.ThemePreviewActivity;

public class AlertsCreator {

    public interface AccountSelectDelegate {
        void didSelectAccount(int i);
    }

    public interface BlockDialogCallback {
        void run(boolean z, boolean z2);
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

    /* JADX WARNING: Can't fix incorrect switch cases order */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static android.app.Dialog processError(int r17, org.telegram.tgnet.TLRPC.TL_error r18, org.telegram.ui.ActionBar.BaseFragment r19, org.telegram.tgnet.TLObject r20, java.lang.Object... r21) {
        /*
            r0 = r18
            r1 = r19
            r2 = r20
            r3 = r21
            int r4 = r0.code
            r5 = 0
            r6 = 406(0x196, float:5.69E-43)
            if (r4 == r6) goto L_0x077c
            java.lang.String r4 = r0.text
            if (r4 != 0) goto L_0x0016
            r4 = r5
            goto L_0x077d
        L_0x0016:
            boolean r4 = r2 instanceof org.telegram.tgnet.TLRPC.TL_messages_initHistoryImport
            java.lang.String r6 = "\n"
            r7 = 2131625436(0x7f0e05dc, float:1.887808E38)
            java.lang.String r8 = "ErrorOccurred"
            java.lang.String r9 = "FLOOD_WAIT"
            if (r4 != 0) goto L_0x063c
            boolean r4 = r2 instanceof org.telegram.tgnet.TLRPC.TL_messages_checkHistoryImportPeer
            if (r4 != 0) goto L_0x063c
            boolean r4 = r2 instanceof org.telegram.tgnet.TLRPC.TL_messages_checkHistoryImport
            if (r4 != 0) goto L_0x063c
            boolean r4 = r2 instanceof org.telegram.tgnet.TLRPC.TL_messages_startHistoryImport
            if (r4 == 0) goto L_0x0031
            goto L_0x063c
        L_0x0031:
            boolean r4 = r2 instanceof org.telegram.tgnet.TLRPC.TL_account_saveSecureValue
            java.lang.String r10 = "InvalidPhoneNumber"
            java.lang.String r11 = "PHONE_NUMBER_INVALID"
            r12 = 2131625680(0x7f0e06d0, float:1.8878575E38)
            java.lang.String r13 = "FloodWait"
            if (r4 != 0) goto L_0x05d7
            boolean r4 = r2 instanceof org.telegram.tgnet.TLRPC.TL_account_getAuthorizationForm
            if (r4 == 0) goto L_0x0044
            goto L_0x05d7
        L_0x0044:
            boolean r4 = r2 instanceof org.telegram.tgnet.TLRPC.TL_channels_joinChannel
            java.lang.String r15 = "CHANNELS_TOO_MUCH"
            r14 = 0
            if (r4 != 0) goto L_0x0577
            boolean r4 = r2 instanceof org.telegram.tgnet.TLRPC.TL_channels_editAdmin
            if (r4 != 0) goto L_0x0577
            boolean r4 = r2 instanceof org.telegram.tgnet.TLRPC.TL_channels_inviteToChannel
            if (r4 != 0) goto L_0x0577
            boolean r4 = r2 instanceof org.telegram.tgnet.TLRPC.TL_messages_addChatUser
            if (r4 != 0) goto L_0x0577
            boolean r4 = r2 instanceof org.telegram.tgnet.TLRPC.TL_messages_startBot
            if (r4 != 0) goto L_0x0577
            boolean r4 = r2 instanceof org.telegram.tgnet.TLRPC.TL_channels_editBanned
            if (r4 != 0) goto L_0x0577
            boolean r4 = r2 instanceof org.telegram.tgnet.TLRPC.TL_messages_editChatDefaultBannedRights
            if (r4 != 0) goto L_0x0577
            boolean r4 = r2 instanceof org.telegram.tgnet.TLRPC.TL_messages_editChatAdmin
            if (r4 != 0) goto L_0x0577
            boolean r4 = r2 instanceof org.telegram.tgnet.TLRPC.TL_messages_migrateChat
            if (r4 != 0) goto L_0x0577
            boolean r4 = r2 instanceof org.telegram.tgnet.TLRPC.TL_phone_inviteToGroupCall
            if (r4 == 0) goto L_0x0071
            goto L_0x0577
        L_0x0071:
            boolean r4 = r2 instanceof org.telegram.tgnet.TLRPC.TL_messages_createChat
            if (r4 == 0) goto L_0x009d
            java.lang.String r4 = r0.text
            boolean r4 = r4.equals(r15)
            if (r4 == 0) goto L_0x0087
            org.telegram.ui.TooManyCommunitiesActivity r4 = new org.telegram.ui.TooManyCommunitiesActivity
            r6 = 2
            r4.<init>(r6)
            r1.presentFragment(r4)
            return r5
        L_0x0087:
            java.lang.String r4 = r0.text
            boolean r4 = r4.startsWith(r9)
            if (r4 == 0) goto L_0x0096
            java.lang.String r4 = r0.text
            showFloodWaitAlert(r4, r1)
            goto L_0x077a
        L_0x0096:
            java.lang.String r4 = r0.text
            showAddUserAlert(r4, r1, r14, r2)
            goto L_0x077a
        L_0x009d:
            boolean r4 = r2 instanceof org.telegram.tgnet.TLRPC.TL_channels_createChannel
            if (r4 == 0) goto L_0x00c9
            java.lang.String r4 = r0.text
            boolean r4 = r4.equals(r15)
            if (r4 == 0) goto L_0x00b3
            org.telegram.ui.TooManyCommunitiesActivity r4 = new org.telegram.ui.TooManyCommunitiesActivity
            r6 = 2
            r4.<init>(r6)
            r1.presentFragment(r4)
            return r5
        L_0x00b3:
            java.lang.String r4 = r0.text
            boolean r4 = r4.startsWith(r9)
            if (r4 == 0) goto L_0x00c2
            java.lang.String r4 = r0.text
            showFloodWaitAlert(r4, r1)
            goto L_0x077a
        L_0x00c2:
            java.lang.String r4 = r0.text
            showAddUserAlert(r4, r1, r14, r2)
            goto L_0x077a
        L_0x00c9:
            boolean r4 = r2 instanceof org.telegram.tgnet.TLRPC.TL_messages_editMessage
            if (r4 == 0) goto L_0x00f5
            java.lang.String r4 = r0.text
            java.lang.String r6 = "MESSAGE_NOT_MODIFIED"
            boolean r4 = r4.equals(r6)
            if (r4 != 0) goto L_0x077a
            if (r1 == 0) goto L_0x00e7
            r4 = 2131625367(0x7f0e0597, float:1.887794E38)
            java.lang.String r6 = "EditMessageError"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r6, r4)
            showSimpleAlert(r1, r4)
            goto L_0x077a
        L_0x00e7:
            r4 = 2131625367(0x7f0e0597, float:1.887794E38)
            java.lang.String r6 = "EditMessageError"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r6, r4)
            showSimpleToast(r5, r4)
            goto L_0x077a
        L_0x00f5:
            boolean r4 = r2 instanceof org.telegram.tgnet.TLRPC.TL_messages_sendMessage
            r16 = -1
            if (r4 != 0) goto L_0x0510
            boolean r4 = r2 instanceof org.telegram.tgnet.TLRPC.TL_messages_sendMedia
            if (r4 != 0) goto L_0x0510
            boolean r4 = r2 instanceof org.telegram.tgnet.TLRPC.TL_messages_sendInlineBotResult
            if (r4 != 0) goto L_0x0510
            boolean r4 = r2 instanceof org.telegram.tgnet.TLRPC.TL_messages_forwardMessages
            if (r4 != 0) goto L_0x0510
            boolean r4 = r2 instanceof org.telegram.tgnet.TLRPC.TL_messages_sendMultiMedia
            if (r4 != 0) goto L_0x0510
            boolean r4 = r2 instanceof org.telegram.tgnet.TLRPC.TL_messages_sendScheduledMessages
            if (r4 == 0) goto L_0x0111
            goto L_0x0510
        L_0x0111:
            boolean r4 = r2 instanceof org.telegram.tgnet.TLRPC.TL_messages_importChatInvite
            if (r4 == 0) goto L_0x017f
            java.lang.String r4 = r0.text
            boolean r4 = r4.startsWith(r9)
            if (r4 == 0) goto L_0x0126
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r13, r12)
            showSimpleAlert(r1, r4)
            goto L_0x077a
        L_0x0126:
            java.lang.String r4 = r0.text
            java.lang.String r6 = "USERS_TOO_MUCH"
            boolean r4 = r4.equals(r6)
            if (r4 == 0) goto L_0x013e
            r4 = 2131626067(0x7f0e0853, float:1.887936E38)
            java.lang.String r6 = "JoinToGroupErrorFull"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r6, r4)
            showSimpleAlert(r1, r4)
            goto L_0x077a
        L_0x013e:
            java.lang.String r4 = r0.text
            boolean r4 = r4.equals(r15)
            if (r4 == 0) goto L_0x0150
            org.telegram.ui.TooManyCommunitiesActivity r4 = new org.telegram.ui.TooManyCommunitiesActivity
            r4.<init>(r14)
            r1.presentFragment(r4)
            goto L_0x077a
        L_0x0150:
            java.lang.String r4 = r0.text
            java.lang.String r6 = "INVITE_HASH_EXPIRED"
            boolean r4 = r4.equals(r6)
            if (r4 == 0) goto L_0x0171
            r4 = 2131625565(0x7f0e065d, float:1.8878342E38)
            java.lang.String r6 = "ExpiredLink"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r6, r4)
            r6 = 2131626011(0x7f0e081b, float:1.8879246E38)
            java.lang.String r7 = "InviteExpired"
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r7, r6)
            showSimpleAlert(r1, r4, r6)
            goto L_0x077a
        L_0x0171:
            r4 = 2131626068(0x7f0e0854, float:1.8879362E38)
            java.lang.String r6 = "JoinToGroupErrorNotExist"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r6, r4)
            showSimpleAlert(r1, r4)
            goto L_0x077a
        L_0x017f:
            boolean r4 = r2 instanceof org.telegram.tgnet.TLRPC.TL_messages_getAttachedStickers
            if (r4 == 0) goto L_0x01b0
            if (r1 == 0) goto L_0x077a
            android.app.Activity r4 = r19.getParentActivity()
            if (r4 == 0) goto L_0x077a
            android.app.Activity r4 = r19.getParentActivity()
            java.lang.StringBuilder r9 = new java.lang.StringBuilder
            r9.<init>()
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r8, r7)
            r9.append(r7)
            r9.append(r6)
            java.lang.String r6 = r0.text
            r9.append(r6)
            java.lang.String r6 = r9.toString()
            android.widget.Toast r4 = android.widget.Toast.makeText(r4, r6, r14)
            r4.show()
            goto L_0x077a
        L_0x01b0:
            boolean r4 = r2 instanceof org.telegram.tgnet.TLRPC.TL_account_confirmPhone
            java.lang.String r5 = "CodeExpired"
            java.lang.String r14 = "PHONE_CODE_EXPIRED"
            java.lang.String r7 = "PHONE_CODE_INVALID"
            java.lang.String r12 = "InvalidCode"
            java.lang.String r15 = "PHONE_CODE_EMPTY"
            if (r4 != 0) goto L_0x04a5
            boolean r4 = r2 instanceof org.telegram.tgnet.TLRPC.TL_account_verifyPhone
            if (r4 != 0) goto L_0x04a5
            boolean r4 = r2 instanceof org.telegram.tgnet.TLRPC.TL_account_verifyEmail
            if (r4 == 0) goto L_0x01c8
            goto L_0x04a5
        L_0x01c8:
            boolean r4 = r2 instanceof org.telegram.tgnet.TLRPC.TL_auth_resendCode
            if (r4 == 0) goto L_0x024b
            java.lang.String r4 = r0.text
            boolean r4 = r4.contains(r11)
            if (r4 == 0) goto L_0x01e0
            r4 = 2131626001(0x7f0e0811, float:1.8879226E38)
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r10, r4)
            android.app.Dialog r4 = showSimpleAlert(r1, r4)
            return r4
        L_0x01e0:
            java.lang.String r4 = r0.text
            boolean r4 = r4.contains(r15)
            if (r4 != 0) goto L_0x023f
            java.lang.String r4 = r0.text
            boolean r4 = r4.contains(r7)
            if (r4 == 0) goto L_0x01f1
            goto L_0x023f
        L_0x01f1:
            java.lang.String r4 = r0.text
            boolean r4 = r4.contains(r14)
            if (r4 == 0) goto L_0x0205
            r4 = 2131625009(0x7f0e0431, float:1.8877214E38)
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
            android.app.Dialog r4 = showSimpleAlert(r1, r4)
            return r4
        L_0x0205:
            java.lang.String r4 = r0.text
            boolean r4 = r4.startsWith(r9)
            if (r4 == 0) goto L_0x0219
            r4 = 2131625680(0x7f0e06d0, float:1.8878575E38)
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r13, r4)
            android.app.Dialog r4 = showSimpleAlert(r1, r4)
            return r4
        L_0x0219:
            int r4 = r0.code
            r5 = -1000(0xfffffffffffffCLASSNAME, float:NaN)
            if (r4 == r5) goto L_0x077a
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            r5 = 2131625436(0x7f0e05dc, float:1.887808E38)
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r8, r5)
            r4.append(r5)
            r4.append(r6)
            java.lang.String r5 = r0.text
            r4.append(r5)
            java.lang.String r4 = r4.toString()
            android.app.Dialog r4 = showSimpleAlert(r1, r4)
            return r4
        L_0x023f:
            r4 = 2131625998(0x7f0e080e, float:1.887922E38)
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r12, r4)
            android.app.Dialog r4 = showSimpleAlert(r1, r4)
            return r4
        L_0x024b:
            boolean r4 = r2 instanceof org.telegram.tgnet.TLRPC.TL_account_sendConfirmPhoneCode
            if (r4 == 0) goto L_0x0283
            int r4 = r0.code
            r5 = 400(0x190, float:5.6E-43)
            if (r4 != r5) goto L_0x0263
            r4 = 2131624702(0x7f0e02fe, float:1.8876591E38)
            java.lang.String r5 = "CancelLinkExpired"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
            android.app.Dialog r4 = showSimpleAlert(r1, r4)
            return r4
        L_0x0263:
            java.lang.String r4 = r0.text
            boolean r4 = r4.startsWith(r9)
            if (r4 == 0) goto L_0x0277
            r4 = 2131625680(0x7f0e06d0, float:1.8878575E38)
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r13, r4)
            android.app.Dialog r4 = showSimpleAlert(r1, r4)
            return r4
        L_0x0277:
            r4 = 2131625436(0x7f0e05dc, float:1.887808E38)
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r8, r4)
            android.app.Dialog r4 = showSimpleAlert(r1, r4)
            return r4
        L_0x0283:
            boolean r4 = r2 instanceof org.telegram.tgnet.TLRPC.TL_account_changePhone
            if (r4 == 0) goto L_0x02e7
            java.lang.String r4 = r0.text
            boolean r4 = r4.contains(r11)
            if (r4 == 0) goto L_0x029b
            r4 = 2131626001(0x7f0e0811, float:1.8879226E38)
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r10, r4)
            showSimpleAlert(r1, r4)
            goto L_0x077a
        L_0x029b:
            java.lang.String r4 = r0.text
            boolean r4 = r4.contains(r15)
            if (r4 != 0) goto L_0x02db
            java.lang.String r4 = r0.text
            boolean r4 = r4.contains(r7)
            if (r4 == 0) goto L_0x02ac
            goto L_0x02db
        L_0x02ac:
            java.lang.String r4 = r0.text
            boolean r4 = r4.contains(r14)
            if (r4 == 0) goto L_0x02c0
            r4 = 2131625009(0x7f0e0431, float:1.8877214E38)
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
            showSimpleAlert(r1, r4)
            goto L_0x077a
        L_0x02c0:
            java.lang.String r4 = r0.text
            boolean r4 = r4.startsWith(r9)
            if (r4 == 0) goto L_0x02d4
            r4 = 2131625680(0x7f0e06d0, float:1.8878575E38)
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r13, r4)
            showSimpleAlert(r1, r4)
            goto L_0x077a
        L_0x02d4:
            java.lang.String r4 = r0.text
            showSimpleAlert(r1, r4)
            goto L_0x077a
        L_0x02db:
            r4 = 2131625998(0x7f0e080e, float:1.887922E38)
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r12, r4)
            showSimpleAlert(r1, r4)
            goto L_0x077a
        L_0x02e7:
            boolean r4 = r2 instanceof org.telegram.tgnet.TLRPC.TL_account_sendChangePhoneCode
            if (r4 == 0) goto L_0x0383
            java.lang.String r4 = r0.text
            boolean r4 = r4.contains(r11)
            if (r4 == 0) goto L_0x02fd
            r4 = 0
            r5 = r3[r4]
            java.lang.String r5 = (java.lang.String) r5
            org.telegram.ui.LoginActivity.needShowInvalidAlert(r1, r5, r4)
            goto L_0x077a
        L_0x02fd:
            java.lang.String r4 = r0.text
            boolean r4 = r4.contains(r15)
            if (r4 != 0) goto L_0x0377
            java.lang.String r4 = r0.text
            boolean r4 = r4.contains(r7)
            if (r4 == 0) goto L_0x030e
            goto L_0x0377
        L_0x030e:
            java.lang.String r4 = r0.text
            boolean r4 = r4.contains(r14)
            if (r4 == 0) goto L_0x0322
            r4 = 2131625009(0x7f0e0431, float:1.8877214E38)
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
            showSimpleAlert(r1, r4)
            goto L_0x077a
        L_0x0322:
            java.lang.String r4 = r0.text
            boolean r4 = r4.startsWith(r9)
            if (r4 == 0) goto L_0x0336
            r4 = 2131625680(0x7f0e06d0, float:1.8878575E38)
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r13, r4)
            showSimpleAlert(r1, r4)
            goto L_0x077a
        L_0x0336:
            java.lang.String r4 = r0.text
            java.lang.String r5 = "PHONE_NUMBER_OCCUPIED"
            boolean r4 = r4.startsWith(r5)
            if (r4 == 0) goto L_0x0356
            r4 = 2131624730(0x7f0e031a, float:1.8876648E38)
            r5 = 1
            java.lang.Object[] r5 = new java.lang.Object[r5]
            r6 = 0
            r7 = r3[r6]
            r5[r6] = r7
            java.lang.String r6 = "ChangePhoneNumberOccupied"
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatString(r6, r4, r5)
            showSimpleAlert(r1, r4)
            goto L_0x077a
        L_0x0356:
            r6 = 0
            java.lang.String r4 = r0.text
            java.lang.String r5 = "PHONE_NUMBER_BANNED"
            boolean r4 = r4.startsWith(r5)
            if (r4 == 0) goto L_0x036b
            r4 = r3[r6]
            java.lang.String r4 = (java.lang.String) r4
            r5 = 1
            org.telegram.ui.LoginActivity.needShowInvalidAlert(r1, r4, r5)
            goto L_0x077a
        L_0x036b:
            r4 = 2131625436(0x7f0e05dc, float:1.887808E38)
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r8, r4)
            showSimpleAlert(r1, r4)
            goto L_0x077a
        L_0x0377:
            r4 = 2131625998(0x7f0e080e, float:1.887922E38)
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r12, r4)
            showSimpleAlert(r1, r4)
            goto L_0x077a
        L_0x0383:
            boolean r4 = r2 instanceof org.telegram.tgnet.TLRPC.TL_updateUserName
            if (r4 == 0) goto L_0x03d0
            java.lang.String r4 = r0.text
            int r5 = r4.hashCode()
            switch(r5) {
                case 288843630: goto L_0x039b;
                case 533175271: goto L_0x0391;
                default: goto L_0x0390;
            }
        L_0x0390:
            goto L_0x03a5
        L_0x0391:
            java.lang.String r5 = "USERNAME_OCCUPIED"
            boolean r4 = r4.equals(r5)
            if (r4 == 0) goto L_0x0390
            r14 = 1
            goto L_0x03a6
        L_0x039b:
            java.lang.String r5 = "USERNAME_INVALID"
            boolean r4 = r4.equals(r5)
            if (r4 == 0) goto L_0x0390
            r14 = 0
            goto L_0x03a6
        L_0x03a5:
            r14 = -1
        L_0x03a6:
            switch(r14) {
                case 0: goto L_0x03c1;
                case 1: goto L_0x03b4;
                default: goto L_0x03a9;
            }
        L_0x03a9:
            r4 = 2131625436(0x7f0e05dc, float:1.887808E38)
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r8, r4)
            showSimpleAlert(r1, r4)
            goto L_0x03ce
        L_0x03b4:
            r4 = 2131628318(0x7f0e111e, float:1.8883925E38)
            java.lang.String r5 = "UsernameInUse"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
            showSimpleAlert(r1, r4)
            goto L_0x03ce
        L_0x03c1:
            r4 = 2131628319(0x7f0e111f, float:1.8883927E38)
            java.lang.String r5 = "UsernameInvalid"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
            showSimpleAlert(r1, r4)
        L_0x03ce:
            goto L_0x077a
        L_0x03d0:
            boolean r4 = r2 instanceof org.telegram.tgnet.TLRPC.TL_contacts_importContacts
            if (r4 == 0) goto L_0x0408
            java.lang.String r4 = r0.text
            boolean r4 = r4.startsWith(r9)
            if (r4 == 0) goto L_0x03e8
            r4 = 2131625680(0x7f0e06d0, float:1.8878575E38)
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r13, r4)
            showSimpleAlert(r1, r4)
            goto L_0x077a
        L_0x03e8:
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            r5 = 2131625436(0x7f0e05dc, float:1.887808E38)
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r8, r5)
            r4.append(r5)
            r4.append(r6)
            java.lang.String r5 = r0.text
            r4.append(r5)
            java.lang.String r4 = r4.toString()
            showSimpleAlert(r1, r4)
            goto L_0x077a
        L_0x0408:
            boolean r4 = r2 instanceof org.telegram.tgnet.TLRPC.TL_account_getPassword
            if (r4 != 0) goto L_0x048b
            boolean r4 = r2 instanceof org.telegram.tgnet.TLRPC.TL_account_getTmpPassword
            if (r4 == 0) goto L_0x0412
            goto L_0x048b
        L_0x0412:
            boolean r4 = r2 instanceof org.telegram.tgnet.TLRPC.TL_payments_sendPaymentForm
            if (r4 == 0) goto L_0x045a
            java.lang.String r4 = r0.text
            int r5 = r4.hashCode()
            switch(r5) {
                case -1144062453: goto L_0x042a;
                case -784238410: goto L_0x0420;
                default: goto L_0x041f;
            }
        L_0x041f:
            goto L_0x0434
        L_0x0420:
            java.lang.String r5 = "PAYMENT_FAILED"
            boolean r4 = r4.equals(r5)
            if (r4 == 0) goto L_0x041f
            r14 = 1
            goto L_0x0435
        L_0x042a:
            java.lang.String r5 = "BOT_PRECHECKOUT_FAILED"
            boolean r4 = r4.equals(r5)
            if (r4 == 0) goto L_0x041f
            r14 = 0
            goto L_0x0435
        L_0x0434:
            r14 = -1
        L_0x0435:
            switch(r14) {
                case 0: goto L_0x044b;
                case 1: goto L_0x043e;
                default: goto L_0x0438;
            }
        L_0x0438:
            java.lang.String r4 = r0.text
            showSimpleToast(r1, r4)
            goto L_0x0458
        L_0x043e:
            r4 = 2131627058(0x7f0e0CLASSNAME, float:1.888137E38)
            java.lang.String r5 = "PaymentFailed"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
            showSimpleToast(r1, r4)
            goto L_0x0458
        L_0x044b:
            r4 = 2131627072(0x7f0e0CLASSNAME, float:1.8881398E38)
            java.lang.String r5 = "PaymentPrecheckoutFailed"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
            showSimpleToast(r1, r4)
        L_0x0458:
            goto L_0x077a
        L_0x045a:
            boolean r4 = r2 instanceof org.telegram.tgnet.TLRPC.TL_payments_validateRequestedInfo
            if (r4 == 0) goto L_0x077a
            java.lang.String r4 = r0.text
            int r5 = r4.hashCode()
            switch(r5) {
                case 1758025548: goto L_0x0468;
                default: goto L_0x0467;
            }
        L_0x0467:
            goto L_0x0472
        L_0x0468:
            java.lang.String r5 = "SHIPPING_NOT_AVAILABLE"
            boolean r4 = r4.equals(r5)
            if (r4 == 0) goto L_0x0467
            r14 = 0
            goto L_0x0473
        L_0x0472:
            r14 = -1
        L_0x0473:
            switch(r14) {
                case 0: goto L_0x047d;
                default: goto L_0x0476;
            }
        L_0x0476:
            java.lang.String r4 = r0.text
            showSimpleToast(r1, r4)
            goto L_0x077a
        L_0x047d:
            r4 = 2131627061(0x7f0e0CLASSNAME, float:1.8881376E38)
            java.lang.String r5 = "PaymentNoShippingMethod"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
            showSimpleToast(r1, r4)
            goto L_0x077a
        L_0x048b:
            java.lang.String r4 = r0.text
            boolean r4 = r4.startsWith(r9)
            if (r4 == 0) goto L_0x049e
            java.lang.String r4 = r0.text
            java.lang.String r4 = getFloodWaitString(r4)
            showSimpleToast(r1, r4)
            goto L_0x077a
        L_0x049e:
            java.lang.String r4 = r0.text
            showSimpleToast(r1, r4)
            goto L_0x077a
        L_0x04a5:
            java.lang.String r4 = r0.text
            boolean r4 = r4.contains(r15)
            if (r4 != 0) goto L_0x0504
            java.lang.String r4 = r0.text
            boolean r4 = r4.contains(r7)
            if (r4 != 0) goto L_0x0504
            java.lang.String r4 = r0.text
            java.lang.String r6 = "CODE_INVALID"
            boolean r4 = r4.contains(r6)
            if (r4 != 0) goto L_0x0504
            java.lang.String r4 = r0.text
            java.lang.String r6 = "CODE_EMPTY"
            boolean r4 = r4.contains(r6)
            if (r4 == 0) goto L_0x04ca
            goto L_0x0504
        L_0x04ca:
            java.lang.String r4 = r0.text
            boolean r4 = r4.contains(r14)
            if (r4 != 0) goto L_0x04f8
            java.lang.String r4 = r0.text
            java.lang.String r6 = "EMAIL_VERIFY_EXPIRED"
            boolean r4 = r4.contains(r6)
            if (r4 == 0) goto L_0x04dd
            goto L_0x04f8
        L_0x04dd:
            java.lang.String r4 = r0.text
            boolean r4 = r4.startsWith(r9)
            if (r4 == 0) goto L_0x04f1
            r4 = 2131625680(0x7f0e06d0, float:1.8878575E38)
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r13, r4)
            android.app.Dialog r4 = showSimpleAlert(r1, r4)
            return r4
        L_0x04f1:
            java.lang.String r4 = r0.text
            android.app.Dialog r4 = showSimpleAlert(r1, r4)
            return r4
        L_0x04f8:
            r4 = 2131625009(0x7f0e0431, float:1.8877214E38)
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
            android.app.Dialog r4 = showSimpleAlert(r1, r4)
            return r4
        L_0x0504:
            r4 = 2131625998(0x7f0e080e, float:1.887922E38)
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r12, r4)
            android.app.Dialog r4 = showSimpleAlert(r1, r4)
            return r4
        L_0x0510:
            java.lang.String r4 = r0.text
            int r5 = r4.hashCode()
            switch(r5) {
                case -1809401834: goto L_0x0531;
                case -454039871: goto L_0x0526;
                case 1169786080: goto L_0x051a;
                default: goto L_0x0519;
            }
        L_0x0519:
            goto L_0x053b
        L_0x051a:
            java.lang.String r5 = "SCHEDULE_TOO_MUCH"
            boolean r4 = r4.equals(r5)
            if (r4 == 0) goto L_0x0519
            r4 = 2
            r16 = 2
            goto L_0x053b
        L_0x0526:
            java.lang.String r5 = "PEER_FLOOD"
            boolean r4 = r4.equals(r5)
            if (r4 == 0) goto L_0x0519
            r16 = 0
            goto L_0x053b
        L_0x0531:
            java.lang.String r5 = "USER_BANNED_IN_CHANNEL"
            boolean r4 = r4.equals(r5)
            if (r4 == 0) goto L_0x0519
            r16 = 1
        L_0x053b:
            switch(r16) {
                case 0: goto L_0x0561;
                case 1: goto L_0x054c;
                case 2: goto L_0x053f;
                default: goto L_0x053e;
            }
        L_0x053e:
            goto L_0x0575
        L_0x053f:
            r4 = 2131626342(0x7f0e0966, float:1.8879917E38)
            java.lang.String r5 = "MessageScheduledLimitReached"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
            showSimpleToast(r1, r4)
            goto L_0x0575
        L_0x054c:
            org.telegram.messenger.NotificationCenter r4 = org.telegram.messenger.NotificationCenter.getInstance(r17)
            int r5 = org.telegram.messenger.NotificationCenter.needShowAlert
            r6 = 1
            java.lang.Object[] r6 = new java.lang.Object[r6]
            r7 = 5
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            r8 = 0
            r6[r8] = r7
            r4.postNotificationName(r5, r6)
            goto L_0x0575
        L_0x0561:
            r6 = 1
            r8 = 0
            org.telegram.messenger.NotificationCenter r4 = org.telegram.messenger.NotificationCenter.getInstance(r17)
            int r5 = org.telegram.messenger.NotificationCenter.needShowAlert
            java.lang.Object[] r6 = new java.lang.Object[r6]
            java.lang.Integer r7 = java.lang.Integer.valueOf(r8)
            r6[r8] = r7
            r4.postNotificationName(r5, r6)
        L_0x0575:
            goto L_0x077a
        L_0x0577:
            if (r1 == 0) goto L_0x059f
            java.lang.String r4 = r0.text
            boolean r4 = r4.equals(r15)
            if (r4 == 0) goto L_0x059f
            boolean r4 = r2 instanceof org.telegram.tgnet.TLRPC.TL_channels_joinChannel
            if (r4 != 0) goto L_0x0594
            boolean r4 = r2 instanceof org.telegram.tgnet.TLRPC.TL_channels_inviteToChannel
            if (r4 == 0) goto L_0x058a
            goto L_0x0594
        L_0x058a:
            org.telegram.ui.TooManyCommunitiesActivity r4 = new org.telegram.ui.TooManyCommunitiesActivity
            r5 = 1
            r4.<init>(r5)
            r1.presentFragment(r4)
            goto L_0x059d
        L_0x0594:
            org.telegram.ui.TooManyCommunitiesActivity r4 = new org.telegram.ui.TooManyCommunitiesActivity
            r5 = 0
            r4.<init>(r5)
            r1.presentFragment(r4)
        L_0x059d:
            r4 = 0
            return r4
        L_0x059f:
            if (r1 == 0) goto L_0x05b8
            java.lang.String r4 = r0.text
            if (r3 == 0) goto L_0x05b2
            int r5 = r3.length
            if (r5 <= 0) goto L_0x05b2
            r5 = 0
            r5 = r3[r5]
            java.lang.Boolean r5 = (java.lang.Boolean) r5
            boolean r14 = r5.booleanValue()
            goto L_0x05b3
        L_0x05b2:
            r14 = 0
        L_0x05b3:
            showAddUserAlert(r4, r1, r14, r2)
            goto L_0x077a
        L_0x05b8:
            java.lang.String r4 = r0.text
            java.lang.String r5 = "PEER_FLOOD"
            boolean r4 = r4.equals(r5)
            if (r4 == 0) goto L_0x077a
            org.telegram.messenger.NotificationCenter r4 = org.telegram.messenger.NotificationCenter.getInstance(r17)
            int r5 = org.telegram.messenger.NotificationCenter.needShowAlert
            r6 = 1
            java.lang.Object[] r7 = new java.lang.Object[r6]
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            r8 = 0
            r7[r8] = r6
            r4.postNotificationName(r5, r7)
            goto L_0x077a
        L_0x05d7:
            java.lang.String r4 = r0.text
            boolean r4 = r4.contains(r11)
            if (r4 == 0) goto L_0x05eb
            r4 = 2131626001(0x7f0e0811, float:1.8879226E38)
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r10, r4)
            showSimpleAlert(r1, r4)
            goto L_0x077a
        L_0x05eb:
            java.lang.String r4 = r0.text
            boolean r4 = r4.startsWith(r9)
            if (r4 == 0) goto L_0x05ff
            r4 = 2131625680(0x7f0e06d0, float:1.8878575E38)
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r13, r4)
            showSimpleAlert(r1, r4)
            goto L_0x077a
        L_0x05ff:
            java.lang.String r4 = r0.text
            java.lang.String r5 = "APP_VERSION_OUTDATED"
            boolean r4 = r5.equals(r4)
            if (r4 == 0) goto L_0x061c
            android.app.Activity r4 = r19.getParentActivity()
            r5 = 2131628231(0x7f0e10c7, float:1.8883749E38)
            java.lang.String r6 = "UpdateAppAlert"
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r6, r5)
            r6 = 1
            showUpdateAppAlert(r4, r5, r6)
            goto L_0x077a
        L_0x061c:
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            r5 = 2131625436(0x7f0e05dc, float:1.887808E38)
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r8, r5)
            r4.append(r5)
            r4.append(r6)
            java.lang.String r5 = r0.text
            r4.append(r5)
            java.lang.String r4 = r4.toString()
            showSimpleAlert(r1, r4)
            goto L_0x077a
        L_0x063c:
            boolean r4 = r2 instanceof org.telegram.tgnet.TLRPC.TL_messages_initHistoryImport
            if (r4 == 0) goto L_0x0646
            r4 = r2
            org.telegram.tgnet.TLRPC$TL_messages_initHistoryImport r4 = (org.telegram.tgnet.TLRPC.TL_messages_initHistoryImport) r4
            org.telegram.tgnet.TLRPC$InputPeer r4 = r4.peer
            goto L_0x0651
        L_0x0646:
            boolean r4 = r2 instanceof org.telegram.tgnet.TLRPC.TL_messages_startHistoryImport
            if (r4 == 0) goto L_0x0650
            r4 = r2
            org.telegram.tgnet.TLRPC$TL_messages_startHistoryImport r4 = (org.telegram.tgnet.TLRPC.TL_messages_startHistoryImport) r4
            org.telegram.tgnet.TLRPC$InputPeer r4 = r4.peer
            goto L_0x0651
        L_0x0650:
            r4 = 0
        L_0x0651:
            java.lang.String r5 = r0.text
            java.lang.String r7 = "USER_IS_BLOCKED"
            boolean r5 = r5.contains(r7)
            r7 = 2131625941(0x7f0e07d5, float:1.8879104E38)
            java.lang.String r10 = "ImportErrorTitle"
            if (r5 == 0) goto L_0x0672
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r10, r7)
            r6 = 2131625942(0x7f0e07d6, float:1.8879106E38)
            java.lang.String r7 = "ImportErrorUserBlocked"
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r7, r6)
            showSimpleAlert(r1, r5, r6)
            goto L_0x0779
        L_0x0672:
            java.lang.String r5 = r0.text
            java.lang.String r11 = "USER_NOT_MUTUAL_CONTACT"
            boolean r5 = r5.contains(r11)
            if (r5 == 0) goto L_0x068e
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r10, r7)
            r6 = 2131625950(0x7f0e07de, float:1.8879122E38)
            java.lang.String r7 = "ImportMutualError"
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r7, r6)
            showSimpleAlert(r1, r5, r6)
            goto L_0x0779
        L_0x068e:
            java.lang.String r5 = r0.text
            java.lang.String r11 = "IMPORT_PEER_TYPE_INVALID"
            boolean r5 = r5.contains(r11)
            if (r5 == 0) goto L_0x06c0
            boolean r5 = r4 instanceof org.telegram.tgnet.TLRPC.TL_inputPeerUser
            if (r5 == 0) goto L_0x06ae
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r10, r7)
            r6 = 2131625936(0x7f0e07d0, float:1.8879094E38)
            java.lang.String r7 = "ImportErrorChatInvalidUser"
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r7, r6)
            showSimpleAlert(r1, r5, r6)
            goto L_0x0779
        L_0x06ae:
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r10, r7)
            r6 = 2131625935(0x7f0e07cf, float:1.8879092E38)
            java.lang.String r7 = "ImportErrorChatInvalidGroup"
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r7, r6)
            showSimpleAlert(r1, r5, r6)
            goto L_0x0779
        L_0x06c0:
            java.lang.String r5 = r0.text
            java.lang.String r11 = "CHAT_ADMIN_REQUIRED"
            boolean r5 = r5.contains(r11)
            if (r5 == 0) goto L_0x06dc
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r10, r7)
            r6 = 2131625939(0x7f0e07d3, float:1.88791E38)
            java.lang.String r7 = "ImportErrorNotAdmin"
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r7, r6)
            showSimpleAlert(r1, r5, r6)
            goto L_0x0779
        L_0x06dc:
            java.lang.String r5 = r0.text
            java.lang.String r11 = "IMPORT_FORMAT"
            boolean r5 = r5.startsWith(r11)
            if (r5 == 0) goto L_0x06f8
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r10, r7)
            r6 = 2131625937(0x7f0e07d1, float:1.8879096E38)
            java.lang.String r7 = "ImportErrorFileFormatInvalid"
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r7, r6)
            showSimpleAlert(r1, r5, r6)
            goto L_0x0779
        L_0x06f8:
            java.lang.String r5 = r0.text
            java.lang.String r11 = "PEER_ID_INVALID"
            boolean r5 = r5.startsWith(r11)
            if (r5 == 0) goto L_0x0713
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r10, r7)
            r6 = 2131625940(0x7f0e07d4, float:1.8879102E38)
            java.lang.String r7 = "ImportErrorPeerInvalid"
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r7, r6)
            showSimpleAlert(r1, r5, r6)
            goto L_0x0779
        L_0x0713:
            java.lang.String r5 = r0.text
            java.lang.String r11 = "IMPORT_LANG_NOT_FOUND"
            boolean r5 = r5.contains(r11)
            if (r5 == 0) goto L_0x072e
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r10, r7)
            r6 = 2131625938(0x7f0e07d2, float:1.8879098E38)
            java.lang.String r7 = "ImportErrorFileLang"
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r7, r6)
            showSimpleAlert(r1, r5, r6)
            goto L_0x0779
        L_0x072e:
            java.lang.String r5 = r0.text
            java.lang.String r11 = "IMPORT_UPLOAD_FAILED"
            boolean r5 = r5.contains(r11)
            if (r5 == 0) goto L_0x0749
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r10, r7)
            r6 = 2131625943(0x7f0e07d7, float:1.8879108E38)
            java.lang.String r7 = "ImportFailedToUpload"
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r7, r6)
            showSimpleAlert(r1, r5, r6)
            goto L_0x0779
        L_0x0749:
            java.lang.String r5 = r0.text
            boolean r5 = r5.startsWith(r9)
            if (r5 == 0) goto L_0x0757
            java.lang.String r5 = r0.text
            showFloodWaitAlert(r5, r1)
            goto L_0x0779
        L_0x0757:
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r10, r7)
            java.lang.StringBuilder r7 = new java.lang.StringBuilder
            r7.<init>()
            r9 = 2131625436(0x7f0e05dc, float:1.887808E38)
            java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r8, r9)
            r7.append(r8)
            r7.append(r6)
            java.lang.String r6 = r0.text
            r7.append(r6)
            java.lang.String r6 = r7.toString()
            showSimpleAlert(r1, r5, r6)
        L_0x0779:
        L_0x077a:
            r4 = 0
            return r4
        L_0x077c:
            r4 = r5
        L_0x077d:
            return r4
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.AlertsCreator.processError(int, org.telegram.tgnet.TLRPC$TL_error, org.telegram.ui.ActionBar.BaseFragment, org.telegram.tgnet.TLObject, java.lang.Object[]):android.app.Dialog");
    }

    public static Toast showSimpleToast(BaseFragment baseFragment, String text) {
        Context context;
        if (text == null) {
            return null;
        }
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
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(LocaleController.getString("AppName", NUM));
        builder.setMessage(text);
        builder.setPositiveButton(LocaleController.getString("OK", NUM), (DialogInterface.OnClickListener) null);
        if (updateApp) {
            builder.setNegativeButton(LocaleController.getString("UpdateApp", NUM), new AlertsCreator$$ExternalSyntheticLambda7(context));
        }
        return builder.show();
    }

    public static AlertDialog.Builder createLanguageAlert(LaunchActivity activity, TLRPC.TL_langPackLanguage language) {
        String str;
        int end;
        if (language == null) {
            return null;
        }
        language.lang_code = language.lang_code.replace('-', '_').toLowerCase();
        language.plural_code = language.plural_code.replace('-', '_').toLowerCase();
        if (language.base_lang_code != null) {
            language.base_lang_code = language.base_lang_code.replace('-', '_').toLowerCase();
        }
        final AlertDialog.Builder builder = new AlertDialog.Builder((Context) activity);
        if (LocaleController.getInstance().getCurrentLocaleInfo().shortName.equals(language.lang_code)) {
            builder.setTitle(LocaleController.getString("Language", NUM));
            str = LocaleController.formatString("LanguageSame", NUM, language.name);
            builder.setNegativeButton(LocaleController.getString("OK", NUM), (DialogInterface.OnClickListener) null);
            builder.setNeutralButton(LocaleController.getString("SETTINGS", NUM), new AlertsCreator$$ExternalSyntheticLambda26(activity));
        } else if (language.strings_count == 0) {
            builder.setTitle(LocaleController.getString("LanguageUnknownTitle", NUM));
            str = LocaleController.formatString("LanguageUnknownCustomAlert", NUM, language.name);
            builder.setNegativeButton(LocaleController.getString("OK", NUM), (DialogInterface.OnClickListener) null);
        } else {
            builder.setTitle(LocaleController.getString("LanguageTitle", NUM));
            if (language.official) {
                str = LocaleController.formatString("LanguageAlert", NUM, language.name, Integer.valueOf((int) Math.ceil((double) ((((float) language.translated_count) / ((float) language.strings_count)) * 100.0f))));
            } else {
                str = LocaleController.formatString("LanguageCustomAlert", NUM, language.name, Integer.valueOf((int) Math.ceil((double) ((((float) language.translated_count) / ((float) language.strings_count)) * 100.0f))));
            }
            builder.setPositiveButton(LocaleController.getString("Change", NUM), new AlertsCreator$$ExternalSyntheticLambda16(language, activity));
            builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
        }
        SpannableStringBuilder spanned = new SpannableStringBuilder(AndroidUtilities.replaceTags(str));
        int start = TextUtils.indexOf(spanned, '[');
        if (start != -1) {
            end = TextUtils.indexOf(spanned, ']', start + 1);
            if (end != -1) {
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
        message.setMovementMethod(new AndroidUtilities.LinkMovementMethodMy());
        message.setTextColor(Theme.getColor("dialogTextBlack"));
        builder.setView(message);
        return builder;
    }

    static /* synthetic */ void lambda$createLanguageAlert$2(TLRPC.TL_langPackLanguage language, LaunchActivity activity, DialogInterface dialogInterface, int i) {
        String key;
        if (language.official) {
            key = "remote_" + language.lang_code;
        } else {
            key = "unofficial_" + language.lang_code;
        }
        LocaleController.LocaleInfo localeInfo = LocaleController.getInstance().getLanguageFromDict(key);
        if (localeInfo == null) {
            localeInfo = new LocaleController.LocaleInfo();
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

    public static boolean checkSlowMode(Context context, int currentAccount, long did, boolean few) {
        TLRPC.Chat chat;
        if (!DialogObject.isChatDialog(did) || (chat = MessagesController.getInstance(currentAccount).getChat(Long.valueOf(-did))) == null || !chat.slowmode_enabled || ChatObject.hasAdminRights(chat)) {
            return false;
        }
        if (!few) {
            TLRPC.ChatFull chatFull = MessagesController.getInstance(currentAccount).getChatFull(chat.id);
            if (chatFull == null) {
                chatFull = MessagesStorage.getInstance(currentAccount).loadChatInfo(chat.id, ChatObject.isChannel(chat), new CountDownLatch(1), false, false);
            }
            if (chatFull != null && chatFull.slowmode_next_send_date >= ConnectionsManager.getInstance(currentAccount).getCurrentTime()) {
                few = true;
            }
        }
        if (!few) {
            return false;
        }
        createSimpleAlert(context, chat.title, LocaleController.getString("SlowmodeSendError", NUM)).show();
        return true;
    }

    public static AlertDialog.Builder createSimpleAlert(Context context, String text) {
        return createSimpleAlert(context, (String) null, text);
    }

    public static AlertDialog.Builder createSimpleAlert(Context context, String title, String text) {
        return createSimpleAlert(context, title, text, (Theme.ResourcesProvider) null);
    }

    public static AlertDialog.Builder createSimpleAlert(Context context, String title, String text, Theme.ResourcesProvider resourcesProvider) {
        if (context == null || text == null) {
            return null;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title == null ? LocaleController.getString("AppName", NUM) : title);
        builder.setMessage(text);
        builder.setPositiveButton(LocaleController.getString("OK", NUM), (DialogInterface.OnClickListener) null);
        return builder;
    }

    public static Dialog showSimpleAlert(BaseFragment baseFragment, String text) {
        return showSimpleAlert(baseFragment, (String) null, text);
    }

    public static Dialog showSimpleAlert(BaseFragment baseFragment, String title, String text) {
        return showSimpleAlert(baseFragment, title, text, (Theme.ResourcesProvider) null);
    }

    public static Dialog showSimpleAlert(BaseFragment baseFragment, String title, String text, Theme.ResourcesProvider resourcesProvider) {
        if (text == null || baseFragment == null || baseFragment.getParentActivity() == null) {
            return null;
        }
        Dialog dialog = createSimpleAlert(baseFragment.getParentActivity(), title, text, resourcesProvider).create();
        baseFragment.showDialog(dialog);
        return dialog;
    }

    public static void showBlockReportSpamReplyAlert(ChatActivity fragment, MessageObject messageObject, long peerId, Theme.ResourcesProvider resourcesProvider) {
        ChatActivity chatActivity = fragment;
        long j = peerId;
        Theme.ResourcesProvider resourcesProvider2 = resourcesProvider;
        if (chatActivity != null && fragment.getParentActivity() != null && messageObject != null) {
            AccountInstance accountInstance = fragment.getAccountInstance();
            TLRPC.User user = j > 0 ? accountInstance.getMessagesController().getUser(Long.valueOf(peerId)) : null;
            TLRPC.Chat chat = j < 0 ? accountInstance.getMessagesController().getChat(Long.valueOf(-j)) : null;
            if (user != null || chat != null) {
                AlertDialog.Builder builder = new AlertDialog.Builder(fragment.getParentActivity(), resourcesProvider2);
                builder.setTitle(LocaleController.getString("BlockUser", NUM));
                if (user != null) {
                    builder.setMessage(AndroidUtilities.replaceTags(LocaleController.formatString("BlockUserReplyAlert", NUM, UserObject.getFirstName(user))));
                } else {
                    builder.setMessage(AndroidUtilities.replaceTags(LocaleController.formatString("BlockUserReplyAlert", NUM, chat.title)));
                }
                LinearLayout linearLayout = new LinearLayout(fragment.getParentActivity());
                linearLayout.setOrientation(1);
                CheckBoxCell[] cells = {new CheckBoxCell(fragment.getParentActivity(), 1, resourcesProvider2)};
                cells[0].setBackgroundDrawable(Theme.getSelectorDrawable(false));
                cells[0].setTag(0);
                cells[0].setText(LocaleController.getString("DeleteReportSpam", NUM), "", true, false);
                cells[0].setPadding(LocaleController.isRTL ? AndroidUtilities.dp(16.0f) : AndroidUtilities.dp(8.0f), 0, LocaleController.isRTL ? AndroidUtilities.dp(8.0f) : AndroidUtilities.dp(16.0f), 0);
                linearLayout.addView(cells[0], LayoutHelper.createLinear(-1, -2));
                cells[0].setOnClickListener(new AlertsCreator$$ExternalSyntheticLambda55(cells));
                builder.setCustomViewOffset(12);
                builder.setView(linearLayout);
                AlertsCreator$$ExternalSyntheticLambda18 alertsCreator$$ExternalSyntheticLambda18 = r0;
                String string = LocaleController.getString("BlockAndDeleteReplies", NUM);
                LinearLayout linearLayout2 = linearLayout;
                CheckBoxCell[] checkBoxCellArr = cells;
                AlertDialog.Builder builder2 = builder;
                AlertsCreator$$ExternalSyntheticLambda18 alertsCreator$$ExternalSyntheticLambda182 = new AlertsCreator$$ExternalSyntheticLambda18(user, accountInstance, fragment, chat, messageObject, cells, resourcesProvider);
                builder2.setPositiveButton(string, alertsCreator$$ExternalSyntheticLambda18);
                builder2.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
                AlertDialog dialog = builder2.create();
                chatActivity.showDialog(dialog);
                TextView button = (TextView) dialog.getButton(-1);
                if (button != null) {
                    button.setTextColor(Theme.getColor("dialogTextRed2"));
                }
            }
        }
    }

    static /* synthetic */ void lambda$showBlockReportSpamReplyAlert$3(CheckBoxCell[] cells, View v) {
        Integer num = (Integer) v.getTag();
        cells[num.intValue()].setChecked(!cells[num.intValue()].isChecked(), true);
    }

    static /* synthetic */ void lambda$showBlockReportSpamReplyAlert$5(TLRPC.User user, AccountInstance accountInstance, ChatActivity fragment, TLRPC.Chat chat, MessageObject messageObject, CheckBoxCell[] cells, Theme.ResourcesProvider resourcesProvider, DialogInterface dialogInterface, int i) {
        if (user != null) {
            accountInstance.getMessagesStorage().deleteUserChatHistory(fragment.getDialogId(), user.id);
        } else {
            accountInstance.getMessagesStorage().deleteUserChatHistory(fragment.getDialogId(), -chat.id);
        }
        TLRPC.TL_contacts_blockFromReplies request = new TLRPC.TL_contacts_blockFromReplies();
        request.msg_id = messageObject.getId();
        request.delete_message = true;
        request.delete_history = true;
        if (cells[0].isChecked()) {
            request.report_spam = true;
            if (fragment.getParentActivity() != null) {
                if (fragment instanceof ChatActivity) {
                    fragment.getUndoView().showWithAction(0, 74, (Runnable) null);
                } else if (fragment != null) {
                    BulletinFactory.of(fragment).createReportSent(resourcesProvider).show();
                } else {
                    Toast.makeText(fragment.getParentActivity(), LocaleController.getString("ReportChatSent", NUM), 0).show();
                }
            }
        }
        accountInstance.getConnectionsManager().sendRequest(request, new AlertsCreator$$ExternalSyntheticLambda82(accountInstance));
    }

    static /* synthetic */ void lambda$showBlockReportSpamReplyAlert$4(AccountInstance accountInstance, TLObject response, TLRPC.TL_error error) {
        if (response instanceof TLRPC.Updates) {
            accountInstance.getMessagesController().processUpdates((TLRPC.Updates) response, false);
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:13:0x004f  */
    /* JADX WARNING: Removed duplicated region for block: B:33:0x0147  */
    /* JADX WARNING: Removed duplicated region for block: B:51:0x0202  */
    /* JADX WARNING: Removed duplicated region for block: B:56:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static void showBlockReportSpamAlert(org.telegram.ui.ActionBar.BaseFragment r21, long r22, org.telegram.tgnet.TLRPC.User r24, org.telegram.tgnet.TLRPC.Chat r25, org.telegram.tgnet.TLRPC.EncryptedChat r26, boolean r27, org.telegram.tgnet.TLRPC.ChatFull r28, org.telegram.messenger.MessagesStorage.IntCallback r29, org.telegram.ui.ActionBar.Theme.ResourcesProvider r30) {
        /*
            r0 = r21
            r11 = r25
            r12 = r28
            r13 = r30
            if (r0 == 0) goto L_0x020c
            android.app.Activity r1 = r21.getParentActivity()
            if (r1 != 0) goto L_0x0012
            goto L_0x020c
        L_0x0012:
            org.telegram.messenger.AccountInstance r14 = r21.getAccountInstance()
            org.telegram.ui.ActionBar.AlertDialog$Builder r1 = new org.telegram.ui.ActionBar.AlertDialog$Builder
            android.app.Activity r2 = r21.getParentActivity()
            r1.<init>(r2, r13)
            r15 = r1
            int r1 = r21.getCurrentAccount()
            android.content.SharedPreferences r10 = org.telegram.messenger.MessagesController.getNotificationsSettings(r1)
            r1 = 1
            r2 = 0
            if (r26 != 0) goto L_0x0048
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r4 = "dialog_bar_report"
            r3.append(r4)
            r8 = r22
            r3.append(r8)
            java.lang.String r3 = r3.toString()
            boolean r3 = r10.getBoolean(r3, r2)
            if (r3 == 0) goto L_0x0046
            goto L_0x004a
        L_0x0046:
            r3 = 0
            goto L_0x004b
        L_0x0048:
            r8 = r22
        L_0x004a:
            r3 = 1
        L_0x004b:
            r16 = r3
            if (r24 == 0) goto L_0x0147
            r3 = 2131624593(0x7f0e0291, float:1.887637E38)
            java.lang.Object[] r4 = new java.lang.Object[r1]
            java.lang.String r5 = org.telegram.messenger.UserObject.getFirstName(r24)
            r4[r2] = r5
            java.lang.String r5 = "BlockUserTitle"
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r5, r3, r4)
            r15.setTitle(r3)
            r3 = 2131624587(0x7f0e028b, float:1.8876358E38)
            java.lang.Object[] r4 = new java.lang.Object[r1]
            java.lang.String r5 = org.telegram.messenger.UserObject.getFirstName(r24)
            r4[r2] = r5
            java.lang.String r5 = "BlockUserAlert"
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r5, r3, r4)
            android.text.SpannableStringBuilder r3 = org.telegram.messenger.AndroidUtilities.replaceTags(r3)
            r15.setMessage(r3)
            r3 = 2131624585(0x7f0e0289, float:1.8876354E38)
            java.lang.String r4 = "BlockContact"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            r4 = 2
            org.telegram.ui.Cells.CheckBoxCell[] r5 = new org.telegram.ui.Cells.CheckBoxCell[r4]
            android.widget.LinearLayout r6 = new android.widget.LinearLayout
            android.app.Activity r7 = r21.getParentActivity()
            r6.<init>(r7)
            r6.setOrientation(r1)
            r7 = 0
        L_0x0094:
            if (r7 >= r4) goto L_0x0134
            if (r7 != 0) goto L_0x00a1
            if (r16 != 0) goto L_0x00a1
            r19 = r3
            r20 = r10
            r8 = -1
            goto L_0x0127
        L_0x00a1:
            org.telegram.ui.Cells.CheckBoxCell r4 = new org.telegram.ui.Cells.CheckBoxCell
            android.app.Activity r2 = r21.getParentActivity()
            r4.<init>(r2, r1, r13)
            r5[r7] = r4
            r2 = r5[r7]
            r4 = 0
            android.graphics.drawable.Drawable r1 = org.telegram.ui.ActionBar.Theme.getSelectorDrawable(r4)
            r2.setBackgroundDrawable(r1)
            r1 = r5[r7]
            java.lang.Integer r2 = java.lang.Integer.valueOf(r7)
            r1.setTag(r2)
            java.lang.String r1 = ""
            if (r7 != 0) goto L_0x00d8
            r2 = r5[r7]
            r4 = 2131625247(0x7f0e051f, float:1.8877697E38)
            r19 = r3
            java.lang.String r3 = "DeleteReportSpam"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r3, r4)
            r4 = 1
            r8 = 0
            r2.setText(r3, r1, r4, r8)
            r20 = r10
            goto L_0x00ee
        L_0x00d8:
            r19 = r3
            r4 = 1
            r8 = 0
            r2 = r5[r7]
            r3 = 2131625256(0x7f0e0528, float:1.8877715E38)
            java.lang.Object[] r9 = new java.lang.Object[r8]
            r20 = r10
            java.lang.String r10 = "DeleteThisChat"
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r10, r3, r9)
            r2.setText(r3, r1, r4, r8)
        L_0x00ee:
            r1 = r5[r7]
            boolean r2 = org.telegram.messenger.LocaleController.isRTL
            r3 = 1098907648(0x41800000, float:16.0)
            r4 = 1090519040(0x41000000, float:8.0)
            if (r2 == 0) goto L_0x00fd
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r3)
            goto L_0x0101
        L_0x00fd:
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r4)
        L_0x0101:
            boolean r8 = org.telegram.messenger.LocaleController.isRTL
            if (r8 == 0) goto L_0x010a
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r4)
            goto L_0x010e
        L_0x010a:
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
        L_0x010e:
            r4 = 0
            r1.setPadding(r2, r4, r3, r4)
            r1 = r5[r7]
            r2 = -2
            r8 = -1
            android.widget.LinearLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createLinear(r8, r2)
            r6.addView(r1, r2)
            r1 = r5[r7]
            org.telegram.ui.Components.AlertsCreator$$ExternalSyntheticLambda54 r2 = new org.telegram.ui.Components.AlertsCreator$$ExternalSyntheticLambda54
            r2.<init>(r5)
            r1.setOnClickListener(r2)
        L_0x0127:
            int r7 = r7 + 1
            r8 = r22
            r3 = r19
            r10 = r20
            r1 = 1
            r2 = 0
            r4 = 2
            goto L_0x0094
        L_0x0134:
            r19 = r3
            r20 = r10
            r8 = -1
            r1 = 12
            r15.setCustomViewOffset(r1)
            r15.setView(r6)
            r17 = r5
            r10 = r19
            goto L_0x01c8
        L_0x0147:
            r20 = r10
            r8 = -1
            r5 = 0
            if (r11 == 0) goto L_0x018d
            if (r27 == 0) goto L_0x018d
            r1 = 2131627509(0x7f0e0df5, float:1.8882284E38)
            java.lang.String r2 = "ReportUnrelatedGroup"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r15.setTitle(r1)
            if (r12 == 0) goto L_0x0180
            org.telegram.tgnet.TLRPC$ChannelLocation r1 = r12.location
            boolean r1 = r1 instanceof org.telegram.tgnet.TLRPC.TL_channelLocation
            if (r1 == 0) goto L_0x0180
            org.telegram.tgnet.TLRPC$ChannelLocation r1 = r12.location
            org.telegram.tgnet.TLRPC$TL_channelLocation r1 = (org.telegram.tgnet.TLRPC.TL_channelLocation) r1
            r2 = 2131627510(0x7f0e0df6, float:1.8882286E38)
            r3 = 1
            java.lang.Object[] r3 = new java.lang.Object[r3]
            java.lang.String r4 = r1.address
            r6 = 0
            r3[r6] = r4
            java.lang.String r4 = "ReportUnrelatedGroupText"
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r4, r2, r3)
            android.text.SpannableStringBuilder r2 = org.telegram.messenger.AndroidUtilities.replaceTags(r2)
            r15.setMessage(r2)
            goto L_0x01bc
        L_0x0180:
            r1 = 2131627511(0x7f0e0df7, float:1.8882289E38)
            java.lang.String r2 = "ReportUnrelatedGroupTextNoAddress"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r15.setMessage(r1)
            goto L_0x01bc
        L_0x018d:
            r1 = 2131627502(0x7f0e0dee, float:1.888227E38)
            java.lang.String r2 = "ReportSpamTitle"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r15.setTitle(r1)
            boolean r1 = org.telegram.messenger.ChatObject.isChannel(r25)
            if (r1 == 0) goto L_0x01b0
            boolean r1 = r11.megagroup
            if (r1 != 0) goto L_0x01b0
            r1 = 2131627498(0x7f0e0dea, float:1.8882262E38)
            java.lang.String r2 = "ReportSpamAlertChannel"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r15.setMessage(r1)
            goto L_0x01bc
        L_0x01b0:
            r1 = 2131627499(0x7f0e0deb, float:1.8882264E38)
            java.lang.String r2 = "ReportSpamAlertGroup"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r15.setMessage(r1)
        L_0x01bc:
            r1 = 2131627480(0x7f0e0dd8, float:1.8882226E38)
            java.lang.String r2 = "ReportChat"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r10 = r3
            r17 = r5
        L_0x01c8:
            org.telegram.ui.Components.AlertsCreator$$ExternalSyntheticLambda19 r9 = new org.telegram.ui.Components.AlertsCreator$$ExternalSyntheticLambda19
            r1 = r9
            r2 = r24
            r3 = r14
            r4 = r17
            r5 = r22
            r7 = r25
            r11 = -1
            r8 = r26
            r11 = r9
            r9 = r27
            r12 = r10
            r18 = r20
            r10 = r29
            r1.<init>(r2, r3, r4, r5, r7, r8, r9, r10)
            r15.setPositiveButton(r12, r11)
            r1 = 2131624692(0x7f0e02f4, float:1.887657E38)
            java.lang.String r2 = "Cancel"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r2 = 0
            r15.setNegativeButton(r1, r2)
            org.telegram.ui.ActionBar.AlertDialog r1 = r15.create()
            r0.showDialog(r1)
            r2 = -1
            android.view.View r2 = r1.getButton(r2)
            android.widget.TextView r2 = (android.widget.TextView) r2
            if (r2 == 0) goto L_0x020b
            java.lang.String r3 = "dialogTextRed2"
            int r3 = org.telegram.ui.ActionBar.Theme.getColor(r3)
            r2.setTextColor(r3)
        L_0x020b:
            return
        L_0x020c:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.AlertsCreator.showBlockReportSpamAlert(org.telegram.ui.ActionBar.BaseFragment, long, org.telegram.tgnet.TLRPC$User, org.telegram.tgnet.TLRPC$Chat, org.telegram.tgnet.TLRPC$EncryptedChat, boolean, org.telegram.tgnet.TLRPC$ChatFull, org.telegram.messenger.MessagesStorage$IntCallback, org.telegram.ui.ActionBar.Theme$ResourcesProvider):void");
    }

    static /* synthetic */ void lambda$showBlockReportSpamAlert$6(CheckBoxCell[] cells, View v) {
        Integer num = (Integer) v.getTag();
        cells[num.intValue()].setChecked(!cells[num.intValue()].isChecked(), true);
    }

    static /* synthetic */ void lambda$showBlockReportSpamAlert$7(TLRPC.User currentUser, AccountInstance accountInstance, CheckBoxCell[] cells, long dialog_id, TLRPC.Chat currentChat, TLRPC.EncryptedChat encryptedChat, boolean isLocation, MessagesStorage.IntCallback callback, DialogInterface dialogInterface, int i) {
        TLRPC.User user = currentUser;
        long j = dialog_id;
        MessagesStorage.IntCallback intCallback = callback;
        if (user != null) {
            accountInstance.getMessagesController().blockPeer(user.id);
        }
        if (cells == null || (cells[0] != null && cells[0].isChecked())) {
            accountInstance.getMessagesController().reportSpam(dialog_id, currentUser, currentChat, encryptedChat, currentChat != null && isLocation);
        }
        if (cells == null || cells[1].isChecked()) {
            if (currentChat == null) {
                accountInstance.getMessagesController().deleteDialog(j, 0);
            } else if (ChatObject.isNotInChat(currentChat)) {
                accountInstance.getMessagesController().deleteDialog(j, 0);
            } else {
                accountInstance.getMessagesController().deleteParticipantFromChat(-j, accountInstance.getMessagesController().getUser(Long.valueOf(accountInstance.getUserConfig().getClientUserId())), (TLRPC.ChatFull) null);
            }
            intCallback.run(1);
            return;
        }
        intCallback.run(0);
    }

    public static void showCustomNotificationsDialog(BaseFragment parentFragment, long did, int globalType, ArrayList<NotificationsSettingsActivity.NotificationException> exceptions, int currentAccount, MessagesStorage.IntCallback callback) {
        showCustomNotificationsDialog(parentFragment, did, globalType, exceptions, currentAccount, callback, (MessagesStorage.IntCallback) null);
    }

    public static void showCustomNotificationsDialog(BaseFragment parentFragment, long did, int globalType, ArrayList<NotificationsSettingsActivity.NotificationException> exceptions, int currentAccount, MessagesStorage.IntCallback callback, MessagesStorage.IntCallback resultCallback) {
        Drawable drawable;
        String[] descriptions;
        boolean defaultEnabled;
        AlertDialog.Builder builder;
        LinearLayout linearLayout;
        int a;
        BaseFragment baseFragment = parentFragment;
        long j = did;
        if (baseFragment != null && parentFragment.getParentActivity() != null) {
            boolean defaultEnabled2 = NotificationsController.getInstance(currentAccount).isGlobalNotificationsEnabled(j);
            String[] strArr = new String[5];
            boolean z = false;
            strArr[0] = LocaleController.getString("NotificationsTurnOn", NUM);
            boolean z2 = true;
            strArr[1] = LocaleController.formatString("MuteFor", NUM, LocaleController.formatPluralString("Hours", 1));
            strArr[2] = LocaleController.formatString("MuteFor", NUM, LocaleController.formatPluralString("Days", 2));
            Drawable drawable2 = null;
            strArr[3] = (j != 0 || !(baseFragment instanceof NotificationsCustomSettingsActivity)) ? LocaleController.getString("NotificationsCustomize", NUM) : null;
            strArr[4] = LocaleController.getString("NotificationsTurnOff", NUM);
            String[] descriptions2 = strArr;
            int[] icons = {NUM, NUM, NUM, NUM, NUM};
            LinearLayout linearLayout2 = new LinearLayout(parentFragment.getParentActivity());
            linearLayout2.setOrientation(1);
            AlertDialog.Builder builder2 = new AlertDialog.Builder((Context) parentFragment.getParentActivity());
            int a2 = 0;
            while (a2 < descriptions2.length) {
                if (descriptions2[a2] == null) {
                    a = a2;
                    builder = builder2;
                    descriptions = descriptions2;
                    drawable = drawable2;
                    defaultEnabled = defaultEnabled2;
                    linearLayout = linearLayout2;
                } else {
                    TextView textView = new TextView(parentFragment.getParentActivity());
                    Drawable drawable3 = parentFragment.getParentActivity().getResources().getDrawable(icons[a2]);
                    if (a2 == descriptions2.length - (z2 ? 1 : 0)) {
                        textView.setTextColor(Theme.getColor("dialogTextRed"));
                        drawable3.setColorFilter(new PorterDuffColorFilter(Theme.getColor("dialogRedIcon"), PorterDuff.Mode.MULTIPLY));
                    } else {
                        textView.setTextColor(Theme.getColor("dialogTextBlack"));
                        drawable3.setColorFilter(new PorterDuffColorFilter(Theme.getColor("dialogIcon"), PorterDuff.Mode.MULTIPLY));
                    }
                    textView.setTextSize(z2, 16.0f);
                    textView.setLines(z2);
                    textView.setMaxLines(z2);
                    textView.setCompoundDrawablesWithIntrinsicBounds(drawable3, drawable2, drawable2, drawable2);
                    textView.setTag(Integer.valueOf(a2));
                    textView.setBackgroundDrawable(Theme.getSelectorDrawable(z));
                    textView.setPadding(AndroidUtilities.dp(24.0f), z ? 1 : 0, AndroidUtilities.dp(24.0f), z);
                    textView.setSingleLine(z2);
                    textView.setGravity(19);
                    textView.setCompoundDrawablePadding(AndroidUtilities.dp(26.0f));
                    textView.setText(descriptions2[a2]);
                    linearLayout2.addView(textView, LayoutHelper.createLinear(-1, 48, 51));
                    AlertsCreator$$ExternalSyntheticLambda37 alertsCreator$$ExternalSyntheticLambda37 = r0;
                    Drawable drawable4 = drawable3;
                    a = a2;
                    builder = builder2;
                    defaultEnabled = defaultEnabled2;
                    linearLayout = linearLayout2;
                    descriptions = descriptions2;
                    drawable = drawable2;
                    AlertsCreator$$ExternalSyntheticLambda37 alertsCreator$$ExternalSyntheticLambda372 = new AlertsCreator$$ExternalSyntheticLambda37(did, currentAccount, defaultEnabled2, resultCallback, globalType, parentFragment, exceptions, callback, builder);
                    textView.setOnClickListener(alertsCreator$$ExternalSyntheticLambda37);
                }
                a2 = a + 1;
                long j2 = did;
                linearLayout2 = linearLayout;
                builder2 = builder;
                defaultEnabled2 = defaultEnabled;
                descriptions2 = descriptions;
                drawable2 = drawable;
                z2 = true;
                z = false;
            }
            boolean z3 = defaultEnabled2;
            AlertDialog.Builder builder3 = builder2;
            builder3.setTitle(LocaleController.getString("Notifications", NUM));
            builder3.setView(linearLayout2);
            baseFragment.showDialog(builder3.create());
        }
    }

    static /* synthetic */ void lambda$showCustomNotificationsDialog$8(long did, int currentAccount, boolean defaultEnabled, MessagesStorage.IntCallback resultCallback, int globalType, BaseFragment parentFragment, ArrayList exceptions, MessagesStorage.IntCallback callback, AlertDialog.Builder builder, View v) {
        long flags;
        long j = did;
        MessagesStorage.IntCallback intCallback = resultCallback;
        int i = globalType;
        BaseFragment baseFragment = parentFragment;
        MessagesStorage.IntCallback intCallback2 = callback;
        int i2 = ((Integer) v.getTag()).intValue();
        if (i2 == 0) {
            if (j != 0) {
                SharedPreferences.Editor editor = MessagesController.getNotificationsSettings(currentAccount).edit();
                if (defaultEnabled) {
                    editor.remove("notify2_" + j);
                } else {
                    editor.putInt("notify2_" + j, 0);
                }
                MessagesStorage.getInstance(currentAccount).setDialogFlags(j, 0);
                editor.commit();
                TLRPC.Dialog dialog = MessagesController.getInstance(currentAccount).dialogs_dict.get(j);
                if (dialog != null) {
                    dialog.notify_settings = new TLRPC.TL_peerNotifySettings();
                }
                NotificationsController.getInstance(currentAccount).updateServerNotificationsSettings(j);
                if (intCallback != null) {
                    if (defaultEnabled) {
                        intCallback.run(0);
                    } else {
                        intCallback.run(1);
                    }
                }
                ArrayList arrayList = exceptions;
            } else {
                NotificationsController.getInstance(currentAccount).setGlobalNotificationsEnabled(i, 0);
                ArrayList arrayList2 = exceptions;
            }
        } else if (i2 != 3) {
            ArrayList arrayList3 = exceptions;
            int untilTime = ConnectionsManager.getInstance(currentAccount).getCurrentTime();
            if (i2 == 1) {
                untilTime += 3600;
            } else if (i2 == 2) {
                untilTime += 172800;
            } else if (i2 == 4) {
                untilTime = Integer.MAX_VALUE;
            }
            if (j != 0) {
                SharedPreferences.Editor editor2 = MessagesController.getNotificationsSettings(currentAccount).edit();
                if (i2 != 4) {
                    editor2.putInt("notify2_" + j, 3);
                    editor2.putInt("notifyuntil_" + j, untilTime);
                    flags = (((long) untilTime) << 32) | 1;
                } else if (!defaultEnabled) {
                    editor2.remove("notify2_" + j);
                    flags = 0;
                } else {
                    editor2.putInt("notify2_" + j, 2);
                    flags = 1;
                }
                NotificationsController.getInstance(currentAccount).removeNotificationsForDialog(j);
                MessagesStorage.getInstance(currentAccount).setDialogFlags(j, flags);
                editor2.commit();
                TLRPC.Dialog dialog2 = MessagesController.getInstance(currentAccount).dialogs_dict.get(j);
                if (dialog2 != null) {
                    dialog2.notify_settings = new TLRPC.TL_peerNotifySettings();
                    if (i2 != 4 || defaultEnabled) {
                        dialog2.notify_settings.mute_until = untilTime;
                    }
                }
                NotificationsController.getInstance(currentAccount).updateServerNotificationsSettings(j);
                if (intCallback != null) {
                    if (i2 != 4 || defaultEnabled) {
                        intCallback.run(1);
                    } else {
                        intCallback.run(0);
                    }
                }
            } else if (i2 == 4) {
                NotificationsController.getInstance(currentAccount).setGlobalNotificationsEnabled(i, Integer.MAX_VALUE);
            } else {
                NotificationsController.getInstance(currentAccount).setGlobalNotificationsEnabled(i, untilTime);
            }
        } else if (j != 0) {
            Bundle args = new Bundle();
            args.putLong("dialog_id", j);
            baseFragment.presentFragment(new ProfileNotificationsActivity(args));
            ArrayList arrayList4 = exceptions;
        } else {
            baseFragment.presentFragment(new NotificationsCustomSettingsActivity(i, exceptions));
        }
        if (intCallback2 != null) {
            intCallback2.run(i2);
        }
        builder.getDismissRunnable().run();
        int setting = -1;
        if (i2 == 0) {
            setting = 4;
        } else if (i2 == 1) {
            setting = 0;
        } else if (i2 == 2) {
            setting = 2;
        } else if (i2 == 4) {
            setting = 3;
        }
        if (setting >= 0 && BulletinFactory.canShowBulletin(parentFragment)) {
            BulletinFactory.createMuteBulletin(baseFragment, setting).show();
        }
    }

    public static AlertDialog showSecretLocationAlert(Context context, int currentAccount, Runnable onSelectRunnable, boolean inChat, Theme.ResourcesProvider resourcesProvider) {
        Context context2 = context;
        Theme.ResourcesProvider resourcesProvider2 = resourcesProvider;
        ArrayList<String> arrayList = new ArrayList<>();
        ArrayList<Integer> types = new ArrayList<>();
        int providers = MessagesController.getInstance(currentAccount).availableMapProviders;
        if ((providers & 1) != 0) {
            arrayList.add(LocaleController.getString("MapPreviewProviderTelegram", NUM));
            types.add(0);
        }
        if ((providers & 2) != 0) {
            arrayList.add(LocaleController.getString("MapPreviewProviderGoogle", NUM));
            types.add(1);
        }
        if ((providers & 4) != 0) {
            arrayList.add(LocaleController.getString("MapPreviewProviderYandex", NUM));
            types.add(3);
        }
        arrayList.add(LocaleController.getString("MapPreviewProviderNobody", NUM));
        types.add(2);
        AlertDialog.Builder builder = new AlertDialog.Builder(context, resourcesProvider2);
        builder.setTitle(LocaleController.getString("MapPreviewProviderTitle", NUM));
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(1);
        builder.setView(linearLayout);
        for (int a = 0; a < arrayList.size(); a++) {
            RadioColorCell cell = new RadioColorCell(context, resourcesProvider2);
            cell.setPadding(AndroidUtilities.dp(4.0f), 0, AndroidUtilities.dp(4.0f), 0);
            cell.setTag(Integer.valueOf(a));
            cell.setCheckColor(Theme.getColor("radioBackground"), Theme.getColor("dialogRadioBackgroundChecked"));
            cell.setTextAndValue(arrayList.get(a), SharedConfig.mapPreviewType == types.get(a).intValue());
            linearLayout.addView(cell);
            cell.setOnClickListener(new AlertsCreator$$ExternalSyntheticLambda41(types, onSelectRunnable, builder));
        }
        Runnable runnable = onSelectRunnable;
        if (!inChat) {
            builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
        }
        AlertDialog dialog = builder.show();
        if (inChat) {
            dialog.setCanceledOnTouchOutside(false);
        }
        return dialog;
    }

    static /* synthetic */ void lambda$showSecretLocationAlert$9(ArrayList types, Runnable onSelectRunnable, AlertDialog.Builder builder, View v) {
        SharedConfig.setSecretMapPreviewType(((Integer) types.get(((Integer) v.getTag()).intValue())).intValue());
        if (onSelectRunnable != null) {
            onSelectRunnable.run();
        }
        builder.getDismissRunnable().run();
    }

    /* access modifiers changed from: private */
    public static void updateDayPicker(NumberPicker dayPicker, NumberPicker monthPicker, NumberPicker yearPicker) {
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

    public static void showOpenUrlAlert(BaseFragment fragment, String url, boolean punycode, boolean ask) {
        showOpenUrlAlert(fragment, url, punycode, true, ask, (Theme.ResourcesProvider) null);
    }

    public static void showOpenUrlAlert(BaseFragment fragment, String url, boolean punycode, boolean ask, Theme.ResourcesProvider resourcesProvider) {
        showOpenUrlAlert(fragment, url, punycode, true, ask, resourcesProvider);
    }

    public static void showOpenUrlAlert(BaseFragment fragment, String url, boolean punycode, boolean tryTelegraph, boolean ask, Theme.ResourcesProvider resourcesProvider) {
        String urlFinal;
        BaseFragment baseFragment = fragment;
        String str = url;
        if (baseFragment == null) {
            boolean z = tryTelegraph;
            Theme.ResourcesProvider resourcesProvider2 = resourcesProvider;
        } else if (fragment.getParentActivity() == null) {
            boolean z2 = tryTelegraph;
            Theme.ResourcesProvider resourcesProvider3 = resourcesProvider;
        } else {
            long inlineReturn = baseFragment instanceof ChatActivity ? ((ChatActivity) baseFragment).getInlineReturn() : 0;
            boolean z3 = true;
            if (Browser.isInternalUrl(str, (boolean[]) null)) {
                Theme.ResourcesProvider resourcesProvider4 = resourcesProvider;
            } else if (!ask) {
                Theme.ResourcesProvider resourcesProvider5 = resourcesProvider;
            } else {
                if (punycode) {
                    try {
                        Uri uri = Uri.parse(url);
                        urlFinal = uri.getScheme() + "://" + IDN.toASCII(uri.getHost(), 1) + uri.getPath();
                    } catch (Exception e) {
                        FileLog.e((Throwable) e);
                        urlFinal = url;
                    }
                } else {
                    urlFinal = url;
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(fragment.getParentActivity(), resourcesProvider);
                builder.setTitle(LocaleController.getString("OpenUrlTitle", NUM));
                String format = LocaleController.getString("OpenUrlAlert2", NUM);
                int index = format.indexOf("%");
                SpannableStringBuilder stringBuilder = new SpannableStringBuilder(String.format(format, new Object[]{urlFinal}));
                if (index >= 0) {
                    stringBuilder.setSpan(new URLSpan(urlFinal), index, urlFinal.length() + index, 33);
                }
                builder.setMessage(stringBuilder);
                builder.setMessageTextViewClickable(false);
                String str2 = urlFinal;
                String string = LocaleController.getString("Open", NUM);
                AlertsCreator$$ExternalSyntheticLambda22 alertsCreator$$ExternalSyntheticLambda22 = r1;
                SpannableStringBuilder spannableStringBuilder = stringBuilder;
                AlertsCreator$$ExternalSyntheticLambda22 alertsCreator$$ExternalSyntheticLambda222 = new AlertsCreator$$ExternalSyntheticLambda22(fragment, url, inlineReturn, tryTelegraph);
                builder.setPositiveButton(string, alertsCreator$$ExternalSyntheticLambda22);
                builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
                baseFragment.showDialog(builder.create());
                boolean z4 = tryTelegraph;
                return;
            }
            Activity parentActivity = fragment.getParentActivity();
            if (inlineReturn != 0) {
                z3 = false;
            }
            Browser.openUrl((Context) parentActivity, str, z3, tryTelegraph);
        }
    }

    static /* synthetic */ void lambda$showOpenUrlAlert$10(BaseFragment fragment, String url, long inlineReturn, boolean tryTelegraph, DialogInterface dialogInterface, int i) {
        Browser.openUrl((Context) fragment.getParentActivity(), url, inlineReturn == 0, tryTelegraph);
    }

    public static AlertDialog createSupportAlert(final BaseFragment fragment) {
        if (fragment == null || fragment.getParentActivity() == null) {
            return null;
        }
        TextView message = new TextView(fragment.getParentActivity());
        Spannable spanned = new SpannableString(Html.fromHtml(LocaleController.getString("AskAQuestionInfo", NUM).replace("\n", "<br>")));
        URLSpan[] spans = (URLSpan[]) spanned.getSpans(0, spanned.length(), URLSpan.class);
        for (URLSpan span : spans) {
            int start = spanned.getSpanStart(span);
            int end = spanned.getSpanEnd(span);
            spanned.removeSpan(span);
            spanned.setSpan(new URLSpanNoUnderline(span.getURL()) {
                public void onClick(View widget) {
                    fragment.dismissCurrentDialog();
                    super.onClick(widget);
                }
            }, start, end, 0);
        }
        message.setText(spanned);
        message.setTextSize(1, 16.0f);
        message.setLinkTextColor(Theme.getColor("dialogTextLink"));
        message.setHighlightColor(Theme.getColor("dialogLinkSelection"));
        message.setPadding(AndroidUtilities.dp(23.0f), 0, AndroidUtilities.dp(23.0f), 0);
        message.setMovementMethod(new AndroidUtilities.LinkMovementMethodMy());
        message.setTextColor(Theme.getColor("dialogTextBlack"));
        AlertDialog.Builder builder1 = new AlertDialog.Builder((Context) fragment.getParentActivity());
        builder1.setView(message);
        builder1.setTitle(LocaleController.getString("AskAQuestion", NUM));
        builder1.setPositiveButton(LocaleController.getString("AskButton", NUM), new AlertsCreator$$ExternalSyntheticLambda20(fragment));
        builder1.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
        return builder1.create();
    }

    /* access modifiers changed from: private */
    public static void performAskAQuestion(BaseFragment fragment) {
        String userString;
        int currentAccount = fragment.getCurrentAccount();
        SharedPreferences preferences = MessagesController.getMainSettings(currentAccount);
        long uid = AndroidUtilities.getPrefIntOrLong(preferences, "support_id2", 0);
        TLRPC.User supportUser = null;
        if (!(uid == 0 || (supportUser = MessagesController.getInstance(currentAccount).getUser(Long.valueOf(uid))) != null || (userString = preferences.getString("support_user", (String) null)) == null)) {
            try {
                byte[] datacentersBytes = Base64.decode(userString, 0);
                if (datacentersBytes != null) {
                    SerializedData data = new SerializedData(datacentersBytes);
                    supportUser = TLRPC.User.TLdeserialize(data, data.readInt32(false), false);
                    if (supportUser != null && supportUser.id == 333000) {
                        supportUser = null;
                    }
                    data.cleanup();
                }
            } catch (Exception e) {
                FileLog.e((Throwable) e);
                supportUser = null;
            }
        }
        if (supportUser == null) {
            AlertDialog progressDialog = new AlertDialog(fragment.getParentActivity(), 3);
            progressDialog.setCanCacnel(false);
            progressDialog.show();
            ConnectionsManager.getInstance(currentAccount).sendRequest(new TLRPC.TL_help_getSupport(), new AlertsCreator$$ExternalSyntheticLambda81(preferences, progressDialog, currentAccount, fragment));
            return;
        }
        MessagesController.getInstance(currentAccount).putUser(supportUser, true);
        Bundle args = new Bundle();
        args.putLong("user_id", supportUser.id);
        fragment.presentFragment(new ChatActivity(args));
    }

    static /* synthetic */ void lambda$performAskAQuestion$14(SharedPreferences preferences, AlertDialog progressDialog, int currentAccount, BaseFragment fragment, TLObject response, TLRPC.TL_error error) {
        if (error == null) {
            AndroidUtilities.runOnUIThread(new AlertsCreator$$ExternalSyntheticLambda73(preferences, (TLRPC.TL_help_support) response, progressDialog, currentAccount, fragment));
        } else {
            AndroidUtilities.runOnUIThread(new AlertsCreator$$ExternalSyntheticLambda74(progressDialog));
        }
    }

    static /* synthetic */ void lambda$performAskAQuestion$12(SharedPreferences preferences, TLRPC.TL_help_support res, AlertDialog progressDialog, int currentAccount, BaseFragment fragment) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putLong("support_id2", res.user.id);
        SerializedData data = new SerializedData();
        res.user.serializeToStream(data);
        editor.putString("support_user", Base64.encodeToString(data.toByteArray(), 0));
        editor.commit();
        data.cleanup();
        try {
            progressDialog.dismiss();
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
        ArrayList<TLRPC.User> users = new ArrayList<>();
        users.add(res.user);
        MessagesStorage.getInstance(currentAccount).putUsersAndChats(users, (ArrayList<TLRPC.Chat>) null, true, true);
        MessagesController.getInstance(currentAccount).putUser(res.user, false);
        Bundle args = new Bundle();
        args.putLong("user_id", res.user.id);
        fragment.presentFragment(new ChatActivity(args));
    }

    static /* synthetic */ void lambda$performAskAQuestion$13(AlertDialog progressDialog) {
        try {
            progressDialog.dismiss();
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    public static void createImportDialogAlert(BaseFragment fragment, String title, String message, TLRPC.User user, TLRPC.Chat chat, Runnable onProcessRunnable) {
        BaseFragment baseFragment = fragment;
        TLRPC.User user2 = user;
        TLRPC.Chat chat2 = chat;
        if (baseFragment == null || fragment.getParentActivity() == null) {
            Runnable runnable = onProcessRunnable;
        } else if (chat2 == null && user2 == null) {
            Runnable runnable2 = onProcessRunnable;
        } else {
            int account = fragment.getCurrentAccount();
            Context context = fragment.getParentActivity();
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            long selfUserId = UserConfig.getInstance(account).getClientUserId();
            TextView messageTextView = new TextView(context);
            messageTextView.setTextColor(Theme.getColor("dialogTextBlack"));
            messageTextView.setTextSize(1, 16.0f);
            messageTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
            FrameLayout frameLayout = new FrameLayout(context);
            builder.setView(frameLayout);
            AvatarDrawable avatarDrawable = new AvatarDrawable();
            avatarDrawable.setTextSize(AndroidUtilities.dp(12.0f));
            BackupImageView imageView = new BackupImageView(context);
            imageView.setRoundRadius(AndroidUtilities.dp(20.0f));
            frameLayout.addView(imageView, LayoutHelper.createFrame(40, 40.0f, (LocaleController.isRTL ? 5 : 3) | 48, 22.0f, 5.0f, 22.0f, 0.0f));
            TextView textView = new TextView(context);
            textView.setTextColor(Theme.getColor("actionBarDefaultSubmenuItem"));
            textView.setTextSize(1, 20.0f);
            textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            textView.setLines(1);
            textView.setMaxLines(1);
            textView.setSingleLine(true);
            textView.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
            textView.setEllipsize(TextUtils.TruncateAt.END);
            textView.setText(LocaleController.getString("ImportMessages", NUM));
            int i = (LocaleController.isRTL ? 5 : 3) | 48;
            int i2 = 21;
            float f = (float) (LocaleController.isRTL ? 21 : 76);
            if (LocaleController.isRTL) {
                i2 = 76;
            }
            frameLayout.addView(textView, LayoutHelper.createFrame(-1, -2.0f, i, f, 11.0f, (float) i2, 0.0f));
            frameLayout.addView(messageTextView, LayoutHelper.createFrame(-2, -2.0f, (LocaleController.isRTL ? 5 : 3) | 48, 24.0f, 57.0f, 24.0f, 9.0f));
            if (user2 == null) {
                avatarDrawable.setInfo(chat2);
                imageView.setForUserOrChat(chat2, avatarDrawable);
            } else if (UserObject.isReplyUser(user)) {
                avatarDrawable.setSmallSize(true);
                avatarDrawable.setAvatarType(12);
                imageView.setImage((ImageLocation) null, (String) null, (Drawable) avatarDrawable, (Object) user2);
                TextView textView2 = textView;
            } else {
                TextView textView3 = textView;
                if (user2.id == selfUserId) {
                    avatarDrawable.setSmallSize(true);
                    avatarDrawable.setAvatarType(1);
                    imageView.setImage((ImageLocation) null, (String) null, (Drawable) avatarDrawable, (Object) user2);
                } else {
                    avatarDrawable.setSmallSize(false);
                    avatarDrawable.setInfo(user2);
                    imageView.setForUserOrChat(user2, avatarDrawable);
                }
            }
            messageTextView.setText(AndroidUtilities.replaceTags(message));
            builder.setPositiveButton(LocaleController.getString("Import", NUM), new AlertsCreator$$ExternalSyntheticLambda10(onProcessRunnable));
            builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
            baseFragment.showDialog(builder.create());
        }
    }

    static /* synthetic */ void lambda$createImportDialogAlert$15(Runnable onProcessRunnable, DialogInterface dialogInterface, int i) {
        if (onProcessRunnable != null) {
            onProcessRunnable.run();
        }
    }

    public static void createClearOrDeleteDialogAlert(BaseFragment fragment, boolean clear, TLRPC.Chat chat, TLRPC.User user, boolean secret, MessagesStorage.BooleanCallback onProcessRunnable) {
        createClearOrDeleteDialogAlert(fragment, clear, false, false, chat, user, secret, false, onProcessRunnable, (Theme.ResourcesProvider) null);
    }

    public static void createClearOrDeleteDialogAlert(BaseFragment fragment, boolean clear, TLRPC.Chat chat, TLRPC.User user, boolean secret, boolean checkDeleteForAll, MessagesStorage.BooleanCallback onProcessRunnable) {
        TLRPC.Chat chat2 = chat;
        createClearOrDeleteDialogAlert(fragment, clear, chat2 != null && chat2.creator, false, chat, user, secret, checkDeleteForAll, onProcessRunnable, (Theme.ResourcesProvider) null);
    }

    public static void createClearOrDeleteDialogAlert(BaseFragment fragment, boolean clear, TLRPC.Chat chat, TLRPC.User user, boolean secret, boolean checkDeleteForAll, MessagesStorage.BooleanCallback onProcessRunnable, Theme.ResourcesProvider resourcesProvider) {
        TLRPC.Chat chat2 = chat;
        createClearOrDeleteDialogAlert(fragment, clear, chat2 != null && chat2.creator, false, chat, user, secret, checkDeleteForAll, onProcessRunnable, resourcesProvider);
    }

    /* JADX WARNING: Removed duplicated region for block: B:102:0x0225  */
    /* JADX WARNING: Removed duplicated region for block: B:124:0x02f1  */
    /* JADX WARNING: Removed duplicated region for block: B:127:0x02fc  */
    /* JADX WARNING: Removed duplicated region for block: B:134:0x032f  */
    /* JADX WARNING: Removed duplicated region for block: B:136:0x0339  */
    /* JADX WARNING: Removed duplicated region for block: B:140:0x0363  */
    /* JADX WARNING: Removed duplicated region for block: B:187:0x0508  */
    /* JADX WARNING: Removed duplicated region for block: B:188:0x0514  */
    /* JADX WARNING: Removed duplicated region for block: B:209:0x05d7  */
    /* JADX WARNING: Removed duplicated region for block: B:212:? A[RETURN, SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:76:0x01dd  */
    /* JADX WARNING: Removed duplicated region for block: B:77:0x01e5  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static void createClearOrDeleteDialogAlert(org.telegram.ui.ActionBar.BaseFragment r45, boolean r46, boolean r47, boolean r48, org.telegram.tgnet.TLRPC.Chat r49, org.telegram.tgnet.TLRPC.User r50, boolean r51, boolean r52, org.telegram.messenger.MessagesStorage.BooleanCallback r53, org.telegram.ui.ActionBar.Theme.ResourcesProvider r54) {
        /*
            r13 = r45
            r14 = r49
            r15 = r50
            r12 = r54
            if (r13 == 0) goto L_0x05e1
            android.app.Activity r0 = r45.getParentActivity()
            if (r0 == 0) goto L_0x05e1
            if (r14 != 0) goto L_0x0017
            if (r15 != 0) goto L_0x0017
            r2 = r13
            goto L_0x05e2
        L_0x0017:
            int r16 = r45.getCurrentAccount()
            android.app.Activity r11 = r45.getParentActivity()
            org.telegram.ui.ActionBar.AlertDialog$Builder r0 = new org.telegram.ui.ActionBar.AlertDialog$Builder
            r0.<init>(r11, r12)
            r10 = r0
            org.telegram.messenger.UserConfig r0 = org.telegram.messenger.UserConfig.getInstance(r16)
            long r17 = r0.getClientUserId()
            r0 = 1
            org.telegram.ui.Cells.CheckBoxCell[] r9 = new org.telegram.ui.Cells.CheckBoxCell[r0]
            android.widget.TextView r1 = new android.widget.TextView
            r1.<init>(r11)
            r8 = r1
            java.lang.String r1 = "dialogTextBlack"
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r1)
            r8.setTextColor(r1)
            r1 = 1098907648(0x41800000, float:16.0)
            r8.setTextSize(r0, r1)
            boolean r2 = org.telegram.messenger.LocaleController.isRTL
            if (r2 == 0) goto L_0x004a
            r2 = 5
            goto L_0x004b
        L_0x004a:
            r2 = 3
        L_0x004b:
            r2 = r2 | 48
            r8.setGravity(r2)
            boolean r2 = org.telegram.messenger.ChatObject.isChannel(r49)
            if (r2 == 0) goto L_0x0060
            java.lang.String r2 = r14.username
            boolean r2 = android.text.TextUtils.isEmpty(r2)
            if (r2 != 0) goto L_0x0060
            r2 = 1
            goto L_0x0061
        L_0x0060:
            r2 = 0
        L_0x0061:
            r19 = r2
            org.telegram.ui.Components.AlertsCreator$3 r2 = new org.telegram.ui.Components.AlertsCreator$3
            r2.<init>(r11, r9)
            r7 = r2
            r10.setView(r7)
            org.telegram.ui.Components.AvatarDrawable r2 = new org.telegram.ui.Components.AvatarDrawable
            r2.<init>()
            r6 = r2
            r2 = 1094713344(0x41400000, float:12.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            r6.setTextSize(r2)
            org.telegram.ui.Components.BackupImageView r2 = new org.telegram.ui.Components.BackupImageView
            r2.<init>(r11)
            r3 = 1101004800(0x41a00000, float:20.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r3)
            r2.setRoundRadius(r4)
            r22 = 40
            r23 = 1109393408(0x42200000, float:40.0)
            boolean r4 = org.telegram.messenger.LocaleController.isRTL
            if (r4 == 0) goto L_0x0093
            r4 = 5
            goto L_0x0094
        L_0x0093:
            r4 = 3
        L_0x0094:
            r24 = r4 | 48
            r25 = 1102053376(0x41b00000, float:22.0)
            r26 = 1084227584(0x40a00000, float:5.0)
            r27 = 1102053376(0x41b00000, float:22.0)
            r28 = 0
            android.widget.FrameLayout$LayoutParams r4 = org.telegram.ui.Components.LayoutHelper.createFrame(r22, r23, r24, r25, r26, r27, r28)
            r7.addView(r2, r4)
            android.widget.TextView r4 = new android.widget.TextView
            r4.<init>(r11)
            java.lang.String r22 = "actionBarDefaultSubmenuItem"
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r22)
            r4.setTextColor(r1)
            r4.setTextSize(r0, r3)
            java.lang.String r1 = "fonts/rmedium.ttf"
            android.graphics.Typeface r1 = org.telegram.messenger.AndroidUtilities.getTypeface(r1)
            r4.setTypeface(r1)
            r4.setLines(r0)
            r4.setMaxLines(r0)
            r4.setSingleLine(r0)
            boolean r1 = org.telegram.messenger.LocaleController.isRTL
            if (r1 == 0) goto L_0x00ce
            r1 = 5
            goto L_0x00cf
        L_0x00ce:
            r1 = 3
        L_0x00cf:
            r1 = r1 | 16
            r4.setGravity(r1)
            android.text.TextUtils$TruncateAt r1 = android.text.TextUtils.TruncateAt.END
            r4.setEllipsize(r1)
            java.lang.String r3 = "LeaveChannelMenu"
            java.lang.String r0 = "DeleteChatUser"
            java.lang.String r1 = "ClearHistoryCache"
            java.lang.String r5 = "ClearHistory"
            r29 = r10
            java.lang.String r10 = "LeaveMegaMenu"
            if (r46 == 0) goto L_0x0105
            if (r19 == 0) goto L_0x00f7
            r31 = r2
            r13 = 2131624988(0x7f0e041c, float:1.8877171E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r1, r13)
            r4.setText(r2)
            goto L_0x0172
        L_0x00f7:
            r31 = r2
            r2 = 2131624987(0x7f0e041b, float:1.887717E38)
            java.lang.String r13 = org.telegram.messenger.LocaleController.getString(r5, r2)
            r4.setText(r13)
            goto L_0x0172
        L_0x0105:
            r31 = r2
            if (r47 == 0) goto L_0x0138
            boolean r2 = org.telegram.messenger.ChatObject.isChannel(r49)
            java.lang.String r13 = "DeleteMegaMenu"
            if (r2 == 0) goto L_0x012d
            boolean r2 = r14.megagroup
            if (r2 == 0) goto L_0x0120
            r2 = 2131625237(0x7f0e0515, float:1.8877676E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r13, r2)
            r4.setText(r2)
            goto L_0x0172
        L_0x0120:
            r2 = 2131624766(0x7f0e033e, float:1.887672E38)
            java.lang.String r13 = "ChannelDeleteMenu"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r13, r2)
            r4.setText(r2)
            goto L_0x0172
        L_0x012d:
            r2 = 2131625237(0x7f0e0515, float:1.8877676E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r13, r2)
            r4.setText(r2)
            goto L_0x0172
        L_0x0138:
            if (r14 == 0) goto L_0x0168
            boolean r2 = org.telegram.messenger.ChatObject.isChannel(r49)
            if (r2 == 0) goto L_0x015d
            boolean r2 = r14.megagroup
            if (r2 == 0) goto L_0x014f
            r2 = 2131626123(0x7f0e088b, float:1.8879473E38)
            java.lang.String r13 = org.telegram.messenger.LocaleController.getString(r10, r2)
            r4.setText(r13)
            goto L_0x0172
        L_0x014f:
            r2 = 2131626123(0x7f0e088b, float:1.8879473E38)
            r13 = 2131626121(0x7f0e0889, float:1.887947E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r13)
            r4.setText(r2)
            goto L_0x0172
        L_0x015d:
            r2 = 2131626123(0x7f0e088b, float:1.8879473E38)
            java.lang.String r13 = org.telegram.messenger.LocaleController.getString(r10, r2)
            r4.setText(r13)
            goto L_0x0172
        L_0x0168:
            r2 = 2131625216(0x7f0e0500, float:1.8877634E38)
            java.lang.String r13 = org.telegram.messenger.LocaleController.getString(r0, r2)
            r4.setText(r13)
        L_0x0172:
            r32 = -1
            r33 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            boolean r2 = org.telegram.messenger.LocaleController.isRTL
            if (r2 == 0) goto L_0x017c
            r2 = 5
            goto L_0x017d
        L_0x017c:
            r2 = 3
        L_0x017d:
            r34 = r2 | 48
            boolean r2 = org.telegram.messenger.LocaleController.isRTL
            r13 = 76
            if (r2 == 0) goto L_0x0188
            r2 = 21
            goto L_0x018a
        L_0x0188:
            r2 = 76
        L_0x018a:
            float r2 = (float) r2
            r36 = 1093664768(0x41300000, float:11.0)
            boolean r35 = org.telegram.messenger.LocaleController.isRTL
            if (r35 == 0) goto L_0x0192
            goto L_0x0194
        L_0x0192:
            r13 = 21
        L_0x0194:
            float r13 = (float) r13
            r38 = 0
            r35 = r2
            r37 = r13
            android.widget.FrameLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createFrame(r32, r33, r34, r35, r36, r37, r38)
            r7.addView(r4, r2)
            r32 = -2
            r33 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            boolean r2 = org.telegram.messenger.LocaleController.isRTL
            if (r2 == 0) goto L_0x01ad
            r20 = 5
            goto L_0x01af
        L_0x01ad:
            r20 = 3
        L_0x01af:
            r34 = r20 | 48
            r35 = 1103101952(0x41CLASSNAME, float:24.0)
            r36 = 1113849856(0x42640000, float:57.0)
            r37 = 1103101952(0x41CLASSNAME, float:24.0)
            r38 = 1091567616(0x41100000, float:9.0)
            android.widget.FrameLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createFrame(r32, r33, r34, r35, r36, r37, r38)
            r7.addView(r8, r2)
            if (r15 == 0) goto L_0x01d7
            boolean r2 = r15.bot
            if (r2 != 0) goto L_0x01d7
            r13 = r3
            long r2 = r15.id
            int r20 = (r2 > r17 ? 1 : (r2 == r17 ? 0 : -1))
            if (r20 == 0) goto L_0x01d8
            org.telegram.messenger.MessagesController r2 = org.telegram.messenger.MessagesController.getInstance(r16)
            boolean r2 = r2.canRevokePmInbox
            if (r2 == 0) goto L_0x01d8
            r2 = 1
            goto L_0x01d9
        L_0x01d7:
            r13 = r3
        L_0x01d8:
            r2 = 0
        L_0x01d9:
            r20 = r2
            if (r15 == 0) goto L_0x01e5
            org.telegram.messenger.MessagesController r2 = org.telegram.messenger.MessagesController.getInstance(r16)
            int r2 = r2.revokeTimePmLimit
            r3 = r2
            goto L_0x01ec
        L_0x01e5:
            org.telegram.messenger.MessagesController r2 = org.telegram.messenger.MessagesController.getInstance(r16)
            int r2 = r2.revokeTimeLimit
            r3 = r2
        L_0x01ec:
            if (r51 != 0) goto L_0x01f9
            if (r15 == 0) goto L_0x01f9
            if (r20 == 0) goto L_0x01f9
            r2 = 2147483647(0x7fffffff, float:NaN)
            if (r3 != r2) goto L_0x01f9
            r2 = 1
            goto L_0x01fa
        L_0x01f9:
            r2 = 0
        L_0x01fa:
            r21 = r2
            r32 = r13
            r2 = 1
            boolean[] r13 = new boolean[r2]
            r2 = 0
            if (r48 != 0) goto L_0x0210
            if (r51 == 0) goto L_0x0208
            if (r46 == 0) goto L_0x020a
        L_0x0208:
            if (r21 == 0) goto L_0x0210
        L_0x020a:
            boolean r33 = org.telegram.messenger.UserObject.isDeleted(r50)
            if (r33 == 0) goto L_0x0227
        L_0x0210:
            if (r52 == 0) goto L_0x021e
            if (r46 != 0) goto L_0x021e
            if (r14 == 0) goto L_0x021e
            r33 = r2
            boolean r2 = r14.creator
            if (r2 == 0) goto L_0x0220
            r2 = 1
            goto L_0x0221
        L_0x021e:
            r33 = r2
        L_0x0220:
            r2 = 0
        L_0x0221:
            r33 = r2
            if (r2 == 0) goto L_0x02f1
            r2 = r33
        L_0x0227:
            r34 = r3
            org.telegram.ui.Cells.CheckBoxCell r3 = new org.telegram.ui.Cells.CheckBoxCell
            r35 = r4
            r4 = 1
            r3.<init>(r11, r4, r12)
            r4 = 0
            r9[r4] = r3
            r3 = r9[r4]
            r36 = r11
            android.graphics.drawable.Drawable r11 = org.telegram.ui.ActionBar.Theme.getSelectorDrawable(r4)
            r3.setBackgroundDrawable(r11)
            java.lang.String r3 = ""
            if (r2 == 0) goto L_0x0276
            boolean r4 = org.telegram.messenger.ChatObject.isChannel(r49)
            if (r4 == 0) goto L_0x0262
            boolean r4 = r14.megagroup
            if (r4 != 0) goto L_0x0262
            r4 = 0
            r11 = r9[r4]
            r4 = 2131625213(0x7f0e04fd, float:1.8877628E38)
            r33 = r2
            java.lang.String r2 = "DeleteChannelForAll"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r4)
            r4 = 0
            r11.setText(r2, r3, r4, r4)
            r37 = r0
            goto L_0x02ae
        L_0x0262:
            r33 = r2
            r4 = 0
            r2 = r9[r4]
            r11 = 2131625230(0x7f0e050e, float:1.8877662E38)
            java.lang.String r12 = "DeleteGroupForAll"
            java.lang.String r11 = org.telegram.messenger.LocaleController.getString(r12, r11)
            r2.setText(r11, r3, r4, r4)
            r37 = r0
            goto L_0x02ae
        L_0x0276:
            r33 = r2
            r4 = 0
            if (r46 == 0) goto L_0x0295
            r2 = r9[r4]
            r12 = 1
            java.lang.Object[] r11 = new java.lang.Object[r12]
            java.lang.String r24 = org.telegram.messenger.UserObject.getFirstName(r50)
            r11[r4] = r24
            java.lang.String r12 = "ClearHistoryOptionAlso"
            r37 = r0
            r0 = 2131624991(0x7f0e041f, float:1.8877177E38)
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r12, r0, r11)
            r2.setText(r0, r3, r4, r4)
            goto L_0x02ae
        L_0x0295:
            r37 = r0
            r0 = r9[r4]
            r2 = 2131625239(0x7f0e0517, float:1.887768E38)
            r11 = 1
            java.lang.Object[] r12 = new java.lang.Object[r11]
            java.lang.String r11 = org.telegram.messenger.UserObject.getFirstName(r50)
            r12[r4] = r11
            java.lang.String r11 = "DeleteMessagesOptionAlso"
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r11, r2, r12)
            r0.setText(r2, r3, r4, r4)
        L_0x02ae:
            r0 = r9[r4]
            boolean r2 = org.telegram.messenger.LocaleController.isRTL
            if (r2 == 0) goto L_0x02b7
            r2 = 1098907648(0x41800000, float:16.0)
            goto L_0x02b9
        L_0x02b7:
            r2 = 1090519040(0x41000000, float:8.0)
        L_0x02b9:
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r2)
            boolean r2 = org.telegram.messenger.LocaleController.isRTL
            if (r2 == 0) goto L_0x02c4
            r2 = 1090519040(0x41000000, float:8.0)
            goto L_0x02c6
        L_0x02c4:
            r2 = 1098907648(0x41800000, float:16.0)
        L_0x02c6:
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            r4 = 0
            r0.setPadding(r3, r4, r2, r4)
            r0 = r9[r4]
            r38 = -1
            r39 = 1111490560(0x42400000, float:48.0)
            r40 = 83
            r41 = 0
            r42 = 0
            r43 = 0
            r44 = 0
            android.widget.FrameLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createFrame(r38, r39, r40, r41, r42, r43, r44)
            r7.addView(r0, r2)
            r0 = 0
            r2 = r9[r0]
            org.telegram.ui.Components.AlertsCreator$$ExternalSyntheticLambda57 r0 = new org.telegram.ui.Components.AlertsCreator$$ExternalSyntheticLambda57
            r0.<init>(r13)
            r2.setOnClickListener(r0)
            goto L_0x02f9
        L_0x02f1:
            r37 = r0
            r34 = r3
            r35 = r4
            r36 = r11
        L_0x02f9:
            r12 = 0
            if (r15 == 0) goto L_0x032f
            boolean r0 = org.telegram.messenger.UserObject.isReplyUser((org.telegram.tgnet.TLRPC.User) r50)
            if (r0 == 0) goto L_0x0311
            r0 = 1
            r6.setSmallSize(r0)
            r0 = 12
            r6.setAvatarType(r0)
            r2 = r31
            r2.setImage((org.telegram.messenger.ImageLocation) r12, (java.lang.String) r12, (android.graphics.drawable.Drawable) r6, (java.lang.Object) r15)
            goto L_0x0337
        L_0x0311:
            r2 = r31
            long r3 = r15.id
            int r0 = (r3 > r17 ? 1 : (r3 == r17 ? 0 : -1))
            if (r0 != 0) goto L_0x0324
            r0 = 1
            r6.setSmallSize(r0)
            r6.setAvatarType(r0)
            r2.setImage((org.telegram.messenger.ImageLocation) r12, (java.lang.String) r12, (android.graphics.drawable.Drawable) r6, (java.lang.Object) r15)
            goto L_0x0337
        L_0x0324:
            r0 = 0
            r6.setSmallSize(r0)
            r6.setInfo((org.telegram.tgnet.TLRPC.User) r15)
            r2.setForUserOrChat(r15, r6)
            goto L_0x0337
        L_0x032f:
            r2 = r31
            r6.setInfo((org.telegram.tgnet.TLRPC.Chat) r14)
            r2.setForUserOrChat(r14, r6)
        L_0x0337:
            if (r48 == 0) goto L_0x0363
            boolean r0 = org.telegram.messenger.UserObject.isUserSelf(r50)
            if (r0 == 0) goto L_0x0351
            r0 = 2131625198(0x7f0e04ee, float:1.8877597E38)
            java.lang.String r3 = "DeleteAllMessagesSavedAlert"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r3, r0)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            r8.setText(r0)
            goto L_0x0506
        L_0x0351:
            r0 = 2131625197(0x7f0e04ed, float:1.8877595E38)
            java.lang.String r3 = "DeleteAllMessagesAlert"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r3, r0)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            r8.setText(r0)
            goto L_0x0506
        L_0x0363:
            if (r46 == 0) goto L_0x0406
            if (r15 == 0) goto L_0x03b9
            if (r51 == 0) goto L_0x0385
            r0 = 2131624357(0x7f0e01a5, float:1.8875891E38)
            r3 = 1
            java.lang.Object[] r3 = new java.lang.Object[r3]
            java.lang.String r4 = org.telegram.messenger.UserObject.getUserName(r50)
            r11 = 0
            r3[r11] = r4
            java.lang.String r4 = "AreYouSureClearHistoryWithSecretUser"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r4, r0, r3)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            r8.setText(r0)
            goto L_0x0506
        L_0x0385:
            long r3 = r15.id
            int r0 = (r3 > r17 ? 1 : (r3 == r17 ? 0 : -1))
            if (r0 != 0) goto L_0x039d
            r0 = 2131624355(0x7f0e01a3, float:1.8875887E38)
            java.lang.String r3 = "AreYouSureClearHistorySavedMessages"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r3, r0)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            r8.setText(r0)
            goto L_0x0506
        L_0x039d:
            r0 = 2131624358(0x7f0e01a6, float:1.8875893E38)
            r3 = 1
            java.lang.Object[] r3 = new java.lang.Object[r3]
            java.lang.String r4 = org.telegram.messenger.UserObject.getUserName(r50)
            r11 = 0
            r3[r11] = r4
            java.lang.String r4 = "AreYouSureClearHistoryWithUser"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r4, r0, r3)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            r8.setText(r0)
            goto L_0x0506
        L_0x03b9:
            boolean r0 = org.telegram.messenger.ChatObject.isChannel(r49)
            if (r0 == 0) goto L_0x03ec
            boolean r0 = r14.megagroup
            if (r0 == 0) goto L_0x03cc
            java.lang.String r0 = r14.username
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            if (r0 == 0) goto L_0x03cc
            goto L_0x03ec
        L_0x03cc:
            boolean r0 = r14.megagroup
            if (r0 == 0) goto L_0x03de
            r0 = 2131624354(0x7f0e01a2, float:1.8875885E38)
            java.lang.String r3 = "AreYouSureClearHistoryGroup"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r3, r0)
            r8.setText(r0)
            goto L_0x0506
        L_0x03de:
            r0 = 2131624352(0x7f0e01a0, float:1.8875881E38)
            java.lang.String r3 = "AreYouSureClearHistoryChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r3, r0)
            r8.setText(r0)
            goto L_0x0506
        L_0x03ec:
            r0 = 2131624356(0x7f0e01a4, float:1.887589E38)
            r3 = 1
            java.lang.Object[] r3 = new java.lang.Object[r3]
            java.lang.String r4 = r14.title
            r11 = 0
            r3[r11] = r4
            java.lang.String r4 = "AreYouSureClearHistoryWithChat"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r4, r0, r3)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            r8.setText(r0)
            goto L_0x0506
        L_0x0406:
            if (r47 == 0) goto L_0x043c
            boolean r0 = org.telegram.messenger.ChatObject.isChannel(r49)
            if (r0 == 0) goto L_0x042e
            boolean r0 = r14.megagroup
            if (r0 == 0) goto L_0x0420
            r0 = 2131624359(0x7f0e01a7, float:1.8875895E38)
            java.lang.String r3 = "AreYouSureDeleteAndExit"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r3, r0)
            r8.setText(r0)
            goto L_0x0506
        L_0x0420:
            r0 = 2131624360(0x7f0e01a8, float:1.8875898E38)
            java.lang.String r3 = "AreYouSureDeleteAndExitChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r3, r0)
            r8.setText(r0)
            goto L_0x0506
        L_0x042e:
            r0 = 2131624359(0x7f0e01a7, float:1.8875895E38)
            java.lang.String r3 = "AreYouSureDeleteAndExit"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r3, r0)
            r8.setText(r0)
            goto L_0x0506
        L_0x043c:
            if (r15 == 0) goto L_0x04b2
            if (r51 == 0) goto L_0x045c
            r0 = 2131624378(0x7f0e01ba, float:1.8875934E38)
            r3 = 1
            java.lang.Object[] r3 = new java.lang.Object[r3]
            java.lang.String r4 = org.telegram.messenger.UserObject.getUserName(r50)
            r11 = 0
            r3[r11] = r4
            java.lang.String r4 = "AreYouSureDeleteThisChatWithSecretUser"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r4, r0, r3)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            r8.setText(r0)
            goto L_0x0506
        L_0x045c:
            long r3 = r15.id
            int r0 = (r3 > r17 ? 1 : (r3 == r17 ? 0 : -1))
            if (r0 != 0) goto L_0x0474
            r0 = 2131624375(0x7f0e01b7, float:1.8875928E38)
            java.lang.String r3 = "AreYouSureDeleteThisChatSavedMessages"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r3, r0)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            r8.setText(r0)
            goto L_0x0506
        L_0x0474:
            boolean r0 = r15.bot
            if (r0 == 0) goto L_0x0497
            boolean r0 = r15.support
            if (r0 != 0) goto L_0x0497
            r0 = 2131624376(0x7f0e01b8, float:1.887593E38)
            r3 = 1
            java.lang.Object[] r3 = new java.lang.Object[r3]
            java.lang.String r4 = org.telegram.messenger.UserObject.getUserName(r50)
            r11 = 0
            r3[r11] = r4
            java.lang.String r4 = "AreYouSureDeleteThisChatWithBot"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r4, r0, r3)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            r8.setText(r0)
            goto L_0x0506
        L_0x0497:
            r0 = 2131624379(0x7f0e01bb, float:1.8875936E38)
            r3 = 1
            java.lang.Object[] r3 = new java.lang.Object[r3]
            java.lang.String r4 = org.telegram.messenger.UserObject.getUserName(r50)
            r11 = 0
            r3[r11] = r4
            java.lang.String r4 = "AreYouSureDeleteThisChatWithUser"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r4, r0, r3)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            r8.setText(r0)
            goto L_0x0506
        L_0x04b2:
            boolean r0 = org.telegram.messenger.ChatObject.isChannel(r49)
            if (r0 == 0) goto L_0x04ee
            boolean r0 = r14.megagroup
            if (r0 == 0) goto L_0x04d5
            r0 = 2131626281(0x7f0e0929, float:1.8879794E38)
            r3 = 1
            java.lang.Object[] r3 = new java.lang.Object[r3]
            java.lang.String r4 = r14.title
            r11 = 0
            r3[r11] = r4
            java.lang.String r4 = "MegaLeaveAlertWithName"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r4, r0, r3)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            r8.setText(r0)
            goto L_0x0506
        L_0x04d5:
            r3 = 1
            r11 = 0
            r0 = 2131624778(0x7f0e034a, float:1.8876745E38)
            java.lang.Object[] r3 = new java.lang.Object[r3]
            java.lang.String r4 = r14.title
            r3[r11] = r4
            java.lang.String r4 = "ChannelLeaveAlertWithName"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r4, r0, r3)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            r8.setText(r0)
            goto L_0x0506
        L_0x04ee:
            r3 = 1
            r11 = 0
            r0 = 2131624361(0x7f0e01a9, float:1.88759E38)
            java.lang.Object[] r3 = new java.lang.Object[r3]
            java.lang.String r4 = r14.title
            r3[r11] = r4
            java.lang.String r4 = "AreYouSureDeleteAndExitName"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r4, r0, r3)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            r8.setText(r0)
        L_0x0506:
            if (r48 == 0) goto L_0x0514
            r0 = 2131625193(0x7f0e04e9, float:1.8877587E38)
            java.lang.String r1 = "DeleteAll"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            r11 = r0
            goto L_0x0580
        L_0x0514:
            if (r46 == 0) goto L_0x052b
            if (r19 == 0) goto L_0x0522
            r0 = 2131624988(0x7f0e041c, float:1.8877171E38)
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            r11 = r0
            goto L_0x0580
        L_0x0522:
            r0 = 2131624987(0x7f0e041b, float:1.887717E38)
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r5, r0)
            r11 = r0
            goto L_0x0580
        L_0x052b:
            if (r47 == 0) goto L_0x0558
            boolean r0 = org.telegram.messenger.ChatObject.isChannel(r49)
            if (r0 == 0) goto L_0x054d
            boolean r0 = r14.megagroup
            if (r0 == 0) goto L_0x0542
            r0 = 2131625236(0x7f0e0514, float:1.8877674E38)
            java.lang.String r1 = "DeleteMega"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            r11 = r0
            goto L_0x0580
        L_0x0542:
            r0 = 2131624762(0x7f0e033a, float:1.8876713E38)
            java.lang.String r1 = "ChannelDelete"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            r11 = r0
            goto L_0x0580
        L_0x054d:
            r0 = 2131625236(0x7f0e0514, float:1.8877674E38)
            java.lang.String r1 = "DeleteMega"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            r11 = r0
            goto L_0x0580
        L_0x0558:
            boolean r0 = org.telegram.messenger.ChatObject.isChannel(r49)
            if (r0 == 0) goto L_0x0576
            boolean r0 = r14.megagroup
            if (r0 == 0) goto L_0x056b
            r0 = 2131626123(0x7f0e088b, float:1.8879473E38)
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r10, r0)
            r11 = r0
            goto L_0x0580
        L_0x056b:
            r1 = r32
            r0 = 2131626121(0x7f0e0889, float:1.887947E38)
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            r11 = r0
            goto L_0x0580
        L_0x0576:
            r1 = r37
            r0 = 2131625216(0x7f0e0500, float:1.8877634E38)
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            r11 = r0
        L_0x0580:
            org.telegram.ui.Components.AlertsCreator$$ExternalSyntheticLambda29 r10 = new org.telegram.ui.Components.AlertsCreator$$ExternalSyntheticLambda29
            r0 = r10
            r1 = r19
            r22 = r2
            r2 = r48
            r23 = r34
            r3 = r51
            r24 = r35
            r4 = r50
            r5 = r45
            r25 = r6
            r6 = r46
            r26 = r7
            r7 = r47
            r27 = r8
            r8 = r49
            r28 = r9
            r9 = r52
            r15 = r10
            r14 = r29
            r10 = r53
            r30 = r14
            r29 = r36
            r14 = r11
            r11 = r54
            r12 = r13
            r0.<init>(r1, r2, r3, r4, r5, r6, r7, r8, r9, r10, r11, r12)
            r0 = r30
            r0.setPositiveButton(r14, r15)
            r1 = 2131624692(0x7f0e02f4, float:1.887657E38)
            java.lang.String r2 = "Cancel"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r2 = 0
            r0.setNegativeButton(r1, r2)
            org.telegram.ui.ActionBar.AlertDialog r1 = r0.create()
            r2 = r45
            r2.showDialog(r1)
            r3 = -1
            android.view.View r3 = r1.getButton(r3)
            android.widget.TextView r3 = (android.widget.TextView) r3
            if (r3 == 0) goto L_0x05e0
            java.lang.String r4 = "dialogTextRed2"
            int r4 = org.telegram.ui.ActionBar.Theme.getColor(r4)
            r3.setTextColor(r4)
        L_0x05e0:
            return
        L_0x05e1:
            r2 = r13
        L_0x05e2:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.AlertsCreator.createClearOrDeleteDialogAlert(org.telegram.ui.ActionBar.BaseFragment, boolean, boolean, boolean, org.telegram.tgnet.TLRPC$Chat, org.telegram.tgnet.TLRPC$User, boolean, boolean, org.telegram.messenger.MessagesStorage$BooleanCallback, org.telegram.ui.ActionBar.Theme$ResourcesProvider):void");
    }

    static /* synthetic */ void lambda$createClearOrDeleteDialogAlert$16(boolean[] deleteForAll, View v) {
        deleteForAll[0] = !deleteForAll[0];
        ((CheckBoxCell) v).setChecked(deleteForAll[0], true);
    }

    static /* synthetic */ void lambda$createClearOrDeleteDialogAlert$18(boolean clearingCache, boolean second, boolean secret, TLRPC.User user, BaseFragment fragment, boolean clear, boolean admin, TLRPC.Chat chat, boolean checkDeleteForAll, MessagesStorage.BooleanCallback onProcessRunnable, Theme.ResourcesProvider resourcesProvider, boolean[] deleteForAll, DialogInterface dialogInterface, int i) {
        TLRPC.User user2 = user;
        MessagesStorage.BooleanCallback booleanCallback = onProcessRunnable;
        boolean z = false;
        if (!clearingCache && !second && !secret) {
            if (UserObject.isUserSelf(user)) {
                createClearOrDeleteDialogAlert(fragment, clear, admin, true, chat, user, false, checkDeleteForAll, onProcessRunnable, resourcesProvider);
                return;
            } else if (user2 != null && deleteForAll[0]) {
                MessagesStorage.getInstance(fragment.getCurrentAccount()).getMessagesCount(user2.id, new AlertsCreator$$ExternalSyntheticLambda80(fragment, clear, admin, chat, user, checkDeleteForAll, onProcessRunnable, resourcesProvider, deleteForAll));
                return;
            }
        }
        if (booleanCallback != null) {
            if (second || deleteForAll[0]) {
                z = true;
            }
            booleanCallback.run(z);
        }
    }

    static /* synthetic */ void lambda$createClearOrDeleteDialogAlert$17(BaseFragment fragment, boolean clear, boolean admin, TLRPC.Chat chat, TLRPC.User user, boolean checkDeleteForAll, MessagesStorage.BooleanCallback onProcessRunnable, Theme.ResourcesProvider resourcesProvider, boolean[] deleteForAll, int count) {
        MessagesStorage.BooleanCallback booleanCallback = onProcessRunnable;
        if (count >= 50) {
            createClearOrDeleteDialogAlert(fragment, clear, admin, true, chat, user, false, checkDeleteForAll, onProcessRunnable, resourcesProvider);
        } else if (booleanCallback != null) {
            booleanCallback.run(deleteForAll[0]);
        }
    }

    public static void createClearDaysDialogAlert(BaseFragment fragment, int days, TLRPC.User user, MessagesStorage.BooleanCallback onProcessRunnable, Theme.ResourcesProvider resourcesProvider) {
        BaseFragment baseFragment = fragment;
        TLRPC.User user2 = user;
        Theme.ResourcesProvider resourcesProvider2 = resourcesProvider;
        if (baseFragment == null || fragment.getParentActivity() == null) {
            MessagesStorage.BooleanCallback booleanCallback = onProcessRunnable;
        } else if (user2 == null) {
            MessagesStorage.BooleanCallback booleanCallback2 = onProcessRunnable;
        } else {
            int account = fragment.getCurrentAccount();
            Context context = fragment.getParentActivity();
            AlertDialog.Builder builder = new AlertDialog.Builder(context, resourcesProvider2);
            long selfUserId = UserConfig.getInstance(account).getClientUserId();
            final CheckBoxCell[] cell = new CheckBoxCell[1];
            TextView messageTextView = new TextView(context);
            messageTextView.setTextColor(Theme.getColor("dialogTextBlack"));
            messageTextView.setTextSize(1, 16.0f);
            messageTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
            FrameLayout frameLayout = new FrameLayout(context) {
                /* access modifiers changed from: protected */
                public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                    super.onMeasure(widthMeasureSpec, heightMeasureSpec);
                    if (cell[0] != null) {
                        setMeasuredDimension(getMeasuredWidth(), getMeasuredHeight() + cell[0].getMeasuredHeight());
                    }
                }
            };
            builder.setView(frameLayout);
            TextView textView = new TextView(context);
            textView.setTextColor(Theme.getColor("actionBarDefaultSubmenuItem"));
            textView.setTextSize(1, 20.0f);
            textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            textView.setLines(1);
            textView.setMaxLines(1);
            textView.setSingleLine(true);
            textView.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
            textView.setEllipsize(TextUtils.TruncateAt.END);
            textView.setText(LocaleController.formatPluralString("DeleteDays", days));
            frameLayout.addView(textView, LayoutHelper.createFrame(-1, -2.0f, (LocaleController.isRTL ? 5 : 3) | 48, 24.0f, 11.0f, 24.0f, 0.0f));
            frameLayout.addView(messageTextView, LayoutHelper.createFrame(-2, -2.0f, (LocaleController.isRTL ? 5 : 3) | 48, 24.0f, 48.0f, 24.0f, 18.0f));
            messageTextView.setText(LocaleController.getString("DeleteHistoryByDaysMessage", NUM));
            boolean[] deleteForAll = {false};
            if (user2.id != selfUserId) {
                cell[0] = new CheckBoxCell(context, 1, resourcesProvider2);
                cell[0].setBackgroundDrawable(Theme.getSelectorDrawable(false));
                cell[0].setText(LocaleController.formatString("DeleteMessagesOptionAlso", NUM, UserObject.getFirstName(user)), "", false, false);
                cell[0].setPadding(LocaleController.isRTL ? AndroidUtilities.dp(16.0f) : AndroidUtilities.dp(8.0f), 0, LocaleController.isRTL ? AndroidUtilities.dp(8.0f) : AndroidUtilities.dp(16.0f), 0);
                frameLayout.addView(cell[0], LayoutHelper.createFrame(-1, 48.0f, 83, 0.0f, 0.0f, 0.0f, 0.0f));
                cell[0].setChecked(false, false);
                cell[0].setOnClickListener(new AlertsCreator$$ExternalSyntheticLambda56(deleteForAll));
            }
            builder.setPositiveButton(LocaleController.getString("Delete", NUM), new AlertsCreator$$ExternalSyntheticLambda12(onProcessRunnable, deleteForAll));
            builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
            AlertDialog alertDialog = builder.create();
            baseFragment.showDialog(alertDialog);
            TextView button = (TextView) alertDialog.getButton(-1);
            if (button != null) {
                button.setTextColor(Theme.getColor("dialogTextRed2"));
            }
        }
    }

    static /* synthetic */ void lambda$createClearDaysDialogAlert$19(boolean[] deleteForAll, View v) {
        deleteForAll[0] = !deleteForAll[0];
        ((CheckBoxCell) v).setChecked(deleteForAll[0], true);
    }

    public static void createCallDialogAlert(BaseFragment fragment, TLRPC.User user, boolean videoCall) {
        String message;
        String title;
        BaseFragment baseFragment = fragment;
        TLRPC.User user2 = user;
        boolean z = videoCall;
        if (baseFragment != null && fragment.getParentActivity() != null && user2 != null && !UserObject.isDeleted(user) && UserConfig.getInstance(fragment.getCurrentAccount()).getClientUserId() != user2.id) {
            int currentAccount = fragment.getCurrentAccount();
            Context context = fragment.getParentActivity();
            FrameLayout frameLayout = new FrameLayout(context);
            if (z) {
                title = LocaleController.getString("VideoCallAlertTitle", NUM);
                message = LocaleController.formatString("VideoCallAlert", NUM, UserObject.getUserName(user));
            } else {
                title = LocaleController.getString("CallAlertTitle", NUM);
                message = LocaleController.formatString("CallAlert", NUM, UserObject.getUserName(user));
            }
            TextView messageTextView = new TextView(context);
            messageTextView.setTextColor(Theme.getColor("dialogTextBlack"));
            messageTextView.setTextSize(1, 16.0f);
            messageTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
            messageTextView.setText(AndroidUtilities.replaceTags(message));
            AvatarDrawable avatarDrawable = new AvatarDrawable();
            avatarDrawable.setTextSize(AndroidUtilities.dp(12.0f));
            avatarDrawable.setSmallSize(false);
            avatarDrawable.setInfo(user2);
            BackupImageView imageView = new BackupImageView(context);
            imageView.setRoundRadius(AndroidUtilities.dp(20.0f));
            imageView.setForUserOrChat(user2, avatarDrawable);
            frameLayout.addView(imageView, LayoutHelper.createFrame(40, 40.0f, (LocaleController.isRTL ? 5 : 3) | 48, 22.0f, 5.0f, 22.0f, 0.0f));
            TextView textView = new TextView(context);
            textView.setTextColor(Theme.getColor("actionBarDefaultSubmenuItem"));
            textView.setTextSize(1, 20.0f);
            textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            textView.setLines(1);
            textView.setMaxLines(1);
            textView.setSingleLine(true);
            textView.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
            textView.setEllipsize(TextUtils.TruncateAt.END);
            textView.setText(title);
            int i = (LocaleController.isRTL ? 5 : 3) | 48;
            int i2 = 21;
            float f = (float) (LocaleController.isRTL ? 21 : 76);
            if (LocaleController.isRTL) {
                i2 = 76;
            }
            frameLayout.addView(textView, LayoutHelper.createFrame(-1, -2.0f, i, f, 11.0f, (float) i2, 0.0f));
            frameLayout.addView(messageTextView, LayoutHelper.createFrame(-2, -2.0f, (LocaleController.isRTL ? 5 : 3) | 48, 24.0f, 57.0f, 24.0f, 9.0f));
            baseFragment.showDialog(new AlertDialog.Builder(context).setView(frameLayout).setPositiveButton(LocaleController.getString("Call", NUM), new AlertsCreator$$ExternalSyntheticLambda23(baseFragment, user2, z)).setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null).create());
        }
    }

    static /* synthetic */ void lambda$createCallDialogAlert$21(BaseFragment fragment, TLRPC.User user, boolean videoCall, DialogInterface dialogInterface, int i) {
        TLRPC.UserFull userFull = fragment.getMessagesController().getUserFull(user.id);
        VoIPHelper.startCall(user, videoCall, userFull != null && userFull.video_calls_available, fragment.getParentActivity(), userFull, fragment.getAccountInstance());
    }

    public static void createChangeBioAlert(String currentBio, long peerId, Context context, int currentAccount) {
        String str;
        int i;
        String str2;
        long j = peerId;
        final Context context2 = context;
        AlertDialog.Builder builder = new AlertDialog.Builder(context2);
        int i2 = NUM;
        String str3 = "UserBio";
        builder.setTitle(j > 0 ? LocaleController.getString(str3, NUM) : LocaleController.getString("DescriptionPlaceholder", NUM));
        if (j > 0) {
            i = NUM;
            str = "VoipGroupBioEditAlertText";
        } else {
            i = NUM;
            str = "DescriptionInfo";
        }
        builder.setMessage(LocaleController.getString(str, i));
        FrameLayout dialogView = new FrameLayout(context2);
        dialogView.setClipChildren(false);
        if (j >= 0) {
            str2 = "DescriptionPlaceholder";
        } else if (MessagesController.getInstance(currentAccount).getChatFull(-j) == null) {
            str2 = "DescriptionPlaceholder";
            MessagesController.getInstance(currentAccount).loadFullChat(-j, ConnectionsManager.generateClassGuid(), true);
        } else {
            str2 = "DescriptionPlaceholder";
        }
        final NumberTextView checkTextView = new NumberTextView(context2);
        EditText editTextView = new EditText(context2);
        editTextView.setTextColor(Theme.getColor("voipgroup_actionBarItems"));
        if (j <= 0) {
            str3 = str2;
            i2 = NUM;
        }
        editTextView.setHint(LocaleController.getString(str3, i2));
        editTextView.setTextSize(1, 16.0f);
        editTextView.setBackground(Theme.createEditTextDrawable(context2, true));
        editTextView.setMaxLines(4);
        editTextView.setRawInputType(147457);
        editTextView.setImeOptions(6);
        InputFilter[] inputFilters = new InputFilter[1];
        final int maxSymbolsCount = j > 0 ? 70 : 255;
        inputFilters[0] = new CodepointsLengthInputFilter(maxSymbolsCount) {
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                CharSequence result = super.filter(source, start, end, dest, dstart, dend);
                if (!(result == null || source == null || result.length() == source.length())) {
                    Vibrator v = (Vibrator) context2.getSystemService("vibrator");
                    if (v != null) {
                        v.vibrate(200);
                    }
                    AndroidUtilities.shakeView(checkTextView, 2.0f, 0);
                }
                return result;
            }
        };
        editTextView.setFilters(inputFilters);
        checkTextView.setCenterAlign(true);
        checkTextView.setTextSize(15);
        checkTextView.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText4"));
        checkTextView.setImportantForAccessibility(2);
        dialogView.addView(checkTextView, LayoutHelper.createFrame(20, 20.0f, LocaleController.isRTL ? 3 : 5, 0.0f, 14.0f, 21.0f, 0.0f));
        editTextView.setPadding(LocaleController.isRTL ? AndroidUtilities.dp(24.0f) : 0, AndroidUtilities.dp(8.0f), LocaleController.isRTL ? 0 : AndroidUtilities.dp(24.0f), AndroidUtilities.dp(8.0f));
        editTextView.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            public void afterTextChanged(Editable s) {
                boolean z = false;
                int count = maxSymbolsCount - Character.codePointCount(s, 0, s.length());
                if (count < 30) {
                    NumberTextView numberTextView = checkTextView;
                    if (numberTextView.getVisibility() == 0) {
                        z = true;
                    }
                    numberTextView.setNumber(count, z);
                    AndroidUtilities.updateViewVisibilityAnimated(checkTextView, true);
                    return;
                }
                AndroidUtilities.updateViewVisibilityAnimated(checkTextView, false);
            }
        });
        AndroidUtilities.updateViewVisibilityAnimated(checkTextView, false, 0.0f, false);
        editTextView.setText(currentBio);
        editTextView.setSelection(editTextView.getText().toString().length());
        builder.setView(dialogView);
        DialogInterface.OnClickListener onDoneListener = new AlertsCreator$$ExternalSyntheticLambda28(j, currentAccount, editTextView);
        builder.setPositiveButton(LocaleController.getString("Save", NUM), onDoneListener);
        builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
        builder.setOnPreDismissListener(new AlertsCreator$$ExternalSyntheticLambda33(editTextView));
        dialogView.addView(editTextView, LayoutHelper.createFrame(-1, -2.0f, 0, 23.0f, 12.0f, 23.0f, 21.0f));
        editTextView.requestFocus();
        AndroidUtilities.showKeyboard(editTextView);
        AlertDialog dialog = builder.create();
        editTextView.setOnEditorActionListener(new AlertsCreator$$ExternalSyntheticLambda69(j, dialog, onDoneListener));
        dialog.setBackgroundColor(Theme.getColor("voipgroup_dialogBackground"));
        dialog.show();
        dialog.setTextColor(Theme.getColor("voipgroup_actionBarItems"));
    }

    static /* synthetic */ void lambda$createChangeBioAlert$23(long peerId, int currentAccount, EditText editTextView, DialogInterface dialogInterface, int i) {
        long j = peerId;
        if (j > 0) {
            TLRPC.UserFull userFull = MessagesController.getInstance(currentAccount).getUserFull(UserConfig.getInstance(currentAccount).getClientUserId());
            String newName = editTextView.getText().toString().replace("\n", " ").replaceAll(" +", " ").trim();
            if (userFull != null) {
                String currentName = userFull.about;
                if (currentName == null) {
                    currentName = "";
                }
                if (currentName.equals(newName)) {
                    AndroidUtilities.hideKeyboard(editTextView);
                    dialogInterface.dismiss();
                    return;
                }
                userFull.about = newName;
                NotificationCenter.getInstance(currentAccount).postNotificationName(NotificationCenter.userInfoDidLoad, Long.valueOf(peerId), userFull);
            }
            TLRPC.TL_account_updateProfile req = new TLRPC.TL_account_updateProfile();
            req.about = newName;
            req.flags = 4 | req.flags;
            NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.showBulletin, 2, Long.valueOf(peerId));
            ConnectionsManager.getInstance(currentAccount).sendRequest(req, AlertsCreator$$ExternalSyntheticLambda85.INSTANCE, 2);
        } else {
            TLRPC.ChatFull chatFull = MessagesController.getInstance(currentAccount).getChatFull(-j);
            String newAbout = editTextView.getText().toString();
            if (chatFull != null) {
                String currentName2 = chatFull.about;
                if (currentName2 == null) {
                    currentName2 = "";
                }
                if (currentName2.equals(newAbout)) {
                    AndroidUtilities.hideKeyboard(editTextView);
                    dialogInterface.dismiss();
                    return;
                }
                chatFull.about = newAbout;
                NotificationCenter.getInstance(currentAccount).postNotificationName(NotificationCenter.chatInfoDidLoad, chatFull, 0, false, false);
            }
            NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.showBulletin, 2, Long.valueOf(peerId));
            MessagesController.getInstance(currentAccount).updateChatAbout(-j, newAbout, chatFull);
        }
        dialogInterface.dismiss();
    }

    static /* synthetic */ void lambda$createChangeBioAlert$22(TLObject response, TLRPC.TL_error error) {
    }

    static /* synthetic */ boolean lambda$createChangeBioAlert$25(long peerId, AlertDialog dialog, DialogInterface.OnClickListener onDoneListener, TextView textView, int i, KeyEvent keyEvent) {
        if ((i != 6 && (peerId <= 0 || keyEvent.getKeyCode() != 66)) || !dialog.isShowing()) {
            return false;
        }
        onDoneListener.onClick(dialog, 0);
        return true;
    }

    public static void createChangeNameAlert(long peerId, Context context, int currentAccount) {
        String currentName;
        String currentLastName;
        String str;
        int i;
        EditText lastNameEditTextView;
        long j = peerId;
        Context context2 = context;
        if (DialogObject.isUserDialog(peerId)) {
            TLRPC.User user = MessagesController.getInstance(currentAccount).getUser(Long.valueOf(peerId));
            String currentName2 = user.first_name;
            currentLastName = user.last_name;
            currentName = currentName2;
        } else {
            currentLastName = null;
            currentName = MessagesController.getInstance(currentAccount).getChat(Long.valueOf(-j)).title;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(context2);
        if (j > 0) {
            i = NUM;
            str = "VoipEditName";
        } else {
            i = NUM;
            str = "VoipEditTitle";
        }
        builder.setTitle(LocaleController.getString(str, i));
        LinearLayout dialogView = new LinearLayout(context2);
        dialogView.setOrientation(1);
        EditText firstNameEditTextView = new EditText(context2);
        firstNameEditTextView.setTextColor(Theme.getColor("voipgroup_actionBarItems"));
        firstNameEditTextView.setTextSize(1, 16.0f);
        firstNameEditTextView.setMaxLines(1);
        firstNameEditTextView.setLines(1);
        firstNameEditTextView.setSingleLine(true);
        firstNameEditTextView.setGravity(LocaleController.isRTL ? 5 : 3);
        firstNameEditTextView.setInputType(49152);
        firstNameEditTextView.setImeOptions(j > 0 ? 5 : 6);
        firstNameEditTextView.setHint(j > 0 ? LocaleController.getString("FirstName", NUM) : LocaleController.getString("VoipEditTitleHint", NUM));
        firstNameEditTextView.setBackground(Theme.createEditTextDrawable(context2, true));
        firstNameEditTextView.setPadding(0, AndroidUtilities.dp(8.0f), 0, AndroidUtilities.dp(8.0f));
        firstNameEditTextView.requestFocus();
        if (j > 0) {
            EditText lastNameEditTextView2 = new EditText(context2);
            lastNameEditTextView2.setTextColor(Theme.getColor("voipgroup_actionBarItems"));
            lastNameEditTextView2.setTextSize(1, 16.0f);
            lastNameEditTextView2.setMaxLines(1);
            lastNameEditTextView2.setLines(1);
            lastNameEditTextView2.setSingleLine(true);
            lastNameEditTextView2.setGravity(LocaleController.isRTL ? 5 : 3);
            lastNameEditTextView2.setInputType(49152);
            lastNameEditTextView2.setImeOptions(6);
            lastNameEditTextView2.setHint(LocaleController.getString("LastName", NUM));
            lastNameEditTextView2.setBackground(Theme.createEditTextDrawable(context2, true));
            lastNameEditTextView2.setPadding(0, AndroidUtilities.dp(8.0f), 0, AndroidUtilities.dp(8.0f));
            lastNameEditTextView = lastNameEditTextView2;
        } else {
            lastNameEditTextView = null;
        }
        AndroidUtilities.showKeyboard(firstNameEditTextView);
        dialogView.addView(firstNameEditTextView, LayoutHelper.createLinear(-1, -2, 0, 23, 12, 23, 21));
        if (lastNameEditTextView != null) {
            dialogView.addView(lastNameEditTextView, LayoutHelper.createLinear(-1, -2, 0, 23, 12, 23, 21));
        }
        firstNameEditTextView.setText(currentName);
        firstNameEditTextView.setSelection(firstNameEditTextView.getText().toString().length());
        if (lastNameEditTextView != null) {
            lastNameEditTextView.setText(currentLastName);
            lastNameEditTextView.setSelection(lastNameEditTextView.getText().toString().length());
        }
        builder.setView(dialogView);
        EditText finalLastNameEditTextView = lastNameEditTextView;
        DialogInterface.OnClickListener onDoneListener = new AlertsCreator$$ExternalSyntheticLambda8(firstNameEditTextView, peerId, currentAccount, finalLastNameEditTextView);
        builder.setPositiveButton(LocaleController.getString("Save", NUM), onDoneListener);
        builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
        builder.setOnPreDismissListener(new AlertsCreator$$ExternalSyntheticLambda34(firstNameEditTextView, finalLastNameEditTextView));
        AlertDialog dialog = builder.create();
        dialog.setBackgroundColor(Theme.getColor("voipgroup_dialogBackground"));
        dialog.show();
        dialog.setTextColor(Theme.getColor("voipgroup_actionBarItems"));
        TextView.OnEditorActionListener actionListener = new AlertsCreator$$ExternalSyntheticLambda70(dialog, onDoneListener);
        if (lastNameEditTextView != null) {
            lastNameEditTextView.setOnEditorActionListener(actionListener);
        } else {
            firstNameEditTextView.setOnEditorActionListener(actionListener);
        }
    }

    static /* synthetic */ void lambda$createChangeNameAlert$27(EditText firstNameEditTextView, long peerId, int currentAccount, EditText finalLastNameEditTextView, DialogInterface dialogInterface, int i) {
        long j = peerId;
        if (firstNameEditTextView.getText() != null) {
            if (j > 0) {
                TLRPC.User currentUser = MessagesController.getInstance(currentAccount).getUser(Long.valueOf(peerId));
                String newFirst = firstNameEditTextView.getText().toString();
                String newLast = finalLastNameEditTextView.getText().toString();
                String oldFirst = currentUser.first_name;
                String oldLast = currentUser.last_name;
                if (oldFirst == null) {
                    oldFirst = "";
                }
                if (oldLast == null) {
                    oldLast = "";
                }
                if (!oldFirst.equals(newFirst) || !oldLast.equals(newLast)) {
                    TLRPC.TL_account_updateProfile req = new TLRPC.TL_account_updateProfile();
                    req.flags = 3;
                    req.first_name = newFirst;
                    currentUser.first_name = newFirst;
                    req.last_name = newLast;
                    currentUser.last_name = newLast;
                    TLRPC.User user = MessagesController.getInstance(currentAccount).getUser(Long.valueOf(UserConfig.getInstance(currentAccount).getClientUserId()));
                    if (user != null) {
                        user.first_name = req.first_name;
                        user.last_name = req.last_name;
                    }
                    UserConfig.getInstance(currentAccount).saveConfig(true);
                    NotificationCenter.getInstance(currentAccount).postNotificationName(NotificationCenter.mainUserInfoChanged, new Object[0]);
                    NotificationCenter.getInstance(currentAccount).postNotificationName(NotificationCenter.updateInterfaces, Integer.valueOf(MessagesController.UPDATE_MASK_NAME));
                    ConnectionsManager.getInstance(currentAccount).sendRequest(req, AlertsCreator$$ExternalSyntheticLambda86.INSTANCE);
                    NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.showBulletin, 3, Long.valueOf(peerId));
                } else {
                    dialogInterface.dismiss();
                    return;
                }
            } else {
                TLRPC.Chat chat = MessagesController.getInstance(currentAccount).getChat(Long.valueOf(-j));
                String newFirst2 = firstNameEditTextView.getText().toString();
                if (chat.title == null || !chat.title.equals(newFirst2)) {
                    chat.title = newFirst2;
                    NotificationCenter.getInstance(currentAccount).postNotificationName(NotificationCenter.updateInterfaces, Integer.valueOf(MessagesController.UPDATE_MASK_CHAT_NAME));
                    MessagesController.getInstance(currentAccount).changeChatTitle(-j, newFirst2);
                    NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.showBulletin, 3, Long.valueOf(peerId));
                } else {
                    dialogInterface.dismiss();
                    return;
                }
            }
            dialogInterface.dismiss();
        }
    }

    static /* synthetic */ void lambda$createChangeNameAlert$26(TLObject response, TLRPC.TL_error error) {
    }

    static /* synthetic */ void lambda$createChangeNameAlert$28(EditText firstNameEditTextView, EditText finalLastNameEditTextView, DialogInterface dialogInterface) {
        AndroidUtilities.hideKeyboard(firstNameEditTextView);
        AndroidUtilities.hideKeyboard(finalLastNameEditTextView);
    }

    static /* synthetic */ boolean lambda$createChangeNameAlert$29(AlertDialog dialog, DialogInterface.OnClickListener onDoneListener, TextView textView, int i, KeyEvent keyEvent) {
        if ((i != 6 && keyEvent.getKeyCode() != 66) || !dialog.isShowing()) {
            return false;
        }
        onDoneListener.onClick(dialog, 0);
        return true;
    }

    public static void showChatWithAdmin(BaseFragment fragment, TLRPC.User user, String chatWithAdmin, boolean isChannel, int chatWithAdminDate) {
        String str;
        int i;
        if (fragment.getParentActivity() != null) {
            BottomSheet.Builder builder = new BottomSheet.Builder(fragment.getParentActivity());
            if (isChannel) {
                i = NUM;
                str = "ChatWithAdminChannelTitle";
            } else {
                i = NUM;
                str = "ChatWithAdminGroupTitle";
            }
            builder.setTitle(LocaleController.getString(str, i), true);
            LinearLayout linearLayout = new LinearLayout(fragment.getParentActivity());
            linearLayout.setOrientation(1);
            TextView messageTextView = new TextView(fragment.getParentActivity());
            linearLayout.addView(messageTextView, LayoutHelper.createLinear(-1, -1, 0, 24, 16, 24, 24));
            messageTextView.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
            messageTextView.setTextSize(1, 16.0f);
            messageTextView.setText(AndroidUtilities.replaceTags(LocaleController.formatString("ChatWithAdminMessage", NUM, chatWithAdmin, LocaleController.formatDateAudio((long) chatWithAdminDate, false))));
            TextView buttonTextView = new TextView(fragment.getParentActivity());
            buttonTextView.setPadding(AndroidUtilities.dp(34.0f), 0, AndroidUtilities.dp(34.0f), 0);
            buttonTextView.setGravity(17);
            buttonTextView.setTextSize(1, 14.0f);
            buttonTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            buttonTextView.setText(LocaleController.getString("IUnderstand", NUM));
            buttonTextView.setTextColor(Theme.getColor("featuredStickers_buttonText"));
            buttonTextView.setBackgroundDrawable(Theme.createSimpleSelectorRoundRectDrawable(AndroidUtilities.dp(6.0f), Theme.getColor("featuredStickers_addButton"), Theme.getColor("featuredStickers_addButtonPressed")));
            linearLayout.addView(buttonTextView, LayoutHelper.createLinear(-1, 48, 0, 24, 15, 16, 24));
            builder.setCustomView(linearLayout);
            buttonTextView.setOnClickListener(new AlertsCreator$$ExternalSyntheticLambda45(builder.show()));
        }
    }

    public static void createBlockDialogAlert(BaseFragment fragment, int count, boolean reportSpam, TLRPC.User user, BlockDialogCallback onProcessRunnable) {
        String actionText;
        String str;
        int i;
        BaseFragment baseFragment = fragment;
        int i2 = count;
        TLRPC.User user2 = user;
        if (baseFragment == null || fragment.getParentActivity() == null) {
            BlockDialogCallback blockDialogCallback = onProcessRunnable;
        } else if (i2 == 1 && user2 == null) {
            BlockDialogCallback blockDialogCallback2 = onProcessRunnable;
        } else {
            Context context = fragment.getParentActivity();
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            CheckBoxCell[] cell = new CheckBoxCell[2];
            LinearLayout linearLayout = new LinearLayout(context);
            linearLayout.setOrientation(1);
            builder.setView(linearLayout);
            if (i2 == 1) {
                String name = ContactsController.formatName(user2.first_name, user2.last_name);
                builder.setTitle(LocaleController.formatString("BlockUserTitle", NUM, name));
                actionText = LocaleController.getString("BlockUser", NUM);
                builder.setMessage(AndroidUtilities.replaceTags(LocaleController.formatString("BlockUserMessage", NUM, name)));
            } else {
                builder.setTitle(LocaleController.formatString("BlockUserTitle", NUM, LocaleController.formatPluralString("UsersCountTitle", i2)));
                actionText = LocaleController.getString("BlockUsers", NUM);
                builder.setMessage(AndroidUtilities.replaceTags(LocaleController.formatString("BlockUsersMessage", NUM, LocaleController.formatPluralString("UsersCount", i2))));
            }
            boolean[] checks = {true, true};
            int a = 0;
            while (a < cell.length) {
                if (a != 0 || reportSpam) {
                    int num = a;
                    cell[a] = new CheckBoxCell(context, 1);
                    cell[a].setBackgroundDrawable(Theme.getSelectorDrawable(false));
                    if (a == 0) {
                        cell[a].setText(LocaleController.getString("ReportSpamTitle", NUM), "", true, false);
                    } else {
                        CheckBoxCell checkBoxCell = cell[a];
                        if (i2 == 1) {
                            i = NUM;
                            str = "DeleteThisChatBothSides";
                        } else {
                            i = NUM;
                            str = "DeleteTheseChatsBothSides";
                        }
                        checkBoxCell.setText(LocaleController.getString(str, i), "", true, false);
                    }
                    cell[a].setPadding(LocaleController.isRTL ? AndroidUtilities.dp(16.0f) : AndroidUtilities.dp(8.0f), 0, LocaleController.isRTL ? AndroidUtilities.dp(8.0f) : AndroidUtilities.dp(16.0f), 0);
                    linearLayout.addView(cell[a], LayoutHelper.createLinear(-1, 48));
                    cell[a].setOnClickListener(new AlertsCreator$$ExternalSyntheticLambda62(checks, num));
                }
                a++;
                TLRPC.User user3 = user;
            }
            builder.setPositiveButton(actionText, new AlertsCreator$$ExternalSyntheticLambda24(onProcessRunnable, checks));
            builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
            AlertDialog alertDialog = builder.create();
            baseFragment.showDialog(alertDialog);
            TextView button = (TextView) alertDialog.getButton(-1);
            if (button != null) {
                button.setTextColor(Theme.getColor("dialogTextRed2"));
            }
        }
    }

    static /* synthetic */ void lambda$createBlockDialogAlert$31(boolean[] checks, int num, View v) {
        checks[num] = !checks[num];
        ((CheckBoxCell) v).setChecked(checks[num], true);
    }

    public static AlertDialog.Builder createDatePickerDialog(Context context, int minYear, int maxYear, int currentYearDiff, int selectedDay, int selectedMonth, int selectedYear, String title, boolean checkMinDate, DatePickerDelegate datePickerDelegate) {
        Context context2 = context;
        int i = selectedDay;
        boolean z = checkMinDate;
        if (context2 == null) {
            return null;
        }
        LinearLayout linearLayout = new LinearLayout(context2);
        linearLayout.setOrientation(0);
        linearLayout.setWeightSum(1.0f);
        NumberPicker monthPicker = new NumberPicker(context2);
        NumberPicker dayPicker = new NumberPicker(context2);
        NumberPicker yearPicker = new NumberPicker(context2);
        linearLayout.addView(dayPicker, LayoutHelper.createLinear(0, -2, 0.3f));
        dayPicker.setOnScrollListener(new AlertsCreator$$ExternalSyntheticLambda103(z, dayPicker, monthPicker, yearPicker));
        monthPicker.setMinValue(0);
        monthPicker.setMaxValue(11);
        linearLayout.addView(monthPicker, LayoutHelper.createLinear(0, -2, 0.3f));
        monthPicker.setFormatter(AlertsCreator$$ExternalSyntheticLambda97.INSTANCE);
        monthPicker.setOnValueChangedListener(new AlertsCreator$$ExternalSyntheticLambda5(dayPicker, monthPicker, yearPicker));
        monthPicker.setOnScrollListener(new AlertsCreator$$ExternalSyntheticLambda104(z, dayPicker, monthPicker, yearPicker));
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        int currentYear = calendar.get(1);
        yearPicker.setMinValue(currentYear + minYear);
        yearPicker.setMaxValue(currentYear + maxYear);
        yearPicker.setValue(currentYear + currentYearDiff);
        linearLayout.addView(yearPicker, LayoutHelper.createLinear(0, -2, 0.4f));
        yearPicker.setOnValueChangedListener(new AlertsCreator$$ExternalSyntheticLambda6(dayPicker, monthPicker, yearPicker));
        yearPicker.setOnScrollListener(new AlertsCreator$$ExternalSyntheticLambda1(z, dayPicker, monthPicker, yearPicker));
        updateDayPicker(dayPicker, monthPicker, yearPicker);
        if (z) {
            checkPickerDate(dayPicker, monthPicker, yearPicker);
        }
        if (i != -1) {
            dayPicker.setValue(i);
            monthPicker.setValue(selectedMonth);
            yearPicker.setValue(selectedYear);
        } else {
            int i2 = selectedMonth;
            int i3 = selectedYear;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(context2);
        builder.setTitle(title);
        builder.setView(linearLayout);
        AlertsCreator$$ExternalSyntheticLambda27 alertsCreator$$ExternalSyntheticLambda27 = r2;
        String string = LocaleController.getString("Set", NUM);
        AlertDialog.Builder builder2 = builder;
        AlertsCreator$$ExternalSyntheticLambda27 alertsCreator$$ExternalSyntheticLambda272 = new AlertsCreator$$ExternalSyntheticLambda27(checkMinDate, dayPicker, monthPicker, yearPicker, datePickerDelegate);
        builder2.setPositiveButton(string, alertsCreator$$ExternalSyntheticLambda272);
        builder2.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
        return builder2;
    }

    static /* synthetic */ void lambda$createDatePickerDialog$33(boolean checkMinDate, NumberPicker dayPicker, NumberPicker monthPicker, NumberPicker yearPicker, NumberPicker view, int scrollState) {
        if (checkMinDate && scrollState == 0) {
            checkPickerDate(dayPicker, monthPicker, yearPicker);
        }
    }

    static /* synthetic */ String lambda$createDatePickerDialog$34(int value) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(5, 1);
        calendar.set(2, value);
        return calendar.getDisplayName(2, 1, Locale.getDefault());
    }

    static /* synthetic */ void lambda$createDatePickerDialog$36(boolean checkMinDate, NumberPicker dayPicker, NumberPicker monthPicker, NumberPicker yearPicker, NumberPicker view, int scrollState) {
        if (checkMinDate && scrollState == 0) {
            checkPickerDate(dayPicker, monthPicker, yearPicker);
        }
    }

    static /* synthetic */ void lambda$createDatePickerDialog$38(boolean checkMinDate, NumberPicker dayPicker, NumberPicker monthPicker, NumberPicker yearPicker, NumberPicker view, int scrollState) {
        if (checkMinDate && scrollState == 0) {
            checkPickerDate(dayPicker, monthPicker, yearPicker);
        }
    }

    static /* synthetic */ void lambda$createDatePickerDialog$39(boolean checkMinDate, NumberPicker dayPicker, NumberPicker monthPicker, NumberPicker yearPicker, DatePickerDelegate datePickerDelegate, DialogInterface dialog, int which) {
        if (checkMinDate) {
            checkPickerDate(dayPicker, monthPicker, yearPicker);
        }
        datePickerDelegate.didSelectDate(yearPicker.getValue(), monthPicker.getValue(), dayPicker.getValue());
    }

    public static boolean checkScheduleDate(TextView button, TextView infoText, int type, NumberPicker dayPicker, NumberPicker hourPicker, NumberPicker minutePicker) {
        return checkScheduleDate(button, infoText, 0, type, dayPicker, hourPicker, minutePicker);
    }

    public static boolean checkScheduleDate(TextView button, TextView infoText, long maxDate, int type, NumberPicker dayPicker, NumberPicker hourPicker, NumberPicker minutePicker) {
        int currentYear;
        long maxDate2;
        int hour;
        int day;
        boolean z;
        String t;
        int num;
        int currentYear2;
        int day2;
        TextView textView = button;
        TextView textView2 = infoText;
        int i = type;
        NumberPicker numberPicker = dayPicker;
        NumberPicker numberPicker2 = hourPicker;
        NumberPicker numberPicker3 = minutePicker;
        int day3 = dayPicker.getValue();
        int hour2 = hourPicker.getValue();
        int minute = minutePicker.getValue();
        Calendar calendar = Calendar.getInstance();
        long systemTime = System.currentTimeMillis();
        calendar.setTimeInMillis(systemTime);
        int currentYear3 = calendar.get(1);
        int currentDay = calendar.get(6);
        if (maxDate > 0) {
            currentYear = currentYear3;
            calendar.setTimeInMillis(systemTime + (maxDate * 1000));
            calendar.set(11, 23);
            calendar.set(12, 59);
            calendar.set(13, 59);
            maxDate2 = calendar.getTimeInMillis();
        } else {
            currentYear = currentYear3;
            maxDate2 = maxDate;
        }
        int currentDay2 = currentDay;
        calendar.setTimeInMillis(System.currentTimeMillis() + (((long) day3) * 24 * 3600 * 1000));
        calendar.set(11, hour2);
        calendar.set(12, minute);
        long currentTime = calendar.getTimeInMillis();
        if (currentTime <= systemTime + 60000) {
            int day4 = day3;
            int i2 = hour2;
            calendar.setTimeInMillis(systemTime + 60000);
            int currentDay3 = currentDay2;
            if (currentDay3 != calendar.get(6)) {
                day2 = 1;
                numberPicker.setValue(1);
            } else {
                day2 = day4;
            }
            int day5 = day2;
            int i3 = currentDay3;
            int currentDay4 = calendar.get(11);
            numberPicker2.setValue(currentDay4);
            int hour3 = calendar.get(12);
            minute = hour3;
            numberPicker3.setValue(hour3);
            day = day5;
            hour = currentDay4;
        } else {
            int day6 = day3;
            int hour4 = hour2;
            if (maxDate2 <= 0 || currentTime <= maxDate2) {
                day = day6;
                hour = hour4;
            } else {
                calendar.setTimeInMillis(maxDate2);
                numberPicker.setValue(7);
                int hour5 = calendar.get(11);
                numberPicker2.setValue(hour5);
                int i4 = calendar.get(12);
                minute = i4;
                numberPicker3.setValue(i4);
                hour = hour5;
                day = 7;
            }
        }
        int selectedYear = calendar.get(1);
        long j = maxDate2;
        calendar.setTimeInMillis(System.currentTimeMillis() + (((long) day) * 24 * 3600 * 1000));
        calendar.set(11, hour);
        calendar.set(12, minute);
        long time = calendar.getTimeInMillis();
        if (textView != null) {
            if (day == 0) {
                num = 0;
                currentYear2 = currentYear;
            } else {
                currentYear2 = currentYear;
                if (currentYear2 == selectedYear) {
                    num = 1;
                } else {
                    num = 2;
                }
            }
            int i5 = currentYear2;
            if (i == 1) {
                num += 3;
            } else if (i == 2) {
                num += 6;
            } else if (i == 3) {
                num += 9;
            }
            textView.setText(LocaleController.getInstance().formatterScheduleSend[num].format(time));
        }
        if (textView2 != null) {
            int i6 = selectedYear;
            int diff = (int) ((time - systemTime) / 1000);
            if (diff > 86400) {
                t = LocaleController.formatPluralString("DaysSchedule", Math.round(((float) diff) / 86400.0f));
            } else if (diff >= 3600) {
                t = LocaleController.formatPluralString("HoursSchedule", Math.round(((float) diff) / 3600.0f));
            } else if (diff >= 60) {
                t = LocaleController.formatPluralString("MinutesSchedule", Math.round(((float) diff) / 60.0f));
            } else {
                t = LocaleController.formatPluralString("SecondsSchedule", diff);
            }
            if (infoText.getTag() != null) {
                z = false;
                int i7 = diff;
                textView2.setText(LocaleController.formatString("VoipChannelScheduleInfo", NUM, t));
            } else {
                z = false;
                textView2.setText(LocaleController.formatString("VoipGroupScheduleInfo", NUM, t));
            }
        } else {
            z = false;
        }
        if (currentTime - systemTime > 60000) {
            return true;
        }
        return z;
    }

    public static class ScheduleDatePickerColors {
        public final int backgroundColor;
        public final int buttonBackgroundColor;
        public final int buttonBackgroundPressedColor;
        public final int buttonTextColor;
        public final int iconColor;
        public final int iconSelectorColor;
        public final int subMenuBackgroundColor;
        public final int subMenuSelectorColor;
        public final int subMenuTextColor;
        public final int textColor;

        private ScheduleDatePickerColors() {
            this((Theme.ResourcesProvider) null);
        }

        /* JADX INFO: this call moved to the top of the method (can break code semantics) */
        private ScheduleDatePickerColors(Theme.ResourcesProvider rp) {
            this(rp != null ? rp.getColorOrDefault("dialogTextBlack") : Theme.getColor("dialogTextBlack"), rp != null ? rp.getColorOrDefault("dialogBackground") : Theme.getColor("dialogBackground"), rp != null ? rp.getColorOrDefault("key_sheet_other") : Theme.getColor("key_sheet_other"), rp != null ? rp.getColorOrDefault("player_actionBarSelector") : Theme.getColor("player_actionBarSelector"), rp != null ? rp.getColorOrDefault("actionBarDefaultSubmenuItem") : Theme.getColor("actionBarDefaultSubmenuItem"), rp != null ? rp.getColorOrDefault("actionBarDefaultSubmenuBackground") : Theme.getColor("actionBarDefaultSubmenuBackground"), rp != null ? rp.getColorOrDefault("listSelectorSDK21") : Theme.getColor("listSelectorSDK21"), rp != null ? rp.getColorOrDefault("featuredStickers_buttonText") : Theme.getColor("featuredStickers_buttonText"), rp != null ? rp.getColorOrDefault("featuredStickers_addButton") : Theme.getColor("featuredStickers_addButton"), rp != null ? rp.getColorOrDefault("featuredStickers_addButtonPressed") : Theme.getColor("featuredStickers_addButtonPressed"));
        }

        public ScheduleDatePickerColors(int textColor2, int backgroundColor2, int iconColor2, int iconSelectorColor2, int subMenuTextColor2, int subMenuBackgroundColor2, int subMenuSelectorColor2) {
            this(textColor2, backgroundColor2, iconColor2, iconSelectorColor2, subMenuTextColor2, subMenuBackgroundColor2, subMenuSelectorColor2, Theme.getColor("featuredStickers_buttonText"), Theme.getColor("featuredStickers_addButton"), Theme.getColor("featuredStickers_addButtonPressed"));
        }

        public ScheduleDatePickerColors(int textColor2, int backgroundColor2, int iconColor2, int iconSelectorColor2, int subMenuTextColor2, int subMenuBackgroundColor2, int subMenuSelectorColor2, int buttonTextColor2, int buttonBackgroundColor2, int buttonBackgroundPressedColor2) {
            this.textColor = textColor2;
            this.backgroundColor = backgroundColor2;
            this.iconColor = iconColor2;
            this.iconSelectorColor = iconSelectorColor2;
            this.subMenuTextColor = subMenuTextColor2;
            this.subMenuBackgroundColor = subMenuBackgroundColor2;
            this.subMenuSelectorColor = subMenuSelectorColor2;
            this.buttonTextColor = buttonTextColor2;
            this.buttonBackgroundColor = buttonBackgroundColor2;
            this.buttonBackgroundPressedColor = buttonBackgroundPressedColor2;
        }
    }

    public static BottomSheet.Builder createScheduleDatePickerDialog(Context context, long dialogId, ScheduleDatePickerDelegate datePickerDelegate) {
        return createScheduleDatePickerDialog(context, dialogId, -1, datePickerDelegate, (Runnable) null);
    }

    public static BottomSheet.Builder createScheduleDatePickerDialog(Context context, long dialogId, ScheduleDatePickerDelegate datePickerDelegate, Theme.ResourcesProvider resourcesProvider) {
        return createScheduleDatePickerDialog(context, dialogId, -1, datePickerDelegate, (Runnable) null, resourcesProvider);
    }

    public static BottomSheet.Builder createScheduleDatePickerDialog(Context context, long dialogId, ScheduleDatePickerDelegate datePickerDelegate, ScheduleDatePickerColors datePickerColors) {
        return createScheduleDatePickerDialog(context, dialogId, -1, datePickerDelegate, (Runnable) null, datePickerColors, (Theme.ResourcesProvider) null);
    }

    public static BottomSheet.Builder createScheduleDatePickerDialog(Context context, long dialogId, ScheduleDatePickerDelegate datePickerDelegate, Runnable cancelRunnable, Theme.ResourcesProvider resourcesProvider) {
        return createScheduleDatePickerDialog(context, dialogId, -1, datePickerDelegate, cancelRunnable, resourcesProvider);
    }

    public static BottomSheet.Builder createScheduleDatePickerDialog(Context context, long dialogId, long currentDate, ScheduleDatePickerDelegate datePickerDelegate, Runnable cancelRunnable) {
        return createScheduleDatePickerDialog(context, dialogId, currentDate, datePickerDelegate, cancelRunnable, new ScheduleDatePickerColors(), (Theme.ResourcesProvider) null);
    }

    public static BottomSheet.Builder createScheduleDatePickerDialog(Context context, long dialogId, long currentDate, ScheduleDatePickerDelegate datePickerDelegate, Runnable cancelRunnable, Theme.ResourcesProvider resourcesProvider) {
        return createScheduleDatePickerDialog(context, dialogId, currentDate, datePickerDelegate, cancelRunnable, new ScheduleDatePickerColors(resourcesProvider), resourcesProvider);
    }

    public static BottomSheet.Builder createScheduleDatePickerDialog(Context context, long dialogId, long currentDate, ScheduleDatePickerDelegate datePickerDelegate, Runnable cancelRunnable, ScheduleDatePickerColors datePickerColors, Theme.ResourcesProvider resourcesProvider) {
        String name;
        Context context2 = context;
        ScheduleDatePickerColors scheduleDatePickerColors = datePickerColors;
        Theme.ResourcesProvider resourcesProvider2 = resourcesProvider;
        if (context2 == null) {
            return null;
        }
        long selfUserId = UserConfig.getInstance(UserConfig.selectedAccount).getClientUserId();
        BottomSheet.Builder builder = new BottomSheet.Builder(context2, false, resourcesProvider2);
        builder.setApplyBottomPadding(false);
        final NumberPicker dayPicker = new NumberPicker(context2, resourcesProvider2);
        dayPicker.setTextColor(scheduleDatePickerColors.textColor);
        dayPicker.setTextOffset(AndroidUtilities.dp(10.0f));
        dayPicker.setItemCount(5);
        final NumberPicker hourPicker = new NumberPicker(context2, resourcesProvider2) {
            /* access modifiers changed from: protected */
            public CharSequence getContentDescription(int value) {
                return LocaleController.formatPluralString("Hours", value);
            }
        };
        hourPicker.setItemCount(5);
        hourPicker.setTextColor(scheduleDatePickerColors.textColor);
        hourPicker.setTextOffset(-AndroidUtilities.dp(10.0f));
        final NumberPicker minutePicker = new NumberPicker(context2, resourcesProvider2) {
            /* access modifiers changed from: protected */
            public CharSequence getContentDescription(int value) {
                return LocaleController.formatPluralString("Minutes", value);
            }
        };
        minutePicker.setItemCount(5);
        minutePicker.setTextColor(scheduleDatePickerColors.textColor);
        minutePicker.setTextOffset(-AndroidUtilities.dp(34.0f));
        LinearLayout container = new LinearLayout(context2) {
            boolean ignoreLayout = false;

            /* access modifiers changed from: protected */
            public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                int count;
                this.ignoreLayout = true;
                if (AndroidUtilities.displaySize.x > AndroidUtilities.displaySize.y) {
                    count = 3;
                } else {
                    count = 5;
                }
                dayPicker.setItemCount(count);
                hourPicker.setItemCount(count);
                minutePicker.setItemCount(count);
                dayPicker.getLayoutParams().height = AndroidUtilities.dp(54.0f) * count;
                hourPicker.getLayoutParams().height = AndroidUtilities.dp(54.0f) * count;
                minutePicker.getLayoutParams().height = AndroidUtilities.dp(54.0f) * count;
                this.ignoreLayout = false;
                super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            }

            public void requestLayout() {
                if (!this.ignoreLayout) {
                    super.requestLayout();
                }
            }
        };
        int i = 1;
        container.setOrientation(1);
        FrameLayout titleLayout = new FrameLayout(context2);
        container.addView(titleLayout, LayoutHelper.createLinear(-1, -2, 51, 22, 0, 0, 4));
        TextView titleView = new TextView(context2);
        if (dialogId == selfUserId) {
            titleView.setText(LocaleController.getString("SetReminder", NUM));
        } else {
            titleView.setText(LocaleController.getString("ScheduleMessage", NUM));
        }
        titleView.setTextColor(scheduleDatePickerColors.textColor);
        titleView.setTextSize(1, 20.0f);
        titleView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        titleLayout.addView(titleView, LayoutHelper.createFrame(-2, -2.0f, 51, 0.0f, 12.0f, 0.0f, 0.0f));
        titleView.setOnTouchListener(AlertsCreator$$ExternalSyntheticLambda68.INSTANCE);
        if (!DialogObject.isUserDialog(dialogId) || dialogId == selfUserId) {
            ScheduleDatePickerDelegate scheduleDatePickerDelegate = datePickerDelegate;
        } else {
            TLRPC.User user = MessagesController.getInstance(UserConfig.selectedAccount).getUser(Long.valueOf(dialogId));
            if (user == null || user.bot || user.status == null || user.status.expires <= 0) {
                TextView textView = titleView;
                ScheduleDatePickerDelegate scheduleDatePickerDelegate2 = datePickerDelegate;
            } else {
                String name2 = UserObject.getFirstName(user);
                if (name2.length() > 10) {
                    name = name2.substring(0, 10) + "…";
                } else {
                    name = name2;
                }
                TLRPC.User user2 = user;
                TextView textView2 = titleView;
                ActionBarMenuItem optionsButton = new ActionBarMenuItem(context, (ActionBarMenu) null, 0, scheduleDatePickerColors.iconColor, false, resourcesProvider);
                optionsButton.setLongClickEnabled(false);
                optionsButton.setSubMenuOpenSide(2);
                optionsButton.setIcon(NUM);
                i = 1;
                optionsButton.setBackgroundDrawable(Theme.createSelectorDrawable(scheduleDatePickerColors.iconSelectorColor, 1));
                titleLayout = titleLayout;
                titleLayout.addView(optionsButton, LayoutHelper.createFrame(40, 40.0f, 53, 0.0f, 8.0f, 5.0f, 0.0f));
                optionsButton.addSubItem(1, LocaleController.formatString("ScheduleWhenOnline", NUM, name));
                optionsButton.setOnClickListener(new AlertsCreator$$ExternalSyntheticLambda42(optionsButton, scheduleDatePickerColors));
                optionsButton.setDelegate(new AlertsCreator$$ExternalSyntheticLambda90(datePickerDelegate, builder));
                optionsButton.setContentDescription(LocaleController.getString("AccDescrMoreOptions", NUM));
            }
        }
        LinearLayout linearLayout = new LinearLayout(context2);
        linearLayout.setOrientation(0);
        linearLayout.setWeightSum(1.0f);
        container.addView(linearLayout, LayoutHelper.createLinear(-1, -2));
        long currentTime = System.currentTimeMillis();
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(currentTime);
        int currentYear = calendar.get(i);
        TextView buttonTextView = new TextView(context2) {
            public CharSequence getAccessibilityClassName() {
                return Button.class.getName();
            }
        };
        FrameLayout frameLayout = titleLayout;
        linearLayout.addView(dayPicker, LayoutHelper.createLinear(0, 270, 0.5f));
        dayPicker.setMinValue(0);
        dayPicker.setMaxValue(365);
        dayPicker.setWrapSelectorWheel(false);
        dayPicker.setFormatter(new AlertsCreator$$ExternalSyntheticLambda92(currentTime, calendar, currentYear));
        LinearLayout container2 = container;
        NumberPicker minutePicker2 = minutePicker;
        NumberPicker hourPicker2 = hourPicker;
        BottomSheet.Builder builder2 = builder;
        NumberPicker dayPicker2 = dayPicker;
        int i2 = currentYear;
        AlertsCreator$$ExternalSyntheticLambda3 alertsCreator$$ExternalSyntheticLambda3 = new AlertsCreator$$ExternalSyntheticLambda3(container2, buttonTextView, selfUserId, dialogId, dayPicker2, hourPicker2, minutePicker2);
        NumberPicker dayPicker3 = dayPicker2;
        dayPicker3.setOnValueChangedListener(alertsCreator$$ExternalSyntheticLambda3);
        NumberPicker hourPicker3 = hourPicker2;
        hourPicker3.setMinValue(0);
        hourPicker3.setMaxValue(23);
        linearLayout.addView(hourPicker3, LayoutHelper.createLinear(0, 270, 0.2f));
        hourPicker3.setFormatter(AlertsCreator$$ExternalSyntheticLambda100.INSTANCE);
        hourPicker3.setOnValueChangedListener(alertsCreator$$ExternalSyntheticLambda3);
        NumberPicker minutePicker3 = minutePicker2;
        minutePicker3.setMinValue(0);
        minutePicker3.setMaxValue(59);
        minutePicker3.setValue(0);
        minutePicker3.setFormatter(AlertsCreator$$ExternalSyntheticLambda101.INSTANCE);
        linearLayout.addView(minutePicker3, LayoutHelper.createLinear(0, 270, 0.3f));
        minutePicker3.setOnValueChangedListener(alertsCreator$$ExternalSyntheticLambda3);
        if (currentDate <= 0 || currentDate == NUM) {
            long j = currentDate;
        } else {
            long currentDate2 = 1000 * currentDate;
            calendar.setTimeInMillis(System.currentTimeMillis());
            calendar.set(12, 0);
            calendar.set(13, 0);
            calendar.set(14, 0);
            calendar.set(11, 0);
            int days = (int) ((currentDate2 - calendar.getTimeInMillis()) / 86400000);
            calendar.setTimeInMillis(currentDate2);
            if (days >= 0) {
                minutePicker3.setValue(calendar.get(12));
                hourPicker3.setValue(calendar.get(11));
                dayPicker3.setValue(days);
            }
            long j2 = currentDate2;
        }
        boolean[] canceled = {true};
        Calendar calendar2 = calendar;
        long j3 = currentTime;
        TextView buttonTextView2 = buttonTextView;
        checkScheduleDate(buttonTextView, (TextView) null, selfUserId == dialogId ? 1 : 0, dayPicker3, hourPicker3, minutePicker3);
        buttonTextView2.setPadding(AndroidUtilities.dp(34.0f), 0, AndroidUtilities.dp(34.0f), 0);
        buttonTextView2.setGravity(17);
        ScheduleDatePickerColors scheduleDatePickerColors2 = datePickerColors;
        buttonTextView2.setTextColor(scheduleDatePickerColors2.buttonTextColor);
        buttonTextView2.setTextSize(1, 14.0f);
        buttonTextView2.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        buttonTextView2.setBackgroundDrawable(Theme.createSimpleSelectorRoundRectDrawable(AndroidUtilities.dp(4.0f), scheduleDatePickerColors2.buttonBackgroundColor, scheduleDatePickerColors2.buttonBackgroundPressedColor));
        LinearLayout container3 = container2;
        container3.addView(buttonTextView2, LayoutHelper.createLinear(-1, 48, 83, 16, 15, 16, 16));
        NumberPicker numberPicker = dayPicker3;
        AlertsCreator$$ExternalSyntheticLambda3 alertsCreator$$ExternalSyntheticLambda32 = alertsCreator$$ExternalSyntheticLambda3;
        buttonTextView2.setOnClickListener(new AlertsCreator$$ExternalSyntheticLambda63(canceled, selfUserId, dialogId, dayPicker3, hourPicker3, minutePicker3, calendar2, datePickerDelegate, builder2));
        BottomSheet.Builder builder3 = builder2;
        builder3.setCustomView(container3);
        BottomSheet bottomSheet = builder3.show();
        bottomSheet.setOnDismissListener(new AlertsCreator$$ExternalSyntheticLambda35(cancelRunnable, canceled));
        bottomSheet.setBackgroundColor(scheduleDatePickerColors2.backgroundColor);
        return builder3;
    }

    static /* synthetic */ boolean lambda$createScheduleDatePickerDialog$40(View v, MotionEvent event) {
        return true;
    }

    static /* synthetic */ void lambda$createScheduleDatePickerDialog$41(ActionBarMenuItem optionsButton, ScheduleDatePickerColors datePickerColors, View v) {
        optionsButton.toggleSubMenu();
        optionsButton.setPopupItemsColor(datePickerColors.subMenuTextColor, false);
        optionsButton.setupPopupRadialSelectors(datePickerColors.subMenuSelectorColor);
        optionsButton.redrawPopup(datePickerColors.subMenuBackgroundColor);
    }

    static /* synthetic */ void lambda$createScheduleDatePickerDialog$42(ScheduleDatePickerDelegate datePickerDelegate, BottomSheet.Builder builder, int id) {
        if (id == 1) {
            datePickerDelegate.didSelectDate(true, NUM);
            builder.getDismissRunnable().run();
        }
    }

    static /* synthetic */ String lambda$createScheduleDatePickerDialog$43(long currentTime, Calendar calendar, int currentYear, int value) {
        if (value == 0) {
            return LocaleController.getString("MessageScheduleToday", NUM);
        }
        long date = (((long) value) * 86400000) + currentTime;
        calendar.setTimeInMillis(date);
        if (calendar.get(1) == currentYear) {
            return LocaleController.getInstance().formatterScheduleDay.format(date);
        }
        return LocaleController.getInstance().formatterScheduleYear.format(date);
    }

    static /* synthetic */ void lambda$createScheduleDatePickerDialog$44(LinearLayout container, TextView buttonTextView, long selfUserId, long dialogId, NumberPicker dayPicker, NumberPicker hourPicker, NumberPicker minutePicker, NumberPicker picker, int oldVal, int newVal) {
        LinearLayout linearLayout = container;
        try {
            container.performHapticFeedback(3, 2);
        } catch (Exception e) {
        }
        checkScheduleDate(buttonTextView, (TextView) null, selfUserId == dialogId ? 1 : 0, dayPicker, hourPicker, minutePicker);
    }

    static /* synthetic */ void lambda$createScheduleDatePickerDialog$47(boolean[] canceled, long selfUserId, long dialogId, NumberPicker dayPicker, NumberPicker hourPicker, NumberPicker minutePicker, Calendar calendar, ScheduleDatePickerDelegate datePickerDelegate, BottomSheet.Builder builder, View v) {
        Calendar calendar2 = calendar;
        canceled[0] = false;
        boolean setSeconds = checkScheduleDate((TextView) null, (TextView) null, selfUserId == dialogId ? 1 : 0, dayPicker, hourPicker, minutePicker);
        calendar2.setTimeInMillis(System.currentTimeMillis() + (((long) dayPicker.getValue()) * 24 * 3600 * 1000));
        calendar2.set(11, hourPicker.getValue());
        calendar2.set(12, minutePicker.getValue());
        if (setSeconds) {
            calendar2.set(13, 0);
        }
        datePickerDelegate.didSelectDate(true, (int) (calendar.getTimeInMillis() / 1000));
        builder.getDismissRunnable().run();
    }

    static /* synthetic */ void lambda$createScheduleDatePickerDialog$48(Runnable cancelRunnable, boolean[] canceled, DialogInterface dialog) {
        if (cancelRunnable != null && canceled[0]) {
            cancelRunnable.run();
        }
    }

    public static BottomSheet.Builder createDatePickerDialog(Context context, long currentDate, ScheduleDatePickerDelegate datePickerDelegate) {
        FrameLayout titleLayout;
        Context context2 = context;
        if (context2 == null) {
            return null;
        }
        ScheduleDatePickerColors datePickerColors = new ScheduleDatePickerColors();
        BottomSheet.Builder builder = new BottomSheet.Builder(context2, false);
        builder.setApplyBottomPadding(false);
        final NumberPicker dayPicker = new NumberPicker(context2);
        dayPicker.setTextColor(datePickerColors.textColor);
        dayPicker.setTextOffset(AndroidUtilities.dp(10.0f));
        dayPicker.setItemCount(5);
        final NumberPicker hourPicker = new NumberPicker(context2) {
            /* access modifiers changed from: protected */
            public CharSequence getContentDescription(int value) {
                return LocaleController.formatPluralString("Hours", value);
            }
        };
        hourPicker.setItemCount(5);
        hourPicker.setTextColor(datePickerColors.textColor);
        hourPicker.setTextOffset(-AndroidUtilities.dp(10.0f));
        final NumberPicker minutePicker = new NumberPicker(context2) {
            /* access modifiers changed from: protected */
            public CharSequence getContentDescription(int value) {
                return LocaleController.formatPluralString("Minutes", value);
            }
        };
        minutePicker.setItemCount(5);
        minutePicker.setTextColor(datePickerColors.textColor);
        minutePicker.setTextOffset(-AndroidUtilities.dp(34.0f));
        LinearLayout container = new LinearLayout(context2) {
            boolean ignoreLayout = false;

            /* access modifiers changed from: protected */
            public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                int count;
                this.ignoreLayout = true;
                if (AndroidUtilities.displaySize.x > AndroidUtilities.displaySize.y) {
                    count = 3;
                } else {
                    count = 5;
                }
                dayPicker.setItemCount(count);
                hourPicker.setItemCount(count);
                minutePicker.setItemCount(count);
                dayPicker.getLayoutParams().height = AndroidUtilities.dp(54.0f) * count;
                hourPicker.getLayoutParams().height = AndroidUtilities.dp(54.0f) * count;
                minutePicker.getLayoutParams().height = AndroidUtilities.dp(54.0f) * count;
                this.ignoreLayout = false;
                super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            }

            public void requestLayout() {
                if (!this.ignoreLayout) {
                    super.requestLayout();
                }
            }
        };
        container.setOrientation(1);
        FrameLayout titleLayout2 = new FrameLayout(context2);
        container.addView(titleLayout2, LayoutHelper.createLinear(-1, -2, 51, 22, 0, 0, 4));
        TextView titleView = new TextView(context2);
        titleView.setText(LocaleController.getString("ExpireAfter", NUM));
        titleView.setTextColor(datePickerColors.textColor);
        titleView.setTextSize(1, 20.0f);
        titleView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        titleLayout2.addView(titleView, LayoutHelper.createFrame(-2, -2.0f, 51, 0.0f, 12.0f, 0.0f, 0.0f));
        titleView.setOnTouchListener(AlertsCreator$$ExternalSyntheticLambda67.INSTANCE);
        LinearLayout linearLayout = new LinearLayout(context2);
        linearLayout.setOrientation(0);
        linearLayout.setWeightSum(1.0f);
        container.addView(linearLayout, LayoutHelper.createLinear(-1, -2));
        long currentTime = System.currentTimeMillis();
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(currentTime);
        int currentYear = calendar.get(1);
        TextView buttonTextView = new TextView(context2) {
            public CharSequence getAccessibilityClassName() {
                return Button.class.getName();
            }
        };
        TextView textView = titleView;
        linearLayout.addView(dayPicker, LayoutHelper.createLinear(0, 270, 0.5f));
        dayPicker.setMinValue(0);
        dayPicker.setMaxValue(365);
        dayPicker.setWrapSelectorWheel(false);
        dayPicker.setFormatter(new AlertsCreator$$ExternalSyntheticLambda91(currentTime, calendar, currentYear));
        AlertsCreator$$ExternalSyntheticLambda4 alertsCreator$$ExternalSyntheticLambda4 = new AlertsCreator$$ExternalSyntheticLambda4(container, dayPicker, hourPicker, minutePicker);
        dayPicker.setOnValueChangedListener(alertsCreator$$ExternalSyntheticLambda4);
        hourPicker.setMinValue(0);
        hourPicker.setMaxValue(23);
        int currentYear2 = currentYear;
        linearLayout.addView(hourPicker, LayoutHelper.createLinear(0, 270, 0.2f));
        hourPicker.setFormatter(AlertsCreator$$ExternalSyntheticLambda98.INSTANCE);
        hourPicker.setOnValueChangedListener(alertsCreator$$ExternalSyntheticLambda4);
        minutePicker.setMinValue(0);
        minutePicker.setMaxValue(59);
        minutePicker.setValue(0);
        minutePicker.setFormatter(AlertsCreator$$ExternalSyntheticLambda99.INSTANCE);
        linearLayout.addView(minutePicker, LayoutHelper.createLinear(0, 270, 0.3f));
        minutePicker.setOnValueChangedListener(alertsCreator$$ExternalSyntheticLambda4);
        if (currentDate <= 0 || currentDate == NUM) {
            titleLayout = titleLayout2;
            long j = currentDate;
        } else {
            long currentDate2 = 1000 * currentDate;
            long j2 = currentTime;
            calendar.setTimeInMillis(System.currentTimeMillis());
            calendar.set(12, 0);
            calendar.set(13, 0);
            calendar.set(14, 0);
            calendar.set(11, 0);
            titleLayout = titleLayout2;
            int days = (int) ((currentDate2 - calendar.getTimeInMillis()) / 86400000);
            calendar.setTimeInMillis(currentDate2);
            if (days >= 0) {
                minutePicker.setValue(calendar.get(12));
                hourPicker.setValue(calendar.get(11));
                dayPicker.setValue(days);
            }
            long j3 = currentDate2;
        }
        LinearLayout linearLayout2 = linearLayout;
        FrameLayout frameLayout = titleLayout;
        checkScheduleDate((TextView) null, (TextView) null, 0, dayPicker, hourPicker, minutePicker);
        buttonTextView.setPadding(AndroidUtilities.dp(34.0f), 0, AndroidUtilities.dp(34.0f), 0);
        buttonTextView.setGravity(17);
        buttonTextView.setTextColor(datePickerColors.buttonTextColor);
        buttonTextView.setTextSize(1, 14.0f);
        buttonTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        buttonTextView.setBackgroundDrawable(Theme.createSimpleSelectorRoundRectDrawable(AndroidUtilities.dp(4.0f), datePickerColors.buttonBackgroundColor, datePickerColors.buttonBackgroundPressedColor));
        buttonTextView.setText(LocaleController.getString("SetTimeLimit", NUM));
        container.addView(buttonTextView, LayoutHelper.createLinear(-1, 48, 83, 16, 15, 16, 16));
        int i = currentYear2;
        AlertsCreator$$ExternalSyntheticLambda4 alertsCreator$$ExternalSyntheticLambda42 = alertsCreator$$ExternalSyntheticLambda4;
        AlertsCreator$$ExternalSyntheticLambda46 alertsCreator$$ExternalSyntheticLambda46 = r4;
        AlertsCreator$$ExternalSyntheticLambda46 alertsCreator$$ExternalSyntheticLambda462 = new AlertsCreator$$ExternalSyntheticLambda46(dayPicker, hourPicker, minutePicker, calendar, datePickerDelegate, builder);
        buttonTextView.setOnClickListener(alertsCreator$$ExternalSyntheticLambda46);
        builder.setCustomView(container);
        builder.show().setBackgroundColor(datePickerColors.backgroundColor);
        return builder;
    }

    static /* synthetic */ boolean lambda$createDatePickerDialog$49(View v, MotionEvent event) {
        return true;
    }

    static /* synthetic */ String lambda$createDatePickerDialog$50(long currentTime, Calendar calendar, int currentYear, int value) {
        if (value == 0) {
            return LocaleController.getString("MessageScheduleToday", NUM);
        }
        long date = (((long) value) * 86400000) + currentTime;
        calendar.setTimeInMillis(date);
        if (calendar.get(1) == currentYear) {
            return LocaleController.getInstance().formatterScheduleDay.format(date);
        }
        return LocaleController.getInstance().formatterScheduleYear.format(date);
    }

    static /* synthetic */ void lambda$createDatePickerDialog$51(LinearLayout container, NumberPicker dayPicker, NumberPicker hourPicker, NumberPicker minutePicker, NumberPicker picker, int oldVal, int newVal) {
        try {
            container.performHapticFeedback(3, 2);
        } catch (Exception e) {
        }
        checkScheduleDate((TextView) null, (TextView) null, 0, dayPicker, hourPicker, minutePicker);
    }

    static /* synthetic */ void lambda$createDatePickerDialog$54(NumberPicker dayPicker, NumberPicker hourPicker, NumberPicker minutePicker, Calendar calendar, ScheduleDatePickerDelegate datePickerDelegate, BottomSheet.Builder builder, View v) {
        boolean setSeconds = checkScheduleDate((TextView) null, (TextView) null, 0, dayPicker, hourPicker, minutePicker);
        calendar.setTimeInMillis(System.currentTimeMillis() + (((long) dayPicker.getValue()) * 24 * 3600 * 1000));
        calendar.set(11, hourPicker.getValue());
        calendar.set(12, minutePicker.getValue());
        if (setSeconds) {
            calendar.set(13, 0);
        }
        datePickerDelegate.didSelectDate(true, (int) (calendar.getTimeInMillis() / 1000));
        builder.getDismissRunnable().run();
    }

    private static void checkCalendarDate(long minDate, NumberPicker dayPicker, NumberPicker monthPicker, NumberPicker yearPicker) {
        NumberPicker numberPicker = dayPicker;
        NumberPicker numberPicker2 = monthPicker;
        NumberPicker numberPicker3 = yearPicker;
        int day = dayPicker.getValue();
        int month = monthPicker.getValue();
        int year = yearPicker.getValue();
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(minDate);
        int minYear = calendar.get(1);
        int minMonth = calendar.get(2);
        int minDay = calendar.get(5);
        calendar.setTimeInMillis(System.currentTimeMillis());
        int maxYear = calendar.get(1);
        int maxMonth = calendar.get(2);
        int maxDay = calendar.get(5);
        if (year > maxYear) {
            year = maxYear;
            numberPicker3.setValue(maxYear);
        }
        if (year == maxYear) {
            if (month > maxMonth) {
                month = maxMonth;
                numberPicker2.setValue(maxMonth);
            }
            if (month == maxMonth && day > maxDay) {
                day = maxDay;
                numberPicker.setValue(maxDay);
            }
        }
        if (year < minYear) {
            year = minYear;
            numberPicker3.setValue(minYear);
        }
        if (year == minYear) {
            if (month < minMonth) {
                month = minMonth;
                numberPicker2.setValue(minMonth);
            }
            if (month == minMonth) {
                int minDay2 = minDay;
                if (day < minDay2) {
                    day = minDay2;
                    numberPicker.setValue(minDay2);
                }
            }
        }
        calendar.set(1, year);
        calendar.set(2, month);
        int daysInMonth = calendar.getActualMaximum(5);
        numberPicker.setMaxValue(daysInMonth);
        if (day > daysInMonth) {
            numberPicker.setValue(daysInMonth);
        }
    }

    public static BottomSheet.Builder createCalendarPickerDialog(Context context, long minDate, MessagesStorage.IntCallback callback, Theme.ResourcesProvider resourcesProvider) {
        Context context2 = context;
        long j = minDate;
        Theme.ResourcesProvider resourcesProvider2 = resourcesProvider;
        if (context2 == null) {
            return null;
        }
        BottomSheet.Builder builder = new BottomSheet.Builder(context2, false, resourcesProvider2);
        builder.setApplyBottomPadding(false);
        final NumberPicker dayPicker = new NumberPicker(context2, resourcesProvider2);
        dayPicker.setTextOffset(AndroidUtilities.dp(10.0f));
        dayPicker.setItemCount(5);
        final NumberPicker monthPicker = new NumberPicker(context2, resourcesProvider2);
        monthPicker.setItemCount(5);
        monthPicker.setTextOffset(-AndroidUtilities.dp(10.0f));
        final NumberPicker yearPicker = new NumberPicker(context2, resourcesProvider2);
        yearPicker.setItemCount(5);
        yearPicker.setTextOffset(-AndroidUtilities.dp(24.0f));
        LinearLayout container = new LinearLayout(context2) {
            boolean ignoreLayout = false;

            /* access modifiers changed from: protected */
            public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                int count;
                this.ignoreLayout = true;
                if (AndroidUtilities.displaySize.x > AndroidUtilities.displaySize.y) {
                    count = 3;
                } else {
                    count = 5;
                }
                dayPicker.setItemCount(count);
                monthPicker.setItemCount(count);
                yearPicker.setItemCount(count);
                dayPicker.getLayoutParams().height = AndroidUtilities.dp(54.0f) * count;
                monthPicker.getLayoutParams().height = AndroidUtilities.dp(54.0f) * count;
                yearPicker.getLayoutParams().height = AndroidUtilities.dp(54.0f) * count;
                this.ignoreLayout = false;
                super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            }

            public void requestLayout() {
                if (!this.ignoreLayout) {
                    super.requestLayout();
                }
            }
        };
        container.setOrientation(1);
        FrameLayout titleLayout = new FrameLayout(context2);
        container.addView(titleLayout, LayoutHelper.createLinear(-1, -2, 51, 22, 0, 0, 4));
        TextView titleView = new TextView(context2);
        titleView.setText(LocaleController.getString("ChooseDate", NUM));
        titleView.setTextColor(Theme.getColor("dialogTextBlack", resourcesProvider2));
        titleView.setTextSize(1, 20.0f);
        titleView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        titleLayout.addView(titleView, LayoutHelper.createFrame(-2, -2.0f, 51, 0.0f, 12.0f, 0.0f, 0.0f));
        titleView.setOnTouchListener(AlertsCreator$$ExternalSyntheticLambda66.INSTANCE);
        LinearLayout linearLayout = new LinearLayout(context2);
        linearLayout.setOrientation(0);
        linearLayout.setWeightSum(1.0f);
        container.addView(linearLayout, LayoutHelper.createLinear(-1, -2));
        long currentTimeMillis = System.currentTimeMillis();
        TextView buttonTextView = new TextView(context2) {
            public CharSequence getAccessibilityClassName() {
                return Button.class.getName();
            }
        };
        linearLayout.addView(dayPicker, LayoutHelper.createLinear(0, 270, 0.25f));
        dayPicker.setMinValue(1);
        dayPicker.setMaxValue(31);
        dayPicker.setWrapSelectorWheel(false);
        dayPicker.setFormatter(AlertsCreator$$ExternalSyntheticLambda93.INSTANCE);
        TextView buttonTextView2 = buttonTextView;
        TextView textView = titleView;
        FrameLayout frameLayout = titleLayout;
        LinearLayout container2 = container;
        AlertsCreator$$ExternalSyntheticLambda2 alertsCreator$$ExternalSyntheticLambda2 = new AlertsCreator$$ExternalSyntheticLambda2(container, minDate, dayPicker, monthPicker, yearPicker);
        dayPicker.setOnValueChangedListener(alertsCreator$$ExternalSyntheticLambda2);
        monthPicker.setMinValue(0);
        monthPicker.setMaxValue(11);
        monthPicker.setWrapSelectorWheel(false);
        LinearLayout linearLayout2 = linearLayout;
        linearLayout2.addView(monthPicker, LayoutHelper.createLinear(0, 270, 0.5f));
        monthPicker.setFormatter(AlertsCreator$$ExternalSyntheticLambda95.INSTANCE);
        monthPicker.setOnValueChangedListener(alertsCreator$$ExternalSyntheticLambda2);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(j);
        int minYear = calendar.get(1);
        calendar.setTimeInMillis(System.currentTimeMillis());
        int maxYear = calendar.get(1);
        yearPicker.setMinValue(minYear);
        yearPicker.setMaxValue(maxYear);
        yearPicker.setWrapSelectorWheel(false);
        yearPicker.setFormatter(AlertsCreator$$ExternalSyntheticLambda96.INSTANCE);
        linearLayout2.addView(yearPicker, LayoutHelper.createLinear(0, 270, 0.25f));
        yearPicker.setOnValueChangedListener(alertsCreator$$ExternalSyntheticLambda2);
        dayPicker.setValue(31);
        monthPicker.setValue(12);
        yearPicker.setValue(maxYear);
        checkCalendarDate(j, dayPicker, monthPicker, yearPicker);
        TextView buttonTextView3 = buttonTextView2;
        buttonTextView3.setPadding(AndroidUtilities.dp(34.0f), 0, AndroidUtilities.dp(34.0f), 0);
        buttonTextView3.setGravity(17);
        buttonTextView3.setTextColor(Theme.getColor("featuredStickers_buttonText", resourcesProvider2));
        buttonTextView3.setTextSize(1, 14.0f);
        buttonTextView3.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        buttonTextView3.setText(LocaleController.getString("JumpToDate", NUM));
        buttonTextView3.setBackgroundDrawable(Theme.createSimpleSelectorRoundRectDrawable(AndroidUtilities.dp(4.0f), Theme.getColor("featuredStickers_addButton", resourcesProvider2), Theme.getColor("featuredStickers_addButtonPressed", resourcesProvider2)));
        LinearLayout container3 = container2;
        container3.addView(buttonTextView3, LayoutHelper.createLinear(-1, 48, 83, 16, 15, 16, 16));
        int i = minYear;
        LinearLayout linearLayout3 = linearLayout2;
        AlertsCreator$$ExternalSyntheticLambda2 alertsCreator$$ExternalSyntheticLambda22 = alertsCreator$$ExternalSyntheticLambda2;
        int i2 = maxYear;
        NumberPicker numberPicker = yearPicker;
        buttonTextView3.setOnClickListener(new AlertsCreator$$ExternalSyntheticLambda38(minDate, dayPicker, monthPicker, yearPicker, calendar, callback, builder));
        builder.setCustomView(container3);
        return builder;
    }

    static /* synthetic */ boolean lambda$createCalendarPickerDialog$55(View v, MotionEvent event) {
        return true;
    }

    static /* synthetic */ String lambda$createCalendarPickerDialog$56(int value) {
        return "" + value;
    }

    static /* synthetic */ void lambda$createCalendarPickerDialog$57(LinearLayout container, long minDate, NumberPicker dayPicker, NumberPicker monthPicker, NumberPicker yearPicker, NumberPicker picker, int oldVal, int newVal) {
        try {
            container.performHapticFeedback(3, 2);
        } catch (Exception e) {
        }
        checkCalendarDate(minDate, dayPicker, monthPicker, yearPicker);
    }

    static /* synthetic */ String lambda$createCalendarPickerDialog$58(int value) {
        switch (value) {
            case 0:
                return LocaleController.getString("January", NUM);
            case 1:
                return LocaleController.getString("February", NUM);
            case 2:
                return LocaleController.getString("March", NUM);
            case 3:
                return LocaleController.getString("April", NUM);
            case 4:
                return LocaleController.getString("May", NUM);
            case 5:
                return LocaleController.getString("June", NUM);
            case 6:
                return LocaleController.getString("July", NUM);
            case 7:
                return LocaleController.getString("August", NUM);
            case 8:
                return LocaleController.getString("September", NUM);
            case 9:
                return LocaleController.getString("October", NUM);
            case 10:
                return LocaleController.getString("November", NUM);
            default:
                return LocaleController.getString("December", NUM);
        }
    }

    static /* synthetic */ void lambda$createCalendarPickerDialog$60(long minDate, NumberPicker dayPicker, NumberPicker monthPicker, NumberPicker yearPicker, Calendar calendar, MessagesStorage.IntCallback callback, BottomSheet.Builder builder, View v) {
        checkCalendarDate(minDate, dayPicker, monthPicker, yearPicker);
        calendar.set(1, yearPicker.getValue());
        calendar.set(2, monthPicker.getValue());
        calendar.set(5, dayPicker.getValue());
        calendar.set(12, 0);
        calendar.set(11, 0);
        calendar.set(13, 0);
        callback.run((int) (calendar.getTimeInMillis() / 1000));
        builder.getDismissRunnable().run();
    }

    public static BottomSheet createMuteAlert(BaseFragment fragment, long dialog_id, Theme.ResourcesProvider resourcesProvider) {
        if (fragment == null || fragment.getParentActivity() == null) {
            return null;
        }
        BottomSheet.Builder builder = new BottomSheet.Builder(fragment.getParentActivity(), false, resourcesProvider);
        builder.setTitle(LocaleController.getString("Notifications", NUM), true);
        builder.setItems(new CharSequence[]{LocaleController.formatString("MuteFor", NUM, LocaleController.formatPluralString("Hours", 1)), LocaleController.formatString("MuteFor", NUM, LocaleController.formatPluralString("Hours", 8)), LocaleController.formatString("MuteFor", NUM, LocaleController.formatPluralString("Days", 2)), LocaleController.getString("MuteDisable", NUM)}, new AlertsCreator$$ExternalSyntheticLambda61(dialog_id, fragment, resourcesProvider));
        return builder.create();
    }

    static /* synthetic */ void lambda$createMuteAlert$61(long dialog_id, BaseFragment fragment, Theme.ResourcesProvider resourcesProvider, DialogInterface dialogInterface, int i) {
        int setting;
        if (i == 0) {
            setting = 0;
        } else if (i == 1) {
            setting = 1;
        } else if (i == 2) {
            setting = 2;
        } else {
            setting = 3;
        }
        NotificationsController.getInstance(UserConfig.selectedAccount).setDialogNotificationsSettings(dialog_id, setting);
        if (BulletinFactory.canShowBulletin(fragment)) {
            BulletinFactory.createMuteBulletin(fragment, setting, resourcesProvider).show();
        }
    }

    public static void sendReport(TLRPC.InputPeer peer, int type, String message, ArrayList<Integer> messages) {
        TLRPC.TL_messages_report request = new TLRPC.TL_messages_report();
        request.peer = peer;
        request.id.addAll(messages);
        request.message = message;
        if (type == 0) {
            request.reason = new TLRPC.TL_inputReportReasonSpam();
        } else if (type == 1) {
            request.reason = new TLRPC.TL_inputReportReasonFake();
        } else if (type == 2) {
            request.reason = new TLRPC.TL_inputReportReasonViolence();
        } else if (type == 3) {
            request.reason = new TLRPC.TL_inputReportReasonChildAbuse();
        } else if (type == 4) {
            request.reason = new TLRPC.TL_inputReportReasonPornography();
        } else if (type == 5) {
            request.reason = new TLRPC.TL_inputReportReasonOther();
        }
        ConnectionsManager.getInstance(UserConfig.selectedAccount).sendRequest(request, AlertsCreator$$ExternalSyntheticLambda89.INSTANCE);
    }

    static /* synthetic */ void lambda$sendReport$62(TLObject response, TLRPC.TL_error error) {
    }

    public static void createReportAlert(Context context, long dialog_id, int messageId, BaseFragment parentFragment) {
        createReportAlert(context, dialog_id, messageId, parentFragment, (Theme.ResourcesProvider) null);
    }

    public static void createReportAlert(Context context, long dialog_id, int messageId, BaseFragment parentFragment, Theme.ResourcesProvider resourcesProvider) {
        int[] icons;
        CharSequence[] items;
        Context context2 = context;
        BaseFragment baseFragment = parentFragment;
        if (context2 == null) {
            Theme.ResourcesProvider resourcesProvider2 = resourcesProvider;
        } else if (baseFragment == null) {
            Theme.ResourcesProvider resourcesProvider3 = resourcesProvider;
        } else {
            BottomSheet.Builder builder = new BottomSheet.Builder(context2, true, resourcesProvider);
            builder.setTitle(LocaleController.getString("ReportChat", NUM), true);
            if (messageId != 0) {
                items = new CharSequence[]{LocaleController.getString("ReportChatSpam", NUM), LocaleController.getString("ReportChatViolence", NUM), LocaleController.getString("ReportChatChild", NUM), LocaleController.getString("ReportChatPornography", NUM), LocaleController.getString("ReportChatOther", NUM)};
                icons = new int[]{NUM, NUM, NUM, NUM, NUM};
            } else {
                items = new CharSequence[]{LocaleController.getString("ReportChatSpam", NUM), LocaleController.getString("ReportChatFakeAccount", NUM), LocaleController.getString("ReportChatViolence", NUM), LocaleController.getString("ReportChatChild", NUM), LocaleController.getString("ReportChatPornography", NUM), LocaleController.getString("ReportChatOther", NUM)};
                icons = new int[]{NUM, NUM, NUM, NUM, NUM, NUM};
            }
            builder.setItems(items, icons, new AlertsCreator$$ExternalSyntheticLambda17(messageId, parentFragment, context, dialog_id, resourcesProvider));
            baseFragment.showDialog(builder.create());
        }
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v4, resolved type: org.telegram.tgnet.TLRPC$TL_account_reportPeer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v5, resolved type: org.telegram.tgnet.TLRPC$TL_account_reportPeer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v8, resolved type: org.telegram.tgnet.TLRPC$TL_messages_report} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v11, resolved type: org.telegram.tgnet.TLRPC$TL_account_reportPeer} */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    static /* synthetic */ void lambda$createReportAlert$64(int r12, org.telegram.ui.ActionBar.BaseFragment r13, android.content.Context r14, long r15, org.telegram.ui.ActionBar.Theme.ResourcesProvider r17, android.content.DialogInterface r18, int r19) {
        /*
            r7 = r13
            r8 = r19
            r0 = 3
            r1 = 2
            r2 = 4
            if (r12 != 0) goto L_0x001b
            if (r8 == 0) goto L_0x0010
            if (r8 == r1) goto L_0x0010
            if (r8 == r0) goto L_0x0010
            if (r8 != r2) goto L_0x001b
        L_0x0010:
            boolean r3 = r7 instanceof org.telegram.ui.ChatActivity
            if (r3 == 0) goto L_0x001b
            r0 = r7
            org.telegram.ui.ChatActivity r0 = (org.telegram.ui.ChatActivity) r0
            r0.openReportChat(r8)
            return
        L_0x001b:
            r3 = 5
            r4 = 1
            if (r12 != 0) goto L_0x0023
            if (r8 == r3) goto L_0x0027
            if (r8 == r4) goto L_0x0027
        L_0x0023:
            if (r12 == 0) goto L_0x0049
            if (r8 != r2) goto L_0x0049
        L_0x0027:
            boolean r0 = r7 instanceof org.telegram.ui.ChatActivity
            if (r0 == 0) goto L_0x0036
            android.app.Activity r0 = r13.getParentActivity()
            int r1 = r13.getClassGuid()
            org.telegram.messenger.AndroidUtilities.requestAdjustNothing(r0, r1)
        L_0x0036:
            org.telegram.ui.Components.AlertsCreator$17 r9 = new org.telegram.ui.Components.AlertsCreator$17
            if (r8 != r2) goto L_0x003c
            r2 = 5
            goto L_0x003d
        L_0x003c:
            r2 = r8
        L_0x003d:
            r0 = r9
            r1 = r14
            r3 = r13
            r4 = r12
            r5 = r15
            r0.<init>(r1, r2, r3, r4, r5)
            r13.showDialog(r9)
            return
        L_0x0049:
            int r3 = org.telegram.messenger.UserConfig.selectedAccount
            org.telegram.messenger.MessagesController r3 = org.telegram.messenger.MessagesController.getInstance(r3)
            r5 = r15
            org.telegram.tgnet.TLRPC$InputPeer r3 = r3.getInputPeer((long) r5)
            java.lang.String r9 = ""
            if (r12 == 0) goto L_0x0093
            org.telegram.tgnet.TLRPC$TL_messages_report r2 = new org.telegram.tgnet.TLRPC$TL_messages_report
            r2.<init>()
            r2.peer = r3
            java.util.ArrayList<java.lang.Integer> r10 = r2.id
            java.lang.Integer r11 = java.lang.Integer.valueOf(r12)
            r10.add(r11)
            r2.message = r9
            if (r8 != 0) goto L_0x0074
            org.telegram.tgnet.TLRPC$TL_inputReportReasonSpam r0 = new org.telegram.tgnet.TLRPC$TL_inputReportReasonSpam
            r0.<init>()
            r2.reason = r0
            goto L_0x0091
        L_0x0074:
            if (r8 != r4) goto L_0x007e
            org.telegram.tgnet.TLRPC$TL_inputReportReasonViolence r0 = new org.telegram.tgnet.TLRPC$TL_inputReportReasonViolence
            r0.<init>()
            r2.reason = r0
            goto L_0x0091
        L_0x007e:
            if (r8 != r1) goto L_0x0088
            org.telegram.tgnet.TLRPC$TL_inputReportReasonChildAbuse r0 = new org.telegram.tgnet.TLRPC$TL_inputReportReasonChildAbuse
            r0.<init>()
            r2.reason = r0
            goto L_0x0091
        L_0x0088:
            if (r8 != r0) goto L_0x0091
            org.telegram.tgnet.TLRPC$TL_inputReportReasonPornography r0 = new org.telegram.tgnet.TLRPC$TL_inputReportReasonPornography
            r0.<init>()
            r2.reason = r0
        L_0x0091:
            r0 = r2
            goto L_0x00ce
        L_0x0093:
            org.telegram.tgnet.TLRPC$TL_account_reportPeer r10 = new org.telegram.tgnet.TLRPC$TL_account_reportPeer
            r10.<init>()
            r10.peer = r3
            r10.message = r9
            if (r8 != 0) goto L_0x00a6
            org.telegram.tgnet.TLRPC$TL_inputReportReasonSpam r0 = new org.telegram.tgnet.TLRPC$TL_inputReportReasonSpam
            r0.<init>()
            r10.reason = r0
            goto L_0x00cd
        L_0x00a6:
            if (r8 != r4) goto L_0x00b0
            org.telegram.tgnet.TLRPC$TL_inputReportReasonFake r0 = new org.telegram.tgnet.TLRPC$TL_inputReportReasonFake
            r0.<init>()
            r10.reason = r0
            goto L_0x00cd
        L_0x00b0:
            if (r8 != r1) goto L_0x00ba
            org.telegram.tgnet.TLRPC$TL_inputReportReasonViolence r0 = new org.telegram.tgnet.TLRPC$TL_inputReportReasonViolence
            r0.<init>()
            r10.reason = r0
            goto L_0x00cd
        L_0x00ba:
            if (r8 != r0) goto L_0x00c4
            org.telegram.tgnet.TLRPC$TL_inputReportReasonChildAbuse r0 = new org.telegram.tgnet.TLRPC$TL_inputReportReasonChildAbuse
            r0.<init>()
            r10.reason = r0
            goto L_0x00cd
        L_0x00c4:
            if (r8 != r2) goto L_0x00cd
            org.telegram.tgnet.TLRPC$TL_inputReportReasonPornography r0 = new org.telegram.tgnet.TLRPC$TL_inputReportReasonPornography
            r0.<init>()
            r10.reason = r0
        L_0x00cd:
            r0 = r10
        L_0x00ce:
            int r1 = org.telegram.messenger.UserConfig.selectedAccount
            org.telegram.tgnet.ConnectionsManager r1 = org.telegram.tgnet.ConnectionsManager.getInstance(r1)
            org.telegram.ui.Components.AlertsCreator$$ExternalSyntheticLambda88 r2 = org.telegram.ui.Components.AlertsCreator$$ExternalSyntheticLambda88.INSTANCE
            r1.sendRequest(r0, r2)
            boolean r1 = r7 instanceof org.telegram.ui.ChatActivity
            if (r1 == 0) goto L_0x00ef
            r1 = r7
            org.telegram.ui.ChatActivity r1 = (org.telegram.ui.ChatActivity) r1
            org.telegram.ui.Components.UndoView r1 = r1.getUndoView()
            r9 = 0
            r2 = 74
            r4 = 0
            r1.showWithAction((long) r9, (int) r2, (java.lang.Runnable) r4)
            r2 = r17
            goto L_0x00fc
        L_0x00ef:
            org.telegram.ui.Components.BulletinFactory r1 = org.telegram.ui.Components.BulletinFactory.of(r13)
            r2 = r17
            org.telegram.ui.Components.Bulletin r1 = r1.createReportSent(r2)
            r1.show()
        L_0x00fc:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.AlertsCreator.lambda$createReportAlert$64(int, org.telegram.ui.ActionBar.BaseFragment, android.content.Context, long, org.telegram.ui.ActionBar.Theme$ResourcesProvider, android.content.DialogInterface, int):void");
    }

    static /* synthetic */ void lambda$createReportAlert$63(TLObject response, TLRPC.TL_error error) {
    }

    private static String getFloodWaitString(String error) {
        String timeString;
        int time = Utilities.parseInt(error).intValue();
        if (time < 60) {
            timeString = LocaleController.formatPluralString("Seconds", time);
        } else {
            timeString = LocaleController.formatPluralString("Minutes", time / 60);
        }
        return LocaleController.formatString("FloodWaitTime", NUM, timeString);
    }

    public static void showFloodWaitAlert(String error, BaseFragment fragment) {
        String timeString;
        if (error != null && error.startsWith("FLOOD_WAIT") && fragment != null && fragment.getParentActivity() != null) {
            int time = Utilities.parseInt(error).intValue();
            if (time < 60) {
                timeString = LocaleController.formatPluralString("Seconds", time);
            } else {
                timeString = LocaleController.formatPluralString("Minutes", time / 60);
            }
            AlertDialog.Builder builder = new AlertDialog.Builder((Context) fragment.getParentActivity());
            builder.setTitle(LocaleController.getString("AppName", NUM));
            builder.setMessage(LocaleController.formatString("FloodWaitTime", NUM, timeString));
            builder.setPositiveButton(LocaleController.getString("OK", NUM), (DialogInterface.OnClickListener) null);
            fragment.showDialog(builder.create(), true, (DialogInterface.OnDismissListener) null);
        }
    }

    public static void showSendMediaAlert(int result, BaseFragment fragment, Theme.ResourcesProvider resourcesProvider) {
        if (result != 0) {
            AlertDialog.Builder builder = new AlertDialog.Builder(fragment.getParentActivity(), resourcesProvider);
            builder.setTitle(LocaleController.getString("AppName", NUM));
            if (result == 1) {
                builder.setMessage(LocaleController.getString("ErrorSendRestrictedStickers", NUM));
            } else if (result == 2) {
                builder.setMessage(LocaleController.getString("ErrorSendRestrictedMedia", NUM));
            } else if (result == 3) {
                builder.setMessage(LocaleController.getString("ErrorSendRestrictedPolls", NUM));
            } else if (result == 4) {
                builder.setMessage(LocaleController.getString("ErrorSendRestrictedStickersAll", NUM));
            } else if (result == 5) {
                builder.setMessage(LocaleController.getString("ErrorSendRestrictedMediaAll", NUM));
            } else if (result == 6) {
                builder.setMessage(LocaleController.getString("ErrorSendRestrictedPollsAll", NUM));
            }
            builder.setPositiveButton(LocaleController.getString("OK", NUM), (DialogInterface.OnClickListener) null);
            fragment.showDialog(builder.create(), true, (DialogInterface.OnDismissListener) null);
        }
    }

    public static void showAddUserAlert(String error, BaseFragment fragment, boolean isChannel, TLObject request) {
        if (error != null && fragment != null && fragment.getParentActivity() != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder((Context) fragment.getParentActivity());
            builder.setTitle(LocaleController.getString("AppName", NUM));
            char c = 65535;
            switch (error.hashCode()) {
                case -2120721660:
                    if (error.equals("CHANNELS_ADMIN_LOCATED_TOO_MUCH")) {
                        c = 17;
                        break;
                    }
                    break;
                case -2012133105:
                    if (error.equals("CHANNELS_ADMIN_PUBLIC_TOO_MUCH")) {
                        c = 16;
                        break;
                    }
                    break;
                case -1763467626:
                    if (error.equals("USERS_TOO_FEW")) {
                        c = 9;
                        break;
                    }
                    break;
                case -538116776:
                    if (error.equals("USER_BLOCKED")) {
                        c = 1;
                        break;
                    }
                    break;
                case -512775857:
                    if (error.equals("USER_RESTRICTED")) {
                        c = 10;
                        break;
                    }
                    break;
                case -454039871:
                    if (error.equals("PEER_FLOOD")) {
                        c = 0;
                        break;
                    }
                    break;
                case -420079733:
                    if (error.equals("BOTS_TOO_MUCH")) {
                        c = 7;
                        break;
                    }
                    break;
                case 98635865:
                    if (error.equals("USER_KICKED")) {
                        c = 13;
                        break;
                    }
                    break;
                case 517420851:
                    if (error.equals("USER_BOT")) {
                        c = 2;
                        break;
                    }
                    break;
                case 845559454:
                    if (error.equals("YOU_BLOCKED_USER")) {
                        c = 11;
                        break;
                    }
                    break;
                case 916342611:
                    if (error.equals("USER_ADMIN_INVALID")) {
                        c = 15;
                        break;
                    }
                    break;
                case 1047173446:
                    if (error.equals("CHAT_ADMIN_BAN_REQUIRED")) {
                        c = 12;
                        break;
                    }
                    break;
                case 1167301807:
                    if (error.equals("USERS_TOO_MUCH")) {
                        c = 4;
                        break;
                    }
                    break;
                case 1227003815:
                    if (error.equals("USER_ID_INVALID")) {
                        c = 3;
                        break;
                    }
                    break;
                case 1253103379:
                    if (error.equals("ADMINS_TOO_MUCH")) {
                        c = 6;
                        break;
                    }
                    break;
                case 1355367367:
                    if (error.equals("CHANNELS_TOO_MUCH")) {
                        c = 18;
                        break;
                    }
                    break;
                case 1377621075:
                    if (error.equals("USER_CHANNELS_TOO_MUCH")) {
                        c = 19;
                        break;
                    }
                    break;
                case 1623167701:
                    if (error.equals("USER_NOT_MUTUAL_CONTACT")) {
                        c = 5;
                        break;
                    }
                    break;
                case 1754587486:
                    if (error.equals("CHAT_ADMIN_INVITE_REQUIRED")) {
                        c = 14;
                        break;
                    }
                    break;
                case 1916725894:
                    if (error.equals("USER_PRIVACY_RESTRICTED")) {
                        c = 8;
                        break;
                    }
                    break;
                case 1965565720:
                    if (error.equals("USER_ALREADY_PARTICIPANT")) {
                        c = 20;
                        break;
                    }
                    break;
            }
            switch (c) {
                case 0:
                    builder.setMessage(LocaleController.getString("NobodyLikesSpam2", NUM));
                    builder.setNegativeButton(LocaleController.getString("MoreInfo", NUM), new AlertsCreator$$ExternalSyntheticLambda21(fragment));
                    break;
                case 1:
                case 2:
                case 3:
                    if (!isChannel) {
                        builder.setMessage(LocaleController.getString("GroupUserCantAdd", NUM));
                        break;
                    } else {
                        builder.setMessage(LocaleController.getString("ChannelUserCantAdd", NUM));
                        break;
                    }
                case 4:
                    if (!isChannel) {
                        builder.setMessage(LocaleController.getString("GroupUserAddLimit", NUM));
                        break;
                    } else {
                        builder.setMessage(LocaleController.getString("ChannelUserAddLimit", NUM));
                        break;
                    }
                case 5:
                    if (!isChannel) {
                        builder.setMessage(LocaleController.getString("GroupUserLeftError", NUM));
                        break;
                    } else {
                        builder.setMessage(LocaleController.getString("ChannelUserLeftError", NUM));
                        break;
                    }
                case 6:
                    if (!isChannel) {
                        builder.setMessage(LocaleController.getString("GroupUserCantAdmin", NUM));
                        break;
                    } else {
                        builder.setMessage(LocaleController.getString("ChannelUserCantAdmin", NUM));
                        break;
                    }
                case 7:
                    if (!isChannel) {
                        builder.setMessage(LocaleController.getString("GroupUserCantBot", NUM));
                        break;
                    } else {
                        builder.setMessage(LocaleController.getString("ChannelUserCantBot", NUM));
                        break;
                    }
                case 8:
                    if (!isChannel) {
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
                    if (!(request instanceof TLRPC.TL_channels_inviteToChannel)) {
                        builder.setMessage(LocaleController.getString("AddAdminErrorBlacklisted", NUM));
                        break;
                    } else {
                        builder.setMessage(LocaleController.getString("AddUserErrorBlacklisted", NUM));
                        break;
                    }
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
                    builder.setTitle(LocaleController.getString("ChannelTooMuchTitle", NUM));
                    if (!(request instanceof TLRPC.TL_channels_createChannel)) {
                        builder.setMessage(LocaleController.getString("ChannelTooMuchJoin", NUM));
                        break;
                    } else {
                        builder.setMessage(LocaleController.getString("ChannelTooMuch", NUM));
                        break;
                    }
                case 19:
                    builder.setTitle(LocaleController.getString("ChannelTooMuchTitle", NUM));
                    builder.setMessage(LocaleController.getString("UserChannelTooMuchJoin", NUM));
                    break;
                case 20:
                    builder.setTitle(LocaleController.getString("VoipGroupVoiceChat", NUM));
                    builder.setMessage(LocaleController.getString("VoipGroupInviteAlreadyParticipant", NUM));
                    break;
                default:
                    builder.setMessage(LocaleController.getString("ErrorOccurred", NUM) + "\n" + error);
                    break;
            }
            builder.setPositiveButton(LocaleController.getString("OK", NUM), (DialogInterface.OnClickListener) null);
            fragment.showDialog(builder.create(), true, (DialogInterface.OnDismissListener) null);
        }
    }

    public static Dialog createColorSelectDialog(Activity parentActivity, long dialog_id, int globalType, Runnable onSelect) {
        int currentColor;
        Activity activity = parentActivity;
        long j = dialog_id;
        int i = globalType;
        Runnable runnable = onSelect;
        SharedPreferences preferences = MessagesController.getNotificationsSettings(UserConfig.selectedAccount);
        if (j != 0) {
            if (preferences.contains("color_" + j)) {
                currentColor = preferences.getInt("color_" + j, -16776961);
            } else if (DialogObject.isChatDialog(dialog_id)) {
                currentColor = preferences.getInt("GroupLed", -16776961);
            } else {
                currentColor = preferences.getInt("MessagesLed", -16776961);
            }
        } else if (i == 1) {
            currentColor = preferences.getInt("MessagesLed", -16776961);
        } else if (i == 0) {
            currentColor = preferences.getInt("GroupLed", -16776961);
        } else {
            currentColor = preferences.getInt("ChannelLed", -16776961);
        }
        LinearLayout linearLayout = new LinearLayout(activity);
        linearLayout.setOrientation(1);
        String[] descriptions = {LocaleController.getString("ColorRed", NUM), LocaleController.getString("ColorOrange", NUM), LocaleController.getString("ColorYellow", NUM), LocaleController.getString("ColorGreen", NUM), LocaleController.getString("ColorCyan", NUM), LocaleController.getString("ColorBlue", NUM), LocaleController.getString("ColorViolet", NUM), LocaleController.getString("ColorPink", NUM), LocaleController.getString("ColorWhite", NUM)};
        int[] selectedColor = {currentColor};
        int a = 0;
        for (int i2 = 9; a < i2; i2 = 9) {
            RadioColorCell cell = new RadioColorCell(activity);
            cell.setPadding(AndroidUtilities.dp(4.0f), 0, AndroidUtilities.dp(4.0f), 0);
            cell.setTag(Integer.valueOf(a));
            cell.setCheckColor(TextColorCell.colors[a], TextColorCell.colors[a]);
            cell.setTextAndValue(descriptions[a], currentColor == TextColorCell.colorsToSave[a]);
            linearLayout.addView(cell);
            cell.setOnClickListener(new AlertsCreator$$ExternalSyntheticLambda40(linearLayout, selectedColor));
            a++;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder((Context) activity);
        builder.setTitle(LocaleController.getString("LedColor", NUM));
        builder.setView(linearLayout);
        AlertsCreator$$ExternalSyntheticLambda72 alertsCreator$$ExternalSyntheticLambda72 = r1;
        SharedPreferences sharedPreferences = preferences;
        String string = LocaleController.getString("Set", NUM);
        String[] strArr = descriptions;
        AlertDialog.Builder builder2 = builder;
        AlertsCreator$$ExternalSyntheticLambda72 alertsCreator$$ExternalSyntheticLambda722 = new AlertsCreator$$ExternalSyntheticLambda72(dialog_id, selectedColor, globalType, onSelect);
        builder2.setPositiveButton(string, alertsCreator$$ExternalSyntheticLambda72);
        builder2.setNeutralButton(LocaleController.getString("LedDisabled", NUM), new AlertsCreator$$ExternalSyntheticLambda39(j, i, runnable));
        if (j != 0) {
            builder2.setNegativeButton(LocaleController.getString("Default", NUM), new AlertsCreator$$ExternalSyntheticLambda50(j, runnable));
        }
        return builder2.create();
    }

    static /* synthetic */ void lambda$createColorSelectDialog$66(LinearLayout linearLayout, int[] selectedColor, View v) {
        int count = linearLayout.getChildCount();
        int a1 = 0;
        while (true) {
            boolean z = false;
            if (a1 < count) {
                RadioColorCell cell1 = (RadioColorCell) linearLayout.getChildAt(a1);
                if (cell1 == v) {
                    z = true;
                }
                cell1.setChecked(z, true);
                a1++;
            } else {
                selectedColor[0] = TextColorCell.colorsToSave[((Integer) v.getTag()).intValue()];
                return;
            }
        }
    }

    static /* synthetic */ void lambda$createColorSelectDialog$67(long dialog_id, int[] selectedColor, int globalType, Runnable onSelect, DialogInterface dialogInterface, int which) {
        SharedPreferences.Editor editor = MessagesController.getNotificationsSettings(UserConfig.selectedAccount).edit();
        if (dialog_id != 0) {
            editor.putInt("color_" + dialog_id, selectedColor[0]);
            NotificationsController.getInstance(UserConfig.selectedAccount).deleteNotificationChannel(dialog_id);
        } else {
            if (globalType == 1) {
                editor.putInt("MessagesLed", selectedColor[0]);
            } else if (globalType == 0) {
                editor.putInt("GroupLed", selectedColor[0]);
            } else {
                editor.putInt("ChannelLed", selectedColor[0]);
            }
            NotificationsController.getInstance(UserConfig.selectedAccount).deleteNotificationChannelGlobal(globalType);
        }
        editor.commit();
        if (onSelect != null) {
            onSelect.run();
        }
    }

    static /* synthetic */ void lambda$createColorSelectDialog$68(long dialog_id, int globalType, Runnable onSelect, DialogInterface dialog, int which) {
        SharedPreferences.Editor editor = MessagesController.getNotificationsSettings(UserConfig.selectedAccount).edit();
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

    static /* synthetic */ void lambda$createColorSelectDialog$69(long dialog_id, Runnable onSelect, DialogInterface dialog, int which) {
        SharedPreferences.Editor editor = MessagesController.getNotificationsSettings(UserConfig.selectedAccount).edit();
        editor.remove("color_" + dialog_id);
        editor.commit();
        if (onSelect != null) {
            onSelect.run();
        }
    }

    public static Dialog createVibrationSelectDialog(Activity parentActivity, long dialogId, boolean globalGroup, boolean globalAll, Runnable onSelect) {
        String prefix;
        if (dialogId != 0) {
            prefix = "vibrate_" + dialogId;
        } else {
            prefix = globalGroup ? "vibrate_group" : "vibrate_messages";
        }
        return createVibrationSelectDialog(parentActivity, dialogId, prefix, onSelect);
    }

    public static Dialog createVibrationSelectDialog(Activity parentActivity, long dialogId, String prefKeyPrefix, Runnable onSelect) {
        String[] descriptions;
        Activity activity = parentActivity;
        String str = prefKeyPrefix;
        SharedPreferences preferences = MessagesController.getNotificationsSettings(UserConfig.selectedAccount);
        int[] selected = new int[1];
        int i = 0;
        if (dialogId != 0) {
            selected[0] = preferences.getInt(str, 0);
            if (selected[0] == 3) {
                selected[0] = 2;
            } else if (selected[0] == 2) {
                selected[0] = 3;
            }
            descriptions = new String[]{LocaleController.getString("VibrationDefault", NUM), LocaleController.getString("Short", NUM), LocaleController.getString("Long", NUM), LocaleController.getString("VibrationDisabled", NUM)};
        } else {
            selected[0] = preferences.getInt(str, 0);
            if (selected[0] == 0) {
                selected[0] = 1;
            } else if (selected[0] == 1) {
                selected[0] = 2;
            } else if (selected[0] == 2) {
                selected[0] = 0;
            }
            descriptions = new String[]{LocaleController.getString("VibrationDisabled", NUM), LocaleController.getString("VibrationDefault", NUM), LocaleController.getString("Short", NUM), LocaleController.getString("Long", NUM), LocaleController.getString("OnlyIfSilent", NUM)};
        }
        LinearLayout linearLayout = new LinearLayout(activity);
        linearLayout.setOrientation(1);
        AlertDialog.Builder builder = new AlertDialog.Builder((Context) activity);
        int a = 0;
        while (a < descriptions.length) {
            RadioColorCell cell = new RadioColorCell(activity);
            cell.setPadding(AndroidUtilities.dp(4.0f), i, AndroidUtilities.dp(4.0f), i);
            cell.setTag(Integer.valueOf(a));
            cell.setCheckColor(Theme.getColor("radioBackground"), Theme.getColor("dialogRadioBackgroundChecked"));
            cell.setTextAndValue(descriptions[a], selected[i] == a);
            linearLayout.addView(cell);
            AlertsCreator$$ExternalSyntheticLambda49 alertsCreator$$ExternalSyntheticLambda49 = r1;
            AlertsCreator$$ExternalSyntheticLambda49 alertsCreator$$ExternalSyntheticLambda492 = new AlertsCreator$$ExternalSyntheticLambda49(selected, dialogId, prefKeyPrefix, builder, onSelect);
            cell.setOnClickListener(alertsCreator$$ExternalSyntheticLambda49);
            a++;
            i = 0;
        }
        builder.setTitle(LocaleController.getString("Vibrate", NUM));
        builder.setView(linearLayout);
        builder.setPositiveButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
        return builder.create();
    }

    static /* synthetic */ void lambda$createVibrationSelectDialog$70(int[] selected, long dialogId, String prefKeyPrefix, AlertDialog.Builder builder, Runnable onSelect, View v) {
        selected[0] = ((Integer) v.getTag()).intValue();
        SharedPreferences.Editor editor = MessagesController.getNotificationsSettings(UserConfig.selectedAccount).edit();
        if (dialogId != 0) {
            if (selected[0] == 0) {
                editor.putInt(prefKeyPrefix, 0);
            } else if (selected[0] == 1) {
                editor.putInt(prefKeyPrefix, 1);
            } else if (selected[0] == 2) {
                editor.putInt(prefKeyPrefix, 3);
            } else if (selected[0] == 3) {
                editor.putInt(prefKeyPrefix, 2);
            }
            NotificationsController.getInstance(UserConfig.selectedAccount).deleteNotificationChannel(dialogId);
        } else {
            if (selected[0] == 0) {
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
            if (prefKeyPrefix.equals("vibrate_channel")) {
                NotificationsController.getInstance(UserConfig.selectedAccount).deleteNotificationChannelGlobal(2);
            } else if (prefKeyPrefix.equals("vibrate_group")) {
                NotificationsController.getInstance(UserConfig.selectedAccount).deleteNotificationChannelGlobal(0);
            } else {
                NotificationsController.getInstance(UserConfig.selectedAccount).deleteNotificationChannelGlobal(1);
            }
        }
        editor.commit();
        builder.getDismissRunnable().run();
        if (onSelect != null) {
            onSelect.run();
        }
    }

    public static Dialog createLocationUpdateDialog(Activity parentActivity, TLRPC.User user, MessagesStorage.IntCallback callback, Theme.ResourcesProvider resourcesProvider) {
        Activity activity = parentActivity;
        Theme.ResourcesProvider resourcesProvider2 = resourcesProvider;
        int[] selected = new int[1];
        int i = 3;
        String[] descriptions = {LocaleController.getString("SendLiveLocationFor15m", NUM), LocaleController.getString("SendLiveLocationFor1h", NUM), LocaleController.getString("SendLiveLocationFor8h", NUM)};
        LinearLayout linearLayout = new LinearLayout(activity);
        linearLayout.setOrientation(1);
        TextView titleTextView = new TextView(activity);
        if (user != null) {
            titleTextView.setText(LocaleController.formatString("LiveLocationAlertPrivate", NUM, UserObject.getFirstName(user)));
        } else {
            titleTextView.setText(LocaleController.getString("LiveLocationAlertGroup", NUM));
        }
        titleTextView.setTextColor(resourcesProvider2 != null ? resourcesProvider2.getColorOrDefault("dialogTextBlack") : Theme.getColor("dialogTextBlack"));
        titleTextView.setTextSize(1, 16.0f);
        titleTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
        if (LocaleController.isRTL) {
            i = 5;
        }
        linearLayout.addView(titleTextView, LayoutHelper.createLinear(-2, -2, i | 48, 24, 0, 24, 8));
        int a = 0;
        while (a < descriptions.length) {
            RadioColorCell cell = new RadioColorCell(activity, resourcesProvider2);
            cell.setPadding(AndroidUtilities.dp(4.0f), 0, AndroidUtilities.dp(4.0f), 0);
            cell.setTag(Integer.valueOf(a));
            cell.setCheckColor(resourcesProvider2 != null ? resourcesProvider2.getColorOrDefault("radioBackground") : Theme.getColor("radioBackground"), resourcesProvider2 != null ? resourcesProvider2.getColorOrDefault("dialogRadioBackgroundChecked") : Theme.getColor("dialogRadioBackgroundChecked"));
            cell.setTextAndValue(descriptions[a], selected[0] == a);
            linearLayout.addView(cell);
            cell.setOnClickListener(new AlertsCreator$$ExternalSyntheticLambda52(selected, linearLayout));
            a++;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(activity, resourcesProvider2);
        builder.setTopImage((Drawable) new ShareLocationDrawable(activity, 0), resourcesProvider2 != null ? resourcesProvider2.getColorOrDefault("dialogTopBackground") : Theme.getColor("dialogTopBackground"));
        builder.setView(linearLayout);
        builder.setPositiveButton(LocaleController.getString("ShareFile", NUM), new AlertsCreator$$ExternalSyntheticLambda31(selected, callback));
        builder.setNeutralButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
        return builder.create();
    }

    static /* synthetic */ void lambda$createLocationUpdateDialog$71(int[] selected, LinearLayout linearLayout, View v) {
        selected[0] = ((Integer) v.getTag()).intValue();
        int count = linearLayout.getChildCount();
        for (int a1 = 0; a1 < count; a1++) {
            View child = linearLayout.getChildAt(a1);
            if (child instanceof RadioColorCell) {
                ((RadioColorCell) child).setChecked(child == v, true);
            }
        }
    }

    static /* synthetic */ void lambda$createLocationUpdateDialog$72(int[] selected, MessagesStorage.IntCallback callback, DialogInterface dialog, int which) {
        int time;
        if (selected[0] == 0) {
            time = 900;
        } else if (selected[0] == 1) {
            time = 3600;
        } else {
            time = 28800;
        }
        callback.run(time);
    }

    public static AlertDialog.Builder createBackgroundLocationPermissionDialog(Activity activity, TLRPC.User selfUser, Runnable cancelRunnable, Theme.ResourcesProvider resourcesProvider) {
        Activity activity2 = activity;
        TLRPC.User user = selfUser;
        if (activity2 == null) {
            Runnable runnable = cancelRunnable;
            Theme.ResourcesProvider resourcesProvider2 = resourcesProvider;
        } else if (Build.VERSION.SDK_INT < 29) {
            Runnable runnable2 = cancelRunnable;
            Theme.ResourcesProvider resourcesProvider3 = resourcesProvider;
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(activity2, resourcesProvider);
            String svg = RLottieDrawable.readRes((File) null, Theme.getCurrentTheme().isDark() ? NUM : NUM);
            String pinSvg = RLottieDrawable.readRes((File) null, Theme.getCurrentTheme().isDark() ? NUM : NUM);
            FrameLayout frameLayout = new FrameLayout(activity2);
            frameLayout.setClipToOutline(true);
            frameLayout.setOutlineProvider(new ViewOutlineProvider() {
                public void getOutline(View view, Outline outline) {
                    outline.setRoundRect(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight() + AndroidUtilities.dp(6.0f), (float) AndroidUtilities.dp(6.0f));
                }
            });
            View background = new View(activity2);
            background.setBackground(SvgHelper.getDrawable(svg));
            frameLayout.addView(background, LayoutHelper.createFrame(-1, -1.0f, 51, 0.0f, 0.0f, 0.0f, 0.0f));
            View pin = new View(activity2);
            pin.setBackground(SvgHelper.getDrawable(pinSvg));
            frameLayout.addView(pin, LayoutHelper.createFrame(60, 82.0f, 17, 0.0f, 0.0f, 0.0f, 0.0f));
            BackupImageView imageView = new BackupImageView(activity2);
            imageView.setRoundRadius(AndroidUtilities.dp(26.0f));
            imageView.setForUserOrChat(user, new AvatarDrawable(user));
            frameLayout.addView(imageView, LayoutHelper.createFrame(52, 52.0f, 17, 0.0f, 0.0f, 0.0f, 11.0f));
            builder.setTopView(frameLayout);
            builder.setTopViewAspectRatio(0.37820512f);
            builder.setMessage(AndroidUtilities.replaceTags(LocaleController.getString("PermissionBackgroundLocation", NUM)));
            builder.setPositiveButton(LocaleController.getString("OK", NUM), new AlertsCreator$$ExternalSyntheticLambda83(activity2));
            builder.setNegativeButton(LocaleController.getString("Cancel", NUM), new AlertsCreator$$ExternalSyntheticLambda9(cancelRunnable));
            return builder;
        }
        return null;
    }

    static /* synthetic */ void lambda$createBackgroundLocationPermissionDialog$73(Activity activity, DialogInterface dialog, int which) {
        if (activity.checkSelfPermission("android.permission.ACCESS_BACKGROUND_LOCATION") != 0) {
            activity.requestPermissions(new String[]{"android.permission.ACCESS_BACKGROUND_LOCATION"}, 30);
        }
    }

    public static AlertDialog.Builder createGigagroupConvertAlert(Activity activity, DialogInterface.OnClickListener onProcess, DialogInterface.OnClickListener onCancel) {
        Activity activity2 = activity;
        AlertDialog.Builder builder = new AlertDialog.Builder((Context) activity);
        String svg = RLottieDrawable.readRes((File) null, NUM);
        FrameLayout frameLayout = new FrameLayout(activity);
        if (Build.VERSION.SDK_INT >= 21) {
            frameLayout.setClipToOutline(true);
            frameLayout.setOutlineProvider(new ViewOutlineProvider() {
                public void getOutline(View view, Outline outline) {
                    outline.setRoundRect(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight() + AndroidUtilities.dp(6.0f), (float) AndroidUtilities.dp(6.0f));
                }
            });
        }
        View background = new View(activity);
        background.setBackground(new BitmapDrawable(SvgHelper.getBitmap(svg, AndroidUtilities.dp(320.0f), AndroidUtilities.dp(320.0f * 0.3974359f), false)));
        frameLayout.addView(background, LayoutHelper.createFrame(-1, -1.0f, 0, -1.0f, -1.0f, -1.0f, -1.0f));
        builder.setTopView(frameLayout);
        builder.setTopViewAspectRatio(0.3974359f);
        builder.setTitle(LocaleController.getString("GigagroupAlertTitle", NUM));
        builder.setMessage(AndroidUtilities.replaceTags(LocaleController.getString("GigagroupAlertText", NUM)));
        builder.setPositiveButton(LocaleController.getString("GigagroupAlertLearnMore", NUM), onProcess);
        builder.setNegativeButton(LocaleController.getString("Cancel", NUM), onCancel);
        return builder;
    }

    public static AlertDialog.Builder createDrawOverlayPermissionDialog(Activity activity, DialogInterface.OnClickListener onCancel) {
        Activity activity2 = activity;
        AlertDialog.Builder builder = new AlertDialog.Builder((Context) activity2);
        String svg = RLottieDrawable.readRes((File) null, NUM);
        FrameLayout frameLayout = new FrameLayout(activity2);
        frameLayout.setBackground(new GradientDrawable(GradientDrawable.Orientation.BL_TR, new int[]{-14535089, -14527894}));
        frameLayout.setClipToOutline(true);
        frameLayout.setOutlineProvider(new ViewOutlineProvider() {
            public void getOutline(View view, Outline outline) {
                outline.setRoundRect(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight() + AndroidUtilities.dp(6.0f), AndroidUtilities.dpf2(6.0f));
            }
        });
        View background = new View(activity2);
        background.setBackground(new BitmapDrawable(SvgHelper.getBitmap(svg, AndroidUtilities.dp(320.0f), AndroidUtilities.dp(320.0f * 0.50427353f), false)));
        frameLayout.addView(background, LayoutHelper.createFrame(-1, -1.0f, 0, -1.0f, -1.0f, -1.0f, -1.0f));
        builder.setTopView(frameLayout);
        builder.setTitle(LocaleController.getString("PermissionDrawAboveOtherAppsTitle", NUM));
        builder.setMessage(LocaleController.getString("PermissionDrawAboveOtherApps", NUM));
        builder.setPositiveButton(LocaleController.getString("Enable", NUM), new AlertsCreator$$ExternalSyntheticLambda94(activity2));
        builder.notDrawBackgroundOnTopView(true);
        builder.setNegativeButton(LocaleController.getString("Cancel", NUM), onCancel);
        builder.setTopViewAspectRatio(0.50427353f);
        return builder;
    }

    static /* synthetic */ void lambda$createDrawOverlayPermissionDialog$75(Activity activity, DialogInterface dialogInterface, int i) {
        if (activity != null && Build.VERSION.SDK_INT >= 23) {
            try {
                activity.startActivity(new Intent("android.settings.action.MANAGE_OVERLAY_PERMISSION", Uri.parse("package:" + activity.getPackageName())));
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        }
    }

    public static AlertDialog.Builder createDrawOverlayGroupCallPermissionDialog(Context context) {
        Context context2 = context;
        AlertDialog.Builder builder = new AlertDialog.Builder(context2);
        String svg = RLottieDrawable.readRes((File) null, NUM);
        final GroupCallPipButton button = new GroupCallPipButton(context2, 0, true);
        button.setImportantForAccessibility(2);
        FrameLayout frameLayout = new FrameLayout(context2) {
            /* access modifiers changed from: protected */
            public void onLayout(boolean changed, int left, int top, int right, int bottom) {
                super.onLayout(changed, left, top, right, bottom);
                button.setTranslationY((((float) getMeasuredHeight()) * 0.28f) - (((float) button.getMeasuredWidth()) / 2.0f));
                button.setTranslationX((((float) getMeasuredWidth()) * 0.82f) - (((float) button.getMeasuredWidth()) / 2.0f));
            }
        };
        frameLayout.setBackground(new GradientDrawable(GradientDrawable.Orientation.BL_TR, new int[]{-15128003, -15118002}));
        frameLayout.setClipToOutline(true);
        frameLayout.setOutlineProvider(new ViewOutlineProvider() {
            public void getOutline(View view, Outline outline) {
                outline.setRoundRect(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight() + AndroidUtilities.dp(6.0f), AndroidUtilities.dpf2(6.0f));
            }
        });
        View background = new View(context2);
        background.setBackground(new BitmapDrawable(SvgHelper.getBitmap(svg, AndroidUtilities.dp(320.0f), AndroidUtilities.dp(320.0f * 0.5769231f), false)));
        frameLayout.addView(background, LayoutHelper.createFrame(-1, -1.0f, 0, -1.0f, -1.0f, -1.0f, -1.0f));
        frameLayout.addView(button, LayoutHelper.createFrame(117, 117.0f));
        builder.setTopView(frameLayout);
        builder.setTitle(LocaleController.getString("PermissionDrawAboveOtherAppsGroupCallTitle", NUM));
        builder.setMessage(LocaleController.getString("PermissionDrawAboveOtherAppsGroupCall", NUM));
        builder.setPositiveButton(LocaleController.getString("Enable", NUM), new AlertsCreator$$ExternalSyntheticLambda105(context2));
        builder.notDrawBackgroundOnTopView(true);
        builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
        builder.setTopViewAspectRatio(0.5769231f);
        return builder;
    }

    static /* synthetic */ void lambda$createDrawOverlayGroupCallPermissionDialog$76(Context context, DialogInterface dialogInterface, int i) {
        if (context != null) {
            try {
                if (Build.VERSION.SDK_INT >= 23) {
                    Intent intent = new Intent("android.settings.action.MANAGE_OVERLAY_PERMISSION", Uri.parse("package:" + context.getPackageName()));
                    Activity activity = AndroidUtilities.findActivity(context);
                    if (activity instanceof LaunchActivity) {
                        activity.startActivityForResult(intent, 105);
                    } else {
                        context.startActivity(intent);
                    }
                }
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        }
    }

    public static AlertDialog.Builder createContactsPermissionDialog(Activity parentActivity, MessagesStorage.IntCallback callback) {
        AlertDialog.Builder builder = new AlertDialog.Builder((Context) parentActivity);
        builder.setTopImage(NUM, Theme.getColor("dialogTopBackground"));
        builder.setMessage(AndroidUtilities.replaceTags(LocaleController.getString("ContactsPermissionAlert", NUM)));
        builder.setPositiveButton(LocaleController.getString("ContactsPermissionAlertContinue", NUM), new AlertsCreator$$ExternalSyntheticLambda13(callback));
        builder.setNegativeButton(LocaleController.getString("ContactsPermissionAlertNotNow", NUM), new AlertsCreator$$ExternalSyntheticLambda14(callback));
        return builder;
    }

    public static Dialog createFreeSpaceDialog(LaunchActivity parentActivity) {
        LaunchActivity launchActivity = parentActivity;
        int[] selected = new int[1];
        int i = 3;
        if (SharedConfig.keepMedia == 2) {
            selected[0] = 3;
        } else if (SharedConfig.keepMedia == 0) {
            selected[0] = 1;
        } else if (SharedConfig.keepMedia == 1) {
            selected[0] = 2;
        } else if (SharedConfig.keepMedia == 3) {
            selected[0] = 0;
        }
        String[] descriptions = {LocaleController.formatPluralString("Days", 3), LocaleController.formatPluralString("Weeks", 1), LocaleController.formatPluralString("Months", 1), LocaleController.getString("LowDiskSpaceNeverRemove", NUM)};
        LinearLayout linearLayout = new LinearLayout(launchActivity);
        linearLayout.setOrientation(1);
        TextView titleTextView = new TextView(launchActivity);
        titleTextView.setText(LocaleController.getString("LowDiskSpaceTitle2", NUM));
        titleTextView.setTextColor(Theme.getColor("dialogTextBlack"));
        titleTextView.setTextSize(1, 16.0f);
        titleTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        titleTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
        if (LocaleController.isRTL) {
            i = 5;
        }
        linearLayout.addView(titleTextView, LayoutHelper.createLinear(-2, -2, i | 48, 24, 0, 24, 8));
        int a = 0;
        while (a < descriptions.length) {
            RadioColorCell cell = new RadioColorCell(launchActivity);
            cell.setPadding(AndroidUtilities.dp(4.0f), 0, AndroidUtilities.dp(4.0f), 0);
            cell.setTag(Integer.valueOf(a));
            cell.setCheckColor(Theme.getColor("radioBackground"), Theme.getColor("dialogRadioBackgroundChecked"));
            cell.setTextAndValue(descriptions[a], selected[0] == a);
            linearLayout.addView(cell);
            cell.setOnClickListener(new AlertsCreator$$ExternalSyntheticLambda51(selected, linearLayout));
            a++;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder((Context) launchActivity);
        builder.setTitle(LocaleController.getString("LowDiskSpaceTitle", NUM));
        builder.setMessage(LocaleController.getString("LowDiskSpaceMessage", NUM));
        builder.setView(linearLayout);
        builder.setPositiveButton(LocaleController.getString("OK", NUM), new AlertsCreator$$ExternalSyntheticLambda30(selected));
        builder.setNeutralButton(LocaleController.getString("ClearMediaCache", NUM), new AlertsCreator$$ExternalSyntheticLambda25(launchActivity));
        return builder.create();
    }

    static /* synthetic */ void lambda$createFreeSpaceDialog$79(int[] selected, LinearLayout linearLayout, View v) {
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
                ((RadioColorCell) child).setChecked(child == v, true);
            }
        }
    }

    public static Dialog createPrioritySelectDialog(Activity parentActivity, long dialog_id, int globalType, Runnable onSelect) {
        String[] descriptions;
        Activity activity = parentActivity;
        long j = dialog_id;
        int i = globalType;
        SharedPreferences preferences = MessagesController.getNotificationsSettings(UserConfig.selectedAccount);
        int[] selected = new int[1];
        int i2 = 0;
        if (j != 0) {
            selected[0] = preferences.getInt("priority_" + j, 3);
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
            descriptions = new String[]{LocaleController.getString("NotificationsPrioritySettings", NUM), LocaleController.getString("NotificationsPriorityLow", NUM), LocaleController.getString("NotificationsPriorityMedium", NUM), LocaleController.getString("NotificationsPriorityHigh", NUM), LocaleController.getString("NotificationsPriorityUrgent", NUM)};
        } else {
            if (i == 1) {
                selected[0] = preferences.getInt("priority_messages", 1);
            } else if (i == 0) {
                selected[0] = preferences.getInt("priority_group", 1);
            } else if (i == 2) {
                selected[0] = preferences.getInt("priority_channel", 1);
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
            descriptions = new String[]{LocaleController.getString("NotificationsPriorityLow", NUM), LocaleController.getString("NotificationsPriorityMedium", NUM), LocaleController.getString("NotificationsPriorityHigh", NUM), LocaleController.getString("NotificationsPriorityUrgent", NUM)};
        }
        LinearLayout linearLayout = new LinearLayout(activity);
        linearLayout.setOrientation(1);
        AlertDialog.Builder builder = new AlertDialog.Builder((Context) activity);
        int a = 0;
        while (a < descriptions.length) {
            RadioColorCell cell = new RadioColorCell(activity);
            cell.setPadding(AndroidUtilities.dp(4.0f), i2, AndroidUtilities.dp(4.0f), i2);
            cell.setTag(Integer.valueOf(a));
            cell.setCheckColor(Theme.getColor("radioBackground"), Theme.getColor("dialogRadioBackgroundChecked"));
            cell.setTextAndValue(descriptions[a], selected[i2] == a);
            linearLayout.addView(cell);
            AlertsCreator$$ExternalSyntheticLambda48 alertsCreator$$ExternalSyntheticLambda48 = r1;
            AlertDialog.Builder builder2 = builder;
            AlertsCreator$$ExternalSyntheticLambda48 alertsCreator$$ExternalSyntheticLambda482 = new AlertsCreator$$ExternalSyntheticLambda48(selected, dialog_id, globalType, preferences, builder2, onSelect);
            cell.setOnClickListener(alertsCreator$$ExternalSyntheticLambda48);
            a++;
            activity = parentActivity;
            linearLayout = linearLayout;
            builder = builder2;
            i2 = 0;
            long j2 = dialog_id;
        }
        AlertDialog.Builder builder3 = builder;
        builder3.setTitle(LocaleController.getString("NotificationsImportance", NUM));
        builder3.setView(linearLayout);
        builder3.setPositiveButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
        return builder3.create();
    }

    static /* synthetic */ void lambda$createPrioritySelectDialog$82(int[] selected, long dialog_id, int globalType, SharedPreferences preferences, AlertDialog.Builder builder, Runnable onSelect, View v) {
        int option;
        int option2;
        selected[0] = ((Integer) v.getTag()).intValue();
        SharedPreferences.Editor editor = MessagesController.getNotificationsSettings(UserConfig.selectedAccount).edit();
        if (dialog_id != 0) {
            if (selected[0] == 0) {
                option2 = 3;
            } else if (selected[0] == 1) {
                option2 = 4;
            } else if (selected[0] == 2) {
                option2 = 5;
            } else if (selected[0] == 3) {
                option2 = 0;
            } else {
                option2 = 1;
            }
            editor.putInt("priority_" + dialog_id, option2);
            NotificationsController.getInstance(UserConfig.selectedAccount).deleteNotificationChannel(dialog_id);
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
            NotificationsController.getInstance(UserConfig.selectedAccount).deleteNotificationChannelGlobal(globalType);
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
        String[] descriptions = {LocaleController.getString("NoPopup", NUM), LocaleController.getString("OnlyWhenScreenOn", NUM), LocaleController.getString("OnlyWhenScreenOff", NUM), LocaleController.getString("AlwaysShowPopup", NUM)};
        LinearLayout linearLayout = new LinearLayout(parentActivity);
        linearLayout.setOrientation(1);
        AlertDialog.Builder builder = new AlertDialog.Builder((Context) parentActivity);
        int a = 0;
        while (a < descriptions.length) {
            RadioColorCell cell = new RadioColorCell(parentActivity);
            cell.setTag(Integer.valueOf(a));
            cell.setPadding(AndroidUtilities.dp(4.0f), 0, AndroidUtilities.dp(4.0f), 0);
            cell.setCheckColor(Theme.getColor("radioBackground"), Theme.getColor("dialogRadioBackgroundChecked"));
            cell.setTextAndValue(descriptions[a], selected[0] == a);
            linearLayout.addView(cell);
            cell.setOnClickListener(new AlertsCreator$$ExternalSyntheticLambda47(selected, globalType, builder, onSelect));
            a++;
        }
        builder.setTitle(LocaleController.getString("PopupNotification", NUM));
        builder.setView(linearLayout);
        builder.setPositiveButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
        return builder.create();
    }

    static /* synthetic */ void lambda$createPopupSelectDialog$83(int[] selected, int globalType, AlertDialog.Builder builder, Runnable onSelect, View v) {
        selected[0] = ((Integer) v.getTag()).intValue();
        SharedPreferences.Editor editor = MessagesController.getNotificationsSettings(UserConfig.selectedAccount).edit();
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

    public static Dialog createSingleChoiceDialog(Activity parentActivity, String[] options, String title, int selected, DialogInterface.OnClickListener listener) {
        LinearLayout linearLayout = new LinearLayout(parentActivity);
        linearLayout.setOrientation(1);
        AlertDialog.Builder builder = new AlertDialog.Builder((Context) parentActivity);
        for (int a = 0; a < options.length; a++) {
            RadioColorCell cell = new RadioColorCell(parentActivity);
            boolean z = false;
            cell.setPadding(AndroidUtilities.dp(4.0f), 0, AndroidUtilities.dp(4.0f), 0);
            cell.setTag(Integer.valueOf(a));
            cell.setCheckColor(Theme.getColor("radioBackground"), Theme.getColor("dialogRadioBackgroundChecked"));
            String str = options[a];
            if (selected == a) {
                z = true;
            }
            cell.setTextAndValue(str, z);
            linearLayout.addView(cell);
            cell.setOnClickListener(new AlertsCreator$$ExternalSyntheticLambda43(builder, listener));
        }
        builder.setTitle(title);
        builder.setView(linearLayout);
        builder.setPositiveButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
        return builder.create();
    }

    static /* synthetic */ void lambda$createSingleChoiceDialog$84(AlertDialog.Builder builder, DialogInterface.OnClickListener listener, View v) {
        int sel = ((Integer) v.getTag()).intValue();
        builder.getDismissRunnable().run();
        listener.onClick((DialogInterface) null, sel);
    }

    public static AlertDialog.Builder createTTLAlert(Context context, TLRPC.EncryptedChat encryptedChat, Theme.ResourcesProvider resourcesProvider) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context, resourcesProvider);
        builder.setTitle(LocaleController.getString("MessageLifetime", NUM));
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
        numberPicker.setFormatter(AlertsCreator$$ExternalSyntheticLambda102.INSTANCE);
        builder.setView(numberPicker);
        builder.setNegativeButton(LocaleController.getString("Done", NUM), new AlertsCreator$$ExternalSyntheticLambda15(encryptedChat, numberPicker));
        return builder;
    }

    static /* synthetic */ String lambda$createTTLAlert$85(int value) {
        if (value == 0) {
            return LocaleController.getString("ShortMessageLifetimeForever", NUM);
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

    static /* synthetic */ void lambda$createTTLAlert$86(TLRPC.EncryptedChat encryptedChat, NumberPicker numberPicker, DialogInterface dialog, int which) {
        int oldValue = encryptedChat.ttl;
        int which2 = numberPicker.getValue();
        if (which2 >= 0 && which2 < 16) {
            encryptedChat.ttl = which2;
        } else if (which2 == 16) {
            encryptedChat.ttl = 30;
        } else if (which2 == 17) {
            encryptedChat.ttl = 60;
        } else if (which2 == 18) {
            encryptedChat.ttl = 3600;
        } else if (which2 == 19) {
            encryptedChat.ttl = 86400;
        } else if (which2 == 20) {
            encryptedChat.ttl = 604800;
        }
        if (oldValue != encryptedChat.ttl) {
            SecretChatHelper.getInstance(UserConfig.selectedAccount).sendTTLMessage(encryptedChat, (TLRPC.Message) null);
            MessagesStorage.getInstance(UserConfig.selectedAccount).updateEncryptedChatTTL(encryptedChat);
        }
    }

    public static AlertDialog createAccountSelectDialog(Activity parentActivity, AccountSelectDelegate delegate) {
        if (UserConfig.getActivatedAccountsCount() < 2) {
            return null;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder((Context) parentActivity);
        Runnable dismissRunnable = builder.getDismissRunnable();
        AlertDialog[] alertDialog = new AlertDialog[1];
        LinearLayout linearLayout = new LinearLayout(parentActivity);
        linearLayout.setOrientation(1);
        for (int a = 0; a < 3; a++) {
            if (UserConfig.getInstance(a).getCurrentUser() != null) {
                AccountSelectCell cell = new AccountSelectCell(parentActivity, false);
                cell.setAccount(a, false);
                cell.setPadding(AndroidUtilities.dp(14.0f), 0, AndroidUtilities.dp(14.0f), 0);
                cell.setBackgroundDrawable(Theme.getSelectorDrawable(false));
                linearLayout.addView(cell, LayoutHelper.createLinear(-1, 50));
                cell.setOnClickListener(new AlertsCreator$$ExternalSyntheticLambda53(alertDialog, dismissRunnable, delegate));
            }
        }
        builder.setTitle(LocaleController.getString("SelectAccount", NUM));
        builder.setView(linearLayout);
        builder.setPositiveButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
        AlertDialog create = builder.create();
        alertDialog[0] = create;
        return create;
    }

    static /* synthetic */ void lambda$createAccountSelectDialog$87(AlertDialog[] alertDialog, Runnable dismissRunnable, AccountSelectDelegate delegate, View v) {
        if (alertDialog[0] != null) {
            alertDialog[0].setOnDismissListener((DialogInterface.OnDismissListener) null);
        }
        dismissRunnable.run();
        delegate.didSelectAccount(((AccountSelectCell) v).getAccountNumber());
    }

    /* JADX WARNING: Removed duplicated region for block: B:103:0x01fa  */
    /* JADX WARNING: Removed duplicated region for block: B:104:0x01fc  */
    /* JADX WARNING: Removed duplicated region for block: B:106:0x01ff  */
    /* JADX WARNING: Removed duplicated region for block: B:210:0x04db  */
    /* JADX WARNING: Removed duplicated region for block: B:253:0x0571  */
    /* JADX WARNING: Removed duplicated region for block: B:320:0x070b  */
    /* JADX WARNING: Removed duplicated region for block: B:321:0x071a  */
    /* JADX WARNING: Removed duplicated region for block: B:324:0x073c A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:333:0x0774 A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:348:0x07db  */
    /* JADX WARNING: Removed duplicated region for block: B:349:0x07e3  */
    /* JADX WARNING: Removed duplicated region for block: B:352:0x080c  */
    /* JADX WARNING: Removed duplicated region for block: B:385:? A[RETURN, SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:65:0x012b  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static void createDeleteMessagesAlert(org.telegram.ui.ActionBar.BaseFragment r59, org.telegram.tgnet.TLRPC.User r60, org.telegram.tgnet.TLRPC.Chat r61, org.telegram.tgnet.TLRPC.EncryptedChat r62, org.telegram.tgnet.TLRPC.ChatFull r63, long r64, org.telegram.messenger.MessageObject r66, android.util.SparseArray<org.telegram.messenger.MessageObject>[] r67, org.telegram.messenger.MessageObject.GroupedMessages r68, boolean r69, int r70, java.lang.Runnable r71, org.telegram.ui.ActionBar.Theme.ResourcesProvider r72) {
        /*
            r15 = r59
            r14 = r60
            r13 = r61
            r12 = r62
            r11 = r66
            r10 = r68
            r9 = r70
            r7 = r72
            if (r15 == 0) goto L_0x0816
            if (r14 != 0) goto L_0x001e
            if (r13 != 0) goto L_0x001e
            if (r12 != 0) goto L_0x001e
            r12 = r13
            r5 = r14
            r8 = r15
            r13 = r7
            goto L_0x081a
        L_0x001e:
            android.app.Activity r8 = r59.getParentActivity()
            if (r8 != 0) goto L_0x0025
            return
        L_0x0025:
            int r6 = r59.getCurrentAccount()
            org.telegram.ui.ActionBar.AlertDialog$Builder r0 = new org.telegram.ui.ActionBar.AlertDialog$Builder
            r0.<init>(r8, r7)
            r5 = r0
            r0 = 1
            r1 = 0
            if (r10 == 0) goto L_0x003b
            java.util.ArrayList<org.telegram.messenger.MessageObject> r2 = r10.messages
            int r2 = r2.size()
            r4 = r2
            goto L_0x004e
        L_0x003b:
            if (r11 == 0) goto L_0x0040
            r2 = 1
            r4 = r2
            goto L_0x004e
        L_0x0040:
            r2 = r67[r1]
            int r2 = r2.size()
            r3 = r67[r0]
            int r3 = r3.size()
            int r2 = r2 + r3
            r4 = r2
        L_0x004e:
            if (r12 == 0) goto L_0x005a
            int r2 = r12.id
            long r2 = (long) r2
            long r2 = org.telegram.messenger.DialogObject.makeEncryptedDialogId(r2)
            r32 = r2
            goto L_0x0066
        L_0x005a:
            if (r14 == 0) goto L_0x0061
            long r2 = r14.id
            r32 = r2
            goto L_0x0066
        L_0x0061:
            long r2 = r13.id
            long r2 = -r2
            r32 = r2
        L_0x0066:
            org.telegram.tgnet.ConnectionsManager r2 = org.telegram.tgnet.ConnectionsManager.getInstance(r6)
            int r34 = r2.getCurrentTime()
            r2 = 0
            r3 = 86400(0x15180, float:1.21072E-40)
            r1 = 2
            if (r11 == 0) goto L_0x008e
            boolean r16 = r66.isDice()
            if (r16 == 0) goto L_0x008a
            org.telegram.tgnet.TLRPC$Message r0 = r11.messageOwner
            int r0 = r0.date
            int r0 = r34 - r0
            int r0 = java.lang.Math.abs(r0)
            if (r0 <= r3) goto L_0x0088
            goto L_0x008a
        L_0x0088:
            r0 = 0
            goto L_0x008b
        L_0x008a:
            r0 = 1
        L_0x008b:
            r35 = r0
            goto L_0x00da
        L_0x008e:
            r0 = 0
        L_0x008f:
            if (r0 >= r1) goto L_0x00d6
            r16 = 0
            r1 = r16
        L_0x0095:
            r16 = r67[r0]
            int r3 = r16.size()
            if (r1 >= r3) goto L_0x00cd
            r3 = r67[r0]
            java.lang.Object r3 = r3.valueAt(r1)
            org.telegram.messenger.MessageObject r3 = (org.telegram.messenger.MessageObject) r3
            boolean r16 = r3.isDice()
            if (r16 == 0) goto L_0x00c4
            r16 = r2
            org.telegram.tgnet.TLRPC$Message r2 = r3.messageOwner
            int r2 = r2.date
            int r2 = r34 - r2
            int r2 = java.lang.Math.abs(r2)
            r18 = r3
            r3 = 86400(0x15180, float:1.21072E-40)
            if (r2 <= r3) goto L_0x00bf
            goto L_0x00cb
        L_0x00bf:
            int r1 = r1 + 1
            r2 = r16
            goto L_0x0095
        L_0x00c4:
            r16 = r2
            r18 = r3
            r3 = 86400(0x15180, float:1.21072E-40)
        L_0x00cb:
            r2 = 1
            goto L_0x00d2
        L_0x00cd:
            r16 = r2
            r3 = 86400(0x15180, float:1.21072E-40)
        L_0x00d2:
            int r0 = r0 + 1
            r1 = 2
            goto L_0x008f
        L_0x00d6:
            r16 = r2
            r35 = r16
        L_0x00da:
            r0 = 3
            boolean[] r3 = new boolean[r0]
            r1 = 1
            boolean[] r2 = new boolean[r1]
            r1 = 0
            r16 = 0
            if (r14 == 0) goto L_0x00ef
            org.telegram.messenger.MessagesController r0 = org.telegram.messenger.MessagesController.getInstance(r6)
            boolean r0 = r0.canRevokePmInbox
            if (r0 == 0) goto L_0x00ef
            r0 = 1
            goto L_0x00f0
        L_0x00ef:
            r0 = 0
        L_0x00f0:
            r36 = r0
            if (r14 == 0) goto L_0x00fb
            org.telegram.messenger.MessagesController r0 = org.telegram.messenger.MessagesController.getInstance(r6)
            int r0 = r0.revokeTimePmLimit
            goto L_0x0101
        L_0x00fb:
            org.telegram.messenger.MessagesController r0 = org.telegram.messenger.MessagesController.getInstance(r6)
            int r0 = r0.revokeTimeLimit
        L_0x0101:
            r18 = 0
            r19 = 0
            r20 = 0
            if (r12 != 0) goto L_0x0116
            if (r14 == 0) goto L_0x0116
            if (r36 == 0) goto L_0x0116
            r21 = r1
            r1 = 2147483647(0x7fffffff, float:NaN)
            if (r0 != r1) goto L_0x0118
            r1 = 1
            goto L_0x0119
        L_0x0116:
            r21 = r1
        L_0x0118:
            r1 = 0
        L_0x0119:
            r37 = r1
            java.lang.String r1 = "DeleteForAll"
            r24 = r2
            java.lang.String r2 = "DeleteMessagesOption"
            r26 = 1098907648(0x41800000, float:16.0)
            r27 = 1090519040(0x41000000, float:8.0)
            r28 = r3
            java.lang.String r3 = ""
            if (r13 == 0) goto L_0x04db
            r29 = r4
            boolean r4 = r13.megagroup
            if (r4 == 0) goto L_0x04c9
            if (r69 != 0) goto L_0x04c9
            boolean r30 = org.telegram.messenger.ChatObject.canBlockUsers(r61)
            r38 = 0
            if (r11 == 0) goto L_0x0206
            org.telegram.tgnet.TLRPC$Message r4 = r11.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r4 = r4.action
            if (r4 == 0) goto L_0x0169
            org.telegram.tgnet.TLRPC$Message r4 = r11.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r4 = r4.action
            boolean r4 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageActionEmpty
            if (r4 != 0) goto L_0x0169
            org.telegram.tgnet.TLRPC$Message r4 = r11.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r4 = r4.action
            boolean r4 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageActionChatDeleteUser
            if (r4 != 0) goto L_0x0169
            org.telegram.tgnet.TLRPC$Message r4 = r11.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r4 = r4.action
            boolean r4 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageActionChatJoinedByLink
            if (r4 != 0) goto L_0x0169
            org.telegram.tgnet.TLRPC$Message r4 = r11.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r4 = r4.action
            boolean r4 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageActionChatAddUser
            if (r4 == 0) goto L_0x0162
            goto L_0x0169
        L_0x0162:
            r40 = r1
            r41 = r2
            r31 = r5
            goto L_0x01ce
        L_0x0169:
            org.telegram.tgnet.TLRPC$Message r4 = r11.messageOwner
            org.telegram.tgnet.TLRPC$Peer r4 = r4.from_id
            r31 = r5
            long r4 = r4.user_id
            int r40 = (r4 > r38 ? 1 : (r4 == r38 ? 0 : -1))
            if (r40 == 0) goto L_0x018c
            org.telegram.messenger.MessagesController r4 = org.telegram.messenger.MessagesController.getInstance(r6)
            org.telegram.tgnet.TLRPC$Message r5 = r11.messageOwner
            org.telegram.tgnet.TLRPC$Peer r5 = r5.from_id
            r40 = r1
            r41 = r2
            long r1 = r5.user_id
            java.lang.Long r1 = java.lang.Long.valueOf(r1)
            org.telegram.tgnet.TLRPC$User r1 = r4.getUser(r1)
            goto L_0x01d0
        L_0x018c:
            r40 = r1
            r41 = r2
            org.telegram.tgnet.TLRPC$Message r1 = r11.messageOwner
            org.telegram.tgnet.TLRPC$Peer r1 = r1.from_id
            long r1 = r1.channel_id
            int r4 = (r1 > r38 ? 1 : (r1 == r38 ? 0 : -1))
            if (r4 == 0) goto L_0x01af
            org.telegram.messenger.MessagesController r1 = org.telegram.messenger.MessagesController.getInstance(r6)
            org.telegram.tgnet.TLRPC$Message r2 = r11.messageOwner
            org.telegram.tgnet.TLRPC$Peer r2 = r2.from_id
            long r4 = r2.channel_id
            java.lang.Long r2 = java.lang.Long.valueOf(r4)
            org.telegram.tgnet.TLRPC$Chat r16 = r1.getChat(r2)
            r1 = r21
            goto L_0x01d0
        L_0x01af:
            org.telegram.tgnet.TLRPC$Message r1 = r11.messageOwner
            org.telegram.tgnet.TLRPC$Peer r1 = r1.from_id
            long r1 = r1.chat_id
            int r4 = (r1 > r38 ? 1 : (r1 == r38 ? 0 : -1))
            if (r4 == 0) goto L_0x01ce
            org.telegram.messenger.MessagesController r1 = org.telegram.messenger.MessagesController.getInstance(r6)
            org.telegram.tgnet.TLRPC$Message r2 = r11.messageOwner
            org.telegram.tgnet.TLRPC$Peer r2 = r2.from_id
            long r4 = r2.chat_id
            java.lang.Long r2 = java.lang.Long.valueOf(r4)
            org.telegram.tgnet.TLRPC$Chat r16 = r1.getChat(r2)
            r1 = r21
            goto L_0x01d0
        L_0x01ce:
            r1 = r21
        L_0x01d0:
            boolean r2 = r66.isSendError()
            if (r2 != 0) goto L_0x01fc
            long r4 = r66.getDialogId()
            int r2 = (r4 > r64 ? 1 : (r4 == r64 ? 0 : -1))
            if (r2 != 0) goto L_0x01fc
            org.telegram.tgnet.TLRPC$Message r2 = r11.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r2 = r2.action
            if (r2 == 0) goto L_0x01ec
            org.telegram.tgnet.TLRPC$Message r2 = r11.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r2 = r2.action
            boolean r2 = r2 instanceof org.telegram.tgnet.TLRPC.TL_messageActionEmpty
            if (r2 == 0) goto L_0x01fc
        L_0x01ec:
            boolean r2 = r66.isOut()
            if (r2 == 0) goto L_0x01fc
            org.telegram.tgnet.TLRPC$Message r2 = r11.messageOwner
            int r2 = r2.date
            int r2 = r34 - r2
            if (r2 > r0) goto L_0x01fc
            r2 = 1
            goto L_0x01fd
        L_0x01fc:
            r2 = 0
        L_0x01fd:
            if (r2 == 0) goto L_0x0201
            int r20 = r20 + 1
        L_0x0201:
            r10 = r1
            r7 = r16
            goto L_0x029d
        L_0x0206:
            r40 = r1
            r41 = r2
            r31 = r5
            r1 = -1
            r4 = 1
        L_0x020f:
            r42 = -1
            if (r4 < 0) goto L_0x024e
            r44 = 0
            r5 = 0
        L_0x0216:
            r46 = r67[r4]
            int r7 = r46.size()
            if (r5 >= r7) goto L_0x0242
            r7 = r67[r4]
            java.lang.Object r7 = r7.valueAt(r5)
            org.telegram.messenger.MessageObject r7 = (org.telegram.messenger.MessageObject) r7
            int r46 = (r1 > r42 ? 1 : (r1 == r42 ? 0 : -1))
            if (r46 != 0) goto L_0x022e
            long r1 = r7.getFromChatId()
        L_0x022e:
            int r46 = (r1 > r38 ? 1 : (r1 == r38 ? 0 : -1))
            if (r46 < 0) goto L_0x0240
            long r46 = r7.getSenderId()
            int r48 = (r1 > r46 ? 1 : (r1 == r46 ? 0 : -1))
            if (r48 == 0) goto L_0x023b
            goto L_0x0240
        L_0x023b:
            int r5 = r5 + 1
            r7 = r72
            goto L_0x0216
        L_0x0240:
            r1 = -2
        L_0x0242:
            r46 = -2
            int r5 = (r1 > r46 ? 1 : (r1 == r46 ? 0 : -1))
            if (r5 != 0) goto L_0x0249
            goto L_0x024e
        L_0x0249:
            int r4 = r4 + -1
            r7 = r72
            goto L_0x020f
        L_0x024e:
            r4 = 1
        L_0x024f:
            if (r4 < 0) goto L_0x0285
            r5 = 0
        L_0x0252:
            r7 = r67[r4]
            int r7 = r7.size()
            if (r5 >= r7) goto L_0x0280
            r7 = r67[r4]
            java.lang.Object r7 = r7.valueAt(r5)
            org.telegram.messenger.MessageObject r7 = (org.telegram.messenger.MessageObject) r7
            r10 = 1
            if (r4 != r10) goto L_0x027b
            boolean r10 = r7.isOut()
            if (r10 == 0) goto L_0x027b
            org.telegram.tgnet.TLRPC$Message r10 = r7.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r10 = r10.action
            if (r10 != 0) goto L_0x027b
            org.telegram.tgnet.TLRPC$Message r10 = r7.messageOwner
            int r10 = r10.date
            int r10 = r34 - r10
            if (r10 > r0) goto L_0x027b
            int r20 = r20 + 1
        L_0x027b:
            int r5 = r5 + 1
            r10 = r68
            goto L_0x0252
        L_0x0280:
            int r4 = r4 + -1
            r10 = r68
            goto L_0x024f
        L_0x0285:
            int r4 = (r1 > r42 ? 1 : (r1 == r42 ? 0 : -1))
            if (r4 == 0) goto L_0x0299
            org.telegram.messenger.MessagesController r4 = org.telegram.messenger.MessagesController.getInstance(r6)
            java.lang.Long r5 = java.lang.Long.valueOf(r1)
            org.telegram.tgnet.TLRPC$User r4 = r4.getUser(r5)
            r10 = r4
            r7 = r16
            goto L_0x029d
        L_0x0299:
            r7 = r16
            r10 = r21
        L_0x029d:
            if (r10 == 0) goto L_0x02ad
            long r1 = r10.id
            org.telegram.messenger.UserConfig r4 = org.telegram.messenger.UserConfig.getInstance(r6)
            long r4 = r4.getClientUserId()
            int r16 = (r1 > r4 ? 1 : (r1 == r4 ? 0 : -1))
            if (r16 != 0) goto L_0x02b5
        L_0x02ad:
            if (r7 == 0) goto L_0x041f
            boolean r1 = org.telegram.messenger.ChatObject.hasAdminRights(r7)
            if (r1 != 0) goto L_0x041f
        L_0x02b5:
            r1 = 1
            if (r9 != r1) goto L_0x0332
            boolean r2 = r13.creator
            if (r2 != 0) goto L_0x0332
            if (r10 == 0) goto L_0x0332
            org.telegram.ui.ActionBar.AlertDialog[] r1 = new org.telegram.ui.ActionBar.AlertDialog[r1]
            org.telegram.ui.ActionBar.AlertDialog r2 = new org.telegram.ui.ActionBar.AlertDialog
            r3 = 3
            r2.<init>(r8, r3)
            r3 = 0
            r1[r3] = r2
            r5 = r1
            org.telegram.tgnet.TLRPC$TL_channels_getParticipant r1 = new org.telegram.tgnet.TLRPC$TL_channels_getParticipant
            r1.<init>()
            r4 = r1
            org.telegram.tgnet.TLRPC$InputChannel r1 = org.telegram.messenger.MessagesController.getInputChannel((org.telegram.tgnet.TLRPC.Chat) r61)
            r4.channel = r1
            org.telegram.tgnet.TLRPC$InputPeer r1 = org.telegram.messenger.MessagesController.getInputPeer((org.telegram.tgnet.TLRPC.User) r10)
            r4.participant = r1
            org.telegram.tgnet.ConnectionsManager r3 = org.telegram.tgnet.ConnectionsManager.getInstance(r6)
            org.telegram.ui.Components.AlertsCreator$$ExternalSyntheticLambda84 r2 = new org.telegram.ui.Components.AlertsCreator$$ExternalSyntheticLambda84
            r1 = r0
            r0 = r2
            r49 = r1
            r1 = r5
            r15 = r2
            r50 = r24
            r2 = r59
            r16 = r15
            r51 = r28
            r15 = r3
            r3 = r60
            r17 = r15
            r52 = r29
            r15 = r4
            r4 = r61
            r54 = r5
            r53 = r31
            r5 = r62
            r55 = r6
            r6 = r63
            r57 = r7
            r56 = r8
            r7 = r64
            r9 = r66
            r58 = r10
            r10 = r67
            r11 = r68
            r12 = r69
            r13 = r71
            r14 = r72
            r0.<init>(r1, r2, r3, r4, r5, r6, r7, r9, r10, r11, r12, r13, r14)
            r1 = r16
            r0 = r17
            int r0 = r0.sendRequest(r15, r1)
            org.telegram.ui.Components.AlertsCreator$$ExternalSyntheticLambda78 r1 = new org.telegram.ui.Components.AlertsCreator$$ExternalSyntheticLambda78
            r3 = r54
            r4 = r55
            r1.<init>(r3, r4, r0, r2)
            r5 = 1000(0x3e8, double:4.94E-321)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r1, r5)
            return
        L_0x0332:
            r49 = r0
            r4 = r6
            r57 = r7
            r56 = r8
            r58 = r10
            r2 = r15
            r50 = r24
            r51 = r28
            r52 = r29
            r53 = r31
            android.widget.FrameLayout r0 = new android.widget.FrameLayout
            r1 = r56
            r0.<init>(r1)
            r5 = 0
            r6 = r58
            if (r6 == 0) goto L_0x035c
            java.lang.String r7 = r6.first_name
            java.lang.String r8 = r6.last_name
            java.lang.String r7 = org.telegram.messenger.ContactsController.formatName(r7, r8)
            r8 = r7
            r7 = r57
            goto L_0x0360
        L_0x035c:
            r7 = r57
            java.lang.String r8 = r7.title
        L_0x0360:
            r9 = 0
        L_0x0361:
            r10 = 3
            if (r9 >= r10) goto L_0x040c
            r11 = r70
            r12 = 2
            if (r11 == r12) goto L_0x036b
            if (r30 != 0) goto L_0x0375
        L_0x036b:
            if (r9 != 0) goto L_0x0375
            r13 = r72
            r58 = r6
            r10 = r51
            goto L_0x0404
        L_0x0375:
            org.telegram.ui.Cells.CheckBoxCell r12 = new org.telegram.ui.Cells.CheckBoxCell
            r13 = r72
            r14 = 1
            r12.<init>(r1, r14, r13)
            r14 = 0
            android.graphics.drawable.Drawable r15 = org.telegram.ui.ActionBar.Theme.getSelectorDrawable(r14)
            r12.setBackgroundDrawable(r15)
            java.lang.Integer r15 = java.lang.Integer.valueOf(r9)
            r12.setTag(r15)
            if (r9 != 0) goto L_0x039d
            r15 = 2131625210(0x7f0e04fa, float:1.8877622E38)
            java.lang.String r10 = "DeleteBanUser"
            java.lang.String r10 = org.telegram.messenger.LocaleController.getString(r10, r15)
            r12.setText(r10, r3, r14, r14)
            r58 = r6
            goto L_0x03c2
        L_0x039d:
            r10 = 1
            if (r9 != r10) goto L_0x03af
            r15 = 2131625247(0x7f0e051f, float:1.8877697E38)
            java.lang.String r10 = "DeleteReportSpam"
            java.lang.String r10 = org.telegram.messenger.LocaleController.getString(r10, r15)
            r12.setText(r10, r3, r14, r14)
            r58 = r6
            goto L_0x03c2
        L_0x03af:
            r15 = 1
            java.lang.Object[] r10 = new java.lang.Object[r15]
            r10[r14] = r8
            java.lang.String r15 = "DeleteAllFrom"
            r58 = r6
            r6 = 2131625196(0x7f0e04ec, float:1.8877593E38)
            java.lang.String r6 = org.telegram.messenger.LocaleController.formatString(r15, r6, r10)
            r12.setText(r6, r3, r14, r14)
        L_0x03c2:
            boolean r6 = org.telegram.messenger.LocaleController.isRTL
            if (r6 == 0) goto L_0x03cb
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r26)
            goto L_0x03cf
        L_0x03cb:
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r27)
        L_0x03cf:
            boolean r10 = org.telegram.messenger.LocaleController.isRTL
            if (r10 == 0) goto L_0x03d8
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r27)
            goto L_0x03dc
        L_0x03d8:
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r26)
        L_0x03dc:
            r14 = 0
            r12.setPadding(r6, r14, r10, r14)
            r38 = -1
            r39 = 1111490560(0x42400000, float:48.0)
            r40 = 51
            r41 = 0
            int r6 = r5 * 48
            float r6 = (float) r6
            r43 = 0
            r44 = 0
            r42 = r6
            android.widget.FrameLayout$LayoutParams r6 = org.telegram.ui.Components.LayoutHelper.createFrame(r38, r39, r40, r41, r42, r43, r44)
            r0.addView(r12, r6)
            org.telegram.ui.Components.AlertsCreator$$ExternalSyntheticLambda58 r6 = new org.telegram.ui.Components.AlertsCreator$$ExternalSyntheticLambda58
            r10 = r51
            r6.<init>(r10)
            r12.setOnClickListener(r6)
            int r5 = r5 + 1
        L_0x0404:
            int r9 = r9 + 1
            r51 = r10
            r6 = r58
            goto L_0x0361
        L_0x040c:
            r11 = r70
            r13 = r72
            r58 = r6
            r10 = r51
            r6 = r53
            r6.setView(r0)
            r12 = r61
            r9 = r50
            goto L_0x04b9
        L_0x041f:
            r13 = r72
            r49 = r0
            r4 = r6
            r1 = r8
            r11 = r9
            r58 = r10
            r2 = r15
            r50 = r24
            r10 = r28
            r52 = r29
            r6 = r31
            if (r19 != 0) goto L_0x04b2
            if (r20 <= 0) goto L_0x04b2
            if (r35 == 0) goto L_0x04b2
            r0 = 1
            android.widget.FrameLayout r5 = new android.widget.FrameLayout
            r5.<init>(r1)
            org.telegram.ui.Cells.CheckBoxCell r8 = new org.telegram.ui.Cells.CheckBoxCell
            r9 = 1
            r8.<init>(r1, r9, r13)
            r9 = 0
            android.graphics.drawable.Drawable r12 = org.telegram.ui.ActionBar.Theme.getSelectorDrawable(r9)
            r8.setBackgroundDrawable(r12)
            r12 = r61
            if (r12 == 0) goto L_0x045e
            if (r19 == 0) goto L_0x045e
            r15 = r40
            r14 = 2131625225(0x7f0e0509, float:1.8877652E38)
            java.lang.String r14 = org.telegram.messenger.LocaleController.getString(r15, r14)
            r8.setText(r14, r3, r9, r9)
            goto L_0x046a
        L_0x045e:
            r15 = r41
            r14 = 2131625238(0x7f0e0516, float:1.8877678E38)
            java.lang.String r14 = org.telegram.messenger.LocaleController.getString(r15, r14)
            r8.setText(r14, r3, r9, r9)
        L_0x046a:
            boolean r3 = org.telegram.messenger.LocaleController.isRTL
            if (r3 == 0) goto L_0x0473
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r26)
            goto L_0x0477
        L_0x0473:
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r27)
        L_0x0477:
            boolean r9 = org.telegram.messenger.LocaleController.isRTL
            if (r9 == 0) goto L_0x0480
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r27)
            goto L_0x0484
        L_0x0480:
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r26)
        L_0x0484:
            r14 = 0
            r8.setPadding(r3, r14, r9, r14)
            r23 = -1
            r24 = 1111490560(0x42400000, float:48.0)
            r25 = 51
            r26 = 0
            r27 = 0
            r28 = 0
            r29 = 0
            android.widget.FrameLayout$LayoutParams r3 = org.telegram.ui.Components.LayoutHelper.createFrame(r23, r24, r25, r26, r27, r28, r29)
            r5.addView(r8, r3)
            org.telegram.ui.Components.AlertsCreator$$ExternalSyntheticLambda59 r3 = new org.telegram.ui.Components.AlertsCreator$$ExternalSyntheticLambda59
            r9 = r50
            r3.<init>(r9)
            r8.setOnClickListener(r3)
            r6.setView(r5)
            r3 = 9
            r6.setCustomViewOffset(r3)
            r18 = r0
            goto L_0x04b9
        L_0x04b2:
            r12 = r61
            r9 = r50
            r0 = 0
            r58 = r0
        L_0x04b9:
            r5 = r60
            r56 = r1
            r0 = r18
            r2 = r19
            r3 = r20
            r14 = r49
            r1 = r52
            goto L_0x06d7
        L_0x04c9:
            r49 = r0
            r0 = r2
            r4 = r6
            r11 = r9
            r12 = r13
            r2 = r15
            r9 = r24
            r10 = r28
            r52 = r29
            r15 = r1
            r6 = r5
            r13 = r7
            r1 = r8
            goto L_0x04ec
        L_0x04db:
            r49 = r0
            r0 = r2
            r52 = r4
            r4 = r6
            r11 = r9
            r12 = r13
            r2 = r15
            r9 = r24
            r10 = r28
            r15 = r1
            r6 = r5
            r13 = r7
            r1 = r8
        L_0x04ec:
            if (r69 != 0) goto L_0x06c5
            boolean r5 = org.telegram.messenger.ChatObject.isChannel(r61)
            if (r5 != 0) goto L_0x06c5
            if (r62 != 0) goto L_0x06c5
            r5 = r60
            if (r5 == 0) goto L_0x0510
            long r7 = r5.id
            org.telegram.messenger.UserConfig r14 = org.telegram.messenger.UserConfig.getInstance(r4)
            long r28 = r14.getClientUserId()
            int r14 = (r7 > r28 ? 1 : (r7 == r28 ? 0 : -1))
            if (r14 == 0) goto L_0x0510
            boolean r7 = r5.bot
            if (r7 == 0) goto L_0x0512
            boolean r7 = r5.support
            if (r7 != 0) goto L_0x0512
        L_0x0510:
            if (r12 == 0) goto L_0x05f8
        L_0x0512:
            r7 = r66
            if (r7 == 0) goto L_0x0581
            boolean r8 = r66.isSendError()
            if (r8 != 0) goto L_0x056c
            org.telegram.tgnet.TLRPC$Message r8 = r7.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r8 = r8.action
            if (r8 == 0) goto L_0x054e
            org.telegram.tgnet.TLRPC$Message r8 = r7.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r8 = r8.action
            boolean r8 = r8 instanceof org.telegram.tgnet.TLRPC.TL_messageActionEmpty
            if (r8 != 0) goto L_0x054e
            org.telegram.tgnet.TLRPC$Message r8 = r7.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r8 = r8.action
            boolean r8 = r8 instanceof org.telegram.tgnet.TLRPC.TL_messageActionPhoneCall
            if (r8 != 0) goto L_0x054e
            org.telegram.tgnet.TLRPC$Message r8 = r7.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r8 = r8.action
            boolean r8 = r8 instanceof org.telegram.tgnet.TLRPC.TL_messageActionPinMessage
            if (r8 != 0) goto L_0x054e
            org.telegram.tgnet.TLRPC$Message r8 = r7.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r8 = r8.action
            boolean r8 = r8 instanceof org.telegram.tgnet.TLRPC.TL_messageActionGeoProximityReached
            if (r8 != 0) goto L_0x054e
            org.telegram.tgnet.TLRPC$Message r8 = r7.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r8 = r8.action
            boolean r8 = r8 instanceof org.telegram.tgnet.TLRPC.TL_messageActionSetChatTheme
            if (r8 == 0) goto L_0x054b
            goto L_0x054e
        L_0x054b:
            r14 = r49
            goto L_0x056e
        L_0x054e:
            boolean r8 = r66.isOut()
            if (r8 != 0) goto L_0x0560
            if (r36 != 0) goto L_0x0560
            boolean r8 = org.telegram.messenger.ChatObject.hasAdminRights(r61)
            if (r8 == 0) goto L_0x055d
            goto L_0x0560
        L_0x055d:
            r14 = r49
            goto L_0x056e
        L_0x0560:
            org.telegram.tgnet.TLRPC$Message r8 = r7.messageOwner
            int r8 = r8.date
            int r8 = r34 - r8
            r14 = r49
            if (r8 > r14) goto L_0x056e
            r8 = 1
            goto L_0x056f
        L_0x056c:
            r14 = r49
        L_0x056e:
            r8 = 0
        L_0x056f:
            if (r8 == 0) goto L_0x0573
            int r20 = r20 + 1
        L_0x0573:
            boolean r17 = r66.isOut()
            r24 = 1
            r17 = r17 ^ 1
            r19 = r17
            r2 = r20
            goto L_0x05fc
        L_0x0581:
            r14 = r49
            r8 = 1
        L_0x0584:
            if (r8 < 0) goto L_0x05f5
            r17 = 0
            r7 = r17
        L_0x058a:
            r17 = r67[r8]
            int r11 = r17.size()
            if (r7 >= r11) goto L_0x05ec
            r11 = r67[r8]
            java.lang.Object r11 = r11.valueAt(r7)
            org.telegram.messenger.MessageObject r11 = (org.telegram.messenger.MessageObject) r11
            org.telegram.tgnet.TLRPC$Message r2 = r11.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r2 = r2.action
            if (r2 == 0) goto L_0x05c1
            org.telegram.tgnet.TLRPC$Message r2 = r11.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r2 = r2.action
            boolean r2 = r2 instanceof org.telegram.tgnet.TLRPC.TL_messageActionEmpty
            if (r2 != 0) goto L_0x05c1
            org.telegram.tgnet.TLRPC$Message r2 = r11.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r2 = r2.action
            boolean r2 = r2 instanceof org.telegram.tgnet.TLRPC.TL_messageActionPhoneCall
            if (r2 != 0) goto L_0x05c1
            org.telegram.tgnet.TLRPC$Message r2 = r11.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r2 = r2.action
            boolean r2 = r2 instanceof org.telegram.tgnet.TLRPC.TL_messageActionPinMessage
            if (r2 != 0) goto L_0x05c1
            org.telegram.tgnet.TLRPC$Message r2 = r11.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r2 = r2.action
            boolean r2 = r2 instanceof org.telegram.tgnet.TLRPC.TL_messageActionGeoProximityReached
            if (r2 != 0) goto L_0x05c1
            goto L_0x05e5
        L_0x05c1:
            boolean r2 = r11.isOut()
            if (r2 != 0) goto L_0x05d1
            if (r36 != 0) goto L_0x05d1
            if (r12 == 0) goto L_0x05e5
            boolean r2 = org.telegram.messenger.ChatObject.canBlockUsers(r61)
            if (r2 == 0) goto L_0x05e5
        L_0x05d1:
            org.telegram.tgnet.TLRPC$Message r2 = r11.messageOwner
            int r2 = r2.date
            int r2 = r34 - r2
            if (r2 > r14) goto L_0x05e5
            int r20 = r20 + 1
            if (r19 != 0) goto L_0x05e5
            boolean r2 = r11.isOut()
            if (r2 != 0) goto L_0x05e5
            r19 = 1
        L_0x05e5:
            int r7 = r7 + 1
            r2 = r59
            r11 = r70
            goto L_0x058a
        L_0x05ec:
            int r8 = r8 + -1
            r2 = r59
            r7 = r66
            r11 = r70
            goto L_0x0584
        L_0x05f5:
            r2 = r20
            goto L_0x05fc
        L_0x05f8:
            r14 = r49
            r2 = r20
        L_0x05fc:
            if (r2 <= 0) goto L_0x06b7
            if (r35 == 0) goto L_0x06b7
            if (r5 == 0) goto L_0x060f
            boolean r7 = org.telegram.messenger.UserObject.isDeleted(r60)
            if (r7 != 0) goto L_0x0609
            goto L_0x060f
        L_0x0609:
            r56 = r1
            r1 = r52
            goto L_0x06bb
        L_0x060f:
            r18 = 1
            android.widget.FrameLayout r7 = new android.widget.FrameLayout
            r7.<init>(r1)
            org.telegram.ui.Cells.CheckBoxCell r8 = new org.telegram.ui.Cells.CheckBoxCell
            r11 = 1
            r8.<init>(r1, r11, r13)
            r17 = 0
            android.graphics.drawable.Drawable r11 = org.telegram.ui.ActionBar.Theme.getSelectorDrawable(r17)
            r8.setBackgroundDrawable(r11)
            if (r37 == 0) goto L_0x0642
            r11 = 1
            java.lang.Object[] r15 = new java.lang.Object[r11]
            java.lang.String r11 = org.telegram.messenger.UserObject.getFirstName(r60)
            r0 = 0
            r15[r0] = r11
            java.lang.String r11 = "DeleteMessagesOptionAlso"
            r56 = r1
            r1 = 2131625239(0x7f0e0517, float:1.887768E38)
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r11, r1, r15)
            r8.setText(r1, r3, r0, r0)
            r1 = r52
            goto L_0x066a
        L_0x0642:
            r56 = r1
            if (r12 == 0) goto L_0x065d
            if (r19 != 0) goto L_0x064f
            r1 = r52
            if (r2 != r1) goto L_0x064d
            goto L_0x0651
        L_0x064d:
            r11 = 0
            goto L_0x0660
        L_0x064f:
            r1 = r52
        L_0x0651:
            r0 = 2131625225(0x7f0e0509, float:1.8877652E38)
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r15, r0)
            r11 = 0
            r8.setText(r0, r3, r11, r11)
            goto L_0x066a
        L_0x065d:
            r1 = r52
            r11 = 0
        L_0x0660:
            r15 = 2131625238(0x7f0e0516, float:1.8877678E38)
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r0, r15)
            r8.setText(r0, r3, r11, r11)
        L_0x066a:
            boolean r0 = org.telegram.messenger.LocaleController.isRTL
            if (r0 == 0) goto L_0x0673
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r26)
            goto L_0x0677
        L_0x0673:
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r27)
        L_0x0677:
            boolean r3 = org.telegram.messenger.LocaleController.isRTL
            if (r3 == 0) goto L_0x0680
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r27)
            goto L_0x0684
        L_0x0680:
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r26)
        L_0x0684:
            r11 = 0
            r8.setPadding(r0, r11, r3, r11)
            r23 = -1
            r24 = 1111490560(0x42400000, float:48.0)
            r25 = 51
            r26 = 0
            r27 = 0
            r28 = 0
            r29 = 0
            android.widget.FrameLayout$LayoutParams r0 = org.telegram.ui.Components.LayoutHelper.createFrame(r23, r24, r25, r26, r27, r28, r29)
            r7.addView(r8, r0)
            org.telegram.ui.Components.AlertsCreator$$ExternalSyntheticLambda60 r0 = new org.telegram.ui.Components.AlertsCreator$$ExternalSyntheticLambda60
            r0.<init>(r9)
            r8.setOnClickListener(r0)
            r6.setView(r7)
            r0 = 9
            r6.setCustomViewOffset(r0)
            r3 = r2
            r7 = r16
            r0 = r18
            r2 = r19
            r58 = r21
            goto L_0x06d7
        L_0x06b7:
            r56 = r1
            r1 = r52
        L_0x06bb:
            r3 = r2
            r7 = r16
            r0 = r18
            r2 = r19
            r58 = r21
            goto L_0x06d7
        L_0x06c5:
            r5 = r60
            r56 = r1
            r14 = r49
            r1 = r52
            r7 = r16
            r0 = r18
            r2 = r19
            r3 = r20
            r58 = r21
        L_0x06d7:
            r26 = r58
            r27 = r7
            r8 = 2131625188(0x7f0e04e4, float:1.8877577E38)
            java.lang.String r11 = "Delete"
            java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r11, r8)
            org.telegram.ui.Components.AlertsCreator$$ExternalSyntheticLambda11 r11 = new org.telegram.ui.Components.AlertsCreator$$ExternalSyntheticLambda11
            r16 = r11
            r17 = r66
            r18 = r68
            r19 = r62
            r20 = r4
            r21 = r32
            r23 = r9
            r24 = r69
            r25 = r67
            r28 = r10
            r29 = r61
            r30 = r63
            r31 = r71
            r16.<init>(r17, r18, r19, r20, r21, r23, r24, r25, r26, r27, r28, r29, r30, r31)
            r6.setPositiveButton(r8, r11)
            java.lang.String r8 = "messages"
            r11 = 1
            if (r1 != r11) goto L_0x071a
            r15 = 2131625249(0x7f0e0521, float:1.88777E38)
            java.lang.String r11 = "DeleteSingleMessagesTitle"
            java.lang.String r11 = org.telegram.messenger.LocaleController.getString(r11, r15)
            r6.setTitle(r11)
            r55 = r4
            goto L_0x0733
        L_0x071a:
            r15 = 1
            java.lang.Object[] r11 = new java.lang.Object[r15]
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatPluralString(r8, r1)
            r17 = 0
            r11[r17] = r15
            java.lang.String r15 = "DeleteMessagesTitle"
            r55 = r4
            r4 = 2131625243(0x7f0e051b, float:1.8877688E38)
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatString(r15, r4, r11)
            r6.setTitle(r4)
        L_0x0733:
            r4 = 2131624372(0x7f0e01b4, float:1.8875922E38)
            java.lang.String r11 = "AreYouSureDeleteSingleMessage"
            java.lang.String r15 = "AreYouSureDeleteFewMessages"
            if (r12 == 0) goto L_0x0772
            if (r2 == 0) goto L_0x0772
            if (r0 == 0) goto L_0x075a
            if (r3 == r1) goto L_0x075a
            r4 = 2131625242(0x7f0e051a, float:1.8877686E38)
            r11 = 1
            java.lang.Object[] r11 = new java.lang.Object[r11]
            java.lang.String r8 = org.telegram.messenger.LocaleController.formatPluralString(r8, r3)
            r15 = 0
            r11[r15] = r8
            java.lang.String r8 = "DeleteMessagesTextGroupPart"
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatString(r8, r4, r11)
            r6.setMessage(r4)
            goto L_0x07ed
        L_0x075a:
            r8 = 1
            if (r1 != r8) goto L_0x0766
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r11, r4)
            r6.setMessage(r4)
            goto L_0x07ed
        L_0x0766:
            r4 = 2131624364(0x7f0e01ac, float:1.8875906E38)
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r15, r4)
            r6.setMessage(r4)
            goto L_0x07ed
        L_0x0772:
            if (r0 == 0) goto L_0x07b3
            if (r37 != 0) goto L_0x07b3
            if (r3 == r1) goto L_0x07b3
            if (r12 == 0) goto L_0x0791
            r4 = 2131625241(0x7f0e0519, float:1.8877684E38)
            r11 = 1
            java.lang.Object[] r11 = new java.lang.Object[r11]
            java.lang.String r8 = org.telegram.messenger.LocaleController.formatPluralString(r8, r3)
            r15 = 0
            r11[r15] = r8
            java.lang.String r8 = "DeleteMessagesTextGroup"
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatString(r8, r4, r11)
            r6.setMessage(r4)
            goto L_0x07ed
        L_0x0791:
            r15 = 0
            r4 = 2131625240(0x7f0e0518, float:1.8877682E38)
            r11 = 2
            java.lang.Object[] r11 = new java.lang.Object[r11]
            java.lang.String r8 = org.telegram.messenger.LocaleController.formatPluralString(r8, r3)
            r11[r15] = r8
            java.lang.String r8 = org.telegram.messenger.UserObject.getFirstName(r60)
            r15 = 1
            r11[r15] = r8
            java.lang.String r8 = "DeleteMessagesText"
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatString(r8, r4, r11)
            android.text.SpannableStringBuilder r4 = org.telegram.messenger.AndroidUtilities.replaceTags(r4)
            r6.setMessage(r4)
            goto L_0x07ed
        L_0x07b3:
            if (r12 == 0) goto L_0x07d8
            boolean r8 = r12.megagroup
            if (r8 == 0) goto L_0x07d8
            if (r69 != 0) goto L_0x07d8
            r4 = 1
            if (r1 != r4) goto L_0x07cb
            r4 = 2131624373(0x7f0e01b5, float:1.8875924E38)
            java.lang.String r8 = "AreYouSureDeleteSingleMessageMega"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r8, r4)
            r6.setMessage(r4)
            goto L_0x07ed
        L_0x07cb:
            r4 = 2131624365(0x7f0e01ad, float:1.8875908E38)
            java.lang.String r8 = "AreYouSureDeleteFewMessagesMega"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r8, r4)
            r6.setMessage(r4)
            goto L_0x07ed
        L_0x07d8:
            r8 = 1
            if (r1 != r8) goto L_0x07e3
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r11, r4)
            r6.setMessage(r4)
            goto L_0x07ed
        L_0x07e3:
            r4 = 2131624364(0x7f0e01ac, float:1.8875906E38)
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r15, r4)
            r6.setMessage(r4)
        L_0x07ed:
            r4 = 2131624692(0x7f0e02f4, float:1.887657E38)
            java.lang.String r8 = "Cancel"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r8, r4)
            r8 = 0
            r6.setNegativeButton(r4, r8)
            org.telegram.ui.ActionBar.AlertDialog r4 = r6.create()
            r8 = r59
            r8.showDialog(r4)
            r11 = -1
            android.view.View r11 = r4.getButton(r11)
            android.widget.TextView r11 = (android.widget.TextView) r11
            if (r11 == 0) goto L_0x0815
            java.lang.String r15 = "dialogTextRed2"
            int r15 = org.telegram.ui.ActionBar.Theme.getColor(r15)
            r11.setTextColor(r15)
        L_0x0815:
            return
        L_0x0816:
            r12 = r13
            r5 = r14
            r8 = r15
            r13 = r7
        L_0x081a:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.AlertsCreator.createDeleteMessagesAlert(org.telegram.ui.ActionBar.BaseFragment, org.telegram.tgnet.TLRPC$User, org.telegram.tgnet.TLRPC$Chat, org.telegram.tgnet.TLRPC$EncryptedChat, org.telegram.tgnet.TLRPC$ChatFull, long, org.telegram.messenger.MessageObject, android.util.SparseArray[], org.telegram.messenger.MessageObject$GroupedMessages, boolean, int, java.lang.Runnable, org.telegram.ui.ActionBar.Theme$ResourcesProvider):void");
    }

    static /* synthetic */ void lambda$createDeleteMessagesAlert$88(AlertDialog[] progressDialog, TLObject response, TLRPC.TL_error error, BaseFragment fragment, TLRPC.User user, TLRPC.Chat chat, TLRPC.EncryptedChat encryptedChat, TLRPC.ChatFull chatInfo, long mergeDialogId, MessageObject selectedMessage, SparseArray[] selectedMessages, MessageObject.GroupedMessages selectedGroup, boolean scheduled, Runnable onDelete, Theme.ResourcesProvider resourcesProvider) {
        TLRPC.TL_error tL_error = error;
        try {
            progressDialog[0].dismiss();
        } catch (Throwable th) {
        }
        progressDialog[0] = null;
        int loadType = 2;
        if (response != null) {
            TLRPC.TL_channels_channelParticipant participant = (TLRPC.TL_channels_channelParticipant) response;
            if (!(participant.participant instanceof TLRPC.TL_channelParticipantAdmin) && !(participant.participant instanceof TLRPC.TL_channelParticipantCreator)) {
                loadType = 0;
            }
        } else if (tL_error != null && "USER_NOT_PARTICIPANT".equals(tL_error.text)) {
            loadType = 0;
        }
        createDeleteMessagesAlert(fragment, user, chat, encryptedChat, chatInfo, mergeDialogId, selectedMessage, selectedMessages, selectedGroup, scheduled, loadType, onDelete, resourcesProvider);
    }

    static /* synthetic */ void lambda$createDeleteMessagesAlert$91(AlertDialog[] progressDialog, int currentAccount, int requestId, BaseFragment fragment) {
        if (progressDialog[0] != null) {
            progressDialog[0].setOnCancelListener(new AlertsCreator$$ExternalSyntheticLambda0(currentAccount, requestId));
            fragment.showDialog(progressDialog[0]);
        }
    }

    static /* synthetic */ void lambda$createDeleteMessagesAlert$92(boolean[] checks, View v) {
        if (v.isEnabled()) {
            CheckBoxCell cell13 = (CheckBoxCell) v;
            Integer num1 = (Integer) cell13.getTag();
            checks[num1.intValue()] = !checks[num1.intValue()];
            cell13.setChecked(checks[num1.intValue()], true);
        }
    }

    static /* synthetic */ void lambda$createDeleteMessagesAlert$93(boolean[] deleteForAll, View v) {
        deleteForAll[0] = !deleteForAll[0];
        ((CheckBoxCell) v).setChecked(deleteForAll[0], true);
    }

    static /* synthetic */ void lambda$createDeleteMessagesAlert$94(boolean[] deleteForAll, View v) {
        deleteForAll[0] = !deleteForAll[0];
        ((CheckBoxCell) v).setChecked(deleteForAll[0], true);
    }

    /* JADX WARNING: Removed duplicated region for block: B:40:0x00f7  */
    /* JADX WARNING: Removed duplicated region for block: B:75:0x012d A[SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    static /* synthetic */ void lambda$createDeleteMessagesAlert$96(org.telegram.messenger.MessageObject r22, org.telegram.messenger.MessageObject.GroupedMessages r23, org.telegram.tgnet.TLRPC.EncryptedChat r24, int r25, long r26, boolean[] r28, boolean r29, android.util.SparseArray[] r30, org.telegram.tgnet.TLRPC.User r31, org.telegram.tgnet.TLRPC.Chat r32, boolean[] r33, org.telegram.tgnet.TLRPC.Chat r34, org.telegram.tgnet.TLRPC.ChatFull r35, java.lang.Runnable r36, android.content.DialogInterface r37, int r38) {
        /*
            r0 = r22
            r1 = r23
            r10 = r31
            r11 = r32
            r12 = r34
            r2 = 0
            r13 = 10
            r14 = 0
            r9 = 0
            if (r0 == 0) goto L_0x00a1
            java.util.ArrayList r3 = new java.util.ArrayList
            r3.<init>()
            r8 = r3
            r2 = 0
            if (r1 == 0) goto L_0x005d
            r3 = 0
        L_0x001c:
            java.util.ArrayList<org.telegram.messenger.MessageObject> r4 = r1.messages
            int r4 = r4.size()
            if (r3 >= r4) goto L_0x005b
            java.util.ArrayList<org.telegram.messenger.MessageObject> r4 = r1.messages
            java.lang.Object r4 = r4.get(r3)
            org.telegram.messenger.MessageObject r4 = (org.telegram.messenger.MessageObject) r4
            int r5 = r4.getId()
            java.lang.Integer r5 = java.lang.Integer.valueOf(r5)
            r8.add(r5)
            if (r24 == 0) goto L_0x0058
            org.telegram.tgnet.TLRPC$Message r5 = r4.messageOwner
            long r5 = r5.random_id
            int r7 = (r5 > r14 ? 1 : (r5 == r14 ? 0 : -1))
            if (r7 == 0) goto L_0x0058
            int r5 = r4.type
            if (r5 == r13) goto L_0x0058
            if (r2 != 0) goto L_0x004d
            java.util.ArrayList r5 = new java.util.ArrayList
            r5.<init>()
            r2 = r5
        L_0x004d:
            org.telegram.tgnet.TLRPC$Message r5 = r4.messageOwner
            long r5 = r5.random_id
            java.lang.Long r5 = java.lang.Long.valueOf(r5)
            r2.add(r5)
        L_0x0058:
            int r3 = r3 + 1
            goto L_0x001c
        L_0x005b:
            r13 = r2
            goto L_0x008a
        L_0x005d:
            int r3 = r22.getId()
            java.lang.Integer r3 = java.lang.Integer.valueOf(r3)
            r8.add(r3)
            if (r24 == 0) goto L_0x0089
            org.telegram.tgnet.TLRPC$Message r3 = r0.messageOwner
            long r3 = r3.random_id
            int r5 = (r3 > r14 ? 1 : (r3 == r14 ? 0 : -1))
            if (r5 == 0) goto L_0x0089
            int r3 = r0.type
            if (r3 == r13) goto L_0x0089
            java.util.ArrayList r3 = new java.util.ArrayList
            r3.<init>()
            r2 = r3
            org.telegram.tgnet.TLRPC$Message r3 = r0.messageOwner
            long r3 = r3.random_id
            java.lang.Long r3 = java.lang.Long.valueOf(r3)
            r2.add(r3)
            r13 = r2
            goto L_0x008a
        L_0x0089:
            r13 = r2
        L_0x008a:
            org.telegram.messenger.MessagesController r2 = org.telegram.messenger.MessagesController.getInstance(r25)
            boolean r14 = r28[r9]
            r3 = r8
            r4 = r13
            r5 = r24
            r6 = r26
            r15 = r8
            r8 = r14
            r14 = 0
            r9 = r29
            r2.deleteMessages(r3, r4, r5, r6, r8, r9)
            r13 = 0
            goto L_0x0154
        L_0x00a1:
            r3 = 1
            r16 = r3
        L_0x00a4:
            if (r16 < 0) goto L_0x0152
            java.util.ArrayList r3 = new java.util.ArrayList
            r3.<init>()
            r8 = r3
            r2 = 0
        L_0x00ad:
            r3 = r30[r16]
            int r3 = r3.size()
            if (r2 >= r3) goto L_0x00c5
            r3 = r30[r16]
            int r3 = r3.keyAt(r2)
            java.lang.Integer r3 = java.lang.Integer.valueOf(r3)
            r8.add(r3)
            int r2 = r2 + 1
            goto L_0x00ad
        L_0x00c5:
            r2 = 0
            r3 = 0
            boolean r5 = r8.isEmpty()
            if (r5 != 0) goto L_0x00f3
            r5 = r30[r16]
            java.lang.Object r6 = r8.get(r9)
            java.lang.Integer r6 = (java.lang.Integer) r6
            int r6 = r6.intValue()
            java.lang.Object r5 = r5.get(r6)
            org.telegram.messenger.MessageObject r5 = (org.telegram.messenger.MessageObject) r5
            org.telegram.tgnet.TLRPC$Message r6 = r5.messageOwner
            org.telegram.tgnet.TLRPC$Peer r6 = r6.peer_id
            long r6 = r6.channel_id
            int r17 = (r6 > r14 ? 1 : (r6 == r14 ? 0 : -1))
            if (r17 == 0) goto L_0x00f3
            org.telegram.tgnet.TLRPC$Message r6 = r5.messageOwner
            org.telegram.tgnet.TLRPC$Peer r6 = r6.peer_id
            long r3 = r6.channel_id
            r17 = r3
            goto L_0x00f5
        L_0x00f3:
            r17 = r3
        L_0x00f5:
            if (r24 == 0) goto L_0x012b
            java.util.ArrayList r3 = new java.util.ArrayList
            r3.<init>()
            r2 = r3
            r3 = 0
        L_0x00fe:
            r4 = r30[r16]
            int r4 = r4.size()
            if (r3 >= r4) goto L_0x0128
            r4 = r30[r16]
            java.lang.Object r4 = r4.valueAt(r3)
            org.telegram.messenger.MessageObject r4 = (org.telegram.messenger.MessageObject) r4
            org.telegram.tgnet.TLRPC$Message r5 = r4.messageOwner
            long r5 = r5.random_id
            int r7 = (r5 > r14 ? 1 : (r5 == r14 ? 0 : -1))
            if (r7 == 0) goto L_0x0125
            int r5 = r4.type
            if (r5 == r13) goto L_0x0125
            org.telegram.tgnet.TLRPC$Message r5 = r4.messageOwner
            long r5 = r5.random_id
            java.lang.Long r5 = java.lang.Long.valueOf(r5)
            r2.add(r5)
        L_0x0125:
            int r3 = r3 + 1
            goto L_0x00fe
        L_0x0128:
            r19 = r2
            goto L_0x012d
        L_0x012b:
            r19 = r2
        L_0x012d:
            org.telegram.messenger.MessagesController r2 = org.telegram.messenger.MessagesController.getInstance(r25)
            boolean r20 = r28[r9]
            r3 = r8
            r4 = r19
            r5 = r24
            r6 = r26
            r21 = r8
            r8 = r20
            r13 = 0
            r9 = r29
            r2.deleteMessages(r3, r4, r5, r6, r8, r9)
            r2 = r30[r16]
            r2.clear()
            int r16 = r16 + -1
            r2 = r21
            r9 = 0
            r13 = 10
            goto L_0x00a4
        L_0x0152:
            r13 = 0
            r15 = r2
        L_0x0154:
            if (r10 != 0) goto L_0x0158
            if (r11 == 0) goto L_0x01a3
        L_0x0158:
            boolean r2 = r33[r13]
            if (r2 == 0) goto L_0x016d
            org.telegram.messenger.MessagesController r2 = org.telegram.messenger.MessagesController.getInstance(r25)
            long r3 = r12.id
            r8 = 0
            r9 = 0
            r5 = r31
            r6 = r32
            r7 = r35
            r2.deleteParticipantFromChat(r3, r5, r6, r7, r8, r9)
        L_0x016d:
            r2 = 1
            boolean r2 = r33[r2]
            if (r2 == 0) goto L_0x0197
            org.telegram.tgnet.TLRPC$TL_channels_reportSpam r2 = new org.telegram.tgnet.TLRPC$TL_channels_reportSpam
            r2.<init>()
            org.telegram.tgnet.TLRPC$InputChannel r3 = org.telegram.messenger.MessagesController.getInputChannel((org.telegram.tgnet.TLRPC.Chat) r34)
            r2.channel = r3
            if (r10 == 0) goto L_0x0186
            org.telegram.tgnet.TLRPC$InputPeer r3 = org.telegram.messenger.MessagesController.getInputPeer((org.telegram.tgnet.TLRPC.User) r31)
            r2.participant = r3
            goto L_0x018c
        L_0x0186:
            org.telegram.tgnet.TLRPC$InputPeer r3 = org.telegram.messenger.MessagesController.getInputPeer((org.telegram.tgnet.TLRPC.Chat) r32)
            r2.participant = r3
        L_0x018c:
            r2.id = r15
            org.telegram.tgnet.ConnectionsManager r3 = org.telegram.tgnet.ConnectionsManager.getInstance(r25)
            org.telegram.ui.Components.AlertsCreator$$ExternalSyntheticLambda87 r4 = org.telegram.ui.Components.AlertsCreator$$ExternalSyntheticLambda87.INSTANCE
            r3.sendRequest(r2, r4)
        L_0x0197:
            r2 = 2
            boolean r2 = r33[r2]
            if (r2 == 0) goto L_0x01a3
            org.telegram.messenger.MessagesController r2 = org.telegram.messenger.MessagesController.getInstance(r25)
            r2.deleteUserChannelHistory(r12, r10, r11, r13)
        L_0x01a3:
            if (r36 == 0) goto L_0x01a8
            r36.run()
        L_0x01a8:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.AlertsCreator.lambda$createDeleteMessagesAlert$96(org.telegram.messenger.MessageObject, org.telegram.messenger.MessageObject$GroupedMessages, org.telegram.tgnet.TLRPC$EncryptedChat, int, long, boolean[], boolean, android.util.SparseArray[], org.telegram.tgnet.TLRPC$User, org.telegram.tgnet.TLRPC$Chat, boolean[], org.telegram.tgnet.TLRPC$Chat, org.telegram.tgnet.TLRPC$ChatFull, java.lang.Runnable, android.content.DialogInterface, int):void");
    }

    static /* synthetic */ void lambda$createDeleteMessagesAlert$95(TLObject response, TLRPC.TL_error error) {
    }

    public static void createThemeCreateDialog(BaseFragment fragment, int type, Theme.ThemeInfo switchToTheme, Theme.ThemeAccent switchToAccent) {
        BaseFragment baseFragment = fragment;
        if (baseFragment != null && fragment.getParentActivity() != null) {
            Context context = fragment.getParentActivity();
            EditTextBoldCursor editText = new EditTextBoldCursor(context);
            editText.setBackgroundDrawable(Theme.createEditTextDrawable(context, true));
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle(LocaleController.getString("NewTheme", NUM));
            builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
            builder.setPositiveButton(LocaleController.getString("Create", NUM), AlertsCreator$$ExternalSyntheticLambda32.INSTANCE);
            LinearLayout linearLayout = new LinearLayout(context);
            linearLayout.setOrientation(1);
            builder.setView(linearLayout);
            TextView message = new TextView(context);
            if (type != 0) {
                message.setText(AndroidUtilities.replaceTags(LocaleController.getString("EnterThemeNameEdit", NUM)));
            } else {
                message.setText(LocaleController.getString("EnterThemeName", NUM));
            }
            message.setTextSize(1, 16.0f);
            message.setPadding(AndroidUtilities.dp(23.0f), AndroidUtilities.dp(12.0f), AndroidUtilities.dp(23.0f), AndroidUtilities.dp(6.0f));
            message.setTextColor(Theme.getColor("dialogTextBlack"));
            linearLayout.addView(message, LayoutHelper.createLinear(-1, -2));
            editText.setTextSize(1, 16.0f);
            editText.setTextColor(Theme.getColor("dialogTextBlack"));
            editText.setMaxLines(1);
            editText.setLines(1);
            editText.setInputType(16385);
            editText.setGravity(51);
            editText.setSingleLine(true);
            editText.setImeOptions(6);
            editText.setCursorColor(Theme.getColor("windowBackgroundWhiteBlackText"));
            editText.setCursorSize(AndroidUtilities.dp(20.0f));
            editText.setCursorWidth(1.5f);
            editText.setPadding(0, AndroidUtilities.dp(4.0f), 0, 0);
            linearLayout.addView(editText, LayoutHelper.createLinear(-1, 36, 51, 24, 6, 24, 0));
            editText.setOnEditorActionListener(AlertsCreator$$ExternalSyntheticLambda71.INSTANCE);
            editText.setText(generateThemeName(switchToAccent));
            editText.setSelection(editText.length());
            AlertDialog alertDialog = builder.create();
            alertDialog.setOnShowListener(new AlertsCreator$$ExternalSyntheticLambda36(editText));
            baseFragment.showDialog(alertDialog);
            editText.requestFocus();
            alertDialog.getButton(-1).setOnClickListener(new AlertsCreator$$ExternalSyntheticLambda44(fragment, editText, switchToAccent, switchToTheme, alertDialog));
        }
    }

    static /* synthetic */ void lambda$createThemeCreateDialog$97(DialogInterface dialog, int which) {
    }

    static /* synthetic */ void lambda$createThemeCreateDialog$99(EditTextBoldCursor editText) {
        editText.requestFocus();
        AndroidUtilities.showKeyboard(editText);
    }

    static /* synthetic */ void lambda$createThemeCreateDialog$103(BaseFragment fragment, EditTextBoldCursor editText, Theme.ThemeAccent switchToAccent, Theme.ThemeInfo switchToTheme, AlertDialog alertDialog, View v) {
        if (fragment.getParentActivity() != null) {
            if (editText.length() == 0) {
                Vibrator vibrator = (Vibrator) ApplicationLoader.applicationContext.getSystemService("vibrator");
                if (vibrator != null) {
                    vibrator.vibrate(200);
                }
                AndroidUtilities.shakeView(editText, 2.0f, 0);
                return;
            }
            if (fragment instanceof ThemePreviewActivity) {
                Theme.applyPreviousTheme();
                fragment.finishFragment();
            }
            if (switchToAccent != null) {
                switchToTheme.setCurrentAccentId(switchToAccent.id);
                Theme.refreshThemeColors();
                Utilities.searchQueue.postRunnable(new AlertsCreator$$ExternalSyntheticLambda77(editText, alertDialog, fragment));
                return;
            }
            processCreate(editText, alertDialog, fragment);
        }
    }

    /* access modifiers changed from: private */
    public static void processCreate(EditTextBoldCursor editText, AlertDialog alertDialog, BaseFragment fragment) {
        if (fragment != null && fragment.getParentActivity() != null) {
            AndroidUtilities.hideKeyboard(editText);
            Theme.ThemeInfo themeInfo = Theme.createNewTheme(editText.getText().toString());
            NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.themeListUpdated, new Object[0]);
            new ThemeEditorView().show(fragment.getParentActivity(), themeInfo);
            alertDialog.dismiss();
            SharedPreferences preferences = MessagesController.getGlobalMainSettings();
            if (!preferences.getBoolean("themehint", false)) {
                preferences.edit().putBoolean("themehint", true).commit();
                try {
                    Toast.makeText(fragment.getParentActivity(), LocaleController.getString("CreateNewThemeHelp", NUM), 1).show();
                } catch (Exception e) {
                    FileLog.e((Throwable) e);
                }
            }
        }
    }

    private static String generateThemeName(Theme.ThemeAccent accent) {
        Theme.ThemeAccent accent2;
        int color;
        List<String> adjectives = Arrays.asList(new String[]{"Ancient", "Antique", "Autumn", "Baby", "Barely", "Baroque", "Blazing", "Blushing", "Bohemian", "Bubbly", "Burning", "Buttered", "Classic", "Clear", "Cool", "Cosmic", "Cotton", "Cozy", "Crystal", "Dark", "Daring", "Darling", "Dawn", "Dazzling", "Deep", "Deepest", "Delicate", "Delightful", "Divine", "Double", "Downtown", "Dreamy", "Dusky", "Dusty", "Electric", "Enchanted", "Endless", "Evening", "Fantastic", "Flirty", "Forever", "Frigid", "Frosty", "Frozen", "Gentle", "Heavenly", "Hyper", "Icy", "Infinite", "Innocent", "Instant", "Luscious", "Lunar", "Lustrous", "Magic", "Majestic", "Mambo", "Midnight", "Millenium", "Morning", "Mystic", "Natural", "Neon", "Night", "Opaque", "Paradise", "Perfect", "Perky", "Polished", "Powerful", "Rich", "Royal", "Sheer", "Simply", "Sizzling", "Solar", "Sparkling", "Splendid", "Spicy", "Spring", "Stellar", "Sugared", "Summer", "Sunny", "Super", "Sweet", "Tender", "Tenacious", "Tidal", "Toasted", "Totally", "Tranquil", "Tropical", "True", "Twilight", "Twinkling", "Ultimate", "Ultra", "Velvety", "Vibrant", "Vintage", "Virtual", "Warm", "Warmest", "Whipped", "Wild", "Winsome"});
        List<String> subjectives = Arrays.asList(new String[]{"Ambrosia", "Attack", "Avalanche", "Blast", "Bliss", "Blossom", "Blush", "Burst", "Butter", "Candy", "Carnival", "Charm", "Chiffon", "Cloud", "Comet", "Delight", "Dream", "Dust", "Fantasy", "Flame", "Flash", "Fire", "Freeze", "Frost", "Glade", "Glaze", "Gleam", "Glimmer", "Glitter", "Glow", "Grande", "Haze", "Highlight", "Ice", "Illusion", "Intrigue", "Jewel", "Jubilee", "Kiss", "Lights", "Lollypop", "Love", "Luster", "Madness", "Matte", "Mirage", "Mist", "Moon", "Muse", "Myth", "Nectar", "Nova", "Parfait", "Passion", "Pop", "Rain", "Reflection", "Rhapsody", "Romance", "Satin", "Sensation", "Silk", "Shine", "Shadow", "Shimmer", "Sky", "Spice", "Star", "Sugar", "Sunrise", "Sunset", "Sun", "Twist", "Unbound", "Velvet", "Vibrant", "Waters", "Wine", "Wink", "Wonder", "Zone"});
        HashMap<Integer, String> colors = new HashMap<>();
        colors.put(9306112, "Berry");
        colors.put(14598550, "Brandy");
        colors.put(8391495, "Cherry");
        colors.put(16744272, "Coral");
        colors.put(14372985, "Cranberry");
        colors.put(14423100, "Crimson");
        colors.put(14725375, "Mauve");
        colors.put(16761035, "Pink");
        colors.put(16711680, "Red");
        colors.put(16711807, "Rose");
        colors.put(8406555, "Russet");
        colors.put(16720896, "Scarlet");
        colors.put(15856113, "Seashell");
        colors.put(16724889, "Strawberry");
        colors.put(16760576, "Amber");
        colors.put(15438707, "Apricot");
        colors.put(16508850, "Banana");
        colors.put(10601738, "Citrus");
        colors.put(11560192, "Ginger");
        colors.put(16766720, "Gold");
        colors.put(16640272, "Lemon");
        colors.put(16753920, "Orange");
        colors.put(16770484, "Peach");
        colors.put(16739155, "Persimmon");
        colors.put(14996514, "Sunflower");
        colors.put(15893760, "Tangerine");
        colors.put(16763004, "Topaz");
        colors.put(16776960, "Yellow");
        colors.put(3688720, "Clover");
        colors.put(8628829, "Cucumber");
        colors.put(5294200, "Emerald");
        colors.put(11907932, "Olive");
        colors.put(65280, "Green");
        colors.put(43115, "Jade");
        colors.put(2730887, "Jungle");
        colors.put(12582656, "Lime");
        colors.put(776785, "Malachite");
        colors.put(10026904, "Mint");
        colors.put(11394989, "Moss");
        colors.put(3234721, "Azure");
        colors.put(255, "Blue");
        colors.put(18347, "Cobalt");
        colors.put(5204422, "Indigo");
        colors.put(96647, "Lagoon");
        colors.put(7461346, "Aquamarine");
        colors.put(1182351, "Ultramarine");
        colors.put(128, "Navy");
        colors.put(3101086, "Sapphire");
        colors.put(7788522, "Sky");
        colors.put(32896, "Teal");
        colors.put(4251856, "Turquoise");
        colors.put(10053324, "Amethyst");
        colors.put(5046581, "Blackberry");
        colors.put(6373457, "Eggplant");
        colors.put(13148872, "Lilac");
        colors.put(11894492, "Lavender");
        colors.put(13421823, "Periwinkle");
        colors.put(8663417, "Plum");
        colors.put(6684825, "Purple");
        colors.put(14204888, "Thistle");
        colors.put(14315734, "Orchid");
        colors.put(2361920, "Violet");
        colors.put(4137225, "Bronze");
        colors.put(3604994, "Chocolate");
        colors.put(8077056, "Cinnamon");
        colors.put(3153694, "Cocoa");
        colors.put(7365973, "Coffee");
        colors.put(7956873, "Rum");
        colors.put(5113350, "Mahogany");
        colors.put(7875865, "Mocha");
        colors.put(12759680, "Sand");
        colors.put(8924439, "Sienna");
        colors.put(7864585, "Maple");
        colors.put(15787660, "Khaki");
        colors.put(12088115, "Copper");
        colors.put(12144200, "Chestnut");
        colors.put(15653316, "Almond");
        colors.put(16776656, "Cream");
        colors.put(12186367, "Diamond");
        colors.put(11109127, "Honey");
        colors.put(16777200, "Ivory");
        colors.put(15392968, "Pearl");
        colors.put(15725299, "Porcelain");
        colors.put(13745832, "Vanilla");
        colors.put(16777215, "White");
        colors.put(8421504, "Gray");
        colors.put(0, "Black");
        colors.put(15266260, "Chrome");
        colors.put(3556687, "Charcoal");
        colors.put(789277, "Ebony");
        colors.put(12632256, "Silver");
        colors.put(16119285, "Smoke");
        colors.put(2499381, "Steel");
        colors.put(5220413, "Apple");
        colors.put(8434628, "Glacier");
        colors.put(16693933, "Melon");
        colors.put(12929932, "Mulberry");
        colors.put(11126466, "Opal");
        colors.put(5547512, "Blue");
        if (accent == null) {
            accent2 = Theme.getCurrentTheme().getAccent(false);
        } else {
            accent2 = accent;
        }
        if (accent2 == null || accent2.accentColor == 0) {
            color = AndroidUtilities.calcDrawableColor(Theme.getCachedWallpaper())[0];
        } else {
            color = accent2.accentColor;
        }
        String minKey = null;
        int minValue = Integer.MAX_VALUE;
        int r1 = Color.red(color);
        int g1 = Color.green(color);
        int b1 = Color.blue(color);
        for (Map.Entry<Integer, String> entry : colors.entrySet()) {
            Integer value = entry.getKey();
            int r2 = Color.red(value.intValue());
            int rMean = (r1 + r2) / 2;
            int r = r1 - r2;
            int g = g1 - Color.green(value.intValue());
            int b = b1 - Color.blue(value.intValue());
            int color2 = color;
            int d = ((((rMean + 512) * r) * r) >> 8) + (g * 4 * g) + ((((767 - rMean) * b) * b) >> 8);
            if (d < minValue) {
                minValue = d;
                minKey = entry.getValue();
            }
            color = color2;
        }
        if (Utilities.random.nextInt() % 2 == 0) {
            return adjectives.get(Utilities.random.nextInt(adjectives.size())) + " " + minKey;
        }
        return minKey + " " + subjectives.get(Utilities.random.nextInt(subjectives.size()));
    }

    public static ActionBarPopupWindow showPopupMenu(ActionBarPopupWindow.ActionBarPopupWindowLayout popupLayout, View anchorView, int offsetX, int offsetY) {
        Rect rect = new Rect();
        ActionBarPopupWindow popupWindow = new ActionBarPopupWindow(popupLayout, -2, -2);
        if (Build.VERSION.SDK_INT >= 19) {
            popupWindow.setAnimationStyle(0);
        } else {
            popupWindow.setAnimationStyle(NUM);
        }
        popupWindow.setAnimationEnabled(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setClippingEnabled(true);
        popupWindow.setInputMethodMode(2);
        popupWindow.setSoftInputMode(0);
        popupWindow.setFocusable(true);
        popupLayout.setFocusableInTouchMode(true);
        popupLayout.setOnKeyListener(new AlertsCreator$$ExternalSyntheticLambda64(popupWindow));
        popupLayout.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.displaySize.x - AndroidUtilities.dp(40.0f), Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.displaySize.y, Integer.MIN_VALUE));
        popupWindow.showAsDropDown(anchorView, offsetX, offsetY);
        popupLayout.updateRadialSelectors();
        popupWindow.startAnimation();
        popupLayout.setOnTouchListener(new AlertsCreator$$ExternalSyntheticLambda65(popupWindow, rect));
        return popupWindow;
    }

    static /* synthetic */ boolean lambda$showPopupMenu$104(ActionBarPopupWindow popupWindow, View v, int keyCode, KeyEvent event) {
        if (keyCode != 82 || event.getRepeatCount() != 0 || event.getAction() != 1 || !popupWindow.isShowing()) {
            return false;
        }
        popupWindow.dismiss();
        return true;
    }

    static /* synthetic */ boolean lambda$showPopupMenu$105(ActionBarPopupWindow popupWindow, Rect rect, View v, MotionEvent event) {
        if (event.getActionMasked() != 0 || popupWindow == null || !popupWindow.isShowing()) {
            return false;
        }
        v.getHitRect(rect);
        if (rect.contains((int) event.getX(), (int) event.getY())) {
            return false;
        }
        popupWindow.dismiss();
        return false;
    }
}
