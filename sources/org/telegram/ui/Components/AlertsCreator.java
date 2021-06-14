package org.telegram.ui.Components;

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
import java.io.File;
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
import org.telegram.messenger.SvgHelper;
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
import org.telegram.tgnet.TLRPC$TL_contacts_blockFromReplies;
import org.telegram.tgnet.TLRPC$TL_contacts_importContacts;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$TL_help_getSupport;
import org.telegram.tgnet.TLRPC$TL_help_support;
import org.telegram.tgnet.TLRPC$TL_inputPeerUser;
import org.telegram.tgnet.TLRPC$TL_inputReportReasonChildAbuse;
import org.telegram.tgnet.TLRPC$TL_inputReportReasonFake;
import org.telegram.tgnet.TLRPC$TL_inputReportReasonOther;
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
import org.telegram.tgnet.TLRPC$TL_phone_inviteToGroupCall;
import org.telegram.tgnet.TLRPC$TL_updateUserName;
import org.telegram.tgnet.TLRPC$Updates;
import org.telegram.tgnet.TLRPC$User;
import org.telegram.tgnet.TLRPC$UserFull;
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
import org.telegram.ui.Components.voip.VoIPHelper;
import org.telegram.ui.LanguageSelectActivity;
import org.telegram.ui.LaunchActivity;
import org.telegram.ui.LoginActivity;
import org.telegram.ui.NotificationsCustomSettingsActivity;
import org.telegram.ui.NotificationsSettingsActivity;
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

    static /* synthetic */ boolean lambda$createCalendarPickerDialog$52(View view, MotionEvent motionEvent) {
        return true;
    }

    static /* synthetic */ void lambda$createChangeBioAlert$20(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
    }

    static /* synthetic */ void lambda$createChangeNameAlert$24(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
    }

    static /* synthetic */ boolean lambda$createDatePickerDialog$46(View view, MotionEvent motionEvent) {
        return true;
    }

    static /* synthetic */ void lambda$createDeleteMessagesAlert$92(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
    }

    static /* synthetic */ void lambda$createReportAlert$60(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
    }

    static /* synthetic */ boolean lambda$createScheduleDatePickerDialog$37(View view, MotionEvent motionEvent) {
        return true;
    }

    static /* synthetic */ void lambda$createThemeCreateDialog$94(DialogInterface dialogInterface, int i) {
    }

    static /* synthetic */ void lambda$sendReport$59(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
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
            builder.setNegativeButton(LocaleController.getString("UpdateApp", NUM), new DialogInterface.OnClickListener(context) {
                public final /* synthetic */ Context f$0;

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
                    LaunchActivity.this.lambda$runLinkRequest$42(new LanguageSelectActivity());
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
                public final /* synthetic */ LaunchActivity f$1;

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
        if (i2 >= 0 || (chat = MessagesController.getInstance(i).getChat(Integer.valueOf(-i2))) == null || !chat.slowmode_enabled || ChatObject.hasAdminRights(chat)) {
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
        if (str2 == null || baseFragment == null || baseFragment.getParentActivity() == null) {
            return null;
        }
        AlertDialog create = createSimpleAlert(baseFragment.getParentActivity(), str, str2).create();
        baseFragment.showDialog(create);
        return create;
    }

    public static void showBlockReportSpamReplyAlert(ChatActivity chatActivity, MessageObject messageObject, int i) {
        if (chatActivity != null && chatActivity.getParentActivity() != null && messageObject != null) {
            AccountInstance accountInstance = chatActivity.getAccountInstance();
            TLRPC$User user = i > 0 ? accountInstance.getMessagesController().getUser(Integer.valueOf(i)) : null;
            TLRPC$Chat chat = i < 0 ? accountInstance.getMessagesController().getChat(Integer.valueOf(-i)) : null;
            if (user != null || chat != null) {
                AlertDialog.Builder builder = new AlertDialog.Builder((Context) chatActivity.getParentActivity());
                builder.setTitle(LocaleController.getString("BlockUser", NUM));
                if (user != null) {
                    builder.setMessage(AndroidUtilities.replaceTags(LocaleController.formatString("BlockUserReplyAlert", NUM, UserObject.getFirstName(user))));
                } else {
                    builder.setMessage(AndroidUtilities.replaceTags(LocaleController.formatString("BlockUserReplyAlert", NUM, chat.title)));
                }
                LinearLayout linearLayout = new LinearLayout(chatActivity.getParentActivity());
                linearLayout.setOrientation(1);
                CheckBoxCell[] checkBoxCellArr = {new CheckBoxCell(chatActivity.getParentActivity(), 1)};
                checkBoxCellArr[0].setBackgroundDrawable(Theme.getSelectorDrawable(false));
                checkBoxCellArr[0].setTag(0);
                checkBoxCellArr[0].setText(LocaleController.getString("DeleteReportSpam", NUM), "", true, false);
                checkBoxCellArr[0].setPadding(LocaleController.isRTL ? AndroidUtilities.dp(16.0f) : AndroidUtilities.dp(8.0f), 0, LocaleController.isRTL ? AndroidUtilities.dp(8.0f) : AndroidUtilities.dp(16.0f), 0);
                linearLayout.addView(checkBoxCellArr[0], LayoutHelper.createLinear(-1, -2));
                checkBoxCellArr[0].setOnClickListener(new View.OnClickListener(checkBoxCellArr) {
                    public final /* synthetic */ CheckBoxCell[] f$0;

                    {
                        this.f$0 = r1;
                    }

                    public final void onClick(View view) {
                        AlertsCreator.lambda$showBlockReportSpamReplyAlert$3(this.f$0, view);
                    }
                });
                builder.setCustomViewOffset(12);
                builder.setView(linearLayout);
                builder.setPositiveButton(LocaleController.getString("BlockAndDeleteReplies", NUM), new DialogInterface.OnClickListener(accountInstance, chatActivity, chat, messageObject, checkBoxCellArr) {
                    public final /* synthetic */ AccountInstance f$1;
                    public final /* synthetic */ ChatActivity f$2;
                    public final /* synthetic */ TLRPC$Chat f$3;
                    public final /* synthetic */ MessageObject f$4;
                    public final /* synthetic */ CheckBoxCell[] f$5;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                        this.f$3 = r4;
                        this.f$4 = r5;
                        this.f$5 = r6;
                    }

                    public final void onClick(DialogInterface dialogInterface, int i) {
                        AlertsCreator.lambda$showBlockReportSpamReplyAlert$5(TLRPC$User.this, this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, dialogInterface, i);
                    }
                });
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

    static /* synthetic */ void lambda$showBlockReportSpamReplyAlert$3(CheckBoxCell[] checkBoxCellArr, View view) {
        Integer num = (Integer) view.getTag();
        checkBoxCellArr[num.intValue()].setChecked(!checkBoxCellArr[num.intValue()].isChecked(), true);
    }

    static /* synthetic */ void lambda$showBlockReportSpamReplyAlert$5(TLRPC$User tLRPC$User, AccountInstance accountInstance, ChatActivity chatActivity, TLRPC$Chat tLRPC$Chat, MessageObject messageObject, CheckBoxCell[] checkBoxCellArr, DialogInterface dialogInterface, int i) {
        if (tLRPC$User != null) {
            accountInstance.getMessagesStorage().deleteUserChatHistory(chatActivity.getDialogId(), 0, tLRPC$User.id);
        } else {
            accountInstance.getMessagesStorage().deleteUserChatHistory(chatActivity.getDialogId(), 0, -tLRPC$Chat.id);
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
        accountInstance.getConnectionsManager().sendRequest(tLRPC$TL_contacts_blockFromReplies, new RequestDelegate() {
            public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                AlertsCreator.lambda$showBlockReportSpamReplyAlert$4(AccountInstance.this, tLObject, tLRPC$TL_error);
            }
        });
    }

    static /* synthetic */ void lambda$showBlockReportSpamReplyAlert$4(AccountInstance accountInstance, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        if (tLObject instanceof TLRPC$Updates) {
            accountInstance.getMessagesController().processUpdates((TLRPC$Updates) tLObject, false);
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:12:0x004a  */
    /* JADX WARNING: Removed duplicated region for block: B:32:0x0133  */
    /* JADX WARNING: Removed duplicated region for block: B:49:0x01dd  */
    /* JADX WARNING: Removed duplicated region for block: B:55:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static void showBlockReportSpamAlert(org.telegram.ui.ActionBar.BaseFragment r18, long r19, org.telegram.tgnet.TLRPC$User r21, org.telegram.tgnet.TLRPC$Chat r22, org.telegram.tgnet.TLRPC$EncryptedChat r23, boolean r24, org.telegram.tgnet.TLRPC$ChatFull r25, org.telegram.messenger.MessagesStorage.IntCallback r26) {
        /*
            r0 = r18
            r7 = r22
            r1 = r25
            if (r0 == 0) goto L_0x01e6
            android.app.Activity r2 = r18.getParentActivity()
            if (r2 != 0) goto L_0x0010
            goto L_0x01e6
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
            if (r21 == 0) goto L_0x0133
            r1 = 2131624566(0x7f0e0276, float:1.8876315E38)
            java.lang.Object[] r6 = new java.lang.Object[r4]
            java.lang.String r10 = org.telegram.messenger.UserObject.getFirstName(r21)
            r6[r5] = r10
            java.lang.String r10 = "BlockUserTitle"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r10, r1, r6)
            r11.setTitle(r1)
            r1 = 2131624560(0x7f0e0270, float:1.8876303E38)
            java.lang.Object[] r6 = new java.lang.Object[r4]
            java.lang.String r10 = org.telegram.messenger.UserObject.getFirstName(r21)
            r6[r5] = r10
            java.lang.String r10 = "BlockUserAlert"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r10, r1, r6)
            android.text.SpannableStringBuilder r1 = org.telegram.messenger.AndroidUtilities.replaceTags(r1)
            r11.setMessage(r1)
            r1 = 2131624558(0x7f0e026e, float:1.88763E38)
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
            if (r15 >= r6) goto L_0x0124
            if (r15 != 0) goto L_0x009b
            if (r2 != 0) goto L_0x009b
            r16 = r1
            r17 = r2
            goto L_0x011b
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
            if (r15 != 0) goto L_0x00cf
            r13 = r10[r15]
            r12 = 2131625158(0x7f0e04c6, float:1.8877516E38)
            r16 = r1
            java.lang.String r1 = "DeleteReportSpam"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r1, r12)
            r13.setText(r1, r6, r4, r5)
            r17 = r2
            goto L_0x00e3
        L_0x00cf:
            r16 = r1
            r1 = r10[r15]
            r12 = 2131625167(0x7f0e04cf, float:1.8877534E38)
            java.lang.Object[] r13 = new java.lang.Object[r5]
            r17 = r2
            java.lang.String r2 = "DeleteThisChat"
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r12, r13)
            r1.setText(r2, r6, r4, r5)
        L_0x00e3:
            r1 = r10[r15]
            boolean r2 = org.telegram.messenger.LocaleController.isRTL
            r6 = 1098907648(0x41800000, float:16.0)
            r12 = 1090519040(0x41000000, float:8.0)
            if (r2 == 0) goto L_0x00f2
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r6)
            goto L_0x00f6
        L_0x00f2:
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r12)
        L_0x00f6:
            boolean r13 = org.telegram.messenger.LocaleController.isRTL
            if (r13 == 0) goto L_0x00ff
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r12)
            goto L_0x0103
        L_0x00ff:
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
        L_0x0103:
            r1.setPadding(r2, r5, r6, r5)
            r1 = r10[r15]
            r2 = -2
            r6 = -1
            android.widget.LinearLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createLinear(r6, r2)
            r14.addView(r1, r2)
            r1 = r10[r15]
            org.telegram.ui.Components.-$$Lambda$AlertsCreator$XOv0VE_gHd14UuU40YAvw9NlXKk r2 = new org.telegram.ui.Components.-$$Lambda$AlertsCreator$XOv0VE_gHd14UuU40YAvw9NlXKk
            r2.<init>(r10)
            r1.setOnClickListener(r2)
        L_0x011b:
            int r15 = r15 + 1
            r1 = r16
            r2 = r17
            r6 = 2
            goto L_0x008f
        L_0x0124:
            r16 = r1
            r1 = 12
            r11.setCustomViewOffset(r1)
            r11.setView(r14)
            r4 = r10
            r12 = r16
            goto L_0x01ab
        L_0x0133:
            if (r7 == 0) goto L_0x0171
            if (r24 == 0) goto L_0x0171
            r2 = 2131627210(0x7f0e0cca, float:1.8881678E38)
            java.lang.String r6 = "ReportUnrelatedGroup"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r6, r2)
            r11.setTitle(r2)
            if (r1 == 0) goto L_0x0164
            org.telegram.tgnet.TLRPC$ChannelLocation r1 = r1.location
            boolean r2 = r1 instanceof org.telegram.tgnet.TLRPC$TL_channelLocation
            if (r2 == 0) goto L_0x0164
            org.telegram.tgnet.TLRPC$TL_channelLocation r1 = (org.telegram.tgnet.TLRPC$TL_channelLocation) r1
            r2 = 2131627211(0x7f0e0ccb, float:1.888168E38)
            java.lang.Object[] r4 = new java.lang.Object[r4]
            java.lang.String r1 = r1.address
            r4[r5] = r1
            java.lang.String r1 = "ReportUnrelatedGroupText"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r4)
            android.text.SpannableStringBuilder r1 = org.telegram.messenger.AndroidUtilities.replaceTags(r1)
            r11.setMessage(r1)
            goto L_0x01a0
        L_0x0164:
            r1 = 2131627212(0x7f0e0ccc, float:1.8881682E38)
            java.lang.String r2 = "ReportUnrelatedGroupTextNoAddress"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r11.setMessage(r1)
            goto L_0x01a0
        L_0x0171:
            r1 = 2131627203(0x7f0e0cc3, float:1.8881664E38)
            java.lang.String r2 = "ReportSpamTitle"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r11.setTitle(r1)
            boolean r1 = org.telegram.messenger.ChatObject.isChannel(r22)
            if (r1 == 0) goto L_0x0194
            boolean r1 = r7.megagroup
            if (r1 != 0) goto L_0x0194
            r1 = 2131627199(0x7f0e0cbf, float:1.8881656E38)
            java.lang.String r2 = "ReportSpamAlertChannel"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r11.setMessage(r1)
            goto L_0x01a0
        L_0x0194:
            r1 = 2131627200(0x7f0e0cc0, float:1.8881658E38)
            java.lang.String r2 = "ReportSpamAlertGroup"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r11.setMessage(r1)
        L_0x01a0:
            r1 = 2131627181(0x7f0e0cad, float:1.888162E38)
            java.lang.String r2 = "ReportChat"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r12 = r1
            r4 = 0
        L_0x01ab:
            org.telegram.ui.Components.-$$Lambda$AlertsCreator$gPN4YJIWOAt6YFUFK2WPve21WNI r13 = new org.telegram.ui.Components.-$$Lambda$AlertsCreator$gPN4YJIWOAt6YFUFK2WPve21WNI
            r1 = r13
            r2 = r21
            r5 = r19
            r7 = r22
            r8 = r23
            r9 = r24
            r10 = r26
            r1.<init>(r3, r4, r5, r7, r8, r9, r10)
            r11.setPositiveButton(r12, r13)
            r1 = 2131624654(0x7f0e02ce, float:1.8876494E38)
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
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.AlertsCreator.showBlockReportSpamAlert(org.telegram.ui.ActionBar.BaseFragment, long, org.telegram.tgnet.TLRPC$User, org.telegram.tgnet.TLRPC$Chat, org.telegram.tgnet.TLRPC$EncryptedChat, boolean, org.telegram.tgnet.TLRPC$ChatFull, org.telegram.messenger.MessagesStorage$IntCallback):void");
    }

    static /* synthetic */ void lambda$showBlockReportSpamAlert$6(CheckBoxCell[] checkBoxCellArr, View view) {
        Integer num = (Integer) view.getTag();
        checkBoxCellArr[num.intValue()].setChecked(!checkBoxCellArr[num.intValue()].isChecked(), true);
    }

    static /* synthetic */ void lambda$showBlockReportSpamAlert$7(TLRPC$User tLRPC$User, AccountInstance accountInstance, CheckBoxCell[] checkBoxCellArr, long j, TLRPC$Chat tLRPC$Chat, TLRPC$EncryptedChat tLRPC$EncryptedChat, boolean z, MessagesStorage.IntCallback intCallback, DialogInterface dialogInterface, int i) {
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
                accountInstance.getMessagesController().deleteParticipantFromChat((int) (-j2), accountInstance.getMessagesController().getUser(Integer.valueOf(accountInstance.getUserConfig().getClientUserId())), (TLRPC$ChatFull) null);
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
                    textView.setOnClickListener(new View.OnClickListener(j, i2, isGlobalNotificationsEnabled, intCallback2, i, baseFragment, arrayList, intCallback, builder) {
                        public final /* synthetic */ long f$0;
                        public final /* synthetic */ int f$1;
                        public final /* synthetic */ boolean f$2;
                        public final /* synthetic */ MessagesStorage.IntCallback f$3;
                        public final /* synthetic */ int f$4;
                        public final /* synthetic */ BaseFragment f$5;
                        public final /* synthetic */ ArrayList f$6;
                        public final /* synthetic */ MessagesStorage.IntCallback f$7;
                        public final /* synthetic */ AlertDialog.Builder f$8;

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
                            AlertsCreator.lambda$showCustomNotificationsDialog$8(this.f$0, this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, this.f$8, view);
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

    /* JADX WARNING: Removed duplicated region for block: B:55:0x018c  */
    /* JADX WARNING: Removed duplicated region for block: B:58:0x0199  */
    /* JADX WARNING: Removed duplicated region for block: B:59:0x019b  */
    /* JADX WARNING: Removed duplicated region for block: B:68:0x01af  */
    /* JADX WARNING: Removed duplicated region for block: B:71:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    static /* synthetic */ void lambda$showCustomNotificationsDialog$8(long r18, int r20, boolean r21, org.telegram.messenger.MessagesStorage.IntCallback r22, int r23, org.telegram.ui.ActionBar.BaseFragment r24, java.util.ArrayList r25, org.telegram.messenger.MessagesStorage.IntCallback r26, org.telegram.ui.ActionBar.AlertDialog.Builder r27, android.view.View r28) {
        /*
            r0 = r18
            r2 = r22
            r3 = r23
            r4 = r24
            r5 = r26
            java.lang.Object r6 = r28.getTag()
            java.lang.Integer r6 = (java.lang.Integer) r6
            int r6 = r6.intValue()
            r7 = 3
            r8 = 2
            r9 = 1
            java.lang.String r10 = "notify2_"
            r11 = 0
            r12 = 0
            r14 = 4
            if (r6 != 0) goto L_0x008e
            int r15 = (r0 > r12 ? 1 : (r0 == r12 ? 0 : -1))
            if (r15 == 0) goto L_0x0084
            android.content.SharedPreferences r3 = org.telegram.messenger.MessagesController.getNotificationsSettings(r20)
            android.content.SharedPreferences$Editor r3 = r3.edit()
            if (r21 == 0) goto L_0x0040
            java.lang.StringBuilder r15 = new java.lang.StringBuilder
            r15.<init>()
            r15.append(r10)
            r15.append(r0)
            java.lang.String r10 = r15.toString()
            r3.remove(r10)
            goto L_0x0052
        L_0x0040:
            java.lang.StringBuilder r15 = new java.lang.StringBuilder
            r15.<init>()
            r15.append(r10)
            r15.append(r0)
            java.lang.String r10 = r15.toString()
            r3.putInt(r10, r11)
        L_0x0052:
            org.telegram.messenger.MessagesStorage r10 = org.telegram.messenger.MessagesStorage.getInstance(r20)
            r10.setDialogFlags(r0, r12)
            r3.commit()
            org.telegram.messenger.MessagesController r3 = org.telegram.messenger.MessagesController.getInstance(r20)
            android.util.LongSparseArray<org.telegram.tgnet.TLRPC$Dialog> r3 = r3.dialogs_dict
            java.lang.Object r3 = r3.get(r0)
            org.telegram.tgnet.TLRPC$Dialog r3 = (org.telegram.tgnet.TLRPC$Dialog) r3
            if (r3 == 0) goto L_0x0071
            org.telegram.tgnet.TLRPC$TL_peerNotifySettings r10 = new org.telegram.tgnet.TLRPC$TL_peerNotifySettings
            r10.<init>()
            r3.notify_settings = r10
        L_0x0071:
            org.telegram.messenger.NotificationsController r3 = org.telegram.messenger.NotificationsController.getInstance(r20)
            r3.updateServerNotificationsSettings((long) r0)
            if (r2 == 0) goto L_0x008b
            if (r21 == 0) goto L_0x0080
            r2.run(r11)
            goto L_0x008b
        L_0x0080:
            r2.run(r9)
            goto L_0x008b
        L_0x0084:
            org.telegram.messenger.NotificationsController r0 = org.telegram.messenger.NotificationsController.getInstance(r20)
            r0.setGlobalNotificationsEnabled(r3, r11)
        L_0x008b:
            r0 = 0
            goto L_0x018a
        L_0x008e:
            if (r6 != r7) goto L_0x00b2
            int r2 = (r0 > r12 ? 1 : (r0 == r12 ? 0 : -1))
            if (r2 == 0) goto L_0x00a7
            android.os.Bundle r2 = new android.os.Bundle
            r2.<init>()
            java.lang.String r3 = "dialog_id"
            r2.putLong(r3, r0)
            org.telegram.ui.ProfileNotificationsActivity r0 = new org.telegram.ui.ProfileNotificationsActivity
            r0.<init>(r2)
            r4.presentFragment(r0)
            goto L_0x008b
        L_0x00a7:
            org.telegram.ui.NotificationsCustomSettingsActivity r0 = new org.telegram.ui.NotificationsCustomSettingsActivity
            r1 = r25
            r0.<init>(r3, r1)
            r4.presentFragment(r0)
            goto L_0x008b
        L_0x00b2:
            org.telegram.tgnet.ConnectionsManager r15 = org.telegram.tgnet.ConnectionsManager.getInstance(r20)
            int r15 = r15.getCurrentTime()
            r11 = 2147483647(0x7fffffff, float:NaN)
            if (r6 != r9) goto L_0x00c2
            int r15 = r15 + 3600
            goto L_0x00cf
        L_0x00c2:
            if (r6 != r8) goto L_0x00ca
            r16 = 172800(0x2a300, float:2.42144E-40)
            int r15 = r15 + r16
            goto L_0x00cf
        L_0x00ca:
            if (r6 != r14) goto L_0x00cf
            r15 = 2147483647(0x7fffffff, float:NaN)
        L_0x00cf:
            int r16 = (r0 > r12 ? 1 : (r0 == r12 ? 0 : -1))
            if (r16 == 0) goto L_0x0178
            android.content.SharedPreferences r3 = org.telegram.messenger.MessagesController.getNotificationsSettings(r20)
            android.content.SharedPreferences$Editor r3 = r3.edit()
            r16 = 1
            if (r6 != r14) goto L_0x0109
            if (r21 != 0) goto L_0x00f4
            java.lang.StringBuilder r11 = new java.lang.StringBuilder
            r11.<init>()
            r11.append(r10)
            r11.append(r0)
            java.lang.String r10 = r11.toString()
            r3.remove(r10)
            goto L_0x0135
        L_0x00f4:
            java.lang.StringBuilder r11 = new java.lang.StringBuilder
            r11.<init>()
            r11.append(r10)
            r11.append(r0)
            java.lang.String r10 = r11.toString()
            r3.putInt(r10, r8)
            r12 = r16
            goto L_0x0135
        L_0x0109:
            java.lang.StringBuilder r11 = new java.lang.StringBuilder
            r11.<init>()
            r11.append(r10)
            r11.append(r0)
            java.lang.String r10 = r11.toString()
            r3.putInt(r10, r7)
            java.lang.StringBuilder r10 = new java.lang.StringBuilder
            r10.<init>()
            java.lang.String r11 = "notifyuntil_"
            r10.append(r11)
            r10.append(r0)
            java.lang.String r10 = r10.toString()
            r3.putInt(r10, r15)
            long r10 = (long) r15
            r12 = 32
            long r10 = r10 << r12
            long r12 = r10 | r16
        L_0x0135:
            org.telegram.messenger.NotificationsController r10 = org.telegram.messenger.NotificationsController.getInstance(r20)
            r10.removeNotificationsForDialog(r0)
            org.telegram.messenger.MessagesStorage r10 = org.telegram.messenger.MessagesStorage.getInstance(r20)
            r10.setDialogFlags(r0, r12)
            r3.commit()
            org.telegram.messenger.MessagesController r3 = org.telegram.messenger.MessagesController.getInstance(r20)
            android.util.LongSparseArray<org.telegram.tgnet.TLRPC$Dialog> r3 = r3.dialogs_dict
            java.lang.Object r3 = r3.get(r0)
            org.telegram.tgnet.TLRPC$Dialog r3 = (org.telegram.tgnet.TLRPC$Dialog) r3
            if (r3 == 0) goto L_0x0161
            org.telegram.tgnet.TLRPC$TL_peerNotifySettings r10 = new org.telegram.tgnet.TLRPC$TL_peerNotifySettings
            r10.<init>()
            r3.notify_settings = r10
            if (r6 != r14) goto L_0x015f
            if (r21 == 0) goto L_0x0161
        L_0x015f:
            r10.mute_until = r15
        L_0x0161:
            org.telegram.messenger.NotificationsController r3 = org.telegram.messenger.NotificationsController.getInstance(r20)
            r3.updateServerNotificationsSettings((long) r0)
            if (r2 == 0) goto L_0x008b
            if (r6 != r14) goto L_0x0173
            if (r21 != 0) goto L_0x0173
            r0 = 0
            r2.run(r0)
            goto L_0x018a
        L_0x0173:
            r0 = 0
            r2.run(r9)
            goto L_0x018a
        L_0x0178:
            r0 = 0
            if (r6 != r14) goto L_0x0183
            org.telegram.messenger.NotificationsController r1 = org.telegram.messenger.NotificationsController.getInstance(r20)
            r1.setGlobalNotificationsEnabled(r3, r11)
            goto L_0x018a
        L_0x0183:
            org.telegram.messenger.NotificationsController r1 = org.telegram.messenger.NotificationsController.getInstance(r20)
            r1.setGlobalNotificationsEnabled(r3, r15)
        L_0x018a:
            if (r5 == 0) goto L_0x018f
            r5.run(r6)
        L_0x018f:
            java.lang.Runnable r1 = r27.getDismissRunnable()
            r1.run()
            r1 = -1
            if (r6 != 0) goto L_0x019b
            r7 = 4
            goto L_0x01a7
        L_0x019b:
            if (r6 != r9) goto L_0x019f
            r7 = 0
            goto L_0x01a7
        L_0x019f:
            if (r6 != r8) goto L_0x01a3
            r7 = 2
            goto L_0x01a7
        L_0x01a3:
            if (r6 != r14) goto L_0x01a6
            goto L_0x01a7
        L_0x01a6:
            r7 = -1
        L_0x01a7:
            if (r7 < 0) goto L_0x01b6
            boolean r0 = org.telegram.ui.Components.BulletinFactory.canShowBulletin(r24)
            if (r0 == 0) goto L_0x01b6
            org.telegram.ui.Components.Bulletin r0 = org.telegram.ui.Components.BulletinFactory.createMuteBulletin((org.telegram.ui.ActionBar.BaseFragment) r4, (int) r7)
            r0.show()
        L_0x01b6:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.AlertsCreator.lambda$showCustomNotificationsDialog$8(long, int, boolean, org.telegram.messenger.MessagesStorage$IntCallback, int, org.telegram.ui.ActionBar.BaseFragment, java.util.ArrayList, org.telegram.messenger.MessagesStorage$IntCallback, org.telegram.ui.ActionBar.AlertDialog$Builder, android.view.View):void");
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
                public final /* synthetic */ ArrayList f$0;
                public final /* synthetic */ Runnable f$1;
                public final /* synthetic */ AlertDialog.Builder f$2;

                {
                    this.f$0 = r1;
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void onClick(View view) {
                    AlertsCreator.lambda$showSecretLocationAlert$9(this.f$0, this.f$1, this.f$2, view);
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

    static /* synthetic */ void lambda$showSecretLocationAlert$9(ArrayList arrayList, Runnable runnable, AlertDialog.Builder builder, View view) {
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
        showOpenUrlAlert(baseFragment, str, z, true, z2);
    }

    /* JADX WARNING: Removed duplicated region for block: B:18:0x008d  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static void showOpenUrlAlert(org.telegram.ui.ActionBar.BaseFragment r11, java.lang.String r12, boolean r13, boolean r14, boolean r15) {
        /*
            if (r11 == 0) goto L_0x00d8
            android.app.Activity r0 = r11.getParentActivity()
            if (r0 != 0) goto L_0x000a
            goto L_0x00d8
        L_0x000a:
            boolean r0 = r11 instanceof org.telegram.ui.ChatActivity
            r1 = 0
            if (r0 == 0) goto L_0x0019
            r0 = r11
            org.telegram.ui.ChatActivity r0 = (org.telegram.ui.ChatActivity) r0
            long r3 = r0.getInlineReturn()
            r8 = r3
            goto L_0x001a
        L_0x0019:
            r8 = r1
        L_0x001a:
            r0 = 0
            boolean r3 = org.telegram.messenger.browser.Browser.isInternalUrl(r12, r0)
            r4 = 1
            r5 = 0
            if (r3 != 0) goto L_0x00cb
            if (r15 != 0) goto L_0x0027
            goto L_0x00cb
        L_0x0027:
            if (r13 == 0) goto L_0x0059
            android.net.Uri r13 = android.net.Uri.parse(r12)     // Catch:{ Exception -> 0x0055 }
            java.lang.String r15 = r13.getHost()     // Catch:{ Exception -> 0x0055 }
            java.lang.String r15 = java.net.IDN.toASCII(r15, r4)     // Catch:{ Exception -> 0x0055 }
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0055 }
            r1.<init>()     // Catch:{ Exception -> 0x0055 }
            java.lang.String r2 = r13.getScheme()     // Catch:{ Exception -> 0x0055 }
            r1.append(r2)     // Catch:{ Exception -> 0x0055 }
            java.lang.String r2 = "://"
            r1.append(r2)     // Catch:{ Exception -> 0x0055 }
            r1.append(r15)     // Catch:{ Exception -> 0x0055 }
            java.lang.String r13 = r13.getPath()     // Catch:{ Exception -> 0x0055 }
            r1.append(r13)     // Catch:{ Exception -> 0x0055 }
            java.lang.String r13 = r1.toString()     // Catch:{ Exception -> 0x0055 }
            goto L_0x005a
        L_0x0055:
            r13 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r13)
        L_0x0059:
            r13 = r12
        L_0x005a:
            org.telegram.ui.ActionBar.AlertDialog$Builder r15 = new org.telegram.ui.ActionBar.AlertDialog$Builder
            android.app.Activity r1 = r11.getParentActivity()
            r15.<init>((android.content.Context) r1)
            r1 = 2131626562(0x7f0e0a42, float:1.8880364E38)
            java.lang.String r2 = "OpenUrlTitle"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r15.setTitle(r1)
            r1 = 2131626559(0x7f0e0a3f, float:1.8880358E38)
            java.lang.String r2 = "OpenUrlAlert2"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            java.lang.String r2 = "%"
            int r2 = r1.indexOf(r2)
            android.text.SpannableStringBuilder r3 = new android.text.SpannableStringBuilder
            java.lang.Object[] r4 = new java.lang.Object[r4]
            r4[r5] = r13
            java.lang.String r1 = java.lang.String.format(r1, r4)
            r3.<init>(r1)
            if (r2 < 0) goto L_0x009c
            android.text.style.URLSpan r1 = new android.text.style.URLSpan
            r1.<init>(r13)
            int r13 = r13.length()
            int r13 = r13 + r2
            r4 = 33
            r3.setSpan(r1, r2, r13, r4)
        L_0x009c:
            r15.setMessage(r3)
            r15.setMessageTextViewClickable(r5)
            r13 = 2131626546(0x7f0e0a32, float:1.8880331E38)
            java.lang.String r1 = "Open"
            java.lang.String r13 = org.telegram.messenger.LocaleController.getString(r1, r13)
            org.telegram.ui.Components.-$$Lambda$AlertsCreator$U2MmMIxSeP3iUonn2jcMXTz4tUI r1 = new org.telegram.ui.Components.-$$Lambda$AlertsCreator$U2MmMIxSeP3iUonn2jcMXTz4tUI
            r5 = r1
            r6 = r11
            r7 = r12
            r10 = r14
            r5.<init>(r7, r8, r10)
            r15.setPositiveButton(r13, r1)
            r12 = 2131624654(0x7f0e02ce, float:1.8876494E38)
            java.lang.String r13 = "Cancel"
            java.lang.String r12 = org.telegram.messenger.LocaleController.getString(r13, r12)
            r15.setNegativeButton(r12, r0)
            org.telegram.ui.ActionBar.AlertDialog r12 = r15.create()
            r11.showDialog(r12)
            goto L_0x00d8
        L_0x00cb:
            android.app.Activity r11 = r11.getParentActivity()
            int r13 = (r8 > r1 ? 1 : (r8 == r1 ? 0 : -1))
            if (r13 != 0) goto L_0x00d4
            goto L_0x00d5
        L_0x00d4:
            r4 = 0
        L_0x00d5:
            org.telegram.messenger.browser.Browser.openUrl((android.content.Context) r11, (java.lang.String) r12, (boolean) r4, (boolean) r14)
        L_0x00d8:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.AlertsCreator.showOpenUrlAlert(org.telegram.ui.ActionBar.BaseFragment, java.lang.String, boolean, boolean, boolean):void");
    }

    static /* synthetic */ void lambda$showOpenUrlAlert$10(BaseFragment baseFragment, String str, long j, boolean z, DialogInterface dialogInterface, int i) {
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
                public final /* synthetic */ SharedPreferences f$0;
                public final /* synthetic */ AlertDialog f$1;
                public final /* synthetic */ int f$2;
                public final /* synthetic */ BaseFragment f$3;

                {
                    this.f$0 = r1;
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                }

                public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                    AlertsCreator.lambda$performAskAQuestion$14(this.f$0, this.f$1, this.f$2, this.f$3, tLObject, tLRPC$TL_error);
                }
            });
            return;
        }
        MessagesController.getInstance(currentAccount).putUser(tLRPC$User, true);
        Bundle bundle = new Bundle();
        bundle.putInt("user_id", tLRPC$User.id);
        baseFragment.presentFragment(new ChatActivity(bundle));
    }

    static /* synthetic */ void lambda$performAskAQuestion$14(SharedPreferences sharedPreferences, AlertDialog alertDialog, int i, BaseFragment baseFragment, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        if (tLRPC$TL_error == null) {
            AndroidUtilities.runOnUIThread(new Runnable(sharedPreferences, (TLRPC$TL_help_support) tLObject, alertDialog, i, baseFragment) {
                public final /* synthetic */ SharedPreferences f$0;
                public final /* synthetic */ TLRPC$TL_help_support f$1;
                public final /* synthetic */ AlertDialog f$2;
                public final /* synthetic */ int f$3;
                public final /* synthetic */ BaseFragment f$4;

                {
                    this.f$0 = r1;
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                    this.f$4 = r5;
                }

                public final void run() {
                    AlertsCreator.lambda$performAskAQuestion$12(this.f$0, this.f$1, this.f$2, this.f$3, this.f$4);
                }
            });
            return;
        }
        AndroidUtilities.runOnUIThread(new Runnable() {
            public final void run() {
                AlertsCreator.lambda$performAskAQuestion$13(AlertDialog.this);
            }
        });
    }

    static /* synthetic */ void lambda$performAskAQuestion$12(SharedPreferences sharedPreferences, TLRPC$TL_help_support tLRPC$TL_help_support, AlertDialog alertDialog, int i, BaseFragment baseFragment) {
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

    static /* synthetic */ void lambda$performAskAQuestion$13(AlertDialog alertDialog) {
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
                int clientUserId = UserConfig.getInstance(currentAccount).getClientUserId();
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
                builder.setPositiveButton(LocaleController.getString("Import", NUM), new DialogInterface.OnClickListener(runnable) {
                    public final /* synthetic */ Runnable f$0;

                    {
                        this.f$0 = r1;
                    }

                    public final void onClick(DialogInterface dialogInterface, int i) {
                        AlertsCreator.lambda$createImportDialogAlert$15(this.f$0, dialogInterface, i);
                    }
                });
                builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
                baseFragment2.showDialog(builder.create());
            }
        }
    }

    static /* synthetic */ void lambda$createImportDialogAlert$15(Runnable runnable, DialogInterface dialogInterface, int i) {
        if (runnable != null) {
            runnable.run();
        }
    }

    public static void createClearOrDeleteDialogAlert(BaseFragment baseFragment, boolean z, TLRPC$Chat tLRPC$Chat, TLRPC$User tLRPC$User, boolean z2, boolean z3, MessagesStorage.BooleanCallback booleanCallback) {
        createClearOrDeleteDialogAlert(baseFragment, z, tLRPC$Chat != null && tLRPC$Chat.creator, false, tLRPC$Chat, tLRPC$User, z2, z3, booleanCallback);
    }

    /* JADX WARNING: Removed duplicated region for block: B:123:0x02d5  */
    /* JADX WARNING: Removed duplicated region for block: B:130:0x030a  */
    /* JADX WARNING: Removed duplicated region for block: B:132:0x0316  */
    /* JADX WARNING: Removed duplicated region for block: B:136:0x0340  */
    /* JADX WARNING: Removed duplicated region for block: B:183:0x04e1  */
    /* JADX WARNING: Removed duplicated region for block: B:185:0x04ed  */
    /* JADX WARNING: Removed duplicated region for block: B:206:0x058f  */
    /* JADX WARNING: Removed duplicated region for block: B:210:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static void createClearOrDeleteDialogAlert(org.telegram.ui.ActionBar.BaseFragment r36, boolean r37, boolean r38, boolean r39, org.telegram.tgnet.TLRPC$Chat r40, org.telegram.tgnet.TLRPC$User r41, boolean r42, boolean r43, org.telegram.messenger.MessagesStorage.BooleanCallback r44) {
        /*
            r12 = r36
            r8 = r40
            r4 = r41
            if (r12 == 0) goto L_0x0598
            android.app.Activity r0 = r36.getParentActivity()
            if (r0 == 0) goto L_0x0598
            if (r8 != 0) goto L_0x0014
            if (r4 != 0) goto L_0x0014
            goto L_0x0598
        L_0x0014:
            int r0 = r36.getCurrentAccount()
            android.app.Activity r1 = r36.getParentActivity()
            org.telegram.ui.ActionBar.AlertDialog$Builder r13 = new org.telegram.ui.ActionBar.AlertDialog$Builder
            r13.<init>((android.content.Context) r1)
            org.telegram.messenger.UserConfig r2 = org.telegram.messenger.UserConfig.getInstance(r0)
            int r2 = r2.getClientUserId()
            r3 = 1
            org.telegram.ui.Cells.CheckBoxCell[] r5 = new org.telegram.ui.Cells.CheckBoxCell[r3]
            android.widget.TextView r6 = new android.widget.TextView
            r6.<init>(r1)
            java.lang.String r7 = "dialogTextBlack"
            int r7 = org.telegram.ui.ActionBar.Theme.getColor(r7)
            r6.setTextColor(r7)
            r7 = 1098907648(0x41800000, float:16.0)
            r6.setTextSize(r3, r7)
            boolean r9 = org.telegram.messenger.LocaleController.isRTL
            if (r9 == 0) goto L_0x0045
            r9 = 5
            goto L_0x0046
        L_0x0045:
            r9 = 3
        L_0x0046:
            r9 = r9 | 48
            r6.setGravity(r9)
            boolean r9 = org.telegram.messenger.ChatObject.isChannel(r40)
            if (r9 == 0) goto L_0x005b
            java.lang.String r9 = r8.username
            boolean r9 = android.text.TextUtils.isEmpty(r9)
            if (r9 != 0) goto L_0x005b
            r9 = 1
            goto L_0x005c
        L_0x005b:
            r9 = 0
        L_0x005c:
            org.telegram.ui.Components.AlertsCreator$3 r15 = new org.telegram.ui.Components.AlertsCreator$3
            r15.<init>(r1, r5)
            r13.setView(r15)
            org.telegram.ui.Components.AvatarDrawable r10 = new org.telegram.ui.Components.AvatarDrawable
            r10.<init>()
            r17 = 1094713344(0x41400000, float:12.0)
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r17)
            r10.setTextSize(r11)
            org.telegram.ui.Components.BackupImageView r11 = new org.telegram.ui.Components.BackupImageView
            r11.<init>(r1)
            r7 = 1101004800(0x41a00000, float:20.0)
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r7)
            r11.setRoundRadius(r14)
            r19 = 40
            r20 = 1109393408(0x42200000, float:40.0)
            boolean r14 = org.telegram.messenger.LocaleController.isRTL
            if (r14 == 0) goto L_0x008a
            r14 = 5
            goto L_0x008b
        L_0x008a:
            r14 = 3
        L_0x008b:
            r21 = r14 | 48
            r22 = 1102053376(0x41b00000, float:22.0)
            r23 = 1084227584(0x40a00000, float:5.0)
            r24 = 1102053376(0x41b00000, float:22.0)
            r25 = 0
            android.widget.FrameLayout$LayoutParams r14 = org.telegram.ui.Components.LayoutHelper.createFrame(r19, r20, r21, r22, r23, r24, r25)
            r15.addView(r11, r14)
            android.widget.TextView r14 = new android.widget.TextView
            r14.<init>(r1)
            java.lang.String r19 = "actionBarDefaultSubmenuItem"
            int r12 = org.telegram.ui.ActionBar.Theme.getColor(r19)
            r14.setTextColor(r12)
            r14.setTextSize(r3, r7)
            java.lang.String r7 = "fonts/rmedium.ttf"
            android.graphics.Typeface r7 = org.telegram.messenger.AndroidUtilities.getTypeface(r7)
            r14.setTypeface(r7)
            r14.setLines(r3)
            r14.setMaxLines(r3)
            r14.setSingleLine(r3)
            boolean r7 = org.telegram.messenger.LocaleController.isRTL
            if (r7 == 0) goto L_0x00c5
            r7 = 5
            goto L_0x00c6
        L_0x00c5:
            r7 = 3
        L_0x00c6:
            r7 = r7 | 16
            r14.setGravity(r7)
            android.text.TextUtils$TruncateAt r7 = android.text.TextUtils.TruncateAt.END
            r14.setEllipsize(r7)
            java.lang.String r12 = "LeaveChannelMenu"
            java.lang.String r3 = "DeleteChatUser"
            java.lang.String r7 = "ClearHistoryCache"
            r23 = r13
            java.lang.String r13 = "ClearHistory"
            r25 = r11
            java.lang.String r11 = "LeaveMegaMenu"
            if (r37 == 0) goto L_0x0102
            if (r9 == 0) goto L_0x00f2
            r26 = r9
            r27 = r10
            r9 = 2131624912(0x7f0e03d0, float:1.8877017E38)
            java.lang.String r10 = org.telegram.messenger.LocaleController.getString(r7, r9)
            r14.setText(r10)
            goto L_0x016e
        L_0x00f2:
            r26 = r9
            r27 = r10
            r9 = 2131624911(0x7f0e03cf, float:1.8877015E38)
            java.lang.String r10 = org.telegram.messenger.LocaleController.getString(r13, r9)
            r14.setText(r10)
            goto L_0x016e
        L_0x0102:
            r26 = r9
            r27 = r10
            if (r38 == 0) goto L_0x0137
            boolean r9 = org.telegram.messenger.ChatObject.isChannel(r40)
            java.lang.String r10 = "DeleteMegaMenu"
            if (r9 == 0) goto L_0x012c
            boolean r9 = r8.megagroup
            if (r9 == 0) goto L_0x011f
            r9 = 2131625148(0x7f0e04bc, float:1.8877496E38)
            java.lang.String r9 = org.telegram.messenger.LocaleController.getString(r10, r9)
            r14.setText(r9)
            goto L_0x016e
        L_0x011f:
            r9 = 2131624720(0x7f0e0310, float:1.8876628E38)
            java.lang.String r10 = "ChannelDeleteMenu"
            java.lang.String r9 = org.telegram.messenger.LocaleController.getString(r10, r9)
            r14.setText(r9)
            goto L_0x016e
        L_0x012c:
            r9 = 2131625148(0x7f0e04bc, float:1.8877496E38)
            java.lang.String r9 = org.telegram.messenger.LocaleController.getString(r10, r9)
            r14.setText(r9)
            goto L_0x016e
        L_0x0137:
            if (r8 == 0) goto L_0x0164
            boolean r9 = org.telegram.messenger.ChatObject.isChannel(r40)
            if (r9 == 0) goto L_0x0159
            boolean r9 = r8.megagroup
            if (r9 == 0) goto L_0x014e
            r9 = 2131625953(0x7f0e07e1, float:1.8879129E38)
            java.lang.String r10 = org.telegram.messenger.LocaleController.getString(r11, r9)
            r14.setText(r10)
            goto L_0x016e
        L_0x014e:
            r10 = 2131625951(0x7f0e07df, float:1.8879124E38)
            java.lang.String r9 = org.telegram.messenger.LocaleController.getString(r12, r10)
            r14.setText(r9)
            goto L_0x016e
        L_0x0159:
            r9 = 2131625953(0x7f0e07e1, float:1.8879129E38)
            java.lang.String r10 = org.telegram.messenger.LocaleController.getString(r11, r9)
            r14.setText(r10)
            goto L_0x016e
        L_0x0164:
            r9 = 2131625134(0x7f0e04ae, float:1.8877467E38)
            java.lang.String r10 = org.telegram.messenger.LocaleController.getString(r3, r9)
            r14.setText(r10)
        L_0x016e:
            r28 = -1
            r29 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            boolean r9 = org.telegram.messenger.LocaleController.isRTL
            if (r9 == 0) goto L_0x0178
            r10 = 5
            goto L_0x0179
        L_0x0178:
            r10 = 3
        L_0x0179:
            r30 = r10 | 48
            if (r9 == 0) goto L_0x0182
            r31 = 21
            r10 = 21
            goto L_0x0184
        L_0x0182:
            r10 = 76
        L_0x0184:
            float r10 = (float) r10
            r32 = 1093664768(0x41300000, float:11.0)
            if (r9 == 0) goto L_0x018c
            r9 = 76
            goto L_0x018e
        L_0x018c:
            r9 = 21
        L_0x018e:
            float r9 = (float) r9
            r34 = 0
            r31 = r10
            r33 = r9
            android.widget.FrameLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createFrame(r28, r29, r30, r31, r32, r33, r34)
            r15.addView(r14, r9)
            r28 = -2
            r29 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            boolean r9 = org.telegram.messenger.LocaleController.isRTL
            if (r9 == 0) goto L_0x01a6
            r10 = 5
            goto L_0x01a7
        L_0x01a6:
            r10 = 3
        L_0x01a7:
            r30 = r10 | 48
            r31 = 1103101952(0x41CLASSNAME, float:24.0)
            r32 = 1113849856(0x42640000, float:57.0)
            r33 = 1103101952(0x41CLASSNAME, float:24.0)
            r34 = 1091567616(0x41100000, float:9.0)
            android.widget.FrameLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createFrame(r28, r29, r30, r31, r32, r33, r34)
            r15.addView(r6, r9)
            if (r4 == 0) goto L_0x01cc
            boolean r9 = r4.bot
            if (r9 != 0) goto L_0x01cc
            int r9 = r4.id
            if (r9 == r2) goto L_0x01cc
            org.telegram.messenger.MessagesController r9 = org.telegram.messenger.MessagesController.getInstance(r0)
            boolean r9 = r9.canRevokePmInbox
            if (r9 == 0) goto L_0x01cc
            r9 = 1
            goto L_0x01cd
        L_0x01cc:
            r9 = 0
        L_0x01cd:
            if (r4 == 0) goto L_0x01d6
            org.telegram.messenger.MessagesController r0 = org.telegram.messenger.MessagesController.getInstance(r0)
            int r0 = r0.revokeTimePmLimit
            goto L_0x01dc
        L_0x01d6:
            org.telegram.messenger.MessagesController r0 = org.telegram.messenger.MessagesController.getInstance(r0)
            int r0 = r0.revokeTimeLimit
        L_0x01dc:
            if (r42 != 0) goto L_0x01e9
            if (r4 == 0) goto L_0x01e9
            if (r9 == 0) goto L_0x01e9
            r9 = 2147483647(0x7fffffff, float:NaN)
            if (r0 != r9) goto L_0x01e9
            r0 = 1
            goto L_0x01ea
        L_0x01e9:
            r0 = 0
        L_0x01ea:
            r9 = 1
            boolean[] r14 = new boolean[r9]
            if (r39 != 0) goto L_0x01fe
            if (r42 == 0) goto L_0x01f3
            if (r37 == 0) goto L_0x01f5
        L_0x01f3:
            if (r0 == 0) goto L_0x01fe
        L_0x01f5:
            boolean r0 = org.telegram.messenger.UserObject.isDeleted(r41)
            if (r0 == 0) goto L_0x01fc
            goto L_0x01fe
        L_0x01fc:
            r0 = 0
            goto L_0x020d
        L_0x01fe:
            if (r43 == 0) goto L_0x020a
            if (r37 != 0) goto L_0x020a
            if (r8 == 0) goto L_0x020a
            boolean r0 = r8.creator
            if (r0 == 0) goto L_0x020a
            r0 = 1
            goto L_0x020b
        L_0x020a:
            r0 = 0
        L_0x020b:
            if (r0 == 0) goto L_0x02ce
        L_0x020d:
            org.telegram.ui.Cells.CheckBoxCell r9 = new org.telegram.ui.Cells.CheckBoxCell
            r10 = 1
            r9.<init>(r1, r10)
            r1 = 0
            r5[r1] = r9
            r9 = r5[r1]
            android.graphics.drawable.Drawable r10 = org.telegram.ui.ActionBar.Theme.getSelectorDrawable(r1)
            r9.setBackgroundDrawable(r10)
            java.lang.String r9 = ""
            if (r0 == 0) goto L_0x0251
            boolean r0 = org.telegram.messenger.ChatObject.isChannel(r40)
            if (r0 == 0) goto L_0x023e
            boolean r0 = r8.megagroup
            if (r0 != 0) goto L_0x023e
            r0 = r5[r1]
            r10 = 2131625131(0x7f0e04ab, float:1.8877461E38)
            r16 = r3
            java.lang.String r3 = "DeleteChannelForAll"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r3, r10)
            r0.setText(r3, r9, r1, r1)
            goto L_0x024e
        L_0x023e:
            r16 = r3
            r0 = r5[r1]
            r3 = 2131625142(0x7f0e04b6, float:1.8877484E38)
            java.lang.String r10 = "DeleteGroupForAll"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r10, r3)
            r0.setText(r3, r9, r1, r1)
        L_0x024e:
            r28 = r12
            goto L_0x0288
        L_0x0251:
            r16 = r3
            if (r37 == 0) goto L_0x026f
            r0 = r5[r1]
            r10 = 1
            java.lang.Object[] r3 = new java.lang.Object[r10]
            java.lang.String r18 = org.telegram.messenger.UserObject.getFirstName(r41)
            r3[r1] = r18
            java.lang.String r10 = "ClearHistoryOptionAlso"
            r28 = r12
            r12 = 2131624913(0x7f0e03d1, float:1.887702E38)
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r10, r12, r3)
            r0.setText(r3, r9, r1, r1)
            goto L_0x0288
        L_0x026f:
            r28 = r12
            r0 = r5[r1]
            r3 = 2131625150(0x7f0e04be, float:1.88775E38)
            r10 = 1
            java.lang.Object[] r12 = new java.lang.Object[r10]
            java.lang.String r10 = org.telegram.messenger.UserObject.getFirstName(r41)
            r12[r1] = r10
            java.lang.String r10 = "DeleteMessagesOptionAlso"
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r10, r3, r12)
            r0.setText(r3, r9, r1, r1)
        L_0x0288:
            r0 = r5[r1]
            boolean r1 = org.telegram.messenger.LocaleController.isRTL
            if (r1 == 0) goto L_0x0295
            r1 = 1098907648(0x41800000, float:16.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r1)
            goto L_0x029d
        L_0x0295:
            r1 = 1098907648(0x41800000, float:16.0)
            r3 = 1090519040(0x41000000, float:8.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
        L_0x029d:
            boolean r9 = org.telegram.messenger.LocaleController.isRTL
            if (r9 == 0) goto L_0x02a3
            r1 = 1090519040(0x41000000, float:8.0)
        L_0x02a3:
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            r9 = 0
            r0.setPadding(r3, r9, r1, r9)
            r0 = r5[r9]
            r29 = -1
            r30 = 1111490560(0x42400000, float:48.0)
            r31 = 83
            r32 = 0
            r33 = 0
            r34 = 0
            r35 = 0
            android.widget.FrameLayout$LayoutParams r1 = org.telegram.ui.Components.LayoutHelper.createFrame(r29, r30, r31, r32, r33, r34, r35)
            r15.addView(r0, r1)
            r0 = 0
            r1 = r5[r0]
            org.telegram.ui.Components.-$$Lambda$AlertsCreator$FvEsDKI58ORotZu0OBVVOMj-LIY r0 = new org.telegram.ui.Components.-$$Lambda$AlertsCreator$FvEsDKI58ORotZu0OBVVOMj-LIY
            r0.<init>(r14)
            r1.setOnClickListener(r0)
            goto L_0x02d2
        L_0x02ce:
            r16 = r3
            r28 = r12
        L_0x02d2:
            r12 = 0
            if (r4 == 0) goto L_0x030a
            boolean r0 = org.telegram.messenger.UserObject.isReplyUser((org.telegram.tgnet.TLRPC$User) r41)
            if (r0 == 0) goto L_0x02ec
            r0 = r27
            r1 = 1
            r0.setSmallSize(r1)
            r3 = 12
            r0.setAvatarType(r3)
            r3 = r25
            r3.setImage((org.telegram.messenger.ImageLocation) r12, (java.lang.String) r12, (android.graphics.drawable.Drawable) r0, (java.lang.Object) r4)
            goto L_0x0314
        L_0x02ec:
            r3 = r25
            r0 = r27
            r1 = 1
            int r5 = r4.id
            if (r5 != r2) goto L_0x02ff
            r0.setSmallSize(r1)
            r0.setAvatarType(r1)
            r3.setImage((org.telegram.messenger.ImageLocation) r12, (java.lang.String) r12, (android.graphics.drawable.Drawable) r0, (java.lang.Object) r4)
            goto L_0x0314
        L_0x02ff:
            r1 = 0
            r0.setSmallSize(r1)
            r0.setInfo((org.telegram.tgnet.TLRPC$User) r4)
            r3.setForUserOrChat(r4, r0)
            goto L_0x0314
        L_0x030a:
            r3 = r25
            r0 = r27
            r0.setInfo((org.telegram.tgnet.TLRPC$Chat) r8)
            r3.setForUserOrChat(r8, r0)
        L_0x0314:
            if (r39 == 0) goto L_0x0340
            boolean r0 = org.telegram.messenger.UserObject.isUserSelf(r41)
            if (r0 == 0) goto L_0x032e
            r0 = 2131625116(0x7f0e049c, float:1.887743E38)
            java.lang.String r1 = "DeleteAllMessagesSavedAlert"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            r6.setText(r0)
            goto L_0x04df
        L_0x032e:
            r0 = 2131625115(0x7f0e049b, float:1.8877429E38)
            java.lang.String r1 = "DeleteAllMessagesAlert"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            r6.setText(r0)
            goto L_0x04df
        L_0x0340:
            if (r37 == 0) goto L_0x03e1
            if (r4 == 0) goto L_0x0394
            if (r42 == 0) goto L_0x0362
            r0 = 2131624336(0x7f0e0190, float:1.8875849E38)
            r1 = 1
            java.lang.Object[] r1 = new java.lang.Object[r1]
            java.lang.String r2 = org.telegram.messenger.UserObject.getUserName(r41)
            r3 = 0
            r1[r3] = r2
            java.lang.String r2 = "AreYouSureClearHistoryWithSecretUser"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            r6.setText(r0)
            goto L_0x04df
        L_0x0362:
            int r0 = r4.id
            if (r0 != r2) goto L_0x0378
            r0 = 2131624334(0x7f0e018e, float:1.8875845E38)
            java.lang.String r1 = "AreYouSureClearHistorySavedMessages"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            r6.setText(r0)
            goto L_0x04df
        L_0x0378:
            r0 = 2131624337(0x7f0e0191, float:1.887585E38)
            r1 = 1
            java.lang.Object[] r1 = new java.lang.Object[r1]
            java.lang.String r2 = org.telegram.messenger.UserObject.getUserName(r41)
            r3 = 0
            r1[r3] = r2
            java.lang.String r2 = "AreYouSureClearHistoryWithUser"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            r6.setText(r0)
            goto L_0x04df
        L_0x0394:
            boolean r0 = org.telegram.messenger.ChatObject.isChannel(r40)
            if (r0 == 0) goto L_0x03c7
            boolean r0 = r8.megagroup
            if (r0 == 0) goto L_0x03a7
            java.lang.String r0 = r8.username
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            if (r0 == 0) goto L_0x03a7
            goto L_0x03c7
        L_0x03a7:
            boolean r0 = r8.megagroup
            if (r0 == 0) goto L_0x03b9
            r0 = 2131624333(0x7f0e018d, float:1.8875843E38)
            java.lang.String r1 = "AreYouSureClearHistoryGroup"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            r6.setText(r0)
            goto L_0x04df
        L_0x03b9:
            r0 = 2131624331(0x7f0e018b, float:1.8875839E38)
            java.lang.String r1 = "AreYouSureClearHistoryChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            r6.setText(r0)
            goto L_0x04df
        L_0x03c7:
            r0 = 2131624335(0x7f0e018f, float:1.8875847E38)
            r1 = 1
            java.lang.Object[] r1 = new java.lang.Object[r1]
            java.lang.String r2 = r8.title
            r3 = 0
            r1[r3] = r2
            java.lang.String r2 = "AreYouSureClearHistoryWithChat"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            r6.setText(r0)
            goto L_0x04df
        L_0x03e1:
            if (r38 == 0) goto L_0x0417
            boolean r0 = org.telegram.messenger.ChatObject.isChannel(r40)
            if (r0 == 0) goto L_0x0409
            boolean r0 = r8.megagroup
            if (r0 == 0) goto L_0x03fb
            r0 = 2131624338(0x7f0e0192, float:1.8875853E38)
            java.lang.String r1 = "AreYouSureDeleteAndExit"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            r6.setText(r0)
            goto L_0x04df
        L_0x03fb:
            r0 = 2131624339(0x7f0e0193, float:1.8875855E38)
            java.lang.String r1 = "AreYouSureDeleteAndExitChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            r6.setText(r0)
            goto L_0x04df
        L_0x0409:
            r0 = 2131624338(0x7f0e0192, float:1.8875853E38)
            java.lang.String r1 = "AreYouSureDeleteAndExit"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            r6.setText(r0)
            goto L_0x04df
        L_0x0417:
            if (r4 == 0) goto L_0x048b
            if (r42 == 0) goto L_0x0437
            r0 = 2131624357(0x7f0e01a5, float:1.8875891E38)
            r1 = 1
            java.lang.Object[] r1 = new java.lang.Object[r1]
            java.lang.String r2 = org.telegram.messenger.UserObject.getUserName(r41)
            r3 = 0
            r1[r3] = r2
            java.lang.String r2 = "AreYouSureDeleteThisChatWithSecretUser"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            r6.setText(r0)
            goto L_0x04df
        L_0x0437:
            int r0 = r4.id
            if (r0 != r2) goto L_0x044d
            r0 = 2131624354(0x7f0e01a2, float:1.8875885E38)
            java.lang.String r1 = "AreYouSureDeleteThisChatSavedMessages"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            r6.setText(r0)
            goto L_0x04df
        L_0x044d:
            boolean r0 = r4.bot
            if (r0 == 0) goto L_0x0470
            boolean r0 = r4.support
            if (r0 != 0) goto L_0x0470
            r0 = 2131624355(0x7f0e01a3, float:1.8875887E38)
            r1 = 1
            java.lang.Object[] r1 = new java.lang.Object[r1]
            java.lang.String r2 = org.telegram.messenger.UserObject.getUserName(r41)
            r3 = 0
            r1[r3] = r2
            java.lang.String r2 = "AreYouSureDeleteThisChatWithBot"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            r6.setText(r0)
            goto L_0x04df
        L_0x0470:
            r1 = 1
            r3 = 0
            r0 = 2131624358(0x7f0e01a6, float:1.8875893E38)
            java.lang.Object[] r1 = new java.lang.Object[r1]
            java.lang.String r2 = org.telegram.messenger.UserObject.getUserName(r41)
            r1[r3] = r2
            java.lang.String r2 = "AreYouSureDeleteThisChatWithUser"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            r6.setText(r0)
            goto L_0x04df
        L_0x048b:
            boolean r0 = org.telegram.messenger.ChatObject.isChannel(r40)
            if (r0 == 0) goto L_0x04c7
            boolean r0 = r8.megagroup
            if (r0 == 0) goto L_0x04ae
            r0 = 2131626101(0x7f0e0875, float:1.8879429E38)
            r1 = 1
            java.lang.Object[] r1 = new java.lang.Object[r1]
            java.lang.String r2 = r8.title
            r3 = 0
            r1[r3] = r2
            java.lang.String r2 = "MegaLeaveAlertWithName"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            r6.setText(r0)
            goto L_0x04df
        L_0x04ae:
            r1 = 1
            r3 = 0
            r0 = 2131624732(0x7f0e031c, float:1.8876652E38)
            java.lang.Object[] r1 = new java.lang.Object[r1]
            java.lang.String r2 = r8.title
            r1[r3] = r2
            java.lang.String r2 = "ChannelLeaveAlertWithName"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            r6.setText(r0)
            goto L_0x04df
        L_0x04c7:
            r1 = 1
            r3 = 0
            r0 = 2131624340(0x7f0e0194, float:1.8875857E38)
            java.lang.Object[] r1 = new java.lang.Object[r1]
            java.lang.String r2 = r8.title
            r1[r3] = r2
            java.lang.String r2 = "AreYouSureDeleteAndExitName"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            r6.setText(r0)
        L_0x04df:
            if (r39 == 0) goto L_0x04ed
            r0 = 2131625111(0x7f0e0497, float:1.887742E38)
            java.lang.String r1 = "DeleteAll"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
        L_0x04ea:
            r13 = r0
            goto L_0x0551
        L_0x04ed:
            if (r37 == 0) goto L_0x0501
            if (r26 == 0) goto L_0x04f9
            r0 = 2131624912(0x7f0e03d0, float:1.8877017E38)
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r7, r0)
            goto L_0x04ea
        L_0x04f9:
            r0 = 2131624911(0x7f0e03cf, float:1.8877015E38)
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r13, r0)
            goto L_0x04ea
        L_0x0501:
            if (r38 == 0) goto L_0x052b
            boolean r0 = org.telegram.messenger.ChatObject.isChannel(r40)
            if (r0 == 0) goto L_0x0521
            boolean r0 = r8.megagroup
            if (r0 == 0) goto L_0x0517
            r0 = 2131625147(0x7f0e04bb, float:1.8877494E38)
            java.lang.String r1 = "DeleteMega"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            goto L_0x04ea
        L_0x0517:
            r0 = 2131624716(0x7f0e030c, float:1.887662E38)
            java.lang.String r1 = "ChannelDelete"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            goto L_0x04ea
        L_0x0521:
            r0 = 2131625147(0x7f0e04bb, float:1.8877494E38)
            java.lang.String r1 = "DeleteMega"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            goto L_0x04ea
        L_0x052b:
            boolean r0 = org.telegram.messenger.ChatObject.isChannel(r40)
            if (r0 == 0) goto L_0x0547
            boolean r0 = r8.megagroup
            if (r0 == 0) goto L_0x053d
            r0 = 2131625953(0x7f0e07e1, float:1.8879129E38)
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r11, r0)
            goto L_0x04ea
        L_0x053d:
            r1 = r28
            r0 = 2131625951(0x7f0e07df, float:1.8879124E38)
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            goto L_0x04ea
        L_0x0547:
            r1 = r16
            r0 = 2131625134(0x7f0e04ae, float:1.8877467E38)
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            goto L_0x04ea
        L_0x0551:
            org.telegram.ui.Components.-$$Lambda$AlertsCreator$mZe7goBwfhpKMDcF_xi5W8fzpd8 r15 = new org.telegram.ui.Components.-$$Lambda$AlertsCreator$mZe7goBwfhpKMDcF_xi5W8fzpd8
            r0 = r15
            r1 = r26
            r2 = r39
            r3 = r42
            r4 = r41
            r5 = r36
            r6 = r37
            r7 = r38
            r8 = r40
            r9 = r43
            r10 = r44
            r11 = r14
            r0.<init>(r1, r2, r3, r4, r5, r6, r7, r8, r9, r10, r11)
            r0 = r23
            r0.setPositiveButton(r13, r15)
            r1 = 2131624654(0x7f0e02ce, float:1.8876494E38)
            java.lang.String r2 = "Cancel"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r0.setNegativeButton(r1, r12)
            org.telegram.ui.ActionBar.AlertDialog r0 = r0.create()
            r1 = r36
            r1.showDialog(r0)
            r1 = -1
            android.view.View r0 = r0.getButton(r1)
            android.widget.TextView r0 = (android.widget.TextView) r0
            if (r0 == 0) goto L_0x0598
            java.lang.String r1 = "dialogTextRed2"
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r1)
            r0.setTextColor(r1)
        L_0x0598:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.AlertsCreator.createClearOrDeleteDialogAlert(org.telegram.ui.ActionBar.BaseFragment, boolean, boolean, boolean, org.telegram.tgnet.TLRPC$Chat, org.telegram.tgnet.TLRPC$User, boolean, boolean, org.telegram.messenger.MessagesStorage$BooleanCallback):void");
    }

    static /* synthetic */ void lambda$createClearOrDeleteDialogAlert$16(boolean[] zArr, View view) {
        zArr[0] = !zArr[0];
        ((CheckBoxCell) view).setChecked(zArr[0], true);
    }

    static /* synthetic */ void lambda$createClearOrDeleteDialogAlert$18(boolean z, boolean z2, boolean z3, TLRPC$User tLRPC$User, BaseFragment baseFragment, boolean z4, boolean z5, TLRPC$Chat tLRPC$Chat, boolean z6, MessagesStorage.BooleanCallback booleanCallback, boolean[] zArr, DialogInterface dialogInterface, int i) {
        TLRPC$User tLRPC$User2 = tLRPC$User;
        MessagesStorage.BooleanCallback booleanCallback2 = booleanCallback;
        boolean z7 = false;
        if (!z && !z2 && !z3) {
            if (UserObject.isUserSelf(tLRPC$User)) {
                createClearOrDeleteDialogAlert(baseFragment, z4, z5, true, tLRPC$Chat, tLRPC$User, false, z6, booleanCallback);
                return;
            } else if (tLRPC$User2 != null && zArr[0]) {
                MessagesStorage.getInstance(baseFragment.getCurrentAccount()).getMessagesCount((long) tLRPC$User2.id, new MessagesStorage.IntCallback(z4, z5, tLRPC$Chat, tLRPC$User, z6, booleanCallback, zArr) {
                    public final /* synthetic */ boolean f$1;
                    public final /* synthetic */ boolean f$2;
                    public final /* synthetic */ TLRPC$Chat f$3;
                    public final /* synthetic */ TLRPC$User f$4;
                    public final /* synthetic */ boolean f$5;
                    public final /* synthetic */ MessagesStorage.BooleanCallback f$6;
                    public final /* synthetic */ boolean[] f$7;

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
                        AlertsCreator.lambda$createClearOrDeleteDialogAlert$17(BaseFragment.this, this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, i);
                    }
                });
                return;
            }
        }
        if (booleanCallback2 != null) {
            if (z2 || zArr[0]) {
                z7 = true;
            }
            booleanCallback2.run(z7);
        }
    }

    static /* synthetic */ void lambda$createClearOrDeleteDialogAlert$17(BaseFragment baseFragment, boolean z, boolean z2, TLRPC$Chat tLRPC$Chat, TLRPC$User tLRPC$User, boolean z3, MessagesStorage.BooleanCallback booleanCallback, boolean[] zArr, int i) {
        MessagesStorage.BooleanCallback booleanCallback2 = booleanCallback;
        if (i >= 50) {
            createClearOrDeleteDialogAlert(baseFragment, z, z2, true, tLRPC$Chat, tLRPC$User, false, z3, booleanCallback);
        } else if (booleanCallback2 != null) {
            booleanCallback.run(zArr[0]);
        }
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
            baseFragment2.showDialog(new AlertDialog.Builder((Context) parentActivity).setView(frameLayout).setPositiveButton(LocaleController.getString("Call", NUM), new DialogInterface.OnClickListener(tLRPC$User2, z2) {
                public final /* synthetic */ TLRPC$User f$1;
                public final /* synthetic */ boolean f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void onClick(DialogInterface dialogInterface, int i) {
                    AlertsCreator.lambda$createCallDialogAlert$19(BaseFragment.this, this.f$1, this.f$2, dialogInterface, i);
                }
            }).setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null).create());
        }
    }

    static /* synthetic */ void lambda$createCallDialogAlert$19(BaseFragment baseFragment, TLRPC$User tLRPC$User, boolean z, DialogInterface dialogInterface, int i) {
        TLRPC$UserFull userFull = baseFragment.getMessagesController().getUserFull(tLRPC$User.id);
        VoIPHelper.startCall(tLRPC$User, z, userFull != null && userFull.video_calls_available, baseFragment.getParentActivity(), userFull, baseFragment.getAccountInstance());
    }

    public static void createChangeBioAlert(String str, int i, Context context, int i2) {
        String str2;
        int i3;
        int i4 = i;
        final Context context2 = context;
        AlertDialog.Builder builder = new AlertDialog.Builder(context2);
        builder.setTitle(i4 > 0 ? LocaleController.getString("UserBio", NUM) : LocaleController.getString("DescriptionPlaceholder", NUM));
        if (i4 > 0) {
            i3 = NUM;
            str2 = "VoipGroupBioEditAlertText";
        } else {
            i3 = NUM;
            str2 = "DescriptionInfo";
        }
        builder.setMessage(LocaleController.getString(str2, i3));
        FrameLayout frameLayout = new FrameLayout(context2);
        frameLayout.setClipChildren(false);
        if (i4 < 0) {
            int i5 = -i4;
            if (MessagesController.getInstance(i2).getChatFull(i5) == null) {
                MessagesController.getInstance(i2).loadFullChat(i5, ConnectionsManager.generateClassGuid(), true);
            }
        }
        final NumberTextView numberTextView = new NumberTextView(context2);
        EditText editText = new EditText(context2);
        editText.setTextColor(Theme.getColor("voipgroup_actionBarItems"));
        editText.setHint(i4 > 0 ? LocaleController.getString("UserBio", NUM) : LocaleController.getString("DescriptionPlaceholder", NUM));
        editText.setTextSize(1, 16.0f);
        editText.setBackground(Theme.createEditTextDrawable(context2, true));
        editText.setMaxLines(4);
        editText.setRawInputType(147457);
        editText.setImeOptions(6);
        InputFilter[] inputFilterArr = new InputFilter[1];
        final int i6 = i4 > 0 ? 70 : 255;
        inputFilterArr[0] = new CodepointsLengthInputFilter(i6) {
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
                int codePointCount = i6 - Character.codePointCount(editable, 0, editable.length());
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
        $$Lambda$AlertsCreator$1xdHdSmms0hJHatoH9R4vMPRN30 r1 = new DialogInterface.OnClickListener(i4, i2, editText) {
            public final /* synthetic */ int f$0;
            public final /* synthetic */ int f$1;
            public final /* synthetic */ EditText f$2;

            {
                this.f$0 = r1;
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void onClick(DialogInterface dialogInterface, int i) {
                AlertsCreator.lambda$createChangeBioAlert$21(this.f$0, this.f$1, this.f$2, dialogInterface, i);
            }
        };
        builder.setPositiveButton(LocaleController.getString("Save", NUM), r1);
        builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
        builder.setOnPreDismissListener(new DialogInterface.OnDismissListener(editText) {
            public final /* synthetic */ EditText f$0;

            {
                this.f$0 = r1;
            }

            public final void onDismiss(DialogInterface dialogInterface) {
                AndroidUtilities.hideKeyboard(this.f$0);
            }
        });
        frameLayout.addView(editText, LayoutHelper.createFrame(-1, -2.0f, 0, 23.0f, 12.0f, 23.0f, 21.0f));
        editText.requestFocus();
        AndroidUtilities.showKeyboard(editText);
        AlertDialog create = builder.create();
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener(i4, create, r1) {
            public final /* synthetic */ int f$0;
            public final /* synthetic */ AlertDialog f$1;
            public final /* synthetic */ DialogInterface.OnClickListener f$2;

            {
                this.f$0 = r1;
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                return AlertsCreator.lambda$createChangeBioAlert$23(this.f$0, this.f$1, this.f$2, textView, i, keyEvent);
            }
        });
        create.setBackgroundColor(Theme.getColor("voipgroup_dialogBackground"));
        create.show();
        create.setTextColor(Theme.getColor("voipgroup_actionBarItems"));
    }

    static /* synthetic */ void lambda$createChangeBioAlert$21(int i, int i2, EditText editText, DialogInterface dialogInterface, int i3) {
        String str = "";
        if (i > 0) {
            TLRPC$UserFull userFull = MessagesController.getInstance(i2).getUserFull(UserConfig.getInstance(i2).getClientUserId());
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
                NotificationCenter.getInstance(i2).postNotificationName(NotificationCenter.userInfoDidLoad, Integer.valueOf(i), userFull);
            }
            TLRPC$TL_account_updateProfile tLRPC$TL_account_updateProfile = new TLRPC$TL_account_updateProfile();
            tLRPC$TL_account_updateProfile.about = trim;
            tLRPC$TL_account_updateProfile.flags |= 4;
            NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.showBulletin, 2, Integer.valueOf(i));
            ConnectionsManager.getInstance(i2).sendRequest(tLRPC$TL_account_updateProfile, $$Lambda$AlertsCreator$pWxJAj20y34fahDp1vZ_OeBSlEc.INSTANCE, 2);
        } else {
            int i4 = -i;
            TLRPC$ChatFull chatFull = MessagesController.getInstance(i2).getChatFull(i4);
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
                NotificationCenter instance = NotificationCenter.getInstance(i2);
                int i5 = NotificationCenter.chatInfoDidLoad;
                Boolean bool = Boolean.FALSE;
                instance.postNotificationName(i5, chatFull, 0, bool, bool);
            }
            NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.showBulletin, 2, Integer.valueOf(i));
            MessagesController.getInstance(i2).updateChatAbout(i4, obj, chatFull);
        }
        dialogInterface.dismiss();
    }

    static /* synthetic */ boolean lambda$createChangeBioAlert$23(int i, AlertDialog alertDialog, DialogInterface.OnClickListener onClickListener, TextView textView, int i2, KeyEvent keyEvent) {
        if ((i2 != 6 && (i <= 0 || keyEvent.getKeyCode() != 66)) || !alertDialog.isShowing()) {
            return false;
        }
        onClickListener.onClick(alertDialog, 0);
        return true;
    }

    public static void createChangeNameAlert(int i, Context context, int i2) {
        String str;
        String str2;
        String str3;
        int i3;
        EditText editText;
        int i4 = i;
        Context context2 = context;
        if (i4 > 0) {
            TLRPC$User user = MessagesController.getInstance(i2).getUser(Integer.valueOf(i));
            str = user.first_name;
            str2 = user.last_name;
        } else {
            str = MessagesController.getInstance(i2).getChat(Integer.valueOf(-i4)).title;
            str2 = null;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(context2);
        if (i4 > 0) {
            i3 = NUM;
            str3 = "VoipEditName";
        } else {
            i3 = NUM;
            str3 = "VoipEditTitle";
        }
        builder.setTitle(LocaleController.getString(str3, i3));
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
        editText2.setImeOptions(i4 > 0 ? 5 : 6);
        editText2.setHint(i4 > 0 ? LocaleController.getString("FirstName", NUM) : LocaleController.getString("VoipEditTitleHint", NUM));
        editText2.setBackground(Theme.createEditTextDrawable(context2, true));
        editText2.setPadding(0, AndroidUtilities.dp(8.0f), 0, AndroidUtilities.dp(8.0f));
        editText2.requestFocus();
        if (i4 > 0) {
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
        $$Lambda$AlertsCreator$xdWth50uG4qWexHLtESqn80tEA r1 = new DialogInterface.OnClickListener(editText2, i4, i2, editText) {
            public final /* synthetic */ EditText f$0;
            public final /* synthetic */ int f$1;
            public final /* synthetic */ int f$2;
            public final /* synthetic */ EditText f$3;

            {
                this.f$0 = r1;
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
            }

            public final void onClick(DialogInterface dialogInterface, int i) {
                AlertsCreator.lambda$createChangeNameAlert$25(this.f$0, this.f$1, this.f$2, this.f$3, dialogInterface, i);
            }
        };
        builder.setPositiveButton(LocaleController.getString("Save", NUM), r1);
        builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
        builder.setOnPreDismissListener(new DialogInterface.OnDismissListener(editText2, editText) {
            public final /* synthetic */ EditText f$0;
            public final /* synthetic */ EditText f$1;

            {
                this.f$0 = r1;
                this.f$1 = r2;
            }

            public final void onDismiss(DialogInterface dialogInterface) {
                AlertsCreator.lambda$createChangeNameAlert$26(this.f$0, this.f$1, dialogInterface);
            }
        });
        AlertDialog create = builder.create();
        create.setBackgroundColor(Theme.getColor("voipgroup_dialogBackground"));
        create.show();
        create.setTextColor(Theme.getColor("voipgroup_actionBarItems"));
        $$Lambda$AlertsCreator$7tBeomH2QlEhjYETlYiIQnelNpk r2 = new TextView.OnEditorActionListener(r1) {
            public final /* synthetic */ DialogInterface.OnClickListener f$1;

            {
                this.f$1 = r2;
            }

            public final boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                return AlertsCreator.lambda$createChangeNameAlert$27(AlertDialog.this, this.f$1, textView, i, keyEvent);
            }
        };
        if (editText != null) {
            editText.setOnEditorActionListener(r2);
        } else {
            editText2.setOnEditorActionListener(r2);
        }
    }

    static /* synthetic */ void lambda$createChangeNameAlert$25(EditText editText, int i, int i2, EditText editText2, DialogInterface dialogInterface, int i3) {
        if (editText.getText() != null) {
            if (i > 0) {
                TLRPC$User user = MessagesController.getInstance(i2).getUser(Integer.valueOf(i));
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
                    TLRPC$User user2 = MessagesController.getInstance(i2).getUser(Integer.valueOf(UserConfig.getInstance(i2).getClientUserId()));
                    if (user2 != null) {
                        user2.first_name = tLRPC$TL_account_updateProfile.first_name;
                        user2.last_name = tLRPC$TL_account_updateProfile.last_name;
                    }
                    UserConfig.getInstance(i2).saveConfig(true);
                    NotificationCenter.getInstance(i2).postNotificationName(NotificationCenter.mainUserInfoChanged, new Object[0]);
                    NotificationCenter.getInstance(i2).postNotificationName(NotificationCenter.updateInterfaces, 1);
                    ConnectionsManager.getInstance(i2).sendRequest(tLRPC$TL_account_updateProfile, $$Lambda$AlertsCreator$dGsOO18D0U0XA_2qRag6hbbyeJY.INSTANCE);
                    NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.showBulletin, 3, Integer.valueOf(i));
                } else {
                    dialogInterface.dismiss();
                    return;
                }
            } else {
                int i4 = -i;
                TLRPC$Chat chat = MessagesController.getInstance(i2).getChat(Integer.valueOf(i4));
                String obj3 = editText.getText().toString();
                String str3 = chat.title;
                if (str3 == null || !str3.equals(obj3)) {
                    chat.title = obj3;
                    NotificationCenter.getInstance(i2).postNotificationName(NotificationCenter.updateInterfaces, 16);
                    MessagesController.getInstance(i2).changeChatTitle(i4, obj3);
                    NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.showBulletin, 3, Integer.valueOf(i));
                } else {
                    dialogInterface.dismiss();
                    return;
                }
            }
            dialogInterface.dismiss();
        }
    }

    static /* synthetic */ void lambda$createChangeNameAlert$26(EditText editText, EditText editText2, DialogInterface dialogInterface) {
        AndroidUtilities.hideKeyboard(editText);
        AndroidUtilities.hideKeyboard(editText2);
    }

    static /* synthetic */ boolean lambda$createChangeNameAlert$27(AlertDialog alertDialog, DialogInterface.OnClickListener onClickListener, TextView textView, int i, KeyEvent keyEvent) {
        if ((i != 6 && keyEvent.getKeyCode() != 66) || !alertDialog.isShowing()) {
            return false;
        }
        onClickListener.onClick(alertDialog, 0);
        return true;
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
                        checkBoxCellArr[i4].setOnClickListener(new View.OnClickListener(zArr, i4) {
                            public final /* synthetic */ boolean[] f$0;
                            public final /* synthetic */ int f$1;

                            {
                                this.f$0 = r1;
                                this.f$1 = r2;
                            }

                            public final void onClick(View view) {
                                AlertsCreator.lambda$createBlockDialogAlert$28(this.f$0, this.f$1, view);
                            }
                        });
                    }
                    i4++;
                }
                builder.setPositiveButton(str, new DialogInterface.OnClickListener(zArr) {
                    public final /* synthetic */ boolean[] f$1;

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

    static /* synthetic */ void lambda$createBlockDialogAlert$28(boolean[] zArr, int i, View view) {
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
            public final /* synthetic */ boolean f$0;
            public final /* synthetic */ NumberPicker f$1;
            public final /* synthetic */ NumberPicker f$2;
            public final /* synthetic */ NumberPicker f$3;

            {
                this.f$0 = r1;
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
            }

            public final void onScrollStateChange(NumberPicker numberPicker, int i) {
                AlertsCreator.lambda$createDatePickerDialog$30(this.f$0, this.f$1, this.f$2, this.f$3, numberPicker, i);
            }
        });
        numberPicker.setMinValue(0);
        numberPicker.setMaxValue(11);
        linearLayout.addView(numberPicker, LayoutHelper.createLinear(0, -2, 0.3f));
        numberPicker.setFormatter($$Lambda$AlertsCreator$IgeB3sStBfcCphXjOJr1yRRevWg.INSTANCE);
        numberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener(numberPicker, numberPicker3) {
            public final /* synthetic */ NumberPicker f$1;
            public final /* synthetic */ NumberPicker f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void onValueChange(NumberPicker numberPicker, int i, int i2) {
                AlertsCreator.updateDayPicker(NumberPicker.this, this.f$1, this.f$2);
            }
        });
        numberPicker.setOnScrollListener(new NumberPicker.OnScrollListener(z2, numberPicker2, numberPicker, numberPicker3) {
            public final /* synthetic */ boolean f$0;
            public final /* synthetic */ NumberPicker f$1;
            public final /* synthetic */ NumberPicker f$2;
            public final /* synthetic */ NumberPicker f$3;

            {
                this.f$0 = r1;
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
            }

            public final void onScrollStateChange(NumberPicker numberPicker, int i) {
                AlertsCreator.lambda$createDatePickerDialog$33(this.f$0, this.f$1, this.f$2, this.f$3, numberPicker, i);
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
            public final /* synthetic */ NumberPicker f$1;
            public final /* synthetic */ NumberPicker f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void onValueChange(NumberPicker numberPicker, int i, int i2) {
                AlertsCreator.updateDayPicker(NumberPicker.this, this.f$1, this.f$2);
            }
        });
        numberPicker3.setOnScrollListener(new NumberPicker.OnScrollListener(z2, numberPicker2, numberPicker, numberPicker3) {
            public final /* synthetic */ boolean f$0;
            public final /* synthetic */ NumberPicker f$1;
            public final /* synthetic */ NumberPicker f$2;
            public final /* synthetic */ NumberPicker f$3;

            {
                this.f$0 = r1;
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
            }

            public final void onScrollStateChange(NumberPicker numberPicker, int i) {
                AlertsCreator.lambda$createDatePickerDialog$35(this.f$0, this.f$1, this.f$2, this.f$3, numberPicker, i);
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
            public final /* synthetic */ boolean f$0;
            public final /* synthetic */ NumberPicker f$1;
            public final /* synthetic */ NumberPicker f$2;
            public final /* synthetic */ NumberPicker f$3;
            public final /* synthetic */ AlertsCreator.DatePickerDelegate f$4;

            {
                this.f$0 = r1;
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
                this.f$4 = r5;
            }

            public final void onClick(DialogInterface dialogInterface, int i) {
                AlertsCreator.lambda$createDatePickerDialog$36(this.f$0, this.f$1, this.f$2, this.f$3, this.f$4, dialogInterface, i);
            }
        });
        builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
        return builder;
    }

    static /* synthetic */ void lambda$createDatePickerDialog$30(boolean z, NumberPicker numberPicker, NumberPicker numberPicker2, NumberPicker numberPicker3, NumberPicker numberPicker4, int i) {
        if (z && i == 0) {
            checkPickerDate(numberPicker, numberPicker2, numberPicker3);
        }
    }

    static /* synthetic */ String lambda$createDatePickerDialog$31(int i) {
        Calendar instance = Calendar.getInstance();
        instance.set(5, 1);
        instance.set(2, i);
        return instance.getDisplayName(2, 1, Locale.getDefault());
    }

    static /* synthetic */ void lambda$createDatePickerDialog$33(boolean z, NumberPicker numberPicker, NumberPicker numberPicker2, NumberPicker numberPicker3, NumberPicker numberPicker4, int i) {
        if (z && i == 0) {
            checkPickerDate(numberPicker, numberPicker2, numberPicker3);
        }
    }

    static /* synthetic */ void lambda$createDatePickerDialog$35(boolean z, NumberPicker numberPicker, NumberPicker numberPicker2, NumberPicker numberPicker3, NumberPicker numberPicker4, int i) {
        if (z && i == 0) {
            checkPickerDate(numberPicker, numberPicker2, numberPicker3);
        }
    }

    static /* synthetic */ void lambda$createDatePickerDialog$36(boolean z, NumberPicker numberPicker, NumberPicker numberPicker2, NumberPicker numberPicker3, DatePickerDelegate datePickerDelegate, DialogInterface dialogInterface, int i) {
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
            this(Theme.getColor("dialogTextBlack"), Theme.getColor("dialogBackground"), Theme.getColor("key_sheet_other"), Theme.getColor("player_actionBarSelector"), Theme.getColor("actionBarDefaultSubmenuItem"), Theme.getColor("actionBarDefaultSubmenuBackground"), Theme.getColor("listSelectorSDK21"));
        }

        public ScheduleDatePickerColors(int i, int i2, int i3, int i4, int i5, int i6, int i7) {
            this.buttonTextColor = Theme.getColor("featuredStickers_buttonText");
            this.buttonBackgroundColor = Theme.getColor("featuredStickers_addButton");
            this.buttonBackgroundPressedColor = Theme.getColor("featuredStickers_addButtonPressed");
            this.textColor = i;
            this.backgroundColor = i2;
            this.iconColor = i3;
            this.iconSelectorColor = i4;
            this.subMenuTextColor = i5;
            this.subMenuBackgroundColor = i6;
            this.subMenuSelectorColor = i7;
        }
    }

    public static BottomSheet.Builder createScheduleDatePickerDialog(Context context, long j, ScheduleDatePickerDelegate scheduleDatePickerDelegate) {
        return createScheduleDatePickerDialog(context, j, -1, scheduleDatePickerDelegate, (Runnable) null);
    }

    public static BottomSheet.Builder createScheduleDatePickerDialog(Context context, long j, ScheduleDatePickerDelegate scheduleDatePickerDelegate, ScheduleDatePickerColors scheduleDatePickerColors) {
        return createScheduleDatePickerDialog(context, j, -1, scheduleDatePickerDelegate, (Runnable) null, scheduleDatePickerColors);
    }

    public static BottomSheet.Builder createScheduleDatePickerDialog(Context context, long j, ScheduleDatePickerDelegate scheduleDatePickerDelegate, Runnable runnable) {
        return createScheduleDatePickerDialog(context, j, -1, scheduleDatePickerDelegate, runnable);
    }

    public static BottomSheet.Builder createScheduleDatePickerDialog(Context context, long j, long j2, ScheduleDatePickerDelegate scheduleDatePickerDelegate, Runnable runnable) {
        return createScheduleDatePickerDialog(context, j, j2, scheduleDatePickerDelegate, runnable, new ScheduleDatePickerColors());
    }

    public static BottomSheet.Builder createScheduleDatePickerDialog(Context context, long j, long j2, ScheduleDatePickerDelegate scheduleDatePickerDelegate, Runnable runnable, ScheduleDatePickerColors scheduleDatePickerColors) {
        AnonymousClass7 r22;
        TLRPC$User user;
        TLRPC$UserStatus tLRPC$UserStatus;
        boolean z;
        Context context2 = context;
        long j3 = j;
        ScheduleDatePickerColors scheduleDatePickerColors2 = scheduleDatePickerColors;
        if (context2 == null) {
            return null;
        }
        int clientUserId = UserConfig.getInstance(UserConfig.selectedAccount).getClientUserId();
        BottomSheet.Builder builder = new BottomSheet.Builder(context2, false);
        builder.setApplyBottomPadding(false);
        final NumberPicker numberPicker = new NumberPicker(context2);
        numberPicker.setTextColor(scheduleDatePickerColors2.textColor);
        numberPicker.setTextOffset(AndroidUtilities.dp(10.0f));
        numberPicker.setItemCount(5);
        final AnonymousClass6 r8 = new NumberPicker(context2) {
            /* access modifiers changed from: protected */
            public CharSequence getContentDescription(int i) {
                return LocaleController.formatPluralString("Hours", i);
            }
        };
        r8.setItemCount(5);
        r8.setTextColor(scheduleDatePickerColors2.textColor);
        r8.setTextOffset(-AndroidUtilities.dp(10.0f));
        final AnonymousClass7 r7 = new NumberPicker(context2) {
            /* access modifiers changed from: protected */
            public CharSequence getContentDescription(int i) {
                return LocaleController.formatPluralString("Minutes", i);
            }
        };
        r7.setItemCount(5);
        r7.setTextColor(scheduleDatePickerColors2.textColor);
        r7.setTextOffset(-AndroidUtilities.dp(34.0f));
        AnonymousClass8 r6 = new LinearLayout(context2) {
            boolean ignoreLayout = false;

            /* access modifiers changed from: protected */
            public void onMeasure(int i, int i2) {
                this.ignoreLayout = true;
                Point point = AndroidUtilities.displaySize;
                int i3 = point.x > point.y ? 3 : 5;
                numberPicker.setItemCount(i3);
                r8.setItemCount(i3);
                r7.setItemCount(i3);
                numberPicker.getLayoutParams().height = AndroidUtilities.dp(54.0f) * i3;
                r8.getLayoutParams().height = AndroidUtilities.dp(54.0f) * i3;
                r7.getLayoutParams().height = AndroidUtilities.dp(54.0f) * i3;
                this.ignoreLayout = false;
                super.onMeasure(i, i2);
            }

            public void requestLayout() {
                if (!this.ignoreLayout) {
                    super.requestLayout();
                }
            }
        };
        r6.setOrientation(1);
        FrameLayout frameLayout = new FrameLayout(context2);
        r6.addView(frameLayout, LayoutHelper.createLinear(-1, -2, 51, 22, 0, 0, 4));
        TextView textView = new TextView(context2);
        NumberPicker numberPicker2 = numberPicker;
        long j4 = (long) clientUserId;
        if (j3 == j4) {
            textView.setText(LocaleController.getString("SetReminder", NUM));
        } else {
            textView.setText(LocaleController.getString("ScheduleMessage", NUM));
        }
        textView.setTextColor(scheduleDatePickerColors2.textColor);
        textView.setTextSize(1, 20.0f);
        textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        frameLayout.addView(textView, LayoutHelper.createFrame(-2, -2.0f, 51, 0.0f, 12.0f, 0.0f, 0.0f));
        textView.setOnTouchListener($$Lambda$AlertsCreator$a7umIyuRXFOi4az0gJfiyXq6wDA.INSTANCE);
        int i = (int) j3;
        if (i <= 0 || j3 == j4 || (user = MessagesController.getInstance(UserConfig.selectedAccount).getUser(Integer.valueOf(i))) == null || user.bot || (tLRPC$UserStatus = user.status) == null || tLRPC$UserStatus.expires <= 0) {
            r22 = r7;
            ScheduleDatePickerDelegate scheduleDatePickerDelegate2 = scheduleDatePickerDelegate;
        } else {
            String firstName = UserObject.getFirstName(user);
            if (firstName.length() > 10) {
                StringBuilder sb = new StringBuilder();
                z = false;
                sb.append(firstName.substring(0, 10));
                sb.append("…");
                firstName = sb.toString();
            } else {
                z = false;
            }
            r22 = r7;
            ActionBarMenuItem actionBarMenuItem = new ActionBarMenuItem(context2, (ActionBarMenu) null, z ? 1 : 0, scheduleDatePickerColors2.iconColor);
            actionBarMenuItem.setLongClickEnabled(z);
            actionBarMenuItem.setSubMenuOpenSide(2);
            actionBarMenuItem.setIcon(NUM);
            actionBarMenuItem.setBackgroundDrawable(Theme.createSelectorDrawable(scheduleDatePickerColors2.iconSelectorColor, 1));
            frameLayout.addView(actionBarMenuItem, LayoutHelper.createFrame(40, 40.0f, 53, 0.0f, 8.0f, 5.0f, 0.0f));
            actionBarMenuItem.addSubItem(1, LocaleController.formatString("ScheduleWhenOnline", NUM, firstName));
            actionBarMenuItem.setOnClickListener(new View.OnClickListener(scheduleDatePickerColors2) {
                public final /* synthetic */ AlertsCreator.ScheduleDatePickerColors f$1;

                {
                    this.f$1 = r2;
                }

                public final void onClick(View view) {
                    AlertsCreator.lambda$createScheduleDatePickerDialog$38(ActionBarMenuItem.this, this.f$1, view);
                }
            });
            actionBarMenuItem.setDelegate(new ActionBarMenuItem.ActionBarMenuItemDelegate(builder) {
                public final /* synthetic */ BottomSheet.Builder f$1;

                {
                    this.f$1 = r2;
                }

                public final void onItemClick(int i) {
                    AlertsCreator.lambda$createScheduleDatePickerDialog$39(AlertsCreator.ScheduleDatePickerDelegate.this, this.f$1, i);
                }
            });
            actionBarMenuItem.setContentDescription(LocaleController.getString("AccDescrMoreOptions", NUM));
        }
        LinearLayout linearLayout = new LinearLayout(context2);
        linearLayout.setOrientation(0);
        linearLayout.setWeightSum(1.0f);
        r6.addView(linearLayout, LayoutHelper.createLinear(-1, -2));
        long currentTimeMillis = System.currentTimeMillis();
        Calendar instance = Calendar.getInstance();
        instance.setTimeInMillis(currentTimeMillis);
        int i2 = instance.get(1);
        BottomSheet.Builder builder2 = builder;
        AnonymousClass9 r13 = new TextView(context2) {
            public CharSequence getAccessibilityClassName() {
                return Button.class.getName();
            }
        };
        NumberPicker numberPicker3 = numberPicker2;
        linearLayout.addView(numberPicker3, LayoutHelper.createLinear(0, 270, 0.5f));
        numberPicker3.setMinValue(0);
        numberPicker3.setMaxValue(365);
        numberPicker3.setWrapSelectorWheel(false);
        numberPicker3.setFormatter(new NumberPicker.Formatter(currentTimeMillis, instance, i2) {
            public final /* synthetic */ long f$0;
            public final /* synthetic */ Calendar f$1;
            public final /* synthetic */ int f$2;

            {
                this.f$0 = r1;
                this.f$1 = r3;
                this.f$2 = r4;
            }

            public final String format(int i) {
                return AlertsCreator.lambda$createScheduleDatePickerDialog$40(this.f$0, this.f$1, this.f$2, i);
            }
        });
        AnonymousClass9 r2 = r13;
        int i3 = clientUserId;
        int i4 = clientUserId;
        AnonymousClass9 r18 = r13;
        LinearLayout linearLayout2 = linearLayout;
        Calendar calendar = instance;
        $$Lambda$AlertsCreator$NKoW30iH99no_WipWAMAjlbIv8 r9 = r0;
        AnonymousClass6 r72 = r8;
        AnonymousClass8 r24 = r6;
        AnonymousClass6 r10 = r8;
        AnonymousClass7 r82 = r22;
        $$Lambda$AlertsCreator$NKoW30iH99no_WipWAMAjlbIv8 r0 = new NumberPicker.OnValueChangeListener(r6, r2, i3, j, numberPicker3, r72, r82) {
            public final /* synthetic */ LinearLayout f$0;
            public final /* synthetic */ TextView f$1;
            public final /* synthetic */ int f$2;
            public final /* synthetic */ long f$3;
            public final /* synthetic */ NumberPicker f$4;
            public final /* synthetic */ NumberPicker f$5;
            public final /* synthetic */ NumberPicker f$6;

            {
                this.f$0 = r1;
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
                this.f$4 = r6;
                this.f$5 = r7;
                this.f$6 = r8;
            }

            public final void onValueChange(NumberPicker numberPicker, int i, int i2) {
                AlertsCreator.lambda$createScheduleDatePickerDialog$41(this.f$0, this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, numberPicker, i, i2);
            }
        };
        numberPicker3.setOnValueChangedListener(r9);
        r10.setMinValue(0);
        r10.setMaxValue(23);
        linearLayout2.addView(r10, LayoutHelper.createLinear(0, 270, 0.2f));
        r10.setFormatter($$Lambda$AlertsCreator$RO4yA0H6S9Z94y4u0iBUc6zitQY.INSTANCE);
        r10.setOnValueChangedListener(r9);
        r82.setMinValue(0);
        r82.setMaxValue(59);
        r82.setValue(0);
        r82.setFormatter($$Lambda$AlertsCreator$hHyUgfEs4Ydp9SHkcErcgW5IN4.INSTANCE);
        linearLayout2.addView(r82, LayoutHelper.createLinear(0, 270, 0.3f));
        r82.setOnValueChangedListener(r9);
        if (j2 > 0 && j2 != NUM) {
            long j5 = 1000 * j2;
            calendar.setTimeInMillis(System.currentTimeMillis());
            calendar.set(12, 0);
            calendar.set(13, 0);
            calendar.set(14, 0);
            calendar.set(11, 0);
            int timeInMillis = (int) ((j5 - calendar.getTimeInMillis()) / 86400000);
            calendar.setTimeInMillis(j5);
            if (timeInMillis >= 0) {
                r82.setValue(calendar.get(12));
                r10.setValue(calendar.get(11));
                numberPicker3.setValue(timeInMillis);
            }
        }
        boolean[] zArr = {true};
        checkScheduleDate(r18, (TextView) null, j4 == j ? 1 : 0, numberPicker3, r10, r82);
        AnonymousClass9 r14 = r18;
        r14.setPadding(AndroidUtilities.dp(34.0f), 0, AndroidUtilities.dp(34.0f), 0);
        r14.setGravity(17);
        ScheduleDatePickerColors scheduleDatePickerColors3 = scheduleDatePickerColors;
        r14.setTextColor(scheduleDatePickerColors3.buttonTextColor);
        r14.setTextSize(1, 14.0f);
        r14.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        r14.setBackgroundDrawable(Theme.createSimpleSelectorRoundRectDrawable(AndroidUtilities.dp(4.0f), scheduleDatePickerColors3.buttonBackgroundColor, scheduleDatePickerColors3.buttonBackgroundPressedColor));
        AnonymousClass8 r92 = r24;
        r92.addView(r14, LayoutHelper.createLinear(-1, 48, 83, 16, 15, 16, 16));
        $$Lambda$AlertsCreator$v34Vynxd7M1rij02vcndxLJClQ r11 = r0;
        $$Lambda$AlertsCreator$v34Vynxd7M1rij02vcndxLJClQ r02 = new View.OnClickListener(zArr, i4, j, numberPicker3, r10, r82, calendar, scheduleDatePickerDelegate, builder2) {
            public final /* synthetic */ boolean[] f$0;
            public final /* synthetic */ int f$1;
            public final /* synthetic */ long f$2;
            public final /* synthetic */ NumberPicker f$3;
            public final /* synthetic */ NumberPicker f$4;
            public final /* synthetic */ NumberPicker f$5;
            public final /* synthetic */ Calendar f$6;
            public final /* synthetic */ AlertsCreator.ScheduleDatePickerDelegate f$7;
            public final /* synthetic */ BottomSheet.Builder f$8;

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
                AlertsCreator.lambda$createScheduleDatePickerDialog$44(this.f$0, this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, this.f$8, view);
            }
        };
        r14.setOnClickListener(r11);
        BottomSheet.Builder builder3 = builder2;
        builder3.setCustomView(r92);
        BottomSheet show = builder3.show();
        show.setOnDismissListener(new DialogInterface.OnDismissListener(runnable, zArr) {
            public final /* synthetic */ Runnable f$0;
            public final /* synthetic */ boolean[] f$1;

            {
                this.f$0 = r1;
                this.f$1 = r2;
            }

            public final void onDismiss(DialogInterface dialogInterface) {
                AlertsCreator.lambda$createScheduleDatePickerDialog$45(this.f$0, this.f$1, dialogInterface);
            }
        });
        show.setBackgroundColor(scheduleDatePickerColors3.backgroundColor);
        return builder3;
    }

    static /* synthetic */ void lambda$createScheduleDatePickerDialog$38(ActionBarMenuItem actionBarMenuItem, ScheduleDatePickerColors scheduleDatePickerColors, View view) {
        actionBarMenuItem.toggleSubMenu();
        actionBarMenuItem.setPopupItemsColor(scheduleDatePickerColors.subMenuTextColor, false);
        actionBarMenuItem.setupPopupRadialSelectors(scheduleDatePickerColors.subMenuSelectorColor);
        actionBarMenuItem.redrawPopup(scheduleDatePickerColors.subMenuBackgroundColor);
    }

    static /* synthetic */ void lambda$createScheduleDatePickerDialog$39(ScheduleDatePickerDelegate scheduleDatePickerDelegate, BottomSheet.Builder builder, int i) {
        if (i == 1) {
            scheduleDatePickerDelegate.didSelectDate(true, NUM);
            builder.getDismissRunnable().run();
        }
    }

    static /* synthetic */ String lambda$createScheduleDatePickerDialog$40(long j, Calendar calendar, int i, int i2) {
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

    static /* synthetic */ void lambda$createScheduleDatePickerDialog$41(LinearLayout linearLayout, TextView textView, int i, long j, NumberPicker numberPicker, NumberPicker numberPicker2, NumberPicker numberPicker3, NumberPicker numberPicker4, int i2, int i3) {
        LinearLayout linearLayout2 = linearLayout;
        try {
            linearLayout.performHapticFeedback(3, 2);
        } catch (Exception unused) {
        }
        checkScheduleDate(textView, (TextView) null, ((long) i) == j ? 1 : 0, numberPicker, numberPicker2, numberPicker3);
    }

    static /* synthetic */ void lambda$createScheduleDatePickerDialog$44(boolean[] zArr, int i, long j, NumberPicker numberPicker, NumberPicker numberPicker2, NumberPicker numberPicker3, Calendar calendar, ScheduleDatePickerDelegate scheduleDatePickerDelegate, BottomSheet.Builder builder, View view) {
        Calendar calendar2 = calendar;
        zArr[0] = false;
        boolean checkScheduleDate = checkScheduleDate((TextView) null, (TextView) null, ((long) i) == j ? 1 : 0, numberPicker, numberPicker2, numberPicker3);
        calendar2.setTimeInMillis(System.currentTimeMillis() + (((long) numberPicker.getValue()) * 24 * 3600 * 1000));
        calendar2.set(11, numberPicker2.getValue());
        calendar2.set(12, numberPicker3.getValue());
        if (checkScheduleDate) {
            calendar2.set(13, 0);
        }
        scheduleDatePickerDelegate.didSelectDate(true, (int) (calendar.getTimeInMillis() / 1000));
        builder.getDismissRunnable().run();
    }

    static /* synthetic */ void lambda$createScheduleDatePickerDialog$45(Runnable runnable, boolean[] zArr, DialogInterface dialogInterface) {
        if (runnable != null && zArr[0]) {
            runnable.run();
        }
    }

    public static BottomSheet.Builder createDatePickerDialog(Context context, long j, ScheduleDatePickerDelegate scheduleDatePickerDelegate) {
        AnonymousClass12 r22;
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
        final AnonymousClass10 r11 = new NumberPicker(context2) {
            /* access modifiers changed from: protected */
            public CharSequence getContentDescription(int i) {
                return LocaleController.formatPluralString("Hours", i);
            }
        };
        r11.setItemCount(5);
        r11.setTextColor(scheduleDatePickerColors.textColor);
        r11.setTextOffset(-AndroidUtilities.dp(10.0f));
        final AnonymousClass11 r12 = new NumberPicker(context2) {
            /* access modifiers changed from: protected */
            public CharSequence getContentDescription(int i) {
                return LocaleController.formatPluralString("Minutes", i);
            }
        };
        r12.setItemCount(5);
        r12.setTextColor(scheduleDatePickerColors.textColor);
        r12.setTextOffset(-AndroidUtilities.dp(34.0f));
        AnonymousClass12 r14 = new LinearLayout(context2) {
            boolean ignoreLayout = false;

            /* access modifiers changed from: protected */
            public void onMeasure(int i, int i2) {
                this.ignoreLayout = true;
                Point point = AndroidUtilities.displaySize;
                int i3 = point.x > point.y ? 3 : 5;
                numberPicker.setItemCount(i3);
                r11.setItemCount(i3);
                r12.setItemCount(i3);
                numberPicker.getLayoutParams().height = AndroidUtilities.dp(54.0f) * i3;
                r11.getLayoutParams().height = AndroidUtilities.dp(54.0f) * i3;
                r12.getLayoutParams().height = AndroidUtilities.dp(54.0f) * i3;
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
        textView.setOnTouchListener($$Lambda$AlertsCreator$dDDbRP_oDsoidCPPQJMxxQmpPs.INSTANCE);
        LinearLayout linearLayout = new LinearLayout(context2);
        linearLayout.setOrientation(0);
        linearLayout.setWeightSum(1.0f);
        r14.addView(linearLayout, LayoutHelper.createLinear(-1, -2));
        long currentTimeMillis = System.currentTimeMillis();
        Calendar instance = Calendar.getInstance();
        instance.setTimeInMillis(currentTimeMillis);
        int i = instance.get(1);
        AnonymousClass13 r8 = new TextView(context2) {
            public CharSequence getAccessibilityClassName() {
                return Button.class.getName();
            }
        };
        linearLayout.addView(numberPicker, LayoutHelper.createLinear(0, 270, 0.5f));
        numberPicker.setMinValue(0);
        numberPicker.setMaxValue(365);
        numberPicker.setWrapSelectorWheel(false);
        numberPicker.setFormatter(new NumberPicker.Formatter(currentTimeMillis, instance, i) {
            public final /* synthetic */ long f$0;
            public final /* synthetic */ Calendar f$1;
            public final /* synthetic */ int f$2;

            {
                this.f$0 = r1;
                this.f$1 = r3;
                this.f$2 = r4;
            }

            public final String format(int i) {
                return AlertsCreator.lambda$createDatePickerDialog$47(this.f$0, this.f$1, this.f$2, i);
            }
        });
        $$Lambda$AlertsCreator$iLOnDZE6c2eRRIU52h8si5SlzEU r0 = new NumberPicker.OnValueChangeListener(r14, numberPicker, r11, r12) {
            public final /* synthetic */ LinearLayout f$0;
            public final /* synthetic */ NumberPicker f$1;
            public final /* synthetic */ NumberPicker f$2;
            public final /* synthetic */ NumberPicker f$3;

            {
                this.f$0 = r1;
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
            }

            public final void onValueChange(NumberPicker numberPicker, int i, int i2) {
                AlertsCreator.lambda$createDatePickerDialog$48(this.f$0, this.f$1, this.f$2, this.f$3, numberPicker, i, i2);
            }
        };
        numberPicker.setOnValueChangedListener(r0);
        r11.setMinValue(0);
        r11.setMaxValue(23);
        linearLayout.addView(r11, LayoutHelper.createLinear(0, 270, 0.2f));
        r11.setFormatter($$Lambda$AlertsCreator$7mNB07Lp7BicI6JTNs1lBNbECrs.INSTANCE);
        r11.setOnValueChangedListener(r0);
        r12.setMinValue(0);
        r12.setMaxValue(59);
        r12.setValue(0);
        r12.setFormatter($$Lambda$AlertsCreator$w718LIHaSw3jcrb_2_g9jMna8w.INSTANCE);
        linearLayout.addView(r12, LayoutHelper.createLinear(0, 270, 0.3f));
        r12.setOnValueChangedListener(r0);
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
        AnonymousClass13 r02 = r8;
        checkScheduleDate((TextView) null, (TextView) null, 0, numberPicker, r11, r12);
        r02.setPadding(AndroidUtilities.dp(34.0f), 0, AndroidUtilities.dp(34.0f), 0);
        r02.setGravity(17);
        r02.setTextColor(scheduleDatePickerColors.buttonTextColor);
        r02.setTextSize(1, 14.0f);
        r02.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        r02.setBackgroundDrawable(Theme.createSimpleSelectorRoundRectDrawable(AndroidUtilities.dp(4.0f), scheduleDatePickerColors.buttonBackgroundColor, scheduleDatePickerColors.buttonBackgroundPressedColor));
        r02.setText(LocaleController.getString("SetTimeLimit", NUM));
        AnonymousClass12 r142 = r22;
        r142.addView(r02, LayoutHelper.createLinear(-1, 48, 83, 16, 15, 16, 16));
        r02.setOnClickListener(new View.OnClickListener(r11, r12, instance, scheduleDatePickerDelegate, builder) {
            public final /* synthetic */ NumberPicker f$1;
            public final /* synthetic */ NumberPicker f$2;
            public final /* synthetic */ Calendar f$3;
            public final /* synthetic */ AlertsCreator.ScheduleDatePickerDelegate f$4;
            public final /* synthetic */ BottomSheet.Builder f$5;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
                this.f$4 = r5;
                this.f$5 = r6;
            }

            public final void onClick(View view) {
                AlertsCreator.lambda$createDatePickerDialog$51(NumberPicker.this, this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, view);
            }
        });
        builder.setCustomView(r142);
        builder.show().setBackgroundColor(scheduleDatePickerColors.backgroundColor);
        return builder;
    }

    static /* synthetic */ String lambda$createDatePickerDialog$47(long j, Calendar calendar, int i, int i2) {
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

    static /* synthetic */ void lambda$createDatePickerDialog$48(LinearLayout linearLayout, NumberPicker numberPicker, NumberPicker numberPicker2, NumberPicker numberPicker3, NumberPicker numberPicker4, int i, int i2) {
        try {
            linearLayout.performHapticFeedback(3, 2);
        } catch (Exception unused) {
        }
        checkScheduleDate((TextView) null, (TextView) null, 0, numberPicker, numberPicker2, numberPicker3);
    }

    static /* synthetic */ void lambda$createDatePickerDialog$51(NumberPicker numberPicker, NumberPicker numberPicker2, NumberPicker numberPicker3, Calendar calendar, ScheduleDatePickerDelegate scheduleDatePickerDelegate, BottomSheet.Builder builder, View view) {
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

    public static BottomSheet.Builder createCalendarPickerDialog(Context context, long j, MessagesStorage.IntCallback intCallback) {
        Context context2 = context;
        long j2 = j;
        if (context2 == null) {
            return null;
        }
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
        numberPicker3.setTextOffset(-AndroidUtilities.dp(24.0f));
        AnonymousClass14 r14 = new LinearLayout(context2) {
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
        r14.setOrientation(1);
        FrameLayout frameLayout = new FrameLayout(context2);
        r14.addView(frameLayout, LayoutHelper.createLinear(-1, -2, 51, 22, 0, 0, 4));
        TextView textView = new TextView(context2);
        textView.setText(LocaleController.getString("ChooseDate", NUM));
        textView.setTextColor(Theme.getColor("dialogTextBlack"));
        textView.setTextSize(1, 20.0f);
        textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        frameLayout.addView(textView, LayoutHelper.createFrame(-2, -2.0f, 51, 0.0f, 12.0f, 0.0f, 0.0f));
        textView.setOnTouchListener($$Lambda$AlertsCreator$aVh26INf4DeDGDCukQ6AhGjqm6Q.INSTANCE);
        LinearLayout linearLayout = new LinearLayout(context2);
        linearLayout.setOrientation(0);
        linearLayout.setWeightSum(1.0f);
        r14.addView(linearLayout, LayoutHelper.createLinear(-1, -2));
        System.currentTimeMillis();
        AnonymousClass15 r5 = new TextView(context2) {
            public CharSequence getAccessibilityClassName() {
                return Button.class.getName();
            }
        };
        linearLayout.addView(numberPicker, LayoutHelper.createLinear(0, 270, 0.25f));
        numberPicker.setMinValue(1);
        numberPicker.setMaxValue(31);
        numberPicker.setWrapSelectorWheel(false);
        numberPicker.setFormatter($$Lambda$AlertsCreator$OqlIsnap0hWunmStYhflv1Mktg4.INSTANCE);
        $$Lambda$AlertsCreator$aEWuciq64llESx3mUnfBjUwM08 r15 = r0;
        $$Lambda$AlertsCreator$aEWuciq64llESx3mUnfBjUwM08 r0 = new NumberPicker.OnValueChangeListener(r14, j, numberPicker, numberPicker2, numberPicker3) {
            public final /* synthetic */ LinearLayout f$0;
            public final /* synthetic */ long f$1;
            public final /* synthetic */ NumberPicker f$2;
            public final /* synthetic */ NumberPicker f$3;
            public final /* synthetic */ NumberPicker f$4;

            {
                this.f$0 = r1;
                this.f$1 = r2;
                this.f$2 = r4;
                this.f$3 = r5;
                this.f$4 = r6;
            }

            public final void onValueChange(NumberPicker numberPicker, int i, int i2) {
                AlertsCreator.lambda$createCalendarPickerDialog$54(this.f$0, this.f$1, this.f$2, this.f$3, this.f$4, numberPicker, i, i2);
            }
        };
        numberPicker.setOnValueChangedListener(r15);
        numberPicker2.setMinValue(0);
        numberPicker2.setMaxValue(11);
        numberPicker2.setWrapSelectorWheel(false);
        LinearLayout linearLayout2 = linearLayout;
        linearLayout2.addView(numberPicker2, LayoutHelper.createLinear(0, 270, 0.5f));
        numberPicker2.setFormatter($$Lambda$AlertsCreator$S81dY02RfImQlKEHmIVz29NOjn4.INSTANCE);
        numberPicker2.setOnValueChangedListener(r15);
        Calendar instance = Calendar.getInstance();
        instance.setTimeInMillis(j2);
        int i = instance.get(1);
        instance.setTimeInMillis(System.currentTimeMillis());
        int i2 = instance.get(1);
        numberPicker3.setMinValue(i);
        numberPicker3.setMaxValue(i2);
        numberPicker3.setWrapSelectorWheel(false);
        numberPicker3.setFormatter($$Lambda$AlertsCreator$QbCDQ8Bedo56uLdqX9_gJHuKU2A.INSTANCE);
        linearLayout2.addView(numberPicker3, LayoutHelper.createLinear(0, 270, 0.25f));
        numberPicker3.setOnValueChangedListener(r15);
        numberPicker.setValue(31);
        numberPicker2.setValue(12);
        numberPicker3.setValue(i2);
        checkCalendarDate(j2, numberPicker, numberPicker2, numberPicker3);
        AnonymousClass15 r152 = r5;
        r152.setPadding(AndroidUtilities.dp(34.0f), 0, AndroidUtilities.dp(34.0f), 0);
        r152.setGravity(17);
        r152.setTextColor(Theme.getColor("featuredStickers_buttonText"));
        r152.setTextSize(1, 14.0f);
        r152.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        r152.setText(LocaleController.getString("JumpToDate", NUM));
        r152.setBackgroundDrawable(Theme.createSimpleSelectorRoundRectDrawable(AndroidUtilities.dp(4.0f), Theme.getColor("featuredStickers_addButton"), Theme.getColor("featuredStickers_addButtonPressed")));
        r14.addView(r152, LayoutHelper.createLinear(-1, 48, 83, 16, 15, 16, 16));
        r152.setOnClickListener(new View.OnClickListener(j, numberPicker, numberPicker2, numberPicker3, instance, intCallback, builder) {
            public final /* synthetic */ long f$0;
            public final /* synthetic */ NumberPicker f$1;
            public final /* synthetic */ NumberPicker f$2;
            public final /* synthetic */ NumberPicker f$3;
            public final /* synthetic */ Calendar f$4;
            public final /* synthetic */ MessagesStorage.IntCallback f$5;
            public final /* synthetic */ BottomSheet.Builder f$6;

            {
                this.f$0 = r1;
                this.f$1 = r3;
                this.f$2 = r4;
                this.f$3 = r5;
                this.f$4 = r6;
                this.f$5 = r7;
                this.f$6 = r8;
            }

            public final void onClick(View view) {
                AlertsCreator.lambda$createCalendarPickerDialog$57(this.f$0, this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, view);
            }
        });
        builder.setCustomView(r14);
        return builder;
    }

    static /* synthetic */ String lambda$createCalendarPickerDialog$53(int i) {
        return "" + i;
    }

    static /* synthetic */ void lambda$createCalendarPickerDialog$54(LinearLayout linearLayout, long j, NumberPicker numberPicker, NumberPicker numberPicker2, NumberPicker numberPicker3, NumberPicker numberPicker4, int i, int i2) {
        try {
            linearLayout.performHapticFeedback(3, 2);
        } catch (Exception unused) {
        }
        checkCalendarDate(j, numberPicker, numberPicker2, numberPicker3);
    }

    static /* synthetic */ String lambda$createCalendarPickerDialog$55(int i) {
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

    static /* synthetic */ void lambda$createCalendarPickerDialog$57(long j, NumberPicker numberPicker, NumberPicker numberPicker2, NumberPicker numberPicker3, Calendar calendar, MessagesStorage.IntCallback intCallback, BottomSheet.Builder builder, View view) {
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

    public static BottomSheet createMuteAlert(BaseFragment baseFragment, long j) {
        if (baseFragment == null || baseFragment.getParentActivity() == null) {
            return null;
        }
        BottomSheet.Builder builder = new BottomSheet.Builder(baseFragment.getParentActivity());
        builder.setTitle(LocaleController.getString("Notifications", NUM), true);
        builder.setItems(new CharSequence[]{LocaleController.formatString("MuteFor", NUM, LocaleController.formatPluralString("Hours", 1)), LocaleController.formatString("MuteFor", NUM, LocaleController.formatPluralString("Hours", 8)), LocaleController.formatString("MuteFor", NUM, LocaleController.formatPluralString("Days", 2)), LocaleController.getString("MuteDisable", NUM)}, new DialogInterface.OnClickListener(j, baseFragment) {
            public final /* synthetic */ long f$0;
            public final /* synthetic */ BaseFragment f$1;

            {
                this.f$0 = r1;
                this.f$1 = r3;
            }

            public final void onClick(DialogInterface dialogInterface, int i) {
                AlertsCreator.lambda$createMuteAlert$58(this.f$0, this.f$1, dialogInterface, i);
            }
        });
        return builder.create();
    }

    static /* synthetic */ void lambda$createMuteAlert$58(long j, BaseFragment baseFragment, DialogInterface dialogInterface, int i) {
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
            BulletinFactory.createMuteBulletin(baseFragment, i2).show();
        }
    }

    public static void sendReport(TLRPC$InputPeer tLRPC$InputPeer, int i, String str, ArrayList<Integer> arrayList) {
        TLRPC$TL_messages_report tLRPC$TL_messages_report = new TLRPC$TL_messages_report();
        tLRPC$TL_messages_report.peer = tLRPC$InputPeer;
        tLRPC$TL_messages_report.id.addAll(arrayList);
        tLRPC$TL_messages_report.message = str;
        if (i == 0) {
            tLRPC$TL_messages_report.reason = new TLRPC$TL_inputReportReasonSpam();
        } else if (i == 1) {
            tLRPC$TL_messages_report.reason = new TLRPC$TL_inputReportReasonFake();
        } else if (i == 2) {
            tLRPC$TL_messages_report.reason = new TLRPC$TL_inputReportReasonViolence();
        } else if (i == 3) {
            tLRPC$TL_messages_report.reason = new TLRPC$TL_inputReportReasonChildAbuse();
        } else if (i == 4) {
            tLRPC$TL_messages_report.reason = new TLRPC$TL_inputReportReasonPornography();
        } else if (i == 5) {
            tLRPC$TL_messages_report.reason = new TLRPC$TL_inputReportReasonOther();
        }
        ConnectionsManager.getInstance(UserConfig.selectedAccount).sendRequest(tLRPC$TL_messages_report, $$Lambda$AlertsCreator$UeXI4iH8lRU_HyubpBXuz4lYjlg.INSTANCE);
    }

    public static void createReportAlert(Context context, long j, int i, BaseFragment baseFragment) {
        CharSequence[] charSequenceArr;
        int[] iArr;
        Context context2 = context;
        BaseFragment baseFragment2 = baseFragment;
        if (context2 != null && baseFragment2 != null) {
            BottomSheet.Builder builder = new BottomSheet.Builder(context2);
            builder.setTitle(LocaleController.getString("ReportChat", NUM), true);
            if (i != 0) {
                charSequenceArr = new CharSequence[]{LocaleController.getString("ReportChatSpam", NUM), LocaleController.getString("ReportChatViolence", NUM), LocaleController.getString("ReportChatChild", NUM), LocaleController.getString("ReportChatPornography", NUM), LocaleController.getString("ReportChatOther", NUM)};
                iArr = new int[]{NUM, NUM, NUM, NUM, NUM};
            } else {
                charSequenceArr = new CharSequence[]{LocaleController.getString("ReportChatSpam", NUM), LocaleController.getString("ReportChatFakeAccount", NUM), LocaleController.getString("ReportChatViolence", NUM), LocaleController.getString("ReportChatChild", NUM), LocaleController.getString("ReportChatPornography", NUM), LocaleController.getString("ReportChatOther", NUM)};
                iArr = new int[]{NUM, NUM, NUM, NUM, NUM, NUM};
            }
            builder.setItems(charSequenceArr, iArr, new DialogInterface.OnClickListener(i, baseFragment, context, j) {
                public final /* synthetic */ int f$0;
                public final /* synthetic */ BaseFragment f$1;
                public final /* synthetic */ Context f$2;
                public final /* synthetic */ long f$3;

                {
                    this.f$0 = r1;
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                }

                public final void onClick(DialogInterface dialogInterface, int i) {
                    AlertsCreator.lambda$createReportAlert$61(this.f$0, this.f$1, this.f$2, this.f$3, dialogInterface, i);
                }
            });
            baseFragment2.showDialog(builder.create());
        }
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v2, resolved type: org.telegram.tgnet.TLRPC$TL_account_reportPeer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v4, resolved type: org.telegram.tgnet.TLRPC$TL_account_reportPeer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v5, resolved type: org.telegram.tgnet.TLRPC$TL_messages_report} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v6, resolved type: org.telegram.tgnet.TLRPC$TL_account_reportPeer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v7, resolved type: org.telegram.tgnet.TLRPC$TL_account_reportPeer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v8, resolved type: org.telegram.tgnet.TLRPC$TL_account_reportPeer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v9, resolved type: org.telegram.tgnet.TLRPC$TL_account_reportPeer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v10, resolved type: org.telegram.tgnet.TLRPC$TL_account_reportPeer} */
    /* JADX WARNING: Code restructure failed: missing block: B:34:0x0089, code lost:
        if (r0 != 3) goto L_0x00ce;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:35:0x008b, code lost:
        r3.reason = new org.telegram.tgnet.TLRPC$TL_inputReportReasonPornography();
        r3 = r3;
     */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    static /* synthetic */ void lambda$createReportAlert$61(int r10, org.telegram.ui.ActionBar.BaseFragment r11, android.content.Context r12, long r13, android.content.DialogInterface r15, int r16) {
        /*
            r7 = r11
            r0 = r16
            r1 = 3
            r2 = 2
            r3 = 4
            if (r10 != 0) goto L_0x001b
            if (r0 == 0) goto L_0x0010
            if (r0 == r2) goto L_0x0010
            if (r0 == r1) goto L_0x0010
            if (r0 != r3) goto L_0x001b
        L_0x0010:
            boolean r4 = r7 instanceof org.telegram.ui.ChatActivity
            if (r4 == 0) goto L_0x001b
            r1 = r7
            org.telegram.ui.ChatActivity r1 = (org.telegram.ui.ChatActivity) r1
            r1.openReportChat(r0)
            return
        L_0x001b:
            r4 = 5
            r5 = 1
            if (r10 != 0) goto L_0x0023
            if (r0 == r4) goto L_0x0027
            if (r0 == r5) goto L_0x0027
        L_0x0023:
            if (r10 == 0) goto L_0x0049
            if (r0 != r3) goto L_0x0049
        L_0x0027:
            boolean r1 = r7 instanceof org.telegram.ui.ChatActivity
            if (r1 == 0) goto L_0x0036
            android.app.Activity r1 = r11.getParentActivity()
            int r2 = r11.getClassGuid()
            org.telegram.messenger.AndroidUtilities.requestAdjustNothing(r1, r2)
        L_0x0036:
            org.telegram.ui.Components.AlertsCreator$16 r8 = new org.telegram.ui.Components.AlertsCreator$16
            if (r0 != r3) goto L_0x003c
            r2 = 5
            goto L_0x003d
        L_0x003c:
            r2 = r0
        L_0x003d:
            r0 = r8
            r1 = r12
            r3 = r11
            r4 = r10
            r5 = r13
            r0.<init>(r1, r2, r3, r4, r5)
            r11.showDialog(r8)
            return
        L_0x0049:
            int r4 = org.telegram.messenger.UserConfig.selectedAccount
            org.telegram.messenger.MessagesController r4 = org.telegram.messenger.MessagesController.getInstance(r4)
            r8 = r13
            int r6 = (int) r8
            org.telegram.tgnet.TLRPC$InputPeer r4 = r4.getInputPeer((int) r6)
            java.lang.String r6 = ""
            if (r10 == 0) goto L_0x0093
            org.telegram.tgnet.TLRPC$TL_messages_report r3 = new org.telegram.tgnet.TLRPC$TL_messages_report
            r3.<init>()
            r3.peer = r4
            java.util.ArrayList<java.lang.Integer> r4 = r3.id
            java.lang.Integer r8 = java.lang.Integer.valueOf(r10)
            r4.add(r8)
            r3.message = r6
            if (r0 != 0) goto L_0x0075
            org.telegram.tgnet.TLRPC$TL_inputReportReasonSpam r0 = new org.telegram.tgnet.TLRPC$TL_inputReportReasonSpam
            r0.<init>()
            r3.reason = r0
            goto L_0x00ce
        L_0x0075:
            if (r0 != r5) goto L_0x007f
            org.telegram.tgnet.TLRPC$TL_inputReportReasonViolence r0 = new org.telegram.tgnet.TLRPC$TL_inputReportReasonViolence
            r0.<init>()
            r3.reason = r0
            goto L_0x00ce
        L_0x007f:
            if (r0 != r2) goto L_0x0089
            org.telegram.tgnet.TLRPC$TL_inputReportReasonChildAbuse r0 = new org.telegram.tgnet.TLRPC$TL_inputReportReasonChildAbuse
            r0.<init>()
            r3.reason = r0
            goto L_0x00ce
        L_0x0089:
            if (r0 != r1) goto L_0x00ce
            org.telegram.tgnet.TLRPC$TL_inputReportReasonPornography r0 = new org.telegram.tgnet.TLRPC$TL_inputReportReasonPornography
            r0.<init>()
            r3.reason = r0
            goto L_0x00ce
        L_0x0093:
            org.telegram.tgnet.TLRPC$TL_account_reportPeer r8 = new org.telegram.tgnet.TLRPC$TL_account_reportPeer
            r8.<init>()
            r8.peer = r4
            r8.message = r6
            if (r0 != 0) goto L_0x00a6
            org.telegram.tgnet.TLRPC$TL_inputReportReasonSpam r0 = new org.telegram.tgnet.TLRPC$TL_inputReportReasonSpam
            r0.<init>()
            r8.reason = r0
            goto L_0x00cd
        L_0x00a6:
            if (r0 != r5) goto L_0x00b0
            org.telegram.tgnet.TLRPC$TL_inputReportReasonFake r0 = new org.telegram.tgnet.TLRPC$TL_inputReportReasonFake
            r0.<init>()
            r8.reason = r0
            goto L_0x00cd
        L_0x00b0:
            if (r0 != r2) goto L_0x00ba
            org.telegram.tgnet.TLRPC$TL_inputReportReasonViolence r0 = new org.telegram.tgnet.TLRPC$TL_inputReportReasonViolence
            r0.<init>()
            r8.reason = r0
            goto L_0x00cd
        L_0x00ba:
            if (r0 != r1) goto L_0x00c4
            org.telegram.tgnet.TLRPC$TL_inputReportReasonChildAbuse r0 = new org.telegram.tgnet.TLRPC$TL_inputReportReasonChildAbuse
            r0.<init>()
            r8.reason = r0
            goto L_0x00cd
        L_0x00c4:
            if (r0 != r3) goto L_0x00cd
            org.telegram.tgnet.TLRPC$TL_inputReportReasonPornography r0 = new org.telegram.tgnet.TLRPC$TL_inputReportReasonPornography
            r0.<init>()
            r8.reason = r0
        L_0x00cd:
            r3 = r8
        L_0x00ce:
            int r0 = org.telegram.messenger.UserConfig.selectedAccount
            org.telegram.tgnet.ConnectionsManager r0 = org.telegram.tgnet.ConnectionsManager.getInstance(r0)
            org.telegram.ui.Components.-$$Lambda$AlertsCreator$8Jcc3-Rzdbo5fNQAiho0Cfncx08 r1 = org.telegram.ui.Components.$$Lambda$AlertsCreator$8Jcc3Rzdbo5fNQAiho0Cfncx08.INSTANCE
            r0.sendRequest(r3, r1)
            boolean r0 = r7 instanceof org.telegram.ui.ChatActivity
            if (r0 == 0) goto L_0x00ed
            r0 = r7
            org.telegram.ui.ChatActivity r0 = (org.telegram.ui.ChatActivity) r0
            org.telegram.ui.Components.UndoView r0 = r0.getUndoView()
            r1 = 0
            r3 = 74
            r4 = 0
            r0.showWithAction((long) r1, (int) r3, (java.lang.Runnable) r4)
            goto L_0x00f8
        L_0x00ed:
            org.telegram.ui.Components.BulletinFactory r0 = org.telegram.ui.Components.BulletinFactory.of((org.telegram.ui.ActionBar.BaseFragment) r11)
            org.telegram.ui.Components.Bulletin r0 = r0.createReportSent()
            r0.show()
        L_0x00f8:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.AlertsCreator.lambda$createReportAlert$61(int, org.telegram.ui.ActionBar.BaseFragment, android.content.Context, long, android.content.DialogInterface, int):void");
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
                    builder.setNegativeButton(LocaleController.getString("MoreInfo", NUM), new DialogInterface.OnClickListener() {
                        public final void onClick(DialogInterface dialogInterface, int i) {
                            MessagesController.getInstance(BaseFragment.this.getCurrentAccount()).openByUserName("spambot", BaseFragment.this, 1);
                        }
                    });
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
                public final /* synthetic */ LinearLayout f$0;
                public final /* synthetic */ int[] f$1;

                {
                    this.f$0 = r1;
                    this.f$1 = r2;
                }

                public final void onClick(View view) {
                    AlertsCreator.lambda$createColorSelectDialog$63(this.f$0, this.f$1, view);
                }
            });
            i4++;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder((Context) activity2);
        builder.setTitle(LocaleController.getString("LedColor", NUM));
        builder.setView(linearLayout);
        builder.setPositiveButton(LocaleController.getString("Set", NUM), new DialogInterface.OnClickListener(j, iArr, i, runnable) {
            public final /* synthetic */ long f$0;
            public final /* synthetic */ int[] f$1;
            public final /* synthetic */ int f$2;
            public final /* synthetic */ Runnable f$3;

            {
                this.f$0 = r1;
                this.f$1 = r3;
                this.f$2 = r4;
                this.f$3 = r5;
            }

            public final void onClick(DialogInterface dialogInterface, int i) {
                AlertsCreator.lambda$createColorSelectDialog$64(this.f$0, this.f$1, this.f$2, this.f$3, dialogInterface, i);
            }
        });
        builder.setNeutralButton(LocaleController.getString("LedDisabled", NUM), new DialogInterface.OnClickListener(j2, i3, runnable2) {
            public final /* synthetic */ long f$0;
            public final /* synthetic */ int f$1;
            public final /* synthetic */ Runnable f$2;

            {
                this.f$0 = r1;
                this.f$1 = r3;
                this.f$2 = r4;
            }

            public final void onClick(DialogInterface dialogInterface, int i) {
                AlertsCreator.lambda$createColorSelectDialog$65(this.f$0, this.f$1, this.f$2, dialogInterface, i);
            }
        });
        if (j2 != 0) {
            builder.setNegativeButton(LocaleController.getString("Default", NUM), new DialogInterface.OnClickListener(j2, runnable2) {
                public final /* synthetic */ long f$0;
                public final /* synthetic */ Runnable f$1;

                {
                    this.f$0 = r1;
                    this.f$1 = r3;
                }

                public final void onClick(DialogInterface dialogInterface, int i) {
                    AlertsCreator.lambda$createColorSelectDialog$66(this.f$0, this.f$1, dialogInterface, i);
                }
            });
        }
        return builder.create();
    }

    static /* synthetic */ void lambda$createColorSelectDialog$63(LinearLayout linearLayout, int[] iArr, View view) {
        int childCount = linearLayout.getChildCount();
        for (int i = 0; i < childCount; i++) {
            RadioColorCell radioColorCell = (RadioColorCell) linearLayout.getChildAt(i);
            radioColorCell.setChecked(radioColorCell == view, true);
        }
        iArr[0] = TextColorCell.colorsToSave[((Integer) view.getTag()).intValue()];
    }

    static /* synthetic */ void lambda$createColorSelectDialog$64(long j, int[] iArr, int i, Runnable runnable, DialogInterface dialogInterface, int i2) {
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

    static /* synthetic */ void lambda$createColorSelectDialog$65(long j, int i, Runnable runnable, DialogInterface dialogInterface, int i2) {
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

    static /* synthetic */ void lambda$createColorSelectDialog$66(long j, Runnable runnable, DialogInterface dialogInterface, int i) {
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
            $$Lambda$AlertsCreator$3FFy5jRKZ5UTu0A3N6B7UQ5q2Kw r9 = r1;
            $$Lambda$AlertsCreator$3FFy5jRKZ5UTu0A3N6B7UQ5q2Kw r1 = new View.OnClickListener(iArr, j, str, builder, runnable) {
                public final /* synthetic */ int[] f$0;
                public final /* synthetic */ long f$1;
                public final /* synthetic */ String f$2;
                public final /* synthetic */ AlertDialog.Builder f$3;
                public final /* synthetic */ Runnable f$4;

                {
                    this.f$0 = r1;
                    this.f$1 = r2;
                    this.f$2 = r4;
                    this.f$3 = r5;
                    this.f$4 = r6;
                }

                public final void onClick(View view) {
                    AlertsCreator.lambda$createVibrationSelectDialog$67(this.f$0, this.f$1, this.f$2, this.f$3, this.f$4, view);
                }
            };
            radioColorCell.setOnClickListener(r9);
            i2++;
            i = 0;
        }
        builder.setTitle(LocaleController.getString("Vibrate", NUM));
        builder.setView(linearLayout);
        builder.setPositiveButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
        return builder.create();
    }

    static /* synthetic */ void lambda$createVibrationSelectDialog$67(int[] iArr, long j, String str, AlertDialog.Builder builder, Runnable runnable, View view) {
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
                public final /* synthetic */ int[] f$0;
                public final /* synthetic */ LinearLayout f$1;

                {
                    this.f$0 = r1;
                    this.f$1 = r2;
                }

                public final void onClick(View view) {
                    AlertsCreator.lambda$createLocationUpdateDialog$68(this.f$0, this.f$1, view);
                }
            });
            i2++;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder((Context) activity2);
        builder.setTopImage((Drawable) new ShareLocationDrawable(activity2, 0), Theme.getColor("dialogTopBackground"));
        builder.setView(linearLayout);
        builder.setPositiveButton(LocaleController.getString("ShareFile", NUM), new DialogInterface.OnClickListener(iArr, intCallback) {
            public final /* synthetic */ int[] f$0;
            public final /* synthetic */ MessagesStorage.IntCallback f$1;

            {
                this.f$0 = r1;
                this.f$1 = r2;
            }

            public final void onClick(DialogInterface dialogInterface, int i) {
                AlertsCreator.lambda$createLocationUpdateDialog$69(this.f$0, this.f$1, dialogInterface, i);
            }
        });
        builder.setNeutralButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
        return builder.create();
    }

    static /* synthetic */ void lambda$createLocationUpdateDialog$68(int[] iArr, LinearLayout linearLayout, View view) {
        iArr[0] = ((Integer) view.getTag()).intValue();
        int childCount = linearLayout.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childAt = linearLayout.getChildAt(i);
            if (childAt instanceof RadioColorCell) {
                ((RadioColorCell) childAt).setChecked(childAt == view, true);
            }
        }
    }

    static /* synthetic */ void lambda$createLocationUpdateDialog$69(int[] iArr, MessagesStorage.IntCallback intCallback, DialogInterface dialogInterface, int i) {
        int i2;
        if (iArr[0] == 0) {
            i2 = 900;
        } else {
            i2 = iArr[0] == 1 ? 3600 : 28800;
        }
        intCallback.run(i2);
    }

    public static AlertDialog.Builder createBackgroundLocationPermissionDialog(Activity activity, TLRPC$User tLRPC$User, Runnable runnable) {
        if (activity == null || Build.VERSION.SDK_INT < 29) {
            return null;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder((Context) activity);
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
        builder.setMessage(AndroidUtilities.replaceTags(LocaleController.getString("PermissionBackgroundLocation", NUM)));
        builder.setPositiveButton(LocaleController.getString("OK", NUM), new DialogInterface.OnClickListener(activity) {
            public final /* synthetic */ Activity f$0;

            {
                this.f$0 = r1;
            }

            public final void onClick(DialogInterface dialogInterface, int i) {
                AlertsCreator.lambda$createBackgroundLocationPermissionDialog$70(this.f$0, dialogInterface, i);
            }
        });
        builder.setNegativeButton(LocaleController.getString("Cancel", NUM), new DialogInterface.OnClickListener(runnable) {
            public final /* synthetic */ Runnable f$0;

            {
                this.f$0 = r1;
            }

            public final void onClick(DialogInterface dialogInterface, int i) {
                this.f$0.run();
            }
        });
        return builder;
    }

    static /* synthetic */ void lambda$createBackgroundLocationPermissionDialog$70(Activity activity, DialogInterface dialogInterface, int i) {
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
        builder.setPositiveButton(LocaleController.getString("Enable", NUM), new DialogInterface.OnClickListener(activity) {
            public final /* synthetic */ Activity f$0;

            {
                this.f$0 = r1;
            }

            public final void onClick(DialogInterface dialogInterface, int i) {
                AlertsCreator.lambda$createDrawOverlayPermissionDialog$72(this.f$0, dialogInterface, i);
            }
        });
        builder.notDrawBackgroundOnTopView(true);
        builder.setNegativeButton(LocaleController.getString("Cancel", NUM), onClickListener);
        builder.setTopViewAspectRatio(0.50427353f);
        return builder;
    }

    static /* synthetic */ void lambda$createDrawOverlayPermissionDialog$72(Activity activity, DialogInterface dialogInterface, int i) {
        if (activity != null && Build.VERSION.SDK_INT >= 23) {
            activity.startActivity(new Intent("android.settings.action.MANAGE_OVERLAY_PERMISSION", Uri.parse("package:" + activity.getPackageName())));
        }
    }

    public static AlertDialog.Builder createDrawOverlayGroupCallPermissionDialog(Context context) {
        Context context2 = context;
        AlertDialog.Builder builder = new AlertDialog.Builder(context2);
        String readRes = RLottieDrawable.readRes((File) null, NUM);
        final GroupCallPipButton groupCallPipButton = new GroupCallPipButton(context2, 0, true);
        groupCallPipButton.setImportantForAccessibility(2);
        AnonymousClass20 r8 = new FrameLayout(context2) {
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
        builder.setPositiveButton(LocaleController.getString("Enable", NUM), new DialogInterface.OnClickListener(context2) {
            public final /* synthetic */ Context f$0;

            {
                this.f$0 = r1;
            }

            public final void onClick(DialogInterface dialogInterface, int i) {
                AlertsCreator.lambda$createDrawOverlayGroupCallPermissionDialog$73(this.f$0, dialogInterface, i);
            }
        });
        builder.notDrawBackgroundOnTopView(true);
        builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
        builder.setTopViewAspectRatio(0.5769231f);
        return builder;
    }

    static /* synthetic */ void lambda$createDrawOverlayGroupCallPermissionDialog$73(Context context, DialogInterface dialogInterface, int i) {
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
                public final /* synthetic */ int[] f$0;
                public final /* synthetic */ LinearLayout f$1;

                {
                    this.f$0 = r1;
                    this.f$1 = r2;
                }

                public final void onClick(View view) {
                    AlertsCreator.lambda$createFreeSpaceDialog$76(this.f$0, this.f$1, view);
                }
            });
            i3++;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder((Context) launchActivity2);
        builder.setTitle(LocaleController.getString("LowDiskSpaceTitle", NUM));
        builder.setMessage(LocaleController.getString("LowDiskSpaceMessage", NUM));
        builder.setView(linearLayout);
        builder.setPositiveButton(LocaleController.getString("OK", NUM), new DialogInterface.OnClickListener(iArr) {
            public final /* synthetic */ int[] f$0;

            {
                this.f$0 = r1;
            }

            public final void onClick(DialogInterface dialogInterface, int i) {
                SharedConfig.setKeepMedia(this.f$0[0]);
            }
        });
        builder.setNeutralButton(LocaleController.getString("ClearMediaCache", NUM), new DialogInterface.OnClickListener() {
            public final void onClick(DialogInterface dialogInterface, int i) {
                LaunchActivity.this.lambda$runLinkRequest$42(new CacheControlActivity());
            }
        });
        return builder.create();
    }

    static /* synthetic */ void lambda$createFreeSpaceDialog$76(int[] iArr, LinearLayout linearLayout, View view) {
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
            $$Lambda$AlertsCreator$1sQtoBT5WWk1F1pTPzNvS2lq_Rg r0 = r1;
            AlertDialog.Builder builder2 = builder;
            $$Lambda$AlertsCreator$1sQtoBT5WWk1F1pTPzNvS2lq_Rg r1 = new View.OnClickListener(iArr, j, i, notificationsSettings, builder2, runnable) {
                public final /* synthetic */ int[] f$0;
                public final /* synthetic */ long f$1;
                public final /* synthetic */ int f$2;
                public final /* synthetic */ SharedPreferences f$3;
                public final /* synthetic */ AlertDialog.Builder f$4;
                public final /* synthetic */ Runnable f$5;

                {
                    this.f$0 = r1;
                    this.f$1 = r2;
                    this.f$2 = r4;
                    this.f$3 = r5;
                    this.f$4 = r6;
                    this.f$5 = r7;
                }

                public final void onClick(View view) {
                    AlertsCreator.lambda$createPrioritySelectDialog$79(this.f$0, this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, view);
                }
            };
            radioColorCell.setOnClickListener(r0);
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

    static /* synthetic */ void lambda$createPrioritySelectDialog$79(int[] iArr, long j, int i, SharedPreferences sharedPreferences, AlertDialog.Builder builder, Runnable runnable, View view) {
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
            radioColorCell.setOnClickListener(new View.OnClickListener(iArr, i, builder, runnable) {
                public final /* synthetic */ int[] f$0;
                public final /* synthetic */ int f$1;
                public final /* synthetic */ AlertDialog.Builder f$2;
                public final /* synthetic */ Runnable f$3;

                {
                    this.f$0 = r1;
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                }

                public final void onClick(View view) {
                    AlertsCreator.lambda$createPopupSelectDialog$80(this.f$0, this.f$1, this.f$2, this.f$3, view);
                }
            });
            i2++;
        }
        builder.setTitle(LocaleController.getString("PopupNotification", NUM));
        builder.setView(linearLayout);
        builder.setPositiveButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
        return builder.create();
    }

    static /* synthetic */ void lambda$createPopupSelectDialog$80(int[] iArr, int i, AlertDialog.Builder builder, Runnable runnable, View view) {
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
                public final /* synthetic */ DialogInterface.OnClickListener f$1;

                {
                    this.f$1 = r2;
                }

                public final void onClick(View view) {
                    AlertsCreator.lambda$createSingleChoiceDialog$81(AlertDialog.Builder.this, this.f$1, view);
                }
            });
            i2++;
        }
        builder.setTitle(str);
        builder.setView(linearLayout);
        builder.setPositiveButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
        return builder.create();
    }

    static /* synthetic */ void lambda$createSingleChoiceDialog$81(AlertDialog.Builder builder, DialogInterface.OnClickListener onClickListener, View view) {
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
        numberPicker.setFormatter($$Lambda$AlertsCreator$sxI5Hw4WUOc3NAlnQ456D_GKndw.INSTANCE);
        builder.setView(numberPicker);
        builder.setNegativeButton(LocaleController.getString("Done", NUM), new DialogInterface.OnClickListener(numberPicker) {
            public final /* synthetic */ NumberPicker f$1;

            {
                this.f$1 = r2;
            }

            public final void onClick(DialogInterface dialogInterface, int i) {
                AlertsCreator.lambda$createTTLAlert$83(TLRPC$EncryptedChat.this, this.f$1, dialogInterface, i);
            }
        });
        return builder;
    }

    static /* synthetic */ String lambda$createTTLAlert$82(int i) {
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

    static /* synthetic */ void lambda$createTTLAlert$83(TLRPC$EncryptedChat tLRPC$EncryptedChat, NumberPicker numberPicker, DialogInterface dialogInterface, int i) {
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
                accountSelectCell.setOnClickListener(new View.OnClickListener(alertDialogArr, dismissRunnable, accountSelectDelegate) {
                    public final /* synthetic */ AlertDialog[] f$0;
                    public final /* synthetic */ Runnable f$1;
                    public final /* synthetic */ AlertsCreator.AccountSelectDelegate f$2;

                    {
                        this.f$0 = r1;
                        this.f$1 = r2;
                        this.f$2 = r3;
                    }

                    public final void onClick(View view) {
                        AlertsCreator.lambda$createAccountSelectDialog$84(this.f$0, this.f$1, this.f$2, view);
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

    static /* synthetic */ void lambda$createAccountSelectDialog$84(AlertDialog[] alertDialogArr, Runnable runnable, AccountSelectDelegate accountSelectDelegate, View view) {
        if (alertDialogArr[0] != null) {
            alertDialogArr[0].setOnDismissListener((DialogInterface.OnDismissListener) null);
        }
        runnable.run();
        accountSelectDelegate.didSelectAccount(((AccountSelectCell) view).getAccountNumber());
    }

    /* JADX WARNING: Removed duplicated region for block: B:157:0x02d5  */
    /* JADX WARNING: Removed duplicated region for block: B:158:0x02da  */
    /* JADX WARNING: Removed duplicated region for block: B:161:0x02e2  */
    /* JADX WARNING: Removed duplicated region for block: B:162:0x02e7  */
    /* JADX WARNING: Removed duplicated region for block: B:278:0x0558  */
    /* JADX WARNING: Removed duplicated region for block: B:279:0x0565  */
    /* JADX WARNING: Removed duplicated region for block: B:306:0x0622  */
    /* JADX WARNING: Removed duplicated region for block: B:307:0x062a  */
    /* JADX WARNING: Removed duplicated region for block: B:310:0x0650  */
    /* JADX WARNING: Removed duplicated region for block: B:342:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static void createDeleteMessagesAlert(org.telegram.ui.ActionBar.BaseFragment r41, org.telegram.tgnet.TLRPC$User r42, org.telegram.tgnet.TLRPC$Chat r43, org.telegram.tgnet.TLRPC$EncryptedChat r44, org.telegram.tgnet.TLRPC$ChatFull r45, long r46, org.telegram.messenger.MessageObject r48, android.util.SparseArray<org.telegram.messenger.MessageObject>[] r49, org.telegram.messenger.MessageObject.GroupedMessages r50, boolean r51, int r52, java.lang.Runnable r53) {
        /*
            r14 = r41
            r3 = r42
            r4 = r43
            r5 = r44
            r9 = r48
            r11 = r50
            r0 = r52
            if (r14 == 0) goto L_0x0659
            if (r3 != 0) goto L_0x0018
            if (r4 != 0) goto L_0x0018
            if (r5 != 0) goto L_0x0018
            goto L_0x0659
        L_0x0018:
            android.app.Activity r1 = r41.getParentActivity()
            if (r1 != 0) goto L_0x001f
            return
        L_0x001f:
            int r15 = r41.getCurrentAccount()
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
            r8 = r49[r7]
            int r8 = r8.size()
            r10 = r49[r6]
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
            org.telegram.tgnet.ConnectionsManager r10 = org.telegram.tgnet.ConnectionsManager.getInstance(r15)
            int r10 = r10.getCurrentTime()
            r12 = 86400(0x15180, float:1.21072E-40)
            r13 = 2
            if (r9 == 0) goto L_0x007e
            boolean r16 = r48.isDice()
            if (r16 == 0) goto L_0x007c
            org.telegram.tgnet.TLRPC$Message r7 = r9.messageOwner
            int r7 = r7.date
            int r7 = r10 - r7
            int r7 = java.lang.Math.abs(r7)
            if (r7 <= r12) goto L_0x007a
            goto L_0x007c
        L_0x007a:
            r7 = 0
            goto L_0x00b4
        L_0x007c:
            r7 = 1
            goto L_0x00b4
        L_0x007e:
            r7 = 0
            r16 = 0
        L_0x0081:
            if (r7 >= r13) goto L_0x00b2
            r13 = 0
        L_0x0084:
            r17 = r49[r7]
            int r6 = r17.size()
            if (r13 >= r6) goto L_0x00ad
            r6 = r49[r7]
            java.lang.Object r6 = r6.valueAt(r13)
            org.telegram.messenger.MessageObject r6 = (org.telegram.messenger.MessageObject) r6
            boolean r17 = r6.isDice()
            if (r17 == 0) goto L_0x00ab
            org.telegram.tgnet.TLRPC$Message r6 = r6.messageOwner
            int r6 = r6.date
            int r6 = r10 - r6
            int r6 = java.lang.Math.abs(r6)
            if (r6 <= r12) goto L_0x00a7
            goto L_0x00ab
        L_0x00a7:
            int r13 = r13 + 1
            r6 = 1
            goto L_0x0084
        L_0x00ab:
            r16 = 1
        L_0x00ad:
            int r7 = r7 + 1
            r6 = 1
            r13 = 2
            goto L_0x0081
        L_0x00b2:
            r7 = r16
        L_0x00b4:
            r6 = 3
            boolean[] r12 = new boolean[r6]
            r13 = 1
            boolean[] r6 = new boolean[r13]
            if (r3 == 0) goto L_0x00c6
            org.telegram.messenger.MessagesController r13 = org.telegram.messenger.MessagesController.getInstance(r15)
            boolean r13 = r13.canRevokePmInbox
            if (r13 == 0) goto L_0x00c6
            r13 = 1
            goto L_0x00c7
        L_0x00c6:
            r13 = 0
        L_0x00c7:
            if (r3 == 0) goto L_0x00d0
            org.telegram.messenger.MessagesController r11 = org.telegram.messenger.MessagesController.getInstance(r15)
            int r11 = r11.revokeTimePmLimit
            goto L_0x00d6
        L_0x00d0:
            org.telegram.messenger.MessagesController r11 = org.telegram.messenger.MessagesController.getInstance(r15)
            int r11 = r11.revokeTimeLimit
        L_0x00d6:
            if (r5 != 0) goto L_0x00e6
            if (r3 == 0) goto L_0x00e6
            if (r13 == 0) goto L_0x00e6
            r30 = r8
            r8 = 2147483647(0x7fffffff, float:NaN)
            if (r11 != r8) goto L_0x00e8
            r31 = 1
            goto L_0x00ea
        L_0x00e6:
            r30 = r8
        L_0x00e8:
            r31 = 0
        L_0x00ea:
            java.lang.String r8 = "DeleteMessagesOption"
            r19 = r13
            r22 = 1098907648(0x41800000, float:16.0)
            r23 = 1090519040(0x41000000, float:8.0)
            java.lang.String r13 = ""
            if (r4 == 0) goto L_0x03a1
            boolean r3 = r4.megagroup
            if (r3 == 0) goto L_0x03a1
            if (r51 != 0) goto L_0x03a1
            boolean r3 = org.telegram.messenger.ChatObject.canBlockUsers(r43)
            if (r9 == 0) goto L_0x0160
            org.telegram.tgnet.TLRPC$Message r5 = r9.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r5 = r5.action
            r25 = r6
            if (r5 == 0) goto L_0x011d
            boolean r6 = r5 instanceof org.telegram.tgnet.TLRPC$TL_messageActionEmpty
            if (r6 != 0) goto L_0x011d
            boolean r6 = r5 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatDeleteUser
            if (r6 != 0) goto L_0x011d
            boolean r6 = r5 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatJoinedByLink
            if (r6 != 0) goto L_0x011d
            boolean r5 = r5 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatAddUser
            if (r5 == 0) goto L_0x011b
            goto L_0x011d
        L_0x011b:
            r5 = 0
            goto L_0x012f
        L_0x011d:
            org.telegram.messenger.MessagesController r5 = org.telegram.messenger.MessagesController.getInstance(r15)
            org.telegram.tgnet.TLRPC$Message r6 = r9.messageOwner
            org.telegram.tgnet.TLRPC$Peer r6 = r6.from_id
            int r6 = r6.user_id
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            org.telegram.tgnet.TLRPC$User r5 = r5.getUser(r6)
        L_0x012f:
            boolean r6 = r48.isSendError()
            if (r6 != 0) goto L_0x0156
            long r26 = r48.getDialogId()
            int r6 = (r26 > r46 ? 1 : (r26 == r46 ? 0 : -1))
            if (r6 != 0) goto L_0x0156
            org.telegram.tgnet.TLRPC$Message r6 = r9.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r6 = r6.action
            if (r6 == 0) goto L_0x0147
            boolean r6 = r6 instanceof org.telegram.tgnet.TLRPC$TL_messageActionEmpty
            if (r6 == 0) goto L_0x0156
        L_0x0147:
            boolean r6 = r48.isOut()
            if (r6 == 0) goto L_0x0156
            org.telegram.tgnet.TLRPC$Message r6 = r9.messageOwner
            int r6 = r6.date
            int r10 = r10 - r6
            if (r10 > r11) goto L_0x0156
            r6 = 1
            goto L_0x0157
        L_0x0156:
            r6 = 0
        L_0x0157:
            r32 = r2
            r27 = r7
            r26 = r8
            r2 = -1
            goto L_0x01f5
        L_0x0160:
            r25 = r6
            r5 = 1
            r6 = -1
        L_0x0164:
            if (r5 < 0) goto L_0x01a0
            r9 = 0
        L_0x0167:
            r19 = r49[r5]
            r26 = r8
            int r8 = r19.size()
            r27 = r7
            if (r9 >= r8) goto L_0x0193
            r8 = r49[r5]
            java.lang.Object r8 = r8.valueAt(r9)
            org.telegram.messenger.MessageObject r8 = (org.telegram.messenger.MessageObject) r8
            r7 = -1
            if (r6 != r7) goto L_0x0182
            int r6 = r8.getFromChatId()
        L_0x0182:
            if (r6 < 0) goto L_0x0192
            int r7 = r8.getSenderId()
            if (r6 == r7) goto L_0x018b
            goto L_0x0192
        L_0x018b:
            int r9 = r9 + 1
            r8 = r26
            r7 = r27
            goto L_0x0167
        L_0x0192:
            r6 = -2
        L_0x0193:
            r7 = -2
            if (r6 != r7) goto L_0x0197
            goto L_0x01a4
        L_0x0197:
            int r5 = r5 + -1
            r9 = r48
            r8 = r26
            r7 = r27
            goto L_0x0164
        L_0x01a0:
            r27 = r7
            r26 = r8
        L_0x01a4:
            r5 = 0
            r7 = 1
        L_0x01a6:
            if (r7 < 0) goto L_0x01dc
            r8 = 0
        L_0x01a9:
            r9 = r49[r7]
            int r9 = r9.size()
            if (r8 >= r9) goto L_0x01d7
            r9 = r49[r7]
            java.lang.Object r9 = r9.valueAt(r8)
            org.telegram.messenger.MessageObject r9 = (org.telegram.messenger.MessageObject) r9
            r32 = r2
            r2 = 1
            if (r7 != r2) goto L_0x01d2
            boolean r2 = r9.isOut()
            if (r2 == 0) goto L_0x01d2
            org.telegram.tgnet.TLRPC$Message r2 = r9.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r9 = r2.action
            if (r9 != 0) goto L_0x01d2
            int r2 = r2.date
            int r2 = r10 - r2
            if (r2 > r11) goto L_0x01d2
            int r5 = r5 + 1
        L_0x01d2:
            int r8 = r8 + 1
            r2 = r32
            goto L_0x01a9
        L_0x01d7:
            r32 = r2
            int r7 = r7 + -1
            goto L_0x01a6
        L_0x01dc:
            r32 = r2
            r2 = -1
            if (r6 == r2) goto L_0x01f3
            org.telegram.messenger.MessagesController r7 = org.telegram.messenger.MessagesController.getInstance(r15)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            org.telegram.tgnet.TLRPC$User r6 = r7.getUser(r6)
            r40 = r6
            r6 = r5
            r5 = r40
            goto L_0x01f5
        L_0x01f3:
            r6 = r5
            r5 = 0
        L_0x01f5:
            if (r5 == 0) goto L_0x0324
            int r7 = r5.id
            org.telegram.messenger.UserConfig r8 = org.telegram.messenger.UserConfig.getInstance(r15)
            int r8 = r8.getClientUserId()
            if (r7 == r8) goto L_0x0324
            r7 = 1
            if (r0 != r7) goto L_0x0267
            boolean r8 = r4.creator
            if (r8 != 0) goto L_0x0267
            org.telegram.ui.ActionBar.AlertDialog[] r13 = new org.telegram.ui.ActionBar.AlertDialog[r7]
            org.telegram.ui.ActionBar.AlertDialog r0 = new org.telegram.ui.ActionBar.AlertDialog
            r2 = 3
            r0.<init>(r1, r2)
            r1 = 0
            r13[r1] = r0
            org.telegram.tgnet.TLRPC$TL_channels_getParticipant r12 = new org.telegram.tgnet.TLRPC$TL_channels_getParticipant
            r12.<init>()
            org.telegram.tgnet.TLRPC$InputChannel r0 = org.telegram.messenger.MessagesController.getInputChannel((org.telegram.tgnet.TLRPC$Chat) r43)
            r12.channel = r0
            org.telegram.tgnet.TLRPC$InputPeer r0 = org.telegram.messenger.MessagesController.getInputPeer((org.telegram.tgnet.TLRPC$User) r5)
            r12.participant = r0
            org.telegram.tgnet.ConnectionsManager r11 = org.telegram.tgnet.ConnectionsManager.getInstance(r15)
            org.telegram.ui.Components.-$$Lambda$AlertsCreator$jGZ_BlSkSF0atOyJ7yeEsQQxb7U r10 = new org.telegram.ui.Components.-$$Lambda$AlertsCreator$jGZ_BlSkSF0atOyJ7yeEsQQxb7U
            r0 = r10
            r1 = r13
            r2 = r41
            r3 = r42
            r4 = r43
            r5 = r44
            r6 = r45
            r7 = r46
            r9 = r48
            r14 = r10
            r10 = r49
            r24 = r15
            r15 = r11
            r11 = r50
            r16 = r14
            r14 = r12
            r12 = r51
            r33 = r13
            r13 = r53
            r0.<init>(r1, r2, r3, r4, r5, r6, r7, r9, r10, r11, r12, r13)
            r0 = r16
            int r0 = r15.sendRequest(r14, r0)
            org.telegram.ui.Components.-$$Lambda$AlertsCreator$padH8ZflY_7x7Vcpz07IcT1hGlU r1 = new org.telegram.ui.Components.-$$Lambda$AlertsCreator$padH8ZflY_7x7Vcpz07IcT1hGlU
            r7 = r41
            r8 = r24
            r2 = r33
            r1.<init>(r2, r8, r0, r7)
            r2 = 1000(0x3e8, double:4.94E-321)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r1, r2)
            return
        L_0x0267:
            r7 = r14
            r8 = r15
            android.widget.FrameLayout r9 = new android.widget.FrameLayout
            r9.<init>(r1)
            r10 = 0
            r11 = 0
        L_0x0270:
            r14 = 3
            if (r10 >= r14) goto L_0x031a
            r15 = 2
            if (r0 == r15) goto L_0x0278
            if (r3 != 0) goto L_0x027e
        L_0x0278:
            if (r10 != 0) goto L_0x027e
            r19 = r3
            goto L_0x0311
        L_0x027e:
            org.telegram.ui.Cells.CheckBoxCell r15 = new org.telegram.ui.Cells.CheckBoxCell
            r2 = 1
            r15.<init>(r1, r2)
            r46 = 0
            android.graphics.drawable.Drawable r14 = org.telegram.ui.ActionBar.Theme.getSelectorDrawable(r46)
            r15.setBackgroundDrawable(r14)
            java.lang.Integer r14 = java.lang.Integer.valueOf(r10)
            r15.setTag(r14)
            if (r10 != 0) goto L_0x02a6
            r14 = 2131625128(0x7f0e04a8, float:1.8877455E38)
            java.lang.String r2 = "DeleteBanUser"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r14)
            r14 = 0
            r15.setText(r2, r13, r14, r14)
        L_0x02a3:
            r19 = r3
            goto L_0x02d1
        L_0x02a6:
            r14 = 0
            if (r10 != r2) goto L_0x02b6
            r2 = 2131625158(0x7f0e04c6, float:1.8877516E38)
            java.lang.String r0 = "DeleteReportSpam"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r0, r2)
            r15.setText(r0, r13, r14, r14)
            goto L_0x02a3
        L_0x02b6:
            r2 = 1
            java.lang.Object[] r0 = new java.lang.Object[r2]
            java.lang.String r2 = r5.first_name
            r19 = r3
            java.lang.String r3 = r5.last_name
            java.lang.String r2 = org.telegram.messenger.ContactsController.formatName(r2, r3)
            r0[r14] = r2
            java.lang.String r2 = "DeleteAllFrom"
            r3 = 2131625114(0x7f0e049a, float:1.8877427E38)
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r3, r0)
            r15.setText(r0, r13, r14, r14)
        L_0x02d1:
            boolean r0 = org.telegram.messenger.LocaleController.isRTL
            if (r0 == 0) goto L_0x02da
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r22)
            goto L_0x02de
        L_0x02da:
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r23)
        L_0x02de:
            boolean r2 = org.telegram.messenger.LocaleController.isRTL
            if (r2 == 0) goto L_0x02e7
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r23)
            goto L_0x02eb
        L_0x02e7:
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r22)
        L_0x02eb:
            r3 = 0
            r15.setPadding(r0, r3, r2, r3)
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
            r9.addView(r15, r0)
            org.telegram.ui.Components.-$$Lambda$AlertsCreator$Q63OmbOdfLdEUNFYOgv8Gvar_vSA r0 = new org.telegram.ui.Components.-$$Lambda$AlertsCreator$Q63OmbOdfLdEUNFYOgv8Gvar_vSA
            r0.<init>(r12)
            r15.setOnClickListener(r0)
            int r11 = r11 + 1
        L_0x0311:
            int r10 = r10 + 1
            r0 = r52
            r3 = r19
            r2 = -1
            goto L_0x0270
        L_0x031a:
            r0 = r32
            r0.setView(r9)
            r9 = r25
            r1 = 0
            goto L_0x0396
        L_0x0324:
            r7 = r14
            r8 = r15
            r0 = r32
            if (r6 <= 0) goto L_0x0392
            if (r27 == 0) goto L_0x0392
            android.widget.FrameLayout r2 = new android.widget.FrameLayout
            r2.<init>(r1)
            org.telegram.ui.Cells.CheckBoxCell r3 = new org.telegram.ui.Cells.CheckBoxCell
            r9 = 1
            r3.<init>(r1, r9)
            r1 = 0
            android.graphics.drawable.Drawable r9 = org.telegram.ui.ActionBar.Theme.getSelectorDrawable(r1)
            r3.setBackgroundDrawable(r9)
            r14 = r26
            r9 = 2131625149(0x7f0e04bd, float:1.8877498E38)
            java.lang.String r9 = org.telegram.messenger.LocaleController.getString(r14, r9)
            r3.setText(r9, r13, r1, r1)
            boolean r1 = org.telegram.messenger.LocaleController.isRTL
            if (r1 == 0) goto L_0x0354
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r22)
            goto L_0x0358
        L_0x0354:
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r23)
        L_0x0358:
            boolean r9 = org.telegram.messenger.LocaleController.isRTL
            if (r9 == 0) goto L_0x0361
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r23)
            goto L_0x0365
        L_0x0361:
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r22)
        L_0x0365:
            r10 = 0
            r3.setPadding(r1, r10, r9, r10)
            r32 = -1
            r33 = 1111490560(0x42400000, float:48.0)
            r34 = 51
            r35 = 0
            r36 = 0
            r37 = 0
            r38 = 0
            android.widget.FrameLayout$LayoutParams r1 = org.telegram.ui.Components.LayoutHelper.createFrame(r32, r33, r34, r35, r36, r37, r38)
            r2.addView(r3, r1)
            org.telegram.ui.Components.-$$Lambda$AlertsCreator$whK5b5eFqVfdpuaQIogGpiZ8LpM r1 = new org.telegram.ui.Components.-$$Lambda$AlertsCreator$whK5b5eFqVfdpuaQIogGpiZ8LpM
            r9 = r25
            r1.<init>(r9)
            r3.setOnClickListener(r1)
            r0.setView(r2)
            r1 = 9
            r0.setCustomViewOffset(r1)
            r1 = 1
            goto L_0x0396
        L_0x0392:
            r9 = r25
            r1 = 0
            r5 = 0
        L_0x0396:
            r2 = r42
            r25 = r5
            r24 = r8
            r8 = r30
            r10 = 0
            goto L_0x0529
        L_0x03a1:
            r0 = r2
            r9 = r6
            r27 = r7
            r7 = r14
            r14 = r8
            r8 = r15
            if (r51 != 0) goto L_0x051e
            boolean r2 = org.telegram.messenger.ChatObject.isChannel(r43)
            if (r2 != 0) goto L_0x051e
            if (r44 != 0) goto L_0x051e
            r2 = r42
            r3 = -1
            if (r2 == 0) goto L_0x03cb
            int r5 = r2.id
            org.telegram.messenger.UserConfig r6 = org.telegram.messenger.UserConfig.getInstance(r8)
            int r6 = r6.getClientUserId()
            if (r5 == r6) goto L_0x03cb
            boolean r5 = r2.bot
            if (r5 == 0) goto L_0x03cd
            boolean r5 = r2.support
            if (r5 != 0) goto L_0x03cd
        L_0x03cb:
            if (r4 == 0) goto L_0x047a
        L_0x03cd:
            r5 = r48
            if (r5 == 0) goto L_0x040f
            boolean r6 = r48.isSendError()
            if (r6 != 0) goto L_0x0404
            org.telegram.tgnet.TLRPC$Message r6 = r5.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r6 = r6.action
            if (r6 == 0) goto L_0x03ed
            boolean r15 = r6 instanceof org.telegram.tgnet.TLRPC$TL_messageActionEmpty
            if (r15 != 0) goto L_0x03ed
            boolean r15 = r6 instanceof org.telegram.tgnet.TLRPC$TL_messageActionPhoneCall
            if (r15 != 0) goto L_0x03ed
            boolean r15 = r6 instanceof org.telegram.tgnet.TLRPC$TL_messageActionPinMessage
            if (r15 != 0) goto L_0x03ed
            boolean r6 = r6 instanceof org.telegram.tgnet.TLRPC$TL_messageActionGeoProximityReached
            if (r6 == 0) goto L_0x0404
        L_0x03ed:
            boolean r6 = r48.isOut()
            if (r6 != 0) goto L_0x03fb
            if (r19 != 0) goto L_0x03fb
            boolean r6 = org.telegram.messenger.ChatObject.hasAdminRights(r43)
            if (r6 == 0) goto L_0x0404
        L_0x03fb:
            org.telegram.tgnet.TLRPC$Message r6 = r5.messageOwner
            int r6 = r6.date
            int r10 = r10 - r6
            if (r10 > r11) goto L_0x0404
            r6 = 1
            goto L_0x0405
        L_0x0404:
            r6 = 0
        L_0x0405:
            boolean r10 = r48.isOut()
            r11 = 1
            r10 = r10 ^ r11
            r24 = r8
            goto L_0x047e
        L_0x040f:
            r6 = 0
            r15 = 0
            r16 = 1
        L_0x0413:
            if (r16 < 0) goto L_0x0475
            r3 = 0
        L_0x0416:
            r24 = r49[r16]
            int r5 = r24.size()
            if (r3 >= r5) goto L_0x046b
            r5 = r49[r16]
            java.lang.Object r5 = r5.valueAt(r3)
            org.telegram.messenger.MessageObject r5 = (org.telegram.messenger.MessageObject) r5
            r24 = r8
            org.telegram.tgnet.TLRPC$Message r8 = r5.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r8 = r8.action
            if (r8 == 0) goto L_0x043f
            boolean r7 = r8 instanceof org.telegram.tgnet.TLRPC$TL_messageActionEmpty
            if (r7 != 0) goto L_0x043f
            boolean r7 = r8 instanceof org.telegram.tgnet.TLRPC$TL_messageActionPhoneCall
            if (r7 != 0) goto L_0x043f
            boolean r7 = r8 instanceof org.telegram.tgnet.TLRPC$TL_messageActionPinMessage
            if (r7 != 0) goto L_0x043f
            boolean r7 = r8 instanceof org.telegram.tgnet.TLRPC$TL_messageActionGeoProximityReached
            if (r7 != 0) goto L_0x043f
            goto L_0x0462
        L_0x043f:
            boolean r7 = r5.isOut()
            if (r7 != 0) goto L_0x044f
            if (r19 != 0) goto L_0x044f
            if (r4 == 0) goto L_0x0462
            boolean r7 = org.telegram.messenger.ChatObject.canBlockUsers(r43)
            if (r7 == 0) goto L_0x0462
        L_0x044f:
            org.telegram.tgnet.TLRPC$Message r7 = r5.messageOwner
            int r7 = r7.date
            int r7 = r10 - r7
            if (r7 > r11) goto L_0x0462
            int r15 = r15 + 1
            if (r6 != 0) goto L_0x0462
            boolean r5 = r5.isOut()
            if (r5 != 0) goto L_0x0462
            r6 = 1
        L_0x0462:
            int r3 = r3 + 1
            r7 = r41
            r5 = r48
            r8 = r24
            goto L_0x0416
        L_0x046b:
            r24 = r8
            int r16 = r16 + -1
            r7 = r41
            r5 = r48
            r3 = -1
            goto L_0x0413
        L_0x0475:
            r24 = r8
            r10 = r6
            r6 = r15
            goto L_0x047e
        L_0x047a:
            r24 = r8
            r6 = 0
            r10 = 0
        L_0x047e:
            if (r6 <= 0) goto L_0x051a
            if (r27 == 0) goto L_0x051a
            if (r2 == 0) goto L_0x048a
            boolean r3 = org.telegram.messenger.UserObject.isDeleted(r42)
            if (r3 != 0) goto L_0x051a
        L_0x048a:
            android.widget.FrameLayout r3 = new android.widget.FrameLayout
            r3.<init>(r1)
            org.telegram.ui.Cells.CheckBoxCell r5 = new org.telegram.ui.Cells.CheckBoxCell
            r7 = 1
            r5.<init>(r1, r7)
            r1 = 0
            android.graphics.drawable.Drawable r8 = org.telegram.ui.ActionBar.Theme.getSelectorDrawable(r1)
            r5.setBackgroundDrawable(r8)
            if (r31 == 0) goto L_0x04b6
            r8 = 2131625150(0x7f0e04be, float:1.88775E38)
            java.lang.Object[] r11 = new java.lang.Object[r7]
            java.lang.String r7 = org.telegram.messenger.UserObject.getFirstName(r42)
            r11[r1] = r7
            java.lang.String r7 = "DeleteMessagesOptionAlso"
            java.lang.String r7 = org.telegram.messenger.LocaleController.formatString(r7, r8, r11)
            r5.setText(r7, r13, r1, r1)
            r8 = r30
            goto L_0x04d5
        L_0x04b6:
            r8 = r30
            if (r4 == 0) goto L_0x04cb
            if (r10 != 0) goto L_0x04be
            if (r6 != r8) goto L_0x04cb
        L_0x04be:
            r7 = 2131625137(0x7f0e04b1, float:1.8877473E38)
            java.lang.String r11 = "DeleteForAll"
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r11, r7)
            r5.setText(r7, r13, r1, r1)
            goto L_0x04d5
        L_0x04cb:
            r7 = 2131625149(0x7f0e04bd, float:1.8877498E38)
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r14, r7)
            r5.setText(r7, r13, r1, r1)
        L_0x04d5:
            boolean r1 = org.telegram.messenger.LocaleController.isRTL
            if (r1 == 0) goto L_0x04de
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r22)
            goto L_0x04e2
        L_0x04de:
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r23)
        L_0x04e2:
            boolean r7 = org.telegram.messenger.LocaleController.isRTL
            if (r7 == 0) goto L_0x04eb
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r23)
            goto L_0x04ef
        L_0x04eb:
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r22)
        L_0x04ef:
            r11 = 0
            r5.setPadding(r1, r11, r7, r11)
            r33 = -1
            r34 = 1111490560(0x42400000, float:48.0)
            r35 = 51
            r36 = 0
            r37 = 0
            r38 = 0
            r39 = 0
            android.widget.FrameLayout$LayoutParams r1 = org.telegram.ui.Components.LayoutHelper.createFrame(r33, r34, r35, r36, r37, r38, r39)
            r3.addView(r5, r1)
            org.telegram.ui.Components.-$$Lambda$AlertsCreator$wOoEuWZEQzKPiYd459Q12gu_tgo r1 = new org.telegram.ui.Components.-$$Lambda$AlertsCreator$wOoEuWZEQzKPiYd459Q12gu_tgo
            r1.<init>(r9)
            r5.setOnClickListener(r1)
            r0.setView(r3)
            r1 = 9
            r0.setCustomViewOffset(r1)
            r1 = 1
            goto L_0x0527
        L_0x051a:
            r8 = r30
            r1 = 0
            goto L_0x0527
        L_0x051e:
            r2 = r42
            r24 = r8
            r8 = r30
            r1 = 0
            r6 = 0
            r10 = 0
        L_0x0527:
            r25 = 0
        L_0x0529:
            r3 = 2131625106(0x7f0e0492, float:1.887741E38)
            java.lang.String r5 = "Delete"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r5, r3)
            org.telegram.ui.Components.-$$Lambda$AlertsCreator$DK7dltJytOr9PC_8_hdz59JI4Qw r5 = new org.telegram.ui.Components.-$$Lambda$AlertsCreator$DK7dltJytOr9PC_8_hdz59JI4Qw
            r7 = r24
            r15 = r5
            r16 = r48
            r17 = r50
            r18 = r44
            r19 = r7
            r22 = r9
            r23 = r51
            r24 = r49
            r26 = r12
            r27 = r43
            r28 = r45
            r29 = r53
            r15.<init>(r17, r18, r19, r20, r22, r23, r24, r25, r26, r27, r28, r29)
            r0.setPositiveButton(r3, r5)
            java.lang.String r3 = "messages"
            r5 = 1
            if (r8 != r5) goto L_0x0565
            r7 = 2131625160(0x7f0e04c8, float:1.887752E38)
            java.lang.String r9 = "DeleteSingleMessagesTitle"
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r9, r7)
            r0.setTitle(r7)
            goto L_0x057a
        L_0x0565:
            r7 = 2131625154(0x7f0e04c2, float:1.8877508E38)
            java.lang.Object[] r9 = new java.lang.Object[r5]
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatPluralString(r3, r8)
            r11 = 0
            r9[r11] = r5
            java.lang.String r5 = "DeleteMessagesTitle"
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r5, r7, r9)
            r0.setTitle(r5)
        L_0x057a:
            r5 = 2131624351(0x7f0e019f, float:1.887588E38)
            java.lang.String r7 = "AreYouSureDeleteSingleMessage"
            r9 = 2131624343(0x7f0e0197, float:1.8875863E38)
            java.lang.String r11 = "AreYouSureDeleteFewMessages"
            if (r4 == 0) goto L_0x05b9
            if (r10 == 0) goto L_0x05b9
            if (r1 == 0) goto L_0x05a4
            if (r6 == r8) goto L_0x05a4
            r1 = 2131625153(0x7f0e04c1, float:1.8877506E38)
            r2 = 1
            java.lang.Object[] r2 = new java.lang.Object[r2]
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatPluralString(r3, r6)
            r4 = 0
            r2[r4] = r3
            java.lang.String r3 = "DeleteMessagesTextGroupPart"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r3, r1, r2)
            r0.setMessage(r1)
            goto L_0x0631
        L_0x05a4:
            r2 = 1
            if (r8 != r2) goto L_0x05b0
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r7, r5)
            r0.setMessage(r1)
            goto L_0x0631
        L_0x05b0:
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r11, r9)
            r0.setMessage(r1)
            goto L_0x0631
        L_0x05b9:
            if (r1 == 0) goto L_0x05fa
            if (r31 != 0) goto L_0x05fa
            if (r6 == r8) goto L_0x05fa
            if (r4 == 0) goto L_0x05d8
            r1 = 2131625152(0x7f0e04c0, float:1.8877504E38)
            r2 = 1
            java.lang.Object[] r2 = new java.lang.Object[r2]
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatPluralString(r3, r6)
            r4 = 0
            r2[r4] = r3
            java.lang.String r3 = "DeleteMessagesTextGroup"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r3, r1, r2)
            r0.setMessage(r1)
            goto L_0x0631
        L_0x05d8:
            r4 = 0
            r1 = 2131625151(0x7f0e04bf, float:1.8877502E38)
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatPluralString(r3, r6)
            r5[r4] = r3
            java.lang.String r2 = org.telegram.messenger.UserObject.getFirstName(r42)
            r3 = 1
            r5[r3] = r2
            java.lang.String r2 = "DeleteMessagesText"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r2, r1, r5)
            android.text.SpannableStringBuilder r1 = org.telegram.messenger.AndroidUtilities.replaceTags(r1)
            r0.setMessage(r1)
            goto L_0x0631
        L_0x05fa:
            if (r4 == 0) goto L_0x061f
            boolean r1 = r4.megagroup
            if (r1 == 0) goto L_0x061f
            if (r51 != 0) goto L_0x061f
            r1 = 1
            if (r8 != r1) goto L_0x0612
            r1 = 2131624352(0x7f0e01a0, float:1.8875881E38)
            java.lang.String r2 = "AreYouSureDeleteSingleMessageMega"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r0.setMessage(r1)
            goto L_0x0631
        L_0x0612:
            r1 = 2131624344(0x7f0e0198, float:1.8875865E38)
            java.lang.String r2 = "AreYouSureDeleteFewMessagesMega"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r0.setMessage(r1)
            goto L_0x0631
        L_0x061f:
            r1 = 1
            if (r8 != r1) goto L_0x062a
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r7, r5)
            r0.setMessage(r1)
            goto L_0x0631
        L_0x062a:
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r11, r9)
            r0.setMessage(r1)
        L_0x0631:
            r1 = 2131624654(0x7f0e02ce, float:1.8876494E38)
            java.lang.String r2 = "Cancel"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r2 = 0
            r0.setNegativeButton(r1, r2)
            org.telegram.ui.ActionBar.AlertDialog r0 = r0.create()
            r1 = r41
            r1.showDialog(r0)
            r1 = -1
            android.view.View r0 = r0.getButton(r1)
            android.widget.TextView r0 = (android.widget.TextView) r0
            if (r0 == 0) goto L_0x0659
            java.lang.String r1 = "dialogTextRed2"
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r1)
            r0.setTextColor(r1)
        L_0x0659:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.AlertsCreator.createDeleteMessagesAlert(org.telegram.ui.ActionBar.BaseFragment, org.telegram.tgnet.TLRPC$User, org.telegram.tgnet.TLRPC$Chat, org.telegram.tgnet.TLRPC$EncryptedChat, org.telegram.tgnet.TLRPC$ChatFull, long, org.telegram.messenger.MessageObject, android.util.SparseArray[], org.telegram.messenger.MessageObject$GroupedMessages, boolean, int, java.lang.Runnable):void");
    }

    static /* synthetic */ void lambda$createDeleteMessagesAlert$85(AlertDialog[] alertDialogArr, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error, BaseFragment baseFragment, TLRPC$User tLRPC$User, TLRPC$Chat tLRPC$Chat, TLRPC$EncryptedChat tLRPC$EncryptedChat, TLRPC$ChatFull tLRPC$ChatFull, long j, MessageObject messageObject, SparseArray[] sparseArrayArr, MessageObject.GroupedMessages groupedMessages, boolean z, Runnable runnable) {
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
        createDeleteMessagesAlert(baseFragment, tLRPC$User, tLRPC$Chat, tLRPC$EncryptedChat, tLRPC$ChatFull, j, messageObject, sparseArrayArr, groupedMessages, z, i, runnable);
    }

    static /* synthetic */ void lambda$createDeleteMessagesAlert$88(AlertDialog[] alertDialogArr, int i, int i2, BaseFragment baseFragment) {
        if (alertDialogArr[0] != null) {
            alertDialogArr[0].setOnCancelListener(new DialogInterface.OnCancelListener(i, i2) {
                public final /* synthetic */ int f$0;
                public final /* synthetic */ int f$1;

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

    static /* synthetic */ void lambda$createDeleteMessagesAlert$89(boolean[] zArr, View view) {
        if (view.isEnabled()) {
            CheckBoxCell checkBoxCell = (CheckBoxCell) view;
            Integer num = (Integer) checkBoxCell.getTag();
            zArr[num.intValue()] = !zArr[num.intValue()];
            checkBoxCell.setChecked(zArr[num.intValue()], true);
        }
    }

    static /* synthetic */ void lambda$createDeleteMessagesAlert$90(boolean[] zArr, View view) {
        zArr[0] = !zArr[0];
        ((CheckBoxCell) view).setChecked(zArr[0], true);
    }

    static /* synthetic */ void lambda$createDeleteMessagesAlert$91(boolean[] zArr, View view) {
        zArr[0] = !zArr[0];
        ((CheckBoxCell) view).setChecked(zArr[0], true);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:34:0x00c9, code lost:
        r0 = ((org.telegram.messenger.MessageObject) r27[r16].get(r7.get(r8).intValue())).messageOwner.peer_id.channel_id;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    static /* synthetic */ void lambda$createDeleteMessagesAlert$93(org.telegram.messenger.MessageObject r19, org.telegram.messenger.MessageObject.GroupedMessages r20, org.telegram.tgnet.TLRPC$EncryptedChat r21, int r22, long r23, boolean[] r25, boolean r26, android.util.SparseArray[] r27, org.telegram.tgnet.TLRPC$User r28, boolean[] r29, org.telegram.tgnet.TLRPC$Chat r30, org.telegram.tgnet.TLRPC$ChatFull r31, java.lang.Runnable r32, android.content.DialogInterface r33, int r34) {
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
            org.telegram.tgnet.TLRPC$Peer r0 = r0.peer_id
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
            org.telegram.tgnet.TLRPC$Peer r0 = r0.peer_id
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
            r0.deleteParticipantFromChat(r1, r9, r2)
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
            org.telegram.ui.Components.-$$Lambda$AlertsCreator$QdHaKVPAyq20GGAreVkCVXINw8U r2 = org.telegram.ui.Components.$$Lambda$AlertsCreator$QdHaKVPAyq20GGAreVkCVXINw8U.INSTANCE
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
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.AlertsCreator.lambda$createDeleteMessagesAlert$93(org.telegram.messenger.MessageObject, org.telegram.messenger.MessageObject$GroupedMessages, org.telegram.tgnet.TLRPC$EncryptedChat, int, long, boolean[], boolean, android.util.SparseArray[], org.telegram.tgnet.TLRPC$User, boolean[], org.telegram.tgnet.TLRPC$Chat, org.telegram.tgnet.TLRPC$ChatFull, java.lang.Runnable, android.content.DialogInterface, int):void");
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
            builder.setPositiveButton(LocaleController.getString("Create", NUM), $$Lambda$AlertsCreator$I2aVpXtT39q07R_m_VW4G46AUnw.INSTANCE);
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
            editTextBoldCursor.setOnEditorActionListener($$Lambda$AlertsCreator$wrDlcwudUDFKokBNzjoKzuQrx4s.INSTANCE);
            editTextBoldCursor.setText(generateThemeName(themeAccent));
            editTextBoldCursor.setSelection(editTextBoldCursor.length());
            AlertDialog create = builder.create();
            create.setOnShowListener(new DialogInterface.OnShowListener() {
                public final void onShow(DialogInterface dialogInterface) {
                    AndroidUtilities.runOnUIThread(new Runnable() {
                        public final void run() {
                            AlertsCreator.lambda$createThemeCreateDialog$96(EditTextBoldCursor.this);
                        }
                    });
                }
            });
            baseFragment2.showDialog(create);
            editTextBoldCursor.requestFocus();
            create.getButton(-1).setOnClickListener(new View.OnClickListener(editTextBoldCursor, themeAccent, themeInfo, create) {
                public final /* synthetic */ EditTextBoldCursor f$1;
                public final /* synthetic */ Theme.ThemeAccent f$2;
                public final /* synthetic */ Theme.ThemeInfo f$3;
                public final /* synthetic */ AlertDialog f$4;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                    this.f$4 = r5;
                }

                public final void onClick(View view) {
                    AlertsCreator.lambda$createThemeCreateDialog$100(BaseFragment.this, this.f$1, this.f$2, this.f$3, this.f$4, view);
                }
            });
        }
    }

    static /* synthetic */ void lambda$createThemeCreateDialog$96(EditTextBoldCursor editTextBoldCursor) {
        editTextBoldCursor.requestFocus();
        AndroidUtilities.showKeyboard(editTextBoldCursor);
    }

    static /* synthetic */ void lambda$createThemeCreateDialog$100(BaseFragment baseFragment, EditTextBoldCursor editTextBoldCursor, Theme.ThemeAccent themeAccent, Theme.ThemeInfo themeInfo, AlertDialog alertDialog, View view) {
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
                    public final /* synthetic */ AlertDialog f$1;
                    public final /* synthetic */ BaseFragment f$2;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                    }

                    public final void run() {
                        AndroidUtilities.runOnUIThread(new Runnable(this.f$1, this.f$2) {
                            public final /* synthetic */ AlertDialog f$1;
                            public final /* synthetic */ BaseFragment f$2;

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
