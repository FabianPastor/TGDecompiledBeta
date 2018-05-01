package org.telegram.messenger;

import android.app.IntentService;
import android.content.Intent;

public class NotificationRepeat extends IntentService {
    public NotificationRepeat() {
        super("NotificationRepeat");
    }

    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            intent = intent.getIntExtra("currentAccount", UserConfig.selectedAccount);
            AndroidUtilities.runOnUIThread(new Runnable() {
                public void run() {
                    NotificationsController.getInstance(intent).repeatNotificationMaybe();
                }
            });
        }
    }
}
