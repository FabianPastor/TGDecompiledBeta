package org.telegram.messenger;

import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda139 implements Runnable {
    public final /* synthetic */ MessagesController f$0;
    public final /* synthetic */ TLRPC$TL_error f$1;
    public final /* synthetic */ int f$2;

    public /* synthetic */ MessagesController$$ExternalSyntheticLambda139(MessagesController messagesController, TLRPC$TL_error tLRPC$TL_error, int i) {
        this.f$0 = messagesController;
        this.f$1 = tLRPC$TL_error;
        this.f$2 = i;
    }

    public final void run() {
        this.f$0.lambda$loadFullChat$40(this.f$1, this.f$2);
    }
}
