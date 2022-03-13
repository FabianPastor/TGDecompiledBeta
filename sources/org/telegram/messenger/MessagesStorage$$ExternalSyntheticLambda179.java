package org.telegram.messenger;

import org.telegram.tgnet.TLRPC$TL_updates;

public final /* synthetic */ class MessagesStorage$$ExternalSyntheticLambda179 implements Runnable {
    public final /* synthetic */ MessagesStorage f$0;
    public final /* synthetic */ TLRPC$TL_updates f$1;

    public /* synthetic */ MessagesStorage$$ExternalSyntheticLambda179(MessagesStorage messagesStorage, TLRPC$TL_updates tLRPC$TL_updates) {
        this.f$0 = messagesStorage;
        this.f$1 = tLRPC$TL_updates;
    }

    public final void run() {
        this.f$0.lambda$updateMessageStateAndIdInternal$165(this.f$1);
    }
}
