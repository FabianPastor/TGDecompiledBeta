package org.telegram.messenger;

import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class MessagesStorage$$ExternalSyntheticLambda12 implements Runnable {
    public final /* synthetic */ MessagesStorage f$0;
    public final /* synthetic */ long f$1;
    public final /* synthetic */ boolean f$2;
    public final /* synthetic */ TLRPC.InputPeer f$3;
    public final /* synthetic */ long f$4;

    public /* synthetic */ MessagesStorage$$ExternalSyntheticLambda12(MessagesStorage messagesStorage, long j, boolean z, TLRPC.InputPeer inputPeer, long j2) {
        this.f$0 = messagesStorage;
        this.f$1 = j;
        this.f$2 = z;
        this.f$3 = inputPeer;
        this.f$4 = j2;
    }

    public final void run() {
        this.f$0.m918xa28a9b69(this.f$1, this.f$2, this.f$3, this.f$4);
    }
}
