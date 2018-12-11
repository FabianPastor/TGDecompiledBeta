package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

final /* synthetic */ class ContactsController$$Lambda$23 implements RequestDelegate {
    private final ContactsController arg$1;

    ContactsController$$Lambda$23(ContactsController contactsController) {
        this.arg$1 = contactsController;
    }

    public void run(TLObject tLObject, TL_error tL_error) {
        this.arg$1.lambda$addContact$50$ContactsController(tLObject, tL_error);
    }
}
