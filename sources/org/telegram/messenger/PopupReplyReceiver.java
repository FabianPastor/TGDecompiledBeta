package org.telegram.messenger;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class PopupReplyReceiver extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
        if (intent != null) {
            ApplicationLoader.postInitApplication();
            int intExtra = intent.getIntExtra("currentAccount", UserConfig.selectedAccount);
            if (UserConfig.isValidAccount(intExtra)) {
                NotificationsController.getInstance(intExtra).forceShowPopupForReply();
            }
        }
    }
}
