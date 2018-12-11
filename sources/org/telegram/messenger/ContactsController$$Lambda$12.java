package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

final /* synthetic */ class ContactsController$$Lambda$12 implements RequestDelegate {
    private final ContactsController arg$1;
    private final int arg$2;

    ContactsController$$Lambda$12(ContactsController contactsController, int i) {
        this.arg$1 = contactsController;
        this.arg$2 = i;
    }

    public void run(TLObject tLObject, TL_error tL_error) {
        this.arg$1.lambda$loadContacts$27$ContactsController(this.arg$2, tLObject, tL_error);
    }
}
