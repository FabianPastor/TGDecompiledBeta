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
import org.telegram.messenger.AndroidUtilities;
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
import org.telegram.messenger.exoplayer2.C0539C;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.EncryptedChat;
import org.telegram.tgnet.TLRPC.InputPeer;
import org.telegram.tgnet.TLRPC.TL_account_changePhone;
import org.telegram.tgnet.TLRPC.TL_account_confirmPhone;
import org.telegram.tgnet.TLRPC.TL_account_getPassword;
import org.telegram.tgnet.TLRPC.TL_account_getTmpPassword;
import org.telegram.tgnet.TLRPC.TL_account_reportPeer;
import org.telegram.tgnet.TLRPC.TL_account_sendChangePhoneCode;
import org.telegram.tgnet.TLRPC.TL_account_sendConfirmPhoneCode;
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
import org.telegram.ui.ActionBar.AlertDialog.Builder;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.CacheControlActivity;
import org.telegram.ui.Cells.RadioColorCell;
import org.telegram.ui.Cells.TextColorCell;
import org.telegram.ui.Components.NumberPicker.Formatter;
import org.telegram.ui.LaunchActivity;
import org.telegram.ui.ReportOtherActivity;

public class AlertsCreator {

    public interface PaymentAlertDelegate {
        void didPressedNewCard();
    }

