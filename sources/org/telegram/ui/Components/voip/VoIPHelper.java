package org.telegram.ui.Components.voip;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.Build.VERSION;
import android.provider.Settings.System;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import org.telegram.messenger.AccountInstance;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.DownloadController;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.SendMessagesHelper;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.voip.VoIPService;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.PhoneCallDiscardReason;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_inputPhoneCall;
import org.telegram.tgnet.TLRPC.TL_messageActionPhoneCall;
import org.telegram.tgnet.TLRPC.TL_phoneCallDiscardReasonBusy;
import org.telegram.tgnet.TLRPC.TL_phoneCallDiscardReasonMissed;
import org.telegram.tgnet.TLRPC.TL_phone_setCallRating;
import org.telegram.tgnet.TLRPC.TL_updates;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.tgnet.TLRPC.UserFull;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.AlertDialog.Builder;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.CheckBoxCell;
import org.telegram.ui.Cells.TextCheckCell;
import org.telegram.ui.Components.BetterRatingView;
import org.telegram.ui.Components.BetterRatingView.OnRatingChangeListener;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.LaunchActivity;
import org.telegram.ui.VoIPActivity;

public class VoIPHelper {
    private static final int VOIP_SUPPORT_ID = 4244000;
    public static long lastCallTime;

    public static void startCall(User user, Activity activity, UserFull userFull) {
        String str = "OK";
        int i = 1;
        if (userFull != null && userFull.phone_calls_private) {
            new Builder((Context) activity).setTitle(LocaleController.getString("VoipFailed", NUM)).setMessage(AndroidUtilities.replaceTags(LocaleController.formatString("CallNotAvailable", NUM, ContactsController.formatName(user.first_name, user.last_name)))).setPositiveButton(LocaleController.getString(str, NUM), null).show();
        } else if (ConnectionsManager.getInstance(UserConfig.selectedAccount).getConnectionState() != 3) {
            int i2;
            String str2;
            if (System.getInt(activity.getContentResolver(), "airplane_mode_on", 0) == 0) {
                i = 0;
            }
            Builder builder = new Builder((Context) activity);
            if (i != 0) {
                i2 = NUM;
                str2 = "VoipOfflineAirplaneTitle";
            } else {
                i2 = NUM;
                str2 = "VoipOfflineTitle";
            }
            builder = builder.setTitle(LocaleController.getString(str2, i2));
            if (i != 0) {
                i2 = NUM;
                str2 = "VoipOfflineAirplane";
            } else {
                i2 = NUM;
                str2 = "VoipOffline";
            }
            builder = builder.setMessage(LocaleController.getString(str2, i2)).setPositiveButton(LocaleController.getString(str, NUM), null);
            if (i != 0) {
                Intent intent = new Intent("android.settings.AIRPLANE_MODE_SETTINGS");
                if (intent.resolveActivity(activity.getPackageManager()) != null) {
                    builder.setNeutralButton(LocaleController.getString("VoipOfflineOpenSettings", NUM), new -$$Lambda$VoIPHelper$p72unkFJ30ZMfZqzJkWjR1RFxeM(activity, intent));
                }
            }
            builder.show();
        } else {
            if (VERSION.SDK_INT >= 23) {
                if (activity.checkSelfPermission("android.permission.RECORD_AUDIO") != 0) {
                    activity.requestPermissions(new String[]{"android.permission.RECORD_AUDIO"}, 101);
                }
            }
            initiateCall(user, activity);
        }
    }

