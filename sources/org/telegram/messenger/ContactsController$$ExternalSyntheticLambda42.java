package org.telegram.messenger;

import org.telegram.tgnet.TLRPC$Updates;

public final /* synthetic */ class ContactsController$$ExternalSyntheticLambda42 implements Runnable {
    public final /* synthetic */ ContactsController f$0;
    public final /* synthetic */ TLRPC$Updates f$1;

    public /* synthetic */ ContactsController$$ExternalSyntheticLambda42(ContactsController contactsController, TLRPC$Updates tLRPC$Updates) {
        this.f$0 = contactsController;
        this.f$1 = tLRPC$Updates;
    }

    public final void run() {
        this.f$0.lambda$addContact$52(this.f$1);
    }
}