    public static Dialog processError(int currentAccount, TL_error error, BaseFragment fragment, TLObject request, Object... args) {
        if (error.code != 406) {
            if (error.text != null) {
                int i = 1;
                boolean z = false;
                if (!((request instanceof TL_channels_joinChannel) || (request instanceof TL_channels_editAdmin) || (request instanceof TL_channels_inviteToChannel) || (request instanceof TL_messages_addChatUser) || (request instanceof TL_messages_startBot))) {
                    if (!(request instanceof TL_channels_editBanned)) {
                        if (request instanceof TL_messages_createChat) {
                            if (error.text.startsWith("FLOOD_WAIT")) {
                                showFloodWaitAlert(error.text, fragment);
                            } else {
                                showAddUserAlert(error.text, fragment, false);
                            }
                        } else if (request instanceof TL_channels_createChannel) {
                            if (error.text.startsWith("FLOOD_WAIT")) {
                                showFloodWaitAlert(error.text, fragment);
                            } else {
                                showAddUserAlert(error.text, fragment, false);
                            }
                        } else if (!(request instanceof TL_messages_editMessage)) {
                            if (!((request instanceof TL_messages_sendMessage) || (request instanceof TL_messages_sendMedia) || (request instanceof TL_messages_sendBroadcast) || (request instanceof TL_messages_sendInlineBotResult))) {
                                if (!(request instanceof TL_messages_forwardMessages)) {
                                    if (request instanceof TL_messages_importChatInvite) {
                                        if (error.text.startsWith("FLOOD_WAIT")) {
                                            showSimpleAlert(fragment, LocaleController.getString("FloodWait", R.string.FloodWait));
                                        } else if (error.text.equals("USERS_TOO_MUCH")) {
                                            showSimpleAlert(fragment, LocaleController.getString("JoinToGroupErrorFull", R.string.JoinToGroupErrorFull));
                                        } else {
                                            showSimpleAlert(fragment, LocaleController.getString("JoinToGroupErrorNotExist", R.string.JoinToGroupErrorNotExist));
                                        }
                                    } else if (request instanceof TL_messages_getAttachedStickers) {
                                        if (!(fragment == null || fragment.getParentActivity() == null)) {
                                            Context parentActivity = fragment.getParentActivity();
                                            StringBuilder stringBuilder = new StringBuilder();
                                            stringBuilder.append(LocaleController.getString("ErrorOccurred", R.string.ErrorOccurred));
                                            stringBuilder.append("\n");
                                            stringBuilder.append(error.text);
                                            Toast.makeText(parentActivity, stringBuilder.toString(), 0).show();
                                        }
                                    } else if (request instanceof TL_account_confirmPhone) {
                                        if (!error.text.contains("PHONE_CODE_EMPTY")) {
                                            if (!error.text.contains("PHONE_CODE_INVALID")) {
                                                if (error.text.contains("PHONE_CODE_EXPIRED")) {
                                                    showSimpleAlert(fragment, LocaleController.getString("CodeExpired", R.string.CodeExpired));
                                                } else if (error.text.startsWith("FLOOD_WAIT")) {
                                                    showSimpleAlert(fragment, LocaleController.getString("FloodWait", R.string.FloodWait));
                                                } else {
                                                    showSimpleAlert(fragment, error.text);
                                                }
                                            }
                                        }
                                        showSimpleAlert(fragment, LocaleController.getString("InvalidCode", R.string.InvalidCode));
                                    } else if (request instanceof TL_auth_resendCode) {
                                        if (error.text.contains("PHONE_NUMBER_INVALID")) {
                                            showSimpleAlert(fragment, LocaleController.getString("InvalidPhoneNumber", R.string.InvalidPhoneNumber));
                                        } else {
                                            if (!error.text.contains("PHONE_CODE_EMPTY")) {
                                                if (!error.text.contains("PHONE_CODE_INVALID")) {
                                                    if (error.text.contains("PHONE_CODE_EXPIRED")) {
                                                        showSimpleAlert(fragment, LocaleController.getString("CodeExpired", R.string.CodeExpired));
                                                    } else if (error.text.startsWith("FLOOD_WAIT")) {
                                                        showSimpleAlert(fragment, LocaleController.getString("FloodWait", R.string.FloodWait));
                                                    } else if (error.code != C0539C.PRIORITY_DOWNLOAD) {
                                                        r0 = new StringBuilder();
                                                        r0.append(LocaleController.getString("ErrorOccurred", R.string.ErrorOccurred));
                                                        r0.append("\n");
                                                        r0.append(error.text);
                                                        showSimpleAlert(fragment, r0.toString());
                                                    }
                                                }
                                            }
                                            showSimpleAlert(fragment, LocaleController.getString("InvalidCode", R.string.InvalidCode));
                                        }
                                    } else if (request instanceof TL_account_sendConfirmPhoneCode) {
                                        if (error.code == 400) {
                                            return showSimpleAlert(fragment, LocaleController.getString("CancelLinkExpired", R.string.CancelLinkExpired));
                                        }
                                        if (error.text != null) {
                                            if (error.text.startsWith("FLOOD_WAIT")) {
                                                return showSimpleAlert(fragment, LocaleController.getString("FloodWait", R.string.FloodWait));
                                            }
                                            return showSimpleAlert(fragment, LocaleController.getString("ErrorOccurred", R.string.ErrorOccurred));
                                        }
                                    } else if (request instanceof TL_account_changePhone) {
                                        if (error.text.contains("PHONE_NUMBER_INVALID")) {
                                            showSimpleAlert(fragment, LocaleController.getString("InvalidPhoneNumber", R.string.InvalidPhoneNumber));
                                        } else {
                                            if (!error.text.contains("PHONE_CODE_EMPTY")) {
                                                if (!error.text.contains("PHONE_CODE_INVALID")) {
                                                    if (error.text.contains("PHONE_CODE_EXPIRED")) {
                                                        showSimpleAlert(fragment, LocaleController.getString("CodeExpired", R.string.CodeExpired));
                                                    } else if (error.text.startsWith("FLOOD_WAIT")) {
                                                        showSimpleAlert(fragment, LocaleController.getString("FloodWait", R.string.FloodWait));
                                                    } else {
                                                        showSimpleAlert(fragment, error.text);
                                                    }
                                                }
                                            }
                                            showSimpleAlert(fragment, LocaleController.getString("InvalidCode", R.string.InvalidCode));
                                        }
                                    } else if (request instanceof TL_account_sendChangePhoneCode) {
                                        if (error.text.contains("PHONE_NUMBER_INVALID")) {
                                            showSimpleAlert(fragment, LocaleController.getString("InvalidPhoneNumber", R.string.InvalidPhoneNumber));
                                        } else {
                                            if (!error.text.contains("PHONE_CODE_EMPTY")) {
                                                if (!error.text.contains("PHONE_CODE_INVALID")) {
                                                    if (error.text.contains("PHONE_CODE_EXPIRED")) {
                                                        showSimpleAlert(fragment, LocaleController.getString("CodeExpired", R.string.CodeExpired));
                                                    } else if (error.text.startsWith("FLOOD_WAIT")) {
                                                        showSimpleAlert(fragment, LocaleController.getString("FloodWait", R.string.FloodWait));
                                                    } else if (error.text.startsWith("PHONE_NUMBER_OCCUPIED")) {
                                                        showSimpleAlert(fragment, LocaleController.formatString("ChangePhoneNumberOccupied", R.string.ChangePhoneNumberOccupied, (String) args[0]));
                                                    } else {
                                                        showSimpleAlert(fragment, LocaleController.getString("ErrorOccurred", R.string.ErrorOccurred));
                                                    }
                                                }
                                            }
                                            showSimpleAlert(fragment, LocaleController.getString("InvalidCode", R.string.InvalidCode));
                                        }
                                    } else if (request instanceof TL_updateUserName) {
                                        r0 = error.text;
                                        r4 = r0.hashCode();
                                        if (r4 == 288843630) {
                                            if (r0.equals("USERNAME_INVALID")) {
                                                i = 0;
                                                switch (i) {
                                                    case 0:
                                                        showSimpleAlert(fragment, LocaleController.getString("UsernameInvalid", R.string.UsernameInvalid));
                                                        break;
                                                    case 1:
                                                        showSimpleAlert(fragment, LocaleController.getString("UsernameInUse", R.string.UsernameInUse));
                                                        break;
                                                    default:
                                                        showSimpleAlert(fragment, LocaleController.getString("ErrorOccurred", R.string.ErrorOccurred));
                                                        break;
                                                }
                                            }
                                        } else if (r4 == 533175271) {
                                            if (r0.equals("USERNAME_OCCUPIED")) {
                                                switch (i) {
                                                    case 0:
                                                        showSimpleAlert(fragment, LocaleController.getString("UsernameInvalid", R.string.UsernameInvalid));
                                                        break;
                                                    case 1:
                                                        showSimpleAlert(fragment, LocaleController.getString("UsernameInUse", R.string.UsernameInUse));
                                                        break;
                                                    default:
                                                        showSimpleAlert(fragment, LocaleController.getString("ErrorOccurred", R.string.ErrorOccurred));
                                                        break;
                                                }
                                            }
                                        }
                                        i = -1;
                                        switch (i) {
                                            case 0:
                                                showSimpleAlert(fragment, LocaleController.getString("UsernameInvalid", R.string.UsernameInvalid));
                                                break;
                                            case 1:
                                                showSimpleAlert(fragment, LocaleController.getString("UsernameInUse", R.string.UsernameInUse));
                                                break;
                                            default:
                                                showSimpleAlert(fragment, LocaleController.getString("ErrorOccurred", R.string.ErrorOccurred));
                                                break;
                                        }
                                    } else if (request instanceof TL_contacts_importContacts) {
                                        if (error != null) {
                                            if (!error.text.startsWith("FLOOD_WAIT")) {
                                                r0 = new StringBuilder();
                                                r0.append(LocaleController.getString("ErrorOccurred", R.string.ErrorOccurred));
                                                r0.append("\n");
                                                r0.append(error.text);
                                                showSimpleAlert(fragment, r0.toString());
                                            }
                                        }
                                        showSimpleAlert(fragment, LocaleController.getString("FloodWait", R.string.FloodWait));
                                    } else {
                                        if (!(request instanceof TL_account_getPassword)) {
                                            if (!(request instanceof TL_account_getTmpPassword)) {
                                                if (request instanceof TL_payments_sendPaymentForm) {
                                                    r0 = error.text;
                                                    r4 = r0.hashCode();
                                                    if (r4 == -NUM) {
                                                        if (r0.equals("BOT_PRECHECKOUT_FAILED")) {
                                                            i = 0;
                                                            switch (i) {
                                                                case 0:
                                                                    showSimpleToast(fragment, LocaleController.getString("PaymentPrecheckoutFailed", R.string.PaymentPrecheckoutFailed));
                                                                    break;
                                                                case 1:
                                                                    showSimpleToast(fragment, LocaleController.getString("PaymentFailed", R.string.PaymentFailed));
                                                                    break;
                                                                default:
                                                                    showSimpleToast(fragment, error.text);
                                                                    break;
                                                            }
                                                        }
                                                    } else if (r4 == -784238410) {
                                                        if (r0.equals("PAYMENT_FAILED")) {
                                                            switch (i) {
                                                                case 0:
                                                                    showSimpleToast(fragment, LocaleController.getString("PaymentPrecheckoutFailed", R.string.PaymentPrecheckoutFailed));
                                                                    break;
                                                                case 1:
                                                                    showSimpleToast(fragment, LocaleController.getString("PaymentFailed", R.string.PaymentFailed));
                                                                    break;
                                                                default:
                                                                    showSimpleToast(fragment, error.text);
                                                                    break;
                                                            }
                                                        }
                                                    }
                                                    i = -1;
                                                    switch (i) {
                                                        case 0:
                                                            showSimpleToast(fragment, LocaleController.getString("PaymentPrecheckoutFailed", R.string.PaymentPrecheckoutFailed));
                                                            break;
                                                        case 1:
                                                            showSimpleToast(fragment, LocaleController.getString("PaymentFailed", R.string.PaymentFailed));
                                                            break;
                                                        default:
                                                            showSimpleToast(fragment, error.text);
                                                            break;
                                                    }
                                                } else if (request instanceof TL_payments_validateRequestedInfo) {
                                                    r0 = error.text;
                                                    if (r0.hashCode() == NUM) {
                                                        if (r0.equals("SHIPPING_NOT_AVAILABLE")) {
                                                            if (z) {
                                                                showSimpleToast(fragment, LocaleController.getString("PaymentNoShippingMethod", R.string.PaymentNoShippingMethod));
                                                            } else {
                                                                showSimpleToast(fragment, error.text);
                                                            }
                                                        }
                                                    }
                                                    z = true;
                                                    if (z) {
                                                        showSimpleToast(fragment, LocaleController.getString("PaymentNoShippingMethod", R.string.PaymentNoShippingMethod));
                                                    } else {
                                                        showSimpleToast(fragment, error.text);
                                                    }
                                                }
                                            }
                                        }
                                        if (error.text.startsWith("FLOOD_WAIT")) {
                                            showSimpleToast(fragment, getFloodWaitString(error.text));
                                        } else {
                                            showSimpleToast(fragment, error.text);
                                        }
                                    }
                                }
                            }
                            if (error.text.equals("PEER_FLOOD")) {
                                NotificationCenter.getInstance(currentAccount).postNotificationName(NotificationCenter.needShowAlert, Integer.valueOf(0));
                            }
                        } else if (!error.text.equals("MESSAGE_NOT_MODIFIED")) {
                            showSimpleAlert(fragment, LocaleController.getString("EditMessageError", R.string.EditMessageError));
                        }
                        return null;
                    }
                }
                if (fragment != null) {
                    showAddUserAlert(error.text, fragment, ((Boolean) args[0]).booleanValue());
                } else if (error.text.equals("PEER_FLOOD")) {
                    NotificationCenter.getInstance(currentAccount).postNotificationName(NotificationCenter.needShowAlert, Integer.valueOf(1));
                }
                return null;
            }
        }
        return null;
    }

