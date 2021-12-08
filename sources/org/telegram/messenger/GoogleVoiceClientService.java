package org.telegram.messenger;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import com.google.android.search.verification.client.SearchActionVerificationClientService;
import java.util.ArrayList;
import java.util.HashMap;
import org.telegram.messenger.MessageObject;
import org.telegram.tgnet.TLRPC;

public class GoogleVoiceClientService extends SearchActionVerificationClientService {
    public void performAction(Intent intent, boolean isVerified, Bundle options) {
        if (isVerified) {
            AndroidUtilities.runOnUIThread(new GoogleVoiceClientService$$ExternalSyntheticLambda0(intent));
        }
    }

    static /* synthetic */ void lambda$performAction$0(Intent intent) {
        TLRPC.User user;
        Intent intent2 = intent;
        try {
            int currentAccount = UserConfig.selectedAccount;
            ApplicationLoader.postInitApplication();
            if (AndroidUtilities.needShowPasscode()) {
                return;
            }
            if (!SharedConfig.isWaitingForPasscodeEnter) {
                String text = intent2.getStringExtra("android.intent.extra.TEXT");
                if (!TextUtils.isEmpty(text)) {
                    String contactUri = intent2.getStringExtra("com.google.android.voicesearch.extra.RECIPIENT_CONTACT_URI");
                    long uid = Long.parseLong(intent2.getStringExtra("com.google.android.voicesearch.extra.RECIPIENT_CONTACT_CHAT_ID"));
                    TLRPC.User user2 = MessagesController.getInstance(currentAccount).getUser(Long.valueOf(uid));
                    if (user2 == null) {
                        TLRPC.User user3 = MessagesStorage.getInstance(currentAccount).getUserSync(uid);
                        if (user3 != null) {
                            MessagesController.getInstance(currentAccount).putUser(user3, true);
                        }
                        user = user3;
                    } else {
                        user = user2;
                    }
                    if (user != null) {
                        ContactsController.getInstance(currentAccount).markAsContacted(contactUri);
                        TLRPC.User user4 = user;
                        long j = uid;
                        SendMessagesHelper.getInstance(currentAccount).sendMessage(text, user.id, (MessageObject) null, (MessageObject) null, (TLRPC.WebPage) null, true, (ArrayList<TLRPC.MessageEntity>) null, (TLRPC.ReplyMarkup) null, (HashMap<String, String>) null, true, 0, (MessageObject.SendAnimationData) null);
                        return;
                    }
                    long j2 = uid;
                }
            }
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }
}
