package org.telegram.messenger;

import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class MessagesStorage$$ExternalSyntheticLambda80 implements Runnable {
    public final /* synthetic */ MessagesStorage f$0;
    public final /* synthetic */ TLRPC.Message f$1;
    public final /* synthetic */ boolean f$2;

    public /* synthetic */ MessagesStorage$$ExternalSyntheticLambda80(MessagesStorage messagesStorage, TLRPC.Message message, boolean z) {
        this.f$0 = messagesStorage;
        this.f$1 = message;
        this.f$2 = z;
    }

    public final void run() {
        this.f$0.m2221x8cd091e(this.f$1, this.f$2);
    }
}
