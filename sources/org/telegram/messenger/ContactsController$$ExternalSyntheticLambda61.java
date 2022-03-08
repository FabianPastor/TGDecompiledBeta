package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$User;

public final /* synthetic */ class ContactsController$$ExternalSyntheticLambda61 implements RequestDelegate {
    public final /* synthetic */ ContactsController f$0;
    public final /* synthetic */ TLRPC$User f$1;

    public /* synthetic */ ContactsController$$ExternalSyntheticLambda61(ContactsController contactsController, TLRPC$User tLRPC$User) {
        this.f$0 = contactsController;
        this.f$1 = tLRPC$User;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$addContact$52(this.f$1, tLObject, tLRPC$TL_error);
    }
}
