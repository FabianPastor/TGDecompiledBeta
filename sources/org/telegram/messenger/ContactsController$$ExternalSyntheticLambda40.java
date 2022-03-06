package org.telegram.messenger;

import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class ContactsController$$ExternalSyntheticLambda40 implements Runnable {
    public final /* synthetic */ ContactsController f$0;
    public final /* synthetic */ TLRPC$TL_error f$1;
    public final /* synthetic */ TLObject f$2;
    public final /* synthetic */ int f$3;

    public /* synthetic */ ContactsController$$ExternalSyntheticLambda40(ContactsController contactsController, TLRPC$TL_error tLRPC$TL_error, TLObject tLObject, int i) {
        this.f$0 = contactsController;
        this.f$1 = tLRPC$TL_error;
        this.f$2 = tLObject;
        this.f$3 = i;
    }

    public final void run() {
        this.f$0.lambda$loadPrivacySettings$63(this.f$1, this.f$2, this.f$3);
    }
}
