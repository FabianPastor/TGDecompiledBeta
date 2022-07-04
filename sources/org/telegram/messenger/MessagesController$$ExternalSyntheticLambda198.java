package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda198 implements RequestDelegate {
    public final /* synthetic */ MessagesController f$0;
    public final /* synthetic */ long f$1;

    public /* synthetic */ MessagesController$$ExternalSyntheticLambda198(MessagesController messagesController, long j) {
        this.f$0 = messagesController;
        this.f$1 = j;
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        this.f$0.m293xvar_a577(this.f$1, tLObject, tL_error);
    }
}
