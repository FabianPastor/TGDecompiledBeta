package org.telegram.messenger;

import java.util.Comparator;
import org.telegram.tgnet.TLRPC.TL_contact;

final /* synthetic */ class ContactsController$$Lambda$11 implements Comparator {
    static final Comparator $instance = new ContactsController$$Lambda$11();

    private ContactsController$$Lambda$11() {
    }

    public int compare(Object obj, Object obj2) {
        return ContactsController.lambda$getContactsHash$25$ContactsController((TL_contact) obj, (TL_contact) obj2);
    }
}
