package org.telegram.messenger;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences.Editor;

public class NotificationDismissReceiver extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
        if (intent != null) {
            int intExtra = intent.getIntExtra("currentAccount", UserConfig.selectedAccount);
            long longExtra = intent.getLongExtra("dialogId", 0);
            int intExtra2 = intent.getIntExtra("messageDate", 0);
            String str = "dismissDate";
            if (longExtra == 0) {
                MessagesController.getNotificationsSettings(intExtra).edit().putInt(str, intExtra2).commit();
            } else {
                Editor edit = MessagesController.getNotificationsSettings(intExtra).edit();
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(str);
                stringBuilder.append(longExtra);
                edit.putInt(stringBuilder.toString(), intExtra2).commit();
            }
        }
    }
}
