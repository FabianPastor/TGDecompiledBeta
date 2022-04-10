package org.telegram.ui.Components;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Outline;
import android.graphics.Point;
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
import androidx.core.util.Consumer;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
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
import org.telegram.messenger.OneUIUtilities;
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
import org.telegram.tgnet.TLRPC$ChannelParticipant;
import org.telegram.tgnet.TLRPC$Chat;
import org.telegram.tgnet.TLRPC$ChatFull;
import org.telegram.tgnet.TLRPC$Dialog;
import org.telegram.tgnet.TLRPC$EncryptedChat;
import org.telegram.tgnet.TLRPC$InputPeer;
import org.telegram.tgnet.TLRPC$Message;
import org.telegram.tgnet.TLRPC$TL_account_changePhone;
import org.telegram.tgnet.TLRPC$TL_account_confirmPhone;
import org.telegram.tgnet.TLRPC$TL_account_getAuthorizationForm;
import org.telegram.tgnet.TLRPC$TL_account_getPassword;
import org.telegram.tgnet.TLRPC$TL_account_getTmpPassword;
import org.telegram.tgnet.TLRPC$TL_account_saveSecureValue;
import org.telegram.tgnet.TLRPC$TL_account_sendChangePhoneCode;
import org.telegram.tgnet.TLRPC$TL_account_sendConfirmPhoneCode;
import org.telegram.tgnet.TLRPC$TL_account_updateProfile;
import org.telegram.tgnet.TLRPC$TL_account_verifyEmail;
import org.telegram.tgnet.TLRPC$TL_account_verifyPhone;
import org.telegram.tgnet.TLRPC$TL_auth_resendCode;
import org.telegram.tgnet.TLRPC$TL_channelParticipantAdmin;
import org.telegram.tgnet.TLRPC$TL_channelParticipantCreator;
import org.telegram.tgnet.TLRPC$TL_channels_channelParticipant;
import org.telegram.tgnet.TLRPC$TL_channels_createChannel;
import org.telegram.tgnet.TLRPC$TL_channels_editAdmin;
import org.telegram.tgnet.TLRPC$TL_channels_editBanned;
import org.telegram.tgnet.TLRPC$TL_channels_inviteToChannel;
import org.telegram.tgnet.TLRPC$TL_channels_joinChannel;
import org.telegram.tgnet.TLRPC$TL_channels_reportSpam;
import org.telegram.tgnet.TLRPC$TL_contacts_blockFromReplies;
import org.telegram.tgnet.TLRPC$TL_contacts_importContacts;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$TL_help_getSupport;
import org.telegram.tgnet.TLRPC$TL_help_support;
import org.telegram.tgnet.TLRPC$TL_inputPeerUser;
import org.telegram.tgnet.TLRPC$TL_inputReportReasonChildAbuse;
import org.telegram.tgnet.TLRPC$TL_inputReportReasonFake;
import org.telegram.tgnet.TLRPC$TL_inputReportReasonIllegalDrugs;
import org.telegram.tgnet.TLRPC$TL_inputReportReasonOther;
import org.telegram.tgnet.TLRPC$TL_inputReportReasonPersonalDetails;
import org.telegram.tgnet.TLRPC$TL_inputReportReasonPornography;
import org.telegram.tgnet.TLRPC$TL_inputReportReasonSpam;
import org.telegram.tgnet.TLRPC$TL_inputReportReasonViolence;
import org.telegram.tgnet.TLRPC$TL_langPackLanguage;
import org.telegram.tgnet.TLRPC$TL_messages_addChatUser;
import org.telegram.tgnet.TLRPC$TL_messages_checkHistoryImport;
import org.telegram.tgnet.TLRPC$TL_messages_checkHistoryImportPeer;
import org.telegram.tgnet.TLRPC$TL_messages_createChat;
import org.telegram.tgnet.TLRPC$TL_messages_editChatAdmin;
import org.telegram.tgnet.TLRPC$TL_messages_editChatDefaultBannedRights;
import org.telegram.tgnet.TLRPC$TL_messages_editMessage;
import org.telegram.tgnet.TLRPC$TL_messages_forwardMessages;
import org.telegram.tgnet.TLRPC$TL_messages_getAttachedStickers;
import org.telegram.tgnet.TLRPC$TL_messages_importChatInvite;
import org.telegram.tgnet.TLRPC$TL_messages_initHistoryImport;
import org.telegram.tgnet.TLRPC$TL_messages_migrateChat;
import org.telegram.tgnet.TLRPC$TL_messages_report;
import org.telegram.tgnet.TLRPC$TL_messages_sendInlineBotResult;
import org.telegram.tgnet.TLRPC$TL_messages_sendMedia;
import org.telegram.tgnet.TLRPC$TL_messages_sendMessage;
import org.telegram.tgnet.TLRPC$TL_messages_sendMultiMedia;
import org.telegram.tgnet.TLRPC$TL_messages_sendScheduledMessages;
import org.telegram.tgnet.TLRPC$TL_messages_startBot;
import org.telegram.tgnet.TLRPC$TL_messages_startHistoryImport;
import org.telegram.tgnet.TLRPC$TL_payments_sendPaymentForm;
import org.telegram.tgnet.TLRPC$TL_payments_validateRequestedInfo;
import org.telegram.tgnet.TLRPC$TL_peerNotifySettings;
import org.telegram.tgnet.TLRPC$TL_phone_inviteToGroupCall;
import org.telegram.tgnet.TLRPC$TL_updateUserName;
import org.telegram.tgnet.TLRPC$Updates;
import org.telegram.tgnet.TLRPC$User;
import org.telegram.tgnet.TLRPC$UserFull;
import org.telegram.tgnet.TLRPC$UserStatus;
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
import org.telegram.ui.LoginActivity;
import org.telegram.ui.NotificationsCustomSettingsActivity;
import org.telegram.ui.NotificationsSettingsActivity;
import org.telegram.ui.ProfileNotificationsActivity;
import org.telegram.ui.ThemePreviewActivity;
import org.telegram.ui.TooManyCommunitiesActivity;

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

    public interface SoundFrequencyDelegate {
        void didSelectValues(int i, int i2);
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$createAutoDeleteDatePickerDialog$63(View view, MotionEvent motionEvent) {
        return true;
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$createCalendarPickerDialog$77(View view, MotionEvent motionEvent) {
        return true;
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$createChangeBioAlert$29(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$createChangeNameAlert$33(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$createDatePickerDialog$56(View view, MotionEvent motionEvent) {
        return true;
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$createDeleteMessagesAlert$118(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$createMuteForPickerDialog$72(View view, MotionEvent motionEvent) {
        return true;
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$createReportAlert$86(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$createScheduleDatePickerDialog$47(View view, MotionEvent motionEvent) {
        return true;
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$createSoundFrequencyPickerDialog$69(View view, MotionEvent motionEvent) {
        return true;
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$createThemeCreateDialog$121(DialogInterface dialogInterface, int i) {
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$sendReport$84(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
    }

    public static Dialog createForgotPasscodeDialog(Context context) {
        return new AlertDialog.Builder(context).setTitle(LocaleController.getString(NUM)).setMessage(LocaleController.getString(NUM)).setPositiveButton(LocaleController.getString(NUM), (DialogInterface.OnClickListener) null).create();
    }

    public static Dialog createLocationRequiredDialog(Context context, boolean z) {
        String str;
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        if (z) {
            str = LocaleController.getString("PermissionNoLocationFriends", NUM);
        } else {
            str = LocaleController.getString("PermissionNoLocationPeopleNearby", NUM);
        }
        return builder.setMessage(AndroidUtilities.replaceTags(str)).setTopAnimation(NUM, 72, false, Theme.getColor("dialogTopBackground")).setPositiveButton(LocaleController.getString("PermissionOpenSettings", NUM), new AlertsCreator$$ExternalSyntheticLambda9(context)).setNegativeButton(LocaleController.getString("ContactsPermissionAlertNotNow", NUM), (DialogInterface.OnClickListener) null).create();
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$createLocationRequiredDialog$0(Context context, DialogInterface dialogInterface, int i) {
        try {
            Intent intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
            intent.setData(Uri.parse("package:" + ApplicationLoader.applicationContext.getPackageName()));
            context.startActivity(intent);
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    public static Dialog createBackgroundActivityDialog(Context context) {
        return new AlertDialog.Builder(context).setTitle(LocaleController.getString(NUM)).setMessage(AndroidUtilities.replaceTags(LocaleController.getString(OneUIUtilities.isOneUI() ? Build.VERSION.SDK_INT >= 31 ? NUM : NUM : NUM))).setTopAnimation(NUM, 72, false, Theme.getColor("dialogTopBackground")).setPositiveButton(LocaleController.getString(NUM), new AlertsCreator$$ExternalSyntheticLambda10(context)).setNegativeButton(LocaleController.getString("ContactsPermissionAlertNotNow", NUM), (DialogInterface.OnClickListener) null).create();
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$createBackgroundActivityDialog$1(Context context, DialogInterface dialogInterface, int i) {
        try {
            Intent intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
            intent.setData(Uri.parse("package:" + ApplicationLoader.applicationContext.getPackageName()));
            context.startActivity(intent);
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    public static Dialog createWebViewPermissionsRequestDialog(Context context, Theme.ResourcesProvider resourcesProvider, String[] strArr, int i, String str, String str2, Consumer<Boolean> consumer) {
        boolean z;
        if (strArr != null && (context instanceof Activity) && Build.VERSION.SDK_INT >= 23) {
            Activity activity = (Activity) context;
            int length = strArr.length;
            int i2 = 0;
            while (true) {
                if (i2 >= length) {
                    break;
                }
                String str3 = strArr[i2];
                if (activity.checkSelfPermission(str3) != 0 && activity.shouldShowRequestPermissionRationale(str3)) {
                    z = true;
                    break;
                }
                i2++;
            }
        }
        z = false;
        AtomicBoolean atomicBoolean = new AtomicBoolean();
        AlertDialog.Builder topAnimation = new AlertDialog.Builder(context, resourcesProvider).setTopAnimation(i, 72, false, Theme.getColor("dialogTopBackground"));
        if (z) {
            str = str2;
        }
        return topAnimation.setMessage(AndroidUtilities.replaceTags(str)).setPositiveButton(LocaleController.getString(z ? NUM : NUM), new AlertsCreator$$ExternalSyntheticLambda32(z, context, atomicBoolean, consumer)).setNegativeButton(LocaleController.getString(NUM), new AlertsCreator$$ExternalSyntheticLambda16(atomicBoolean, consumer)).setOnDismissListener(new AlertsCreator$$ExternalSyntheticLambda45(atomicBoolean, consumer)).create();
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$createWebViewPermissionsRequestDialog$2(boolean z, Context context, AtomicBoolean atomicBoolean, Consumer consumer, DialogInterface dialogInterface, int i) {
        if (z) {
            try {
                Intent intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
                intent.setData(Uri.parse("package:" + ApplicationLoader.applicationContext.getPackageName()));
                context.startActivity(intent);
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        } else {
            atomicBoolean.set(true);
            consumer.accept(Boolean.TRUE);
        }
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$createWebViewPermissionsRequestDialog$3(AtomicBoolean atomicBoolean, Consumer consumer, DialogInterface dialogInterface, int i) {
        atomicBoolean.set(true);
        consumer.accept(Boolean.FALSE);
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$createWebViewPermissionsRequestDialog$4(AtomicBoolean atomicBoolean, Consumer consumer, DialogInterface dialogInterface) {
        if (!atomicBoolean.get()) {
            consumer.accept(Boolean.FALSE);
        }
    }

    public static Dialog createApkRestrictedDialog(Context context, Theme.ResourcesProvider resourcesProvider) {
        return new AlertDialog.Builder(context, resourcesProvider).setMessage(LocaleController.getString("ApkRestricted", NUM)).setTopAnimation(NUM, 72, false, Theme.getColor("dialogTopBackground")).setPositiveButton(LocaleController.getString("PermissionOpenSettings", NUM), new AlertsCreator$$ExternalSyntheticLambda11(context)).setNegativeButton(LocaleController.getString("ContactsPermissionAlertNotNow", NUM), (DialogInterface.OnClickListener) null).create();
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$createApkRestrictedDialog$5(Context context, DialogInterface dialogInterface, int i) {
        try {
            context.startActivity(new Intent("android.settings.MANAGE_UNKNOWN_APP_SOURCES", Uri.parse("package:" + context.getPackageName())));
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    public static Dialog processError(int i, TLRPC$TL_error tLRPC$TL_error, BaseFragment baseFragment, TLObject tLObject, Object... objArr) {
        String str;
        TLRPC$InputPeer tLRPC$InputPeer;
        TLRPC$TL_error tLRPC$TL_error2 = tLRPC$TL_error;
        BaseFragment baseFragment2 = baseFragment;
        TLObject tLObject2 = tLObject;
        Object[] objArr2 = objArr;
        int i2 = tLRPC$TL_error2.code;
        if (i2 == 406 || (str = tLRPC$TL_error2.text) == null) {
            return null;
        }
        boolean z = tLObject2 instanceof TLRPC$TL_messages_initHistoryImport;
        if (z || (tLObject2 instanceof TLRPC$TL_messages_checkHistoryImportPeer) || (tLObject2 instanceof TLRPC$TL_messages_checkHistoryImport) || (tLObject2 instanceof TLRPC$TL_messages_startHistoryImport)) {
            if (z) {
                tLRPC$InputPeer = ((TLRPC$TL_messages_initHistoryImport) tLObject2).peer;
            } else {
                tLRPC$InputPeer = tLObject2 instanceof TLRPC$TL_messages_startHistoryImport ? ((TLRPC$TL_messages_startHistoryImport) tLObject2).peer : null;
            }
            if (str.contains("USER_IS_BLOCKED")) {
                showSimpleAlert(baseFragment2, LocaleController.getString("ImportErrorTitle", NUM), LocaleController.getString("ImportErrorUserBlocked", NUM));
            } else if (tLRPC$TL_error2.text.contains("USER_NOT_MUTUAL_CONTACT")) {
                showSimpleAlert(baseFragment2, LocaleController.getString("ImportErrorTitle", NUM), LocaleController.getString("ImportMutualError", NUM));
            } else if (tLRPC$TL_error2.text.contains("IMPORT_PEER_TYPE_INVALID")) {
                if (tLRPC$InputPeer instanceof TLRPC$TL_inputPeerUser) {
                    showSimpleAlert(baseFragment2, LocaleController.getString("ImportErrorTitle", NUM), LocaleController.getString("ImportErrorChatInvalidUser", NUM));
                } else {
                    showSimpleAlert(baseFragment2, LocaleController.getString("ImportErrorTitle", NUM), LocaleController.getString("ImportErrorChatInvalidGroup", NUM));
                }
            } else if (tLRPC$TL_error2.text.contains("CHAT_ADMIN_REQUIRED")) {
                showSimpleAlert(baseFragment2, LocaleController.getString("ImportErrorTitle", NUM), LocaleController.getString("ImportErrorNotAdmin", NUM));
            } else if (tLRPC$TL_error2.text.startsWith("IMPORT_FORMAT")) {
                showSimpleAlert(baseFragment2, LocaleController.getString("ImportErrorTitle", NUM), LocaleController.getString("ImportErrorFileFormatInvalid", NUM));
            } else if (tLRPC$TL_error2.text.startsWith("PEER_ID_INVALID")) {
                showSimpleAlert(baseFragment2, LocaleController.getString("ImportErrorTitle", NUM), LocaleController.getString("ImportErrorPeerInvalid", NUM));
            } else if (tLRPC$TL_error2.text.contains("IMPORT_LANG_NOT_FOUND")) {
                showSimpleAlert(baseFragment2, LocaleController.getString("ImportErrorTitle", NUM), LocaleController.getString("ImportErrorFileLang", NUM));
            } else if (tLRPC$TL_error2.text.contains("IMPORT_UPLOAD_FAILED")) {
                showSimpleAlert(baseFragment2, LocaleController.getString("ImportErrorTitle", NUM), LocaleController.getString("ImportFailedToUpload", NUM));
            } else if (tLRPC$TL_error2.text.startsWith("FLOOD_WAIT")) {
                showFloodWaitAlert(tLRPC$TL_error2.text, baseFragment2);
            } else {
                String string = LocaleController.getString("ImportErrorTitle", NUM);
                showSimpleAlert(baseFragment2, string, LocaleController.getString("ErrorOccurred", NUM) + "\n" + tLRPC$TL_error2.text);
            }
        } else if (!(tLObject2 instanceof TLRPC$TL_account_saveSecureValue) && !(tLObject2 instanceof TLRPC$TL_account_getAuthorizationForm)) {
            boolean z2 = tLObject2 instanceof TLRPC$TL_channels_joinChannel;
            if (z2 || (tLObject2 instanceof TLRPC$TL_channels_editAdmin) || (tLObject2 instanceof TLRPC$TL_channels_inviteToChannel) || (tLObject2 instanceof TLRPC$TL_messages_addChatUser) || (tLObject2 instanceof TLRPC$TL_messages_startBot) || (tLObject2 instanceof TLRPC$TL_channels_editBanned) || (tLObject2 instanceof TLRPC$TL_messages_editChatDefaultBannedRights) || (tLObject2 instanceof TLRPC$TL_messages_editChatAdmin) || (tLObject2 instanceof TLRPC$TL_messages_migrateChat) || (tLObject2 instanceof TLRPC$TL_phone_inviteToGroupCall)) {
                Object[] objArr3 = objArr2;
                if (baseFragment2 == null || !str.equals("CHANNELS_TOO_MUCH")) {
                    if (baseFragment2 != null) {
                        showAddUserAlert(tLRPC$TL_error2.text, baseFragment2, (objArr3 == null || objArr3.length <= 0) ? false : ((Boolean) objArr3[0]).booleanValue(), tLObject2);
                    } else if (tLRPC$TL_error2.text.equals("PEER_FLOOD")) {
                        NotificationCenter.getInstance(i).postNotificationName(NotificationCenter.needShowAlert, 1);
                    }
                } else if (z2 || (tLObject2 instanceof TLRPC$TL_channels_inviteToChannel)) {
                    baseFragment2.presentFragment(new TooManyCommunitiesActivity(0));
                    return null;
                } else {
                    baseFragment2.presentFragment(new TooManyCommunitiesActivity(1));
                    return null;
                }
            } else {
                if (tLObject2 instanceof TLRPC$TL_messages_createChat) {
                    if (str.equals("CHANNELS_TOO_MUCH")) {
                        baseFragment2.presentFragment(new TooManyCommunitiesActivity(2));
                        return null;
                    } else if (tLRPC$TL_error2.text.startsWith("FLOOD_WAIT")) {
                        showFloodWaitAlert(tLRPC$TL_error2.text, baseFragment2);
                    } else {
                        showAddUserAlert(tLRPC$TL_error2.text, baseFragment2, false, tLObject2);
                    }
                } else if (tLObject2 instanceof TLRPC$TL_channels_createChannel) {
                    if (str.equals("CHANNELS_TOO_MUCH")) {
                        baseFragment2.presentFragment(new TooManyCommunitiesActivity(2));
                        return null;
                    } else if (tLRPC$TL_error2.text.startsWith("FLOOD_WAIT")) {
                        showFloodWaitAlert(tLRPC$TL_error2.text, baseFragment2);
                    } else {
                        showAddUserAlert(tLRPC$TL_error2.text, baseFragment2, false, tLObject2);
                    }
                } else if (tLObject2 instanceof TLRPC$TL_messages_editMessage) {
                    if (!str.equals("MESSAGE_NOT_MODIFIED")) {
                        if (baseFragment2 != null) {
                            showSimpleAlert(baseFragment2, LocaleController.getString("EditMessageError", NUM));
                        } else {
                            showSimpleToast((BaseFragment) null, LocaleController.getString("EditMessageError", NUM));
                        }
                    }
                } else if ((tLObject2 instanceof TLRPC$TL_messages_sendMessage) || (tLObject2 instanceof TLRPC$TL_messages_sendMedia) || (tLObject2 instanceof TLRPC$TL_messages_sendInlineBotResult) || (tLObject2 instanceof TLRPC$TL_messages_forwardMessages) || (tLObject2 instanceof TLRPC$TL_messages_sendMultiMedia) || (tLObject2 instanceof TLRPC$TL_messages_sendScheduledMessages)) {
                    str.hashCode();
                    char c = 65535;
                    switch (str.hashCode()) {
                        case -1809401834:
                            if (str.equals("USER_BANNED_IN_CHANNEL")) {
                                c = 0;
                                break;
                            }
                            break;
                        case -454039871:
                            if (str.equals("PEER_FLOOD")) {
                                c = 1;
                                break;
                            }
                            break;
                        case 1169786080:
                            if (str.equals("SCHEDULE_TOO_MUCH")) {
                                c = 2;
                                break;
                            }
                            break;
                    }
                    switch (c) {
                        case 0:
                            NotificationCenter.getInstance(i).postNotificationName(NotificationCenter.needShowAlert, 5);
                            break;
                        case 1:
                            NotificationCenter.getInstance(i).postNotificationName(NotificationCenter.needShowAlert, 0);
                            break;
                        case 2:
                            showSimpleToast(baseFragment2, LocaleController.getString("MessageScheduledLimitReached", NUM));
                            break;
                    }
                } else if (tLObject2 instanceof TLRPC$TL_messages_importChatInvite) {
                    if (str.startsWith("FLOOD_WAIT")) {
                        showSimpleAlert(baseFragment2, LocaleController.getString("FloodWait", NUM));
                    } else if (tLRPC$TL_error2.text.equals("USERS_TOO_MUCH")) {
                        showSimpleAlert(baseFragment2, LocaleController.getString("JoinToGroupErrorFull", NUM));
                    } else if (tLRPC$TL_error2.text.equals("CHANNELS_TOO_MUCH")) {
                        baseFragment2.presentFragment(new TooManyCommunitiesActivity(0));
                    } else if (tLRPC$TL_error2.text.equals("INVITE_HASH_EXPIRED")) {
                        showSimpleAlert(baseFragment2, LocaleController.getString("ExpiredLink", NUM), LocaleController.getString("InviteExpired", NUM));
                    } else {
                        showSimpleAlert(baseFragment2, LocaleController.getString("JoinToGroupErrorNotExist", NUM));
                    }
                } else if (!(tLObject2 instanceof TLRPC$TL_messages_getAttachedStickers)) {
                    int i3 = i2;
                    if ((tLObject2 instanceof TLRPC$TL_account_confirmPhone) || (tLObject2 instanceof TLRPC$TL_account_verifyPhone) || (tLObject2 instanceof TLRPC$TL_account_verifyEmail)) {
                        if (str.contains("PHONE_CODE_EMPTY") || tLRPC$TL_error2.text.contains("PHONE_CODE_INVALID") || tLRPC$TL_error2.text.contains("CODE_INVALID") || tLRPC$TL_error2.text.contains("CODE_EMPTY")) {
                            return showSimpleAlert(baseFragment2, LocaleController.getString("InvalidCode", NUM));
                        }
                        if (tLRPC$TL_error2.text.contains("PHONE_CODE_EXPIRED") || tLRPC$TL_error2.text.contains("EMAIL_VERIFY_EXPIRED")) {
                            return showSimpleAlert(baseFragment2, LocaleController.getString("CodeExpired", NUM));
                        }
                        if (tLRPC$TL_error2.text.startsWith("FLOOD_WAIT")) {
                            return showSimpleAlert(baseFragment2, LocaleController.getString("FloodWait", NUM));
                        }
                        return showSimpleAlert(baseFragment2, tLRPC$TL_error2.text);
                    } else if (tLObject2 instanceof TLRPC$TL_auth_resendCode) {
                        if (str.contains("PHONE_NUMBER_INVALID")) {
                            return showSimpleAlert(baseFragment2, LocaleController.getString("InvalidPhoneNumber", NUM));
                        }
                        if (tLRPC$TL_error2.text.contains("PHONE_CODE_EMPTY") || tLRPC$TL_error2.text.contains("PHONE_CODE_INVALID")) {
                            return showSimpleAlert(baseFragment2, LocaleController.getString("InvalidCode", NUM));
                        }
                        if (tLRPC$TL_error2.text.contains("PHONE_CODE_EXPIRED")) {
                            return showSimpleAlert(baseFragment2, LocaleController.getString("CodeExpired", NUM));
                        }
                        if (tLRPC$TL_error2.text.startsWith("FLOOD_WAIT")) {
                            return showSimpleAlert(baseFragment2, LocaleController.getString("FloodWait", NUM));
                        }
                        if (tLRPC$TL_error2.code != -1000) {
                            return showSimpleAlert(baseFragment2, LocaleController.getString("ErrorOccurred", NUM) + "\n" + tLRPC$TL_error2.text);
                        }
                    } else if (tLObject2 instanceof TLRPC$TL_account_sendConfirmPhoneCode) {
                        if (i3 == 400) {
                            return showSimpleAlert(baseFragment2, LocaleController.getString("CancelLinkExpired", NUM));
                        }
                        if (str.startsWith("FLOOD_WAIT")) {
                            return showSimpleAlert(baseFragment2, LocaleController.getString("FloodWait", NUM));
                        }
                        return showSimpleAlert(baseFragment2, LocaleController.getString("ErrorOccurred", NUM));
                    } else if (tLObject2 instanceof TLRPC$TL_account_changePhone) {
                        if (str.contains("PHONE_NUMBER_INVALID")) {
                            showSimpleAlert(baseFragment2, LocaleController.getString("InvalidPhoneNumber", NUM));
                        } else if (tLRPC$TL_error2.text.contains("PHONE_CODE_EMPTY") || tLRPC$TL_error2.text.contains("PHONE_CODE_INVALID")) {
                            showSimpleAlert(baseFragment2, LocaleController.getString("InvalidCode", NUM));
                        } else if (tLRPC$TL_error2.text.contains("PHONE_CODE_EXPIRED")) {
                            showSimpleAlert(baseFragment2, LocaleController.getString("CodeExpired", NUM));
                        } else if (tLRPC$TL_error2.text.startsWith("FLOOD_WAIT")) {
                            showSimpleAlert(baseFragment2, LocaleController.getString("FloodWait", NUM));
                        } else {
                            showSimpleAlert(baseFragment2, tLRPC$TL_error2.text);
                        }
                    } else if (tLObject2 instanceof TLRPC$TL_account_sendChangePhoneCode) {
                        if (str.contains("PHONE_NUMBER_INVALID")) {
                            LoginActivity.needShowInvalidAlert(baseFragment2, (String) objArr[0], false);
                        } else {
                            Object[] objArr4 = objArr;
                            if (tLRPC$TL_error2.text.contains("PHONE_CODE_EMPTY") || tLRPC$TL_error2.text.contains("PHONE_CODE_INVALID")) {
                                showSimpleAlert(baseFragment2, LocaleController.getString("InvalidCode", NUM));
                            } else if (tLRPC$TL_error2.text.contains("PHONE_CODE_EXPIRED")) {
                                showSimpleAlert(baseFragment2, LocaleController.getString("CodeExpired", NUM));
                            } else if (tLRPC$TL_error2.text.startsWith("FLOOD_WAIT")) {
                                showSimpleAlert(baseFragment2, LocaleController.getString("FloodWait", NUM));
                            } else if (tLRPC$TL_error2.text.startsWith("PHONE_NUMBER_OCCUPIED")) {
                                showSimpleAlert(baseFragment2, LocaleController.formatString("ChangePhoneNumberOccupied", NUM, objArr4[0]));
                            } else if (tLRPC$TL_error2.text.startsWith("PHONE_NUMBER_BANNED")) {
                                LoginActivity.needShowInvalidAlert(baseFragment2, (String) objArr4[0], true);
                            } else {
                                showSimpleAlert(baseFragment2, LocaleController.getString("ErrorOccurred", NUM));
                            }
                        }
                    } else if (tLObject2 instanceof TLRPC$TL_updateUserName) {
                        str.hashCode();
                        if (str.equals("USERNAME_INVALID")) {
                            showSimpleAlert(baseFragment2, LocaleController.getString("UsernameInvalid", NUM));
                        } else if (!str.equals("USERNAME_OCCUPIED")) {
                            showSimpleAlert(baseFragment2, LocaleController.getString("ErrorOccurred", NUM));
                        } else {
                            showSimpleAlert(baseFragment2, LocaleController.getString("UsernameInUse", NUM));
                        }
                    } else if (tLObject2 instanceof TLRPC$TL_contacts_importContacts) {
                        if (str.startsWith("FLOOD_WAIT")) {
                            showSimpleAlert(baseFragment2, LocaleController.getString("FloodWait", NUM));
                        } else {
                            showSimpleAlert(baseFragment2, LocaleController.getString("ErrorOccurred", NUM) + "\n" + tLRPC$TL_error2.text);
                        }
                    } else if ((tLObject2 instanceof TLRPC$TL_account_getPassword) || (tLObject2 instanceof TLRPC$TL_account_getTmpPassword)) {
                        if (str.startsWith("FLOOD_WAIT")) {
                            showSimpleToast(baseFragment2, getFloodWaitString(tLRPC$TL_error2.text));
                        } else {
                            showSimpleToast(baseFragment2, tLRPC$TL_error2.text);
                        }
                    } else if (tLObject2 instanceof TLRPC$TL_payments_sendPaymentForm) {
                        str.hashCode();
                        if (str.equals("BOT_PRECHECKOUT_FAILED")) {
                            showSimpleToast(baseFragment2, LocaleController.getString("PaymentPrecheckoutFailed", NUM));
                        } else if (!str.equals("PAYMENT_FAILED")) {
                            showSimpleToast(baseFragment2, tLRPC$TL_error2.text);
                        } else {
                            showSimpleToast(baseFragment2, LocaleController.getString("PaymentFailed", NUM));
                        }
                    } else if (tLObject2 instanceof TLRPC$TL_payments_validateRequestedInfo) {
                        str.hashCode();
                        if (!str.equals("SHIPPING_NOT_AVAILABLE")) {
                            showSimpleToast(baseFragment2, tLRPC$TL_error2.text);
                        } else {
                            showSimpleToast(baseFragment2, LocaleController.getString("PaymentNoShippingMethod", NUM));
                        }
                    }
                } else if (!(baseFragment2 == null || baseFragment.getParentActivity() == null)) {
                    Activity parentActivity = baseFragment.getParentActivity();
                    Toast.makeText(parentActivity, LocaleController.getString("ErrorOccurred", NUM) + "\n" + tLRPC$TL_error2.text, 0).show();
                }
                return null;
            }
        } else if (str.contains("PHONE_NUMBER_INVALID")) {
            showSimpleAlert(baseFragment2, LocaleController.getString("InvalidPhoneNumber", NUM));
        } else if (tLRPC$TL_error2.text.startsWith("FLOOD_WAIT")) {
            showSimpleAlert(baseFragment2, LocaleController.getString("FloodWait", NUM));
        } else if ("APP_VERSION_OUTDATED".equals(tLRPC$TL_error2.text)) {
            showUpdateAppAlert(baseFragment.getParentActivity(), LocaleController.getString("UpdateAppAlert", NUM), true);
        } else {
            showSimpleAlert(baseFragment2, LocaleController.getString("ErrorOccurred", NUM) + "\n" + tLRPC$TL_error2.text);
        }
        return null;
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
            builder.setNegativeButton(LocaleController.getString("UpdateApp", NUM), new AlertsCreator$$ExternalSyntheticLambda12(context));
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
            builder.setNeutralButton(LocaleController.getString("SETTINGS", NUM), new AlertsCreator$$ExternalSyntheticLambda31(launchActivity));
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
            builder.setPositiveButton(LocaleController.getString("Change", NUM), new AlertsCreator$$ExternalSyntheticLambda22(tLRPC$TL_langPackLanguage, launchActivity));
            builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
        }
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(AndroidUtilities.replaceTags(str));
        int indexOf = TextUtils.indexOf(spannableStringBuilder, '[');
        if (indexOf != -1) {
            int i2 = indexOf + 1;
            i = TextUtils.indexOf(spannableStringBuilder, ']', i2);
            if (i != -1) {
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

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$createLanguageAlert$8(TLRPC$TL_langPackLanguage tLRPC$TL_langPackLanguage, LaunchActivity launchActivity, DialogInterface dialogInterface, int i) {
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
        if (!DialogObject.isChatDialog(j) || (chat = MessagesController.getInstance(i).getChat(Long.valueOf(-j))) == null || !chat.slowmode_enabled || ChatObject.hasAdminRights(chat)) {
            return false;
        }
        if (!z) {
            TLRPC$ChatFull chatFull = MessagesController.getInstance(i).getChatFull(chat.id);
            if (chatFull == null) {
                chatFull = MessagesStorage.getInstance(i).loadChatInfo(chat.id, ChatObject.isChannel(chat), new CountDownLatch(1), false, false);
            }
            if (chatFull != null && chatFull.slowmode_next_send_date >= ConnectionsManager.getInstance(i).getCurrentTime()) {
                z = true;
            }
        }
        if (!z) {
            return false;
        }
        createSimpleAlert(context, chat.title, LocaleController.getString("SlowmodeSendError", NUM)).show();
        return true;
    }

    public static AlertDialog.Builder createSimpleAlert(Context context, String str) {
        return createSimpleAlert(context, (String) null, str);
    }

    public static AlertDialog.Builder createSimpleAlert(Context context, String str, String str2) {
        return createSimpleAlert(context, str, str2, (Theme.ResourcesProvider) null);
    }

    public static AlertDialog.Builder createSimpleAlert(Context context, String str, String str2, Theme.ResourcesProvider resourcesProvider) {
        if (context == null || str2 == null) {
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
        return showSimpleAlert(baseFragment, str, str2, (Theme.ResourcesProvider) null);
    }

    public static Dialog showSimpleAlert(BaseFragment baseFragment, String str, String str2, Theme.ResourcesProvider resourcesProvider) {
        if (str2 == null || baseFragment == null || baseFragment.getParentActivity() == null) {
            return null;
        }
        AlertDialog create = createSimpleAlert(baseFragment.getParentActivity(), str, str2, resourcesProvider).create();
        baseFragment.showDialog(create);
        return create;
    }

    public static void showBlockReportSpamReplyAlert(ChatActivity chatActivity, MessageObject messageObject, long j, Theme.ResourcesProvider resourcesProvider, Runnable runnable) {
        long j2 = j;
        Theme.ResourcesProvider resourcesProvider2 = resourcesProvider;
        Runnable runnable2 = runnable;
        if (chatActivity != null && chatActivity.getParentActivity() != null && messageObject != null) {
            AccountInstance accountInstance = chatActivity.getAccountInstance();
            TLRPC$User user = j2 > 0 ? accountInstance.getMessagesController().getUser(Long.valueOf(j)) : null;
            TLRPC$Chat chat = j2 < 0 ? accountInstance.getMessagesController().getChat(Long.valueOf(-j2)) : null;
            if (user != null || chat != null) {
                AlertDialog.Builder builder = new AlertDialog.Builder(chatActivity.getParentActivity(), resourcesProvider2);
                builder.setDimEnabled(runnable2 == null);
                builder.setOnPreDismissListener(new AlertsCreator$$ExternalSyntheticLambda42(runnable2));
                builder.setTitle(LocaleController.getString("BlockUser", NUM));
                if (user != null) {
                    builder.setMessage(AndroidUtilities.replaceTags(LocaleController.formatString("BlockUserReplyAlert", NUM, UserObject.getFirstName(user))));
                } else {
                    builder.setMessage(AndroidUtilities.replaceTags(LocaleController.formatString("BlockUserReplyAlert", NUM, chat.title)));
                }
                LinearLayout linearLayout = new LinearLayout(chatActivity.getParentActivity());
                linearLayout.setOrientation(1);
                CheckBoxCell[] checkBoxCellArr = {new CheckBoxCell(chatActivity.getParentActivity(), 1, resourcesProvider2)};
                checkBoxCellArr[0].setBackgroundDrawable(Theme.getSelectorDrawable(false));
                checkBoxCellArr[0].setTag(0);
                checkBoxCellArr[0].setText(LocaleController.getString("DeleteReportSpam", NUM), "", true, false);
                checkBoxCellArr[0].setPadding(LocaleController.isRTL ? AndroidUtilities.dp(16.0f) : AndroidUtilities.dp(8.0f), 0, LocaleController.isRTL ? AndroidUtilities.dp(8.0f) : AndroidUtilities.dp(16.0f), 0);
                linearLayout.addView(checkBoxCellArr[0], LayoutHelper.createLinear(-1, -2));
                checkBoxCellArr[0].setOnClickListener(new AlertsCreator$$ExternalSyntheticLambda65(checkBoxCellArr));
                builder.setCustomViewOffset(12);
                builder.setView(linearLayout);
                builder.setPositiveButton(LocaleController.getString("BlockAndDeleteReplies", NUM), new AlertsCreator$$ExternalSyntheticLambda23(user, accountInstance, chatActivity, chat, messageObject, checkBoxCellArr, resourcesProvider));
                builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
                AlertDialog create = builder.create();
                chatActivity.showDialog(create);
                TextView textView = (TextView) create.getButton(-1);
                if (textView != null) {
                    textView.setTextColor(Theme.getColor("dialogTextRed2"));
                }
            }
        }
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$showBlockReportSpamReplyAlert$9(Runnable runnable, DialogInterface dialogInterface) {
        if (runnable != null) {
            runnable.run();
        }
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$showBlockReportSpamReplyAlert$10(CheckBoxCell[] checkBoxCellArr, View view) {
        Integer num = (Integer) view.getTag();
        checkBoxCellArr[num.intValue()].setChecked(!checkBoxCellArr[num.intValue()].isChecked(), true);
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$showBlockReportSpamReplyAlert$12(TLRPC$User tLRPC$User, AccountInstance accountInstance, ChatActivity chatActivity, TLRPC$Chat tLRPC$Chat, MessageObject messageObject, CheckBoxCell[] checkBoxCellArr, Theme.ResourcesProvider resourcesProvider, DialogInterface dialogInterface, int i) {
        if (tLRPC$User != null) {
            accountInstance.getMessagesStorage().deleteUserChatHistory(chatActivity.getDialogId(), tLRPC$User.id);
        } else {
            accountInstance.getMessagesStorage().deleteUserChatHistory(chatActivity.getDialogId(), -tLRPC$Chat.id);
        }
        TLRPC$TL_contacts_blockFromReplies tLRPC$TL_contacts_blockFromReplies = new TLRPC$TL_contacts_blockFromReplies();
        tLRPC$TL_contacts_blockFromReplies.msg_id = messageObject.getId();
        tLRPC$TL_contacts_blockFromReplies.delete_message = true;
        tLRPC$TL_contacts_blockFromReplies.delete_history = true;
        if (checkBoxCellArr[0].isChecked()) {
            tLRPC$TL_contacts_blockFromReplies.report_spam = true;
            if (chatActivity.getParentActivity() != null) {
                chatActivity.getUndoView().showWithAction(0, 74, (Runnable) null);
            }
        }
        accountInstance.getConnectionsManager().sendRequest(tLRPC$TL_contacts_blockFromReplies, new AlertsCreator$$ExternalSyntheticLambda94(accountInstance));
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$showBlockReportSpamReplyAlert$11(AccountInstance accountInstance, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        if (tLObject instanceof TLRPC$Updates) {
            accountInstance.getMessagesController().processUpdates((TLRPC$Updates) tLObject, false);
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:12:0x004c  */
    /* JADX WARNING: Removed duplicated region for block: B:32:0x0132  */
    /* JADX WARNING: Removed duplicated region for block: B:49:0x01dd  */
    /* JADX WARNING: Removed duplicated region for block: B:55:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static void showBlockReportSpamAlert(org.telegram.ui.ActionBar.BaseFragment r17, long r18, org.telegram.tgnet.TLRPC$User r20, org.telegram.tgnet.TLRPC$Chat r21, org.telegram.tgnet.TLRPC$EncryptedChat r22, boolean r23, org.telegram.tgnet.TLRPC$ChatFull r24, org.telegram.messenger.MessagesStorage.IntCallback r25, org.telegram.ui.ActionBar.Theme.ResourcesProvider r26) {
        /*
            r0 = r17
            r7 = r21
            r1 = r24
            r2 = r26
            if (r0 == 0) goto L_0x01e6
            android.app.Activity r3 = r17.getParentActivity()
            if (r3 != 0) goto L_0x0012
            goto L_0x01e6
        L_0x0012:
            org.telegram.messenger.AccountInstance r3 = r17.getAccountInstance()
            org.telegram.ui.ActionBar.AlertDialog$Builder r11 = new org.telegram.ui.ActionBar.AlertDialog$Builder
            android.app.Activity r4 = r17.getParentActivity()
            r11.<init>(r4, r2)
            int r4 = r17.getCurrentAccount()
            android.content.SharedPreferences r4 = org.telegram.messenger.MessagesController.getNotificationsSettings(r4)
            r5 = 1
            r6 = 0
            if (r22 != 0) goto L_0x0047
            java.lang.StringBuilder r8 = new java.lang.StringBuilder
            r8.<init>()
            java.lang.String r9 = "dialog_bar_report"
            r8.append(r9)
            r9 = r18
            r8.append(r9)
            java.lang.String r8 = r8.toString()
            boolean r4 = r4.getBoolean(r8, r6)
            if (r4 == 0) goto L_0x0045
            goto L_0x0049
        L_0x0045:
            r4 = 0
            goto L_0x004a
        L_0x0047:
            r9 = r18
        L_0x0049:
            r4 = 1
        L_0x004a:
            if (r20 == 0) goto L_0x0132
            r1 = 2131624624(0x7f0e02b0, float:1.8876433E38)
            java.lang.Object[] r8 = new java.lang.Object[r5]
            java.lang.String r14 = org.telegram.messenger.UserObject.getFirstName(r20)
            r8[r6] = r14
            java.lang.String r14 = "BlockUserTitle"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r14, r1, r8)
            r11.setTitle(r1)
            r1 = 2131624618(0x7f0e02aa, float:1.887642E38)
            java.lang.Object[] r8 = new java.lang.Object[r5]
            java.lang.String r14 = org.telegram.messenger.UserObject.getFirstName(r20)
            r8[r6] = r14
            java.lang.String r14 = "BlockUserAlert"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r14, r1, r8)
            android.text.SpannableStringBuilder r1 = org.telegram.messenger.AndroidUtilities.replaceTags(r1)
            r11.setMessage(r1)
            r1 = 2131624616(0x7f0e02a8, float:1.8876417E38)
            java.lang.String r8 = "BlockContact"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r8, r1)
            r8 = 2
            org.telegram.ui.Cells.CheckBoxCell[] r14 = new org.telegram.ui.Cells.CheckBoxCell[r8]
            android.widget.LinearLayout r15 = new android.widget.LinearLayout
            android.app.Activity r13 = r17.getParentActivity()
            r15.<init>(r13)
            r15.setOrientation(r5)
            r13 = 0
        L_0x0091:
            if (r13 >= r8) goto L_0x0123
            if (r13 != 0) goto L_0x009b
            if (r4 != 0) goto L_0x009b
            r16 = r1
            goto L_0x0119
        L_0x009b:
            org.telegram.ui.Cells.CheckBoxCell r8 = new org.telegram.ui.Cells.CheckBoxCell
            android.app.Activity r12 = r17.getParentActivity()
            r8.<init>(r12, r5, r2)
            r14[r13] = r8
            r8 = r14[r13]
            android.graphics.drawable.Drawable r12 = org.telegram.ui.ActionBar.Theme.getSelectorDrawable(r6)
            r8.setBackgroundDrawable(r12)
            r8 = r14[r13]
            java.lang.Integer r12 = java.lang.Integer.valueOf(r13)
            r8.setTag(r12)
            java.lang.String r8 = ""
            if (r13 != 0) goto L_0x00ce
            r12 = r14[r13]
            r5 = 2131625325(0x7f0e056d, float:1.8877855E38)
            r16 = r1
            java.lang.String r1 = "DeleteReportSpam"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r1, r5)
            r5 = 1
            r12.setText(r1, r8, r5, r6)
            goto L_0x00e1
        L_0x00ce:
            r16 = r1
            r1 = r14[r13]
            r12 = 2131625334(0x7f0e0576, float:1.8877873E38)
            java.lang.Object[] r5 = new java.lang.Object[r6]
            java.lang.String r2 = "DeleteThisChat"
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r12, r5)
            r5 = 1
            r1.setText(r2, r8, r5, r6)
        L_0x00e1:
            r1 = r14[r13]
            boolean r2 = org.telegram.messenger.LocaleController.isRTL
            r5 = 1098907648(0x41800000, float:16.0)
            r8 = 1090519040(0x41000000, float:8.0)
            if (r2 == 0) goto L_0x00f0
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r5)
            goto L_0x00f4
        L_0x00f0:
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r8)
        L_0x00f4:
            boolean r12 = org.telegram.messenger.LocaleController.isRTL
            if (r12 == 0) goto L_0x00fd
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r8)
            goto L_0x0101
        L_0x00fd:
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
        L_0x0101:
            r1.setPadding(r2, r6, r5, r6)
            r1 = r14[r13]
            r2 = -2
            r5 = -1
            android.widget.LinearLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createLinear(r5, r2)
            r15.addView(r1, r2)
            r1 = r14[r13]
            org.telegram.ui.Components.AlertsCreator$$ExternalSyntheticLambda66 r2 = new org.telegram.ui.Components.AlertsCreator$$ExternalSyntheticLambda66
            r2.<init>(r14)
            r1.setOnClickListener(r2)
        L_0x0119:
            int r13 = r13 + 1
            r2 = r26
            r1 = r16
            r5 = 1
            r8 = 2
            goto L_0x0091
        L_0x0123:
            r16 = r1
            r1 = 12
            r11.setCustomViewOffset(r1)
            r11.setView(r15)
            r4 = r14
            r12 = r16
            goto L_0x01ab
        L_0x0132:
            if (r7 == 0) goto L_0x0171
            if (r23 == 0) goto L_0x0171
            r2 = 2131627756(0x7f0e0eec, float:1.8882785E38)
            java.lang.String r4 = "ReportUnrelatedGroup"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r4, r2)
            r11.setTitle(r2)
            if (r1 == 0) goto L_0x0164
            org.telegram.tgnet.TLRPC$ChannelLocation r1 = r1.location
            boolean r2 = r1 instanceof org.telegram.tgnet.TLRPC$TL_channelLocation
            if (r2 == 0) goto L_0x0164
            org.telegram.tgnet.TLRPC$TL_channelLocation r1 = (org.telegram.tgnet.TLRPC$TL_channelLocation) r1
            r2 = 2131627757(0x7f0e0eed, float:1.8882787E38)
            r4 = 1
            java.lang.Object[] r4 = new java.lang.Object[r4]
            java.lang.String r1 = r1.address
            r4[r6] = r1
            java.lang.String r1 = "ReportUnrelatedGroupText"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r4)
            android.text.SpannableStringBuilder r1 = org.telegram.messenger.AndroidUtilities.replaceTags(r1)
            r11.setMessage(r1)
            goto L_0x01a0
        L_0x0164:
            r1 = 2131627758(0x7f0e0eee, float:1.888279E38)
            java.lang.String r2 = "ReportUnrelatedGroupTextNoAddress"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r11.setMessage(r1)
            goto L_0x01a0
        L_0x0171:
            r1 = 2131627749(0x7f0e0ee5, float:1.8882771E38)
            java.lang.String r2 = "ReportSpamTitle"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r11.setTitle(r1)
            boolean r1 = org.telegram.messenger.ChatObject.isChannel(r21)
            if (r1 == 0) goto L_0x0194
            boolean r1 = r7.megagroup
            if (r1 != 0) goto L_0x0194
            r1 = 2131627745(0x7f0e0ee1, float:1.8882763E38)
            java.lang.String r2 = "ReportSpamAlertChannel"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r11.setMessage(r1)
            goto L_0x01a0
        L_0x0194:
            r1 = 2131627746(0x7f0e0ee2, float:1.8882765E38)
            java.lang.String r2 = "ReportSpamAlertGroup"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r11.setMessage(r1)
        L_0x01a0:
            r1 = 2131627725(0x7f0e0ecd, float:1.8882723E38)
            java.lang.String r2 = "ReportChat"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r12 = r1
            r4 = 0
        L_0x01ab:
            org.telegram.ui.Components.AlertsCreator$$ExternalSyntheticLambda24 r13 = new org.telegram.ui.Components.AlertsCreator$$ExternalSyntheticLambda24
            r1 = r13
            r2 = r20
            r5 = r18
            r7 = r21
            r8 = r22
            r9 = r23
            r10 = r25
            r1.<init>(r2, r3, r4, r5, r7, r8, r9, r10)
            r11.setPositiveButton(r12, r13)
            r1 = 2131624750(0x7f0e032e, float:1.8876689E38)
            java.lang.String r2 = "Cancel"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r2 = 0
            r11.setNegativeButton(r1, r2)
            org.telegram.ui.ActionBar.AlertDialog r1 = r11.create()
            r0.showDialog(r1)
            r0 = -1
            android.view.View r0 = r1.getButton(r0)
            android.widget.TextView r0 = (android.widget.TextView) r0
            if (r0 == 0) goto L_0x01e6
            java.lang.String r1 = "dialogTextRed2"
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r1)
            r0.setTextColor(r1)
        L_0x01e6:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.AlertsCreator.showBlockReportSpamAlert(org.telegram.ui.ActionBar.BaseFragment, long, org.telegram.tgnet.TLRPC$User, org.telegram.tgnet.TLRPC$Chat, org.telegram.tgnet.TLRPC$EncryptedChat, boolean, org.telegram.tgnet.TLRPC$ChatFull, org.telegram.messenger.MessagesStorage$IntCallback, org.telegram.ui.ActionBar.Theme$ResourcesProvider):void");
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$showBlockReportSpamAlert$13(CheckBoxCell[] checkBoxCellArr, View view) {
        Integer num = (Integer) view.getTag();
        checkBoxCellArr[num.intValue()].setChecked(!checkBoxCellArr[num.intValue()].isChecked(), true);
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$showBlockReportSpamAlert$14(TLRPC$User tLRPC$User, AccountInstance accountInstance, CheckBoxCell[] checkBoxCellArr, long j, TLRPC$Chat tLRPC$Chat, TLRPC$EncryptedChat tLRPC$EncryptedChat, boolean z, MessagesStorage.IntCallback intCallback, DialogInterface dialogInterface, int i) {
        TLRPC$User tLRPC$User2 = tLRPC$User;
        long j2 = j;
        MessagesStorage.IntCallback intCallback2 = intCallback;
        if (tLRPC$User2 != null) {
            accountInstance.getMessagesController().blockPeer(tLRPC$User2.id);
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
                accountInstance.getMessagesController().deleteParticipantFromChat(-j2, accountInstance.getMessagesController().getUser(Long.valueOf(accountInstance.getUserConfig().getClientUserId())), (TLRPC$ChatFull) null);
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
            strArr2[3] = (j2 != 0 || !(baseFragment2 instanceof NotificationsCustomSettingsActivity)) ? LocaleController.getString("NotificationsCustomize", NUM) : null;
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
                    textView.setOnClickListener(new AlertsCreator$$ExternalSyntheticLambda47(j, i2, isGlobalNotificationsEnabled, intCallback2, i, baseFragment, arrayList, intCallback, builder));
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

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$showCustomNotificationsDialog$15(long j, int i, boolean z, MessagesStorage.IntCallback intCallback, int i2, BaseFragment baseFragment, ArrayList arrayList, MessagesStorage.IntCallback intCallback2, AlertDialog.Builder builder, View view) {
        long j2 = j;
        MessagesStorage.IntCallback intCallback3 = intCallback;
        int i3 = i2;
        BaseFragment baseFragment2 = baseFragment;
        MessagesStorage.IntCallback intCallback4 = intCallback2;
        int intValue = ((Integer) view.getTag()).intValue();
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
            NotificationsController.getInstance(i).muteUntil(j2, currentTime);
            if (!(j2 == 0 || intCallback3 == null)) {
                if (intValue != 4 || z) {
                    intCallback3.run(1);
                } else {
                    intCallback3.run(0);
                }
            }
            if (j2 == 0) {
                NotificationsController.getInstance(i).setGlobalNotificationsEnabled(i3, Integer.MAX_VALUE);
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
        int i4 = intValue == 0 ? 4 : intValue == 1 ? 0 : intValue == 2 ? 2 : intValue == 4 ? 3 : -1;
        if (i4 >= 0 && BulletinFactory.canShowBulletin(baseFragment)) {
            BulletinFactory.createMuteBulletin(baseFragment2, i4).show();
        }
    }

    public static AlertDialog showSecretLocationAlert(Context context, int i, Runnable runnable, boolean z, Theme.ResourcesProvider resourcesProvider) {
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
        AlertDialog.Builder builder = new AlertDialog.Builder(context, resourcesProvider);
        builder.setTitle(LocaleController.getString("MapPreviewProviderTitle", NUM));
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(1);
        builder.setView(linearLayout);
        for (int i3 = 0; i3 < arrayList.size(); i3++) {
            RadioColorCell radioColorCell = new RadioColorCell(context, resourcesProvider);
            radioColorCell.setPadding(AndroidUtilities.dp(4.0f), 0, AndroidUtilities.dp(4.0f), 0);
            radioColorCell.setTag(Integer.valueOf(i3));
            radioColorCell.setCheckColor(Theme.getColor("radioBackground"), Theme.getColor("dialogRadioBackgroundChecked"));
            radioColorCell.setTextAndValue((String) arrayList.get(i3), SharedConfig.mapPreviewType == ((Integer) arrayList2.get(i3)).intValue());
            linearLayout.addView(radioColorCell);
            radioColorCell.setOnClickListener(new AlertsCreator$$ExternalSyntheticLambda50(arrayList2, runnable, builder));
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

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$showSecretLocationAlert$16(ArrayList arrayList, Runnable runnable, AlertDialog.Builder builder, View view) {
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

    public static void showOpenUrlAlert(BaseFragment baseFragment, String str, boolean z, boolean z2) {
        showOpenUrlAlert(baseFragment, str, z, true, z2, (Theme.ResourcesProvider) null);
    }

    public static void showOpenUrlAlert(BaseFragment baseFragment, String str, boolean z, boolean z2, Theme.ResourcesProvider resourcesProvider) {
        showOpenUrlAlert(baseFragment, str, z, true, z2, resourcesProvider);
    }

    /* JADX WARNING: Removed duplicated region for block: B:19:0x0090  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static void showOpenUrlAlert(org.telegram.ui.ActionBar.BaseFragment r12, java.lang.String r13, boolean r14, boolean r15, boolean r16, org.telegram.ui.ActionBar.Theme.ResourcesProvider r17) {
        /*
            r7 = r12
            r3 = r13
            if (r7 == 0) goto L_0x00dc
            android.app.Activity r0 = r12.getParentActivity()
            if (r0 != 0) goto L_0x000c
            goto L_0x00dc
        L_0x000c:
            boolean r0 = r7 instanceof org.telegram.ui.ChatActivity
            r1 = 0
            if (r0 == 0) goto L_0x001a
            r0 = r7
            org.telegram.ui.ChatActivity r0 = (org.telegram.ui.ChatActivity) r0
            long r4 = r0.getInlineReturn()
            goto L_0x001b
        L_0x001a:
            r4 = r1
        L_0x001b:
            r8 = 0
            boolean r0 = org.telegram.messenger.browser.Browser.isInternalUrl(r13, r8)
            r6 = 1
            r9 = 0
            if (r0 != 0) goto L_0x00ce
            if (r16 != 0) goto L_0x0028
            goto L_0x00ce
        L_0x0028:
            if (r14 == 0) goto L_0x005a
            android.net.Uri r0 = android.net.Uri.parse(r13)     // Catch:{ Exception -> 0x0056 }
            java.lang.String r1 = r0.getHost()     // Catch:{ Exception -> 0x0056 }
            java.lang.String r1 = java.net.IDN.toASCII(r1, r6)     // Catch:{ Exception -> 0x0056 }
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0056 }
            r2.<init>()     // Catch:{ Exception -> 0x0056 }
            java.lang.String r10 = r0.getScheme()     // Catch:{ Exception -> 0x0056 }
            r2.append(r10)     // Catch:{ Exception -> 0x0056 }
            java.lang.String r10 = "://"
            r2.append(r10)     // Catch:{ Exception -> 0x0056 }
            r2.append(r1)     // Catch:{ Exception -> 0x0056 }
            java.lang.String r0 = r0.getPath()     // Catch:{ Exception -> 0x0056 }
            r2.append(r0)     // Catch:{ Exception -> 0x0056 }
            java.lang.String r0 = r2.toString()     // Catch:{ Exception -> 0x0056 }
            goto L_0x005b
        L_0x0056:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x005a:
            r0 = r3
        L_0x005b:
            org.telegram.ui.ActionBar.AlertDialog$Builder r10 = new org.telegram.ui.ActionBar.AlertDialog$Builder
            android.app.Activity r1 = r12.getParentActivity()
            r2 = r17
            r10.<init>(r1, r2)
            r1 = 2131626941(0x7f0e0bbd, float:1.8881132E38)
            java.lang.String r2 = "OpenUrlTitle"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r10.setTitle(r1)
            r1 = 2131626938(0x7f0e0bba, float:1.8881126E38)
            java.lang.String r2 = "OpenUrlAlert2"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            java.lang.String r2 = "%"
            int r2 = r1.indexOf(r2)
            android.text.SpannableStringBuilder r11 = new android.text.SpannableStringBuilder
            java.lang.Object[] r6 = new java.lang.Object[r6]
            r6[r9] = r0
            java.lang.String r1 = java.lang.String.format(r1, r6)
            r11.<init>(r1)
            if (r2 < 0) goto L_0x009f
            android.text.style.URLSpan r1 = new android.text.style.URLSpan
            r1.<init>(r0)
            int r0 = r0.length()
            int r0 = r0 + r2
            r6 = 33
            r11.setSpan(r1, r2, r0, r6)
        L_0x009f:
            r10.setMessage(r11)
            r10.setMessageTextViewClickable(r9)
            r0 = 2131626923(0x7f0e0bab, float:1.8881096E38)
            java.lang.String r1 = "Open"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            org.telegram.ui.Components.AlertsCreator$$ExternalSyntheticLambda27 r9 = new org.telegram.ui.Components.AlertsCreator$$ExternalSyntheticLambda27
            r1 = r9
            r2 = r12
            r3 = r13
            r6 = r15
            r1.<init>(r2, r3, r4, r6)
            r10.setPositiveButton(r0, r9)
            r0 = 2131624750(0x7f0e032e, float:1.8876689E38)
            java.lang.String r1 = "Cancel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            r10.setNegativeButton(r0, r8)
            org.telegram.ui.ActionBar.AlertDialog r0 = r10.create()
            r12.showDialog(r0)
            goto L_0x00dc
        L_0x00ce:
            android.app.Activity r0 = r12.getParentActivity()
            int r7 = (r4 > r1 ? 1 : (r4 == r1 ? 0 : -1))
            r1 = r15
            if (r7 != 0) goto L_0x00d8
            goto L_0x00d9
        L_0x00d8:
            r6 = 0
        L_0x00d9:
            org.telegram.messenger.browser.Browser.openUrl((android.content.Context) r0, (java.lang.String) r13, (boolean) r6, (boolean) r15)
        L_0x00dc:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.AlertsCreator.showOpenUrlAlert(org.telegram.ui.ActionBar.BaseFragment, java.lang.String, boolean, boolean, boolean, org.telegram.ui.ActionBar.Theme$ResourcesProvider):void");
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$showOpenUrlAlert$17(BaseFragment baseFragment, String str, long j, boolean z, DialogInterface dialogInterface, int i) {
        Browser.openUrl((Context) baseFragment.getParentActivity(), str, j == 0, z);
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
                    baseFragment.dismissCurrentDialog();
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
        builder.setPositiveButton(LocaleController.getString("AskButton", NUM), new AlertsCreator$$ExternalSyntheticLambda26(baseFragment));
        builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
        return builder.create();
    }

    /* access modifiers changed from: private */
    public static void performAskAQuestion(BaseFragment baseFragment) {
        String string;
        int currentAccount = baseFragment.getCurrentAccount();
        SharedPreferences mainSettings = MessagesController.getMainSettings(currentAccount);
        long prefIntOrLong = AndroidUtilities.getPrefIntOrLong(mainSettings, "support_id2", 0);
        TLRPC$User tLRPC$User = null;
        if (prefIntOrLong != 0) {
            TLRPC$User user = MessagesController.getInstance(currentAccount).getUser(Long.valueOf(prefIntOrLong));
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
            alertDialog.setCanCancel(false);
            alertDialog.show();
            ConnectionsManager.getInstance(currentAccount).sendRequest(new TLRPC$TL_help_getSupport(), new AlertsCreator$$ExternalSyntheticLambda93(mainSettings, alertDialog, currentAccount, baseFragment));
            return;
        }
        MessagesController.getInstance(currentAccount).putUser(tLRPC$User, true);
        Bundle bundle = new Bundle();
        bundle.putLong("user_id", tLRPC$User.id);
        baseFragment.presentFragment(new ChatActivity(bundle));
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$performAskAQuestion$21(SharedPreferences sharedPreferences, AlertDialog alertDialog, int i, BaseFragment baseFragment, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        if (tLRPC$TL_error == null) {
            AndroidUtilities.runOnUIThread(new AlertsCreator$$ExternalSyntheticLambda85(sharedPreferences, (TLRPC$TL_help_support) tLObject, alertDialog, i, baseFragment));
            return;
        }
        AndroidUtilities.runOnUIThread(new AlertsCreator$$ExternalSyntheticLambda86(alertDialog));
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$performAskAQuestion$19(SharedPreferences sharedPreferences, TLRPC$TL_help_support tLRPC$TL_help_support, AlertDialog alertDialog, int i, BaseFragment baseFragment) {
        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.putLong("support_id2", tLRPC$TL_help_support.user.id);
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
        bundle.putLong("user_id", tLRPC$TL_help_support.user.id);
        baseFragment.presentFragment(new ChatActivity(bundle));
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$performAskAQuestion$20(AlertDialog alertDialog) {
        try {
            alertDialog.dismiss();
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    public static void createImportDialogAlert(BaseFragment baseFragment, String str, String str2, TLRPC$User tLRPC$User, TLRPC$Chat tLRPC$Chat, Runnable runnable) {
        BaseFragment baseFragment2 = baseFragment;
        TLRPC$User tLRPC$User2 = tLRPC$User;
        TLRPC$Chat tLRPC$Chat2 = tLRPC$Chat;
        if (baseFragment2 != null && baseFragment.getParentActivity() != null) {
            if (tLRPC$Chat2 != null || tLRPC$User2 != null) {
                int currentAccount = baseFragment.getCurrentAccount();
                Activity parentActivity = baseFragment.getParentActivity();
                AlertDialog.Builder builder = new AlertDialog.Builder((Context) parentActivity);
                long clientUserId = UserConfig.getInstance(currentAccount).getClientUserId();
                TextView textView = new TextView(parentActivity);
                textView.setTextColor(Theme.getColor("dialogTextBlack"));
                textView.setTextSize(1, 16.0f);
                textView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
                FrameLayout frameLayout = new FrameLayout(parentActivity);
                builder.setView(frameLayout);
                AvatarDrawable avatarDrawable = new AvatarDrawable();
                avatarDrawable.setTextSize(AndroidUtilities.dp(12.0f));
                BackupImageView backupImageView = new BackupImageView(parentActivity);
                backupImageView.setRoundRadius(AndroidUtilities.dp(20.0f));
                frameLayout.addView(backupImageView, LayoutHelper.createFrame(40, 40.0f, (LocaleController.isRTL ? 5 : 3) | 48, 22.0f, 5.0f, 22.0f, 0.0f));
                TextView textView2 = new TextView(parentActivity);
                textView2.setTextColor(Theme.getColor("actionBarDefaultSubmenuItem"));
                textView2.setTextSize(1, 20.0f);
                textView2.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
                textView2.setLines(1);
                textView2.setMaxLines(1);
                textView2.setSingleLine(true);
                textView2.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
                textView2.setEllipsize(TextUtils.TruncateAt.END);
                textView2.setText(LocaleController.getString("ImportMessages", NUM));
                boolean z = LocaleController.isRTL;
                int i = (z ? 5 : 3) | 48;
                int i2 = 21;
                float f = (float) (z ? 21 : 76);
                if (z) {
                    i2 = 76;
                }
                frameLayout.addView(textView2, LayoutHelper.createFrame(-1, -2.0f, i, f, 11.0f, (float) i2, 0.0f));
                frameLayout.addView(textView, LayoutHelper.createFrame(-2, -2.0f, (LocaleController.isRTL ? 5 : 3) | 48, 24.0f, 57.0f, 24.0f, 9.0f));
                if (tLRPC$User2 == null) {
                    avatarDrawable.setInfo(tLRPC$Chat2);
                    backupImageView.setForUserOrChat(tLRPC$Chat2, avatarDrawable);
                } else if (UserObject.isReplyUser(tLRPC$User)) {
                    avatarDrawable.setSmallSize(true);
                    avatarDrawable.setAvatarType(12);
                    backupImageView.setImage((ImageLocation) null, (String) null, (Drawable) avatarDrawable, (Object) tLRPC$User2);
                } else if (tLRPC$User2.id == clientUserId) {
                    avatarDrawable.setSmallSize(true);
                    avatarDrawable.setAvatarType(1);
                    backupImageView.setImage((ImageLocation) null, (String) null, (Drawable) avatarDrawable, (Object) tLRPC$User2);
                } else {
                    avatarDrawable.setSmallSize(false);
                    avatarDrawable.setInfo(tLRPC$User2);
                    backupImageView.setForUserOrChat(tLRPC$User2, avatarDrawable);
                }
                textView.setText(AndroidUtilities.replaceTags(str2));
                builder.setPositiveButton(LocaleController.getString("Import", NUM), new AlertsCreator$$ExternalSyntheticLambda14(runnable));
                builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
                baseFragment2.showDialog(builder.create());
            }
        }
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$createImportDialogAlert$22(Runnable runnable, DialogInterface dialogInterface, int i) {
        if (runnable != null) {
            runnable.run();
        }
    }

    public static void createClearOrDeleteDialogAlert(BaseFragment baseFragment, boolean z, TLRPC$Chat tLRPC$Chat, TLRPC$User tLRPC$User, boolean z2, boolean z3, MessagesStorage.BooleanCallback booleanCallback) {
        createClearOrDeleteDialogAlert(baseFragment, z, false, false, tLRPC$Chat, tLRPC$User, z2, false, z3, booleanCallback, (Theme.ResourcesProvider) null);
    }

    public static void createClearOrDeleteDialogAlert(BaseFragment baseFragment, boolean z, TLRPC$Chat tLRPC$Chat, TLRPC$User tLRPC$User, boolean z2, boolean z3, boolean z4, MessagesStorage.BooleanCallback booleanCallback, Theme.ResourcesProvider resourcesProvider) {
        TLRPC$Chat tLRPC$Chat2 = tLRPC$Chat;
        createClearOrDeleteDialogAlert(baseFragment, z, tLRPC$Chat2 != null && tLRPC$Chat2.creator, false, tLRPC$Chat, tLRPC$User, z2, z3, z4, booleanCallback, resourcesProvider);
    }

    /* JADX WARNING: Removed duplicated region for block: B:104:0x0234  */
    /* JADX WARNING: Removed duplicated region for block: B:111:0x025e  */
    /* JADX WARNING: Removed duplicated region for block: B:116:0x0299  */
    /* JADX WARNING: Removed duplicated region for block: B:117:0x02a0  */
    /* JADX WARNING: Removed duplicated region for block: B:120:0x02ac  */
    /* JADX WARNING: Removed duplicated region for block: B:122:0x02d9  */
    /* JADX WARNING: Removed duplicated region for block: B:125:0x02e0  */
    /* JADX WARNING: Removed duplicated region for block: B:132:0x0317  */
    /* JADX WARNING: Removed duplicated region for block: B:134:0x0323  */
    /* JADX WARNING: Removed duplicated region for block: B:138:0x034d  */
    /* JADX WARNING: Removed duplicated region for block: B:185:0x04f2  */
    /* JADX WARNING: Removed duplicated region for block: B:187:0x04fe  */
    /* JADX WARNING: Removed duplicated region for block: B:208:0x05ab  */
    /* JADX WARNING: Removed duplicated region for block: B:212:? A[RETURN, SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:77:0x01dc  */
    /* JADX WARNING: Removed duplicated region for block: B:78:0x01e3  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static void createClearOrDeleteDialogAlert(org.telegram.ui.ActionBar.BaseFragment r36, boolean r37, boolean r38, boolean r39, org.telegram.tgnet.TLRPC$Chat r40, org.telegram.tgnet.TLRPC$User r41, boolean r42, boolean r43, boolean r44, org.telegram.messenger.MessagesStorage.BooleanCallback r45, org.telegram.ui.ActionBar.Theme.ResourcesProvider r46) {
        /*
            r14 = r36
            r8 = r40
            r4 = r41
            r12 = r46
            if (r14 == 0) goto L_0x05b4
            android.app.Activity r0 = r36.getParentActivity()
            if (r0 == 0) goto L_0x05b4
            if (r8 != 0) goto L_0x0016
            if (r4 != 0) goto L_0x0016
            goto L_0x05b4
        L_0x0016:
            int r0 = r36.getCurrentAccount()
            android.app.Activity r1 = r36.getParentActivity()
            org.telegram.ui.ActionBar.AlertDialog$Builder r15 = new org.telegram.ui.ActionBar.AlertDialog$Builder
            r15.<init>(r1, r12)
            org.telegram.messenger.UserConfig r2 = org.telegram.messenger.UserConfig.getInstance(r0)
            long r2 = r2.getClientUserId()
            r5 = 1
            org.telegram.ui.Cells.CheckBoxCell[] r6 = new org.telegram.ui.Cells.CheckBoxCell[r5]
            android.widget.TextView r7 = new android.widget.TextView
            r7.<init>(r1)
            java.lang.String r9 = "dialogTextBlack"
            int r9 = org.telegram.ui.ActionBar.Theme.getColor(r9)
            r7.setTextColor(r9)
            r9 = 1098907648(0x41800000, float:16.0)
            r7.setTextSize(r5, r9)
            boolean r10 = org.telegram.messenger.LocaleController.isRTL
            if (r10 == 0) goto L_0x0047
            r10 = 5
            goto L_0x0048
        L_0x0047:
            r10 = 3
        L_0x0048:
            r10 = r10 | 48
            r7.setGravity(r10)
            if (r44 != 0) goto L_0x005f
            boolean r16 = org.telegram.messenger.ChatObject.isChannel(r40)
            if (r16 == 0) goto L_0x005f
            java.lang.String r11 = r8.username
            boolean r11 = android.text.TextUtils.isEmpty(r11)
            if (r11 != 0) goto L_0x005f
            r11 = 1
            goto L_0x0060
        L_0x005f:
            r11 = 0
        L_0x0060:
            org.telegram.ui.Components.AlertsCreator$3 r13 = new org.telegram.ui.Components.AlertsCreator$3
            r13.<init>(r1, r6)
            r15.setView(r13)
            org.telegram.ui.Components.AvatarDrawable r9 = new org.telegram.ui.Components.AvatarDrawable
            r9.<init>()
            r18 = 1094713344(0x41400000, float:12.0)
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r18)
            r9.setTextSize(r10)
            org.telegram.ui.Components.BackupImageView r10 = new org.telegram.ui.Components.BackupImageView
            r10.<init>(r1)
            r5 = 1101004800(0x41a00000, float:20.0)
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r5)
            r10.setRoundRadius(r14)
            r19 = 40
            r20 = 1109393408(0x42200000, float:40.0)
            boolean r14 = org.telegram.messenger.LocaleController.isRTL
            if (r14 == 0) goto L_0x008e
            r14 = 5
            goto L_0x008f
        L_0x008e:
            r14 = 3
        L_0x008f:
            r21 = r14 | 48
            r22 = 1102053376(0x41b00000, float:22.0)
            r23 = 1084227584(0x40a00000, float:5.0)
            r24 = 1102053376(0x41b00000, float:22.0)
            r25 = 0
            android.widget.FrameLayout$LayoutParams r14 = org.telegram.ui.Components.LayoutHelper.createFrame(r19, r20, r21, r22, r23, r24, r25)
            r13.addView(r10, r14)
            android.widget.TextView r14 = new android.widget.TextView
            r14.<init>(r1)
            java.lang.String r19 = "actionBarDefaultSubmenuItem"
            r20 = r15
            int r15 = org.telegram.ui.ActionBar.Theme.getColor(r19)
            r14.setTextColor(r15)
            r15 = 1
            r14.setTextSize(r15, r5)
            java.lang.String r5 = "fonts/rmedium.ttf"
            android.graphics.Typeface r5 = org.telegram.messenger.AndroidUtilities.getTypeface(r5)
            r14.setTypeface(r5)
            r14.setLines(r15)
            r14.setMaxLines(r15)
            r14.setSingleLine(r15)
            boolean r5 = org.telegram.messenger.LocaleController.isRTL
            if (r5 == 0) goto L_0x00cc
            r5 = 5
            goto L_0x00cd
        L_0x00cc:
            r5 = 3
        L_0x00cd:
            r5 = r5 | 16
            r14.setGravity(r5)
            android.text.TextUtils$TruncateAt r5 = android.text.TextUtils.TruncateAt.END
            r14.setEllipsize(r5)
            java.lang.String r15 = "LeaveChannelMenu"
            java.lang.String r5 = "DeleteChatUser"
            r22 = r10
            java.lang.String r10 = "ClearHistoryCache"
            r24 = r9
            java.lang.String r9 = "ClearHistory"
            r26 = r6
            java.lang.String r6 = "LeaveMegaMenu"
            if (r37 == 0) goto L_0x010b
            if (r11 == 0) goto L_0x00fb
            r28 = r1
            r27 = r11
            r11 = 2131625053(0x7f0e045d, float:1.8877303E38)
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r10, r11)
            r14.setText(r1)
            goto L_0x0177
        L_0x00fb:
            r28 = r1
            r27 = r11
            r1 = 2131625052(0x7f0e045c, float:1.8877301E38)
            java.lang.String r11 = org.telegram.messenger.LocaleController.getString(r9, r1)
            r14.setText(r11)
            goto L_0x0177
        L_0x010b:
            r28 = r1
            r27 = r11
            if (r38 == 0) goto L_0x0140
            boolean r1 = org.telegram.messenger.ChatObject.isChannel(r40)
            java.lang.String r11 = "DeleteMegaMenu"
            if (r1 == 0) goto L_0x0135
            boolean r1 = r8.megagroup
            if (r1 == 0) goto L_0x0128
            r1 = 2131625314(0x7f0e0562, float:1.8877832E38)
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r11, r1)
            r14.setText(r1)
            goto L_0x0177
        L_0x0128:
            r1 = 2131624826(0x7f0e037a, float:1.8876843E38)
            java.lang.String r11 = "ChannelDeleteMenu"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r11, r1)
            r14.setText(r1)
            goto L_0x0177
        L_0x0135:
            r1 = 2131625314(0x7f0e0562, float:1.8877832E38)
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r11, r1)
            r14.setText(r1)
            goto L_0x0177
        L_0x0140:
            if (r8 == 0) goto L_0x016d
            boolean r1 = org.telegram.messenger.ChatObject.isChannel(r40)
            if (r1 == 0) goto L_0x0162
            boolean r1 = r8.megagroup
            if (r1 == 0) goto L_0x0157
            r1 = 2131626261(0x7f0e0915, float:1.8879753E38)
            java.lang.String r11 = org.telegram.messenger.LocaleController.getString(r6, r1)
            r14.setText(r11)
            goto L_0x0177
        L_0x0157:
            r11 = 2131626259(0x7f0e0913, float:1.887975E38)
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r15, r11)
            r14.setText(r1)
            goto L_0x0177
        L_0x0162:
            r1 = 2131626261(0x7f0e0915, float:1.8879753E38)
            java.lang.String r11 = org.telegram.messenger.LocaleController.getString(r6, r1)
            r14.setText(r11)
            goto L_0x0177
        L_0x016d:
            r1 = 2131625293(0x7f0e054d, float:1.887779E38)
            java.lang.String r11 = org.telegram.messenger.LocaleController.getString(r5, r1)
            r14.setText(r11)
        L_0x0177:
            r29 = -1
            r30 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            boolean r1 = org.telegram.messenger.LocaleController.isRTL
            if (r1 == 0) goto L_0x0181
            r11 = 5
            goto L_0x0182
        L_0x0181:
            r11 = 3
        L_0x0182:
            r31 = r11 | 48
            if (r1 == 0) goto L_0x018b
            r32 = 21
            r11 = 21
            goto L_0x018d
        L_0x018b:
            r11 = 76
        L_0x018d:
            float r11 = (float) r11
            r33 = 1093664768(0x41300000, float:11.0)
            if (r1 == 0) goto L_0x0195
            r1 = 76
            goto L_0x0197
        L_0x0195:
            r1 = 21
        L_0x0197:
            float r1 = (float) r1
            r35 = 0
            r32 = r11
            r34 = r1
            android.widget.FrameLayout$LayoutParams r1 = org.telegram.ui.Components.LayoutHelper.createFrame(r29, r30, r31, r32, r33, r34, r35)
            r13.addView(r14, r1)
            r29 = -2
            r30 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            boolean r1 = org.telegram.messenger.LocaleController.isRTL
            if (r1 == 0) goto L_0x01af
            r11 = 5
            goto L_0x01b0
        L_0x01af:
            r11 = 3
        L_0x01b0:
            r31 = r11 | 48
            r32 = 1103101952(0x41CLASSNAME, float:24.0)
            r33 = 1113849856(0x42640000, float:57.0)
            r34 = 1103101952(0x41CLASSNAME, float:24.0)
            r35 = 1091567616(0x41100000, float:9.0)
            android.widget.FrameLayout$LayoutParams r1 = org.telegram.ui.Components.LayoutHelper.createFrame(r29, r30, r31, r32, r33, r34, r35)
            r13.addView(r7, r1)
            if (r4 == 0) goto L_0x01d8
            boolean r1 = r4.bot
            if (r1 != 0) goto L_0x01d8
            r1 = r15
            long r14 = r4.id
            int r11 = (r14 > r2 ? 1 : (r14 == r2 ? 0 : -1))
            if (r11 == 0) goto L_0x01d9
            org.telegram.messenger.MessagesController r11 = org.telegram.messenger.MessagesController.getInstance(r0)
            boolean r11 = r11.canRevokePmInbox
            if (r11 == 0) goto L_0x01d9
            r11 = 1
            goto L_0x01da
        L_0x01d8:
            r1 = r15
        L_0x01d9:
            r11 = 0
        L_0x01da:
            if (r4 == 0) goto L_0x01e3
            org.telegram.messenger.MessagesController r0 = org.telegram.messenger.MessagesController.getInstance(r0)
            int r0 = r0.revokeTimePmLimit
            goto L_0x01e9
        L_0x01e3:
            org.telegram.messenger.MessagesController r0 = org.telegram.messenger.MessagesController.getInstance(r0)
            int r0 = r0.revokeTimeLimit
        L_0x01e9:
            if (r42 != 0) goto L_0x01f6
            if (r4 == 0) goto L_0x01f6
            if (r11 == 0) goto L_0x01f6
            r11 = 2147483647(0x7fffffff, float:NaN)
            if (r0 != r11) goto L_0x01f6
            r0 = 1
            goto L_0x01f7
        L_0x01f6:
            r0 = 0
        L_0x01f7:
            r11 = 1
            boolean[] r14 = new boolean[r11]
            if (r39 != 0) goto L_0x020b
            if (r42 == 0) goto L_0x0200
            if (r37 == 0) goto L_0x0202
        L_0x0200:
            if (r0 == 0) goto L_0x020b
        L_0x0202:
            boolean r0 = org.telegram.messenger.UserObject.isDeleted(r41)
            if (r0 == 0) goto L_0x0209
            goto L_0x020b
        L_0x0209:
            r0 = 0
            goto L_0x021a
        L_0x020b:
            if (r43 == 0) goto L_0x0217
            if (r37 != 0) goto L_0x0217
            if (r8 == 0) goto L_0x0217
            boolean r0 = r8.creator
            if (r0 == 0) goto L_0x0217
            r0 = 1
            goto L_0x0218
        L_0x0217:
            r0 = 0
        L_0x0218:
            if (r0 == 0) goto L_0x02d9
        L_0x021a:
            org.telegram.ui.Cells.CheckBoxCell r11 = new org.telegram.ui.Cells.CheckBoxCell
            r16 = r5
            r15 = r28
            r5 = 1
            r11.<init>(r15, r5, r12)
            r5 = 0
            r26[r5] = r11
            r11 = r26[r5]
            android.graphics.drawable.Drawable r15 = org.telegram.ui.ActionBar.Theme.getSelectorDrawable(r5)
            r11.setBackgroundDrawable(r15)
            java.lang.String r11 = ""
            if (r0 == 0) goto L_0x025e
            boolean r0 = org.telegram.messenger.ChatObject.isChannel(r40)
            if (r0 == 0) goto L_0x024d
            boolean r0 = r8.megagroup
            if (r0 != 0) goto L_0x024d
            r0 = r26[r5]
            r15 = 2131625290(0x7f0e054a, float:1.8877784E38)
            java.lang.String r12 = "DeleteChannelForAll"
            java.lang.String r12 = org.telegram.messenger.LocaleController.getString(r12, r15)
            r0.setText(r12, r11, r5, r5)
            goto L_0x025b
        L_0x024d:
            r0 = r26[r5]
            r12 = 2131625307(0x7f0e055b, float:1.8877818E38)
            java.lang.String r15 = "DeleteGroupForAll"
            java.lang.String r12 = org.telegram.messenger.LocaleController.getString(r15, r12)
            r0.setText(r12, r11, r5, r5)
        L_0x025b:
            r28 = r1
            goto L_0x0293
        L_0x025e:
            if (r37 == 0) goto L_0x027a
            r0 = r26[r5]
            r15 = 1
            java.lang.Object[] r12 = new java.lang.Object[r15]
            java.lang.String r18 = org.telegram.messenger.UserObject.getFirstName(r41)
            r12[r5] = r18
            java.lang.String r15 = "ClearHistoryOptionAlso"
            r28 = r1
            r1 = 2131625056(0x7f0e0460, float:1.887731E38)
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r15, r1, r12)
            r0.setText(r1, r11, r5, r5)
            goto L_0x0293
        L_0x027a:
            r28 = r1
            r0 = r26[r5]
            r1 = 2131625316(0x7f0e0564, float:1.8877837E38)
            r12 = 1
            java.lang.Object[] r15 = new java.lang.Object[r12]
            java.lang.String r12 = org.telegram.messenger.UserObject.getFirstName(r41)
            r15[r5] = r12
            java.lang.String r12 = "DeleteMessagesOptionAlso"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r12, r1, r15)
            r0.setText(r1, r11, r5, r5)
        L_0x0293:
            r0 = r26[r5]
            boolean r1 = org.telegram.messenger.LocaleController.isRTL
            if (r1 == 0) goto L_0x02a0
            r1 = 1098907648(0x41800000, float:16.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r1)
            goto L_0x02a8
        L_0x02a0:
            r1 = 1098907648(0x41800000, float:16.0)
            r5 = 1090519040(0x41000000, float:8.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
        L_0x02a8:
            boolean r11 = org.telegram.messenger.LocaleController.isRTL
            if (r11 == 0) goto L_0x02ae
            r1 = 1090519040(0x41000000, float:8.0)
        L_0x02ae:
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            r11 = 0
            r0.setPadding(r5, r11, r1, r11)
            r0 = r26[r11]
            r29 = -1
            r30 = 1111490560(0x42400000, float:48.0)
            r31 = 83
            r32 = 0
            r33 = 0
            r34 = 0
            r35 = 0
            android.widget.FrameLayout$LayoutParams r1 = org.telegram.ui.Components.LayoutHelper.createFrame(r29, r30, r31, r32, r33, r34, r35)
            r13.addView(r0, r1)
            r0 = 0
            r1 = r26[r0]
            org.telegram.ui.Components.AlertsCreator$$ExternalSyntheticLambda69 r0 = new org.telegram.ui.Components.AlertsCreator$$ExternalSyntheticLambda69
            r0.<init>(r14)
            r1.setOnClickListener(r0)
            goto L_0x02dd
        L_0x02d9:
            r28 = r1
            r16 = r5
        L_0x02dd:
            r15 = 0
            if (r4 == 0) goto L_0x0317
            boolean r0 = org.telegram.messenger.UserObject.isReplyUser((org.telegram.tgnet.TLRPC$User) r41)
            if (r0 == 0) goto L_0x02f7
            r0 = r24
            r1 = 1
            r0.setSmallSize(r1)
            r5 = 12
            r0.setAvatarType(r5)
            r5 = r22
            r5.setImage((org.telegram.messenger.ImageLocation) r15, (java.lang.String) r15, (android.graphics.drawable.Drawable) r0, (java.lang.Object) r4)
            goto L_0x0321
        L_0x02f7:
            r5 = r22
            r0 = r24
            r1 = 1
            long r11 = r4.id
            int r13 = (r11 > r2 ? 1 : (r11 == r2 ? 0 : -1))
            if (r13 != 0) goto L_0x030c
            r0.setSmallSize(r1)
            r0.setAvatarType(r1)
            r5.setImage((org.telegram.messenger.ImageLocation) r15, (java.lang.String) r15, (android.graphics.drawable.Drawable) r0, (java.lang.Object) r4)
            goto L_0x0321
        L_0x030c:
            r1 = 0
            r0.setSmallSize(r1)
            r0.setInfo((org.telegram.tgnet.TLRPC$User) r4)
            r5.setForUserOrChat(r4, r0)
            goto L_0x0321
        L_0x0317:
            r5 = r22
            r0 = r24
            r0.setInfo((org.telegram.tgnet.TLRPC$Chat) r8)
            r5.setForUserOrChat(r8, r0)
        L_0x0321:
            if (r39 == 0) goto L_0x034d
            boolean r0 = org.telegram.messenger.UserObject.isUserSelf(r41)
            if (r0 == 0) goto L_0x033b
            r0 = 2131625275(0x7f0e053b, float:1.8877753E38)
            java.lang.String r1 = "DeleteAllMessagesSavedAlert"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            r7.setText(r0)
            goto L_0x04f0
        L_0x033b:
            r0 = 2131625274(0x7f0e053a, float:1.8877751E38)
            java.lang.String r1 = "DeleteAllMessagesAlert"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            r7.setText(r0)
            goto L_0x04f0
        L_0x034d:
            if (r37 == 0) goto L_0x03f0
            if (r4 == 0) goto L_0x03a3
            if (r42 == 0) goto L_0x036f
            r0 = 2131624373(0x7f0e01b5, float:1.8875924E38)
            r1 = 1
            java.lang.Object[] r1 = new java.lang.Object[r1]
            java.lang.String r2 = org.telegram.messenger.UserObject.getUserName(r41)
            r3 = 0
            r1[r3] = r2
            java.lang.String r2 = "AreYouSureClearHistoryWithSecretUser"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            r7.setText(r0)
            goto L_0x04f0
        L_0x036f:
            long r0 = r4.id
            int r5 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r5 != 0) goto L_0x0387
            r0 = 2131624371(0x7f0e01b3, float:1.887592E38)
            java.lang.String r1 = "AreYouSureClearHistorySavedMessages"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            r7.setText(r0)
            goto L_0x04f0
        L_0x0387:
            r0 = 2131624374(0x7f0e01b6, float:1.8875926E38)
            r1 = 1
            java.lang.Object[] r1 = new java.lang.Object[r1]
            java.lang.String r2 = org.telegram.messenger.UserObject.getUserName(r41)
            r3 = 0
            r1[r3] = r2
            java.lang.String r2 = "AreYouSureClearHistoryWithUser"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            r7.setText(r0)
            goto L_0x04f0
        L_0x03a3:
            boolean r0 = org.telegram.messenger.ChatObject.isChannel(r40)
            if (r0 == 0) goto L_0x03d6
            boolean r0 = r8.megagroup
            if (r0 == 0) goto L_0x03b6
            java.lang.String r0 = r8.username
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            if (r0 == 0) goto L_0x03b6
            goto L_0x03d6
        L_0x03b6:
            boolean r0 = r8.megagroup
            if (r0 == 0) goto L_0x03c8
            r0 = 2131624370(0x7f0e01b2, float:1.8875918E38)
            java.lang.String r1 = "AreYouSureClearHistoryGroup"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            r7.setText(r0)
            goto L_0x04f0
        L_0x03c8:
            r0 = 2131624368(0x7f0e01b0, float:1.8875914E38)
            java.lang.String r1 = "AreYouSureClearHistoryChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            r7.setText(r0)
            goto L_0x04f0
        L_0x03d6:
            r0 = 2131624372(0x7f0e01b4, float:1.8875922E38)
            r1 = 1
            java.lang.Object[] r1 = new java.lang.Object[r1]
            java.lang.String r2 = r8.title
            r3 = 0
            r1[r3] = r2
            java.lang.String r2 = "AreYouSureClearHistoryWithChat"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            r7.setText(r0)
            goto L_0x04f0
        L_0x03f0:
            if (r38 == 0) goto L_0x0426
            boolean r0 = org.telegram.messenger.ChatObject.isChannel(r40)
            if (r0 == 0) goto L_0x0418
            boolean r0 = r8.megagroup
            if (r0 == 0) goto L_0x040a
            r0 = 2131624375(0x7f0e01b7, float:1.8875928E38)
            java.lang.String r1 = "AreYouSureDeleteAndExit"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            r7.setText(r0)
            goto L_0x04f0
        L_0x040a:
            r0 = 2131624376(0x7f0e01b8, float:1.887593E38)
            java.lang.String r1 = "AreYouSureDeleteAndExitChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            r7.setText(r0)
            goto L_0x04f0
        L_0x0418:
            r0 = 2131624375(0x7f0e01b7, float:1.8875928E38)
            java.lang.String r1 = "AreYouSureDeleteAndExit"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            r7.setText(r0)
            goto L_0x04f0
        L_0x0426:
            if (r4 == 0) goto L_0x049c
            if (r42 == 0) goto L_0x0446
            r0 = 2131624394(0x7f0e01ca, float:1.8875966E38)
            r1 = 1
            java.lang.Object[] r1 = new java.lang.Object[r1]
            java.lang.String r2 = org.telegram.messenger.UserObject.getUserName(r41)
            r3 = 0
            r1[r3] = r2
            java.lang.String r2 = "AreYouSureDeleteThisChatWithSecretUser"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            r7.setText(r0)
            goto L_0x04f0
        L_0x0446:
            long r0 = r4.id
            int r5 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r5 != 0) goto L_0x045e
            r0 = 2131624391(0x7f0e01c7, float:1.887596E38)
            java.lang.String r1 = "AreYouSureDeleteThisChatSavedMessages"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            r7.setText(r0)
            goto L_0x04f0
        L_0x045e:
            boolean r0 = r4.bot
            if (r0 == 0) goto L_0x0481
            boolean r0 = r4.support
            if (r0 != 0) goto L_0x0481
            r0 = 2131624392(0x7f0e01c8, float:1.8875962E38)
            r1 = 1
            java.lang.Object[] r1 = new java.lang.Object[r1]
            java.lang.String r2 = org.telegram.messenger.UserObject.getUserName(r41)
            r3 = 0
            r1[r3] = r2
            java.lang.String r2 = "AreYouSureDeleteThisChatWithBot"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            r7.setText(r0)
            goto L_0x04f0
        L_0x0481:
            r1 = 1
            r3 = 0
            r0 = 2131624395(0x7f0e01cb, float:1.8875969E38)
            java.lang.Object[] r1 = new java.lang.Object[r1]
            java.lang.String r2 = org.telegram.messenger.UserObject.getUserName(r41)
            r1[r3] = r2
            java.lang.String r2 = "AreYouSureDeleteThisChatWithUser"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            r7.setText(r0)
            goto L_0x04f0
        L_0x049c:
            boolean r0 = org.telegram.messenger.ChatObject.isChannel(r40)
            if (r0 == 0) goto L_0x04d8
            boolean r0 = r8.megagroup
            if (r0 == 0) goto L_0x04bf
            r0 = 2131626422(0x7f0e09b6, float:1.888008E38)
            r1 = 1
            java.lang.Object[] r1 = new java.lang.Object[r1]
            java.lang.String r2 = r8.title
            r3 = 0
            r1[r3] = r2
            java.lang.String r2 = "MegaLeaveAlertWithName"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            r7.setText(r0)
            goto L_0x04f0
        L_0x04bf:
            r1 = 1
            r3 = 0
            r0 = 2131624838(0x7f0e0386, float:1.8876867E38)
            java.lang.Object[] r1 = new java.lang.Object[r1]
            java.lang.String r2 = r8.title
            r1[r3] = r2
            java.lang.String r2 = "ChannelLeaveAlertWithName"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            r7.setText(r0)
            goto L_0x04f0
        L_0x04d8:
            r1 = 1
            r3 = 0
            r0 = 2131624377(0x7f0e01b9, float:1.8875932E38)
            java.lang.Object[] r1 = new java.lang.Object[r1]
            java.lang.String r2 = r8.title
            r1[r3] = r2
            java.lang.String r2 = "AreYouSureDeleteAndExitName"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            r7.setText(r0)
        L_0x04f0:
            if (r39 == 0) goto L_0x04fe
            r0 = 2131625270(0x7f0e0536, float:1.8877743E38)
            java.lang.String r1 = "DeleteAll"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
        L_0x04fb:
            r13 = r0
            goto L_0x0562
        L_0x04fe:
            if (r37 == 0) goto L_0x0512
            if (r27 == 0) goto L_0x050a
            r0 = 2131625053(0x7f0e045d, float:1.8877303E38)
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r10, r0)
            goto L_0x04fb
        L_0x050a:
            r0 = 2131625052(0x7f0e045c, float:1.8877301E38)
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r9, r0)
            goto L_0x04fb
        L_0x0512:
            if (r38 == 0) goto L_0x053c
            boolean r0 = org.telegram.messenger.ChatObject.isChannel(r40)
            if (r0 == 0) goto L_0x0532
            boolean r0 = r8.megagroup
            if (r0 == 0) goto L_0x0528
            r0 = 2131625313(0x7f0e0561, float:1.887783E38)
            java.lang.String r1 = "DeleteMega"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            goto L_0x04fb
        L_0x0528:
            r0 = 2131624822(0x7f0e0376, float:1.8876835E38)
            java.lang.String r1 = "ChannelDelete"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            goto L_0x04fb
        L_0x0532:
            r0 = 2131625313(0x7f0e0561, float:1.887783E38)
            java.lang.String r1 = "DeleteMega"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            goto L_0x04fb
        L_0x053c:
            boolean r0 = org.telegram.messenger.ChatObject.isChannel(r40)
            if (r0 == 0) goto L_0x0558
            boolean r0 = r8.megagroup
            if (r0 == 0) goto L_0x054e
            r0 = 2131626261(0x7f0e0915, float:1.8879753E38)
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r6, r0)
            goto L_0x04fb
        L_0x054e:
            r1 = r28
            r0 = 2131626259(0x7f0e0913, float:1.887975E38)
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            goto L_0x04fb
        L_0x0558:
            r1 = r16
            r0 = 2131625293(0x7f0e054d, float:1.887779E38)
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            goto L_0x04fb
        L_0x0562:
            org.telegram.ui.Components.AlertsCreator$$ExternalSyntheticLambda34 r12 = new org.telegram.ui.Components.AlertsCreator$$ExternalSyntheticLambda34
            r0 = r12
            r1 = r27
            r2 = r39
            r3 = r42
            r4 = r41
            r5 = r36
            r6 = r37
            r7 = r38
            r8 = r40
            r9 = r43
            r10 = r44
            r11 = r45
            r15 = r12
            r12 = r46
            r17 = r15
            r15 = r13
            r13 = r14
            r0.<init>(r1, r2, r3, r4, r5, r6, r7, r8, r9, r10, r11, r12, r13)
            r1 = r17
            r0 = r20
            r0.setPositiveButton(r15, r1)
            r1 = 2131624750(0x7f0e032e, float:1.8876689E38)
            java.lang.String r2 = "Cancel"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r2 = 0
            r0.setNegativeButton(r1, r2)
            org.telegram.ui.ActionBar.AlertDialog r0 = r0.create()
            r1 = r36
            r1.showDialog(r0)
            r1 = -1
            android.view.View r0 = r0.getButton(r1)
            android.widget.TextView r0 = (android.widget.TextView) r0
            if (r0 == 0) goto L_0x05b4
            java.lang.String r1 = "dialogTextRed2"
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r1)
            r0.setTextColor(r1)
        L_0x05b4:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.AlertsCreator.createClearOrDeleteDialogAlert(org.telegram.ui.ActionBar.BaseFragment, boolean, boolean, boolean, org.telegram.tgnet.TLRPC$Chat, org.telegram.tgnet.TLRPC$User, boolean, boolean, boolean, org.telegram.messenger.MessagesStorage$BooleanCallback, org.telegram.ui.ActionBar.Theme$ResourcesProvider):void");
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$createClearOrDeleteDialogAlert$23(boolean[] zArr, View view) {
        zArr[0] = !zArr[0];
        ((CheckBoxCell) view).setChecked(zArr[0], true);
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$createClearOrDeleteDialogAlert$25(boolean z, boolean z2, boolean z3, TLRPC$User tLRPC$User, BaseFragment baseFragment, boolean z4, boolean z5, TLRPC$Chat tLRPC$Chat, boolean z6, boolean z7, MessagesStorage.BooleanCallback booleanCallback, Theme.ResourcesProvider resourcesProvider, boolean[] zArr, DialogInterface dialogInterface, int i) {
        TLRPC$User tLRPC$User2 = tLRPC$User;
        MessagesStorage.BooleanCallback booleanCallback2 = booleanCallback;
        boolean z8 = false;
        if (!z && !z2 && !z3) {
            if (UserObject.isUserSelf(tLRPC$User)) {
                createClearOrDeleteDialogAlert(baseFragment, z4, z5, true, tLRPC$Chat, tLRPC$User, false, z6, z7, booleanCallback, resourcesProvider);
                return;
            } else if (tLRPC$User2 != null && zArr[0]) {
                MessagesStorage.getInstance(baseFragment.getCurrentAccount()).getMessagesCount(tLRPC$User2.id, new AlertsCreator$$ExternalSyntheticLambda92(baseFragment, z4, z5, tLRPC$Chat, tLRPC$User, z6, z7, booleanCallback, resourcesProvider, zArr));
                return;
            }
        }
        if (booleanCallback2 != null) {
            if (z2 || zArr[0]) {
                z8 = true;
            }
            booleanCallback2.run(z8);
        }
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$createClearOrDeleteDialogAlert$24(BaseFragment baseFragment, boolean z, boolean z2, TLRPC$Chat tLRPC$Chat, TLRPC$User tLRPC$User, boolean z3, boolean z4, MessagesStorage.BooleanCallback booleanCallback, Theme.ResourcesProvider resourcesProvider, boolean[] zArr, int i) {
        MessagesStorage.BooleanCallback booleanCallback2 = booleanCallback;
        if (i >= 50) {
            createClearOrDeleteDialogAlert(baseFragment, z, z2, true, tLRPC$Chat, tLRPC$User, false, z3, z4, booleanCallback, resourcesProvider);
        } else if (booleanCallback2 != null) {
            booleanCallback2.run(zArr[0]);
        }
    }

    public static void createClearDaysDialogAlert(BaseFragment baseFragment, int i, TLRPC$User tLRPC$User, TLRPC$Chat tLRPC$Chat, boolean z, MessagesStorage.BooleanCallback booleanCallback, Theme.ResourcesProvider resourcesProvider) {
        int i2;
        float f;
        BaseFragment baseFragment2 = baseFragment;
        int i3 = i;
        TLRPC$User tLRPC$User2 = tLRPC$User;
        TLRPC$Chat tLRPC$Chat2 = tLRPC$Chat;
        Theme.ResourcesProvider resourcesProvider2 = resourcesProvider;
        if (baseFragment2 != null && baseFragment.getParentActivity() != null) {
            if (tLRPC$User2 != null || tLRPC$Chat2 != null) {
                int currentAccount = baseFragment.getCurrentAccount();
                Activity parentActivity = baseFragment.getParentActivity();
                AlertDialog.Builder builder = new AlertDialog.Builder(parentActivity, resourcesProvider2);
                long clientUserId = UserConfig.getInstance(currentAccount).getClientUserId();
                final CheckBoxCell[] checkBoxCellArr = new CheckBoxCell[1];
                TextView textView = new TextView(parentActivity);
                textView.setTextColor(Theme.getColor("dialogTextBlack"));
                textView.setTextSize(1, 16.0f);
                textView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
                AnonymousClass4 r13 = new FrameLayout(parentActivity) {
                    /* access modifiers changed from: protected */
                    public void onMeasure(int i, int i2) {
                        super.onMeasure(i, i2);
                        if (checkBoxCellArr[0] != null) {
                            setMeasuredDimension(getMeasuredWidth(), getMeasuredHeight() + checkBoxCellArr[0].getMeasuredHeight());
                        }
                    }
                };
                builder.setView(r13);
                TextView textView2 = new TextView(parentActivity);
                textView2.setTextColor(Theme.getColor("actionBarDefaultSubmenuItem"));
                textView2.setTextSize(1, 20.0f);
                textView2.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
                textView2.setLines(1);
                textView2.setMaxLines(1);
                textView2.setSingleLine(true);
                textView2.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
                textView2.setEllipsize(TextUtils.TruncateAt.END);
                r13.addView(textView2, LayoutHelper.createFrame(-1, -2.0f, (LocaleController.isRTL ? 5 : 3) | 48, 24.0f, 11.0f, 24.0f, 0.0f));
                r13.addView(textView, LayoutHelper.createFrame(-2, -2.0f, (LocaleController.isRTL ? 5 : 3) | 48, 24.0f, 48.0f, 24.0f, 18.0f));
                if (i3 == -1) {
                    textView2.setText(LocaleController.formatString("ClearHistory", NUM, new Object[0]));
                    if (tLRPC$User2 != null) {
                        textView.setText(AndroidUtilities.replaceTags(LocaleController.formatString("AreYouSureClearHistoryWithUser", NUM, UserObject.getUserName(tLRPC$User))));
                    } else if (tLRPC$Chat2 != null && z) {
                        textView.setText(AndroidUtilities.replaceTags(LocaleController.formatString("AreYouSureClearHistoryWithChat", NUM, tLRPC$Chat2.title)));
                    } else if (tLRPC$Chat2.megagroup) {
                        textView.setText(LocaleController.getString("AreYouSureClearHistoryGroup", NUM));
                    } else {
                        textView.setText(LocaleController.getString("AreYouSureClearHistoryChannel", NUM));
                    }
                } else {
                    textView2.setText(LocaleController.formatPluralString("DeleteDays", i3));
                    textView.setText(LocaleController.getString("DeleteHistoryByDaysMessage", NUM));
                }
                boolean[] zArr = {false};
                if (tLRPC$Chat2 != null && z && !TextUtils.isEmpty(tLRPC$Chat2.username)) {
                    zArr[0] = true;
                }
                if (!(tLRPC$User2 == null || tLRPC$User2.id == clientUserId) || (tLRPC$Chat2 != null && z && TextUtils.isEmpty(tLRPC$Chat2.username))) {
                    checkBoxCellArr[0] = new CheckBoxCell(parentActivity, 1, resourcesProvider2);
                    checkBoxCellArr[0].setBackgroundDrawable(Theme.getSelectorDrawable(false));
                    if (tLRPC$Chat2 != null) {
                        checkBoxCellArr[0].setText(LocaleController.getString("DeleteMessagesOptionAlsoChat", NUM), "", false, false);
                    } else {
                        checkBoxCellArr[0].setText(LocaleController.formatString("DeleteMessagesOptionAlso", NUM, UserObject.getFirstName(tLRPC$User)), "", false, false);
                    }
                    CheckBoxCell checkBoxCell = checkBoxCellArr[0];
                    if (LocaleController.isRTL) {
                        f = 16.0f;
                        i2 = AndroidUtilities.dp(16.0f);
                    } else {
                        f = 16.0f;
                        i2 = AndroidUtilities.dp(8.0f);
                    }
                    checkBoxCell.setPadding(i2, 0, LocaleController.isRTL ? AndroidUtilities.dp(8.0f) : AndroidUtilities.dp(f), 0);
                    r13.addView(checkBoxCellArr[0], LayoutHelper.createFrame(-1, 48.0f, 83, 0.0f, 0.0f, 0.0f, 0.0f));
                    checkBoxCellArr[0].setChecked(false, false);
                    checkBoxCellArr[0].setOnClickListener(new AlertsCreator$$ExternalSyntheticLambda70(zArr));
                }
                builder.setPositiveButton(LocaleController.getString("Delete", NUM), new AlertsCreator$$ExternalSyntheticLambda18(booleanCallback, zArr));
                builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
                AlertDialog create = builder.create();
                baseFragment2.showDialog(create);
                TextView textView3 = (TextView) create.getButton(-1);
                if (textView3 != null) {
                    textView3.setTextColor(Theme.getColor("dialogTextRed2"));
                }
            }
        }
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$createClearDaysDialogAlert$26(boolean[] zArr, View view) {
        zArr[0] = !zArr[0];
        ((CheckBoxCell) view).setChecked(zArr[0], true);
    }

    public static void createCallDialogAlert(BaseFragment baseFragment, TLRPC$User tLRPC$User, boolean z) {
        String str;
        String str2;
        BaseFragment baseFragment2 = baseFragment;
        TLRPC$User tLRPC$User2 = tLRPC$User;
        boolean z2 = z;
        if (baseFragment2 != null && baseFragment.getParentActivity() != null && tLRPC$User2 != null && !UserObject.isDeleted(tLRPC$User) && UserConfig.getInstance(baseFragment.getCurrentAccount()).getClientUserId() != tLRPC$User2.id) {
            baseFragment.getCurrentAccount();
            Activity parentActivity = baseFragment.getParentActivity();
            FrameLayout frameLayout = new FrameLayout(parentActivity);
            if (z2) {
                str2 = LocaleController.getString("VideoCallAlertTitle", NUM);
                str = LocaleController.formatString("VideoCallAlert", NUM, UserObject.getUserName(tLRPC$User));
            } else {
                str2 = LocaleController.getString("CallAlertTitle", NUM);
                str = LocaleController.formatString("CallAlert", NUM, UserObject.getUserName(tLRPC$User));
            }
            TextView textView = new TextView(parentActivity);
            textView.setTextColor(Theme.getColor("dialogTextBlack"));
            textView.setTextSize(1, 16.0f);
            int i = 5;
            textView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
            textView.setText(AndroidUtilities.replaceTags(str));
            AvatarDrawable avatarDrawable = new AvatarDrawable();
            avatarDrawable.setTextSize(AndroidUtilities.dp(12.0f));
            avatarDrawable.setSmallSize(false);
            avatarDrawable.setInfo(tLRPC$User2);
            BackupImageView backupImageView = new BackupImageView(parentActivity);
            backupImageView.setRoundRadius(AndroidUtilities.dp(20.0f));
            backupImageView.setForUserOrChat(tLRPC$User2, avatarDrawable);
            frameLayout.addView(backupImageView, LayoutHelper.createFrame(40, 40.0f, (LocaleController.isRTL ? 5 : 3) | 48, 22.0f, 5.0f, 22.0f, 0.0f));
            TextView textView2 = new TextView(parentActivity);
            textView2.setTextColor(Theme.getColor("actionBarDefaultSubmenuItem"));
            textView2.setTextSize(1, 20.0f);
            textView2.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            textView2.setLines(1);
            textView2.setMaxLines(1);
            textView2.setSingleLine(true);
            textView2.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
            textView2.setEllipsize(TextUtils.TruncateAt.END);
            textView2.setText(str2);
            boolean z3 = LocaleController.isRTL;
            int i2 = (z3 ? 5 : 3) | 48;
            int i3 = 21;
            float f = (float) (z3 ? 21 : 76);
            if (z3) {
                i3 = 76;
            }
            frameLayout.addView(textView2, LayoutHelper.createFrame(-1, -2.0f, i2, f, 11.0f, (float) i3, 0.0f));
            if (!LocaleController.isRTL) {
                i = 3;
            }
            frameLayout.addView(textView, LayoutHelper.createFrame(-2, -2.0f, i | 48, 24.0f, 57.0f, 24.0f, 9.0f));
            baseFragment2.showDialog(new AlertDialog.Builder((Context) parentActivity).setView(frameLayout).setPositiveButton(LocaleController.getString("Call", NUM), new AlertsCreator$$ExternalSyntheticLambda28(baseFragment2, tLRPC$User2, z2)).setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null).create());
        }
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$createCallDialogAlert$28(BaseFragment baseFragment, TLRPC$User tLRPC$User, boolean z, DialogInterface dialogInterface, int i) {
        TLRPC$UserFull userFull = baseFragment.getMessagesController().getUserFull(tLRPC$User.id);
        VoIPHelper.startCall(tLRPC$User, z, userFull != null && userFull.video_calls_available, baseFragment.getParentActivity(), userFull, baseFragment.getAccountInstance());
    }

    public static void createChangeBioAlert(String str, long j, Context context, int i) {
        String str2;
        int i2;
        long j2 = j;
        final Context context2 = context;
        AlertDialog.Builder builder = new AlertDialog.Builder(context2);
        builder.setTitle(j2 > 0 ? LocaleController.getString("UserBio", NUM) : LocaleController.getString("DescriptionPlaceholder", NUM));
        if (j2 > 0) {
            i2 = NUM;
            str2 = "VoipGroupBioEditAlertText";
        } else {
            i2 = NUM;
            str2 = "DescriptionInfo";
        }
        builder.setMessage(LocaleController.getString(str2, i2));
        FrameLayout frameLayout = new FrameLayout(context2);
        frameLayout.setClipChildren(false);
        if (j2 < 0) {
            long j3 = -j2;
            if (MessagesController.getInstance(i).getChatFull(j3) == null) {
                MessagesController.getInstance(i).loadFullChat(j3, ConnectionsManager.generateClassGuid(), true);
            }
        }
        final NumberTextView numberTextView = new NumberTextView(context2);
        EditText editText = new EditText(context2);
        editText.setTextColor(Theme.getColor("voipgroup_actionBarItems"));
        editText.setHint(j2 > 0 ? LocaleController.getString("UserBio", NUM) : LocaleController.getString("DescriptionPlaceholder", NUM));
        editText.setTextSize(1, 16.0f);
        editText.setBackground(Theme.createEditTextDrawable(context2, true));
        editText.setMaxLines(4);
        editText.setRawInputType(147457);
        editText.setImeOptions(6);
        InputFilter[] inputFilterArr = new InputFilter[1];
        final int i3 = j2 > 0 ? 70 : 255;
        inputFilterArr[0] = new CodepointsLengthInputFilter(i3) {
            public CharSequence filter(CharSequence charSequence, int i, int i2, Spanned spanned, int i3, int i4) {
                CharSequence filter = super.filter(charSequence, i, i2, spanned, i3, i4);
                if (!(filter == null || charSequence == null || filter.length() == charSequence.length())) {
                    Vibrator vibrator = (Vibrator) context2.getSystemService("vibrator");
                    if (vibrator != null) {
                        vibrator.vibrate(200);
                    }
                    AndroidUtilities.shakeView(numberTextView, 2.0f, 0);
                }
                return filter;
            }
        };
        editText.setFilters(inputFilterArr);
        numberTextView.setCenterAlign(true);
        numberTextView.setTextSize(15);
        numberTextView.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText4"));
        numberTextView.setImportantForAccessibility(2);
        frameLayout.addView(numberTextView, LayoutHelper.createFrame(20, 20.0f, LocaleController.isRTL ? 3 : 5, 0.0f, 14.0f, 21.0f, 0.0f));
        editText.setPadding(LocaleController.isRTL ? AndroidUtilities.dp(24.0f) : 0, AndroidUtilities.dp(8.0f), LocaleController.isRTL ? 0 : AndroidUtilities.dp(24.0f), AndroidUtilities.dp(8.0f));
        editText.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            public void afterTextChanged(Editable editable) {
                boolean z = false;
                int codePointCount = i3 - Character.codePointCount(editable, 0, editable.length());
                if (codePointCount < 30) {
                    NumberTextView numberTextView = numberTextView;
                    if (numberTextView.getVisibility() == 0) {
                        z = true;
                    }
                    numberTextView.setNumber(codePointCount, z);
                    AndroidUtilities.updateViewVisibilityAnimated(numberTextView, true);
                    return;
                }
                AndroidUtilities.updateViewVisibilityAnimated(numberTextView, false);
            }
        });
        AndroidUtilities.updateViewVisibilityAnimated(numberTextView, false, 0.0f, false);
        editText.setText(str);
        editText.setSelection(editText.getText().toString().length());
        builder.setView(frameLayout);
        AlertsCreator$$ExternalSyntheticLambda1 alertsCreator$$ExternalSyntheticLambda1 = new AlertsCreator$$ExternalSyntheticLambda1(j2, i, editText);
        builder.setPositiveButton(LocaleController.getString("Save", NUM), alertsCreator$$ExternalSyntheticLambda1);
        builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
        builder.setOnPreDismissListener(new AlertsCreator$$ExternalSyntheticLambda39(editText));
        frameLayout.addView(editText, LayoutHelper.createFrame(-1, -2.0f, 0, 23.0f, 12.0f, 23.0f, 21.0f));
        editText.requestFocus();
        AndroidUtilities.showKeyboard(editText);
        AlertDialog create = builder.create();
        editText.setOnEditorActionListener(new AlertsCreator$$ExternalSyntheticLambda82(j2, create, alertsCreator$$ExternalSyntheticLambda1));
        create.setBackgroundColor(Theme.getColor("voipgroup_dialogBackground"));
        create.show();
        create.setTextColor(Theme.getColor("voipgroup_actionBarItems"));
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$createChangeBioAlert$30(long j, int i, EditText editText, DialogInterface dialogInterface, int i2) {
        long j2 = j;
        String str = "";
        if (j2 > 0) {
            TLRPC$UserFull userFull = MessagesController.getInstance(i).getUserFull(UserConfig.getInstance(i).getClientUserId());
            String trim = editText.getText().toString().replace("\n", " ").replaceAll(" +", " ").trim();
            if (userFull != null) {
                String str2 = userFull.about;
                if (str2 != null) {
                    str = str2;
                }
                if (str.equals(trim)) {
                    AndroidUtilities.hideKeyboard(editText);
                    dialogInterface.dismiss();
                    return;
                }
                userFull.about = trim;
                NotificationCenter.getInstance(i).postNotificationName(NotificationCenter.userInfoDidLoad, Long.valueOf(j), userFull);
            }
            TLRPC$TL_account_updateProfile tLRPC$TL_account_updateProfile = new TLRPC$TL_account_updateProfile();
            tLRPC$TL_account_updateProfile.about = trim;
            tLRPC$TL_account_updateProfile.flags = 4 | tLRPC$TL_account_updateProfile.flags;
            NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.showBulletin, 2, Long.valueOf(j));
            ConnectionsManager.getInstance(i).sendRequest(tLRPC$TL_account_updateProfile, AlertsCreator$$ExternalSyntheticLambda97.INSTANCE, 2);
        } else {
            long j3 = -j2;
            TLRPC$ChatFull chatFull = MessagesController.getInstance(i).getChatFull(j3);
            String obj = editText.getText().toString();
            if (chatFull != null) {
                String str3 = chatFull.about;
                if (str3 != null) {
                    str = str3;
                }
                if (str.equals(obj)) {
                    AndroidUtilities.hideKeyboard(editText);
                    dialogInterface.dismiss();
                    return;
                }
                chatFull.about = obj;
                NotificationCenter instance = NotificationCenter.getInstance(i);
                int i3 = NotificationCenter.chatInfoDidLoad;
                Boolean bool = Boolean.FALSE;
                instance.postNotificationName(i3, chatFull, 0, bool, bool);
            }
            NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.showBulletin, 2, Long.valueOf(j));
            MessagesController.getInstance(i).updateChatAbout(j3, obj, chatFull);
        }
        dialogInterface.dismiss();
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$createChangeBioAlert$32(long j, AlertDialog alertDialog, DialogInterface.OnClickListener onClickListener, TextView textView, int i, KeyEvent keyEvent) {
        if ((i != 6 && (j <= 0 || keyEvent.getKeyCode() != 66)) || !alertDialog.isShowing()) {
            return false;
        }
        onClickListener.onClick(alertDialog, 0);
        return true;
    }

    public static void createChangeNameAlert(long j, Context context, int i) {
        String str;
        String str2;
        String str3;
        int i2;
        String str4;
        int i3;
        EditText editText;
        long j2 = j;
        Context context2 = context;
        if (DialogObject.isUserDialog(j)) {
            TLRPC$User user = MessagesController.getInstance(i).getUser(Long.valueOf(j));
            str = user.first_name;
            str2 = user.last_name;
        } else {
            str = MessagesController.getInstance(i).getChat(Long.valueOf(-j2)).title;
            str2 = null;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(context2);
        if (j2 > 0) {
            i2 = NUM;
            str3 = "VoipEditName";
        } else {
            i2 = NUM;
            str3 = "VoipEditTitle";
        }
        builder.setTitle(LocaleController.getString(str3, i2));
        LinearLayout linearLayout = new LinearLayout(context2);
        linearLayout.setOrientation(1);
        EditText editText2 = new EditText(context2);
        editText2.setTextColor(Theme.getColor("voipgroup_actionBarItems"));
        editText2.setTextSize(1, 16.0f);
        editText2.setMaxLines(1);
        editText2.setLines(1);
        editText2.setSingleLine(true);
        editText2.setGravity(LocaleController.isRTL ? 5 : 3);
        editText2.setInputType(49152);
        editText2.setImeOptions(j2 > 0 ? 5 : 6);
        if (j2 > 0) {
            i3 = NUM;
            str4 = "FirstName";
        } else {
            i3 = NUM;
            str4 = "VoipEditTitleHint";
        }
        editText2.setHint(LocaleController.getString(str4, i3));
        editText2.setBackground(Theme.createEditTextDrawable(context2, true));
        editText2.setPadding(0, AndroidUtilities.dp(8.0f), 0, AndroidUtilities.dp(8.0f));
        editText2.requestFocus();
        if (j2 > 0) {
            editText = new EditText(context2);
            editText.setTextColor(Theme.getColor("voipgroup_actionBarItems"));
            editText.setTextSize(1, 16.0f);
            editText.setMaxLines(1);
            editText.setLines(1);
            editText.setSingleLine(true);
            editText.setGravity(LocaleController.isRTL ? 5 : 3);
            editText.setInputType(49152);
            editText.setImeOptions(6);
            editText.setHint(LocaleController.getString("LastName", NUM));
            editText.setBackground(Theme.createEditTextDrawable(context2, true));
            editText.setPadding(0, AndroidUtilities.dp(8.0f), 0, AndroidUtilities.dp(8.0f));
        } else {
            editText = null;
        }
        AndroidUtilities.showKeyboard(editText2);
        linearLayout.addView(editText2, LayoutHelper.createLinear(-1, -2, 0, 23, 12, 23, 21));
        if (editText != null) {
            linearLayout.addView(editText, LayoutHelper.createLinear(-1, -2, 0, 23, 12, 23, 21));
        }
        editText2.setText(str);
        editText2.setSelection(editText2.getText().toString().length());
        if (editText != null) {
            editText.setText(str2);
            editText.setSelection(editText.getText().toString().length());
        }
        builder.setView(linearLayout);
        AlertsCreator$$ExternalSyntheticLambda13 alertsCreator$$ExternalSyntheticLambda13 = new AlertsCreator$$ExternalSyntheticLambda13(editText2, j, i, editText);
        builder.setPositiveButton(LocaleController.getString("Save", NUM), alertsCreator$$ExternalSyntheticLambda13);
        builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
        builder.setOnPreDismissListener(new AlertsCreator$$ExternalSyntheticLambda40(editText2, editText));
        AlertDialog create = builder.create();
        create.setBackgroundColor(Theme.getColor("voipgroup_dialogBackground"));
        create.show();
        create.setTextColor(Theme.getColor("voipgroup_actionBarItems"));
        AlertsCreator$$ExternalSyntheticLambda83 alertsCreator$$ExternalSyntheticLambda83 = new AlertsCreator$$ExternalSyntheticLambda83(create, alertsCreator$$ExternalSyntheticLambda13);
        if (editText != null) {
            editText.setOnEditorActionListener(alertsCreator$$ExternalSyntheticLambda83);
        } else {
            editText2.setOnEditorActionListener(alertsCreator$$ExternalSyntheticLambda83);
        }
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$createChangeNameAlert$34(EditText editText, long j, int i, EditText editText2, DialogInterface dialogInterface, int i2) {
        if (editText.getText() != null) {
            if (j > 0) {
                TLRPC$User user = MessagesController.getInstance(i).getUser(Long.valueOf(j));
                String obj = editText.getText().toString();
                String obj2 = editText2.getText().toString();
                String str = user.first_name;
                String str2 = user.last_name;
                if (str == null) {
                    str = "";
                }
                if (str2 == null) {
                    str2 = "";
                }
                if (!str.equals(obj) || !str2.equals(obj2)) {
                    TLRPC$TL_account_updateProfile tLRPC$TL_account_updateProfile = new TLRPC$TL_account_updateProfile();
                    tLRPC$TL_account_updateProfile.flags = 3;
                    tLRPC$TL_account_updateProfile.first_name = obj;
                    user.first_name = obj;
                    tLRPC$TL_account_updateProfile.last_name = obj2;
                    user.last_name = obj2;
                    TLRPC$User user2 = MessagesController.getInstance(i).getUser(Long.valueOf(UserConfig.getInstance(i).getClientUserId()));
                    if (user2 != null) {
                        user2.first_name = tLRPC$TL_account_updateProfile.first_name;
                        user2.last_name = tLRPC$TL_account_updateProfile.last_name;
                    }
                    UserConfig.getInstance(i).saveConfig(true);
                    NotificationCenter.getInstance(i).postNotificationName(NotificationCenter.mainUserInfoChanged, new Object[0]);
                    NotificationCenter.getInstance(i).postNotificationName(NotificationCenter.updateInterfaces, Integer.valueOf(MessagesController.UPDATE_MASK_NAME));
                    ConnectionsManager.getInstance(i).sendRequest(tLRPC$TL_account_updateProfile, AlertsCreator$$ExternalSyntheticLambda100.INSTANCE);
                    NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.showBulletin, 3, Long.valueOf(j));
                } else {
                    dialogInterface.dismiss();
                    return;
                }
            } else {
                long j2 = -j;
                TLRPC$Chat chat = MessagesController.getInstance(i).getChat(Long.valueOf(j2));
                String obj3 = editText.getText().toString();
                String str3 = chat.title;
                if (str3 == null || !str3.equals(obj3)) {
                    chat.title = obj3;
                    NotificationCenter.getInstance(i).postNotificationName(NotificationCenter.updateInterfaces, Integer.valueOf(MessagesController.UPDATE_MASK_CHAT_NAME));
                    MessagesController.getInstance(i).changeChatTitle(j2, obj3);
                    NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.showBulletin, 3, Long.valueOf(j));
                } else {
                    dialogInterface.dismiss();
                    return;
                }
            }
            dialogInterface.dismiss();
        }
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$createChangeNameAlert$35(EditText editText, EditText editText2, DialogInterface dialogInterface) {
        AndroidUtilities.hideKeyboard(editText);
        AndroidUtilities.hideKeyboard(editText2);
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$createChangeNameAlert$36(AlertDialog alertDialog, DialogInterface.OnClickListener onClickListener, TextView textView, int i, KeyEvent keyEvent) {
        if ((i != 6 && keyEvent.getKeyCode() != 66) || !alertDialog.isShowing()) {
            return false;
        }
        onClickListener.onClick(alertDialog, 0);
        return true;
    }

    public static void showChatWithAdmin(BaseFragment baseFragment, TLRPC$User tLRPC$User, String str, boolean z, int i) {
        int i2;
        String str2;
        if (baseFragment.getParentActivity() != null) {
            BottomSheet.Builder builder = new BottomSheet.Builder(baseFragment.getParentActivity());
            if (z) {
                i2 = NUM;
                str2 = "ChatWithAdminChannelTitle";
            } else {
                i2 = NUM;
                str2 = "ChatWithAdminGroupTitle";
            }
            builder.setTitle(LocaleController.getString(str2, i2), true);
            LinearLayout linearLayout = new LinearLayout(baseFragment.getParentActivity());
            linearLayout.setOrientation(1);
            TextView textView = new TextView(baseFragment.getParentActivity());
            linearLayout.addView(textView, LayoutHelper.createLinear(-1, -1, 0, 24, 16, 24, 24));
            textView.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
            textView.setTextSize(1, 16.0f);
            textView.setText(AndroidUtilities.replaceTags(LocaleController.formatString("ChatWithAdminMessage", NUM, str, LocaleController.formatDateAudio((long) i, false))));
            TextView textView2 = new TextView(baseFragment.getParentActivity());
            textView2.setPadding(AndroidUtilities.dp(34.0f), 0, AndroidUtilities.dp(34.0f), 0);
            textView2.setGravity(17);
            textView2.setTextSize(1, 14.0f);
            textView2.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            textView2.setText(LocaleController.getString("IUnderstand", NUM));
            textView2.setTextColor(Theme.getColor("featuredStickers_buttonText"));
            textView2.setBackgroundDrawable(Theme.createSimpleSelectorRoundRectDrawable(AndroidUtilities.dp(6.0f), Theme.getColor("featuredStickers_addButton"), Theme.getColor("featuredStickers_addButtonPressed")));
            linearLayout.addView(textView2, LayoutHelper.createLinear(-1, 48, 0, 24, 15, 16, 24));
            builder.setCustomView(linearLayout);
            textView2.setOnClickListener(new AlertsCreator$$ExternalSyntheticLambda54(builder.show()));
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
                                str2 = "DeleteThisChatBothSides";
                            } else {
                                i2 = NUM;
                                str2 = "DeleteTheseChatsBothSides";
                            }
                            checkBoxCell.setText(LocaleController.getString(str2, i2), "", true, false);
                        }
                        checkBoxCellArr[i4].setPadding(LocaleController.isRTL ? AndroidUtilities.dp(16.0f) : AndroidUtilities.dp(8.0f), 0, LocaleController.isRTL ? AndroidUtilities.dp(8.0f) : AndroidUtilities.dp(16.0f), 0);
                        linearLayout.addView(checkBoxCellArr[i4], LayoutHelper.createLinear(-1, 48));
                        checkBoxCellArr[i4].setOnClickListener(new AlertsCreator$$ExternalSyntheticLambda72(zArr, i4));
                    }
                    i4++;
                }
                builder.setPositiveButton(str, new AlertsCreator$$ExternalSyntheticLambda29(blockDialogCallback, zArr));
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

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$createBlockDialogAlert$38(boolean[] zArr, int i, View view) {
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
        numberPicker2.setOnScrollListener(new AlertsCreator$$ExternalSyntheticLambda120(z2, numberPicker2, numberPicker, numberPicker3));
        numberPicker.setMinValue(0);
        numberPicker.setMaxValue(11);
        linearLayout.addView(numberPicker, LayoutHelper.createLinear(0, -2, 0.3f));
        numberPicker.setFormatter(AlertsCreator$$ExternalSyntheticLambda116.INSTANCE);
        numberPicker.setOnValueChangedListener(new AlertsCreator$$ExternalSyntheticLambda129(numberPicker2, numberPicker, numberPicker3));
        numberPicker.setOnScrollListener(new AlertsCreator$$ExternalSyntheticLambda119(z2, numberPicker2, numberPicker, numberPicker3));
        Calendar instance = Calendar.getInstance();
        instance.setTimeInMillis(System.currentTimeMillis());
        int i8 = instance.get(1);
        numberPicker3.setMinValue(i8 + i);
        numberPicker3.setMaxValue(i8 + i2);
        numberPicker3.setValue(i8 + i3);
        linearLayout.addView(numberPicker3, LayoutHelper.createLinear(0, -2, 0.4f));
        numberPicker3.setOnValueChangedListener(new AlertsCreator$$ExternalSyntheticLambda128(numberPicker2, numberPicker, numberPicker3));
        numberPicker3.setOnScrollListener(new AlertsCreator$$ExternalSyntheticLambda121(z2, numberPicker2, numberPicker, numberPicker3));
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
        builder.setPositiveButton(LocaleController.getString("Set", NUM), new AlertsCreator$$ExternalSyntheticLambda33(z, numberPicker2, numberPicker, numberPicker3, datePickerDelegate));
        builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
        return builder;
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$createDatePickerDialog$40(boolean z, NumberPicker numberPicker, NumberPicker numberPicker2, NumberPicker numberPicker3, NumberPicker numberPicker4, int i) {
        if (z && i == 0) {
            checkPickerDate(numberPicker, numberPicker2, numberPicker3);
        }
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ String lambda$createDatePickerDialog$41(int i) {
        Calendar instance = Calendar.getInstance();
        instance.set(5, 1);
        instance.set(2, i);
        return instance.getDisplayName(2, 1, Locale.getDefault());
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$createDatePickerDialog$43(boolean z, NumberPicker numberPicker, NumberPicker numberPicker2, NumberPicker numberPicker3, NumberPicker numberPicker4, int i) {
        if (z && i == 0) {
            checkPickerDate(numberPicker, numberPicker2, numberPicker3);
        }
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$createDatePickerDialog$45(boolean z, NumberPicker numberPicker, NumberPicker numberPicker2, NumberPicker numberPicker3, NumberPicker numberPicker4, int i) {
        if (z && i == 0) {
            checkPickerDate(numberPicker, numberPicker2, numberPicker3);
        }
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$createDatePickerDialog$46(boolean z, NumberPicker numberPicker, NumberPicker numberPicker2, NumberPicker numberPicker3, DatePickerDelegate datePickerDelegate, DialogInterface dialogInterface, int i) {
        if (z) {
            checkPickerDate(numberPicker, numberPicker2, numberPicker3);
        }
        datePickerDelegate.didSelectDate(numberPicker3.getValue(), numberPicker2.getValue(), numberPicker.getValue());
    }

    public static boolean checkScheduleDate(TextView textView, TextView textView2, int i, NumberPicker numberPicker, NumberPicker numberPicker2, NumberPicker numberPicker3) {
        return checkScheduleDate(textView, textView2, 0, i, numberPicker, numberPicker2, numberPicker3);
    }

    public static boolean checkScheduleDate(TextView textView, TextView textView2, long j, int i, NumberPicker numberPicker, NumberPicker numberPicker2, NumberPicker numberPicker3) {
        int i2;
        long j2;
        int i3;
        int i4;
        String str;
        int i5;
        int i6;
        int i7;
        TextView textView3 = textView;
        TextView textView4 = textView2;
        int i8 = i;
        NumberPicker numberPicker4 = numberPicker;
        NumberPicker numberPicker5 = numberPicker2;
        NumberPicker numberPicker6 = numberPicker3;
        int value = numberPicker.getValue();
        int value2 = numberPicker2.getValue();
        int value3 = numberPicker3.getValue();
        Calendar instance = Calendar.getInstance();
        long currentTimeMillis = System.currentTimeMillis();
        instance.setTimeInMillis(currentTimeMillis);
        int i9 = instance.get(1);
        int i10 = instance.get(6);
        if (j > 0) {
            i2 = i9;
            instance.setTimeInMillis(currentTimeMillis + (j * 1000));
            instance.set(11, 23);
            instance.set(12, 59);
            instance.set(13, 59);
            j2 = instance.getTimeInMillis();
        } else {
            i2 = i9;
            j2 = j;
        }
        int i11 = i10;
        instance.setTimeInMillis(System.currentTimeMillis() + (((long) value) * 24 * 3600 * 1000));
        instance.set(11, value2);
        instance.set(12, value3);
        long timeInMillis = instance.getTimeInMillis();
        int i12 = value;
        int i13 = value2;
        long j3 = currentTimeMillis + 60000;
        if (timeInMillis <= j3) {
            instance.setTimeInMillis(j3);
            if (i11 != instance.get(6)) {
                numberPicker4.setValue(1);
                i7 = 11;
                i4 = 1;
            } else {
                i4 = i12;
                i7 = 11;
            }
            i3 = instance.get(i7);
            numberPicker5.setValue(i3);
            value3 = instance.get(12);
            numberPicker6.setValue(value3);
        } else if (j2 <= 0 || timeInMillis <= j2) {
            i4 = i12;
            i3 = i13;
        } else {
            instance.setTimeInMillis(j2);
            i4 = 7;
            numberPicker4.setValue(7);
            i3 = instance.get(11);
            numberPicker5.setValue(i3);
            value3 = instance.get(12);
            numberPicker6.setValue(value3);
        }
        int i14 = instance.get(1);
        long j4 = timeInMillis;
        instance.setTimeInMillis(System.currentTimeMillis() + (((long) i4) * 24 * 3600 * 1000));
        instance.set(11, i3);
        instance.set(12, value3);
        long timeInMillis2 = instance.getTimeInMillis();
        if (textView3 != null) {
            if (i4 == 0) {
                i6 = 1;
                i5 = 0;
            } else if (i2 == i14) {
                i6 = 1;
                i5 = 1;
            } else {
                i6 = 1;
                i5 = 2;
            }
            if (i8 == i6) {
                i5 += 3;
            } else if (i8 == 2) {
                i5 += 6;
            } else if (i8 == 3) {
                i5 += 9;
            }
            textView3.setText(LocaleController.getInstance().formatterScheduleSend[i5].format(timeInMillis2));
        }
        if (textView4 != null) {
            int i15 = (int) ((timeInMillis2 - currentTimeMillis) / 1000);
            if (i15 > 86400) {
                str = LocaleController.formatPluralString("DaysSchedule", Math.round(((float) i15) / 86400.0f));
            } else if (i15 >= 3600) {
                str = LocaleController.formatPluralString("HoursSchedule", Math.round(((float) i15) / 3600.0f));
            } else if (i15 >= 60) {
                str = LocaleController.formatPluralString("MinutesSchedule", Math.round(((float) i15) / 60.0f));
            } else {
                str = LocaleController.formatPluralString("SecondsSchedule", i15);
            }
            if (textView2.getTag() != null) {
                textView4.setText(LocaleController.formatString("VoipChannelScheduleInfo", NUM, str));
            } else {
                textView4.setText(LocaleController.formatString("VoipGroupScheduleInfo", NUM, str));
            }
        }
        return j4 - currentTimeMillis > 60000;
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

        /* JADX WARNING: Illegal instructions before constructor call */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        private ScheduleDatePickerColors(org.telegram.ui.ActionBar.Theme.ResourcesProvider r13) {
            /*
                r12 = this;
                java.lang.String r0 = "dialogTextBlack"
                if (r13 == 0) goto L_0x0009
                int r0 = r13.getColorOrDefault(r0)
                goto L_0x000d
            L_0x0009:
                int r0 = org.telegram.ui.ActionBar.Theme.getColor(r0)
            L_0x000d:
                r2 = r0
                java.lang.String r0 = "dialogBackground"
                if (r13 == 0) goto L_0x0017
                int r0 = r13.getColorOrDefault(r0)
                goto L_0x001b
            L_0x0017:
                int r0 = org.telegram.ui.ActionBar.Theme.getColor(r0)
            L_0x001b:
                r3 = r0
                java.lang.String r0 = "key_sheet_other"
                if (r13 == 0) goto L_0x0025
                int r0 = r13.getColorOrDefault(r0)
                goto L_0x0029
            L_0x0025:
                int r0 = org.telegram.ui.ActionBar.Theme.getColor(r0)
            L_0x0029:
                r4 = r0
                java.lang.String r0 = "player_actionBarSelector"
                if (r13 == 0) goto L_0x0033
                int r0 = r13.getColorOrDefault(r0)
                goto L_0x0037
            L_0x0033:
                int r0 = org.telegram.ui.ActionBar.Theme.getColor(r0)
            L_0x0037:
                r5 = r0
                java.lang.String r0 = "actionBarDefaultSubmenuItem"
                if (r13 == 0) goto L_0x0041
                int r0 = r13.getColorOrDefault(r0)
                goto L_0x0045
            L_0x0041:
                int r0 = org.telegram.ui.ActionBar.Theme.getColor(r0)
            L_0x0045:
                r6 = r0
                java.lang.String r0 = "actionBarDefaultSubmenuBackground"
                if (r13 == 0) goto L_0x004f
                int r0 = r13.getColorOrDefault(r0)
                goto L_0x0053
            L_0x004f:
                int r0 = org.telegram.ui.ActionBar.Theme.getColor(r0)
            L_0x0053:
                r7 = r0
                java.lang.String r0 = "listSelectorSDK21"
                if (r13 == 0) goto L_0x005d
                int r0 = r13.getColorOrDefault(r0)
                goto L_0x0061
            L_0x005d:
                int r0 = org.telegram.ui.ActionBar.Theme.getColor(r0)
            L_0x0061:
                r8 = r0
                java.lang.String r0 = "featuredStickers_buttonText"
                if (r13 == 0) goto L_0x006b
                int r0 = r13.getColorOrDefault(r0)
                goto L_0x006f
            L_0x006b:
                int r0 = org.telegram.ui.ActionBar.Theme.getColor(r0)
            L_0x006f:
                r9 = r0
                java.lang.String r0 = "featuredStickers_addButton"
                if (r13 == 0) goto L_0x0079
                int r0 = r13.getColorOrDefault(r0)
                goto L_0x007d
            L_0x0079:
                int r0 = org.telegram.ui.ActionBar.Theme.getColor(r0)
            L_0x007d:
                r10 = r0
                java.lang.String r0 = "featuredStickers_addButtonPressed"
                if (r13 == 0) goto L_0x0087
                int r13 = r13.getColorOrDefault(r0)
                goto L_0x008b
            L_0x0087:
                int r13 = org.telegram.ui.ActionBar.Theme.getColor(r0)
            L_0x008b:
                r11 = r13
                r1 = r12
                r1.<init>(r2, r3, r4, r5, r6, r7, r8, r9, r10, r11)
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.AlertsCreator.ScheduleDatePickerColors.<init>(org.telegram.ui.ActionBar.Theme$ResourcesProvider):void");
        }

        public ScheduleDatePickerColors(int i, int i2, int i3, int i4, int i5, int i6, int i7) {
            this(i, i2, i3, i4, i5, i6, i7, Theme.getColor("featuredStickers_buttonText"), Theme.getColor("featuredStickers_addButton"), Theme.getColor("featuredStickers_addButtonPressed"));
        }

        public ScheduleDatePickerColors(int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8, int i9, int i10) {
            this.textColor = i;
            this.backgroundColor = i2;
            this.iconColor = i3;
            this.iconSelectorColor = i4;
            this.subMenuTextColor = i5;
            this.subMenuBackgroundColor = i6;
            this.subMenuSelectorColor = i7;
            this.buttonTextColor = i8;
            this.buttonBackgroundColor = i9;
            this.buttonBackgroundPressedColor = i10;
        }
    }

    public static BottomSheet.Builder createScheduleDatePickerDialog(Context context, long j, ScheduleDatePickerDelegate scheduleDatePickerDelegate) {
        return createScheduleDatePickerDialog(context, j, -1, scheduleDatePickerDelegate, (Runnable) null);
    }

    public static BottomSheet.Builder createScheduleDatePickerDialog(Context context, long j, ScheduleDatePickerDelegate scheduleDatePickerDelegate, Theme.ResourcesProvider resourcesProvider) {
        return createScheduleDatePickerDialog(context, j, -1, scheduleDatePickerDelegate, (Runnable) null, resourcesProvider);
    }

    public static BottomSheet.Builder createScheduleDatePickerDialog(Context context, long j, ScheduleDatePickerDelegate scheduleDatePickerDelegate, ScheduleDatePickerColors scheduleDatePickerColors) {
        return createScheduleDatePickerDialog(context, j, -1, scheduleDatePickerDelegate, (Runnable) null, scheduleDatePickerColors, (Theme.ResourcesProvider) null);
    }

    public static BottomSheet.Builder createScheduleDatePickerDialog(Context context, long j, ScheduleDatePickerDelegate scheduleDatePickerDelegate, Runnable runnable, Theme.ResourcesProvider resourcesProvider) {
        return createScheduleDatePickerDialog(context, j, -1, scheduleDatePickerDelegate, runnable, resourcesProvider);
    }

    public static BottomSheet.Builder createScheduleDatePickerDialog(Context context, long j, long j2, ScheduleDatePickerDelegate scheduleDatePickerDelegate, Runnable runnable) {
        return createScheduleDatePickerDialog(context, j, j2, scheduleDatePickerDelegate, runnable, new ScheduleDatePickerColors(), (Theme.ResourcesProvider) null);
    }

    public static BottomSheet.Builder createScheduleDatePickerDialog(Context context, long j, long j2, ScheduleDatePickerDelegate scheduleDatePickerDelegate, Runnable runnable, Theme.ResourcesProvider resourcesProvider) {
        Theme.ResourcesProvider resourcesProvider2 = resourcesProvider;
        return createScheduleDatePickerDialog(context, j, j2, scheduleDatePickerDelegate, runnable, new ScheduleDatePickerColors(resourcesProvider2), resourcesProvider2);
    }

    public static BottomSheet.Builder createScheduleDatePickerDialog(Context context, long j, long j2, ScheduleDatePickerDelegate scheduleDatePickerDelegate, Runnable runnable, ScheduleDatePickerColors scheduleDatePickerColors, Theme.ResourcesProvider resourcesProvider) {
        AnonymousClass9 r26;
        int i;
        Calendar calendar;
        TLRPC$User user;
        TLRPC$UserStatus tLRPC$UserStatus;
        Context context2 = context;
        ScheduleDatePickerColors scheduleDatePickerColors2 = scheduleDatePickerColors;
        Theme.ResourcesProvider resourcesProvider2 = resourcesProvider;
        if (context2 == null) {
            return null;
        }
        long clientUserId = UserConfig.getInstance(UserConfig.selectedAccount).getClientUserId();
        BottomSheet.Builder builder = new BottomSheet.Builder(context2, false, resourcesProvider2);
        builder.setApplyBottomPadding(false);
        final NumberPicker numberPicker = new NumberPicker(context2, resourcesProvider2);
        numberPicker.setTextColor(scheduleDatePickerColors2.textColor);
        numberPicker.setTextOffset(AndroidUtilities.dp(10.0f));
        numberPicker.setItemCount(5);
        final AnonymousClass7 r9 = new NumberPicker(context2, resourcesProvider2) {
            /* access modifiers changed from: protected */
            public CharSequence getContentDescription(int i) {
                return LocaleController.formatPluralString("Hours", i);
            }
        };
        r9.setItemCount(5);
        r9.setTextColor(scheduleDatePickerColors2.textColor);
        r9.setTextOffset(-AndroidUtilities.dp(10.0f));
        final AnonymousClass8 r8 = new NumberPicker(context2, resourcesProvider2) {
            /* access modifiers changed from: protected */
            public CharSequence getContentDescription(int i) {
                return LocaleController.formatPluralString("Minutes", i);
            }
        };
        r8.setItemCount(5);
        r8.setTextColor(scheduleDatePickerColors2.textColor);
        r8.setTextOffset(-AndroidUtilities.dp(34.0f));
        AnonymousClass9 r5 = new LinearLayout(context2) {
            boolean ignoreLayout = false;

            /* access modifiers changed from: protected */
            public void onMeasure(int i, int i2) {
                this.ignoreLayout = true;
                Point point = AndroidUtilities.displaySize;
                int i3 = point.x > point.y ? 3 : 5;
                numberPicker.setItemCount(i3);
                r9.setItemCount(i3);
                r8.setItemCount(i3);
                numberPicker.getLayoutParams().height = AndroidUtilities.dp(42.0f) * i3;
                r9.getLayoutParams().height = AndroidUtilities.dp(42.0f) * i3;
                r8.getLayoutParams().height = AndroidUtilities.dp(42.0f) * i3;
                this.ignoreLayout = false;
                super.onMeasure(i, i2);
            }

            public void requestLayout() {
                if (!this.ignoreLayout) {
                    super.requestLayout();
                }
            }
        };
        r5.setOrientation(1);
        FrameLayout frameLayout = new FrameLayout(context2);
        r5.addView(frameLayout, LayoutHelper.createLinear(-1, -2, 51, 22, 0, 0, 4));
        TextView textView = new TextView(context2);
        if (j == clientUserId) {
            textView.setText(LocaleController.getString("SetReminder", NUM));
        } else {
            textView.setText(LocaleController.getString("ScheduleMessage", NUM));
        }
        textView.setTextColor(scheduleDatePickerColors2.textColor);
        textView.setTextSize(1, 20.0f);
        textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        frameLayout.addView(textView, LayoutHelper.createFrame(-2, -2.0f, 51, 0.0f, 12.0f, 0.0f, 0.0f));
        textView.setOnTouchListener(AlertsCreator$$ExternalSyntheticLambda79.INSTANCE);
        if (!DialogObject.isUserDialog(j) || j == clientUserId || (user = MessagesController.getInstance(UserConfig.selectedAccount).getUser(Long.valueOf(j))) == null || user.bot || (tLRPC$UserStatus = user.status) == null || tLRPC$UserStatus.expires <= 0) {
            ScheduleDatePickerDelegate scheduleDatePickerDelegate2 = scheduleDatePickerDelegate;
            r26 = r5;
            i = 1;
        } else {
            String firstName = UserObject.getFirstName(user);
            if (firstName.length() > 10) {
                firstName = firstName.substring(0, 10) + "…";
            }
            String str = firstName;
            ActionBarMenuItem actionBarMenuItem = r0;
            r26 = r5;
            ActionBarMenuItem actionBarMenuItem2 = new ActionBarMenuItem(context, (ActionBarMenu) null, 0, scheduleDatePickerColors2.iconColor, false, resourcesProvider);
            actionBarMenuItem.setLongClickEnabled(false);
            actionBarMenuItem.setSubMenuOpenSide(2);
            actionBarMenuItem.setIcon(NUM);
            i = 1;
            actionBarMenuItem.setBackgroundDrawable(Theme.createSelectorDrawable(scheduleDatePickerColors2.iconSelectorColor, 1));
            frameLayout.addView(actionBarMenuItem, LayoutHelper.createFrame(40, 40.0f, 53, 0.0f, 8.0f, 5.0f, 0.0f));
            actionBarMenuItem.addSubItem(1, LocaleController.formatString("ScheduleWhenOnline", NUM, str));
            actionBarMenuItem.setOnClickListener(new AlertsCreator$$ExternalSyntheticLambda51(actionBarMenuItem, scheduleDatePickerColors2));
            actionBarMenuItem.setDelegate(new AlertsCreator$$ExternalSyntheticLambda101(scheduleDatePickerDelegate, builder));
            actionBarMenuItem.setContentDescription(LocaleController.getString("AccDescrMoreOptions", NUM));
        }
        LinearLayout linearLayout = new LinearLayout(context2);
        linearLayout.setOrientation(0);
        linearLayout.setWeightSum(1.0f);
        AnonymousClass9 r3 = r26;
        r3.addView(linearLayout, LayoutHelper.createLinear(-1, -2, 1.0f, 0, 0, 12, 0, 12));
        long currentTimeMillis = System.currentTimeMillis();
        Calendar instance = Calendar.getInstance();
        instance.setTimeInMillis(currentTimeMillis);
        int i2 = instance.get(i);
        BottomSheet.Builder builder2 = builder;
        AnonymousClass10 r13 = new TextView(context2) {
            public CharSequence getAccessibilityClassName() {
                return Button.class.getName();
            }
        };
        linearLayout.addView(numberPicker, LayoutHelper.createLinear(0, 270, 0.5f));
        numberPicker.setMinValue(0);
        numberPicker.setMaxValue(365);
        numberPicker.setWrapSelectorWheel(false);
        numberPicker.setFormatter(new AlertsCreator$$ExternalSyntheticLambda102(currentTimeMillis, instance, i2));
        Calendar calendar2 = instance;
        AnonymousClass9 r36 = r3;
        AlertsCreator$$ExternalSyntheticLambda125 alertsCreator$$ExternalSyntheticLambda125 = r0;
        AnonymousClass8 r45 = r8;
        AnonymousClass10 r19 = r13;
        AnonymousClass7 r132 = r9;
        AlertsCreator$$ExternalSyntheticLambda125 alertsCreator$$ExternalSyntheticLambda1252 = new AlertsCreator$$ExternalSyntheticLambda125(r3, r13, clientUserId, j, numberPicker, r9, r45);
        numberPicker.setOnValueChangedListener(alertsCreator$$ExternalSyntheticLambda125);
        r132.setMinValue(0);
        r132.setMaxValue(23);
        linearLayout.addView(r132, LayoutHelper.createLinear(0, 270, 0.2f));
        r132.setFormatter(AlertsCreator$$ExternalSyntheticLambda112.INSTANCE);
        r132.setOnValueChangedListener(alertsCreator$$ExternalSyntheticLambda125);
        AnonymousClass8 r82 = r45;
        r82.setMinValue(0);
        r82.setMaxValue(59);
        r82.setValue(0);
        r82.setFormatter(AlertsCreator$$ExternalSyntheticLambda114.INSTANCE);
        linearLayout.addView(r82, LayoutHelper.createLinear(0, 270, 0.3f));
        r82.setOnValueChangedListener(alertsCreator$$ExternalSyntheticLambda125);
        if (j2 <= 0 || j2 == NUM) {
            calendar = calendar2;
        } else {
            long j3 = 1000 * j2;
            calendar = calendar2;
            calendar.setTimeInMillis(System.currentTimeMillis());
            calendar.set(12, 0);
            calendar.set(13, 0);
            calendar.set(14, 0);
            calendar.set(11, 0);
            int timeInMillis = (int) ((j3 - calendar.getTimeInMillis()) / 86400000);
            calendar.setTimeInMillis(j3);
            if (timeInMillis >= 0) {
                r82.setValue(calendar.get(12));
                r132.setValue(calendar.get(11));
                numberPicker.setValue(timeInMillis);
            }
        }
        boolean[] zArr = {true};
        checkScheduleDate(r19, (TextView) null, clientUserId == j ? 1 : 0, numberPicker, r132, r82);
        AnonymousClass10 r14 = r19;
        r14.setPadding(AndroidUtilities.dp(34.0f), 0, AndroidUtilities.dp(34.0f), 0);
        r14.setGravity(17);
        ScheduleDatePickerColors scheduleDatePickerColors3 = scheduleDatePickerColors;
        r14.setTextColor(scheduleDatePickerColors3.buttonTextColor);
        r14.setTextSize(1, 14.0f);
        r14.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        r14.setBackgroundDrawable(Theme.createSimpleSelectorRoundRectDrawable(AndroidUtilities.dp(4.0f), scheduleDatePickerColors3.buttonBackgroundColor, scheduleDatePickerColors3.buttonBackgroundPressedColor));
        AnonymousClass9 r6 = r36;
        r6.addView(r14, LayoutHelper.createLinear(-1, 48, 83, 16, 15, 16, 16));
        AnonymousClass9 r10 = r6;
        NumberPicker numberPicker2 = numberPicker;
        ScheduleDatePickerColors scheduleDatePickerColors4 = scheduleDatePickerColors3;
        AlertsCreator$$ExternalSyntheticLambda73 alertsCreator$$ExternalSyntheticLambda73 = r0;
        AlertsCreator$$ExternalSyntheticLambda73 alertsCreator$$ExternalSyntheticLambda732 = new AlertsCreator$$ExternalSyntheticLambda73(zArr, clientUserId, j, numberPicker2, r132, r82, calendar, scheduleDatePickerDelegate, builder2);
        r14.setOnClickListener(alertsCreator$$ExternalSyntheticLambda73);
        BottomSheet.Builder builder3 = builder2;
        builder3.setCustomView(r10);
        BottomSheet show = builder3.show();
        show.setOnDismissListener(new AlertsCreator$$ExternalSyntheticLambda44(runnable, zArr));
        show.setBackgroundColor(scheduleDatePickerColors.backgroundColor);
        return builder3;
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$createScheduleDatePickerDialog$48(ActionBarMenuItem actionBarMenuItem, ScheduleDatePickerColors scheduleDatePickerColors, View view) {
        actionBarMenuItem.toggleSubMenu();
        actionBarMenuItem.setPopupItemsColor(scheduleDatePickerColors.subMenuTextColor, false);
        actionBarMenuItem.setupPopupRadialSelectors(scheduleDatePickerColors.subMenuSelectorColor);
        actionBarMenuItem.redrawPopup(scheduleDatePickerColors.subMenuBackgroundColor);
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$createScheduleDatePickerDialog$49(ScheduleDatePickerDelegate scheduleDatePickerDelegate, BottomSheet.Builder builder, int i) {
        if (i == 1) {
            scheduleDatePickerDelegate.didSelectDate(true, NUM);
            builder.getDismissRunnable().run();
        }
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ String lambda$createScheduleDatePickerDialog$50(long j, Calendar calendar, int i, int i2) {
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

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$createScheduleDatePickerDialog$51(LinearLayout linearLayout, TextView textView, long j, long j2, NumberPicker numberPicker, NumberPicker numberPicker2, NumberPicker numberPicker3, NumberPicker numberPicker4, int i, int i2) {
        try {
            linearLayout.performHapticFeedback(3, 2);
        } catch (Exception unused) {
        }
        checkScheduleDate(textView, (TextView) null, j == j2 ? 1 : 0, numberPicker, numberPicker2, numberPicker3);
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$createScheduleDatePickerDialog$54(boolean[] zArr, long j, long j2, NumberPicker numberPicker, NumberPicker numberPicker2, NumberPicker numberPicker3, Calendar calendar, ScheduleDatePickerDelegate scheduleDatePickerDelegate, BottomSheet.Builder builder, View view) {
        Calendar calendar2 = calendar;
        zArr[0] = false;
        boolean checkScheduleDate = checkScheduleDate((TextView) null, (TextView) null, j == j2 ? 1 : 0, numberPicker, numberPicker2, numberPicker3);
        calendar2.setTimeInMillis(System.currentTimeMillis() + (((long) numberPicker.getValue()) * 24 * 3600 * 1000));
        calendar2.set(11, numberPicker2.getValue());
        calendar2.set(12, numberPicker3.getValue());
        if (checkScheduleDate) {
            calendar2.set(13, 0);
        }
        scheduleDatePickerDelegate.didSelectDate(true, (int) (calendar.getTimeInMillis() / 1000));
        builder.getDismissRunnable().run();
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$createScheduleDatePickerDialog$55(Runnable runnable, boolean[] zArr, DialogInterface dialogInterface) {
        if (runnable != null && zArr[0]) {
            runnable.run();
        }
    }

    public static BottomSheet.Builder createDatePickerDialog(Context context, long j, ScheduleDatePickerDelegate scheduleDatePickerDelegate) {
        AnonymousClass13 r22;
        Context context2 = context;
        if (context2 == null) {
            return null;
        }
        ScheduleDatePickerColors scheduleDatePickerColors = new ScheduleDatePickerColors();
        BottomSheet.Builder builder = new BottomSheet.Builder(context2, false);
        builder.setApplyBottomPadding(false);
        final NumberPicker numberPicker = new NumberPicker(context2);
        numberPicker.setTextColor(scheduleDatePickerColors.textColor);
        numberPicker.setTextOffset(AndroidUtilities.dp(10.0f));
        numberPicker.setItemCount(5);
        final AnonymousClass11 r11 = new NumberPicker(context2) {
            /* access modifiers changed from: protected */
            public CharSequence getContentDescription(int i) {
                return LocaleController.formatPluralString("Hours", i);
            }
        };
        r11.setItemCount(5);
        r11.setTextColor(scheduleDatePickerColors.textColor);
        r11.setTextOffset(-AndroidUtilities.dp(10.0f));
        final AnonymousClass12 r12 = new NumberPicker(context2) {
            /* access modifiers changed from: protected */
            public CharSequence getContentDescription(int i) {
                return LocaleController.formatPluralString("Minutes", i);
            }
        };
        r12.setItemCount(5);
        r12.setTextColor(scheduleDatePickerColors.textColor);
        r12.setTextOffset(-AndroidUtilities.dp(34.0f));
        AnonymousClass13 r14 = new LinearLayout(context2) {
            boolean ignoreLayout = false;

            /* access modifiers changed from: protected */
            public void onMeasure(int i, int i2) {
                this.ignoreLayout = true;
                Point point = AndroidUtilities.displaySize;
                int i3 = point.x > point.y ? 3 : 5;
                numberPicker.setItemCount(i3);
                r11.setItemCount(i3);
                r12.setItemCount(i3);
                numberPicker.getLayoutParams().height = AndroidUtilities.dp(42.0f) * i3;
                r11.getLayoutParams().height = AndroidUtilities.dp(42.0f) * i3;
                r12.getLayoutParams().height = AndroidUtilities.dp(42.0f) * i3;
                this.ignoreLayout = false;
                super.onMeasure(i, i2);
            }

            public void requestLayout() {
                if (!this.ignoreLayout) {
                    super.requestLayout();
                }
            }
        };
        r14.setOrientation(1);
        FrameLayout frameLayout = new FrameLayout(context2);
        r14.addView(frameLayout, LayoutHelper.createLinear(-1, -2, 51, 22, 0, 0, 4));
        TextView textView = new TextView(context2);
        textView.setText(LocaleController.getString("ExpireAfter", NUM));
        textView.setTextColor(scheduleDatePickerColors.textColor);
        textView.setTextSize(1, 20.0f);
        textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        frameLayout.addView(textView, LayoutHelper.createFrame(-2, -2.0f, 51, 0.0f, 12.0f, 0.0f, 0.0f));
        textView.setOnTouchListener(AlertsCreator$$ExternalSyntheticLambda76.INSTANCE);
        LinearLayout linearLayout = new LinearLayout(context2);
        linearLayout.setOrientation(0);
        linearLayout.setWeightSum(1.0f);
        r14.addView(linearLayout, LayoutHelper.createLinear(-1, -2, 1.0f, 0, 0, 12, 0, 12));
        long currentTimeMillis = System.currentTimeMillis();
        Calendar instance = Calendar.getInstance();
        instance.setTimeInMillis(currentTimeMillis);
        int i = instance.get(1);
        AnonymousClass14 r8 = new TextView(context2) {
            public CharSequence getAccessibilityClassName() {
                return Button.class.getName();
            }
        };
        linearLayout.addView(numberPicker, LayoutHelper.createLinear(0, 270, 0.5f));
        numberPicker.setMinValue(0);
        numberPicker.setMaxValue(365);
        numberPicker.setWrapSelectorWheel(false);
        numberPicker.setFormatter(new AlertsCreator$$ExternalSyntheticLambda103(currentTimeMillis, instance, i));
        AlertsCreator$$ExternalSyntheticLambda127 alertsCreator$$ExternalSyntheticLambda127 = new AlertsCreator$$ExternalSyntheticLambda127(r14, numberPicker, r11, r12);
        numberPicker.setOnValueChangedListener(alertsCreator$$ExternalSyntheticLambda127);
        r11.setMinValue(0);
        r11.setMaxValue(23);
        linearLayout.addView(r11, LayoutHelper.createLinear(0, 270, 0.2f));
        r11.setFormatter(AlertsCreator$$ExternalSyntheticLambda113.INSTANCE);
        r11.setOnValueChangedListener(alertsCreator$$ExternalSyntheticLambda127);
        r12.setMinValue(0);
        r12.setMaxValue(59);
        r12.setValue(0);
        r12.setFormatter(AlertsCreator$$ExternalSyntheticLambda117.INSTANCE);
        linearLayout.addView(r12, LayoutHelper.createLinear(0, 270, 0.3f));
        r12.setOnValueChangedListener(alertsCreator$$ExternalSyntheticLambda127);
        if (j <= 0 || j == NUM) {
            r22 = r14;
        } else {
            long j2 = 1000 * j;
            instance.setTimeInMillis(System.currentTimeMillis());
            instance.set(12, 0);
            instance.set(13, 0);
            instance.set(14, 0);
            instance.set(11, 0);
            r22 = r14;
            int timeInMillis = (int) ((j2 - instance.getTimeInMillis()) / 86400000);
            instance.setTimeInMillis(j2);
            if (timeInMillis >= 0) {
                r12.setValue(instance.get(12));
                r11.setValue(instance.get(11));
                numberPicker.setValue(timeInMillis);
            }
        }
        AnonymousClass14 r0 = r8;
        checkScheduleDate((TextView) null, (TextView) null, 0, numberPicker, r11, r12);
        r0.setPadding(AndroidUtilities.dp(34.0f), 0, AndroidUtilities.dp(34.0f), 0);
        r0.setGravity(17);
        r0.setTextColor(scheduleDatePickerColors.buttonTextColor);
        r0.setTextSize(1, 14.0f);
        r0.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        r0.setBackgroundDrawable(Theme.createSimpleSelectorRoundRectDrawable(AndroidUtilities.dp(4.0f), scheduleDatePickerColors.buttonBackgroundColor, scheduleDatePickerColors.buttonBackgroundPressedColor));
        r0.setText(LocaleController.getString("SetTimeLimit", NUM));
        AnonymousClass13 r142 = r22;
        r142.addView(r0, LayoutHelper.createLinear(-1, 48, 83, 16, 15, 16, 16));
        r0.setOnClickListener(new AlertsCreator$$ExternalSyntheticLambda57(numberPicker, r11, r12, instance, scheduleDatePickerDelegate, builder));
        builder.setCustomView(r142);
        builder.show().setBackgroundColor(scheduleDatePickerColors.backgroundColor);
        return builder;
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ String lambda$createDatePickerDialog$57(long j, Calendar calendar, int i, int i2) {
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

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$createDatePickerDialog$58(LinearLayout linearLayout, NumberPicker numberPicker, NumberPicker numberPicker2, NumberPicker numberPicker3, NumberPicker numberPicker4, int i, int i2) {
        try {
            linearLayout.performHapticFeedback(3, 2);
        } catch (Exception unused) {
        }
        checkScheduleDate((TextView) null, (TextView) null, 0, numberPicker, numberPicker2, numberPicker3);
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$createDatePickerDialog$61(NumberPicker numberPicker, NumberPicker numberPicker2, NumberPicker numberPicker3, Calendar calendar, ScheduleDatePickerDelegate scheduleDatePickerDelegate, BottomSheet.Builder builder, View view) {
        boolean checkScheduleDate = checkScheduleDate((TextView) null, (TextView) null, 0, numberPicker, numberPicker2, numberPicker3);
        calendar.setTimeInMillis(System.currentTimeMillis() + (((long) numberPicker.getValue()) * 24 * 3600 * 1000));
        calendar.set(11, numberPicker2.getValue());
        calendar.set(12, numberPicker3.getValue());
        if (checkScheduleDate) {
            calendar.set(13, 0);
        }
        scheduleDatePickerDelegate.didSelectDate(true, (int) (calendar.getTimeInMillis() / 1000));
        builder.getDismissRunnable().run();
    }

    public static BottomSheet.Builder createAutoDeleteDatePickerDialog(Context context, ScheduleDatePickerDelegate scheduleDatePickerDelegate) {
        Context context2 = context;
        if (context2 == null) {
            return null;
        }
        ScheduleDatePickerColors scheduleDatePickerColors = new ScheduleDatePickerColors();
        BottomSheet.Builder builder = new BottomSheet.Builder(context2, false);
        builder.setApplyBottomPadding(false);
        final int[] iArr = {0, 1440, 2880, 4320, 5760, 7200, 8640, 10080, 20160, 30240, 44640, 89280, 133920, 178560, 223200, 267840, 525600};
        final AnonymousClass15 r6 = new NumberPicker(context2) {
            /* access modifiers changed from: protected */
            public CharSequence getContentDescription(int i) {
                int[] iArr = iArr;
                if (iArr[i] == 0) {
                    return LocaleController.getString("AutoDeleteNever", NUM);
                }
                if (iArr[i] < 10080) {
                    return LocaleController.formatPluralString("Days", iArr[i] / 1440);
                }
                if (iArr[i] < 44640) {
                    return LocaleController.formatPluralString("Weeks", iArr[i] / 1440);
                }
                if (iArr[i] < 525600) {
                    return LocaleController.formatPluralString("Months", iArr[i] / 10080);
                }
                return LocaleController.formatPluralString("Years", ((iArr[i] * 5) / 31) * 60 * 24);
            }
        };
        r6.setMinValue(0);
        r6.setMaxValue(16);
        r6.setTextColor(scheduleDatePickerColors.textColor);
        r6.setValue(0);
        r6.setWrapSelectorWheel(false);
        r6.setFormatter(new AlertsCreator$$ExternalSyntheticLambda104(iArr));
        AnonymousClass16 r7 = new LinearLayout(context2) {
            boolean ignoreLayout = false;

            /* access modifiers changed from: protected */
            public void onMeasure(int i, int i2) {
                this.ignoreLayout = true;
                Point point = AndroidUtilities.displaySize;
                int i3 = point.x > point.y ? 3 : 5;
                r6.setItemCount(i3);
                r6.getLayoutParams().height = AndroidUtilities.dp(42.0f) * i3;
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
        textView.setText(LocaleController.getString("AutoDeleteAfteTitle", NUM));
        textView.setTextColor(scheduleDatePickerColors.textColor);
        textView.setTextSize(1, 20.0f);
        textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        frameLayout.addView(textView, LayoutHelper.createFrame(-2, -2.0f, 51, 0.0f, 12.0f, 0.0f, 0.0f));
        textView.setOnTouchListener(AlertsCreator$$ExternalSyntheticLambda80.INSTANCE);
        LinearLayout linearLayout = new LinearLayout(context2);
        linearLayout.setOrientation(0);
        linearLayout.setWeightSum(1.0f);
        r7.addView(linearLayout, LayoutHelper.createLinear(-1, -2, 1.0f, 0, 0, 12, 0, 12));
        AnonymousClass17 r12 = new TextView(context2) {
            public CharSequence getAccessibilityClassName() {
                return Button.class.getName();
            }
        };
        linearLayout.addView(r6, LayoutHelper.createLinear(0, 270, 1.0f));
        r12.setPadding(AndroidUtilities.dp(34.0f), 0, AndroidUtilities.dp(34.0f), 0);
        r12.setGravity(17);
        r12.setTextColor(scheduleDatePickerColors.buttonTextColor);
        r12.setTextSize(1, 14.0f);
        r12.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        r12.setBackgroundDrawable(Theme.createSimpleSelectorRoundRectDrawable(AndroidUtilities.dp(4.0f), scheduleDatePickerColors.buttonBackgroundColor, scheduleDatePickerColors.buttonBackgroundPressedColor));
        r12.setText(LocaleController.getString("AutoDeleteConfirm", NUM));
        r7.addView(r12, LayoutHelper.createLinear(-1, 48, 83, 16, 15, 16, 16));
        r6.setOnValueChangedListener(new AlertsCreator$$ExternalSyntheticLambda122(r7));
        r12.setOnClickListener(new AlertsCreator$$ExternalSyntheticLambda63(iArr, r6, scheduleDatePickerDelegate, builder));
        builder.setCustomView(r7);
        builder.show().setBackgroundColor(scheduleDatePickerColors.backgroundColor);
        return builder;
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ String lambda$createAutoDeleteDatePickerDialog$62(int[] iArr, int i) {
        if (iArr[i] == 0) {
            return LocaleController.getString("AutoDeleteNever", NUM);
        }
        if (iArr[i] < 10080) {
            return LocaleController.formatPluralString("Days", iArr[i] / 1440);
        }
        if (iArr[i] < 44640) {
            return LocaleController.formatPluralString("Weeks", iArr[i] / 10080);
        }
        if (iArr[i] < 525600) {
            return LocaleController.formatPluralString("Months", iArr[i] / 44640);
        }
        return LocaleController.formatPluralString("Years", iArr[i] / 525600);
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$createAutoDeleteDatePickerDialog$64(LinearLayout linearLayout, NumberPicker numberPicker, int i, int i2) {
        try {
            linearLayout.performHapticFeedback(3, 2);
        } catch (Exception unused) {
        }
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$createAutoDeleteDatePickerDialog$65(int[] iArr, NumberPicker numberPicker, ScheduleDatePickerDelegate scheduleDatePickerDelegate, BottomSheet.Builder builder, View view) {
        scheduleDatePickerDelegate.didSelectDate(true, iArr[numberPicker.getValue()]);
        builder.getDismissRunnable().run();
    }

    public static BottomSheet.Builder createSoundFrequencyPickerDialog(Context context, int i, int i2, SoundFrequencyDelegate soundFrequencyDelegate) {
        Context context2 = context;
        if (context2 == null) {
            return null;
        }
        ScheduleDatePickerColors scheduleDatePickerColors = new ScheduleDatePickerColors();
        BottomSheet.Builder builder = new BottomSheet.Builder(context2, false);
        builder.setApplyBottomPadding(false);
        final AnonymousClass18 r4 = new NumberPicker(context2) {
            /* access modifiers changed from: protected */
            public CharSequence getContentDescription(int i) {
                return LocaleController.formatPluralString("Times", i + 1);
            }
        };
        r4.setMinValue(0);
        r4.setMaxValue(10);
        r4.setTextColor(scheduleDatePickerColors.textColor);
        r4.setValue(i - 1);
        r4.setWrapSelectorWheel(false);
        r4.setFormatter(AlertsCreator$$ExternalSyntheticLambda118.INSTANCE);
        final AnonymousClass19 r7 = new NumberPicker(context2) {
            /* access modifiers changed from: protected */
            public CharSequence getContentDescription(int i) {
                return LocaleController.formatPluralString("Times", i + 1);
            }
        };
        r7.setMinValue(0);
        r7.setMaxValue(10);
        r7.setTextColor(scheduleDatePickerColors.textColor);
        r7.setValue((i2 / 60) - 1);
        r7.setWrapSelectorWheel(false);
        r7.setFormatter(AlertsCreator$$ExternalSyntheticLambda107.INSTANCE);
        final NumberPicker numberPicker = new NumberPicker(context2);
        numberPicker.setMinValue(0);
        numberPicker.setMaxValue(0);
        numberPicker.setTextColor(scheduleDatePickerColors.textColor);
        numberPicker.setValue(0);
        numberPicker.setWrapSelectorWheel(false);
        numberPicker.setFormatter(AlertsCreator$$ExternalSyntheticLambda110.INSTANCE);
        AnonymousClass20 r8 = new LinearLayout(context2) {
            boolean ignoreLayout = false;

            /* access modifiers changed from: protected */
            public void onMeasure(int i, int i2) {
                this.ignoreLayout = true;
                Point point = AndroidUtilities.displaySize;
                int i3 = point.x > point.y ? 3 : 5;
                r4.setItemCount(i3);
                r4.getLayoutParams().height = AndroidUtilities.dp(42.0f) * i3;
                r7.setItemCount(i3);
                r7.getLayoutParams().height = AndroidUtilities.dp(42.0f) * i3;
                numberPicker.setItemCount(i3);
                numberPicker.getLayoutParams().height = AndroidUtilities.dp(42.0f) * i3;
                this.ignoreLayout = false;
                super.onMeasure(i, i2);
            }

            public void requestLayout() {
                if (!this.ignoreLayout) {
                    super.requestLayout();
                }
            }
        };
        r8.setOrientation(1);
        FrameLayout frameLayout = new FrameLayout(context2);
        r8.addView(frameLayout, LayoutHelper.createLinear(-1, -2, 51, 22, 0, 0, 4));
        TextView textView = new TextView(context2);
        textView.setText(LocaleController.getString("NotfificationsFrequencyTitle", NUM));
        textView.setTextColor(scheduleDatePickerColors.textColor);
        textView.setTextSize(1, 20.0f);
        textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        frameLayout.addView(textView, LayoutHelper.createFrame(-2, -2.0f, 51, 0.0f, 12.0f, 0.0f, 0.0f));
        textView.setOnTouchListener(AlertsCreator$$ExternalSyntheticLambda81.INSTANCE);
        LinearLayout linearLayout = new LinearLayout(context2);
        linearLayout.setOrientation(0);
        linearLayout.setWeightSum(1.0f);
        r8.addView(linearLayout, LayoutHelper.createLinear(-1, -2, 1.0f, 0, 0, 12, 0, 12));
        AnonymousClass21 r10 = new TextView(context2) {
            public CharSequence getAccessibilityClassName() {
                return Button.class.getName();
            }
        };
        linearLayout.addView(r4, LayoutHelper.createLinear(0, 270, 0.4f));
        linearLayout.addView(numberPicker, LayoutHelper.createLinear(0, -2, 0.2f, 16));
        linearLayout.addView(r7, LayoutHelper.createLinear(0, 270, 0.4f));
        r10.setPadding(AndroidUtilities.dp(34.0f), 0, AndroidUtilities.dp(34.0f), 0);
        r10.setGravity(17);
        r10.setTextColor(scheduleDatePickerColors.buttonTextColor);
        r10.setTextSize(1, 14.0f);
        r10.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        r10.setBackgroundDrawable(Theme.createSimpleSelectorRoundRectDrawable(AndroidUtilities.dp(4.0f), scheduleDatePickerColors.buttonBackgroundColor, scheduleDatePickerColors.buttonBackgroundPressedColor));
        r10.setText(LocaleController.getString("AutoDeleteConfirm", NUM));
        r8.addView(r10, LayoutHelper.createLinear(-1, 48, 83, 16, 15, 16, 16));
        AlertsCreator$$ExternalSyntheticLambda123 alertsCreator$$ExternalSyntheticLambda123 = new AlertsCreator$$ExternalSyntheticLambda123(r8);
        r4.setOnValueChangedListener(alertsCreator$$ExternalSyntheticLambda123);
        r7.setOnValueChangedListener(alertsCreator$$ExternalSyntheticLambda123);
        r10.setOnClickListener(new AlertsCreator$$ExternalSyntheticLambda56(r4, r7, soundFrequencyDelegate, builder));
        builder.setCustomView(r8);
        builder.show().setBackgroundColor(scheduleDatePickerColors.backgroundColor);
        return builder;
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$createSoundFrequencyPickerDialog$70(LinearLayout linearLayout, NumberPicker numberPicker, int i, int i2) {
        try {
            linearLayout.performHapticFeedback(3, 2);
        } catch (Exception unused) {
        }
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$createSoundFrequencyPickerDialog$71(NumberPicker numberPicker, NumberPicker numberPicker2, SoundFrequencyDelegate soundFrequencyDelegate, BottomSheet.Builder builder, View view) {
        soundFrequencyDelegate.didSelectValues(numberPicker.getValue() + 1, (numberPicker2.getValue() + 1) * 60);
        builder.getDismissRunnable().run();
    }

    public static BottomSheet.Builder createMuteForPickerDialog(Context context, ScheduleDatePickerDelegate scheduleDatePickerDelegate) {
        Context context2 = context;
        if (context2 == null) {
            return null;
        }
        ScheduleDatePickerColors scheduleDatePickerColors = new ScheduleDatePickerColors();
        BottomSheet.Builder builder = new BottomSheet.Builder(context2, false);
        builder.setApplyBottomPadding(false);
        final NumberPicker numberPicker = new NumberPicker(context2);
        numberPicker.setTextColor(scheduleDatePickerColors.textColor);
        numberPicker.setTextOffset(AndroidUtilities.dp(10.0f));
        numberPicker.setItemCount(5);
        final AnonymousClass22 r6 = new NumberPicker(context2) {
            /* access modifiers changed from: protected */
            public CharSequence getContentDescription(int i) {
                return LocaleController.formatPluralString("Hours", i);
            }
        };
        r6.setItemCount(5);
        r6.setTextColor(scheduleDatePickerColors.textColor);
        AnonymousClass23 r5 = new LinearLayout(context2) {
            boolean ignoreLayout = false;

            /* access modifiers changed from: protected */
            public void onMeasure(int i, int i2) {
                this.ignoreLayout = true;
                Point point = AndroidUtilities.displaySize;
                int i3 = point.x > point.y ? 3 : 5;
                numberPicker.setItemCount(i3);
                r6.setItemCount(i3);
                numberPicker.getLayoutParams().height = AndroidUtilities.dp(42.0f) * i3;
                r6.getLayoutParams().height = AndroidUtilities.dp(42.0f) * i3;
                this.ignoreLayout = false;
                super.onMeasure(i, i2);
            }

            public void requestLayout() {
                if (!this.ignoreLayout) {
                    super.requestLayout();
                }
            }
        };
        r5.setOrientation(1);
        FrameLayout frameLayout = new FrameLayout(context2);
        r5.addView(frameLayout, LayoutHelper.createLinear(-1, -2, 51, 22, 0, 0, 4));
        TextView textView = new TextView(context2);
        textView.setText(LocaleController.getString("MuteForAlert", NUM));
        textView.setTextColor(scheduleDatePickerColors.textColor);
        textView.setTextSize(1, 20.0f);
        textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        frameLayout.addView(textView, LayoutHelper.createFrame(-2, -2.0f, 51, 0.0f, 12.0f, 0.0f, 0.0f));
        textView.setOnTouchListener(AlertsCreator$$ExternalSyntheticLambda77.INSTANCE);
        LinearLayout linearLayout = new LinearLayout(context2);
        linearLayout.setOrientation(0);
        linearLayout.setWeightSum(1.0f);
        r5.addView(linearLayout, LayoutHelper.createLinear(-1, -2, 1.0f, 0, 0, 12, 0, 12));
        AnonymousClass24 r9 = new TextView(context2) {
            public CharSequence getAccessibilityClassName() {
                return Button.class.getName();
            }
        };
        linearLayout.addView(numberPicker, LayoutHelper.createLinear(0, 270, 0.5f));
        numberPicker.setMinValue(0);
        numberPicker.setMaxValue(365);
        numberPicker.setWrapSelectorWheel(false);
        numberPicker.setFormatter(AlertsCreator$$ExternalSyntheticLambda106.INSTANCE);
        AlertsCreator$$ExternalSyntheticLambda126 alertsCreator$$ExternalSyntheticLambda126 = new AlertsCreator$$ExternalSyntheticLambda126(r5, numberPicker, r6, r9);
        numberPicker.setOnValueChangedListener(alertsCreator$$ExternalSyntheticLambda126);
        r6.setMinValue(0);
        r6.setMaxValue(23);
        linearLayout.addView(r6, LayoutHelper.createLinear(0, 270, 0.5f));
        r6.setFormatter(AlertsCreator$$ExternalSyntheticLambda105.INSTANCE);
        r6.setOnValueChangedListener(alertsCreator$$ExternalSyntheticLambda126);
        r9.setPadding(AndroidUtilities.dp(34.0f), 0, AndroidUtilities.dp(34.0f), 0);
        r9.setGravity(17);
        r9.setTextColor(scheduleDatePickerColors.buttonTextColor);
        r9.setTextSize(1, 14.0f);
        r9.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        r9.setBackgroundDrawable(Theme.createSimpleSelectorRoundRectDrawable(AndroidUtilities.dp(4.0f), scheduleDatePickerColors.buttonBackgroundColor, scheduleDatePickerColors.buttonBackgroundPressedColor));
        r9.setText(LocaleController.getString("SetTimeLimit", NUM));
        r5.addView(r9, LayoutHelper.createLinear(-1, 48, 83, 16, 15, 16, 16));
        r9.setOnClickListener(new AlertsCreator$$ExternalSyntheticLambda55(r6, numberPicker, scheduleDatePickerDelegate, builder));
        builder.setCustomView(r5);
        builder.show().setBackgroundColor(scheduleDatePickerColors.backgroundColor);
        checkMuteForButton(numberPicker, r6, r9, false);
        return builder;
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$createMuteForPickerDialog$74(LinearLayout linearLayout, NumberPicker numberPicker, NumberPicker numberPicker2, TextView textView, NumberPicker numberPicker3, int i, int i2) {
        try {
            linearLayout.performHapticFeedback(3, 2);
        } catch (Exception unused) {
        }
        checkMuteForButton(numberPicker, numberPicker2, textView, true);
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$createMuteForPickerDialog$76(NumberPicker numberPicker, NumberPicker numberPicker2, ScheduleDatePickerDelegate scheduleDatePickerDelegate, BottomSheet.Builder builder, View view) {
        scheduleDatePickerDelegate.didSelectDate(true, (numberPicker.getValue() * 60) + (numberPicker2.getValue() * 60 * 24));
        builder.getDismissRunnable().run();
    }

    private static void checkMuteForButton(NumberPicker numberPicker, NumberPicker numberPicker2, TextView textView, boolean z) {
        StringBuilder sb = new StringBuilder();
        if (numberPicker.getValue() != 0) {
            sb.append(numberPicker.getValue());
            sb.append(LocaleController.getString("SecretChatTimerDays", NUM));
        }
        if (numberPicker2.getValue() != 0) {
            if (sb.length() > 0) {
                sb.append(" ");
            }
            sb.append(numberPicker2.getValue());
            sb.append(LocaleController.getString("SecretChatTimerHours", NUM));
        }
        if (sb.length() == 0) {
            textView.setText(LocaleController.getString("ChooseTimeForMute", NUM));
            if (textView.isEnabled()) {
                textView.setEnabled(false);
                if (z) {
                    textView.animate().alpha(0.5f);
                } else {
                    textView.setAlpha(0.5f);
                }
            }
        } else {
            textView.setText(LocaleController.formatString("MuteForButton", NUM, sb.toString()));
            if (!textView.isEnabled()) {
                textView.setEnabled(true);
                if (z) {
                    textView.animate().alpha(1.0f);
                } else {
                    textView.setAlpha(1.0f);
                }
            }
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:20:0x0071  */
    /* JADX WARNING: Removed duplicated region for block: B:22:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static void checkCalendarDate(long r11, org.telegram.ui.Components.NumberPicker r13, org.telegram.ui.Components.NumberPicker r14, org.telegram.ui.Components.NumberPicker r15) {
        /*
            int r0 = r13.getValue()
            int r1 = r14.getValue()
            int r2 = r15.getValue()
            java.util.Calendar r3 = java.util.Calendar.getInstance()
            r3.setTimeInMillis(r11)
            r11 = 1
            int r12 = r3.get(r11)
            r4 = 2
            int r5 = r3.get(r4)
            r6 = 5
            int r7 = r3.get(r6)
            long r8 = java.lang.System.currentTimeMillis()
            r3.setTimeInMillis(r8)
            int r8 = r3.get(r11)
            int r9 = r3.get(r4)
            int r10 = r3.get(r6)
            if (r2 <= r8) goto L_0x003b
            r15.setValue(r8)
            r2 = r8
        L_0x003b:
            if (r2 != r8) goto L_0x004b
            if (r1 <= r9) goto L_0x0043
            r14.setValue(r9)
            r1 = r9
        L_0x0043:
            if (r1 != r9) goto L_0x004b
            if (r0 <= r10) goto L_0x004b
            r13.setValue(r10)
            r0 = r10
        L_0x004b:
            if (r2 >= r12) goto L_0x0051
            r15.setValue(r12)
            r2 = r12
        L_0x0051:
            if (r2 != r12) goto L_0x0061
            if (r1 >= r5) goto L_0x0059
            r14.setValue(r5)
            r1 = r5
        L_0x0059:
            if (r1 != r5) goto L_0x0061
            if (r0 >= r7) goto L_0x0061
            r13.setValue(r7)
            goto L_0x0062
        L_0x0061:
            r7 = r0
        L_0x0062:
            r3.set(r11, r2)
            r3.set(r4, r1)
            int r11 = r3.getActualMaximum(r6)
            r13.setMaxValue(r11)
            if (r7 <= r11) goto L_0x0074
            r13.setValue(r11)
        L_0x0074:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.AlertsCreator.checkCalendarDate(long, org.telegram.ui.Components.NumberPicker, org.telegram.ui.Components.NumberPicker, org.telegram.ui.Components.NumberPicker):void");
    }

    public static BottomSheet.Builder createCalendarPickerDialog(Context context, long j, MessagesStorage.IntCallback intCallback, Theme.ResourcesProvider resourcesProvider) {
        Context context2 = context;
        long j2 = j;
        Theme.ResourcesProvider resourcesProvider2 = resourcesProvider;
        if (context2 == null) {
            return null;
        }
        BottomSheet.Builder builder = new BottomSheet.Builder(context2, false, resourcesProvider2);
        builder.setApplyBottomPadding(false);
        final NumberPicker numberPicker = new NumberPicker(context2, resourcesProvider2);
        numberPicker.setTextOffset(AndroidUtilities.dp(10.0f));
        numberPicker.setItemCount(5);
        final NumberPicker numberPicker2 = new NumberPicker(context2, resourcesProvider2);
        numberPicker2.setItemCount(5);
        numberPicker2.setTextOffset(-AndroidUtilities.dp(10.0f));
        final NumberPicker numberPicker3 = new NumberPicker(context2, resourcesProvider2);
        numberPicker3.setItemCount(5);
        numberPicker3.setTextOffset(-AndroidUtilities.dp(24.0f));
        AnonymousClass25 r15 = new LinearLayout(context2) {
            boolean ignoreLayout = false;

            /* access modifiers changed from: protected */
            public void onMeasure(int i, int i2) {
                this.ignoreLayout = true;
                Point point = AndroidUtilities.displaySize;
                int i3 = point.x > point.y ? 3 : 5;
                numberPicker.setItemCount(i3);
                numberPicker2.setItemCount(i3);
                numberPicker3.setItemCount(i3);
                numberPicker.getLayoutParams().height = AndroidUtilities.dp(42.0f) * i3;
                numberPicker2.getLayoutParams().height = AndroidUtilities.dp(42.0f) * i3;
                numberPicker3.getLayoutParams().height = AndroidUtilities.dp(42.0f) * i3;
                this.ignoreLayout = false;
                super.onMeasure(i, i2);
            }

            public void requestLayout() {
                if (!this.ignoreLayout) {
                    super.requestLayout();
                }
            }
        };
        r15.setOrientation(1);
        FrameLayout frameLayout = new FrameLayout(context2);
        r15.addView(frameLayout, LayoutHelper.createLinear(-1, -2, 51, 22, 0, 0, 4));
        TextView textView = new TextView(context2);
        textView.setText(LocaleController.getString("ChooseDate", NUM));
        textView.setTextColor(Theme.getColor("dialogTextBlack", resourcesProvider2));
        textView.setTextSize(1, 20.0f);
        textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        frameLayout.addView(textView, LayoutHelper.createFrame(-2, -2.0f, 51, 0.0f, 12.0f, 0.0f, 0.0f));
        textView.setOnTouchListener(AlertsCreator$$ExternalSyntheticLambda78.INSTANCE);
        LinearLayout linearLayout = new LinearLayout(context2);
        linearLayout.setOrientation(0);
        linearLayout.setWeightSum(1.0f);
        r15.addView(linearLayout, LayoutHelper.createLinear(-1, -2, 1.0f, 0, 0, 12, 0, 12));
        System.currentTimeMillis();
        AnonymousClass26 r4 = new TextView(context2) {
            public CharSequence getAccessibilityClassName() {
                return Button.class.getName();
            }
        };
        linearLayout.addView(numberPicker, LayoutHelper.createLinear(0, 270, 0.25f));
        numberPicker.setMinValue(1);
        numberPicker.setMaxValue(31);
        numberPicker.setWrapSelectorWheel(false);
        numberPicker.setFormatter(AlertsCreator$$ExternalSyntheticLambda111.INSTANCE);
        AlertsCreator$$ExternalSyntheticLambda124 alertsCreator$$ExternalSyntheticLambda124 = r0;
        AnonymousClass26 r25 = r4;
        LinearLayout linearLayout2 = linearLayout;
        AlertsCreator$$ExternalSyntheticLambda124 alertsCreator$$ExternalSyntheticLambda1242 = new AlertsCreator$$ExternalSyntheticLambda124(r15, j, numberPicker, numberPicker2, numberPicker3);
        numberPicker.setOnValueChangedListener(alertsCreator$$ExternalSyntheticLambda1242);
        numberPicker2.setMinValue(0);
        numberPicker2.setMaxValue(11);
        numberPicker2.setWrapSelectorWheel(false);
        linearLayout2.addView(numberPicker2, LayoutHelper.createLinear(0, 270, 0.5f));
        numberPicker2.setFormatter(AlertsCreator$$ExternalSyntheticLambda108.INSTANCE);
        numberPicker2.setOnValueChangedListener(alertsCreator$$ExternalSyntheticLambda1242);
        Calendar instance = Calendar.getInstance();
        instance.setTimeInMillis(j2);
        int i = instance.get(1);
        instance.setTimeInMillis(System.currentTimeMillis());
        int i2 = instance.get(1);
        numberPicker3.setMinValue(i);
        numberPicker3.setMaxValue(i2);
        numberPicker3.setWrapSelectorWheel(false);
        numberPicker3.setFormatter(AlertsCreator$$ExternalSyntheticLambda109.INSTANCE);
        linearLayout2.addView(numberPicker3, LayoutHelper.createLinear(0, 270, 0.25f));
        numberPicker3.setOnValueChangedListener(alertsCreator$$ExternalSyntheticLambda1242);
        numberPicker.setValue(31);
        numberPicker2.setValue(12);
        numberPicker3.setValue(i2);
        checkCalendarDate(j2, numberPicker, numberPicker2, numberPicker3);
        AnonymousClass26 r5 = r25;
        r5.setPadding(AndroidUtilities.dp(34.0f), 0, AndroidUtilities.dp(34.0f), 0);
        r5.setGravity(17);
        r5.setTextColor(Theme.getColor("featuredStickers_buttonText", resourcesProvider2));
        r5.setTextSize(1, 14.0f);
        r5.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        r5.setText(LocaleController.getString("JumpToDate", NUM));
        r5.setBackgroundDrawable(Theme.createSimpleSelectorRoundRectDrawable(AndroidUtilities.dp(4.0f), Theme.getColor("featuredStickers_addButton", resourcesProvider2), Theme.getColor("featuredStickers_addButtonPressed", resourcesProvider2)));
        r15.addView(r5, LayoutHelper.createLinear(-1, 48, 83, 16, 15, 16, 16));
        r5.setOnClickListener(new AlertsCreator$$ExternalSyntheticLambda48(j, numberPicker, numberPicker2, numberPicker3, instance, intCallback, builder));
        builder.setCustomView(r15);
        return builder;
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ String lambda$createCalendarPickerDialog$78(int i) {
        return "" + i;
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$createCalendarPickerDialog$79(LinearLayout linearLayout, long j, NumberPicker numberPicker, NumberPicker numberPicker2, NumberPicker numberPicker3, NumberPicker numberPicker4, int i, int i2) {
        try {
            linearLayout.performHapticFeedback(3, 2);
        } catch (Exception unused) {
        }
        checkCalendarDate(j, numberPicker, numberPicker2, numberPicker3);
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ String lambda$createCalendarPickerDialog$80(int i) {
        switch (i) {
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

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$createCalendarPickerDialog$82(long j, NumberPicker numberPicker, NumberPicker numberPicker2, NumberPicker numberPicker3, Calendar calendar, MessagesStorage.IntCallback intCallback, BottomSheet.Builder builder, View view) {
        checkCalendarDate(j, numberPicker, numberPicker2, numberPicker3);
        calendar.set(1, numberPicker3.getValue());
        calendar.set(2, numberPicker2.getValue());
        calendar.set(5, numberPicker.getValue());
        calendar.set(12, 0);
        calendar.set(11, 0);
        calendar.set(13, 0);
        intCallback.run((int) (calendar.getTimeInMillis() / 1000));
        builder.getDismissRunnable().run();
    }

    public static BottomSheet createMuteAlert(BaseFragment baseFragment, long j, Theme.ResourcesProvider resourcesProvider) {
        if (baseFragment == null || baseFragment.getParentActivity() == null) {
            return null;
        }
        BottomSheet.Builder builder = new BottomSheet.Builder(baseFragment.getParentActivity(), false, resourcesProvider);
        builder.setTitle(LocaleController.getString("Notifications", NUM), true);
        builder.setItems(new CharSequence[]{LocaleController.formatString("MuteFor", NUM, LocaleController.formatPluralString("Hours", 1)), LocaleController.formatString("MuteFor", NUM, LocaleController.formatPluralString("Hours", 8)), LocaleController.formatString("MuteFor", NUM, LocaleController.formatPluralString("Days", 2)), LocaleController.getString("MuteDisable", NUM)}, new AlertsCreator$$ExternalSyntheticLambda4(j, baseFragment, resourcesProvider));
        return builder.create();
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$createMuteAlert$83(long j, BaseFragment baseFragment, Theme.ResourcesProvider resourcesProvider, DialogInterface dialogInterface, int i) {
        int i2 = 2;
        if (i == 0) {
            i2 = 0;
        } else if (i == 1) {
            i2 = 1;
        } else if (i != 2) {
            i2 = 3;
        }
        NotificationsController.getInstance(UserConfig.selectedAccount).setDialogNotificationsSettings(j, i2);
        if (BulletinFactory.canShowBulletin(baseFragment)) {
            BulletinFactory.createMuteBulletin(baseFragment, i2, 0, resourcesProvider).show();
        }
    }

    public static void sendReport(TLRPC$InputPeer tLRPC$InputPeer, int i, String str, ArrayList<Integer> arrayList) {
        TLRPC$TL_messages_report tLRPC$TL_messages_report = new TLRPC$TL_messages_report();
        tLRPC$TL_messages_report.peer = tLRPC$InputPeer;
        tLRPC$TL_messages_report.id.addAll(arrayList);
        tLRPC$TL_messages_report.message = str;
        if (i == 0) {
            tLRPC$TL_messages_report.reason = new TLRPC$TL_inputReportReasonSpam();
        } else if (i == 6) {
            tLRPC$TL_messages_report.reason = new TLRPC$TL_inputReportReasonFake();
        } else if (i == 1) {
            tLRPC$TL_messages_report.reason = new TLRPC$TL_inputReportReasonViolence();
        } else if (i == 2) {
            tLRPC$TL_messages_report.reason = new TLRPC$TL_inputReportReasonChildAbuse();
        } else if (i == 5) {
            tLRPC$TL_messages_report.reason = new TLRPC$TL_inputReportReasonPornography();
        } else if (i == 3) {
            tLRPC$TL_messages_report.reason = new TLRPC$TL_inputReportReasonIllegalDrugs();
        } else if (i == 4) {
            tLRPC$TL_messages_report.reason = new TLRPC$TL_inputReportReasonPersonalDetails();
        } else if (i == 100) {
            tLRPC$TL_messages_report.reason = new TLRPC$TL_inputReportReasonOther();
        }
        ConnectionsManager.getInstance(UserConfig.selectedAccount).sendRequest(tLRPC$TL_messages_report, AlertsCreator$$ExternalSyntheticLambda98.INSTANCE);
    }

    public static void createReportAlert(Context context, long j, int i, BaseFragment baseFragment, Runnable runnable) {
        createReportAlert(context, j, i, baseFragment, (Theme.ResourcesProvider) null, runnable);
    }

    public static void createReportAlert(Context context, long j, int i, BaseFragment baseFragment, Theme.ResourcesProvider resourcesProvider, Runnable runnable) {
        CharSequence[] charSequenceArr;
        int[] iArr;
        int[] iArr2;
        Context context2 = context;
        BaseFragment baseFragment2 = baseFragment;
        Runnable runnable2 = runnable;
        if (context2 != null && baseFragment2 != null) {
            BottomSheet.Builder builder = new BottomSheet.Builder(context2, true, resourcesProvider);
            builder.setDimBehind(runnable2 == null);
            builder.setOnPreDismissListener(new AlertsCreator$$ExternalSyntheticLambda43(runnable2));
            builder.setTitle(LocaleController.getString("ReportChat", NUM), true);
            if (i != 0) {
                iArr = new int[]{NUM, NUM, NUM, NUM, NUM, NUM, NUM};
                charSequenceArr = new CharSequence[]{LocaleController.getString("ReportChatSpam", NUM), LocaleController.getString("ReportChatViolence", NUM), LocaleController.getString("ReportChatChild", NUM), LocaleController.getString("ReportChatIllegalDrugs", NUM), LocaleController.getString("ReportChatPersonalDetails", NUM), LocaleController.getString("ReportChatPornography", NUM), LocaleController.getString("ReportChatOther", NUM)};
                iArr2 = new int[]{0, 1, 2, 3, 4, 5, 100};
            } else {
                iArr2 = new int[]{0, 6, 1, 2, 3, 4, 5, 100};
                iArr = new int[]{NUM, NUM, NUM, NUM, NUM, NUM, NUM, NUM};
                charSequenceArr = new CharSequence[]{LocaleController.getString("ReportChatSpam", NUM), LocaleController.getString("ReportChatFakeAccount", NUM), LocaleController.getString("ReportChatViolence", NUM), LocaleController.getString("ReportChatChild", NUM), LocaleController.getString("ReportChatIllegalDrugs", NUM), LocaleController.getString("ReportChatPersonalDetails", NUM), LocaleController.getString("ReportChatPornography", NUM), LocaleController.getString("ReportChatOther", NUM)};
            }
            builder.setItems(charSequenceArr, iArr, new AlertsCreator$$ExternalSyntheticLambda36(iArr2, i, baseFragment, context, j, resourcesProvider));
            baseFragment2.showDialog(builder.create());
        }
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$createReportAlert$85(Runnable runnable, DialogInterface dialogInterface) {
        if (runnable != null) {
            runnable.run();
        }
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v0, resolved type: org.telegram.tgnet.TLRPC$TL_account_reportPeer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v2, resolved type: org.telegram.tgnet.TLRPC$TL_messages_report} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v3, resolved type: org.telegram.tgnet.TLRPC$TL_account_reportPeer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v4, resolved type: org.telegram.tgnet.TLRPC$TL_account_reportPeer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v5, resolved type: org.telegram.tgnet.TLRPC$TL_account_reportPeer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v6, resolved type: org.telegram.tgnet.TLRPC$TL_account_reportPeer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v7, resolved type: org.telegram.tgnet.TLRPC$TL_account_reportPeer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v8, resolved type: org.telegram.tgnet.TLRPC$TL_account_reportPeer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v9, resolved type: org.telegram.tgnet.TLRPC$TL_account_reportPeer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v10, resolved type: org.telegram.tgnet.TLRPC$TL_account_reportPeer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v11, resolved type: org.telegram.tgnet.TLRPC$TL_account_reportPeer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v12, resolved type: org.telegram.tgnet.TLRPC$TL_account_reportPeer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v13, resolved type: org.telegram.tgnet.TLRPC$TL_account_reportPeer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v14, resolved type: org.telegram.tgnet.TLRPC$TL_account_reportPeer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v15, resolved type: org.telegram.tgnet.TLRPC$TL_account_reportPeer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v16, resolved type: org.telegram.tgnet.TLRPC$TL_account_reportPeer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v17, resolved type: org.telegram.tgnet.TLRPC$TL_account_reportPeer} */
    /* access modifiers changed from: private */
    /* JADX WARNING: Code restructure failed: missing block: B:36:0x009e, code lost:
        if (r2 != 4) goto L_0x00f6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:37:0x00a0, code lost:
        r12.reason = new org.telegram.tgnet.TLRPC$TL_inputReportReasonPersonalDetails();
        r12 = r12;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:51:0x00ed, code lost:
        if (r2 != 4) goto L_0x00f6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:52:0x00ef, code lost:
        r12.reason = new org.telegram.tgnet.TLRPC$TL_inputReportReasonPersonalDetails();
        r12 = r12;
     */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static /* synthetic */ void lambda$createReportAlert$87(int[] r7, int r8, org.telegram.ui.ActionBar.BaseFragment r9, android.content.Context r10, long r11, org.telegram.ui.ActionBar.Theme.ResourcesProvider r13, android.content.DialogInterface r14, int r15) {
        /*
            r2 = r7[r15]
            r7 = 4
            r14 = 3
            r15 = 5
            r0 = 2
            r1 = 1
            if (r8 != 0) goto L_0x001f
            if (r2 == 0) goto L_0x0015
            if (r2 == r1) goto L_0x0015
            if (r2 == r0) goto L_0x0015
            if (r2 == r15) goto L_0x0015
            if (r2 == r14) goto L_0x0015
            if (r2 != r7) goto L_0x001f
        L_0x0015:
            boolean r3 = r9 instanceof org.telegram.ui.ChatActivity
            if (r3 == 0) goto L_0x001f
            org.telegram.ui.ChatActivity r9 = (org.telegram.ui.ChatActivity) r9
            r9.openReportChat(r2)
            return
        L_0x001f:
            r3 = 6
            r4 = 100
            if (r8 != 0) goto L_0x0028
            if (r2 == r4) goto L_0x002c
            if (r2 == r3) goto L_0x002c
        L_0x0028:
            if (r8 == 0) goto L_0x0049
            if (r2 != r4) goto L_0x0049
        L_0x002c:
            boolean r7 = r9 instanceof org.telegram.ui.ChatActivity
            if (r7 == 0) goto L_0x003b
            android.app.Activity r7 = r9.getParentActivity()
            int r13 = r9.getClassGuid()
            org.telegram.messenger.AndroidUtilities.requestAdjustNothing(r7, r13)
        L_0x003b:
            org.telegram.ui.Components.AlertsCreator$27 r7 = new org.telegram.ui.Components.AlertsCreator$27
            r0 = r7
            r1 = r10
            r3 = r9
            r4 = r8
            r5 = r11
            r0.<init>(r1, r2, r3, r4, r5)
            r9.showDialog(r7)
            return
        L_0x0049:
            int r10 = org.telegram.messenger.UserConfig.selectedAccount
            org.telegram.messenger.MessagesController r10 = org.telegram.messenger.MessagesController.getInstance(r10)
            org.telegram.tgnet.TLRPC$InputPeer r10 = r10.getInputPeer((long) r11)
            java.lang.String r11 = ""
            if (r8 == 0) goto L_0x00a8
            org.telegram.tgnet.TLRPC$TL_messages_report r12 = new org.telegram.tgnet.TLRPC$TL_messages_report
            r12.<init>()
            r12.peer = r10
            java.util.ArrayList<java.lang.Integer> r10 = r12.id
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            r10.add(r8)
            r12.message = r11
            if (r2 != 0) goto L_0x0074
            org.telegram.tgnet.TLRPC$TL_inputReportReasonSpam r7 = new org.telegram.tgnet.TLRPC$TL_inputReportReasonSpam
            r7.<init>()
            r12.reason = r7
            goto L_0x00f6
        L_0x0074:
            if (r2 != r1) goto L_0x007f
            org.telegram.tgnet.TLRPC$TL_inputReportReasonViolence r7 = new org.telegram.tgnet.TLRPC$TL_inputReportReasonViolence
            r7.<init>()
            r12.reason = r7
            goto L_0x00f6
        L_0x007f:
            if (r2 != r0) goto L_0x008a
            org.telegram.tgnet.TLRPC$TL_inputReportReasonChildAbuse r7 = new org.telegram.tgnet.TLRPC$TL_inputReportReasonChildAbuse
            r7.<init>()
            r12.reason = r7
            goto L_0x00f6
        L_0x008a:
            if (r2 != r15) goto L_0x0094
            org.telegram.tgnet.TLRPC$TL_inputReportReasonPornography r7 = new org.telegram.tgnet.TLRPC$TL_inputReportReasonPornography
            r7.<init>()
            r12.reason = r7
            goto L_0x00f6
        L_0x0094:
            if (r2 != r14) goto L_0x009e
            org.telegram.tgnet.TLRPC$TL_inputReportReasonIllegalDrugs r7 = new org.telegram.tgnet.TLRPC$TL_inputReportReasonIllegalDrugs
            r7.<init>()
            r12.reason = r7
            goto L_0x00f6
        L_0x009e:
            if (r2 != r7) goto L_0x00f6
            org.telegram.tgnet.TLRPC$TL_inputReportReasonPersonalDetails r7 = new org.telegram.tgnet.TLRPC$TL_inputReportReasonPersonalDetails
            r7.<init>()
            r12.reason = r7
            goto L_0x00f6
        L_0x00a8:
            org.telegram.tgnet.TLRPC$TL_account_reportPeer r12 = new org.telegram.tgnet.TLRPC$TL_account_reportPeer
            r12.<init>()
            r12.peer = r10
            r12.message = r11
            if (r2 != 0) goto L_0x00bb
            org.telegram.tgnet.TLRPC$TL_inputReportReasonSpam r7 = new org.telegram.tgnet.TLRPC$TL_inputReportReasonSpam
            r7.<init>()
            r12.reason = r7
            goto L_0x00f6
        L_0x00bb:
            if (r2 != r3) goto L_0x00c5
            org.telegram.tgnet.TLRPC$TL_inputReportReasonFake r7 = new org.telegram.tgnet.TLRPC$TL_inputReportReasonFake
            r7.<init>()
            r12.reason = r7
            goto L_0x00f6
        L_0x00c5:
            if (r2 != r1) goto L_0x00cf
            org.telegram.tgnet.TLRPC$TL_inputReportReasonViolence r7 = new org.telegram.tgnet.TLRPC$TL_inputReportReasonViolence
            r7.<init>()
            r12.reason = r7
            goto L_0x00f6
        L_0x00cf:
            if (r2 != r0) goto L_0x00d9
            org.telegram.tgnet.TLRPC$TL_inputReportReasonChildAbuse r7 = new org.telegram.tgnet.TLRPC$TL_inputReportReasonChildAbuse
            r7.<init>()
            r12.reason = r7
            goto L_0x00f6
        L_0x00d9:
            if (r2 != r15) goto L_0x00e3
            org.telegram.tgnet.TLRPC$TL_inputReportReasonPornography r7 = new org.telegram.tgnet.TLRPC$TL_inputReportReasonPornography
            r7.<init>()
            r12.reason = r7
            goto L_0x00f6
        L_0x00e3:
            if (r2 != r14) goto L_0x00ed
            org.telegram.tgnet.TLRPC$TL_inputReportReasonIllegalDrugs r7 = new org.telegram.tgnet.TLRPC$TL_inputReportReasonIllegalDrugs
            r7.<init>()
            r12.reason = r7
            goto L_0x00f6
        L_0x00ed:
            if (r2 != r7) goto L_0x00f6
            org.telegram.tgnet.TLRPC$TL_inputReportReasonPersonalDetails r7 = new org.telegram.tgnet.TLRPC$TL_inputReportReasonPersonalDetails
            r7.<init>()
            r12.reason = r7
        L_0x00f6:
            int r7 = org.telegram.messenger.UserConfig.selectedAccount
            org.telegram.tgnet.ConnectionsManager r7 = org.telegram.tgnet.ConnectionsManager.getInstance(r7)
            org.telegram.ui.Components.AlertsCreator$$ExternalSyntheticLambda96 r8 = org.telegram.ui.Components.AlertsCreator$$ExternalSyntheticLambda96.INSTANCE
            r7.sendRequest(r12, r8)
            boolean r7 = r9 instanceof org.telegram.ui.ChatActivity
            if (r7 == 0) goto L_0x0114
            org.telegram.ui.ChatActivity r9 = (org.telegram.ui.ChatActivity) r9
            org.telegram.ui.Components.UndoView r7 = r9.getUndoView()
            r8 = 0
            r10 = 74
            r11 = 0
            r7.showWithAction((long) r8, (int) r10, (java.lang.Runnable) r11)
            goto L_0x011f
        L_0x0114:
            org.telegram.ui.Components.BulletinFactory r7 = org.telegram.ui.Components.BulletinFactory.of(r9)
            org.telegram.ui.Components.Bulletin r7 = r7.createReportSent(r13)
            r7.show()
        L_0x011f:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.AlertsCreator.lambda$createReportAlert$87(int[], int, org.telegram.ui.ActionBar.BaseFragment, android.content.Context, long, org.telegram.ui.ActionBar.Theme$ResourcesProvider, android.content.DialogInterface, int):void");
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

    public static void showSendMediaAlert(int i, BaseFragment baseFragment, Theme.ResourcesProvider resourcesProvider) {
        if (i != 0) {
            AlertDialog.Builder builder = new AlertDialog.Builder(baseFragment.getParentActivity(), resourcesProvider);
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
                        c = 0;
                        break;
                    }
                    break;
                case -2012133105:
                    if (str.equals("CHANNELS_ADMIN_PUBLIC_TOO_MUCH")) {
                        c = 1;
                        break;
                    }
                    break;
                case -1763467626:
                    if (str.equals("USERS_TOO_FEW")) {
                        c = 2;
                        break;
                    }
                    break;
                case -538116776:
                    if (str.equals("USER_BLOCKED")) {
                        c = 3;
                        break;
                    }
                    break;
                case -512775857:
                    if (str.equals("USER_RESTRICTED")) {
                        c = 4;
                        break;
                    }
                    break;
                case -454039871:
                    if (str.equals("PEER_FLOOD")) {
                        c = 5;
                        break;
                    }
                    break;
                case -420079733:
                    if (str.equals("BOTS_TOO_MUCH")) {
                        c = 6;
                        break;
                    }
                    break;
                case 98635865:
                    if (str.equals("USER_KICKED")) {
                        c = 7;
                        break;
                    }
                    break;
                case 517420851:
                    if (str.equals("USER_BOT")) {
                        c = 8;
                        break;
                    }
                    break;
                case 845559454:
                    if (str.equals("YOU_BLOCKED_USER")) {
                        c = 9;
                        break;
                    }
                    break;
                case 916342611:
                    if (str.equals("USER_ADMIN_INVALID")) {
                        c = 10;
                        break;
                    }
                    break;
                case 1047173446:
                    if (str.equals("CHAT_ADMIN_BAN_REQUIRED")) {
                        c = 11;
                        break;
                    }
                    break;
                case 1167301807:
                    if (str.equals("USERS_TOO_MUCH")) {
                        c = 12;
                        break;
                    }
                    break;
                case 1227003815:
                    if (str.equals("USER_ID_INVALID")) {
                        c = 13;
                        break;
                    }
                    break;
                case 1253103379:
                    if (str.equals("ADMINS_TOO_MUCH")) {
                        c = 14;
                        break;
                    }
                    break;
                case 1355367367:
                    if (str.equals("CHANNELS_TOO_MUCH")) {
                        c = 15;
                        break;
                    }
                    break;
                case 1377621075:
                    if (str.equals("USER_CHANNELS_TOO_MUCH")) {
                        c = 16;
                        break;
                    }
                    break;
                case 1623167701:
                    if (str.equals("USER_NOT_MUTUAL_CONTACT")) {
                        c = 17;
                        break;
                    }
                    break;
                case 1754587486:
                    if (str.equals("CHAT_ADMIN_INVITE_REQUIRED")) {
                        c = 18;
                        break;
                    }
                    break;
                case 1916725894:
                    if (str.equals("USER_PRIVACY_RESTRICTED")) {
                        c = 19;
                        break;
                    }
                    break;
                case 1965565720:
                    if (str.equals("USER_ALREADY_PARTICIPANT")) {
                        c = 20;
                        break;
                    }
                    break;
            }
            switch (c) {
                case 0:
                    builder.setMessage(LocaleController.getString("LocatedChannelsTooMuch", NUM));
                    break;
                case 1:
                    builder.setMessage(LocaleController.getString("PublicChannelsTooMuch", NUM));
                    break;
                case 2:
                    builder.setMessage(LocaleController.getString("CreateGroupError", NUM));
                    break;
                case 3:
                case 8:
                case 13:
                    if (!z) {
                        builder.setMessage(LocaleController.getString("GroupUserCantAdd", NUM));
                        break;
                    } else {
                        builder.setMessage(LocaleController.getString("ChannelUserCantAdd", NUM));
                        break;
                    }
                case 4:
                    builder.setMessage(LocaleController.getString("UserRestricted", NUM));
                    break;
                case 5:
                    builder.setMessage(LocaleController.getString("NobodyLikesSpam2", NUM));
                    builder.setNegativeButton(LocaleController.getString("MoreInfo", NUM), new AlertsCreator$$ExternalSyntheticLambda25(baseFragment));
                    break;
                case 6:
                    if (!z) {
                        builder.setMessage(LocaleController.getString("GroupUserCantBot", NUM));
                        break;
                    } else {
                        builder.setMessage(LocaleController.getString("ChannelUserCantBot", NUM));
                        break;
                    }
                case 7:
                case 11:
                    if (!(tLObject instanceof TLRPC$TL_channels_inviteToChannel)) {
                        builder.setMessage(LocaleController.getString("AddAdminErrorBlacklisted", NUM));
                        break;
                    } else {
                        builder.setMessage(LocaleController.getString("AddUserErrorBlacklisted", NUM));
                        break;
                    }
                case 9:
                    builder.setMessage(LocaleController.getString("YouBlockedUser", NUM));
                    break;
                case 10:
                    builder.setMessage(LocaleController.getString("AddBannedErrorAdmin", NUM));
                    break;
                case 12:
                    if (!z) {
                        builder.setMessage(LocaleController.getString("GroupUserAddLimit", NUM));
                        break;
                    } else {
                        builder.setMessage(LocaleController.getString("ChannelUserAddLimit", NUM));
                        break;
                    }
                case 14:
                    if (!z) {
                        builder.setMessage(LocaleController.getString("GroupUserCantAdmin", NUM));
                        break;
                    } else {
                        builder.setMessage(LocaleController.getString("ChannelUserCantAdmin", NUM));
                        break;
                    }
                case 15:
                    builder.setTitle(LocaleController.getString("ChannelTooMuchTitle", NUM));
                    if (!(tLObject instanceof TLRPC$TL_channels_createChannel)) {
                        builder.setMessage(LocaleController.getString("ChannelTooMuchJoin", NUM));
                        break;
                    } else {
                        builder.setMessage(LocaleController.getString("ChannelTooMuch", NUM));
                        break;
                    }
                case 16:
                    builder.setTitle(LocaleController.getString("ChannelTooMuchTitle", NUM));
                    builder.setMessage(LocaleController.getString("UserChannelTooMuchJoin", NUM));
                    break;
                case 17:
                    if (!z) {
                        builder.setMessage(LocaleController.getString("GroupUserLeftError", NUM));
                        break;
                    } else {
                        builder.setMessage(LocaleController.getString("ChannelUserLeftError", NUM));
                        break;
                    }
                case 18:
                    builder.setMessage(LocaleController.getString("AddAdminErrorNotAMember", NUM));
                    break;
                case 19:
                    if (!z) {
                        builder.setMessage(LocaleController.getString("InviteToGroupError", NUM));
                        break;
                    } else {
                        builder.setMessage(LocaleController.getString("InviteToChannelError", NUM));
                        break;
                    }
                case 20:
                    builder.setTitle(LocaleController.getString("VoipGroupVoiceChat", NUM));
                    builder.setMessage(LocaleController.getString("VoipGroupInviteAlreadyParticipant", NUM));
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
            } else if (DialogObject.isChatDialog(j)) {
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
            radioColorCell.setOnClickListener(new AlertsCreator$$ExternalSyntheticLambda49(linearLayout, iArr));
            i4++;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder((Context) activity2);
        builder.setTitle(LocaleController.getString("LedColor", NUM));
        builder.setView(linearLayout);
        builder.setPositiveButton(LocaleController.getString("Set", NUM), new AlertsCreator$$ExternalSyntheticLambda5(j, iArr, i, runnable));
        builder.setNeutralButton(LocaleController.getString("LedDisabled", NUM), new AlertsCreator$$ExternalSyntheticLambda2(j2, i3, runnable2));
        if (j2 != 0) {
            builder.setNegativeButton(LocaleController.getString("Default", NUM), new AlertsCreator$$ExternalSyntheticLambda3(j2, runnable2));
        }
        return builder.create();
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$createColorSelectDialog$89(LinearLayout linearLayout, int[] iArr, View view) {
        int childCount = linearLayout.getChildCount();
        for (int i = 0; i < childCount; i++) {
            RadioColorCell radioColorCell = (RadioColorCell) linearLayout.getChildAt(i);
            radioColorCell.setChecked(radioColorCell == view, true);
        }
        iArr[0] = TextColorCell.colorsToSave[((Integer) view.getTag()).intValue()];
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$createColorSelectDialog$90(long j, int[] iArr, int i, Runnable runnable, DialogInterface dialogInterface, int i2) {
        SharedPreferences.Editor edit = MessagesController.getNotificationsSettings(UserConfig.selectedAccount).edit();
        if (j != 0) {
            edit.putInt("color_" + j, iArr[0]);
            NotificationsController.getInstance(UserConfig.selectedAccount).deleteNotificationChannel(j);
        } else {
            if (i == 1) {
                edit.putInt("MessagesLed", iArr[0]);
            } else if (i == 0) {
                edit.putInt("GroupLed", iArr[0]);
            } else {
                edit.putInt("ChannelLed", iArr[0]);
            }
            NotificationsController.getInstance(UserConfig.selectedAccount).deleteNotificationChannelGlobal(i);
        }
        edit.commit();
        if (runnable != null) {
            runnable.run();
        }
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$createColorSelectDialog$91(long j, int i, Runnable runnable, DialogInterface dialogInterface, int i2) {
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

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$createColorSelectDialog$92(long j, Runnable runnable, DialogInterface dialogInterface, int i) {
        SharedPreferences.Editor edit = MessagesController.getNotificationsSettings(UserConfig.selectedAccount).edit();
        edit.remove("color_" + j);
        edit.commit();
        if (runnable != null) {
            runnable.run();
        }
    }

    public static Dialog createVibrationSelectDialog(Activity activity, long j, boolean z, boolean z2, Runnable runnable) {
        String str;
        if (j != 0) {
            str = "vibrate_" + j;
        } else {
            str = z ? "vibrate_group" : "vibrate_messages";
        }
        return createVibrationSelectDialog(activity, j, str, runnable);
    }

    public static Dialog createVibrationSelectDialog(Activity activity, long j, String str, Runnable runnable) {
        String[] strArr;
        Activity activity2 = activity;
        String str2 = str;
        SharedPreferences notificationsSettings = MessagesController.getNotificationsSettings(UserConfig.selectedAccount);
        int[] iArr = new int[1];
        int i = 0;
        if (j != 0) {
            iArr[0] = notificationsSettings.getInt(str2, 0);
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
            AlertsCreator$$ExternalSyntheticLambda60 alertsCreator$$ExternalSyntheticLambda60 = r1;
            AlertsCreator$$ExternalSyntheticLambda60 alertsCreator$$ExternalSyntheticLambda602 = new AlertsCreator$$ExternalSyntheticLambda60(iArr, j, str, builder, runnable);
            radioColorCell.setOnClickListener(alertsCreator$$ExternalSyntheticLambda60);
            i2++;
            i = 0;
        }
        builder.setTitle(LocaleController.getString("Vibrate", NUM));
        builder.setView(linearLayout);
        builder.setPositiveButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
        return builder.create();
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$createVibrationSelectDialog$93(int[] iArr, long j, String str, AlertDialog.Builder builder, Runnable runnable, View view) {
        iArr[0] = ((Integer) view.getTag()).intValue();
        SharedPreferences.Editor edit = MessagesController.getNotificationsSettings(UserConfig.selectedAccount).edit();
        if (j != 0) {
            if (iArr[0] == 0) {
                edit.putInt(str, 0);
            } else if (iArr[0] == 1) {
                edit.putInt(str, 1);
            } else if (iArr[0] == 2) {
                edit.putInt(str, 3);
            } else if (iArr[0] == 3) {
                edit.putInt(str, 2);
            }
            NotificationsController.getInstance(UserConfig.selectedAccount).deleteNotificationChannel(j);
        } else {
            if (iArr[0] == 0) {
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
            if (str.equals("vibrate_channel")) {
                NotificationsController.getInstance(UserConfig.selectedAccount).deleteNotificationChannelGlobal(2);
            } else if (str.equals("vibrate_group")) {
                NotificationsController.getInstance(UserConfig.selectedAccount).deleteNotificationChannelGlobal(0);
            } else {
                NotificationsController.getInstance(UserConfig.selectedAccount).deleteNotificationChannelGlobal(1);
            }
        }
        edit.commit();
        builder.getDismissRunnable().run();
        if (runnable != null) {
            runnable.run();
        }
    }

    public static Dialog createLocationUpdateDialog(Activity activity, TLRPC$User tLRPC$User, MessagesStorage.IntCallback intCallback, Theme.ResourcesProvider resourcesProvider) {
        Activity activity2 = activity;
        Theme.ResourcesProvider resourcesProvider2 = resourcesProvider;
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
        textView.setTextColor(resourcesProvider2 != null ? resourcesProvider2.getColorOrDefault("dialogTextBlack") : Theme.getColor("dialogTextBlack"));
        textView.setTextSize(1, 16.0f);
        int i = 5;
        textView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
        if (!LocaleController.isRTL) {
            i = 3;
        }
        linearLayout.addView(textView, LayoutHelper.createLinear(-2, -2, i | 48, 24, 0, 24, 8));
        int i2 = 0;
        while (i2 < 3) {
            RadioColorCell radioColorCell = new RadioColorCell(activity2, resourcesProvider2);
            radioColorCell.setPadding(AndroidUtilities.dp(4.0f), 0, AndroidUtilities.dp(4.0f), 0);
            radioColorCell.setTag(Integer.valueOf(i2));
            radioColorCell.setCheckColor(resourcesProvider2 != null ? resourcesProvider2.getColorOrDefault("radioBackground") : Theme.getColor("radioBackground"), resourcesProvider2 != null ? resourcesProvider2.getColorOrDefault("dialogRadioBackgroundChecked") : Theme.getColor("dialogRadioBackgroundChecked"));
            radioColorCell.setTextAndValue(strArr[i2], iArr[0] == i2);
            linearLayout.addView(radioColorCell);
            radioColorCell.setOnClickListener(new AlertsCreator$$ExternalSyntheticLambda61(iArr, linearLayout));
            i2++;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(activity2, resourcesProvider2);
        builder.setTopImage(new ShareLocationDrawable(activity2, 0), resourcesProvider2 != null ? resourcesProvider2.getColorOrDefault("dialogTopBackground") : Theme.getColor("dialogTopBackground"));
        builder.setView(linearLayout);
        builder.setPositiveButton(LocaleController.getString("ShareFile", NUM), new AlertsCreator$$ExternalSyntheticLambda37(iArr, intCallback));
        builder.setNeutralButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
        return builder.create();
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$createLocationUpdateDialog$94(int[] iArr, LinearLayout linearLayout, View view) {
        iArr[0] = ((Integer) view.getTag()).intValue();
        int childCount = linearLayout.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childAt = linearLayout.getChildAt(i);
            if (childAt instanceof RadioColorCell) {
                ((RadioColorCell) childAt).setChecked(childAt == view, true);
            }
        }
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$createLocationUpdateDialog$95(int[] iArr, MessagesStorage.IntCallback intCallback, DialogInterface dialogInterface, int i) {
        int i2;
        if (iArr[0] == 0) {
            i2 = 900;
        } else {
            i2 = iArr[0] == 1 ? 3600 : 28800;
        }
        intCallback.run(i2);
    }

    public static AlertDialog.Builder createBackgroundLocationPermissionDialog(Activity activity, TLRPC$User tLRPC$User, Runnable runnable, Theme.ResourcesProvider resourcesProvider) {
        if (activity == null || Build.VERSION.SDK_INT < 29) {
            return null;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(activity, resourcesProvider);
        String readRes = RLottieDrawable.readRes((File) null, Theme.getCurrentTheme().isDark() ? NUM : NUM);
        String readRes2 = RLottieDrawable.readRes((File) null, Theme.getCurrentTheme().isDark() ? NUM : NUM);
        FrameLayout frameLayout = new FrameLayout(activity);
        frameLayout.setClipToOutline(true);
        frameLayout.setOutlineProvider(new ViewOutlineProvider() {
            public void getOutline(View view, Outline outline) {
                outline.setRoundRect(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight() + AndroidUtilities.dp(6.0f), (float) AndroidUtilities.dp(6.0f));
            }
        });
        View view = new View(activity);
        view.setBackground(SvgHelper.getDrawable(readRes));
        frameLayout.addView(view, LayoutHelper.createFrame(-1, -1.0f, 51, 0.0f, 0.0f, 0.0f, 0.0f));
        View view2 = new View(activity);
        view2.setBackground(SvgHelper.getDrawable(readRes2));
        frameLayout.addView(view2, LayoutHelper.createFrame(60, 82.0f, 17, 0.0f, 0.0f, 0.0f, 0.0f));
        BackupImageView backupImageView = new BackupImageView(activity);
        backupImageView.setRoundRadius(AndroidUtilities.dp(26.0f));
        backupImageView.setForUserOrChat(tLRPC$User, new AvatarDrawable(tLRPC$User));
        frameLayout.addView(backupImageView, LayoutHelper.createFrame(52, 52.0f, 17, 0.0f, 0.0f, 0.0f, 11.0f));
        builder.setTopView(frameLayout);
        builder.setTopViewAspectRatio(0.37820512f);
        builder.setMessage(AndroidUtilities.replaceTags(LocaleController.getString(NUM)));
        builder.setPositiveButton(LocaleController.getString(NUM), new AlertsCreator$$ExternalSyntheticLambda7(activity));
        builder.setNegativeButton(LocaleController.getString("Cancel", NUM), new AlertsCreator$$ExternalSyntheticLambda15(runnable));
        return builder;
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$createBackgroundLocationPermissionDialog$96(Activity activity, DialogInterface dialogInterface, int i) {
        if (activity.checkSelfPermission("android.permission.ACCESS_BACKGROUND_LOCATION") != 0) {
            activity.requestPermissions(new String[]{"android.permission.ACCESS_BACKGROUND_LOCATION"}, 30);
        }
    }

    public static AlertDialog.Builder createGigagroupConvertAlert(Activity activity, DialogInterface.OnClickListener onClickListener, DialogInterface.OnClickListener onClickListener2) {
        AlertDialog.Builder builder = new AlertDialog.Builder((Context) activity);
        String readRes = RLottieDrawable.readRes((File) null, NUM);
        FrameLayout frameLayout = new FrameLayout(activity);
        if (Build.VERSION.SDK_INT >= 21) {
            frameLayout.setClipToOutline(true);
            frameLayout.setOutlineProvider(new ViewOutlineProvider() {
                public void getOutline(View view, Outline outline) {
                    outline.setRoundRect(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight() + AndroidUtilities.dp(6.0f), (float) AndroidUtilities.dp(6.0f));
                }
            });
        }
        View view = new View(activity);
        view.setBackground(new BitmapDrawable(SvgHelper.getBitmap(readRes, AndroidUtilities.dp(320.0f), AndroidUtilities.dp(127.17949f), false)));
        frameLayout.addView(view, LayoutHelper.createFrame(-1, -1.0f, 0, -1.0f, -1.0f, -1.0f, -1.0f));
        builder.setTopView(frameLayout);
        builder.setTopViewAspectRatio(0.3974359f);
        builder.setTitle(LocaleController.getString("GigagroupAlertTitle", NUM));
        builder.setMessage(AndroidUtilities.replaceTags(LocaleController.getString("GigagroupAlertText", NUM)));
        builder.setPositiveButton(LocaleController.getString("GigagroupAlertLearnMore", NUM), onClickListener);
        builder.setNegativeButton(LocaleController.getString("Cancel", NUM), onClickListener2);
        return builder;
    }

    public static AlertDialog.Builder createDrawOverlayPermissionDialog(Activity activity, DialogInterface.OnClickListener onClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder((Context) activity);
        String readRes = RLottieDrawable.readRes((File) null, NUM);
        FrameLayout frameLayout = new FrameLayout(activity);
        frameLayout.setBackground(new GradientDrawable(GradientDrawable.Orientation.BL_TR, new int[]{-14535089, -14527894}));
        frameLayout.setClipToOutline(true);
        frameLayout.setOutlineProvider(new ViewOutlineProvider() {
            public void getOutline(View view, Outline outline) {
                outline.setRoundRect(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight() + AndroidUtilities.dp(6.0f), AndroidUtilities.dpf2(6.0f));
            }
        });
        View view = new View(activity);
        view.setBackground(new BitmapDrawable(SvgHelper.getBitmap(readRes, AndroidUtilities.dp(320.0f), AndroidUtilities.dp(161.36752f), false)));
        frameLayout.addView(view, LayoutHelper.createFrame(-1, -1.0f, 0, -1.0f, -1.0f, -1.0f, -1.0f));
        builder.setTopView(frameLayout);
        builder.setTitle(LocaleController.getString("PermissionDrawAboveOtherAppsTitle", NUM));
        builder.setMessage(LocaleController.getString("PermissionDrawAboveOtherApps", NUM));
        builder.setPositiveButton(LocaleController.getString("Enable", NUM), new AlertsCreator$$ExternalSyntheticLambda6(activity));
        builder.notDrawBackgroundOnTopView(true);
        builder.setNegativeButton(LocaleController.getString("Cancel", NUM), onClickListener);
        builder.setTopViewAspectRatio(0.50427353f);
        return builder;
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$createDrawOverlayPermissionDialog$98(Activity activity, DialogInterface dialogInterface, int i) {
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
        String readRes = RLottieDrawable.readRes((File) null, NUM);
        final GroupCallPipButton groupCallPipButton = new GroupCallPipButton(context2, 0, true);
        groupCallPipButton.setImportantForAccessibility(2);
        AnonymousClass31 r8 = new FrameLayout(context2) {
            /* access modifiers changed from: protected */
            public void onLayout(boolean z, int i, int i2, int i3, int i4) {
                super.onLayout(z, i, i2, i3, i4);
                groupCallPipButton.setTranslationY((((float) getMeasuredHeight()) * 0.28f) - (((float) groupCallPipButton.getMeasuredWidth()) / 2.0f));
                groupCallPipButton.setTranslationX((((float) getMeasuredWidth()) * 0.82f) - (((float) groupCallPipButton.getMeasuredWidth()) / 2.0f));
            }
        };
        r8.setBackground(new GradientDrawable(GradientDrawable.Orientation.BL_TR, new int[]{-15128003, -15118002}));
        r8.setClipToOutline(true);
        r8.setOutlineProvider(new ViewOutlineProvider() {
            public void getOutline(View view, Outline outline) {
                outline.setRoundRect(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight() + AndroidUtilities.dp(6.0f), AndroidUtilities.dpf2(6.0f));
            }
        });
        View view = new View(context2);
        view.setBackground(new BitmapDrawable(SvgHelper.getBitmap(readRes, AndroidUtilities.dp(320.0f), AndroidUtilities.dp(184.61539f), false)));
        r8.addView(view, LayoutHelper.createFrame(-1, -1.0f, 0, -1.0f, -1.0f, -1.0f, -1.0f));
        r8.addView(groupCallPipButton, LayoutHelper.createFrame(117, 117.0f));
        builder.setTopView(r8);
        builder.setTitle(LocaleController.getString("PermissionDrawAboveOtherAppsGroupCallTitle", NUM));
        builder.setMessage(LocaleController.getString("PermissionDrawAboveOtherAppsGroupCall", NUM));
        builder.setPositiveButton(LocaleController.getString("Enable", NUM), new AlertsCreator$$ExternalSyntheticLambda8(context2));
        builder.notDrawBackgroundOnTopView(true);
        builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
        builder.setTopViewAspectRatio(0.5769231f);
        return builder;
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$createDrawOverlayGroupCallPermissionDialog$99(Context context, DialogInterface dialogInterface, int i) {
        if (context != null) {
            try {
                if (Build.VERSION.SDK_INT >= 23) {
                    Intent intent = new Intent("android.settings.action.MANAGE_OVERLAY_PERMISSION", Uri.parse("package:" + context.getPackageName()));
                    Activity findActivity = AndroidUtilities.findActivity(context);
                    if (findActivity instanceof LaunchActivity) {
                        findActivity.startActivityForResult(intent, 105);
                    } else {
                        context.startActivity(intent);
                    }
                }
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        }
    }

    public static AlertDialog.Builder createContactsPermissionDialog(Activity activity, MessagesStorage.IntCallback intCallback) {
        AlertDialog.Builder builder = new AlertDialog.Builder((Context) activity);
        builder.setTopAnimation(NUM, 72, false, Theme.getColor("dialogTopBackground"));
        builder.setMessage(AndroidUtilities.replaceTags(LocaleController.getString("ContactsPermissionAlert", NUM)));
        builder.setPositiveButton(LocaleController.getString("ContactsPermissionAlertContinue", NUM), new AlertsCreator$$ExternalSyntheticLambda19(intCallback));
        builder.setNegativeButton(LocaleController.getString("ContactsPermissionAlertNotNow", NUM), new AlertsCreator$$ExternalSyntheticLambda20(intCallback));
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
            radioColorCell.setOnClickListener(new AlertsCreator$$ExternalSyntheticLambda62(iArr, linearLayout));
            i3++;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder((Context) launchActivity2);
        builder.setTitle(LocaleController.getString("LowDiskSpaceTitle", NUM));
        builder.setMessage(LocaleController.getString("LowDiskSpaceMessage", NUM));
        builder.setView(linearLayout);
        builder.setPositiveButton(LocaleController.getString("OK", NUM), new AlertsCreator$$ExternalSyntheticLambda35(iArr));
        builder.setNeutralButton(LocaleController.getString("ClearMediaCache", NUM), new AlertsCreator$$ExternalSyntheticLambda30(launchActivity2));
        return builder.create();
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$createFreeSpaceDialog$102(int[] iArr, LinearLayout linearLayout, View view) {
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
            if (i3 == 1) {
                iArr[0] = notificationsSettings.getInt("priority_messages", 1);
            } else if (i3 == 0) {
                iArr[0] = notificationsSettings.getInt("priority_group", 1);
            } else if (i3 == 2) {
                iArr[0] = notificationsSettings.getInt("priority_channel", 1);
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
            AlertsCreator$$ExternalSyntheticLambda59 alertsCreator$$ExternalSyntheticLambda59 = r1;
            AlertDialog.Builder builder2 = builder;
            AlertsCreator$$ExternalSyntheticLambda59 alertsCreator$$ExternalSyntheticLambda592 = new AlertsCreator$$ExternalSyntheticLambda59(iArr, j, i, notificationsSettings, builder2, runnable);
            radioColorCell.setOnClickListener(alertsCreator$$ExternalSyntheticLambda59);
            i5++;
            activity2 = activity;
            linearLayout = linearLayout;
            builder = builder2;
            i4 = 0;
            long j3 = j;
        }
        AlertDialog.Builder builder3 = builder;
        builder3.setTitle(LocaleController.getString("NotificationsImportance", NUM));
        builder3.setView(linearLayout);
        builder3.setPositiveButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
        return builder3.create();
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$createPrioritySelectDialog$105(int[] iArr, long j, int i, SharedPreferences sharedPreferences, AlertDialog.Builder builder, Runnable runnable, View view) {
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
            NotificationsController.getInstance(UserConfig.selectedAccount).deleteNotificationChannel(j);
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
            NotificationsController.getInstance(UserConfig.selectedAccount).deleteNotificationChannelGlobal(i);
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
            radioColorCell.setOnClickListener(new AlertsCreator$$ExternalSyntheticLambda58(iArr, i, builder, runnable));
            i2++;
        }
        builder.setTitle(LocaleController.getString("PopupNotification", NUM));
        builder.setView(linearLayout);
        builder.setPositiveButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
        return builder.create();
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$createPopupSelectDialog$106(int[] iArr, int i, AlertDialog.Builder builder, Runnable runnable, View view) {
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
            radioColorCell.setOnClickListener(new AlertsCreator$$ExternalSyntheticLambda52(builder, onClickListener));
            i2++;
        }
        builder.setTitle(str);
        builder.setView(linearLayout);
        builder.setPositiveButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
        return builder.create();
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$createSingleChoiceDialog$107(AlertDialog.Builder builder, DialogInterface.OnClickListener onClickListener, View view) {
        int intValue = ((Integer) view.getTag()).intValue();
        builder.getDismissRunnable().run();
        onClickListener.onClick((DialogInterface) null, intValue);
    }

    public static AlertDialog.Builder createTTLAlert(Context context, TLRPC$EncryptedChat tLRPC$EncryptedChat, Theme.ResourcesProvider resourcesProvider) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context, resourcesProvider);
        builder.setTitle(LocaleController.getString("MessageLifetime", NUM));
        NumberPicker numberPicker = new NumberPicker(context);
        numberPicker.setMinValue(0);
        numberPicker.setMaxValue(20);
        int i = tLRPC$EncryptedChat.ttl;
        if (i > 0 && i < 16) {
            numberPicker.setValue(i);
        } else if (i == 30) {
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
        numberPicker.setFormatter(AlertsCreator$$ExternalSyntheticLambda115.INSTANCE);
        builder.setView(numberPicker);
        builder.setNegativeButton(LocaleController.getString("Done", NUM), new AlertsCreator$$ExternalSyntheticLambda21(tLRPC$EncryptedChat, numberPicker));
        return builder;
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ String lambda$createTTLAlert$108(int i) {
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

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$createTTLAlert$109(TLRPC$EncryptedChat tLRPC$EncryptedChat, NumberPicker numberPicker, DialogInterface dialogInterface, int i) {
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
                AccountSelectCell accountSelectCell = new AccountSelectCell(activity, false);
                accountSelectCell.setAccount(i, false);
                accountSelectCell.setPadding(AndroidUtilities.dp(14.0f), 0, AndroidUtilities.dp(14.0f), 0);
                accountSelectCell.setBackgroundDrawable(Theme.getSelectorDrawable(false));
                linearLayout.addView(accountSelectCell, LayoutHelper.createLinear(-1, 50));
                accountSelectCell.setOnClickListener(new AlertsCreator$$ExternalSyntheticLambda64(alertDialogArr, dismissRunnable, accountSelectDelegate));
            }
        }
        builder.setTitle(LocaleController.getString("SelectAccount", NUM));
        builder.setView(linearLayout);
        builder.setPositiveButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
        AlertDialog create = builder.create();
        alertDialogArr[0] = create;
        return create;
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$createAccountSelectDialog$110(AlertDialog[] alertDialogArr, Runnable runnable, AccountSelectDelegate accountSelectDelegate, View view) {
        if (alertDialogArr[0] != null) {
            alertDialogArr[0].setOnDismissListener((DialogInterface.OnDismissListener) null);
        }
        runnable.run();
        accountSelectDelegate.didSelectAccount(((AccountSelectCell) view).getAccountNumber());
    }

    /*  JADX ERROR: JadxRuntimeException in pass: IfRegionVisitor
        jadx.core.utils.exceptions.JadxRuntimeException: Don't wrap MOVE or CONST insns: 0x0676: MOVE  (r9v4 int) = (r23v1 int)
        	at jadx.core.dex.instructions.args.InsnArg.wrapArg(InsnArg.java:164)
        	at jadx.core.dex.visitors.shrink.CodeShrinkVisitor.assignInline(CodeShrinkVisitor.java:133)
        	at jadx.core.dex.visitors.shrink.CodeShrinkVisitor.checkInline(CodeShrinkVisitor.java:118)
        	at jadx.core.dex.visitors.shrink.CodeShrinkVisitor.shrinkBlock(CodeShrinkVisitor.java:65)
        	at jadx.core.dex.visitors.shrink.CodeShrinkVisitor.shrinkMethod(CodeShrinkVisitor.java:43)
        	at jadx.core.dex.visitors.regions.TernaryMod.makeTernaryInsn(TernaryMod.java:122)
        	at jadx.core.dex.visitors.regions.TernaryMod.visitRegion(TernaryMod.java:34)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverseIterativeStepInternal(DepthRegionTraversal.java:73)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverseIterativeStepInternal(DepthRegionTraversal.java:78)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverseIterativeStepInternal(DepthRegionTraversal.java:78)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverseIterativeStepInternal(DepthRegionTraversal.java:78)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverseIterativeStepInternal(DepthRegionTraversal.java:78)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverseIterativeStepInternal(DepthRegionTraversal.java:78)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverseIterative(DepthRegionTraversal.java:27)
        	at jadx.core.dex.visitors.regions.IfRegionVisitor.visit(IfRegionVisitor.java:31)
        */
    /* JADX WARNING: Removed duplicated region for block: B:105:0x01b2  */
    /* JADX WARNING: Removed duplicated region for block: B:106:0x01b4  */
    /* JADX WARNING: Removed duplicated region for block: B:179:0x0367  */
    /* JADX WARNING: Removed duplicated region for block: B:180:0x036c  */
    /* JADX WARNING: Removed duplicated region for block: B:183:0x0374  */
    /* JADX WARNING: Removed duplicated region for block: B:184:0x0379  */
    /* JADX WARNING: Removed duplicated region for block: B:20:0x0057  */
    /* JADX WARNING: Removed duplicated region for block: B:22:0x0061  */
    /* JADX WARNING: Removed duplicated region for block: B:27:0x0078  */
    /* JADX WARNING: Removed duplicated region for block: B:304:0x0643  */
    /* JADX WARNING: Removed duplicated region for block: B:305:0x0650  */
    /* JADX WARNING: Removed duplicated region for block: B:334:0x070f  */
    /* JADX WARNING: Removed duplicated region for block: B:335:0x0717  */
    /* JADX WARNING: Removed duplicated region for block: B:338:0x0747  */
    /* JADX WARNING: Removed duplicated region for block: B:33:0x008f  */
    /* JADX WARNING: Removed duplicated region for block: B:371:? A[RETURN, SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:52:0x00e7  */
    /* JADX WARNING: Removed duplicated region for block: B:53:0x00e9  */
    /* JADX WARNING: Removed duplicated region for block: B:55:0x00ec  */
    /* JADX WARNING: Removed duplicated region for block: B:56:0x00f3  */
    public static void createDeleteMessagesAlert(org.telegram.ui.ActionBar.BaseFragment r43, org.telegram.tgnet.TLRPC$User r44, org.telegram.tgnet.TLRPC$Chat r45, org.telegram.tgnet.TLRPC$EncryptedChat r46, org.telegram.tgnet.TLRPC$ChatFull r47, long r48, org.telegram.messenger.MessageObject r50, android.util.SparseArray<org.telegram.messenger.MessageObject>[] r51, org.telegram.messenger.MessageObject.GroupedMessages r52, boolean r53, int r54, java.lang.Runnable r55, java.lang.Runnable r56, org.telegram.ui.ActionBar.Theme.ResourcesProvider r57) {
        /*
            r15 = r43
            r3 = r44
            r14 = r45
            r7 = r46
            r9 = r50
            r11 = r52
            r0 = r54
            r13 = r56
            r12 = r57
            if (r15 == 0) goto L_0x0750
            if (r3 != 0) goto L_0x001c
            if (r14 != 0) goto L_0x001c
            if (r7 != 0) goto L_0x001c
            goto L_0x0750
        L_0x001c:
            android.app.Activity r1 = r43.getParentActivity()
            if (r1 != 0) goto L_0x0023
            return
        L_0x0023:
            int r10 = r43.getCurrentAccount()
            org.telegram.ui.ActionBar.AlertDialog$Builder r2 = new org.telegram.ui.ActionBar.AlertDialog$Builder
            r2.<init>(r1, r12)
            if (r13 == 0) goto L_0x0031
            r4 = 1056964608(0x3var_, float:0.5)
            goto L_0x0034
        L_0x0031:
            r4 = 1058642330(0x3var_a, float:0.6)
        L_0x0034:
            r2.setDimAlpha(r4)
            r8 = 1
            r6 = 0
            if (r11 == 0) goto L_0x0043
            java.util.ArrayList<org.telegram.messenger.MessageObject> r4 = r11.messages
            int r4 = r4.size()
        L_0x0041:
            r5 = r4
            goto L_0x0055
        L_0x0043:
            if (r9 == 0) goto L_0x0047
            r5 = 1
            goto L_0x0055
        L_0x0047:
            r4 = r51[r6]
            int r4 = r4.size()
            r5 = r51[r8]
            int r5 = r5.size()
            int r4 = r4 + r5
            goto L_0x0041
        L_0x0055:
            if (r7 == 0) goto L_0x0061
            int r4 = r7.id
            long r6 = (long) r4
            long r6 = org.telegram.messenger.DialogObject.makeEncryptedDialogId(r6)
        L_0x005e:
            r17 = r6
            goto L_0x006a
        L_0x0061:
            if (r3 == 0) goto L_0x0066
            long r6 = r3.id
            goto L_0x005e
        L_0x0066:
            long r6 = r14.id
            long r6 = -r6
            goto L_0x005e
        L_0x006a:
            org.telegram.tgnet.ConnectionsManager r4 = org.telegram.tgnet.ConnectionsManager.getInstance(r10)
            int r4 = r4.getCurrentTime()
            r6 = 86400(0x15180, float:1.21072E-40)
            r7 = 2
            if (r9 == 0) goto L_0x008f
            boolean r19 = r50.isDice()
            if (r19 == 0) goto L_0x008d
            org.telegram.tgnet.TLRPC$Message r8 = r9.messageOwner
            int r8 = r8.date
            int r8 = r4 - r8
            int r8 = java.lang.Math.abs(r8)
            if (r8 <= r6) goto L_0x008b
            goto L_0x008d
        L_0x008b:
            r6 = 0
            goto L_0x00d7
        L_0x008d:
            r6 = 1
            goto L_0x00d7
        L_0x008f:
            r8 = 0
            r20 = 0
        L_0x0092:
            if (r8 >= r7) goto L_0x00d5
            r7 = 0
        L_0x0095:
            r21 = r51[r8]
            int r6 = r21.size()
            if (r7 >= r6) goto L_0x00c9
            r6 = r51[r8]
            java.lang.Object r6 = r6.valueAt(r7)
            org.telegram.messenger.MessageObject r6 = (org.telegram.messenger.MessageObject) r6
            boolean r21 = r6.isDice()
            if (r21 == 0) goto L_0x00c3
            org.telegram.tgnet.TLRPC$Message r6 = r6.messageOwner
            int r6 = r6.date
            int r6 = r4 - r6
            int r6 = java.lang.Math.abs(r6)
            r11 = 86400(0x15180, float:1.21072E-40)
            if (r6 <= r11) goto L_0x00bb
            goto L_0x00c6
        L_0x00bb:
            int r7 = r7 + 1
            r11 = r52
            r6 = 86400(0x15180, float:1.21072E-40)
            goto L_0x0095
        L_0x00c3:
            r11 = 86400(0x15180, float:1.21072E-40)
        L_0x00c6:
            r20 = 1
            goto L_0x00cc
        L_0x00c9:
            r11 = 86400(0x15180, float:1.21072E-40)
        L_0x00cc:
            int r8 = r8 + 1
            r11 = r52
            r6 = 86400(0x15180, float:1.21072E-40)
            r7 = 2
            goto L_0x0092
        L_0x00d5:
            r6 = r20
        L_0x00d7:
            r7 = 3
            boolean[] r11 = new boolean[r7]
            r8 = 1
            boolean[] r7 = new boolean[r8]
            if (r3 == 0) goto L_0x00e9
            org.telegram.messenger.MessagesController r8 = org.telegram.messenger.MessagesController.getInstance(r10)
            boolean r8 = r8.canRevokePmInbox
            if (r8 == 0) goto L_0x00e9
            r8 = 1
            goto L_0x00ea
        L_0x00e9:
            r8 = 0
        L_0x00ea:
            if (r3 == 0) goto L_0x00f3
            org.telegram.messenger.MessagesController r12 = org.telegram.messenger.MessagesController.getInstance(r10)
            int r12 = r12.revokeTimePmLimit
            goto L_0x00f9
        L_0x00f3:
            org.telegram.messenger.MessagesController r12 = org.telegram.messenger.MessagesController.getInstance(r10)
            int r12 = r12.revokeTimeLimit
        L_0x00f9:
            if (r46 != 0) goto L_0x0107
            if (r3 == 0) goto L_0x0107
            if (r8 == 0) goto L_0x0107
            r13 = 2147483647(0x7fffffff, float:NaN)
            if (r12 != r13) goto L_0x0107
            r21 = 1
            goto L_0x0109
        L_0x0107:
            r21 = 0
        L_0x0109:
            java.lang.String r13 = "DeleteMessagesOption"
            r23 = 1098907648(0x41800000, float:16.0)
            r24 = 1090519040(0x41000000, float:8.0)
            java.lang.String r15 = ""
            r25 = r5
            if (r14 == 0) goto L_0x0449
            boolean r5 = r14.megagroup
            if (r5 == 0) goto L_0x0449
            if (r53 != 0) goto L_0x0449
            boolean r5 = org.telegram.messenger.ChatObject.canBlockUsers(r45)
            r26 = 0
            if (r9 == 0) goto L_0x01b9
            org.telegram.tgnet.TLRPC$Message r8 = r9.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r3 = r8.action
            r28 = r7
            if (r3 == 0) goto L_0x013b
            boolean r7 = r3 instanceof org.telegram.tgnet.TLRPC$TL_messageActionEmpty
            if (r7 != 0) goto L_0x013b
            boolean r7 = r3 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatDeleteUser
            if (r7 != 0) goto L_0x013b
            boolean r7 = r3 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatJoinedByLink
            if (r7 != 0) goto L_0x013b
            boolean r3 = r3 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatAddUser
            if (r3 == 0) goto L_0x018b
        L_0x013b:
            org.telegram.tgnet.TLRPC$Peer r3 = r8.from_id
            long r7 = r3.user_id
            int r29 = (r7 > r26 ? 1 : (r7 == r26 ? 0 : -1))
            if (r29 == 0) goto L_0x0157
            org.telegram.messenger.MessagesController r3 = org.telegram.messenger.MessagesController.getInstance(r10)
            org.telegram.tgnet.TLRPC$Message r7 = r9.messageOwner
            org.telegram.tgnet.TLRPC$Peer r7 = r7.from_id
            long r7 = r7.user_id
            java.lang.Long r7 = java.lang.Long.valueOf(r7)
            org.telegram.tgnet.TLRPC$User r3 = r3.getUser(r7)
        L_0x0155:
            r7 = 0
            goto L_0x018d
        L_0x0157:
            long r7 = r3.channel_id
            int r29 = (r7 > r26 ? 1 : (r7 == r26 ? 0 : -1))
            if (r29 == 0) goto L_0x0172
            org.telegram.messenger.MessagesController r3 = org.telegram.messenger.MessagesController.getInstance(r10)
            org.telegram.tgnet.TLRPC$Message r7 = r9.messageOwner
            org.telegram.tgnet.TLRPC$Peer r7 = r7.from_id
            long r7 = r7.channel_id
            java.lang.Long r7 = java.lang.Long.valueOf(r7)
            org.telegram.tgnet.TLRPC$Chat r3 = r3.getChat(r7)
        L_0x016f:
            r7 = r3
            r3 = 0
            goto L_0x018d
        L_0x0172:
            long r7 = r3.chat_id
            int r3 = (r7 > r26 ? 1 : (r7 == r26 ? 0 : -1))
            if (r3 == 0) goto L_0x018b
            org.telegram.messenger.MessagesController r3 = org.telegram.messenger.MessagesController.getInstance(r10)
            org.telegram.tgnet.TLRPC$Message r7 = r9.messageOwner
            org.telegram.tgnet.TLRPC$Peer r7 = r7.from_id
            long r7 = r7.chat_id
            java.lang.Long r7 = java.lang.Long.valueOf(r7)
            org.telegram.tgnet.TLRPC$Chat r3 = r3.getChat(r7)
            goto L_0x016f
        L_0x018b:
            r3 = 0
            goto L_0x0155
        L_0x018d:
            boolean r8 = r50.isSendError()
            if (r8 != 0) goto L_0x01b4
            long r26 = r50.getDialogId()
            int r8 = (r26 > r48 ? 1 : (r26 == r48 ? 0 : -1))
            if (r8 != 0) goto L_0x01b4
            org.telegram.tgnet.TLRPC$Message r8 = r9.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r8 = r8.action
            if (r8 == 0) goto L_0x01a5
            boolean r8 = r8 instanceof org.telegram.tgnet.TLRPC$TL_messageActionEmpty
            if (r8 == 0) goto L_0x01b4
        L_0x01a5:
            boolean r8 = r50.isOut()
            if (r8 == 0) goto L_0x01b4
            org.telegram.tgnet.TLRPC$Message r8 = r9.messageOwner
            int r8 = r8.date
            int r4 = r4 - r8
            if (r4 > r12) goto L_0x01b4
            r4 = 1
            goto L_0x01b5
        L_0x01b4:
            r4 = 0
        L_0x01b5:
            r26 = r13
            goto L_0x0251
        L_0x01b9:
            r28 = r7
            r3 = 1
            r29 = -1
        L_0x01be:
            if (r3 < 0) goto L_0x01f7
            r7 = 0
        L_0x01c1:
            r8 = r51[r3]
            int r8 = r8.size()
            r33 = -2
            if (r7 >= r8) goto L_0x01ef
            r8 = r51[r3]
            java.lang.Object r8 = r8.valueAt(r7)
            org.telegram.messenger.MessageObject r8 = (org.telegram.messenger.MessageObject) r8
            r31 = -1
            int r35 = (r29 > r31 ? 1 : (r29 == r31 ? 0 : -1))
            if (r35 != 0) goto L_0x01dd
            long r29 = r8.getFromChatId()
        L_0x01dd:
            int r35 = (r29 > r26 ? 1 : (r29 == r26 ? 0 : -1))
            if (r35 < 0) goto L_0x01ed
            long r35 = r8.getSenderId()
            int r8 = (r29 > r35 ? 1 : (r29 == r35 ? 0 : -1))
            if (r8 == 0) goto L_0x01ea
            goto L_0x01ed
        L_0x01ea:
            int r7 = r7 + 1
            goto L_0x01c1
        L_0x01ed:
            r29 = r33
        L_0x01ef:
            int r7 = (r29 > r33 ? 1 : (r29 == r33 ? 0 : -1))
            if (r7 != 0) goto L_0x01f4
            goto L_0x01f7
        L_0x01f4:
            int r3 = r3 + -1
            goto L_0x01be
        L_0x01f7:
            r3 = 0
            r7 = 1
        L_0x01f9:
            if (r7 < 0) goto L_0x0233
            r8 = 0
        L_0x01fc:
            r26 = r51[r7]
            int r9 = r26.size()
            if (r8 >= r9) goto L_0x022c
            r9 = r51[r7]
            java.lang.Object r9 = r9.valueAt(r8)
            org.telegram.messenger.MessageObject r9 = (org.telegram.messenger.MessageObject) r9
            r26 = r13
            r13 = 1
            if (r7 != r13) goto L_0x0225
            boolean r13 = r9.isOut()
            if (r13 == 0) goto L_0x0225
            org.telegram.tgnet.TLRPC$Message r9 = r9.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r13 = r9.action
            if (r13 != 0) goto L_0x0225
            int r9 = r9.date
            int r9 = r4 - r9
            if (r9 > r12) goto L_0x0225
            int r3 = r3 + 1
        L_0x0225:
            int r8 = r8 + 1
            r9 = r50
            r13 = r26
            goto L_0x01fc
        L_0x022c:
            r26 = r13
            int r7 = r7 + -1
            r9 = r50
            goto L_0x01f9
        L_0x0233:
            r26 = r13
            r7 = -1
            int r4 = (r29 > r7 ? 1 : (r29 == r7 ? 0 : -1))
            if (r4 == 0) goto L_0x024e
            org.telegram.messenger.MessagesController r4 = org.telegram.messenger.MessagesController.getInstance(r10)
            java.lang.Long r7 = java.lang.Long.valueOf(r29)
            org.telegram.tgnet.TLRPC$User r4 = r4.getUser(r7)
            r7 = 0
            r42 = r4
            r4 = r3
            r3 = r42
            goto L_0x0251
        L_0x024e:
            r4 = r3
            r3 = 0
            r7 = 0
        L_0x0251:
            if (r3 == 0) goto L_0x0264
            long r8 = r3.id
            org.telegram.messenger.UserConfig r12 = org.telegram.messenger.UserConfig.getInstance(r10)
            long r12 = r12.getClientUserId()
            int r27 = (r8 > r12 ? 1 : (r8 == r12 ? 0 : -1))
            if (r27 != 0) goto L_0x0262
            goto L_0x0264
        L_0x0262:
            r6 = 1
            goto L_0x026d
        L_0x0264:
            if (r7 == 0) goto L_0x03bc
            boolean r8 = org.telegram.messenger.ChatObject.hasAdminRights(r7)
            if (r8 != 0) goto L_0x03bc
            goto L_0x0262
        L_0x026d:
            if (r0 != r6) goto L_0x02de
            boolean r8 = r14.creator
            if (r8 != 0) goto L_0x02de
            if (r3 == 0) goto L_0x02de
            org.telegram.ui.ActionBar.AlertDialog[] r15 = new org.telegram.ui.ActionBar.AlertDialog[r6]
            org.telegram.ui.ActionBar.AlertDialog r0 = new org.telegram.ui.ActionBar.AlertDialog
            r2 = 3
            r0.<init>(r1, r2)
            r1 = 0
            r15[r1] = r0
            org.telegram.tgnet.TLRPC$TL_channels_getParticipant r13 = new org.telegram.tgnet.TLRPC$TL_channels_getParticipant
            r13.<init>()
            org.telegram.tgnet.TLRPC$InputChannel r0 = org.telegram.messenger.MessagesController.getInputChannel((org.telegram.tgnet.TLRPC$Chat) r45)
            r13.channel = r0
            org.telegram.tgnet.TLRPC$InputPeer r0 = org.telegram.messenger.MessagesController.getInputPeer((org.telegram.tgnet.TLRPC$User) r3)
            r13.participant = r0
            org.telegram.tgnet.ConnectionsManager r12 = org.telegram.tgnet.ConnectionsManager.getInstance(r10)
            org.telegram.ui.Components.AlertsCreator$$ExternalSyntheticLambda95 r11 = new org.telegram.ui.Components.AlertsCreator$$ExternalSyntheticLambda95
            r0 = r11
            r1 = r15
            r2 = r43
            r3 = r44
            r4 = r45
            r5 = r46
            r6 = r47
            r7 = r48
            r9 = r50
            r14 = r10
            r10 = r51
            r37 = r11
            r11 = r52
            r38 = r12
            r16 = r15
            r15 = r57
            r12 = r53
            r39 = r13
            r13 = r55
            r40 = r14
            r14 = r56
            r41 = r16
            r0.<init>(r1, r2, r3, r4, r5, r6, r7, r9, r10, r11, r12, r13, r14, r15)
            r2 = r37
            r1 = r38
            r0 = r39
            int r0 = r1.sendRequest(r0, r2)
            org.telegram.ui.Components.AlertsCreator$$ExternalSyntheticLambda90 r1 = new org.telegram.ui.Components.AlertsCreator$$ExternalSyntheticLambda90
            r13 = r43
            r9 = r40
            r2 = r41
            r1.<init>(r2, r9, r0, r13)
            r2 = 1000(0x3e8, double:4.94E-321)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r1, r2)
            return
        L_0x02de:
            r13 = r43
            r9 = r10
            r10 = r15
            r15 = r57
            android.widget.FrameLayout r6 = new android.widget.FrameLayout
            r6.<init>(r1)
            if (r3 == 0) goto L_0x02f4
            java.lang.String r8 = r3.first_name
            java.lang.String r12 = r3.last_name
            java.lang.String r8 = org.telegram.messenger.ContactsController.formatName(r8, r12)
            goto L_0x02f6
        L_0x02f4:
            java.lang.String r8 = r7.title
        L_0x02f6:
            r27 = r3
            r3 = 3
            r12 = 0
            r20 = 0
        L_0x02fc:
            if (r12 >= r3) goto L_0x03b0
            r3 = 2
            if (r0 == r3) goto L_0x0303
            if (r5 != 0) goto L_0x030d
        L_0x0303:
            if (r12 != 0) goto L_0x030d
            r29 = r5
            r30 = r7
            r49 = r8
            goto L_0x03a3
        L_0x030d:
            org.telegram.ui.Cells.CheckBoxCell r3 = new org.telegram.ui.Cells.CheckBoxCell
            r0 = 1
            r3.<init>(r1, r0, r15)
            r48 = 0
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.getSelectorDrawable(r48)
            r3.setBackgroundDrawable(r0)
            java.lang.Integer r0 = java.lang.Integer.valueOf(r12)
            r3.setTag(r0)
            if (r12 != 0) goto L_0x0339
            r0 = 2131625287(0x7f0e0547, float:1.8877778E38)
            r29 = r5
            java.lang.String r5 = "DeleteBanUser"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r5, r0)
            r5 = 0
            r3.setText(r0, r10, r5, r5)
            r30 = r7
        L_0x0336:
            r49 = r8
            goto L_0x0363
        L_0x0339:
            r29 = r5
            r0 = 1
            r5 = 0
            if (r12 != r0) goto L_0x034e
            r0 = 2131625325(0x7f0e056d, float:1.8877855E38)
            r30 = r7
            java.lang.String r7 = "DeleteReportSpam"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r7, r0)
            r3.setText(r0, r10, r5, r5)
            goto L_0x0336
        L_0x034e:
            r30 = r7
            r7 = 1
            java.lang.Object[] r0 = new java.lang.Object[r7]
            r0[r5] = r8
            java.lang.String r7 = "DeleteAllFrom"
            r49 = r8
            r8 = 2131625273(0x7f0e0539, float:1.887775E38)
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r7, r8, r0)
            r3.setText(r0, r10, r5, r5)
        L_0x0363:
            boolean r0 = org.telegram.messenger.LocaleController.isRTL
            if (r0 == 0) goto L_0x036c
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r23)
            goto L_0x0370
        L_0x036c:
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r24)
        L_0x0370:
            boolean r5 = org.telegram.messenger.LocaleController.isRTL
            if (r5 == 0) goto L_0x0379
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r24)
            goto L_0x037d
        L_0x0379:
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r23)
        L_0x037d:
            r7 = 0
            r3.setPadding(r0, r7, r5, r7)
            r31 = -1
            r32 = 1111490560(0x42400000, float:48.0)
            r33 = 51
            r34 = 0
            int r0 = r20 * 48
            float r0 = (float) r0
            r36 = 0
            r37 = 0
            r35 = r0
            android.widget.FrameLayout$LayoutParams r0 = org.telegram.ui.Components.LayoutHelper.createFrame(r31, r32, r33, r34, r35, r36, r37)
            r6.addView(r3, r0)
            org.telegram.ui.Components.AlertsCreator$$ExternalSyntheticLambda67 r0 = new org.telegram.ui.Components.AlertsCreator$$ExternalSyntheticLambda67
            r0.<init>(r11)
            r3.setOnClickListener(r0)
            int r20 = r20 + 1
        L_0x03a3:
            int r12 = r12 + 1
            r8 = r49
            r0 = r54
            r5 = r29
            r7 = r30
            r3 = 3
            goto L_0x02fc
        L_0x03b0:
            r30 = r7
            r2.setView(r6)
            r3 = r27
            r5 = r28
            r0 = 0
            goto L_0x0436
        L_0x03bc:
            r13 = r43
            r27 = r3
            r30 = r7
            r9 = r10
            r10 = r15
            r15 = r57
            if (r4 <= 0) goto L_0x0432
            if (r6 == 0) goto L_0x0432
            android.widget.FrameLayout r0 = new android.widget.FrameLayout
            r0.<init>(r1)
            org.telegram.ui.Cells.CheckBoxCell r3 = new org.telegram.ui.Cells.CheckBoxCell
            r5 = 1
            r3.<init>(r1, r5, r15)
            r1 = 0
            android.graphics.drawable.Drawable r5 = org.telegram.ui.ActionBar.Theme.getSelectorDrawable(r1)
            r3.setBackgroundDrawable(r5)
            r7 = r26
            r5 = 2131625315(0x7f0e0563, float:1.8877834E38)
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r7, r5)
            r3.setText(r5, r10, r1, r1)
            boolean r1 = org.telegram.messenger.LocaleController.isRTL
            if (r1 == 0) goto L_0x03f2
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r23)
            goto L_0x03f6
        L_0x03f2:
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r24)
        L_0x03f6:
            boolean r5 = org.telegram.messenger.LocaleController.isRTL
            if (r5 == 0) goto L_0x03ff
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r24)
            goto L_0x0403
        L_0x03ff:
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r23)
        L_0x0403:
            r6 = 0
            r3.setPadding(r1, r6, r5, r6)
            r31 = -1
            r32 = 1111490560(0x42400000, float:48.0)
            r33 = 51
            r34 = 0
            r35 = 0
            r36 = 0
            r37 = 0
            android.widget.FrameLayout$LayoutParams r1 = org.telegram.ui.Components.LayoutHelper.createFrame(r31, r32, r33, r34, r35, r36, r37)
            r0.addView(r3, r1)
            org.telegram.ui.Components.AlertsCreator$$ExternalSyntheticLambda71 r1 = new org.telegram.ui.Components.AlertsCreator$$ExternalSyntheticLambda71
            r5 = r28
            r1.<init>(r5)
            r3.setOnClickListener(r1)
            r2.setView(r0)
            r0 = 9
            r2.setCustomViewOffset(r0)
            r3 = r27
            r0 = 1
            goto L_0x0436
        L_0x0432:
            r5 = r28
            r0 = 0
            r3 = 0
        L_0x0436:
            r20 = r0
            r1 = r2
            r15 = r3
            r3 = r4
            r7 = r5
            r40 = r9
            r26 = r11
            r6 = r25
            r2 = 0
            r8 = 1
            r9 = 0
            r0 = r44
            goto L_0x0600
        L_0x0449:
            r5 = r7
            r9 = r10
            r7 = r13
            r10 = r15
            r13 = r43
            r15 = r57
            if (r53 != 0) goto L_0x05ed
            boolean r0 = org.telegram.messenger.ChatObject.isChannel(r45)
            if (r0 != 0) goto L_0x05ed
            if (r46 != 0) goto L_0x05ed
            r0 = r44
            r20 = r2
            if (r0 == 0) goto L_0x0477
            long r2 = r0.id
            org.telegram.messenger.UserConfig r26 = org.telegram.messenger.UserConfig.getInstance(r9)
            long r26 = r26.getClientUserId()
            int r28 = (r2 > r26 ? 1 : (r2 == r26 ? 0 : -1))
            if (r28 == 0) goto L_0x0477
            boolean r2 = r0.bot
            if (r2 == 0) goto L_0x0479
            boolean r2 = r0.support
            if (r2 != 0) goto L_0x0479
        L_0x0477:
            if (r14 == 0) goto L_0x0538
        L_0x0479:
            r2 = r50
            if (r2 == 0) goto L_0x04c5
            boolean r3 = r50.isSendError()
            if (r3 != 0) goto L_0x04b6
            org.telegram.tgnet.TLRPC$Message r3 = r2.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r3 = r3.action
            r26 = r11
            if (r3 == 0) goto L_0x049f
            boolean r11 = r3 instanceof org.telegram.tgnet.TLRPC$TL_messageActionEmpty
            if (r11 != 0) goto L_0x049f
            boolean r11 = r3 instanceof org.telegram.tgnet.TLRPC$TL_messageActionPhoneCall
            if (r11 != 0) goto L_0x049f
            boolean r11 = r3 instanceof org.telegram.tgnet.TLRPC$TL_messageActionPinMessage
            if (r11 != 0) goto L_0x049f
            boolean r11 = r3 instanceof org.telegram.tgnet.TLRPC$TL_messageActionGeoProximityReached
            if (r11 != 0) goto L_0x049f
            boolean r3 = r3 instanceof org.telegram.tgnet.TLRPC$TL_messageActionSetChatTheme
            if (r3 == 0) goto L_0x04b8
        L_0x049f:
            boolean r3 = r50.isOut()
            if (r3 != 0) goto L_0x04ad
            if (r8 != 0) goto L_0x04ad
            boolean r3 = org.telegram.messenger.ChatObject.hasAdminRights(r45)
            if (r3 == 0) goto L_0x04b8
        L_0x04ad:
            org.telegram.tgnet.TLRPC$Message r3 = r2.messageOwner
            int r3 = r3.date
            int r4 = r4 - r3
            if (r4 > r12) goto L_0x04b8
            r3 = 1
            goto L_0x04b9
        L_0x04b6:
            r26 = r11
        L_0x04b8:
            r3 = 0
        L_0x04b9:
            boolean r4 = r50.isOut()
            r8 = 1
            r4 = r4 ^ r8
            r28 = r5
            r40 = r9
            goto L_0x0540
        L_0x04c5:
            r26 = r11
            r3 = 1
            r11 = 0
            r27 = 0
        L_0x04cb:
            if (r3 < 0) goto L_0x0530
            r2 = 0
        L_0x04ce:
            r28 = r51[r3]
            int r13 = r28.size()
            if (r2 >= r13) goto L_0x0525
            r13 = r51[r3]
            java.lang.Object r13 = r13.valueAt(r2)
            org.telegram.messenger.MessageObject r13 = (org.telegram.messenger.MessageObject) r13
            r40 = r9
            org.telegram.tgnet.TLRPC$Message r9 = r13.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r9 = r9.action
            r28 = r5
            if (r9 == 0) goto L_0x04f9
            boolean r5 = r9 instanceof org.telegram.tgnet.TLRPC$TL_messageActionEmpty
            if (r5 != 0) goto L_0x04f9
            boolean r5 = r9 instanceof org.telegram.tgnet.TLRPC$TL_messageActionPhoneCall
            if (r5 != 0) goto L_0x04f9
            boolean r5 = r9 instanceof org.telegram.tgnet.TLRPC$TL_messageActionPinMessage
            if (r5 != 0) goto L_0x04f9
            boolean r5 = r9 instanceof org.telegram.tgnet.TLRPC$TL_messageActionGeoProximityReached
            if (r5 != 0) goto L_0x04f9
            goto L_0x051c
        L_0x04f9:
            boolean r5 = r13.isOut()
            if (r5 != 0) goto L_0x0509
            if (r8 != 0) goto L_0x0509
            if (r14 == 0) goto L_0x051c
            boolean r5 = org.telegram.messenger.ChatObject.canBlockUsers(r45)
            if (r5 == 0) goto L_0x051c
        L_0x0509:
            org.telegram.tgnet.TLRPC$Message r5 = r13.messageOwner
            int r5 = r5.date
            int r5 = r4 - r5
            if (r5 > r12) goto L_0x051c
            int r27 = r27 + 1
            if (r11 != 0) goto L_0x051c
            boolean r5 = r13.isOut()
            if (r5 != 0) goto L_0x051c
            r11 = 1
        L_0x051c:
            int r2 = r2 + 1
            r13 = r43
            r5 = r28
            r9 = r40
            goto L_0x04ce
        L_0x0525:
            r28 = r5
            r40 = r9
            int r3 = r3 + -1
            r13 = r43
            r2 = r50
            goto L_0x04cb
        L_0x0530:
            r28 = r5
            r40 = r9
            r4 = r11
            r3 = r27
            goto L_0x0540
        L_0x0538:
            r28 = r5
            r40 = r9
            r26 = r11
            r3 = 0
            r4 = 0
        L_0x0540:
            if (r3 <= 0) goto L_0x05e3
            if (r6 == 0) goto L_0x05e3
            if (r0 == 0) goto L_0x054c
            boolean r2 = org.telegram.messenger.UserObject.isDeleted(r44)
            if (r2 != 0) goto L_0x05e3
        L_0x054c:
            android.widget.FrameLayout r2 = new android.widget.FrameLayout
            r2.<init>(r1)
            org.telegram.ui.Cells.CheckBoxCell r5 = new org.telegram.ui.Cells.CheckBoxCell
            r8 = 1
            r5.<init>(r1, r8, r15)
            r1 = 0
            android.graphics.drawable.Drawable r6 = org.telegram.ui.ActionBar.Theme.getSelectorDrawable(r1)
            r5.setBackgroundDrawable(r6)
            if (r21 == 0) goto L_0x0578
            r6 = 2131625316(0x7f0e0564, float:1.8877837E38)
            java.lang.Object[] r7 = new java.lang.Object[r8]
            java.lang.String r9 = org.telegram.messenger.UserObject.getFirstName(r44)
            r7[r1] = r9
            java.lang.String r9 = "DeleteMessagesOptionAlso"
            java.lang.String r6 = org.telegram.messenger.LocaleController.formatString(r9, r6, r7)
            r5.setText(r6, r10, r1, r1)
            r6 = r25
            goto L_0x0597
        L_0x0578:
            r6 = r25
            if (r14 == 0) goto L_0x058d
            if (r4 != 0) goto L_0x0580
            if (r3 != r6) goto L_0x058d
        L_0x0580:
            r7 = 2131625302(0x7f0e0556, float:1.8877808E38)
            java.lang.String r9 = "DeleteForAll"
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r9, r7)
            r5.setText(r7, r10, r1, r1)
            goto L_0x0597
        L_0x058d:
            r9 = 2131625315(0x7f0e0563, float:1.8877834E38)
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r7, r9)
            r5.setText(r7, r10, r1, r1)
        L_0x0597:
            boolean r1 = org.telegram.messenger.LocaleController.isRTL
            if (r1 == 0) goto L_0x05a0
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r23)
            goto L_0x05a4
        L_0x05a0:
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r24)
        L_0x05a4:
            boolean r7 = org.telegram.messenger.LocaleController.isRTL
            if (r7 == 0) goto L_0x05ad
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r24)
            goto L_0x05b1
        L_0x05ad:
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r23)
        L_0x05b1:
            r9 = 0
            r5.setPadding(r1, r9, r7, r9)
            r29 = -1
            r30 = 1111490560(0x42400000, float:48.0)
            r31 = 51
            r32 = 0
            r33 = 0
            r34 = 0
            r35 = 0
            android.widget.FrameLayout$LayoutParams r1 = org.telegram.ui.Components.LayoutHelper.createFrame(r29, r30, r31, r32, r33, r34, r35)
            r2.addView(r5, r1)
            org.telegram.ui.Components.AlertsCreator$$ExternalSyntheticLambda68 r1 = new org.telegram.ui.Components.AlertsCreator$$ExternalSyntheticLambda68
            r7 = r28
            r1.<init>(r7)
            r5.setOnClickListener(r1)
            r1 = r20
            r1.setView(r2)
            r2 = 9
            r1.setCustomViewOffset(r2)
            r2 = r4
            r15 = 0
            r20 = 1
            goto L_0x05fe
        L_0x05e3:
            r1 = r20
            r6 = r25
            r7 = r28
            r8 = 1
            r9 = 0
            r2 = r4
            goto L_0x05fb
        L_0x05ed:
            r0 = r44
            r1 = r2
            r7 = r5
            r40 = r9
            r26 = r11
            r6 = r25
            r8 = 1
            r9 = 0
            r2 = 0
            r3 = 0
        L_0x05fb:
            r15 = 0
            r20 = 0
        L_0x05fe:
            r30 = 0
        L_0x0600:
            r4 = 2131625265(0x7f0e0531, float:1.8877733E38)
            java.lang.String r5 = "Delete"
            java.lang.String r13 = org.telegram.messenger.LocaleController.getString(r5, r4)
            org.telegram.ui.Components.AlertsCreator$$ExternalSyntheticLambda17 r12 = new org.telegram.ui.Components.AlertsCreator$$ExternalSyntheticLambda17
            r4 = r12
            r11 = r6
            r5 = r50
            r22 = 0
            r6 = r52
            r16 = r7
            r9 = 2
            r7 = r46
            r10 = 1
            r8 = r40
            r0 = 1
            r9 = r17
            r0 = r11
            r17 = r26
            r11 = r16
            r23 = r3
            r3 = r12
            r12 = r53
            r48 = r2
            r2 = r13
            r13 = r51
            r14 = r15
            r15 = r30
            r16 = r17
            r17 = r45
            r18 = r47
            r19 = r55
            r4.<init>(r5, r6, r7, r8, r9, r11, r12, r13, r14, r15, r16, r17, r18, r19)
            r1.setPositiveButton(r2, r3)
            java.lang.String r2 = "messages"
            r3 = 1
            if (r0 != r3) goto L_0x0650
            r4 = 2131625327(0x7f0e056f, float:1.8877859E38)
            java.lang.String r5 = "DeleteSingleMessagesTitle"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
            r1.setTitle(r4)
            goto L_0x0664
        L_0x0650:
            r4 = 2131625321(0x7f0e0569, float:1.8877847E38)
            java.lang.Object[] r5 = new java.lang.Object[r3]
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatPluralString(r2, r0)
            r5[r22] = r3
            java.lang.String r3 = "DeleteMessagesTitle"
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r4, r5)
            r1.setTitle(r3)
        L_0x0664:
            r3 = 2131624388(0x7f0e01c4, float:1.8875954E38)
            java.lang.String r4 = "AreYouSureDeleteSingleMessage"
            r5 = 2131624380(0x7f0e01bc, float:1.8875938E38)
            java.lang.String r6 = "AreYouSureDeleteFewMessages"
            r7 = r45
            if (r7 == 0) goto L_0x06a6
            if (r48 == 0) goto L_0x06a6
            if (r20 == 0) goto L_0x0691
            r9 = r23
            if (r9 == r0) goto L_0x0691
            r0 = 2131625320(0x7f0e0568, float:1.8877845E38)
            r7 = 1
            java.lang.Object[] r3 = new java.lang.Object[r7]
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatPluralString(r2, r9)
            r3[r22] = r2
            java.lang.String r2 = "DeleteMessagesTextGroupPart"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r3)
            r1.setMessage(r0)
            goto L_0x071e
        L_0x0691:
            r7 = 1
            if (r0 != r7) goto L_0x069d
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r4, r3)
            r1.setMessage(r0)
            goto L_0x071e
        L_0x069d:
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r6, r5)
            r1.setMessage(r0)
            goto L_0x071e
        L_0x06a6:
            r9 = r23
            if (r20 == 0) goto L_0x06e7
            if (r21 != 0) goto L_0x06e7
            if (r9 == r0) goto L_0x06e7
            if (r7 == 0) goto L_0x06c6
            r0 = 2131625319(0x7f0e0567, float:1.8877843E38)
            r3 = 1
            java.lang.Object[] r3 = new java.lang.Object[r3]
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatPluralString(r2, r9)
            r3[r22] = r2
            java.lang.String r2 = "DeleteMessagesTextGroup"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r3)
            r1.setMessage(r0)
            goto L_0x071e
        L_0x06c6:
            r0 = 2131625318(0x7f0e0566, float:1.887784E38)
            r3 = 2
            java.lang.Object[] r3 = new java.lang.Object[r3]
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatPluralString(r2, r9)
            r3[r22] = r2
            java.lang.String r2 = org.telegram.messenger.UserObject.getFirstName(r44)
            r4 = 1
            r3[r4] = r2
            java.lang.String r2 = "DeleteMessagesText"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r3)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            r1.setMessage(r0)
            goto L_0x071e
        L_0x06e7:
            if (r7 == 0) goto L_0x070c
            boolean r2 = r7.megagroup
            if (r2 == 0) goto L_0x070c
            if (r53 != 0) goto L_0x070c
            r2 = 1
            if (r0 != r2) goto L_0x06ff
            r0 = 2131624389(0x7f0e01c5, float:1.8875956E38)
            java.lang.String r2 = "AreYouSureDeleteSingleMessageMega"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            r1.setMessage(r0)
            goto L_0x071e
        L_0x06ff:
            r0 = 2131624381(0x7f0e01bd, float:1.887594E38)
            java.lang.String r2 = "AreYouSureDeleteFewMessagesMega"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            r1.setMessage(r0)
            goto L_0x071e
        L_0x070c:
            r2 = 1
            if (r0 != r2) goto L_0x0717
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r4, r3)
            r1.setMessage(r0)
            goto L_0x071e
        L_0x0717:
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r6, r5)
            r1.setMessage(r0)
        L_0x071e:
            r0 = 2131624750(0x7f0e032e, float:1.8876689E38)
            java.lang.String r2 = "Cancel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            r2 = 0
            r1.setNegativeButton(r0, r2)
            org.telegram.ui.Components.AlertsCreator$$ExternalSyntheticLambda41 r0 = new org.telegram.ui.Components.AlertsCreator$$ExternalSyntheticLambda41
            r2 = r56
            r0.<init>(r2)
            r1.setOnPreDismissListener(r0)
            org.telegram.ui.ActionBar.AlertDialog r0 = r1.create()
            r1 = r43
            r1.showDialog(r0)
            r1 = -1
            android.view.View r0 = r0.getButton(r1)
            android.widget.TextView r0 = (android.widget.TextView) r0
            if (r0 == 0) goto L_0x0750
            java.lang.String r1 = "dialogTextRed2"
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r1)
            r0.setTextColor(r1)
        L_0x0750:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.AlertsCreator.createDeleteMessagesAlert(org.telegram.ui.ActionBar.BaseFragment, org.telegram.tgnet.TLRPC$User, org.telegram.tgnet.TLRPC$Chat, org.telegram.tgnet.TLRPC$EncryptedChat, org.telegram.tgnet.TLRPC$ChatFull, long, org.telegram.messenger.MessageObject, android.util.SparseArray[], org.telegram.messenger.MessageObject$GroupedMessages, boolean, int, java.lang.Runnable, java.lang.Runnable, org.telegram.ui.ActionBar.Theme$ResourcesProvider):void");
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$createDeleteMessagesAlert$111(AlertDialog[] alertDialogArr, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error, BaseFragment baseFragment, TLRPC$User tLRPC$User, TLRPC$Chat tLRPC$Chat, TLRPC$EncryptedChat tLRPC$EncryptedChat, TLRPC$ChatFull tLRPC$ChatFull, long j, MessageObject messageObject, SparseArray[] sparseArrayArr, MessageObject.GroupedMessages groupedMessages, boolean z, Runnable runnable, Runnable runnable2, Theme.ResourcesProvider resourcesProvider) {
        int i;
        TLRPC$TL_error tLRPC$TL_error2 = tLRPC$TL_error;
        int i2 = 0;
        try {
            alertDialogArr[0].dismiss();
        } catch (Throwable unused) {
        }
        alertDialogArr[0] = null;
        if (tLObject != null) {
            TLRPC$ChannelParticipant tLRPC$ChannelParticipant = ((TLRPC$TL_channels_channelParticipant) tLObject).participant;
            if ((tLRPC$ChannelParticipant instanceof TLRPC$TL_channelParticipantAdmin) || (tLRPC$ChannelParticipant instanceof TLRPC$TL_channelParticipantCreator)) {
                i2 = 2;
            }
            i = i2;
        } else {
            i = (tLRPC$TL_error2 == null || !"USER_NOT_PARTICIPANT".equals(tLRPC$TL_error2.text)) ? 2 : 0;
        }
        createDeleteMessagesAlert(baseFragment, tLRPC$User, tLRPC$Chat, tLRPC$EncryptedChat, tLRPC$ChatFull, j, messageObject, sparseArrayArr, groupedMessages, z, i, runnable, runnable2, resourcesProvider);
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$createDeleteMessagesAlert$114(AlertDialog[] alertDialogArr, int i, int i2, BaseFragment baseFragment) {
        if (alertDialogArr[0] != null) {
            alertDialogArr[0].setOnCancelListener(new AlertsCreator$$ExternalSyntheticLambda0(i, i2));
            baseFragment.showDialog(alertDialogArr[0]);
        }
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$createDeleteMessagesAlert$115(boolean[] zArr, View view) {
        if (view.isEnabled()) {
            CheckBoxCell checkBoxCell = (CheckBoxCell) view;
            Integer num = (Integer) checkBoxCell.getTag();
            zArr[num.intValue()] = !zArr[num.intValue()];
            checkBoxCell.setChecked(zArr[num.intValue()], true);
        }
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$createDeleteMessagesAlert$116(boolean[] zArr, View view) {
        zArr[0] = !zArr[0];
        ((CheckBoxCell) view).setChecked(zArr[0], true);
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$createDeleteMessagesAlert$117(boolean[] zArr, View view) {
        zArr[0] = !zArr[0];
        ((CheckBoxCell) view).setChecked(zArr[0], true);
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$createDeleteMessagesAlert$119(MessageObject messageObject, MessageObject.GroupedMessages groupedMessages, TLRPC$EncryptedChat tLRPC$EncryptedChat, int i, long j, boolean[] zArr, boolean z, SparseArray[] sparseArrayArr, TLRPC$User tLRPC$User, TLRPC$Chat tLRPC$Chat, boolean[] zArr2, TLRPC$Chat tLRPC$Chat2, TLRPC$ChatFull tLRPC$ChatFull, Runnable runnable, DialogInterface dialogInterface, int i2) {
        ArrayList<Integer> arrayList;
        int i3;
        ArrayList arrayList2;
        ArrayList arrayList3;
        MessageObject messageObject2 = messageObject;
        MessageObject.GroupedMessages groupedMessages2 = groupedMessages;
        TLRPC$User tLRPC$User2 = tLRPC$User;
        TLRPC$Chat tLRPC$Chat3 = tLRPC$Chat;
        TLRPC$Chat tLRPC$Chat4 = tLRPC$Chat2;
        int i4 = 10;
        ArrayList arrayList4 = null;
        int i5 = 0;
        if (messageObject2 != null) {
            ArrayList<Integer> arrayList5 = new ArrayList<>();
            if (groupedMessages2 != null) {
                for (int i6 = 0; i6 < groupedMessages2.messages.size(); i6++) {
                    MessageObject messageObject3 = groupedMessages2.messages.get(i6);
                    arrayList5.add(Integer.valueOf(messageObject3.getId()));
                    if (!(tLRPC$EncryptedChat == null || messageObject3.messageOwner.random_id == 0 || messageObject3.type == 10)) {
                        if (arrayList4 == null) {
                            arrayList4 = new ArrayList();
                        }
                        arrayList4.add(Long.valueOf(messageObject3.messageOwner.random_id));
                    }
                }
            } else {
                arrayList5.add(Integer.valueOf(messageObject.getId()));
                if (!(tLRPC$EncryptedChat == null || messageObject2.messageOwner.random_id == 0 || messageObject2.type == 10)) {
                    ArrayList arrayList6 = new ArrayList();
                    arrayList6.add(Long.valueOf(messageObject2.messageOwner.random_id));
                    arrayList3 = arrayList6;
                    arrayList = arrayList5;
                    i3 = 0;
                    MessagesController.getInstance(i).deleteMessages(arrayList5, arrayList3, tLRPC$EncryptedChat, j, zArr[0], z);
                }
            }
            arrayList3 = arrayList4;
            arrayList = arrayList5;
            i3 = 0;
            MessagesController.getInstance(i).deleteMessages(arrayList5, arrayList3, tLRPC$EncryptedChat, j, zArr[0], z);
        } else {
            ArrayList<Integer> arrayList7 = null;
            int i7 = 1;
            while (i7 >= 0) {
                ArrayList<Integer> arrayList8 = new ArrayList<>();
                for (int i8 = 0; i8 < sparseArrayArr[i7].size(); i8++) {
                    arrayList8.add(Integer.valueOf(sparseArrayArr[i7].keyAt(i8)));
                }
                if (!arrayList8.isEmpty()) {
                    long j2 = ((MessageObject) sparseArrayArr[i7].get(arrayList8.get(i5).intValue())).messageOwner.peer_id.channel_id;
                }
                if (tLRPC$EncryptedChat != null) {
                    ArrayList arrayList9 = new ArrayList();
                    for (int i9 = 0; i9 < sparseArrayArr[i7].size(); i9++) {
                        MessageObject messageObject4 = (MessageObject) sparseArrayArr[i7].valueAt(i9);
                        long j3 = messageObject4.messageOwner.random_id;
                        if (!(j3 == 0 || messageObject4.type == i4)) {
                            arrayList9.add(Long.valueOf(j3));
                        }
                    }
                    arrayList2 = arrayList9;
                } else {
                    arrayList2 = null;
                }
                MessagesController.getInstance(i).deleteMessages(arrayList8, arrayList2, tLRPC$EncryptedChat, j, zArr[i5], z);
                sparseArrayArr[i7].clear();
                i7--;
                arrayList7 = arrayList8;
                i5 = 0;
                i4 = 10;
            }
            i3 = 0;
            arrayList = arrayList7;
        }
        if (!(tLRPC$User2 == null && tLRPC$Chat3 == null)) {
            if (zArr2[i3]) {
                MessagesController.getInstance(i).deleteParticipantFromChat(tLRPC$Chat4.id, tLRPC$User, tLRPC$Chat, tLRPC$ChatFull, false, false);
            }
            if (zArr2[1]) {
                TLRPC$TL_channels_reportSpam tLRPC$TL_channels_reportSpam = new TLRPC$TL_channels_reportSpam();
                tLRPC$TL_channels_reportSpam.channel = MessagesController.getInputChannel(tLRPC$Chat2);
                if (tLRPC$User2 != null) {
                    tLRPC$TL_channels_reportSpam.participant = MessagesController.getInputPeer(tLRPC$User);
                } else {
                    tLRPC$TL_channels_reportSpam.participant = MessagesController.getInputPeer(tLRPC$Chat);
                }
                tLRPC$TL_channels_reportSpam.id = arrayList;
                ConnectionsManager.getInstance(i).sendRequest(tLRPC$TL_channels_reportSpam, AlertsCreator$$ExternalSyntheticLambda99.INSTANCE);
            }
            if (zArr2[2]) {
                MessagesController.getInstance(i).deleteUserChannelHistory(tLRPC$Chat4, tLRPC$User2, tLRPC$Chat3, i3);
            }
        }
        if (runnable != null) {
            runnable.run();
        }
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$createDeleteMessagesAlert$120(Runnable runnable, DialogInterface dialogInterface) {
        if (runnable != null) {
            runnable.run();
        }
    }

    public static void createThemeCreateDialog(BaseFragment baseFragment, int i, Theme.ThemeInfo themeInfo, Theme.ThemeAccent themeAccent) {
        BaseFragment baseFragment2 = baseFragment;
        if (baseFragment2 != null && baseFragment.getParentActivity() != null) {
            Activity parentActivity = baseFragment.getParentActivity();
            EditTextBoldCursor editTextBoldCursor = new EditTextBoldCursor(parentActivity);
            editTextBoldCursor.setBackground((Drawable) null);
            editTextBoldCursor.setLineColors(Theme.getColor("dialogInputField"), Theme.getColor("dialogInputFieldActivated"), Theme.getColor("dialogTextRed2"));
            AlertDialog.Builder builder = new AlertDialog.Builder((Context) parentActivity);
            builder.setTitle(LocaleController.getString("NewTheme", NUM));
            builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
            builder.setPositiveButton(LocaleController.getString("Create", NUM), AlertsCreator$$ExternalSyntheticLambda38.INSTANCE);
            LinearLayout linearLayout = new LinearLayout(parentActivity);
            linearLayout.setOrientation(1);
            builder.setView(linearLayout);
            TextView textView = new TextView(parentActivity);
            if (i != 0) {
                textView.setText(AndroidUtilities.replaceTags(LocaleController.getString("EnterThemeNameEdit", NUM)));
            } else {
                textView.setText(LocaleController.getString("EnterThemeName", NUM));
            }
            textView.setTextSize(1, 16.0f);
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
            editTextBoldCursor.setOnEditorActionListener(AlertsCreator$$ExternalSyntheticLambda84.INSTANCE);
            editTextBoldCursor.setText(generateThemeName(themeAccent));
            editTextBoldCursor.setSelection(editTextBoldCursor.length());
            AlertDialog create = builder.create();
            create.setOnShowListener(new AlertsCreator$$ExternalSyntheticLambda46(editTextBoldCursor));
            baseFragment2.showDialog(create);
            editTextBoldCursor.requestFocus();
            create.getButton(-1).setOnClickListener(new AlertsCreator$$ExternalSyntheticLambda53(baseFragment, editTextBoldCursor, themeAccent, themeInfo, create));
        }
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$createThemeCreateDialog$123(EditTextBoldCursor editTextBoldCursor) {
        editTextBoldCursor.requestFocus();
        AndroidUtilities.showKeyboard(editTextBoldCursor);
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$createThemeCreateDialog$127(BaseFragment baseFragment, EditTextBoldCursor editTextBoldCursor, Theme.ThemeAccent themeAccent, Theme.ThemeInfo themeInfo, AlertDialog alertDialog, View view) {
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
                Utilities.searchQueue.postRunnable(new AlertsCreator$$ExternalSyntheticLambda88(editTextBoldCursor, alertDialog, baseFragment));
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

    @SuppressLint({"ClickableViewAccessibility"})
    public static ActionBarPopupWindow showPopupMenu(ActionBarPopupWindow.ActionBarPopupWindowLayout actionBarPopupWindowLayout, View view, int i, int i2) {
        Rect rect = new Rect();
        ActionBarPopupWindow actionBarPopupWindow = new ActionBarPopupWindow(actionBarPopupWindowLayout, -2, -2);
        if (Build.VERSION.SDK_INT >= 19) {
            actionBarPopupWindow.setAnimationStyle(0);
        } else {
            actionBarPopupWindow.setAnimationStyle(NUM);
        }
        actionBarPopupWindow.setAnimationEnabled(true);
        actionBarPopupWindow.setOutsideTouchable(true);
        actionBarPopupWindow.setClippingEnabled(true);
        actionBarPopupWindow.setInputMethodMode(2);
        actionBarPopupWindow.setSoftInputMode(0);
        actionBarPopupWindow.setFocusable(true);
        actionBarPopupWindowLayout.setFocusableInTouchMode(true);
        actionBarPopupWindowLayout.setOnKeyListener(new AlertsCreator$$ExternalSyntheticLambda74(actionBarPopupWindow));
        actionBarPopupWindowLayout.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.displaySize.x - AndroidUtilities.dp(40.0f), Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.displaySize.y, Integer.MIN_VALUE));
        actionBarPopupWindow.showAsDropDown(view, i, i2);
        actionBarPopupWindowLayout.updateRadialSelectors();
        actionBarPopupWindow.startAnimation();
        actionBarPopupWindowLayout.setOnTouchListener(new AlertsCreator$$ExternalSyntheticLambda75(actionBarPopupWindow, rect));
        return actionBarPopupWindow;
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$showPopupMenu$128(ActionBarPopupWindow actionBarPopupWindow, View view, int i, KeyEvent keyEvent) {
        if (i != 82 || keyEvent.getRepeatCount() != 0 || keyEvent.getAction() != 1 || !actionBarPopupWindow.isShowing()) {
            return false;
        }
        actionBarPopupWindow.dismiss();
        return true;
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$showPopupMenu$129(ActionBarPopupWindow actionBarPopupWindow, Rect rect, View view, MotionEvent motionEvent) {
        if (motionEvent.getActionMasked() != 0 || actionBarPopupWindow == null || !actionBarPopupWindow.isShowing()) {
            return false;
        }
        view.getHitRect(rect);
        if (rect.contains((int) motionEvent.getX(), (int) motionEvent.getY())) {
            return false;
        }
        actionBarPopupWindow.dismiss();
        return false;
    }
}
