package org.telegram.messenger;

import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class ContactsController$$ExternalSyntheticLambda32 implements Runnable {
    public final /* synthetic */ ContactsController f$0;
    public final /* synthetic */ TLRPC.TL_error f$1;
    public final /* synthetic */ TLObject f$2;

    public /* synthetic */ ContactsController$$ExternalSyntheticLambda32(ContactsController contactsController, TLRPC.TL_error tL_error, TLObject tLObject) {
        this.f$0 = contactsController;
        this.f$1 = tL_error;
        this.f$2 = tLObject;
    }

    public final void run() {
        this.f$0.m32xfae6529e(this.f$1, this.f$2);
    }
}
