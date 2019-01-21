package org.telegram.ui;

import org.telegram.tgnet.TLRPC.User;
import org.telegram.ui.ContactsActivity.ContactsActivityDelegate;

final /* synthetic */ class ChatUsersActivity$$Lambda$20 implements ContactsActivityDelegate {
    private final ChatUsersActivity arg$1;

    ChatUsersActivity$$Lambda$20(ChatUsersActivity chatUsersActivity) {
        this.arg$1 = chatUsersActivity;
    }

    public void didSelectContact(User user, String str, ContactsActivity contactsActivity) {
        this.arg$1.lambda$null$2$ChatUsersActivity(user, str, contactsActivity);
    }
}
