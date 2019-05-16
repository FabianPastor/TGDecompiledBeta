package org.telegram.messenger;

import org.telegram.tgnet.TLRPC.InputPeer;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesStorage$OTCbnyNXoirwhvbsu8TFVflzodM implements Runnable {
    private final /* synthetic */ MessagesStorage f$0;
    private final /* synthetic */ long f$1;
    private final /* synthetic */ InputPeer f$2;
    private final /* synthetic */ long f$3;

    public /* synthetic */ -$$Lambda$MessagesStorage$OTCbnyNXoirwhvbsu8TFVflzodM(MessagesStorage messagesStorage, long j, InputPeer inputPeer, long j2) {
        this.f$0 = messagesStorage;
        this.f$1 = j;
        this.f$2 = inputPeer;
        this.f$3 = j2;
    }

    public final void run() {
        this.f$0.lambda$null$14$MessagesStorage(this.f$1, this.f$2, this.f$3);
    }
}
