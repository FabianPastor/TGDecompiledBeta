package org.telegram.messenger;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import org.telegram.tgnet.TLRPC;

public class AutoMessageHeardReceiver extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
        Intent intent2 = intent;
        ApplicationLoader.postInitApplication();
        long dialogId = intent2.getLongExtra("dialog_id", 0);
        int maxId = intent2.getIntExtra("max_id", 0);
        int currentAccount = intent2.getIntExtra("currentAccount", 0);
        if (dialogId == 0 || maxId == 0) {
        } else if (!UserConfig.isValidAccount(currentAccount)) {
            long j = dialogId;
        } else {
            AccountInstance accountInstance = AccountInstance.getInstance(currentAccount);
            if (DialogObject.isUserDialog(dialogId)) {
                if (accountInstance.getMessagesController().getUser(Long.valueOf(dialogId)) == null) {
                    Utilities.globalQueue.postRunnable(new AutoMessageHeardReceiver$$ExternalSyntheticLambda0(accountInstance, dialogId, currentAccount, maxId));
                    return;
                }
            } else if (DialogObject.isChatDialog(dialogId) && accountInstance.getMessagesController().getChat(Long.valueOf(-dialogId)) == null) {
                Utilities.globalQueue.postRunnable(new AutoMessageHeardReceiver$$ExternalSyntheticLambda1(accountInstance, dialogId, currentAccount, maxId));
                return;
            }
            long j2 = dialogId;
            MessagesController.getInstance(currentAccount).markDialogAsRead(dialogId, maxId, maxId, 0, false, 0, 0, true, 0);
        }
    }

    static /* synthetic */ void lambda$onReceive$0(AccountInstance accountInstance, TLRPC.User user1, int currentAccount, long dialogId, int maxId) {
        TLRPC.User user = user1;
        accountInstance.getMessagesController().putUser(user1, true);
        MessagesController.getInstance(currentAccount).markDialogAsRead(dialogId, maxId, maxId, 0, false, 0, 0, true, 0);
    }

    static /* synthetic */ void lambda$onReceive$2(AccountInstance accountInstance, TLRPC.Chat chat1, int currentAccount, long dialogId, int maxId) {
        TLRPC.Chat chat = chat1;
        accountInstance.getMessagesController().putChat(chat1, true);
        MessagesController.getInstance(currentAccount).markDialogAsRead(dialogId, maxId, maxId, 0, false, 0, 0, true, 0);
    }
}
