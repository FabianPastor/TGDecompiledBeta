package org.telegram.messenger;

import org.telegram.tgnet.TLRPC$Chat;

public final /* synthetic */ class MessagesStorage$$ExternalSyntheticLambda158 implements Runnable {
    public final /* synthetic */ MessagesStorage f$0;
    public final /* synthetic */ TLRPC$Chat f$1;
    public final /* synthetic */ long f$2;

    public /* synthetic */ MessagesStorage$$ExternalSyntheticLambda158(MessagesStorage messagesStorage, TLRPC$Chat tLRPC$Chat, long j) {
        this.f$0 = messagesStorage;
        this.f$1 = tLRPC$Chat;
        this.f$2 = j;
    }

    public final void run() {
        this.f$0.lambda$loadPendingTasks$11(this.f$1, this.f$2);
    }
}
