package org.telegram.ui.Components.voip;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
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
import org.telegram.messenger.C0446R;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.SendMessagesHelper;
import org.telegram.messenger.UserConfig;
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
    public static long lastCallTime;

    public static void startCall(User user, final Activity activity, TL_userFull tL_userFull) {
        int i = 1;
        if (tL_userFull != null && tL_userFull.phone_calls_private != null) {
            new Builder((Context) activity).setTitle(LocaleController.getString("VoipFailed", C0446R.string.VoipFailed)).setMessage(AndroidUtilities.replaceTags(LocaleController.formatString("CallNotAvailable", C0446R.string.CallNotAvailable, ContactsController.formatName(user.first_name, user.last_name)))).setPositiveButton(LocaleController.getString("OK", C0446R.string.OK), null).show();
        } else if (ConnectionsManager.getInstance(UserConfig.selectedAccount).getConnectionState() != 3) {
            int i2;
            if (System.getInt(activity.getContentResolver(), "airplane_mode_on", 0) == null) {
                i = 0;
            }
            user = new Builder((Context) activity);
            if (i != 0) {
                tL_userFull = "VoipOfflineAirplaneTitle";
                i2 = C0446R.string.VoipOfflineAirplaneTitle;
            } else {
                tL_userFull = "VoipOfflineTitle";
                i2 = C0446R.string.VoipOfflineTitle;
            }
            user = user.setTitle(LocaleController.getString(tL_userFull, i2));
            if (i != 0) {
                tL_userFull = "VoipOfflineAirplane";
                i2 = C0446R.string.VoipOfflineAirplane;
            } else {
                tL_userFull = "VoipOffline";
                i2 = C0446R.string.VoipOffline;
            }
            user = user.setMessage(LocaleController.getString(tL_userFull, i2)).setPositiveButton(LocaleController.getString("OK", C0446R.string.OK), null);
            if (i != 0) {
                tL_userFull = new Intent("android.settings.AIRPLANE_MODE_SETTINGS");
                if (tL_userFull.resolveActivity(activity.getPackageManager()) != null) {
                    user.setNeutralButton(LocaleController.getString("VoipOfflineOpenSettings", C0446R.string.VoipOfflineOpenSettings), new OnClickListener() {
                        public void onClick(DialogInterface dialogInterface, int i) {
                            activity.startActivity(tL_userFull);
                        }
                    });
                }
            }
            user.show();
        } else {
            if (VERSION.SDK_INT < 23 || activity.checkSelfPermission("android.permission.RECORD_AUDIO") == null) {
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
                        new Builder((Context) activity).setTitle(LocaleController.getString("VoipOngoingAlertTitle", C0446R.string.VoipOngoingAlertTitle)).setMessage(AndroidUtilities.replaceTags(LocaleController.formatString("VoipOngoingAlert", C0446R.string.VoipOngoingAlert, ContactsController.formatName(r0.first_name, r0.last_name), ContactsController.formatName(user.first_name, user.last_name)))).setPositiveButton(LocaleController.getString("OK", C0446R.string.OK), new OnClickListener() {

                            /* renamed from: org.telegram.ui.Components.voip.VoIPHelper$2$1 */
                            class C13531 implements Runnable {
                                C13531() {
                                }

                                public void run() {
                                    VoIPHelper.doInitiateCall(user, activity);
                                }
                            }

                            public void onClick(DialogInterface dialogInterface, int i) {
                                if (VoIPService.getSharedInstance() != null) {
                                    VoIPService.getSharedInstance().hangUp(new C13531());
                                } else {
                                    VoIPHelper.doInitiateCall(user, activity);
                                }
                            }
                        }).setNegativeButton(LocaleController.getString("Cancel", C0446R.string.Cancel), null).show();
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
                    } catch (Throwable th) {
                        FileLog.m3e(th);
                    }
                }
            }
        }
    }

    @TargetApi(23)
    public static void permissionDenied(final Activity activity, final Runnable runnable) {
        if (!activity.shouldShowRequestPermissionRationale("android.permission.RECORD_AUDIO")) {
            new Builder((Context) activity).setTitle(LocaleController.getString("AppName", C0446R.string.AppName)).setMessage(LocaleController.getString("VoipNeedMicPermission", C0446R.string.VoipNeedMicPermission)).setPositiveButton(LocaleController.getString("OK", C0446R.string.OK), null).setNegativeButton(LocaleController.getString("Settings", C0446R.string.Settings), new OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
                    dialogInterface.setData(Uri.fromParts("package", activity.getPackageName(), null));
                    activity.startActivity(dialogInterface);
                }
            }).show().setOnDismissListener(new OnDismissListener() {
                public void onDismiss(DialogInterface dialogInterface) {
                    if (runnable != null) {
                        runnable.run();
                    }
                }
            });
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
        if (!((tL_messageActionPhoneCall.reason instanceof TL_phoneCallDiscardReasonBusy) || (tL_messageActionPhoneCall.reason instanceof TL_phoneCallDiscardReasonMissed))) {
            for (String split : MessagesController.getNotificationsSettings(UserConfig.selectedAccount).getStringSet("calls_access_hashes", Collections.EMPTY_SET)) {
                String split2;
                String[] split3 = split2.split(" ");
                if (split3.length >= 2) {
                    split2 = split3[0];
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append(tL_messageActionPhoneCall.call_id);
                    stringBuilder.append(TtmlNode.ANONYMOUS_REGION_ID);
                    if (split2.equals(stringBuilder.toString())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static void showRateAlert(android.content.Context r8, org.telegram.tgnet.TLRPC.TL_messageActionPhoneCall r9) {
        /* JADX: method processing error */
/*
Error: java.lang.NullPointerException
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.searchTryCatchDominators(ProcessTryCatchRegions.java:75)
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.process(ProcessTryCatchRegions.java:45)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.postProcessRegions(RegionMakerVisitor.java:63)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.visit(RegionMakerVisitor.java:58)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:60)
	at jadx.core.ProcessClass.process(ProcessClass.java:39)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
*/
        /*
        r0 = org.telegram.messenger.UserConfig.selectedAccount;
        r0 = org.telegram.messenger.MessagesController.getNotificationsSettings(r0);
        r1 = "calls_access_hashes";
        r2 = java.util.Collections.EMPTY_SET;
        r0 = r0.getStringSet(r1, r2);
        r0 = r0.iterator();
    L_0x0012:
        r1 = r0.hasNext();
        if (r1 == 0) goto L_0x0056;
    L_0x0018:
        r1 = r0.next();
        r1 = (java.lang.String) r1;
        r2 = " ";
        r1 = r1.split(r2);
        r2 = r1.length;
        r3 = 2;
        if (r2 >= r3) goto L_0x0029;
    L_0x0028:
        goto L_0x0012;
    L_0x0029:
        r2 = 0;
        r2 = r1[r2];
        r3 = new java.lang.StringBuilder;
        r3.<init>();
        r4 = r9.call_id;
        r3.append(r4);
        r4 = "";
        r3.append(r4);
        r3 = r3.toString();
        r2 = r2.equals(r3);
        if (r2 == 0) goto L_0x0012;
    L_0x0045:
        r0 = 1;
        r0 = r1[r0];	 Catch:{ Exception -> 0x0055 }
        r5 = java.lang.Long.parseLong(r0);	 Catch:{ Exception -> 0x0055 }
        r2 = 0;	 Catch:{ Exception -> 0x0055 }
        r3 = r9.call_id;	 Catch:{ Exception -> 0x0055 }
        r7 = org.telegram.messenger.UserConfig.selectedAccount;	 Catch:{ Exception -> 0x0055 }
        r1 = r8;	 Catch:{ Exception -> 0x0055 }
        showRateAlert(r1, r2, r3, r5, r7);	 Catch:{ Exception -> 0x0055 }
    L_0x0055:
        return;
    L_0x0056:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.voip.VoIPHelper.showRateAlert(android.content.Context, org.telegram.tgnet.TLRPC$TL_messageActionPhoneCall):void");
    }

    public static void showRateAlert(Context context, Runnable runnable, long j, long j2, int i) {
        Context context2 = context;
        File logsDir = getLogsDir();
        StringBuilder stringBuilder = new StringBuilder();
        final long j3 = j;
        stringBuilder.append(j3);
        stringBuilder.append(".log");
        File file = new File(logsDir, stringBuilder.toString());
        View linearLayout = new LinearLayout(context2);
        linearLayout.setOrientation(1);
        int dp = AndroidUtilities.dp(16.0f);
        linearLayout.setPadding(dp, dp, dp, 0);
        View textView = new TextView(context2);
        textView.setTextSize(2, 16.0f);
        textView.setTextColor(Theme.getColor(Theme.key_dialogTextBlack));
        textView.setGravity(17);
        textView.setText(LocaleController.getString("VoipRateCallAlert", C0446R.string.VoipRateCallAlert));
        linearLayout.addView(textView);
        View betterRatingView = new BetterRatingView(context2);
        linearLayout.addView(betterRatingView, LayoutHelper.createLinear(-2, -2, 1, 0, 16, 0, 0));
        View editText = new EditText(context2);
        editText.setHint(LocaleController.getString("CallReportHint", C0446R.string.CallReportHint));
        editText.setInputType(147457);
        editText.setTextColor(Theme.getColor(Theme.key_dialogTextBlack));
        editText.setHintTextColor(Theme.getColor(Theme.key_dialogTextHint));
        editText.setBackgroundDrawable(Theme.createEditTextDrawable(context2, true));
        editText.setPadding(0, AndroidUtilities.dp(4.0f), 0, AndroidUtilities.dp(4.0f));
        editText.setTextSize(18.0f);
        editText.setVisibility(8);
        linearLayout.addView(editText, LayoutHelper.createLinear(-1, -2, 8.0f, 8.0f, 8.0f, 0.0f));
        final boolean[] zArr = new boolean[]{true};
        final View checkBoxCell = new CheckBoxCell(context2, 1);
        View.OnClickListener c13575 = new View.OnClickListener() {
            public void onClick(View view) {
                zArr[0] = zArr[0] ^ true;
                checkBoxCell.setChecked(zArr[0], true);
            }
        };
        checkBoxCell.setText(LocaleController.getString("CallReportIncludeLogs", C0446R.string.CallReportIncludeLogs), null, true, false);
        checkBoxCell.setClipToPadding(false);
        checkBoxCell.setOnClickListener(c13575);
        linearLayout.addView(checkBoxCell, LayoutHelper.createLinear(-1, -2, -8.0f, 0.0f, -8.0f, 0.0f));
        View textView2 = new TextView(context2);
        textView2.setTextSize(2, 14.0f);
        textView2.setTextColor(Theme.getColor(Theme.key_dialogTextGray3));
        textView2.setText(LocaleController.getString("CallReportLogsExplain", C0446R.string.CallReportLogsExplain));
        textView2.setPadding(AndroidUtilities.dp(8.0f), 0, AndroidUtilities.dp(8.0f), 0);
        textView2.setOnClickListener(c13575);
        linearLayout.addView(textView2);
        checkBoxCell.setVisibility(8);
        textView2.setVisibility(8);
        if (!file.exists()) {
            zArr[0] = false;
        }
        Builder view = new Builder(context2).setTitle(LocaleController.getString("CallMessageReportProblem", C0446R.string.CallMessageReportProblem)).setView(linearLayout);
        final View view2 = betterRatingView;
        C13597 c13597 = r0;
        final View view3 = editText;
        View view4 = betterRatingView;
        View view5 = editText;
        Builder builder = view;
        String string = LocaleController.getString("Send", C0446R.string.Send);
        final long j4 = j2;
        View view6 = textView2;
        final int i2 = i;
        final File file2 = file;
        View view7 = checkBoxCell;
        final Context context3 = context2;
        C13597 c135972 = new OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface = UserConfig.selectedAccount;
                i = new TL_phone_setCallRating();
                i.rating = view2.getRating();
                if (i.rating < 5) {
                    i.comment = view3.getText().toString();
                } else {
                    i.comment = TtmlNode.ANONYMOUS_REGION_ID;
                }
                i.peer = new TL_inputPhoneCall();
                i.peer.access_hash = j4;
                i.peer.id = j3;
                ConnectionsManager.getInstance(i2).sendRequest(i, new RequestDelegate() {
                    public void run(TLObject tLObject, TL_error tL_error) {
                        if ((tLObject instanceof TL_updates) != null) {
                            MessagesController.getInstance(dialogInterface).processUpdates((TL_updates) tLObject, false);
                            if (zArr[0] != null && file2.exists() != null && i.rating < 4) {
                                SendMessagesHelper.prepareSendingDocument(file2.getAbsolutePath(), file2.getAbsolutePath(), null, "text/plain", 4244000, null, null);
                                Toast.makeText(context3, LocaleController.getString("CallReportSent", C0446R.string.CallReportSent), 1).show();
                            }
                        }
                    }
                });
            }
        };
        final Runnable runnable2 = runnable;
        view2 = builder.setPositiveButton(string, c13597).setNegativeButton(LocaleController.getString("Cancel", C0446R.string.Cancel), null).setOnDismissListener(new OnDismissListener() {
            public void onDismiss(DialogInterface dialogInterface) {
                if (runnable2 != null) {
                    runnable2.run();
                }
            }
        }).show().getButton(-1);
        view2.setEnabled(false);
        view3 = view5;
        final Context context4 = context2;
        final File file3 = file;
        final View view8 = view7;
        final View view9 = view6;
        view4.setOnRatingChangeListener(new OnRatingChangeListener() {
            public void onRatingChanged(int i) {
                String str;
                int i2;
                int i3 = 0;
                view2.setEnabled(i > 0);
                EditText editText = view3;
                if (i < 4) {
                    str = "CallReportHint";
                    i2 = C0446R.string.CallReportHint;
                } else {
                    str = "VoipFeedbackCommentHint";
                    i2 = C0446R.string.VoipFeedbackCommentHint;
                }
                editText.setHint(LocaleController.getString(str, i2));
                editText = view3;
                int i4 = (i >= 5 || i <= 0) ? 8 : 0;
                editText.setVisibility(i4);
                if (view3.getVisibility() == 8) {
                    ((InputMethodManager) context4.getSystemService("input_method")).hideSoftInputFromWindow(view3.getWindowToken(), 0);
                }
                if (file3.exists()) {
                    view8.setVisibility(i < 4 ? 0 : 8);
                    TextView textView = view9;
                    if (i >= 4) {
                        i3 = 8;
                    }
                    textView.setVisibility(i3);
                }
            }
        });
    }

    public static void upgradeP2pSetting(int i) {
        i = MessagesController.getMainSettings(i);
        if (i.contains("calls_p2p")) {
            Editor edit = i.edit();
            if (i.getBoolean("calls_p2p", true) == 0) {
                edit.putInt("calls_p2p_new", 2);
            }
            edit.remove("calls_p2p").commit();
        }
    }
}
