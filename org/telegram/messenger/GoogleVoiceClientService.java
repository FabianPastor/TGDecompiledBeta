package org.telegram.messenger;

import android.content.Intent;
import android.os.Bundle;
import com.google.android.search.verification.client.SearchActionVerificationClientService;
import org.telegram.tgnet.TLRPC.User;

public class GoogleVoiceClientService extends SearchActionVerificationClientService {
    public boolean performAction(final Intent intent, boolean isVerified, Bundle options) {
        if (!isVerified) {
            return false;
        }
        AndroidUtilities.runOnUIThread(new Runnable() {
            public void run() {
                try {
                    ApplicationLoader.postInitApplication();
                    String text = intent.getStringExtra("android.intent.extra.TEXT");
                    String contactUri = intent.getStringExtra("com.google.android.voicesearch.extra.RECIPIENT_CONTACT_URI");
                    if (text != null && text.length() > 0) {
                        int uid = Integer.parseInt(intent.getStringExtra("com.google.android.voicesearch.extra.RECIPIENT_CONTACT_CHAT_ID"));
                        User user = MessagesController.getInstance().getUser(Integer.valueOf(uid));
                        if (user == null) {
                            user = MessagesStorage.getInstance().getUserSync(uid);
                            if (user != null) {
                                MessagesController.getInstance().putUser(user, true);
                            }
                        }
                        if (user != null) {
                            ContactsController.getInstance().markAsContacted(contactUri);
                            SendMessagesHelper.getInstance().sendMessage(text, (long) user.id, null, null, true, null, null, null);
                        }
                    }
                } catch (Throwable e) {
                    FileLog.e(e);
                }
            }
        });
        return true;
    }
}
