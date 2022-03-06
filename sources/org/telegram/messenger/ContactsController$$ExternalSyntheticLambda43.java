package org.telegram.messenger;

import org.telegram.tgnet.TLRPC$User;

public final /* synthetic */ class ContactsController$$ExternalSyntheticLambda43 implements Runnable {
    public final /* synthetic */ ContactsController f$0;
    public final /* synthetic */ TLRPC$User f$1;

    public /* synthetic */ ContactsController$$ExternalSyntheticLambda43(ContactsController contactsController, TLRPC$User tLRPC$User) {
        this.f$0 = contactsController;
        this.f$1 = tLRPC$User;
    }

    public final void run() {
        this.f$0.lambda$addContact$51(this.f$1);
    }
}
