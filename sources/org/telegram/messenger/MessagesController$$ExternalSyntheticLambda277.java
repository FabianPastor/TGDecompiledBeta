package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda277 implements RequestDelegate {
    public final /* synthetic */ MessagesController f$0;
    public final /* synthetic */ Integer f$1;

    public /* synthetic */ MessagesController$$ExternalSyntheticLambda277(MessagesController messagesController, Integer num) {
        this.f$0 = messagesController;
        this.f$1 = num;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$loadChannelParticipants$109(this.f$1, tLObject, tLRPC$TL_error);
    }
}
