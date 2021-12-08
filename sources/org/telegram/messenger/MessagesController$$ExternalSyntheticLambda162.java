package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda162 implements RequestDelegate {
    public final /* synthetic */ MessagesController f$0;
    public final /* synthetic */ int f$1;
    public final /* synthetic */ int f$2;
    public final /* synthetic */ Runnable f$3;

    public /* synthetic */ MessagesController$$ExternalSyntheticLambda162(MessagesController messagesController, int i, int i2, Runnable runnable) {
        this.f$0 = messagesController;
        this.f$1 = i;
        this.f$2 = i2;
        this.f$3 = runnable;
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        this.f$0.m240lambda$loadDialogs$152$orgtelegrammessengerMessagesController(this.f$1, this.f$2, this.f$3, tLObject, tL_error);
    }
}
