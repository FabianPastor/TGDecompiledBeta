package org.telegram.messenger;

import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class MessagesStorage$$ExternalSyntheticLambda78 implements Runnable {
    public final /* synthetic */ MessagesStorage f$0;
    public final /* synthetic */ TLRPC.InputPeer f$1;
    public final /* synthetic */ long f$2;

    public /* synthetic */ MessagesStorage$$ExternalSyntheticLambda78(MessagesStorage messagesStorage, TLRPC.InputPeer inputPeer, long j) {
        this.f$0 = messagesStorage;
        this.f$1 = inputPeer;
        this.f$2 = j;
    }

    public final void run() {
        this.f$0.m2213x8ba289e6(this.f$1, this.f$2);
    }
}
