package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class ContactsController$$ExternalSyntheticLambda52 implements RequestDelegate {
    public final /* synthetic */ ContactsController f$0;
    public final /* synthetic */ long f$1;

    public /* synthetic */ ContactsController$$ExternalSyntheticLambda52(ContactsController contactsController, long j) {
        this.f$0 = contactsController;
        this.f$1 = j;
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        this.f$0.m20lambda$loadContacts$27$orgtelegrammessengerContactsController(this.f$1, tLObject, tL_error);
    }
}
