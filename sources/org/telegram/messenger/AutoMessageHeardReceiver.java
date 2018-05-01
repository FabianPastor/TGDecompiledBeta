package org.telegram.messenger;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AutoMessageHeardReceiver extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
        ApplicationLoader.postInitApplication();
        long longExtra = intent.getLongExtra("dialog_id", 0);
        int intExtra = intent.getIntExtra("max_id", 0);
        context = intent.getIntExtra("currentAccount", 0);
        if (longExtra != 0) {
            if (intExtra != 0) {
                MessagesController.getInstance(context).markDialogAsRead(longExtra, intExtra, intExtra, 0, false, 0, true);
            }
        }
    }
}
