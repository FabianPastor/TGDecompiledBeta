package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda229 implements RequestDelegate {
    public final /* synthetic */ MessagesController f$0;
    public final /* synthetic */ Object f$1;
    public final /* synthetic */ TLRPC.TL_messages_saveGif f$2;

    public /* synthetic */ MessagesController$$ExternalSyntheticLambda229(MessagesController messagesController, Object obj, TLRPC.TL_messages_saveGif tL_messages_saveGif) {
        this.f$0 = messagesController;
        this.f$1 = obj;
        this.f$2 = tL_messages_saveGif;
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        this.f$0.m374lambda$saveGif$113$orgtelegrammessengerMessagesController(this.f$1, this.f$2, tLObject, tL_error);
    }
}
