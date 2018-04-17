package org.telegram.ui.Components.voip;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.Build.VERSION;
import android.provider.Settings.System;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import java.io.File;
import java.util.Collections;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.SendMessagesHelper;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.beta.R;
import org.telegram.messenger.exoplayer2.trackselection.AdaptiveTrackSelection;
import org.telegram.messenger.voip.VoIPService;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_inputPhoneCall;
import org.telegram.tgnet.TLRPC.TL_messageActionPhoneCall;
import org.telegram.tgnet.TLRPC.TL_phoneCallDiscardReasonBusy;
import org.telegram.tgnet.TLRPC.TL_phoneCallDiscardReasonMissed;
import org.telegram.tgnet.TLRPC.TL_phone_setCallRating;
import org.telegram.tgnet.TLRPC.TL_updates;
import org.telegram.tgnet.TLRPC.TL_userFull;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.ui.ActionBar.AlertDialog.Builder;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.CheckBoxCell;
import org.telegram.ui.Components.BetterRatingView;
import org.telegram.ui.Components.BetterRatingView.OnRatingChangeListener;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.VoIPActivity;

public class VoIPHelper {
    private static final int VOIP_SUPPORT_ID = 4244000;
    public static long lastCallTime = 0;

    public static void startCall(User user, final Activity activity, TL_userFull userFull) {
        boolean isAirplaneMode = true;
        if (userFull != null && userFull.phone_calls_private) {
            new Builder((Context) activity).setTitle(LocaleController.getString("VoipFailed", R.string.VoipFailed)).setMessage(AndroidUtilities.replaceTags(LocaleController.formatString("CallNotAvailable", R.string.CallNotAvailable, ContactsController.formatName(user.first_name, user.last_name)))).setPositiveButton(LocaleController.getString("OK", R.string.OK), null).show();
        } else if (ConnectionsManager.getInstance(UserConfig.selectedAccount).getConnectionState() != 3) {
            String str;
            int i;
            if (System.getInt(activity.getContentResolver(), "airplane_mode_on", 0) == 0) {
                isAirplaneMode = false;
            }
            Builder builder = new Builder((Context) activity);
            if (isAirplaneMode) {
                str = "VoipOfflineAirplaneTitle";
                i = R.string.VoipOfflineAirplaneTitle;
            } else {
                str = "VoipOfflineTitle";
                i = R.string.VoipOfflineTitle;
            }
            builder = builder.setTitle(LocaleController.getString(str, i));
            if (isAirplaneMode) {
                str = "VoipOfflineAirplane";
                i = R.string.VoipOfflineAirplane;
            } else {
                str = "VoipOffline";
                i = R.string.VoipOffline;
            }
            Builder bldr = builder.setMessage(LocaleController.getString(str, i)).setPositiveButton(LocaleController.getString("OK", R.string.OK), null);
            if (isAirplaneMode) {
                final Intent settingsIntent = new Intent("android.settings.AIRPLANE_MODE_SETTINGS");
                if (settingsIntent.resolveActivity(activity.getPackageManager()) != null) {
                    bldr.setNeutralButton(LocaleController.getString("VoipOfflineOpenSettings", R.string.VoipOfflineOpenSettings), new OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            activity.startActivity(settingsIntent);
                        }
                    });
                }
            }
            bldr.show();
        } else {
            if (VERSION.SDK_INT < 23 || activity.checkSelfPermission("android.permission.RECORD_AUDIO") == 0) {
                initiateCall(user, activity);
            } else {
                activity.requestPermissions(new String[]{"android.permission.RECORD_AUDIO"}, 101);
            }
        }
    }

    private static void initiateCall(final User user, final Activity activity) {
        if (activity != null) {
            if (user != null) {
                if (VoIPService.getSharedInstance() != null) {
                    if (VoIPService.getSharedInstance().getUser().id != user.id) {
                        new Builder((Context) activity).setTitle(LocaleController.getString("VoipOngoingAlertTitle", R.string.VoipOngoingAlertTitle)).setMessage(AndroidUtilities.replaceTags(LocaleController.formatString("VoipOngoingAlert", R.string.VoipOngoingAlert, ContactsController.formatName(callUser.first_name, callUser.last_name), ContactsController.formatName(user.first_name, user.last_name)))).setPositiveButton(LocaleController.getString("OK", R.string.OK), new OnClickListener() {

                            /* renamed from: org.telegram.ui.Components.voip.VoIPHelper$2$1 */
                            class C13531 implements Runnable {
                                C13531() {
                                }

                                public void run() {
                                    VoIPHelper.doInitiateCall(user, activity);
                                }
                            }

                            public void onClick(DialogInterface dialog, int which) {
                                if (VoIPService.getSharedInstance() != null) {
                                    VoIPService.getSharedInstance().hangUp(new C13531());
                                } else {
                                    VoIPHelper.doInitiateCall(user, activity);
                                }
                            }
                        }).setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null).show();
                    } else {
                        activity.startActivity(new Intent(activity, VoIPActivity.class).addFlags(268435456));
                    }
                } else if (VoIPService.callIShouldHavePutIntoIntent == null) {
                    doInitiateCall(user, activity);
                }
            }
        }
    }

    private static void doInitiateCall(User user, Activity activity) {
        if (activity != null) {
            if (user != null) {
                if (System.currentTimeMillis() - lastCallTime >= AdaptiveTrackSelection.DEFAULT_MIN_TIME_BETWEEN_BUFFER_REEVALUTATION_MS) {
                    lastCallTime = System.currentTimeMillis();
                    Intent intent = new Intent(activity, VoIPService.class);
                    intent.putExtra("user_id", user.id);
                    intent.putExtra("is_outgoing", true);
                    intent.putExtra("start_incall_activity", true);
                    intent.putExtra("account", UserConfig.selectedAccount);
                    try {
                        activity.startService(intent);
                    } catch (Throwable e) {
                        FileLog.m3e(e);
                    }
                }
            }
        }
    }

    @TargetApi(23)
    public static void permissionDenied(final Activity activity, final Runnable onFinish) {
        if (!activity.shouldShowRequestPermissionRationale("android.permission.RECORD_AUDIO")) {
            new Builder((Context) activity).setTitle(LocaleController.getString("AppName", R.string.AppName)).setMessage(LocaleController.getString("VoipNeedMicPermission", R.string.VoipNeedMicPermission)).setPositiveButton(LocaleController.getString("OK", R.string.OK), null).setNegativeButton(LocaleController.getString("Settings", R.string.Settings), new OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
                    intent.setData(Uri.fromParts("package", activity.getPackageName(), null));
                    activity.startActivity(intent);
                }
            }).show().setOnDismissListener(new OnDismissListener() {
                public void onDismiss(DialogInterface dialog) {
                    if (onFinish != null) {
                        onFinish.run();
                    }
                }
            });
        }
    }

    public static File getLogsDir() {
        File logsDir = new File(ApplicationLoader.applicationContext.getCacheDir(), "voip_logs");
        if (!logsDir.exists()) {
            logsDir.mkdirs();
        }
        return logsDir;
    }

    public static boolean canRateCall(TL_messageActionPhoneCall call) {
        if (!((call.reason instanceof TL_phoneCallDiscardReasonBusy) || (call.reason instanceof TL_phoneCallDiscardReasonMissed))) {
            for (String hash : MessagesController.getNotificationsSettings(UserConfig.selectedAccount).getStringSet("calls_access_hashes", Collections.EMPTY_SET)) {
                String[] d = hash.split(" ");
                if (d.length >= 2) {
                    String str = d[0];
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append(call.call_id);
                    stringBuilder.append(TtmlNode.ANONYMOUS_REGION_ID);
                    if (str.equals(stringBuilder.toString())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static void showRateAlert(Context context, TL_messageActionPhoneCall call) {
        for (String hash : MessagesController.getNotificationsSettings(UserConfig.selectedAccount).getStringSet("calls_access_hashes", Collections.EMPTY_SET)) {
            String[] d = hash.split(" ");
            if (d.length >= 2) {
                String str = d[0];
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(call.call_id);
                stringBuilder.append(TtmlNode.ANONYMOUS_REGION_ID);
                if (str.equals(stringBuilder.toString())) {
                    try {
                        Context context2 = context;
                        showRateAlert(context2, null, call.call_id, Long.parseLong(d[1]), UserConfig.selectedAccount);
                    } catch (Exception e) {
                    }
                    return;
                }
            }
        }
    }

    public static void showRateAlert(Context context, Runnable onDismiss, long callID, long accessHash, int account) {
        Context context2 = context;
        File logsDir = getLogsDir();
        StringBuilder stringBuilder = new StringBuilder();
        long j = callID;
        stringBuilder.append(j);
        stringBuilder.append(".log");
        File log = new File(logsDir, stringBuilder.toString());
        LinearLayout alertView = new LinearLayout(context2);
        alertView.setOrientation(1);
        int pad = AndroidUtilities.dp(16.0f);
        alertView.setPadding(pad, pad, pad, 0);
        TextView text = new TextView(context2);
        text.setTextSize(2, 16.0f);
        text.setTextColor(Theme.getColor(Theme.key_dialogTextBlack));
        text.setGravity(17);
        text.setText(LocaleController.getString("VoipRateCallAlert", R.string.VoipRateCallAlert));
        alertView.addView(text);
        BetterRatingView bar = new BetterRatingView(context2);
        alertView.addView(bar, LayoutHelper.createLinear(-2, -2, 1, 0, 16, 0, 0));
        EditText commentBox = new EditText(context2);
        commentBox.setHint(LocaleController.getString("CallReportHint", R.string.CallReportHint));
        commentBox.setInputType(147457);
        commentBox.setTextColor(Theme.getColor(Theme.key_dialogTextBlack));
        commentBox.setHintTextColor(Theme.getColor(Theme.key_dialogTextHint));
        commentBox.setBackgroundDrawable(Theme.createEditTextDrawable(context2, true));
        commentBox.setPadding(0, AndroidUtilities.dp(4.0f), 0, AndroidUtilities.dp(4.0f));
        commentBox.setTextSize(18.0f);
        commentBox.setVisibility(8);
        alertView.addView(commentBox, LayoutHelper.createLinear(-1, -2, 8.0f, 8.0f, 8.0f, 0.0f));
        final boolean[] includeLogs = new boolean[]{true};
        final CheckBoxCell checkbox = new CheckBoxCell(context2, 1);
        View.OnClickListener checkClickListener = new View.OnClickListener() {
            public void onClick(View v) {
                includeLogs[0] = includeLogs[0] ^ true;
                checkbox.setChecked(includeLogs[0], true);
            }
        };
        checkbox.setText(LocaleController.getString("CallReportIncludeLogs", R.string.CallReportIncludeLogs), null, true, false);
        checkbox.setClipToPadding(false);
        checkbox.setOnClickListener(checkClickListener);
        alertView.addView(checkbox, LayoutHelper.createLinear(-1, -2, -8.0f, 0.0f, -8.0f, 0.0f));
        TextView logsText = new TextView(context2);
        logsText.setTextSize(2, 14.0f);
        logsText.setTextColor(Theme.getColor(Theme.key_dialogTextGray3));
        logsText.setText(LocaleController.getString("CallReportLogsExplain", R.string.CallReportLogsExplain));
        logsText.setPadding(AndroidUtilities.dp(8.0f), 0, AndroidUtilities.dp(8.0f), 0);
        logsText.setOnClickListener(checkClickListener);
        alertView.addView(logsText);
        checkbox.setVisibility(8);
        logsText.setVisibility(8);
        if (!log.exists()) {
            includeLogs[0] = false;
        }
        Builder view = new Builder(context2).setTitle(LocaleController.getString("CallMessageReportProblem", R.string.CallMessageReportProblem)).setView(alertView);
        OnClickListener onClickListener = r0;
        TextView logsText2 = logsText;
        final BetterRatingView betterRatingView = bar;
        String string = LocaleController.getString("Send", R.string.Send);
        final EditText editText = commentBox;
        CheckBoxCell checkbox2 = checkbox;
        final long j2 = accessHash;
        EditText commentBox2 = commentBox;
        final long j3 = j;
        BetterRatingView bar2 = bar;
        final int i = account;
        TextView text2 = text;
        final boolean[] zArr = includeLogs;
        BetterRatingView bar3 = bar2;
        Builder builder = view;
        final File file = log;
        pad = context2;
        OnClickListener c13597 = new OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                final int currentAccount = UserConfig.selectedAccount;
                final TL_phone_setCallRating req = new TL_phone_setCallRating();
                req.rating = betterRatingView.getRating();
                if (req.rating < 5) {
                    req.comment = editText.getText().toString();
                } else {
                    req.comment = TtmlNode.ANONYMOUS_REGION_ID;
                }
                req.peer = new TL_inputPhoneCall();
                req.peer.access_hash = j2;
                req.peer.id = j3;
                ConnectionsManager.getInstance(i).sendRequest(req, new RequestDelegate() {
                    public void run(TLObject response, TL_error error) {
                        if (response instanceof TL_updates) {
                            MessagesController.getInstance(currentAccount).processUpdates((TL_updates) response, false);
                            if (zArr[0] && file.exists() && req.rating < 4) {
                                SendMessagesHelper.prepareSendingDocument(file.getAbsolutePath(), file.getAbsolutePath(), null, "text/plain", 4244000, null, null);
                                Toast.makeText(pad, LocaleController.getString("CallReportSent", R.string.CallReportSent), 1).show();
                            }
                        }
                    }
                });
            }
        };
        final Runnable runnable = onDismiss;
        View btn = builder.setPositiveButton(string, c13597).setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null).setOnDismissListener(new OnDismissListener() {
            public void onDismiss(DialogInterface dialog) {
                if (runnable != null) {
                    runnable.run();
                }
            }
        }).show().getButton(-1);
        btn.setEnabled(false);
        final View view2 = btn;
        editText = commentBox2;
        final Context context3 = context2;
        final File file2 = log;
        final CheckBoxCell checkBoxCell = checkbox2;
        final TextView textView = logsText2;
        bar3.setOnRatingChangeListener(new OnRatingChangeListener() {
            public void onRatingChanged(int rating) {
                String str;
                int i;
                int i2 = 0;
                view2.setEnabled(rating > 0);
                EditText editText = editText;
                if (rating < 4) {
                    str = "CallReportHint";
                    i = R.string.CallReportHint;
                } else {
                    str = "VoipFeedbackCommentHint";
                    i = R.string.VoipFeedbackCommentHint;
                }
                editText.setHint(LocaleController.getString(str, i));
                editText = editText;
                int i3 = (rating >= 5 || rating <= 0) ? 8 : 0;
                editText.setVisibility(i3);
                if (editText.getVisibility() == 8) {
                    ((InputMethodManager) context3.getSystemService("input_method")).hideSoftInputFromWindow(editText.getWindowToken(), 0);
                }
                if (file2.exists()) {
                    checkBoxCell.setVisibility(rating < 4 ? 0 : 8);
                    TextView textView = textView;
                    if (rating >= 4) {
                        i2 = 8;
                    }
                    textView.setVisibility(i2);
                }
            }
        });
    }

    public static void upgradeP2pSetting(int account) {
        SharedPreferences prefs = MessagesController.getMainSettings(account);
        if (prefs.contains("calls_p2p")) {
            Editor e = prefs.edit();
            if (!prefs.getBoolean("calls_p2p", true)) {
                e.putInt("calls_p2p_new", 2);
            }
            e.remove("calls_p2p").commit();
        }
    }
}
