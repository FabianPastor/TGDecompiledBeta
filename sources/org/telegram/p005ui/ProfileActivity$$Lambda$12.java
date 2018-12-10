package org.telegram.p005ui;

import org.telegram.p005ui.ContactsActivity.ContactsActivityDelegate;
import org.telegram.tgnet.TLRPC.User;

/* renamed from: org.telegram.ui.ProfileActivity$$Lambda$12 */
final /* synthetic */ class ProfileActivity$$Lambda$12 implements ContactsActivityDelegate {
    private final ProfileActivity arg$1;

    ProfileActivity$$Lambda$12(ProfileActivity profileActivity) {
        this.arg$1 = profileActivity;
    }

    public void didSelectContact(User user, String str, ContactsActivity contactsActivity) {
        this.arg$1.lambda$openAddMember$20$ProfileActivity(user, str, contactsActivity);
    }
}
