package org.telegram.ui.Components.voip;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.core.view.inputmethod.InputContentInfoCompat;
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
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.SendMessagesHelper;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.voip.VoIPService;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.CheckBoxCell;
import org.telegram.ui.Cells.TextCheckCell;
import org.telegram.ui.Components.BetterRatingView;
import org.telegram.ui.Components.EditTextBoldCursor;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.LaunchActivity;
import org.telegram.ui.VoIPActivity;

public class VoIPHelper {
    private static final int VOIP_SUPPORT_ID = 4244000;
    public static long lastCallTime;

    public static void startCall(TLRPC.User user, Activity activity, TLRPC.UserFull userFull) {
        int i;
        String str;
        int i2;
        String str2;
        boolean z = true;
        if (userFull != null && userFull.phone_calls_private) {
            new AlertDialog.Builder((Context) activity).setTitle(LocaleController.getString("VoipFailed", NUM)).setMessage(AndroidUtilities.replaceTags(LocaleController.formatString("CallNotAvailable", NUM, ContactsController.formatName(user.first_name, user.last_name)))).setPositiveButton(LocaleController.getString("OK", NUM), (DialogInterface.OnClickListener) null).show();
        } else if (ConnectionsManager.getInstance(UserConfig.selectedAccount).getConnectionState() != 3) {
            if (Settings.System.getInt(activity.getContentResolver(), "airplane_mode_on", 0) == 0) {
                z = false;
            }
            AlertDialog.Builder builder = new AlertDialog.Builder((Context) activity);
            if (z) {
                i = NUM;
                str = "VoipOfflineAirplaneTitle";
            } else {
                i = NUM;
                str = "VoipOfflineTitle";
            }
            AlertDialog.Builder title = builder.setTitle(LocaleController.getString(str, i));
            if (z) {
                i2 = NUM;
                str2 = "VoipOfflineAirplane";
            } else {
                i2 = NUM;
                str2 = "VoipOffline";
            }
            AlertDialog.Builder positiveButton = title.setMessage(LocaleController.getString(str2, i2)).setPositiveButton(LocaleController.getString("OK", NUM), (DialogInterface.OnClickListener) null);
            if (z) {
                Intent intent = new Intent("android.settings.AIRPLANE_MODE_SETTINGS");
                if (intent.resolveActivity(activity.getPackageManager()) != null) {
                    positiveButton.setNeutralButton(LocaleController.getString("VoipOfflineOpenSettings", NUM), new DialogInterface.OnClickListener(activity, intent) {
                        private final /* synthetic */ Activity f$0;
                        private final /* synthetic */ Intent f$1;

                        {
                            this.f$0 = r1;
                            this.f$1 = r2;
                        }

                        public final void onClick(DialogInterface dialogInterface, int i) {
                            this.f$0.startActivity(this.f$1);
                        }
                    });
                }
            }
            positiveButton.show();
        } else if (Build.VERSION.SDK_INT < 23 || activity.checkSelfPermission("android.permission.RECORD_AUDIO") == 0) {
            initiateCall(user, activity);
        } else {
            activity.requestPermissions(new String[]{"android.permission.RECORD_AUDIO"}, 101);
        }
    }

