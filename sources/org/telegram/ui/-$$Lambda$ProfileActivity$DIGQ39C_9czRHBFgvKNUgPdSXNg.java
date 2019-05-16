package org.telegram.ui;

import org.telegram.tgnet.TLRPC.User;
import org.telegram.ui.ContactsActivity.ContactsActivityDelegate;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ProfileActivity$DIGQ39C_9czRHBFgvKNUgPdSXNg implements ContactsActivityDelegate {
    private final /* synthetic */ ProfileActivity f$0;

    public /* synthetic */ -$$Lambda$ProfileActivity$DIGQ39C_9czRHBFgvKNUgPdSXNg(ProfileActivity profileActivity) {
        this.f$0 = profileActivity;
    }

    public final void didSelectContact(User user, String str, ContactsActivity contactsActivity) {
        this.f$0.lambda$openAddMember$20$ProfileActivity(user, str, contactsActivity);
    }
}
