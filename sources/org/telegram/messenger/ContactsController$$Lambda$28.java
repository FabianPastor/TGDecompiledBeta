package org.telegram.messenger;

import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

final /* synthetic */ class ContactsController$$Lambda$28 implements Runnable {
    private final ContactsController arg$1;
    private final TL_error arg$2;
    private final TLObject arg$3;
    private final int arg$4;

    ContactsController$$Lambda$28(ContactsController contactsController, TL_error tL_error, TLObject tLObject, int i) {
        this.arg$1 = contactsController;
        this.arg$2 = tL_error;
        this.arg$3 = tLObject;
        this.arg$4 = i;
    }

    public void run() {
        this.arg$1.lambda$null$58$ContactsController(this.arg$2, this.arg$3, this.arg$4);
    }
}