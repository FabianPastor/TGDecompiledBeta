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
import org.telegram.messenger.ContactsController;
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
import org.telegram.tgnet.TLRPC$ChannelParticipant;
import org.telegram.tgnet.TLRPC$Chat;
import org.telegram.tgnet.TLRPC$ChatFull;
import org.telegram.tgnet.TLRPC$Dialog;
import org.telegram.tgnet.TLRPC$EncryptedChat;
import org.telegram.tgnet.TLRPC$Message;
import org.telegram.tgnet.TLRPC$TL_channelParticipantAdmin;
import org.telegram.tgnet.TLRPC$TL_channelParticipantCreator;
import org.telegram.tgnet.TLRPC$TL_channels_channelParticipant;
import org.telegram.tgnet.TLRPC$TL_channels_createChannel;
import org.telegram.tgnet.TLRPC$TL_channels_inviteToChannel;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$TL_help_getSupport;
import org.telegram.tgnet.TLRPC$TL_help_support;
import org.telegram.tgnet.TLRPC$TL_langPackLanguage;
import org.telegram.tgnet.TLRPC$TL_peerNotifySettings;
import org.telegram.tgnet.TLRPC$User;
import org.telegram.tgnet.TLRPC$UserStatus;
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

    public interface BlockDialogCallback {
        void run(boolean z, boolean z2);
    }

    public interface DatePickerDelegate {
        void didSelectDate(int i, int i2, int i3);
    }

    public interface ScheduleDatePickerDelegate {
        void didSelectDate(boolean z, int i);
    }

    static /* synthetic */ boolean lambda$createScheduleDatePickerDialog$23(View view, MotionEvent motionEvent) {
        return true;
    }

    static /* synthetic */ void lambda$createThemeCreateDialog$63(DialogInterface dialogInterface, int i) {
    }

    static /* synthetic */ void lambda$null$33(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
    }

    static /* synthetic */ void lambda$null$61(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
    }

    /* JADX WARNING: Removed duplicated region for block: B:178:0x0352  */
    /* JADX WARNING: Removed duplicated region for block: B:182:0x036f  */
    /* JADX WARNING: Removed duplicated region for block: B:208:0x03ea  */
    /* JADX WARNING: Removed duplicated region for block: B:212:0x0402  */
    /* JADX WARNING: Removed duplicated region for block: B:267:0x04fc  */
    /* JADX WARNING: Removed duplicated region for block: B:272:0x0526  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static android.app.Dialog processError(int r18, org.telegram.tgnet.TLRPC$TL_error r19, org.telegram.ui.ActionBar.BaseFragment r20, org.telegram.tgnet.TLObject r21, java.lang.Object... r22) {
        /*
            r0 = r19
            r1 = r20
            r2 = r21
            r3 = r22
            int r4 = r0.code
            r5 = 0
            r6 = 406(0x196, float:5.69E-43)
            if (r4 == r6) goto L_0x05fa
            java.lang.String r6 = r0.text
            if (r6 != 0) goto L_0x0015
            goto L_0x05fa
        L_0x0015:
            boolean r7 = r2 instanceof org.telegram.tgnet.TLRPC$TL_account_saveSecureValue
            java.lang.String r8 = "\n"
            java.lang.String r10 = "InvalidPhoneNumber"
            java.lang.String r11 = "PHONE_NUMBER_INVALID"
            java.lang.String r13 = "ErrorOccurred"
            java.lang.String r15 = "FloodWait"
            java.lang.String r9 = "FLOOD_WAIT"
            if (r7 != 0) goto L_0x0597
            boolean r7 = r2 instanceof org.telegram.tgnet.TLRPC$TL_account_getAuthorizationForm
            if (r7 == 0) goto L_0x002b
            goto L_0x0597
        L_0x002b:
            boolean r7 = r2 instanceof org.telegram.tgnet.TLRPC$TL_channels_joinChannel
            java.lang.String r12 = "CHANNELS_TOO_MUCH"
            if (r7 != 0) goto L_0x053b
            boolean r14 = r2 instanceof org.telegram.tgnet.TLRPC$TL_channels_editAdmin
            if (r14 != 0) goto L_0x053b
            boolean r14 = r2 instanceof org.telegram.tgnet.TLRPC$TL_channels_inviteToChannel
            if (r14 != 0) goto L_0x053b
            boolean r14 = r2 instanceof org.telegram.tgnet.TLRPC$TL_messages_addChatUser
            if (r14 != 0) goto L_0x053b
            boolean r14 = r2 instanceof org.telegram.tgnet.TLRPC$TL_messages_startBot
            if (r14 != 0) goto L_0x053b
            boolean r14 = r2 instanceof org.telegram.tgnet.TLRPC$TL_channels_editBanned
            if (r14 != 0) goto L_0x053b
            boolean r14 = r2 instanceof org.telegram.tgnet.TLRPC$TL_messages_editChatDefaultBannedRights
            if (r14 != 0) goto L_0x053b
            boolean r14 = r2 instanceof org.telegram.tgnet.TLRPC$TL_messages_editChatAdmin
            if (r14 != 0) goto L_0x053b
            boolean r14 = r2 instanceof org.telegram.tgnet.TLRPC$TL_messages_migrateChat
            if (r14 == 0) goto L_0x0053
            goto L_0x053b
        L_0x0053:
            boolean r7 = r2 instanceof org.telegram.tgnet.TLRPC$TL_messages_createChat
            r14 = 2
            if (r7 == 0) goto L_0x007c
            boolean r3 = r6.equals(r12)
            if (r3 == 0) goto L_0x0067
            org.telegram.ui.TooManyCommunitiesActivity r0 = new org.telegram.ui.TooManyCommunitiesActivity
            r0.<init>(r14)
            r1.presentFragment(r0)
            return r5
        L_0x0067:
            java.lang.String r3 = r0.text
            boolean r3 = r3.startsWith(r9)
            if (r3 == 0) goto L_0x0075
            java.lang.String r0 = r0.text
            showFloodWaitAlert(r0, r1)
            goto L_0x00cb
        L_0x0075:
            java.lang.String r0 = r0.text
            r3 = 0
            showAddUserAlert(r0, r1, r3, r2)
            goto L_0x00cb
        L_0x007c:
            boolean r7 = r2 instanceof org.telegram.tgnet.TLRPC$TL_channels_createChannel
            if (r7 == 0) goto L_0x00a4
            boolean r3 = r6.equals(r12)
            if (r3 == 0) goto L_0x008f
            org.telegram.ui.TooManyCommunitiesActivity r0 = new org.telegram.ui.TooManyCommunitiesActivity
            r0.<init>(r14)
            r1.presentFragment(r0)
            return r5
        L_0x008f:
            java.lang.String r3 = r0.text
            boolean r3 = r3.startsWith(r9)
            if (r3 == 0) goto L_0x009d
            java.lang.String r0 = r0.text
            showFloodWaitAlert(r0, r1)
            goto L_0x00cb
        L_0x009d:
            java.lang.String r0 = r0.text
            r3 = 0
            showAddUserAlert(r0, r1, r3, r2)
            goto L_0x00cb
        L_0x00a4:
            boolean r7 = r2 instanceof org.telegram.tgnet.TLRPC$TL_messages_editMessage
            if (r7 == 0) goto L_0x00ce
            java.lang.String r0 = "MESSAGE_NOT_MODIFIED"
            boolean r0 = r6.equals(r0)
            if (r0 != 0) goto L_0x00cb
            if (r1 == 0) goto L_0x00bf
            r0 = 2131625013(0x7f0e0435, float:1.8877222E38)
            java.lang.String r2 = "EditMessageError"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            showSimpleAlert(r1, r0)
            goto L_0x00cb
        L_0x00bf:
            r0 = 2131625013(0x7f0e0435, float:1.8877222E38)
            java.lang.String r2 = "EditMessageError"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            showSimpleToast(r1, r0)
        L_0x00cb:
            r0 = r5
            goto L_0x05f9
        L_0x00ce:
            boolean r7 = r2 instanceof org.telegram.tgnet.TLRPC$TL_messages_sendMessage
            r16 = -1
            if (r7 != 0) goto L_0x04c5
            boolean r7 = r2 instanceof org.telegram.tgnet.TLRPC$TL_messages_sendMedia
            if (r7 != 0) goto L_0x04c5
            boolean r7 = r2 instanceof org.telegram.tgnet.TLRPC$TL_messages_sendInlineBotResult
            if (r7 != 0) goto L_0x04c5
            boolean r7 = r2 instanceof org.telegram.tgnet.TLRPC$TL_messages_forwardMessages
            if (r7 != 0) goto L_0x04c5
            boolean r7 = r2 instanceof org.telegram.tgnet.TLRPC$TL_messages_sendMultiMedia
            if (r7 != 0) goto L_0x04c5
            boolean r7 = r2 instanceof org.telegram.tgnet.TLRPC$TL_messages_sendScheduledMessages
            if (r7 == 0) goto L_0x00ea
            goto L_0x04c5
        L_0x00ea:
            boolean r7 = r2 instanceof org.telegram.tgnet.TLRPC$TL_messages_importChatInvite
            if (r7 == 0) goto L_0x0135
            boolean r2 = r6.startsWith(r9)
            if (r2 == 0) goto L_0x00ff
            r2 = 2131625263(0x7f0e052f, float:1.887773E38)
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r15, r2)
            showSimpleAlert(r1, r0)
            goto L_0x00cb
        L_0x00ff:
            java.lang.String r2 = r0.text
            java.lang.String r3 = "USERS_TOO_MUCH"
            boolean r2 = r2.equals(r3)
            if (r2 == 0) goto L_0x0116
            r0 = 2131625497(0x7f0e0619, float:1.8878204E38)
            java.lang.String r2 = "JoinToGroupErrorFull"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            showSimpleAlert(r1, r0)
            goto L_0x00cb
        L_0x0116:
            java.lang.String r0 = r0.text
            boolean r0 = r0.equals(r12)
            if (r0 == 0) goto L_0x0128
            org.telegram.ui.TooManyCommunitiesActivity r0 = new org.telegram.ui.TooManyCommunitiesActivity
            r2 = 0
            r0.<init>(r2)
            r1.presentFragment(r0)
            goto L_0x00cb
        L_0x0128:
            r0 = 2131625498(0x7f0e061a, float:1.8878206E38)
            java.lang.String r2 = "JoinToGroupErrorNotExist"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            showSimpleAlert(r1, r0)
            goto L_0x00cb
        L_0x0135:
            boolean r7 = r2 instanceof org.telegram.tgnet.TLRPC$TL_messages_getAttachedStickers
            if (r7 == 0) goto L_0x016a
            if (r1 == 0) goto L_0x00cb
            android.app.Activity r2 = r20.getParentActivity()
            if (r2 == 0) goto L_0x00cb
            android.app.Activity r1 = r20.getParentActivity()
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            r3 = 2131625066(0x7f0e046a, float:1.887733E38)
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r13, r3)
            r2.append(r3)
            r2.append(r8)
            java.lang.String r0 = r0.text
            r2.append(r0)
            java.lang.String r0 = r2.toString()
            r2 = 0
            android.widget.Toast r0 = android.widget.Toast.makeText(r1, r0, r2)
            r0.show()
            goto L_0x00cb
        L_0x016a:
            boolean r7 = r2 instanceof org.telegram.tgnet.TLRPC$TL_account_confirmPhone
            java.lang.String r14 = "CodeExpired"
            java.lang.String r5 = "PHONE_CODE_EXPIRED"
            java.lang.String r12 = "PHONE_CODE_INVALID"
            java.lang.String r3 = "InvalidCode"
            r17 = r4
            java.lang.String r4 = "PHONE_CODE_EMPTY"
            if (r7 != 0) goto L_0x045a
            boolean r7 = r2 instanceof org.telegram.tgnet.TLRPC$TL_account_verifyPhone
            if (r7 != 0) goto L_0x045a
            boolean r7 = r2 instanceof org.telegram.tgnet.TLRPC$TL_account_verifyEmail
            if (r7 == 0) goto L_0x0184
            goto L_0x045a
        L_0x0184:
            boolean r7 = r2 instanceof org.telegram.tgnet.TLRPC$TL_auth_resendCode
            if (r7 == 0) goto L_0x0205
            boolean r2 = r6.contains(r11)
            if (r2 == 0) goto L_0x019a
            r2 = 2131625467(0x7f0e05fb, float:1.8878143E38)
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r10, r2)
            android.app.Dialog r0 = showSimpleAlert(r1, r0)
            return r0
        L_0x019a:
            java.lang.String r2 = r0.text
            boolean r2 = r2.contains(r4)
            if (r2 != 0) goto L_0x01f9
            java.lang.String r2 = r0.text
            boolean r2 = r2.contains(r12)
            if (r2 == 0) goto L_0x01ab
            goto L_0x01f9
        L_0x01ab:
            java.lang.String r2 = r0.text
            boolean r2 = r2.contains(r5)
            if (r2 == 0) goto L_0x01bf
            r2 = 2131624733(0x7f0e031d, float:1.8876654E38)
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r14, r2)
            android.app.Dialog r0 = showSimpleAlert(r1, r0)
            return r0
        L_0x01bf:
            java.lang.String r2 = r0.text
            boolean r2 = r2.startsWith(r9)
            if (r2 == 0) goto L_0x01d3
            r2 = 2131625263(0x7f0e052f, float:1.887773E38)
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r15, r2)
            android.app.Dialog r0 = showSimpleAlert(r1, r0)
            return r0
        L_0x01d3:
            int r2 = r0.code
            r3 = -1000(0xfffffffffffffCLASSNAME, float:NaN)
            if (r2 == r3) goto L_0x05a9
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            r3 = 2131625066(0x7f0e046a, float:1.887733E38)
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r13, r3)
            r2.append(r3)
            r2.append(r8)
            java.lang.String r0 = r0.text
            r2.append(r0)
            java.lang.String r0 = r2.toString()
            android.app.Dialog r0 = showSimpleAlert(r1, r0)
            return r0
        L_0x01f9:
            r0 = 2131625464(0x7f0e05f8, float:1.8878137E38)
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r3, r0)
            android.app.Dialog r0 = showSimpleAlert(r1, r0)
            return r0
        L_0x0205:
            boolean r7 = r2 instanceof org.telegram.tgnet.TLRPC$TL_account_sendConfirmPhoneCode
            if (r7 == 0) goto L_0x023d
            r0 = 400(0x190, float:5.6E-43)
            r2 = r17
            if (r2 != r0) goto L_0x021d
            r0 = 2131624489(0x7f0e0229, float:1.887616E38)
            java.lang.String r2 = "CancelLinkExpired"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            android.app.Dialog r0 = showSimpleAlert(r1, r0)
            return r0
        L_0x021d:
            if (r6 == 0) goto L_0x05a9
            boolean r0 = r6.startsWith(r9)
            if (r0 == 0) goto L_0x0231
            r0 = 2131625263(0x7f0e052f, float:1.887773E38)
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r15, r0)
            android.app.Dialog r0 = showSimpleAlert(r1, r0)
            return r0
        L_0x0231:
            r0 = 2131625066(0x7f0e046a, float:1.887733E38)
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r13, r0)
            android.app.Dialog r0 = showSimpleAlert(r1, r0)
            return r0
        L_0x023d:
            boolean r7 = r2 instanceof org.telegram.tgnet.TLRPC$TL_account_changePhone
            if (r7 == 0) goto L_0x029f
            boolean r2 = r6.contains(r11)
            if (r2 == 0) goto L_0x0253
            r2 = 2131625467(0x7f0e05fb, float:1.8878143E38)
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r10, r2)
            showSimpleAlert(r1, r0)
            goto L_0x05a9
        L_0x0253:
            java.lang.String r2 = r0.text
            boolean r2 = r2.contains(r4)
            if (r2 != 0) goto L_0x0293
            java.lang.String r2 = r0.text
            boolean r2 = r2.contains(r12)
            if (r2 == 0) goto L_0x0264
            goto L_0x0293
        L_0x0264:
            java.lang.String r2 = r0.text
            boolean r2 = r2.contains(r5)
            if (r2 == 0) goto L_0x0278
            r2 = 2131624733(0x7f0e031d, float:1.8876654E38)
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r14, r2)
            showSimpleAlert(r1, r0)
            goto L_0x05a9
        L_0x0278:
            java.lang.String r2 = r0.text
            boolean r2 = r2.startsWith(r9)
            if (r2 == 0) goto L_0x028c
            r2 = 2131625263(0x7f0e052f, float:1.887773E38)
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r15, r2)
            showSimpleAlert(r1, r0)
            goto L_0x05a9
        L_0x028c:
            java.lang.String r0 = r0.text
            showSimpleAlert(r1, r0)
            goto L_0x05a9
        L_0x0293:
            r7 = 2131625464(0x7f0e05f8, float:1.8878137E38)
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r3, r7)
            showSimpleAlert(r1, r0)
            goto L_0x05a9
        L_0x029f:
            boolean r7 = r2 instanceof org.telegram.tgnet.TLRPC$TL_account_sendChangePhoneCode
            if (r7 == 0) goto L_0x0328
            boolean r2 = r6.contains(r11)
            if (r2 == 0) goto L_0x02b5
            r2 = 2131625467(0x7f0e05fb, float:1.8878143E38)
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r10, r2)
            showSimpleAlert(r1, r0)
            goto L_0x05a9
        L_0x02b5:
            java.lang.String r2 = r0.text
            boolean r2 = r2.contains(r4)
            if (r2 != 0) goto L_0x031c
            java.lang.String r2 = r0.text
            boolean r2 = r2.contains(r12)
            if (r2 == 0) goto L_0x02c6
            goto L_0x031c
        L_0x02c6:
            java.lang.String r2 = r0.text
            boolean r2 = r2.contains(r5)
            if (r2 == 0) goto L_0x02da
            r2 = 2131624733(0x7f0e031d, float:1.8876654E38)
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r14, r2)
            showSimpleAlert(r1, r0)
            goto L_0x05a9
        L_0x02da:
            java.lang.String r2 = r0.text
            boolean r2 = r2.startsWith(r9)
            if (r2 == 0) goto L_0x02ee
            r2 = 2131625263(0x7f0e052f, float:1.887773E38)
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r15, r2)
            showSimpleAlert(r1, r0)
            goto L_0x05a9
        L_0x02ee:
            java.lang.String r0 = r0.text
            java.lang.String r2 = "PHONE_NUMBER_OCCUPIED"
            boolean r0 = r0.startsWith(r2)
            if (r0 == 0) goto L_0x0310
            r0 = 2131624511(0x7f0e023f, float:1.8876204E38)
            r2 = 1
            java.lang.Object[] r2 = new java.lang.Object[r2]
            r3 = r22
            r4 = 0
            r3 = r3[r4]
            r2[r4] = r3
            java.lang.String r3 = "ChangePhoneNumberOccupied"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r3, r0, r2)
            showSimpleAlert(r1, r0)
            goto L_0x05a9
        L_0x0310:
            r0 = 2131625066(0x7f0e046a, float:1.887733E38)
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r13, r0)
            showSimpleAlert(r1, r0)
            goto L_0x05a9
        L_0x031c:
            r2 = 2131625464(0x7f0e05f8, float:1.8878137E38)
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r3, r2)
            showSimpleAlert(r1, r0)
            goto L_0x05a9
        L_0x0328:
            boolean r3 = r2 instanceof org.telegram.tgnet.TLRPC$TL_updateUserName
            if (r3 == 0) goto L_0x037d
            int r0 = r6.hashCode()
            r2 = 288843630(0x1137676e, float:1.4468026E-28)
            if (r0 == r2) goto L_0x0345
            r2 = 533175271(0x1fCLASSNAMEbe7, float:8.45377E-20)
            if (r0 == r2) goto L_0x033b
            goto L_0x034f
        L_0x033b:
            java.lang.String r0 = "USERNAME_OCCUPIED"
            boolean r0 = r6.equals(r0)
            if (r0 == 0) goto L_0x034f
            r0 = 1
            goto L_0x0350
        L_0x0345:
            java.lang.String r0 = "USERNAME_INVALID"
            boolean r0 = r6.equals(r0)
            if (r0 == 0) goto L_0x034f
            r0 = 0
            goto L_0x0350
        L_0x034f:
            r0 = -1
        L_0x0350:
            if (r0 == 0) goto L_0x036f
            r2 = 1
            if (r0 == r2) goto L_0x0361
            r0 = 2131625066(0x7f0e046a, float:1.887733E38)
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r13, r0)
            showSimpleAlert(r1, r0)
            goto L_0x05a9
        L_0x0361:
            r0 = 2131627131(0x7f0e0c7b, float:1.8881518E38)
            java.lang.String r2 = "UsernameInUse"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            showSimpleAlert(r1, r0)
            goto L_0x05a9
        L_0x036f:
            r0 = 2131627132(0x7f0e0c7c, float:1.888152E38)
            java.lang.String r2 = "UsernameInvalid"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            showSimpleAlert(r1, r0)
            goto L_0x05a9
        L_0x037d:
            boolean r3 = r2 instanceof org.telegram.tgnet.TLRPC$TL_contacts_importContacts
            if (r3 == 0) goto L_0x03b6
            if (r0 == 0) goto L_0x03aa
            boolean r2 = r6.startsWith(r9)
            if (r2 == 0) goto L_0x038a
            goto L_0x03aa
        L_0x038a:
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            r3 = 2131625066(0x7f0e046a, float:1.887733E38)
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r13, r3)
            r2.append(r3)
            r2.append(r8)
            java.lang.String r0 = r0.text
            r2.append(r0)
            java.lang.String r0 = r2.toString()
            showSimpleAlert(r1, r0)
            goto L_0x05a9
        L_0x03aa:
            r0 = 2131625263(0x7f0e052f, float:1.887773E38)
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r15, r0)
            showSimpleAlert(r1, r0)
            goto L_0x05a9
        L_0x03b6:
            boolean r3 = r2 instanceof org.telegram.tgnet.TLRPC$TL_account_getPassword
            if (r3 != 0) goto L_0x0440
            boolean r3 = r2 instanceof org.telegram.tgnet.TLRPC$TL_account_getTmpPassword
            if (r3 == 0) goto L_0x03c0
            goto L_0x0440
        L_0x03c0:
            boolean r3 = r2 instanceof org.telegram.tgnet.TLRPC$TL_payments_sendPaymentForm
            if (r3 == 0) goto L_0x0410
            int r2 = r6.hashCode()
            r3 = -1144062453(0xffffffffbbcefe0b, float:-0.NUM)
            if (r2 == r3) goto L_0x03dd
            r3 = -784238410(0xffffffffd14178b6, float:-5.1934618E10)
            if (r2 == r3) goto L_0x03d3
            goto L_0x03e7
        L_0x03d3:
            java.lang.String r2 = "PAYMENT_FAILED"
            boolean r2 = r6.equals(r2)
            if (r2 == 0) goto L_0x03e7
            r2 = 1
            goto L_0x03e8
        L_0x03dd:
            java.lang.String r2 = "BOT_PRECHECKOUT_FAILED"
            boolean r2 = r6.equals(r2)
            if (r2 == 0) goto L_0x03e7
            r2 = 0
            goto L_0x03e8
        L_0x03e7:
            r2 = -1
        L_0x03e8:
            if (r2 == 0) goto L_0x0402
            r3 = 1
            if (r2 == r3) goto L_0x03f4
            java.lang.String r0 = r0.text
            showSimpleToast(r1, r0)
            goto L_0x05a9
        L_0x03f4:
            r0 = 2131626269(0x7f0e091d, float:1.887977E38)
            java.lang.String r2 = "PaymentFailed"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            showSimpleToast(r1, r0)
            goto L_0x05a9
        L_0x0402:
            r0 = 2131626282(0x7f0e092a, float:1.8879796E38)
            java.lang.String r2 = "PaymentPrecheckoutFailed"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            showSimpleToast(r1, r0)
            goto L_0x05a9
        L_0x0410:
            boolean r2 = r2 instanceof org.telegram.tgnet.TLRPC$TL_payments_validateRequestedInfo
            if (r2 == 0) goto L_0x05a9
            int r2 = r6.hashCode()
            r3 = 1758025548(0x68CLASSNAMEc, float:7.606448E24)
            if (r2 == r3) goto L_0x041e
            goto L_0x0428
        L_0x041e:
            java.lang.String r2 = "SHIPPING_NOT_AVAILABLE"
            boolean r2 = r6.equals(r2)
            if (r2 == 0) goto L_0x0428
            r14 = 0
            goto L_0x0429
        L_0x0428:
            r14 = -1
        L_0x0429:
            if (r14 == 0) goto L_0x0432
            java.lang.String r0 = r0.text
            showSimpleToast(r1, r0)
            goto L_0x05a9
        L_0x0432:
            r0 = 2131626271(0x7f0e091f, float:1.8879773E38)
            java.lang.String r2 = "PaymentNoShippingMethod"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            showSimpleToast(r1, r0)
            goto L_0x05a9
        L_0x0440:
            java.lang.String r2 = r0.text
            boolean r2 = r2.startsWith(r9)
            if (r2 == 0) goto L_0x0453
            java.lang.String r0 = r0.text
            java.lang.String r0 = getFloodWaitString(r0)
            showSimpleToast(r1, r0)
            goto L_0x05a9
        L_0x0453:
            java.lang.String r0 = r0.text
            showSimpleToast(r1, r0)
            goto L_0x05a9
        L_0x045a:
            r2 = 2131625464(0x7f0e05f8, float:1.8878137E38)
            java.lang.String r6 = r0.text
            boolean r4 = r6.contains(r4)
            if (r4 != 0) goto L_0x04bc
            java.lang.String r4 = r0.text
            boolean r4 = r4.contains(r12)
            if (r4 != 0) goto L_0x04bc
            java.lang.String r4 = r0.text
            java.lang.String r6 = "CODE_INVALID"
            boolean r4 = r4.contains(r6)
            if (r4 != 0) goto L_0x04bc
            java.lang.String r4 = r0.text
            java.lang.String r6 = "CODE_EMPTY"
            boolean r4 = r4.contains(r6)
            if (r4 == 0) goto L_0x0482
            goto L_0x04bc
        L_0x0482:
            java.lang.String r2 = r0.text
            boolean r2 = r2.contains(r5)
            if (r2 != 0) goto L_0x04b0
            java.lang.String r2 = r0.text
            java.lang.String r3 = "EMAIL_VERIFY_EXPIRED"
            boolean r2 = r2.contains(r3)
            if (r2 == 0) goto L_0x0495
            goto L_0x04b0
        L_0x0495:
            java.lang.String r2 = r0.text
            boolean r2 = r2.startsWith(r9)
            if (r2 == 0) goto L_0x04a9
            r2 = 2131625263(0x7f0e052f, float:1.887773E38)
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r15, r2)
            android.app.Dialog r0 = showSimpleAlert(r1, r0)
            return r0
        L_0x04a9:
            java.lang.String r0 = r0.text
            android.app.Dialog r0 = showSimpleAlert(r1, r0)
            return r0
        L_0x04b0:
            r0 = 2131624733(0x7f0e031d, float:1.8876654E38)
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r14, r0)
            android.app.Dialog r0 = showSimpleAlert(r1, r0)
            return r0
        L_0x04bc:
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r3, r2)
            android.app.Dialog r0 = showSimpleAlert(r1, r0)
            return r0
        L_0x04c5:
            java.lang.String r0 = r0.text
            int r2 = r0.hashCode()
            r3 = -1809401834(0xfffffffvar_b816, float:-8.417163E-27)
            if (r2 == r3) goto L_0x04ef
            r3 = -454039871(0xffffffffe4efe6c1, float:-3.5403195E22)
            if (r2 == r3) goto L_0x04e5
            r3 = 1169786080(0x45b984e0, float:5936.6094)
            if (r2 == r3) goto L_0x04db
            goto L_0x04f9
        L_0x04db:
            java.lang.String r2 = "SCHEDULE_TOO_MUCH"
            boolean r0 = r0.equals(r2)
            if (r0 == 0) goto L_0x04f9
            r0 = 2
            goto L_0x04fa
        L_0x04e5:
            java.lang.String r2 = "PEER_FLOOD"
            boolean r0 = r0.equals(r2)
            if (r0 == 0) goto L_0x04f9
            r0 = 0
            goto L_0x04fa
        L_0x04ef:
            java.lang.String r2 = "USER_BANNED_IN_CHANNEL"
            boolean r0 = r0.equals(r2)
            if (r0 == 0) goto L_0x04f9
            r0 = 1
            goto L_0x04fa
        L_0x04f9:
            r0 = -1
        L_0x04fa:
            if (r0 == 0) goto L_0x0526
            r2 = 1
            if (r0 == r2) goto L_0x0511
            if (r0 == r14) goto L_0x0503
            goto L_0x05a9
        L_0x0503:
            r0 = 2131625690(0x7f0e06da, float:1.8878595E38)
            java.lang.String r2 = "MessageScheduledLimitReached"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            showSimpleToast(r1, r0)
            goto L_0x05a9
        L_0x0511:
            org.telegram.messenger.NotificationCenter r0 = org.telegram.messenger.NotificationCenter.getInstance(r18)
            int r1 = org.telegram.messenger.NotificationCenter.needShowAlert
            java.lang.Object[] r2 = new java.lang.Object[r2]
            r3 = 5
            java.lang.Integer r3 = java.lang.Integer.valueOf(r3)
            r4 = 0
            r2[r4] = r3
            r0.postNotificationName(r1, r2)
            goto L_0x05a9
        L_0x0526:
            r2 = 1
            r4 = 0
            org.telegram.messenger.NotificationCenter r0 = org.telegram.messenger.NotificationCenter.getInstance(r18)
            int r1 = org.telegram.messenger.NotificationCenter.needShowAlert
            java.lang.Object[] r2 = new java.lang.Object[r2]
            java.lang.Integer r3 = java.lang.Integer.valueOf(r4)
            r2[r4] = r3
            r0.postNotificationName(r1, r2)
            goto L_0x05a9
        L_0x053b:
            if (r1 == 0) goto L_0x0561
            java.lang.String r4 = r0.text
            boolean r4 = r4.equals(r12)
            if (r4 == 0) goto L_0x0561
            if (r7 != 0) goto L_0x0556
            boolean r0 = r2 instanceof org.telegram.tgnet.TLRPC$TL_channels_inviteToChannel
            if (r0 == 0) goto L_0x054c
            goto L_0x0556
        L_0x054c:
            org.telegram.ui.TooManyCommunitiesActivity r0 = new org.telegram.ui.TooManyCommunitiesActivity
            r2 = 1
            r0.<init>(r2)
            r1.presentFragment(r0)
            goto L_0x055f
        L_0x0556:
            org.telegram.ui.TooManyCommunitiesActivity r0 = new org.telegram.ui.TooManyCommunitiesActivity
            r4 = 0
            r0.<init>(r4)
            r1.presentFragment(r0)
        L_0x055f:
            r0 = 0
            return r0
        L_0x0561:
            r4 = 0
            if (r1 == 0) goto L_0x0579
            java.lang.String r0 = r0.text
            if (r3 == 0) goto L_0x0574
            int r5 = r3.length
            if (r5 <= 0) goto L_0x0574
            r3 = r3[r4]
            java.lang.Boolean r3 = (java.lang.Boolean) r3
            boolean r14 = r3.booleanValue()
            goto L_0x0575
        L_0x0574:
            r14 = 0
        L_0x0575:
            showAddUserAlert(r0, r1, r14, r2)
            goto L_0x05a9
        L_0x0579:
            java.lang.String r0 = r0.text
            java.lang.String r1 = "PEER_FLOOD"
            boolean r0 = r0.equals(r1)
            if (r0 == 0) goto L_0x05a9
            org.telegram.messenger.NotificationCenter r0 = org.telegram.messenger.NotificationCenter.getInstance(r18)
            int r1 = org.telegram.messenger.NotificationCenter.needShowAlert
            r2 = 1
            java.lang.Object[] r3 = new java.lang.Object[r2]
            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)
            r4 = 0
            r3[r4] = r2
            r0.postNotificationName(r1, r3)
            goto L_0x05a9
        L_0x0597:
            java.lang.String r2 = r0.text
            boolean r2 = r2.contains(r11)
            if (r2 == 0) goto L_0x05ab
            r2 = 2131625467(0x7f0e05fb, float:1.8878143E38)
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r10, r2)
            showSimpleAlert(r1, r0)
        L_0x05a9:
            r0 = 0
            goto L_0x05f9
        L_0x05ab:
            java.lang.String r2 = r0.text
            boolean r2 = r2.startsWith(r9)
            if (r2 == 0) goto L_0x05be
            r2 = 2131625263(0x7f0e052f, float:1.887773E38)
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r15, r2)
            showSimpleAlert(r1, r0)
            goto L_0x05a9
        L_0x05be:
            java.lang.String r2 = r0.text
            java.lang.String r3 = "APP_VERSION_OUTDATED"
            boolean r2 = r3.equals(r2)
            if (r2 == 0) goto L_0x05da
            android.app.Activity r0 = r20.getParentActivity()
            r1 = 2131627051(0x7f0e0c2b, float:1.8881356E38)
            java.lang.String r2 = "UpdateAppAlert"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r2 = 1
            showUpdateAppAlert(r0, r1, r2)
            goto L_0x05a9
        L_0x05da:
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            r3 = 2131625066(0x7f0e046a, float:1.887733E38)
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r13, r3)
            r2.append(r3)
            r2.append(r8)
            java.lang.String r0 = r0.text
            r2.append(r0)
            java.lang.String r0 = r2.toString()
            showSimpleAlert(r1, r0)
            goto L_0x05a9
        L_0x05f9:
            return r0
        L_0x05fa:
            r0 = r5
            return r0
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

    public static AlertDialog.Builder createLanguageAlert(LaunchActivity launchActivity, TLRPC$TL_langPackLanguage tLRPC$TL_langPackLanguage) {
        String str;
        int i;
        if (tLRPC$TL_langPackLanguage == null) {
            return null;
        }
        tLRPC$TL_langPackLanguage.lang_code = tLRPC$TL_langPackLanguage.lang_code.replace('-', '_').toLowerCase();
        tLRPC$TL_langPackLanguage.plural_code = tLRPC$TL_langPackLanguage.plural_code.replace('-', '_').toLowerCase();
        String str2 = tLRPC$TL_langPackLanguage.base_lang_code;
        if (str2 != null) {
            tLRPC$TL_langPackLanguage.base_lang_code = str2.replace('-', '_').toLowerCase();
        }
        final AlertDialog.Builder builder = new AlertDialog.Builder((Context) launchActivity);
        if (LocaleController.getInstance().getCurrentLocaleInfo().shortName.equals(tLRPC$TL_langPackLanguage.lang_code)) {
            builder.setTitle(LocaleController.getString("Language", NUM));
            str = LocaleController.formatString("LanguageSame", NUM, tLRPC$TL_langPackLanguage.name);
            builder.setNegativeButton(LocaleController.getString("OK", NUM), (DialogInterface.OnClickListener) null);
            builder.setNeutralButton(LocaleController.getString("SETTINGS", NUM), new DialogInterface.OnClickListener() {
                public final void onClick(DialogInterface dialogInterface, int i) {
                    LaunchActivity.this.lambda$runLinkRequest$30$LaunchActivity(new LanguageSelectActivity());
                }
            });
        } else if (tLRPC$TL_langPackLanguage.strings_count == 0) {
            builder.setTitle(LocaleController.getString("LanguageUnknownTitle", NUM));
            str = LocaleController.formatString("LanguageUnknownCustomAlert", NUM, tLRPC$TL_langPackLanguage.name);
            builder.setNegativeButton(LocaleController.getString("OK", NUM), (DialogInterface.OnClickListener) null);
        } else {
            builder.setTitle(LocaleController.getString("LanguageTitle", NUM));
            if (tLRPC$TL_langPackLanguage.official) {
                str = LocaleController.formatString("LanguageAlert", NUM, tLRPC$TL_langPackLanguage.name, Integer.valueOf((int) Math.ceil((double) ((((float) tLRPC$TL_langPackLanguage.translated_count) / ((float) tLRPC$TL_langPackLanguage.strings_count)) * 100.0f))));
            } else {
                str = LocaleController.formatString("LanguageCustomAlert", NUM, tLRPC$TL_langPackLanguage.name, Integer.valueOf((int) Math.ceil((double) ((((float) tLRPC$TL_langPackLanguage.translated_count) / ((float) tLRPC$TL_langPackLanguage.strings_count)) * 100.0f))));
            }
            builder.setPositiveButton(LocaleController.getString("Change", NUM), new DialogInterface.OnClickListener(launchActivity) {
                private final /* synthetic */ LaunchActivity f$1;

                {
                    this.f$1 = r2;
                }

                public final void onClick(DialogInterface dialogInterface, int i) {
                    AlertsCreator.lambda$createLanguageAlert$2(TLRPC$TL_langPackLanguage.this, this.f$1, dialogInterface, i);
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
            spannableStringBuilder.setSpan(new URLSpanNoUnderline(tLRPC$TL_langPackLanguage.translations_url) {
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

    static /* synthetic */ void lambda$createLanguageAlert$2(TLRPC$TL_langPackLanguage tLRPC$TL_langPackLanguage, LaunchActivity launchActivity, DialogInterface dialogInterface, int i) {
        String str;
        if (tLRPC$TL_langPackLanguage.official) {
            str = "remote_" + tLRPC$TL_langPackLanguage.lang_code;
        } else {
            str = "unofficial_" + tLRPC$TL_langPackLanguage.lang_code;
        }
        LocaleController.LocaleInfo languageFromDict = LocaleController.getInstance().getLanguageFromDict(str);
        if (languageFromDict == null) {
            languageFromDict = new LocaleController.LocaleInfo();
            languageFromDict.name = tLRPC$TL_langPackLanguage.native_name;
            languageFromDict.nameEnglish = tLRPC$TL_langPackLanguage.name;
            languageFromDict.shortName = tLRPC$TL_langPackLanguage.lang_code;
            languageFromDict.baseLangCode = tLRPC$TL_langPackLanguage.base_lang_code;
            languageFromDict.pluralLangCode = tLRPC$TL_langPackLanguage.plural_code;
            languageFromDict.isRtl = tLRPC$TL_langPackLanguage.rtl;
            if (tLRPC$TL_langPackLanguage.official) {
                languageFromDict.pathToFile = "remote";
            } else {
                languageFromDict.pathToFile = "unofficial";
            }
        }
        LocaleController.getInstance().applyLanguage(languageFromDict, true, false, false, true, UserConfig.selectedAccount);
        launchActivity.rebuildAllFragments(true);
    }

    public static boolean checkSlowMode(Context context, int i, long j, boolean z) {
        TLRPC$Chat chat;
        int i2 = (int) j;
        if (i2 < 0 && (chat = MessagesController.getInstance(i).getChat(Integer.valueOf(-i2))) != null && chat.slowmode_enabled && !ChatObject.hasAdminRights(chat)) {
            if (!z) {
                TLRPC$ChatFull chatFull = MessagesController.getInstance(i).getChatFull(chat.id);
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
    public static void showBlockReportSpamAlert(org.telegram.ui.ActionBar.BaseFragment r18, long r19, org.telegram.tgnet.TLRPC$User r21, org.telegram.tgnet.TLRPC$Chat r22, org.telegram.tgnet.TLRPC$EncryptedChat r23, boolean r24, org.telegram.tgnet.TLRPC$ChatFull r25, org.telegram.messenger.MessagesStorage.IntCallback r26) {
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
            r1 = 2131624422(0x7f0e01e6, float:1.8876023E38)
            java.lang.Object[] r6 = new java.lang.Object[r4]
            java.lang.String r10 = org.telegram.messenger.UserObject.getFirstName(r21)
            r6[r5] = r10
            java.lang.String r10 = "BlockUserTitle"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r10, r1, r6)
            r11.setTitle(r1)
            r1 = 2131624417(0x7f0e01e1, float:1.8876013E38)
            java.lang.Object[] r6 = new java.lang.Object[r4]
            java.lang.String r10 = org.telegram.messenger.UserObject.getFirstName(r21)
            r6[r5] = r10
            java.lang.String r10 = "BlockUserAlert"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r10, r1, r6)
            android.text.SpannableStringBuilder r1 = org.telegram.messenger.AndroidUtilities.replaceTags(r1)
            r11.setMessage(r1)
            r1 = 2131624415(0x7f0e01df, float:1.887601E38)
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
            r12 = 2131624909(0x7f0e03cd, float:1.8877011E38)
            r16 = r1
            java.lang.String r1 = "DeleteReportSpam"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r1, r12)
            r13.setText(r1, r6, r4, r5)
            goto L_0x00e4
        L_0x00cd:
            r16 = r1
            if (r15 != r4) goto L_0x00e4
            r1 = r10[r15]
            r12 = 2131624917(0x7f0e03d5, float:1.8877027E38)
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
            r2 = 2131626531(0x7f0e0a23, float:1.88803E38)
            java.lang.String r6 = "ReportUnrelatedGroup"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r6, r2)
            r11.setTitle(r2)
            if (r1 == 0) goto L_0x0167
            org.telegram.tgnet.TLRPC$ChannelLocation r1 = r1.location
            boolean r2 = r1 instanceof org.telegram.tgnet.TLRPC$TL_channelLocation
            if (r2 == 0) goto L_0x0167
            org.telegram.tgnet.TLRPC$TL_channelLocation r1 = (org.telegram.tgnet.TLRPC$TL_channelLocation) r1
            r2 = 2131626532(0x7f0e0a24, float:1.8880303E38)
            java.lang.Object[] r4 = new java.lang.Object[r4]
            java.lang.String r1 = r1.address
            r4[r5] = r1
            java.lang.String r1 = "ReportUnrelatedGroupText"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r4)
            android.text.SpannableStringBuilder r1 = org.telegram.messenger.AndroidUtilities.replaceTags(r1)
            r11.setMessage(r1)
            goto L_0x01a3
        L_0x0167:
            r1 = 2131626533(0x7f0e0a25, float:1.8880305E38)
            java.lang.String r2 = "ReportUnrelatedGroupTextNoAddress"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r11.setMessage(r1)
            goto L_0x01a3
        L_0x0174:
            r1 = 2131626529(0x7f0e0a21, float:1.8880297E38)
            java.lang.String r2 = "ReportSpamTitle"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r11.setTitle(r1)
            boolean r1 = org.telegram.messenger.ChatObject.isChannel(r22)
            if (r1 == 0) goto L_0x0197
            boolean r1 = r7.megagroup
            if (r1 != 0) goto L_0x0197
            r1 = 2131626525(0x7f0e0a1d, float:1.8880289E38)
            java.lang.String r2 = "ReportSpamAlertChannel"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r11.setMessage(r1)
            goto L_0x01a3
        L_0x0197:
            r1 = 2131626526(0x7f0e0a1e, float:1.888029E38)
            java.lang.String r2 = "ReportSpamAlertGroup"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r11.setMessage(r1)
        L_0x01a3:
            r1 = 2131626515(0x7f0e0a13, float:1.8880268E38)
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
            r1 = 2131624484(0x7f0e0224, float:1.887615E38)
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

    static /* synthetic */ void lambda$showBlockReportSpamAlert$4(TLRPC$User tLRPC$User, AccountInstance accountInstance, CheckBoxCell[] checkBoxCellArr, long j, TLRPC$Chat tLRPC$Chat, TLRPC$EncryptedChat tLRPC$EncryptedChat, boolean z, MessagesStorage.IntCallback intCallback, DialogInterface dialogInterface, int i) {
        TLRPC$User tLRPC$User2 = tLRPC$User;
        long j2 = j;
        MessagesStorage.IntCallback intCallback2 = intCallback;
        if (tLRPC$User2 != null) {
            accountInstance.getMessagesController().blockUser(tLRPC$User2.id);
        }
        if (checkBoxCellArr == null || (checkBoxCellArr[0] != null && checkBoxCellArr[0].isChecked())) {
            accountInstance.getMessagesController().reportSpam(j, tLRPC$User, tLRPC$Chat, tLRPC$EncryptedChat, tLRPC$Chat != null && z);
        }
        if (checkBoxCellArr == null || checkBoxCellArr[1].isChecked()) {
            if (tLRPC$Chat == null) {
                accountInstance.getMessagesController().deleteDialog(j2, 0);
            } else if (ChatObject.isNotInChat(tLRPC$Chat)) {
                accountInstance.getMessagesController().deleteDialog(j2, 0);
            } else {
                accountInstance.getMessagesController().deleteUserFromChat((int) (-j2), accountInstance.getMessagesController().getUser(Integer.valueOf(accountInstance.getUserConfig().getClientUserId())), (TLRPC$ChatFull) null);
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
        String[] strArr;
        Drawable drawable;
        int[] iArr;
        AlertDialog.Builder builder;
        int i3;
        LinearLayout linearLayout;
        BaseFragment baseFragment2 = baseFragment;
        long j2 = j;
        if (baseFragment2 != null && baseFragment.getParentActivity() != null) {
            boolean isGlobalNotificationsEnabled = NotificationsController.getInstance(i2).isGlobalNotificationsEnabled(j2);
            int i4 = 5;
            String[] strArr2 = new String[5];
            strArr2[0] = LocaleController.getString("NotificationsTurnOn", NUM);
            boolean z = true;
            strArr2[1] = LocaleController.formatString("MuteFor", NUM, LocaleController.formatPluralString("Hours", 1));
            strArr2[2] = LocaleController.formatString("MuteFor", NUM, LocaleController.formatPluralString("Days", 2));
            Drawable drawable2 = null;
            if (j2 != 0 || !(baseFragment2 instanceof NotificationsCustomSettingsActivity)) {
                str = LocaleController.getString("NotificationsCustomize", NUM);
            } else {
                str = null;
            }
            strArr2[3] = str;
            int i5 = 4;
            strArr2[4] = LocaleController.getString("NotificationsTurnOff", NUM);
            int[] iArr2 = {NUM, NUM, NUM, NUM, NUM};
            LinearLayout linearLayout2 = new LinearLayout(baseFragment.getParentActivity());
            linearLayout2.setOrientation(1);
            AlertDialog.Builder builder2 = new AlertDialog.Builder((Context) baseFragment.getParentActivity());
            int i6 = 0;
            while (i6 < i4) {
                if (strArr2[i6] == null) {
                    i3 = i6;
                    builder = builder2;
                    linearLayout = linearLayout2;
                    iArr = iArr2;
                    drawable = drawable2;
                    strArr = strArr2;
                } else {
                    TextView textView = new TextView(baseFragment.getParentActivity());
                    Drawable drawable3 = baseFragment.getParentActivity().getResources().getDrawable(iArr2[i6]);
                    if (i6 == i5) {
                        textView.setTextColor(Theme.getColor("dialogTextRed"));
                        drawable3.setColorFilter(new PorterDuffColorFilter(Theme.getColor("dialogRedIcon"), PorterDuff.Mode.MULTIPLY));
                    } else {
                        textView.setTextColor(Theme.getColor("dialogTextBlack"));
                        drawable3.setColorFilter(new PorterDuffColorFilter(Theme.getColor("dialogIcon"), PorterDuff.Mode.MULTIPLY));
                    }
                    textView.setTextSize(z ? 1 : 0, 16.0f);
                    textView.setLines(z);
                    textView.setMaxLines(z);
                    textView.setCompoundDrawablesWithIntrinsicBounds(drawable3, drawable2, drawable2, drawable2);
                    textView.setTag(Integer.valueOf(i6));
                    textView.setBackgroundDrawable(Theme.getSelectorDrawable(false));
                    textView.setPadding(AndroidUtilities.dp(24.0f), 0, AndroidUtilities.dp(24.0f), 0);
                    textView.setSingleLine(z);
                    textView.setGravity(19);
                    textView.setCompoundDrawablePadding(AndroidUtilities.dp(26.0f));
                    textView.setText(strArr2[i6]);
                    linearLayout2.addView(textView, LayoutHelper.createLinear(-1, 48, 51));
                    i3 = i6;
                    builder = builder2;
                    linearLayout = linearLayout2;
                    iArr = iArr2;
                    drawable = drawable2;
                    strArr = strArr2;
                    textView.setOnClickListener(new View.OnClickListener(j, i2, isGlobalNotificationsEnabled, intCallback2, i, baseFragment, arrayList, intCallback, builder) {
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
                    });
                }
                i6 = i3 + 1;
                linearLayout2 = linearLayout;
                builder2 = builder;
                iArr2 = iArr;
                drawable2 = drawable;
                strArr2 = strArr;
                i5 = 4;
                z = true;
                i4 = 5;
                long j3 = j;
            }
            AlertDialog.Builder builder3 = builder2;
            builder3.setTitle(LocaleController.getString("Notifications", NUM));
            builder3.setView(linearLayout2);
            baseFragment2.showDialog(builder3.create());
        }
    }

    static /* synthetic */ void lambda$showCustomNotificationsDialog$5(long j, int i, boolean z, MessagesStorage.IntCallback intCallback, int i2, BaseFragment baseFragment, ArrayList arrayList, MessagesStorage.IntCallback intCallback2, AlertDialog.Builder builder, View view) {
        long j2 = j;
        MessagesStorage.IntCallback intCallback3 = intCallback;
        int i3 = i2;
        BaseFragment baseFragment2 = baseFragment;
        MessagesStorage.IntCallback intCallback4 = intCallback2;
        int intValue = ((Integer) view.getTag()).intValue();
        long j3 = 0;
        if (intValue == 0) {
            if (j2 != 0) {
                SharedPreferences.Editor edit = MessagesController.getNotificationsSettings(i).edit();
                if (z) {
                    edit.remove("notify2_" + j2);
                } else {
                    edit.putInt("notify2_" + j2, 0);
                }
                MessagesStorage.getInstance(i).setDialogFlags(j2, 0);
                edit.commit();
                TLRPC$Dialog tLRPC$Dialog = MessagesController.getInstance(i).dialogs_dict.get(j2);
                if (tLRPC$Dialog != null) {
                    tLRPC$Dialog.notify_settings = new TLRPC$TL_peerNotifySettings();
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
                SharedPreferences.Editor edit2 = MessagesController.getNotificationsSettings(i).edit();
                if (intValue != 4) {
                    edit2.putInt("notify2_" + j2, 3);
                    edit2.putInt("notifyuntil_" + j2, currentTime);
                    j3 = (((long) currentTime) << 32) | 1;
                } else if (!z) {
                    edit2.remove("notify2_" + j2);
                } else {
                    edit2.putInt("notify2_" + j2, 2);
                    j3 = 1;
                }
                NotificationsController.getInstance(i).removeNotificationsForDialog(j2);
                MessagesStorage.getInstance(i).setDialogFlags(j2, j3);
                edit2.commit();
                TLRPC$Dialog tLRPC$Dialog2 = MessagesController.getInstance(i).dialogs_dict.get(j2);
                if (tLRPC$Dialog2 != null) {
                    tLRPC$Dialog2.notify_settings = new TLRPC$TL_peerNotifySettings();
                    if (intValue != 4 || z) {
                        tLRPC$Dialog2.notify_settings.mute_until = currentTime;
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
        TLRPC$User tLRPC$User = null;
        if (i != 0) {
            TLRPC$User user = MessagesController.getInstance(currentAccount).getUser(Integer.valueOf(i));
            if (user == null && (string = mainSettings.getString("support_user", (String) null)) != null) {
                try {
                    byte[] decode = Base64.decode(string, 0);
                    if (decode != null) {
                        SerializedData serializedData = new SerializedData(decode);
                        TLRPC$User TLdeserialize = TLRPC$User.TLdeserialize(serializedData, serializedData.readInt32(false), false);
                        if (TLdeserialize != null && TLdeserialize.id == 333000) {
                            TLdeserialize = null;
                        }
                        serializedData.cleanup();
                        tLRPC$User = TLdeserialize;
                    }
                } catch (Exception e) {
                    FileLog.e((Throwable) e);
                }
            }
            tLRPC$User = user;
        }
        if (tLRPC$User == null) {
            AlertDialog alertDialog = new AlertDialog(baseFragment.getParentActivity(), 3);
            alertDialog.setCanCacnel(false);
            alertDialog.show();
            ConnectionsManager.getInstance(currentAccount).sendRequest(new TLRPC$TL_help_getSupport(), new RequestDelegate(mainSettings, alertDialog, currentAccount, baseFragment) {
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

                public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                    AlertsCreator.lambda$performAskAQuestion$10(this.f$0, this.f$1, this.f$2, this.f$3, tLObject, tLRPC$TL_error);
                }
            });
            return;
        }
        MessagesController.getInstance(currentAccount).putUser(tLRPC$User, true);
        Bundle bundle = new Bundle();
        bundle.putInt("user_id", tLRPC$User.id);
        baseFragment.presentFragment(new ChatActivity(bundle));
    }

    static /* synthetic */ void lambda$performAskAQuestion$10(SharedPreferences sharedPreferences, AlertDialog alertDialog, int i, BaseFragment baseFragment, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        if (tLRPC$TL_error == null) {
            AndroidUtilities.runOnUIThread(new Runnable(sharedPreferences, (TLRPC$TL_help_support) tLObject, alertDialog, i, baseFragment) {
                private final /* synthetic */ SharedPreferences f$0;
                private final /* synthetic */ TLRPC$TL_help_support f$1;
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

    static /* synthetic */ void lambda$null$8(SharedPreferences sharedPreferences, TLRPC$TL_help_support tLRPC$TL_help_support, AlertDialog alertDialog, int i, BaseFragment baseFragment) {
        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.putInt("support_id", tLRPC$TL_help_support.user.id);
        SerializedData serializedData = new SerializedData();
        tLRPC$TL_help_support.user.serializeToStream(serializedData);
        edit.putString("support_user", Base64.encodeToString(serializedData.toByteArray(), 0));
        edit.commit();
        serializedData.cleanup();
        try {
            alertDialog.dismiss();
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
        ArrayList arrayList = new ArrayList();
        arrayList.add(tLRPC$TL_help_support.user);
        MessagesStorage.getInstance(i).putUsersAndChats(arrayList, (ArrayList<TLRPC$Chat>) null, true, true);
        MessagesController.getInstance(i).putUser(tLRPC$TL_help_support.user, false);
        Bundle bundle = new Bundle();
        bundle.putInt("user_id", tLRPC$TL_help_support.user.id);
        baseFragment.presentFragment(new ChatActivity(bundle));
    }

    static /* synthetic */ void lambda$null$9(AlertDialog alertDialog) {
        try {
            alertDialog.dismiss();
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    public static void createClearOrDeleteDialogAlert(BaseFragment baseFragment, boolean z, TLRPC$Chat tLRPC$Chat, TLRPC$User tLRPC$User, boolean z2, MessagesStorage.BooleanCallback booleanCallback) {
        createClearOrDeleteDialogAlert(baseFragment, z, false, false, tLRPC$Chat, tLRPC$User, z2, booleanCallback);
    }

    public static void createClearOrDeleteDialogAlert(BaseFragment baseFragment, boolean z, boolean z2, boolean z3, TLRPC$Chat tLRPC$Chat, TLRPC$User tLRPC$User, boolean z4, MessagesStorage.BooleanCallback booleanCallback) {
        AvatarDrawable avatarDrawable;
        boolean z5;
        int i;
        boolean z6;
        int i2;
        String string;
        char c;
        int i3;
        float f;
        TLRPC$Chat tLRPC$Chat2 = tLRPC$Chat;
        TLRPC$User tLRPC$User2 = tLRPC$User;
        if (baseFragment != null && baseFragment.getParentActivity() != null) {
            if (tLRPC$Chat2 != null || tLRPC$User2 != null) {
                int currentAccount = baseFragment.getCurrentAccount();
                Activity parentActivity = baseFragment.getParentActivity();
                AlertDialog.Builder builder = new AlertDialog.Builder((Context) parentActivity);
                int clientUserId = UserConfig.getInstance(currentAccount).getClientUserId();
                final CheckBoxCell[] checkBoxCellArr = new CheckBoxCell[1];
                TextView textView = new TextView(parentActivity);
                textView.setTextColor(Theme.getColor("dialogTextBlack"));
                textView.setTextSize(1, 16.0f);
                textView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
                boolean z7 = ChatObject.isChannel(tLRPC$Chat) && !TextUtils.isEmpty(tLRPC$Chat2.username);
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
                AvatarDrawable avatarDrawable2 = new AvatarDrawable();
                avatarDrawable2.setTextSize(AndroidUtilities.dp(12.0f));
                BackupImageView backupImageView = new BackupImageView(parentActivity);
                backupImageView.setRoundRadius(AndroidUtilities.dp(20.0f));
                r15.addView(backupImageView, LayoutHelper.createFrame(40, 40.0f, (LocaleController.isRTL ? 5 : 3) | 48, 22.0f, 5.0f, 22.0f, 0.0f));
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
                BackupImageView backupImageView2 = backupImageView;
                if (!z) {
                    z5 = z7;
                    avatarDrawable = avatarDrawable2;
                    if (z2) {
                        if (!ChatObject.isChannel(tLRPC$Chat)) {
                            textView2.setText(LocaleController.getString("DeleteMegaMenu", NUM));
                        } else if (tLRPC$Chat2.megagroup) {
                            textView2.setText(LocaleController.getString("DeleteMegaMenu", NUM));
                        } else {
                            textView2.setText(LocaleController.getString("ChannelDeleteMenu", NUM));
                        }
                    } else if (tLRPC$Chat2 == null) {
                        textView2.setText(LocaleController.getString("DeleteChatUser", NUM));
                    } else if (!ChatObject.isChannel(tLRPC$Chat)) {
                        textView2.setText(LocaleController.getString("LeaveMegaMenu", NUM));
                    } else if (tLRPC$Chat2.megagroup) {
                        textView2.setText(LocaleController.getString("LeaveMegaMenu", NUM));
                    } else {
                        textView2.setText(LocaleController.getString("LeaveChannelMenu", NUM));
                    }
                } else if (z7) {
                    z5 = z7;
                    avatarDrawable = avatarDrawable2;
                    textView2.setText(LocaleController.getString("ClearHistoryCache", NUM));
                } else {
                    z5 = z7;
                    avatarDrawable = avatarDrawable2;
                    textView2.setText(LocaleController.getString("ClearHistory", NUM));
                }
                int i4 = (LocaleController.isRTL ? 5 : 3) | 48;
                int i5 = 21;
                float f2 = (float) (LocaleController.isRTL ? 21 : 76);
                if (LocaleController.isRTL) {
                    i5 = 76;
                }
                r15.addView(textView2, LayoutHelper.createFrame(-1, -2.0f, i4, f2, 11.0f, (float) i5, 0.0f));
                r15.addView(textView, LayoutHelper.createFrame(-2, -2.0f, (LocaleController.isRTL ? 5 : 3) | 48, 24.0f, 57.0f, 24.0f, 9.0f));
                boolean z8 = tLRPC$User2 != null && !tLRPC$User2.bot && tLRPC$User2.id != clientUserId && MessagesController.getInstance(currentAccount).canRevokePmInbox;
                if (tLRPC$User2 != null) {
                    i = MessagesController.getInstance(currentAccount).revokeTimePmLimit;
                } else {
                    i = MessagesController.getInstance(currentAccount).revokeTimeLimit;
                }
                if (z4 || tLRPC$User2 == null || !z8 || i != Integer.MAX_VALUE) {
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
                        objArr[0] = UserObject.getFirstName(tLRPC$User);
                        String formatString = LocaleController.formatString("ClearHistoryOptionAlso", NUM, objArr);
                        c = 0;
                        checkBoxCell.setText(formatString, "", false, false);
                    } else {
                        c = 0;
                        checkBoxCellArr[0].setText(LocaleController.formatString("DeleteMessagesOptionAlso", NUM, UserObject.getFirstName(tLRPC$User)), "", false, false);
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
                if (tLRPC$User2 == null) {
                    BackupImageView backupImageView3 = backupImageView2;
                    AvatarDrawable avatarDrawable3 = avatarDrawable;
                    if (tLRPC$Chat2 != null) {
                        avatarDrawable3.setInfo(tLRPC$Chat2);
                        backupImageView3.setImage(ImageLocation.getForChat(tLRPC$Chat2, false), "50_50", (Drawable) avatarDrawable3, (Object) tLRPC$Chat2);
                    }
                } else if (tLRPC$User2.id == clientUserId) {
                    AvatarDrawable avatarDrawable4 = avatarDrawable;
                    avatarDrawable4.setSmallSize(true);
                    avatarDrawable4.setAvatarType(1);
                    backupImageView2.setImage((ImageLocation) null, (String) null, (Drawable) avatarDrawable4, (Object) tLRPC$User2);
                } else {
                    AvatarDrawable avatarDrawable5 = avatarDrawable;
                    avatarDrawable5.setSmallSize(false);
                    avatarDrawable5.setInfo(tLRPC$User2);
                    backupImageView2.setImage(ImageLocation.getForUser(tLRPC$User2, false), "50_50", (Drawable) avatarDrawable5, (Object) tLRPC$User2);
                }
                if (z3) {
                    textView.setText(AndroidUtilities.replaceTags(LocaleController.getString("DeleteAllMessagesAlert", NUM)));
                } else if (z) {
                    if (tLRPC$User2 != null) {
                        if (z4) {
                            textView.setText(AndroidUtilities.replaceTags(LocaleController.formatString("AreYouSureClearHistoryWithSecretUser", NUM, UserObject.getUserName(tLRPC$User))));
                        } else if (tLRPC$User2.id == clientUserId) {
                            textView.setText(AndroidUtilities.replaceTags(LocaleController.getString("AreYouSureClearHistorySavedMessages", NUM)));
                        } else {
                            textView.setText(AndroidUtilities.replaceTags(LocaleController.formatString("AreYouSureClearHistoryWithUser", NUM, UserObject.getUserName(tLRPC$User))));
                        }
                    } else if (tLRPC$Chat2 != null) {
                        if (!ChatObject.isChannel(tLRPC$Chat) || (tLRPC$Chat2.megagroup && TextUtils.isEmpty(tLRPC$Chat2.username))) {
                            textView.setText(AndroidUtilities.replaceTags(LocaleController.formatString("AreYouSureClearHistoryWithChat", NUM, tLRPC$Chat2.title)));
                        } else if (tLRPC$Chat2.megagroup) {
                            textView.setText(LocaleController.getString("AreYouSureClearHistoryGroup", NUM));
                        } else {
                            textView.setText(LocaleController.getString("AreYouSureClearHistoryChannel", NUM));
                        }
                    }
                } else if (z2) {
                    if (!ChatObject.isChannel(tLRPC$Chat)) {
                        textView.setText(LocaleController.getString("AreYouSureDeleteAndExit", NUM));
                    } else if (tLRPC$Chat2.megagroup) {
                        textView.setText(LocaleController.getString("MegaDeleteAlert", NUM));
                    } else {
                        textView.setText(LocaleController.getString("ChannelDeleteAlert", NUM));
                    }
                } else if (tLRPC$User2 != null) {
                    if (z4) {
                        textView.setText(AndroidUtilities.replaceTags(LocaleController.formatString("AreYouSureDeleteThisChatWithSecretUser", NUM, UserObject.getUserName(tLRPC$User))));
                    } else if (tLRPC$User2.id == clientUserId) {
                        textView.setText(AndroidUtilities.replaceTags(LocaleController.getString("AreYouSureDeleteThisChatSavedMessages", NUM)));
                    } else {
                        textView.setText(AndroidUtilities.replaceTags(LocaleController.formatString("AreYouSureDeleteThisChatWithUser", NUM, UserObject.getUserName(tLRPC$User))));
                    }
                } else if (!ChatObject.isChannel(tLRPC$Chat)) {
                    textView.setText(AndroidUtilities.replaceTags(LocaleController.formatString("AreYouSureDeleteAndExitName", NUM, tLRPC$Chat2.title)));
                } else if (tLRPC$Chat2.megagroup) {
                    textView.setText(AndroidUtilities.replaceTags(LocaleController.formatString("MegaLeaveAlertWithName", NUM, tLRPC$Chat2.title)));
                } else {
                    textView.setText(AndroidUtilities.replaceTags(LocaleController.formatString("ChannelLeaveAlertWithName", NUM, tLRPC$Chat2.title)));
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
                    if (!ChatObject.isChannel(tLRPC$Chat)) {
                        string = LocaleController.getString("DeleteMega", NUM);
                    } else if (tLRPC$Chat2.megagroup) {
                        string = LocaleController.getString("DeleteMega", NUM);
                    } else {
                        string = LocaleController.getString("ChannelDelete", NUM);
                    }
                } else if (!ChatObject.isChannel(tLRPC$Chat)) {
                    string = LocaleController.getString("DeleteChatUser", NUM);
                } else if (tLRPC$Chat2.megagroup) {
                    string = LocaleController.getString("LeaveMegaMenu", NUM);
                } else {
                    string = LocaleController.getString("LeaveChannelMenu", NUM);
                }
                String str = string;
                AlertDialog.Builder builder3 = builder2;
                builder3.setPositiveButton(str, new DialogInterface.OnClickListener(z5, z3, zArr, baseFragment, z, z2, tLRPC$Chat, z4, booleanCallback) {
                    private final /* synthetic */ boolean f$1;
                    private final /* synthetic */ boolean f$2;
                    private final /* synthetic */ boolean[] f$3;
                    private final /* synthetic */ BaseFragment f$4;
                    private final /* synthetic */ boolean f$5;
                    private final /* synthetic */ boolean f$6;
                    private final /* synthetic */ TLRPC$Chat f$7;
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
                        AlertsCreator.lambda$createClearOrDeleteDialogAlert$13(TLRPC$User.this, this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, this.f$8, this.f$9, dialogInterface, i);
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

    static /* synthetic */ void lambda$createClearOrDeleteDialogAlert$13(TLRPC$User tLRPC$User, boolean z, boolean z2, boolean[] zArr, BaseFragment baseFragment, boolean z3, boolean z4, TLRPC$Chat tLRPC$Chat, boolean z5, MessagesStorage.BooleanCallback booleanCallback, DialogInterface dialogInterface, int i) {
        TLRPC$User tLRPC$User2 = tLRPC$User;
        MessagesStorage.BooleanCallback booleanCallback2 = booleanCallback;
        boolean z6 = false;
        if (tLRPC$User2 != null && !z && !z2 && zArr[0]) {
            MessagesStorage.getInstance(baseFragment.getCurrentAccount()).getMessagesCount((long) tLRPC$User2.id, new MessagesStorage.IntCallback(z3, z4, tLRPC$Chat, tLRPC$User, z5, booleanCallback, zArr) {
                private final /* synthetic */ boolean f$1;
                private final /* synthetic */ boolean f$2;
                private final /* synthetic */ TLRPC$Chat f$3;
                private final /* synthetic */ TLRPC$User f$4;
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

    static /* synthetic */ void lambda$null$12(BaseFragment baseFragment, boolean z, boolean z2, TLRPC$Chat tLRPC$Chat, TLRPC$User tLRPC$User, boolean z3, MessagesStorage.BooleanCallback booleanCallback, boolean[] zArr, int i) {
        MessagesStorage.BooleanCallback booleanCallback2 = booleanCallback;
        if (i >= 50) {
            createClearOrDeleteDialogAlert(baseFragment, z, z2, true, tLRPC$Chat, tLRPC$User, z3, booleanCallback);
        } else if (booleanCallback2 != null) {
            booleanCallback.run(zArr[0]);
        }
    }

    public static void createBlockDialogAlert(BaseFragment baseFragment, int i, boolean z, TLRPC$User tLRPC$User, BlockDialogCallback blockDialogCallback) {
        String str;
        String str2;
        int i2;
        BaseFragment baseFragment2 = baseFragment;
        int i3 = i;
        TLRPC$User tLRPC$User2 = tLRPC$User;
        if (baseFragment2 != null && baseFragment.getParentActivity() != null) {
            if (i3 != 1 || tLRPC$User2 != null) {
                Activity parentActivity = baseFragment.getParentActivity();
                AlertDialog.Builder builder = new AlertDialog.Builder((Context) parentActivity);
                CheckBoxCell[] checkBoxCellArr = new CheckBoxCell[2];
                LinearLayout linearLayout = new LinearLayout(parentActivity);
                linearLayout.setOrientation(1);
                builder.setView(linearLayout);
                if (i3 == 1) {
                    String formatName = ContactsController.formatName(tLRPC$User2.first_name, tLRPC$User2.last_name);
                    builder.setTitle(LocaleController.formatString("BlockUserTitle", NUM, formatName));
                    str = LocaleController.getString("BlockUser", NUM);
                    builder.setMessage(AndroidUtilities.replaceTags(LocaleController.formatString("BlockUserMessage", NUM, formatName)));
                } else {
                    builder.setTitle(LocaleController.formatString("BlockUserTitle", NUM, LocaleController.formatPluralString("UsersCountTitle", i3)));
                    str = LocaleController.getString("BlockUsers", NUM);
                    builder.setMessage(AndroidUtilities.replaceTags(LocaleController.formatString("BlockUsersMessage", NUM, LocaleController.formatPluralString("UsersCount", i3))));
                }
                boolean[] zArr = {true, true};
                int i4 = 0;
                for (int i5 = 2; i4 < i5; i5 = 2) {
                    if (i4 != 0 || z) {
                        checkBoxCellArr[i4] = new CheckBoxCell(parentActivity, 1);
                        checkBoxCellArr[i4].setBackgroundDrawable(Theme.getSelectorDrawable(false));
                        if (i4 == 0) {
                            checkBoxCellArr[i4].setText(LocaleController.getString("ReportSpamTitle", NUM), "", true, false);
                        } else {
                            CheckBoxCell checkBoxCell = checkBoxCellArr[i4];
                            if (i3 == 1) {
                                i2 = NUM;
                                str2 = "DeleteThisChat";
                            } else {
                                i2 = NUM;
                                str2 = "DeleteTheseChats";
                            }
                            checkBoxCell.setText(LocaleController.getString(str2, i2), "", true, false);
                        }
                        checkBoxCellArr[i4].setPadding(LocaleController.isRTL ? AndroidUtilities.dp(16.0f) : AndroidUtilities.dp(8.0f), 0, LocaleController.isRTL ? AndroidUtilities.dp(8.0f) : AndroidUtilities.dp(16.0f), 0);
                        linearLayout.addView(checkBoxCellArr[i4], LayoutHelper.createLinear(-1, 48));
                        checkBoxCellArr[i4].setOnClickListener(new View.OnClickListener(zArr, i4) {
                            private final /* synthetic */ boolean[] f$0;
                            private final /* synthetic */ int f$1;

                            {
                                this.f$0 = r1;
                                this.f$1 = r2;
                            }

                            public final void onClick(View view) {
                                AlertsCreator.lambda$createBlockDialogAlert$14(this.f$0, this.f$1, view);
                            }
                        });
                    }
                    i4++;
                }
                builder.setPositiveButton(str, new DialogInterface.OnClickListener(zArr) {
                    private final /* synthetic */ boolean[] f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void onClick(DialogInterface dialogInterface, int i) {
                        AlertsCreator.BlockDialogCallback.this.run(this.f$1[0], this.f$1[1]);
                    }
                });
                builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
                AlertDialog create = builder.create();
                baseFragment2.showDialog(create);
                TextView textView = (TextView) create.getButton(-1);
                if (textView != null) {
                    textView.setTextColor(Theme.getColor("dialogTextRed2"));
                }
            }
        }
    }

    static /* synthetic */ void lambda$createBlockDialogAlert$14(boolean[] zArr, int i, View view) {
        zArr[i] = !zArr[i];
        ((CheckBoxCell) view).setChecked(zArr[i], true);
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
                AlertsCreator.lambda$createDatePickerDialog$16(this.f$0, this.f$1, this.f$2, this.f$3, numberPicker, i);
            }
        });
        numberPicker.setMinValue(0);
        numberPicker.setMaxValue(11);
        linearLayout.addView(numberPicker, LayoutHelper.createLinear(0, -2, 0.3f));
        numberPicker.setFormatter($$Lambda$AlertsCreator$hd9d9OXsweIQ2zSebducWtxQnd4.INSTANCE);
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
                AlertsCreator.lambda$createDatePickerDialog$19(this.f$0, this.f$1, this.f$2, this.f$3, numberPicker, i);
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
                AlertsCreator.lambda$createDatePickerDialog$21(this.f$0, this.f$1, this.f$2, this.f$3, numberPicker, i);
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
                AlertsCreator.lambda$createDatePickerDialog$22(this.f$0, this.f$1, this.f$2, this.f$3, this.f$4, dialogInterface, i);
            }
        });
        builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
        return builder;
    }

    static /* synthetic */ void lambda$createDatePickerDialog$16(boolean z, NumberPicker numberPicker, NumberPicker numberPicker2, NumberPicker numberPicker3, NumberPicker numberPicker4, int i) {
        if (z && i == 0) {
            checkPickerDate(numberPicker, numberPicker2, numberPicker3);
        }
    }

    static /* synthetic */ String lambda$createDatePickerDialog$17(int i) {
        Calendar instance = Calendar.getInstance();
        instance.set(5, 1);
        instance.set(2, i);
        return instance.getDisplayName(2, 1, Locale.getDefault());
    }

    static /* synthetic */ void lambda$createDatePickerDialog$19(boolean z, NumberPicker numberPicker, NumberPicker numberPicker2, NumberPicker numberPicker3, NumberPicker numberPicker4, int i) {
        if (z && i == 0) {
            checkPickerDate(numberPicker, numberPicker2, numberPicker3);
        }
    }

    static /* synthetic */ void lambda$createDatePickerDialog$21(boolean z, NumberPicker numberPicker, NumberPicker numberPicker2, NumberPicker numberPicker3, NumberPicker numberPicker4, int i) {
        if (z && i == 0) {
            checkPickerDate(numberPicker, numberPicker2, numberPicker3);
        }
    }

    static /* synthetic */ void lambda$createDatePickerDialog$22(boolean z, NumberPicker numberPicker, NumberPicker numberPicker2, NumberPicker numberPicker3, DatePickerDelegate datePickerDelegate, DialogInterface dialogInterface, int i) {
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
        TLRPC$User user;
        TLRPC$UserStatus tLRPC$UserStatus;
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
        textView.setOnTouchListener($$Lambda$AlertsCreator$syZEKWnPec7AbHF__QsA81KTQt0.INSTANCE);
        int i = (int) j4;
        if (i <= 0 || j4 == j5 || (user = MessagesController.getInstance(UserConfig.selectedAccount).getUser(Integer.valueOf(i))) == null || user.bot || (tLRPC$UserStatus = user.status) == null || tLRPC$UserStatus.expires <= 0) {
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
                    AlertsCreator.lambda$createScheduleDatePickerDialog$25(AlertsCreator.ScheduleDatePickerDelegate.this, this.f$1, i);
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
                return AlertsCreator.lambda$createScheduleDatePickerDialog$26(this.f$0, this.f$1, this.f$2, i);
            }
        });
        Calendar calendar2 = instance;
        $$Lambda$AlertsCreator$Z1Tn9Q6hPnne851hJPH_ELNfygg r12 = r0;
        int i3 = clientUserId;
        AnonymousClass4 r10 = r7;
        $$Lambda$AlertsCreator$Z1Tn9Q6hPnne851hJPH_ELNfygg r0 = new NumberPicker.OnValueChangeListener(textView2, clientUserId, j, numberPicker, numberPicker2, numberPicker3) {
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
                AlertsCreator.lambda$createScheduleDatePickerDialog$27(this.f$0, this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, numberPicker, i, i2);
            }
        };
        numberPicker.setOnValueChangedListener(r12);
        numberPicker2.setMinValue(0);
        numberPicker2.setMaxValue(23);
        LinearLayout linearLayout2 = linearLayout;
        linearLayout2.addView(numberPicker2, LayoutHelper.createLinear(0, 270, 0.2f));
        numberPicker2.setFormatter($$Lambda$AlertsCreator$IHw25NY9tImD3WWBi50jge4lm0.INSTANCE);
        numberPicker2.setOnValueChangedListener(r12);
        numberPicker3.setMinValue(0);
        numberPicker3.setMaxValue(59);
        numberPicker3.setValue(0);
        numberPicker3.setFormatter($$Lambda$AlertsCreator$IsQJhbRDJN1tME_qoMm8CkKDPc.INSTANCE);
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
        r10.addView(textView2, LayoutHelper.createLinear(-1, 48, 83, 16, 15, 16, 16));
        NumberPicker numberPicker4 = numberPicker;
        $$Lambda$AlertsCreator$JUzkD_3qbbvQxG3huem1Vu9yLOU r13 = r0;
        $$Lambda$AlertsCreator$JUzkD_3qbbvQxG3huem1Vu9yLOU r02 = new View.OnClickListener(zArr, i3, j, numberPicker4, numberPicker2, numberPicker3, calendar, scheduleDatePickerDelegate, builder2) {
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
                AlertsCreator.lambda$createScheduleDatePickerDialog$30(this.f$0, this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, this.f$8, view);
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
                AlertsCreator.lambda$createScheduleDatePickerDialog$31(this.f$0, this.f$1, dialogInterface);
            }
        });
        return builder3;
    }

    static /* synthetic */ void lambda$createScheduleDatePickerDialog$25(ScheduleDatePickerDelegate scheduleDatePickerDelegate, BottomSheet.Builder builder, int i) {
        if (i == 1) {
            scheduleDatePickerDelegate.didSelectDate(true, NUM);
            builder.getDismissRunnable().run();
        }
    }

    static /* synthetic */ String lambda$createScheduleDatePickerDialog$26(long j, Calendar calendar, int i, int i2) {
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

    static /* synthetic */ void lambda$createScheduleDatePickerDialog$27(TextView textView, int i, long j, NumberPicker numberPicker, NumberPicker numberPicker2, NumberPicker numberPicker3, NumberPicker numberPicker4, int i2, int i3) {
        checkScheduleDate(textView, ((long) i) == j, numberPicker, numberPicker2, numberPicker3);
    }

    static /* synthetic */ void lambda$createScheduleDatePickerDialog$30(boolean[] zArr, int i, long j, NumberPicker numberPicker, NumberPicker numberPicker2, NumberPicker numberPicker3, Calendar calendar, ScheduleDatePickerDelegate scheduleDatePickerDelegate, BottomSheet.Builder builder, View view) {
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

    static /* synthetic */ void lambda$createScheduleDatePickerDialog$31(Runnable runnable, boolean[] zArr, DialogInterface dialogInterface) {
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
                AlertsCreator.lambda$createMuteAlert$32(this.f$0, dialogInterface, i);
            }
        });
        return builder.create();
    }

    static /* synthetic */ void lambda$createMuteAlert$32(long j, DialogInterface dialogInterface, int i) {
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
                    AlertsCreator.lambda$createReportAlert$34(this.f$0, this.f$1, this.f$2, this.f$3, dialogInterface, i);
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
        r0.reason = new org.telegram.tgnet.TLRPC$TL_inputReportReasonPornography();
        r0 = r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:22:0x0089, code lost:
        if (r7 != 3) goto L_0x0092;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:23:0x008b, code lost:
        r0.reason = new org.telegram.tgnet.TLRPC$TL_inputReportReasonPornography();
        r0 = r0;
     */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    static /* synthetic */ void lambda$createReportAlert$34(long r1, int r3, org.telegram.ui.ActionBar.BaseFragment r4, android.content.Context r5, android.content.DialogInterface r6, int r7) {
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
            org.telegram.ui.Components.-$$Lambda$AlertsCreator$N5JDtYY4uQ7tfALm1GKY2LKdDkM r2 = org.telegram.ui.Components.$$Lambda$AlertsCreator$N5JDtYY4uQ7tfALm1GKY2LKdDkM.INSTANCE
            r1.sendRequest(r0, r2)
            r1 = 2131626520(0x7f0e0a18, float:1.8880279E38)
            java.lang.String r2 = "ReportChatSent"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r2 = 0
            android.widget.Toast r1 = android.widget.Toast.makeText(r5, r1, r2)
            r1.show()
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.AlertsCreator.lambda$createReportAlert$34(long, int, org.telegram.ui.ActionBar.BaseFragment, android.content.Context, android.content.DialogInterface, int):void");
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
                    if (!(tLObject instanceof TLRPC$TL_channels_inviteToChannel)) {
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
                    if (!(tLObject instanceof TLRPC$TL_channels_createChannel)) {
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
                    AlertsCreator.lambda$createColorSelectDialog$36(this.f$0, this.f$1, view);
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
                AlertsCreator.lambda$createColorSelectDialog$37(this.f$0, this.f$1, this.f$2, this.f$3, dialogInterface, i);
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
                AlertsCreator.lambda$createColorSelectDialog$38(this.f$0, this.f$1, this.f$2, dialogInterface, i);
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
                    AlertsCreator.lambda$createColorSelectDialog$39(this.f$0, this.f$1, dialogInterface, i);
                }
            });
        }
        return builder.create();
    }

    static /* synthetic */ void lambda$createColorSelectDialog$36(LinearLayout linearLayout, int[] iArr, View view) {
        int childCount = linearLayout.getChildCount();
        for (int i = 0; i < childCount; i++) {
            RadioColorCell radioColorCell = (RadioColorCell) linearLayout.getChildAt(i);
            radioColorCell.setChecked(radioColorCell == view, true);
        }
        iArr[0] = TextColorCell.colorsToSave[((Integer) view.getTag()).intValue()];
    }

    static /* synthetic */ void lambda$createColorSelectDialog$37(long j, int[] iArr, int i, Runnable runnable, DialogInterface dialogInterface, int i2) {
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

    static /* synthetic */ void lambda$createColorSelectDialog$38(long j, int i, Runnable runnable, DialogInterface dialogInterface, int i2) {
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

    static /* synthetic */ void lambda$createColorSelectDialog$39(long j, Runnable runnable, DialogInterface dialogInterface, int i) {
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
            $$Lambda$AlertsCreator$rJpiqYwXJBIWNiNjIaVzQovNq4 r11 = r1;
            $$Lambda$AlertsCreator$rJpiqYwXJBIWNiNjIaVzQovNq4 r1 = new View.OnClickListener(iArr, j, str, builder, runnable) {
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
                    AlertsCreator.lambda$createVibrationSelectDialog$40(this.f$0, this.f$1, this.f$2, this.f$3, this.f$4, view);
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

    static /* synthetic */ void lambda$createVibrationSelectDialog$40(int[] iArr, long j, String str, AlertDialog.Builder builder, Runnable runnable, View view) {
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

    public static Dialog createLocationUpdateDialog(Activity activity, TLRPC$User tLRPC$User, MessagesStorage.IntCallback intCallback) {
        Activity activity2 = activity;
        int[] iArr = new int[1];
        String[] strArr = {LocaleController.getString("SendLiveLocationFor15m", NUM), LocaleController.getString("SendLiveLocationFor1h", NUM), LocaleController.getString("SendLiveLocationFor8h", NUM)};
        LinearLayout linearLayout = new LinearLayout(activity2);
        linearLayout.setOrientation(1);
        TextView textView = new TextView(activity2);
        if (tLRPC$User != null) {
            textView.setText(LocaleController.formatString("LiveLocationAlertPrivate", NUM, UserObject.getFirstName(tLRPC$User)));
        } else {
            textView.setText(LocaleController.getString("LiveLocationAlertGroup", NUM));
        }
        textView.setTextColor(Theme.getColor("dialogTextBlack"));
        textView.setTextSize(1, 16.0f);
        int i = 5;
        textView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
        if (!LocaleController.isRTL) {
            i = 3;
        }
        linearLayout.addView(textView, LayoutHelper.createLinear(-2, -2, i | 48, 24, 0, 24, 8));
        int i2 = 0;
        while (i2 < 3) {
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
                    AlertsCreator.lambda$createLocationUpdateDialog$41(this.f$0, this.f$1, view);
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
                AlertsCreator.lambda$createLocationUpdateDialog$42(this.f$0, this.f$1, dialogInterface, i);
            }
        });
        builder.setNeutralButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
        return builder.create();
    }

    static /* synthetic */ void lambda$createLocationUpdateDialog$41(int[] iArr, LinearLayout linearLayout, View view) {
        iArr[0] = ((Integer) view.getTag()).intValue();
        int childCount = linearLayout.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childAt = linearLayout.getChildAt(i);
            if (childAt instanceof RadioColorCell) {
                ((RadioColorCell) childAt).setChecked(childAt == view, true);
            }
        }
    }

    static /* synthetic */ void lambda$createLocationUpdateDialog$42(int[] iArr, MessagesStorage.IntCallback intCallback, DialogInterface dialogInterface, int i) {
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
        while (i3 < 4) {
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
                    AlertsCreator.lambda$createFreeSpaceDialog$45(this.f$0, this.f$1, view);
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

    static /* synthetic */ void lambda$createFreeSpaceDialog$45(int[] iArr, LinearLayout linearLayout, View view) {
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
            $$Lambda$AlertsCreator$qrOV4q2FdPiAtDtQEO9tmoptw8w r13 = r1;
            AlertDialog.Builder builder2 = builder;
            $$Lambda$AlertsCreator$qrOV4q2FdPiAtDtQEO9tmoptw8w r1 = new View.OnClickListener(iArr, j, i, notificationsSettings, builder, runnable) {
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
                    AlertsCreator.lambda$createPrioritySelectDialog$48(this.f$0, this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, view);
                }
            };
            radioColorCell.setOnClickListener(r13);
            i5++;
            activity2 = activity;
            linearLayout = linearLayout;
            i4 = 0;
            long j3 = j;
        }
        AlertDialog.Builder builder3 = builder;
        builder3.setTitle(LocaleController.getString("NotificationsImportance", NUM));
        builder3.setView(linearLayout);
        builder3.setPositiveButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
        return builder3.create();
    }

    static /* synthetic */ void lambda$createPrioritySelectDialog$48(int[] iArr, long j, int i, SharedPreferences sharedPreferences, AlertDialog.Builder builder, Runnable runnable, View view) {
        int i2 = 0;
        iArr[0] = ((Integer) view.getTag()).intValue();
        SharedPreferences.Editor edit = MessagesController.getNotificationsSettings(UserConfig.selectedAccount).edit();
        int i3 = 5;
        if (j != 0) {
            if (iArr[0] == 0) {
                i2 = 3;
            } else if (iArr[0] == 1) {
                i2 = 4;
            } else if (iArr[0] == 2) {
                i2 = 5;
            } else if (iArr[0] != 3) {
                i2 = 1;
            }
            edit.putInt("priority_" + j, i2);
        } else {
            if (iArr[0] == 0) {
                i3 = 4;
            } else if (iArr[0] != 1) {
                i3 = iArr[0] == 2 ? 0 : 1;
            }
            if (i == 1) {
                edit.putInt("priority_messages", i3);
                iArr[0] = sharedPreferences.getInt("priority_messages", 1);
            } else if (i == 0) {
                edit.putInt("priority_group", i3);
                iArr[0] = sharedPreferences.getInt("priority_group", 1);
            } else if (i == 2) {
                edit.putInt("priority_channel", i3);
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
        while (i2 < 4) {
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
                    AlertsCreator.lambda$createPopupSelectDialog$49(this.f$0, this.f$1, this.f$2, this.f$3, view);
                }
            });
            i2++;
        }
        builder.setTitle(LocaleController.getString("PopupNotification", NUM));
        builder.setView(linearLayout);
        builder.setPositiveButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
        return builder.create();
    }

    static /* synthetic */ void lambda$createPopupSelectDialog$49(int[] iArr, int i, AlertDialog.Builder builder, Runnable runnable, View view) {
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
                    AlertsCreator.lambda$createSingleChoiceDialog$50(AlertDialog.Builder.this, this.f$1, view);
                }
            });
            i2++;
        }
        builder.setTitle(str);
        builder.setView(linearLayout);
        builder.setPositiveButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
        return builder.create();
    }

    static /* synthetic */ void lambda$createSingleChoiceDialog$50(AlertDialog.Builder builder, DialogInterface.OnClickListener onClickListener, View view) {
        int intValue = ((Integer) view.getTag()).intValue();
        builder.getDismissRunnable().run();
        onClickListener.onClick((DialogInterface) null, intValue);
    }

    public static AlertDialog.Builder createTTLAlert(Context context, TLRPC$EncryptedChat tLRPC$EncryptedChat) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(LocaleController.getString("MessageLifetime", NUM));
        NumberPicker numberPicker = new NumberPicker(context);
        numberPicker.setMinValue(0);
        numberPicker.setMaxValue(20);
        int i = tLRPC$EncryptedChat.ttl;
        if (i <= 0 || i >= 16) {
            int i2 = tLRPC$EncryptedChat.ttl;
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
        numberPicker.setFormatter($$Lambda$AlertsCreator$Qwcgv6B8yUdm86rR9HhZbguF7g.INSTANCE);
        builder.setView(numberPicker);
        builder.setNegativeButton(LocaleController.getString("Done", NUM), new DialogInterface.OnClickListener(numberPicker) {
            private final /* synthetic */ NumberPicker f$1;

            {
                this.f$1 = r2;
            }

            public final void onClick(DialogInterface dialogInterface, int i) {
                AlertsCreator.lambda$createTTLAlert$52(TLRPC$EncryptedChat.this, this.f$1, dialogInterface, i);
            }
        });
        return builder;
    }

    static /* synthetic */ String lambda$createTTLAlert$51(int i) {
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

    static /* synthetic */ void lambda$createTTLAlert$52(TLRPC$EncryptedChat tLRPC$EncryptedChat, NumberPicker numberPicker, DialogInterface dialogInterface, int i) {
        int i2 = tLRPC$EncryptedChat.ttl;
        int value = numberPicker.getValue();
        if (value >= 0 && value < 16) {
            tLRPC$EncryptedChat.ttl = value;
        } else if (value == 16) {
            tLRPC$EncryptedChat.ttl = 30;
        } else if (value == 17) {
            tLRPC$EncryptedChat.ttl = 60;
        } else if (value == 18) {
            tLRPC$EncryptedChat.ttl = 3600;
        } else if (value == 19) {
            tLRPC$EncryptedChat.ttl = 86400;
        } else if (value == 20) {
            tLRPC$EncryptedChat.ttl = 604800;
        }
        if (i2 != tLRPC$EncryptedChat.ttl) {
            SecretChatHelper.getInstance(UserConfig.selectedAccount).sendTTLMessage(tLRPC$EncryptedChat, (TLRPC$Message) null);
            MessagesStorage.getInstance(UserConfig.selectedAccount).updateEncryptedChatTTL(tLRPC$EncryptedChat);
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
                        AlertsCreator.lambda$createAccountSelectDialog$53(this.f$0, this.f$1, this.f$2, view);
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

    static /* synthetic */ void lambda$createAccountSelectDialog$53(AlertDialog[] alertDialogArr, Runnable runnable, AccountSelectDelegate accountSelectDelegate, View view) {
        if (alertDialogArr[0] != null) {
            alertDialogArr[0].setOnDismissListener((DialogInterface.OnDismissListener) null);
        }
        runnable.run();
        accountSelectDelegate.didSelectAccount(((AccountSelectCell) view).getAccountNumber());
    }

    /* JADX WARNING: Removed duplicated region for block: B:140:0x027e  */
    /* JADX WARNING: Removed duplicated region for block: B:141:0x0283  */
    /* JADX WARNING: Removed duplicated region for block: B:144:0x028b  */
    /* JADX WARNING: Removed duplicated region for block: B:145:0x0290  */
    /* JADX WARNING: Removed duplicated region for block: B:250:0x04e6  */
    /* JADX WARNING: Removed duplicated region for block: B:251:0x04f3  */
    /* JADX WARNING: Removed duplicated region for block: B:278:0x05b0  */
    /* JADX WARNING: Removed duplicated region for block: B:279:0x05b8  */
    /* JADX WARNING: Removed duplicated region for block: B:282:0x05de  */
    /* JADX WARNING: Removed duplicated region for block: B:309:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static void createDeleteMessagesAlert(org.telegram.ui.ActionBar.BaseFragment r40, org.telegram.tgnet.TLRPC$User r41, org.telegram.tgnet.TLRPC$Chat r42, org.telegram.tgnet.TLRPC$EncryptedChat r43, org.telegram.tgnet.TLRPC$ChatFull r44, long r45, org.telegram.messenger.MessageObject r47, android.util.SparseArray<org.telegram.messenger.MessageObject>[] r48, org.telegram.messenger.MessageObject.GroupedMessages r49, boolean r50, int r51, java.lang.Runnable r52) {
        /*
            r14 = r40
            r3 = r41
            r4 = r42
            r5 = r43
            r9 = r47
            r11 = r49
            r0 = r51
            if (r14 == 0) goto L_0x05e7
            if (r3 != 0) goto L_0x0018
            if (r4 != 0) goto L_0x0018
            if (r5 != 0) goto L_0x0018
            goto L_0x05e7
        L_0x0018:
            android.app.Activity r1 = r40.getParentActivity()
            if (r1 != 0) goto L_0x001f
            return
        L_0x001f:
            int r15 = r40.getCurrentAccount()
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
            r8 = r48[r7]
            int r8 = r8.size()
            r10 = r48[r6]
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
            java.lang.String r11 = "DeleteMessagesOption"
            r30 = r8
            r19 = 1098907648(0x41800000, float:16.0)
            r22 = 1090519040(0x41000000, float:8.0)
            java.lang.String r8 = ""
            r31 = r6
            if (r4 == 0) goto L_0x0342
            boolean r6 = r4.megagroup
            if (r6 == 0) goto L_0x0342
            if (r50 != 0) goto L_0x0342
            boolean r6 = org.telegram.messenger.ChatObject.canBlockUsers(r42)
            org.telegram.tgnet.ConnectionsManager r7 = org.telegram.tgnet.ConnectionsManager.getInstance(r15)
            int r7 = r7.getCurrentTime()
            if (r9 == 0) goto L_0x0108
            org.telegram.tgnet.TLRPC$Message r3 = r9.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r3 = r3.action
            if (r3 == 0) goto L_0x00c3
            boolean r5 = r3 instanceof org.telegram.tgnet.TLRPC$TL_messageActionEmpty
            if (r5 != 0) goto L_0x00c3
            boolean r5 = r3 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatDeleteUser
            if (r5 != 0) goto L_0x00c3
            boolean r5 = r3 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatJoinedByLink
            if (r5 != 0) goto L_0x00c3
            boolean r3 = r3 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatAddUser
            if (r3 == 0) goto L_0x00c1
            goto L_0x00c3
        L_0x00c1:
            r3 = 0
            goto L_0x00d3
        L_0x00c3:
            org.telegram.messenger.MessagesController r3 = org.telegram.messenger.MessagesController.getInstance(r15)
            org.telegram.tgnet.TLRPC$Message r5 = r9.messageOwner
            int r5 = r5.from_id
            java.lang.Integer r5 = java.lang.Integer.valueOf(r5)
            org.telegram.tgnet.TLRPC$User r3 = r3.getUser(r5)
        L_0x00d3:
            boolean r5 = r47.isSendError()
            if (r5 != 0) goto L_0x00fa
            long r23 = r47.getDialogId()
            int r5 = (r23 > r45 ? 1 : (r23 == r45 ? 0 : -1))
            if (r5 != 0) goto L_0x00fa
            org.telegram.tgnet.TLRPC$Message r5 = r9.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r5 = r5.action
            if (r5 == 0) goto L_0x00eb
            boolean r5 = r5 instanceof org.telegram.tgnet.TLRPC$TL_messageActionEmpty
            if (r5 == 0) goto L_0x00fa
        L_0x00eb:
            boolean r5 = r47.isOut()
            if (r5 == 0) goto L_0x00fa
            org.telegram.tgnet.TLRPC$Message r5 = r9.messageOwner
            int r5 = r5.date
            int r7 = r7 - r5
            if (r7 > r10) goto L_0x00fa
            r5 = 1
            goto L_0x00fb
        L_0x00fa:
            r5 = 0
        L_0x00fb:
            if (r5 == 0) goto L_0x00ff
            r5 = 1
            goto L_0x0100
        L_0x00ff:
            r5 = 0
        L_0x0100:
            r32 = r2
            r23 = r11
            r24 = r13
            goto L_0x0198
        L_0x0108:
            r3 = 1
            r5 = -1
        L_0x010a:
            if (r3 < 0) goto L_0x0146
            r9 = 0
        L_0x010d:
            r23 = r48[r3]
            r24 = r13
            int r13 = r23.size()
            r23 = r11
            if (r9 >= r13) goto L_0x0139
            r13 = r48[r3]
            java.lang.Object r13 = r13.valueAt(r9)
            org.telegram.messenger.MessageObject r13 = (org.telegram.messenger.MessageObject) r13
            r11 = -1
            if (r5 != r11) goto L_0x0128
            org.telegram.tgnet.TLRPC$Message r5 = r13.messageOwner
            int r5 = r5.from_id
        L_0x0128:
            if (r5 < 0) goto L_0x0138
            org.telegram.tgnet.TLRPC$Message r11 = r13.messageOwner
            int r11 = r11.from_id
            if (r5 == r11) goto L_0x0131
            goto L_0x0138
        L_0x0131:
            int r9 = r9 + 1
            r11 = r23
            r13 = r24
            goto L_0x010d
        L_0x0138:
            r5 = -2
        L_0x0139:
            r9 = -2
            if (r5 != r9) goto L_0x013d
            goto L_0x014a
        L_0x013d:
            int r3 = r3 + -1
            r9 = r47
            r11 = r23
            r13 = r24
            goto L_0x010a
        L_0x0146:
            r23 = r11
            r24 = r13
        L_0x014a:
            r3 = 0
            r9 = 1
        L_0x014c:
            if (r9 < 0) goto L_0x0182
            r11 = 0
        L_0x014f:
            r13 = r48[r9]
            int r13 = r13.size()
            if (r11 >= r13) goto L_0x017d
            r13 = r48[r9]
            java.lang.Object r13 = r13.valueAt(r11)
            org.telegram.messenger.MessageObject r13 = (org.telegram.messenger.MessageObject) r13
            r32 = r2
            r2 = 1
            if (r9 != r2) goto L_0x0178
            boolean r2 = r13.isOut()
            if (r2 == 0) goto L_0x0178
            org.telegram.tgnet.TLRPC$Message r2 = r13.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r13 = r2.action
            if (r13 != 0) goto L_0x0178
            int r2 = r2.date
            int r2 = r7 - r2
            if (r2 > r10) goto L_0x0178
            int r3 = r3 + 1
        L_0x0178:
            int r11 = r11 + 1
            r2 = r32
            goto L_0x014f
        L_0x017d:
            r32 = r2
            int r9 = r9 + -1
            goto L_0x014c
        L_0x0182:
            r32 = r2
            r2 = -1
            if (r5 == r2) goto L_0x0196
            org.telegram.messenger.MessagesController r2 = org.telegram.messenger.MessagesController.getInstance(r15)
            java.lang.Integer r5 = java.lang.Integer.valueOf(r5)
            org.telegram.tgnet.TLRPC$User r2 = r2.getUser(r5)
            r5 = r3
            r3 = r2
            goto L_0x0198
        L_0x0196:
            r5 = r3
            r3 = 0
        L_0x0198:
            if (r3 == 0) goto L_0x02cc
            int r2 = r3.id
            org.telegram.messenger.UserConfig r7 = org.telegram.messenger.UserConfig.getInstance(r15)
            int r7 = r7.getClientUserId()
            if (r2 == r7) goto L_0x02cc
            r2 = 1
            if (r0 != r2) goto L_0x020c
            boolean r7 = r4.creator
            if (r7 != 0) goto L_0x020c
            org.telegram.ui.ActionBar.AlertDialog[] r13 = new org.telegram.ui.ActionBar.AlertDialog[r2]
            org.telegram.ui.ActionBar.AlertDialog r0 = new org.telegram.ui.ActionBar.AlertDialog
            r2 = 3
            r0.<init>(r1, r2)
            r1 = 0
            r13[r1] = r0
            org.telegram.tgnet.TLRPC$TL_channels_getParticipant r12 = new org.telegram.tgnet.TLRPC$TL_channels_getParticipant
            r12.<init>()
            org.telegram.tgnet.TLRPC$InputChannel r0 = org.telegram.messenger.MessagesController.getInputChannel((org.telegram.tgnet.TLRPC$Chat) r42)
            r12.channel = r0
            org.telegram.messenger.MessagesController r0 = org.telegram.messenger.MessagesController.getInstance(r15)
            org.telegram.tgnet.TLRPC$InputUser r0 = r0.getInputUser((org.telegram.tgnet.TLRPC$User) r3)
            r12.user_id = r0
            org.telegram.tgnet.ConnectionsManager r11 = org.telegram.tgnet.ConnectionsManager.getInstance(r15)
            org.telegram.ui.Components.-$$Lambda$AlertsCreator$Gm0AIdCruJqFnUwRblI9gyDWcPw r10 = new org.telegram.ui.Components.-$$Lambda$AlertsCreator$Gm0AIdCruJqFnUwRblI9gyDWcPw
            r0 = r10
            r1 = r13
            r2 = r40
            r3 = r41
            r4 = r42
            r5 = r43
            r6 = r44
            r7 = r45
            r9 = r47
            r14 = r10
            r10 = r48
            r25 = r15
            r15 = r11
            r11 = r49
            r16 = r14
            r14 = r12
            r12 = r50
            r33 = r13
            r13 = r52
            r0.<init>(r1, r2, r3, r4, r5, r6, r7, r9, r10, r11, r12, r13)
            r0 = r16
            int r0 = r15.sendRequest(r14, r0)
            org.telegram.ui.Components.-$$Lambda$AlertsCreator$HftMHd4JQQbstT6KrAsrV-sZDng r1 = new org.telegram.ui.Components.-$$Lambda$AlertsCreator$HftMHd4JQQbstT6KrAsrV-sZDng
            r9 = r25
            r3 = r33
            r1.<init>(r3, r9, r0, r2)
            r2 = 1000(0x3e8, double:4.94E-321)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r1, r2)
            return
        L_0x020c:
            r2 = r14
            r9 = r15
            android.widget.FrameLayout r7 = new android.widget.FrameLayout
            r7.<init>(r1)
            r10 = 0
            r11 = 0
        L_0x0215:
            r13 = 3
            if (r10 >= r13) goto L_0x02c2
            r14 = 2
            if (r0 == r14) goto L_0x021d
            if (r6 != 0) goto L_0x0223
        L_0x021d:
            if (r10 != 0) goto L_0x0223
            r25 = r6
            goto L_0x02ba
        L_0x0223:
            org.telegram.ui.Cells.CheckBoxCell r14 = new org.telegram.ui.Cells.CheckBoxCell
            r15 = 1
            r14.<init>(r1, r15)
            r45 = 0
            android.graphics.drawable.Drawable r13 = org.telegram.ui.ActionBar.Theme.getSelectorDrawable(r45)
            r14.setBackgroundDrawable(r13)
            java.lang.Integer r13 = java.lang.Integer.valueOf(r10)
            r14.setTag(r13)
            if (r10 != 0) goto L_0x024b
            r13 = 2131624886(0x7f0e03b6, float:1.8876964E38)
            java.lang.String r15 = "DeleteBanUser"
            java.lang.String r13 = org.telegram.messenger.LocaleController.getString(r15, r13)
            r15 = 0
            r14.setText(r13, r8, r15, r15)
        L_0x0248:
            r25 = r6
            goto L_0x027a
        L_0x024b:
            r13 = 1
            r15 = 0
            if (r10 != r13) goto L_0x025c
            r13 = 2131624909(0x7f0e03cd, float:1.8877011E38)
            java.lang.String r0 = "DeleteReportSpam"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r0, r13)
            r14.setText(r0, r8, r15, r15)
            goto L_0x0248
        L_0x025c:
            r0 = 2
            if (r10 != r0) goto L_0x0248
            r13 = 1
            java.lang.Object[] r0 = new java.lang.Object[r13]
            java.lang.String r13 = r3.first_name
            r25 = r6
            java.lang.String r6 = r3.last_name
            java.lang.String r6 = org.telegram.messenger.ContactsController.formatName(r13, r6)
            r0[r15] = r6
            java.lang.String r6 = "DeleteAllFrom"
            r13 = 2131624875(0x7f0e03ab, float:1.8876942E38)
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r6, r13, r0)
            r14.setText(r0, r8, r15, r15)
        L_0x027a:
            boolean r0 = org.telegram.messenger.LocaleController.isRTL
            if (r0 == 0) goto L_0x0283
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r19)
            goto L_0x0287
        L_0x0283:
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r22)
        L_0x0287:
            boolean r6 = org.telegram.messenger.LocaleController.isRTL
            if (r6 == 0) goto L_0x0290
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r22)
            goto L_0x0294
        L_0x0290:
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r19)
        L_0x0294:
            r13 = 0
            r14.setPadding(r0, r13, r6, r13)
            r33 = -1
            r34 = 1111490560(0x42400000, float:48.0)
            r35 = 51
            r36 = 0
            int r0 = r11 * 48
            float r0 = (float) r0
            r38 = 0
            r39 = 0
            r37 = r0
            android.widget.FrameLayout$LayoutParams r0 = org.telegram.ui.Components.LayoutHelper.createFrame(r33, r34, r35, r36, r37, r38, r39)
            r7.addView(r14, r0)
            org.telegram.ui.Components.-$$Lambda$AlertsCreator$4OHKaYeK4rq2SjIUdvqrdqZQdMA r0 = new org.telegram.ui.Components.-$$Lambda$AlertsCreator$4OHKaYeK4rq2SjIUdvqrdqZQdMA
            r0.<init>(r12)
            r14.setOnClickListener(r0)
            int r11 = r11 + 1
        L_0x02ba:
            int r10 = r10 + 1
            r0 = r51
            r6 = r25
            goto L_0x0215
        L_0x02c2:
            r0 = r32
            r0.setView(r7)
            r13 = r24
            r1 = 0
            goto L_0x033c
        L_0x02cc:
            r2 = r14
            r9 = r15
            r0 = r32
            if (r5 <= 0) goto L_0x0338
            android.widget.FrameLayout r6 = new android.widget.FrameLayout
            r6.<init>(r1)
            org.telegram.ui.Cells.CheckBoxCell r7 = new org.telegram.ui.Cells.CheckBoxCell
            r10 = 1
            r7.<init>(r1, r10)
            r1 = 0
            android.graphics.drawable.Drawable r10 = org.telegram.ui.ActionBar.Theme.getSelectorDrawable(r1)
            r7.setBackgroundDrawable(r10)
            r11 = r23
            r10 = 2131624901(0x7f0e03c5, float:1.8876995E38)
            java.lang.String r10 = org.telegram.messenger.LocaleController.getString(r11, r10)
            r7.setText(r10, r8, r1, r1)
            boolean r1 = org.telegram.messenger.LocaleController.isRTL
            if (r1 == 0) goto L_0x02fa
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r19)
            goto L_0x02fe
        L_0x02fa:
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r22)
        L_0x02fe:
            boolean r8 = org.telegram.messenger.LocaleController.isRTL
            if (r8 == 0) goto L_0x0307
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r22)
            goto L_0x030b
        L_0x0307:
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r19)
        L_0x030b:
            r10 = 0
            r7.setPadding(r1, r10, r8, r10)
            r32 = -1
            r33 = 1111490560(0x42400000, float:48.0)
            r34 = 51
            r35 = 0
            r36 = 0
            r37 = 0
            r38 = 0
            android.widget.FrameLayout$LayoutParams r1 = org.telegram.ui.Components.LayoutHelper.createFrame(r32, r33, r34, r35, r36, r37, r38)
            r6.addView(r7, r1)
            org.telegram.ui.Components.-$$Lambda$AlertsCreator$ino5Fs0KfBjuRVse3fdh1jvK9ng r1 = new org.telegram.ui.Components.-$$Lambda$AlertsCreator$ino5Fs0KfBjuRVse3fdh1jvK9ng
            r13 = r24
            r1.<init>(r13)
            r7.setOnClickListener(r1)
            r0.setView(r6)
            r1 = 9
            r0.setCustomViewOffset(r1)
            r1 = 1
            goto L_0x033c
        L_0x0338:
            r13 = r24
            r1 = 0
            r3 = 0
        L_0x033c:
            r25 = r9
            r6 = r30
            goto L_0x04b4
        L_0x0342:
            r0 = r2
            r2 = r14
            r9 = r15
            if (r50 != 0) goto L_0x04ad
            boolean r3 = org.telegram.messenger.ChatObject.isChannel(r42)
            if (r3 != 0) goto L_0x04ad
            if (r43 != 0) goto L_0x04ad
            org.telegram.tgnet.ConnectionsManager r3 = org.telegram.tgnet.ConnectionsManager.getInstance(r9)
            int r3 = r3.getCurrentTime()
            r5 = r41
            if (r5 == 0) goto L_0x036b
            int r6 = r5.id
            org.telegram.messenger.UserConfig r14 = org.telegram.messenger.UserConfig.getInstance(r9)
            int r14 = r14.getClientUserId()
            if (r6 == r14) goto L_0x036b
            boolean r6 = r5.bot
            if (r6 == 0) goto L_0x036d
        L_0x036b:
            if (r4 == 0) goto L_0x0410
        L_0x036d:
            r6 = r47
            if (r6 == 0) goto L_0x03ac
            boolean r14 = r47.isSendError()
            if (r14 != 0) goto L_0x039c
            org.telegram.tgnet.TLRPC$Message r14 = r6.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r14 = r14.action
            if (r14 == 0) goto L_0x0385
            boolean r15 = r14 instanceof org.telegram.tgnet.TLRPC$TL_messageActionEmpty
            if (r15 != 0) goto L_0x0385
            boolean r14 = r14 instanceof org.telegram.tgnet.TLRPC$TL_messageActionPhoneCall
            if (r14 == 0) goto L_0x039c
        L_0x0385:
            boolean r14 = r47.isOut()
            if (r14 != 0) goto L_0x0393
            if (r7 != 0) goto L_0x0393
            boolean r7 = org.telegram.messenger.ChatObject.hasAdminRights(r42)
            if (r7 == 0) goto L_0x039c
        L_0x0393:
            org.telegram.tgnet.TLRPC$Message r7 = r6.messageOwner
            int r7 = r7.date
            int r3 = r3 - r7
            if (r3 > r10) goto L_0x039c
            r3 = 1
            goto L_0x039d
        L_0x039c:
            r3 = 0
        L_0x039d:
            if (r3 == 0) goto L_0x03a1
            r3 = 1
            goto L_0x03a2
        L_0x03a1:
            r3 = 0
        L_0x03a2:
            boolean r7 = r47.isOut()
            r10 = 1
            r7 = r7 ^ r10
            r25 = r9
            goto L_0x0414
        L_0x03ac:
            r14 = 0
            r15 = 0
            r16 = 1
        L_0x03b0:
            if (r16 < 0) goto L_0x040b
            r5 = 0
        L_0x03b3:
            r23 = r48[r16]
            int r6 = r23.size()
            if (r5 >= r6) goto L_0x0400
            r6 = r48[r16]
            java.lang.Object r6 = r6.valueAt(r5)
            org.telegram.messenger.MessageObject r6 = (org.telegram.messenger.MessageObject) r6
            r25 = r9
            org.telegram.tgnet.TLRPC$Message r9 = r6.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r9 = r9.action
            if (r9 == 0) goto L_0x03d4
            boolean r2 = r9 instanceof org.telegram.tgnet.TLRPC$TL_messageActionEmpty
            if (r2 != 0) goto L_0x03d4
            boolean r2 = r9 instanceof org.telegram.tgnet.TLRPC$TL_messageActionPhoneCall
            if (r2 != 0) goto L_0x03d4
            goto L_0x03f7
        L_0x03d4:
            boolean r2 = r6.isOut()
            if (r2 != 0) goto L_0x03e4
            if (r7 != 0) goto L_0x03e4
            if (r4 == 0) goto L_0x03f7
            boolean r2 = org.telegram.messenger.ChatObject.canBlockUsers(r42)
            if (r2 == 0) goto L_0x03f7
        L_0x03e4:
            org.telegram.tgnet.TLRPC$Message r2 = r6.messageOwner
            int r2 = r2.date
            int r2 = r3 - r2
            if (r2 > r10) goto L_0x03f7
            int r15 = r15 + 1
            if (r14 != 0) goto L_0x03f7
            boolean r2 = r6.isOut()
            if (r2 != 0) goto L_0x03f7
            r14 = 1
        L_0x03f7:
            int r5 = r5 + 1
            r2 = r40
            r6 = r47
            r9 = r25
            goto L_0x03b3
        L_0x0400:
            r25 = r9
            int r16 = r16 + -1
            r2 = r40
            r5 = r41
            r6 = r47
            goto L_0x03b0
        L_0x040b:
            r25 = r9
            r7 = r14
            r3 = r15
            goto L_0x0414
        L_0x0410:
            r25 = r9
            r3 = 0
            r7 = 0
        L_0x0414:
            if (r3 <= 0) goto L_0x04a7
            android.widget.FrameLayout r2 = new android.widget.FrameLayout
            r2.<init>(r1)
            org.telegram.ui.Cells.CheckBoxCell r5 = new org.telegram.ui.Cells.CheckBoxCell
            r6 = 1
            r5.<init>(r1, r6)
            r1 = 0
            android.graphics.drawable.Drawable r9 = org.telegram.ui.ActionBar.Theme.getSelectorDrawable(r1)
            r5.setBackgroundDrawable(r9)
            if (r31 == 0) goto L_0x0442
            r9 = 2131624902(0x7f0e03c6, float:1.8876997E38)
            java.lang.Object[] r10 = new java.lang.Object[r6]
            java.lang.String r6 = org.telegram.messenger.UserObject.getFirstName(r41)
            r10[r1] = r6
            java.lang.String r6 = "DeleteMessagesOptionAlso"
            java.lang.String r6 = org.telegram.messenger.LocaleController.formatString(r6, r9, r10)
            r5.setText(r6, r8, r1, r1)
            r6 = r30
            goto L_0x0461
        L_0x0442:
            r6 = r30
            if (r4 == 0) goto L_0x0457
            if (r7 != 0) goto L_0x044a
            if (r3 != r6) goto L_0x0457
        L_0x044a:
            r9 = 2131624892(0x7f0e03bc, float:1.8876977E38)
            java.lang.String r10 = "DeleteForAll"
            java.lang.String r9 = org.telegram.messenger.LocaleController.getString(r10, r9)
            r5.setText(r9, r8, r1, r1)
            goto L_0x0461
        L_0x0457:
            r9 = 2131624901(0x7f0e03c5, float:1.8876995E38)
            java.lang.String r9 = org.telegram.messenger.LocaleController.getString(r11, r9)
            r5.setText(r9, r8, r1, r1)
        L_0x0461:
            boolean r1 = org.telegram.messenger.LocaleController.isRTL
            if (r1 == 0) goto L_0x046a
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r19)
            goto L_0x046e
        L_0x046a:
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r22)
        L_0x046e:
            boolean r8 = org.telegram.messenger.LocaleController.isRTL
            if (r8 == 0) goto L_0x0477
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r22)
            goto L_0x047b
        L_0x0477:
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r19)
        L_0x047b:
            r9 = 0
            r5.setPadding(r1, r9, r8, r9)
            r32 = -1
            r33 = 1111490560(0x42400000, float:48.0)
            r34 = 51
            r35 = 0
            r36 = 0
            r37 = 0
            r38 = 0
            android.widget.FrameLayout$LayoutParams r1 = org.telegram.ui.Components.LayoutHelper.createFrame(r32, r33, r34, r35, r36, r37, r38)
            r2.addView(r5, r1)
            org.telegram.ui.Components.-$$Lambda$AlertsCreator$6NSQV9o1IGC7Hd2VI854XEeT3co r1 = new org.telegram.ui.Components.-$$Lambda$AlertsCreator$6NSQV9o1IGC7Hd2VI854XEeT3co
            r1.<init>(r13)
            r5.setOnClickListener(r1)
            r0.setView(r2)
            r1 = 9
            r0.setCustomViewOffset(r1)
            r5 = r3
            r1 = 1
            goto L_0x04ab
        L_0x04a7:
            r6 = r30
            r5 = r3
            r1 = 0
        L_0x04ab:
            r3 = 0
            goto L_0x04b5
        L_0x04ad:
            r25 = r9
            r6 = r30
            r1 = 0
            r3 = 0
            r5 = 0
        L_0x04b4:
            r7 = 0
        L_0x04b5:
            r2 = 2131624870(0x7f0e03a6, float:1.8876932E38)
            java.lang.String r8 = "Delete"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r8, r2)
            org.telegram.ui.Components.-$$Lambda$AlertsCreator$7IlVj5zUKirEYGvLRpKR_lJZhUY r8 = new org.telegram.ui.Components.-$$Lambda$AlertsCreator$7IlVj5zUKirEYGvLRpKR_lJZhUY
            r9 = r25
            r15 = r8
            r16 = r47
            r17 = r49
            r18 = r43
            r19 = r9
            r22 = r13
            r23 = r50
            r24 = r48
            r25 = r3
            r26 = r12
            r27 = r42
            r28 = r44
            r29 = r52
            r15.<init>(r17, r18, r19, r20, r22, r23, r24, r25, r26, r27, r28, r29)
            r0.setPositiveButton(r2, r8)
            java.lang.String r2 = "messages"
            r3 = 1
            if (r6 != r3) goto L_0x04f3
            r8 = 2131624910(0x7f0e03ce, float:1.8877013E38)
            java.lang.String r9 = "DeleteSingleMessagesTitle"
            java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r9, r8)
            r0.setTitle(r8)
            goto L_0x0508
        L_0x04f3:
            r8 = 2131624906(0x7f0e03ca, float:1.8877005E38)
            java.lang.Object[] r9 = new java.lang.Object[r3]
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatPluralString(r2, r6)
            r10 = 0
            r9[r10] = r3
            java.lang.String r3 = "DeleteMessagesTitle"
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r8, r9)
            r0.setTitle(r3)
        L_0x0508:
            r3 = 2131624251(0x7f0e013b, float:1.8875676E38)
            java.lang.String r8 = "AreYouSureDeleteSingleMessage"
            r9 = 2131624245(0x7f0e0135, float:1.8875664E38)
            java.lang.String r10 = "AreYouSureDeleteFewMessages"
            if (r4 == 0) goto L_0x0547
            if (r7 == 0) goto L_0x0547
            if (r1 == 0) goto L_0x0532
            if (r5 == r6) goto L_0x0532
            r1 = 2131624905(0x7f0e03c9, float:1.8877003E38)
            r4 = 1
            java.lang.Object[] r3 = new java.lang.Object[r4]
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatPluralString(r2, r5)
            r4 = 0
            r3[r4] = r2
            java.lang.String r2 = "DeleteMessagesTextGroupPart"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r2, r1, r3)
            r0.setMessage(r1)
            goto L_0x05bf
        L_0x0532:
            r4 = 1
            if (r6 != r4) goto L_0x053e
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r8, r3)
            r0.setMessage(r1)
            goto L_0x05bf
        L_0x053e:
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r10, r9)
            r0.setMessage(r1)
            goto L_0x05bf
        L_0x0547:
            if (r1 == 0) goto L_0x0588
            if (r31 != 0) goto L_0x0588
            if (r5 == r6) goto L_0x0588
            if (r4 == 0) goto L_0x0566
            r1 = 2131624904(0x7f0e03c8, float:1.8877E38)
            r3 = 1
            java.lang.Object[] r3 = new java.lang.Object[r3]
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatPluralString(r2, r5)
            r4 = 0
            r3[r4] = r2
            java.lang.String r2 = "DeleteMessagesTextGroup"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r2, r1, r3)
            r0.setMessage(r1)
            goto L_0x05bf
        L_0x0566:
            r4 = 0
            r1 = 2131624903(0x7f0e03c7, float:1.8876999E38)
            r3 = 2
            java.lang.Object[] r3 = new java.lang.Object[r3]
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatPluralString(r2, r5)
            r3[r4] = r2
            java.lang.String r2 = org.telegram.messenger.UserObject.getFirstName(r41)
            r4 = 1
            r3[r4] = r2
            java.lang.String r2 = "DeleteMessagesText"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r2, r1, r3)
            android.text.SpannableStringBuilder r1 = org.telegram.messenger.AndroidUtilities.replaceTags(r1)
            r0.setMessage(r1)
            goto L_0x05bf
        L_0x0588:
            if (r4 == 0) goto L_0x05ad
            boolean r1 = r4.megagroup
            if (r1 == 0) goto L_0x05ad
            if (r50 != 0) goto L_0x05ad
            r1 = 1
            if (r6 != r1) goto L_0x05a0
            r1 = 2131624252(0x7f0e013c, float:1.8875678E38)
            java.lang.String r2 = "AreYouSureDeleteSingleMessageMega"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r0.setMessage(r1)
            goto L_0x05bf
        L_0x05a0:
            r1 = 2131624246(0x7f0e0136, float:1.8875666E38)
            java.lang.String r2 = "AreYouSureDeleteFewMessagesMega"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r0.setMessage(r1)
            goto L_0x05bf
        L_0x05ad:
            r1 = 1
            if (r6 != r1) goto L_0x05b8
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r8, r3)
            r0.setMessage(r1)
            goto L_0x05bf
        L_0x05b8:
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r10, r9)
            r0.setMessage(r1)
        L_0x05bf:
            r1 = 2131624484(0x7f0e0224, float:1.887615E38)
            java.lang.String r2 = "Cancel"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r2 = 0
            r0.setNegativeButton(r1, r2)
            org.telegram.ui.ActionBar.AlertDialog r0 = r0.create()
            r1 = r40
            r1.showDialog(r0)
            r1 = -1
            android.view.View r0 = r0.getButton(r1)
            android.widget.TextView r0 = (android.widget.TextView) r0
            if (r0 == 0) goto L_0x05e7
            java.lang.String r1 = "dialogTextRed2"
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r1)
            r0.setTextColor(r1)
        L_0x05e7:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.AlertsCreator.createDeleteMessagesAlert(org.telegram.ui.ActionBar.BaseFragment, org.telegram.tgnet.TLRPC$User, org.telegram.tgnet.TLRPC$Chat, org.telegram.tgnet.TLRPC$EncryptedChat, org.telegram.tgnet.TLRPC$ChatFull, long, org.telegram.messenger.MessageObject, android.util.SparseArray[], org.telegram.messenger.MessageObject$GroupedMessages, boolean, int, java.lang.Runnable):void");
    }

    static /* synthetic */ void lambda$null$54(AlertDialog[] alertDialogArr, TLObject tLObject, BaseFragment baseFragment, TLRPC$User tLRPC$User, TLRPC$Chat tLRPC$Chat, TLRPC$EncryptedChat tLRPC$EncryptedChat, TLRPC$ChatFull tLRPC$ChatFull, long j, MessageObject messageObject, SparseArray[] sparseArrayArr, MessageObject.GroupedMessages groupedMessages, boolean z, Runnable runnable) {
        int i;
        try {
            alertDialogArr[0].dismiss();
        } catch (Throwable unused) {
        }
        alertDialogArr[0] = null;
        if (tLObject != null) {
            TLRPC$ChannelParticipant tLRPC$ChannelParticipant = ((TLRPC$TL_channels_channelParticipant) tLObject).participant;
            if (!(tLRPC$ChannelParticipant instanceof TLRPC$TL_channelParticipantAdmin) && !(tLRPC$ChannelParticipant instanceof TLRPC$TL_channelParticipantCreator)) {
                i = 0;
                createDeleteMessagesAlert(baseFragment, tLRPC$User, tLRPC$Chat, tLRPC$EncryptedChat, tLRPC$ChatFull, j, messageObject, sparseArrayArr, groupedMessages, z, i, runnable);
            }
        }
        i = 2;
        createDeleteMessagesAlert(baseFragment, tLRPC$User, tLRPC$Chat, tLRPC$EncryptedChat, tLRPC$ChatFull, j, messageObject, sparseArrayArr, groupedMessages, z, i, runnable);
    }

    static /* synthetic */ void lambda$createDeleteMessagesAlert$57(AlertDialog[] alertDialogArr, int i, int i2, BaseFragment baseFragment) {
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

    static /* synthetic */ void lambda$createDeleteMessagesAlert$58(boolean[] zArr, View view) {
        if (view.isEnabled()) {
            CheckBoxCell checkBoxCell = (CheckBoxCell) view;
            Integer num = (Integer) checkBoxCell.getTag();
            zArr[num.intValue()] = !zArr[num.intValue()];
            checkBoxCell.setChecked(zArr[num.intValue()], true);
        }
    }

    static /* synthetic */ void lambda$createDeleteMessagesAlert$59(boolean[] zArr, View view) {
        zArr[0] = !zArr[0];
        ((CheckBoxCell) view).setChecked(zArr[0], true);
    }

    static /* synthetic */ void lambda$createDeleteMessagesAlert$60(boolean[] zArr, View view) {
        zArr[0] = !zArr[0];
        ((CheckBoxCell) view).setChecked(zArr[0], true);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:34:0x00c9, code lost:
        r0 = ((org.telegram.messenger.MessageObject) r27[r16].get(r7.get(r8).intValue())).messageOwner.to_id.channel_id;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    static /* synthetic */ void lambda$createDeleteMessagesAlert$62(org.telegram.messenger.MessageObject r19, org.telegram.messenger.MessageObject.GroupedMessages r20, org.telegram.tgnet.TLRPC$EncryptedChat r21, int r22, long r23, boolean[] r25, boolean r26, android.util.SparseArray[] r27, org.telegram.tgnet.TLRPC$User r28, boolean[] r29, org.telegram.tgnet.TLRPC$Chat r30, org.telegram.tgnet.TLRPC$ChatFull r31, java.lang.Runnable r32, android.content.DialogInterface r33, int r34) {
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
            org.telegram.tgnet.TLRPC$InputChannel r1 = org.telegram.messenger.MessagesController.getInputChannel((org.telegram.tgnet.TLRPC$Chat) r30)
            r0.channel = r1
            org.telegram.messenger.MessagesController r1 = org.telegram.messenger.MessagesController.getInstance(r22)
            org.telegram.tgnet.TLRPC$InputUser r1 = r1.getInputUser((org.telegram.tgnet.TLRPC$User) r9)
            r0.user_id = r1
            r0.id = r7
            org.telegram.tgnet.ConnectionsManager r1 = org.telegram.tgnet.ConnectionsManager.getInstance(r22)
            org.telegram.ui.Components.-$$Lambda$AlertsCreator$flWeSv76nJBVVDQQp1E_UPug5as r2 = org.telegram.ui.Components.$$Lambda$AlertsCreator$flWeSv76nJBVVDQQp1E_UPug5as.INSTANCE
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
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.AlertsCreator.lambda$createDeleteMessagesAlert$62(org.telegram.messenger.MessageObject, org.telegram.messenger.MessageObject$GroupedMessages, org.telegram.tgnet.TLRPC$EncryptedChat, int, long, boolean[], boolean, android.util.SparseArray[], org.telegram.tgnet.TLRPC$User, boolean[], org.telegram.tgnet.TLRPC$Chat, org.telegram.tgnet.TLRPC$ChatFull, java.lang.Runnable, android.content.DialogInterface, int):void");
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
            builder.setPositiveButton(LocaleController.getString("Create", NUM), $$Lambda$AlertsCreator$4A66wtMT3Tq7XqW6bkhcNuEPcs.INSTANCE);
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
            editTextBoldCursor.setOnEditorActionListener($$Lambda$AlertsCreator$CMQfj4D7MzLevGjQh5r5_Wbhj8.INSTANCE);
            editTextBoldCursor.setText(generateThemeName(themeAccent));
            editTextBoldCursor.setSelection(editTextBoldCursor.length());
            AlertDialog create = builder.create();
            create.setOnShowListener(new DialogInterface.OnShowListener() {
                public final void onShow(DialogInterface dialogInterface) {
                    AndroidUtilities.runOnUIThread(new Runnable() {
                        public final void run() {
                            AlertsCreator.lambda$null$65(EditTextBoldCursor.this);
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
                    AlertsCreator.lambda$createThemeCreateDialog$69(BaseFragment.this, this.f$1, this.f$2, this.f$3, this.f$4, view);
                }
            });
        }
    }

    static /* synthetic */ void lambda$null$65(EditTextBoldCursor editTextBoldCursor) {
        editTextBoldCursor.requestFocus();
        AndroidUtilities.showKeyboard(editTextBoldCursor);
    }

    static /* synthetic */ void lambda$createThemeCreateDialog$69(BaseFragment baseFragment, EditTextBoldCursor editTextBoldCursor, Theme.ThemeAccent themeAccent, Theme.ThemeInfo themeInfo, AlertDialog alertDialog, View view) {
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
