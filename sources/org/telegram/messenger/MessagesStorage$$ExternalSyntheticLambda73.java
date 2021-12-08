package org.telegram.messenger;

import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class MessagesStorage$$ExternalSyntheticLambda73 implements Runnable {
    public final /* synthetic */ MessagesStorage f$0;
    public final /* synthetic */ TLRPC.TL_updates f$1;

    public /* synthetic */ MessagesStorage$$ExternalSyntheticLambda73(MessagesStorage messagesStorage, TLRPC.TL_updates tL_updates) {
        this.f$0 = messagesStorage;
        this.f$1 = tL_updates;
    }

    public final void run() {
        this.f$0.m1080xavar_eb(this.f$1);
    }
}
