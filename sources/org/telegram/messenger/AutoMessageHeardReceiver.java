package org.telegram.messenger;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.User;

public class AutoMessageHeardReceiver extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
        ApplicationLoader.postInitApplication();
        long longExtra = intent.getLongExtra("dialog_id", 0);
        int intExtra = intent.getIntExtra("max_id", 0);
        int intExtra2 = intent.getIntExtra("currentAccount", 0);
        if (!(longExtra == 0 || intExtra == 0)) {
            int i = (int) longExtra;
            AccountInstance instance = AccountInstance.getInstance(intExtra2);
            if (i > 0) {
                if (instance.getMessagesController().getUser(Integer.valueOf(i)) == null) {
                    Utilities.globalQueue.postRunnable(new -$$Lambda$AutoMessageHeardReceiver$UMjKVnDw1qOboSNZfqFvCH4SLUI(instance, i, intExtra2, longExtra, intExtra));
                    return;
                }
            } else if (i < 0 && instance.getMessagesController().getChat(Integer.valueOf(-i)) == null) {
                Utilities.globalQueue.postRunnable(new -$$Lambda$AutoMessageHeardReceiver$R9C-O9TfhsmJTCeCbR268b8rTs8(instance, i, intExtra2, longExtra, intExtra));
                return;
            }
            MessagesController.getInstance(intExtra2).markDialogAsRead(longExtra, intExtra, intExtra, 0, false, 0, true);
        }
    }

    static /* synthetic */ void lambda$null$0(AccountInstance accountInstance, User user, int i, long j, int i2) {
        accountInstance.getMessagesController().putUser(user, true);
        MessagesController.getInstance(i).markDialogAsRead(j, i2, i2, 0, false, 0, true);
    }

    static /* synthetic */ void lambda$null$2(AccountInstance accountInstance, Chat chat, int i, long j, int i2) {
        accountInstance.getMessagesController().putChat(chat, true);
        MessagesController.getInstance(i).markDialogAsRead(j, i2, i2, 0, false, 0, true);
    }
}
