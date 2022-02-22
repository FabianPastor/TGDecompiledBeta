package org.telegram.messenger;

import org.telegram.tgnet.TLRPC$InputPeer;

public final /* synthetic */ class MessagesStorage$$ExternalSyntheticLambda173 implements Runnable {
    public final /* synthetic */ MessagesStorage f$0;
    public final /* synthetic */ TLRPC$InputPeer f$1;
    public final /* synthetic */ long f$2;

    public /* synthetic */ MessagesStorage$$ExternalSyntheticLambda173(MessagesStorage messagesStorage, TLRPC$InputPeer tLRPC$InputPeer, long j) {
        this.f$0 = messagesStorage;
        this.f$1 = tLRPC$InputPeer;
        this.f$2 = j;
    }

    public final void run() {
        this.f$0.lambda$loadPendingTasks$28(this.f$1, this.f$2);
    }
}
