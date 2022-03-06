package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class ContactsController$$ExternalSyntheticLambda63 implements RequestDelegate {
    public static final /* synthetic */ ContactsController$$ExternalSyntheticLambda63 INSTANCE = new ContactsController$$ExternalSyntheticLambda63();

    private /* synthetic */ ContactsController$$ExternalSyntheticLambda63() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        ContactsController.lambda$resetImportedContacts$10(tLObject, tLRPC$TL_error);
    }
}