    public static Toast showSimpleToast(BaseFragment baseFragment, String text) {
        if (!(text == null || baseFragment == null)) {
            if (baseFragment.getParentActivity() != null) {
                Toast toast = Toast.makeText(baseFragment.getParentActivity(), text, 1);
                toast.show();
                return toast;
            }
        }
        return null;
    }

    public static Dialog showSimpleAlert(BaseFragment baseFragment, String text) {
        if (!(text == null || baseFragment == null)) {
            if (baseFragment.getParentActivity() != null) {
                Builder builder = new Builder(baseFragment.getParentActivity());
                builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
                builder.setMessage(text);
                builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), null);
                Dialog dialog = builder.create();
                baseFragment.showDialog(dialog);
                return dialog;
            }
        }
        return null;
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
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("notify2_");
                    stringBuilder.append(dialog_id);
                    editor.putInt(stringBuilder.toString(), 2);
                    flags = 1;
                } else {
                    StringBuilder stringBuilder2 = new StringBuilder();
                    stringBuilder2.append("notify2_");
                    stringBuilder2.append(dialog_id);
                    editor.putInt(stringBuilder2.toString(), 3);
                    stringBuilder2 = new StringBuilder();
                    stringBuilder2.append("notifyuntil_");
                    stringBuilder2.append(dialog_id);
                    editor.putInt(stringBuilder2.toString(), untilTime);
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
        if (context != null) {
            if (parentFragment != null) {
                BottomSheet.Builder builder = new BottomSheet.Builder(context);
                builder.setTitle(LocaleController.getString("ReportChat", R.string.ReportChat));
                final long j = dialog_id;
                final int i = messageId;
                final BaseFragment baseFragment = parentFragment;
                final Context context2 = context;
                builder.setItems(new CharSequence[]{LocaleController.getString("ReportChatSpam", R.string.ReportChatSpam), LocaleController.getString("ReportChatViolence", R.string.ReportChatViolence), LocaleController.getString("ReportChatPornography", R.string.ReportChatPornography), LocaleController.getString("ReportChatOther", R.string.ReportChatOther)}, new OnClickListener() {

                    /* renamed from: org.telegram.ui.Components.AlertsCreator$2$1 */
                    class C20301 implements RequestDelegate {
                        C20301() {
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
                        if (i != 0) {
                            req = new TL_messages_report();
                            req.peer = peer;
                            req.id.add(Integer.valueOf(i));
                            if (i == 0) {
                                req.reason = new TL_inputReportReasonSpam();
                            } else if (i == 1) {
                                req.reason = new TL_inputReportReasonViolence();
                            } else if (i == 2) {
                                req.reason = new TL_inputReportReasonPornography();
                            }
                        } else {
                            req = new TL_account_reportPeer();
                            req.peer = peer;
                            if (i == 0) {
                                req.reason = new TL_inputReportReasonSpam();
                            } else if (i == 1) {
                                req.reason = new TL_inputReportReasonViolence();
                            } else if (i == 2) {
                                req.reason = new TL_inputReportReasonPornography();
                            }
                        }
                        ConnectionsManager.getInstance(UserConfig.selectedAccount).sendRequest(req, new C20301());
                        Toast.makeText(context2, LocaleController.getString("ReportChatSent", R.string.ReportChatSent), 0).show();
                    }
                });
                return builder.create();
            }
        }
        return null;
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
        if (!(error == null || !error.startsWith("FLOOD_WAIT") || fragment == null)) {
            if (fragment.getParentActivity() != null) {
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
        if (!(error == null || fragment == null)) {
            if (fragment.getParentActivity() != null) {
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
                    default:
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
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append(LocaleController.getString("ErrorOccurred", R.string.ErrorOccurred));
                        stringBuilder.append("\n");
                        stringBuilder.append(error);
                        builder.setMessage(stringBuilder.toString());
                        break;
                }
                builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), null);
                fragment.showDialog(builder.create(), true, null);
            }
        }
    }

    public static Dialog createColorSelectDialog(Activity parentActivity, long dialog_id, boolean globalGroup, boolean globalAll, Runnable onSelect) {
        int currentColor;
        int currentColor2;
        final LinearLayout linearLayout;
        String[] descriptions;
        final int[] selectedColor;
        int a;
        Builder builder;
        final boolean z;
        final int[] iArr;
        C10695 c10695;
        C10695 c106952;
        final boolean z2;
        Builder builder2;
        String string;
        final long j;
        final boolean z3;
        final long j2;
        final Runnable runnable;
        Runnable runnable2;
        Context context = parentActivity;
        final long j3 = dialog_id;
        SharedPreferences preferences = MessagesController.getNotificationsSettings(UserConfig.selectedAccount);
        if (globalGroup) {
            currentColor = preferences.getInt("GroupLed", -16776961);
        } else if (globalAll) {
            currentColor = preferences.getInt("MessagesLed", -16776961);
        } else {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("color_");
            stringBuilder.append(j3);
            if (preferences.contains(stringBuilder.toString())) {
                stringBuilder = new StringBuilder();
                stringBuilder.append("color_");
                stringBuilder.append(j3);
                currentColor = preferences.getInt(stringBuilder.toString(), -16776961);
            } else if (((int) j3) < 0) {
                currentColor = preferences.getInt("GroupLed", -16776961);
            } else {
                currentColor = preferences.getInt("MessagesLed", -16776961);
                currentColor2 = currentColor;
                linearLayout = new LinearLayout(context);
                linearLayout.setOrientation(1);
                descriptions = new String[]{LocaleController.getString("ColorRed", R.string.ColorRed), LocaleController.getString("ColorOrange", R.string.ColorOrange), LocaleController.getString("ColorYellow", R.string.ColorYellow), LocaleController.getString("ColorGreen", R.string.ColorGreen), LocaleController.getString("ColorCyan", R.string.ColorCyan), LocaleController.getString("ColorBlue", R.string.ColorBlue), LocaleController.getString("ColorViolet", R.string.ColorViolet), LocaleController.getString("ColorPink", R.string.ColorPink), LocaleController.getString("ColorWhite", R.string.ColorWhite)};
                selectedColor = new int[]{currentColor2};
                for (a = 0; a < 9; a++) {
                    RadioColorCell cell = new RadioColorCell(context);
                    cell.setPadding(AndroidUtilities.dp(4.0f), 0, AndroidUtilities.dp(4.0f), 0);
                    cell.setTag(Integer.valueOf(a));
                    cell.setCheckColor(TextColorCell.colors[a], TextColorCell.colors[a]);
                    cell.setTextAndValue(descriptions[a], currentColor2 != TextColorCell.colorsToSave[a]);
                    linearLayout.addView(cell);
                    cell.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            int count = linearLayout.getChildCount();
                            for (int a = 0; a < count; a++) {
                                View cell = (RadioColorCell) linearLayout.getChildAt(a);
                                cell.setChecked(cell == v, true);
                            }
                            selectedColor[0] = TextColorCell.colorsToSave[((Integer) v.getTag()).intValue()];
                        }
                    });
                }
                builder = new Builder(context);
                builder.setTitle(LocaleController.getString("LedColor", R.string.LedColor));
                builder.setView(linearLayout);
                z = globalAll;
                iArr = selectedColor;
                c10695 = c106952;
                z2 = globalGroup;
                builder2 = builder;
                string = LocaleController.getString("Set", R.string.Set);
                j = j3;
                selectedColor = onSelect;
                c106952 = new OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int which) {
                        Editor editor = MessagesController.getNotificationsSettings(UserConfig.selectedAccount).edit();
                        if (z) {
                            editor.putInt("MessagesLed", iArr[0]);
                        } else if (z2) {
                            editor.putInt("GroupLed", iArr[0]);
                        } else {
                            StringBuilder stringBuilder = new StringBuilder();
                            stringBuilder.append("color_");
                            stringBuilder.append(j);
                            editor.putInt(stringBuilder.toString(), iArr[0]);
                        }
                        editor.commit();
                        if (selectedColor != null) {
                            selectedColor.run();
                        }
                    }
                };
                builder2.setPositiveButton(string, c10695);
                z3 = globalGroup;
                j2 = j3;
                runnable = onSelect;
                builder2.setNeutralButton(LocaleController.getString("LedDisabled", R.string.LedDisabled), new OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Editor editor = MessagesController.getNotificationsSettings(UserConfig.selectedAccount).edit();
                        if (z) {
                            editor.putInt("MessagesLed", 0);
                        } else if (z3) {
                            editor.putInt("GroupLed", 0);
                        } else {
                            StringBuilder stringBuilder = new StringBuilder();
                            stringBuilder.append("color_");
                            stringBuilder.append(j2);
                            editor.putInt(stringBuilder.toString(), 0);
                        }
                        editor.commit();
                        if (runnable != null) {
                            runnable.run();
                        }
                    }
                });
                if (!globalAll || globalGroup) {
                    runnable2 = onSelect;
                } else {
                    runnable2 = onSelect;
                    builder2.setNegativeButton(LocaleController.getString("Default", R.string.Default), new OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Editor editor = MessagesController.getNotificationsSettings(UserConfig.selectedAccount).edit();
                            StringBuilder stringBuilder = new StringBuilder();
                            stringBuilder.append("color_");
                            stringBuilder.append(j3);
                            editor.remove(stringBuilder.toString());
                            editor.commit();
                            if (runnable2 != null) {
                                runnable2.run();
                            }
                        }
                    });
                }
                return builder2.create();
            }
        }
        currentColor2 = currentColor;
        linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(1);
        descriptions = new String[]{LocaleController.getString("ColorRed", R.string.ColorRed), LocaleController.getString("ColorOrange", R.string.ColorOrange), LocaleController.getString("ColorYellow", R.string.ColorYellow), LocaleController.getString("ColorGreen", R.string.ColorGreen), LocaleController.getString("ColorCyan", R.string.ColorCyan), LocaleController.getString("ColorBlue", R.string.ColorBlue), LocaleController.getString("ColorViolet", R.string.ColorViolet), LocaleController.getString("ColorPink", R.string.ColorPink), LocaleController.getString("ColorWhite", R.string.ColorWhite)};
        selectedColor = new int[]{currentColor2};
        for (a = 0; a < 9; a++) {
            RadioColorCell cell2 = new RadioColorCell(context);
            cell2.setPadding(AndroidUtilities.dp(4.0f), 0, AndroidUtilities.dp(4.0f), 0);
            cell2.setTag(Integer.valueOf(a));
            cell2.setCheckColor(TextColorCell.colors[a], TextColorCell.colors[a]);
            if (currentColor2 != TextColorCell.colorsToSave[a]) {
            }
            cell2.setTextAndValue(descriptions[a], currentColor2 != TextColorCell.colorsToSave[a]);
            linearLayout.addView(cell2);
            cell2.setOnClickListener(/* anonymous class already generated */);
        }
        builder = new Builder(context);
        builder.setTitle(LocaleController.getString("LedColor", R.string.LedColor));
        builder.setView(linearLayout);
        z = globalAll;
        iArr = selectedColor;
        c10695 = c106952;
        z2 = globalGroup;
        builder2 = builder;
        string = LocaleController.getString("Set", R.string.Set);
        j = j3;
        selectedColor = onSelect;
        c106952 = /* anonymous class already generated */;
        builder2.setPositiveButton(string, c10695);
        z3 = globalGroup;
        j2 = j3;
        runnable = onSelect;
        builder2.setNeutralButton(LocaleController.getString("LedDisabled", R.string.LedDisabled), /* anonymous class already generated */);
        if (globalAll) {
        }
        runnable2 = onSelect;
        return builder2.create();
    }

    public static Dialog createVibrationSelectDialog(Activity parentActivity, BaseFragment parentFragment, long dialog_id, boolean globalGroup, boolean globalAll, Runnable onSelect) {
        String str = dialog_id != 0 ? "vibrate_" : globalGroup ? "vibrate_group" : "vibrate_messages";
        return createVibrationSelectDialog(parentActivity, parentFragment, dialog_id, str, onSelect);
    }

    public static Dialog createVibrationSelectDialog(Activity parentActivity, BaseFragment parentFragment, long dialog_id, String prefKeyPrefix, Runnable onSelect) {
        String[] descriptions;
        Context context = parentActivity;
        long j = dialog_id;
        String str = prefKeyPrefix;
        SharedPreferences preferences = MessagesController.getNotificationsSettings(UserConfig.selectedAccount);
        boolean z = true;
        int[] selected = new int[1];
        boolean z2 = false;
        if (j != 0) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(str);
            stringBuilder.append(j);
            selected[0] = preferences.getInt(stringBuilder.toString(), 0);
            if (selected[0] == 3) {
                selected[0] = 2;
            } else if (selected[0] == 2) {
                selected[0] = 3;
            }
            descriptions = new String[]{LocaleController.getString("VibrationDefault", R.string.VibrationDefault), LocaleController.getString("Short", R.string.Short), LocaleController.getString("Long", R.string.Long), LocaleController.getString("VibrationDisabled", R.string.VibrationDisabled)};
        } else {
            selected[0] = preferences.getInt(str, 0);
            if (selected[0] == 0) {
                selected[0] = 1;
            } else if (selected[0] == 1) {
                selected[0] = 2;
            } else if (selected[0] == 2) {
                selected[0] = 0;
            }
            descriptions = new String[]{LocaleController.getString("VibrationDisabled", R.string.VibrationDisabled), LocaleController.getString("VibrationDefault", R.string.VibrationDefault), LocaleController.getString("Short", R.string.Short), LocaleController.getString("Long", R.string.Long), LocaleController.getString("OnlyIfSilent", R.string.OnlyIfSilent)};
        }
        String[] descriptions2 = descriptions;
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(1);
        int a = 0;
        while (true) {
            int a2 = a;
            LinearLayout linearLayout2;
            if (a2 < descriptions2.length) {
                RadioColorCell cell = new RadioColorCell(context);
                cell.setPadding(AndroidUtilities.dp(4.0f), z2, AndroidUtilities.dp(4.0f), z2);
                cell.setTag(Integer.valueOf(a2));
                cell.setCheckColor(Theme.getColor(Theme.key_radioBackground), Theme.getColor(Theme.key_dialogRadioBackgroundChecked));
                cell.setTextAndValue(descriptions2[a2], selected[z2] == a2 ? z : z2);
                linearLayout.addView(cell);
                final int[] iArr = selected;
                C10728 c10728 = r1;
                final long j2 = j;
                RadioColorCell cell2 = cell;
                final String str2 = str;
                int a3 = a2;
                final BaseFragment baseFragment = parentFragment;
                linearLayout2 = linearLayout;
                final Runnable linearLayout3 = onSelect;
                C10728 c107282 = new View.OnClickListener() {
                    public void onClick(View v) {
                        iArr[0] = ((Integer) v.getTag()).intValue();
                        Editor editor = MessagesController.getNotificationsSettings(UserConfig.selectedAccount).edit();
                        if (j2 != 0) {
                            if (iArr[0] == 0) {
                                StringBuilder stringBuilder = new StringBuilder();
                                stringBuilder.append(str2);
                                stringBuilder.append(j2);
                                editor.putInt(stringBuilder.toString(), 0);
                            } else if (iArr[0] == 1) {
                                r2 = new StringBuilder();
                                r2.append(str2);
                                r2.append(j2);
                                editor.putInt(r2.toString(), 1);
                            } else if (iArr[0] == 2) {
                                r2 = new StringBuilder();
                                r2.append(str2);
                                r2.append(j2);
                                editor.putInt(r2.toString(), 3);
                            } else if (iArr[0] == 3) {
                                r2 = new StringBuilder();
                                r2.append(str2);
                                r2.append(j2);
                                editor.putInt(r2.toString(), 2);
                            }
                        } else if (iArr[0] == 0) {
                            editor.putInt(str2, 2);
                        } else if (iArr[0] == 1) {
                            editor.putInt(str2, 0);
                        } else if (iArr[0] == 2) {
                            editor.putInt(str2, 1);
                        } else if (iArr[0] == 3) {
                            editor.putInt(str2, 3);
                        } else if (iArr[0] == 4) {
                            editor.putInt(str2, 4);
                        }
                        editor.commit();
                        if (baseFragment != null) {
                            baseFragment.dismissCurrentDialig();
                        }
                        if (linearLayout3 != null) {
                            linearLayout3.run();
                        }
                    }
                };
                cell2.setOnClickListener(c10728);
                a = a3 + 1;
                linearLayout = linearLayout2;
                j = dialog_id;
                z = true;
                z2 = false;
            } else {
                linearLayout2 = linearLayout;
                Builder builder = new Builder(context);
                builder.setTitle(LocaleController.getString("Vibrate", R.string.Vibrate));
                builder.setView(linearLayout2);
                builder.setPositiveButton(LocaleController.getString("Cancel", R.string.Cancel), null);
                return builder.create();
            }
        }
    }

    public static Dialog createLocationUpdateDialog(Activity parentActivity, User user, IntCallback callback) {
        Context context = parentActivity;
        final int[] selected = new int[1];
        int i = 3;
        String[] descriptions = new String[]{LocaleController.getString("SendLiveLocationFor15m", R.string.SendLiveLocationFor15m), LocaleController.getString("SendLiveLocationFor1h", R.string.SendLiveLocationFor1h), LocaleController.getString("SendLiveLocationFor8h", R.string.SendLiveLocationFor8h)};
        final LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(1);
        TextView titleTextView = new TextView(context);
        if (user != null) {
            titleTextView.setText(LocaleController.formatString("LiveLocationAlertPrivate", R.string.LiveLocationAlertPrivate, UserObject.getFirstName(user)));
        } else {
            titleTextView.setText(LocaleController.getString("LiveLocationAlertGroup", R.string.LiveLocationAlertGroup));
        }
        titleTextView.setTextColor(Theme.getColor(Theme.key_dialogTextBlack));
        titleTextView.setTextSize(1, 16.0f);
        titleTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
        if (LocaleController.isRTL) {
            i = 5;
        }
        linearLayout.addView(titleTextView, LayoutHelper.createLinear(-2, -2, i | 48, 24, 0, 24, 8));
        i = 0;
        while (i < descriptions.length) {
            RadioColorCell cell = new RadioColorCell(context);
            cell.setPadding(AndroidUtilities.dp(4.0f), 0, AndroidUtilities.dp(4.0f), 0);
            cell.setTag(Integer.valueOf(i));
            cell.setCheckColor(Theme.getColor(Theme.key_radioBackground), Theme.getColor(Theme.key_dialogRadioBackgroundChecked));
            cell.setTextAndValue(descriptions[i], selected[0] == i);
            linearLayout.addView(cell);
            cell.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    selected[0] = ((Integer) v.getTag()).intValue();
                    int count = linearLayout.getChildCount();
                    for (int a = 0; a < count; a++) {
                        View child = linearLayout.getChildAt(a);
                        if (child instanceof RadioColorCell) {
                            ((RadioColorCell) child).setChecked(child == v, true);
                        }
                    }
                }
            });
            i++;
        }
        Builder builder = new Builder(context);
        builder.setTopImage(new ShareLocationDrawable(context, false), Theme.getColor(Theme.key_dialogTopBackground));
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
                    intCallback.run(time);
                }
                intCallback.run(time);
            }
        });
        builder.setNeutralButton(LocaleController.getString("Cancel", R.string.Cancel), null);
        return builder.create();
    }

    public static Dialog createFreeSpaceDialog(LaunchActivity parentActivity) {
        final Context context = parentActivity;
        final int[] selected = new int[1];
        int keepMedia = MessagesController.getGlobalMainSettings().getInt("keep_media", 2);
        int i = 3;
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
        final LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(1);
        TextView titleTextView = new TextView(context);
        titleTextView.setText(LocaleController.getString("LowDiskSpaceTitle2", R.string.LowDiskSpaceTitle2));
        titleTextView.setTextColor(Theme.getColor(Theme.key_dialogTextBlack));
        titleTextView.setTextSize(1, 16.0f);
        titleTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        titleTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
        if (LocaleController.isRTL) {
            i = 5;
        }
        linearLayout.addView(titleTextView, LayoutHelper.createLinear(-2, -2, i | 48, 24, 0, 24, 8));
        i = 0;
        while (i < descriptions.length) {
            RadioColorCell cell = new RadioColorCell(context);
            cell.setPadding(AndroidUtilities.dp(4.0f), 0, AndroidUtilities.dp(4.0f), 0);
            cell.setTag(Integer.valueOf(i));
            cell.setCheckColor(Theme.getColor(Theme.key_radioBackground), Theme.getColor(Theme.key_dialogRadioBackgroundChecked));
            cell.setTextAndValue(descriptions[i], selected[0] == i);
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
                            ((RadioColorCell) child).setChecked(child == v, true);
                        }
                    }
                }
            });
            i++;
        }
        Builder builder = new Builder(context);
        builder.setTitle(LocaleController.getString("LowDiskSpaceTitle", R.string.LowDiskSpaceTitle));
        builder.setMessage(LocaleController.getString("LowDiskSpaceMessage", R.string.LowDiskSpaceMessage));
        builder.setView(linearLayout);
        builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), new OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                MessagesController.getGlobalMainSettings().edit().putInt("keep_media", selected[0]).commit();
            }
        });
        builder.setNeutralButton(LocaleController.getString("ClearMediaCache", R.string.ClearMediaCache), new OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                context.presentFragment(new CacheControlActivity());
            }
        });
        return builder.create();
    }

    public static Dialog createPrioritySelectDialog(Activity parentActivity, BaseFragment parentFragment, long dialog_id, boolean globalGroup, boolean globalAll, Runnable onSelect) {
        String[] descriptions;
        Context context = parentActivity;
        long j = dialog_id;
        SharedPreferences preferences = MessagesController.getNotificationsSettings(UserConfig.selectedAccount);
        boolean z = true;
        int[] selected = new int[1];
        boolean z2 = false;
        if (j != 0) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("priority_");
            stringBuilder.append(j);
            selected[0] = preferences.getInt(stringBuilder.toString(), 3);
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
        String[] descriptions2 = descriptions;
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(1);
        int a = 0;
        while (true) {
            int a2 = a;
            if (a2 < descriptions2.length) {
                RadioColorCell cell = new RadioColorCell(context);
                cell.setPadding(AndroidUtilities.dp(4.0f), z2, AndroidUtilities.dp(4.0f), z2);
                cell.setTag(Integer.valueOf(a2));
                cell.setCheckColor(Theme.getColor(Theme.key_radioBackground), Theme.getColor(Theme.key_dialogRadioBackgroundChecked));
                cell.setTextAndValue(descriptions2[a2], selected[z2] == a2 ? z : z2);
                linearLayout.addView(cell);
                final int[] iArr = selected;
                final long j2 = j;
                AnonymousClass14 anonymousClass14 = r1;
                final boolean z3 = globalGroup;
                RadioColorCell cell2 = cell;
                final BaseFragment baseFragment = parentFragment;
                int a3 = a2;
                a2 = onSelect;
                AnonymousClass14 anonymousClass142 = new View.OnClickListener() {
                    public void onClick(View v) {
                        iArr[0] = ((Integer) v.getTag()).intValue();
                        Editor editor = MessagesController.getNotificationsSettings(UserConfig.selectedAccount).edit();
                        int option = 1;
                        if (j2 != 0) {
                            int option2;
                            StringBuilder stringBuilder;
                            if (iArr[0] == 0) {
                                option = 3;
                            } else if (iArr[0] == 1) {
                                option = 4;
                            } else if (iArr[0] == 2) {
                                option = 5;
                            } else if (iArr[0] == 3) {
                                option = 0;
                            } else {
                                option2 = option;
                                stringBuilder = new StringBuilder();
                                stringBuilder.append("priority_");
                                stringBuilder.append(j2);
                                editor.putInt(stringBuilder.toString(), option2);
                            }
                            option2 = option;
                            stringBuilder = new StringBuilder();
                            stringBuilder.append("priority_");
                            stringBuilder.append(j2);
                            editor.putInt(stringBuilder.toString(), option2);
                        } else {
                            if (iArr[0] == 0) {
                                option = 4;
                            } else if (iArr[0] == 1) {
                                option = 5;
                            } else if (iArr[0] == 2) {
                                option = 0;
                            } else {
                                editor.putInt(z3 ? "priority_group" : "priority_messages", option);
                            }
                            if (z3) {
                            }
                            editor.putInt(z3 ? "priority_group" : "priority_messages", option);
                        }
                        editor.commit();
                        if (baseFragment != null) {
                            baseFragment.dismissCurrentDialig();
                        }
                        if (a2 != null) {
                            a2.run();
                        }
                    }
                };
                cell2.setOnClickListener(anonymousClass14);
                a = a3 + 1;
                z = true;
                z2 = false;
            } else {
                Builder builder = new Builder(context);
                builder.setTitle(LocaleController.getString("NotificationsImportance", R.string.NotificationsImportance));
                builder.setView(linearLayout);
                builder.setPositiveButton(LocaleController.getString("Cancel", R.string.Cancel), null);
                return builder.create();
            }
        }
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
        int a = 0;
        while (a < options.length) {
            RadioColorCell cell = new RadioColorCell(parentActivity);
            cell.setPadding(AndroidUtilities.dp(4.0f), 0, AndroidUtilities.dp(4.0f), 0);
            cell.setTag(Integer.valueOf(a));
            cell.setCheckColor(Theme.getColor(Theme.key_radioBackground), Theme.getColor(Theme.key_dialogRadioBackgroundChecked));
            cell.setTextAndValue(options[a], selected == a);
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
            a++;
        }
        Builder builder = new Builder((Context) parentActivity);
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
}
