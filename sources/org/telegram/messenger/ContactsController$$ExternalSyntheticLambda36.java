package org.telegram.messenger;

import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class ContactsController$$ExternalSyntheticLambda36 implements Runnable {
    public final /* synthetic */ ContactsController f$0;
    public final /* synthetic */ TLRPC.Updates f$1;

    public /* synthetic */ ContactsController$$ExternalSyntheticLambda36(ContactsController contactsController, TLRPC.Updates updates) {
        this.f$0 = contactsController;
        this.f$1 = updates;
    }

    public final void run() {
        this.f$0.m2lambda$addContact$51$orgtelegrammessengerContactsController(this.f$1);
    }
}
