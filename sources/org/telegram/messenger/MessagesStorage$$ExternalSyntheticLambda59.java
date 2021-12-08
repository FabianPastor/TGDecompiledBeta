package org.telegram.messenger;

import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class MessagesStorage$$ExternalSyntheticLambda59 implements Runnable {
    public final /* synthetic */ MessagesStorage f$0;
    public final /* synthetic */ TLRPC.ChatParticipants f$1;

    public /* synthetic */ MessagesStorage$$ExternalSyntheticLambda59(MessagesStorage messagesStorage, TLRPC.ChatParticipants chatParticipants) {
        this.f$0 = messagesStorage;
        this.f$1 = chatParticipants;
    }

    public final void run() {
        this.f$0.m1067xa1417e95(this.f$1);
    }
}
