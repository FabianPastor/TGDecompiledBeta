package org.telegram.messenger;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AutoMessageHeardReceiver extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
        Intent intent2 = intent;
        ApplicationLoader.postInitApplication();
        long dialog_id = intent2.getLongExtra("dialog_id", 0);
        int max_id = intent2.getIntExtra("max_id", 0);
        int currentAccount = intent2.getIntExtra("currentAccount", 0);
        if (dialog_id != 0) {
            if (max_id != 0) {
                MessagesController.getInstance(currentAccount).markDialogAsRead(dialog_id, max_id, max_id, 0, false, 0, true);
            }
        }
    }
}
