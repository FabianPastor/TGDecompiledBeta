package org.telegram.messenger;

import org.telegram.tgnet.TLRPC$Updates;

public final /* synthetic */ class ContactsController$$ExternalSyntheticLambda41 implements Runnable {
    public final /* synthetic */ ContactsController f$0;
    public final /* synthetic */ TLRPC$Updates f$1;

    public /* synthetic */ ContactsController$$ExternalSyntheticLambda41(ContactsController contactsController, TLRPC$Updates tLRPC$Updates) {
        this.f$0 = contactsController;
        this.f$1 = tLRPC$Updates;
    }

    public final void run() {
        this.f$0.lambda$addContact$51(this.f$1);
    }
}
