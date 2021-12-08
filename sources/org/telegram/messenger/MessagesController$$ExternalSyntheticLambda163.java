package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda163 implements RequestDelegate {
    public final /* synthetic */ MessagesController f$0;
    public final /* synthetic */ int f$1;
    public final /* synthetic */ long f$2;
    public final /* synthetic */ int f$3;

    public /* synthetic */ MessagesController$$ExternalSyntheticLambda163(MessagesController messagesController, int i, long j, int i2) {
        this.f$0 = messagesController;
        this.f$1 = i;
        this.f$2 = j;
        this.f$3 = i2;
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        this.f$0.m374lambda$sendTyping$132$orgtelegrammessengerMessagesController(this.f$1, this.f$2, this.f$3, tLObject, tL_error);
    }
}
