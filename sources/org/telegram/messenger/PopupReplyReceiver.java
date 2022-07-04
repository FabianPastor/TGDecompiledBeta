package org.telegram.messenger;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class PopupReplyReceiver extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
        if (intent != null) {
            ApplicationLoader.postInitApplication();
            int currentAccount = intent.getIntExtra("currentAccount", UserConfig.selectedAccount);
            if (UserConfig.isValidAccount(currentAccount)) {
                NotificationsController.getInstance(currentAccount).forceShowPopupForReply();
            }
        }
    }
}
