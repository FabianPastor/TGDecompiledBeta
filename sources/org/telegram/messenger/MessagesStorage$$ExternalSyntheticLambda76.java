package org.telegram.messenger;

import org.telegram.tgnet.TLRPC$InputChannel;

public final /* synthetic */ class MessagesStorage$$ExternalSyntheticLambda76 implements Runnable {
    public final /* synthetic */ MessagesStorage f$0;
    public final /* synthetic */ long f$1;
    public final /* synthetic */ int f$2;
    public final /* synthetic */ TLRPC$InputChannel f$3;
    public final /* synthetic */ int f$4;
    public final /* synthetic */ long f$5;

    public /* synthetic */ MessagesStorage$$ExternalSyntheticLambda76(MessagesStorage messagesStorage, long j, int i, TLRPC$InputChannel tLRPC$InputChannel, int i2, long j2) {
        this.f$0 = messagesStorage;
        this.f$1 = j;
        this.f$2 = i;
        this.f$3 = tLRPC$InputChannel;
        this.f$4 = i2;
        this.f$5 = j2;
    }

    public final void run() {
        this.f$0.lambda$loadPendingTasks$21(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5);
    }
}
