package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.User;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ContactsController$DJRv0YgvC4lARbyVfXBJRqdOY2g implements RequestDelegate {
    private final /* synthetic */ ContactsController f$0;
    private final /* synthetic */ User f$1;

    public /* synthetic */ -$$Lambda$ContactsController$DJRv0YgvC4lARbyVfXBJRqdOY2g(ContactsController contactsController, User user) {
        this.f$0 = contactsController;
        this.f$1 = user;
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        this.f$0.lambda$addContact$50$ContactsController(this.f$1, tLObject, tL_error);
    }
}
