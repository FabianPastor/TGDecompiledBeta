package org.telegram.messenger;

import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class MessagesStorage$$ExternalSyntheticLambda54 implements Runnable {
    public final /* synthetic */ MessagesStorage f$0;
    public final /* synthetic */ TLRPC.Chat f$1;
    public final /* synthetic */ long f$2;

    public /* synthetic */ MessagesStorage$$ExternalSyntheticLambda54(MessagesStorage messagesStorage, TLRPC.Chat chat, long j) {
        this.f$0 = messagesStorage;
        this.f$1 = chat;
        this.f$2 = j;
    }

    public final void run() {
        this.f$0.m977x4612d7ed(this.f$1, this.f$2);
    }
}
