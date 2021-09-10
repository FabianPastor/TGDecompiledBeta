package org.telegram.messenger;

import org.telegram.tgnet.TLRPC$Dialog;
import org.telegram.tgnet.TLRPC$InputPeer;

public final /* synthetic */ class MessagesStorage$$ExternalSyntheticLambda154 implements Runnable {
    public final /* synthetic */ MessagesStorage f$0;
    public final /* synthetic */ TLRPC$Dialog f$1;
    public final /* synthetic */ TLRPC$InputPeer f$2;
    public final /* synthetic */ long f$3;

    public /* synthetic */ MessagesStorage$$ExternalSyntheticLambda154(MessagesStorage messagesStorage, TLRPC$Dialog tLRPC$Dialog, TLRPC$InputPeer tLRPC$InputPeer, long j) {
        this.f$0 = messagesStorage;
        this.f$1 = tLRPC$Dialog;
        this.f$2 = tLRPC$InputPeer;
        this.f$3 = j;
    }

    public final void run() {
        this.f$0.lambda$loadPendingTasks$12(this.f$1, this.f$2, this.f$3);
    }
}
