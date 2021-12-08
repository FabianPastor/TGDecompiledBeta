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
            int currentAccount = intent.getIntExtra("currentAccount", UserConfig.selectedAccount);
            if (UserConfig.isValidAccount(currentAccount)) {
                AndroidUtilities.runOnUIThread(new NotificationRepeat$$ExternalSyntheticLambda0(currentAccount));
            }
        }
    }
}
