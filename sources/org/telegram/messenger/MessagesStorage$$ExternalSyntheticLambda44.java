package org.telegram.messenger;

import org.telegram.tgnet.TLRPC$InputChannel;

public final /* synthetic */ class MessagesStorage$$ExternalSyntheticLambda44 implements Runnable {
    public final /* synthetic */ MessagesStorage f$0;
    public final /* synthetic */ int f$1;
    public final /* synthetic */ int f$2;
    public final /* synthetic */ long f$3;
    public final /* synthetic */ TLRPC$InputChannel f$4;

    public /* synthetic */ MessagesStorage$$ExternalSyntheticLambda44(MessagesStorage messagesStorage, int i, int i2, long j, TLRPC$InputChannel tLRPC$InputChannel) {
        this.f$0 = messagesStorage;
        this.f$1 = i;
        this.f$2 = i2;
        this.f$3 = j;
        this.f$4 = tLRPC$InputChannel;
    }

    public final void run() {
        this.f$0.lambda$loadPendingTasks$12(this.f$1, this.f$2, this.f$3, this.f$4);
    }
}
