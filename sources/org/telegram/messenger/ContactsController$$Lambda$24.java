package org.telegram.messenger;

import java.util.ArrayList;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

final /* synthetic */ class ContactsController$$Lambda$24 implements RequestDelegate {
    private final ContactsController arg$1;
    private final ArrayList arg$2;
    private final ArrayList arg$3;

    ContactsController$$Lambda$24(ContactsController contactsController, ArrayList arrayList, ArrayList arrayList2) {
        this.arg$1 = contactsController;
        this.arg$2 = arrayList;
        this.arg$3 = arrayList2;
    }

    public void run(TLObject tLObject, TL_error tL_error) {
        this.arg$1.lambda$deleteContact$53$ContactsController(this.arg$2, this.arg$3, tLObject, tL_error);
    }
}
