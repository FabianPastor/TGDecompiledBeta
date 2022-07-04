package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda175 implements RequestDelegate {
    public final /* synthetic */ MessagesController f$0;
    public final /* synthetic */ int f$1;

    public /* synthetic */ MessagesController$$ExternalSyntheticLambda175(MessagesController messagesController, int i) {
        this.f$0 = messagesController;
        this.f$1 = i;
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        this.f$0.m259xe05885d1(this.f$1, tLObject, tL_error);
    }
}
