package org.telegram.ui.Components;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import java.util.Calendar;
import java.util.Locale;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.MessagesStorage.IntCallback;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationsController;
import org.telegram.messenger.SecretChatHelper;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.beta.R;
import org.telegram.messenger.browser.Browser;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.EncryptedChat;
import org.telegram.tgnet.TLRPC.InputPeer;
import org.telegram.tgnet.TLRPC.TL_account_changePhone;
import org.telegram.tgnet.TLRPC.TL_account_confirmPhone;
import org.telegram.tgnet.TLRPC.TL_account_getAuthorizationForm;
import org.telegram.tgnet.TLRPC.TL_account_getPassword;
import org.telegram.tgnet.TLRPC.TL_account_getTmpPassword;
import org.telegram.tgnet.TLRPC.TL_account_reportPeer;
import org.telegram.tgnet.TLRPC.TL_account_saveSecureValue;
import org.telegram.tgnet.TLRPC.TL_account_sendChangePhoneCode;
import org.telegram.tgnet.TLRPC.TL_account_sendConfirmPhoneCode;
import org.telegram.tgnet.TLRPC.TL_account_verifyEmail;
import org.telegram.tgnet.TLRPC.TL_account_verifyPhone;
import org.telegram.tgnet.TLRPC.TL_auth_resendCode;
import org.telegram.tgnet.TLRPC.TL_channels_createChannel;
import org.telegram.tgnet.TLRPC.TL_channels_editAdmin;
import org.telegram.tgnet.TLRPC.TL_channels_editBanned;
import org.telegram.tgnet.TLRPC.TL_channels_inviteToChannel;
import org.telegram.tgnet.TLRPC.TL_channels_joinChannel;
import org.telegram.tgnet.TLRPC.TL_contacts_importContacts;
import org.telegram.tgnet.TLRPC.TL_dialog;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_inputReportReasonPornography;
import org.telegram.tgnet.TLRPC.TL_inputReportReasonSpam;
import org.telegram.tgnet.TLRPC.TL_inputReportReasonViolence;
import org.telegram.tgnet.TLRPC.TL_messages_addChatUser;
import org.telegram.tgnet.TLRPC.TL_messages_createChat;
import org.telegram.tgnet.TLRPC.TL_messages_editMessage;
import org.telegram.tgnet.TLRPC.TL_messages_forwardMessages;
import org.telegram.tgnet.TLRPC.TL_messages_getAttachedStickers;
import org.telegram.tgnet.TLRPC.TL_messages_importChatInvite;
import org.telegram.tgnet.TLRPC.TL_messages_report;
import org.telegram.tgnet.TLRPC.TL_messages_sendBroadcast;
import org.telegram.tgnet.TLRPC.TL_messages_sendInlineBotResult;
import org.telegram.tgnet.TLRPC.TL_messages_sendMedia;
import org.telegram.tgnet.TLRPC.TL_messages_sendMessage;
import org.telegram.tgnet.TLRPC.TL_messages_startBot;
import org.telegram.tgnet.TLRPC.TL_payments_sendPaymentForm;
import org.telegram.tgnet.TLRPC.TL_payments_validateRequestedInfo;
import org.telegram.tgnet.TLRPC.TL_peerNotifySettings;
import org.telegram.tgnet.TLRPC.TL_updateUserName;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.AlertDialog.Builder;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.CacheControlActivity;
import org.telegram.ui.Cells.AccountSelectCell;
import org.telegram.ui.Cells.RadioColorCell;
import org.telegram.ui.Cells.TextColorCell;
import org.telegram.ui.Components.NumberPicker.Formatter;
import org.telegram.ui.Components.NumberPicker.OnScrollListener;
import org.telegram.ui.Components.NumberPicker.OnValueChangeListener;
import org.telegram.ui.LaunchActivity;
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

    /* renamed from: org.telegram.ui.Components.AlertsCreator$2 */
    static class C21752 implements Formatter {
        C21752() {
        }

        public String format(int value) {
            Calendar calendar = Calendar.getInstance();
            calendar.set(2, value);
            return calendar.getDisplayName(2, 1, Locale.getDefault());
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static Dialog processError(int currentAccount, TL_error error, BaseFragment fragment, TLObject request, Object... args) {
        boolean z = false;
        if (error.code == 406 || error.text == null) {
            return null;
        }
        if (!(request instanceof TL_account_saveSecureValue) && !(request instanceof TL_account_getAuthorizationForm)) {
            if (!(request instanceof TL_channels_joinChannel) && !(request instanceof TL_channels_editAdmin) && !(request instanceof TL_channels_inviteToChannel) && !(request instanceof TL_messages_addChatUser) && !(request instanceof TL_messages_startBot) && !(request instanceof TL_channels_editBanned)) {
                if (!(request instanceof TL_messages_createChat)) {
                    if (!(request instanceof TL_channels_createChannel)) {
                        if (!(request instanceof TL_messages_editMessage)) {
                            if (!(request instanceof TL_messages_sendMessage) && !(request instanceof TL_messages_sendMedia) && !(request instanceof TL_messages_sendBroadcast) && !(request instanceof TL_messages_sendInlineBotResult) && !(request instanceof TL_messages_forwardMessages)) {
                                if (!(request instanceof TL_messages_importChatInvite)) {
                                    if (!(request instanceof TL_messages_getAttachedStickers)) {
                                        if (!(request instanceof TL_account_confirmPhone) && !(request instanceof TL_account_verifyPhone) && !(request instanceof TL_account_verifyEmail)) {
                                            if (!(request instanceof TL_auth_resendCode)) {
                                                if (!(request instanceof TL_account_sendConfirmPhoneCode)) {
                                                    if (!(request instanceof TL_account_changePhone)) {
                                                        if (!(request instanceof TL_account_sendChangePhoneCode)) {
                                                            String str;
                                                            if (!(request instanceof TL_updateUserName)) {
                                                                if (!(request instanceof TL_contacts_importContacts)) {
                                                                    if (!(request instanceof TL_account_getPassword) && !(request instanceof TL_account_getTmpPassword)) {
                                                                        if (!(request instanceof TL_payments_sendPaymentForm)) {
                                                                            if (request instanceof TL_payments_validateRequestedInfo) {
                                                                                String str2 = error.text;
                                                                                boolean z2 = true;
                                                                                switch (str2.hashCode()) {
                                                                                    case 1758025548:
                                                                                        if (str2.equals("SHIPPING_NOT_AVAILABLE")) {
                                                                                            z2 = false;
                                                                                            break;
                                                                                        }
                                                                                        break;
                                                                                }
                                                                                switch (z2) {
                                                                                    case false:
                                                                                        showSimpleToast(fragment, LocaleController.getString("PaymentNoShippingMethod", R.string.PaymentNoShippingMethod));
                                                                                        break;
                                                                                    default:
                                                                                        showSimpleToast(fragment, error.text);
                                                                                        break;
                                                                                }
                                                                            }
                                                                        }
                                                                        str = error.text;
                                                                        switch (str.hashCode()) {
                                                                            case -1144062453:
                                                                                if (str.equals("BOT_PRECHECKOUT_FAILED")) {
                                                                                    break;
                                                                                }
                                                                            case -784238410:
                                                                                if (str.equals("PAYMENT_FAILED")) {
                                                                                    z = true;
                                                                                    break;
                                                                                }
                                                                            default:
                                                                                z = true;
                                                                                break;
                                                                        }
                                                                        switch (z) {
                                                                            case false:
                                                                                showSimpleToast(fragment, LocaleController.getString("PaymentPrecheckoutFailed", R.string.PaymentPrecheckoutFailed));
                                                                                break;
                                                                            case true:
                                                                                showSimpleToast(fragment, LocaleController.getString("PaymentFailed", R.string.PaymentFailed));
                                                                                break;
                                                                            default:
                                                                                showSimpleToast(fragment, error.text);
                                                                                break;
                                                                        }
                                                                    } else if (error.text.startsWith("FLOOD_WAIT")) {
                                                                        showSimpleToast(fragment, getFloodWaitString(error.text));
                                                                    } else {
                                                                        showSimpleToast(fragment, error.text);
                                                                    }
                                                                } else if (error == null || error.text.startsWith("FLOOD_WAIT")) {
                                                                    showSimpleAlert(fragment, LocaleController.getString("FloodWait", R.string.FloodWait));
                                                                } else {
                                                                    showSimpleAlert(fragment, LocaleController.getString("ErrorOccurred", R.string.ErrorOccurred) + "\n" + error.text);
                                                                }
                                                            } else {
                                                                str = error.text;
                                                                switch (str.hashCode()) {
                                                                    case 288843630:
                                                                        if (str.equals("USERNAME_INVALID")) {
                                                                            break;
                                                                        }
                                                                    case 533175271:
                                                                        if (str.equals("USERNAME_OCCUPIED")) {
                                                                            z = true;
                                                                            break;
                                                                        }
                                                                    default:
                                                                        z = true;
                                                                        break;
                                                                }
                                                                switch (z) {
                                                                    case false:
                                                                        showSimpleAlert(fragment, LocaleController.getString("UsernameInvalid", R.string.UsernameInvalid));
                                                                        break;
                                                                    case true:
                                                                        showSimpleAlert(fragment, LocaleController.getString("UsernameInUse", R.string.UsernameInUse));
                                                                        break;
                                                                    default:
                                                                        showSimpleAlert(fragment, LocaleController.getString("ErrorOccurred", R.string.ErrorOccurred));
                                                                        break;
                                                                }
                                                            }
                                                        } else if (error.text.contains("PHONE_NUMBER_INVALID")) {
                                                            showSimpleAlert(fragment, LocaleController.getString("InvalidPhoneNumber", R.string.InvalidPhoneNumber));
                                                        } else if (error.text.contains("PHONE_CODE_EMPTY") || error.text.contains("PHONE_CODE_INVALID")) {
                                                            showSimpleAlert(fragment, LocaleController.getString("InvalidCode", R.string.InvalidCode));
                                                        } else if (error.text.contains("PHONE_CODE_EXPIRED")) {
                                                            showSimpleAlert(fragment, LocaleController.getString("CodeExpired", R.string.CodeExpired));
                                                        } else if (error.text.startsWith("FLOOD_WAIT")) {
                                                            showSimpleAlert(fragment, LocaleController.getString("FloodWait", R.string.FloodWait));
                                                        } else if (error.text.startsWith("PHONE_NUMBER_OCCUPIED")) {
                                                            showSimpleAlert(fragment, LocaleController.formatString("ChangePhoneNumberOccupied", R.string.ChangePhoneNumberOccupied, (String) args[0]));
                                                        } else {
                                                            showSimpleAlert(fragment, LocaleController.getString("ErrorOccurred", R.string.ErrorOccurred));
                                                        }
                                                    } else if (error.text.contains("PHONE_NUMBER_INVALID")) {
                                                        showSimpleAlert(fragment, LocaleController.getString("InvalidPhoneNumber", R.string.InvalidPhoneNumber));
                                                    } else if (error.text.contains("PHONE_CODE_EMPTY") || error.text.contains("PHONE_CODE_INVALID")) {
                                                        showSimpleAlert(fragment, LocaleController.getString("InvalidCode", R.string.InvalidCode));
                                                    } else if (error.text.contains("PHONE_CODE_EXPIRED")) {
                                                        showSimpleAlert(fragment, LocaleController.getString("CodeExpired", R.string.CodeExpired));
                                                    } else if (error.text.startsWith("FLOOD_WAIT")) {
                                                        showSimpleAlert(fragment, LocaleController.getString("FloodWait", R.string.FloodWait));
                                                    } else {
                                                        showSimpleAlert(fragment, error.text);
                                                    }
                                                } else if (error.code == 400) {
                                                    return showSimpleAlert(fragment, LocaleController.getString("CancelLinkExpired", R.string.CancelLinkExpired));
                                                } else {
                                                    if (error.text != null) {
                                                        if (error.text.startsWith("FLOOD_WAIT")) {
                                                            return showSimpleAlert(fragment, LocaleController.getString("FloodWait", R.string.FloodWait));
                                                        }
                                                        return showSimpleAlert(fragment, LocaleController.getString("ErrorOccurred", R.string.ErrorOccurred));
                                                    }
                                                }
                                            } else if (error.text.contains("PHONE_NUMBER_INVALID")) {
                                                return showSimpleAlert(fragment, LocaleController.getString("InvalidPhoneNumber", R.string.InvalidPhoneNumber));
                                            } else {
                                                if (error.text.contains("PHONE_CODE_EMPTY") || error.text.contains("PHONE_CODE_INVALID")) {
                                                    return showSimpleAlert(fragment, LocaleController.getString("InvalidCode", R.string.InvalidCode));
                                                }
                                                if (error.text.contains("PHONE_CODE_EXPIRED")) {
                                                    return showSimpleAlert(fragment, LocaleController.getString("CodeExpired", R.string.CodeExpired));
                                                }
                                                if (error.text.startsWith("FLOOD_WAIT")) {
                                                    return showSimpleAlert(fragment, LocaleController.getString("FloodWait", R.string.FloodWait));
                                                }
                                                if (error.code != -1000) {
                                                    return showSimpleAlert(fragment, LocaleController.getString("ErrorOccurred", R.string.ErrorOccurred) + "\n" + error.text);
                                                }
                                            }
                                        } else if (error.text.contains("PHONE_CODE_EMPTY") || error.text.contains("PHONE_CODE_INVALID") || error.text.contains("CODE_INVALID") || error.text.contains("CODE_EMPTY")) {
                                            return showSimpleAlert(fragment, LocaleController.getString("InvalidCode", R.string.InvalidCode));
                                        } else {
                                            if (error.text.contains("PHONE_CODE_EXPIRED") || error.text.contains("EMAIL_VERIFY_EXPIRED")) {
                                                return showSimpleAlert(fragment, LocaleController.getString("CodeExpired", R.string.CodeExpired));
                                            }
                                            if (error.text.startsWith("FLOOD_WAIT")) {
                                                return showSimpleAlert(fragment, LocaleController.getString("FloodWait", R.string.FloodWait));
                                            }
                                            return showSimpleAlert(fragment, error.text);
                                        }
                                    } else if (!(fragment == null || fragment.getParentActivity() == null)) {
                                        Toast.makeText(fragment.getParentActivity(), LocaleController.getString("ErrorOccurred", R.string.ErrorOccurred) + "\n" + error.text, 0).show();
                                    }
                                } else if (error.text.startsWith("FLOOD_WAIT")) {
                                    showSimpleAlert(fragment, LocaleController.getString("FloodWait", R.string.FloodWait));
                                } else if (error.text.equals("USERS_TOO_MUCH")) {
                                    showSimpleAlert(fragment, LocaleController.getString("JoinToGroupErrorFull", R.string.JoinToGroupErrorFull));
                                } else {
                                    showSimpleAlert(fragment, LocaleController.getString("JoinToGroupErrorNotExist", R.string.JoinToGroupErrorNotExist));
                                }
                            } else if (error.text.equals("PEER_FLOOD")) {
                                NotificationCenter.getInstance(currentAccount).postNotificationName(NotificationCenter.needShowAlert, Integer.valueOf(0));
                            }
                        } else if (!error.text.equals("MESSAGE_NOT_MODIFIED")) {
                            if (fragment != null) {
                                showSimpleAlert(fragment, LocaleController.getString("EditMessageError", R.string.EditMessageError));
                            } else {
                                showSimpleToast(fragment, LocaleController.getString("EditMessageError", R.string.EditMessageError));
                            }
                        }
                    } else if (error.text.startsWith("FLOOD_WAIT")) {
                        showFloodWaitAlert(error.text, fragment);
                    } else {
                        showAddUserAlert(error.text, fragment, false);
                    }
                } else if (error.text.startsWith("FLOOD_WAIT")) {
                    showFloodWaitAlert(error.text, fragment);
                } else {
                    showAddUserAlert(error.text, fragment, false);
                }
            } else if (fragment != null) {
                showAddUserAlert(error.text, fragment, ((Boolean) args[0]).booleanValue());
            } else if (error.text.equals("PEER_FLOOD")) {
                NotificationCenter.getInstance(currentAccount).postNotificationName(NotificationCenter.needShowAlert, Integer.valueOf(1));
            }
        } else if (error.text.contains("PHONE_NUMBER_INVALID")) {
            showSimpleAlert(fragment, LocaleController.getString("InvalidPhoneNumber", R.string.InvalidPhoneNumber));
        } else if (error.text.startsWith("FLOOD_WAIT")) {
            showSimpleAlert(fragment, LocaleController.getString("FloodWait", R.string.FloodWait));
        } else {
            showSimpleAlert(fragment, LocaleController.getString("ErrorOccurred", R.string.ErrorOccurred) + "\n" + error.text);
        }
        return null;
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

    public static void showUpdateAppAlert(final Context context, String text, boolean updateApp) {
        if (context != null && text != null) {
            Builder builder = new Builder(context);
            builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
            builder.setMessage(text);
            builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), null);
            if (updateApp) {
                builder.setNegativeButton(LocaleController.getString("UpdateApp", R.string.UpdateApp), new OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Browser.openUrl(context, BuildVars.PLAYSTORE_APP_URL);
                    }
                });
            }
            builder.show();
        }
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

    public static Builder createDatePickerDialog(Context context, int minYear, int maxYear, String title, final boolean checkMinDate, DatePickerDelegate datePickerDelegate) {
        if (context == null) {
            return null;
        }
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(0);
        linearLayout.setWeightSum(1.0f);
        final NumberPicker monthPicker = new NumberPicker(context);
        final NumberPicker dayPicker = new NumberPicker(context);
        final NumberPicker yearPicker = new NumberPicker(context);
        monthPicker.setMinValue(0);
        monthPicker.setMaxValue(11);
        linearLayout.addView(monthPicker, LayoutHelper.createLinear(0, -2, 0.4f));
        monthPicker.setFormatter(new C21752());
        monthPicker.setOnValueChangedListener(new OnValueChangeListener() {
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                AlertsCreator.updateDayPicker(dayPicker, monthPicker, yearPicker);
            }
        });
        monthPicker.setOnScrollListener(new OnScrollListener() {
            public void onScrollStateChange(NumberPicker view, int scrollState) {
                if (checkMinDate && scrollState == 0) {
                    AlertsCreator.checkPickerDate(dayPicker, monthPicker, yearPicker);
                }
            }
        });
        linearLayout.addView(dayPicker, LayoutHelper.createLinear(0, -2, 0.2f));
        dayPicker.setOnScrollListener(new OnScrollListener() {
            public void onScrollStateChange(NumberPicker view, int scrollState) {
                if (checkMinDate && scrollState == 0) {
                    AlertsCreator.checkPickerDate(dayPicker, monthPicker, yearPicker);
                }
            }
        });
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        int currentYear = calendar.get(1);
        yearPicker.setMinValue(currentYear + minYear);
        yearPicker.setMaxValue(currentYear + maxYear);
        yearPicker.setValue(currentYear);
        linearLayout.addView(yearPicker, LayoutHelper.createLinear(0, -2, 0.4f));
        yearPicker.setOnValueChangedListener(new OnValueChangeListener() {
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                AlertsCreator.updateDayPicker(dayPicker, monthPicker, yearPicker);
            }
        });
        yearPicker.setOnScrollListener(new OnScrollListener() {
            public void onScrollStateChange(NumberPicker view, int scrollState) {
                if (checkMinDate && scrollState == 0) {
                    AlertsCreator.checkPickerDate(dayPicker, monthPicker, yearPicker);
                }
            }
        });
        updateDayPicker(dayPicker, monthPicker, yearPicker);
        if (checkMinDate) {
            checkPickerDate(dayPicker, monthPicker, yearPicker);
        }
        Builder builder = new Builder(context);
        builder.setTitle(title);
        builder.setView(linearLayout);
        final boolean z = checkMinDate;
        final DatePickerDelegate datePickerDelegate2 = datePickerDelegate;
        builder.setPositiveButton(LocaleController.getString("Set", R.string.Set), new OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if (z) {
                    AlertsCreator.checkPickerDate(dayPicker, monthPicker, yearPicker);
                }
                datePickerDelegate2.didSelectDate(yearPicker.getValue(), monthPicker.getValue(), dayPicker.getValue());
            }
        });
        builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
        return builder;
    }

    public static Dialog createMuteAlert(Context context, final long dialog_id) {
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
        builder.setItems(items, new OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                long flags;
                int untilTime = ConnectionsManager.getInstance(UserConfig.selectedAccount).getCurrentTime();
                if (i == 0) {
                    untilTime += 3600;
                } else if (i == 1) {
                    untilTime += 28800;
                } else if (i == 2) {
                    untilTime += 172800;
                } else if (i == 3) {
                    untilTime = ConnectionsManager.DEFAULT_DATACENTER_ID;
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
        });
        return builder.create();
    }

    public static Dialog createReportAlert(Context context, long dialog_id, int messageId, BaseFragment parentFragment) {
        if (context == null || parentFragment == null) {
            return null;
        }
        BottomSheet.Builder builder = new BottomSheet.Builder(context);
        builder.setTitle(LocaleController.getString("ReportChat", R.string.ReportChat));
        final long j = dialog_id;
        final int i = messageId;
        final BaseFragment baseFragment = parentFragment;
        final Context context2 = context;
        builder.setItems(new CharSequence[]{LocaleController.getString("ReportChatSpam", R.string.ReportChatSpam), LocaleController.getString("ReportChatViolence", R.string.ReportChatViolence), LocaleController.getString("ReportChatPornography", R.string.ReportChatPornography), LocaleController.getString("ReportChatOther", R.string.ReportChatOther)}, new OnClickListener() {

            /* renamed from: org.telegram.ui.Components.AlertsCreator$10$1 */
            class C21741 implements RequestDelegate {
                C21741() {
                }

                public void run(TLObject response, TL_error error) {
                }
            }

            public void onClick(DialogInterface dialogInterface, int i) {
                if (i == 3) {
                    Bundle args = new Bundle();
                    args.putLong("dialog_id", j);
                    args.putLong("message_id", (long) i);
                    baseFragment.presentFragment(new ReportOtherActivity(args));
                    return;
                }
                TLObject req;
                InputPeer peer = MessagesController.getInstance(UserConfig.selectedAccount).getInputPeer((int) j);
                TLObject request;
                if (i != 0) {
                    request = new TL_messages_report();
                    request.peer = peer;
                    request.id.add(Integer.valueOf(i));
                    if (i == 0) {
                        request.reason = new TL_inputReportReasonSpam();
                    } else if (i == 1) {
                        request.reason = new TL_inputReportReasonViolence();
                    } else if (i == 2) {
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
                        request.reason = new TL_inputReportReasonPornography();
                    }
                    req = request;
                }
                ConnectionsManager.getInstance(UserConfig.selectedAccount).sendRequest(req, new C21741());
                Toast.makeText(context2, LocaleController.getString("ReportChatSent", R.string.ReportChatSent), 0).show();
            }
        });
        return builder.create();
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
            }
            builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), null);
            fragment.showDialog(builder.create(), true, null);
        }
    }

    public static void showAddUserAlert(String error, final BaseFragment fragment, boolean isChannel) {
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
                    builder.setNegativeButton(LocaleController.getString("MoreInfo", R.string.MoreInfo), new OnClickListener() {
                        public void onClick(DialogInterface dialogInterface, int i) {
                            MessagesController.getInstance(fragment.getCurrentAccount()).openByUserName("spambot", fragment, 1);
                        }
                    });
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

    public static Dialog createColorSelectDialog(Activity parentActivity, long dialog_id, boolean globalGroup, boolean globalAll, Runnable onSelect) {
        int currentColor;
        SharedPreferences preferences = MessagesController.getNotificationsSettings(UserConfig.selectedAccount);
        if (globalGroup) {
            currentColor = preferences.getInt("GroupLed", -16776961);
        } else if (globalAll) {
            currentColor = preferences.getInt("MessagesLed", -16776961);
        } else {
            if (preferences.contains("color_" + dialog_id)) {
                currentColor = preferences.getInt("color_" + dialog_id, -16776961);
            } else if (((int) dialog_id) < 0) {
                currentColor = preferences.getInt("GroupLed", -16776961);
            } else {
                currentColor = preferences.getInt("MessagesLed", -16776961);
            }
        }
        View linearLayout = new LinearLayout(parentActivity);
        linearLayout.setOrientation(1);
        String[] descriptions = new String[]{LocaleController.getString("ColorRed", R.string.ColorRed), LocaleController.getString("ColorOrange", R.string.ColorOrange), LocaleController.getString("ColorYellow", R.string.ColorYellow), LocaleController.getString("ColorGreen", R.string.ColorGreen), LocaleController.getString("ColorCyan", R.string.ColorCyan), LocaleController.getString("ColorBlue", R.string.ColorBlue), LocaleController.getString("ColorViolet", R.string.ColorViolet), LocaleController.getString("ColorPink", R.string.ColorPink), LocaleController.getString("ColorWhite", R.string.ColorWhite)};
        final int[] selectedColor = new int[]{currentColor};
        for (int a = 0; a < 9; a++) {
            RadioColorCell cell = new RadioColorCell(parentActivity);
            cell.setPadding(AndroidUtilities.dp(4.0f), 0, AndroidUtilities.dp(4.0f), 0);
            cell.setTag(Integer.valueOf(a));
            cell.setCheckColor(TextColorCell.colors[a], TextColorCell.colors[a]);
            cell.setTextAndValue(descriptions[a], currentColor == TextColorCell.colorsToSave[a]);
            linearLayout.addView(cell);
            linearLayout = linearLayout;
            cell.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    int count = linearLayout.getChildCount();
                    for (int a = 0; a < count; a++) {
                        boolean z;
                        View cell = (RadioColorCell) linearLayout.getChildAt(a);
                        if (cell == v) {
                            z = true;
                        } else {
                            z = false;
                        }
                        cell.setChecked(z, true);
                    }
                    selectedColor[0] = TextColorCell.colorsToSave[((Integer) v.getTag()).intValue()];
                }
            });
        }
        Builder builder = new Builder((Context) parentActivity);
        builder.setTitle(LocaleController.getString("LedColor", R.string.LedColor));
        builder.setView(linearLayout);
        final boolean z = globalAll;
        final boolean z2 = globalGroup;
        final long j = dialog_id;
        final Runnable runnable = onSelect;
        builder.setPositiveButton(LocaleController.getString("Set", R.string.Set), new OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int which) {
                Editor editor = MessagesController.getNotificationsSettings(UserConfig.selectedAccount).edit();
                if (z) {
                    editor.putInt("MessagesLed", selectedColor[0]);
                } else if (z2) {
                    editor.putInt("GroupLed", selectedColor[0]);
                } else {
                    editor.putInt("color_" + j, selectedColor[0]);
                }
                editor.commit();
                if (runnable != null) {
                    runnable.run();
                }
            }
        });
        final boolean z3 = globalAll;
        final boolean z4 = globalGroup;
        final long j2 = dialog_id;
        final Runnable runnable2 = onSelect;
        builder.setNeutralButton(LocaleController.getString("LedDisabled", R.string.LedDisabled), new OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Editor editor = MessagesController.getNotificationsSettings(UserConfig.selectedAccount).edit();
                if (z3) {
                    editor.putInt("MessagesLed", 0);
                } else if (z4) {
                    editor.putInt("GroupLed", 0);
                } else {
                    editor.putInt("color_" + j2, 0);
                }
                editor.commit();
                if (runnable2 != null) {
                    runnable2.run();
                }
            }
        });
        if (!(globalAll || globalGroup)) {
            final long j3 = dialog_id;
            final Runnable runnable3 = onSelect;
            builder.setNegativeButton(LocaleController.getString("Default", R.string.Default), new OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    Editor editor = MessagesController.getNotificationsSettings(UserConfig.selectedAccount).edit();
                    editor.remove("color_" + j3);
                    editor.commit();
                    if (runnable3 != null) {
                        runnable3.run();
                    }
                }
            });
        }
        return builder.create();
    }

    public static Dialog createVibrationSelectDialog(Activity parentActivity, BaseFragment parentFragment, long dialog_id, boolean globalGroup, boolean globalAll, Runnable onSelect) {
        String prefix;
        if (dialog_id != 0) {
            prefix = "vibrate_";
        } else {
            prefix = globalGroup ? "vibrate_group" : "vibrate_messages";
        }
        return createVibrationSelectDialog(parentActivity, parentFragment, dialog_id, prefix, onSelect);
    }

    public static Dialog createVibrationSelectDialog(Activity parentActivity, BaseFragment parentFragment, long dialog_id, String prefKeyPrefix, Runnable onSelect) {
        String[] descriptions;
        SharedPreferences preferences = MessagesController.getNotificationsSettings(UserConfig.selectedAccount);
        final int[] selected = new int[1];
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
        int a = 0;
        while (a < descriptions.length) {
            RadioColorCell cell = new RadioColorCell(parentActivity);
            cell.setPadding(AndroidUtilities.dp(4.0f), 0, AndroidUtilities.dp(4.0f), 0);
            cell.setTag(Integer.valueOf(a));
            cell.setCheckColor(Theme.getColor(Theme.key_radioBackground), Theme.getColor(Theme.key_dialogRadioBackgroundChecked));
            cell.setTextAndValue(descriptions[a], selected[0] == a);
            linearLayout.addView(cell);
            final long j = dialog_id;
            final String str = prefKeyPrefix;
            final BaseFragment baseFragment = parentFragment;
            final Runnable runnable = onSelect;
            cell.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    selected[0] = ((Integer) v.getTag()).intValue();
                    Editor editor = MessagesController.getNotificationsSettings(UserConfig.selectedAccount).edit();
                    if (j != 0) {
                        if (selected[0] == 0) {
                            editor.putInt(str + j, 0);
                        } else if (selected[0] == 1) {
                            editor.putInt(str + j, 1);
                        } else if (selected[0] == 2) {
                            editor.putInt(str + j, 3);
                        } else if (selected[0] == 3) {
                            editor.putInt(str + j, 2);
                        }
                    } else if (selected[0] == 0) {
                        editor.putInt(str, 2);
                    } else if (selected[0] == 1) {
                        editor.putInt(str, 0);
                    } else if (selected[0] == 2) {
                        editor.putInt(str, 1);
                    } else if (selected[0] == 3) {
                        editor.putInt(str, 3);
                    } else if (selected[0] == 4) {
                        editor.putInt(str, 4);
                    }
                    editor.commit();
                    if (baseFragment != null) {
                        baseFragment.dismissCurrentDialig();
                    }
                    if (runnable != null) {
                        runnable.run();
                    }
                }
            });
            a++;
        }
        Builder builder = new Builder((Context) parentActivity);
        builder.setTitle(LocaleController.getString("Vibrate", R.string.Vibrate));
        builder.setView(linearLayout);
        builder.setPositiveButton(LocaleController.getString("Cancel", R.string.Cancel), null);
        return builder.create();
    }

    public static Dialog createLocationUpdateDialog(Activity parentActivity, User user, IntCallback callback) {
        final int[] selected = new int[1];
        String[] descriptions = new String[]{LocaleController.getString("SendLiveLocationFor15m", R.string.SendLiveLocationFor15m), LocaleController.getString("SendLiveLocationFor1h", R.string.SendLiveLocationFor1h), LocaleController.getString("SendLiveLocationFor8h", R.string.SendLiveLocationFor8h)};
        final LinearLayout linearLayout = new LinearLayout(parentActivity);
        linearLayout.setOrientation(1);
        TextView titleTextView = new TextView(parentActivity);
        if (user != null) {
            titleTextView.setText(LocaleController.formatString("LiveLocationAlertPrivate", R.string.LiveLocationAlertPrivate, UserObject.getFirstName(user)));
        } else {
            titleTextView.setText(LocaleController.getString("LiveLocationAlertGroup", R.string.LiveLocationAlertGroup));
        }
        titleTextView.setTextColor(Theme.getColor(Theme.key_dialogTextBlack));
        titleTextView.setTextSize(1, 16.0f);
        titleTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
        linearLayout.addView(titleTextView, LayoutHelper.createLinear(-2, -2, (LocaleController.isRTL ? 5 : 3) | 48, 24, 0, 24, 8));
        int a = 0;
        while (a < descriptions.length) {
            RadioColorCell cell = new RadioColorCell(parentActivity);
            cell.setPadding(AndroidUtilities.dp(4.0f), 0, AndroidUtilities.dp(4.0f), 0);
            cell.setTag(Integer.valueOf(a));
            cell.setCheckColor(Theme.getColor(Theme.key_radioBackground), Theme.getColor(Theme.key_dialogRadioBackgroundChecked));
            cell.setTextAndValue(descriptions[a], selected[0] == a);
            linearLayout.addView(cell);
            cell.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    selected[0] = ((Integer) v.getTag()).intValue();
                    int count = linearLayout.getChildCount();
                    for (int a = 0; a < count; a++) {
                        View child = linearLayout.getChildAt(a);
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
            });
            a++;
        }
        Builder builder = new Builder((Context) parentActivity);
        builder.setTopImage(new ShareLocationDrawable(parentActivity, false), Theme.getColor(Theme.key_dialogTopBackground));
        builder.setView(linearLayout);
        final IntCallback intCallback = callback;
        builder.setPositiveButton(LocaleController.getString("ShareFile", R.string.ShareFile), new OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                int time;
                if (selected[0] == 0) {
                    time = 900;
                } else if (selected[0] == 1) {
                    time = 3600;
                } else {
                    time = 28800;
                }
                intCallback.run(time);
            }
        });
        builder.setNeutralButton(LocaleController.getString("Cancel", R.string.Cancel), null);
        return builder.create();
    }

    public static Builder createContactsPermissionDialog(Activity parentActivity, final IntCallback callback) {
        Builder builder = new Builder((Context) parentActivity);
        builder.setTopImage((int) R.drawable.permissions_contacts, Theme.getColor(Theme.key_dialogTopBackground));
        builder.setMessage(AndroidUtilities.replaceTags(LocaleController.getString("ContactsPermissionAlert", R.string.ContactsPermissionAlert)));
        builder.setPositiveButton(LocaleController.getString("ContactsPermissionAlertContinue", R.string.ContactsPermissionAlertContinue), new OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                callback.run(1);
            }
        });
        builder.setNegativeButton(LocaleController.getString("ContactsPermissionAlertNotNow", R.string.ContactsPermissionAlertNotNow), new OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                callback.run(0);
            }
        });
        return builder;
    }

    public static Dialog createFreeSpaceDialog(LaunchActivity parentActivity) {
        final int[] selected = new int[1];
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
        final LinearLayout linearLayout = new LinearLayout(parentActivity);
        linearLayout.setOrientation(1);
        View titleTextView = new TextView(parentActivity);
        titleTextView.setText(LocaleController.getString("LowDiskSpaceTitle2", R.string.LowDiskSpaceTitle2));
        titleTextView.setTextColor(Theme.getColor(Theme.key_dialogTextBlack));
        titleTextView.setTextSize(1, 16.0f);
        titleTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        titleTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
        linearLayout.addView(titleTextView, LayoutHelper.createLinear(-2, -2, (LocaleController.isRTL ? 5 : 3) | 48, 24, 0, 24, 8));
        int a = 0;
        while (a < descriptions.length) {
            RadioColorCell cell = new RadioColorCell(parentActivity);
            cell.setPadding(AndroidUtilities.dp(4.0f), 0, AndroidUtilities.dp(4.0f), 0);
            cell.setTag(Integer.valueOf(a));
            cell.setCheckColor(Theme.getColor(Theme.key_radioBackground), Theme.getColor(Theme.key_dialogRadioBackgroundChecked));
            cell.setTextAndValue(descriptions[a], selected[0] == a);
            linearLayout.addView(cell);
            cell.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
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
                    for (int a = 0; a < count; a++) {
                        View child = linearLayout.getChildAt(a);
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
            });
            a++;
        }
        Builder builder = new Builder((Context) parentActivity);
        builder.setTitle(LocaleController.getString("LowDiskSpaceTitle", R.string.LowDiskSpaceTitle));
        builder.setMessage(LocaleController.getString("LowDiskSpaceMessage", R.string.LowDiskSpaceMessage));
        builder.setView(linearLayout);
        builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), new OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                MessagesController.getGlobalMainSettings().edit().putInt("keep_media", selected[0]).commit();
            }
        });
        final LaunchActivity launchActivity = parentActivity;
        builder.setNeutralButton(LocaleController.getString("ClearMediaCache", R.string.ClearMediaCache), new OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                launchActivity.presentFragment(new CacheControlActivity());
            }
        });
        return builder.create();
    }

    public static Dialog createPrioritySelectDialog(Activity parentActivity, BaseFragment parentFragment, long dialog_id, boolean globalGroup, boolean globalAll, Runnable onSelect) {
        String[] descriptions;
        SharedPreferences preferences = MessagesController.getNotificationsSettings(UserConfig.selectedAccount);
        final int[] selected = new int[1];
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
            if (globalAll) {
                selected[0] = preferences.getInt("priority_messages", 1);
            } else if (globalGroup) {
                selected[0] = preferences.getInt("priority_group", 1);
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
        int a = 0;
        while (a < descriptions.length) {
            RadioColorCell cell = new RadioColorCell(parentActivity);
            cell.setPadding(AndroidUtilities.dp(4.0f), 0, AndroidUtilities.dp(4.0f), 0);
            cell.setTag(Integer.valueOf(a));
            cell.setCheckColor(Theme.getColor(Theme.key_radioBackground), Theme.getColor(Theme.key_dialogRadioBackgroundChecked));
            cell.setTextAndValue(descriptions[a], selected[0] == a);
            linearLayout.addView(cell);
            final long j = dialog_id;
            final boolean z = globalGroup;
            final BaseFragment baseFragment = parentFragment;
            final Runnable runnable = onSelect;
            cell.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    selected[0] = ((Integer) v.getTag()).intValue();
                    Editor editor = MessagesController.getNotificationsSettings(UserConfig.selectedAccount).edit();
                    int option;
                    if (j != 0) {
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
                        editor.putInt("priority_" + j, option);
                    } else {
                        String str;
                        if (selected[0] == 0) {
                            option = 4;
                        } else if (selected[0] == 1) {
                            option = 5;
                        } else if (selected[0] == 2) {
                            option = 0;
                        } else {
                            option = 1;
                        }
                        if (z) {
                            str = "priority_group";
                        } else {
                            str = "priority_messages";
                        }
                        editor.putInt(str, option);
                    }
                    editor.commit();
                    if (baseFragment != null) {
                        baseFragment.dismissCurrentDialig();
                    }
                    if (runnable != null) {
                        runnable.run();
                    }
                }
            });
            a++;
        }
        Builder builder = new Builder((Context) parentActivity);
        builder.setTitle(LocaleController.getString("NotificationsImportance", R.string.NotificationsImportance));
        builder.setView(linearLayout);
        builder.setPositiveButton(LocaleController.getString("Cancel", R.string.Cancel), null);
        return builder.create();
    }

    public static Dialog createPopupSelectDialog(Activity parentActivity, final BaseFragment parentFragment, final boolean globalGroup, boolean globalAll, final Runnable onSelect) {
        SharedPreferences preferences = MessagesController.getNotificationsSettings(UserConfig.selectedAccount);
        final int[] selected = new int[1];
        if (globalAll) {
            selected[0] = preferences.getInt("popupAll", 0);
        } else if (globalGroup) {
            selected[0] = preferences.getInt("popupGroup", 0);
        }
        String[] descriptions = new String[]{LocaleController.getString("NoPopup", R.string.NoPopup), LocaleController.getString("OnlyWhenScreenOn", R.string.OnlyWhenScreenOn), LocaleController.getString("OnlyWhenScreenOff", R.string.OnlyWhenScreenOff), LocaleController.getString("AlwaysShowPopup", R.string.AlwaysShowPopup)};
        LinearLayout linearLayout = new LinearLayout(parentActivity);
        linearLayout.setOrientation(1);
        int a = 0;
        while (a < descriptions.length) {
            RadioColorCell cell = new RadioColorCell(parentActivity);
            cell.setTag(Integer.valueOf(a));
            cell.setPadding(AndroidUtilities.dp(4.0f), 0, AndroidUtilities.dp(4.0f), 0);
            cell.setCheckColor(Theme.getColor(Theme.key_radioBackground), Theme.getColor(Theme.key_dialogRadioBackgroundChecked));
            cell.setTextAndValue(descriptions[a], selected[0] == a);
            linearLayout.addView(cell);
            cell.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    selected[0] = ((Integer) v.getTag()).intValue();
                    Editor editor = MessagesController.getNotificationsSettings(UserConfig.selectedAccount).edit();
                    editor.putInt(globalGroup ? "popupGroup" : "popupAll", selected[0]);
                    editor.commit();
                    if (parentFragment != null) {
                        parentFragment.dismissCurrentDialig();
                    }
                    if (onSelect != null) {
                        onSelect.run();
                    }
                }
            });
            a++;
        }
        Builder builder = new Builder((Context) parentActivity);
        builder.setTitle(LocaleController.getString("PopupNotification", R.string.PopupNotification));
        builder.setView(linearLayout);
        builder.setPositiveButton(LocaleController.getString("Cancel", R.string.Cancel), null);
        return builder.create();
    }

    public static Dialog createSingleChoiceDialog(Activity parentActivity, final BaseFragment parentFragment, String[] options, String title, int selected, final OnClickListener listener) {
        LinearLayout linearLayout = new LinearLayout(parentActivity);
        linearLayout.setOrientation(1);
        Builder builder = new Builder((Context) parentActivity);
        for (int a = 0; a < options.length; a++) {
            boolean z;
            RadioColorCell cell = new RadioColorCell(parentActivity);
            cell.setPadding(AndroidUtilities.dp(4.0f), 0, AndroidUtilities.dp(4.0f), 0);
            cell.setTag(Integer.valueOf(a));
            cell.setCheckColor(Theme.getColor(Theme.key_radioBackground), Theme.getColor(Theme.key_dialogRadioBackgroundChecked));
            String str = options[a];
            if (selected == a) {
                z = true;
            } else {
                z = false;
            }
            cell.setTextAndValue(str, z);
            linearLayout.addView(cell);
            cell.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    int sel = ((Integer) v.getTag()).intValue();
                    if (parentFragment != null) {
                        parentFragment.dismissCurrentDialig();
                    }
                    listener.onClick(null, sel);
                }
            });
        }
        builder.setTitle(title);
        builder.setView(linearLayout);
        builder.setPositiveButton(LocaleController.getString("Cancel", R.string.Cancel), null);
        return builder.create();
    }

    public static Builder createTTLAlert(Context context, final EncryptedChat encryptedChat) {
        Builder builder = new Builder(context);
        builder.setTitle(LocaleController.getString("MessageLifetime", R.string.MessageLifetime));
        final NumberPicker numberPicker = new NumberPicker(context);
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
        numberPicker.setFormatter(new Formatter() {
            public String format(int value) {
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
                return TtmlNode.ANONYMOUS_REGION_ID;
            }
        });
        builder.setView(numberPicker);
        builder.setNegativeButton(LocaleController.getString("Done", R.string.Done), new OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
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
        });
        return builder;
    }

    public static AlertDialog createAccountSelectDialog(Activity parentActivity, final AccountSelectDelegate delegate) {
        if (UserConfig.getActivatedAccountsCount() < 2) {
            return null;
        }
        Builder builder = new Builder((Context) parentActivity);
        final Runnable dismissRunnable = builder.getDismissRunnable();
        LinearLayout linearLayout = new LinearLayout(parentActivity);
        linearLayout.setOrientation(1);
        for (int a = 0; a < 3; a++) {
            if (UserConfig.getInstance(a).getCurrentUser() != null) {
                AccountSelectCell cell = new AccountSelectCell(parentActivity);
                cell.setAccount(a, false);
                cell.setPadding(AndroidUtilities.dp(14.0f), 0, AndroidUtilities.dp(14.0f), 0);
                cell.setBackgroundDrawable(Theme.getSelectorDrawable(false));
                linearLayout.addView(cell, LayoutHelper.createLinear(-1, 48));
                cell.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        dismissRunnable.run();
                        delegate.didSelectAccount(((AccountSelectCell) v).getAccountNumber());
                    }
                });
            }
        }
        builder.setTitle(LocaleController.getString("SelectAccount", R.string.SelectAccount));
        builder.setView(linearLayout);
        builder.setPositiveButton(LocaleController.getString("Cancel", R.string.Cancel), null);
        return builder.create();
    }
}
