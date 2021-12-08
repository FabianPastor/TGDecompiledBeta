package org.telegram.messenger;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import org.telegram.tgnet.TLRPC$Chat;
import org.telegram.tgnet.TLRPC$User;

public class AutoMessageHeardReceiver extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
        ApplicationLoader.postInitApplication();
        long longExtra = intent.getLongExtra("dialog_id", 0);
        int intExtra = intent.getIntExtra("max_id", 0);
        int intExtra2 = intent.getIntExtra("currentAccount", 0);
        if (longExtra != 0 && intExtra != 0 && UserConfig.isValidAccount(intExtra2)) {
            AccountInstance instance = AccountInstance.getInstance(intExtra2);
            if (DialogObject.isUserDialog(longExtra)) {
                if (instance.getMessagesController().getUser(Long.valueOf(longExtra)) == null) {
                    Utilities.globalQueue.postRunnable(new AutoMessageHeardReceiver$$ExternalSyntheticLambda1(instance, longExtra, intExtra2, intExtra));
                    return;
                }
            } else if (DialogObject.isChatDialog(longExtra) && instance.getMessagesController().getChat(Long.valueOf(-longExtra)) == null) {
                Utilities.globalQueue.postRunnable(new AutoMessageHeardReceiver$$ExternalSyntheticLambda0(instance, longExtra, intExtra2, intExtra));
                return;
            }
            MessagesController.getInstance(intExtra2).markDialogAsRead(longExtra, intExtra, intExtra, 0, false, 0, 0, true, 0);
        }
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$onReceive$0(AccountInstance accountInstance, TLRPC$User tLRPC$User, int i, long j, int i2) {
        TLRPC$User tLRPC$User2 = tLRPC$User;
        accountInstance.getMessagesController().putUser(tLRPC$User, true);
        MessagesController.getInstance(i).markDialogAsRead(j, i2, i2, 0, false, 0, 0, true, 0);
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$onReceive$2(AccountInstance accountInstance, TLRPC$Chat tLRPC$Chat, int i, long j, int i2) {
        TLRPC$Chat tLRPC$Chat2 = tLRPC$Chat;
        accountInstance.getMessagesController().putChat(tLRPC$Chat, true);
        MessagesController.getInstance(i).markDialogAsRead(j, i2, i2, 0, false, 0, 0, true, 0);
    }
}
