package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

final /* synthetic */ class ContactsController$$Lambda$6 implements RequestDelegate {
    private final ContactsController arg$1;
    private final Runnable arg$2;

    ContactsController$$Lambda$6(ContactsController contactsController, Runnable runnable) {
        this.arg$1 = contactsController;
        this.arg$2 = runnable;
    }

    public void run(TLObject tLObject, TL_error tL_error) {
        this.arg$1.lambda$deleteAllContacts$8$ContactsController(this.arg$2, tLObject, tL_error);
    }
}
