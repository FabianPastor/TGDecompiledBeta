package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda302 implements RequestDelegate {
    public final /* synthetic */ MessagesController f$0;
    public final /* synthetic */ Long f$1;

    public /* synthetic */ MessagesController$$ExternalSyntheticLambda302(MessagesController messagesController, Long l) {
        this.f$0 = messagesController;
        this.f$1 = l;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$loadChannelParticipants$115(this.f$1, tLObject, tLRPC$TL_error);
    }
}
