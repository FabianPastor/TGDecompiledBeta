package org.telegram.messenger;

import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class MessagesStorage$$ExternalSyntheticLambda79 implements Runnable {
    public final /* synthetic */ MessagesStorage f$0;
    public final /* synthetic */ TLRPC.Message f$1;
    public final /* synthetic */ long f$2;

    public /* synthetic */ MessagesStorage$$ExternalSyntheticLambda79(MessagesStorage messagesStorage, TLRPC.Message message, long j) {
        this.f$0 = messagesStorage;
        this.f$1 = message;
        this.f$2 = j;
    }

    public final void run() {
        this.f$0.m2300xab339ca8(this.f$1, this.f$2);
    }
}