    private static void initiateCall(User user, Activity activity) {
        if (activity != null && user != null) {
            if (VoIPService.getSharedInstance() != null) {
                if (VoIPService.getSharedInstance().getUser().id != user.id) {
                    new Builder((Context) activity).setTitle(LocaleController.getString("VoipOngoingAlertTitle", NUM)).setMessage(AndroidUtilities.replaceTags(LocaleController.formatString("VoipOngoingAlert", NUM, ContactsController.formatName(r0.first_name, r0.last_name), ContactsController.formatName(user.first_name, user.last_name)))).setPositiveButton(LocaleController.getString("OK", NUM), new -$$Lambda$VoIPHelper$zyuwLXXVdgKRJLU2rpi7FSmrvEY(user, activity)).setNegativeButton(LocaleController.getString("Cancel", NUM), null).show();
                    return;
                }
                activity.startActivity(new Intent(activity, VoIPActivity.class).addFlags(NUM));
            } else if (VoIPService.callIShouldHavePutIntoIntent == null) {
                doInitiateCall(user, activity);
            }
        }
    }

    static /* synthetic */ void lambda$initiateCall$2(User user, Activity activity, DialogInterface dialogInterface, int i) {
        if (VoIPService.getSharedInstance() != null) {
            VoIPService.getSharedInstance().hangUp(new -$$Lambda$VoIPHelper$FLpKQd1yLbCCIVZIjPmbCXMk1eM(user, activity));
        } else {
            doInitiateCall(user, activity);
        }
    }

    private static void doInitiateCall(User user, Activity activity) {
        if (activity != null && user != null && System.currentTimeMillis() - lastCallTime >= 2000) {
            lastCallTime = System.currentTimeMillis();
            Intent intent = new Intent(activity, VoIPService.class);
            intent.putExtra("user_id", user.id);
            intent.putExtra("is_outgoing", true);
            intent.putExtra("start_incall_activity", true);
            intent.putExtra("account", UserConfig.selectedAccount);
            try {
                activity.startService(intent);
            } catch (Throwable th) {
                FileLog.e(th);
            }
        }
    }

    @TargetApi(23)
    public static void permissionDenied(Activity activity, Runnable runnable) {
        if (!activity.shouldShowRequestPermissionRationale("android.permission.RECORD_AUDIO")) {
            new Builder((Context) activity).setTitle(LocaleController.getString("AppName", NUM)).setMessage(LocaleController.getString("VoipNeedMicPermission", NUM)).setPositiveButton(LocaleController.getString("OK", NUM), null).setNegativeButton(LocaleController.getString("Settings", NUM), new -$$Lambda$VoIPHelper$FA_FNnkMv1v3U-GVwPoWnADAJTE(activity)).show().setOnDismissListener(new -$$Lambda$VoIPHelper$DMzandLH_8HcSvLneI5IhtdRHzk(runnable));
        }
    }

    static /* synthetic */ void lambda$permissionDenied$3(Activity activity, DialogInterface dialogInterface, int i) {
        Intent intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
        intent.setData(Uri.fromParts("package", activity.getPackageName(), null));
        activity.startActivity(intent);
    }

    static /* synthetic */ void lambda$permissionDenied$4(Runnable runnable, DialogInterface dialogInterface) {
        if (runnable != null) {
            runnable.run();
        }
    }

    public static File getLogsDir() {
        File file = new File(ApplicationLoader.applicationContext.getCacheDir(), "voip_logs");
        if (!file.exists()) {
            file.mkdirs();
        }
        return file;
    }

