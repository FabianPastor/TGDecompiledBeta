package org.telegram.messenger;

import org.telegram.tgnet.TLRPC.TL_contacts_importedContacts;

final /* synthetic */ class ContactsController$$Lambda$34 implements Runnable {
    private final ContactsController arg$1;
    private final TL_contacts_importedContacts arg$2;

    ContactsController$$Lambda$34(ContactsController contactsController, TL_contacts_importedContacts tL_contacts_importedContacts) {
        this.arg$1 = contactsController;
        this.arg$2 = tL_contacts_importedContacts;
    }

    public void run() {
        this.arg$1.lambda$null$49$ContactsController(this.arg$2);
    }
}
