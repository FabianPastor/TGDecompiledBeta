package org.telegram.messenger;

import android.app.IntentService;
import android.content.Intent;
import org.telegram.ui.LaunchActivity;

public class BringAppForegroundService extends IntentService {
    public BringAppForegroundService() {
        super("BringAppForegroundService");
    }

    /* Access modifiers changed, original: protected */
    public void onHandleIntent(Intent intent) {
        Intent intent2 = new Intent(this, LaunchActivity.class);
        intent2.setFlags(NUM);
        startActivity(intent2);
    }
}
