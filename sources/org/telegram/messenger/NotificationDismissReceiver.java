package org.telegram.messenger;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class NotificationDismissReceiver extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
        if (intent != null) {
            MessagesController.getNotificationsSettings(intent.getIntExtra("currentAccount", UserConfig.selectedAccount)).edit().putInt("dismissDate", intent.getIntExtra("messageDate", 0)).commit();
        }
    }
}
