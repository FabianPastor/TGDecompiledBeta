package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ContactsController$rEtEE7WzBCVDghotOSTBjLdLnsM implements RequestDelegate {
    public static final /* synthetic */ -$$Lambda$ContactsController$rEtEE7WzBCVDghotOSTBjLdLnsM INSTANCE = new -$$Lambda$ContactsController$rEtEE7WzBCVDghotOSTBjLdLnsM();

    private /* synthetic */ -$$Lambda$ContactsController$rEtEE7WzBCVDghotOSTBjLdLnsM() {
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        ContactsController.lambda$resetImportedContacts$9(tLObject, tL_error);
    }
}
