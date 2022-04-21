package org.telegram.messenger;

import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class MessagesStorage$$ExternalSyntheticLambda64 implements Runnable {
    public final /* synthetic */ MessagesStorage f$0;
    public final /* synthetic */ TLRPC.ChatParticipants f$1;

    public /* synthetic */ MessagesStorage$$ExternalSyntheticLambda64(MessagesStorage messagesStorage, TLRPC.ChatParticipants chatParticipants) {
        this.f$0 = messagesStorage;
        this.f$1 = chatParticipants;
    }

    public final void run() {
        this.f$0.m1007xa69b5132(this.f$1);
    }
}
