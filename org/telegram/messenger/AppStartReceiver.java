package org.telegram.messenger;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AppStartReceiver extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            public void run() {
                ApplicationLoader.startPushService();
            }
        });
    }
}
