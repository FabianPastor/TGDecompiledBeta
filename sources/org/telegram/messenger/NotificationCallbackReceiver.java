package org.telegram.messenger;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
/* loaded from: classes.dex */
public class NotificationCallbackReceiver extends BroadcastReceiver {
    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {
        if (intent == null) {
            return;
        }
        ApplicationLoader.postInitApplication();
        int intExtra = intent.getIntExtra("currentAccount", UserConfig.selectedAccount);
        if (!UserConfig.isValidAccount(intExtra)) {
            return;
        }
        long longExtra = intent.getLongExtra("did", 777000L);
        byte[] byteArrayExtra = intent.getByteArrayExtra("data");
        SendMessagesHelper.getInstance(intExtra).sendNotificationCallback(longExtra, intent.getIntExtra("mid", 0), byteArrayExtra);
    }
}
