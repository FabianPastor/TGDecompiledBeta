package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class ContactsController$$ExternalSyntheticLambda56 implements RequestDelegate {
    public final /* synthetic */ ContactsController f$0;
    public final /* synthetic */ long f$1;

    public /* synthetic */ ContactsController$$ExternalSyntheticLambda56(ContactsController contactsController, long j) {
        this.f$0 = contactsController;
        this.f$1 = j;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$loadContacts$27(this.f$1, tLObject, tLRPC$TL_error);
    }
}
