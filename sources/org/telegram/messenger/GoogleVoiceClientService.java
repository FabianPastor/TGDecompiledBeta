package org.telegram.messenger;

import android.content.Intent;
import android.os.Bundle;
import com.google.android.search.verification.client.SearchActionVerificationClientService;
import org.telegram.tgnet.TLRPC.User;

public class GoogleVoiceClientService extends SearchActionVerificationClientService {
    public boolean performAction(final Intent intent, boolean z, Bundle bundle) {
        if (!z) {
            return null;
        }
        AndroidUtilities.runOnUIThread(new Runnable() {
            public void run() {
                try {
                    int i = UserConfig.selectedAccount;
                    ApplicationLoader.postInitApplication();
                    String stringExtra = intent.getStringExtra("android.intent.extra.TEXT");
                    String stringExtra2 = intent.getStringExtra("com.google.android.voicesearch.extra.RECIPIENT_CONTACT_URI");
                    if (stringExtra != null && stringExtra.length() > 0) {
                        int parseInt = Integer.parseInt(intent.getStringExtra("com.google.android.voicesearch.extra.RECIPIENT_CONTACT_CHAT_ID"));
                        User user = MessagesController.getInstance(i).getUser(Integer.valueOf(parseInt));
                        if (user == null) {
                            user = MessagesStorage.getInstance(i).getUserSync(parseInt);
                            if (user != null) {
                                MessagesController.getInstance(i).putUser(user, true);
                            }
                        }
                        if (user != null) {
                            ContactsController.getInstance(i).markAsContacted(stringExtra2);
                            SendMessagesHelper.getInstance(i).sendMessage(stringExtra, (long) user.id, null, null, true, null, null, null);
                        }
                    }
                } catch (Throwable e) {
                    FileLog.m3e(e);
                }
            }
        });
        return true;
    }
}
