package org.telegram.messenger;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import androidx.core.app.RemoteInput;

public class WearReplyReceiver extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
        ApplicationLoader.postInitApplication();
        Bundle resultsFromIntent = RemoteInput.getResultsFromIntent(intent);
        if (resultsFromIntent != null) {
            CharSequence charSequence = resultsFromIntent.getCharSequence("extra_voice_reply");
            if (!TextUtils.isEmpty(charSequence)) {
                long longExtra = intent.getLongExtra("dialog_id", 0);
                int intExtra = intent.getIntExtra("max_id", 0);
                int intExtra2 = intent.getIntExtra("currentAccount", 0);
                if (!(longExtra == 0 || intExtra == 0)) {
                    int i = (int) longExtra;
                    AccountInstance instance = AccountInstance.getInstance(intExtra2);
                    if (i > 0) {
                        if (instance.getMessagesController().getUser(Integer.valueOf(i)) == null) {
                            Utilities.globalQueue.postRunnable(new -$$Lambda$WearReplyReceiver$jZzgSQJbuoEMdsbz0qTsozxClxA(this, instance, i, charSequence, longExtra, intExtra));
                            return;
                        }
                    } else if (i < 0 && instance.getMessagesController().getChat(Integer.valueOf(-i)) == null) {
                        Utilities.globalQueue.postRunnable(new -$$Lambda$WearReplyReceiver$A-P_Vh4KASYxyAXDl8X9ZsZ4Svo(this, instance, i, charSequence, longExtra, intExtra));
                        return;
                    }
                    sendMessage(instance, charSequence, longExtra, intExtra);
                }
            }
        }
    }

    public /* synthetic */ void lambda$onReceive$0$WearReplyReceiver(AccountInstance accountInstance, int i, CharSequence charSequence, long j, int i2) {
        accountInstance.getMessagesController().putUser(accountInstance.getMessagesStorage().getUserSync(i), true);
        sendMessage(accountInstance, charSequence, j, i2);
    }

    public /* synthetic */ void lambda$onReceive$1$WearReplyReceiver(AccountInstance accountInstance, int i, CharSequence charSequence, long j, int i2) {
        accountInstance.getMessagesController().putChat(accountInstance.getMessagesStorage().getChatSync(-i), true);
        sendMessage(accountInstance, charSequence, j, i2);
    }

    private void sendMessage(AccountInstance accountInstance, CharSequence charSequence, long j, int i) {
        accountInstance.getSendMessagesHelper().sendMessage(charSequence.toString(), j, null, null, true, null, null, null);
        accountInstance.getMessagesController().markDialogAsRead(j, i, i, 0, false, 0, true);
    }
}
