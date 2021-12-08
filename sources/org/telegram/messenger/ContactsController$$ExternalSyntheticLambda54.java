package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class ContactsController$$ExternalSyntheticLambda54 implements RequestDelegate {
    public final /* synthetic */ ContactsController f$0;
    public final /* synthetic */ Runnable f$1;

    public /* synthetic */ ContactsController$$ExternalSyntheticLambda54(ContactsController contactsController, Runnable runnable) {
        this.f$0 = contactsController;
        this.f$1 = runnable;
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        this.f$0.m23x3b83ab48(this.f$1, tLObject, tL_error);
    }
}
