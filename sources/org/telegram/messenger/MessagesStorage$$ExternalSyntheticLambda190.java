package org.telegram.messenger;

import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class MessagesStorage$$ExternalSyntheticLambda190 implements Runnable {
    public final /* synthetic */ MessagesStorage f$0;
    public final /* synthetic */ long f$1;
    public final /* synthetic */ TLRPC.InputPeer f$2;
    public final /* synthetic */ long f$3;

    public /* synthetic */ MessagesStorage$$ExternalSyntheticLambda190(MessagesStorage messagesStorage, long j, TLRPC.InputPeer inputPeer, long j2) {
        this.f$0 = messagesStorage;
        this.f$1 = j;
        this.f$2 = inputPeer;
        this.f$3 = j2;
    }

    public final void run() {
        this.f$0.m985xfvar_ee5(this.f$1, this.f$2, this.f$3);
    }
}
