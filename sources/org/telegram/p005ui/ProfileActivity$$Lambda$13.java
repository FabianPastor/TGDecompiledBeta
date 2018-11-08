package org.telegram.p005ui;

import org.telegram.p005ui.ContactsActivity.ContactsActivityDelegate;
import org.telegram.tgnet.TLRPC.User;

/* renamed from: org.telegram.ui.ProfileActivity$$Lambda$13 */
final /* synthetic */ class ProfileActivity$$Lambda$13 implements ContactsActivityDelegate {
    private final ProfileActivity arg$1;

    ProfileActivity$$Lambda$13(ProfileActivity profileActivity) {
        this.arg$1 = profileActivity;
    }

    public void didSelectContact(User user, String str, ContactsActivity contactsActivity) {
        this.arg$1.lambda$openAddMember$22$ProfileActivity(user, str, contactsActivity);
    }
}
