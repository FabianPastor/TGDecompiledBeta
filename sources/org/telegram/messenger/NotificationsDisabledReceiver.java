package org.telegram.messenger;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.SystemClock;
import android.text.TextUtils;

public class NotificationsDisabledReceiver extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
        Intent intent2 = intent;
        if ("android.app.action.NOTIFICATION_CHANNEL_BLOCK_STATE_CHANGED".equals(intent.getAction())) {
            String channelId = intent2.getStringExtra("android.app.extra.NOTIFICATION_CHANNEL_ID");
            int i = 0;
            boolean state = intent2.getBooleanExtra("android.app.extra.BLOCKED_STATE", false);
            if (!TextUtils.isEmpty(channelId) && !channelId.contains("_ia_")) {
                String[] args = channelId.split("_");
                if (args.length >= 3) {
                    ApplicationLoader.postInitApplication();
                    int account = Utilities.parseInt((CharSequence) args[0]).intValue();
                    if (account >= 0 && account < 4) {
                        if (BuildVars.LOGS_ENABLED) {
                            FileLog.d("received disabled notification channel event for " + channelId + " state = " + state);
                        }
                        if (SystemClock.elapsedRealtime() - AccountInstance.getInstance(account).getNotificationsController().lastNotificationChannelCreateTime > 1000) {
                            SharedPreferences preferences = AccountInstance.getInstance(account).getNotificationsSettings();
                            int i2 = Integer.MAX_VALUE;
                            if (args[1].startsWith("channel")) {
                                if (channelId.equals(preferences.getString("channels", (String) null))) {
                                    if (BuildVars.LOGS_ENABLED) {
                                        FileLog.d("apply channel " + channelId + " state");
                                    }
                                    SharedPreferences.Editor edit = preferences.edit();
                                    String globalNotificationsKey = NotificationsController.getGlobalNotificationsKey(2);
                                    if (state) {
                                        i = Integer.MAX_VALUE;
                                    }
                                    edit.putInt(globalNotificationsKey, i).commit();
                                    AccountInstance.getInstance(account).getNotificationsController().updateServerNotificationsSettings(2);
                                } else {
                                    return;
                                }
                            } else if (args[1].startsWith("groups")) {
                                if (channelId.equals(preferences.getString("groups", (String) null))) {
                                    if (BuildVars.LOGS_ENABLED) {
                                        FileLog.d("apply channel " + channelId + " state");
                                    }
                                    SharedPreferences.Editor edit2 = preferences.edit();
                                    String globalNotificationsKey2 = NotificationsController.getGlobalNotificationsKey(0);
                                    if (!state) {
                                        i2 = 0;
                                    }
                                    edit2.putInt(globalNotificationsKey2, i2).commit();
                                    AccountInstance.getInstance(account).getNotificationsController().updateServerNotificationsSettings(0);
                                } else {
                                    return;
                                }
                            } else if (!args[1].startsWith("private")) {
                                long dialogId = Utilities.parseLong(args[1]).longValue();
                                if (dialogId != 0) {
                                    if (channelId.equals(preferences.getString("org.telegram.key" + dialogId, (String) null))) {
                                        if (BuildVars.LOGS_ENABLED) {
                                            FileLog.d("apply channel " + channelId + " state");
                                        }
                                        SharedPreferences.Editor editor = preferences.edit();
                                        String str = "notify2_" + dialogId;
                                        if (state) {
                                            i = 2;
                                        }
                                        editor.putInt(str, i);
                                        if (!state) {
                                            editor.remove("notifyuntil_" + dialogId);
                                        }
                                        editor.commit();
                                        AccountInstance.getInstance(account).getNotificationsController().updateServerNotificationsSettings(dialogId, true);
                                    } else {
                                        return;
                                    }
                                } else {
                                    return;
                                }
                            } else if (channelId.equals(preferences.getString("private", (String) null))) {
                                if (BuildVars.LOGS_ENABLED) {
                                    FileLog.d("apply channel " + channelId + " state");
                                }
                                SharedPreferences.Editor edit3 = preferences.edit();
                                String globalNotificationsKey3 = NotificationsController.getGlobalNotificationsKey(1);
                                if (state) {
                                    i = Integer.MAX_VALUE;
                                }
                                edit3.putInt(globalNotificationsKey3, i).commit();
                                AccountInstance.getInstance(account).getNotificationsController().updateServerNotificationsSettings(1);
                            } else {
                                return;
                            }
                            AccountInstance.getInstance(account).getConnectionsManager().resumeNetworkMaybe();
                        } else if (BuildVars.LOGS_ENABLED) {
                            FileLog.d("received disable notification event right after creating notification channel, ignoring");
                        }
                    }
                }
            }
        }
    }
}
