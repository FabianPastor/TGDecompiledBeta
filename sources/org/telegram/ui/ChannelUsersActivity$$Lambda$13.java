package org.telegram.ui;

import org.telegram.tgnet.TLRPC.User;
import org.telegram.ui.ContactsActivity.ContactsActivityDelegate;

final /* synthetic */ class ChannelUsersActivity$$Lambda$13 implements ContactsActivityDelegate {
    private final ChannelUsersActivity arg$1;

    ChannelUsersActivity$$Lambda$13(ChannelUsersActivity channelUsersActivity) {
        this.arg$1 = channelUsersActivity;
    }

    public void didSelectContact(User user, String str, ContactsActivity contactsActivity) {
        this.arg$1.lambda$null$0$ChannelUsersActivity(user, str, contactsActivity);
    }
}
