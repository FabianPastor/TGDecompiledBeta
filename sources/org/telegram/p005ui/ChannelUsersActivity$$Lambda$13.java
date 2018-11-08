package org.telegram.p005ui;

import org.telegram.p005ui.ContactsActivity.ContactsActivityDelegate;
import org.telegram.tgnet.TLRPC.User;

/* renamed from: org.telegram.ui.ChannelUsersActivity$$Lambda$13 */
final /* synthetic */ class ChannelUsersActivity$$Lambda$13 implements ContactsActivityDelegate {
    private final ChannelUsersActivity arg$1;

    ChannelUsersActivity$$Lambda$13(ChannelUsersActivity channelUsersActivity) {
        this.arg$1 = channelUsersActivity;
    }

    public void didSelectContact(User user, String str, ContactsActivity contactsActivity) {
        this.arg$1.lambda$null$0$ChannelUsersActivity(user, str, contactsActivity);
    }
}
