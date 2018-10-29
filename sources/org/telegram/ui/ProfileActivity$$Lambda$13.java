package org.telegram.ui;

import org.telegram.tgnet.TLRPC.User;
import org.telegram.ui.ContactsActivity.ContactsActivityDelegate;

final /* synthetic */ class ProfileActivity$$Lambda$13 implements ContactsActivityDelegate {
    private final ProfileActivity arg$1;

    ProfileActivity$$Lambda$13(ProfileActivity profileActivity) {
        this.arg$1 = profileActivity;
    }

    public void didSelectContact(User user, String str, ContactsActivity contactsActivity) {
        this.arg$1.lambda$openAddMember$22$ProfileActivity(user, str, contactsActivity);
    }
}