    private static void initiateCall(TLRPC.User user, Activity activity) {
        if (activity != null && user != null) {
            if (VoIPService.getSharedInstance() != null) {
                TLRPC.User user2 = VoIPService.getSharedInstance().getUser();
                if (user2.id != user.id) {
                    new AlertDialog.Builder((Context) activity).setTitle(LocaleController.getString("VoipOngoingAlertTitle", NUM)).setMessage(AndroidUtilities.replaceTags(LocaleController.formatString("VoipOngoingAlert", NUM, ContactsController.formatName(user2.first_name, user2.last_name), ContactsController.formatName(user.first_name, user.last_name)))).setPositiveButton(LocaleController.getString("OK", NUM), new DialogInterface.OnClickListener(activity) {
                        private final /* synthetic */ Activity f$1;

                        {
                            this.f$1 = r2;
                        }

                        public final void onClick(DialogInterface dialogInterface, int i) {
                            VoIPHelper.lambda$initiateCall$2(TLRPC.User.this, this.f$1, dialogInterface, i);
                        }
                    }).setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null).show();
                    return;
                }
                activity.startActivity(new Intent(activity, VoIPActivity.class).addFlags(NUM));
            } else if (VoIPService.callIShouldHavePutIntoIntent == null) {
                doInitiateCall(user, activity);
            }
        }
    }

    static /* synthetic */ void lambda$initiateCall$2(TLRPC.User user, Activity activity, DialogInterface dialogInterface, int i) {
        if (VoIPService.getSharedInstance() != null) {
            VoIPService.getSharedInstance().hangUp(new Runnable(activity) {
                private final /* synthetic */ Activity f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    VoIPHelper.doInitiateCall(TLRPC.User.this, this.f$1);
                }
            });
        } else {
            doInitiateCall(user, activity);
        }
    }

    /* access modifiers changed from: private */
    public static void doInitiateCall(TLRPC.User user, Activity activity) {
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
            new AlertDialog.Builder((Context) activity).setTitle(LocaleController.getString("AppName", NUM)).setMessage(LocaleController.getString("VoipNeedMicPermission", NUM)).setPositiveButton(LocaleController.getString("OK", NUM), (DialogInterface.OnClickListener) null).setNegativeButton(LocaleController.getString("Settings", NUM), new DialogInterface.OnClickListener(activity) {
                private final /* synthetic */ Activity f$0;

                {
                    this.f$0 = r1;
                }

                public final void onClick(DialogInterface dialogInterface, int i) {
                    VoIPHelper.lambda$permissionDenied$3(this.f$0, dialogInterface, i);
                }
            }).show().setOnDismissListener(new DialogInterface.OnDismissListener(runnable) {
                private final /* synthetic */ Runnable f$0;

                {
                    this.f$0 = r1;
                }

                public final void onDismiss(DialogInterface dialogInterface) {
                    VoIPHelper.lambda$permissionDenied$4(this.f$0, dialogInterface);
                }
            });
        }
    }

    static /* synthetic */ void lambda$permissionDenied$3(Activity activity, DialogInterface dialogInterface, int i) {
        Intent intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
        intent.setData(Uri.fromParts("package", activity.getPackageName(), (String) null));
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

    public static boolean canRateCall(TLRPC.TL_messageActionPhoneCall tL_messageActionPhoneCall) {
        TLRPC.PhoneCallDiscardReason phoneCallDiscardReason = tL_messageActionPhoneCall.reason;
        if (!(phoneCallDiscardReason instanceof TLRPC.TL_phoneCallDiscardReasonBusy) && !(phoneCallDiscardReason instanceof TLRPC.TL_phoneCallDiscardReasonMissed)) {
            for (String split : MessagesController.getNotificationsSettings(UserConfig.selectedAccount).getStringSet("calls_access_hashes", Collections.EMPTY_SET)) {
                String[] split2 = split.split(" ");
                if (split2.length >= 2) {
                    String str = split2[0];
                    if (str.equals(tL_messageActionPhoneCall.call_id + "")) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static void showRateAlert(Context context, TLRPC.TL_messageActionPhoneCall tL_messageActionPhoneCall) {
        for (String split : MessagesController.getNotificationsSettings(UserConfig.selectedAccount).getStringSet("calls_access_hashes", Collections.EMPTY_SET)) {
            String[] split2 = split.split(" ");
            if (split2.length >= 2) {
                String str = split2[0];
                if (str.equals(tL_messageActionPhoneCall.call_id + "")) {
                    try {
                        long parseLong = Long.parseLong(split2[1]);
                        showRateAlert(context, (Runnable) null, tL_messageActionPhoneCall.call_id, parseLong, UserConfig.selectedAccount, true);
                        return;
                    } catch (Exception unused) {
                        return;
                    }
                }
            }
        }
    }

    public static void showRateAlert(Context context, Runnable runnable, long j, long j2, int i, boolean z) {
        String str;
        final Context context2 = context;
        final File logFile = getLogFile(j);
        int i2 = 1;
        final int[] iArr = {0};
        LinearLayout linearLayout = new LinearLayout(context2);
        linearLayout.setOrientation(1);
        int dp = AndroidUtilities.dp(16.0f);
        linearLayout.setPadding(dp, dp, dp, 0);
        final TextView textView = new TextView(context2);
        textView.setTextSize(2, 16.0f);
        textView.setTextColor(Theme.getColor("dialogTextBlack"));
        textView.setGravity(17);
        textView.setText(LocaleController.getString("VoipRateCallAlert", NUM));
        linearLayout.addView(textView);
        BetterRatingView betterRatingView = new BetterRatingView(context2);
        linearLayout.addView(betterRatingView, LayoutHelper.createLinear(-2, -2, 1, 0, 16, 0, 0));
        LinearLayout linearLayout2 = new LinearLayout(context2);
        linearLayout2.setOrientation(1);
        AnonymousClass1 r8 = new View.OnClickListener() {
            public void onClick(View view) {
                CheckBoxCell checkBoxCell = (CheckBoxCell) view;
                checkBoxCell.setChecked(!checkBoxCell.isChecked(), true);
            }
        };
        String[] strArr = {"echo", "noise", "interruptions", "distorted_speech", "silent_local", "silent_remote", "dropped"};
        int i3 = 0;
        while (i3 < strArr.length) {
            CheckBoxCell checkBoxCell = new CheckBoxCell(context2, i2);
            checkBoxCell.setClipToPadding(false);
            checkBoxCell.setTag(strArr[i3]);
            switch (i3) {
                case 0:
                    str = LocaleController.getString("RateCallEcho", NUM);
                    break;
                case 1:
                    str = LocaleController.getString("RateCallNoise", NUM);
                    break;
                case 2:
                    str = LocaleController.getString("RateCallInterruptions", NUM);
                    break;
                case 3:
                    str = LocaleController.getString("RateCallDistorted", NUM);
                    break;
                case 4:
                    str = LocaleController.getString("RateCallSilentLocal", NUM);
                    break;
                case 5:
                    str = LocaleController.getString("RateCallSilentRemote", NUM);
                    break;
                case 6:
                    str = LocaleController.getString("RateCallDropped", NUM);
                    break;
                default:
                    str = null;
                    break;
            }
            checkBoxCell.setText(str, (String) null, false, false);
            checkBoxCell.setOnClickListener(r8);
            checkBoxCell.setTag(strArr[i3]);
            linearLayout2.addView(checkBoxCell);
            i3++;
            i2 = 1;
        }
        linearLayout.addView(linearLayout2, LayoutHelper.createLinear(-1, -2, -8.0f, 0.0f, -8.0f, 0.0f));
        linearLayout2.setVisibility(8);
        EditTextBoldCursor editTextBoldCursor = new EditTextBoldCursor(context2);
        editTextBoldCursor.setHint(LocaleController.getString("VoipFeedbackCommentHint", NUM));
        editTextBoldCursor.setInputType(147457);
        editTextBoldCursor.setTextColor(Theme.getColor("dialogTextBlack"));
        editTextBoldCursor.setHintTextColor(Theme.getColor("dialogTextHint"));
        editTextBoldCursor.setBackgroundDrawable(Theme.createEditTextDrawable(context2, true));
        editTextBoldCursor.setPadding(0, AndroidUtilities.dp(4.0f), 0, AndroidUtilities.dp(4.0f));
        editTextBoldCursor.setTextSize(18.0f);
        editTextBoldCursor.setVisibility(8);
        linearLayout.addView(editTextBoldCursor, LayoutHelper.createLinear(-1, -2, 8.0f, 8.0f, 8.0f, 0.0f));
        final boolean[] zArr = {true};
        final CheckBoxCell checkBoxCell2 = new CheckBoxCell(context2, 1);
        AnonymousClass2 r9 = new View.OnClickListener() {
            public void onClick(View view) {
                boolean[] zArr = zArr;
                zArr[0] = !zArr[0];
                checkBoxCell2.setChecked(zArr[0], true);
            }
        };
        checkBoxCell2.setText(LocaleController.getString("CallReportIncludeLogs", NUM), (String) null, true, false);
        checkBoxCell2.setClipToPadding(false);
        checkBoxCell2.setOnClickListener(r9);
        linearLayout.addView(checkBoxCell2, LayoutHelper.createLinear(-1, -2, -8.0f, 0.0f, -8.0f, 0.0f));
        TextView textView2 = new TextView(context2);
        textView2.setTextSize(2, 14.0f);
        textView2.setTextColor(Theme.getColor("dialogTextGray3"));
        textView2.setText(LocaleController.getString("CallReportLogsExplain", NUM));
        textView2.setPadding(AndroidUtilities.dp(8.0f), 0, AndroidUtilities.dp(8.0f), 0);
        textView2.setOnClickListener(r9);
        linearLayout.addView(textView2);
        checkBoxCell2.setVisibility(8);
        textView2.setVisibility(8);
        if (!logFile.exists()) {
            zArr[0] = false;
        }
        final Runnable runnable2 = runnable;
        AlertDialog create = new AlertDialog.Builder(context2).setTitle(LocaleController.getString("CallMessageReportProblem", NUM)).setView(linearLayout).setPositiveButton(LocaleController.getString("Send", NUM), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        }).setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null).setOnDismissListener(new DialogInterface.OnDismissListener() {
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
        betterRatingView.setOnRatingChangeListener(new BetterRatingView.OnRatingChangeListener() {
            public void onRatingChanged(int i) {
                int i2;
                String str;
                button.setEnabled(i > 0);
                TextView textView = (TextView) button;
                if (i < 4) {
                    i2 = NUM;
                    str = "Next";
                } else {
                    i2 = NUM;
                    str = "Send";
                }
                textView.setText(LocaleController.getString(str, i2).toUpperCase());
            }
        });
        final BetterRatingView betterRatingView2 = betterRatingView;
        final LinearLayout linearLayout3 = linearLayout2;
        final EditTextBoldCursor editTextBoldCursor2 = editTextBoldCursor;
        final boolean[] zArr2 = zArr;
        final long j3 = j2;
        AnonymousClass7 r26 = r0;
        final long j4 = j;
        AlertDialog alertDialog = create;
        final boolean z2 = z;
        final TextView textView3 = textView2;
        final int i4 = i;
        final Context context3 = context;
        CheckBoxCell checkBoxCell3 = checkBoxCell2;
        final AlertDialog alertDialog2 = alertDialog;
        final CheckBoxCell checkBoxCell4 = checkBoxCell3;
        AnonymousClass7 r0 = new View.OnClickListener() {
            public void onClick(View view) {
                if (betterRatingView2.getRating() < 4) {
                    int[] iArr = iArr;
                    if (iArr[0] != 1) {
                        iArr[0] = 1;
                        betterRatingView2.setVisibility(8);
                        textView.setVisibility(8);
                        alertDialog2.setTitle(LocaleController.getString("CallReportHint", NUM));
                        editTextBoldCursor2.setVisibility(0);
                        if (logFile.exists()) {
                            checkBoxCell4.setVisibility(0);
                            textView3.setVisibility(0);
                        }
                        linearLayout3.setVisibility(0);
                        ((TextView) view).setText(LocaleController.getString("Send", NUM).toUpperCase());
                        return;
                    }
                }
                final int i = UserConfig.selectedAccount;
                final TLRPC.TL_phone_setCallRating tL_phone_setCallRating = new TLRPC.TL_phone_setCallRating();
                tL_phone_setCallRating.rating = betterRatingView2.getRating();
                final ArrayList arrayList = new ArrayList();
                for (int i2 = 0; i2 < linearLayout3.getChildCount(); i2++) {
                    CheckBoxCell checkBoxCell = (CheckBoxCell) linearLayout3.getChildAt(i2);
                    if (checkBoxCell.isChecked()) {
                        arrayList.add("#" + checkBoxCell.getTag());
                    }
                }
                if (tL_phone_setCallRating.rating < 5) {
                    tL_phone_setCallRating.comment = editTextBoldCursor2.getText().toString();
                } else {
                    tL_phone_setCallRating.comment = "";
                }
                if (!arrayList.isEmpty() && !zArr2[0]) {
                    tL_phone_setCallRating.comment += " " + TextUtils.join(" ", arrayList);
                }
                tL_phone_setCallRating.peer = new TLRPC.TL_inputPhoneCall();
                TLRPC.TL_inputPhoneCall tL_inputPhoneCall = tL_phone_setCallRating.peer;
                tL_inputPhoneCall.access_hash = j3;
                tL_inputPhoneCall.id = j4;
                tL_phone_setCallRating.user_initiative = z2;
                ConnectionsManager.getInstance(i4).sendRequest(tL_phone_setCallRating, new RequestDelegate() {
                    public void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                        if (tLObject instanceof TLRPC.TL_updates) {
                            MessagesController.getInstance(i).processUpdates((TLRPC.TL_updates) tLObject, false);
                        }
                        AnonymousClass7 r14 = AnonymousClass7.this;
                        if (zArr2[0] && logFile.exists() && tL_phone_setCallRating.rating < 4) {
                            SendMessagesHelper.prepareSendingDocument(AccountInstance.getInstance(UserConfig.selectedAccount), logFile.getAbsolutePath(), logFile.getAbsolutePath(), (Uri) null, TextUtils.join(" ", arrayList), "text/plain", 4244000, (MessageObject) null, (InputContentInfoCompat) null, (MessageObject) null, true, 0);
                            Toast.makeText(context3, LocaleController.getString("CallReportSent", NUM), 1).show();
                        }
                    }
                });
                alertDialog2.dismiss();
            }
        };
        button.setOnClickListener(r26);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:2:0x0004, code lost:
        r0 = new java.io.File(org.telegram.messenger.ApplicationLoader.applicationContext.getExternalFilesDir((java.lang.String) null), "logs");
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static java.io.File getLogFile(long r7) {
        /*
            boolean r0 = org.telegram.messenger.BuildVars.DEBUG_VERSION
            if (r0 == 0) goto L_0x0044
            java.io.File r0 = new java.io.File
            android.content.Context r1 = org.telegram.messenger.ApplicationLoader.applicationContext
            r2 = 0
            java.io.File r1 = r1.getExternalFilesDir(r2)
            java.lang.String r2 = "logs"
            r0.<init>(r1, r2)
            java.lang.String[] r1 = r0.list()
            if (r1 == 0) goto L_0x0044
            int r2 = r1.length
            r3 = 0
        L_0x001a:
            if (r3 >= r2) goto L_0x0044
            r4 = r1[r3]
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            java.lang.String r6 = "voip"
            r5.append(r6)
            r5.append(r7)
            java.lang.String r6 = ".txt"
            r5.append(r6)
            java.lang.String r5 = r5.toString()
            boolean r5 = r4.endsWith(r5)
            if (r5 == 0) goto L_0x0041
            java.io.File r7 = new java.io.File
            r7.<init>(r0, r4)
            return r7
        L_0x0041:
            int r3 = r3 + 1
            goto L_0x001a
        L_0x0044:
            java.io.File r0 = new java.io.File
            java.io.File r1 = getLogsDir()
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            r2.append(r7)
            java.lang.String r7 = ".log"
            r2.append(r7)
            java.lang.String r7 = r2.toString()
            r0.<init>(r1, r7)
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.voip.VoIPHelper.getLogFile(long):java.io.File");
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
        textCheckCell.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                boolean z = globalMainSettings.getBoolean("dbg_force_tcp_in_calls", false);
                SharedPreferences.Editor edit = globalMainSettings.edit();
                edit.putBoolean("dbg_force_tcp_in_calls", !z);
                edit.commit();
                textCheckCell.setChecked(!z);
            }
        });
        linearLayout.addView(textCheckCell);
        if (BuildVars.DEBUG_VERSION && BuildVars.LOGS_ENABLED) {
            final TextCheckCell textCheckCell2 = new TextCheckCell(context);
            textCheckCell2.setTextAndCheck("Dump detailed stats", globalMainSettings.getBoolean("dbg_dump_call_stats", false), false);
            textCheckCell2.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    boolean z = globalMainSettings.getBoolean("dbg_dump_call_stats", false);
                    SharedPreferences.Editor edit = globalMainSettings.edit();
                    edit.putBoolean("dbg_dump_call_stats", !z);
                    edit.commit();
                    textCheckCell2.setChecked(!z);
                }
            });
            linearLayout.addView(textCheckCell2);
        }
        if (Build.VERSION.SDK_INT >= 26) {
            final TextCheckCell textCheckCell3 = new TextCheckCell(context);
            textCheckCell3.setTextAndCheck("Enable ConnectionService", globalMainSettings.getBoolean("dbg_force_connection_service", false), false);
            textCheckCell3.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    boolean z = globalMainSettings.getBoolean("dbg_force_connection_service", false);
                    SharedPreferences.Editor edit = globalMainSettings.edit();
                    edit.putBoolean("dbg_force_connection_service", !z);
                    edit.commit();
                    textCheckCell3.setChecked(!z);
                }
            });
            linearLayout.addView(textCheckCell3);
        }
        new AlertDialog.Builder(context).setTitle(LocaleController.getString("DebugMenuCallSettings", NUM)).setView(linearLayout).show();
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
            FileLog.w("Invalid call data saving preset configuration: " + z + "/" + z2 + "/" + z3);
        }
        return 0;
    }
}
