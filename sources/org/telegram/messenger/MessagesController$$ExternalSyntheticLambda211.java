package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda211 implements RequestDelegate {
    public final /* synthetic */ MessagesController f$0;
    public final /* synthetic */ long f$1;
    public final /* synthetic */ Runnable f$2;

    public /* synthetic */ MessagesController$$ExternalSyntheticLambda211(MessagesController messagesController, long j, Runnable runnable) {
        this.f$0 = messagesController;
        this.f$1 = j;
        this.f$2 = runnable;
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        this.f$0.m229lambda$getGroupCall$41$orgtelegrammessengerMessagesController(this.f$1, this.f$2, tLObject, tL_error);
    }
}
