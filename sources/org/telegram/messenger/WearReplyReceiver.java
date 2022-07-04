package org.telegram.messenger;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import androidx.core.app.RemoteInput;
import java.util.ArrayList;
import java.util.HashMap;
import org.telegram.messenger.MessageObject;
import org.telegram.tgnet.TLRPC;

public class WearReplyReceiver extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
        Intent intent2 = intent;
        ApplicationLoader.postInitApplication();
        Bundle remoteInput = RemoteInput.getResultsFromIntent(intent);
        if (remoteInput != null) {
            CharSequence text = remoteInput.getCharSequence("extra_voice_reply");
            if (!TextUtils.isEmpty(text)) {
                long dialogId = intent2.getLongExtra("dialog_id", 0);
                int maxId = intent2.getIntExtra("max_id", 0);
                int currentAccount = intent2.getIntExtra("currentAccount", 0);
                if (dialogId == 0 || maxId == 0) {
                } else if (!UserConfig.isValidAccount(currentAccount)) {
                    Bundle bundle = remoteInput;
                } else {
                    AccountInstance accountInstance = AccountInstance.getInstance(currentAccount);
                    if (!DialogObject.isUserDialog(dialogId)) {
                        if (DialogObject.isChatDialog(dialogId) && accountInstance.getMessagesController().getChat(Long.valueOf(-dialogId)) == null) {
                            Utilities.globalQueue.postRunnable(new WearReplyReceiver$$ExternalSyntheticLambda1(this, accountInstance, dialogId, text, maxId));
                            return;
                        }
                    } else if (accountInstance.getMessagesController().getUser(Long.valueOf(dialogId)) == null) {
                        WearReplyReceiver$$ExternalSyntheticLambda0 wearReplyReceiver$$ExternalSyntheticLambda0 = r3;
                        Bundle bundle2 = remoteInput;
                        DispatchQueue dispatchQueue = Utilities.globalQueue;
                        WearReplyReceiver$$ExternalSyntheticLambda0 wearReplyReceiver$$ExternalSyntheticLambda02 = new WearReplyReceiver$$ExternalSyntheticLambda0(this, accountInstance, dialogId, text, maxId);
                        dispatchQueue.postRunnable(wearReplyReceiver$$ExternalSyntheticLambda0);
                        return;
                    }
                    sendMessage(accountInstance, text, dialogId, maxId);
                }
            }
        }
    }

    /* renamed from: lambda$onReceive$1$org-telegram-messenger-WearReplyReceiver  reason: not valid java name */
    public /* synthetic */ void m2393lambda$onReceive$1$orgtelegrammessengerWearReplyReceiver(AccountInstance accountInstance, long dialogId, CharSequence text, int maxId) {
        AndroidUtilities.runOnUIThread(new WearReplyReceiver$$ExternalSyntheticLambda3(this, accountInstance, accountInstance.getMessagesStorage().getUserSync(dialogId), text, dialogId, maxId));
    }

    /* renamed from: lambda$onReceive$0$org-telegram-messenger-WearReplyReceiver  reason: not valid java name */
    public /* synthetic */ void m2392lambda$onReceive$0$orgtelegrammessengerWearReplyReceiver(AccountInstance accountInstance, TLRPC.User user1, CharSequence text, long dialogId, int maxId) {
        accountInstance.getMessagesController().putUser(user1, true);
        sendMessage(accountInstance, text, dialogId, maxId);
    }

    /* renamed from: lambda$onReceive$3$org-telegram-messenger-WearReplyReceiver  reason: not valid java name */
    public /* synthetic */ void m2395lambda$onReceive$3$orgtelegrammessengerWearReplyReceiver(AccountInstance accountInstance, long dialogId, CharSequence text, int maxId) {
        AndroidUtilities.runOnUIThread(new WearReplyReceiver$$ExternalSyntheticLambda2(this, accountInstance, accountInstance.getMessagesStorage().getChatSync(-dialogId), text, dialogId, maxId));
    }

    /* renamed from: lambda$onReceive$2$org-telegram-messenger-WearReplyReceiver  reason: not valid java name */
    public /* synthetic */ void m2394lambda$onReceive$2$orgtelegrammessengerWearReplyReceiver(AccountInstance accountInstance, TLRPC.Chat chat1, CharSequence text, long dialogId, int maxId) {
        accountInstance.getMessagesController().putChat(chat1, true);
        sendMessage(accountInstance, text, dialogId, maxId);
    }

    private void sendMessage(AccountInstance accountInstance, CharSequence text, long dialog_id, int max_id) {
        accountInstance.getSendMessagesHelper().sendMessage(text.toString(), dialog_id, (MessageObject) null, (MessageObject) null, (TLRPC.WebPage) null, true, (ArrayList<TLRPC.MessageEntity>) null, (TLRPC.ReplyMarkup) null, (HashMap<String, String>) null, true, 0, (MessageObject.SendAnimationData) null);
        accountInstance.getMessagesController().markDialogAsRead(dialog_id, max_id, max_id, 0, false, 0, 0, true, 0);
    }
}
