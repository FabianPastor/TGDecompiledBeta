package org.telegram.messenger;

import java.util.ArrayList;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ContactsController$AcnaGQSIUUZcaRV7Rm4-aExKG2o implements RequestDelegate {
    private final /* synthetic */ ContactsController f$0;
    private final /* synthetic */ ArrayList f$1;
    private final /* synthetic */ ArrayList f$2;

    public /* synthetic */ -$$Lambda$ContactsController$AcnaGQSIUUZcaRV7Rm4-aExKG2o(ContactsController contactsController, ArrayList arrayList, ArrayList arrayList2) {
        this.f$0 = contactsController;
        this.f$1 = arrayList;
        this.f$2 = arrayList2;
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        this.f$0.lambda$deleteContact$53$ContactsController(this.f$1, this.f$2, tLObject, tL_error);
    }
}
