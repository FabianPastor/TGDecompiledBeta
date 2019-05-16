package org.telegram.ui;

import org.telegram.tgnet.TLRPC.User;
import org.telegram.ui.ContactsActivity.ContactsActivityDelegate;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$CallLogActivity$glOYCOInhnTGmJL8rtqySIZYCwY implements ContactsActivityDelegate {
    private final /* synthetic */ CallLogActivity f$0;

    public /* synthetic */ -$$Lambda$CallLogActivity$glOYCOInhnTGmJL8rtqySIZYCwY(CallLogActivity callLogActivity) {
        this.f$0 = callLogActivity;
    }

    public final void didSelectContact(User user, String str, ContactsActivity contactsActivity) {
        this.f$0.lambda$null$3$CallLogActivity(user, str, contactsActivity);
    }
}
