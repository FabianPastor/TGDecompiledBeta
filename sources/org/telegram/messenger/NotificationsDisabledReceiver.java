package org.telegram.messenger;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.TextUtils;

public class NotificationsDisabledReceiver extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
        String stringExtra = intent.getStringExtra("android.app.extra.NOTIFICATION_CHANNEL_ID");
        int i = 0;
        boolean booleanExtra = intent.getBooleanExtra("android.app.extra.BLOCKED_STATE", false);
        if (!TextUtils.isEmpty(stringExtra) && !stringExtra.contains("_ia_")) {
            String[] split = stringExtra.split("_");
            if (split.length >= 3) {
                ApplicationLoader.postInitApplication();
                int intValue = Utilities.parseInt(split[0]).intValue();
                if (intValue >= 0 && intValue < 3) {
                    SharedPreferences.Editor edit = AccountInstance.getInstance(intValue).getNotificationsSettings().edit();
                    int i2 = Integer.MAX_VALUE;
                    if (split[1].startsWith("channel")) {
                        String globalNotificationsKey = NotificationsController.getGlobalNotificationsKey(2);
                        if (booleanExtra) {
                            i = Integer.MAX_VALUE;
                        }
                        edit.putInt(globalNotificationsKey, i).commit();
                        AccountInstance.getInstance(intValue).getNotificationsController().updateServerNotificationsSettings(2);
                    } else if (split[1].startsWith("groups")) {
                        String globalNotificationsKey2 = NotificationsController.getGlobalNotificationsKey(0);
                        if (!booleanExtra) {
                            i2 = 0;
                        }
                        edit.putInt(globalNotificationsKey2, i2).commit();
                        AccountInstance.getInstance(intValue).getNotificationsController().updateServerNotificationsSettings(0);
                    } else if (split[1].startsWith("private")) {
                        String globalNotificationsKey3 = NotificationsController.getGlobalNotificationsKey(1);
                        if (booleanExtra) {
                            i = Integer.MAX_VALUE;
                        }
                        edit.putInt(globalNotificationsKey3, i).commit();
                        AccountInstance.getInstance(intValue).getNotificationsController().updateServerNotificationsSettings(1);
                    } else {
                        long longValue = Utilities.parseLong(split[1]).longValue();
                        if (longValue != 0) {
                            String str = "notify2_" + longValue;
                            if (booleanExtra) {
                                i = 2;
                            }
                            edit.putInt(str, i);
                            if (!booleanExtra) {
                                edit.remove("notifyuntil_" + longValue);
                            }
                            edit.commit();
                            AccountInstance.getInstance(intValue).getNotificationsController().updateServerNotificationsSettings(longValue, true);
                        } else {
                            return;
                        }
                    }
                    AccountInstance.getInstance(intValue).getConnectionsManager().resumeNetworkMaybe();
                }
            }
        }
    }
}
