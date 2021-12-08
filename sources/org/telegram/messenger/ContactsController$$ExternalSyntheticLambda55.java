package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class ContactsController$$ExternalSyntheticLambda55 implements RequestDelegate {
    public final /* synthetic */ ContactsController f$0;
    public final /* synthetic */ int f$1;

    public /* synthetic */ ContactsController$$ExternalSyntheticLambda55(ContactsController contactsController, int i) {
        this.f$0 = contactsController;
        this.f$1 = i;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$loadPrivacySettings$63(this.f$1, tLObject, tLRPC$TL_error);
    }
}
