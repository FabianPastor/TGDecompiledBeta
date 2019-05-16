package org.telegram.messenger;

import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ContactsController$EuAKug4zrNU4o4RK27r3kMVoC7I implements Runnable {
    private final /* synthetic */ ContactsController f$0;
    private final /* synthetic */ TL_error f$1;
    private final /* synthetic */ TLObject f$2;
    private final /* synthetic */ int f$3;

    public /* synthetic */ -$$Lambda$ContactsController$EuAKug4zrNU4o4RK27r3kMVoC7I(ContactsController contactsController, TL_error tL_error, TLObject tLObject, int i) {
        this.f$0 = contactsController;
        this.f$1 = tL_error;
        this.f$2 = tLObject;
        this.f$3 = i;
    }

    public final void run() {
        this.f$0.lambda$null$58$ContactsController(this.f$1, this.f$2, this.f$3);
    }
}
