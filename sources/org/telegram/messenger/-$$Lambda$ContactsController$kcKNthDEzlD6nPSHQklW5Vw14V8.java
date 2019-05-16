package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ContactsController$kcKNthDEzlD6nPSHQklW5Vw14V8 implements RequestDelegate {
    private final /* synthetic */ ContactsController f$0;

    public /* synthetic */ -$$Lambda$ContactsController$kcKNthDEzlD6nPSHQklW5Vw14V8(ContactsController contactsController) {
        this.f$0 = contactsController;
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        this.f$0.lambda$checkInviteText$3$ContactsController(tLObject, tL_error);
    }
}
