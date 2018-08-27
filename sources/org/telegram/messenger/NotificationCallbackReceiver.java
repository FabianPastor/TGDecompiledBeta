package org.telegram.messenger;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.google.android.exoplayer2.upstream.DataSchemeDataSource;

public class NotificationCallbackReceiver extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
        if (intent != null) {
            ApplicationLoader.postInitApplication();
            int currentAccount = intent.getIntExtra("currentAccount", UserConfig.selectedAccount);
            long did = intent.getLongExtra("did", 777000);
            byte[] data = intent.getByteArrayExtra(DataSchemeDataSource.SCHEME_DATA);
            SendMessagesHelper.getInstance(currentAccount).sendNotificationCallback(did, intent.getIntExtra("mid", 0), data);
        }
    }
}