    public static boolean canRateCall(TL_messageActionPhoneCall tL_messageActionPhoneCall) {
        PhoneCallDiscardReason phoneCallDiscardReason = tL_messageActionPhoneCall.reason;
        if (!((phoneCallDiscardReason instanceof TL_phoneCallDiscardReasonBusy) || (phoneCallDiscardReason instanceof TL_phoneCallDiscardReasonMissed))) {
            for (String split : MessagesController.getNotificationsSettings(UserConfig.selectedAccount).getStringSet("calls_access_hashes", Collections.EMPTY_SET)) {
                String split2;
                String[] split3 = split2.split(" ");
                if (split3.length >= 2) {
                    split2 = split3[0];
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append(tL_messageActionPhoneCall.call_id);
                    stringBuilder.append("");
                    if (split2.equals(stringBuilder.toString())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static void showRateAlert(Context context, TL_messageActionPhoneCall tL_messageActionPhoneCall) {
        for (String split : MessagesController.getNotificationsSettings(UserConfig.selectedAccount).getStringSet("calls_access_hashes", Collections.EMPTY_SET)) {
            String[] split2 = split.split(" ");
            if (split2.length >= 2) {
                String str = split2[0];
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(tL_messageActionPhoneCall.call_id);
                stringBuilder.append("");
                if (str.equals(stringBuilder.toString())) {
                    try {
                        long parseLong = Long.parseLong(split2[1]);
                        showRateAlert(context, null, tL_messageActionPhoneCall.call_id, parseLong, UserConfig.selectedAccount, true);
                        return;
                    } catch (Exception unused) {
                        return;
                    }
                }
            }
        }
    }

    public static void showRateAlert(Context context, Runnable runnable, long j, long j2, int i, boolean z) {
        CheckBoxCell checkBoxCell;
        final Context context2 = context;
        final File logFile = getLogFile(j);
        int i2 = 1;
        final int[] iArr = new int[]{0};
        LinearLayout linearLayout = new LinearLayout(context2);
        linearLayout.setOrientation(1);
        int dp = AndroidUtilities.dp(16.0f);
        linearLayout.setPadding(dp, dp, dp, 0);
        final TextView textView = new TextView(context2);
        textView.setTextSize(2, 16.0f);
        String str = "dialogTextBlack";
        textView.setTextColor(Theme.getColor(str));
        textView.setGravity(17);
        textView.setText(LocaleController.getString("VoipRateCallAlert", NUM));
        linearLayout.addView(textView);
        BetterRatingView betterRatingView = new BetterRatingView(context2);
        linearLayout.addView(betterRatingView, LayoutHelper.createLinear(-2, -2, 1, 0, 16, 0, 0));
        LinearLayout linearLayout2 = new LinearLayout(context2);
        linearLayout2.setOrientation(1);
        AnonymousClass1 anonymousClass1 = new OnClickListener() {
            public void onClick(View view) {
                CheckBoxCell checkBoxCell = (CheckBoxCell) view;
                checkBoxCell.setChecked(checkBoxCell.isChecked() ^ 1, true);
            }
        };
        String[] strArr = new String[]{"echo", "noise", "interruptions", "distorted_speech", "silent_local", "silent_remote", "dropped"};
        int i3 = 0;
        while (i3 < strArr.length) {
            CharSequence string;
            checkBoxCell = new CheckBoxCell(context2, i2);
            checkBoxCell.setClipToPadding(false);
            checkBoxCell.setTag(strArr[i3]);
            switch (i3) {
                case 0:
                    string = LocaleController.getString("RateCallEcho", NUM);
                    break;
                case 1:
                    string = LocaleController.getString("RateCallNoise", NUM);
                    break;
                case 2:
                    string = LocaleController.getString("RateCallInterruptions", NUM);
                    break;
                case 3:
                    string = LocaleController.getString("RateCallDistorted", NUM);
                    break;
                case 4:
                    string = LocaleController.getString("RateCallSilentLocal", NUM);
                    break;
                case 5:
                    string = LocaleController.getString("RateCallSilentRemote", NUM);
                    break;
                case 6:
                    string = LocaleController.getString("RateCallDropped", NUM);
                    break;
                default:
                    string = null;
                    break;
            }
            checkBoxCell.setText(string, null, false, false);
            checkBoxCell.setOnClickListener(anonymousClass1);
            checkBoxCell.setTag(strArr[i3]);
            linearLayout2.addView(checkBoxCell);
            i3++;
            i2 = 1;
        }
        linearLayout.addView(linearLayout2, LayoutHelper.createLinear(-1, -2, -8.0f, 0.0f, -8.0f, 0.0f));
        linearLayout2.setVisibility(8);
        EditText editText = new EditText(context2);
        editText.setHint(LocaleController.getString("VoipFeedbackCommentHint", NUM));
        editText.setInputType(147457);
        editText.setTextColor(Theme.getColor(str));
        editText.setHintTextColor(Theme.getColor("dialogTextHint"));
        editText.setBackgroundDrawable(Theme.createEditTextDrawable(context2, true));
        editText.setPadding(0, AndroidUtilities.dp(4.0f), 0, AndroidUtilities.dp(4.0f));
        editText.setTextSize(18.0f);
        editText.setVisibility(8);
        linearLayout.addView(editText, LayoutHelper.createLinear(-1, -2, 8.0f, 8.0f, 8.0f, 0.0f));
        final boolean[] zArr = new boolean[]{true};
        checkBoxCell = new CheckBoxCell(context2, 1);
        AnonymousClass2 anonymousClass2 = new OnClickListener() {
            public void onClick(View view) {
                boolean[] zArr = zArr;
                zArr[0] = zArr[0] ^ 1;
                checkBoxCell.setChecked(zArr[0], true);
            }
        };
        checkBoxCell.setText(LocaleController.getString("CallReportIncludeLogs", NUM), null, true, false);
        checkBoxCell.setClipToPadding(false);
        checkBoxCell.setOnClickListener(anonymousClass2);
        linearLayout.addView(checkBoxCell, LayoutHelper.createLinear(-1, -2, -8.0f, 0.0f, -8.0f, 0.0f));
        TextView textView2 = new TextView(context2);
        textView2.setTextSize(2, 14.0f);
        textView2.setTextColor(Theme.getColor("dialogTextGray3"));
        textView2.setText(LocaleController.getString("CallReportLogsExplain", NUM));
        textView2.setPadding(AndroidUtilities.dp(8.0f), 0, AndroidUtilities.dp(8.0f), 0);
        textView2.setOnClickListener(anonymousClass2);
        linearLayout.addView(textView2);
        checkBoxCell.setVisibility(8);
        textView2.setVisibility(8);
        if (!logFile.exists()) {
            zArr[0] = false;
        }
        final Runnable runnable2 = runnable;
        AlertDialog create = new Builder(context2).setTitle(LocaleController.getString("CallMessageReportProblem", NUM)).setView(linearLayout).setPositiveButton(LocaleController.getString("Send", NUM), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        }).setNegativeButton(LocaleController.getString("Cancel", NUM), null).setOnDismissListener(new OnDismissListener() {
            public void onDismiss(DialogInterface dialogInterface) {
                Runnable runnable = runnable2;
                if (runnable != null) {
                    runnable.run();
                }
            }
        }).create();
        if (BuildVars.DEBUG_VERSION && logFile.exists()) {
            create.setNeutralButton("Send log", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int i) {
                    Intent intent = new Intent(context2, LaunchActivity.class);
                    intent.setAction("android.intent.action.SEND");
                    intent.putExtra("android.intent.extra.STREAM", Uri.fromFile(logFile));
                    context2.startActivity(intent);
                }
            });
        }
        create.show();
        create.getWindow().setSoftInputMode(3);
        final View button = create.getButton(-1);
        final View view = button;
        button.setEnabled(false);
        betterRatingView.setOnRatingChangeListener(new OnRatingChangeListener() {
            public void onRatingChanged(int i) {
                String str;
                button.setEnabled(i > 0);
                TextView textView = (TextView) button;
                if (i < 4) {
                    i = NUM;
                    str = "Next";
                } else {
                    i = NUM;
                    str = "Send";
                }
                textView.setText(LocaleController.getString(str, i).toUpperCase());
            }
        });
        final BetterRatingView betterRatingView2 = betterRatingView;
        linearLayout = linearLayout2;
        final EditText editText2 = editText;
        final boolean[] zArr2 = zArr;
        final long j3 = j2;
        AnonymousClass7 anonymousClass7 = r0;
        View view2 = button;
        final long j4 = j;
        AlertDialog alertDialog = create;
        final boolean z2 = z;
        final TextView textView3 = textView2;
        i3 = i;
        context2 = context;
        CheckBoxCell checkBoxCell2 = checkBoxCell;
        final AlertDialog alertDialog2 = alertDialog;
        final CheckBoxCell checkBoxCell3 = checkBoxCell2;
        AnonymousClass7 anonymousClass72 = new OnClickListener() {
            public void onClick(View view) {
                if (betterRatingView2.getRating() < 4) {
                    int[] iArr = iArr;
                    if (iArr[0] != 1) {
                        iArr[0] = 1;
                        betterRatingView2.setVisibility(8);
                        textView.setVisibility(8);
                        alertDialog2.setTitle(LocaleController.getString("CallReportHint", NUM));
                        editText2.setVisibility(0);
                        if (logFile.exists()) {
                            checkBoxCell3.setVisibility(0);
                            textView3.setVisibility(0);
                        }
                        linearLayout.setVisibility(0);
                        ((TextView) view).setText(LocaleController.getString("Send", NUM).toUpperCase());
                        return;
                    }
                }
                final int i = UserConfig.selectedAccount;
                final TL_phone_setCallRating tL_phone_setCallRating = new TL_phone_setCallRating();
                tL_phone_setCallRating.rating = betterRatingView2.getRating();
                final ArrayList arrayList = new ArrayList();
                for (int i2 = 0; i2 < linearLayout.getChildCount(); i2++) {
                    CheckBoxCell checkBoxCell = (CheckBoxCell) linearLayout.getChildAt(i2);
                    if (checkBoxCell.isChecked()) {
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append("#");
                        stringBuilder.append(checkBoxCell.getTag());
                        arrayList.add(stringBuilder.toString());
                    }
                }
                if (tL_phone_setCallRating.rating < 5) {
                    tL_phone_setCallRating.comment = editText2.getText().toString();
                } else {
                    tL_phone_setCallRating.comment = "";
                }
                if (!(arrayList.isEmpty() || zArr2[0])) {
                    StringBuilder stringBuilder2 = new StringBuilder();
                    stringBuilder2.append(tL_phone_setCallRating.comment);
                    String str = " ";
                    stringBuilder2.append(str);
                    stringBuilder2.append(TextUtils.join(str, arrayList));
                    tL_phone_setCallRating.comment = stringBuilder2.toString();
                }
                tL_phone_setCallRating.peer = new TL_inputPhoneCall();
                TL_inputPhoneCall tL_inputPhoneCall = tL_phone_setCallRating.peer;
                tL_inputPhoneCall.access_hash = j3;
                tL_inputPhoneCall.id = j4;
                tL_phone_setCallRating.user_initiative = z2;
                ConnectionsManager.getInstance(i3).sendRequest(tL_phone_setCallRating, new RequestDelegate() {
                    public void run(TLObject tLObject, TL_error tL_error) {
                        if (tLObject instanceof TL_updates) {
                            MessagesController.getInstance(i).processUpdates((TL_updates) tLObject, false);
                        }
                        AnonymousClass7 anonymousClass7 = AnonymousClass7.this;
                        if (zArr2[0] && logFile.exists() && tL_phone_setCallRating.rating < 4) {
                            SendMessagesHelper.prepareSendingDocument(AccountInstance.getInstance(UserConfig.selectedAccount), logFile.getAbsolutePath(), logFile.getAbsolutePath(), null, TextUtils.join(" ", arrayList), "text/plain", 4244000, null, null, null, true, 0);
                            Toast.makeText(context2, LocaleController.getString("CallReportSent", NUM), 1).show();
                        }
                    }
                });
                alertDialog2.dismiss();
            }
        };
        view2.setOnClickListener(anonymousClass7);
    }

    private static File getLogFile(long j) {
        if (BuildVars.DEBUG_VERSION) {
            File file = new File(ApplicationLoader.applicationContext.getExternalFilesDir(null), "logs");
            String[] list = file.list();
            if (list != null) {
                for (String str : list) {
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("voip");
                    stringBuilder.append(j);
                    stringBuilder.append(".txt");
                    if (str.endsWith(stringBuilder.toString())) {
                        return new File(file, str);
                    }
                }
            }
        }
        File logsDir = getLogsDir();
        StringBuilder stringBuilder2 = new StringBuilder();
        stringBuilder2.append(j);
        stringBuilder2.append(".log");
        return new File(logsDir, stringBuilder2.toString());
    }

    public static void showCallDebugSettings(Context context) {
        final SharedPreferences globalMainSettings = MessagesController.getGlobalMainSettings();
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(1);
        TextView textView = new TextView(context);
        textView.setTextSize(1, 15.0f);
        textView.setText("Please only change these settings if you know exactly what they do.");
        textView.setTextColor(Theme.getColor("dialogTextBlack"));
        linearLayout.addView(textView, LayoutHelper.createLinear(-1, -2, 16.0f, 8.0f, 16.0f, 8.0f));
        final TextCheckCell textCheckCell = new TextCheckCell(context);
        textCheckCell.setTextAndCheck("Force TCP", globalMainSettings.getBoolean("dbg_force_tcp_in_calls", false), false);
        textCheckCell.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                String str = "dbg_force_tcp_in_calls";
                boolean z = globalMainSettings.getBoolean(str, false);
                Editor edit = globalMainSettings.edit();
                edit.putBoolean(str, z ^ 1);
                edit.commit();
                textCheckCell.setChecked(z ^ 1);
            }
        });
        linearLayout.addView(textCheckCell);
        if (BuildVars.DEBUG_VERSION && BuildVars.LOGS_ENABLED) {
            textCheckCell = new TextCheckCell(context);
            textCheckCell.setTextAndCheck("Dump detailed stats", globalMainSettings.getBoolean("dbg_dump_call_stats", false), false);
            textCheckCell.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    String str = "dbg_dump_call_stats";
                    boolean z = globalMainSettings.getBoolean(str, false);
                    Editor edit = globalMainSettings.edit();
                    edit.putBoolean(str, z ^ 1);
                    edit.commit();
                    textCheckCell.setChecked(z ^ 1);
                }
            });
            linearLayout.addView(textCheckCell);
        }
        if (VERSION.SDK_INT >= 26) {
            textCheckCell = new TextCheckCell(context);
            textCheckCell.setTextAndCheck("Enable ConnectionService", globalMainSettings.getBoolean("dbg_force_connection_service", false), false);
            textCheckCell.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    String str = "dbg_force_connection_service";
                    boolean z = globalMainSettings.getBoolean(str, false);
                    Editor edit = globalMainSettings.edit();
                    edit.putBoolean(str, z ^ 1);
                    edit.commit();
                    textCheckCell.setChecked(z ^ 1);
                }
            });
            linearLayout.addView(textCheckCell);
        }
        new Builder(context).setTitle(LocaleController.getString("DebugMenuCallSettings", NUM)).setView(linearLayout).show();
    }

    public static int getDataSavingDefault() {
        boolean z = DownloadController.getInstance(0).lowPreset.lessCallData;
        boolean z2 = DownloadController.getInstance(0).mediumPreset.lessCallData;
        boolean z3 = DownloadController.getInstance(0).highPreset.lessCallData;
        if (!z && !z2 && !z3) {
            return 0;
        }
        if (z && !z2 && !z3) {
            return 3;
        }
        if (z && z2 && !z3) {
            return 1;
        }
        if (z && z2 && z3) {
            return 2;
        }
        if (BuildVars.LOGS_ENABLED) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Invalid call data saving preset configuration: ");
            stringBuilder.append(z);
            String str = "/";
            stringBuilder.append(str);
            stringBuilder.append(z2);
            stringBuilder.append(str);
            stringBuilder.append(z3);
            FileLog.w(stringBuilder.toString());
        }
        return 0;
    }
}
