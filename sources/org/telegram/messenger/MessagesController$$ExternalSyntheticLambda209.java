package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda209 implements RequestDelegate {
    public final /* synthetic */ MessagesController f$0;
    public final /* synthetic */ Long f$1;

    public /* synthetic */ MessagesController$$ExternalSyntheticLambda209(MessagesController messagesController, Long l) {
        this.f$0 = messagesController;
        this.f$1 = l;
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        this.f$0.m236xe7ba3417(this.f$1, tLObject, tL_error);
    }
}
