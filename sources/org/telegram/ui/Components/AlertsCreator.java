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
import org.telegram.tgnet.TLRPC.TL_channelParticipantAdmin;
import org.telegram.tgnet.TLRPC.TL_channelParticipantCreator;
import org.telegram.tgnet.TLRPC.TL_channels_channelParticipant;
import org.telegram.tgnet.TLRPC.TL_channels_createChannel;
import org.telegram.tgnet.TLRPC.TL_channels_editAdmin;
import org.telegram.tgnet.TLRPC.TL_channels_editBanned;
import org.telegram.tgnet.TLRPC.TL_channels_inviteToChannel;
import org.telegram.tgnet.TLRPC.TL_channels_joinChannel;
import org.telegram.tgnet.TLRPC.TL_contacts_importContacts;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_help_getSupport;
import org.telegram.tgnet.TLRPC.TL_help_support;
import org.telegram.tgnet.TLRPC.TL_inputReportReasonChildAbuse;
import org.telegram.tgnet.TLRPC.TL_inputReportReasonPornography;
import org.telegram.tgnet.TLRPC.TL_inputReportReasonSpam;
import org.telegram.tgnet.TLRPC.TL_inputReportReasonViolence;
import org.telegram.tgnet.TLRPC.TL_langPackLanguage;
import org.telegram.tgnet.TLRPC.TL_messages_addChatUser;
import org.telegram.tgnet.TLRPC.TL_messages_createChat;
import org.telegram.tgnet.TLRPC.TL_messages_editChatAdmin;
import org.telegram.tgnet.TLRPC.TL_messages_editChatDefaultBannedRights;
import org.telegram.tgnet.TLRPC.TL_messages_editMessage;
import org.telegram.tgnet.TLRPC.TL_messages_forwardMessages;
import org.telegram.tgnet.TLRPC.TL_messages_getAttachedStickers;
import org.telegram.tgnet.TLRPC.TL_messages_importChatInvite;
import org.telegram.tgnet.TLRPC.TL_messages_migrateChat;
import org.telegram.tgnet.TLRPC.TL_messages_report;
import org.telegram.tgnet.TLRPC.TL_messages_sendBroadcast;
import org.telegram.tgnet.TLRPC.TL_messages_sendInlineBotResult;
import org.telegram.tgnet.TLRPC.TL_messages_sendMedia;
import org.telegram.tgnet.TLRPC.TL_messages_sendMessage;
import org.telegram.tgnet.TLRPC.TL_messages_sendMultiMedia;
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

    static /* synthetic */ void lambda$null$22(TLObject tLObject, TL_error tL_error) {
    }

    static /* synthetic */ void lambda$null$50(TLObject tLObject, TL_error tL_error) {
    }

    public static Dialog processError(int i, TL_error tL_error, BaseFragment baseFragment, TLObject tLObject, Object... objArr) {
        TL_error tL_error2 = tL_error;
        BaseFragment baseFragment2 = baseFragment;
        TLObject tLObject2 = tLObject;
        int i2 = tL_error2.code;
        if (i2 != 406) {
            String str = tL_error2.text;
            if (str != null) {
                String str2 = "\n";
                String str3 = "InvalidPhoneNumber";
                String str4 = "PHONE_NUMBER_INVALID";
                String str5 = "ErrorOccurred";
                String str6 = "FloodWait";
                String str7 = "FLOOD_WAIT";
                StringBuilder stringBuilder;
                if ((tLObject2 instanceof TL_account_saveSecureValue) || (tLObject2 instanceof TL_account_getAuthorizationForm)) {
                    if (tL_error2.text.contains(str4)) {
                        showSimpleAlert(baseFragment2, LocaleController.getString(str3, NUM));
                    } else if (tL_error2.text.startsWith(str7)) {
                        showSimpleAlert(baseFragment2, LocaleController.getString(str6, NUM));
                    } else {
                        if ("APP_VERSION_OUTDATED".equals(tL_error2.text)) {
                            showUpdateAppAlert(baseFragment.getParentActivity(), LocaleController.getString("UpdateAppAlert", NUM), true);
                        } else {
                            stringBuilder = new StringBuilder();
                            stringBuilder.append(LocaleController.getString(str5, NUM));
                            stringBuilder.append(str2);
                            stringBuilder.append(tL_error2.text);
                            showSimpleAlert(baseFragment2, stringBuilder.toString());
                        }
                    }
                } else if ((tLObject2 instanceof TL_channels_joinChannel) || (tLObject2 instanceof TL_channels_editAdmin) || (tLObject2 instanceof TL_channels_inviteToChannel) || (tLObject2 instanceof TL_messages_addChatUser) || (tLObject2 instanceof TL_messages_startBot) || (tLObject2 instanceof TL_channels_editBanned) || (tLObject2 instanceof TL_messages_editChatDefaultBannedRights) || (tLObject2 instanceof TL_messages_editChatAdmin) || (tLObject2 instanceof TL_messages_migrateChat)) {
                    if (baseFragment2 != null) {
                        showAddUserAlert(tL_error2.text, baseFragment2, ((Boolean) objArr[0]).booleanValue());
                    } else if (tL_error2.text.equals("PEER_FLOOD")) {
                        NotificationCenter.getInstance(i).postNotificationName(NotificationCenter.needShowAlert, Integer.valueOf(1));
                    }
                } else if (tLObject2 instanceof TL_messages_createChat) {
                    if (str.startsWith(str7)) {
                        showFloodWaitAlert(tL_error2.text, baseFragment2);
                    } else {
                        showAddUserAlert(tL_error2.text, baseFragment2, false);
                    }
                } else if (tLObject2 instanceof TL_channels_createChannel) {
                    if (str.startsWith(str7)) {
                        showFloodWaitAlert(tL_error2.text, baseFragment2);
                    } else {
                        showAddUserAlert(tL_error2.text, baseFragment2, false);
                    }
                } else if (tLObject2 instanceof TL_messages_editMessage) {
                    if (!str.equals("MESSAGE_NOT_MODIFIED")) {
                        if (baseFragment2 != null) {
                            showSimpleAlert(baseFragment2, LocaleController.getString("EditMessageError", NUM));
                        } else {
                            showSimpleToast(baseFragment2, LocaleController.getString("EditMessageError", NUM));
                        }
                    }
                } else if ((tLObject2 instanceof TL_messages_sendMessage) || (tLObject2 instanceof TL_messages_sendMedia) || (tLObject2 instanceof TL_messages_sendBroadcast) || (tLObject2 instanceof TL_messages_sendInlineBotResult) || (tLObject2 instanceof TL_messages_forwardMessages) || (tLObject2 instanceof TL_messages_sendMultiMedia)) {
                    if (tL_error2.text.equals("PEER_FLOOD")) {
                        NotificationCenter.getInstance(i).postNotificationName(NotificationCenter.needShowAlert, Integer.valueOf(0));
                    } else if (tL_error2.text.equals("USER_BANNED_IN_CHANNEL")) {
                        NotificationCenter.getInstance(i).postNotificationName(NotificationCenter.needShowAlert, Integer.valueOf(5));
                    }
                } else if (tLObject2 instanceof TL_messages_importChatInvite) {
                    if (str.startsWith(str7)) {
                        showSimpleAlert(baseFragment2, LocaleController.getString(str6, NUM));
                    } else if (tL_error2.text.equals("USERS_TOO_MUCH")) {
                        showSimpleAlert(baseFragment2, LocaleController.getString("JoinToGroupErrorFull", NUM));
                    } else {
                        showSimpleAlert(baseFragment2, LocaleController.getString("JoinToGroupErrorNotExist", NUM));
                    }
                } else if (!(tLObject2 instanceof TL_messages_getAttachedStickers)) {
                    String str8 = "PHONE_CODE_INVALID";
                    String str9 = "InvalidCode";
                    String str10 = "PHONE_CODE_EMPTY";
                    int i3;
                    if ((tLObject2 instanceof TL_account_confirmPhone) || (tLObject2 instanceof TL_account_verifyPhone) || (tLObject2 instanceof TL_account_verifyEmail)) {
                        if (tL_error2.text.contains(str10) || tL_error2.text.contains(str8) || tL_error2.text.contains("CODE_INVALID") || tL_error2.text.contains("CODE_EMPTY")) {
                            return showSimpleAlert(baseFragment2, LocaleController.getString(str9, NUM));
                        }
                        if (tL_error2.text.contains("PHONE_CODE_EXPIRED") || tL_error2.text.contains("EMAIL_VERIFY_EXPIRED")) {
                            return showSimpleAlert(baseFragment2, LocaleController.getString("CodeExpired", NUM));
                        }
                        if (tL_error2.text.startsWith(str7)) {
                            return showSimpleAlert(baseFragment2, LocaleController.getString(str6, NUM));
                        }
                        return showSimpleAlert(baseFragment2, tL_error2.text);
                    } else if (tLObject2 instanceof TL_auth_resendCode) {
                        if (str.contains(str4)) {
                            return showSimpleAlert(baseFragment2, LocaleController.getString(str3, NUM));
                        }
                        if (tL_error2.text.contains(str10) || tL_error2.text.contains(str8)) {
                            return showSimpleAlert(baseFragment2, LocaleController.getString(str9, NUM));
                        }
                        if (tL_error2.text.contains("PHONE_CODE_EXPIRED")) {
                            return showSimpleAlert(baseFragment2, LocaleController.getString("CodeExpired", NUM));
                        }
                        if (tL_error2.text.startsWith(str7)) {
                            return showSimpleAlert(baseFragment2, LocaleController.getString(str6, NUM));
                        }
                        if (tL_error2.code != -1000) {
                            stringBuilder = new StringBuilder();
                            stringBuilder.append(LocaleController.getString(str5, NUM));
                            stringBuilder.append(str2);
                            stringBuilder.append(tL_error2.text);
                            return showSimpleAlert(baseFragment2, stringBuilder.toString());
                        }
                    } else if (tLObject2 instanceof TL_account_sendConfirmPhoneCode) {
                        if (i2 == 400) {
                            return showSimpleAlert(baseFragment2, LocaleController.getString("CancelLinkExpired", NUM));
                        }
                        if (str != null) {
                            if (str.startsWith(str7)) {
                                return showSimpleAlert(baseFragment2, LocaleController.getString(str6, NUM));
                            }
                            return showSimpleAlert(baseFragment2, LocaleController.getString(str5, NUM));
                        }
                    } else if (tLObject2 instanceof TL_account_changePhone) {
                        if (str.contains(str4)) {
                            showSimpleAlert(baseFragment2, LocaleController.getString(str3, NUM));
                        } else if (tL_error2.text.contains(str10) || tL_error2.text.contains(str8)) {
                            showSimpleAlert(baseFragment2, LocaleController.getString(str9, NUM));
                        } else if (tL_error2.text.contains("PHONE_CODE_EXPIRED")) {
                            showSimpleAlert(baseFragment2, LocaleController.getString("CodeExpired", NUM));
                        } else if (tL_error2.text.startsWith(str7)) {
                            showSimpleAlert(baseFragment2, LocaleController.getString(str6, NUM));
                        } else {
                            showSimpleAlert(baseFragment2, tL_error2.text);
                        }
                    } else if (tLObject2 instanceof TL_account_sendChangePhoneCode) {
                        if (str.contains(str4)) {
                            showSimpleAlert(baseFragment2, LocaleController.getString(str3, NUM));
                        } else if (tL_error2.text.contains(str10) || tL_error2.text.contains(str8)) {
                            showSimpleAlert(baseFragment2, LocaleController.getString(str9, NUM));
                        } else if (tL_error2.text.contains("PHONE_CODE_EXPIRED")) {
                            showSimpleAlert(baseFragment2, LocaleController.getString("CodeExpired", NUM));
                        } else if (tL_error2.text.startsWith(str7)) {
                            showSimpleAlert(baseFragment2, LocaleController.getString(str6, NUM));
                        } else if (tL_error2.text.startsWith("PHONE_NUMBER_OCCUPIED")) {
                            showSimpleAlert(baseFragment2, LocaleController.formatString("ChangePhoneNumberOccupied", NUM, (String) objArr[0]));
                        } else {
                            showSimpleAlert(baseFragment2, LocaleController.getString(str5, NUM));
                        }
                    } else if (tLObject2 instanceof TL_updateUserName) {
                        i3 = -1;
                        int hashCode = str.hashCode();
                        if (hashCode != NUM) {
                            if (hashCode == NUM && str.equals("USERNAME_OCCUPIED")) {
                                i3 = 1;
                            }
                        } else if (str.equals("USERNAME_INVALID")) {
                            i3 = 0;
                        }
                        if (i3 == 0) {
                            showSimpleAlert(baseFragment2, LocaleController.getString("UsernameInvalid", NUM));
                        } else if (i3 != 1) {
                            showSimpleAlert(baseFragment2, LocaleController.getString(str5, NUM));
                        } else {
                            showSimpleAlert(baseFragment2, LocaleController.getString("UsernameInUse", NUM));
                        }
                    } else if (tLObject2 instanceof TL_contacts_importContacts) {
                        if (tL_error2 == null || str.startsWith(str7)) {
                            showSimpleAlert(baseFragment2, LocaleController.getString(str6, NUM));
                        } else {
                            stringBuilder = new StringBuilder();
                            stringBuilder.append(LocaleController.getString(str5, NUM));
                            stringBuilder.append(str2);
                            stringBuilder.append(tL_error2.text);
                            showSimpleAlert(baseFragment2, stringBuilder.toString());
                        }
                    } else if ((tLObject2 instanceof TL_account_getPassword) || (tLObject2 instanceof TL_account_getTmpPassword)) {
                        if (tL_error2.text.startsWith(str7)) {
                            showSimpleToast(baseFragment2, getFloodWaitString(tL_error2.text));
                        } else {
                            showSimpleToast(baseFragment2, tL_error2.text);
                        }
                    } else if (tLObject2 instanceof TL_payments_sendPaymentForm) {
                        i3 = -1;
                        int hashCode2 = str.hashCode();
                        if (hashCode2 != -NUM) {
                            if (hashCode2 == -NUM && str.equals("PAYMENT_FAILED")) {
                                i3 = 1;
                            }
                        } else if (str.equals("BOT_PRECHECKOUT_FAILED")) {
                            i3 = 0;
                        }
                        if (i3 == 0) {
                            showSimpleToast(baseFragment2, LocaleController.getString("PaymentPrecheckoutFailed", NUM));
                        } else if (i3 != 1) {
                            showSimpleToast(baseFragment2, tL_error2.text);
                        } else {
                            showSimpleToast(baseFragment2, LocaleController.getString("PaymentFailed", NUM));
                        }
                    } else if (tLObject2 instanceof TL_payments_validateRequestedInfo) {
                        Object obj = -1;
                        if (str.hashCode() == NUM && str.equals("SHIPPING_NOT_AVAILABLE")) {
                            obj = null;
                        }
                        if (obj != null) {
                            showSimpleToast(baseFragment2, tL_error2.text);
                        } else {
                            showSimpleToast(baseFragment2, LocaleController.getString("PaymentNoShippingMethod", NUM));
                        }
                    }
                } else if (!(baseFragment2 == null || baseFragment.getParentActivity() == null)) {
                    Activity parentActivity = baseFragment.getParentActivity();
                    stringBuilder = new StringBuilder();
                    stringBuilder.append(LocaleController.getString(str5, NUM));
                    stringBuilder.append(str2);
                    stringBuilder.append(tL_error2.text);
                    Toast.makeText(parentActivity, stringBuilder.toString(), 0).show();
                }
                return null;
            }
        }
        return null;
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
        r1 = NUM; // 0x7f0d01be float:1.874302E38 double:1.053129998E-314;
        r9 = new java.lang.Object[r4];
        r10 = org.telegram.messenger.UserObject.getFirstName(r21);
        r9[r8] = r10;
        r10 = "BlockUserTitle";
        r1 = org.telegram.messenger.LocaleController.formatString(r10, r1, r9);
        r11.setTitle(r1);
        r1 = NUM; // 0x7f0d01ba float:1.8743011E38 double:1.053129996E-314;
        r9 = new java.lang.Object[r4];
        r10 = org.telegram.messenger.UserObject.getFirstName(r21);
        r9[r8] = r10;
        r10 = "BlockUserAlert";
        r1 = org.telegram.messenger.LocaleController.formatString(r10, r1, r9);
        r1 = org.telegram.messenger.AndroidUtilities.replaceTags(r1);
        r11.setMessage(r1);
        r1 = NUM; // 0x7f0d01b8 float:1.8743007E38 double:1.053129995E-314;
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
        r12 = NUM; // 0x7f0d036f float:1.8743898E38 double:1.053130212E-314;
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
        r12 = NUM; // 0x7f0d0372 float:1.8743904E38 double:1.0531302133E-314;
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
        r2 = NUM; // 0x7f0d08e7 float:1.8746737E38 double:1.0531309035E-314;
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
        r2 = NUM; // 0x7f0d08e8 float:1.874674E38 double:1.053130904E-314;
        r4 = new java.lang.Object[r4];
        r1 = r1.address;
        r4[r8] = r1;
        r1 = "ReportUnrelatedGroupText";
        r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r4);
        r1 = org.telegram.messenger.AndroidUtilities.replaceTags(r1);
        r11.setMessage(r1);
        goto L_0x0199;
    L_0x015d:
        r1 = NUM; // 0x7f0d08e9 float:1.8746741E38 double:1.0531309045E-314;
        r2 = "ReportUnrelatedGroupTextNoAddress";
        r1 = org.telegram.messenger.LocaleController.getString(r2, r1);
        r11.setMessage(r1);
        goto L_0x0199;
    L_0x016a:
        r1 = NUM; // 0x7f0d08e5 float:1.8746733E38 double:1.0531309025E-314;
        r2 = "ReportSpamTitle";
        r1 = org.telegram.messenger.LocaleController.getString(r2, r1);
        r11.setTitle(r1);
        r1 = org.telegram.messenger.ChatObject.isChannel(r22);
        if (r1 == 0) goto L_0x018d;
    L_0x017c:
        r1 = r7.megagroup;
        if (r1 != 0) goto L_0x018d;
    L_0x0180:
        r1 = NUM; // 0x7f0d08e1 float:1.8746725E38 double:1.0531309006E-314;
        r2 = "ReportSpamAlertChannel";
        r1 = org.telegram.messenger.LocaleController.getString(r2, r1);
        r11.setMessage(r1);
        goto L_0x0199;
    L_0x018d:
        r1 = NUM; // 0x7f0d08e2 float:1.8746727E38 double:1.053130901E-314;
        r2 = "ReportSpamAlertGroup";
        r1 = org.telegram.messenger.LocaleController.getString(r2, r1);
        r11.setMessage(r1);
    L_0x0199:
        r1 = NUM; // 0x7f0d08d7 float:1.8746705E38 double:1.0531308956E-314;
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
        r1 = NUM; // 0x7f0d01f9 float:1.874314E38 double:1.053130027E-314;
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
        builder.setItems(charSequenceArr, new -$$Lambda$AlertsCreator$qP34Mv8lMVUGhvd7xA3QNsAi4yI(j));
        return builder.create();
    }

    static /* synthetic */ void lambda$createMuteAlert$21(long j, DialogInterface dialogInterface, int i) {
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
            builder.setItems(new CharSequence[]{LocaleController.getString("ReportChatSpam", NUM), LocaleController.getString("ReportChatViolence", NUM), LocaleController.getString("ReportChatChild", NUM), LocaleController.getString("ReportChatPornography", NUM), LocaleController.getString("ReportChatOther", NUM)}, new -$$Lambda$AlertsCreator$9i3XB_1s2n8aR5VYe0IfcGoMi5U(j, i, baseFragment, context));
            baseFragment.showDialog(builder.create());
        }
    }

    static /* synthetic */ void lambda$createReportAlert$23(long j, int i, BaseFragment baseFragment, Context context, DialogInterface dialogInterface, int i2) {
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
        ConnectionsManager.getInstance(UserConfig.selectedAccount).sendRequest(tL_messages_report, -$$Lambda$AlertsCreator$UxVaqBigD07vFNnYzgdLn2FpVvQ.INSTANCE);
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

    public static void showAddUserAlert(String str, BaseFragment baseFragment, boolean z) {
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
                    builder.setNegativeButton(LocaleController.getString("MoreInfo", NUM), new -$$Lambda$AlertsCreator$sx0DqPAT4b_e8p2dEyXX9pNKVSQ(baseFragment));
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
                    builder.setMessage(LocaleController.getString("ChannelTooMuch", NUM));
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
            radioColorCell.setOnClickListener(new -$$Lambda$AlertsCreator$aAOx8jDmjnX8cGdPe5oKfarlNH4(linearLayout, iArr));
            i5++;
            i4 = 9;
        }
        Builder builder = new Builder(context);
        builder.setTitle(LocaleController.getString("LedColor", NUM));
        builder.setView(linearLayout);
        builder.setPositiveButton(LocaleController.getString("Set", NUM), new -$$Lambda$AlertsCreator$3BZr93fBgAg0s8pBHQk9upjZs64(j, iArr, i, runnable));
        builder.setNeutralButton(LocaleController.getString("LedDisabled", NUM), new -$$Lambda$AlertsCreator$3DHMlaDe6QSChdTvar_UrxhP4W8(j2, i3, runnable2));
        if (j2 != 0) {
            builder.setNegativeButton(LocaleController.getString("Default", NUM), new -$$Lambda$AlertsCreator$-A91nhgP4-jdEgqUq_lmbLQMD7c(j2, runnable2));
        }
        return builder.create();
    }

    static /* synthetic */ void lambda$createColorSelectDialog$25(LinearLayout linearLayout, int[] iArr, View view) {
        int childCount = linearLayout.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View view2 = (RadioColorCell) linearLayout.getChildAt(i);
            view2.setChecked(view2 == view, true);
        }
        iArr[0] = TextColorCell.colorsToSave[((Integer) view.getTag()).intValue()];
    }

    static /* synthetic */ void lambda$createColorSelectDialog$26(long j, int[] iArr, int i, Runnable runnable, DialogInterface dialogInterface, int i2) {
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

    static /* synthetic */ void lambda$createColorSelectDialog$27(long j, int i, Runnable runnable, DialogInterface dialogInterface, int i2) {
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

    static /* synthetic */ void lambda$createColorSelectDialog$28(long j, Runnable runnable, DialogInterface dialogInterface, int i) {
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
            -$$Lambda$AlertsCreator$q45YmK4nzIOKkdoA-mqrJHyj2RQ -__lambda_alertscreator_q45ymk4nziokkdoa-mqrjhyj2rq = r1;
            FrameLayout frameLayout = radioColorCell;
            int i3 = i2;
            builder = builder2;
            -$$Lambda$AlertsCreator$q45YmK4nzIOKkdoA-mqrJHyj2RQ -__lambda_alertscreator_q45ymk4nziokkdoa-mqrjhyj2rq2 = new -$$Lambda$AlertsCreator$q45YmK4nzIOKkdoA-mqrJHyj2RQ(iArr, j, str, builder2, runnable);
            frameLayout.setOnClickListener(-__lambda_alertscreator_q45ymk4nziokkdoa-mqrjhyj2rq);
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

    static /* synthetic */ void lambda$createVibrationSelectDialog$29(int[] iArr, long j, String str, Builder builder, Runnable runnable, View view) {
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
            radioColorCell.setOnClickListener(new -$$Lambda$AlertsCreator$BgH_T0jBqiNpwZm7K9zrq39D34M(iArr, linearLayout));
            i++;
        }
        Builder builder = new Builder(context);
        builder.setTopImage(new ShareLocationDrawable(context, 0), Theme.getColor("dialogTopBackground"));
        builder.setView(linearLayout);
        builder.setPositiveButton(LocaleController.getString("ShareFile", NUM), new -$$Lambda$AlertsCreator$AIqt27WVxuBhywTShlGfx2vMy-g(iArr, intCallback));
        builder.setNeutralButton(LocaleController.getString("Cancel", NUM), null);
        return builder.create();
    }

    static /* synthetic */ void lambda$createLocationUpdateDialog$30(int[] iArr, LinearLayout linearLayout, View view) {
        iArr[0] = ((Integer) view.getTag()).intValue();
        int childCount = linearLayout.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childAt = linearLayout.getChildAt(i);
            if (childAt instanceof RadioColorCell) {
                ((RadioColorCell) childAt).setChecked(childAt == view, true);
            }
        }
    }

    static /* synthetic */ void lambda$createLocationUpdateDialog$31(int[] iArr, IntCallback intCallback, DialogInterface dialogInterface, int i) {
        int i2 = iArr[0] == 0 ? 900 : iArr[0] == 1 ? 3600 : 28800;
        intCallback.run(i2);
    }

    public static Builder createContactsPermissionDialog(Activity activity, IntCallback intCallback) {
        Builder builder = new Builder((Context) activity);
        builder.setTopImage(NUM, Theme.getColor("dialogTopBackground"));
        builder.setMessage(AndroidUtilities.replaceTags(LocaleController.getString("ContactsPermissionAlert", NUM)));
        builder.setPositiveButton(LocaleController.getString("ContactsPermissionAlertContinue", NUM), new -$$Lambda$AlertsCreator$2WKM6EE4pnafA8hrEjM2a28eOZ0(intCallback));
        builder.setNegativeButton(LocaleController.getString("ContactsPermissionAlertNotNow", NUM), new -$$Lambda$AlertsCreator$DaSEJdkOcvtWFVjCAqdRSYEw56M(intCallback));
        return builder;
    }

    public static Dialog createFreeSpaceDialog(LaunchActivity launchActivity) {
        Context context = launchActivity;
        int[] iArr = new int[1];
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
            radioColorCell.setOnClickListener(new -$$Lambda$AlertsCreator$qhbvkYBNfJNe3_KDI1O0kM5ZQIE(iArr, linearLayout));
            i2++;
        }
        Builder builder = new Builder(context);
        builder.setTitle(LocaleController.getString("LowDiskSpaceTitle", NUM));
        builder.setMessage(LocaleController.getString("LowDiskSpaceMessage", NUM));
        builder.setView(linearLayout);
        builder.setPositiveButton(LocaleController.getString("OK", NUM), new -$$Lambda$AlertsCreator$O8m0sNIHiV9GBEmqz2hD_IF3lj4(iArr));
        builder.setNeutralButton(LocaleController.getString("ClearMediaCache", NUM), new -$$Lambda$AlertsCreator$7zKkmW9v1PSLuJ1Plrx3fcyl7gE(context));
        return builder.create();
    }

    static /* synthetic */ void lambda$createFreeSpaceDialog$34(int[] iArr, LinearLayout linearLayout, View view) {
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
            -$$Lambda$AlertsCreator$ARC8cr6XGhy5ASW9gUtYiXvxPRw -__lambda_alertscreator_arc8cr6xghy5asw9gutyixvxprw = r1;
            FrameLayout frameLayout = radioColorCell;
            int i7 = i6;
            Builder builder2 = builder;
            view = linearLayout;
            -$$Lambda$AlertsCreator$ARC8cr6XGhy5ASW9gUtYiXvxPRw -__lambda_alertscreator_arc8cr6xghy5asw9gutyixvxprw2 = new -$$Lambda$AlertsCreator$ARC8cr6XGhy5ASW9gUtYiXvxPRw(iArr, j, i, notificationsSettings, builder, runnable);
            frameLayout.setOnClickListener(-__lambda_alertscreator_arc8cr6xghy5asw9gutyixvxprw);
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

    static /* synthetic */ void lambda$createPrioritySelectDialog$37(int[] iArr, long j, int i, SharedPreferences sharedPreferences, Builder builder, Runnable runnable, View view) {
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
            radioColorCell.setOnClickListener(new -$$Lambda$AlertsCreator$P5vVRXq6KoqygOyXDjMH8CvBx_Y(iArr, i, builder, runnable));
            i2++;
        }
        builder.setTitle(LocaleController.getString("PopupNotification", NUM));
        builder.setView(linearLayout);
        builder.setPositiveButton(LocaleController.getString("Cancel", NUM), null);
        return builder.create();
    }

    static /* synthetic */ void lambda$createPopupSelectDialog$38(int[] iArr, int i, Builder builder, Runnable runnable, View view) {
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
            radioColorCell.setOnClickListener(new -$$Lambda$AlertsCreator$qrE4i3bt6rFaszma_H0zWNDmBwc(builder, onClickListener));
            i2++;
        }
        builder.setTitle(str);
        builder.setView(linearLayout);
        builder.setPositiveButton(LocaleController.getString("Cancel", NUM), null);
        return builder.create();
    }

    static /* synthetic */ void lambda$createSingleChoiceDialog$39(Builder builder, OnClickListener onClickListener, View view) {
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
        numberPicker.setFormatter(-$$Lambda$AlertsCreator$1_2yaw_kYZf0NvLFpv0HXddl3FA.INSTANCE);
        builder.setView(numberPicker);
        builder.setNegativeButton(LocaleController.getString("Done", NUM), new -$$Lambda$AlertsCreator$ATMX7ChpMezQfBhnCjkFjsc8yHA(encryptedChat, numberPicker));
        return builder;
    }

    static /* synthetic */ String lambda$createTTLAlert$40(int i) {
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

    static /* synthetic */ void lambda$createTTLAlert$41(EncryptedChat encryptedChat, NumberPicker numberPicker, DialogInterface dialogInterface, int i) {
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
                accountSelectCell.setOnClickListener(new -$$Lambda$AlertsCreator$_R1UwzjLFpNxofVpeghMbrdukzM(alertDialogArr, dismissRunnable, accountSelectDelegate));
            }
        }
        builder.setTitle(LocaleController.getString("SelectAccount", NUM));
        builder.setView(linearLayout);
        builder.setPositiveButton(LocaleController.getString("Cancel", NUM), null);
        AlertDialog create = builder.create();
        alertDialogArr[0] = create;
        return create;
    }

    static /* synthetic */ void lambda$createAccountSelectDialog$42(AlertDialog[] alertDialogArr, Runnable runnable, AccountSelectDelegate accountSelectDelegate, View view) {
        if (alertDialogArr[0] != null) {
            alertDialogArr[0].setOnDismissListener(null);
        }
        runnable.run();
        accountSelectDelegate.didSelectAccount(((AccountSelectCell) view).getAccountNumber());
    }

    /* JADX WARNING: Removed duplicated region for block: B:186:0x038a  */
    /* JADX WARNING: Removed duplicated region for block: B:185:0x0388  */
    /* JADX WARNING: Removed duplicated region for block: B:65:0x00e6  */
    /* JADX WARNING: Removed duplicated region for block: B:64:0x00e4  */
    /* JADX WARNING: Removed duplicated region for block: B:64:0x00e4  */
    /* JADX WARNING: Removed duplicated region for block: B:65:0x00e6  */
    /* JADX WARNING: Removed duplicated region for block: B:134:0x0268  */
    /* JADX WARNING: Removed duplicated region for block: B:133:0x0263  */
    /* JADX WARNING: Removed duplicated region for block: B:138:0x0275  */
    /* JADX WARNING: Removed duplicated region for block: B:137:0x0270  */
    /* JADX WARNING: Missing block: B:223:0x0434, code skipped:
            if (r6 == r9) goto L_0x0439;
     */
    public static void createDeleteMessagesAlert(org.telegram.ui.ActionBar.BaseFragment r36, org.telegram.tgnet.TLRPC.User r37, org.telegram.tgnet.TLRPC.Chat r38, org.telegram.tgnet.TLRPC.EncryptedChat r39, org.telegram.tgnet.TLRPC.ChatFull r40, long r41, org.telegram.messenger.MessageObject r43, android.util.SparseArray<org.telegram.messenger.MessageObject>[] r44, org.telegram.messenger.MessageObject.GroupedMessages r45, int r46, java.lang.Runnable r47) {
        /*
        r13 = r36;
        r3 = r37;
        r4 = r38;
        r9 = r43;
        r11 = r45;
        r0 = r46;
        if (r13 == 0) goto L_0x05cf;
    L_0x000e:
        if (r3 != 0) goto L_0x0016;
    L_0x0010:
        if (r4 != 0) goto L_0x0016;
    L_0x0012:
        if (r39 != 0) goto L_0x0016;
    L_0x0014:
        goto L_0x05cf;
    L_0x0016:
        r1 = r36.getParentActivity();
        if (r1 != 0) goto L_0x001d;
    L_0x001c:
        return;
    L_0x001d:
        r15 = r36.getCurrentAccount();
        r2 = new org.telegram.ui.ActionBar.AlertDialog$Builder;
        r2.<init>(r1);
        r5 = 1;
        r6 = 0;
        if (r11 == 0) goto L_0x0031;
    L_0x002a:
        r7 = r11.messages;
        r7 = r7.size();
        goto L_0x0042;
    L_0x0031:
        if (r9 == 0) goto L_0x0035;
    L_0x0033:
        r7 = 1;
        goto L_0x0042;
    L_0x0035:
        r7 = r44[r6];
        r7 = r7.size();
        r8 = r44[r5];
        r8 = r8.size();
        r7 = r7 + r8;
    L_0x0042:
        r8 = 3;
        r10 = new boolean[r8];
        r12 = new boolean[r5];
        if (r3 == 0) goto L_0x0053;
    L_0x0049:
        r14 = org.telegram.messenger.MessagesController.getInstance(r15);
        r14 = r14.canRevokePmInbox;
        if (r14 == 0) goto L_0x0053;
    L_0x0051:
        r14 = 1;
        goto L_0x0054;
    L_0x0053:
        r14 = 0;
    L_0x0054:
        if (r3 == 0) goto L_0x005d;
    L_0x0056:
        r6 = org.telegram.messenger.MessagesController.getInstance(r15);
        r6 = r6.revokeTimePmLimit;
        goto L_0x0063;
    L_0x005d:
        r6 = org.telegram.messenger.MessagesController.getInstance(r15);
        r6 = r6.revokeTimeLimit;
    L_0x0063:
        if (r39 != 0) goto L_0x0070;
    L_0x0065:
        if (r3 == 0) goto L_0x0070;
    L_0x0067:
        if (r14 == 0) goto L_0x0070;
    L_0x0069:
        r8 = NUM; // 0x7fffffff float:NaN double:1.060997895E-314;
        if (r6 != r8) goto L_0x0070;
    L_0x006e:
        r8 = 1;
        goto L_0x0071;
    L_0x0070:
        r8 = 0;
    L_0x0071:
        r17 = NUM; // 0x41800000 float:16.0 double:5.42932517E-315;
        r18 = NUM; // 0x41000000 float:8.0 double:5.38787994E-315;
        r5 = "";
        if (r4 == 0) goto L_0x0328;
    L_0x0079:
        r11 = r4.megagroup;
        if (r11 == 0) goto L_0x0328;
    L_0x007d:
        r11 = org.telegram.messenger.ChatObject.canBlockUsers(r38);
        r14 = org.telegram.tgnet.ConnectionsManager.getInstance(r15);
        r14 = r14.getCurrentTime();
        if (r9 == 0) goto L_0x00ed;
    L_0x008b:
        r26 = r7;
        r7 = r9.messageOwner;
        r7 = r7.action;
        if (r7 == 0) goto L_0x00a8;
    L_0x0093:
        r27 = r8;
        r8 = r7 instanceof org.telegram.tgnet.TLRPC.TL_messageActionEmpty;
        if (r8 != 0) goto L_0x00aa;
    L_0x0099:
        r8 = r7 instanceof org.telegram.tgnet.TLRPC.TL_messageActionChatDeleteUser;
        if (r8 != 0) goto L_0x00aa;
    L_0x009d:
        r8 = r7 instanceof org.telegram.tgnet.TLRPC.TL_messageActionChatJoinedByLink;
        if (r8 != 0) goto L_0x00aa;
    L_0x00a1:
        r7 = r7 instanceof org.telegram.tgnet.TLRPC.TL_messageActionChatAddUser;
        if (r7 == 0) goto L_0x00a6;
    L_0x00a5:
        goto L_0x00aa;
    L_0x00a6:
        r7 = 0;
        goto L_0x00ba;
    L_0x00a8:
        r27 = r8;
    L_0x00aa:
        r7 = org.telegram.messenger.MessagesController.getInstance(r15);
        r8 = r9.messageOwner;
        r8 = r8.from_id;
        r8 = java.lang.Integer.valueOf(r8);
        r7 = r7.getUser(r8);
    L_0x00ba:
        r8 = r43.isSendError();
        if (r8 != 0) goto L_0x00e1;
    L_0x00c0:
        r19 = r43.getDialogId();
        r8 = (r19 > r41 ? 1 : (r19 == r41 ? 0 : -1));
        if (r8 != 0) goto L_0x00e1;
    L_0x00c8:
        r8 = r9.messageOwner;
        r8 = r8.action;
        if (r8 == 0) goto L_0x00d2;
    L_0x00ce:
        r8 = r8 instanceof org.telegram.tgnet.TLRPC.TL_messageActionEmpty;
        if (r8 == 0) goto L_0x00e1;
    L_0x00d2:
        r8 = r43.isOut();
        if (r8 == 0) goto L_0x00e1;
    L_0x00d8:
        r8 = r9.messageOwner;
        r8 = r8.date;
        r14 = r14 - r8;
        if (r14 > r6) goto L_0x00e1;
    L_0x00df:
        r6 = 1;
        goto L_0x00e2;
    L_0x00e1:
        r6 = 0;
    L_0x00e2:
        if (r6 == 0) goto L_0x00e6;
    L_0x00e4:
        r6 = 1;
        goto L_0x00e7;
    L_0x00e6:
        r6 = 0;
    L_0x00e7:
        r28 = r2;
        r19 = r12;
        goto L_0x0183;
    L_0x00ed:
        r26 = r7;
        r27 = r8;
        r7 = 1;
        r8 = -1;
    L_0x00f3:
        if (r7 < 0) goto L_0x0131;
    L_0x00f5:
        r9 = r8;
        r8 = 0;
    L_0x00f7:
        r19 = r44[r7];
        r3 = r19.size();
        r19 = r12;
        if (r8 >= r3) goto L_0x0123;
    L_0x0101:
        r3 = r44[r7];
        r3 = r3.valueAt(r8);
        r3 = (org.telegram.messenger.MessageObject) r3;
        r12 = -1;
        if (r9 != r12) goto L_0x0110;
    L_0x010c:
        r9 = r3.messageOwner;
        r9 = r9.from_id;
    L_0x0110:
        if (r9 < 0) goto L_0x0120;
    L_0x0112:
        r3 = r3.messageOwner;
        r3 = r3.from_id;
        if (r9 == r3) goto L_0x0119;
    L_0x0118:
        goto L_0x0120;
    L_0x0119:
        r8 = r8 + 1;
        r3 = r37;
        r12 = r19;
        goto L_0x00f7;
    L_0x0120:
        r3 = -2;
        r8 = -2;
        goto L_0x0125;
    L_0x0123:
        r8 = r9;
        r3 = -2;
    L_0x0125:
        if (r8 != r3) goto L_0x0128;
    L_0x0127:
        goto L_0x0133;
    L_0x0128:
        r7 = r7 + -1;
        r3 = r37;
        r9 = r43;
        r12 = r19;
        goto L_0x00f3;
    L_0x0131:
        r19 = r12;
    L_0x0133:
        r3 = 1;
        r7 = 0;
    L_0x0135:
        if (r3 < 0) goto L_0x016d;
    L_0x0137:
        r9 = r7;
        r7 = 0;
    L_0x0139:
        r12 = r44[r3];
        r12 = r12.size();
        if (r7 >= r12) goto L_0x0167;
    L_0x0141:
        r12 = r44[r3];
        r12 = r12.valueAt(r7);
        r12 = (org.telegram.messenger.MessageObject) r12;
        r28 = r2;
        r2 = 1;
        if (r3 != r2) goto L_0x0162;
    L_0x014e:
        r2 = r12.isOut();
        if (r2 == 0) goto L_0x0162;
    L_0x0154:
        r2 = r12.messageOwner;
        r12 = r2.action;
        if (r12 != 0) goto L_0x0162;
    L_0x015a:
        r2 = r2.date;
        r2 = r14 - r2;
        if (r2 > r6) goto L_0x0162;
    L_0x0160:
        r9 = r9 + 1;
    L_0x0162:
        r7 = r7 + 1;
        r2 = r28;
        goto L_0x0139;
    L_0x0167:
        r28 = r2;
        r3 = r3 + -1;
        r7 = r9;
        goto L_0x0135;
    L_0x016d:
        r28 = r2;
        r2 = -1;
        if (r8 == r2) goto L_0x0181;
    L_0x0172:
        r2 = org.telegram.messenger.MessagesController.getInstance(r15);
        r3 = java.lang.Integer.valueOf(r8);
        r2 = r2.getUser(r3);
        r6 = r7;
        r7 = r2;
        goto L_0x0183;
    L_0x0181:
        r6 = r7;
        r7 = 0;
    L_0x0183:
        if (r7 == 0) goto L_0x02b1;
    L_0x0185:
        r2 = r7.id;
        r3 = org.telegram.messenger.UserConfig.getInstance(r15);
        r3 = r3.getClientUserId();
        if (r2 == r3) goto L_0x02b1;
    L_0x0191:
        r2 = 1;
        if (r0 != r2) goto L_0x01f1;
    L_0x0194:
        r3 = r4.creator;
        if (r3 != 0) goto L_0x01f1;
    L_0x0198:
        r14 = new org.telegram.ui.ActionBar.AlertDialog[r2];
        r0 = new org.telegram.ui.ActionBar.AlertDialog;
        r2 = 3;
        r0.<init>(r1, r2);
        r1 = 0;
        r14[r1] = r0;
        r12 = new org.telegram.tgnet.TLRPC$TL_channels_getParticipant;
        r12.<init>();
        r0 = org.telegram.messenger.MessagesController.getInputChannel(r38);
        r12.channel = r0;
        r0 = org.telegram.messenger.MessagesController.getInstance(r15);
        r0 = r0.getInputUser(r7);
        r12.user_id = r0;
        r11 = org.telegram.tgnet.ConnectionsManager.getInstance(r15);
        r10 = new org.telegram.ui.Components.-$$Lambda$AlertsCreator$loXHnnbYDTXWG2aFT8jaYEqX_p4;
        r0 = r10;
        r1 = r14;
        r2 = r36;
        r3 = r37;
        r4 = r38;
        r5 = r39;
        r6 = r40;
        r7 = r41;
        r9 = r43;
        r13 = r10;
        r10 = r44;
        r16 = r14;
        r14 = r11;
        r11 = r45;
        r20 = r15;
        r15 = r12;
        r12 = r47;
        r0.<init>(r1, r2, r3, r4, r5, r6, r7, r9, r10, r11, r12);
        r0 = r14.sendRequest(r15, r13);
        r1 = new org.telegram.ui.Components.-$$Lambda$AlertsCreator$SCrK_EWQcGEC1BMsMG0P334yXtQ;
        r4 = r16;
        r3 = r20;
        r1.<init>(r4, r3, r0, r2);
        r2 = 1000; // 0x3e8 float:1.401E-42 double:4.94E-321;
        org.telegram.messenger.AndroidUtilities.runOnUIThread(r1, r2);
        return;
    L_0x01f1:
        r2 = r13;
        r3 = r15;
        r8 = new android.widget.FrameLayout;
        r8.<init>(r1);
        r9 = 0;
        r12 = 0;
    L_0x01fa:
        r13 = 3;
        if (r9 >= r13) goto L_0x02a7;
    L_0x01fd:
        r14 = 2;
        if (r0 == r14) goto L_0x0202;
    L_0x0200:
        if (r11 != 0) goto L_0x0208;
    L_0x0202:
        if (r9 != 0) goto L_0x0208;
    L_0x0204:
        r20 = r11;
        goto L_0x029f;
    L_0x0208:
        r14 = new org.telegram.ui.Cells.CheckBoxCell;
        r15 = 1;
        r14.<init>(r1, r15);
        r41 = 0;
        r13 = org.telegram.ui.ActionBar.Theme.getSelectorDrawable(r41);
        r14.setBackgroundDrawable(r13);
        r13 = java.lang.Integer.valueOf(r9);
        r14.setTag(r13);
        if (r9 != 0) goto L_0x0230;
    L_0x0220:
        r13 = NUM; // 0x7f0d035a float:1.8743855E38 double:1.0531302015E-314;
        r15 = "DeleteBanUser";
        r13 = org.telegram.messenger.LocaleController.getString(r15, r13);
        r15 = 0;
        r14.setText(r13, r5, r15, r15);
    L_0x022d:
        r20 = r11;
        goto L_0x025f;
    L_0x0230:
        r13 = 1;
        r15 = 0;
        if (r9 != r13) goto L_0x0241;
    L_0x0234:
        r13 = NUM; // 0x7f0d036f float:1.8743898E38 double:1.053130212E-314;
        r0 = "DeleteReportSpam";
        r0 = org.telegram.messenger.LocaleController.getString(r0, r13);
        r14.setText(r0, r5, r15, r15);
        goto L_0x022d;
    L_0x0241:
        r0 = 2;
        if (r9 != r0) goto L_0x022d;
    L_0x0244:
        r13 = 1;
        r0 = new java.lang.Object[r13];
        r13 = r7.first_name;
        r20 = r11;
        r11 = r7.last_name;
        r11 = org.telegram.messenger.ContactsController.formatName(r13, r11);
        r0[r15] = r11;
        r11 = "DeleteAllFrom";
        r13 = NUM; // 0x7f0d0355 float:1.8743845E38 double:1.053130199E-314;
        r0 = org.telegram.messenger.LocaleController.formatString(r11, r13, r0);
        r14.setText(r0, r5, r15, r15);
    L_0x025f:
        r0 = org.telegram.messenger.LocaleController.isRTL;
        if (r0 == 0) goto L_0x0268;
    L_0x0263:
        r0 = org.telegram.messenger.AndroidUtilities.dp(r17);
        goto L_0x026c;
    L_0x0268:
        r0 = org.telegram.messenger.AndroidUtilities.dp(r18);
    L_0x026c:
        r11 = org.telegram.messenger.LocaleController.isRTL;
        if (r11 == 0) goto L_0x0275;
    L_0x0270:
        r11 = org.telegram.messenger.AndroidUtilities.dp(r18);
        goto L_0x0279;
    L_0x0275:
        r11 = org.telegram.messenger.AndroidUtilities.dp(r17);
    L_0x0279:
        r13 = 0;
        r14.setPadding(r0, r13, r11, r13);
        r29 = -1;
        r30 = NUM; // 0x42400000 float:48.0 double:5.491493014E-315;
        r31 = 51;
        r32 = 0;
        r0 = r12 * 48;
        r0 = (float) r0;
        r34 = 0;
        r35 = 0;
        r33 = r0;
        r0 = org.telegram.ui.Components.LayoutHelper.createFrame(r29, r30, r31, r32, r33, r34, r35);
        r8.addView(r14, r0);
        r0 = new org.telegram.ui.Components.-$$Lambda$AlertsCreator$394GBGS7wRI0qRQjkwqQ4GWlXk8;
        r0.<init>(r10);
        r14.setOnClickListener(r0);
        r12 = r12 + 1;
    L_0x029f:
        r9 = r9 + 1;
        r0 = r46;
        r11 = r20;
        goto L_0x01fa;
    L_0x02a7:
        r0 = r28;
        r0.setView(r8);
        r11 = r19;
        r5 = 0;
        goto L_0x031f;
    L_0x02b1:
        r2 = r13;
        r3 = r15;
        r0 = r28;
        if (r6 <= 0) goto L_0x031b;
    L_0x02b7:
        r8 = new android.widget.FrameLayout;
        r8.<init>(r1);
        r9 = new org.telegram.ui.Cells.CheckBoxCell;
        r11 = 1;
        r9.<init>(r1, r11);
        r1 = 0;
        r11 = org.telegram.ui.ActionBar.Theme.getSelectorDrawable(r1);
        r9.setBackgroundDrawable(r11);
        r11 = NUM; // 0x7f0d0367 float:1.8743881E38 double:1.053130208E-314;
        r12 = "DeleteMessagesOption";
        r11 = org.telegram.messenger.LocaleController.getString(r12, r11);
        r9.setText(r11, r5, r1, r1);
        r1 = org.telegram.messenger.LocaleController.isRTL;
        if (r1 == 0) goto L_0x02df;
    L_0x02da:
        r1 = org.telegram.messenger.AndroidUtilities.dp(r17);
        goto L_0x02e3;
    L_0x02df:
        r1 = org.telegram.messenger.AndroidUtilities.dp(r18);
    L_0x02e3:
        r5 = org.telegram.messenger.LocaleController.isRTL;
        if (r5 == 0) goto L_0x02ec;
    L_0x02e7:
        r5 = org.telegram.messenger.AndroidUtilities.dp(r18);
        goto L_0x02f0;
    L_0x02ec:
        r5 = org.telegram.messenger.AndroidUtilities.dp(r17);
    L_0x02f0:
        r11 = 0;
        r9.setPadding(r1, r11, r5, r11);
        r12 = -1;
        r13 = NUM; // 0x42400000 float:48.0 double:5.491493014E-315;
        r14 = 51;
        r15 = 0;
        r16 = 0;
        r17 = 0;
        r18 = 0;
        r1 = org.telegram.ui.Components.LayoutHelper.createFrame(r12, r13, r14, r15, r16, r17, r18);
        r8.addView(r9, r1);
        r1 = new org.telegram.ui.Components.-$$Lambda$AlertsCreator$UZErB7gC4RQ76wGSadQF_2vaMh4;
        r11 = r19;
        r1.<init>(r11);
        r9.setOnClickListener(r1);
        r0.setView(r8);
        r1 = 9;
        r0.setCustomViewOffset(r1);
        r5 = 1;
        goto L_0x031f;
    L_0x031b:
        r11 = r19;
        r5 = 0;
        r7 = 0;
    L_0x031f:
        r20 = r3;
        r21 = r7;
        r9 = r26;
        r7 = 0;
        goto L_0x04a5;
    L_0x0328:
        r0 = r2;
        r26 = r7;
        r27 = r8;
        r11 = r12;
        r2 = r13;
        r3 = r15;
        r7 = org.telegram.messenger.ChatObject.isChannel(r38);
        if (r7 != 0) goto L_0x049c;
    L_0x0336:
        if (r39 != 0) goto L_0x049c;
    L_0x0338:
        r7 = org.telegram.tgnet.ConnectionsManager.getInstance(r3);
        r7 = r7.getCurrentTime();
        r8 = r37;
        if (r8 == 0) goto L_0x0354;
    L_0x0344:
        r9 = r8.id;
        r12 = org.telegram.messenger.UserConfig.getInstance(r3);
        r12 = r12.getClientUserId();
        if (r9 == r12) goto L_0x0354;
    L_0x0350:
        r9 = r8.bot;
        if (r9 == 0) goto L_0x0356;
    L_0x0354:
        if (r4 == 0) goto L_0x03fc;
    L_0x0356:
        r9 = r43;
        if (r9 == 0) goto L_0x0395;
    L_0x035a:
        r12 = r43.isSendError();
        if (r12 != 0) goto L_0x0385;
    L_0x0360:
        r12 = r9.messageOwner;
        r12 = r12.action;
        if (r12 == 0) goto L_0x036e;
    L_0x0366:
        r13 = r12 instanceof org.telegram.tgnet.TLRPC.TL_messageActionEmpty;
        if (r13 != 0) goto L_0x036e;
    L_0x036a:
        r12 = r12 instanceof org.telegram.tgnet.TLRPC.TL_messageActionPhoneCall;
        if (r12 == 0) goto L_0x0385;
    L_0x036e:
        r12 = r43.isOut();
        if (r12 != 0) goto L_0x037c;
    L_0x0374:
        if (r14 != 0) goto L_0x037c;
    L_0x0376:
        r12 = org.telegram.messenger.ChatObject.hasAdminRights(r38);
        if (r12 == 0) goto L_0x0385;
    L_0x037c:
        r12 = r9.messageOwner;
        r12 = r12.date;
        r7 = r7 - r12;
        if (r7 > r6) goto L_0x0385;
    L_0x0383:
        r6 = 1;
        goto L_0x0386;
    L_0x0385:
        r6 = 0;
    L_0x0386:
        if (r6 == 0) goto L_0x038a;
    L_0x0388:
        r6 = 1;
        goto L_0x038b;
    L_0x038a:
        r6 = 0;
    L_0x038b:
        r7 = r43.isOut();
        r12 = 1;
        r7 = r7 ^ r12;
        r20 = r3;
        goto L_0x0400;
    L_0x0395:
        r12 = 1;
        r13 = 0;
        r15 = 0;
    L_0x0398:
        if (r12 < 0) goto L_0x03f7;
    L_0x039a:
        r16 = r15;
        r15 = r13;
        r13 = 0;
    L_0x039e:
        r19 = r44[r12];
        r20 = r3;
        r3 = r19.size();
        if (r13 >= r3) goto L_0x03eb;
    L_0x03a8:
        r3 = r44[r12];
        r3 = r3.valueAt(r13);
        r3 = (org.telegram.messenger.MessageObject) r3;
        r8 = r3.messageOwner;
        r8 = r8.action;
        if (r8 == 0) goto L_0x03bf;
    L_0x03b6:
        r9 = r8 instanceof org.telegram.tgnet.TLRPC.TL_messageActionEmpty;
        if (r9 != 0) goto L_0x03bf;
    L_0x03ba:
        r8 = r8 instanceof org.telegram.tgnet.TLRPC.TL_messageActionPhoneCall;
        if (r8 != 0) goto L_0x03bf;
    L_0x03be:
        goto L_0x03e2;
    L_0x03bf:
        r8 = r3.isOut();
        if (r8 != 0) goto L_0x03cf;
    L_0x03c5:
        if (r14 != 0) goto L_0x03cf;
    L_0x03c7:
        if (r4 == 0) goto L_0x03e2;
    L_0x03c9:
        r8 = org.telegram.messenger.ChatObject.canBlockUsers(r38);
        if (r8 == 0) goto L_0x03e2;
    L_0x03cf:
        r8 = r3.messageOwner;
        r8 = r8.date;
        r8 = r7 - r8;
        if (r8 > r6) goto L_0x03e2;
    L_0x03d7:
        r16 = r16 + 1;
        if (r15 != 0) goto L_0x03e2;
    L_0x03db:
        r3 = r3.isOut();
        if (r3 != 0) goto L_0x03e2;
    L_0x03e1:
        r15 = 1;
    L_0x03e2:
        r13 = r13 + 1;
        r8 = r37;
        r9 = r43;
        r3 = r20;
        goto L_0x039e;
    L_0x03eb:
        r12 = r12 + -1;
        r8 = r37;
        r9 = r43;
        r13 = r15;
        r15 = r16;
        r3 = r20;
        goto L_0x0398;
    L_0x03f7:
        r20 = r3;
        r7 = r13;
        r6 = r15;
        goto L_0x0400;
    L_0x03fc:
        r20 = r3;
        r6 = 0;
        r7 = 0;
    L_0x0400:
        if (r6 <= 0) goto L_0x0498;
    L_0x0402:
        r3 = new android.widget.FrameLayout;
        r3.<init>(r1);
        r8 = new org.telegram.ui.Cells.CheckBoxCell;
        r9 = 1;
        r8.<init>(r1, r9);
        r1 = 0;
        r12 = org.telegram.ui.ActionBar.Theme.getSelectorDrawable(r1);
        r8.setBackgroundDrawable(r12);
        if (r27 == 0) goto L_0x042e;
    L_0x0417:
        r12 = NUM; // 0x7f0d0368 float:1.8743883E38 double:1.0531302084E-314;
        r13 = new java.lang.Object[r9];
        r9 = org.telegram.messenger.UserObject.getFirstName(r37);
        r13[r1] = r9;
        r9 = "DeleteMessagesOptionAlso";
        r9 = org.telegram.messenger.LocaleController.formatString(r9, r12, r13);
        r8.setText(r9, r5, r1, r1);
        r9 = r26;
        goto L_0x0454;
    L_0x042e:
        if (r4 == 0) goto L_0x0446;
    L_0x0430:
        if (r7 != 0) goto L_0x0437;
    L_0x0432:
        r9 = r26;
        if (r6 != r9) goto L_0x0448;
    L_0x0436:
        goto L_0x0439;
    L_0x0437:
        r9 = r26;
    L_0x0439:
        r12 = NUM; // 0x7f0d0360 float:1.8743867E38 double:1.0531302044E-314;
        r13 = "DeleteForAll";
        r12 = org.telegram.messenger.LocaleController.getString(r13, r12);
        r8.setText(r12, r5, r1, r1);
        goto L_0x0454;
    L_0x0446:
        r9 = r26;
    L_0x0448:
        r12 = NUM; // 0x7f0d0367 float:1.8743881E38 double:1.053130208E-314;
        r13 = "DeleteMessagesOption";
        r12 = org.telegram.messenger.LocaleController.getString(r13, r12);
        r8.setText(r12, r5, r1, r1);
    L_0x0454:
        r1 = org.telegram.messenger.LocaleController.isRTL;
        if (r1 == 0) goto L_0x045d;
    L_0x0458:
        r1 = org.telegram.messenger.AndroidUtilities.dp(r17);
        goto L_0x0461;
    L_0x045d:
        r1 = org.telegram.messenger.AndroidUtilities.dp(r18);
    L_0x0461:
        r5 = org.telegram.messenger.LocaleController.isRTL;
        if (r5 == 0) goto L_0x046a;
    L_0x0465:
        r5 = org.telegram.messenger.AndroidUtilities.dp(r18);
        goto L_0x046e;
    L_0x046a:
        r5 = org.telegram.messenger.AndroidUtilities.dp(r17);
    L_0x046e:
        r12 = 0;
        r8.setPadding(r1, r12, r5, r12);
        r13 = -1;
        r14 = NUM; // 0x42400000 float:48.0 double:5.491493014E-315;
        r15 = 51;
        r16 = 0;
        r17 = 0;
        r18 = 0;
        r19 = 0;
        r1 = org.telegram.ui.Components.LayoutHelper.createFrame(r13, r14, r15, r16, r17, r18, r19);
        r3.addView(r8, r1);
        r1 = new org.telegram.ui.Components.-$$Lambda$AlertsCreator$-T7qsXm8Vo5ZWF2XJ2124ggV-S8;
        r1.<init>(r11);
        r8.setOnClickListener(r1);
        r0.setView(r3);
        r1 = 9;
        r0.setCustomViewOffset(r1);
        r5 = 1;
        goto L_0x04a3;
    L_0x0498:
        r9 = r26;
        r5 = 0;
        goto L_0x04a3;
    L_0x049c:
        r20 = r3;
        r9 = r26;
        r5 = 0;
        r6 = 0;
        r7 = 0;
    L_0x04a3:
        r21 = 0;
    L_0x04a5:
        r1 = NUM; // 0x7f0d0350 float:1.8743835E38 double:1.0531301965E-314;
        r3 = "Delete";
        r1 = org.telegram.messenger.LocaleController.getString(r3, r1);
        r3 = new org.telegram.ui.Components.-$$Lambda$AlertsCreator$yfZoNW2-xo7PIX4K5_JbF7y85W0;
        r14 = r3;
        r8 = r20;
        r15 = r43;
        r16 = r45;
        r17 = r39;
        r18 = r8;
        r19 = r11;
        r20 = r44;
        r22 = r10;
        r23 = r38;
        r24 = r40;
        r25 = r47;
        r14.<init>(r15, r16, r17, r18, r19, r20, r21, r22, r23, r24, r25);
        r0.setPositiveButton(r1, r3);
        r1 = "messages";
        r3 = 1;
        if (r9 != r3) goto L_0x04df;
    L_0x04d2:
        r8 = NUM; // 0x7f0d0370 float:1.87439E38 double:1.0531302123E-314;
        r10 = "DeleteSingleMessagesTitle";
        r8 = org.telegram.messenger.LocaleController.getString(r10, r8);
        r0.setTitle(r8);
        goto L_0x04f4;
    L_0x04df:
        r8 = NUM; // 0x7f0d036c float:1.8743891E38 double:1.0531302103E-314;
        r10 = new java.lang.Object[r3];
        r3 = org.telegram.messenger.LocaleController.formatPluralString(r1, r9);
        r11 = 0;
        r10[r11] = r3;
        r3 = "DeleteMessagesTitle";
        r3 = org.telegram.messenger.LocaleController.formatString(r3, r8, r10);
        r0.setTitle(r3);
    L_0x04f4:
        r3 = NUM; // 0x7f0d0124 float:1.8742707E38 double:1.053129922E-314;
        r8 = "AreYouSureDeleteSingleMessage";
        r10 = NUM; // 0x7f0d0120 float:1.8742699E38 double:1.05312992E-314;
        r11 = "AreYouSureDeleteFewMessages";
        if (r4 == 0) goto L_0x0533;
    L_0x0500:
        if (r7 == 0) goto L_0x0533;
    L_0x0502:
        if (r5 == 0) goto L_0x051e;
    L_0x0504:
        if (r6 == r9) goto L_0x051e;
    L_0x0506:
        r3 = NUM; // 0x7f0d036b float:1.874389E38 double:1.05313021E-314;
        r4 = 1;
        r4 = new java.lang.Object[r4];
        r1 = org.telegram.messenger.LocaleController.formatPluralString(r1, r6);
        r5 = 0;
        r4[r5] = r1;
        r1 = "DeleteMessagesTextGroupPart";
        r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r4);
        r0.setMessage(r1);
        goto L_0x05a9;
    L_0x051e:
        r4 = 1;
        if (r9 != r4) goto L_0x052a;
    L_0x0521:
        r1 = org.telegram.messenger.LocaleController.getString(r8, r3);
        r0.setMessage(r1);
        goto L_0x05a9;
    L_0x052a:
        r1 = org.telegram.messenger.LocaleController.getString(r11, r10);
        r0.setMessage(r1);
        goto L_0x05a9;
    L_0x0533:
        if (r5 == 0) goto L_0x0574;
    L_0x0535:
        if (r27 != 0) goto L_0x0574;
    L_0x0537:
        if (r6 == r9) goto L_0x0574;
    L_0x0539:
        if (r4 == 0) goto L_0x0552;
    L_0x053b:
        r3 = NUM; // 0x7f0d036a float:1.8743887E38 double:1.0531302094E-314;
        r4 = 1;
        r4 = new java.lang.Object[r4];
        r1 = org.telegram.messenger.LocaleController.formatPluralString(r1, r6);
        r5 = 0;
        r4[r5] = r1;
        r1 = "DeleteMessagesTextGroup";
        r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r4);
        r0.setMessage(r1);
        goto L_0x05a9;
    L_0x0552:
        r5 = 0;
        r3 = NUM; // 0x7f0d0369 float:1.8743885E38 double:1.053130209E-314;
        r4 = 2;
        r4 = new java.lang.Object[r4];
        r1 = org.telegram.messenger.LocaleController.formatPluralString(r1, r6);
        r4[r5] = r1;
        r1 = org.telegram.messenger.UserObject.getFirstName(r37);
        r5 = 1;
        r4[r5] = r1;
        r1 = "DeleteMessagesText";
        r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r4);
        r1 = org.telegram.messenger.AndroidUtilities.replaceTags(r1);
        r0.setMessage(r1);
        goto L_0x05a9;
    L_0x0574:
        if (r4 == 0) goto L_0x0597;
    L_0x0576:
        r1 = r4.megagroup;
        if (r1 == 0) goto L_0x0597;
    L_0x057a:
        r1 = 1;
        if (r9 != r1) goto L_0x058a;
    L_0x057d:
        r1 = NUM; // 0x7f0d0125 float:1.874271E38 double:1.0531299223E-314;
        r3 = "AreYouSureDeleteSingleMessageMega";
        r1 = org.telegram.messenger.LocaleController.getString(r3, r1);
        r0.setMessage(r1);
        goto L_0x05a9;
    L_0x058a:
        r1 = NUM; // 0x7f0d0121 float:1.87427E38 double:1.0531299203E-314;
        r3 = "AreYouSureDeleteFewMessagesMega";
        r1 = org.telegram.messenger.LocaleController.getString(r3, r1);
        r0.setMessage(r1);
        goto L_0x05a9;
    L_0x0597:
        r1 = 1;
        if (r9 != r1) goto L_0x05a2;
    L_0x059a:
        r1 = org.telegram.messenger.LocaleController.getString(r8, r3);
        r0.setMessage(r1);
        goto L_0x05a9;
    L_0x05a2:
        r1 = org.telegram.messenger.LocaleController.getString(r11, r10);
        r0.setMessage(r1);
    L_0x05a9:
        r1 = NUM; // 0x7f0d01f9 float:1.874314E38 double:1.053130027E-314;
        r3 = "Cancel";
        r1 = org.telegram.messenger.LocaleController.getString(r3, r1);
        r3 = 0;
        r0.setNegativeButton(r1, r3);
        r0 = r0.create();
        r2.showDialog(r0);
        r1 = -1;
        r0 = r0.getButton(r1);
        r0 = (android.widget.TextView) r0;
        if (r0 == 0) goto L_0x05cf;
    L_0x05c6:
        r1 = "dialogTextRed2";
        r1 = org.telegram.ui.ActionBar.Theme.getColor(r1);
        r0.setTextColor(r1);
    L_0x05cf:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.AlertsCreator.createDeleteMessagesAlert(org.telegram.ui.ActionBar.BaseFragment, org.telegram.tgnet.TLRPC$User, org.telegram.tgnet.TLRPC$Chat, org.telegram.tgnet.TLRPC$EncryptedChat, org.telegram.tgnet.TLRPC$ChatFull, long, org.telegram.messenger.MessageObject, android.util.SparseArray[], org.telegram.messenger.MessageObject$GroupedMessages, int, java.lang.Runnable):void");
    }

    static /* synthetic */ void lambda$null$43(AlertDialog[] alertDialogArr, TLObject tLObject, BaseFragment baseFragment, User user, Chat chat, EncryptedChat encryptedChat, ChatFull chatFull, long j, MessageObject messageObject, SparseArray[] sparseArrayArr, GroupedMessages groupedMessages, Runnable runnable) {
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
                createDeleteMessagesAlert(baseFragment, user, chat, encryptedChat, chatFull, j, messageObject, sparseArrayArr, groupedMessages, i, runnable);
            }
        }
        i = 2;
        createDeleteMessagesAlert(baseFragment, user, chat, encryptedChat, chatFull, j, messageObject, sparseArrayArr, groupedMessages, i, runnable);
    }

    static /* synthetic */ void lambda$createDeleteMessagesAlert$46(AlertDialog[] alertDialogArr, int i, int i2, BaseFragment baseFragment) {
        if (alertDialogArr[0] != null) {
            alertDialogArr[0].setOnCancelListener(new -$$Lambda$AlertsCreator$VsNQZurlCRiyyJQehdry4_NThf8(i, i2));
            baseFragment.showDialog(alertDialogArr[0]);
        }
    }

    static /* synthetic */ void lambda$createDeleteMessagesAlert$47(boolean[] zArr, View view) {
        if (view.isEnabled()) {
            CheckBoxCell checkBoxCell = (CheckBoxCell) view;
            Integer num = (Integer) checkBoxCell.getTag();
            zArr[num.intValue()] = zArr[num.intValue()] ^ 1;
            checkBoxCell.setChecked(zArr[num.intValue()], true);
        }
    }

    static /* synthetic */ void lambda$createDeleteMessagesAlert$48(boolean[] zArr, View view) {
        CheckBoxCell checkBoxCell = (CheckBoxCell) view;
        zArr[0] = zArr[0] ^ 1;
        checkBoxCell.setChecked(zArr[0], true);
    }

    static /* synthetic */ void lambda$createDeleteMessagesAlert$49(boolean[] zArr, View view) {
        CheckBoxCell checkBoxCell = (CheckBoxCell) view;
        zArr[0] = zArr[0] ^ 1;
        checkBoxCell.setChecked(zArr[0], true);
    }

    /* JADX WARNING: Removed duplicated region for block: B:50:0x010e  */
    /* JADX WARNING: Removed duplicated region for block: B:40:0x00e0  */
    static /* synthetic */ void lambda$createDeleteMessagesAlert$51(org.telegram.messenger.MessageObject r16, org.telegram.messenger.MessageObject.GroupedMessages r17, org.telegram.tgnet.TLRPC.EncryptedChat r18, int r19, boolean[] r20, android.util.SparseArray[] r21, org.telegram.tgnet.TLRPC.User r22, boolean[] r23, org.telegram.tgnet.TLRPC.Chat r24, org.telegram.tgnet.TLRPC.ChatFull r25, java.lang.Runnable r26, android.content.DialogInterface r27, int r28) {
        /*
        r0 = r16;
        r1 = r17;
        r6 = r22;
        r7 = r24;
        r8 = 10;
        r9 = 0;
        r13 = 0;
        if (r0 == 0) goto L_0x0099;
    L_0x000f:
        r14 = new java.util.ArrayList;
        r14.<init>();
        if (r1 == 0) goto L_0x0058;
    L_0x0016:
        r2 = 0;
        r11 = 0;
    L_0x0018:
        r3 = r1.messages;
        r3 = r3.size();
        if (r2 >= r3) goto L_0x0056;
    L_0x0020:
        r3 = r1.messages;
        r3 = r3.get(r2);
        r3 = (org.telegram.messenger.MessageObject) r3;
        r4 = r3.getId();
        r4 = java.lang.Integer.valueOf(r4);
        r14.add(r4);
        if (r18 == 0) goto L_0x0053;
    L_0x0035:
        r4 = r3.messageOwner;
        r4 = r4.random_id;
        r15 = (r4 > r9 ? 1 : (r4 == r9 ? 0 : -1));
        if (r15 == 0) goto L_0x0053;
    L_0x003d:
        r4 = r3.type;
        if (r4 == r8) goto L_0x0053;
    L_0x0041:
        if (r11 != 0) goto L_0x0048;
    L_0x0043:
        r11 = new java.util.ArrayList;
        r11.<init>();
    L_0x0048:
        r3 = r3.messageOwner;
        r3 = r3.random_id;
        r3 = java.lang.Long.valueOf(r3);
        r11.add(r3);
    L_0x0053:
        r2 = r2 + 1;
        goto L_0x0018;
    L_0x0056:
        r2 = r11;
        goto L_0x0084;
    L_0x0058:
        r1 = r16.getId();
        r1 = java.lang.Integer.valueOf(r1);
        r14.add(r1);
        if (r18 == 0) goto L_0x0083;
    L_0x0065:
        r1 = r0.messageOwner;
        r1 = r1.random_id;
        r3 = (r1 > r9 ? 1 : (r1 == r9 ? 0 : -1));
        if (r3 == 0) goto L_0x0083;
    L_0x006d:
        r1 = r0.type;
        if (r1 == r8) goto L_0x0083;
    L_0x0071:
        r1 = new java.util.ArrayList;
        r1.<init>();
        r2 = r0.messageOwner;
        r2 = r2.random_id;
        r2 = java.lang.Long.valueOf(r2);
        r1.add(r2);
        r2 = r1;
        goto L_0x0084;
    L_0x0083:
        r2 = 0;
    L_0x0084:
        r1 = org.telegram.messenger.MessagesController.getInstance(r19);
        r0 = r0.messageOwner;
        r0 = r0.to_id;
        r4 = r0.channel_id;
        r5 = r20[r13];
        r0 = r1;
        r1 = r14;
        r3 = r18;
        r0.deleteMessages(r1, r2, r3, r4, r5);
        goto L_0x0126;
    L_0x0099:
        r0 = 0;
        r14 = 1;
    L_0x009b:
        if (r14 < 0) goto L_0x0125;
    L_0x009d:
        r15 = new java.util.ArrayList;
        r15.<init>();
        r0 = 0;
    L_0x00a3:
        r1 = r21[r14];
        r1 = r1.size();
        if (r0 >= r1) goto L_0x00bb;
    L_0x00ab:
        r1 = r21[r14];
        r1 = r1.keyAt(r0);
        r1 = java.lang.Integer.valueOf(r1);
        r15.add(r1);
        r0 = r0 + 1;
        goto L_0x00a3;
    L_0x00bb:
        r0 = r15.isEmpty();
        if (r0 != 0) goto L_0x00dd;
    L_0x00c1:
        r0 = r21[r14];
        r1 = r15.get(r13);
        r1 = (java.lang.Integer) r1;
        r1 = r1.intValue();
        r0 = r0.get(r1);
        r0 = (org.telegram.messenger.MessageObject) r0;
        r0 = r0.messageOwner;
        r0 = r0.to_id;
        r0 = r0.channel_id;
        if (r0 == 0) goto L_0x00dd;
    L_0x00db:
        r4 = r0;
        goto L_0x00de;
    L_0x00dd:
        r4 = 0;
    L_0x00de:
        if (r18 == 0) goto L_0x010e;
    L_0x00e0:
        r0 = new java.util.ArrayList;
        r0.<init>();
        r1 = 0;
    L_0x00e6:
        r2 = r21[r14];
        r2 = r2.size();
        if (r1 >= r2) goto L_0x010c;
    L_0x00ee:
        r2 = r21[r14];
        r2 = r2.valueAt(r1);
        r2 = (org.telegram.messenger.MessageObject) r2;
        r3 = r2.messageOwner;
        r11 = r3.random_id;
        r3 = (r11 > r9 ? 1 : (r11 == r9 ? 0 : -1));
        if (r3 == 0) goto L_0x0109;
    L_0x00fe:
        r2 = r2.type;
        if (r2 == r8) goto L_0x0109;
    L_0x0102:
        r2 = java.lang.Long.valueOf(r11);
        r0.add(r2);
    L_0x0109:
        r1 = r1 + 1;
        goto L_0x00e6;
    L_0x010c:
        r2 = r0;
        goto L_0x010f;
    L_0x010e:
        r2 = 0;
    L_0x010f:
        r0 = org.telegram.messenger.MessagesController.getInstance(r19);
        r5 = r20[r13];
        r1 = r15;
        r3 = r18;
        r0.deleteMessages(r1, r2, r3, r4, r5);
        r0 = r21[r14];
        r0.clear();
        r14 = r14 + -1;
        r0 = r15;
        goto L_0x009b;
    L_0x0125:
        r14 = r0;
    L_0x0126:
        if (r6 == 0) goto L_0x0168;
    L_0x0128:
        r0 = r23[r13];
        if (r0 == 0) goto L_0x0137;
    L_0x012c:
        r0 = org.telegram.messenger.MessagesController.getInstance(r19);
        r1 = r7.id;
        r2 = r25;
        r0.deleteUserFromChat(r1, r6, r2);
    L_0x0137:
        r0 = 1;
        r0 = r23[r0];
        if (r0 == 0) goto L_0x015c;
    L_0x013c:
        r0 = new org.telegram.tgnet.TLRPC$TL_channels_reportSpam;
        r0.<init>();
        r1 = org.telegram.messenger.MessagesController.getInputChannel(r24);
        r0.channel = r1;
        r1 = org.telegram.messenger.MessagesController.getInstance(r19);
        r1 = r1.getInputUser(r6);
        r0.user_id = r1;
        r0.id = r14;
        r1 = org.telegram.tgnet.ConnectionsManager.getInstance(r19);
        r2 = org.telegram.ui.Components.-$$Lambda$AlertsCreator$DD3tjKQVoP3-YrNmcIKFKh-z1dM.INSTANCE;
        r1.sendRequest(r0, r2);
    L_0x015c:
        r0 = 2;
        r0 = r23[r0];
        if (r0 == 0) goto L_0x0168;
    L_0x0161:
        r0 = org.telegram.messenger.MessagesController.getInstance(r19);
        r0.deleteUserChannelHistory(r7, r6, r13);
    L_0x0168:
        if (r26 == 0) goto L_0x016d;
    L_0x016a:
        r26.run();
    L_0x016d:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.AlertsCreator.lambda$createDeleteMessagesAlert$51(org.telegram.messenger.MessageObject, org.telegram.messenger.MessageObject$GroupedMessages, org.telegram.tgnet.TLRPC$EncryptedChat, int, boolean[], android.util.SparseArray[], org.telegram.tgnet.TLRPC$User, boolean[], org.telegram.tgnet.TLRPC$Chat, org.telegram.tgnet.TLRPC$ChatFull, java.lang.Runnable, android.content.DialogInterface, int):void");
    }
}
