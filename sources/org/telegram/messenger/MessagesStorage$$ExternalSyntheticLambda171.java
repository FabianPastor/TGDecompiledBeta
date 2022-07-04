package org.telegram.messenger;

import org.telegram.tgnet.TLRPC$Message;

public final /* synthetic */ class MessagesStorage$$ExternalSyntheticLambda171 implements Runnable {
    public final /* synthetic */ MessagesStorage f$0;
    public final /* synthetic */ TLRPC$Message f$1;
    public final /* synthetic */ long f$2;

    public /* synthetic */ MessagesStorage$$ExternalSyntheticLambda171(MessagesStorage messagesStorage, TLRPC$Message tLRPC$Message, long j) {
        this.f$0 = messagesStorage;
        this.f$1 = tLRPC$Message;
        this.f$2 = j;
    }

    public final void run() {
        this.f$0.lambda$updateMessageCustomParams$79(this.f$1, this.f$2);
    }
}
