package org.telegram.messenger;

import org.telegram.tgnet.TLRPC.InputPeer;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesStorage$AuH1gRs2iXmoSCC0c3xvFDAEwcM implements Runnable {
    private final /* synthetic */ MessagesStorage f$0;
    private final /* synthetic */ long f$1;
    private final /* synthetic */ boolean f$2;
    private final /* synthetic */ InputPeer f$3;
    private final /* synthetic */ long f$4;

    public /* synthetic */ -$$Lambda$MessagesStorage$AuH1gRs2iXmoSCC0c3xvFDAEwcM(MessagesStorage messagesStorage, long j, boolean z, InputPeer inputPeer, long j2) {
        this.f$0 = messagesStorage;
        this.f$1 = j;
        this.f$2 = z;
        this.f$3 = inputPeer;
        this.f$4 = j2;
    }

    public final void run() {
        this.f$0.lambda$null$11$MessagesStorage(this.f$1, this.f$2, this.f$3, this.f$4);
    }
}
