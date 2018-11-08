package org.telegram.messenger;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AppStartReceiver extends BroadcastReceiver {

    /* renamed from: org.telegram.messenger.AppStartReceiver$1 */
    class C02041 implements Runnable {
        C02041() {
        }

        public void run() {
            ApplicationLoader.startPushService();
        }
    }

    public void onReceive(Context context, Intent intent) {
        AndroidUtilities.runOnUIThread(new C02041());
    }
}
