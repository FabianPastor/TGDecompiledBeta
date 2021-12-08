package org.telegram.messenger;

import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class ContactsController$$ExternalSyntheticLambda37 implements Runnable {
    public final /* synthetic */ ContactsController f$0;
    public final /* synthetic */ TLRPC.User f$1;

    public /* synthetic */ ContactsController$$ExternalSyntheticLambda37(ContactsController contactsController, TLRPC.User user) {
        this.f$0 = contactsController;
        this.f$1 = user;
    }

    public final void run() {
        this.f$0.m11lambda$addContact$50$orgtelegrammessengerContactsController(this.f$1);
    }
}
