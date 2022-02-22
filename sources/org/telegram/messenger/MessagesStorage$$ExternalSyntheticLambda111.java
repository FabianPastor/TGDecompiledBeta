package org.telegram.messenger;

import org.telegram.tgnet.TLRPC$InputPeer;

public final /* synthetic */ class MessagesStorage$$ExternalSyntheticLambda111 implements Runnable {
    public final /* synthetic */ MessagesStorage f$0;
    public final /* synthetic */ long f$1;
    public final /* synthetic */ boolean f$2;
    public final /* synthetic */ TLRPC$InputPeer f$3;
    public final /* synthetic */ long f$4;

    public /* synthetic */ MessagesStorage$$ExternalSyntheticLambda111(MessagesStorage messagesStorage, long j, boolean z, TLRPC$InputPeer tLRPC$InputPeer, long j2) {
        this.f$0 = messagesStorage;
        this.f$1 = j;
        this.f$2 = z;
        this.f$3 = tLRPC$InputPeer;
        this.f$4 = j2;
    }

    public final void run() {
        this.f$0.lambda$loadPendingTasks$14(this.f$1, this.f$2, this.f$3, this.f$4);
    }
}
