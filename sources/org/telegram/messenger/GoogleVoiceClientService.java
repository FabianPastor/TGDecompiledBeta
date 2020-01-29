package org.telegram.messenger;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import com.google.android.search.verification.client.SearchActionVerificationClientService;
import java.util.ArrayList;
import java.util.HashMap;
import org.telegram.tgnet.TLRPC;

public class GoogleVoiceClientService extends SearchActionVerificationClientService {
    public void performAction(Intent intent, boolean z, Bundle bundle) {
        if (z) {
            AndroidUtilities.runOnUIThread(new Runnable(intent) {
                private final /* synthetic */ Intent f$0;

                {
                    this.f$0 = r1;
                }

                public final void run() {
                    GoogleVoiceClientService.lambda$performAction$0(this.f$0);
                }
            });
        }
    }

    static /* synthetic */ void lambda$performAction$0(Intent intent) {
        try {
            int i = UserConfig.selectedAccount;
            ApplicationLoader.postInitApplication();
            if (AndroidUtilities.needShowPasscode(false)) {
                return;
            }
            if (!SharedConfig.isWaitingForPasscodeEnter) {
                String stringExtra = intent.getStringExtra("android.intent.extra.TEXT");
                if (!TextUtils.isEmpty(stringExtra)) {
                    String stringExtra2 = intent.getStringExtra("com.google.android.voicesearch.extra.RECIPIENT_CONTACT_URI");
                    int parseInt = Integer.parseInt(intent.getStringExtra("com.google.android.voicesearch.extra.RECIPIENT_CONTACT_CHAT_ID"));
                    TLRPC.User user = MessagesController.getInstance(i).getUser(Integer.valueOf(parseInt));
                    if (user == null && (user = MessagesStorage.getInstance(i).getUserSync(parseInt)) != null) {
                        MessagesController.getInstance(i).putUser(user, true);
                    }
                    if (user != null) {
                        ContactsController.getInstance(i).markAsContacted(stringExtra2);
                        SendMessagesHelper.getInstance(i).sendMessage(stringExtra, (long) user.id, (MessageObject) null, (TLRPC.WebPage) null, true, (ArrayList<TLRPC.MessageEntity>) null, (TLRPC.ReplyMarkup) null, (HashMap<String, String>) null, true, 0);
                    }
                }
            }
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }
}
