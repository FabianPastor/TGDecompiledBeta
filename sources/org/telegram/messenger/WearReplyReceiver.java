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
        Bundle remoteInput = RemoteInput.getResultsFromIntent(intent);
        if (remoteInput != null) {
            CharSequence text = remoteInput.getCharSequence(NotificationsController.EXTRA_VOICE_REPLY);
            if (text != null) {
                if (text.length() != 0) {
                    long dialog_id = intent2.getLongExtra("dialog_id", 0);
                    int max_id = intent2.getIntExtra("max_id", 0);
                    int currentAccount = intent2.getIntExtra("currentAccount", 0);
                    if (dialog_id == 0) {
                    } else if (max_id == 0) {
                        r5 = currentAccount;
                    } else {
                        r5 = currentAccount;
                        SendMessagesHelper.getInstance(currentAccount).sendMessage(text.toString(), dialog_id, null, null, true, null, null, null);
                        MessagesController.getInstance(r5).markDialogAsRead(dialog_id, max_id, max_id, 0, false, 0, true);
                    }
                }
            }
        }
    }
}
