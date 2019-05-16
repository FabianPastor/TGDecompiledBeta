package org.telegram.messenger;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class NotificationCallbackReceiver extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
        if (intent != null) {
            ApplicationLoader.postInitApplication();
            int intExtra = intent.getIntExtra("currentAccount", UserConfig.selectedAccount);
            long longExtra = intent.getLongExtra("did", 777000);
            byte[] byteArrayExtra = intent.getByteArrayExtra("data");
            SendMessagesHelper.getInstance(intExtra).sendNotificationCallback(longExtra, intent.getIntExtra("mid", 0), byteArrayExtra);
        }
    }
}
