package org.telegram.messenger;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import org.telegram.messenger.exoplayer2.upstream.DataSchemeDataSource;

public class NotificationCallbackReceiver extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
        if (intent != null) {
            ApplicationLoader.postInitApplication();
            context = intent.getIntExtra("currentAccount", UserConfig.selectedAccount);
            long longExtra = intent.getLongExtra("did", 777000);
            byte[] byteArrayExtra = intent.getByteArrayExtra(DataSchemeDataSource.SCHEME_DATA);
            SendMessagesHelper.getInstance(context).sendNotificationCallback(longExtra, intent.getIntExtra("mid", 0), byteArrayExtra);
        }
    }
}
