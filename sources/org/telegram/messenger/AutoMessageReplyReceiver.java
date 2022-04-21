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

public class AutoMessageReplyReceiver extends BroadcastReceiver {
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
                if (dialogId != 0 && maxId != 0 && UserConfig.isValidAccount(currentAccount)) {
                    SendMessagesHelper.getInstance(currentAccount).sendMessage(text.toString(), dialogId, (MessageObject) null, (MessageObject) null, (TLRPC.WebPage) null, true, (ArrayList<TLRPC.MessageEntity>) null, (TLRPC.ReplyMarkup) null, (HashMap<String, String>) null, true, 0, (MessageObject.SendAnimationData) null);
                    MessagesController.getInstance(currentAccount).markDialogAsRead(dialogId, maxId, maxId, 0, false, 0, 0, true, 0);
                }
            }
        }
    }
}
