package org.telegram.messenger;

import org.telegram.tgnet.TLRPC$InputChannel;

public final /* synthetic */ class MessagesStorage$$ExternalSyntheticLambda45 implements Runnable {
    public final /* synthetic */ MessagesStorage f$0;
    public final /* synthetic */ int f$1;
    public final /* synthetic */ int f$2;
    public final /* synthetic */ TLRPC$InputChannel f$3;
    public final /* synthetic */ int f$4;
    public final /* synthetic */ long f$5;

    public /* synthetic */ MessagesStorage$$ExternalSyntheticLambda45(MessagesStorage messagesStorage, int i, int i2, TLRPC$InputChannel tLRPC$InputChannel, int i3, long j) {
        this.f$0 = messagesStorage;
        this.f$1 = i;
        this.f$2 = i2;
        this.f$3 = tLRPC$InputChannel;
        this.f$4 = i3;
        this.f$5 = j;
    }

    public final void run() {
        this.f$0.lambda$loadPendingTasks$15(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5);
    }
}
