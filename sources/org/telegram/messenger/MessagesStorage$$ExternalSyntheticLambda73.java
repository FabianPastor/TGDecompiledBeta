package org.telegram.messenger;

import org.telegram.tgnet.TLRPC$InputChannel;

public final /* synthetic */ class MessagesStorage$$ExternalSyntheticLambda73 implements Runnable {
    public final /* synthetic */ MessagesStorage f$0;
    public final /* synthetic */ long f$1;
    public final /* synthetic */ int f$2;
    public final /* synthetic */ long f$3;
    public final /* synthetic */ TLRPC$InputChannel f$4;

    public /* synthetic */ MessagesStorage$$ExternalSyntheticLambda73(MessagesStorage messagesStorage, long j, int i, long j2, TLRPC$InputChannel tLRPC$InputChannel) {
        this.f$0 = messagesStorage;
        this.f$1 = j;
        this.f$2 = i;
        this.f$3 = j2;
        this.f$4 = tLRPC$InputChannel;
    }

    public final void run() {
        this.f$0.lambda$loadPendingTasks$15(this.f$1, this.f$2, this.f$3, this.f$4);
    }
}
