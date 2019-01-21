package org.telegram.ui;

import java.util.Comparator;
import org.telegram.messenger.ContactsController.Contact;

final /* synthetic */ class InviteContactsActivity$$Lambda$2 implements Comparator {
    static final Comparator $instance = new InviteContactsActivity$$Lambda$2();

    private InviteContactsActivity$$Lambda$2() {
    }

    public int compare(Object obj, Object obj2) {
        return InviteContactsActivity.lambda$fetchContacts$2$InviteContactsActivity((Contact) obj, (Contact) obj2);
    }
}
