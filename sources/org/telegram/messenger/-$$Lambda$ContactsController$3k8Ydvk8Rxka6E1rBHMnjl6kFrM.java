package org.telegram.messenger;

import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ContactsController$3k8Ydvk8Rxka6E1rBHMnjl6kFrM implements Runnable {
    private final /* synthetic */ ContactsController f$0;
    private final /* synthetic */ TL_error f$1;
    private final /* synthetic */ TLObject f$2;

    public /* synthetic */ -$$Lambda$ContactsController$3k8Ydvk8Rxka6E1rBHMnjl6kFrM(ContactsController contactsController, TL_error tL_error, TLObject tLObject) {
        this.f$0 = contactsController;
        this.f$1 = tL_error;
        this.f$2 = tLObject;
    }

    public final void run() {
        this.f$0.lambda$null$56$ContactsController(this.f$1, this.f$2);
    }
}
