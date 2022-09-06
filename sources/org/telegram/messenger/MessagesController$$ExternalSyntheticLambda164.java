package org.telegram.messenger;

import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda164 implements Runnable {
    public final /* synthetic */ MessagesController f$0;
    public final /* synthetic */ TLRPC$TL_error f$1;
    public final /* synthetic */ long f$2;

    public /* synthetic */ MessagesController$$ExternalSyntheticLambda164(MessagesController messagesController, TLRPC$TL_error tLRPC$TL_error, long j) {
        this.f$0 = messagesController;
        this.f$1 = tLRPC$TL_error;
        this.f$2 = j;
    }

    public final void run() {
        this.f$0.lambda$getChannelDifference$273(this.f$1, this.f$2);
    }
}
