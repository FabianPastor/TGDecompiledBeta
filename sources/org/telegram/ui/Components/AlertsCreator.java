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
import org.telegram.messenger.C0446R;
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
import org.telegram.messenger.exoplayer2.C0542C;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.EncryptedChat;
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

    public static Dialog processError(int i, TL_error tL_error, BaseFragment baseFragment, TLObject tLObject, Object... objArr) {
        if (tL_error.code != 406) {
            if (tL_error.text != null) {
                if (!((tLObject instanceof TL_channels_joinChannel) || (tLObject instanceof TL_channels_editAdmin) || (tLObject instanceof TL_channels_inviteToChannel) || (tLObject instanceof TL_messages_addChatUser) || (tLObject instanceof TL_messages_startBot))) {
                    if (!(tLObject instanceof TL_channels_editBanned)) {
                        if (tLObject instanceof TL_messages_createChat) {
                            if (tL_error.text.startsWith("FLOOD_WAIT") != 0) {
                                showFloodWaitAlert(tL_error.text, baseFragment);
                            } else {
                                showAddUserAlert(tL_error.text, baseFragment, false);
                            }
                        } else if (tLObject instanceof TL_channels_createChannel) {
                            if (tL_error.text.startsWith("FLOOD_WAIT") != 0) {
                                showFloodWaitAlert(tL_error.text, baseFragment);
                            } else {
                                showAddUserAlert(tL_error.text, baseFragment, false);
                            }
                        } else if (!(tLObject instanceof TL_messages_editMessage)) {
                            if (!((tLObject instanceof TL_messages_sendMessage) || (tLObject instanceof TL_messages_sendMedia) || (tLObject instanceof TL_messages_sendBroadcast) || (tLObject instanceof TL_messages_sendInlineBotResult))) {
                                if (!(tLObject instanceof TL_messages_forwardMessages)) {
                                    if ((tLObject instanceof TL_messages_importChatInvite) != 0) {
                                        if (tL_error.text.startsWith("FLOOD_WAIT") != 0) {
                                            showSimpleAlert(baseFragment, LocaleController.getString("FloodWait", C0446R.string.FloodWait));
                                        } else if (tL_error.text.equals("USERS_TOO_MUCH") != 0) {
                                            showSimpleAlert(baseFragment, LocaleController.getString("JoinToGroupErrorFull", C0446R.string.JoinToGroupErrorFull));
                                        } else {
                                            showSimpleAlert(baseFragment, LocaleController.getString("JoinToGroupErrorNotExist", C0446R.string.JoinToGroupErrorNotExist));
                                        }
                                    } else if ((tLObject instanceof TL_messages_getAttachedStickers) != 0) {
                                        if (!(baseFragment == null || baseFragment.getParentActivity() == 0)) {
                                            i = baseFragment.getParentActivity();
                                            baseFragment = new StringBuilder();
                                            baseFragment.append(LocaleController.getString("ErrorOccurred", C0446R.string.ErrorOccurred));
                                            baseFragment.append("\n");
                                            baseFragment.append(tL_error.text);
                                            Toast.makeText(i, baseFragment.toString(), 0).show();
                                        }
                                    } else if ((tLObject instanceof TL_account_confirmPhone) != 0) {
                                        if (tL_error.text.contains("PHONE_CODE_EMPTY") == 0) {
                                            if (tL_error.text.contains("PHONE_CODE_INVALID") == 0) {
                                                if (tL_error.text.contains("PHONE_CODE_EXPIRED") != 0) {
                                                    showSimpleAlert(baseFragment, LocaleController.getString("CodeExpired", C0446R.string.CodeExpired));
                                                } else if (tL_error.text.startsWith("FLOOD_WAIT") != 0) {
                                                    showSimpleAlert(baseFragment, LocaleController.getString("FloodWait", C0446R.string.FloodWait));
                                                } else {
                                                    showSimpleAlert(baseFragment, tL_error.text);
                                                }
                                            }
                                        }
                                        showSimpleAlert(baseFragment, LocaleController.getString("InvalidCode", C0446R.string.InvalidCode));
                                    } else if ((tLObject instanceof TL_auth_resendCode) != 0) {
                                        if (tL_error.text.contains("PHONE_NUMBER_INVALID") != 0) {
                                            showSimpleAlert(baseFragment, LocaleController.getString("InvalidPhoneNumber", C0446R.string.InvalidPhoneNumber));
                                        } else {
                                            if (tL_error.text.contains("PHONE_CODE_EMPTY") == 0) {
                                                if (tL_error.text.contains("PHONE_CODE_INVALID") == 0) {
                                                    if (tL_error.text.contains("PHONE_CODE_EXPIRED") != 0) {
                                                        showSimpleAlert(baseFragment, LocaleController.getString("CodeExpired", C0446R.string.CodeExpired));
                                                    } else if (tL_error.text.startsWith("FLOOD_WAIT") != 0) {
                                                        showSimpleAlert(baseFragment, LocaleController.getString("FloodWait", C0446R.string.FloodWait));
                                                    } else if (tL_error.code != C0542C.PRIORITY_DOWNLOAD) {
                                                        i = new StringBuilder();
                                                        i.append(LocaleController.getString("ErrorOccurred", C0446R.string.ErrorOccurred));
                                                        i.append("\n");
                                                        i.append(tL_error.text);
                                                        showSimpleAlert(baseFragment, i.toString());
                                                    }
                                                }
                                            }
                                            showSimpleAlert(baseFragment, LocaleController.getString("InvalidCode", C0446R.string.InvalidCode));
                                        }
                                    } else if ((tLObject instanceof TL_account_sendConfirmPhoneCode) != 0) {
                                        if (tL_error.code == 400) {
                                            return showSimpleAlert(baseFragment, LocaleController.getString("CancelLinkExpired", C0446R.string.CancelLinkExpired));
                                        }
                                        if (tL_error.text != 0) {
                                            if (tL_error.text.startsWith("FLOOD_WAIT") != 0) {
                                                return showSimpleAlert(baseFragment, LocaleController.getString("FloodWait", C0446R.string.FloodWait));
                                            }
                                            return showSimpleAlert(baseFragment, LocaleController.getString("ErrorOccurred", C0446R.string.ErrorOccurred));
                                        }
                                    } else if ((tLObject instanceof TL_account_changePhone) != 0) {
                                        if (tL_error.text.contains("PHONE_NUMBER_INVALID") != 0) {
                                            showSimpleAlert(baseFragment, LocaleController.getString("InvalidPhoneNumber", C0446R.string.InvalidPhoneNumber));
                                        } else {
                                            if (tL_error.text.contains("PHONE_CODE_EMPTY") == 0) {
                                                if (tL_error.text.contains("PHONE_CODE_INVALID") == 0) {
                                                    if (tL_error.text.contains("PHONE_CODE_EXPIRED") != 0) {
                                                        showSimpleAlert(baseFragment, LocaleController.getString("CodeExpired", C0446R.string.CodeExpired));
                                                    } else if (tL_error.text.startsWith("FLOOD_WAIT") != 0) {
                                                        showSimpleAlert(baseFragment, LocaleController.getString("FloodWait", C0446R.string.FloodWait));
                                                    } else {
                                                        showSimpleAlert(baseFragment, tL_error.text);
                                                    }
                                                }
                                            }
                                            showSimpleAlert(baseFragment, LocaleController.getString("InvalidCode", C0446R.string.InvalidCode));
                                        }
                                    } else if ((tLObject instanceof TL_account_sendChangePhoneCode) == 0) {
                                        objArr = -1;
                                        if ((tLObject instanceof TL_updateUserName) != 0) {
                                            i = tL_error.text;
                                            tL_error = i.hashCode();
                                            if (tL_error != 288843630) {
                                                if (tL_error == 533175271) {
                                                    if (i.equals("USERNAME_OCCUPIED") != 0) {
                                                        objArr = 1;
                                                    }
                                                }
                                            } else if (i.equals("USERNAME_INVALID") != 0) {
                                                objArr = null;
                                            }
                                            switch (objArr) {
                                                case null:
                                                    showSimpleAlert(baseFragment, LocaleController.getString("UsernameInvalid", C0446R.string.UsernameInvalid));
                                                    break;
                                                case 1:
                                                    showSimpleAlert(baseFragment, LocaleController.getString("UsernameInUse", C0446R.string.UsernameInUse));
                                                    break;
                                                default:
                                                    showSimpleAlert(baseFragment, LocaleController.getString("ErrorOccurred", C0446R.string.ErrorOccurred));
                                                    break;
                                            }
                                        } else if ((tLObject instanceof TL_contacts_importContacts) != 0) {
                                            if (tL_error != null) {
                                                if (tL_error.text.startsWith("FLOOD_WAIT") == 0) {
                                                    i = new StringBuilder();
                                                    i.append(LocaleController.getString("ErrorOccurred", C0446R.string.ErrorOccurred));
                                                    i.append("\n");
                                                    i.append(tL_error.text);
                                                    showSimpleAlert(baseFragment, i.toString());
                                                }
                                            }
                                            showSimpleAlert(baseFragment, LocaleController.getString("FloodWait", C0446R.string.FloodWait));
                                        } else {
                                            if ((tLObject instanceof TL_account_getPassword) == 0) {
                                                if ((tLObject instanceof TL_account_getTmpPassword) == 0) {
                                                    if ((tLObject instanceof TL_payments_sendPaymentForm) != 0) {
                                                        i = tL_error.text;
                                                        tLObject = i.hashCode();
                                                        if (tLObject != -NUM) {
                                                            if (tLObject == -784238410) {
                                                                if (i.equals("PAYMENT_FAILED") != 0) {
                                                                    objArr = 1;
                                                                }
                                                            }
                                                        } else if (i.equals("BOT_PRECHECKOUT_FAILED") != 0) {
                                                            objArr = null;
                                                        }
                                                        switch (objArr) {
                                                            case null:
                                                                showSimpleToast(baseFragment, LocaleController.getString("PaymentPrecheckoutFailed", C0446R.string.PaymentPrecheckoutFailed));
                                                                break;
                                                            case 1:
                                                                showSimpleToast(baseFragment, LocaleController.getString("PaymentFailed", C0446R.string.PaymentFailed));
                                                                break;
                                                            default:
                                                                showSimpleToast(baseFragment, tL_error.text);
                                                                break;
                                                        }
                                                    } else if ((tLObject instanceof TL_payments_validateRequestedInfo) != 0) {
                                                        i = tL_error.text;
                                                        if (i.hashCode() == NUM) {
                                                            if (i.equals("SHIPPING_NOT_AVAILABLE") != 0) {
                                                                objArr = null;
                                                            }
                                                        }
                                                        if (objArr != null) {
                                                            showSimpleToast(baseFragment, tL_error.text);
                                                        } else {
                                                            showSimpleToast(baseFragment, LocaleController.getString("PaymentNoShippingMethod", C0446R.string.PaymentNoShippingMethod));
                                                        }
                                                    }
                                                }
                                            }
                                            if (tL_error.text.startsWith("FLOOD_WAIT") != 0) {
                                                showSimpleToast(baseFragment, getFloodWaitString(tL_error.text));
                                            } else {
                                                showSimpleToast(baseFragment, tL_error.text);
                                            }
                                        }
                                    } else if (tL_error.text.contains("PHONE_NUMBER_INVALID") != 0) {
                                        showSimpleAlert(baseFragment, LocaleController.getString("InvalidPhoneNumber", C0446R.string.InvalidPhoneNumber));
                                    } else {
                                        if (tL_error.text.contains("PHONE_CODE_EMPTY") == 0) {
                                            if (tL_error.text.contains("PHONE_CODE_INVALID") == 0) {
                                                if (tL_error.text.contains("PHONE_CODE_EXPIRED") != 0) {
                                                    showSimpleAlert(baseFragment, LocaleController.getString("CodeExpired", C0446R.string.CodeExpired));
                                                } else if (tL_error.text.startsWith("FLOOD_WAIT") != 0) {
                                                    showSimpleAlert(baseFragment, LocaleController.getString("FloodWait", C0446R.string.FloodWait));
                                                } else if (tL_error.text.startsWith("PHONE_NUMBER_OCCUPIED") != 0) {
                                                    showSimpleAlert(baseFragment, LocaleController.formatString("ChangePhoneNumberOccupied", C0446R.string.ChangePhoneNumberOccupied, new Object[]{(String) objArr[0]}));
                                                } else {
                                                    showSimpleAlert(baseFragment, LocaleController.getString("ErrorOccurred", C0446R.string.ErrorOccurred));
                                                }
                                            }
                                        }
                                        showSimpleAlert(baseFragment, LocaleController.getString("InvalidCode", C0446R.string.InvalidCode));
                                    }
                                }
                            }
                            if (tL_error.text.equals("PEER_FLOOD") != null) {
                                NotificationCenter.getInstance(i).postNotificationName(NotificationCenter.needShowAlert, new Object[]{Integer.valueOf(0)});
                            }
                        } else if (tL_error.text.equals("MESSAGE_NOT_MODIFIED") == 0) {
                            showSimpleAlert(baseFragment, LocaleController.getString("EditMessageError", C0446R.string.EditMessageError));
                        }
                        return null;
                    }
                }
                if (baseFragment != null) {
                    showAddUserAlert(tL_error.text, baseFragment, ((Boolean) objArr[0]).booleanValue());
                } else if (tL_error.text.equals("PEER_FLOOD") != null) {
                    NotificationCenter.getInstance(i).postNotificationName(NotificationCenter.needShowAlert, new Object[]{Integer.valueOf(1)});
                }
                return null;
            }
        }
        return null;
    }

    public static Toast showSimpleToast(BaseFragment baseFragment, String str) {
        if (!(str == null || baseFragment == null)) {
            if (baseFragment.getParentActivity() != null) {
                baseFragment = Toast.makeText(baseFragment.getParentActivity(), str, 1);
                baseFragment.show();
                return baseFragment;
            }
        }
        return null;
    }

    public static Dialog showSimpleAlert(BaseFragment baseFragment, String str) {
        if (!(str == null || baseFragment == null)) {
            if (baseFragment.getParentActivity() != null) {
                Builder builder = new Builder(baseFragment.getParentActivity());
                builder.setTitle(LocaleController.getString("AppName", C0446R.string.AppName));
                builder.setMessage(str);
                builder.setPositiveButton(LocaleController.getString("OK", C0446R.string.OK), null);
                str = builder.create();
                baseFragment.showDialog(str);
                return str;
            }
        }
        return null;
    }

    public static Dialog createMuteAlert(Context context, final long j) {
        if (context == null) {
            return null;
        }
        BottomSheet.Builder builder = new BottomSheet.Builder(context);
        builder.setTitle(LocaleController.getString("Notifications", C0446R.string.Notifications));
        context = new CharSequence[4];
        context[0] = LocaleController.formatString("MuteFor", C0446R.string.MuteFor, LocaleController.formatPluralString("Hours", 1));
        context[1] = LocaleController.formatString("MuteFor", C0446R.string.MuteFor, LocaleController.formatPluralString("Hours", 8));
        context[2] = LocaleController.formatString("MuteFor", C0446R.string.MuteFor, LocaleController.formatPluralString("Days", 2));
        context[3] = LocaleController.getString("MuteDisable", C0446R.string.MuteDisable);
        builder.setItems(context, new OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface = ConnectionsManager.getInstance(UserConfig.selectedAccount).getCurrentTime();
                if (i == 0) {
                    dialogInterface += 3600;
                } else if (i == 1) {
                    dialogInterface += 28800;
                } else if (i == 2) {
                    dialogInterface += 172800;
                } else if (i == 3) {
                    dialogInterface = ConnectionsManager.DEFAULT_DATACENTER_ID;
                }
                Editor edit = MessagesController.getNotificationsSettings(UserConfig.selectedAccount).edit();
                long j = 1;
                if (i == 3) {
                    i = new StringBuilder();
                    i.append("notify2_");
                    i.append(j);
                    edit.putInt(i.toString(), 2);
                } else {
                    i = new StringBuilder();
                    i.append("notify2_");
                    i.append(j);
                    edit.putInt(i.toString(), 3);
                    i = new StringBuilder();
                    i.append("notifyuntil_");
                    i.append(j);
                    edit.putInt(i.toString(), dialogInterface);
                    j = (((long) dialogInterface) << 32) | 1;
                }
                NotificationsController.getInstance(UserConfig.selectedAccount).removeNotificationsForDialog(j);
                MessagesStorage.getInstance(UserConfig.selectedAccount).setDialogFlags(j, j);
                edit.commit();
                TL_dialog tL_dialog = (TL_dialog) MessagesController.getInstance(UserConfig.selectedAccount).dialogs_dict.get(j);
                if (tL_dialog != null) {
                    tL_dialog.notify_settings = new TL_peerNotifySettings();
                    tL_dialog.notify_settings.mute_until = dialogInterface;
                }
                NotificationsController.getInstance(UserConfig.selectedAccount).updateServerNotificationsSettings(j);
            }
        });
        return builder.create();
    }

    public static Dialog createReportAlert(Context context, long j, int i, BaseFragment baseFragment) {
        if (context != null) {
            if (baseFragment != null) {
                BottomSheet.Builder builder = new BottomSheet.Builder(context);
                builder.setTitle(LocaleController.getString("ReportChat", C0446R.string.ReportChat));
                final long j2 = j;
                final int i2 = i;
                final BaseFragment baseFragment2 = baseFragment;
                final Context context2 = context;
                builder.setItems(new CharSequence[]{LocaleController.getString("ReportChatSpam", C0446R.string.ReportChatSpam), LocaleController.getString("ReportChatViolence", C0446R.string.ReportChatViolence), LocaleController.getString("ReportChatPornography", C0446R.string.ReportChatPornography), LocaleController.getString("ReportChatOther", C0446R.string.ReportChatOther)}, new OnClickListener() {

                    /* renamed from: org.telegram.ui.Components.AlertsCreator$2$1 */
                    class C20361 implements RequestDelegate {
                        public void run(TLObject tLObject, TL_error tL_error) {
                        }

                        C20361() {
                        }
                    }

                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (i == 3) {
                            dialogInterface = new Bundle();
                            dialogInterface.putLong("dialog_id", j2);
                            dialogInterface.putLong("message_id", (long) i2);
                            baseFragment2.presentFragment(new ReportOtherActivity(dialogInterface));
                            return;
                        }
                        TLObject tL_messages_report;
                        dialogInterface = MessagesController.getInstance(UserConfig.selectedAccount).getInputPeer((int) j2);
                        if (i2 != 0) {
                            tL_messages_report = new TL_messages_report();
                            tL_messages_report.peer = dialogInterface;
                            tL_messages_report.id.add(Integer.valueOf(i2));
                            if (i == 0) {
                                tL_messages_report.reason = new TL_inputReportReasonSpam();
                            } else if (i == 1) {
                                tL_messages_report.reason = new TL_inputReportReasonViolence();
                            } else if (i == 2) {
                                tL_messages_report.reason = new TL_inputReportReasonPornography();
                            }
                        } else {
                            tL_messages_report = new TL_account_reportPeer();
                            tL_messages_report.peer = dialogInterface;
                            if (i == 0) {
                                tL_messages_report.reason = new TL_inputReportReasonSpam();
                            } else if (i == 1) {
                                tL_messages_report.reason = new TL_inputReportReasonViolence();
                            } else if (i == 2) {
                                tL_messages_report.reason = new TL_inputReportReasonPornography();
                            }
                        }
                        ConnectionsManager.getInstance(UserConfig.selectedAccount).sendRequest(tL_messages_report, new C20361());
                        Toast.makeText(context2, LocaleController.getString("ReportChatSent", C0446R.string.ReportChatSent), 0).show();
                    }
                });
                return builder.create();
            }
        }
        return null;
    }

    private static String getFloodWaitString(String str) {
        str = Utilities.parseInt(str).intValue();
        if (str < 60) {
            str = LocaleController.formatPluralString("Seconds", str);
        } else {
            str = LocaleController.formatPluralString("Minutes", str / 60);
        }
        return LocaleController.formatString("FloodWaitTime", C0446R.string.FloodWaitTime, str);
    }

    public static void showFloodWaitAlert(String str, BaseFragment baseFragment) {
        if (!(str == null || !str.startsWith("FLOOD_WAIT") || baseFragment == null)) {
            if (baseFragment.getParentActivity() != null) {
                str = Utilities.parseInt(str).intValue();
                if (str < 60) {
                    str = LocaleController.formatPluralString("Seconds", str);
                } else {
                    str = LocaleController.formatPluralString("Minutes", str / 60);
                }
                Builder builder = new Builder(baseFragment.getParentActivity());
                builder.setTitle(LocaleController.getString("AppName", C0446R.string.AppName));
                builder.setMessage(LocaleController.formatString("FloodWaitTime", C0446R.string.FloodWaitTime, str));
                builder.setPositiveButton(LocaleController.getString("OK", C0446R.string.OK), null);
                baseFragment.showDialog(builder.create(), true, null);
            }
        }
    }

    public static void showSendMediaAlert(int i, BaseFragment baseFragment) {
        if (i != 0) {
            Builder builder = new Builder(baseFragment.getParentActivity());
            builder.setTitle(LocaleController.getString("AppName", C0446R.string.AppName));
            if (i == 1) {
                builder.setMessage(LocaleController.getString("ErrorSendRestrictedStickers", C0446R.string.ErrorSendRestrictedStickers));
            } else if (i == 2) {
                builder.setMessage(LocaleController.getString("ErrorSendRestrictedMedia", C0446R.string.ErrorSendRestrictedMedia));
            }
            builder.setPositiveButton(LocaleController.getString("OK", C0446R.string.OK), null);
            baseFragment.showDialog(builder.create(), true, null);
        }
    }

    public static void showAddUserAlert(String str, final BaseFragment baseFragment, boolean z) {
        if (!(str == null || baseFragment == null)) {
            if (baseFragment.getParentActivity() != null) {
                Builder builder = new Builder(baseFragment.getParentActivity());
                builder.setTitle(LocaleController.getString("AppName", C0446R.string.AppName));
                boolean z2 = true;
                switch (str.hashCode()) {
                    case -1763467626:
                        if (str.equals("USERS_TOO_FEW")) {
                            z2 = true;
                            break;
                        }
                        break;
                    case -538116776:
                        if (str.equals("USER_BLOCKED")) {
                            z2 = true;
                            break;
                        }
                        break;
                    case -512775857:
                        if (str.equals("USER_RESTRICTED")) {
                            z2 = true;
                            break;
                        }
                        break;
                    case -454039871:
                        if (str.equals("PEER_FLOOD")) {
                            z2 = false;
                            break;
                        }
                        break;
                    case -420079733:
                        if (str.equals("BOTS_TOO_MUCH")) {
                            z2 = true;
                            break;
                        }
                        break;
                    case 98635865:
                        if (str.equals("USER_KICKED")) {
                            z2 = true;
                            break;
                        }
                        break;
                    case 517420851:
                        if (str.equals("USER_BOT")) {
                            z2 = true;
                            break;
                        }
                        break;
                    case 845559454:
                        if (str.equals("YOU_BLOCKED_USER")) {
                            z2 = true;
                            break;
                        }
                        break;
                    case 916342611:
                        if (str.equals("USER_ADMIN_INVALID")) {
                            z2 = true;
                            break;
                        }
                        break;
                    case 1047173446:
                        if (str.equals("CHAT_ADMIN_BAN_REQUIRED")) {
                            z2 = true;
                            break;
                        }
                        break;
                    case 1167301807:
                        if (str.equals("USERS_TOO_MUCH")) {
                            z2 = true;
                            break;
                        }
                        break;
                    case 1227003815:
                        if (str.equals("USER_ID_INVALID")) {
                            z2 = true;
                            break;
                        }
                        break;
                    case 1253103379:
                        if (str.equals("ADMINS_TOO_MUCH")) {
                            z2 = true;
                            break;
                        }
                        break;
                    case 1623167701:
                        if (str.equals("USER_NOT_MUTUAL_CONTACT")) {
                            z2 = true;
                            break;
                        }
                        break;
                    case 1754587486:
                        if (str.equals("CHAT_ADMIN_INVITE_REQUIRED")) {
                            z2 = true;
                            break;
                        }
                        break;
                    case 1916725894:
                        if (str.equals("USER_PRIVACY_RESTRICTED")) {
                            z2 = true;
                            break;
                        }
                        break;
                    default:
                        break;
                }
                switch (z2) {
                    case false:
                        builder.setMessage(LocaleController.getString("NobodyLikesSpam2", true));
                        builder.setNegativeButton(LocaleController.getString("MoreInfo", true), new OnClickListener() {
                            public void onClick(DialogInterface dialogInterface, int i) {
                                MessagesController.getInstance(baseFragment.getCurrentAccount()).openByUserName("spambot", baseFragment, 1);
                            }
                        });
                        break;
                    case true:
                    case true:
                    case true:
                        if (!z) {
                            builder.setMessage(LocaleController.getString("GroupUserCantAdd", true));
                            break;
                        } else {
                            builder.setMessage(LocaleController.getString("ChannelUserCantAdd", true));
                            break;
                        }
                    case true:
                        if (!z) {
                            builder.setMessage(LocaleController.getString("GroupUserAddLimit", true));
                            break;
                        } else {
                            builder.setMessage(LocaleController.getString("ChannelUserAddLimit", true));
                            break;
                        }
                    case true:
                        if (!z) {
                            builder.setMessage(LocaleController.getString("GroupUserLeftError", true));
                            break;
                        } else {
                            builder.setMessage(LocaleController.getString("ChannelUserLeftError", true));
                            break;
                        }
                    case true:
                        if (!z) {
                            builder.setMessage(LocaleController.getString("GroupUserCantAdmin", true));
                            break;
                        } else {
                            builder.setMessage(LocaleController.getString("ChannelUserCantAdmin", true));
                            break;
                        }
                    case true:
                        if (!z) {
                            builder.setMessage(LocaleController.getString("GroupUserCantBot", true));
                            break;
                        } else {
                            builder.setMessage(LocaleController.getString("ChannelUserCantBot", true));
                            break;
                        }
                    case true:
                        if (!z) {
                            builder.setMessage(LocaleController.getString("InviteToGroupError", true));
                            break;
                        } else {
                            builder.setMessage(LocaleController.getString("InviteToChannelError", true));
                            break;
                        }
                    case true:
                        builder.setMessage(LocaleController.getString("CreateGroupError", true));
                        break;
                    case true:
                        builder.setMessage(LocaleController.getString("UserRestricted", true));
                        break;
                    case true:
                        builder.setMessage(LocaleController.getString("YouBlockedUser", true));
                        break;
                    case true:
                    case true:
                        builder.setMessage(LocaleController.getString("AddAdminErrorBlacklisted", true));
                        break;
                    case true:
                        builder.setMessage(LocaleController.getString("AddAdminErrorNotAMember", true));
                        break;
                    case true:
                        builder.setMessage(LocaleController.getString("AddBannedErrorAdmin", true));
                        break;
                    default:
                        z = new StringBuilder();
                        z.append(LocaleController.getString("ErrorOccurred", C0446R.string.ErrorOccurred));
                        z.append("\n");
                        z.append(str);
                        builder.setMessage(z.toString());
                        break;
                }
                builder.setPositiveButton(LocaleController.getString("OK", true), null);
                baseFragment.showDialog(builder.create(), true, null);
            }
        }
    }

    public static Dialog createColorSelectDialog(Activity activity, long j, boolean z, boolean z2, Runnable runnable) {
        int i;
        Context context = activity;
        final long j2 = j;
        SharedPreferences notificationsSettings = MessagesController.getNotificationsSettings(UserConfig.selectedAccount);
        if (z) {
            i = notificationsSettings.getInt("GroupLed", -16776961);
        } else if (z2) {
            i = notificationsSettings.getInt("MessagesLed", -16776961);
        } else {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("color_");
            stringBuilder.append(j2);
            if (notificationsSettings.contains(stringBuilder.toString())) {
                stringBuilder = new StringBuilder();
                stringBuilder.append("color_");
                stringBuilder.append(j2);
                i = notificationsSettings.getInt(stringBuilder.toString(), -16776961);
            } else if (((int) j2) < 0) {
                i = notificationsSettings.getInt("GroupLed", -16776961);
            } else {
                i = notificationsSettings.getInt("MessagesLed", -16776961);
            }
        }
        final View linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(1);
        String[] strArr = new String[]{LocaleController.getString("ColorRed", C0446R.string.ColorRed), LocaleController.getString("ColorOrange", C0446R.string.ColorOrange), LocaleController.getString("ColorYellow", C0446R.string.ColorYellow), LocaleController.getString("ColorGreen", C0446R.string.ColorGreen), LocaleController.getString("ColorCyan", C0446R.string.ColorCyan), LocaleController.getString("ColorBlue", C0446R.string.ColorBlue), LocaleController.getString("ColorViolet", C0446R.string.ColorViolet), LocaleController.getString("ColorPink", C0446R.string.ColorPink), LocaleController.getString("ColorWhite", C0446R.string.ColorWhite)};
        final int[] iArr = new int[]{i};
        for (int i2 = 0; i2 < 9; i2++) {
            View radioColorCell = new RadioColorCell(context);
            radioColorCell.setPadding(AndroidUtilities.dp(4.0f), 0, AndroidUtilities.dp(4.0f), 0);
            radioColorCell.setTag(Integer.valueOf(i2));
            radioColorCell.setCheckColor(TextColorCell.colors[i2], TextColorCell.colors[i2]);
            radioColorCell.setTextAndValue(strArr[i2], i == TextColorCell.colorsToSave[i2]);
            linearLayout.addView(radioColorCell);
            radioColorCell.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    int childCount = linearLayout.getChildCount();
                    for (int i = 0; i < childCount; i++) {
                        View view2 = (RadioColorCell) linearLayout.getChildAt(i);
                        view2.setChecked(view2 == view, true);
                    }
                    iArr[0] = TextColorCell.colorsToSave[((Integer) view.getTag()).intValue()];
                }
            });
        }
        Builder builder = new Builder(context);
        builder.setTitle(LocaleController.getString("LedColor", C0446R.string.LedColor));
        builder.setView(linearLayout);
        final boolean z3 = z2;
        final int[] iArr2 = iArr;
        final boolean z4 = z;
        final long j3 = j2;
        final Runnable runnable2 = runnable;
        builder.setPositiveButton(LocaleController.getString("Set", C0446R.string.Set), new OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface = MessagesController.getNotificationsSettings(UserConfig.selectedAccount).edit();
                if (z3 != 0) {
                    dialogInterface.putInt("MessagesLed", iArr2[0]);
                } else if (z4 != 0) {
                    dialogInterface.putInt("GroupLed", iArr2[0]);
                } else {
                    i = new StringBuilder();
                    i.append("color_");
                    i.append(j3);
                    dialogInterface.putInt(i.toString(), iArr2[0]);
                }
                dialogInterface.commit();
                if (runnable2 != null) {
                    runnable2.run();
                }
            }
        });
        z3 = z2;
        final boolean z5 = z;
        final long j4 = j2;
        final Runnable runnable3 = runnable;
        builder.setNeutralButton(LocaleController.getString("LedDisabled", C0446R.string.LedDisabled), new OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface = MessagesController.getNotificationsSettings(UserConfig.selectedAccount).edit();
                if (z3 != 0) {
                    dialogInterface.putInt("MessagesLed", 0);
                } else if (z5 != 0) {
                    dialogInterface.putInt("GroupLed", 0);
                } else {
                    i = new StringBuilder();
                    i.append("color_");
                    i.append(j4);
                    dialogInterface.putInt(i.toString(), 0);
                }
                dialogInterface.commit();
                if (runnable3 != null) {
                    runnable3.run();
                }
            }
        });
        if (!(z2 || z)) {
            final Runnable runnable4 = runnable;
            builder.setNegativeButton(LocaleController.getString("Default", C0446R.string.Default), new OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface = MessagesController.getNotificationsSettings(UserConfig.selectedAccount).edit();
                    i = new StringBuilder();
                    i.append("color_");
                    i.append(j2);
                    dialogInterface.remove(i.toString());
                    dialogInterface.commit();
                    if (runnable4 != null) {
                        runnable4.run();
                    }
                }
            });
        }
        return builder.create();
    }

    public static Dialog createVibrationSelectDialog(Activity activity, BaseFragment baseFragment, long j, boolean z, boolean z2, Runnable runnable) {
        z = j != 0 ? "vibrate_" : z ? "vibrate_group" : "vibrate_messages";
        return createVibrationSelectDialog(activity, baseFragment, j, z, runnable);
    }

    public static Dialog createVibrationSelectDialog(Activity activity, BaseFragment baseFragment, long j, String str, Runnable runnable) {
        String[] strArr;
        Context context = activity;
        long j2 = j;
        String str2 = str;
        SharedPreferences notificationsSettings = MessagesController.getNotificationsSettings(UserConfig.selectedAccount);
        boolean z = true;
        int[] iArr = new int[1];
        boolean z2 = false;
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
            strArr = new String[]{LocaleController.getString("VibrationDefault", C0446R.string.VibrationDefault), LocaleController.getString("Short", C0446R.string.Short), LocaleController.getString("Long", C0446R.string.Long), LocaleController.getString("VibrationDisabled", C0446R.string.VibrationDisabled)};
        } else {
            iArr[0] = notificationsSettings.getInt(str2, 0);
            if (iArr[0] == 0) {
                iArr[0] = 1;
            } else if (iArr[0] == 1) {
                iArr[0] = 2;
            } else if (iArr[0] == 2) {
                iArr[0] = 0;
            }
            strArr = new String[]{LocaleController.getString("VibrationDisabled", C0446R.string.VibrationDisabled), LocaleController.getString("VibrationDefault", C0446R.string.VibrationDefault), LocaleController.getString("Short", C0446R.string.Short), LocaleController.getString("Long", C0446R.string.Long), LocaleController.getString("OnlyIfSilent", C0446R.string.OnlyIfSilent)};
        }
        String[] strArr2 = strArr;
        View linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(1);
        int i = 0;
        while (i < strArr2.length) {
            View radioColorCell = new RadioColorCell(context);
            radioColorCell.setPadding(AndroidUtilities.dp(4.0f), z2, AndroidUtilities.dp(4.0f), z2);
            radioColorCell.setTag(Integer.valueOf(i));
            radioColorCell.setCheckColor(Theme.getColor(Theme.key_radioBackground), Theme.getColor(Theme.key_dialogRadioBackgroundChecked));
            radioColorCell.setTextAndValue(strArr2[i], iArr[z2] == i ? z : z2);
            linearLayout.addView(radioColorCell);
            final int[] iArr2 = iArr;
            final long j3 = j2;
            C10788 c10788 = r1;
            final String str3 = str2;
            View view = radioColorCell;
            final BaseFragment baseFragment2 = baseFragment;
            int i2 = i;
            final Runnable runnable2 = runnable;
            C10788 c107882 = new View.OnClickListener() {
                public void onClick(View view) {
                    iArr2[0] = ((Integer) view.getTag()).intValue();
                    view = MessagesController.getNotificationsSettings(UserConfig.selectedAccount).edit();
                    if (j3 != 0) {
                        StringBuilder stringBuilder;
                        if (iArr2[0] == 0) {
                            stringBuilder = new StringBuilder();
                            stringBuilder.append(str3);
                            stringBuilder.append(j3);
                            view.putInt(stringBuilder.toString(), 0);
                        } else if (iArr2[0] == 1) {
                            stringBuilder = new StringBuilder();
                            stringBuilder.append(str3);
                            stringBuilder.append(j3);
                            view.putInt(stringBuilder.toString(), 1);
                        } else if (iArr2[0] == 2) {
                            stringBuilder = new StringBuilder();
                            stringBuilder.append(str3);
                            stringBuilder.append(j3);
                            view.putInt(stringBuilder.toString(), 3);
                        } else if (iArr2[0] == 3) {
                            stringBuilder = new StringBuilder();
                            stringBuilder.append(str3);
                            stringBuilder.append(j3);
                            view.putInt(stringBuilder.toString(), 2);
                        }
                    } else if (iArr2[0] == 0) {
                        view.putInt(str3, 2);
                    } else if (iArr2[0] == 1) {
                        view.putInt(str3, 0);
                    } else if (iArr2[0] == 2) {
                        view.putInt(str3, 1);
                    } else if (iArr2[0] == 3) {
                        view.putInt(str3, 3);
                    } else if (iArr2[0] == 4) {
                        view.putInt(str3, 4);
                    }
                    view.commit();
                    if (baseFragment2 != null) {
                        baseFragment2.dismissCurrentDialig();
                    }
                    if (runnable2 != null) {
                        runnable2.run();
                    }
                }
            };
            view.setOnClickListener(c10788);
            i = i2 + 1;
            z = true;
            z2 = false;
        }
        Builder builder = new Builder(context);
        builder.setTitle(LocaleController.getString("Vibrate", C0446R.string.Vibrate));
        builder.setView(linearLayout);
        builder.setPositiveButton(LocaleController.getString("Cancel", C0446R.string.Cancel), null);
        return builder.create();
    }

    public static Dialog createLocationUpdateDialog(Activity activity, User user, IntCallback intCallback) {
        Context context = activity;
        final int[] iArr = new int[1];
        int i = 3;
        String[] strArr = new String[]{LocaleController.getString("SendLiveLocationFor15m", C0446R.string.SendLiveLocationFor15m), LocaleController.getString("SendLiveLocationFor1h", C0446R.string.SendLiveLocationFor1h), LocaleController.getString("SendLiveLocationFor8h", C0446R.string.SendLiveLocationFor8h)};
        final View linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(1);
        View textView = new TextView(context);
        if (user != null) {
            textView.setText(LocaleController.formatString("LiveLocationAlertPrivate", C0446R.string.LiveLocationAlertPrivate, UserObject.getFirstName(user)));
        } else {
            textView.setText(LocaleController.getString("LiveLocationAlertGroup", C0446R.string.LiveLocationAlertGroup));
        }
        textView.setTextColor(Theme.getColor(Theme.key_dialogTextBlack));
        textView.setTextSize(1, 16.0f);
        textView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
        if (LocaleController.isRTL) {
            i = 5;
        }
        linearLayout.addView(textView, LayoutHelper.createLinear(-2, -2, i | 48, 24, 0, 24, 8));
        i = 0;
        while (i < strArr.length) {
            textView = new RadioColorCell(context);
            textView.setPadding(AndroidUtilities.dp(4.0f), 0, AndroidUtilities.dp(4.0f), 0);
            textView.setTag(Integer.valueOf(i));
            textView.setCheckColor(Theme.getColor(Theme.key_radioBackground), Theme.getColor(Theme.key_dialogRadioBackgroundChecked));
            textView.setTextAndValue(strArr[i], iArr[0] == i);
            linearLayout.addView(textView);
            textView.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    iArr[0] = ((Integer) view.getTag()).intValue();
                    int childCount = linearLayout.getChildCount();
                    for (int i = 0; i < childCount; i++) {
                        View childAt = linearLayout.getChildAt(i);
                        if (childAt instanceof RadioColorCell) {
                            ((RadioColorCell) childAt).setChecked(childAt == view, true);
                        }
                    }
                }
            });
            i++;
        }
        Builder builder = new Builder(context);
        builder.setTopImage(new ShareLocationDrawable(context, false), Theme.getColor(Theme.key_dialogTopBackground));
        builder.setView(linearLayout);
        final IntCallback intCallback2 = intCallback;
        builder.setPositiveButton(LocaleController.getString("ShareFile", C0446R.string.ShareFile), new OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface = iArr[0] == null ? 900 : iArr[0] == 1 ? 3600 : 28800;
                intCallback2.run(dialogInterface);
            }
        });
        builder.setNeutralButton(LocaleController.getString("Cancel", C0446R.string.Cancel), null);
        return builder.create();
    }

    public static Dialog createFreeSpaceDialog(LaunchActivity launchActivity) {
        final Context context = launchActivity;
        final int[] iArr = new int[1];
        int i = MessagesController.getGlobalMainSettings().getInt("keep_media", 2);
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
        String[] strArr = new String[]{LocaleController.formatPluralString("Days", 3), LocaleController.formatPluralString("Weeks", 1), LocaleController.formatPluralString("Months", 1), LocaleController.getString("LowDiskSpaceNeverRemove", C0446R.string.LowDiskSpaceNeverRemove)};
        final View linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(1);
        View textView = new TextView(context);
        textView.setText(LocaleController.getString("LowDiskSpaceTitle2", C0446R.string.LowDiskSpaceTitle2));
        textView.setTextColor(Theme.getColor(Theme.key_dialogTextBlack));
        textView.setTextSize(1, 16.0f);
        textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        textView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
        if (LocaleController.isRTL) {
            i2 = 5;
        }
        linearLayout.addView(textView, LayoutHelper.createLinear(-2, -2, i2 | 48, 24, 0, 24, 8));
        i2 = 0;
        while (i2 < strArr.length) {
            textView = new RadioColorCell(context);
            textView.setPadding(AndroidUtilities.dp(4.0f), 0, AndroidUtilities.dp(4.0f), 0);
            textView.setTag(Integer.valueOf(i2));
            textView.setCheckColor(Theme.getColor(Theme.key_radioBackground), Theme.getColor(Theme.key_dialogRadioBackgroundChecked));
            textView.setTextAndValue(strArr[i2], iArr[0] == i2);
            linearLayout.addView(textView);
            textView.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
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
                    intValue = linearLayout.getChildCount();
                    for (int i = 0; i < intValue; i++) {
                        View childAt = linearLayout.getChildAt(i);
                        if (childAt instanceof RadioColorCell) {
                            ((RadioColorCell) childAt).setChecked(childAt == view, true);
                        }
                    }
                }
            });
            i2++;
        }
        Builder builder = new Builder(context);
        builder.setTitle(LocaleController.getString("LowDiskSpaceTitle", C0446R.string.LowDiskSpaceTitle));
        builder.setMessage(LocaleController.getString("LowDiskSpaceMessage", C0446R.string.LowDiskSpaceMessage));
        builder.setView(linearLayout);
        builder.setPositiveButton(LocaleController.getString("OK", C0446R.string.OK), new OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                MessagesController.getGlobalMainSettings().edit().putInt("keep_media", iArr[0]).commit();
            }
        });
        builder.setNeutralButton(LocaleController.getString("ClearMediaCache", C0446R.string.ClearMediaCache), new OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                context.presentFragment(new CacheControlActivity());
            }
        });
        return builder.create();
    }

    public static Dialog createPrioritySelectDialog(Activity activity, BaseFragment baseFragment, long j, boolean z, boolean z2, Runnable runnable) {
        String[] strArr;
        Context context = activity;
        long j2 = j;
        SharedPreferences notificationsSettings = MessagesController.getNotificationsSettings(UserConfig.selectedAccount);
        boolean z3 = true;
        int[] iArr = new int[1];
        boolean z4 = false;
        if (j2 != 0) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("priority_");
            stringBuilder.append(j2);
            iArr[0] = notificationsSettings.getInt(stringBuilder.toString(), 3);
            if (iArr[0] == 3) {
                iArr[0] = 0;
            } else if (iArr[0] == 4) {
                iArr[0] = 1;
            } else if (iArr[0] == 5) {
                iArr[0] = 2;
            } else if (iArr[0] == 0) {
                iArr[0] = 3;
            } else {
                iArr[0] = 4;
            }
            strArr = new String[]{LocaleController.getString("NotificationsPrioritySettings", C0446R.string.NotificationsPrioritySettings), LocaleController.getString("NotificationsPriorityLow", C0446R.string.NotificationsPriorityLow), LocaleController.getString("NotificationsPriorityMedium", C0446R.string.NotificationsPriorityMedium), LocaleController.getString("NotificationsPriorityHigh", C0446R.string.NotificationsPriorityHigh), LocaleController.getString("NotificationsPriorityUrgent", C0446R.string.NotificationsPriorityUrgent)};
        } else {
            if (z2) {
                iArr[0] = notificationsSettings.getInt("priority_messages", 1);
            } else if (z) {
                iArr[0] = notificationsSettings.getInt("priority_group", 1);
            }
            if (iArr[0] == 4) {
                iArr[0] = 0;
            } else if (iArr[0] == 5) {
                iArr[0] = 1;
            } else if (iArr[0] == 0) {
                iArr[0] = 2;
            } else {
                iArr[0] = 3;
            }
            strArr = new String[]{LocaleController.getString("NotificationsPriorityLow", C0446R.string.NotificationsPriorityLow), LocaleController.getString("NotificationsPriorityMedium", C0446R.string.NotificationsPriorityMedium), LocaleController.getString("NotificationsPriorityHigh", C0446R.string.NotificationsPriorityHigh), LocaleController.getString("NotificationsPriorityUrgent", C0446R.string.NotificationsPriorityUrgent)};
        }
        String[] strArr2 = strArr;
        View linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(1);
        int i = 0;
        while (i < strArr2.length) {
            View radioColorCell = new RadioColorCell(context);
            radioColorCell.setPadding(AndroidUtilities.dp(4.0f), z4, AndroidUtilities.dp(4.0f), z4);
            radioColorCell.setTag(Integer.valueOf(i));
            radioColorCell.setCheckColor(Theme.getColor(Theme.key_radioBackground), Theme.getColor(Theme.key_dialogRadioBackgroundChecked));
            radioColorCell.setTextAndValue(strArr2[i], iArr[z4] == i ? z3 : z4);
            linearLayout.addView(radioColorCell);
            final int[] iArr2 = iArr;
            final long j3 = j2;
            final boolean z5 = z;
            AnonymousClass14 anonymousClass14 = r1;
            final BaseFragment baseFragment2 = baseFragment;
            View view = radioColorCell;
            final Runnable runnable2 = runnable;
            AnonymousClass14 anonymousClass142 = new View.OnClickListener() {
                public void onClick(View view) {
                    int i = 0;
                    iArr2[0] = ((Integer) view.getTag()).intValue();
                    view = MessagesController.getNotificationsSettings(UserConfig.selectedAccount).edit();
                    if (j3 != 0) {
                        if (iArr2[0] == 0) {
                            i = 3;
                        } else if (iArr2[0] == 1) {
                            i = 4;
                        } else if (iArr2[0] == 2) {
                            i = 5;
                        } else if (iArr2[0] != 3) {
                            i = 1;
                        }
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append("priority_");
                        stringBuilder.append(j3);
                        view.putInt(stringBuilder.toString(), i);
                    } else {
                        if (iArr2[0] == 0) {
                            i = 4;
                        } else if (iArr2[0] == 1) {
                            i = 5;
                        } else if (iArr2[0] != 2) {
                            i = 1;
                        }
                        view.putInt(z5 ? "priority_group" : "priority_messages", i);
                    }
                    view.commit();
                    if (baseFragment2 != null) {
                        baseFragment2.dismissCurrentDialig();
                    }
                    if (runnable2 != null) {
                        runnable2.run();
                    }
                }
            };
            view.setOnClickListener(anonymousClass14);
            i++;
            z3 = true;
            z4 = false;
        }
        Builder builder = new Builder(context);
        builder.setTitle(LocaleController.getString("NotificationsImportance", C0446R.string.NotificationsImportance));
        builder.setView(linearLayout);
        builder.setPositiveButton(LocaleController.getString("Cancel", C0446R.string.Cancel), null);
        return builder.create();
    }

    public static Dialog createPopupSelectDialog(Activity activity, final BaseFragment baseFragment, final boolean z, boolean z2, final Runnable runnable) {
        SharedPreferences notificationsSettings = MessagesController.getNotificationsSettings(UserConfig.selectedAccount);
        final int[] iArr = new int[1];
        if (z2) {
            iArr[0] = notificationsSettings.getInt("popupAll", 0);
        } else if (z) {
            iArr[0] = notificationsSettings.getInt("popupGroup", 0);
        }
        z2 = new String[]{LocaleController.getString("NoPopup", C0446R.string.NoPopup), LocaleController.getString("OnlyWhenScreenOn", C0446R.string.OnlyWhenScreenOn), LocaleController.getString("OnlyWhenScreenOff", C0446R.string.OnlyWhenScreenOff), LocaleController.getString("AlwaysShowPopup", C0446R.string.AlwaysShowPopup)};
        View linearLayout = new LinearLayout(activity);
        linearLayout.setOrientation(1);
        int i = 0;
        while (i < z2.length) {
            View radioColorCell = new RadioColorCell(activity);
            radioColorCell.setTag(Integer.valueOf(i));
            radioColorCell.setPadding(AndroidUtilities.dp(4.0f), 0, AndroidUtilities.dp(4.0f), 0);
            radioColorCell.setCheckColor(Theme.getColor(Theme.key_radioBackground), Theme.getColor(Theme.key_dialogRadioBackgroundChecked));
            radioColorCell.setTextAndValue(z2[i], iArr[0] == i);
            linearLayout.addView(radioColorCell);
            radioColorCell.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    iArr[0] = ((Integer) view.getTag()).intValue();
                    view = MessagesController.getNotificationsSettings(UserConfig.selectedAccount).edit();
                    view.putInt(z ? "popupGroup" : "popupAll", iArr[0]);
                    view.commit();
                    if (baseFragment != null) {
                        baseFragment.dismissCurrentDialig();
                    }
                    if (runnable != null) {
                        runnable.run();
                    }
                }
            });
            i++;
        }
        baseFragment = new Builder((Context) activity);
        baseFragment.setTitle(LocaleController.getString("PopupNotification", true));
        baseFragment.setView(linearLayout);
        baseFragment.setPositiveButton(LocaleController.getString("Cancel", true), false);
        return baseFragment.create();
    }

    public static Dialog createSingleChoiceDialog(Activity activity, final BaseFragment baseFragment, String[] strArr, String str, int i, final OnClickListener onClickListener) {
        View linearLayout = new LinearLayout(activity);
        linearLayout.setOrientation(1);
        int i2 = 0;
        while (i2 < strArr.length) {
            View radioColorCell = new RadioColorCell(activity);
            radioColorCell.setPadding(AndroidUtilities.dp(4.0f), 0, AndroidUtilities.dp(4.0f), 0);
            radioColorCell.setTag(Integer.valueOf(i2));
            radioColorCell.setCheckColor(Theme.getColor(Theme.key_radioBackground), Theme.getColor(Theme.key_dialogRadioBackgroundChecked));
            radioColorCell.setTextAndValue(strArr[i2], i == i2);
            linearLayout.addView(radioColorCell);
            radioColorCell.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    view = ((Integer) view.getTag()).intValue();
                    if (baseFragment != null) {
                        baseFragment.dismissCurrentDialig();
                    }
                    onClickListener.onClick(null, view);
                }
            });
            i2++;
        }
        baseFragment = new Builder((Context) activity);
        baseFragment.setTitle(str);
        baseFragment.setView(linearLayout);
        baseFragment.setPositiveButton(LocaleController.getString("Cancel", NUM), null);
        return baseFragment.create();
    }

    public static Builder createTTLAlert(Context context, final EncryptedChat encryptedChat) {
        Builder builder = new Builder(context);
        builder.setTitle(LocaleController.getString("MessageLifetime", C0446R.string.MessageLifetime));
        final View numberPicker = new NumberPicker(context);
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
            public String format(int i) {
                if (i == 0) {
                    return LocaleController.getString("ShortMessageLifetimeForever", C0446R.string.ShortMessageLifetimeForever);
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
                return i == 20 ? LocaleController.formatTTLString(604800) : TtmlNode.ANONYMOUS_REGION_ID;
            }
        });
        builder.setView(numberPicker);
        builder.setNegativeButton(LocaleController.getString("Done", C0446R.string.Done), new OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface = encryptedChat.ttl;
                i = numberPicker.getValue();
                if (i >= 0 && i < 16) {
                    encryptedChat.ttl = i;
                } else if (i == 16) {
                    encryptedChat.ttl = 30;
                } else if (i == 17) {
                    encryptedChat.ttl = 60;
                } else if (i == 18) {
                    encryptedChat.ttl = 3600;
                } else if (i == 19) {
                    encryptedChat.ttl = 86400;
                } else if (i == 20) {
                    encryptedChat.ttl = 604800;
                }
                if (dialogInterface != encryptedChat.ttl) {
                    SecretChatHelper.getInstance(UserConfig.selectedAccount).sendTTLMessage(encryptedChat, null);
                    MessagesStorage.getInstance(UserConfig.selectedAccount).updateEncryptedChatTTL(encryptedChat);
                }
            }
        });
        return builder;
    }
}
