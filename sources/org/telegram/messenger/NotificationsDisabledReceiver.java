package org.telegram.messenger;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.SystemClock;
import android.text.TextUtils;
@TargetApi(28)
/* loaded from: classes.dex */
public class NotificationsDisabledReceiver extends BroadcastReceiver {
    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {
        if (!"android.app.action.NOTIFICATION_CHANNEL_BLOCK_STATE_CHANGED".equals(intent.getAction())) {
            return;
        }
        String stringExtra = intent.getStringExtra("android.app.extra.NOTIFICATION_CHANNEL_ID");
        int i = 0;
        boolean booleanExtra = intent.getBooleanExtra("android.app.extra.BLOCKED_STATE", false);
        if (TextUtils.isEmpty(stringExtra) || stringExtra.contains("_ia_")) {
            return;
        }
        String[] split = stringExtra.split("_");
        if (split.length < 3) {
            return;
        }
        ApplicationLoader.postInitApplication();
        int intValue = Utilities.parseInt((CharSequence) split[0]).intValue();
        if (intValue < 0 || intValue >= 4) {
            return;
        }
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("received disabled notification channel event for " + stringExtra + " state = " + booleanExtra);
        }
        if (SystemClock.elapsedRealtime() - AccountInstance.getInstance(intValue).getNotificationsController().lastNotificationChannelCreateTime <= 1000) {
            if (!BuildVars.LOGS_ENABLED) {
                return;
            }
            FileLog.d("received disable notification event right after creating notification channel, ignoring");
            return;
        }
        SharedPreferences notificationsSettings = AccountInstance.getInstance(intValue).getNotificationsSettings();
        int i2 = Integer.MAX_VALUE;
        if (split[1].startsWith("channel")) {
            if (!stringExtra.equals(notificationsSettings.getString("channels", null))) {
                return;
            }
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("apply channel " + stringExtra + " state");
            }
            SharedPreferences.Editor edit = notificationsSettings.edit();
            String globalNotificationsKey = NotificationsController.getGlobalNotificationsKey(2);
            if (booleanExtra) {
                i = Integer.MAX_VALUE;
            }
            edit.putInt(globalNotificationsKey, i).commit();
            AccountInstance.getInstance(intValue).getNotificationsController().updateServerNotificationsSettings(2);
        } else if (split[1].startsWith("groups")) {
            if (!stringExtra.equals(notificationsSettings.getString("groups", null))) {
                return;
            }
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("apply channel " + stringExtra + " state");
            }
            SharedPreferences.Editor edit2 = notificationsSettings.edit();
            String globalNotificationsKey2 = NotificationsController.getGlobalNotificationsKey(0);
            if (!booleanExtra) {
                i2 = 0;
            }
            edit2.putInt(globalNotificationsKey2, i2).commit();
            AccountInstance.getInstance(intValue).getNotificationsController().updateServerNotificationsSettings(0);
        } else if (split[1].startsWith("private")) {
            if (!stringExtra.equals(notificationsSettings.getString("private", null))) {
                return;
            }
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("apply channel " + stringExtra + " state");
            }
            SharedPreferences.Editor edit3 = notificationsSettings.edit();
            String globalNotificationsKey3 = NotificationsController.getGlobalNotificationsKey(1);
            if (booleanExtra) {
                i = Integer.MAX_VALUE;
            }
            edit3.putInt(globalNotificationsKey3, i).commit();
            AccountInstance.getInstance(intValue).getNotificationsController().updateServerNotificationsSettings(1);
        } else {
            long longValue = Utilities.parseLong(split[1]).longValue();
            if (longValue == 0) {
                return;
            }
            if (!stringExtra.equals(notificationsSettings.getString("org.telegram.key" + longValue, null))) {
                return;
            }
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("apply channel " + stringExtra + " state");
            }
            SharedPreferences.Editor edit4 = notificationsSettings.edit();
            String str = "notify2_" + longValue;
            if (booleanExtra) {
                i = 2;
            }
            edit4.putInt(str, i);
            if (!booleanExtra) {
                edit4.remove("notifyuntil_" + longValue);
            }
            edit4.commit();
            AccountInstance.getInstance(intValue).getNotificationsController().updateServerNotificationsSettings(longValue, true);
        }
        AccountInstance.getInstance(intValue).getConnectionsManager().resumeNetworkMaybe();
    }
}
