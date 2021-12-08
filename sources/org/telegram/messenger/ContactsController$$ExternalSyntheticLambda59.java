package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class ContactsController$$ExternalSyntheticLambda59 implements RequestDelegate {
    public static final /* synthetic */ ContactsController$$ExternalSyntheticLambda59 INSTANCE = new ContactsController$$ExternalSyntheticLambda59();

    private /* synthetic */ ContactsController$$ExternalSyntheticLambda59() {
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        ContactsController.lambda$resetImportedContacts$9(tLObject, tL_error);
    }
}
