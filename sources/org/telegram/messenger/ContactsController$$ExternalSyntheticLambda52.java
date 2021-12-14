package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class ContactsController$$ExternalSyntheticLambda52 implements RequestDelegate {
    public final /* synthetic */ ContactsController f$0;

    public /* synthetic */ ContactsController$$ExternalSyntheticLambda52(ContactsController contactsController) {
        this.f$0 = contactsController;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$loadPrivacySettings$61(tLObject, tLRPC$TL_error);
    }
}
