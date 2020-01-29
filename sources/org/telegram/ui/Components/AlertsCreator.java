package org.telegram.ui.Components;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.Html;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.URLSpan;
import android.util.Base64;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
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
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.ChatObject;
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
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.browser.Browser;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.SerializedData;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.CacheControlActivity;
import org.telegram.ui.Cells.AccountSelectCell;
import org.telegram.ui.Cells.CheckBoxCell;
import org.telegram.ui.Cells.RadioColorCell;
import org.telegram.ui.Cells.TextColorCell;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.NumberPicker;
import org.telegram.ui.LanguageSelectActivity;
import org.telegram.ui.LaunchActivity;
import org.telegram.ui.NotificationsCustomSettingsActivity;
import org.telegram.ui.NotificationsSettingsActivity;
import org.telegram.ui.ProfileNotificationsActivity;
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

    static /* synthetic */ boolean lambda$createScheduleDatePickerDialog$21(View view, MotionEvent motionEvent) {
        return true;
    }

    static /* synthetic */ void lambda$createThemeCreateDialog$61(DialogInterface dialogInterface, int i) {
    }

    static /* synthetic */ void lambda$null$31(TLObject tLObject, TLRPC.TL_error tL_error) {
    }

    static /* synthetic */ void lambda$null$59(TLObject tLObject, TLRPC.TL_error tL_error) {
    }

    /* JADX WARNING: Removed duplicated region for block: B:177:0x036a  */
    /* JADX WARNING: Removed duplicated region for block: B:181:0x0387  */
    /* JADX WARNING: Removed duplicated region for block: B:207:0x0402  */
    /* JADX WARNING: Removed duplicated region for block: B:211:0x041a  */
    /* JADX WARNING: Removed duplicated region for block: B:266:0x0520  */
    /* JADX WARNING: Removed duplicated region for block: B:271:0x054a  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static android.app.Dialog processError(int r18, org.telegram.tgnet.TLRPC.TL_error r19, org.telegram.ui.ActionBar.BaseFragment r20, org.telegram.tgnet.TLObject r21, java.lang.Object... r22) {
        /*
            r0 = r19
            r1 = r20
            r2 = r21
            r3 = r22
            int r4 = r0.code
            r5 = 0
            r6 = 406(0x196, float:5.69E-43)
            if (r4 == r6) goto L_0x061b
            java.lang.String r6 = r0.text
            if (r6 != 0) goto L_0x0015
            goto L_0x061b
        L_0x0015:
            boolean r7 = r2 instanceof org.telegram.tgnet.TLRPC.TL_account_saveSecureValue
            java.lang.String r8 = "\n"
            java.lang.String r10 = "InvalidPhoneNumber"
            java.lang.String r11 = "PHONE_NUMBER_INVALID"
            java.lang.String r13 = "ErrorOccurred"
            java.lang.String r15 = "FloodWait"
            java.lang.String r9 = "FLOOD_WAIT"
            if (r7 != 0) goto L_0x05bb
            boolean r7 = r2 instanceof org.telegram.tgnet.TLRPC.TL_account_getAuthorizationForm
            if (r7 == 0) goto L_0x002b
            goto L_0x05bb
        L_0x002b:
            boolean r7 = r2 instanceof org.telegram.tgnet.TLRPC.TL_channels_joinChannel
            java.lang.String r12 = "CHANNELS_TOO_MUCH"
            if (r7 != 0) goto L_0x055f
            boolean r14 = r2 instanceof org.telegram.tgnet.TLRPC.TL_channels_editAdmin
            if (r14 != 0) goto L_0x055f
            boolean r14 = r2 instanceof org.telegram.tgnet.TLRPC.TL_channels_inviteToChannel
            if (r14 != 0) goto L_0x055f
            boolean r14 = r2 instanceof org.telegram.tgnet.TLRPC.TL_messages_addChatUser
            if (r14 != 0) goto L_0x055f
            boolean r14 = r2 instanceof org.telegram.tgnet.TLRPC.TL_messages_startBot
            if (r14 != 0) goto L_0x055f
            boolean r14 = r2 instanceof org.telegram.tgnet.TLRPC.TL_channels_editBanned
            if (r14 != 0) goto L_0x055f
            boolean r14 = r2 instanceof org.telegram.tgnet.TLRPC.TL_messages_editChatDefaultBannedRights
            if (r14 != 0) goto L_0x055f
            boolean r14 = r2 instanceof org.telegram.tgnet.TLRPC.TL_messages_editChatAdmin
            if (r14 != 0) goto L_0x055f
            boolean r14 = r2 instanceof org.telegram.tgnet.TLRPC.TL_messages_migrateChat
            if (r14 == 0) goto L_0x0053
            goto L_0x055f
        L_0x0053:
            boolean r7 = r2 instanceof org.telegram.tgnet.TLRPC.TL_messages_createChat
            r14 = 2
            if (r7 == 0) goto L_0x007e
            boolean r3 = r6.equals(r12)
            if (r3 == 0) goto L_0x0067
            org.telegram.ui.TooManyCommunitiesActivity r0 = new org.telegram.ui.TooManyCommunitiesActivity
            r0.<init>(r14)
            r1.presentFragment(r0)
            return r5
        L_0x0067:
            java.lang.String r3 = r0.text
            boolean r3 = r3.startsWith(r9)
            if (r3 == 0) goto L_0x0076
            java.lang.String r0 = r0.text
            showFloodWaitAlert(r0, r1)
            goto L_0x061b
        L_0x0076:
            java.lang.String r0 = r0.text
            r3 = 0
            showAddUserAlert(r0, r1, r3, r2)
            goto L_0x061b
        L_0x007e:
            boolean r7 = r2 instanceof org.telegram.tgnet.TLRPC.TL_channels_createChannel
            if (r7 == 0) goto L_0x00a8
            boolean r3 = r6.equals(r12)
            if (r3 == 0) goto L_0x0091
            org.telegram.ui.TooManyCommunitiesActivity r0 = new org.telegram.ui.TooManyCommunitiesActivity
            r0.<init>(r14)
            r1.presentFragment(r0)
            return r5
        L_0x0091:
            java.lang.String r3 = r0.text
            boolean r3 = r3.startsWith(r9)
            if (r3 == 0) goto L_0x00a0
            java.lang.String r0 = r0.text
            showFloodWaitAlert(r0, r1)
            goto L_0x061b
        L_0x00a0:
            java.lang.String r0 = r0.text
            r3 = 0
            showAddUserAlert(r0, r1, r3, r2)
            goto L_0x061b
        L_0x00a8:
            boolean r7 = r2 instanceof org.telegram.tgnet.TLRPC.TL_messages_editMessage
            if (r7 == 0) goto L_0x00d2
            java.lang.String r0 = "MESSAGE_NOT_MODIFIED"
            boolean r0 = r6.equals(r0)
            if (r0 != 0) goto L_0x061b
            if (r1 == 0) goto L_0x00c4
            r0 = 2131624979(0x7f0e0413, float:1.8877153E38)
            java.lang.String r2 = "EditMessageError"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            showSimpleAlert(r1, r0)
            goto L_0x061b
        L_0x00c4:
            r0 = 2131624979(0x7f0e0413, float:1.8877153E38)
            java.lang.String r2 = "EditMessageError"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            showSimpleToast(r1, r0)
            goto L_0x061b
        L_0x00d2:
            boolean r7 = r2 instanceof org.telegram.tgnet.TLRPC.TL_messages_sendMessage
            r17 = -1
            if (r7 != 0) goto L_0x04e9
            boolean r7 = r2 instanceof org.telegram.tgnet.TLRPC.TL_messages_sendMedia
            if (r7 != 0) goto L_0x04e9
            boolean r7 = r2 instanceof org.telegram.tgnet.TLRPC.TL_messages_sendInlineBotResult
            if (r7 != 0) goto L_0x04e9
            boolean r7 = r2 instanceof org.telegram.tgnet.TLRPC.TL_messages_forwardMessages
            if (r7 != 0) goto L_0x04e9
            boolean r7 = r2 instanceof org.telegram.tgnet.TLRPC.TL_messages_sendMultiMedia
            if (r7 != 0) goto L_0x04e9
            boolean r7 = r2 instanceof org.telegram.tgnet.TLRPC.TL_messages_sendScheduledMessages
            if (r7 == 0) goto L_0x00ee
            goto L_0x04e9
        L_0x00ee:
            boolean r7 = r2 instanceof org.telegram.tgnet.TLRPC.TL_messages_importChatInvite
            if (r7 == 0) goto L_0x013d
            boolean r2 = r6.startsWith(r9)
            if (r2 == 0) goto L_0x0104
            r2 = 2131625162(0x7f0e04ca, float:1.8877524E38)
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r15, r2)
            showSimpleAlert(r1, r0)
            goto L_0x061b
        L_0x0104:
            java.lang.String r2 = r0.text
            java.lang.String r3 = "USERS_TOO_MUCH"
            boolean r2 = r2.equals(r3)
            if (r2 == 0) goto L_0x011c
            r0 = 2131625386(0x7f0e05aa, float:1.8877978E38)
            java.lang.String r2 = "JoinToGroupErrorFull"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            showSimpleAlert(r1, r0)
            goto L_0x061b
        L_0x011c:
            java.lang.String r0 = r0.text
            boolean r0 = r0.equals(r12)
            if (r0 == 0) goto L_0x012f
            org.telegram.ui.TooManyCommunitiesActivity r0 = new org.telegram.ui.TooManyCommunitiesActivity
            r2 = 0
            r0.<init>(r2)
            r1.presentFragment(r0)
            goto L_0x061b
        L_0x012f:
            r0 = 2131625387(0x7f0e05ab, float:1.887798E38)
            java.lang.String r2 = "JoinToGroupErrorNotExist"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            showSimpleAlert(r1, r0)
            goto L_0x061b
        L_0x013d:
            boolean r7 = r2 instanceof org.telegram.tgnet.TLRPC.TL_messages_getAttachedStickers
            if (r7 == 0) goto L_0x0172
            if (r1 == 0) goto L_0x061b
            android.app.Activity r2 = r20.getParentActivity()
            if (r2 == 0) goto L_0x061b
            android.app.Activity r1 = r20.getParentActivity()
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            r3 = 2131625032(0x7f0e0448, float:1.887726E38)
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r13, r3)
            r2.append(r3)
            r2.append(r8)
            java.lang.String r0 = r0.text
            r2.append(r0)
            java.lang.String r0 = r2.toString()
            r2 = 0
            android.widget.Toast r0 = android.widget.Toast.makeText(r1, r0, r2)
            r0.show()
            goto L_0x061b
        L_0x0172:
            boolean r7 = r2 instanceof org.telegram.tgnet.TLRPC.TL_account_confirmPhone
            if (r7 != 0) goto L_0x0474
            boolean r7 = r2 instanceof org.telegram.tgnet.TLRPC.TL_account_verifyPhone
            if (r7 != 0) goto L_0x0474
            boolean r7 = r2 instanceof org.telegram.tgnet.TLRPC.TL_account_verifyEmail
            if (r7 == 0) goto L_0x0180
            goto L_0x0474
        L_0x0180:
            boolean r7 = r2 instanceof org.telegram.tgnet.TLRPC.TL_auth_resendCode
            if (r7 == 0) goto L_0x020b
            boolean r2 = r6.contains(r11)
            if (r2 == 0) goto L_0x0196
            r2 = 2131625356(0x7f0e058c, float:1.8877918E38)
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r10, r2)
            android.app.Dialog r0 = showSimpleAlert(r1, r0)
            return r0
        L_0x0196:
            java.lang.String r2 = r0.text
            java.lang.String r3 = "PHONE_CODE_EMPTY"
            boolean r2 = r2.contains(r3)
            if (r2 != 0) goto L_0x01fd
            java.lang.String r2 = r0.text
            java.lang.String r3 = "PHONE_CODE_INVALID"
            boolean r2 = r2.contains(r3)
            if (r2 == 0) goto L_0x01ab
            goto L_0x01fd
        L_0x01ab:
            java.lang.String r2 = r0.text
            java.lang.String r3 = "PHONE_CODE_EXPIRED"
            boolean r2 = r2.contains(r3)
            if (r2 == 0) goto L_0x01c3
            r0 = 2131624719(0x7f0e030f, float:1.8876626E38)
            java.lang.String r2 = "CodeExpired"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            android.app.Dialog r0 = showSimpleAlert(r1, r0)
            return r0
        L_0x01c3:
            java.lang.String r2 = r0.text
            boolean r2 = r2.startsWith(r9)
            if (r2 == 0) goto L_0x01d7
            r2 = 2131625162(0x7f0e04ca, float:1.8877524E38)
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r15, r2)
            android.app.Dialog r0 = showSimpleAlert(r1, r0)
            return r0
        L_0x01d7:
            int r2 = r0.code
            r3 = -1000(0xfffffffffffffCLASSNAME, float:NaN)
            if (r2 == r3) goto L_0x061b
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            r3 = 2131625032(0x7f0e0448, float:1.887726E38)
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r13, r3)
            r2.append(r3)
            r2.append(r8)
            java.lang.String r0 = r0.text
            r2.append(r0)
            java.lang.String r0 = r2.toString()
            android.app.Dialog r0 = showSimpleAlert(r1, r0)
            return r0
        L_0x01fd:
            r0 = 2131625353(0x7f0e0589, float:1.8877912E38)
            java.lang.String r2 = "InvalidCode"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            android.app.Dialog r0 = showSimpleAlert(r1, r0)
            return r0
        L_0x020b:
            boolean r7 = r2 instanceof org.telegram.tgnet.TLRPC.TL_account_sendConfirmPhoneCode
            if (r7 == 0) goto L_0x0241
            r0 = 400(0x190, float:5.6E-43)
            if (r4 != r0) goto L_0x0221
            r0 = 2131624481(0x7f0e0221, float:1.8876143E38)
            java.lang.String r2 = "CancelLinkExpired"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            android.app.Dialog r0 = showSimpleAlert(r1, r0)
            return r0
        L_0x0221:
            if (r6 == 0) goto L_0x061b
            boolean r0 = r6.startsWith(r9)
            if (r0 == 0) goto L_0x0235
            r0 = 2131625162(0x7f0e04ca, float:1.8877524E38)
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r15, r0)
            android.app.Dialog r0 = showSimpleAlert(r1, r0)
            return r0
        L_0x0235:
            r0 = 2131625032(0x7f0e0448, float:1.887726E38)
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r13, r0)
            android.app.Dialog r0 = showSimpleAlert(r1, r0)
            return r0
        L_0x0241:
            boolean r4 = r2 instanceof org.telegram.tgnet.TLRPC.TL_account_changePhone
            if (r4 == 0) goto L_0x02ad
            boolean r2 = r6.contains(r11)
            if (r2 == 0) goto L_0x0257
            r2 = 2131625356(0x7f0e058c, float:1.8877918E38)
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r10, r2)
            showSimpleAlert(r1, r0)
            goto L_0x061b
        L_0x0257:
            java.lang.String r2 = r0.text
            java.lang.String r3 = "PHONE_CODE_EMPTY"
            boolean r2 = r2.contains(r3)
            if (r2 != 0) goto L_0x029f
            java.lang.String r2 = r0.text
            java.lang.String r3 = "PHONE_CODE_INVALID"
            boolean r2 = r2.contains(r3)
            if (r2 == 0) goto L_0x026c
            goto L_0x029f
        L_0x026c:
            java.lang.String r2 = r0.text
            java.lang.String r3 = "PHONE_CODE_EXPIRED"
            boolean r2 = r2.contains(r3)
            if (r2 == 0) goto L_0x0284
            r0 = 2131624719(0x7f0e030f, float:1.8876626E38)
            java.lang.String r2 = "CodeExpired"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            showSimpleAlert(r1, r0)
            goto L_0x061b
        L_0x0284:
            java.lang.String r2 = r0.text
            boolean r2 = r2.startsWith(r9)
            if (r2 == 0) goto L_0x0298
            r2 = 2131625162(0x7f0e04ca, float:1.8877524E38)
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r15, r2)
            showSimpleAlert(r1, r0)
            goto L_0x061b
        L_0x0298:
            java.lang.String r0 = r0.text
            showSimpleAlert(r1, r0)
            goto L_0x061b
        L_0x029f:
            r0 = 2131625353(0x7f0e0589, float:1.8877912E38)
            java.lang.String r2 = "InvalidCode"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            showSimpleAlert(r1, r0)
            goto L_0x061b
        L_0x02ad:
            boolean r4 = r2 instanceof org.telegram.tgnet.TLRPC.TL_account_sendChangePhoneCode
            if (r4 == 0) goto L_0x0340
            boolean r2 = r6.contains(r11)
            if (r2 == 0) goto L_0x02c3
            r2 = 2131625356(0x7f0e058c, float:1.8877918E38)
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r10, r2)
            showSimpleAlert(r1, r0)
            goto L_0x061b
        L_0x02c3:
            java.lang.String r2 = r0.text
            java.lang.String r4 = "PHONE_CODE_EMPTY"
            boolean r2 = r2.contains(r4)
            if (r2 != 0) goto L_0x0332
            java.lang.String r2 = r0.text
            java.lang.String r4 = "PHONE_CODE_INVALID"
            boolean r2 = r2.contains(r4)
            if (r2 == 0) goto L_0x02d8
            goto L_0x0332
        L_0x02d8:
            java.lang.String r2 = r0.text
            java.lang.String r4 = "PHONE_CODE_EXPIRED"
            boolean r2 = r2.contains(r4)
            if (r2 == 0) goto L_0x02f0
            r0 = 2131624719(0x7f0e030f, float:1.8876626E38)
            java.lang.String r2 = "CodeExpired"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            showSimpleAlert(r1, r0)
            goto L_0x061b
        L_0x02f0:
            java.lang.String r2 = r0.text
            boolean r2 = r2.startsWith(r9)
            if (r2 == 0) goto L_0x0304
            r2 = 2131625162(0x7f0e04ca, float:1.8877524E38)
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r15, r2)
            showSimpleAlert(r1, r0)
            goto L_0x061b
        L_0x0304:
            java.lang.String r0 = r0.text
            java.lang.String r2 = "PHONE_NUMBER_OCCUPIED"
            boolean r0 = r0.startsWith(r2)
            if (r0 == 0) goto L_0x0326
            r0 = 2131624502(0x7f0e0236, float:1.8876186E38)
            r2 = 1
            java.lang.Object[] r2 = new java.lang.Object[r2]
            r4 = 0
            r3 = r3[r4]
            java.lang.String r3 = (java.lang.String) r3
            r2[r4] = r3
            java.lang.String r3 = "ChangePhoneNumberOccupied"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r3, r0, r2)
            showSimpleAlert(r1, r0)
            goto L_0x061b
        L_0x0326:
            r0 = 2131625032(0x7f0e0448, float:1.887726E38)
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r13, r0)
            showSimpleAlert(r1, r0)
            goto L_0x061b
        L_0x0332:
            r0 = 2131625353(0x7f0e0589, float:1.8877912E38)
            java.lang.String r2 = "InvalidCode"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            showSimpleAlert(r1, r0)
            goto L_0x061b
        L_0x0340:
            boolean r3 = r2 instanceof org.telegram.tgnet.TLRPC.TL_updateUserName
            if (r3 == 0) goto L_0x0395
            int r0 = r6.hashCode()
            r2 = 288843630(0x1137676e, float:1.4468026E-28)
            if (r0 == r2) goto L_0x035d
            r2 = 533175271(0x1fCLASSNAMEbe7, float:8.45377E-20)
            if (r0 == r2) goto L_0x0353
            goto L_0x0367
        L_0x0353:
            java.lang.String r0 = "USERNAME_OCCUPIED"
            boolean r0 = r6.equals(r0)
            if (r0 == 0) goto L_0x0367
            r0 = 1
            goto L_0x0368
        L_0x035d:
            java.lang.String r0 = "USERNAME_INVALID"
            boolean r0 = r6.equals(r0)
            if (r0 == 0) goto L_0x0367
            r0 = 0
            goto L_0x0368
        L_0x0367:
            r0 = -1
        L_0x0368:
            if (r0 == 0) goto L_0x0387
            r2 = 1
            if (r0 == r2) goto L_0x0379
            r0 = 2131625032(0x7f0e0448, float:1.887726E38)
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r13, r0)
            showSimpleAlert(r1, r0)
            goto L_0x061b
        L_0x0379:
            r0 = 2131626957(0x7f0e0bcd, float:1.8881165E38)
            java.lang.String r2 = "UsernameInUse"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            showSimpleAlert(r1, r0)
            goto L_0x061b
        L_0x0387:
            r0 = 2131626958(0x7f0e0bce, float:1.8881167E38)
            java.lang.String r2 = "UsernameInvalid"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            showSimpleAlert(r1, r0)
            goto L_0x061b
        L_0x0395:
            boolean r3 = r2 instanceof org.telegram.tgnet.TLRPC.TL_contacts_importContacts
            if (r3 == 0) goto L_0x03ce
            if (r0 == 0) goto L_0x03c2
            boolean r2 = r6.startsWith(r9)
            if (r2 == 0) goto L_0x03a2
            goto L_0x03c2
        L_0x03a2:
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            r3 = 2131625032(0x7f0e0448, float:1.887726E38)
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r13, r3)
            r2.append(r3)
            r2.append(r8)
            java.lang.String r0 = r0.text
            r2.append(r0)
            java.lang.String r0 = r2.toString()
            showSimpleAlert(r1, r0)
            goto L_0x061b
        L_0x03c2:
            r0 = 2131625162(0x7f0e04ca, float:1.8877524E38)
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r15, r0)
            showSimpleAlert(r1, r0)
            goto L_0x061b
        L_0x03ce:
            boolean r3 = r2 instanceof org.telegram.tgnet.TLRPC.TL_account_getPassword
            if (r3 != 0) goto L_0x045a
            boolean r3 = r2 instanceof org.telegram.tgnet.TLRPC.TL_account_getTmpPassword
            if (r3 == 0) goto L_0x03d8
            goto L_0x045a
        L_0x03d8:
            boolean r3 = r2 instanceof org.telegram.tgnet.TLRPC.TL_payments_sendPaymentForm
            if (r3 == 0) goto L_0x0428
            int r2 = r6.hashCode()
            r3 = -1144062453(0xffffffffbbcefe0b, float:-0.NUM)
            if (r2 == r3) goto L_0x03f5
            r3 = -784238410(0xffffffffd14178b6, float:-5.1934618E10)
            if (r2 == r3) goto L_0x03eb
            goto L_0x03ff
        L_0x03eb:
            java.lang.String r2 = "PAYMENT_FAILED"
            boolean r2 = r6.equals(r2)
            if (r2 == 0) goto L_0x03ff
            r2 = 1
            goto L_0x0400
        L_0x03f5:
            java.lang.String r2 = "BOT_PRECHECKOUT_FAILED"
            boolean r2 = r6.equals(r2)
            if (r2 == 0) goto L_0x03ff
            r2 = 0
            goto L_0x0400
        L_0x03ff:
            r2 = -1
        L_0x0400:
            if (r2 == 0) goto L_0x041a
            r3 = 1
            if (r2 == r3) goto L_0x040c
            java.lang.String r0 = r0.text
            showSimpleToast(r1, r0)
            goto L_0x061b
        L_0x040c:
            r0 = 2131626127(0x7f0e088f, float:1.8879481E38)
            java.lang.String r2 = "PaymentFailed"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            showSimpleToast(r1, r0)
            goto L_0x061b
        L_0x041a:
            r0 = 2131626140(0x7f0e089c, float:1.8879508E38)
            java.lang.String r2 = "PaymentPrecheckoutFailed"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            showSimpleToast(r1, r0)
            goto L_0x061b
        L_0x0428:
            boolean r2 = r2 instanceof org.telegram.tgnet.TLRPC.TL_payments_validateRequestedInfo
            if (r2 == 0) goto L_0x061b
            int r2 = r6.hashCode()
            r3 = 1758025548(0x68CLASSNAMEc, float:7.606448E24)
            if (r2 == r3) goto L_0x0436
            goto L_0x0441
        L_0x0436:
            java.lang.String r2 = "SHIPPING_NOT_AVAILABLE"
            boolean r2 = r6.equals(r2)
            if (r2 == 0) goto L_0x0441
            r16 = 0
            goto L_0x0443
        L_0x0441:
            r16 = -1
        L_0x0443:
            if (r16 == 0) goto L_0x044c
            java.lang.String r0 = r0.text
            showSimpleToast(r1, r0)
            goto L_0x061b
        L_0x044c:
            r0 = 2131626129(0x7f0e0891, float:1.8879485E38)
            java.lang.String r2 = "PaymentNoShippingMethod"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            showSimpleToast(r1, r0)
            goto L_0x061b
        L_0x045a:
            java.lang.String r2 = r0.text
            boolean r2 = r2.startsWith(r9)
            if (r2 == 0) goto L_0x046d
            java.lang.String r0 = r0.text
            java.lang.String r0 = getFloodWaitString(r0)
            showSimpleToast(r1, r0)
            goto L_0x061b
        L_0x046d:
            java.lang.String r0 = r0.text
            showSimpleToast(r1, r0)
            goto L_0x061b
        L_0x0474:
            java.lang.String r2 = r0.text
            java.lang.String r3 = "PHONE_CODE_EMPTY"
            boolean r2 = r2.contains(r3)
            if (r2 != 0) goto L_0x04db
            java.lang.String r2 = r0.text
            java.lang.String r3 = "PHONE_CODE_INVALID"
            boolean r2 = r2.contains(r3)
            if (r2 != 0) goto L_0x04db
            java.lang.String r2 = r0.text
            java.lang.String r3 = "CODE_INVALID"
            boolean r2 = r2.contains(r3)
            if (r2 != 0) goto L_0x04db
            java.lang.String r2 = r0.text
            java.lang.String r3 = "CODE_EMPTY"
            boolean r2 = r2.contains(r3)
            if (r2 == 0) goto L_0x049d
            goto L_0x04db
        L_0x049d:
            java.lang.String r2 = r0.text
            java.lang.String r3 = "PHONE_CODE_EXPIRED"
            boolean r2 = r2.contains(r3)
            if (r2 != 0) goto L_0x04cd
            java.lang.String r2 = r0.text
            java.lang.String r3 = "EMAIL_VERIFY_EXPIRED"
            boolean r2 = r2.contains(r3)
            if (r2 == 0) goto L_0x04b2
            goto L_0x04cd
        L_0x04b2:
            java.lang.String r2 = r0.text
            boolean r2 = r2.startsWith(r9)
            if (r2 == 0) goto L_0x04c6
            r2 = 2131625162(0x7f0e04ca, float:1.8877524E38)
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r15, r2)
            android.app.Dialog r0 = showSimpleAlert(r1, r0)
            return r0
        L_0x04c6:
            java.lang.String r0 = r0.text
            android.app.Dialog r0 = showSimpleAlert(r1, r0)
            return r0
        L_0x04cd:
            r0 = 2131624719(0x7f0e030f, float:1.8876626E38)
            java.lang.String r2 = "CodeExpired"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            android.app.Dialog r0 = showSimpleAlert(r1, r0)
            return r0
        L_0x04db:
            r0 = 2131625353(0x7f0e0589, float:1.8877912E38)
            java.lang.String r2 = "InvalidCode"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            android.app.Dialog r0 = showSimpleAlert(r1, r0)
            return r0
        L_0x04e9:
            java.lang.String r0 = r0.text
            int r2 = r0.hashCode()
            r3 = -1809401834(0xfffffffvar_b816, float:-8.417163E-27)
            if (r2 == r3) goto L_0x0513
            r3 = -454039871(0xffffffffe4efe6c1, float:-3.5403195E22)
            if (r2 == r3) goto L_0x0509
            r3 = 1169786080(0x45b984e0, float:5936.6094)
            if (r2 == r3) goto L_0x04ff
            goto L_0x051d
        L_0x04ff:
            java.lang.String r2 = "SCHEDULE_TOO_MUCH"
            boolean r0 = r0.equals(r2)
            if (r0 == 0) goto L_0x051d
            r0 = 2
            goto L_0x051e
        L_0x0509:
            java.lang.String r2 = "PEER_FLOOD"
            boolean r0 = r0.equals(r2)
            if (r0 == 0) goto L_0x051d
            r0 = 0
            goto L_0x051e
        L_0x0513:
            java.lang.String r2 = "USER_BANNED_IN_CHANNEL"
            boolean r0 = r0.equals(r2)
            if (r0 == 0) goto L_0x051d
            r0 = 1
            goto L_0x051e
        L_0x051d:
            r0 = -1
        L_0x051e:
            if (r0 == 0) goto L_0x054a
            r2 = 1
            if (r0 == r2) goto L_0x0535
            if (r0 == r14) goto L_0x0527
            goto L_0x061b
        L_0x0527:
            r0 = 2131625558(0x7f0e0656, float:1.8878327E38)
            java.lang.String r2 = "MessageScheduledLimitReached"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            showSimpleToast(r1, r0)
            goto L_0x061b
        L_0x0535:
            org.telegram.messenger.NotificationCenter r0 = org.telegram.messenger.NotificationCenter.getInstance(r18)
            int r1 = org.telegram.messenger.NotificationCenter.needShowAlert
            java.lang.Object[] r2 = new java.lang.Object[r2]
            r3 = 5
            java.lang.Integer r3 = java.lang.Integer.valueOf(r3)
            r4 = 0
            r2[r4] = r3
            r0.postNotificationName(r1, r2)
            goto L_0x061b
        L_0x054a:
            r2 = 1
            r4 = 0
            org.telegram.messenger.NotificationCenter r0 = org.telegram.messenger.NotificationCenter.getInstance(r18)
            int r1 = org.telegram.messenger.NotificationCenter.needShowAlert
            java.lang.Object[] r2 = new java.lang.Object[r2]
            java.lang.Integer r3 = java.lang.Integer.valueOf(r4)
            r2[r4] = r3
            r0.postNotificationName(r1, r2)
            goto L_0x061b
        L_0x055f:
            if (r1 == 0) goto L_0x0584
            java.lang.String r4 = r0.text
            boolean r4 = r4.equals(r12)
            if (r4 == 0) goto L_0x0584
            if (r7 != 0) goto L_0x057a
            boolean r0 = r2 instanceof org.telegram.tgnet.TLRPC.TL_channels_inviteToChannel
            if (r0 == 0) goto L_0x0570
            goto L_0x057a
        L_0x0570:
            org.telegram.ui.TooManyCommunitiesActivity r0 = new org.telegram.ui.TooManyCommunitiesActivity
            r2 = 1
            r0.<init>(r2)
            r1.presentFragment(r0)
            goto L_0x0583
        L_0x057a:
            org.telegram.ui.TooManyCommunitiesActivity r0 = new org.telegram.ui.TooManyCommunitiesActivity
            r4 = 0
            r0.<init>(r4)
            r1.presentFragment(r0)
        L_0x0583:
            return r5
        L_0x0584:
            r4 = 0
            if (r1 == 0) goto L_0x059d
            java.lang.String r0 = r0.text
            if (r3 == 0) goto L_0x0597
            int r6 = r3.length
            if (r6 <= 0) goto L_0x0597
            r3 = r3[r4]
            java.lang.Boolean r3 = (java.lang.Boolean) r3
            boolean r14 = r3.booleanValue()
            goto L_0x0598
        L_0x0597:
            r14 = 0
        L_0x0598:
            showAddUserAlert(r0, r1, r14, r2)
            goto L_0x061b
        L_0x059d:
            java.lang.String r0 = r0.text
            java.lang.String r1 = "PEER_FLOOD"
            boolean r0 = r0.equals(r1)
            if (r0 == 0) goto L_0x061b
            org.telegram.messenger.NotificationCenter r0 = org.telegram.messenger.NotificationCenter.getInstance(r18)
            int r1 = org.telegram.messenger.NotificationCenter.needShowAlert
            r2 = 1
            java.lang.Object[] r3 = new java.lang.Object[r2]
            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)
            r4 = 0
            r3[r4] = r2
            r0.postNotificationName(r1, r3)
            goto L_0x061b
        L_0x05bb:
            java.lang.String r2 = r0.text
            boolean r2 = r2.contains(r11)
            if (r2 == 0) goto L_0x05ce
            r2 = 2131625356(0x7f0e058c, float:1.8877918E38)
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r10, r2)
            showSimpleAlert(r1, r0)
            goto L_0x061b
        L_0x05ce:
            java.lang.String r2 = r0.text
            boolean r2 = r2.startsWith(r9)
            if (r2 == 0) goto L_0x05e1
            r2 = 2131625162(0x7f0e04ca, float:1.8877524E38)
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r15, r2)
            showSimpleAlert(r1, r0)
            goto L_0x061b
        L_0x05e1:
            java.lang.String r2 = r0.text
            java.lang.String r3 = "APP_VERSION_OUTDATED"
            boolean r2 = r3.equals(r2)
            if (r2 == 0) goto L_0x05fd
            android.app.Activity r0 = r20.getParentActivity()
            r1 = 2131626877(0x7f0e0b7d, float:1.8881003E38)
            java.lang.String r2 = "UpdateAppAlert"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r2 = 1
            showUpdateAppAlert(r0, r1, r2)
            goto L_0x061b
        L_0x05fd:
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            r3 = 2131625032(0x7f0e0448, float:1.887726E38)
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r13, r3)
            r2.append(r3)
            r2.append(r8)
            java.lang.String r0 = r0.text
            r2.append(r0)
            java.lang.String r0 = r2.toString()
            showSimpleAlert(r1, r0)
        L_0x061b:
            return r5
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.AlertsCreator.processError(int, org.telegram.tgnet.TLRPC$TL_error, org.telegram.ui.ActionBar.BaseFragment, org.telegram.tgnet.TLObject, java.lang.Object[]):android.app.Dialog");
    }

    public static Toast showSimpleToast(BaseFragment baseFragment, String str) {
        Context context;
        if (str == null) {
            return null;
        }
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
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(LocaleController.getString("AppName", NUM));
        builder.setMessage(str);
        builder.setPositiveButton(LocaleController.getString("OK", NUM), (DialogInterface.OnClickListener) null);
        if (z) {
            builder.setNegativeButton(LocaleController.getString("UpdateApp", NUM), new DialogInterface.OnClickListener(context) {
                private final /* synthetic */ Context f$0;

                {
                    this.f$0 = r1;
                }

                public final void onClick(DialogInterface dialogInterface, int i) {
                    Browser.openUrl(this.f$0, BuildVars.PLAYSTORE_APP_URL);
                }
            });
        }
        return builder.show();
    }

    public static AlertDialog.Builder createLanguageAlert(LaunchActivity launchActivity, TLRPC.TL_langPackLanguage tL_langPackLanguage) {
        String str;
        int i;
        if (tL_langPackLanguage == null) {
            return null;
        }
        tL_langPackLanguage.lang_code = tL_langPackLanguage.lang_code.replace('-', '_').toLowerCase();
        tL_langPackLanguage.plural_code = tL_langPackLanguage.plural_code.replace('-', '_').toLowerCase();
        String str2 = tL_langPackLanguage.base_lang_code;
        if (str2 != null) {
            tL_langPackLanguage.base_lang_code = str2.replace('-', '_').toLowerCase();
        }
        final AlertDialog.Builder builder = new AlertDialog.Builder((Context) launchActivity);
        if (LocaleController.getInstance().getCurrentLocaleInfo().shortName.equals(tL_langPackLanguage.lang_code)) {
            builder.setTitle(LocaleController.getString("Language", NUM));
            str = LocaleController.formatString("LanguageSame", NUM, tL_langPackLanguage.name);
            builder.setNegativeButton(LocaleController.getString("OK", NUM), (DialogInterface.OnClickListener) null);
            builder.setNeutralButton(LocaleController.getString("SETTINGS", NUM), new DialogInterface.OnClickListener() {
                public final void onClick(DialogInterface dialogInterface, int i) {
                    LaunchActivity.this.lambda$runLinkRequest$30$LaunchActivity(new LanguageSelectActivity());
                }
            });
        } else if (tL_langPackLanguage.strings_count == 0) {
            builder.setTitle(LocaleController.getString("LanguageUnknownTitle", NUM));
            str = LocaleController.formatString("LanguageUnknownCustomAlert", NUM, tL_langPackLanguage.name);
            builder.setNegativeButton(LocaleController.getString("OK", NUM), (DialogInterface.OnClickListener) null);
        } else {
            builder.setTitle(LocaleController.getString("LanguageTitle", NUM));
            if (tL_langPackLanguage.official) {
                str = LocaleController.formatString("LanguageAlert", NUM, tL_langPackLanguage.name, Integer.valueOf((int) Math.ceil((double) ((((float) tL_langPackLanguage.translated_count) / ((float) tL_langPackLanguage.strings_count)) * 100.0f))));
            } else {
                str = LocaleController.formatString("LanguageCustomAlert", NUM, tL_langPackLanguage.name, Integer.valueOf((int) Math.ceil((double) ((((float) tL_langPackLanguage.translated_count) / ((float) tL_langPackLanguage.strings_count)) * 100.0f))));
            }
            builder.setPositiveButton(LocaleController.getString("Change", NUM), new DialogInterface.OnClickListener(launchActivity) {
                private final /* synthetic */ LaunchActivity f$1;

                {
                    this.f$1 = r2;
                }

                public final void onClick(DialogInterface dialogInterface, int i) {
                    AlertsCreator.lambda$createLanguageAlert$2(TLRPC.TL_langPackLanguage.this, this.f$1, dialogInterface, i);
                }
            });
            builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
        }
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(AndroidUtilities.replaceTags(str));
        int indexOf = TextUtils.indexOf(spannableStringBuilder, '[');
        if (indexOf != -1) {
            int i2 = indexOf + 1;
            i = TextUtils.indexOf(spannableStringBuilder, ']', i2);
            if (!(indexOf == -1 || i == -1)) {
                spannableStringBuilder.delete(i, i + 1);
                spannableStringBuilder.delete(indexOf, i2);
            }
        } else {
            i = -1;
        }
        if (!(indexOf == -1 || i == -1)) {
            spannableStringBuilder.setSpan(new URLSpanNoUnderline(tL_langPackLanguage.translations_url) {
                public void onClick(View view) {
                    builder.getDismissRunnable().run();
                    super.onClick(view);
                }
            }, indexOf, i - 1, 33);
        }
        TextView textView = new TextView(launchActivity);
        textView.setText(spannableStringBuilder);
        textView.setTextSize(1, 16.0f);
        textView.setLinkTextColor(Theme.getColor("dialogTextLink"));
        textView.setHighlightColor(Theme.getColor("dialogLinkSelection"));
        textView.setPadding(AndroidUtilities.dp(23.0f), 0, AndroidUtilities.dp(23.0f), 0);
        textView.setMovementMethod(new AndroidUtilities.LinkMovementMethodMy());
        textView.setTextColor(Theme.getColor("dialogTextBlack"));
        builder.setView(textView);
        return builder;
    }

    static /* synthetic */ void lambda$createLanguageAlert$2(TLRPC.TL_langPackLanguage tL_langPackLanguage, LaunchActivity launchActivity, DialogInterface dialogInterface, int i) {
        String str;
        if (tL_langPackLanguage.official) {
            str = "remote_" + tL_langPackLanguage.lang_code;
        } else {
            str = "unofficial_" + tL_langPackLanguage.lang_code;
        }
        LocaleController.LocaleInfo languageFromDict = LocaleController.getInstance().getLanguageFromDict(str);
        if (languageFromDict == null) {
            languageFromDict = new LocaleController.LocaleInfo();
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
        TLRPC.Chat chat;
        int i2 = (int) j;
        if (i2 < 0 && (chat = MessagesController.getInstance(i).getChat(Integer.valueOf(-i2))) != null && chat.slowmode_enabled && !ChatObject.hasAdminRights(chat)) {
            if (!z) {
                TLRPC.ChatFull chatFull = MessagesController.getInstance(i).getChatFull(chat.id);
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
        return false;
    }

    public static AlertDialog.Builder createSimpleAlert(Context context, String str) {
        return createSimpleAlert(context, (String) null, str);
    }

    public static AlertDialog.Builder createSimpleAlert(Context context, String str, String str2) {
        if (str2 == null) {
            return null;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        if (str == null) {
            str = LocaleController.getString("AppName", NUM);
        }
        builder.setTitle(str);
        builder.setMessage(str2);
        builder.setPositiveButton(LocaleController.getString("OK", NUM), (DialogInterface.OnClickListener) null);
        return builder;
    }

    public static Dialog showSimpleAlert(BaseFragment baseFragment, String str) {
        return showSimpleAlert(baseFragment, (String) null, str);
    }

    public static Dialog showSimpleAlert(BaseFragment baseFragment, String str, String str2) {
        if (str2 == null || baseFragment == null || baseFragment.getParentActivity() == null) {
            return null;
        }
        AlertDialog create = createSimpleAlert(baseFragment.getParentActivity(), str, str2).create();
        baseFragment.showDialog(create);
        return create;
    }

    /* JADX WARNING: Removed duplicated region for block: B:12:0x004a  */
    /* JADX WARNING: Removed duplicated region for block: B:26:0x00f0  */
    /* JADX WARNING: Removed duplicated region for block: B:27:0x00f5  */
    /* JADX WARNING: Removed duplicated region for block: B:30:0x00fd  */
    /* JADX WARNING: Removed duplicated region for block: B:31:0x0102  */
    /* JADX WARNING: Removed duplicated region for block: B:35:0x0136  */
    /* JADX WARNING: Removed duplicated region for block: B:52:0x01e0  */
    /* JADX WARNING: Removed duplicated region for block: B:58:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static void showBlockReportSpamAlert(org.telegram.ui.ActionBar.BaseFragment r18, long r19, org.telegram.tgnet.TLRPC.User r21, org.telegram.tgnet.TLRPC.Chat r22, org.telegram.tgnet.TLRPC.EncryptedChat r23, boolean r24, org.telegram.tgnet.TLRPC.ChatFull r25, org.telegram.messenger.MessagesStorage.IntCallback r26) {
        /*
            r0 = r18
            r7 = r22
            r1 = r25
            if (r0 == 0) goto L_0x01e9
            android.app.Activity r2 = r18.getParentActivity()
            if (r2 != 0) goto L_0x0010
            goto L_0x01e9
        L_0x0010:
            org.telegram.messenger.AccountInstance r3 = r18.getAccountInstance()
            org.telegram.ui.ActionBar.AlertDialog$Builder r11 = new org.telegram.ui.ActionBar.AlertDialog$Builder
            android.app.Activity r2 = r18.getParentActivity()
            r11.<init>((android.content.Context) r2)
            int r2 = r18.getCurrentAccount()
            android.content.SharedPreferences r2 = org.telegram.messenger.MessagesController.getNotificationsSettings(r2)
            r4 = 1
            r5 = 0
            if (r23 != 0) goto L_0x0045
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            r6.<init>()
            java.lang.String r8 = "dialog_bar_report"
            r6.append(r8)
            r8 = r19
            r6.append(r8)
            java.lang.String r6 = r6.toString()
            boolean r2 = r2.getBoolean(r6, r5)
            if (r2 == 0) goto L_0x0043
            goto L_0x0047
        L_0x0043:
            r2 = 0
            goto L_0x0048
        L_0x0045:
            r8 = r19
        L_0x0047:
            r2 = 1
        L_0x0048:
            if (r21 == 0) goto L_0x0136
            r1 = 2131624416(0x7f0e01e0, float:1.8876011E38)
            java.lang.Object[] r6 = new java.lang.Object[r4]
            java.lang.String r10 = org.telegram.messenger.UserObject.getFirstName(r21)
            r6[r5] = r10
            java.lang.String r10 = "BlockUserTitle"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r10, r1, r6)
            r11.setTitle(r1)
            r1 = 2131624412(0x7f0e01dc, float:1.8876003E38)
            java.lang.Object[] r6 = new java.lang.Object[r4]
            java.lang.String r10 = org.telegram.messenger.UserObject.getFirstName(r21)
            r6[r5] = r10
            java.lang.String r10 = "BlockUserAlert"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r10, r1, r6)
            android.text.SpannableStringBuilder r1 = org.telegram.messenger.AndroidUtilities.replaceTags(r1)
            r11.setMessage(r1)
            r1 = 2131624410(0x7f0e01da, float:1.8875999E38)
            java.lang.String r6 = "BlockContact"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r6, r1)
            r6 = 2
            org.telegram.ui.Cells.CheckBoxCell[] r10 = new org.telegram.ui.Cells.CheckBoxCell[r6]
            android.widget.LinearLayout r14 = new android.widget.LinearLayout
            android.app.Activity r15 = r18.getParentActivity()
            r14.<init>(r15)
            r14.setOrientation(r4)
            r15 = 0
        L_0x008f:
            if (r15 >= r6) goto L_0x0127
            if (r15 != 0) goto L_0x009b
            if (r2 != 0) goto L_0x009b
            r16 = r1
            r17 = r2
            goto L_0x011e
        L_0x009b:
            org.telegram.ui.Cells.CheckBoxCell r6 = new org.telegram.ui.Cells.CheckBoxCell
            android.app.Activity r13 = r18.getParentActivity()
            r6.<init>(r13, r4)
            r10[r15] = r6
            r6 = r10[r15]
            android.graphics.drawable.Drawable r13 = org.telegram.ui.ActionBar.Theme.getSelectorDrawable(r5)
            r6.setBackgroundDrawable(r13)
            r6 = r10[r15]
            java.lang.Integer r13 = java.lang.Integer.valueOf(r15)
            r6.setTag(r13)
            java.lang.String r6 = ""
            if (r15 != 0) goto L_0x00cd
            r13 = r10[r15]
            r12 = 2131624881(0x7f0e03b1, float:1.8876954E38)
            r16 = r1
            java.lang.String r1 = "DeleteReportSpam"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r1, r12)
            r13.setText(r1, r6, r4, r5)
            goto L_0x00e4
        L_0x00cd:
            r16 = r1
            if (r15 != r4) goto L_0x00e4
            r1 = r10[r15]
            r12 = 2131624886(0x7f0e03b6, float:1.8876964E38)
            java.lang.Object[] r13 = new java.lang.Object[r5]
            r17 = r2
            java.lang.String r2 = "DeleteThisChat"
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r12, r13)
            r1.setText(r2, r6, r4, r5)
            goto L_0x00e6
        L_0x00e4:
            r17 = r2
        L_0x00e6:
            r1 = r10[r15]
            boolean r2 = org.telegram.messenger.LocaleController.isRTL
            r6 = 1098907648(0x41800000, float:16.0)
            r12 = 1090519040(0x41000000, float:8.0)
            if (r2 == 0) goto L_0x00f5
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r6)
            goto L_0x00f9
        L_0x00f5:
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r12)
        L_0x00f9:
            boolean r13 = org.telegram.messenger.LocaleController.isRTL
            if (r13 == 0) goto L_0x0102
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r12)
            goto L_0x0106
        L_0x0102:
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
        L_0x0106:
            r1.setPadding(r2, r5, r6, r5)
            r1 = r10[r15]
            r2 = -2
            r6 = -1
            android.widget.LinearLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createLinear(r6, r2)
            r14.addView(r1, r2)
            r1 = r10[r15]
            org.telegram.ui.Components.-$$Lambda$AlertsCreator$UlzF7udPi23pwPuOF4VG7RaKfIg r2 = new org.telegram.ui.Components.-$$Lambda$AlertsCreator$UlzF7udPi23pwPuOF4VG7RaKfIg
            r2.<init>(r10)
            r1.setOnClickListener(r2)
        L_0x011e:
            int r15 = r15 + 1
            r1 = r16
            r2 = r17
            r6 = 2
            goto L_0x008f
        L_0x0127:
            r16 = r1
            r1 = 12
            r11.setCustomViewOffset(r1)
            r11.setView(r14)
            r4 = r10
            r12 = r16
            goto L_0x01ae
        L_0x0136:
            if (r7 == 0) goto L_0x0174
            if (r24 == 0) goto L_0x0174
            r2 = 2131626386(0x7f0e0992, float:1.8880007E38)
            java.lang.String r6 = "ReportUnrelatedGroup"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r6, r2)
            r11.setTitle(r2)
            if (r1 == 0) goto L_0x0167
            org.telegram.tgnet.TLRPC$ChannelLocation r1 = r1.location
            boolean r2 = r1 instanceof org.telegram.tgnet.TLRPC.TL_channelLocation
            if (r2 == 0) goto L_0x0167
            org.telegram.tgnet.TLRPC$TL_channelLocation r1 = (org.telegram.tgnet.TLRPC.TL_channelLocation) r1
            r2 = 2131626387(0x7f0e0993, float:1.8880009E38)
            java.lang.Object[] r4 = new java.lang.Object[r4]
            java.lang.String r1 = r1.address
            r4[r5] = r1
            java.lang.String r1 = "ReportUnrelatedGroupText"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r4)
            android.text.SpannableStringBuilder r1 = org.telegram.messenger.AndroidUtilities.replaceTags(r1)
            r11.setMessage(r1)
            goto L_0x01a3
        L_0x0167:
            r1 = 2131626388(0x7f0e0994, float:1.888001E38)
            java.lang.String r2 = "ReportUnrelatedGroupTextNoAddress"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r11.setMessage(r1)
            goto L_0x01a3
        L_0x0174:
            r1 = 2131626384(0x7f0e0990, float:1.8880003E38)
            java.lang.String r2 = "ReportSpamTitle"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r11.setTitle(r1)
            boolean r1 = org.telegram.messenger.ChatObject.isChannel(r22)
            if (r1 == 0) goto L_0x0197
            boolean r1 = r7.megagroup
            if (r1 != 0) goto L_0x0197
            r1 = 2131626380(0x7f0e098c, float:1.8879995E38)
            java.lang.String r2 = "ReportSpamAlertChannel"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r11.setMessage(r1)
            goto L_0x01a3
        L_0x0197:
            r1 = 2131626381(0x7f0e098d, float:1.8879997E38)
            java.lang.String r2 = "ReportSpamAlertGroup"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r11.setMessage(r1)
        L_0x01a3:
            r1 = 2131626370(0x7f0e0982, float:1.8879974E38)
            java.lang.String r2 = "ReportChat"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r12 = r1
            r4 = 0
        L_0x01ae:
            org.telegram.ui.Components.-$$Lambda$AlertsCreator$FzGvQfgCXLhRw__kmqisROHRtNs r13 = new org.telegram.ui.Components.-$$Lambda$AlertsCreator$FzGvQfgCXLhRw__kmqisROHRtNs
            r1 = r13
            r2 = r21
            r5 = r19
            r7 = r22
            r8 = r23
            r9 = r24
            r10 = r26
            r1.<init>(r3, r4, r5, r7, r8, r9, r10)
            r11.setPositiveButton(r12, r13)
            r1 = 2131624476(0x7f0e021c, float:1.8876133E38)
            java.lang.String r2 = "Cancel"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r2 = 0
            r11.setNegativeButton(r1, r2)
            org.telegram.ui.ActionBar.AlertDialog r1 = r11.create()
            r0.showDialog(r1)
            r0 = -1
            android.view.View r0 = r1.getButton(r0)
            android.widget.TextView r0 = (android.widget.TextView) r0
            if (r0 == 0) goto L_0x01e9
            java.lang.String r1 = "dialogTextRed2"
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r1)
            r0.setTextColor(r1)
        L_0x01e9:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.AlertsCreator.showBlockReportSpamAlert(org.telegram.ui.ActionBar.BaseFragment, long, org.telegram.tgnet.TLRPC$User, org.telegram.tgnet.TLRPC$Chat, org.telegram.tgnet.TLRPC$EncryptedChat, boolean, org.telegram.tgnet.TLRPC$ChatFull, org.telegram.messenger.MessagesStorage$IntCallback):void");
    }

    static /* synthetic */ void lambda$showBlockReportSpamAlert$3(CheckBoxCell[] checkBoxCellArr, View view) {
        Integer num = (Integer) view.getTag();
        checkBoxCellArr[num.intValue()].setChecked(!checkBoxCellArr[num.intValue()].isChecked(), true);
    }

    static /* synthetic */ void lambda$showBlockReportSpamAlert$4(TLRPC.User user, AccountInstance accountInstance, CheckBoxCell[] checkBoxCellArr, long j, TLRPC.Chat chat, TLRPC.EncryptedChat encryptedChat, boolean z, MessagesStorage.IntCallback intCallback, DialogInterface dialogInterface, int i) {
        TLRPC.User user2 = user;
        long j2 = j;
        MessagesStorage.IntCallback intCallback2 = intCallback;
        if (user2 != null) {
            accountInstance.getMessagesController().blockUser(user2.id);
        }
        if (checkBoxCellArr == null || (checkBoxCellArr[0] != null && checkBoxCellArr[0].isChecked())) {
            accountInstance.getMessagesController().reportSpam(j, user, chat, encryptedChat, chat != null && z);
        }
        if (checkBoxCellArr == null || checkBoxCellArr[1].isChecked()) {
            if (chat == null) {
                accountInstance.getMessagesController().deleteDialog(j2, 0);
            } else if (ChatObject.isNotInChat(chat)) {
                accountInstance.getMessagesController().deleteDialog(j2, 0);
            } else {
                accountInstance.getMessagesController().deleteUserFromChat((int) (-j2), accountInstance.getMessagesController().getUser(Integer.valueOf(accountInstance.getUserConfig().getClientUserId())), (TLRPC.ChatFull) null);
            }
            intCallback2.run(1);
            return;
        }
        intCallback2.run(0);
    }

    public static void showCustomNotificationsDialog(BaseFragment baseFragment, long j, int i, ArrayList<NotificationsSettingsActivity.NotificationException> arrayList, int i2, MessagesStorage.IntCallback intCallback) {
        showCustomNotificationsDialog(baseFragment, j, i, arrayList, i2, intCallback, (MessagesStorage.IntCallback) null);
    }

    public static void showCustomNotificationsDialog(BaseFragment baseFragment, long j, int i, ArrayList<NotificationsSettingsActivity.NotificationException> arrayList, int i2, MessagesStorage.IntCallback intCallback, MessagesStorage.IntCallback intCallback2) {
        String str;
        Drawable drawable;
        int[] iArr;
        boolean z;
        AlertDialog.Builder builder;
        int i3;
        LinearLayout linearLayout;
        BaseFragment baseFragment2 = baseFragment;
        long j2 = j;
        if (baseFragment2 != null && baseFragment.getParentActivity() != null) {
            boolean isGlobalNotificationsEnabled = NotificationsController.getInstance(i2).isGlobalNotificationsEnabled(j2);
            String[] strArr = new String[5];
            strArr[0] = LocaleController.getString("NotificationsTurnOn", NUM);
            boolean z2 = true;
            strArr[1] = LocaleController.formatString("MuteFor", NUM, LocaleController.formatPluralString("Hours", 1));
            strArr[2] = LocaleController.formatString("MuteFor", NUM, LocaleController.formatPluralString("Days", 2));
            Drawable drawable2 = null;
            if (j2 != 0 || !(baseFragment2 instanceof NotificationsCustomSettingsActivity)) {
                str = LocaleController.getString("NotificationsCustomize", NUM);
            } else {
                str = null;
            }
            strArr[3] = str;
            strArr[4] = LocaleController.getString("NotificationsTurnOff", NUM);
            int[] iArr2 = {NUM, NUM, NUM, NUM, NUM};
            LinearLayout linearLayout2 = new LinearLayout(baseFragment.getParentActivity());
            linearLayout2.setOrientation(1);
            AlertDialog.Builder builder2 = new AlertDialog.Builder((Context) baseFragment.getParentActivity());
            int i4 = 0;
            while (i4 < strArr.length) {
                if (strArr[i4] == null) {
                    i3 = i4;
                    builder = builder2;
                    iArr = iArr2;
                    drawable = drawable2;
                    z = isGlobalNotificationsEnabled;
                    linearLayout = linearLayout2;
                } else {
                    TextView textView = new TextView(baseFragment.getParentActivity());
                    Drawable drawable3 = baseFragment.getParentActivity().getResources().getDrawable(iArr2[i4]);
                    if (i4 == strArr.length - (z2 ? 1 : 0)) {
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
                    textView.setTag(Integer.valueOf(i4));
                    textView.setBackgroundDrawable(Theme.getSelectorDrawable(false));
                    textView.setPadding(AndroidUtilities.dp(24.0f), 0, AndroidUtilities.dp(24.0f), 0);
                    textView.setSingleLine(z2);
                    textView.setGravity(19);
                    textView.setCompoundDrawablePadding(AndroidUtilities.dp(26.0f));
                    textView.setText(strArr[i4]);
                    linearLayout2.addView(textView, LayoutHelper.createLinear(-1, 48, 51));
                    $$Lambda$AlertsCreator$_asGOIm29x1HlIHBvP0Lw8bBq2Y r12 = r0;
                    i3 = i4;
                    builder = builder2;
                    z = isGlobalNotificationsEnabled;
                    linearLayout = linearLayout2;
                    iArr = iArr2;
                    drawable = drawable2;
                    $$Lambda$AlertsCreator$_asGOIm29x1HlIHBvP0Lw8bBq2Y r0 = new View.OnClickListener(j, i2, isGlobalNotificationsEnabled, intCallback2, i, baseFragment, arrayList, intCallback, builder) {
                        private final /* synthetic */ long f$0;
                        private final /* synthetic */ int f$1;
                        private final /* synthetic */ boolean f$2;
                        private final /* synthetic */ MessagesStorage.IntCallback f$3;
                        private final /* synthetic */ int f$4;
                        private final /* synthetic */ BaseFragment f$5;
                        private final /* synthetic */ ArrayList f$6;
                        private final /* synthetic */ MessagesStorage.IntCallback f$7;
                        private final /* synthetic */ AlertDialog.Builder f$8;

                        {
                            this.f$0 = r1;
                            this.f$1 = r3;
                            this.f$2 = r4;
                            this.f$3 = r5;
                            this.f$4 = r6;
                            this.f$5 = r7;
                            this.f$6 = r8;
                            this.f$7 = r9;
                            this.f$8 = r10;
                        }

                        public final void onClick(View view) {
                            AlertsCreator.lambda$showCustomNotificationsDialog$5(this.f$0, this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, this.f$8, view);
                        }
                    };
                    textView.setOnClickListener(r12);
                }
                i4 = i3 + 1;
                long j3 = j;
                linearLayout2 = linearLayout;
                builder2 = builder;
                isGlobalNotificationsEnabled = z;
                iArr2 = iArr;
                drawable2 = drawable;
                z2 = true;
            }
            AlertDialog.Builder builder3 = builder2;
            builder3.setTitle(LocaleController.getString("Notifications", NUM));
            builder3.setView(linearLayout2);
            baseFragment2.showDialog(builder3.create());
        }
    }

    static /* synthetic */ void lambda$showCustomNotificationsDialog$5(long j, int i, boolean z, MessagesStorage.IntCallback intCallback, int i2, BaseFragment baseFragment, ArrayList arrayList, MessagesStorage.IntCallback intCallback2, AlertDialog.Builder builder, View view) {
        long j2;
        long j3 = j;
        MessagesStorage.IntCallback intCallback3 = intCallback;
        int i3 = i2;
        BaseFragment baseFragment2 = baseFragment;
        MessagesStorage.IntCallback intCallback4 = intCallback2;
        int intValue = ((Integer) view.getTag()).intValue();
        if (intValue == 0) {
            if (j3 != 0) {
                SharedPreferences.Editor edit = MessagesController.getNotificationsSettings(i).edit();
                if (z) {
                    edit.remove("notify2_" + j3);
                } else {
                    edit.putInt("notify2_" + j3, 0);
                }
                MessagesStorage.getInstance(i).setDialogFlags(j3, 0);
                edit.commit();
                TLRPC.Dialog dialog = MessagesController.getInstance(i).dialogs_dict.get(j3);
                if (dialog != null) {
                    dialog.notify_settings = new TLRPC.TL_peerNotifySettings();
                }
                NotificationsController.getInstance(i).updateServerNotificationsSettings(j3);
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
            if (j3 != 0) {
                SharedPreferences.Editor edit2 = MessagesController.getNotificationsSettings(i).edit();
                if (intValue != 4) {
                    edit2.putInt("notify2_" + j3, 3);
                    edit2.putInt("notifyuntil_" + j3, currentTime);
                    j2 = (((long) currentTime) << 32) | 1;
                } else if (!z) {
                    edit2.remove("notify2_" + j3);
                    j2 = 0;
                } else {
                    edit2.putInt("notify2_" + j3, 2);
                    j2 = 1;
                }
                NotificationsController.getInstance(i).removeNotificationsForDialog(j3);
                MessagesStorage.getInstance(i).setDialogFlags(j3, j2);
                edit2.commit();
                TLRPC.Dialog dialog2 = MessagesController.getInstance(i).dialogs_dict.get(j3);
                if (dialog2 != null) {
                    dialog2.notify_settings = new TLRPC.TL_peerNotifySettings();
                    if (intValue != 4 || z) {
                        dialog2.notify_settings.mute_until = currentTime;
                    }
                }
                NotificationsController.getInstance(i).updateServerNotificationsSettings(j3);
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
        } else if (j3 != 0) {
            Bundle bundle = new Bundle();
            bundle.putLong("dialog_id", j3);
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
        int i2 = MessagesController.getInstance(i).availableMapProviders;
        if ((i2 & 1) != 0) {
            arrayList.add(LocaleController.getString("MapPreviewProviderTelegram", NUM));
            arrayList2.add(0);
        }
        if ((i2 & 2) != 0) {
            arrayList.add(LocaleController.getString("MapPreviewProviderGoogle", NUM));
            arrayList2.add(1);
        }
        if ((i2 & 4) != 0) {
            arrayList.add(LocaleController.getString("MapPreviewProviderYandex", NUM));
            arrayList2.add(3);
        }
        arrayList.add(LocaleController.getString("MapPreviewProviderNobody", NUM));
        arrayList2.add(2);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(LocaleController.getString("MapPreviewProviderTitle", NUM));
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(1);
        builder.setView(linearLayout);
        for (int i3 = 0; i3 < arrayList.size(); i3++) {
            RadioColorCell radioColorCell = new RadioColorCell(context);
            radioColorCell.setPadding(AndroidUtilities.dp(4.0f), 0, AndroidUtilities.dp(4.0f), 0);
            radioColorCell.setTag(Integer.valueOf(i3));
            radioColorCell.setCheckColor(Theme.getColor("radioBackground"), Theme.getColor("dialogRadioBackgroundChecked"));
            radioColorCell.setTextAndValue((String) arrayList.get(i3), SharedConfig.mapPreviewType == ((Integer) arrayList2.get(i3)).intValue());
            linearLayout.addView(radioColorCell);
            radioColorCell.setOnClickListener(new View.OnClickListener(arrayList2, runnable, builder) {
                private final /* synthetic */ ArrayList f$0;
                private final /* synthetic */ Runnable f$1;
                private final /* synthetic */ AlertDialog.Builder f$2;

                {
                    this.f$0 = r1;
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void onClick(View view) {
                    AlertsCreator.lambda$showSecretLocationAlert$6(this.f$0, this.f$1, this.f$2, view);
                }
            });
        }
        if (!z) {
            builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
        }
        AlertDialog show = builder.show();
        if (z) {
            show.setCanceledOnTouchOutside(false);
        }
        return show;
    }

    static /* synthetic */ void lambda$showSecretLocationAlert$6(ArrayList arrayList, Runnable runnable, AlertDialog.Builder builder, View view) {
        SharedConfig.setSecretMapPreviewType(((Integer) arrayList.get(((Integer) view.getTag()).intValue())).intValue());
        if (runnable != null) {
            runnable.run();
        }
        builder.getDismissRunnable().run();
    }

    /* access modifiers changed from: private */
    public static void updateDayPicker(NumberPicker numberPicker, NumberPicker numberPicker2, NumberPicker numberPicker3) {
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
        for (URLSpan uRLSpan : uRLSpanArr) {
            int spanStart = spannableString.getSpanStart(uRLSpan);
            int spanEnd = spannableString.getSpanEnd(uRLSpan);
            spannableString.removeSpan(uRLSpan);
            spannableString.setSpan(new URLSpanNoUnderline(uRLSpan.getURL()) {
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
        textView.setMovementMethod(new AndroidUtilities.LinkMovementMethodMy());
        textView.setTextColor(Theme.getColor("dialogTextBlack"));
        AlertDialog.Builder builder = new AlertDialog.Builder((Context) baseFragment.getParentActivity());
        builder.setView(textView);
        builder.setTitle(LocaleController.getString("AskAQuestion", NUM));
        builder.setPositiveButton(LocaleController.getString("AskButton", NUM), new DialogInterface.OnClickListener() {
            public final void onClick(DialogInterface dialogInterface, int i) {
                AlertsCreator.performAskAQuestion(BaseFragment.this);
            }
        });
        builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
        return builder.create();
    }

    /* access modifiers changed from: private */
    public static void performAskAQuestion(BaseFragment baseFragment) {
        String string;
        int currentAccount = baseFragment.getCurrentAccount();
        SharedPreferences mainSettings = MessagesController.getMainSettings(currentAccount);
        int i = mainSettings.getInt("support_id", 0);
        TLRPC.User user = null;
        if (i != 0) {
            TLRPC.User user2 = MessagesController.getInstance(currentAccount).getUser(Integer.valueOf(i));
            if (user2 == null && (string = mainSettings.getString("support_user", (String) null)) != null) {
                try {
                    byte[] decode = Base64.decode(string, 0);
                    if (decode != null) {
                        SerializedData serializedData = new SerializedData(decode);
                        TLRPC.User TLdeserialize = TLRPC.User.TLdeserialize(serializedData, serializedData.readInt32(false), false);
                        if (TLdeserialize != null && TLdeserialize.id == 333000) {
                            TLdeserialize = null;
                        }
                        serializedData.cleanup();
                        user = TLdeserialize;
                    }
                } catch (Exception e) {
                    FileLog.e((Throwable) e);
                }
            }
            user = user2;
        }
        if (user == null) {
            AlertDialog alertDialog = new AlertDialog(baseFragment.getParentActivity(), 3);
            alertDialog.setCanCacnel(false);
            alertDialog.show();
            ConnectionsManager.getInstance(currentAccount).sendRequest(new TLRPC.TL_help_getSupport(), new RequestDelegate(mainSettings, alertDialog, currentAccount, baseFragment) {
                private final /* synthetic */ SharedPreferences f$0;
                private final /* synthetic */ AlertDialog f$1;
                private final /* synthetic */ int f$2;
                private final /* synthetic */ BaseFragment f$3;

                {
                    this.f$0 = r1;
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                }

                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    AlertsCreator.lambda$performAskAQuestion$10(this.f$0, this.f$1, this.f$2, this.f$3, tLObject, tL_error);
                }
            });
            return;
        }
        MessagesController.getInstance(currentAccount).putUser(user, true);
        Bundle bundle = new Bundle();
        bundle.putInt("user_id", user.id);
        baseFragment.presentFragment(new ChatActivity(bundle));
    }

    static /* synthetic */ void lambda$performAskAQuestion$10(SharedPreferences sharedPreferences, AlertDialog alertDialog, int i, BaseFragment baseFragment, TLObject tLObject, TLRPC.TL_error tL_error) {
        if (tL_error == null) {
            AndroidUtilities.runOnUIThread(new Runnable(sharedPreferences, (TLRPC.TL_help_support) tLObject, alertDialog, i, baseFragment) {
                private final /* synthetic */ SharedPreferences f$0;
                private final /* synthetic */ TLRPC.TL_help_support f$1;
                private final /* synthetic */ AlertDialog f$2;
                private final /* synthetic */ int f$3;
                private final /* synthetic */ BaseFragment f$4;

                {
                    this.f$0 = r1;
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                    this.f$4 = r5;
                }

                public final void run() {
                    AlertsCreator.lambda$null$8(this.f$0, this.f$1, this.f$2, this.f$3, this.f$4);
                }
            });
            return;
        }
        AndroidUtilities.runOnUIThread(new Runnable() {
            public final void run() {
                AlertsCreator.lambda$null$9(AlertDialog.this);
            }
        });
    }

    static /* synthetic */ void lambda$null$8(SharedPreferences sharedPreferences, TLRPC.TL_help_support tL_help_support, AlertDialog alertDialog, int i, BaseFragment baseFragment) {
        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.putInt("support_id", tL_help_support.user.id);
        SerializedData serializedData = new SerializedData();
        tL_help_support.user.serializeToStream(serializedData);
        edit.putString("support_user", Base64.encodeToString(serializedData.toByteArray(), 0));
        edit.commit();
        serializedData.cleanup();
        try {
            alertDialog.dismiss();
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
        ArrayList arrayList = new ArrayList();
        arrayList.add(tL_help_support.user);
        MessagesStorage.getInstance(i).putUsersAndChats(arrayList, (ArrayList<TLRPC.Chat>) null, true, true);
        MessagesController.getInstance(i).putUser(tL_help_support.user, false);
        Bundle bundle = new Bundle();
        bundle.putInt("user_id", tL_help_support.user.id);
        baseFragment.presentFragment(new ChatActivity(bundle));
    }

    static /* synthetic */ void lambda$null$9(AlertDialog alertDialog) {
        try {
            alertDialog.dismiss();
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    public static void createClearOrDeleteDialogAlert(BaseFragment baseFragment, boolean z, TLRPC.Chat chat, TLRPC.User user, boolean z2, MessagesStorage.BooleanCallback booleanCallback) {
        createClearOrDeleteDialogAlert(baseFragment, z, false, false, chat, user, z2, booleanCallback);
    }

    public static void createClearOrDeleteDialogAlert(BaseFragment baseFragment, boolean z, boolean z2, boolean z3, TLRPC.Chat chat, TLRPC.User user, boolean z4, MessagesStorage.BooleanCallback booleanCallback) {
        BackupImageView backupImageView;
        boolean z5;
        int i;
        boolean z6;
        int i2;
        String string;
        char c;
        int i3;
        float f;
        TLRPC.Chat chat2 = chat;
        TLRPC.User user2 = user;
        if (baseFragment != null && baseFragment.getParentActivity() != null) {
            if (chat2 != null || user2 != null) {
                int currentAccount = baseFragment.getCurrentAccount();
                Activity parentActivity = baseFragment.getParentActivity();
                AlertDialog.Builder builder = new AlertDialog.Builder((Context) parentActivity);
                int clientUserId = UserConfig.getInstance(currentAccount).getClientUserId();
                final CheckBoxCell[] checkBoxCellArr = new CheckBoxCell[1];
                TextView textView = new TextView(parentActivity);
                textView.setTextColor(Theme.getColor("dialogTextBlack"));
                textView.setTextSize(1, 16.0f);
                textView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
                boolean z7 = ChatObject.isChannel(chat) && !TextUtils.isEmpty(chat2.username);
                AnonymousClass3 r15 = new FrameLayout(parentActivity) {
                    /* access modifiers changed from: protected */
                    public void onMeasure(int i, int i2) {
                        super.onMeasure(i, i2);
                        if (checkBoxCellArr[0] != null) {
                            setMeasuredDimension(getMeasuredWidth(), getMeasuredHeight() + checkBoxCellArr[0].getMeasuredHeight() + AndroidUtilities.dp(7.0f));
                        }
                    }
                };
                builder.setView(r15);
                AvatarDrawable avatarDrawable = new AvatarDrawable();
                avatarDrawable.setTextSize(AndroidUtilities.dp(12.0f));
                BackupImageView backupImageView2 = new BackupImageView(parentActivity);
                backupImageView2.setRoundRadius(AndroidUtilities.dp(20.0f));
                r15.addView(backupImageView2, LayoutHelper.createFrame(40, 40.0f, (LocaleController.isRTL ? 5 : 3) | 48, 22.0f, 5.0f, 22.0f, 0.0f));
                TextView textView2 = new TextView(parentActivity);
                textView2.setTextColor(Theme.getColor("actionBarDefaultSubmenuItem"));
                textView2.setTextSize(1, 20.0f);
                textView2.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
                textView2.setLines(1);
                textView2.setMaxLines(1);
                textView2.setSingleLine(true);
                textView2.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
                textView2.setEllipsize(TextUtils.TruncateAt.END);
                AlertDialog.Builder builder2 = builder;
                if (!z) {
                    z5 = z7;
                    backupImageView = backupImageView2;
                    if (z2) {
                        if (!ChatObject.isChannel(chat)) {
                            textView2.setText(LocaleController.getString("DeleteMegaMenu", NUM));
                        } else if (chat2.megagroup) {
                            textView2.setText(LocaleController.getString("DeleteMegaMenu", NUM));
                        } else {
                            textView2.setText(LocaleController.getString("ChannelDeleteMenu", NUM));
                        }
                    } else if (chat2 == null) {
                        textView2.setText(LocaleController.getString("DeleteChatUser", NUM));
                    } else if (!ChatObject.isChannel(chat)) {
                        textView2.setText(LocaleController.getString("LeaveMegaMenu", NUM));
                    } else if (chat2.megagroup) {
                        textView2.setText(LocaleController.getString("LeaveMegaMenu", NUM));
                    } else {
                        textView2.setText(LocaleController.getString("LeaveChannelMenu", NUM));
                    }
                } else if (z7) {
                    z5 = z7;
                    backupImageView = backupImageView2;
                    textView2.setText(LocaleController.getString("ClearHistoryCache", NUM));
                } else {
                    z5 = z7;
                    backupImageView = backupImageView2;
                    textView2.setText(LocaleController.getString("ClearHistory", NUM));
                }
                r15.addView(textView2, LayoutHelper.createFrame(-1, -2.0f, (LocaleController.isRTL ? 5 : 3) | 48, (float) (LocaleController.isRTL ? 21 : 76), 11.0f, (float) (LocaleController.isRTL ? 76 : 21), 0.0f));
                r15.addView(textView, LayoutHelper.createFrame(-2, -2.0f, (LocaleController.isRTL ? 5 : 3) | 48, 24.0f, 57.0f, 24.0f, 9.0f));
                boolean z8 = user2 != null && !user2.bot && user2.id != clientUserId && MessagesController.getInstance(currentAccount).canRevokePmInbox;
                if (user2 != null) {
                    i = MessagesController.getInstance(currentAccount).revokeTimePmLimit;
                } else {
                    i = MessagesController.getInstance(currentAccount).revokeTimeLimit;
                }
                if (z4 || user2 == null || !z8 || i != Integer.MAX_VALUE) {
                    i2 = 1;
                    z6 = false;
                } else {
                    i2 = 1;
                    z6 = true;
                }
                boolean[] zArr = new boolean[i2];
                if (!z3 && z6) {
                    checkBoxCellArr[0] = new CheckBoxCell(parentActivity, i2);
                    checkBoxCellArr[0].setBackgroundDrawable(Theme.getSelectorDrawable(false));
                    if (z) {
                        CheckBoxCell checkBoxCell = checkBoxCellArr[0];
                        Object[] objArr = new Object[i2];
                        objArr[0] = UserObject.getFirstName(user);
                        String formatString = LocaleController.formatString("ClearHistoryOptionAlso", NUM, objArr);
                        c = 0;
                        checkBoxCell.setText(formatString, "", false, false);
                    } else {
                        c = 0;
                        checkBoxCellArr[0].setText(LocaleController.formatString("DeleteMessagesOptionAlso", NUM, UserObject.getFirstName(user)), "", false, false);
                    }
                    CheckBoxCell checkBoxCell2 = checkBoxCellArr[c];
                    if (LocaleController.isRTL) {
                        f = 16.0f;
                        i3 = AndroidUtilities.dp(16.0f);
                    } else {
                        f = 16.0f;
                        i3 = AndroidUtilities.dp(8.0f);
                    }
                    if (LocaleController.isRTL) {
                        f = 8.0f;
                    }
                    checkBoxCell2.setPadding(i3, 0, AndroidUtilities.dp(f), 0);
                    r15.addView(checkBoxCellArr[0], LayoutHelper.createFrame(-1, 48.0f, 83, 0.0f, 0.0f, 0.0f, 0.0f));
                    checkBoxCellArr[0].setOnClickListener(new View.OnClickListener(zArr) {
                        private final /* synthetic */ boolean[] f$0;

                        {
                            this.f$0 = r1;
                        }

                        public final void onClick(View view) {
                            AlertsCreator.lambda$createClearOrDeleteDialogAlert$11(this.f$0, view);
                        }
                    });
                }
                if (user2 == null) {
                    BackupImageView backupImageView3 = backupImageView;
                    if (chat2 != null) {
                        avatarDrawable.setInfo(chat2);
                        backupImageView3.setImage(ImageLocation.getForChat(chat2, false), "50_50", (Drawable) avatarDrawable, (Object) chat2);
                    }
                } else if (user2.id == clientUserId) {
                    avatarDrawable.setAvatarType(2);
                    backupImageView.setImage((ImageLocation) null, (String) null, (Drawable) avatarDrawable, (Object) user2);
                } else {
                    avatarDrawable.setInfo(user2);
                    backupImageView.setImage(ImageLocation.getForUser(user2, false), "50_50", (Drawable) avatarDrawable, (Object) user2);
                }
                if (z3) {
                    textView.setText(AndroidUtilities.replaceTags(LocaleController.getString("DeleteAllMessagesAlert", NUM)));
                } else if (z) {
                    if (user2 != null) {
                        if (z4) {
                            textView.setText(AndroidUtilities.replaceTags(LocaleController.formatString("AreYouSureClearHistoryWithSecretUser", NUM, UserObject.getUserName(user))));
                        } else if (user2.id == clientUserId) {
                            textView.setText(AndroidUtilities.replaceTags(LocaleController.getString("AreYouSureClearHistorySavedMessages", NUM)));
                        } else {
                            textView.setText(AndroidUtilities.replaceTags(LocaleController.formatString("AreYouSureClearHistoryWithUser", NUM, UserObject.getUserName(user))));
                        }
                    } else if (chat2 != null) {
                        if (!ChatObject.isChannel(chat) || (chat2.megagroup && TextUtils.isEmpty(chat2.username))) {
                            textView.setText(AndroidUtilities.replaceTags(LocaleController.formatString("AreYouSureClearHistoryWithChat", NUM, chat2.title)));
                        } else if (chat2.megagroup) {
                            textView.setText(LocaleController.getString("AreYouSureClearHistoryGroup", NUM));
                        } else {
                            textView.setText(LocaleController.getString("AreYouSureClearHistoryChannel", NUM));
                        }
                    }
                } else if (z2) {
                    if (!ChatObject.isChannel(chat)) {
                        textView.setText(LocaleController.getString("AreYouSureDeleteAndExit", NUM));
                    } else if (chat2.megagroup) {
                        textView.setText(LocaleController.getString("MegaDeleteAlert", NUM));
                    } else {
                        textView.setText(LocaleController.getString("ChannelDeleteAlert", NUM));
                    }
                } else if (user2 != null) {
                    if (z4) {
                        textView.setText(AndroidUtilities.replaceTags(LocaleController.formatString("AreYouSureDeleteThisChatWithSecretUser", NUM, UserObject.getUserName(user))));
                    } else if (user2.id == clientUserId) {
                        textView.setText(AndroidUtilities.replaceTags(LocaleController.getString("AreYouSureDeleteThisChatSavedMessages", NUM)));
                    } else {
                        textView.setText(AndroidUtilities.replaceTags(LocaleController.formatString("AreYouSureDeleteThisChatWithUser", NUM, UserObject.getUserName(user))));
                    }
                } else if (!ChatObject.isChannel(chat)) {
                    textView.setText(AndroidUtilities.replaceTags(LocaleController.formatString("AreYouSureDeleteAndExitName", NUM, chat2.title)));
                } else if (chat2.megagroup) {
                    textView.setText(AndroidUtilities.replaceTags(LocaleController.formatString("MegaLeaveAlertWithName", NUM, chat2.title)));
                } else {
                    textView.setText(AndroidUtilities.replaceTags(LocaleController.formatString("ChannelLeaveAlertWithName", NUM, chat2.title)));
                }
                if (z3) {
                    string = LocaleController.getString("DeleteAll", NUM);
                } else if (z) {
                    if (z5) {
                        string = LocaleController.getString("ClearHistoryCache", NUM);
                    } else {
                        string = LocaleController.getString("ClearHistory", NUM);
                    }
                } else if (z2) {
                    if (!ChatObject.isChannel(chat)) {
                        string = LocaleController.getString("DeleteMega", NUM);
                    } else if (chat2.megagroup) {
                        string = LocaleController.getString("DeleteMega", NUM);
                    } else {
                        string = LocaleController.getString("ChannelDelete", NUM);
                    }
                } else if (!ChatObject.isChannel(chat)) {
                    string = LocaleController.getString("DeleteChatUser", NUM);
                } else if (chat2.megagroup) {
                    string = LocaleController.getString("LeaveMegaMenu", NUM);
                } else {
                    string = LocaleController.getString("LeaveChannelMenu", NUM);
                }
                String str = string;
                AlertDialog.Builder builder3 = builder2;
                builder3.setPositiveButton(str, new DialogInterface.OnClickListener(z5, z3, zArr, baseFragment, z, z2, chat, z4, booleanCallback) {
                    private final /* synthetic */ boolean f$1;
                    private final /* synthetic */ boolean f$2;
                    private final /* synthetic */ boolean[] f$3;
                    private final /* synthetic */ BaseFragment f$4;
                    private final /* synthetic */ boolean f$5;
                    private final /* synthetic */ boolean f$6;
                    private final /* synthetic */ TLRPC.Chat f$7;
                    private final /* synthetic */ boolean f$8;
                    private final /* synthetic */ MessagesStorage.BooleanCallback f$9;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                        this.f$3 = r4;
                        this.f$4 = r5;
                        this.f$5 = r6;
                        this.f$6 = r7;
                        this.f$7 = r8;
                        this.f$8 = r9;
                        this.f$9 = r10;
                    }

                    public final void onClick(DialogInterface dialogInterface, int i) {
                        AlertsCreator.lambda$createClearOrDeleteDialogAlert$13(TLRPC.User.this, this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, this.f$8, this.f$9, dialogInterface, i);
                    }
                });
                builder3.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
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
        zArr[0] = !zArr[0];
        ((CheckBoxCell) view).setChecked(zArr[0], true);
    }

    static /* synthetic */ void lambda$createClearOrDeleteDialogAlert$13(TLRPC.User user, boolean z, boolean z2, boolean[] zArr, BaseFragment baseFragment, boolean z3, boolean z4, TLRPC.Chat chat, boolean z5, MessagesStorage.BooleanCallback booleanCallback, DialogInterface dialogInterface, int i) {
        TLRPC.User user2 = user;
        MessagesStorage.BooleanCallback booleanCallback2 = booleanCallback;
        boolean z6 = false;
        if (user2 != null && !z && !z2 && zArr[0]) {
            MessagesStorage.getInstance(baseFragment.getCurrentAccount()).getMessagesCount((long) user2.id, new MessagesStorage.IntCallback(z3, z4, chat, user, z5, booleanCallback, zArr) {
                private final /* synthetic */ boolean f$1;
                private final /* synthetic */ boolean f$2;
                private final /* synthetic */ TLRPC.Chat f$3;
                private final /* synthetic */ TLRPC.User f$4;
                private final /* synthetic */ boolean f$5;
                private final /* synthetic */ MessagesStorage.BooleanCallback f$6;
                private final /* synthetic */ boolean[] f$7;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                    this.f$4 = r5;
                    this.f$5 = r6;
                    this.f$6 = r7;
                    this.f$7 = r8;
                }

                public final void run(int i) {
                    AlertsCreator.lambda$null$12(BaseFragment.this, this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, i);
                }
            });
        } else if (booleanCallback2 != null) {
            if (z2 || zArr[0]) {
                z6 = true;
            }
            booleanCallback2.run(z6);
        }
    }

    static /* synthetic */ void lambda$null$12(BaseFragment baseFragment, boolean z, boolean z2, TLRPC.Chat chat, TLRPC.User user, boolean z3, MessagesStorage.BooleanCallback booleanCallback, boolean[] zArr, int i) {
        MessagesStorage.BooleanCallback booleanCallback2 = booleanCallback;
        if (i >= 50) {
            createClearOrDeleteDialogAlert(baseFragment, z, z2, true, chat, user, z3, booleanCallback);
        } else if (booleanCallback2 != null) {
            booleanCallback.run(zArr[0]);
        }
    }

    public static AlertDialog.Builder createDatePickerDialog(Context context, int i, int i2, int i3, int i4, int i5, int i6, String str, boolean z, DatePickerDelegate datePickerDelegate) {
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
        numberPicker2.setOnScrollListener(new NumberPicker.OnScrollListener(z2, numberPicker2, numberPicker, numberPicker3) {
            private final /* synthetic */ boolean f$0;
            private final /* synthetic */ NumberPicker f$1;
            private final /* synthetic */ NumberPicker f$2;
            private final /* synthetic */ NumberPicker f$3;

            {
                this.f$0 = r1;
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
            }

            public final void onScrollStateChange(NumberPicker numberPicker, int i) {
                AlertsCreator.lambda$createDatePickerDialog$14(this.f$0, this.f$1, this.f$2, this.f$3, numberPicker, i);
            }
        });
        numberPicker.setMinValue(0);
        numberPicker.setMaxValue(11);
        linearLayout.addView(numberPicker, LayoutHelper.createLinear(0, -2, 0.3f));
        numberPicker.setFormatter($$Lambda$AlertsCreator$VJlLh0GlRlmw33MoCufOTwK8nNQ.INSTANCE);
        numberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener(numberPicker, numberPicker3) {
            private final /* synthetic */ NumberPicker f$1;
            private final /* synthetic */ NumberPicker f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void onValueChange(NumberPicker numberPicker, int i, int i2) {
                AlertsCreator.updateDayPicker(NumberPicker.this, this.f$1, this.f$2);
            }
        });
        numberPicker.setOnScrollListener(new NumberPicker.OnScrollListener(z2, numberPicker2, numberPicker, numberPicker3) {
            private final /* synthetic */ boolean f$0;
            private final /* synthetic */ NumberPicker f$1;
            private final /* synthetic */ NumberPicker f$2;
            private final /* synthetic */ NumberPicker f$3;

            {
                this.f$0 = r1;
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
            }

            public final void onScrollStateChange(NumberPicker numberPicker, int i) {
                AlertsCreator.lambda$createDatePickerDialog$17(this.f$0, this.f$1, this.f$2, this.f$3, numberPicker, i);
            }
        });
        Calendar instance = Calendar.getInstance();
        instance.setTimeInMillis(System.currentTimeMillis());
        int i8 = instance.get(1);
        numberPicker3.setMinValue(i8 + i);
        numberPicker3.setMaxValue(i8 + i2);
        numberPicker3.setValue(i8 + i3);
        linearLayout.addView(numberPicker3, LayoutHelper.createLinear(0, -2, 0.4f));
        numberPicker3.setOnValueChangedListener(new NumberPicker.OnValueChangeListener(numberPicker, numberPicker3) {
            private final /* synthetic */ NumberPicker f$1;
            private final /* synthetic */ NumberPicker f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void onValueChange(NumberPicker numberPicker, int i, int i2) {
                AlertsCreator.updateDayPicker(NumberPicker.this, this.f$1, this.f$2);
            }
        });
        numberPicker3.setOnScrollListener(new NumberPicker.OnScrollListener(z2, numberPicker2, numberPicker, numberPicker3) {
            private final /* synthetic */ boolean f$0;
            private final /* synthetic */ NumberPicker f$1;
            private final /* synthetic */ NumberPicker f$2;
            private final /* synthetic */ NumberPicker f$3;

            {
                this.f$0 = r1;
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
            }

            public final void onScrollStateChange(NumberPicker numberPicker, int i) {
                AlertsCreator.lambda$createDatePickerDialog$19(this.f$0, this.f$1, this.f$2, this.f$3, numberPicker, i);
            }
        });
        updateDayPicker(numberPicker2, numberPicker, numberPicker3);
        if (z2) {
            checkPickerDate(numberPicker2, numberPicker, numberPicker3);
        }
        if (i7 != -1) {
            numberPicker2.setValue(i7);
            numberPicker.setValue(i5);
            numberPicker3.setValue(i6);
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(str);
        builder.setView(linearLayout);
        builder.setPositiveButton(LocaleController.getString("Set", NUM), new DialogInterface.OnClickListener(z, numberPicker2, numberPicker, numberPicker3, datePickerDelegate) {
            private final /* synthetic */ boolean f$0;
            private final /* synthetic */ NumberPicker f$1;
            private final /* synthetic */ NumberPicker f$2;
            private final /* synthetic */ NumberPicker f$3;
            private final /* synthetic */ AlertsCreator.DatePickerDelegate f$4;

            {
                this.f$0 = r1;
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
                this.f$4 = r5;
            }

            public final void onClick(DialogInterface dialogInterface, int i) {
                AlertsCreator.lambda$createDatePickerDialog$20(this.f$0, this.f$1, this.f$2, this.f$3, this.f$4, dialogInterface, i);
            }
        });
        builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
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
        TextView textView2 = textView;
        int value = numberPicker.getValue();
        int value2 = numberPicker2.getValue();
        int value3 = numberPicker3.getValue();
        Calendar instance = Calendar.getInstance();
        long currentTimeMillis = System.currentTimeMillis();
        instance.setTimeInMillis(currentTimeMillis);
        int i = instance.get(1);
        int i2 = instance.get(6);
        instance.setTimeInMillis(System.currentTimeMillis() + (((long) value) * 24 * 3600 * 1000));
        instance.set(11, value2);
        instance.set(12, value3);
        long timeInMillis = instance.getTimeInMillis();
        long j = currentTimeMillis + 60000;
        if (timeInMillis <= j) {
            instance.setTimeInMillis(j);
            if (i2 != instance.get(6)) {
                numberPicker.setValue(1);
                value = 1;
            }
            int i3 = instance.get(11);
            numberPicker2.setValue(i3);
            int i4 = instance.get(12);
            numberPicker3.setValue(i4);
            value2 = i3;
            value3 = i4;
        }
        int i5 = instance.get(1);
        int i6 = i;
        instance.setTimeInMillis(System.currentTimeMillis() + (((long) value) * 24 * 3600 * 1000));
        instance.set(11, value2);
        instance.set(12, value3);
        if (textView2 != null) {
            long timeInMillis2 = instance.getTimeInMillis();
            int i7 = value == 0 ? 0 : i6 == i5 ? 1 : 2;
            if (z) {
                i7 += 3;
            }
            textView2.setText(LocaleController.getInstance().formatterScheduleSend[i7].format(timeInMillis2));
        }
        return timeInMillis - currentTimeMillis > 60000;
    }

    public static BottomSheet.Builder createScheduleDatePickerDialog(Context context, long j, ScheduleDatePickerDelegate scheduleDatePickerDelegate) {
        return createScheduleDatePickerDialog(context, j, -1, scheduleDatePickerDelegate, (Runnable) null);
    }

    public static BottomSheet.Builder createScheduleDatePickerDialog(Context context, long j, ScheduleDatePickerDelegate scheduleDatePickerDelegate, Runnable runnable) {
        return createScheduleDatePickerDialog(context, j, -1, scheduleDatePickerDelegate, runnable);
    }

    public static BottomSheet.Builder createScheduleDatePickerDialog(Context context, long j, long j2, ScheduleDatePickerDelegate scheduleDatePickerDelegate, Runnable runnable) {
        long j3;
        Calendar calendar;
        TLRPC.User user;
        TLRPC.UserStatus userStatus;
        boolean z;
        Context context2 = context;
        long j4 = j;
        if (context2 == null) {
            return null;
        }
        int clientUserId = UserConfig.getInstance(UserConfig.selectedAccount).getClientUserId();
        BottomSheet.Builder builder = new BottomSheet.Builder(context2, false);
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
        AnonymousClass4 r7 = new LinearLayout(context2) {
            boolean ignoreLayout = false;

            /* access modifiers changed from: protected */
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
        r7.setOrientation(1);
        FrameLayout frameLayout = new FrameLayout(context2);
        r7.addView(frameLayout, LayoutHelper.createLinear(-1, -2, 51, 22, 0, 0, 4));
        TextView textView = new TextView(context2);
        long j5 = (long) clientUserId;
        if (j4 == j5) {
            textView.setText(LocaleController.getString("SetReminder", NUM));
        } else {
            textView.setText(LocaleController.getString("ScheduleMessage", NUM));
        }
        textView.setTextColor(Theme.getColor("dialogTextBlack"));
        textView.setTextSize(1, 20.0f);
        textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        frameLayout.addView(textView, LayoutHelper.createFrame(-2, -2.0f, 51, 0.0f, 12.0f, 0.0f, 0.0f));
        textView.setOnTouchListener($$Lambda$AlertsCreator$7vxVL9TgAW5Vn6ZIlSM9Zg3Jmqc.INSTANCE);
        int i = (int) j4;
        if (i <= 0 || j4 == j5 || (user = MessagesController.getInstance(UserConfig.selectedAccount).getUser(Integer.valueOf(i))) == null || user.bot || (userStatus = user.status) == null || userStatus.expires <= 0) {
            ScheduleDatePickerDelegate scheduleDatePickerDelegate2 = scheduleDatePickerDelegate;
            j3 = j5;
        } else {
            String firstName = UserObject.getFirstName(user);
            if (firstName.length() > 10) {
                StringBuilder sb = new StringBuilder();
                j3 = j5;
                z = false;
                sb.append(firstName.substring(0, 10));
                sb.append("…");
                firstName = sb.toString();
            } else {
                j3 = j5;
                z = false;
            }
            ActionBarMenuItem actionBarMenuItem = new ActionBarMenuItem(context2, (ActionBarMenu) null, z ? 1 : 0, Theme.getColor("key_sheet_other"));
            actionBarMenuItem.setLongClickEnabled(z);
            actionBarMenuItem.setSubMenuOpenSide(2);
            actionBarMenuItem.setIcon(NUM);
            actionBarMenuItem.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.getColor("player_actionBarSelector"), 1));
            frameLayout.addView(actionBarMenuItem, LayoutHelper.createFrame(40, 40.0f, 53, 0.0f, 8.0f, 5.0f, 0.0f));
            actionBarMenuItem.addSubItem(1, LocaleController.formatString("ScheduleWhenOnline", NUM, firstName));
            actionBarMenuItem.setOnClickListener(new View.OnClickListener() {
                public final void onClick(View view) {
                    ActionBarMenuItem.this.toggleSubMenu();
                }
            });
            actionBarMenuItem.setDelegate(new ActionBarMenuItem.ActionBarMenuItemDelegate(builder) {
                private final /* synthetic */ BottomSheet.Builder f$1;

                {
                    this.f$1 = r2;
                }

                public final void onItemClick(int i) {
                    AlertsCreator.lambda$createScheduleDatePickerDialog$23(AlertsCreator.ScheduleDatePickerDelegate.this, this.f$1, i);
                }
            });
            actionBarMenuItem.setContentDescription(LocaleController.getString("AccDescrMoreOptions", NUM));
        }
        LinearLayout linearLayout = new LinearLayout(context2);
        linearLayout.setOrientation(0);
        linearLayout.setWeightSum(1.0f);
        r7.addView(linearLayout, LayoutHelper.createLinear(-1, -2));
        long currentTimeMillis = System.currentTimeMillis();
        Calendar instance = Calendar.getInstance();
        instance.setTimeInMillis(currentTimeMillis);
        int i2 = instance.get(1);
        BottomSheet.Builder builder2 = builder;
        TextView textView2 = new TextView(context2);
        String str = "fonts/rmedium.ttf";
        linearLayout.addView(numberPicker, LayoutHelper.createLinear(0, 270, 0.5f));
        numberPicker.setMinValue(0);
        numberPicker.setMaxValue(365);
        numberPicker.setWrapSelectorWheel(false);
        numberPicker.setFormatter(new NumberPicker.Formatter(currentTimeMillis, instance, i2) {
            private final /* synthetic */ long f$0;
            private final /* synthetic */ Calendar f$1;
            private final /* synthetic */ int f$2;

            {
                this.f$0 = r1;
                this.f$1 = r3;
                this.f$2 = r4;
            }

            public final String format(int i) {
                return AlertsCreator.lambda$createScheduleDatePickerDialog$24(this.f$0, this.f$1, this.f$2, i);
            }
        });
        Calendar calendar2 = instance;
        $$Lambda$AlertsCreator$6AnhMphU6whhzbgD5XkCr_q_09g r12 = r0;
        int i3 = clientUserId;
        AnonymousClass4 r31 = r7;
        $$Lambda$AlertsCreator$6AnhMphU6whhzbgD5XkCr_q_09g r0 = new NumberPicker.OnValueChangeListener(textView2, clientUserId, j, numberPicker, numberPicker2, numberPicker3) {
            private final /* synthetic */ TextView f$0;
            private final /* synthetic */ int f$1;
            private final /* synthetic */ long f$2;
            private final /* synthetic */ NumberPicker f$3;
            private final /* synthetic */ NumberPicker f$4;
            private final /* synthetic */ NumberPicker f$5;

            {
                this.f$0 = r1;
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r5;
                this.f$4 = r6;
                this.f$5 = r7;
            }

            public final void onValueChange(NumberPicker numberPicker, int i, int i2) {
                AlertsCreator.lambda$createScheduleDatePickerDialog$25(this.f$0, this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, numberPicker, i, i2);
            }
        };
        numberPicker.setOnValueChangedListener(r12);
        numberPicker2.setMinValue(0);
        numberPicker2.setMaxValue(23);
        LinearLayout linearLayout2 = linearLayout;
        linearLayout2.addView(numberPicker2, LayoutHelper.createLinear(0, 270, 0.2f));
        numberPicker2.setFormatter($$Lambda$AlertsCreator$TSQJEOtVCEhgAIPpdVghXXq1LRE.INSTANCE);
        numberPicker2.setOnValueChangedListener(r12);
        numberPicker3.setMinValue(0);
        numberPicker3.setMaxValue(59);
        numberPicker3.setValue(0);
        numberPicker3.setFormatter($$Lambda$AlertsCreator$KakG1tQ5ETn782zFMW9iKAMpW2A.INSTANCE);
        linearLayout2.addView(numberPicker3, LayoutHelper.createLinear(0, 270, 0.3f));
        numberPicker3.setOnValueChangedListener(r12);
        if (j2 <= 0 || j2 == NUM) {
            calendar = calendar2;
        } else {
            long j6 = 1000 * j2;
            calendar = calendar2;
            calendar.setTimeInMillis(System.currentTimeMillis());
            calendar.set(12, 0);
            calendar.set(11, 0);
            int timeInMillis = (int) ((j6 - calendar.getTimeInMillis()) / 86400000);
            calendar.setTimeInMillis(j6);
            if (timeInMillis >= 0) {
                numberPicker3.setValue(calendar.get(12));
                numberPicker2.setValue(calendar.get(11));
                numberPicker.setValue(timeInMillis);
            }
        }
        boolean[] zArr = {true};
        checkScheduleDate(textView2, j3 == j4, numberPicker, numberPicker2, numberPicker3);
        textView2.setPadding(AndroidUtilities.dp(34.0f), 0, AndroidUtilities.dp(34.0f), 0);
        textView2.setGravity(17);
        textView2.setTextColor(Theme.getColor("featuredStickers_buttonText"));
        textView2.setTextSize(1, 14.0f);
        textView2.setTypeface(AndroidUtilities.getTypeface(str));
        textView2.setBackgroundDrawable(Theme.createSimpleSelectorRoundRectDrawable(AndroidUtilities.dp(4.0f), Theme.getColor("featuredStickers_addButton"), Theme.getColor("featuredStickers_addButtonPressed")));
        AnonymousClass4 r10 = r31;
        r10.addView(textView2, LayoutHelper.createLinear(-1, 48, 83, 16, 15, 16, 16));
        NumberPicker numberPicker4 = numberPicker;
        $$Lambda$AlertsCreator$ZpZtLHOTj8yqCPWZNifekiyWumA r13 = r0;
        $$Lambda$AlertsCreator$ZpZtLHOTj8yqCPWZNifekiyWumA r02 = new View.OnClickListener(zArr, i3, j, numberPicker4, numberPicker2, numberPicker3, calendar, scheduleDatePickerDelegate, builder2) {
            private final /* synthetic */ boolean[] f$0;
            private final /* synthetic */ int f$1;
            private final /* synthetic */ long f$2;
            private final /* synthetic */ NumberPicker f$3;
            private final /* synthetic */ NumberPicker f$4;
            private final /* synthetic */ NumberPicker f$5;
            private final /* synthetic */ Calendar f$6;
            private final /* synthetic */ AlertsCreator.ScheduleDatePickerDelegate f$7;
            private final /* synthetic */ BottomSheet.Builder f$8;

            {
                this.f$0 = r1;
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r5;
                this.f$4 = r6;
                this.f$5 = r7;
                this.f$6 = r8;
                this.f$7 = r9;
                this.f$8 = r10;
            }

            public final void onClick(View view) {
                AlertsCreator.lambda$createScheduleDatePickerDialog$28(this.f$0, this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, this.f$8, view);
            }
        };
        textView2.setOnClickListener(r13);
        BottomSheet.Builder builder3 = builder2;
        builder3.setCustomView(r10);
        builder3.show().setOnDismissListener(new DialogInterface.OnDismissListener(runnable, zArr) {
            private final /* synthetic */ Runnable f$0;
            private final /* synthetic */ boolean[] f$1;

            {
                this.f$0 = r1;
                this.f$1 = r2;
            }

            public final void onDismiss(DialogInterface dialogInterface) {
                AlertsCreator.lambda$createScheduleDatePickerDialog$29(this.f$0, this.f$1, dialogInterface);
            }
        });
        return builder3;
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
        long j2 = j + (((long) i2) * 86400000);
        calendar.setTimeInMillis(j2);
        if (calendar.get(1) == i) {
            return LocaleController.getInstance().formatterScheduleDay.format(j2);
        }
        return LocaleController.getInstance().formatterScheduleYear.format(j2);
    }

    static /* synthetic */ void lambda$createScheduleDatePickerDialog$25(TextView textView, int i, long j, NumberPicker numberPicker, NumberPicker numberPicker2, NumberPicker numberPicker3, NumberPicker numberPicker4, int i2, int i3) {
        checkScheduleDate(textView, ((long) i) == j, numberPicker, numberPicker2, numberPicker3);
    }

    static /* synthetic */ void lambda$createScheduleDatePickerDialog$28(boolean[] zArr, int i, long j, NumberPicker numberPicker, NumberPicker numberPicker2, NumberPicker numberPicker3, Calendar calendar, ScheduleDatePickerDelegate scheduleDatePickerDelegate, BottomSheet.Builder builder, View view) {
        zArr[0] = false;
        boolean checkScheduleDate = checkScheduleDate((TextView) null, ((long) i) == j, numberPicker, numberPicker2, numberPicker3);
        calendar.setTimeInMillis(System.currentTimeMillis() + (((long) numberPicker.getValue()) * 24 * 3600 * 1000));
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
        builder.setItems(new CharSequence[]{LocaleController.formatString("MuteFor", NUM, LocaleController.formatPluralString("Hours", 1)), LocaleController.formatString("MuteFor", NUM, LocaleController.formatPluralString("Hours", 8)), LocaleController.formatString("MuteFor", NUM, LocaleController.formatPluralString("Days", 2)), LocaleController.getString("MuteDisable", NUM)}, new DialogInterface.OnClickListener(j) {
            private final /* synthetic */ long f$0;

            {
                this.f$0 = r1;
            }

            public final void onClick(DialogInterface dialogInterface, int i) {
                AlertsCreator.lambda$createMuteAlert$30(this.f$0, dialogInterface, i);
            }
        });
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
            builder.setItems(new CharSequence[]{LocaleController.getString("ReportChatSpam", NUM), LocaleController.getString("ReportChatViolence", NUM), LocaleController.getString("ReportChatChild", NUM), LocaleController.getString("ReportChatPornography", NUM), LocaleController.getString("ReportChatOther", NUM)}, new DialogInterface.OnClickListener(j, i, baseFragment, context) {
                private final /* synthetic */ long f$0;
                private final /* synthetic */ int f$1;
                private final /* synthetic */ BaseFragment f$2;
                private final /* synthetic */ Context f$3;

                {
                    this.f$0 = r1;
                    this.f$1 = r3;
                    this.f$2 = r4;
                    this.f$3 = r5;
                }

                public final void onClick(DialogInterface dialogInterface, int i) {
                    AlertsCreator.lambda$createReportAlert$32(this.f$0, this.f$1, this.f$2, this.f$3, dialogInterface, i);
                }
            });
            baseFragment.showDialog(builder.create());
        }
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v0, resolved type: org.telegram.tgnet.TLRPC$TL_account_reportPeer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v2, resolved type: org.telegram.tgnet.TLRPC$TL_messages_report} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v3, resolved type: org.telegram.tgnet.TLRPC$TL_account_reportPeer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v4, resolved type: org.telegram.tgnet.TLRPC$TL_account_reportPeer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v5, resolved type: org.telegram.tgnet.TLRPC$TL_account_reportPeer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v6, resolved type: org.telegram.tgnet.TLRPC$TL_account_reportPeer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v7, resolved type: org.telegram.tgnet.TLRPC$TL_account_reportPeer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v8, resolved type: org.telegram.tgnet.TLRPC$TL_account_reportPeer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v9, resolved type: org.telegram.tgnet.TLRPC$TL_account_reportPeer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v10, resolved type: org.telegram.tgnet.TLRPC$TL_account_reportPeer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v11, resolved type: org.telegram.tgnet.TLRPC$TL_account_reportPeer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v12, resolved type: org.telegram.tgnet.TLRPC$TL_account_reportPeer} */
    /* JADX WARNING: Code restructure failed: missing block: B:13:0x005a, code lost:
        if (r7 != 3) goto L_0x0092;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:14:0x005c, code lost:
        r0.reason = new org.telegram.tgnet.TLRPC.TL_inputReportReasonPornography();
        r0 = r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:22:0x0089, code lost:
        if (r7 != 3) goto L_0x0092;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:23:0x008b, code lost:
        r0.reason = new org.telegram.tgnet.TLRPC.TL_inputReportReasonPornography();
        r0 = r0;
     */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    static /* synthetic */ void lambda$createReportAlert$32(long r1, int r3, org.telegram.ui.ActionBar.BaseFragment r4, android.content.Context r5, android.content.DialogInterface r6, int r7) {
        /*
            r6 = 4
            if (r7 != r6) goto L_0x001c
            android.os.Bundle r5 = new android.os.Bundle
            r5.<init>()
            java.lang.String r6 = "dialog_id"
            r5.putLong(r6, r1)
            long r1 = (long) r3
            java.lang.String r3 = "message_id"
            r5.putLong(r3, r1)
            org.telegram.ui.ReportOtherActivity r1 = new org.telegram.ui.ReportOtherActivity
            r1.<init>(r5)
            r4.presentFragment(r1)
            return
        L_0x001c:
            int r4 = org.telegram.messenger.UserConfig.selectedAccount
            org.telegram.messenger.MessagesController r4 = org.telegram.messenger.MessagesController.getInstance(r4)
            int r2 = (int) r1
            org.telegram.tgnet.TLRPC$InputPeer r1 = r4.getInputPeer(r2)
            r2 = 3
            r4 = 2
            r6 = 1
            if (r3 == 0) goto L_0x0064
            org.telegram.tgnet.TLRPC$TL_messages_report r0 = new org.telegram.tgnet.TLRPC$TL_messages_report
            r0.<init>()
            r0.peer = r1
            java.util.ArrayList<java.lang.Integer> r1 = r0.id
            java.lang.Integer r3 = java.lang.Integer.valueOf(r3)
            r1.add(r3)
            if (r7 != 0) goto L_0x0046
            org.telegram.tgnet.TLRPC$TL_inputReportReasonSpam r1 = new org.telegram.tgnet.TLRPC$TL_inputReportReasonSpam
            r1.<init>()
            r0.reason = r1
            goto L_0x0092
        L_0x0046:
            if (r7 != r6) goto L_0x0050
            org.telegram.tgnet.TLRPC$TL_inputReportReasonViolence r1 = new org.telegram.tgnet.TLRPC$TL_inputReportReasonViolence
            r1.<init>()
            r0.reason = r1
            goto L_0x0092
        L_0x0050:
            if (r7 != r4) goto L_0x005a
            org.telegram.tgnet.TLRPC$TL_inputReportReasonChildAbuse r1 = new org.telegram.tgnet.TLRPC$TL_inputReportReasonChildAbuse
            r1.<init>()
            r0.reason = r1
            goto L_0x0092
        L_0x005a:
            if (r7 != r2) goto L_0x0092
            org.telegram.tgnet.TLRPC$TL_inputReportReasonPornography r1 = new org.telegram.tgnet.TLRPC$TL_inputReportReasonPornography
            r1.<init>()
            r0.reason = r1
            goto L_0x0092
        L_0x0064:
            org.telegram.tgnet.TLRPC$TL_account_reportPeer r0 = new org.telegram.tgnet.TLRPC$TL_account_reportPeer
            r0.<init>()
            r0.peer = r1
            if (r7 != 0) goto L_0x0075
            org.telegram.tgnet.TLRPC$TL_inputReportReasonSpam r1 = new org.telegram.tgnet.TLRPC$TL_inputReportReasonSpam
            r1.<init>()
            r0.reason = r1
            goto L_0x0092
        L_0x0075:
            if (r7 != r6) goto L_0x007f
            org.telegram.tgnet.TLRPC$TL_inputReportReasonViolence r1 = new org.telegram.tgnet.TLRPC$TL_inputReportReasonViolence
            r1.<init>()
            r0.reason = r1
            goto L_0x0092
        L_0x007f:
            if (r7 != r4) goto L_0x0089
            org.telegram.tgnet.TLRPC$TL_inputReportReasonChildAbuse r1 = new org.telegram.tgnet.TLRPC$TL_inputReportReasonChildAbuse
            r1.<init>()
            r0.reason = r1
            goto L_0x0092
        L_0x0089:
            if (r7 != r2) goto L_0x0092
            org.telegram.tgnet.TLRPC$TL_inputReportReasonPornography r1 = new org.telegram.tgnet.TLRPC$TL_inputReportReasonPornography
            r1.<init>()
            r0.reason = r1
        L_0x0092:
            int r1 = org.telegram.messenger.UserConfig.selectedAccount
            org.telegram.tgnet.ConnectionsManager r1 = org.telegram.tgnet.ConnectionsManager.getInstance(r1)
            org.telegram.ui.Components.-$$Lambda$AlertsCreator$NpoDU2Mba05BmoLPTdtKeWiFGaI r2 = org.telegram.ui.Components.$$Lambda$AlertsCreator$NpoDU2Mba05BmoLPTdtKeWiFGaI.INSTANCE
            r1.sendRequest(r0, r2)
            r1 = 2131626375(0x7f0e0987, float:1.8879984E38)
            java.lang.String r2 = "ReportChatSent"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r2 = 0
            android.widget.Toast r1 = android.widget.Toast.makeText(r5, r1, r2)
            r1.show()
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.AlertsCreator.lambda$createReportAlert$32(long, int, org.telegram.ui.ActionBar.BaseFragment, android.content.Context, android.content.DialogInterface, int):void");
    }

    private static String getFloodWaitString(String str) {
        String str2;
        int intValue = Utilities.parseInt(str).intValue();
        if (intValue < 60) {
            str2 = LocaleController.formatPluralString("Seconds", intValue);
        } else {
            str2 = LocaleController.formatPluralString("Minutes", intValue / 60);
        }
        return LocaleController.formatString("FloodWaitTime", NUM, str2);
    }

    public static void showFloodWaitAlert(String str, BaseFragment baseFragment) {
        String str2;
        if (str != null && str.startsWith("FLOOD_WAIT") && baseFragment != null && baseFragment.getParentActivity() != null) {
            int intValue = Utilities.parseInt(str).intValue();
            if (intValue < 60) {
                str2 = LocaleController.formatPluralString("Seconds", intValue);
            } else {
                str2 = LocaleController.formatPluralString("Minutes", intValue / 60);
            }
            AlertDialog.Builder builder = new AlertDialog.Builder((Context) baseFragment.getParentActivity());
            builder.setTitle(LocaleController.getString("AppName", NUM));
            builder.setMessage(LocaleController.formatString("FloodWaitTime", NUM, str2));
            builder.setPositiveButton(LocaleController.getString("OK", NUM), (DialogInterface.OnClickListener) null);
            baseFragment.showDialog(builder.create(), true, (DialogInterface.OnDismissListener) null);
        }
    }

    public static void showSendMediaAlert(int i, BaseFragment baseFragment) {
        if (i != 0) {
            AlertDialog.Builder builder = new AlertDialog.Builder((Context) baseFragment.getParentActivity());
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
            builder.setPositiveButton(LocaleController.getString("OK", NUM), (DialogInterface.OnClickListener) null);
            baseFragment.showDialog(builder.create(), true, (DialogInterface.OnDismissListener) null);
        }
    }

    public static void showAddUserAlert(String str, BaseFragment baseFragment, boolean z, TLObject tLObject) {
        if (str != null && baseFragment != null && baseFragment.getParentActivity() != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder((Context) baseFragment.getParentActivity());
            builder.setTitle(LocaleController.getString("AppName", NUM));
            char c = 65535;
            switch (str.hashCode()) {
                case -2120721660:
                    if (str.equals("CHANNELS_ADMIN_LOCATED_TOO_MUCH")) {
                        c = 17;
                        break;
                    }
                    break;
                case -2012133105:
                    if (str.equals("CHANNELS_ADMIN_PUBLIC_TOO_MUCH")) {
                        c = 16;
                        break;
                    }
                    break;
                case -1763467626:
                    if (str.equals("USERS_TOO_FEW")) {
                        c = 9;
                        break;
                    }
                    break;
                case -538116776:
                    if (str.equals("USER_BLOCKED")) {
                        c = 1;
                        break;
                    }
                    break;
                case -512775857:
                    if (str.equals("USER_RESTRICTED")) {
                        c = 10;
                        break;
                    }
                    break;
                case -454039871:
                    if (str.equals("PEER_FLOOD")) {
                        c = 0;
                        break;
                    }
                    break;
                case -420079733:
                    if (str.equals("BOTS_TOO_MUCH")) {
                        c = 7;
                        break;
                    }
                    break;
                case 98635865:
                    if (str.equals("USER_KICKED")) {
                        c = 13;
                        break;
                    }
                    break;
                case 517420851:
                    if (str.equals("USER_BOT")) {
                        c = 2;
                        break;
                    }
                    break;
                case 845559454:
                    if (str.equals("YOU_BLOCKED_USER")) {
                        c = 11;
                        break;
                    }
                    break;
                case 916342611:
                    if (str.equals("USER_ADMIN_INVALID")) {
                        c = 15;
                        break;
                    }
                    break;
                case 1047173446:
                    if (str.equals("CHAT_ADMIN_BAN_REQUIRED")) {
                        c = 12;
                        break;
                    }
                    break;
                case 1167301807:
                    if (str.equals("USERS_TOO_MUCH")) {
                        c = 4;
                        break;
                    }
                    break;
                case 1227003815:
                    if (str.equals("USER_ID_INVALID")) {
                        c = 3;
                        break;
                    }
                    break;
                case 1253103379:
                    if (str.equals("ADMINS_TOO_MUCH")) {
                        c = 6;
                        break;
                    }
                    break;
                case 1355367367:
                    if (str.equals("CHANNELS_TOO_MUCH")) {
                        c = 18;
                        break;
                    }
                    break;
                case 1377621075:
                    if (str.equals("USER_CHANNELS_TOO_MUCH")) {
                        c = 19;
                        break;
                    }
                    break;
                case 1623167701:
                    if (str.equals("USER_NOT_MUTUAL_CONTACT")) {
                        c = 5;
                        break;
                    }
                    break;
                case 1754587486:
                    if (str.equals("CHAT_ADMIN_INVITE_REQUIRED")) {
                        c = 14;
                        break;
                    }
                    break;
                case 1916725894:
                    if (str.equals("USER_PRIVACY_RESTRICTED")) {
                        c = 8;
                        break;
                    }
                    break;
            }
            switch (c) {
                case 0:
                    builder.setMessage(LocaleController.getString("NobodyLikesSpam2", NUM));
                    builder.setNegativeButton(LocaleController.getString("MoreInfo", NUM), new DialogInterface.OnClickListener() {
                        public final void onClick(DialogInterface dialogInterface, int i) {
                            MessagesController.getInstance(BaseFragment.this.getCurrentAccount()).openByUserName("spambot", BaseFragment.this, 1);
                        }
                    });
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
                    builder.setTitle(LocaleController.getString("ChannelTooMuchTitle", NUM));
                    if (!(tLObject instanceof TLRPC.TL_channels_createChannel)) {
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
                default:
                    builder.setMessage(LocaleController.getString("ErrorOccurred", NUM) + "\n" + str);
                    break;
            }
            builder.setPositiveButton(LocaleController.getString("OK", NUM), (DialogInterface.OnClickListener) null);
            baseFragment.showDialog(builder.create(), true, (DialogInterface.OnDismissListener) null);
        }
    }

    public static Dialog createColorSelectDialog(Activity activity, long j, int i, Runnable runnable) {
        int i2;
        Activity activity2 = activity;
        long j2 = j;
        int i3 = i;
        Runnable runnable2 = runnable;
        SharedPreferences notificationsSettings = MessagesController.getNotificationsSettings(UserConfig.selectedAccount);
        if (j2 != 0) {
            if (notificationsSettings.contains("color_" + j2)) {
                i2 = notificationsSettings.getInt("color_" + j2, -16776961);
            } else if (((int) j2) < 0) {
                i2 = notificationsSettings.getInt("GroupLed", -16776961);
            } else {
                i2 = notificationsSettings.getInt("MessagesLed", -16776961);
            }
        } else if (i3 == 1) {
            i2 = notificationsSettings.getInt("MessagesLed", -16776961);
        } else if (i3 == 0) {
            i2 = notificationsSettings.getInt("GroupLed", -16776961);
        } else {
            i2 = notificationsSettings.getInt("ChannelLed", -16776961);
        }
        LinearLayout linearLayout = new LinearLayout(activity2);
        linearLayout.setOrientation(1);
        String[] strArr = {LocaleController.getString("ColorRed", NUM), LocaleController.getString("ColorOrange", NUM), LocaleController.getString("ColorYellow", NUM), LocaleController.getString("ColorGreen", NUM), LocaleController.getString("ColorCyan", NUM), LocaleController.getString("ColorBlue", NUM), LocaleController.getString("ColorViolet", NUM), LocaleController.getString("ColorPink", NUM), LocaleController.getString("ColorWhite", NUM)};
        int[] iArr = {i2};
        int i4 = 0;
        for (int i5 = 9; i4 < i5; i5 = 9) {
            RadioColorCell radioColorCell = new RadioColorCell(activity2);
            radioColorCell.setPadding(AndroidUtilities.dp(4.0f), 0, AndroidUtilities.dp(4.0f), 0);
            radioColorCell.setTag(Integer.valueOf(i4));
            int[] iArr2 = TextColorCell.colors;
            radioColorCell.setCheckColor(iArr2[i4], iArr2[i4]);
            radioColorCell.setTextAndValue(strArr[i4], i2 == TextColorCell.colorsToSave[i4]);
            linearLayout.addView(radioColorCell);
            radioColorCell.setOnClickListener(new View.OnClickListener(linearLayout, iArr) {
                private final /* synthetic */ LinearLayout f$0;
                private final /* synthetic */ int[] f$1;

                {
                    this.f$0 = r1;
                    this.f$1 = r2;
                }

                public final void onClick(View view) {
                    AlertsCreator.lambda$createColorSelectDialog$34(this.f$0, this.f$1, view);
                }
            });
            i4++;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder((Context) activity2);
        builder.setTitle(LocaleController.getString("LedColor", NUM));
        builder.setView(linearLayout);
        builder.setPositiveButton(LocaleController.getString("Set", NUM), new DialogInterface.OnClickListener(j, iArr, i, runnable) {
            private final /* synthetic */ long f$0;
            private final /* synthetic */ int[] f$1;
            private final /* synthetic */ int f$2;
            private final /* synthetic */ Runnable f$3;

            {
                this.f$0 = r1;
                this.f$1 = r3;
                this.f$2 = r4;
                this.f$3 = r5;
            }

            public final void onClick(DialogInterface dialogInterface, int i) {
                AlertsCreator.lambda$createColorSelectDialog$35(this.f$0, this.f$1, this.f$2, this.f$3, dialogInterface, i);
            }
        });
        builder.setNeutralButton(LocaleController.getString("LedDisabled", NUM), new DialogInterface.OnClickListener(j2, i3, runnable2) {
            private final /* synthetic */ long f$0;
            private final /* synthetic */ int f$1;
            private final /* synthetic */ Runnable f$2;

            {
                this.f$0 = r1;
                this.f$1 = r3;
                this.f$2 = r4;
            }

            public final void onClick(DialogInterface dialogInterface, int i) {
                AlertsCreator.lambda$createColorSelectDialog$36(this.f$0, this.f$1, this.f$2, dialogInterface, i);
            }
        });
        if (j2 != 0) {
            builder.setNegativeButton(LocaleController.getString("Default", NUM), new DialogInterface.OnClickListener(j2, runnable2) {
                private final /* synthetic */ long f$0;
                private final /* synthetic */ Runnable f$1;

                {
                    this.f$0 = r1;
                    this.f$1 = r3;
                }

                public final void onClick(DialogInterface dialogInterface, int i) {
                    AlertsCreator.lambda$createColorSelectDialog$37(this.f$0, this.f$1, dialogInterface, i);
                }
            });
        }
        return builder.create();
    }

    static /* synthetic */ void lambda$createColorSelectDialog$34(LinearLayout linearLayout, int[] iArr, View view) {
        int childCount = linearLayout.getChildCount();
        for (int i = 0; i < childCount; i++) {
            RadioColorCell radioColorCell = (RadioColorCell) linearLayout.getChildAt(i);
            radioColorCell.setChecked(radioColorCell == view, true);
        }
        iArr[0] = TextColorCell.colorsToSave[((Integer) view.getTag()).intValue()];
    }

    static /* synthetic */ void lambda$createColorSelectDialog$35(long j, int[] iArr, int i, Runnable runnable, DialogInterface dialogInterface, int i2) {
        SharedPreferences.Editor edit = MessagesController.getNotificationsSettings(UserConfig.selectedAccount).edit();
        if (j != 0) {
            edit.putInt("color_" + j, iArr[0]);
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
        SharedPreferences.Editor edit = MessagesController.getNotificationsSettings(UserConfig.selectedAccount).edit();
        if (j != 0) {
            edit.putInt("color_" + j, 0);
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
        SharedPreferences.Editor edit = MessagesController.getNotificationsSettings(UserConfig.selectedAccount).edit();
        edit.remove("color_" + j);
        edit.commit();
        if (runnable != null) {
            runnable.run();
        }
    }

    public static Dialog createVibrationSelectDialog(Activity activity, long j, boolean z, boolean z2, Runnable runnable) {
        return createVibrationSelectDialog(activity, j, j != 0 ? "vibrate_" : z ? "vibrate_group" : "vibrate_messages", runnable);
    }

    public static Dialog createVibrationSelectDialog(Activity activity, long j, String str, Runnable runnable) {
        String[] strArr;
        Activity activity2 = activity;
        long j2 = j;
        String str2 = str;
        SharedPreferences notificationsSettings = MessagesController.getNotificationsSettings(UserConfig.selectedAccount);
        int[] iArr = new int[1];
        int i = 0;
        if (j2 != 0) {
            iArr[0] = notificationsSettings.getInt(str2 + j2, 0);
            if (iArr[0] == 3) {
                iArr[0] = 2;
            } else if (iArr[0] == 2) {
                iArr[0] = 3;
            }
            strArr = new String[]{LocaleController.getString("VibrationDefault", NUM), LocaleController.getString("Short", NUM), LocaleController.getString("Long", NUM), LocaleController.getString("VibrationDisabled", NUM)};
        } else {
            iArr[0] = notificationsSettings.getInt(str2, 0);
            if (iArr[0] == 0) {
                iArr[0] = 1;
            } else if (iArr[0] == 1) {
                iArr[0] = 2;
            } else if (iArr[0] == 2) {
                iArr[0] = 0;
            }
            strArr = new String[]{LocaleController.getString("VibrationDisabled", NUM), LocaleController.getString("VibrationDefault", NUM), LocaleController.getString("Short", NUM), LocaleController.getString("Long", NUM), LocaleController.getString("OnlyIfSilent", NUM)};
        }
        String[] strArr2 = strArr;
        LinearLayout linearLayout = new LinearLayout(activity2);
        linearLayout.setOrientation(1);
        AlertDialog.Builder builder = new AlertDialog.Builder((Context) activity2);
        int i2 = 0;
        while (i2 < strArr2.length) {
            RadioColorCell radioColorCell = new RadioColorCell(activity2);
            radioColorCell.setPadding(AndroidUtilities.dp(4.0f), i, AndroidUtilities.dp(4.0f), i);
            radioColorCell.setTag(Integer.valueOf(i2));
            radioColorCell.setCheckColor(Theme.getColor("radioBackground"), Theme.getColor("dialogRadioBackgroundChecked"));
            radioColorCell.setTextAndValue(strArr2[i2], iArr[i] == i2);
            linearLayout.addView(radioColorCell);
            $$Lambda$AlertsCreator$Tk2YqVMTqwW8mYxUqnK89w364 r11 = r1;
            $$Lambda$AlertsCreator$Tk2YqVMTqwW8mYxUqnK89w364 r1 = new View.OnClickListener(iArr, j, str, builder, runnable) {
                private final /* synthetic */ int[] f$0;
                private final /* synthetic */ long f$1;
                private final /* synthetic */ String f$2;
                private final /* synthetic */ AlertDialog.Builder f$3;
                private final /* synthetic */ Runnable f$4;

                {
                    this.f$0 = r1;
                    this.f$1 = r2;
                    this.f$2 = r4;
                    this.f$3 = r5;
                    this.f$4 = r6;
                }

                public final void onClick(View view) {
                    AlertsCreator.lambda$createVibrationSelectDialog$38(this.f$0, this.f$1, this.f$2, this.f$3, this.f$4, view);
                }
            };
            radioColorCell.setOnClickListener(r11);
            i2++;
            i = 0;
            builder = builder;
            activity2 = activity;
        }
        AlertDialog.Builder builder2 = builder;
        builder2.setTitle(LocaleController.getString("Vibrate", NUM));
        builder2.setView(linearLayout);
        builder2.setPositiveButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
        return builder2.create();
    }

    static /* synthetic */ void lambda$createVibrationSelectDialog$38(int[] iArr, long j, String str, AlertDialog.Builder builder, Runnable runnable, View view) {
        iArr[0] = ((Integer) view.getTag()).intValue();
        SharedPreferences.Editor edit = MessagesController.getNotificationsSettings(UserConfig.selectedAccount).edit();
        if (j != 0) {
            if (iArr[0] == 0) {
                edit.putInt(str + j, 0);
            } else if (iArr[0] == 1) {
                edit.putInt(str + j, 1);
            } else if (iArr[0] == 2) {
                edit.putInt(str + j, 3);
            } else if (iArr[0] == 3) {
                edit.putInt(str + j, 2);
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

    public static Dialog createLocationUpdateDialog(Activity activity, TLRPC.User user, MessagesStorage.IntCallback intCallback) {
        Activity activity2 = activity;
        int[] iArr = new int[1];
        int i = 3;
        String[] strArr = {LocaleController.getString("SendLiveLocationFor15m", NUM), LocaleController.getString("SendLiveLocationFor1h", NUM), LocaleController.getString("SendLiveLocationFor8h", NUM)};
        LinearLayout linearLayout = new LinearLayout(activity2);
        linearLayout.setOrientation(1);
        TextView textView = new TextView(activity2);
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
        int i2 = 0;
        while (i2 < strArr.length) {
            RadioColorCell radioColorCell = new RadioColorCell(activity2);
            radioColorCell.setPadding(AndroidUtilities.dp(4.0f), 0, AndroidUtilities.dp(4.0f), 0);
            radioColorCell.setTag(Integer.valueOf(i2));
            radioColorCell.setCheckColor(Theme.getColor("radioBackground"), Theme.getColor("dialogRadioBackgroundChecked"));
            radioColorCell.setTextAndValue(strArr[i2], iArr[0] == i2);
            linearLayout.addView(radioColorCell);
            radioColorCell.setOnClickListener(new View.OnClickListener(iArr, linearLayout) {
                private final /* synthetic */ int[] f$0;
                private final /* synthetic */ LinearLayout f$1;

                {
                    this.f$0 = r1;
                    this.f$1 = r2;
                }

                public final void onClick(View view) {
                    AlertsCreator.lambda$createLocationUpdateDialog$39(this.f$0, this.f$1, view);
                }
            });
            i2++;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder((Context) activity2);
        builder.setTopImage((Drawable) new ShareLocationDrawable(activity2, 0), Theme.getColor("dialogTopBackground"));
        builder.setView(linearLayout);
        builder.setPositiveButton(LocaleController.getString("ShareFile", NUM), new DialogInterface.OnClickListener(iArr, intCallback) {
            private final /* synthetic */ int[] f$0;
            private final /* synthetic */ MessagesStorage.IntCallback f$1;

            {
                this.f$0 = r1;
                this.f$1 = r2;
            }

            public final void onClick(DialogInterface dialogInterface, int i) {
                AlertsCreator.lambda$createLocationUpdateDialog$40(this.f$0, this.f$1, dialogInterface, i);
            }
        });
        builder.setNeutralButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
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

    static /* synthetic */ void lambda$createLocationUpdateDialog$40(int[] iArr, MessagesStorage.IntCallback intCallback, DialogInterface dialogInterface, int i) {
        int i2;
        if (iArr[0] == 0) {
            i2 = 900;
        } else {
            i2 = iArr[0] == 1 ? 3600 : 28800;
        }
        intCallback.run(i2);
    }

    public static AlertDialog.Builder createContactsPermissionDialog(Activity activity, MessagesStorage.IntCallback intCallback) {
        AlertDialog.Builder builder = new AlertDialog.Builder((Context) activity);
        builder.setTopImage(NUM, Theme.getColor("dialogTopBackground"));
        builder.setMessage(AndroidUtilities.replaceTags(LocaleController.getString("ContactsPermissionAlert", NUM)));
        builder.setPositiveButton(LocaleController.getString("ContactsPermissionAlertContinue", NUM), new DialogInterface.OnClickListener() {
            public final void onClick(DialogInterface dialogInterface, int i) {
                MessagesStorage.IntCallback.this.run(1);
            }
        });
        builder.setNegativeButton(LocaleController.getString("ContactsPermissionAlertNotNow", NUM), new DialogInterface.OnClickListener() {
            public final void onClick(DialogInterface dialogInterface, int i) {
                MessagesStorage.IntCallback.this.run(0);
            }
        });
        return builder;
    }

    public static Dialog createFreeSpaceDialog(LaunchActivity launchActivity) {
        LaunchActivity launchActivity2 = launchActivity;
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
        String[] strArr = {LocaleController.formatPluralString("Days", 3), LocaleController.formatPluralString("Weeks", 1), LocaleController.formatPluralString("Months", 1), LocaleController.getString("LowDiskSpaceNeverRemove", NUM)};
        LinearLayout linearLayout = new LinearLayout(launchActivity2);
        linearLayout.setOrientation(1);
        TextView textView = new TextView(launchActivity2);
        textView.setText(LocaleController.getString("LowDiskSpaceTitle2", NUM));
        textView.setTextColor(Theme.getColor("dialogTextBlack"));
        textView.setTextSize(1, 16.0f);
        textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        textView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
        if (LocaleController.isRTL) {
            i2 = 5;
        }
        linearLayout.addView(textView, LayoutHelper.createLinear(-2, -2, i2 | 48, 24, 0, 24, 8));
        int i3 = 0;
        while (i3 < strArr.length) {
            RadioColorCell radioColorCell = new RadioColorCell(launchActivity2);
            radioColorCell.setPadding(AndroidUtilities.dp(4.0f), 0, AndroidUtilities.dp(4.0f), 0);
            radioColorCell.setTag(Integer.valueOf(i3));
            radioColorCell.setCheckColor(Theme.getColor("radioBackground"), Theme.getColor("dialogRadioBackgroundChecked"));
            radioColorCell.setTextAndValue(strArr[i3], iArr[0] == i3);
            linearLayout.addView(radioColorCell);
            radioColorCell.setOnClickListener(new View.OnClickListener(iArr, linearLayout) {
                private final /* synthetic */ int[] f$0;
                private final /* synthetic */ LinearLayout f$1;

                {
                    this.f$0 = r1;
                    this.f$1 = r2;
                }

                public final void onClick(View view) {
                    AlertsCreator.lambda$createFreeSpaceDialog$43(this.f$0, this.f$1, view);
                }
            });
            i3++;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder((Context) launchActivity2);
        builder.setTitle(LocaleController.getString("LowDiskSpaceTitle", NUM));
        builder.setMessage(LocaleController.getString("LowDiskSpaceMessage", NUM));
        builder.setView(linearLayout);
        builder.setPositiveButton(LocaleController.getString("OK", NUM), new DialogInterface.OnClickListener(iArr) {
            private final /* synthetic */ int[] f$0;

            {
                this.f$0 = r1;
            }

            public final void onClick(DialogInterface dialogInterface, int i) {
                SharedConfig.setKeepMedia(this.f$0[0]);
            }
        });
        builder.setNeutralButton(LocaleController.getString("ClearMediaCache", NUM), new DialogInterface.OnClickListener() {
            public final void onClick(DialogInterface dialogInterface, int i) {
                LaunchActivity.this.lambda$runLinkRequest$30$LaunchActivity(new CacheControlActivity());
            }
        });
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
        for (int i = 0; i < childCount; i++) {
            View childAt = linearLayout.getChildAt(i);
            if (childAt instanceof RadioColorCell) {
                ((RadioColorCell) childAt).setChecked(childAt == view, true);
            }
        }
    }

    public static Dialog createPrioritySelectDialog(Activity activity, long j, int i, Runnable runnable) {
        String[] strArr;
        char c;
        int i2;
        Activity activity2 = activity;
        long j2 = j;
        int i3 = i;
        SharedPreferences notificationsSettings = MessagesController.getNotificationsSettings(UserConfig.selectedAccount);
        int[] iArr = new int[1];
        int i4 = 0;
        if (j2 != 0) {
            iArr[0] = notificationsSettings.getInt("priority_" + j2, 3);
            if (iArr[0] == 3) {
                iArr[0] = 0;
            } else if (iArr[0] == 4) {
                iArr[0] = 1;
            } else {
                i2 = 5;
                if (iArr[0] == 5) {
                    iArr[0] = 2;
                } else if (iArr[0] == 0) {
                    iArr[0] = 3;
                } else {
                    iArr[0] = 4;
                }
                String[] strArr2 = new String[i2];
                strArr2[0] = LocaleController.getString("NotificationsPrioritySettings", NUM);
                strArr2[1] = LocaleController.getString("NotificationsPriorityLow", NUM);
                strArr2[2] = LocaleController.getString("NotificationsPriorityMedium", NUM);
                strArr2[3] = LocaleController.getString("NotificationsPriorityHigh", NUM);
                strArr2[4] = LocaleController.getString("NotificationsPriorityUrgent", NUM);
                strArr = strArr2;
            }
            i2 = 5;
            String[] strArr22 = new String[i2];
            strArr22[0] = LocaleController.getString("NotificationsPrioritySettings", NUM);
            strArr22[1] = LocaleController.getString("NotificationsPriorityLow", NUM);
            strArr22[2] = LocaleController.getString("NotificationsPriorityMedium", NUM);
            strArr22[3] = LocaleController.getString("NotificationsPriorityHigh", NUM);
            strArr22[4] = LocaleController.getString("NotificationsPriorityUrgent", NUM);
            strArr = strArr22;
        } else {
            if (j2 == 0) {
                if (i3 == 1) {
                    iArr[0] = notificationsSettings.getInt("priority_messages", 1);
                } else if (i3 == 0) {
                    iArr[0] = notificationsSettings.getInt("priority_group", 1);
                } else if (i3 == 2) {
                    iArr[0] = notificationsSettings.getInt("priority_channel", 1);
                }
            }
            if (iArr[0] == 4) {
                iArr[0] = 0;
            } else if (iArr[0] == 5) {
                iArr[0] = 1;
            } else {
                if (iArr[0] == 0) {
                    c = 2;
                    iArr[0] = 2;
                } else {
                    c = 2;
                    iArr[0] = 3;
                }
                String[] strArr3 = new String[4];
                strArr3[0] = LocaleController.getString("NotificationsPriorityLow", NUM);
                strArr3[1] = LocaleController.getString("NotificationsPriorityMedium", NUM);
                strArr3[c] = LocaleController.getString("NotificationsPriorityHigh", NUM);
                strArr3[3] = LocaleController.getString("NotificationsPriorityUrgent", NUM);
                strArr = strArr3;
            }
            c = 2;
            String[] strArr32 = new String[4];
            strArr32[0] = LocaleController.getString("NotificationsPriorityLow", NUM);
            strArr32[1] = LocaleController.getString("NotificationsPriorityMedium", NUM);
            strArr32[c] = LocaleController.getString("NotificationsPriorityHigh", NUM);
            strArr32[3] = LocaleController.getString("NotificationsPriorityUrgent", NUM);
            strArr = strArr32;
        }
        LinearLayout linearLayout = new LinearLayout(activity2);
        linearLayout.setOrientation(1);
        AlertDialog.Builder builder = new AlertDialog.Builder((Context) activity2);
        int i5 = 0;
        while (i5 < strArr.length) {
            RadioColorCell radioColorCell = new RadioColorCell(activity2);
            radioColorCell.setPadding(AndroidUtilities.dp(4.0f), i4, AndroidUtilities.dp(4.0f), i4);
            radioColorCell.setTag(Integer.valueOf(i5));
            radioColorCell.setCheckColor(Theme.getColor("radioBackground"), Theme.getColor("dialogRadioBackgroundChecked"));
            radioColorCell.setTextAndValue(strArr[i5], iArr[i4] == i5);
            linearLayout.addView(radioColorCell);
            $$Lambda$AlertsCreator$A8Ah02RGWIahgCBvfH0imo r13 = r1;
            AlertDialog.Builder builder2 = builder;
            $$Lambda$AlertsCreator$A8Ah02RGWIahgCBvfH0imo r1 = new View.OnClickListener(iArr, j, i, notificationsSettings, builder, runnable) {
                private final /* synthetic */ int[] f$0;
                private final /* synthetic */ long f$1;
                private final /* synthetic */ int f$2;
                private final /* synthetic */ SharedPreferences f$3;
                private final /* synthetic */ AlertDialog.Builder f$4;
                private final /* synthetic */ Runnable f$5;

                {
                    this.f$0 = r1;
                    this.f$1 = r2;
                    this.f$2 = r4;
                    this.f$3 = r5;
                    this.f$4 = r6;
                    this.f$5 = r7;
                }

                public final void onClick(View view) {
                    AlertsCreator.lambda$createPrioritySelectDialog$46(this.f$0, this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, view);
                }
            };
            radioColorCell.setOnClickListener(r13);
            i5++;
            i4 = 0;
            activity2 = activity;
            linearLayout = linearLayout;
            long j3 = j;
        }
        AlertDialog.Builder builder3 = builder;
        builder3.setTitle(LocaleController.getString("NotificationsImportance", NUM));
        builder3.setView(linearLayout);
        builder3.setPositiveButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
        return builder3.create();
    }

    static /* synthetic */ void lambda$createPrioritySelectDialog$46(int[] iArr, long j, int i, SharedPreferences sharedPreferences, AlertDialog.Builder builder, Runnable runnable, View view) {
        iArr[0] = ((Integer) view.getTag()).intValue();
        SharedPreferences.Editor edit = MessagesController.getNotificationsSettings(UserConfig.selectedAccount).edit();
        int i2 = 5;
        if (j != 0) {
            int i3 = 3;
            if (iArr[0] != 0) {
                if (iArr[0] == 1) {
                    i3 = 4;
                } else if (iArr[0] == 2) {
                    i3 = 5;
                } else {
                    i3 = iArr[0] == 3 ? 0 : 1;
                }
            }
            edit.putInt("priority_" + j, i3);
        } else {
            if (iArr[0] == 0) {
                i2 = 4;
            } else if (iArr[0] != 1) {
                i2 = iArr[0] == 2 ? 0 : 1;
            }
            if (i == 1) {
                edit.putInt("priority_messages", i2);
                iArr[0] = sharedPreferences.getInt("priority_messages", 1);
            } else if (i == 0) {
                edit.putInt("priority_group", i2);
                iArr[0] = sharedPreferences.getInt("priority_group", 1);
            } else if (i == 2) {
                edit.putInt("priority_channel", i2);
                iArr[0] = sharedPreferences.getInt("priority_channel", 1);
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
        String[] strArr = {LocaleController.getString("NoPopup", NUM), LocaleController.getString("OnlyWhenScreenOn", NUM), LocaleController.getString("OnlyWhenScreenOff", NUM), LocaleController.getString("AlwaysShowPopup", NUM)};
        LinearLayout linearLayout = new LinearLayout(activity);
        linearLayout.setOrientation(1);
        AlertDialog.Builder builder = new AlertDialog.Builder((Context) activity);
        int i2 = 0;
        while (i2 < strArr.length) {
            RadioColorCell radioColorCell = new RadioColorCell(activity);
            radioColorCell.setTag(Integer.valueOf(i2));
            radioColorCell.setPadding(AndroidUtilities.dp(4.0f), 0, AndroidUtilities.dp(4.0f), 0);
            radioColorCell.setCheckColor(Theme.getColor("radioBackground"), Theme.getColor("dialogRadioBackgroundChecked"));
            radioColorCell.setTextAndValue(strArr[i2], iArr[0] == i2);
            linearLayout.addView(radioColorCell);
            radioColorCell.setOnClickListener(new View.OnClickListener(iArr, i, builder, runnable) {
                private final /* synthetic */ int[] f$0;
                private final /* synthetic */ int f$1;
                private final /* synthetic */ AlertDialog.Builder f$2;
                private final /* synthetic */ Runnable f$3;

                {
                    this.f$0 = r1;
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                }

                public final void onClick(View view) {
                    AlertsCreator.lambda$createPopupSelectDialog$47(this.f$0, this.f$1, this.f$2, this.f$3, view);
                }
            });
            i2++;
        }
        builder.setTitle(LocaleController.getString("PopupNotification", NUM));
        builder.setView(linearLayout);
        builder.setPositiveButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
        return builder.create();
    }

    static /* synthetic */ void lambda$createPopupSelectDialog$47(int[] iArr, int i, AlertDialog.Builder builder, Runnable runnable, View view) {
        iArr[0] = ((Integer) view.getTag()).intValue();
        SharedPreferences.Editor edit = MessagesController.getNotificationsSettings(UserConfig.selectedAccount).edit();
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

    public static Dialog createSingleChoiceDialog(Activity activity, String[] strArr, String str, int i, DialogInterface.OnClickListener onClickListener) {
        LinearLayout linearLayout = new LinearLayout(activity);
        linearLayout.setOrientation(1);
        AlertDialog.Builder builder = new AlertDialog.Builder((Context) activity);
        int i2 = 0;
        while (i2 < strArr.length) {
            RadioColorCell radioColorCell = new RadioColorCell(activity);
            radioColorCell.setPadding(AndroidUtilities.dp(4.0f), 0, AndroidUtilities.dp(4.0f), 0);
            radioColorCell.setTag(Integer.valueOf(i2));
            radioColorCell.setCheckColor(Theme.getColor("radioBackground"), Theme.getColor("dialogRadioBackgroundChecked"));
            radioColorCell.setTextAndValue(strArr[i2], i == i2);
            linearLayout.addView(radioColorCell);
            radioColorCell.setOnClickListener(new View.OnClickListener(onClickListener) {
                private final /* synthetic */ DialogInterface.OnClickListener f$1;

                {
                    this.f$1 = r2;
                }

                public final void onClick(View view) {
                    AlertsCreator.lambda$createSingleChoiceDialog$48(AlertDialog.Builder.this, this.f$1, view);
                }
            });
            i2++;
        }
        builder.setTitle(str);
        builder.setView(linearLayout);
        builder.setPositiveButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
        return builder.create();
    }

    static /* synthetic */ void lambda$createSingleChoiceDialog$48(AlertDialog.Builder builder, DialogInterface.OnClickListener onClickListener, View view) {
        int intValue = ((Integer) view.getTag()).intValue();
        builder.getDismissRunnable().run();
        onClickListener.onClick((DialogInterface) null, intValue);
    }

    public static AlertDialog.Builder createTTLAlert(Context context, TLRPC.EncryptedChat encryptedChat) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(LocaleController.getString("MessageLifetime", NUM));
        NumberPicker numberPicker = new NumberPicker(context);
        numberPicker.setMinValue(0);
        numberPicker.setMaxValue(20);
        int i = encryptedChat.ttl;
        if (i <= 0 || i >= 16) {
            int i2 = encryptedChat.ttl;
            if (i2 == 30) {
                numberPicker.setValue(16);
            } else if (i2 == 60) {
                numberPicker.setValue(17);
            } else if (i2 == 3600) {
                numberPicker.setValue(18);
            } else if (i2 == 86400) {
                numberPicker.setValue(19);
            } else if (i2 == 604800) {
                numberPicker.setValue(20);
            } else if (i2 == 0) {
                numberPicker.setValue(0);
            }
        } else {
            numberPicker.setValue(i);
        }
        numberPicker.setFormatter($$Lambda$AlertsCreator$gGUuyj3b3BbVn9I3aKfktk8so9Q.INSTANCE);
        builder.setView(numberPicker);
        builder.setNegativeButton(LocaleController.getString("Done", NUM), new DialogInterface.OnClickListener(numberPicker) {
            private final /* synthetic */ NumberPicker f$1;

            {
                this.f$1 = r2;
            }

            public final void onClick(DialogInterface dialogInterface, int i) {
                AlertsCreator.lambda$createTTLAlert$50(TLRPC.EncryptedChat.this, this.f$1, dialogInterface, i);
            }
        });
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

    static /* synthetic */ void lambda$createTTLAlert$50(TLRPC.EncryptedChat encryptedChat, NumberPicker numberPicker, DialogInterface dialogInterface, int i) {
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
            SecretChatHelper.getInstance(UserConfig.selectedAccount).sendTTLMessage(encryptedChat, (TLRPC.Message) null);
            MessagesStorage.getInstance(UserConfig.selectedAccount).updateEncryptedChatTTL(encryptedChat);
        }
    }

    public static AlertDialog createAccountSelectDialog(Activity activity, AccountSelectDelegate accountSelectDelegate) {
        if (UserConfig.getActivatedAccountsCount() < 2) {
            return null;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder((Context) activity);
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
                accountSelectCell.setOnClickListener(new View.OnClickListener(alertDialogArr, dismissRunnable, accountSelectDelegate) {
                    private final /* synthetic */ AlertDialog[] f$0;
                    private final /* synthetic */ Runnable f$1;
                    private final /* synthetic */ AlertsCreator.AccountSelectDelegate f$2;

                    {
                        this.f$0 = r1;
                        this.f$1 = r2;
                        this.f$2 = r3;
                    }

                    public final void onClick(View view) {
                        AlertsCreator.lambda$createAccountSelectDialog$51(this.f$0, this.f$1, this.f$2, view);
                    }
                });
            }
        }
        builder.setTitle(LocaleController.getString("SelectAccount", NUM));
        builder.setView(linearLayout);
        builder.setPositiveButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
        AlertDialog create = builder.create();
        alertDialogArr[0] = create;
        return create;
    }

    static /* synthetic */ void lambda$createAccountSelectDialog$51(AlertDialog[] alertDialogArr, Runnable runnable, AccountSelectDelegate accountSelectDelegate, View view) {
        if (alertDialogArr[0] != null) {
            alertDialogArr[0].setOnDismissListener((DialogInterface.OnDismissListener) null);
        }
        runnable.run();
        accountSelectDelegate.didSelectAccount(((AccountSelectCell) view).getAccountNumber());
    }

    /* JADX WARNING: Removed duplicated region for block: B:140:0x027d  */
    /* JADX WARNING: Removed duplicated region for block: B:141:0x0282  */
    /* JADX WARNING: Removed duplicated region for block: B:144:0x028a  */
    /* JADX WARNING: Removed duplicated region for block: B:145:0x028f  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static void createDeleteMessagesAlert(org.telegram.ui.ActionBar.BaseFragment r34, org.telegram.tgnet.TLRPC.User r35, org.telegram.tgnet.TLRPC.Chat r36, org.telegram.tgnet.TLRPC.EncryptedChat r37, org.telegram.tgnet.TLRPC.ChatFull r38, long r39, org.telegram.messenger.MessageObject r41, android.util.SparseArray<org.telegram.messenger.MessageObject>[] r42, org.telegram.messenger.MessageObject.GroupedMessages r43, boolean r44, int r45, java.lang.Runnable r46) {
        /*
            r14 = r34
            r3 = r35
            r4 = r36
            r5 = r37
            r9 = r41
            r11 = r43
            r0 = r45
            if (r14 == 0) goto L_0x05e9
            if (r3 != 0) goto L_0x0018
            if (r4 != 0) goto L_0x0018
            if (r5 != 0) goto L_0x0018
            goto L_0x05e9
        L_0x0018:
            android.app.Activity r1 = r34.getParentActivity()
            if (r1 != 0) goto L_0x001f
            return
        L_0x001f:
            int r15 = r34.getCurrentAccount()
            org.telegram.ui.ActionBar.AlertDialog$Builder r2 = new org.telegram.ui.ActionBar.AlertDialog$Builder
            r2.<init>((android.content.Context) r1)
            r6 = 1
            r7 = 0
            if (r11 == 0) goto L_0x0033
            java.util.ArrayList<org.telegram.messenger.MessageObject> r8 = r11.messages
            int r8 = r8.size()
            goto L_0x0044
        L_0x0033:
            if (r9 == 0) goto L_0x0037
            r8 = 1
            goto L_0x0044
        L_0x0037:
            r8 = r42[r7]
            int r8 = r8.size()
            r10 = r42[r6]
            int r10 = r10.size()
            int r8 = r8 + r10
        L_0x0044:
            if (r5 == 0) goto L_0x004f
            int r10 = r5.id
            long r12 = (long) r10
            r10 = 32
            long r12 = r12 << r10
        L_0x004c:
            r20 = r12
            goto L_0x0059
        L_0x004f:
            if (r3 == 0) goto L_0x0054
            int r10 = r3.id
            goto L_0x0057
        L_0x0054:
            int r10 = r4.id
            int r10 = -r10
        L_0x0057:
            long r12 = (long) r10
            goto L_0x004c
        L_0x0059:
            r10 = 3
            boolean[] r12 = new boolean[r10]
            boolean[] r13 = new boolean[r6]
            if (r3 == 0) goto L_0x006a
            org.telegram.messenger.MessagesController r7 = org.telegram.messenger.MessagesController.getInstance(r15)
            boolean r7 = r7.canRevokePmInbox
            if (r7 == 0) goto L_0x006a
            r7 = 1
            goto L_0x006b
        L_0x006a:
            r7 = 0
        L_0x006b:
            if (r3 == 0) goto L_0x0074
            org.telegram.messenger.MessagesController r10 = org.telegram.messenger.MessagesController.getInstance(r15)
            int r10 = r10.revokeTimePmLimit
            goto L_0x007a
        L_0x0074:
            org.telegram.messenger.MessagesController r10 = org.telegram.messenger.MessagesController.getInstance(r15)
            int r10 = r10.revokeTimeLimit
        L_0x007a:
            if (r5 != 0) goto L_0x0087
            if (r3 == 0) goto L_0x0087
            if (r7 == 0) goto L_0x0087
            r6 = 2147483647(0x7fffffff, float:NaN)
            if (r10 != r6) goto L_0x0087
            r6 = 1
            goto L_0x0088
        L_0x0087:
            r6 = 0
        L_0x0088:
            r17 = 1098907648(0x41800000, float:16.0)
            r18 = 1090519040(0x41000000, float:8.0)
            java.lang.String r11 = ""
            r30 = r8
            if (r4 == 0) goto L_0x0344
            boolean r8 = r4.megagroup
            if (r8 == 0) goto L_0x0344
            if (r44 != 0) goto L_0x0344
            boolean r7 = org.telegram.messenger.ChatObject.canBlockUsers(r36)
            org.telegram.tgnet.ConnectionsManager r8 = org.telegram.tgnet.ConnectionsManager.getInstance(r15)
            int r8 = r8.getCurrentTime()
            if (r9 == 0) goto L_0x0105
            r31 = r6
            org.telegram.tgnet.TLRPC$Message r6 = r9.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r6 = r6.action
            if (r6 == 0) goto L_0x00c1
            boolean r3 = r6 instanceof org.telegram.tgnet.TLRPC.TL_messageActionEmpty
            if (r3 != 0) goto L_0x00c1
            boolean r3 = r6 instanceof org.telegram.tgnet.TLRPC.TL_messageActionChatDeleteUser
            if (r3 != 0) goto L_0x00c1
            boolean r3 = r6 instanceof org.telegram.tgnet.TLRPC.TL_messageActionChatJoinedByLink
            if (r3 != 0) goto L_0x00c1
            boolean r3 = r6 instanceof org.telegram.tgnet.TLRPC.TL_messageActionChatAddUser
            if (r3 == 0) goto L_0x00bf
            goto L_0x00c1
        L_0x00bf:
            r3 = 0
            goto L_0x00d1
        L_0x00c1:
            org.telegram.messenger.MessagesController r3 = org.telegram.messenger.MessagesController.getInstance(r15)
            org.telegram.tgnet.TLRPC$Message r6 = r9.messageOwner
            int r6 = r6.from_id
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            org.telegram.tgnet.TLRPC$User r3 = r3.getUser(r6)
        L_0x00d1:
            boolean r6 = r41.isSendError()
            if (r6 != 0) goto L_0x00f8
            long r22 = r41.getDialogId()
            int r6 = (r22 > r39 ? 1 : (r22 == r39 ? 0 : -1))
            if (r6 != 0) goto L_0x00f8
            org.telegram.tgnet.TLRPC$Message r6 = r9.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r6 = r6.action
            if (r6 == 0) goto L_0x00e9
            boolean r6 = r6 instanceof org.telegram.tgnet.TLRPC.TL_messageActionEmpty
            if (r6 == 0) goto L_0x00f8
        L_0x00e9:
            boolean r6 = r41.isOut()
            if (r6 == 0) goto L_0x00f8
            org.telegram.tgnet.TLRPC$Message r6 = r9.messageOwner
            int r6 = r6.date
            int r8 = r8 - r6
            if (r8 > r10) goto L_0x00f8
            r6 = 1
            goto L_0x00f9
        L_0x00f8:
            r6 = 0
        L_0x00f9:
            if (r6 == 0) goto L_0x00fd
            r6 = 1
            goto L_0x00fe
        L_0x00fd:
            r6 = 0
        L_0x00fe:
            r32 = r2
            r5 = r6
            r22 = r13
            goto L_0x0197
        L_0x0105:
            r31 = r6
            r3 = 1
            r6 = -1
        L_0x0109:
            if (r3 < 0) goto L_0x0147
            r9 = r6
            r6 = 0
        L_0x010d:
            r19 = r42[r3]
            int r5 = r19.size()
            r22 = r13
            if (r6 >= r5) goto L_0x0139
            r5 = r42[r3]
            java.lang.Object r5 = r5.valueAt(r6)
            org.telegram.messenger.MessageObject r5 = (org.telegram.messenger.MessageObject) r5
            r13 = -1
            if (r9 != r13) goto L_0x0126
            org.telegram.tgnet.TLRPC$Message r9 = r5.messageOwner
            int r9 = r9.from_id
        L_0x0126:
            if (r9 < 0) goto L_0x0136
            org.telegram.tgnet.TLRPC$Message r5 = r5.messageOwner
            int r5 = r5.from_id
            if (r9 == r5) goto L_0x012f
            goto L_0x0136
        L_0x012f:
            int r6 = r6 + 1
            r5 = r37
            r13 = r22
            goto L_0x010d
        L_0x0136:
            r5 = -2
            r6 = -2
            goto L_0x013b
        L_0x0139:
            r6 = r9
            r5 = -2
        L_0x013b:
            if (r6 != r5) goto L_0x013e
            goto L_0x0149
        L_0x013e:
            int r3 = r3 + -1
            r5 = r37
            r9 = r41
            r13 = r22
            goto L_0x0109
        L_0x0147:
            r22 = r13
        L_0x0149:
            r3 = 1
            r5 = 0
        L_0x014b:
            if (r3 < 0) goto L_0x0183
            r9 = r5
            r5 = 0
        L_0x014f:
            r13 = r42[r3]
            int r13 = r13.size()
            if (r5 >= r13) goto L_0x017d
            r13 = r42[r3]
            java.lang.Object r13 = r13.valueAt(r5)
            org.telegram.messenger.MessageObject r13 = (org.telegram.messenger.MessageObject) r13
            r32 = r2
            r2 = 1
            if (r3 != r2) goto L_0x0178
            boolean r2 = r13.isOut()
            if (r2 == 0) goto L_0x0178
            org.telegram.tgnet.TLRPC$Message r2 = r13.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r13 = r2.action
            if (r13 != 0) goto L_0x0178
            int r2 = r2.date
            int r2 = r8 - r2
            if (r2 > r10) goto L_0x0178
            int r9 = r9 + 1
        L_0x0178:
            int r5 = r5 + 1
            r2 = r32
            goto L_0x014f
        L_0x017d:
            r32 = r2
            int r3 = r3 + -1
            r5 = r9
            goto L_0x014b
        L_0x0183:
            r32 = r2
            r2 = -1
            if (r6 == r2) goto L_0x0196
            org.telegram.messenger.MessagesController r2 = org.telegram.messenger.MessagesController.getInstance(r15)
            java.lang.Integer r3 = java.lang.Integer.valueOf(r6)
            org.telegram.tgnet.TLRPC$User r2 = r2.getUser(r3)
            r3 = r2
            goto L_0x0197
        L_0x0196:
            r3 = 0
        L_0x0197:
            if (r3 == 0) goto L_0x02cb
            int r2 = r3.id
            org.telegram.messenger.UserConfig r6 = org.telegram.messenger.UserConfig.getInstance(r15)
            int r6 = r6.getClientUserId()
            if (r2 == r6) goto L_0x02cb
            r2 = 1
            if (r0 != r2) goto L_0x020b
            boolean r6 = r4.creator
            if (r6 != 0) goto L_0x020b
            org.telegram.ui.ActionBar.AlertDialog[] r13 = new org.telegram.ui.ActionBar.AlertDialog[r2]
            org.telegram.ui.ActionBar.AlertDialog r0 = new org.telegram.ui.ActionBar.AlertDialog
            r2 = 3
            r0.<init>(r1, r2)
            r1 = 0
            r13[r1] = r0
            org.telegram.tgnet.TLRPC$TL_channels_getParticipant r12 = new org.telegram.tgnet.TLRPC$TL_channels_getParticipant
            r12.<init>()
            org.telegram.tgnet.TLRPC$InputChannel r0 = org.telegram.messenger.MessagesController.getInputChannel((org.telegram.tgnet.TLRPC.Chat) r36)
            r12.channel = r0
            org.telegram.messenger.MessagesController r0 = org.telegram.messenger.MessagesController.getInstance(r15)
            org.telegram.tgnet.TLRPC$InputUser r0 = r0.getInputUser((org.telegram.tgnet.TLRPC.User) r3)
            r12.user_id = r0
            org.telegram.tgnet.ConnectionsManager r11 = org.telegram.tgnet.ConnectionsManager.getInstance(r15)
            org.telegram.ui.Components.-$$Lambda$AlertsCreator$PfqFazdn9NQ97ARqEHrnjPsSobk r10 = new org.telegram.ui.Components.-$$Lambda$AlertsCreator$PfqFazdn9NQ97ARqEHrnjPsSobk
            r0 = r10
            r1 = r13
            r2 = r34
            r3 = r35
            r4 = r36
            r5 = r37
            r6 = r38
            r7 = r39
            r9 = r41
            r14 = r10
            r10 = r42
            r19 = r15
            r15 = r11
            r11 = r43
            r16 = r14
            r14 = r12
            r12 = r44
            r33 = r13
            r13 = r46
            r0.<init>(r1, r2, r3, r4, r5, r6, r7, r9, r10, r11, r12, r13)
            r0 = r16
            int r0 = r15.sendRequest(r14, r0)
            org.telegram.ui.Components.-$$Lambda$AlertsCreator$s2TKXlQWhiQaw3CtiAviNd-var_E r1 = new org.telegram.ui.Components.-$$Lambda$AlertsCreator$s2TKXlQWhiQaw3CtiAviNd-var_E
            r6 = r19
            r3 = r33
            r1.<init>(r3, r6, r0, r2)
            r2 = 1000(0x3e8, double:4.94E-321)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r1, r2)
            return
        L_0x020b:
            r2 = r14
            r6 = r15
            android.widget.FrameLayout r8 = new android.widget.FrameLayout
            r8.<init>(r1)
            r9 = 0
            r10 = 0
        L_0x0214:
            r13 = 3
            if (r9 >= r13) goto L_0x02c1
            r14 = 2
            if (r0 == r14) goto L_0x021c
            if (r7 != 0) goto L_0x0222
        L_0x021c:
            if (r9 != 0) goto L_0x0222
            r19 = r7
            goto L_0x02b9
        L_0x0222:
            org.telegram.ui.Cells.CheckBoxCell r14 = new org.telegram.ui.Cells.CheckBoxCell
            r15 = 1
            r14.<init>(r1, r15)
            r39 = 0
            android.graphics.drawable.Drawable r13 = org.telegram.ui.ActionBar.Theme.getSelectorDrawable(r39)
            r14.setBackgroundDrawable(r13)
            java.lang.Integer r13 = java.lang.Integer.valueOf(r9)
            r14.setTag(r13)
            if (r9 != 0) goto L_0x024a
            r13 = 2131624859(0x7f0e039b, float:1.887691E38)
            java.lang.String r15 = "DeleteBanUser"
            java.lang.String r13 = org.telegram.messenger.LocaleController.getString(r15, r13)
            r15 = 0
            r14.setText(r13, r11, r15, r15)
        L_0x0247:
            r19 = r7
            goto L_0x0279
        L_0x024a:
            r13 = 1
            r15 = 0
            if (r9 != r13) goto L_0x025b
            r13 = 2131624881(0x7f0e03b1, float:1.8876954E38)
            java.lang.String r0 = "DeleteReportSpam"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r0, r13)
            r14.setText(r0, r11, r15, r15)
            goto L_0x0247
        L_0x025b:
            r0 = 2
            if (r9 != r0) goto L_0x0247
            r13 = 1
            java.lang.Object[] r0 = new java.lang.Object[r13]
            java.lang.String r13 = r3.first_name
            r19 = r7
            java.lang.String r7 = r3.last_name
            java.lang.String r7 = org.telegram.messenger.ContactsController.formatName(r13, r7)
            r0[r15] = r7
            java.lang.String r7 = "DeleteAllFrom"
            r13 = 2131624848(0x7f0e0390, float:1.8876887E38)
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r7, r13, r0)
            r14.setText(r0, r11, r15, r15)
        L_0x0279:
            boolean r0 = org.telegram.messenger.LocaleController.isRTL
            if (r0 == 0) goto L_0x0282
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r17)
            goto L_0x0286
        L_0x0282:
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r18)
        L_0x0286:
            boolean r7 = org.telegram.messenger.LocaleController.isRTL
            if (r7 == 0) goto L_0x028f
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r18)
            goto L_0x0293
        L_0x028f:
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r17)
        L_0x0293:
            r13 = 0
            r14.setPadding(r0, r13, r7, r13)
            r23 = -1
            r24 = 1111490560(0x42400000, float:48.0)
            r25 = 51
            r26 = 0
            int r0 = r10 * 48
            float r0 = (float) r0
            r28 = 0
            r29 = 0
            r27 = r0
            android.widget.FrameLayout$LayoutParams r0 = org.telegram.ui.Components.LayoutHelper.createFrame(r23, r24, r25, r26, r27, r28, r29)
            r8.addView(r14, r0)
            org.telegram.ui.Components.-$$Lambda$AlertsCreator$VV1-P4Ux4zkJ-q55i6EgDEwfAZA r0 = new org.telegram.ui.Components.-$$Lambda$AlertsCreator$VV1-P4Ux4zkJ-q55i6EgDEwfAZA
            r0.<init>(r12)
            r14.setOnClickListener(r0)
            int r10 = r10 + 1
        L_0x02b9:
            int r9 = r9 + 1
            r0 = r45
            r7 = r19
            goto L_0x0214
        L_0x02c1:
            r0 = r32
            r0.setView(r8)
            r9 = r22
            r1 = 0
            goto L_0x033a
        L_0x02cb:
            r2 = r14
            r6 = r15
            r0 = r32
            if (r5 <= 0) goto L_0x0336
            android.widget.FrameLayout r7 = new android.widget.FrameLayout
            r7.<init>(r1)
            org.telegram.ui.Cells.CheckBoxCell r8 = new org.telegram.ui.Cells.CheckBoxCell
            r9 = 1
            r8.<init>(r1, r9)
            r1 = 0
            android.graphics.drawable.Drawable r9 = org.telegram.ui.ActionBar.Theme.getSelectorDrawable(r1)
            r8.setBackgroundDrawable(r9)
            r9 = 2131624873(0x7f0e03a9, float:1.8876938E38)
            java.lang.String r10 = "DeleteMessagesOption"
            java.lang.String r9 = org.telegram.messenger.LocaleController.getString(r10, r9)
            r8.setText(r9, r11, r1, r1)
            boolean r1 = org.telegram.messenger.LocaleController.isRTL
            if (r1 == 0) goto L_0x02f9
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r17)
            goto L_0x02fd
        L_0x02f9:
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r18)
        L_0x02fd:
            boolean r9 = org.telegram.messenger.LocaleController.isRTL
            if (r9 == 0) goto L_0x0306
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r18)
            goto L_0x030a
        L_0x0306:
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r17)
        L_0x030a:
            r10 = 0
            r8.setPadding(r1, r10, r9, r10)
            r13 = -1
            r14 = 1111490560(0x42400000, float:48.0)
            r15 = 51
            r16 = 0
            r17 = 0
            r18 = 0
            r19 = 0
            android.widget.FrameLayout$LayoutParams r1 = org.telegram.ui.Components.LayoutHelper.createFrame(r13, r14, r15, r16, r17, r18, r19)
            r7.addView(r8, r1)
            org.telegram.ui.Components.-$$Lambda$AlertsCreator$oLtqhGeaZR0ainTSrxnNxCm_SWk r1 = new org.telegram.ui.Components.-$$Lambda$AlertsCreator$oLtqhGeaZR0ainTSrxnNxCm_SWk
            r9 = r22
            r1.<init>(r9)
            r8.setOnClickListener(r1)
            r0.setView(r7)
            r1 = 9
            r0.setCustomViewOffset(r1)
            r1 = 1
            goto L_0x033a
        L_0x0336:
            r9 = r22
            r1 = 0
            r3 = 0
        L_0x033a:
            r25 = r3
            r7 = r5
            r19 = r6
            r8 = r30
            r3 = 0
            goto L_0x04bd
        L_0x0344:
            r0 = r2
            r31 = r6
            r9 = r13
            r2 = r14
            r6 = r15
            if (r44 != 0) goto L_0x04b4
            boolean r3 = org.telegram.messenger.ChatObject.isChannel(r36)
            if (r3 != 0) goto L_0x04b4
            if (r37 != 0) goto L_0x04b4
            org.telegram.tgnet.ConnectionsManager r3 = org.telegram.tgnet.ConnectionsManager.getInstance(r6)
            int r3 = r3.getCurrentTime()
            r5 = r35
            if (r5 == 0) goto L_0x0370
            int r8 = r5.id
            org.telegram.messenger.UserConfig r13 = org.telegram.messenger.UserConfig.getInstance(r6)
            int r13 = r13.getClientUserId()
            if (r8 == r13) goto L_0x0370
            boolean r8 = r5.bot
            if (r8 == 0) goto L_0x0372
        L_0x0370:
            if (r4 == 0) goto L_0x0418
        L_0x0372:
            r8 = r41
            if (r8 == 0) goto L_0x03b1
            boolean r13 = r41.isSendError()
            if (r13 != 0) goto L_0x03a1
            org.telegram.tgnet.TLRPC$Message r13 = r8.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r13 = r13.action
            if (r13 == 0) goto L_0x038a
            boolean r14 = r13 instanceof org.telegram.tgnet.TLRPC.TL_messageActionEmpty
            if (r14 != 0) goto L_0x038a
            boolean r13 = r13 instanceof org.telegram.tgnet.TLRPC.TL_messageActionPhoneCall
            if (r13 == 0) goto L_0x03a1
        L_0x038a:
            boolean r13 = r41.isOut()
            if (r13 != 0) goto L_0x0398
            if (r7 != 0) goto L_0x0398
            boolean r7 = org.telegram.messenger.ChatObject.hasAdminRights(r36)
            if (r7 == 0) goto L_0x03a1
        L_0x0398:
            org.telegram.tgnet.TLRPC$Message r7 = r8.messageOwner
            int r7 = r7.date
            int r3 = r3 - r7
            if (r3 > r10) goto L_0x03a1
            r3 = 1
            goto L_0x03a2
        L_0x03a1:
            r3 = 0
        L_0x03a2:
            if (r3 == 0) goto L_0x03a6
            r7 = 1
            goto L_0x03a7
        L_0x03a6:
            r7 = 0
        L_0x03a7:
            boolean r3 = r41.isOut()
            r10 = 1
            r3 = r3 ^ r10
            r19 = r6
            goto L_0x041c
        L_0x03b1:
            r13 = 1
            r14 = 0
            r15 = 0
        L_0x03b4:
            if (r13 < 0) goto L_0x0413
            r16 = r15
            r15 = r14
            r14 = 0
        L_0x03ba:
            r19 = r42[r13]
            int r5 = r19.size()
            if (r14 >= r5) goto L_0x0407
            r5 = r42[r13]
            java.lang.Object r5 = r5.valueAt(r14)
            org.telegram.messenger.MessageObject r5 = (org.telegram.messenger.MessageObject) r5
            r19 = r6
            org.telegram.tgnet.TLRPC$Message r6 = r5.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r6 = r6.action
            if (r6 == 0) goto L_0x03db
            boolean r8 = r6 instanceof org.telegram.tgnet.TLRPC.TL_messageActionEmpty
            if (r8 != 0) goto L_0x03db
            boolean r6 = r6 instanceof org.telegram.tgnet.TLRPC.TL_messageActionPhoneCall
            if (r6 != 0) goto L_0x03db
            goto L_0x03fe
        L_0x03db:
            boolean r6 = r5.isOut()
            if (r6 != 0) goto L_0x03eb
            if (r7 != 0) goto L_0x03eb
            if (r4 == 0) goto L_0x03fe
            boolean r6 = org.telegram.messenger.ChatObject.canBlockUsers(r36)
            if (r6 == 0) goto L_0x03fe
        L_0x03eb:
            org.telegram.tgnet.TLRPC$Message r6 = r5.messageOwner
            int r6 = r6.date
            int r6 = r3 - r6
            if (r6 > r10) goto L_0x03fe
            int r16 = r16 + 1
            if (r15 != 0) goto L_0x03fe
            boolean r5 = r5.isOut()
            if (r5 != 0) goto L_0x03fe
            r15 = 1
        L_0x03fe:
            int r14 = r14 + 1
            r5 = r35
            r8 = r41
            r6 = r19
            goto L_0x03ba
        L_0x0407:
            r19 = r6
            int r13 = r13 + -1
            r5 = r35
            r8 = r41
            r14 = r15
            r15 = r16
            goto L_0x03b4
        L_0x0413:
            r19 = r6
            r3 = r14
            r7 = r15
            goto L_0x041c
        L_0x0418:
            r19 = r6
            r3 = 0
            r7 = 0
        L_0x041c:
            if (r7 <= 0) goto L_0x04b0
            android.widget.FrameLayout r5 = new android.widget.FrameLayout
            r5.<init>(r1)
            org.telegram.ui.Cells.CheckBoxCell r6 = new org.telegram.ui.Cells.CheckBoxCell
            r8 = 1
            r6.<init>(r1, r8)
            r1 = 0
            android.graphics.drawable.Drawable r10 = org.telegram.ui.ActionBar.Theme.getSelectorDrawable(r1)
            r6.setBackgroundDrawable(r10)
            if (r31 == 0) goto L_0x044a
            r10 = 2131624874(0x7f0e03aa, float:1.887694E38)
            java.lang.Object[] r13 = new java.lang.Object[r8]
            java.lang.String r8 = org.telegram.messenger.UserObject.getFirstName(r35)
            r13[r1] = r8
            java.lang.String r8 = "DeleteMessagesOptionAlso"
            java.lang.String r8 = org.telegram.messenger.LocaleController.formatString(r8, r10, r13)
            r6.setText(r8, r11, r1, r1)
            r8 = r30
            goto L_0x046b
        L_0x044a:
            r8 = r30
            if (r4 == 0) goto L_0x045f
            if (r3 != 0) goto L_0x0452
            if (r7 != r8) goto L_0x045f
        L_0x0452:
            r10 = 2131624865(0x7f0e03a1, float:1.8876922E38)
            java.lang.String r13 = "DeleteForAll"
            java.lang.String r10 = org.telegram.messenger.LocaleController.getString(r13, r10)
            r6.setText(r10, r11, r1, r1)
            goto L_0x046b
        L_0x045f:
            r10 = 2131624873(0x7f0e03a9, float:1.8876938E38)
            java.lang.String r13 = "DeleteMessagesOption"
            java.lang.String r10 = org.telegram.messenger.LocaleController.getString(r13, r10)
            r6.setText(r10, r11, r1, r1)
        L_0x046b:
            boolean r1 = org.telegram.messenger.LocaleController.isRTL
            if (r1 == 0) goto L_0x0474
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r17)
            goto L_0x0478
        L_0x0474:
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r18)
        L_0x0478:
            boolean r10 = org.telegram.messenger.LocaleController.isRTL
            if (r10 == 0) goto L_0x0481
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r18)
            goto L_0x0485
        L_0x0481:
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r17)
        L_0x0485:
            r11 = 0
            r6.setPadding(r1, r11, r10, r11)
            r22 = -1
            r23 = 1111490560(0x42400000, float:48.0)
            r24 = 51
            r25 = 0
            r26 = 0
            r27 = 0
            r28 = 0
            android.widget.FrameLayout$LayoutParams r1 = org.telegram.ui.Components.LayoutHelper.createFrame(r22, r23, r24, r25, r26, r27, r28)
            r5.addView(r6, r1)
            org.telegram.ui.Components.-$$Lambda$AlertsCreator$4OHKaYeK4rq2SjIUdvqrdqZQdMA r1 = new org.telegram.ui.Components.-$$Lambda$AlertsCreator$4OHKaYeK4rq2SjIUdvqrdqZQdMA
            r1.<init>(r9)
            r6.setOnClickListener(r1)
            r0.setView(r5)
            r1 = 9
            r0.setCustomViewOffset(r1)
            r1 = 1
            goto L_0x04bb
        L_0x04b0:
            r8 = r30
            r1 = 0
            goto L_0x04bb
        L_0x04b4:
            r19 = r6
            r8 = r30
            r1 = 0
            r3 = 0
            r7 = 0
        L_0x04bb:
            r25 = 0
        L_0x04bd:
            r5 = 2131624843(0x7f0e038b, float:1.8876877E38)
            java.lang.String r6 = "Delete"
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r6, r5)
            org.telegram.ui.Components.-$$Lambda$AlertsCreator$broXPqwNv8W5CV_H317xOPkk6GM r6 = new org.telegram.ui.Components.-$$Lambda$AlertsCreator$broXPqwNv8W5CV_H317xOPkk6GM
            r10 = r19
            r15 = r6
            r16 = r41
            r17 = r43
            r18 = r37
            r22 = r9
            r23 = r44
            r24 = r42
            r26 = r12
            r27 = r36
            r28 = r38
            r29 = r46
            r15.<init>(r17, r18, r19, r20, r22, r23, r24, r25, r26, r27, r28, r29)
            r0.setPositiveButton(r5, r6)
            java.lang.String r5 = "messages"
            r6 = 1
            if (r8 != r6) goto L_0x04f7
            r9 = 2131624882(0x7f0e03b2, float:1.8876956E38)
            java.lang.String r10 = "DeleteSingleMessagesTitle"
            java.lang.String r9 = org.telegram.messenger.LocaleController.getString(r10, r9)
            r0.setTitle(r9)
            goto L_0x050c
        L_0x04f7:
            r9 = 2131624878(0x7f0e03ae, float:1.8876948E38)
            java.lang.Object[] r10 = new java.lang.Object[r6]
            java.lang.String r6 = org.telegram.messenger.LocaleController.formatPluralString(r5, r8)
            r11 = 0
            r10[r11] = r6
            java.lang.String r6 = "DeleteMessagesTitle"
            java.lang.String r6 = org.telegram.messenger.LocaleController.formatString(r6, r9, r10)
            r0.setTitle(r6)
        L_0x050c:
            r6 = 2131624246(0x7f0e0136, float:1.8875666E38)
            java.lang.String r9 = "AreYouSureDeleteSingleMessage"
            r10 = 2131624240(0x7f0e0130, float:1.8875654E38)
            java.lang.String r11 = "AreYouSureDeleteFewMessages"
            if (r4 == 0) goto L_0x054b
            if (r3 == 0) goto L_0x054b
            if (r1 == 0) goto L_0x0536
            if (r7 == r8) goto L_0x0536
            r1 = 2131624877(0x7f0e03ad, float:1.8876946E38)
            r3 = 1
            java.lang.Object[] r3 = new java.lang.Object[r3]
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatPluralString(r5, r7)
            r5 = 0
            r3[r5] = r4
            java.lang.String r4 = "DeleteMessagesTextGroupPart"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r4, r1, r3)
            r0.setMessage(r1)
            goto L_0x05c3
        L_0x0536:
            r3 = 1
            if (r8 != r3) goto L_0x0542
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r9, r6)
            r0.setMessage(r1)
            goto L_0x05c3
        L_0x0542:
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r11, r10)
            r0.setMessage(r1)
            goto L_0x05c3
        L_0x054b:
            if (r1 == 0) goto L_0x058c
            if (r31 != 0) goto L_0x058c
            if (r7 == r8) goto L_0x058c
            if (r4 == 0) goto L_0x056a
            r1 = 2131624876(0x7f0e03ac, float:1.8876944E38)
            r3 = 1
            java.lang.Object[] r3 = new java.lang.Object[r3]
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatPluralString(r5, r7)
            r6 = 0
            r3[r6] = r4
            java.lang.String r4 = "DeleteMessagesTextGroup"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r4, r1, r3)
            r0.setMessage(r1)
            goto L_0x05c3
        L_0x056a:
            r6 = 0
            r1 = 2131624875(0x7f0e03ab, float:1.8876942E38)
            r3 = 2
            java.lang.Object[] r3 = new java.lang.Object[r3]
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatPluralString(r5, r7)
            r3[r6] = r4
            java.lang.String r4 = org.telegram.messenger.UserObject.getFirstName(r35)
            r5 = 1
            r3[r5] = r4
            java.lang.String r4 = "DeleteMessagesText"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r4, r1, r3)
            android.text.SpannableStringBuilder r1 = org.telegram.messenger.AndroidUtilities.replaceTags(r1)
            r0.setMessage(r1)
            goto L_0x05c3
        L_0x058c:
            if (r4 == 0) goto L_0x05b1
            boolean r1 = r4.megagroup
            if (r1 == 0) goto L_0x05b1
            if (r44 != 0) goto L_0x05b1
            r1 = 1
            if (r8 != r1) goto L_0x05a4
            r1 = 2131624247(0x7f0e0137, float:1.8875668E38)
            java.lang.String r3 = "AreYouSureDeleteSingleMessageMega"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r3, r1)
            r0.setMessage(r1)
            goto L_0x05c3
        L_0x05a4:
            r1 = 2131624241(0x7f0e0131, float:1.8875656E38)
            java.lang.String r3 = "AreYouSureDeleteFewMessagesMega"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r3, r1)
            r0.setMessage(r1)
            goto L_0x05c3
        L_0x05b1:
            r1 = 1
            if (r8 != r1) goto L_0x05bc
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r9, r6)
            r0.setMessage(r1)
            goto L_0x05c3
        L_0x05bc:
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r11, r10)
            r0.setMessage(r1)
        L_0x05c3:
            r1 = 2131624476(0x7f0e021c, float:1.8876133E38)
            java.lang.String r3 = "Cancel"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r3, r1)
            r3 = 0
            r0.setNegativeButton(r1, r3)
            org.telegram.ui.ActionBar.AlertDialog r0 = r0.create()
            r2.showDialog(r0)
            r1 = -1
            android.view.View r0 = r0.getButton(r1)
            android.widget.TextView r0 = (android.widget.TextView) r0
            if (r0 == 0) goto L_0x05e9
            java.lang.String r1 = "dialogTextRed2"
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r1)
            r0.setTextColor(r1)
        L_0x05e9:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.AlertsCreator.createDeleteMessagesAlert(org.telegram.ui.ActionBar.BaseFragment, org.telegram.tgnet.TLRPC$User, org.telegram.tgnet.TLRPC$Chat, org.telegram.tgnet.TLRPC$EncryptedChat, org.telegram.tgnet.TLRPC$ChatFull, long, org.telegram.messenger.MessageObject, android.util.SparseArray[], org.telegram.messenger.MessageObject$GroupedMessages, boolean, int, java.lang.Runnable):void");
    }

    static /* synthetic */ void lambda$null$52(AlertDialog[] alertDialogArr, TLObject tLObject, BaseFragment baseFragment, TLRPC.User user, TLRPC.Chat chat, TLRPC.EncryptedChat encryptedChat, TLRPC.ChatFull chatFull, long j, MessageObject messageObject, SparseArray[] sparseArrayArr, MessageObject.GroupedMessages groupedMessages, boolean z, Runnable runnable) {
        int i;
        try {
            alertDialogArr[0].dismiss();
        } catch (Throwable unused) {
        }
        alertDialogArr[0] = null;
        if (tLObject != null) {
            TLRPC.ChannelParticipant channelParticipant = ((TLRPC.TL_channels_channelParticipant) tLObject).participant;
            if (!(channelParticipant instanceof TLRPC.TL_channelParticipantAdmin) && !(channelParticipant instanceof TLRPC.TL_channelParticipantCreator)) {
                i = 0;
                createDeleteMessagesAlert(baseFragment, user, chat, encryptedChat, chatFull, j, messageObject, sparseArrayArr, groupedMessages, z, i, runnable);
            }
        }
        i = 2;
        createDeleteMessagesAlert(baseFragment, user, chat, encryptedChat, chatFull, j, messageObject, sparseArrayArr, groupedMessages, z, i, runnable);
    }

    static /* synthetic */ void lambda$createDeleteMessagesAlert$55(AlertDialog[] alertDialogArr, int i, int i2, BaseFragment baseFragment) {
        if (alertDialogArr[0] != null) {
            alertDialogArr[0].setOnCancelListener(new DialogInterface.OnCancelListener(i, i2) {
                private final /* synthetic */ int f$0;
                private final /* synthetic */ int f$1;

                {
                    this.f$0 = r1;
                    this.f$1 = r2;
                }

                public final void onCancel(DialogInterface dialogInterface) {
                    ConnectionsManager.getInstance(this.f$0).cancelRequest(this.f$1, true);
                }
            });
            baseFragment.showDialog(alertDialogArr[0]);
        }
    }

    static /* synthetic */ void lambda$createDeleteMessagesAlert$56(boolean[] zArr, View view) {
        if (view.isEnabled()) {
            CheckBoxCell checkBoxCell = (CheckBoxCell) view;
            Integer num = (Integer) checkBoxCell.getTag();
            zArr[num.intValue()] = !zArr[num.intValue()];
            checkBoxCell.setChecked(zArr[num.intValue()], true);
        }
    }

    static /* synthetic */ void lambda$createDeleteMessagesAlert$57(boolean[] zArr, View view) {
        zArr[0] = !zArr[0];
        ((CheckBoxCell) view).setChecked(zArr[0], true);
    }

    static /* synthetic */ void lambda$createDeleteMessagesAlert$58(boolean[] zArr, View view) {
        zArr[0] = !zArr[0];
        ((CheckBoxCell) view).setChecked(zArr[0], true);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:34:0x00c9, code lost:
        r0 = ((org.telegram.messenger.MessageObject) r27[r16].get(r7.get(r8).intValue())).messageOwner.to_id.channel_id;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    static /* synthetic */ void lambda$createDeleteMessagesAlert$60(org.telegram.messenger.MessageObject r19, org.telegram.messenger.MessageObject.GroupedMessages r20, org.telegram.tgnet.TLRPC.EncryptedChat r21, int r22, long r23, boolean[] r25, boolean r26, android.util.SparseArray[] r27, org.telegram.tgnet.TLRPC.User r28, boolean[] r29, org.telegram.tgnet.TLRPC.Chat r30, org.telegram.tgnet.TLRPC.ChatFull r31, java.lang.Runnable r32, android.content.DialogInterface r33, int r34) {
        /*
            r0 = r19
            r1 = r20
            r9 = r28
            r10 = r30
            r11 = 10
            r12 = 0
            r14 = 0
            r15 = 1
            r8 = 0
            if (r0 == 0) goto L_0x00a0
            java.util.ArrayList r7 = new java.util.ArrayList
            r7.<init>()
            if (r1 == 0) goto L_0x0057
            r2 = 0
        L_0x0019:
            java.util.ArrayList<org.telegram.messenger.MessageObject> r3 = r1.messages
            int r3 = r3.size()
            if (r2 >= r3) goto L_0x0082
            java.util.ArrayList<org.telegram.messenger.MessageObject> r3 = r1.messages
            java.lang.Object r3 = r3.get(r2)
            org.telegram.messenger.MessageObject r3 = (org.telegram.messenger.MessageObject) r3
            int r4 = r3.getId()
            java.lang.Integer r4 = java.lang.Integer.valueOf(r4)
            r7.add(r4)
            if (r21 == 0) goto L_0x0054
            org.telegram.tgnet.TLRPC$Message r4 = r3.messageOwner
            long r4 = r4.random_id
            int r6 = (r4 > r12 ? 1 : (r4 == r12 ? 0 : -1))
            if (r6 == 0) goto L_0x0054
            int r4 = r3.type
            if (r4 == r11) goto L_0x0054
            if (r14 != 0) goto L_0x0049
            java.util.ArrayList r14 = new java.util.ArrayList
            r14.<init>()
        L_0x0049:
            org.telegram.tgnet.TLRPC$Message r3 = r3.messageOwner
            long r3 = r3.random_id
            java.lang.Long r3 = java.lang.Long.valueOf(r3)
            r14.add(r3)
        L_0x0054:
            int r2 = r2 + 1
            goto L_0x0019
        L_0x0057:
            int r1 = r19.getId()
            java.lang.Integer r1 = java.lang.Integer.valueOf(r1)
            r7.add(r1)
            if (r21 == 0) goto L_0x0082
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            long r1 = r1.random_id
            int r3 = (r1 > r12 ? 1 : (r1 == r12 ? 0 : -1))
            if (r3 == 0) goto L_0x0082
            int r1 = r0.type
            if (r1 == r11) goto L_0x0082
            java.util.ArrayList r1 = new java.util.ArrayList
            r1.<init>()
            org.telegram.tgnet.TLRPC$Message r2 = r0.messageOwner
            long r2 = r2.random_id
            java.lang.Long r2 = java.lang.Long.valueOf(r2)
            r1.add(r2)
            r2 = r1
            goto L_0x0083
        L_0x0082:
            r2 = r14
        L_0x0083:
            org.telegram.messenger.MessagesController r1 = org.telegram.messenger.MessagesController.getInstance(r22)
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            org.telegram.tgnet.TLRPC$Peer r0 = r0.to_id
            int r6 = r0.channel_id
            boolean r11 = r25[r8]
            r0 = r1
            r1 = r7
            r3 = r21
            r4 = r23
            r12 = r7
            r7 = r11
            r11 = 0
            r8 = r26
            r0.deleteMessages(r1, r2, r3, r4, r6, r7, r8)
            r7 = r12
            goto L_0x013b
        L_0x00a0:
            r7 = r14
            r16 = 1
        L_0x00a3:
            if (r16 < 0) goto L_0x013a
            java.util.ArrayList r7 = new java.util.ArrayList
            r7.<init>()
            r0 = 0
        L_0x00ab:
            r1 = r27[r16]
            int r1 = r1.size()
            if (r0 >= r1) goto L_0x00c3
            r1 = r27[r16]
            int r1 = r1.keyAt(r0)
            java.lang.Integer r1 = java.lang.Integer.valueOf(r1)
            r7.add(r1)
            int r0 = r0 + 1
            goto L_0x00ab
        L_0x00c3:
            boolean r0 = r7.isEmpty()
            if (r0 != 0) goto L_0x00e5
            r0 = r27[r16]
            java.lang.Object r1 = r7.get(r8)
            java.lang.Integer r1 = (java.lang.Integer) r1
            int r1 = r1.intValue()
            java.lang.Object r0 = r0.get(r1)
            org.telegram.messenger.MessageObject r0 = (org.telegram.messenger.MessageObject) r0
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            org.telegram.tgnet.TLRPC$Peer r0 = r0.to_id
            int r0 = r0.channel_id
            if (r0 == 0) goto L_0x00e5
            r6 = r0
            goto L_0x00e6
        L_0x00e5:
            r6 = 0
        L_0x00e6:
            if (r21 == 0) goto L_0x0116
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            r1 = 0
        L_0x00ee:
            r2 = r27[r16]
            int r2 = r2.size()
            if (r1 >= r2) goto L_0x0114
            r2 = r27[r16]
            java.lang.Object r2 = r2.valueAt(r1)
            org.telegram.messenger.MessageObject r2 = (org.telegram.messenger.MessageObject) r2
            org.telegram.tgnet.TLRPC$Message r3 = r2.messageOwner
            long r3 = r3.random_id
            int r5 = (r3 > r12 ? 1 : (r3 == r12 ? 0 : -1))
            if (r5 == 0) goto L_0x0111
            int r2 = r2.type
            if (r2 == r11) goto L_0x0111
            java.lang.Long r2 = java.lang.Long.valueOf(r3)
            r0.add(r2)
        L_0x0111:
            int r1 = r1 + 1
            goto L_0x00ee
        L_0x0114:
            r2 = r0
            goto L_0x0117
        L_0x0116:
            r2 = r14
        L_0x0117:
            org.telegram.messenger.MessagesController r0 = org.telegram.messenger.MessagesController.getInstance(r22)
            boolean r17 = r25[r8]
            r1 = r7
            r3 = r21
            r4 = r23
            r18 = r7
            r7 = r17
            r11 = 0
            r8 = r26
            r0.deleteMessages(r1, r2, r3, r4, r6, r7, r8)
            r0 = r27[r16]
            r0.clear()
            int r16 = r16 + -1
            r7 = r18
            r8 = 0
            r11 = 10
            goto L_0x00a3
        L_0x013a:
            r11 = 0
        L_0x013b:
            if (r9 == 0) goto L_0x017c
            boolean r0 = r29[r11]
            if (r0 == 0) goto L_0x014c
            org.telegram.messenger.MessagesController r0 = org.telegram.messenger.MessagesController.getInstance(r22)
            int r1 = r10.id
            r2 = r31
            r0.deleteUserFromChat(r1, r9, r2)
        L_0x014c:
            boolean r0 = r29[r15]
            if (r0 == 0) goto L_0x0170
            org.telegram.tgnet.TLRPC$TL_channels_reportSpam r0 = new org.telegram.tgnet.TLRPC$TL_channels_reportSpam
            r0.<init>()
            org.telegram.tgnet.TLRPC$InputChannel r1 = org.telegram.messenger.MessagesController.getInputChannel((org.telegram.tgnet.TLRPC.Chat) r30)
            r0.channel = r1
            org.telegram.messenger.MessagesController r1 = org.telegram.messenger.MessagesController.getInstance(r22)
            org.telegram.tgnet.TLRPC$InputUser r1 = r1.getInputUser((org.telegram.tgnet.TLRPC.User) r9)
            r0.user_id = r1
            r0.id = r7
            org.telegram.tgnet.ConnectionsManager r1 = org.telegram.tgnet.ConnectionsManager.getInstance(r22)
            org.telegram.ui.Components.-$$Lambda$AlertsCreator$A22Jxfm5gYlBAKThQRHubUhFEUI r2 = org.telegram.ui.Components.$$Lambda$AlertsCreator$A22Jxfm5gYlBAKThQRHubUhFEUI.INSTANCE
            r1.sendRequest(r0, r2)
        L_0x0170:
            r0 = 2
            boolean r0 = r29[r0]
            if (r0 == 0) goto L_0x017c
            org.telegram.messenger.MessagesController r0 = org.telegram.messenger.MessagesController.getInstance(r22)
            r0.deleteUserChannelHistory(r10, r9, r11)
        L_0x017c:
            if (r32 == 0) goto L_0x0181
            r32.run()
        L_0x0181:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.AlertsCreator.lambda$createDeleteMessagesAlert$60(org.telegram.messenger.MessageObject, org.telegram.messenger.MessageObject$GroupedMessages, org.telegram.tgnet.TLRPC$EncryptedChat, int, long, boolean[], boolean, android.util.SparseArray[], org.telegram.tgnet.TLRPC$User, boolean[], org.telegram.tgnet.TLRPC$Chat, org.telegram.tgnet.TLRPC$ChatFull, java.lang.Runnable, android.content.DialogInterface, int):void");
    }

    public static void createThemeCreateDialog(BaseFragment baseFragment, int i, Theme.ThemeInfo themeInfo, Theme.ThemeAccent themeAccent) {
        BaseFragment baseFragment2 = baseFragment;
        if (baseFragment2 != null && baseFragment.getParentActivity() != null) {
            Activity parentActivity = baseFragment.getParentActivity();
            EditTextBoldCursor editTextBoldCursor = new EditTextBoldCursor(parentActivity);
            editTextBoldCursor.setBackgroundDrawable(Theme.createEditTextDrawable(parentActivity, true));
            AlertDialog.Builder builder = new AlertDialog.Builder((Context) parentActivity);
            builder.setTitle(LocaleController.getString("NewTheme", NUM));
            builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
            builder.setPositiveButton(LocaleController.getString("Create", NUM), $$Lambda$AlertsCreator$0QtuIWIKEN8KFAWtr5Uy2CbfW1o.INSTANCE);
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
            textView.setTextColor(Theme.getColor("dialogTextBlack"));
            linearLayout.addView(textView, LayoutHelper.createLinear(-1, -2));
            editTextBoldCursor.setTextSize(1, 16.0f);
            editTextBoldCursor.setTextColor(Theme.getColor("dialogTextBlack"));
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
            editTextBoldCursor.setOnEditorActionListener($$Lambda$AlertsCreator$DJqb4RTx9GX9dj02QDzvL2X5Jo.INSTANCE);
            editTextBoldCursor.setText(generateThemeName(themeAccent));
            editTextBoldCursor.setSelection(editTextBoldCursor.length());
            AlertDialog create = builder.create();
            create.setOnShowListener(new DialogInterface.OnShowListener() {
                public final void onShow(DialogInterface dialogInterface) {
                    AndroidUtilities.runOnUIThread(new Runnable() {
                        public final void run() {
                            AlertsCreator.lambda$null$63(EditTextBoldCursor.this);
                        }
                    });
                }
            });
            baseFragment2.showDialog(create);
            editTextBoldCursor.requestFocus();
            create.getButton(-1).setOnClickListener(new View.OnClickListener(editTextBoldCursor, themeAccent, themeInfo, create) {
                private final /* synthetic */ EditTextBoldCursor f$1;
                private final /* synthetic */ Theme.ThemeAccent f$2;
                private final /* synthetic */ Theme.ThemeInfo f$3;
                private final /* synthetic */ AlertDialog f$4;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                    this.f$4 = r5;
                }

                public final void onClick(View view) {
                    AlertsCreator.lambda$createThemeCreateDialog$67(BaseFragment.this, this.f$1, this.f$2, this.f$3, this.f$4, view);
                }
            });
        }
    }

    static /* synthetic */ void lambda$null$63(EditTextBoldCursor editTextBoldCursor) {
        editTextBoldCursor.requestFocus();
        AndroidUtilities.showKeyboard(editTextBoldCursor);
    }

    static /* synthetic */ void lambda$createThemeCreateDialog$67(BaseFragment baseFragment, EditTextBoldCursor editTextBoldCursor, Theme.ThemeAccent themeAccent, Theme.ThemeInfo themeInfo, AlertDialog alertDialog, View view) {
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
                Utilities.searchQueue.postRunnable(new Runnable(alertDialog, baseFragment) {
                    private final /* synthetic */ AlertDialog f$1;
                    private final /* synthetic */ BaseFragment f$2;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                    }

                    public final void run() {
                        AndroidUtilities.runOnUIThread(new Runnable(this.f$1, this.f$2) {
                            private final /* synthetic */ AlertDialog f$1;
                            private final /* synthetic */ BaseFragment f$2;

                            {
                                this.f$1 = r2;
                                this.f$2 = r3;
                            }

                            public final void run() {
                                AlertsCreator.processCreate(EditTextBoldCursor.this, this.f$1, this.f$2);
                            }
                        });
                    }
                });
                return;
            }
            processCreate(editTextBoldCursor, alertDialog, baseFragment);
        }
    }

    /* access modifiers changed from: private */
    public static void processCreate(EditTextBoldCursor editTextBoldCursor, AlertDialog alertDialog, BaseFragment baseFragment) {
        if (baseFragment != null && baseFragment.getParentActivity() != null) {
            AndroidUtilities.hideKeyboard(editTextBoldCursor);
            Theme.ThemeInfo createNewTheme = Theme.createNewTheme(editTextBoldCursor.getText().toString());
            NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.themeListUpdated, new Object[0]);
            new ThemeEditorView().show(baseFragment.getParentActivity(), createNewTheme);
            alertDialog.dismiss();
            SharedPreferences globalMainSettings = MessagesController.getGlobalMainSettings();
            if (!globalMainSettings.getBoolean("themehint", false)) {
                globalMainSettings.edit().putBoolean("themehint", true).commit();
                try {
                    Toast.makeText(baseFragment.getParentActivity(), LocaleController.getString("CreateNewThemeHelp", NUM), 1).show();
                } catch (Exception e) {
                    FileLog.e((Throwable) e);
                }
            }
        }
    }

    private static String generateThemeName(Theme.ThemeAccent themeAccent) {
        int i;
        List asList = Arrays.asList(new String[]{"Ancient", "Antique", "Autumn", "Baby", "Barely", "Baroque", "Blazing", "Blushing", "Bohemian", "Bubbly", "Burning", "Buttered", "Classic", "Clear", "Cool", "Cosmic", "Cotton", "Cozy", "Crystal", "Dark", "Daring", "Darling", "Dawn", "Dazzling", "Deep", "Deepest", "Delicate", "Delightful", "Divine", "Double", "Downtown", "Dreamy", "Dusky", "Dusty", "Electric", "Enchanted", "Endless", "Evening", "Fantastic", "Flirty", "Forever", "Frigid", "Frosty", "Frozen", "Gentle", "Heavenly", "Hyper", "Icy", "Infinite", "Innocent", "Instant", "Luscious", "Lunar", "Lustrous", "Magic", "Majestic", "Mambo", "Midnight", "Millenium", "Morning", "Mystic", "Natural", "Neon", "Night", "Opaque", "Paradise", "Perfect", "Perky", "Polished", "Powerful", "Rich", "Royal", "Sheer", "Simply", "Sizzling", "Solar", "Sparkling", "Splendid", "Spicy", "Spring", "Stellar", "Sugared", "Summer", "Sunny", "Super", "Sweet", "Tender", "Tenacious", "Tidal", "Toasted", "Totally", "Tranquil", "Tropical", "True", "Twilight", "Twinkling", "Ultimate", "Ultra", "Velvety", "Vibrant", "Vintage", "Virtual", "Warm", "Warmest", "Whipped", "Wild", "Winsome"});
        List asList2 = Arrays.asList(new String[]{"Ambrosia", "Attack", "Avalanche", "Blast", "Bliss", "Blossom", "Blush", "Burst", "Butter", "Candy", "Carnival", "Charm", "Chiffon", "Cloud", "Comet", "Delight", "Dream", "Dust", "Fantasy", "Flame", "Flash", "Fire", "Freeze", "Frost", "Glade", "Glaze", "Gleam", "Glimmer", "Glitter", "Glow", "Grande", "Haze", "Highlight", "Ice", "Illusion", "Intrigue", "Jewel", "Jubilee", "Kiss", "Lights", "Lollypop", "Love", "Luster", "Madness", "Matte", "Mirage", "Mist", "Moon", "Muse", "Myth", "Nectar", "Nova", "Parfait", "Passion", "Pop", "Rain", "Reflection", "Rhapsody", "Romance", "Satin", "Sensation", "Silk", "Shine", "Shadow", "Shimmer", "Sky", "Spice", "Star", "Sugar", "Sunrise", "Sunset", "Sun", "Twist", "Unbound", "Velvet", "Vibrant", "Waters", "Wine", "Wink", "Wonder", "Zone"});
        HashMap hashMap = new HashMap();
        hashMap.put(9306112, "Berry");
        hashMap.put(14598550, "Brandy");
        hashMap.put(8391495, "Cherry");
        hashMap.put(16744272, "Coral");
        hashMap.put(14372985, "Cranberry");
        hashMap.put(14423100, "Crimson");
        hashMap.put(14725375, "Mauve");
        hashMap.put(16761035, "Pink");
        hashMap.put(16711680, "Red");
        hashMap.put(16711807, "Rose");
        hashMap.put(8406555, "Russet");
        hashMap.put(16720896, "Scarlet");
        hashMap.put(15856113, "Seashell");
        hashMap.put(16724889, "Strawberry");
        hashMap.put(16760576, "Amber");
        hashMap.put(15438707, "Apricot");
        hashMap.put(16508850, "Banana");
        hashMap.put(10601738, "Citrus");
        hashMap.put(11560192, "Ginger");
        hashMap.put(16766720, "Gold");
        hashMap.put(16640272, "Lemon");
        hashMap.put(16753920, "Orange");
        hashMap.put(16770484, "Peach");
        hashMap.put(16739155, "Persimmon");
        hashMap.put(14996514, "Sunflower");
        hashMap.put(15893760, "Tangerine");
        hashMap.put(16763004, "Topaz");
        hashMap.put(16776960, "Yellow");
        hashMap.put(3688720, "Clover");
        hashMap.put(8628829, "Cucumber");
        hashMap.put(5294200, "Emerald");
        hashMap.put(11907932, "Olive");
        hashMap.put(65280, "Green");
        hashMap.put(43115, "Jade");
        hashMap.put(2730887, "Jungle");
        hashMap.put(12582656, "Lime");
        hashMap.put(776785, "Malachite");
        hashMap.put(10026904, "Mint");
        hashMap.put(11394989, "Moss");
        hashMap.put(3234721, "Azure");
        hashMap.put(255, "Blue");
        hashMap.put(18347, "Cobalt");
        hashMap.put(5204422, "Indigo");
        hashMap.put(96647, "Lagoon");
        hashMap.put(7461346, "Aquamarine");
        hashMap.put(1182351, "Ultramarine");
        hashMap.put(128, "Navy");
        hashMap.put(3101086, "Sapphire");
        hashMap.put(7788522, "Sky");
        hashMap.put(32896, "Teal");
        hashMap.put(4251856, "Turquoise");
        hashMap.put(10053324, "Amethyst");
        hashMap.put(5046581, "Blackberry");
        hashMap.put(6373457, "Eggplant");
        hashMap.put(13148872, "Lilac");
        hashMap.put(11894492, "Lavender");
        hashMap.put(13421823, "Periwinkle");
        hashMap.put(8663417, "Plum");
        hashMap.put(6684825, "Purple");
        hashMap.put(14204888, "Thistle");
        hashMap.put(14315734, "Orchid");
        hashMap.put(2361920, "Violet");
        hashMap.put(4137225, "Bronze");
        hashMap.put(3604994, "Chocolate");
        hashMap.put(8077056, "Cinnamon");
        hashMap.put(3153694, "Cocoa");
        hashMap.put(7365973, "Coffee");
        hashMap.put(7956873, "Rum");
        hashMap.put(5113350, "Mahogany");
        hashMap.put(7875865, "Mocha");
        hashMap.put(12759680, "Sand");
        hashMap.put(8924439, "Sienna");
        hashMap.put(7864585, "Maple");
        hashMap.put(15787660, "Khaki");
        hashMap.put(12088115, "Copper");
        hashMap.put(12144200, "Chestnut");
        hashMap.put(15653316, "Almond");
        hashMap.put(16776656, "Cream");
        hashMap.put(12186367, "Diamond");
        hashMap.put(11109127, "Honey");
        hashMap.put(16777200, "Ivory");
        hashMap.put(15392968, "Pearl");
        hashMap.put(15725299, "Porcelain");
        hashMap.put(13745832, "Vanilla");
        hashMap.put(16777215, "White");
        hashMap.put(8421504, "Gray");
        hashMap.put(0, "Black");
        hashMap.put(15266260, "Chrome");
        hashMap.put(3556687, "Charcoal");
        hashMap.put(789277, "Ebony");
        hashMap.put(12632256, "Silver");
        hashMap.put(16119285, "Smoke");
        hashMap.put(2499381, "Steel");
        hashMap.put(5220413, "Apple");
        hashMap.put(8434628, "Glacier");
        hashMap.put(16693933, "Melon");
        hashMap.put(12929932, "Mulberry");
        hashMap.put(11126466, "Opal");
        hashMap.put(5547512, "Blue");
        Theme.ThemeAccent accent = themeAccent == null ? Theme.getCurrentTheme().getAccent(false) : themeAccent;
        if (accent == null || (i = accent.accentColor) == 0) {
            i = AndroidUtilities.calcDrawableColor(Theme.getCachedWallpaper())[0];
        }
        String str = null;
        int i2 = Integer.MAX_VALUE;
        int red = Color.red(i);
        int green = Color.green(i);
        int blue = Color.blue(i);
        for (Map.Entry entry : hashMap.entrySet()) {
            Integer num = (Integer) entry.getKey();
            int red2 = Color.red(num.intValue());
            int i3 = (red + red2) / 2;
            int i4 = red - red2;
            int green2 = green - Color.green(num.intValue());
            int blue2 = blue - Color.blue(num.intValue());
            int i5 = ((((i3 + 512) * i4) * i4) >> 8) + (green2 * 4 * green2) + ((((767 - i3) * blue2) * blue2) >> 8);
            if (i5 < i2) {
                str = (String) entry.getValue();
                i2 = i5;
            }
        }
        if (Utilities.random.nextInt() % 2 == 0) {
            return ((String) asList.get(Utilities.random.nextInt(asList.size()))) + " " + str;
        }
        return str + " " + ((String) asList2.get(Utilities.random.nextInt(asList2.size())));
    }
}
