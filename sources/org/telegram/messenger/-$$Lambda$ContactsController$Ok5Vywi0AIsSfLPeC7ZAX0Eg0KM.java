package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ContactsController$Ok5Vywi0AIsSfLPeC7ZAX0Eg0KM implements RequestDelegate {
    private final /* synthetic */ ContactsController f$0;
    private final /* synthetic */ Runnable f$1;

    public /* synthetic */ -$$Lambda$ContactsController$Ok5Vywi0AIsSfLPeC7ZAX0Eg0KM(ContactsController contactsController, Runnable runnable) {
        this.f$0 = contactsController;
        this.f$1 = runnable;
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        this.f$0.lambda$deleteAllContacts$8$ContactsController(this.f$1, tLObject, tL_error);
    }
}
