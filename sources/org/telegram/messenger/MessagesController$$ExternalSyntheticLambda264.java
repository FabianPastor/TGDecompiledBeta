package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda264 implements RequestDelegate {
    public final /* synthetic */ MessagesController f$0;
    public final /* synthetic */ boolean f$1;
    public final /* synthetic */ TLRPC.TL_contacts_getBlocked f$2;

    public /* synthetic */ MessagesController$$ExternalSyntheticLambda264(MessagesController messagesController, boolean z, TLRPC.TL_contacts_getBlocked tL_contacts_getBlocked) {
        this.f$0 = messagesController;
        this.f$1 = z;
        this.f$2 = tL_contacts_getBlocked;
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        this.f$0.m211xa6fe97e(this.f$1, this.f$2, tLObject, tL_error);
    }
}
