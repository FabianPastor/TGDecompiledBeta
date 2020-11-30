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
        if (!TextUtils.isEmpty(stringExtra)) {
            String[] split = stringExtra.split("_");
            if (split.length >= 3) {
                ApplicationLoader.postInitApplication();
                int intValue = Utilities.parseInt(split[0]).intValue();
                if (intValue >= 0 && intValue < 3) {
                    SharedPreferences.Editor edit = AccountInstance.getInstance(intValue).getNotificationsSettings().edit();
                    if (split[1].startsWith("channel")) {
                        String globalNotificationsKey = NotificationsController.getGlobalNotificationsKey(2);
                        if (booleanExtra) {
                            i = Integer.MAX_VALUE;
                        }
                        edit.putInt(globalNotificationsKey, i);
                    } else if (split[1].startsWith("groups")) {
                        String globalNotificationsKey2 = NotificationsController.getGlobalNotificationsKey(0);
                        if (booleanExtra) {
                            i = Integer.MAX_VALUE;
                        }
                        edit.putInt(globalNotificationsKey2, i);
                    } else if (split[1].startsWith("private")) {
                        String globalNotificationsKey3 = NotificationsController.getGlobalNotificationsKey(1);
                        if (booleanExtra) {
                            i = Integer.MAX_VALUE;
                        }
                        edit.putInt(globalNotificationsKey3, i);
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
                        } else {
                            return;
                        }
                    }
                    edit.commit();
                }
            }
        }
    }
}
