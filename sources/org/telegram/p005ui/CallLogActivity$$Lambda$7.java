package org.telegram.p005ui;

import org.telegram.p005ui.ContactsActivity.ContactsActivityDelegate;
import org.telegram.tgnet.TLRPC.User;

/* renamed from: org.telegram.ui.CallLogActivity$$Lambda$7 */
final /* synthetic */ class CallLogActivity$$Lambda$7 implements ContactsActivityDelegate {
    private final CallLogActivity arg$1;

    CallLogActivity$$Lambda$7(CallLogActivity callLogActivity) {
        this.arg$1 = callLogActivity;
    }

    public void didSelectContact(User user, String str, ContactsActivity contactsActivity) {
        this.arg$1.lambda$null$3$CallLogActivity(user, str, contactsActivity);
    }
}
