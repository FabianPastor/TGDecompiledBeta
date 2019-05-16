package org.telegram.messenger;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.core.app.RemoteInput;

public class AutoMessageReplyReceiver extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
        Intent intent2 = intent;
        ApplicationLoader.postInitApplication();
        Bundle resultsFromIntent = RemoteInput.getResultsFromIntent(intent);
        if (resultsFromIntent != null) {
            CharSequence charSequence = resultsFromIntent.getCharSequence("extra_voice_reply");
            if (!(charSequence == null || charSequence.length() == 0)) {
                long longExtra = intent2.getLongExtra("dialog_id", 0);
                int intExtra = intent2.getIntExtra("max_id", 0);
                int intExtra2 = intent2.getIntExtra("currentAccount", 0);
                if (!(longExtra == 0 || intExtra == 0)) {
                    SendMessagesHelper.getInstance(intExtra2).sendMessage(charSequence.toString(), longExtra, null, null, true, null, null, null);
                    MessagesController.getInstance(intExtra2).markDialogAsRead(longExtra, intExtra, intExtra, 0, false, 0, true);
                }
            }
        }
    }
}
