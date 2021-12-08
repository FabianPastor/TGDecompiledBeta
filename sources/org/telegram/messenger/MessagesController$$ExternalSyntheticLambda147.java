package org.telegram.messenger;

import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda147 implements Runnable {
    public final /* synthetic */ MessagesController f$0;
    public final /* synthetic */ TLRPC$TL_error f$1;
    public final /* synthetic */ TLObject f$2;
    public final /* synthetic */ Long f$3;

    public /* synthetic */ MessagesController$$ExternalSyntheticLambda147(MessagesController messagesController, TLRPC$TL_error tLRPC$TL_error, TLObject tLObject, Long l) {
        this.f$0 = messagesController;
        this.f$1 = tLRPC$TL_error;
        this.f$2 = tLObject;
        this.f$3 = l;
    }

    public final void run() {
        this.f$0.lambda$loadChannelParticipants$108(this.f$1, this.f$2, this.f$3);
    }
}
