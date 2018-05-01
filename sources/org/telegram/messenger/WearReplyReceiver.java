package org.telegram.messenger;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.RemoteInput;

public class WearReplyReceiver extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
        Intent intent2 = intent;
        ApplicationLoader.postInitApplication();
        Bundle resultsFromIntent = RemoteInput.getResultsFromIntent(intent);
        if (resultsFromIntent != null) {
            CharSequence charSequence = resultsFromIntent.getCharSequence(NotificationsController.EXTRA_VOICE_REPLY);
            if (charSequence != null) {
                if (charSequence.length() != 0) {
                    long longExtra = intent2.getLongExtra("dialog_id", 0);
                    int intExtra = intent2.getIntExtra("max_id", 0);
                    int intExtra2 = intent2.getIntExtra("currentAccount", 0);
                    if (longExtra != 0) {
                        if (intExtra != 0) {
                            SendMessagesHelper.getInstance(intExtra2).sendMessage(charSequence.toString(), longExtra, null, null, true, null, null, null);
                            MessagesController.getInstance(intExtra2).markDialogAsRead(longExtra, intExtra, intExtra, 0, false, 0, true);
                        }
                    }
                }
            }
        }
    }
}
