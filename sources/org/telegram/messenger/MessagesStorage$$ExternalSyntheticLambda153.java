package org.telegram.messenger;

import org.telegram.tgnet.TLRPC$ChatFull;

public final /* synthetic */ class MessagesStorage$$ExternalSyntheticLambda153 implements Runnable {
    public final /* synthetic */ MessagesStorage f$0;
    public final /* synthetic */ TLRPC$ChatFull f$1;

    public /* synthetic */ MessagesStorage$$ExternalSyntheticLambda153(MessagesStorage messagesStorage, TLRPC$ChatFull tLRPC$ChatFull) {
        this.f$0 = messagesStorage;
        this.f$1 = tLRPC$ChatFull;
    }

    public final void run() {
        this.f$0.lambda$updateChatInfo$101(this.f$1);
    }
}
