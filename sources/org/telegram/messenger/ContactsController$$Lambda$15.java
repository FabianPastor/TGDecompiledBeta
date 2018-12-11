package org.telegram.messenger;

import java.util.Comparator;
import org.telegram.messenger.ContactsController.Contact;

final /* synthetic */ class ContactsController$$Lambda$15 implements Comparator {
    static final Comparator $instance = new ContactsController$$Lambda$15();

    private ContactsController$$Lambda$15() {
    }

    public int compare(Object obj, Object obj2) {
        return ContactsController.lambda$updateUnregisteredContacts$40$ContactsController((Contact) obj, (Contact) obj2);
    }
}
