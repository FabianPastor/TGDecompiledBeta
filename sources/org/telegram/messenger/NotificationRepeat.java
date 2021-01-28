package org.telegram.messenger;

import android.app.IntentService;
import android.content.Intent;

public class NotificationRepeat extends IntentService {
    public NotificationRepeat() {
        super("NotificationRepeat");
    }

    /* access modifiers changed from: protected */
    public void onHandleIntent(Intent intent) {
        if (intent != null) {
            final int intExtra = intent.getIntExtra("currentAccount", UserConfig.selectedAccount);
            AndroidUtilities.runOnUIThread(new Runnable() {
                public void run() {
                    NotificationsController.getInstance(intExtra).repeatNotificationMaybe();
                }
            });
        }
    }
}
