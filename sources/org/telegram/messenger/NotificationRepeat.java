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
            int intExtra = intent.getIntExtra("currentAccount", UserConfig.selectedAccount);
            if (UserConfig.isValidAccount(intExtra)) {
                AndroidUtilities.runOnUIThread(new Runnable(intExtra) {
                    public final /* synthetic */ int f$0;

                    {
                        this.f$0 = r1;
                    }

                    public final void run() {
                        NotificationsController.getInstance(this.f$0).repeatNotificationMaybe();
                    }
                });
            }
        }
    }
}
