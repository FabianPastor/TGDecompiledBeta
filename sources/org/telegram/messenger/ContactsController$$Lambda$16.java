package org.telegram.messenger;

import java.util.Comparator;
import org.telegram.tgnet.TLRPC.TL_contact;

final /* synthetic */ class ContactsController$$Lambda$16 implements Comparator {
    private final ContactsController arg$1;

    ContactsController$$Lambda$16(ContactsController contactsController) {
        this.arg$1 = contactsController;
    }

    public int compare(Object obj, Object obj2) {
        return this.arg$1.lambda$buildContactsSectionsArrays$41$ContactsController((TL_contact) obj, (TL_contact) obj2);
    }
}
