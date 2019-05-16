package org.telegram.ui;

import org.telegram.tgnet.TLRPC.User;
import org.telegram.ui.ContactsActivity.ContactsActivityDelegate;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ChatUsersActivity$2Od6Pvar_WsFgSKk8i5ZAWdLCLASSNAMEU implements ContactsActivityDelegate {
    private final /* synthetic */ ChatUsersActivity f$0;

    public /* synthetic */ -$$Lambda$ChatUsersActivity$2Od6Pvar_WsFgSKk8i5ZAWdLCLASSNAMEU(ChatUsersActivity chatUsersActivity) {
        this.f$0 = chatUsersActivity;
    }

    public final void didSelectContact(User user, String str, ContactsActivity contactsActivity) {
        this.f$0.lambda$null$2$ChatUsersActivity(user, str, contactsActivity);
    }
}
