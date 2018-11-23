package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

final /* synthetic */ class ContactsController$$Lambda$7 implements RequestDelegate {
    static final RequestDelegate $instance = new ContactsController$$Lambda$7();

    private ContactsController$$Lambda$7() {
    }

    public void run(TLObject tLObject, TL_error tL_error) {
        ContactsController.lambda$resetImportedContacts$9$ContactsController(tLObject, tL_error);
    }
}
