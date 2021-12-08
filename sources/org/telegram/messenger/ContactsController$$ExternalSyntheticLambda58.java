package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class ContactsController$$ExternalSyntheticLambda58 implements RequestDelegate {
    public final /* synthetic */ ContactsController f$0;
    public final /* synthetic */ TLRPC.User f$1;

    public /* synthetic */ ContactsController$$ExternalSyntheticLambda58(ContactsController contactsController, TLRPC.User user) {
        this.f$0 = contactsController;
        this.f$1 = user;
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        this.f$0.m13lambda$addContact$52$orgtelegrammessengerContactsController(this.f$1, tLObject, tL_error);
    }
}
