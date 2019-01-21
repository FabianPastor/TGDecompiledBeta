package org.telegram.ui;

import org.telegram.tgnet.TLRPC.User;
import org.telegram.ui.ContactsActivity.ContactsActivityDelegate;

final /* synthetic */ class CallLogActivity$$Lambda$7 implements ContactsActivityDelegate {
    private final CallLogActivity arg$1;

    CallLogActivity$$Lambda$7(CallLogActivity callLogActivity) {
        this.arg$1 = callLogActivity;
    }

    public void didSelectContact(User user, String str, ContactsActivity contactsActivity) {
        this.arg$1.lambda$null$3$CallLogActivity(user, str, contactsActivity);
    }
}
