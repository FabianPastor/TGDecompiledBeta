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
import org.telegram.tgnet.TLRPC$Chat;
import org.telegram.tgnet.TLRPC$MessageEntity;
import org.telegram.tgnet.TLRPC$ReplyMarkup;
import org.telegram.tgnet.TLRPC$User;
import org.telegram.tgnet.TLRPC$WebPage;

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
                if (longExtra != 0 && intExtra != 0 && UserConfig.isValidAccount(intExtra2)) {
                    int i = (int) longExtra;
                    AccountInstance instance = AccountInstance.getInstance(intExtra2);
                    if (i > 0) {
                        if (instance.getMessagesController().getUser(Integer.valueOf(i)) == null) {
                            Utilities.globalQueue.postRunnable(new WearReplyReceiver$$ExternalSyntheticLambda1(this, instance, i, charSequence, longExtra, intExtra));
                            return;
                        }
                    } else if (i < 0 && instance.getMessagesController().getChat(Integer.valueOf(-i)) == null) {
                        Utilities.globalQueue.postRunnable(new WearReplyReceiver$$ExternalSyntheticLambda0(this, instance, i, charSequence, longExtra, intExtra));
                        return;
                    }
                    sendMessage(instance, charSequence, longExtra, intExtra);
                }
            }
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onReceive$1(AccountInstance accountInstance, int i, CharSequence charSequence, long j, int i2) {
        AndroidUtilities.runOnUIThread(new WearReplyReceiver$$ExternalSyntheticLambda3(this, accountInstance, accountInstance.getMessagesStorage().getUserSync(i), charSequence, j, i2));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onReceive$0(AccountInstance accountInstance, TLRPC$User tLRPC$User, CharSequence charSequence, long j, int i) {
        accountInstance.getMessagesController().putUser(tLRPC$User, true);
        sendMessage(accountInstance, charSequence, j, i);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onReceive$3(AccountInstance accountInstance, int i, CharSequence charSequence, long j, int i2) {
        AndroidUtilities.runOnUIThread(new WearReplyReceiver$$ExternalSyntheticLambda2(this, accountInstance, accountInstance.getMessagesStorage().getChatSync(-i), charSequence, j, i2));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onReceive$2(AccountInstance accountInstance, TLRPC$Chat tLRPC$Chat, CharSequence charSequence, long j, int i) {
        accountInstance.getMessagesController().putChat(tLRPC$Chat, true);
        sendMessage(accountInstance, charSequence, j, i);
    }

    private void sendMessage(AccountInstance accountInstance, CharSequence charSequence, long j, int i) {
        accountInstance.getSendMessagesHelper().sendMessage(charSequence.toString(), j, (MessageObject) null, (MessageObject) null, (TLRPC$WebPage) null, true, (ArrayList<TLRPC$MessageEntity>) null, (TLRPC$ReplyMarkup) null, (HashMap<String, String>) null, true, 0, (MessageObject.SendAnimationData) null);
        accountInstance.getMessagesController().markDialogAsRead(j, i, i, 0, false, 0, 0, true, 0);
    }
}
