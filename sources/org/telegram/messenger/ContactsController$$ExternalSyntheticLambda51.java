package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class ContactsController$$ExternalSyntheticLambda51 implements RequestDelegate {
    public final /* synthetic */ ContactsController f$0;
    public final /* synthetic */ int f$1;

    public /* synthetic */ ContactsController$$ExternalSyntheticLambda51(ContactsController contactsController, int i) {
        this.f$0 = contactsController;
        this.f$1 = i;
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        this.f$0.m35xaaaa9ee1(this.f$1, tLObject, tL_error);
    }
}
