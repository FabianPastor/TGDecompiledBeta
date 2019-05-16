package org.telegram.messenger;

import org.telegram.tgnet.TLRPC.InputPeer;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesStorage$tZ31OdkMW8PKLVCYnMiDI_DfRTs implements Runnable {
    private final /* synthetic */ MessagesStorage f$0;
    private final /* synthetic */ InputPeer f$1;
    private final /* synthetic */ long f$2;

    public /* synthetic */ -$$Lambda$MessagesStorage$tZ31OdkMW8PKLVCYnMiDI_DfRTs(MessagesStorage messagesStorage, InputPeer inputPeer, long j) {
        this.f$0 = messagesStorage;
        this.f$1 = inputPeer;
        this.f$2 = j;
    }

    public final void run() {
        this.f$0.lambda$null$18$MessagesStorage(this.f$1, this.f$2);
    }
}
