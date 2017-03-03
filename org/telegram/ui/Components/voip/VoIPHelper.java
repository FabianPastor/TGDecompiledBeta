package org.telegram.ui.Components.voip;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.net.Uri;
import android.os.Build.VERSION;
import android.provider.Settings.System;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.beta.R;
import org.telegram.messenger.voip.VoIPService;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.ui.ActionBar.AlertDialog.Builder;
import org.telegram.ui.VoIPActivity;

public class VoIPHelper {
    public static void startCall(User user, final Activity activity) {
        boolean isAirplaneMode = true;
        if (ConnectionsManager.getInstance().getConnectionState() != 3) {
            CharSequence string;
            if (System.getInt(activity.getContentResolver(), "airplane_mode_on", 0) == 0) {
                isAirplaneMode = false;
            }
            Builder title = new Builder(activity).setTitle(isAirplaneMode ? LocaleController.getString("VoipOfflineAirplaneTitle", R.string.VoipOfflineAirplaneTitle) : LocaleController.getString("VoipOfflineTitle", R.string.VoipOfflineTitle));
            if (isAirplaneMode) {
                string = LocaleController.getString("VoipOfflineAirplane", R.string.VoipOfflineAirplane);
            } else {
                string = LocaleController.getString("VoipOffline", R.string.VoipOffline);
            }
            Builder bldr = title.setMessage(string).setPositiveButton(LocaleController.getString("OK", R.string.OK), null);
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
        } else if (VERSION.SDK_INT < 23 || activity.checkSelfPermission("android.permission.RECORD_AUDIO") == 0) {
            initiateCall(user, activity);
        } else {
            activity.requestPermissions(new String[]{"android.permission.RECORD_AUDIO"}, 101);
        }
    }

    private static void initiateCall(final User user, final Activity activity) {
        if (activity != null) {
            if (VoIPService.getSharedInstance() != null) {
                if (VoIPService.getSharedInstance().getUser().id != user.id) {
                    new Builder(activity).setTitle(LocaleController.getString("VoipOngoingAlertTitle", R.string.VoipOngoingAlertTitle)).setMessage(AndroidUtilities.replaceTags(LocaleController.formatString("VoipOngoingAlert", R.string.VoipOngoingAlert, ContactsController.formatName(callUser.first_name, callUser.last_name), ContactsController.formatName(user.first_name, user.last_name)))).setPositiveButton(LocaleController.getString("OK", R.string.OK), new OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            if (VoIPService.getSharedInstance() != null) {
                                VoIPService.getSharedInstance().hangUp(new Runnable() {
                                    public void run() {
                                        VoIPHelper.doInitiateCall(user, activity);
                                    }
                                });
                            } else {
                                VoIPHelper.doInitiateCall(user, activity);
                            }
                        }
                    }).setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null).show();
                    return;
                }
                activity.startActivity(new Intent(activity, VoIPActivity.class).addFlags(268435456));
                return;
            }
            doInitiateCall(user, activity);
        }
    }

    private static void doInitiateCall(User user, Activity activity) {
        if (activity != null && user != null) {
            Intent intent = new Intent(activity, VoIPService.class);
            intent.putExtra("user_id", user.id);
            intent.putExtra("is_outgoing", true);
            intent.putExtra("start_incall_activity", true);
            activity.startService(intent);
        }
    }

    @TargetApi(23)
    public static void permissionDenied(final Activity activity, final Runnable onFinish) {
        if (!activity.shouldShowRequestPermissionRationale("android.permission.RECORD_AUDIO")) {
            new Builder(activity).setTitle(LocaleController.getString("AppName", R.string.AppName)).setMessage(LocaleController.getString("VoipNeedMicPermission", R.string.VoipNeedMicPermission)).setPositiveButton(LocaleController.getString("OK", R.string.OK), null).setNegativeButton(LocaleController.getString("Settings", R.string.Settings), new OnClickListener() {
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
}
