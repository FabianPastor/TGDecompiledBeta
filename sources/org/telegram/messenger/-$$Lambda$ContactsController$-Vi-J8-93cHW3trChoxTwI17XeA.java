package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ContactsController$-Vi-J8-93cHW3trChoxTwI17XeA implements RequestDelegate {
    private final /* synthetic */ ContactsController f$0;

    public /* synthetic */ -$$Lambda$ContactsController$-Vi-J8-93cHW3trChoxTwI17XeA(ContactsController contactsController) {
        this.f$0 = contactsController;
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        this.f$0.lambda$addContact$50$ContactsController(tLObject, tL_error);
    }
}
