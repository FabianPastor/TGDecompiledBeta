package org.telegram.messenger;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.core.app.RemoteInput;
import java.util.ArrayList;
import java.util.HashMap;
import org.telegram.messenger.MessageObject;
import org.telegram.tgnet.TLRPC$MessageEntity;
import org.telegram.tgnet.TLRPC$ReplyMarkup;
import org.telegram.tgnet.TLRPC$WebPage;

public class AutoMessageReplyReceiver extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
        CharSequence charSequence;
        Intent intent2 = intent;
        ApplicationLoader.postInitApplication();
        Bundle resultsFromIntent = RemoteInput.getResultsFromIntent(intent);
        if (resultsFromIntent != null && (charSequence = resultsFromIntent.getCharSequence("extra_voice_reply")) != null && charSequence.length() != 0) {
            long longExtra = intent2.getLongExtra("dialog_id", 0);
            int intExtra = intent2.getIntExtra("max_id", 0);
            int intExtra2 = intent2.getIntExtra("currentAccount", 0);
            if (longExtra != 0 && intExtra != 0 && UserConfig.isValidAccount(intExtra2)) {
                SendMessagesHelper.getInstance(intExtra2).sendMessage(charSequence.toString(), longExtra, (MessageObject) null, (MessageObject) null, (TLRPC$WebPage) null, true, (ArrayList<TLRPC$MessageEntity>) null, (TLRPC$ReplyMarkup) null, (HashMap<String, String>) null, true, 0, (MessageObject.SendAnimationData) null);
                MessagesController.getInstance(intExtra2).markDialogAsRead(longExtra, intExtra, intExtra, 0, false, 0, 0, true, 0);
            }
        }
    }
}
