package org.telegram.messenger;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

public class NotificationDismissReceiver extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
        if (intent != null) {
            int intExtra = intent.getIntExtra("currentAccount", UserConfig.selectedAccount);
            long longExtra = intent.getLongExtra("dialogId", 0);
            int intExtra2 = intent.getIntExtra("messageDate", 0);
            if (longExtra == 0) {
                MessagesController.getNotificationsSettings(intExtra).edit().putInt("dismissDate", intExtra2).commit();
                return;
            }
            SharedPreferences.Editor edit = MessagesController.getNotificationsSettings(intExtra).edit();
            edit.putInt("dismissDate" + longExtra, intExtra2).commit();
        }
    }
}
